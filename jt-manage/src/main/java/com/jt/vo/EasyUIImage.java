package com.jt.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class EasyUIImage {
	private Integer error=0;  //1表示文件上传失败
	private String  url;	  //图片访问地址
	private Integer width;
	private Integer height;	  //设定跨度高度	
}
