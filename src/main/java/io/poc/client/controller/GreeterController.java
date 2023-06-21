package io.poc.client.controller;

import io.poc.client.bean.GreeterParam;
import io.poc.client.contract.GreeterContract;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/greeter")
@Controller
public class GreeterController {

    private final GreeterContract greeterContract;

    @SneakyThrows
    @GetMapping("/index")
    String home() {
        return "greeter";
    }

    @SneakyThrows
    @PostMapping("/get")
    @ResponseBody
    Object get(@RequestBody GreeterParam account) {
        return greeterContract.get(account);
    }

    @SneakyThrows
    @PostMapping("/set")
    @ResponseBody
    Object set(@RequestBody GreeterParam account) {
        return greeterContract.set(account);
    }

}

