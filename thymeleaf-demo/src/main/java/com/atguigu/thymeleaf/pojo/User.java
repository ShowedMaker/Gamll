package com.atguigu.thymeleaf.pojo;

import java.io.Serializable;

/**
 * @ClassName User
 * @Description
 * @Author yzchao
 * @Date 2022/10/31 16:50
 * @Version V1.0
 */
public class User implements Serializable {

    private String name;
    private int age;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", address='" + address + '\'' +
                '}';
    }
}
