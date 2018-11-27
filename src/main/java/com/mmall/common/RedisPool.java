package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by shenzx on 2018/11/11.
 */
public class RedisPool {

    private static JedisPool jedisPool ; // jedis连接池
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20")); // 最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","10"));// 在jedispool中最大的idle状态(空闲的)jedis实例个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","2")); // 在jedispool中最小的idle状态(空闲的)jedis实例个数
    private static String  redisIp = PropertiesUtil.getProperty("redis.ip");
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.port"));
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
        jedisPool = new JedisPool(config,redisIp,redisPort,1000*2);
    }

    static{
        initPool();
    }

    public static Jedis getJedis() {
        return jedisPool.getResource();
    }

    public static void returnResource(Jedis jedis) {
            jedisPool.returnResource(jedis);
    }

    public static void returnBrokenResource(Jedis jedis) {
            jedisPool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        Jedis  jedis = jedisPool.getResource();
        jedis.set("geelykey","geelyValue");
        returnResource(jedis);
        jedisPool.destroy();
        System.out.println("program is end");
    }

}
