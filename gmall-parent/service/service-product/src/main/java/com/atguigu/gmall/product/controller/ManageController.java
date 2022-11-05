package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.BaseTradeMarkMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName ManageController
 * @Description 后台管理控制台
 * @Author yzchao
 * @Date 2022/10/24 16:04
 * @Version V1.0
 */

@RestController
@RequestMapping("/admin/product")
public class ManageController {

    @Resource
    private ManageService manageService;
    


    /**
     * @Description 查询所有的一级分类
     * @Date 17:56 2022/10/24
     * @Param []
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/getCategory1")
    public Result getCategory1(){
        List<BaseCategory1> list = manageService.getCategory1();
        return Result.ok(list);
    }

    /**
     * @Description 根据一级分类查询所有的二级分类
     * @Date 18:02 2022/10/24
     * @Param [category1Id]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable Long category1Id) {
        List<BaseCategory2> list = manageService.getCategory2(category1Id);
        return Result.ok(list);
    }

    /**
     * @Description 根据二级分类查询所有的三级分类
     * @Date 18:05 2022/10/24
     * @Param [category2Id]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable Long category2Id) {
        List<BaseCategory3> list = manageService.getCategory3(category2Id);
        return Result.ok(list);
    }

    /**
     * @Description 保存平台属性信息
     * @Date 18:18 2022/10/24
     * @Param [baseAttrInfo]
     * @return com.atguigu.gmall.common.result.Result<com.atguigu.gmall.model.product.BaseAttrInfo>
     */    
    @PostMapping("/saveAttrInfo")
    public Result<BaseAttrInfo> saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        manageService.save(baseAttrInfo);
        return Result.ok();
    }

    /**
     * @Description 根据分类id获取平台属性
     * @Date 20:54 2022/10/24
     * @Param [category1Id, category2Id, category3Id]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable Long category1Id,
                                  @PathVariable Long category2Id,
                                  @PathVariable Long category3Id){
        List<BaseAttrInfo> list = manageService.getBaseAttrInfo(category3Id);
        return Result.ok(list);
    }


    /**
     * @Description 根据ID删除平台属性
     * @Date 12:45 2022/10/25
     * @Param [id]
     * @return com.atguigu.gmall.common.result.Result
     */
    @DeleteMapping("/deleteBaseAttrInfo/{id}")
    public Result deleteBaseAttrInfo(@PathVariable Long id){
        manageService.deleteBaseAttrInfo(id);
        return Result.ok();
    }

    /**
     * @Description 获取添加SPU时品牌值（所有值）
     * @Date 12:49 2022/10/25
     * @Param []
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/baseTrademark/getTrademarkList")
    public Result getBaseTradeMark(){
        List<BaseTrademark> baseTradeMark = manageService.getBaseTradeMark();
        return Result.ok(baseTradeMark);
    }

    /**
     * @Description 后台管理系统销售属性查询
     * @Date 14:09 2022/10/25
     * @Param []
     * @return com.atguigu.gmall.common.result.Result<com.atguigu.gmall.model.product.BaseSaleAttr>
     */
    @GetMapping("/baseSaleAttrList")
    public Result baseSaleAttrList(){
        List<BaseSaleAttr> baseSaleAttrList = manageService.getBaseSaleAttrList();
        return Result.ok(baseSaleAttrList);
    }

    /**
     * @Description 上传spu信息
     * @Date 15:40 2022/10/25
     * @Param [baseSaleAttr]
     * @return com.atguigu.gmall.common.result.Result
     */
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        manageService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

    /**
     * @Description SpuInfo分页条件查询
     * @Date 18:54 2022/10/25
     * @Param [page, size, category3Id]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/{page}/{size}")
    public Result getSpuInfoByPage(@PathVariable Integer page,
                                   @PathVariable Integer size,Long category3Id){
        IPage<SpuInfo> spuInfoByPage = manageService.getSpuInfoByPage(page, size, category3Id);
        return Result.ok(spuInfoByPage);
    }

    /**
     * @Description 根据spu的id查询这个spu的全部销售属性和每个销售属性对应的值列表
     * @Date 20:01 2022/10/25
     * @Param [spuId]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable Long spuId){
        List<SpuSaleAttr> list = manageService.getSpuSaleAttrList(spuId);
        return Result.ok(list);
    }

    /**
     * @Description 根据spu的id查询这个spu的全部图片列表
     * @Date 20:36 2022/10/25
     * @Param [spuId]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/spuImageList/{spuId}")
    public Result getSpuImageList(@PathVariable Long spuId){
        return Result.ok(manageService.getSpuImageList(spuId));
    }

    /**
     * @Description 上传sku信息
     * @Date 21:09 2022/10/25
     * @Param [skuInfo]
     * @return com.atguigu.gmall.common.result.Result
     */
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        manageService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    /**
     * @Description 分页查询sku的信息
     * @Date 23:41 2022/10/25
     * @Param [page, size, category3Id]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("list/{page}/{size}")
    public Result getSkuInfoByPage(@PathVariable Integer page,
                                   @PathVariable Integer size){
        IPage<SkuInfo> skuInfoByPage = manageService.getSkuInfoByPage(page, size);
        return Result.ok(skuInfoByPage);
    }

    /**
     * @Description 上架
     * @Date 14:11 2022/10/26
     * @Param [skuId]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/onSale/{skuId}")
    public Result OnSale(@PathVariable Long skuId){
        manageService.OnSaleOrCancelSale(skuId, (short) 1);
        return Result.ok();
    }

    /**
     * @Description 下架
     * @Date 14:12 2022/10/26
     * @Param [skuId]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/cancelSale/{skuId}")
    public Result CancelSale(@PathVariable Long skuId){
        manageService.OnSaleOrCancelSale(skuId, (short) 0);
        return Result.ok();
    }

    /**
     * @Description 新增品牌
     * @Date 14:54 2022/10/26
     * @Param [baseTrademark]
     * @return com.atguigu.gmall.common.result.Result
     */
    @PostMapping("/baseTrademark/save")
    public Result addBaseTradeMarkad(@RequestBody BaseTrademark baseTrademark){
        manageService.addOrUpdateBaseTrademark(baseTrademark);
        return Result.ok();
    }

    /**
     * @Description 修改品牌
     * @Date 15:16 2022/10/26
     * @Param [baseTrademark]
     * @return com.atguigu.gmall.common.result.Result
     */
    @PutMapping("/baseTrademark/update")
    public Result updateBaseTradeMark(@RequestBody BaseTrademark baseTrademark){
        manageService.addOrUpdateBaseTrademark(baseTrademark);
        return Result.ok();
    }

    /**
     * @Description 分页查询品牌列表
     * @Date 15:07 2022/10/26
     * @Param [page, size]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/baseTrademark/{page}/{size}")
    public Result getBaseTrademarkByPage(@PathVariable Integer page,@PathVariable Integer size){
        IPage<BaseTrademark> pageModel = manageService.getBaseTradeMarkByPage(page, size);
        return Result.ok(pageModel);
    }

    /**
     * @Description 根据品牌ID查询品牌
     * @Date 15:35 2022/10/26
     * @Param [id]
     * @return com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/baseTrademark/get/{id}")
    public Result getBaseTradeMarkById(@PathVariable Long id){
        BaseTrademark baseTrademark = manageService.getBaseTradeMarkById(id);
        return Result.ok(baseTrademark);
    }

    /**
     * @Description 根据品牌ID删除品牌
     * @Date 15:43 2022/10/26
     * @Param [id]
     * @return com.atguigu.gmall.common.result.Result
     */
    @DeleteMapping("/baseTrademark/remove/{id}")
    public Result removeBaseTradeMark(@PathVariable Long id){
        manageService.removeBaseTradeMark(id);
        return Result.ok();
    }

}
