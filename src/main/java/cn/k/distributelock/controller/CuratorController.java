package cn.k.distributelock.controller;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 使用curator技术实现zookeeper的分布式锁。CuratorFramework 在主启动类中注入的。
 */
@RestController
public class CuratorController {

    @Resource
    private CuratorFramework client;

    @RequestMapping("/curator")
    public String curator() {
        System.out.println("进入了方法......");
        InterProcessMutex lock = new InterProcessMutex(client, "/order");
        try {
            if (lock.acquire(3, TimeUnit.SECONDS)) {
                try {
                    System.out.println("我获得了锁。。。。。。。。。");
                    Thread.sleep(10000);
                } finally {
                    System.out.println("释放了锁。。。。。");
                    lock.release();
                }
            } else {
                System.out.println("获取锁失败.......");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "完成";
    }
}
