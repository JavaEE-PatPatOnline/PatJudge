/*
 * Copyright (C) Patpat Online 2024
 * Made with love by Tony Skywalker
 */

package cn.edu.buaa.patpat.judge.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller for checking the status of the server.
 */
@RestController
@RequestMapping("/status")
public class StatusController {
    @GetMapping("/ping")
    public String ping() {
        return "pong\n";
    }
}
