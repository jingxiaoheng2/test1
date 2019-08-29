package com.jt.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.jt.anno.Cache_Find;
import com.jt.util.ObjectMapperUtil;

import redis.clients.jedis.JedisCluster;


@Component  //将对象交给spring容器管理
@Aspect     //表示切面    切面=切入点+通知
public class CacheAspect {
	@Autowired(required=false)  //表示调用时注入
	private JedisCluster jedis;
	/*
	 * 环绕通知：
	 * 1.返回值 必须为object类型  表示执行完成业务之后返回用户的数据对象
	 * 2.参数 1.必须位于第一位
	 *       2.参数类型必须为ProceedingJoinPoint  因为要控制目标方法
	 *       3.关于注解取值规则：
	 *          springAOP中提供了可以直接获取注解的方法，但是要求参数的名称
	 *          必须一致。否则映射错误
	 *          
	 * 缓存操作
	 *    1.根据key 查询缓存服务器redis
	 */
	@Around("@annotation(cacheFind)")
	public Object around(ProceedingJoinPoint joinPoint,Cache_Find cacheFind) {
		String key=getkey(joinPoint,cacheFind);
		String resultJSON=jedis.get(key);
		Object resultData=null;
		if(StringUtils.isEmpty(resultJSON)) {
			//需要执行真实的目标方法
			try {
				resultData = joinPoint.proceed();//让目标方法执行
				String value=ObjectMapperUtil.toJSON(resultData);
				//判断数据是否永久保存
				if(cacheFind.seconds()>0) {
					jedis.setex(key, cacheFind.seconds(), value);
				}else {
					jedis.set(key, value);
				}
				System.out.println("aop查询成功");
			} catch (Throwable e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}else {
			//由于业务需要，要获取目标方法的返回值类型
			Class returnType=getType(joinPoint);
			//表示redis中有数据，将json转化为对象
			resultData=ObjectMapperUtil.toObject(resultJSON, returnType);
			System.out.println("成功");
		}
		return resultData;
	}
	
	private Class getType(ProceedingJoinPoint joinPoint) {
		//getsignature（）封装了连接点参数的全部内容
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		return signature.getReturnType();//返回值类型
	}
	
	/*
	 * 策略：1.如果用户有key，使用用户的key
	 *     2.如果用户自己定义，则自动生成
	 *        类名+方法名+第一个参数
	 */
	private String getkey(ProceedingJoinPoint joinPoint, Cache_Find cacheFind) {
		String key=cacheFind.key();//默认值为“”
		if(StringUtils.isEmpty(key)) {
			//用户自动生成
			String methodName = joinPoint.getSignature().getName();//方法名
			String ClassName = joinPoint.getSignature().getDeclaringTypeName();//类名
			String arg1 = String.valueOf(joinPoint.getArgs()[0]);
			return ClassName+"."+methodName+"::"+arg1;
		}else {
			return key;
		}
		
	}
}
