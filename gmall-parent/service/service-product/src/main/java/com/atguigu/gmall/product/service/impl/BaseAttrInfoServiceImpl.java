package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.exception.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName BaseAttrInfoServiceImpl
 * @Description 平台属性相关的接口类的实现类
 * @Author yzchao
 * @Date 2022/10/23 0:08
 * @Version V1.0
 */
@Service
public class BaseAttrInfoServiceImpl implements BaseAttrInfoService {

    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;

    /**
     * @param id
     * @Description 主键查询
     * @return: com.atguigu.gmall.model.product.BaseAttrInfo
     * @Date 2022/10/22 16:55
     */
    @Override
    public BaseAttrInfo getBaseAttrInfo(Long id){
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectById(id);
        return baseAttrInfo;
    }

    /**
     * @Description 查询全部
     * @return: java.util.List<com.atguigu.gmall.model.product.BaseAttrInfo>
     * @Date 2022/10/22 16:56
     */
    @Override
    public List<BaseAttrInfo> getAll() {
        return baseAttrInfoMapper.selectList(null);
    }

    /**
     * @param baseAttrInfo
     * @Description 新增
     * @Date 2022/10/22 16:57
     */
    @Override
    public void add(BaseAttrInfo baseAttrInfo) {
        //参数校验
        if(null == baseAttrInfo){
            throw new RuntimeException("参数错误");
        }
        //新增操作
        int insert = baseAttrInfoMapper.insert(baseAttrInfo);
        if(insert <= 0){
            throw new RuntimeException("新增失败,请重试");
        }
    }

    /**
     * @param baseAttrInfo
     * @Description 更新
     * @param: baseAttrInfo
     * @Date 2022/10/22 21:43
     */
    @Override
    public void update(BaseAttrInfo baseAttrInfo) {
        //参数校验 是否非空 查看数据库设计
        if(baseAttrInfo == null || baseAttrInfo.getAttrName() == null){
            throw new RuntimeException("参数错误");
        }
        //修改
        int update = baseAttrInfoMapper.updateById(baseAttrInfo);
        if(update < 0){
            throw new RuntimeException("修改失败,请重试");
        }
    }

    /**
     * @param id
     * @Description 删除
     * @Date 2022/10/22 21:44
     */
    @Override
    public void delete(Long id) {
        //参数校验
        if(null == id){
            return;
        }
        //删除
        int delete = baseAttrInfoMapper.deleteById(id);
        if (delete <= 0) {
            throw new RuntimeException("删除失败,请重试");
        }
    }

    /**
     * @param baseAttrInfo
     * @Description 条件查询
     * @param: baseAttrInfo
     * @return: java.util.List<com.atguigu.gmall.model.product.BaseAttrInfo>
     * @Date 2022/10/22 23:59
     */
    @Override
    public List<BaseAttrInfo> search(BaseAttrInfo baseAttrInfo) {
        //若baseAttrInfo为空 则查询全部数据
        if(null == baseAttrInfo){
            baseAttrInfoMapper.selectList(null);
        }
        //构建查询条件
        LambdaQueryWrapper<BaseAttrInfo> lambdaQueryWrapper = buildQueryParam(baseAttrInfo);
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoMapper.selectList(lambdaQueryWrapper);
        return baseAttrInfoList;
    }

    /**
     * @param page
     * @param size
     * @Description 分页查询
     * @param: page
     * @param: size
     * @return: com.baomidou.mybatisplus.core.metadata.IPage<com.atguigu.gmall.model.product.BaseAttrInfo>
     * @Date 2022/10/23 0:00
     */
    @Override
    public IPage<BaseAttrInfo> search(Long page, Long size) {
        if (page == null) {
            page = 1L;
        }
        if (size == null) {
            size = 1L;
        }
        IPage<BaseAttrInfo> pageModel = new Page<>(page, size);
        IPage<BaseAttrInfo> IPage = baseAttrInfoMapper.selectPage(pageModel, null);
        return IPage;
    }

    /**
     * @param page
     * @param size
     * @param baseAttrInfo
     * @Description 分页条件查询
     * @param: page
     * @param: size
     * @param: baseAttrInfo
     * @return: com.baomidou.mybatisplus.core.metadata.IPage<com.atguigu.gmall.model.product.BaseAttrInfo>
     * @Date 2022/10/23 0:01
     */
    @Override
    public IPage<BaseAttrInfo> search(Long page, Long size, BaseAttrInfo baseAttrInfo) {
        if (page == null) {
            page = 1L;
        }
        if (size == null) {
            size = 1L;
        }

        //构建查询条件
        LambdaQueryWrapper<BaseAttrInfo> lambdaQueryWrapper = buildQueryParam(baseAttrInfo);
        //执行查询返回结果
        IPage<BaseAttrInfo> pageModel = new Page<>(page, size);
        IPage<BaseAttrInfo> IPage = baseAttrInfoMapper.selectPage(pageModel, lambdaQueryWrapper);

        return IPage;
    }




    /**
     * @Description 构建查询条件
     * @param: baseAttrInfo
     * @return: com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.atguigu.gmall.model.product.BaseAttrInfo>
     * @Date 2022/10/23 0:32
     */
    private LambdaQueryWrapper<BaseAttrInfo> buildQueryParam(BaseAttrInfo baseAttrInfo) {
        //baseAttrInfo不为空 查条件询  //构建条件
        LambdaQueryWrapper<BaseAttrInfo> lambdaQueryWrapper = new LambdaQueryWrapper<BaseAttrInfo>();
        if(!StringUtils.isEmpty(baseAttrInfo.getAttrName())){
            lambdaQueryWrapper.like(BaseAttrInfo::getAttrName,baseAttrInfo.getAttrName());
        }
        if(null != baseAttrInfo.getCategoryId()){
            lambdaQueryWrapper.eq(BaseAttrInfo ::getCategoryId,baseAttrInfo.getCategoryId());
        }
        return lambdaQueryWrapper;
    }



}
