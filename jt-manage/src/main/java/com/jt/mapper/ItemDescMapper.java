package com.jt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jt.pojo.ItemDesc;

public interface ItemDescMapper extends BaseMapper<ItemDesc>{
	
	//@Delete("delete from tb_item where id in ([1,2,34,4,5])")
	void deleteItems(Long[] ids);

}
