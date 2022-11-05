package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName BaseAttrInfoController
 * @Description 平台属性相关的接口
 * @Author yzchao
 * @Date 2022/10/22 16:35
 * @Version V1.0
 */
@RestController
@RequestMapping("/api/baseAttrInfo")
public class BaseAttrInfoController {

    @Resource
    private BaseAttrInfoService baseAttrInfoService;

    /**
     * @Description  主键查询
     * @param: id
     * @return: com.atguigu.gmall.common.result.Result
     * @Date 2022/10/23 1:08
     */
    @GetMapping("/getBaseAttrInfo/{id}")
    public Result getBaseAttrInfo(@PathVariable Long id){
        BaseAttrInfo baseAttrInfo = baseAttrInfoService.getBaseAttrInfo(id);
        return Result.ok(baseAttrInfo);
    }

    /**
     * @Description 查询全部
     * @return: com.atguigu.gmall.common.result.Result
     * @Date 2022/10/23 1:08
     */
    @GetMapping("/getAll")
    public Result getAll(){
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoService.getAll();
        return Result.ok(baseAttrInfoList);
    }

    /**
     * @Description  新增
     * @param: baseAttrInfo
     * @return: com.atguigu.gmall.common.result.Result
     * @Date 2022/10/23 1:09
     */
    @PostMapping()
    public Result add(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.add(baseAttrInfo);
        return Result.ok();
    }

    /**
     * @Description  更新
     * @param: baseAttrInfo
     * @return: com.atguigu.gmall.common.result.Result
     * @Date 2022/10/23 1:09
     */
    @PutMapping()
    public Result update(@RequestBody BaseAttrInfo baseAttrInfo) {
        baseAttrInfoService.update(baseAttrInfo);
        return Result.ok();
    }

    /**
     * @Description  删除
     * @param: id
     * @return: com.atguigu.gmall.common.result.Result
     * @Date 2022/10/23 1:09
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id){
        baseAttrInfoService.delete(id);
        return Result.ok();
    }

    /**
     * @Description  分页查询
     * @param: page
     * @param: size
     * @return: com.atguigu.gmall.common.result.Result
     * @Date 2022/10/23 1:09
     */
    @GetMapping("/page/{page}/{size}")
    public Result search(@PathVariable Long page,
                         @PathVariable Long size){
        IPage<BaseAttrInfo> baseAttrInfoIPage = baseAttrInfoService.search(page, size);
        return Result.ok(baseAttrInfoIPage);
    }

    /**
     * @Description  条件查询
     * @param: baseAttrInfo
     * @return: com.atguigu.gmall.common.result.Result
     * @Date 2022/10/23 1:09
     */
    @PostMapping("/search")
    public Result search(@RequestBody BaseAttrInfo baseAttrInfo){
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoService.search(baseAttrInfo);
        return Result.ok(baseAttrInfoList);
    }

    /**
     * @Description  分页查询条件
     * @param: baseAttrInfo
     * @param: page
     * @param: size
     * @return: com.atguigu.gmall.common.result.Result
     * @Date 2022/10/23 1:09
     */
    @PostMapping("/search/{page}/{size}")
    public Result search(@RequestBody BaseAttrInfo baseAttrInfo,
                         @PathVariable Long page,
                         @PathVariable Long size){
        IPage<BaseAttrInfo> baseAttrInfoIPage = baseAttrInfoService.search(page, size, baseAttrInfo);
        return Result.ok(baseAttrInfoIPage);
    }

}
