package com.example.chaincode.helper;

import lombok.*;

@Data
public class RechargeRecord {
    private static final long serialVersionUID = 1L;

    public String recharge_id;

    public String merchant_id;

    public String points;

    public String timestamp;
}
