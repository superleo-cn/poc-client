package io.poc.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class Web3jConfig {
    @Bean
    public Web3j web3j() {
        // 以太坊节点的 RPC URL
        String ethereumRpcUrl = "http://localhost:8545";
        return Web3j.build(new HttpService(ethereumRpcUrl));
    }
}
