package io.poc.client.controller;

import io.poc.client.bean.Account;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/")
@Controller
public class AccountController {

    private final Web3j web3j;

    @SneakyThrows
    @GetMapping("/accounts")
    ModelAndView accounts() {
        // 获取余额
        var list = web3j.ethAccounts().send().getAccounts()
                .stream().map(account -> {
                    var balance = Try.of(() -> web3j.ethGetBalance(account, DefaultBlockParameterName.LATEST).send().getBalance())
                            .map(wei -> Convert.fromWei(wei.toString(), Convert.Unit.ETHER))
                            .getOrElse(BigDecimal.ZERO);
                    return Account.builder().address(account).balance(balance).build();
                }).collect(Collectors.toList());

        ModelAndView modelAndView = new ModelAndView();
        // 设置模型数据
        modelAndView.addObject("list", list);
        // 设置视图名称
        modelAndView.setViewName("account");
        return modelAndView;
    }

}

