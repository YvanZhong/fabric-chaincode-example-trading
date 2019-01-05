package com.example.chaincode;

import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
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

    private static final String KEY_PREFIX_TRANSFER = "TRANSFER_";

    private static final String KEY_PREFIX_ORDERPAY = "ORDERPAY_";

    private static final String KEY_PREFIX_ORDERREFUND = "ORDERREFUND_";

    private static final String KEY_PREFIX_WITHDRAW = "WITHDRAW_";

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
            LOGGER.info("invoke success, response = [status = {}, message = {}, payload = {}]", response.getStatus().getCode(), response.getMessage(), response.getPayload() == null ? null : new String(response.getPayload(), CHARSET));
            return response;
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
            return newErrorResponse(e);
        }
    }

    private Response doInvoke(ChaincodeStub stub, String function, List<String> params) throws Exception {
        try {
            switch (function) {
                case "recharge":
                    return recharge(stub, params);

                case "transfer":
                    return transfer(stub, params);

                case "orderPay":
                    return orderPay(stub, params);

                case "orderRefund":
                    return orderRefund(stub, params);

                case "withdraw":
                    return withdraw(stub, params);

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
        LOGGER.info("value = {}", value);
        if (!value.isEmpty()) {
            return newErrorResponse("recharge_id repeat.");
        }

        Gson gson = new Gson();
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setRecharge_id(recharge_id);
        rechargeRecord.setMerchant_id(merchant_id);
        rechargeRecord.setPoints(points);
        rechargeRecord.setTimestamp(timestamp);
        String json = gson.toJson(rechargeRecord);

        stub.putStringState(key, json);

        LOGGER.info("recharge success, key = {}, json = {}", key, json);
        return newSuccessResponse("recharge finished successfully");
    }

    private Response transfer(ChaincodeStub stub, List<String> params) {
        if (params.size() != 5) {
            return newErrorResponse("Incorrect number of arguments. Expecting 5");
        }
        String transfer_id = params.get(0);
        String merchant_id = params.get(1);
        String user_id = params.get(2);
        String points = params.get(3);
        String timestamp = params.get(4);

        String key = createTransferKey(stub, transfer_id);
        String value = stub.getStringState(key);
        LOGGER.info("value = {}", value);
        if (!value.isEmpty()) {
            return newErrorResponse("transfer_id repeat.");
        }

        Gson gson = new Gson();
        TransferRecord transferRecord = new TransferRecord();
        transferRecord.setTransfer_id(transfer_id);
        transferRecord.setMerchant_id(merchant_id);
        transferRecord.setUser_id(user_id);
        transferRecord.setPoints(points);
        transferRecord.setTimestamp(timestamp);
        String json = gson.toJson(transferRecord);

        stub.putStringState(key, json);

        LOGGER.info("transfer success, key = {}, json = {}", key, json);
        return newSuccessResponse("transfer finished successfully");
    }

    private Response orderPay(ChaincodeStub stub, List<String> params) {
        if (params.size() != 6) {
            return newErrorResponse("Incorrect number of arguments. Expecting 6");
        }
        String order_id = params.get(0);
        String merchant_id = params.get(1);
        String user_id = params.get(2);
        String points = params.get(3);
        String cash = params.get(4);
        String timestamp = params.get(5);

        String key = createOrderPayKey(stub, order_id);
        String value = stub.getStringState(key);
        LOGGER.info("value = {}", value);
        if (!value.isEmpty()) {
            return newErrorResponse("order_id repeat.");
        }

        //check user points

        Gson gson = new Gson();
        OrderPayRecord orderPayRecord = new OrderPayRecord();
        orderPayRecord.setOrder_id(order_id);
        orderPayRecord.setMerchant_id(merchant_id);
        orderPayRecord.setUser_id(user_id);
        orderPayRecord.setPoints(points);
        orderPayRecord.setCash(cash);
        orderPayRecord.setTimestamp(timestamp);
        String json = gson.toJson(orderPayRecord);

        stub.putStringState(key, json);

        LOGGER.info("orderPay success, key = {}, json = {}", key, json);
        return newSuccessResponse("orderPay finished successfully");
    }

    private Response orderRefund(ChaincodeStub stub, List<String> params) {
        if (params.size() != 7) {
            return newErrorResponse("Incorrect number of arguments. Expecting 7");
        }
        String refund_id = params.get(0);
        String order_id = params.get(1);
        String merchant_id = params.get(2);
        String user_id = params.get(3);
        String points = params.get(4);
        String cash = params.get(5);
        String timestamp = params.get(6);

        String key = createOrderRefundKey(stub, refund_id);
        String value = stub.getStringState(key);
        LOGGER.info("value = {}", value);
        if (!value.isEmpty()) {
            return newErrorResponse("refund_id repeat.");
        }

        String orderKey = createOrderPayKey(stub, order_id);
        String order = stub.getStringState(orderKey);
        LOGGER.info("order = {}", order);
        if (order.isEmpty()) {
            return newErrorResponse("order_id not exist.");
        }

        Gson gson = new Gson();

        OrderPayRecord orderPayRecord = gson.fromJson(order, OrderPayRecord.class);
        int order_points = Integer.parseInt(orderPayRecord.points);
        int order_cash = Integer.parseInt(orderPayRecord.cash);

        QueryResultsIterator<KeyValue> refundRecords = stub.getStateByPartialCompositeKey(KEY_PREFIX_ORDERREFUND);
        int refunded_points = 0, refunded_cash = 0;
        for (Iterator<KeyValue> it = refundRecords.iterator(); it.hasNext(); ) {
            KeyValue kv = it.next();
            OrderRefundRecord orderRefundRecord = gson.fromJson(
                    new String(kv.getValue(), CHARSET), OrderRefundRecord.class);
            if (orderRefundRecord.order_id.equals(order_id)) {
                refunded_points += Integer.parseInt(orderRefundRecord.points);
                refunded_cash += Integer.parseInt(orderRefundRecord.cash);
            }
        }

        LOGGER.info("order_points = {}, total_points = {}, order_cash = {}, total_cash = {}",
                order_points, refunded_points + Integer.parseInt(points),
                order_cash, refunded_cash + Integer.parseInt(cash));

        if (order_points < refunded_points + Integer.parseInt(points) &&
                order_cash < refunded_cash + Integer.parseInt(cash)) {
            return newErrorResponse("Points or cash overflow.");
        }

        OrderRefundRecord orderRefundRecord = new OrderRefundRecord();
        orderRefundRecord.setRefund_id(refund_id);
        orderRefundRecord.setOrder_id(order_id);
        orderRefundRecord.setMerchant_id(merchant_id);
        orderRefundRecord.setUser_id(user_id);
        orderRefundRecord.setPoints(points);
        orderRefundRecord.setCash(cash);
        orderRefundRecord.setTimestamp(timestamp);
        String json = gson.toJson(orderRefundRecord);

        stub.putStringState(key, json);

        LOGGER.info("orderRefund success, key = {}, json = {}", key, json);
        return newSuccessResponse("orderRefund finished successfully");
    }

    private Response withdraw(ChaincodeStub stub, List<String> params) {
        if (params.size() != 6) {
            return newErrorResponse("Incorrect number of arguments. Expecting 6");
        }
        String withdraw_id = params.get(0);
        String user_id = params.get(1);
        String points = params.get(2);
        String rate = params.get(3);
        String cash = params.get(4);
        String timestamp = params.get(5);

        String key = createWithdrawKey(stub, withdraw_id);
        String value = stub.getStringState(key);
        LOGGER.info("value = {}", value);
        if (value != null && !value.isEmpty()) {
            return newErrorResponse("withdraw_id repeat.");
        }

        //check user points

        Gson gson = new Gson();
        WithdrawRecord withdrawRecord = new WithdrawRecord();
        withdrawRecord.setWithdraw_id(withdraw_id);
        withdrawRecord.setUser_id(user_id);
        withdrawRecord.setPoints(points);
        withdrawRecord.setRate(rate);
        withdrawRecord.setCash(cash);
        withdrawRecord.setTimestamp(timestamp);
        String json = gson.toJson(withdrawRecord);

        stub.putStringState(key, json);

        LOGGER.info("withdraw success, key = {}, json = {}", key, json);
        return newSuccessResponse("withdraw finished successfully");
    }

    private String createRechargeKey(ChaincodeStub stub, String recharge_id) {
        return stub.createCompositeKey(KEY_PREFIX_RECHARGE, recharge_id).toString();
    }

    private String createTransferKey(ChaincodeStub stub, String transfer_id) {
        return stub.createCompositeKey(KEY_PREFIX_TRANSFER, transfer_id).toString();
    }

    private String createOrderPayKey(ChaincodeStub stub, String order_id) {
        return stub.createCompositeKey(KEY_PREFIX_ORDERPAY, order_id).toString();
    }

    private String createOrderRefundKey(ChaincodeStub stub, String refund_id) {
        return stub.createCompositeKey(KEY_PREFIX_ORDERREFUND, refund_id).toString();
    }

    private String createWithdrawKey(ChaincodeStub stub, String withdraw_id) {
        return stub.createCompositeKey(KEY_PREFIX_WITHDRAW, withdraw_id).toString();
    }

    public static void main(String[] args) {
        new TradingChaincode().start(args);
    }
}
