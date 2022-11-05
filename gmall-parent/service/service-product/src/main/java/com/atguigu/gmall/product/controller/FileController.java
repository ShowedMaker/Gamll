package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.util.FileUpLoadUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName FileController
 * @Description 后台系统图片上传控制层
 * @Author yzchao
 * @Date 2022/10/25 14:31
 * @Version V1.0
 */
@RestController
@RequestMapping("/admin/product")
public class FileController {


    @Value("${fileServer.url}")
    private String url;

    @PostMapping("/fileUpload")
     public Result<String> fileUpload(@RequestParam("file") MultipartFile file)  {
        String path = FileUpLoadUtils.fileUpload(file);
        if(StringUtils.isEmpty(path)){
            throw new RuntimeException("新增图片失败");
        }

        //返回结果 [0]= 组名 [1] = 全量路径和文件名
        return Result.ok(url + path);
    }
}
