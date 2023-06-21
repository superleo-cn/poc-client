package io.poc.client.contract;

import io.poc.client.bean.GreeterParam;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.tx.Contract;
import org.web3j.utils.Numeric;

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
     * 调用只读合约
     */
    @SneakyThrows
    public Object get(GreeterParam param) {
        // 要传递给合约函数的参数
        List<Type> inputParameters = List.of();
        List<TypeReference<?>> outputParameters = List.of(new TypeReference<Utf8String>() {
        });
        // 创建合约函数对象
        Function function = new Function(param.getFunction(), inputParameters, outputParameters);
        // 将函数编码为字节数组
        String encodedFunction = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(param.getAddress(), CONTRACT_ADDRESS, encodedFunction);
        EthCall ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
        var results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
        return results.get(0).getValue();
    }

    /**
     * 修改合约
     */
    @SneakyThrows
    public String set(GreeterParam param) {
        // 要传递给合约函数的参数
        List<Type> inputParameters = Arrays.asList(new Utf8String(param.getNewValue()));
        List<TypeReference<?>> outputParameters = List.of();
        // 创建合约函数对象
        Function function = new Function(param.getFunction(), inputParameters, outputParameters);
        // 将函数编码为字节数组
        String encodedFunction = FunctionEncoder.encode(function);

        BigInteger nonce = web3j.ethGetTransactionCount(param.getAddress(), DefaultBlockParameterName.LATEST).send().getTransactionCount();       //获取nonce
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();     //gas数
        BigInteger gasLimit = Contract.GAS_LIMIT;                           //限制gas数

        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, CONTRACT_ADDRESS, encodedFunction);

        Credentials credentials = Credentials.create(param.getPrivateKey());
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signMessage);
        EthSendTransaction ethResult = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        // 交易编号
        return ethResult.getTransactionHash();
    }

}
