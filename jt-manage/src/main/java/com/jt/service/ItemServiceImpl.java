package com.jt.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jt.mapper.ItemDescMapper;
import com.jt.mapper.ItemMapper;
import com.jt.pojo.Item;
import com.jt.pojo.ItemDesc;
import com.jt.vo.EasyUITable;

@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private ItemMapper itemMapper;
	@Autowired
	private ItemDescMapper itemDescMapper;

	@Override
	public EasyUITable findItemByPage(Integer page, Integer rows) {

		int total = itemMapper.selectCount(null);	//获取商品信息记录总数

		/**
		 * 将最近添加的数据最先展现 按照updated排序
		 * SELECT * FROM tb_item ORDER BY updated DESC LIMIT 起始位置,每页展现记录数
			/*查询第一页 每页20条
			SELECT * FROM tb_item ORDER BY updated DESC LIMIT 0,20;    0-19
			/*查询第二页 每页20条
			SELECT * FROM tb_item ORDER BY updated DESC LIMIT 20,20;   20-39 
			/*查询第三页 每页20条
			SELECT * FROM tb_item ORDER BY updated DESC LIMIT 40,20;   40-59 
			/*查询第N页 每页20条
			SELECT * FROM tb_item ORDER BY updated DESC LIMIT (n-1)*rows,rows;  
		 */
		int start = (page-1)*rows;
		List<Item> itemList = 
				itemMapper.findItemByPage(start,rows);
		return new EasyUITable(total, itemList);
	}

	@Transactional	//控制事务
	@Override
	public void saveItem(Item item,ItemDesc itemDesc) {
		item.setStatus(1)	//表示正常
			.setCreated(new Date())
			.setUpdated(item.getCreated());
		itemMapper.insert(item);
		//只有Item入库之后,才能获取主键id值
		//能否实现数据入库之后,将主键Id自动返回/封装
		
		//完成商品详情入库
		itemDesc.setItemId(item.getId())
				.setCreated(item.getCreated())
				.setUpdated(item.getCreated());
		itemDescMapper.insert(itemDesc);
				
	}

	@Transactional
	@Override
	public void updateItem(Item item,ItemDesc itemDesc) {
		
		item.setUpdated(new Date());
		itemMapper.updateById(item);
		//ItemDesc   itemDesc属性/itemId/updated不为null
		itemDesc.setItemId(item.getId())
				.setUpdated(item.getUpdated());
		//根据主键itemId更新数据!!!
		itemDescMapper.updateById(itemDesc);
	}

	
	/**
	 * 批量更新
	 */
	@Override
	public void upateStatus(Long[] ids, int status) {
		Item item = new Item();
		item.setStatus(status)
			.setUpdated(new Date());
		//sql: update tb_item set status=2 where id in (1001,1002)
		UpdateWrapper<Item> updateWrapper = new UpdateWrapper<Item>();
		List<Long> idList = Arrays.asList(ids);
		updateWrapper.in("id", idList);
		itemMapper.update(item, updateWrapper);
	}

	/**
	 * 利用xml文件实现删除
	 * 同时删除2张表数据库
	 */
	@Transactional
	@Override
	public void deleteItems(Long[] ids) {
		List<Long> idsList = Arrays.asList(ids);
		itemMapper.deleteBatchIds(idsList);
		itemDescMapper.deleteItems(ids);
		
	}
	
	@Override
	public ItemDesc findItemDescById(Long itemId) {
		
		return itemDescMapper.selectById(itemId);
	}
	
	@Override
	public Item findItemById(Long itemId) {
		
		return itemMapper.selectById(itemId);
	}
	
}
