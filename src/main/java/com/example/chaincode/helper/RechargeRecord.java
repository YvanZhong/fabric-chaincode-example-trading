package com.example.chaincode;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class RechargeRecord {
    private static final long serialVersionUID = 1L;

    private String recharge_id;

    private String merchant_id;

    private String points;

    private String timestamp;
}
