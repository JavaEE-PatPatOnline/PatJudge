# Make PatJudge Better

> PatJudge 开发说明

---

## Overview

首先请参考 PatBoot 的 [CONTRIBUTING.md](https://github.com/JavaEE-PatPatOnline/PatBoot/blob/main/CONTRIBUTING.md)。

PatJudge 不向外暴露服务，仅与 RabbitMQ 交互，接收评测请求，返回评测结果，没有数据库。

## 题目评测

### 评测环境

评测机（能够）配备多个 JRE，从而支持不同 Java 版本的提交。目前只使用与评测机本身相同的 JDK 17。

### 评测初始化

评测会使用评测机容器内部的 `/sandbox` 作为代码沙箱，每个评测机同时只评测一个题目，因此不存在冲突。评测开始前，会将其清空。

紧接着，根据评测请求，将 `/submission` 目录下的提交（解压好的项目源文件）复制到 `/sandbox/src` 目录下，作为源代码初始化。

接下来，读取题目配置，如果题目有初始化文件，则此时将文件复制进 `/sandbox/src` 目录，并覆盖同名文件，防止学生提交中包含干扰文件。

最后，会生成 `/sandbox/security.policy`，只允许读写 `/sandbox` 目录下的文件，内容如下。

```
grant {
  permission java.io.FilePermission "/sandbox/-", "read, write";
};
```

### 编译运行

初始化结束后，首先编译 Java 项目，如果有编译错误则直接返回 CE，编译命令如下，其中 `{exe}` 为选定的 JDK 中的 `javac` 路径，将 class 文件输出到 `/sandbox/out`。

```bash
bash -c {exe} -encoding UTF-8 -cp ./src -d ./out ./src/*.java
```

编译成功后，将启动线程池，并行评测各个测试点，运行命令如下，其中 `{Main}` 为题目配置中指定的主类（没有 `.class` 扩展名）。运行时会重定向输入输出。

```bash
{exe} -Djava.security.manager -Djava.security.policy=security.policy -classpath out {Main}
```

### 结果比对

PatJudge 提供基于行和基于 diff 的结果比对。二者都会过滤期望输出和标准输出前后的空白行，以及每行前后的空白。

基于行的比对对应评测配置中的 `mode` 为 basic，将期望输出和实际输出逐行对比，输出第一个不匹配的行。这种方式

基于 diff 的结果比对对应评测配置中的 `mode` 为 `advanced`，采用 [java-diff-utils](https://github.com/java-diff-utils/java-diff-utils)，比对期望输出和实际输出后生成 diff。在所有有区别的行中，选择前 3 处，并且限制每行的长度，避免泄漏标准答案。

### 评测结果

评测机能够提供五种反馈，分别如下。

- AC：完全正确
- WA：答案错误
- RE：运行时错误，发生未捕获的异常，或是触发了 security policy
- TLE：运行时间过长
- JE：评测机错误

## 提交评测

PatJudge 利用 RabbitMQ 实现评测请求的接收以及结果的反馈。具体地，从 `q.judge.pending` 获取评测请求，将评测反馈发送至 `q.judge.result`。

评测请求（`JudgeRequest`）中，仅包含完成评测最基本的信息，其余信息作为 `Object` 类型，接收但不处理，在评测反馈中原封不动地返回，便于评测发起方确定对应的评测记录。