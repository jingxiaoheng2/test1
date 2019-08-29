package com.jt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jt.pojo.Item;
import com.jt.pojo.ItemDesc;
import com.jt.service.ItemService;
import com.jt.vo.EasyUITable;
import com.jt.vo.SysResult;

//@Controller
@RestController
@RequestMapping("/item")
public class ItemController {
	
	@Autowired
	private ItemService itemService;
	
	/**
	 * 根据分页查询商品信息
	 * http://localhost:8091/item/query?page=1&rows=50
	 * 作业:
	 * 	 根据分页参数,实现商品列表展现
	 */
	@RequestMapping("/query")
	public EasyUITable findItemByPage(Integer page,Integer rows) {
		
		return itemService.findItemByPage(page,rows);
	}
	
	/**
	 * 实现商品数据新增
	 * 改进方法:定义全局异常处理机制
	 * 	
	 *  <input id="itemId" name="itemId" value="100"/>
	 *  itemId=100				
	 *	二:实现商品描述的新增
	 */
	@RequestMapping("/save")
	public SysResult saveItem(Item item,ItemDesc itemDesc) {
		
		itemService.saveItem(item,itemDesc);
		return SysResult.success();
	}
	
	/**
	 * 实现商品信息修改
	 */
	@RequestMapping("/update")
	public SysResult updateItem(Item item,ItemDesc itemDesc) {
		
		itemService.updateItem(item,itemDesc);
		return SysResult.success();
	}
	
	/**
	 * 实现商品的下架操作
	 * $.post("/item/instock",{"ids":1001,1002}
	 * 
	 * 规则:
	 * 	如果用户传参使用","号分割参数,则springMVC
	 * 接收参数时可以使用数组接收.由程序内部实现自动
	 * 转化
	 * ids:1001,1002,1003
	 */
	@RequestMapping("/instock")
	public SysResult instock(Long[] ids) {
		int status = 2;	//表示下架
		itemService.upateStatus(ids,status);
		return SysResult.success();
	}
	
	@RequestMapping("/reshelf")
	public SysResult reshelf(Long[] ids) {
		int status = 1;	//表示上架
		itemService.upateStatus(ids,status);
		return SysResult.success();
	}
	
	/**
	  * 根据商品详情信息获取服务器数据
	 */
	@RequestMapping("/query/item/desc/{itemId}")
	public SysResult findItemDescById
	(@PathVariable Long itemId) {
		
		ItemDesc itemDesc = 
				itemService.findItemDescById(itemId);
		return SysResult.success(itemDesc);
	}
	
	
	/**
	 * 实现删除
	 */
	@RequestMapping("/delete")
	public SysResult delteItems(Long[] ids) {
		
		itemService.deleteItems(ids);
		return SysResult.success();
	}
	
	
	
}
