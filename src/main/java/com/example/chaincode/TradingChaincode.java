package com.example.chaincode;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(TradingChaincode.class);

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final String KEY_METADATA = "METADATA";

    /**
     * 初始化
     */
    @Override
    public Response init(ChaincodeStub stub) {
        try {
            String func = stub.getFunction();
            if (!func.equals("init")) {
                return newErrorResponse("function error!");
            }
            List<String> args = stub.getParameters();
            if (args.size() != 1) {
                newErrorResponse("params error!");
            }
            LOGGER.info("init begin, function = {}, args = {}", func, args);
            stub.putStringState(KEY_METADATA, args.get(0));
            return newSuccessResponse("init success.");
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
            LOGGER.info("invoke begin, function = {}, args = {}", function, args);
            Response response = null;
            response = doInvoke(stub, function, args);
            LOGGER.info("invoke success，response = [status = {}, message = {}, payload = {}]", response.getStatus().getCode(), response.getMessage(), response.getPayload() == null ? null : new String(response.getPayload(), CHARSET));
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

    public static void main(String[] args) {
        new TradingChaincode().start(args);
    }
}
