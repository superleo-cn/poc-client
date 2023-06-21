package io.poc.client.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
public class HomeController {

    @GetMapping("/")
    String index() {
        return "index";
    }

    @GetMapping("favicon.ico")
    String favicon() {
        return "forward:/img/favicon.ico";
    }

}
