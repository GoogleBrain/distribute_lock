package cn.k.distributelock.controller;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
public class RedisDistributeController {

    @Resource
    private RedisTemplate redisTemplate;

    @RequestMapping("/redislock")
    public String redisLock() {
        System.out.println("进入了方法");

        String key = "redisKey";
        String value = UUID.randomUUID().toString();

        RedisCallback redisCallback = redisConnection -> {

            //设置NX
            RedisStringCommands.SetOption option = RedisStringCommands.SetOption.ifAbsent();
            //设置过期时间
            Expiration seconds = Expiration.seconds(30);
            //序列化key
            byte[] redisKey = redisTemplate.getKeySerializer().serialize(key);
            //序列化value
            byte[] redisValue = redisTemplate.getValueSerializer().serialize(value);
            Boolean set = redisConnection.set(redisKey, redisValue, seconds, option);
            return set;
        };
        boolean execute = (boolean) redisTemplate.execute(redisCallback);
        if (execute) {
            System.out.println("我进入了锁。。。。。");
            try {
                Thread.sleep(15000);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                String script = "if redis.call(\"get\",KEYS[1])==ARGV[1]then \n" +
                        "return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "return 0\n" +
                        "end";
                RedisScript<Boolean> redisScript = RedisScript.of(script, Boolean.class);
                List keys = Arrays.asList(key);
                Boolean execute1 = (Boolean) redisTemplate.execute(redisScript, keys, value);
                System.out.println("释放锁得结果" + execute1);
            }
        }else{
            System.out.println("获得锁失败>>>>>");
        }


        return "执行完成";
    }

    @RequestMapping("/redislock2")
    public String redisLock2() {
        System.out.println("进入了方法2");

        String key = "redisKey";
        String value = UUID.randomUUID().toString();

        RedisCallback redisCallback = redisConnection -> {

            //设置NX
            RedisStringCommands.SetOption option = RedisStringCommands.SetOption.ifAbsent();
            //设置过期时间
            Expiration seconds = Expiration.seconds(30);
            //序列化key
            byte[] redisKey = redisTemplate.getKeySerializer().serialize(key);
            //序列化value
            byte[] redisValue = redisTemplate.getValueSerializer().serialize(value);
            Boolean set = redisConnection.set(redisKey, redisValue, seconds, option);
            return set;
        };
        boolean execute = (boolean) redisTemplate.execute(redisCallback);
        if (execute) {
            System.out.println("我进入了锁。。。。。2");
            try {
                Thread.sleep(15000);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                String script = "if redis.call(\"get\",KEYS[1])==ARGV[1]then \n" +
                        "return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "return 0\n" +
                        "end";
                RedisScript<Boolean> redisScript = RedisScript.of(script, Boolean.class);
                List keys = Arrays.asList(key);
                Boolean execute1 = (Boolean) redisTemplate.execute(redisScript, keys, value);
                System.out.println("释放锁得结果2" + execute1);
            }
        }else{
            System.out.println("获得锁失败2>>>>>");
        }


        return "执行完成2";
    }
}
