package com.chenzejie.test;

import org.junit.Test;

import java.time.ZonedDateTime;

public class MyTest {
    @Test
    public void test1() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        System.out.println(zonedDateTime);
        // 2021-04-29T11:01:40.838+08:00[Asia/Shanghai]
    }
}
