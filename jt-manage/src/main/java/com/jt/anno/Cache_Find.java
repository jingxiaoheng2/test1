package com.jt.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Retention(RetentionPolicy.RUNTIME) //在运行期生效
@Target(ElementType.METHOD) //该注解对方法生效
public @interface Cache_Find {
	String key() default ""; //用户可以不写,如果为空串，自动生成key
	int seconds() default 0; //0表示用户设置该数据不需要超时时间
	                         //，如果不等于0则说明用户自己定义了超时时间

}
