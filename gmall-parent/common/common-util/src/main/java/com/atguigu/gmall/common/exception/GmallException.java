package com.atguigu.gmall.common.exception;

import com.atguigu.gmall.common.result.ResultCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description  自定义全局异常类
 * @Author yzchao
 * @Date 2022/10/19 21:10
 */
@Data
@ApiModel(value = "自定义全局异常类")
public class GmallException extends RuntimeException {


    private Integer code;
    private String message;

    /**
     * 通过状态码和错误消息创建异常对象
     * @param message
     * @param code
     */
    public GmallException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    /**
     * 接收枚举类型对象
     * @param resultCodeEnum
     */
    public GmallException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "GuliException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}