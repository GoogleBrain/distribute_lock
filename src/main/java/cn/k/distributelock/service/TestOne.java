package cn.k.distributelock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class TestOne {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 定时任务先暂时去掉，要不然程序一直跑。启动的时候把注释去掉。
     */
//    @Scheduled(cron = "0/5 * * * * ?")
    public void sendMsg() {
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
            System.out.println("我进入了锁test one。。。。。" + new Date());
            try {
                System.out.println("发送短信给test one1760031xxxx" + new Date());
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
                System.out.println("释放锁得结果test one" + execute1 + new Date());
            }
        } else {
            System.out.println("获得锁失败test one>>>>>" + new Date());
        }
        System.out.println("---------------------------------");
    }
}