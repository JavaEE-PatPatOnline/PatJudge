/*
 * Copyright (C) Patpat Online 2024
 * Made with love by Tony Skywalker
 */

package cn.edu.buaa.patpat.judge.models;

import lombok.Data;

import java.util.List;

@Data
public class ProblemDescriptor {
    /**
     * Whether the problem has init files.
     */
    private boolean init;

    /**
     * The mode of the problem.
     * <ul>
     *     <li>basic: only compare result as same or different.</li>
     *     <li>advanced: compare result with diff tool.</li>
     * </ul>
     */
    private String mode;

    /**
     * The name of the problem.
     */
    private String name;

    /**
     * The main class of the submitted code.
     * No .java suffix.
     */
    private String mainClass;

    /**
     * Time limit in milliseconds.
     */
    private long timeLimit;

    private List<CaseDescriptor> cases;
}

