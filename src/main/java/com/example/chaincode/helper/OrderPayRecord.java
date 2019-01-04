package com.example.chaincode.helper;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderPayRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    public String order_id;

    public String merchant_id;

    public String user_id;

    public String points;

    public String cash;

    public String timestamp;
}
