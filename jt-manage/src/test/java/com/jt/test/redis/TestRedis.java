package com.jt.test.redis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.Transaction;

public class TestRedis {
	/*
	 * 测试string类型操作
	 */
	@Test
	public void test01() {
		Jedis jedis =new Jedis("192.168.60.132",6379);

		//set数据同时给数据添加超时时间
		jedis.setex("1904", 100,"1904版本redis学习");
		//jedis.set("1904", "1904版本redis学习");
		String result = jedis.get("1904");
		//为key设定超时时间
		//jedis.expire("1904", 100);
		//System.out.println(result);

		//需要将key中的值覆盖
		jedis.set("1904", "1904班毕业啦");
		System.out.println(jedis.get("1904"));
		jedis.del("1904");
		//需求，如果当前key已经存在，则不能修改
		jedis.setnx("1904", "年薪百万");
		System.out.println("获取修改之后的值："+jedis.get("1904"));

		//需求：1.添加超时时间  2.不允许重复操作
		jedis.set("1904a", "中午吃撒后", "NX", "EX", 100);
		System.out.println(jedis.get("1904a"));
	}
	@Test
	public void testHash() {
		Jedis jedis =new Jedis("192.168.60.132",6379);
		jedis.hset("person", "id", "100");
		jedis.hset("person", "name", "邵朝晖");
		jedis.hset("person", "age", "89");
		System.out.println(jedis.hgetAll("person"));
	}
	@Test
	public void testList() {
		Jedis jedis =new Jedis("192.168.60.132",6379);
		jedis.lpush("list", "1,2,3,4,5");
		System.out.println(jedis.rpop("list"));
		jedis.lpush("list", "1","2","3","4","5");
		System.out.println(jedis.rpop("list"));
	}
	@Test
	public void testTx() {
		Jedis jedis =new Jedis("192.168.60.132",6379);
		Transaction transaction = jedis.multi();
		try {
			transaction.set("aa", "aaa");
			transaction.set("bb", "bbb");
			transaction.set("cc", "ccc");
			transaction.exec();
		}catch (Exception e) {
			transaction.discard();
		}

	}
	/*
	 * redis分片测试
	 */
	@Test
	public void testShards() {
		String host="192.168.60.133";
		List<JedisShardInfo> shards=new ArrayList<JedisShardInfo>();
		shards.add(new JedisShardInfo(host,6379));
		shards.add(new JedisShardInfo(host,6380));
		shards.add(new JedisShardInfo(host,6381));
		ShardedJedis jedis = new ShardedJedis(shards);
		jedis.set("1904", "分片操作");
		System.out.println(jedis.get("1904"));
	}
	/*
	 * 测试哨兵
	 */
	@Test
	public void testSentinel() {
		Set<String> sentinels = new HashSet<>();
		sentinels.add("192.168.60.133:26379");
		JedisSentinelPool pool=new JedisSentinelPool("mymaster", sentinels);
	    Jedis jedis = pool.getResource();
	    jedis.set("1904", "aaaa");
	    System.out.println(jedis.get("1904"));
	}
	@Test
	public void testCluster() {
	    Set<HostAndPort> nodes=new HashSet<>();
	    nodes.add(new HostAndPort("192.168.60.133", 7000));
	    nodes.add(new HostAndPort("192.168.60.133", 7001));
	    nodes.add(new HostAndPort("192.168.60.133", 7002));
	    nodes.add(new HostAndPort("192.168.60.133", 7003));
	    nodes.add(new HostAndPort("192.168.60.133", 7004));
	    nodes.add(new HostAndPort("192.168.60.133", 7005));
		
	    JedisCluster cluster=new JedisCluster(nodes);
	    cluster.set("1904", "集群搭建完成");
	    System.out.println(cluster.get("1904"));
	}
}
