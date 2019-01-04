package com.example.trading.chaincode;

import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @auther yifan
 */
public class TradingChaincode extends ChaincodeBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(BankMasterChaincode.class);

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final Double DEFAULT_ACCOUNT_BALANCE = 0.0;

    private static final String KEY_METADATA = "METADATA";

    private static final String KEY_PREFIX_CUSTOMER_ACCOUNT = "CUSTOMER_ACCOUNT_";

    private static final String KEY_PREFIX_ACCOUNT_TRANSACTION = "ACCOUNT_TRANSACTION_";

    /**
     * 初始化
     */
    @Override
    public Response init(ChaincodeStub stub) {
        try {
            String func = stub.getFunction();
            if (!func.equals("init")) {
                return newErrorResponse("函数错误!");
            }
            List<String> args = stub.getParameters();
            if (args.size() != 1) {
                newErrorResponse("参数错误！");
            }
            LOGGER.info("初始化智能合约开始，function = {}, args = {}", func, args);
            stub.putStringState(KEY_METADATA, args.get(0));
            return newSuccessResponse("初始化智能合约成功!");
        } catch (Throwable e) {
            return newErrorResponse(e);
        }
    }

    /**
     * 调用
     */
    @Override
    public Response invoke(ChaincodeStub stub) {
        try {
            String function = stub.getFunction();
            List<String> args = stub.getParameters();
            LOGGER.info("调用智能合约开始，function = {}, args = {}", function, args);
            Response response = null;
            response = doInvoke(stub, function, args);
            LOGGER.info("<<< 调用智能合约结束，response = [status = {}, message = {}, payload = {}]", response.getStatus().getCode(), response.getMessage(), response.getPayload() == null ? null : new String(response.getPayload(), CHARSET));
            return response;
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
            return newErrorResponse(e);
        }
    }

    protected Response doInvoke(ChaincodeStub stub, String function, List<String> args) throws Exception {
        try {
            switch (function) {
                case "test":
                    return newSuccessResponse("调用成功!");

                default:
                    return newErrorResponse(String.format("方法名错误: %s", function));
            }
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
            return newErrorResponse(e);
        }
    }


}
