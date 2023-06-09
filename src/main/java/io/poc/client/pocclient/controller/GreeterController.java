package io.poc.client.pocclient.controller;

import io.poc.client.pocclient.bean.Account;
import io.poc.client.pocclient.bean.GreeterParam;
import io.poc.client.pocclient.contract.GreeterContract;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.util.stream.Collectors;

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
        return greeterContract.get(account.getFunction(), account.getAddress());
    }

}

