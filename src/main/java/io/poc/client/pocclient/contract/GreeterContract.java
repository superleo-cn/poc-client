package io.poc.client.pocclient.contract;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GreeterContract {

    private final Web3j web3j;
    /**
     * todo: 自己的合约地址
     */
    private final static String CONTRACT_ADDRESS = "0x5fbdb2315678afecb367f032d93f642f64180aa3";

    /**
     * 要发送交易的账户地址和私钥
     *
     * @param fromAddress
     * @param privateKey
     */
    @SneakyThrows
    public Object get(String fun, String fromAddress) {
        // 要传递给合约函数的参数
        List<Type> inputParameters = List.of();
        List<TypeReference<?>> outputParameters = List.of(new TypeReference<Utf8String>(){});
        // 创建合约函数对象
        Function function = new Function(fun, inputParameters, outputParameters);
        // 将函数编码为字节数组
        String encodedFunction = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(fromAddress, CONTRACT_ADDRESS, encodedFunction);
        EthCall ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
        var results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
        return results.get(0).getValue();
    }

    /**
     * 要发送交易的账户地址和私钥
     *
     * @param fromAddress
     * @param privateKey
     */
    @SneakyThrows
    public Object set(String fun, String fromAddress, String privateKey, String newValue) {

        // 要传递给合约函数的参数
        List<Type> inputParameters = Arrays.asList(new Uint(BigInteger.ONE));

        // 创建合约函数对象
        Function function = new Function(fun, inputParameters, Arrays.asList());

        // 将函数编码为字节数组
        String encodedFunction = FunctionEncoder.encode(function);

        // 使用私钥创建凭据
        Credentials credentials = Credentials.create(privateKey);

        Transaction transaction = Transaction.createEthCallTransaction(fromAddress, CONTRACT_ADDRESS, encodedFunction);
        EthCall ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
        var results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
        return results.get(0).getValue();
    }

}
