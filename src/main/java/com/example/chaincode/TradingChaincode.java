package com.example.chaincode;

import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.*;
import com.example.chaincode.helper.*;

/**
 * @auther yifan
 */
public class TradingChaincode extends ChaincodeBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradingChaincode.class);

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final String KEY_METADATA = "METADATA";

    private static final String KEY_PREFIX_RECHARGE = "RECHARGE_";

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
            List<String> params = stub.getParameters();
            LOGGER.info("invoke begin, function = {}, args = {}", function, params);
            Response response = doInvoke(stub, function, params);
            LOGGER.info("invoke success，response = [status = {}, message = {}, payload = {}]", response.getStatus().getCode(), response.getMessage(), response.getPayload() == null ? null : new String(response.getPayload(), CHARSET));
            return response;
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
            return newErrorResponse(e);
        }
    }

    private Response doInvoke(ChaincodeStub stub, String function, List<String> params) throws Exception {
        try {
            switch (function) {
                case "test":
                    return newSuccessResponse("test success!");

                default:
                    return newErrorResponse(String.format("function error: %s", function));
            }
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
            return newErrorResponse(e);
        }
    }

    private Response recharge(ChaincodeStub stub, List<String> params) {
        if (params.size() != 4) {
            return newErrorResponse("Incorrect number of arguments. Expecting 4");
        }
        String recharge_id = params.get(0);
        String merchant_id = params.get(1);
        String points = params.get(2);
        String timestamp = params.get(3);

        String key = createRechargeKey(stub, recharge_id);
        String value = stub.getStringState(key);
        if (value != null) {
            return newErrorResponse("Recharge_id repeat.");
        }

        Gson gson = new Gson();
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setRecharge_id(recharge_id);
        rechargeRecord.setMerchant_id(merchant_id);
        rechargeRecord.setPoints(points);
        rechargeRecord.setTimestamp(timestamp);
        String json = gson.toJson(rechargeRecord);

        stub.putStringState(key, json);

        return newSuccessResponse("invoke finished successfully");
    }

    private String createRechargeKey(ChaincodeStub stub, String recharge_id) {
        return stub.createCompositeKey(KEY_PREFIX_RECHARGE, recharge_id).toString();
    }

    public static void main(String[] args) {
        new TradingChaincode().start(args);
    }
}
