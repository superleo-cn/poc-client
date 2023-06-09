package io.poc.client.pocclient.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GreeterParam {
    private String address;
    private String privateKey;
    private String function;
    private String newValue;
}

