package com.atguigu.gmall.common.util;

import com.atguigu.gmall.common.exception.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @ClassName AssertUtil
 * @Description
 * @Author yzchao
 * @Date 2022/10/20 14:43
 * @Version V1.0
 */
@Slf4j
public class AssertUtil {

    /**
     * 断言对象不为空
     * 如果对象obj为空，则抛出异常
     * @param obj 待判断对象
     */
    public static void notNull(Object obj, ResultCodeEnum resultCodeEnum) {
        if (obj == null) {
            log.info("obj is null...............");
            throw new GmallException(resultCodeEnum);
        }
    }


    /**
     * 断言对象不为空
     * 如果对象obj为空，则抛出异常
     * @param obj 待判断对象
     */
    public static void notNull(Object obj, String message,Integer code) {
        if (obj == null) {
            log.info("obj is null...............");
            throw new GmallException(message);
        }
    }

    /**
     * 断言对象为空
     * 如果对象obj不为空，则抛出异常
     * @param object
     * @param resultCodeEnum
     */
    public static void isNull(Object object, ResultCodeEnum resultCodeEnum) {
        if (object != null) {
            log.info("obj is not null......");
            throw new GmallException(resultCodeEnum);
        }
    }


    /**
     * 断言对象为空
     * 如果对象obj不为空，则抛出异常
     * @param object
     * @param resultCodeEnum
     */
    public static void isNull(Object object, String message,Integer code) {
        if (object != null) {
            log.info("obj is not null......");
            throw new GmallException(message);
        }
    }


    /**
     * 断言表达式为真
     * 如果不为真，则抛出异常
     *
     * @param expression 是否成功
     */
    public static void isTrue(boolean expression, ResultCodeEnum resultCodeEnum) {
        if (!expression) {
            log.info("fail...............");
            throw new GmallException(resultCodeEnum);
        }
    }




    /**
     * 断言表达式为真
     * 如果不为真，则抛出异常
     *
     * @param expression 是否成功
     */
    public static void isTrue(boolean expression, String message,Integer code) {
        if (!expression) {
//            log.info("fail...............");
            throw new GmallException(message);
        }
    }


    /**
     * 断言两个对象不相等
     * 如果相等，则抛出异常
     * @param m1
     * @param m2
     * @param resultCodeEnum
     */
    public static void notEquals(Object m1, Object m2, String message,Integer code) {
        if (m1.equals(m2)) {
            log.info("equals...............");
            throw new GmallException(message);
        }
    }


    /**
     * 断言两个对象不相等
     * 如果相等，则抛出异常
     * @param m1
     * @param m2
     * @param resultCodeEnum
     */
    public static void notEquals(Object m1, Object m2, ResultCodeEnum resultCodeEnum) {
        if (m1.equals(m2)) {
            log.info("equals...............");
            throw new GmallException(resultCodeEnum);
        }
    }

    /**
     * 断言两个对象相等
     * 如果不相等，则抛出异常
     * @param m1
     * @param m2
     * @param resultCodeEnum
     */
    public static void equals(Object m1, Object m2, String message,Integer code){
            log.info("not equals...............");
            throw new GmallException(message);
        }


    /**
     * 断言两个对象相等
     * 如果不相等，则抛出异常
     * @param m1
     * @param m2
     * @param resultCodeEnum
     */
    public static void equals(Object m1, Object m2, ResultCodeEnum resultCodeEnum) {
        if (!m1.equals(m2)) {
            log.info("not equals...............");
            throw new GmallException(resultCodeEnum);
        }
    }



    /**
     * 断言参数不为空
     * 如果为空，则抛出异常
     * @param s
     * @param resultCodeEnum
     */
    public static void notEmpty(String s,String message,Integer code){
        if (StringUtils.isEmpty(s)) {
            log.info("is empty...............");
            throw new GmallException(message);
        }
    }

    /**
     * 断言参数不为空
     * 如果为空，则抛出异常
     * @param s
     * @param resultCodeEnum
     */
    public static void notEmpty(String s, ResultCodeEnum resultCodeEnum){
        if (StringUtils.isEmpty(s)) {
            log.info("is empty...............");
            throw new GmallException(resultCodeEnum);
        }
    }
}

