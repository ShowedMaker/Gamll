package com.atguigu.gmall.list.dao;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @InterfaceName GoodsDao
 * @Description 商品上下架服务的dao层
 * @Author yzchao
 * @Date 2022/11/2 16:44
 * @Version V1.0
 */
@Repository
public interface GoodsDao extends ElasticsearchRepository<Goods,Long> {



}
