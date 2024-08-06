package com.gam.cshtest;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimezoneTest {
    public static void main(String[] args) {
        System.out.println("Current Timezone: " + ZoneId.systemDefault());
        System.out.println("Current Time: " + LocalDateTime.now());
    }
}