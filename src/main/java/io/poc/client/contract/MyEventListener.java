package io.poc.client.contract;

import io.poc.client.contracts.MyEventContract;
import io.reactivex.disposables.Disposable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.tx.gas.DefaultGasProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.web3j.tx.Contract.staticExtractEventParameters;

@Slf4j
@RequiredArgsConstructor
@Component
public class MyEventListener {

    private final Web3j web3j;
    private final static String CONTRACT_ADDRESS = "0x5fbdb2315678afecb367f032d93f642f64180aa3";
    private final static String PRIVATE_KEY = "123";
    Disposable subscribe = null;

    @PreDestroy
    public void close() {
        try {
            if (subscribe != null) {
                subscribe.dispose();
            }
        } catch (Exception e) {
            log.error("wen3j subscribe close error:{}", e.getMessage());
        }
    }

    @PostConstruct
    public void init() {
        try {
            run();
        } catch (Exception e) {
            log.error("wen3j subscribe init error:{}", e.getMessage());
        }
    }

    @SneakyThrows
    @PostConstruct
    public void run() {
        DefaultGasProvider contractGasProvider = new DefaultGasProvider();
        Credentials credentials = Credentials.create(PRIVATE_KEY);
        // Load ABI 通过solidity/sol文件编译的java代码默认位置 $buildDir/generated/sources/web3j
        MyEventContract contract = MyEventContract.load(CONTRACT_ADDRESS, web3j, credentials, contractGasProvider);

        // 创建一个 EthFilter 对象来过滤事件
        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, CONTRACT_ADDRESS);
        //监听哪个事件，合约中的event写了几个参数，这里就写几个，类型对应好
        Event event = new Event("MyEvent",
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Address>(true) {
                        },
                        new TypeReference<Utf8String>(false) {
                        }
                ));
        filter.addSingleTopic(EventEncoder.encode(event));

        subscribe = web3j.ethLogFlowable(filter).subscribe(tx -> {
            int newBlock = tx.getBlockNumber().intValue();
            log.info("wen3j subscribe --newBlock-- :{} ", newBlock);
            log.info("wen3j subscribe --tx-- :{} ", tx);
            EventValues eventValues = staticExtractEventParameters(event, tx);
            //定义接收参数(本示例使用的事件返回了6个，具体按自己合约来)
            String address1 = "";
            int uint1 = 0;
            List<Type> indexedValues = eventValues.getIndexedValues();
            if (Objects.nonNull(indexedValues) && indexedValues.size() == 1) {
                Type<?> type1 = indexedValues.get(0);
                address1 = type1.getValue().toString();
            }
            List<Type> nonIndexedValues = eventValues.getNonIndexedValues();
            if (Objects.nonNull(nonIndexedValues) && nonIndexedValues.size() == 1) {
                Type<?> type1 = nonIndexedValues.get(0);
                uint1 = Integer.parseInt(type1.getValue().toString());
            }
            log.info("address1:{}; uint1:{}", address1, uint1);
        });

    }
}
