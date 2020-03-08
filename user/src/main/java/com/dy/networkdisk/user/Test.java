package com.dy.networkdisk.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Test {

    @Data
    static abstract class People{
        private String name;
        private int age;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    static class Man extends People{
        private final String sex = "男";
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    static class Womman extends People{
        private final String sex = "女";
    }

    public static void main(String[] args) throws JsonProcessingException {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        People man = new Man();
//        String json = mapper.writeValueAsString(man);
//        System.out.println(json);
//        Man result = (Man) mapper.readValue(json,Object.class);
//        System.out.println(result);
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
//        String poc = "[\"com.mysql.cj.jdbc.admin.MiniAdmin\", \"jdbc:mysql://X.X.X.X:3306/db\"]";
//        Object obj = serializer.deserialize(poc.getBytes());
//        serializer.setObjectMapper(mapper);
//        List<People> peoples = new ArrayList<>();
//        People man = new Man();
//        man.setName("name");
//        man.setAge(18);
//        peoples.add(man);
//        Long num = 10L;
        LocalDateTime now = LocalDateTime.now();
        byte[] bytes = serializer.serialize(now);
        String json = new String(bytes);
        System.out.println(json);
        LocalDateTime time = serializer.deserialize(bytes,LocalDateTime.class);
        System.out.println(time.toString());

    }
}
