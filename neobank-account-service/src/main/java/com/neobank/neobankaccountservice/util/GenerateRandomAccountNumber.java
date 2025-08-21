package com.neobank.neobankaccountservice.util;

import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class GenerateRandomAccountNumber {

    public Long generateRandomAccountNumber() {
        long min = 100000000000L;  // smallest 12-digit number
        long max = 999999999999L;  // largest 12-digit number
        return ThreadLocalRandom.current().nextLong(min, max + 1);
    }
}