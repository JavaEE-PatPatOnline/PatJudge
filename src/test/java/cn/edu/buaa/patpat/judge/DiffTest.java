/*
 * Copyright (C) Patpat Online 2024
 * Made with love by Tony Skywalker
 */

package cn.edu.buaa.patpat.judge;

import cn.edu.buaa.patpat.judge.utils.diff.AdvancedDiffProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles({ "dev" })
public class DiffTest {
    @Test
    void postDiffTest() {
        AdvancedDiffProvider provider = new AdvancedDiffProvider();
        assertThat(provider.postProcessDiff("a*  b *c", "**"))
                .isEqualTo("a*  b *c");
        assertThat(provider.postProcessDiff("a**  b **c", "**"))
                .isEqualTo("a  **b** c");
        assertThat(provider.postProcessDiff("a ** b ** c", "**"))
                .isEqualTo("a  **b**  c");
        assertThat(provider.postProcessDiff("**  b **", "**"))
                .isEqualTo("  **b** ");
        assertThat(provider.postProcessDiff("**  b", "**"))
                .isEqualTo("**  b");
        assertThat(provider.postProcessDiff("~~  b ~~", "~~"))
                .isEqualTo("  ~~b~~ ");
        assertThat(provider.postProcessDiff("~~  b ~", "~~"))
                .isEqualTo("~~  b ~");
    }
}
