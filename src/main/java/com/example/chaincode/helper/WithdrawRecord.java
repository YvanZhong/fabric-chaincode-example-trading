package com.example.chaincode.helper;

import lombok.Data;

import java.io.Serializable;

@Data
public class WithdrawRecord implements Serializable{
    private static final long serialVersionUID = 1L;

    public String withdraw_id;

    public String user_id;

    public String points;

    public String rate;

    public String cash;

    public String timestamp;
}
