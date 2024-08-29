/*
 * Copyright (C) Patpat Online 2024
 * Made with love by Tony Skywalker
 */

package cn.edu.buaa.patpat.judge.extensions.judge;

import cn.edu.buaa.patpat.judge.dto.JudgeRequest;
import cn.edu.buaa.patpat.judge.dto.JudgeResponse;

public interface IJudger {
    JudgeResponse judge(JudgeRequest request);
}
