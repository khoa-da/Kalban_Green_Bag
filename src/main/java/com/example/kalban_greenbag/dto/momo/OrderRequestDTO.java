package com.example.kalban_greenbag.dto.momo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {

    private Long amount;
    private String orderId;
    private String orderInfo;
    private String lang;
    private String extraData;

}
