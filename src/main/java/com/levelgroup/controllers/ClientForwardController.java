package com.levelgroup.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClientForwardController {

//    @GetMapping(value = {"/{path:[^\\.]*}", "/{path:^(?!api$).*$}/**/{path:[^.]*}"})
    @GetMapping(value = "/**/{path:[^\\.]*}")
    public String forward() {
        return "forward:/";
    }
}
