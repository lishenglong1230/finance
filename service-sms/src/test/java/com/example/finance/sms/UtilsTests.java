package com.example.finance.sms;

import com.example.finance.sms.util.SmsProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/4 17:09
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UtilsTests {

    @Test
    public void testProperties(){
        System.out.println(SmsProperties.REGION_Id);
        System.out.println(SmsProperties.KEY_ID);
        System.out.println(SmsProperties.KEY_SECRET);
        System.out.println(SmsProperties.TEMPLATE_CODE);
        System.out.println(SmsProperties.SIGN_NAME);
    }
}