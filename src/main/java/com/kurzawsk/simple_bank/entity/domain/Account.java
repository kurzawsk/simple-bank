package com.kurzawsk.simple_bank.entity.domain;

import lombok.*;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private Long id;
    private String owner;
    private BigDecimal balance;
    private String number;
    private Lock lock;
}
