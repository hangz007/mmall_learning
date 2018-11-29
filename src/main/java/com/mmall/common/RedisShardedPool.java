package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenzx on 2018/11/29.
 */
public class RedisShardedPool {

    private static ShardedJedisPool pool ; // shardedjedis连接池
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20")); // 最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","10"));// 在jedispool中最大的idle状态(空闲的)jedis实例个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","2")); // 在jedispool中最小的idle状态(空闲的)jedis实例个数
    private static String  redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));
    private static String  redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));
    private static Boolean testOnBorrow = Boolean.valueOf(PropertiesUtil.getProperty("redis.test.borrow","true")); // 在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true，则得到的jedis实例肯定是可用的
    private static Boolean testOnReturn = Boolean.valueOf(PropertiesUtil.getProperty("redis.test.return","true")); // 在return一个jedis实例的时候，是否要进行验证操作，如果赋值true，则放回jedispool的jedis实例肯定是可用的

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        // 默认最大连接数8
        config.setMaxTotal(maxTotal);
        // 默认最大空闲连接数为8
        config.setMaxIdle(maxIdle);
        // 默认最小空闲连接数为0
        config.setMinIdle(minIdle);
        // testOnBorrow默认值为false
        config.setTestOnBorrow(testOnBorrow);
        // testOnReturn默认值为false
        config.setTestOnReturn(testOnReturn);
        // 连接耗尽时，是否阻塞，false会抛出异常，true则阻塞值超时，默认值为true
        config.setBlockWhenExhausted(true);
        // 初始化连接池
        JedisShardInfo info1 = new JedisShardInfo(redis1Ip,redis1Port,1000*2);
        JedisShardInfo info2 = new JedisShardInfo(redis2Ip,redis2Port,1000*2);
        List<JedisShardInfo> jedisShardlist = new ArrayList<JedisShardInfo>(2);
        jedisShardlist.add(info1);
        jedisShardlist.add(info2);
        // 一致性算法
        pool = new ShardedJedisPool(config,jedisShardlist, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static{
        initPool();
    }

    public static ShardedJedis getJedis() {
        return pool.getResource();
    }

    public static void returnResource(ShardedJedis jedis) {
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(ShardedJedis jedis) {
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        ShardedJedis  jedis = pool.getResource();
        for(int i =0;i<10;i++) {
            jedis.set("key"+i,"value"+i);
        }
        returnResource(jedis);
        //pool.destroy();
        System.out.println("program is end");
    }

}
