package com.github_detector.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GithubEventController {


    @PostMapping("/tesss")
    public String handleGithubEvent(){

        return null;
    }
}
