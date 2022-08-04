package com.example.finance.core;

import com.example.finance.core.mapper.DictMapper;
import com.example.finance.core.pojo.entity.Dict;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/4 10:49
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTemplateTests {
    @Resource
    private RedisTemplate redisTemplate;


    @Resource
    DictMapper dictMapper;

    @Test
    public void save(){
        Dict dict = dictMapper.selectById(1);
        //过期时间为5分钟
        redisTemplate.opsForValue().set("dict",dict, 5,TimeUnit.MINUTES);

    }

    @Test
    public void getDict(){
        Dict dict = (Dict)redisTemplate.opsForValue().get("dict");
        System.out.println(dict);
    }

}
