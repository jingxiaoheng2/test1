package com.jt.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jt.vo.EasyUIImage;

@Service
@PropertySource("classpath:/properties/image.properties")
public class FileServiceImpl implements FileService {
	
	@Value("${image.localFileDir}")
	private String localFileDir;	// ="D:\\1-JT\\jt-image";
	@Value("${image.urlPath}")
	private String urlPath;
	/**
	 * 1.判断文件是否为图片   jpg|png|gif
	 * 2.防止恶意程序  判断图片固有属性 宽度和高度
	 * 3.将图片分目录存储       按照时间进行存储  yyyy/MM/dd
	 * 4.解决文件重名问题       UUID
	 */
	@Override
	public EasyUIImage uploadFile(MultipartFile uploadFile) {
		EasyUIImage uiImage = new EasyUIImage();
		//1.获取图片名称       abc.jpg ABC.JPG
		String fileName = uploadFile.getOriginalFilename();
		//应该将用户输入内容统一转化为小写
		fileName = fileName.toLowerCase();
		//2.利用正则表达式判断数据
		if(!fileName.matches("^.+\\.(jpg|png|gif)$")) {
			uiImage.setError(1);//表示不是正经图片
			return uiImage;
		}
		
		//3.获取图片的宽度和高度
		try {
			BufferedImage bufferedImage = 
			ImageIO.read(uploadFile.getInputStream());
			int height = bufferedImage.getHeight();
			int width  = bufferedImage.getWidth();
			if(height==0 || width==0) {
				//如果宽度和高度为0,则程序终止
				uiImage.setError(1);
				return uiImage;
			}
			uiImage.setWidth(width).setHeight(height);
			
			//4.以时间格式进行数据存储 yyyy/MM/dd
			String dateDir = 
				new SimpleDateFormat("yyyy/MM/dd")
				.format(new Date());
			
			//准备文件上传路径D:/1-JT/jt-image/yyyy/MM/dd
			String localdir = localFileDir + dateDir;
			File dirFile = new File(localdir);
			if(!dirFile.exists()) {
				//如果文件路径不存在,则创建文件夹
				dirFile.mkdirs();
			}
			
			/**
			 * 5.重新生成图片名称
			 * 5.1 生成UUID xxxx-xxx
			 * 5.2将-替换""空串
			 * 5.3获取真实图片的后缀  .jpg
			 * 5.4生成真实的图片名称  uuid.jpg
			 */
			String uuid = UUID.randomUUID()
							  .toString()
							  .replace("-","");
			//abc.jpg  从后向前查找第一个.的位置
			//从该位置向后截取数据.   
			String fileType = 
			fileName.substring(fileName.lastIndexOf("."));
			String realFileName =uuid + fileType;
			
			/**
			 * 6.实现文件上传
			 * 6.1准备文件全路径
			 * 		D:\1-JT\jt-image\2019\08\02\ uuid.jpg
			 * 6.2 实现文件上传
			 */
			
			//D:/1-JT/jt-image/yyyy/MM/dd/uuid.jpg
			String realPath = localdir+ "/" + realFileName;
			File realFile = new File(realPath) ;
						
			uploadFile.transferTo(realFile);
			System.out.println("文件上传成功!!!!!!!!");
			
			//http://image.jt.com/yyyy/MM/dd/uuid.jpg
			String url = urlPath + dateDir + "/" + realFileName;
			uiImage.setUrl(url);
			//暂时使用jd商品代替
			//String url = "https://img14.360buyimg.com/n0/jfs/t1/29441/32/10256/129614/5c887364E9ebe340f/89515816d96436d6.jpg";
			//uiImage.setUrl(url);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			uiImage.setError(1);//程序出错.上传终止
		}
		return uiImage;
	}
}
