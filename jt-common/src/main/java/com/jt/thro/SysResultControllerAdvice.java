package com.jt.thro;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.jt.vo.SysResult;
import lombok.extern.slf4j.Slf4j;

//@ControllerAdvice	//定义全局异常处理机制
@RestControllerAdvice //该注解对Controller层生效
@Slf4j				//引入日志API
public class SysResultControllerAdvice {
	
	//当发生什么异常时使用该处理方式
	@ExceptionHandler(RuntimeException.class)
	//@ResponseBody
	public SysResult sysResultException(Exception exception) {
		exception.printStackTrace();
		log.error("服务器异常:"+exception.getMessage());
		return SysResult.fail();
	}
}
