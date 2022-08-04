package com.example.finance.core;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import org.junit.Test;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/1 17:49
 */
public class AssertTests {

    //if else的用法
    @Test
    public void test1() {
        Object o = null;
        if (o == null) {
            throw new IllegalArgumentException("用户不存在.");
        }
    }

    //断言的用法：更为简洁
    @Test
    public void test2() {
        // 另一种写法
        Object o = null;
        Assert.notNull(o, "用户不存在.");
    }
}
