package cn.k.distributelock.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 通过zookeeper实现分布式锁。
 */
@RestController
public class ZkController {

    @RequestMapping("/zklock")
    public String zookeeperLock(){
        System.out.println("我进入了方法........");
        try(ZkLock zkLock = new ZkLock()){
            if(zkLock.getLock("order")){
                System.out.println("我获得了锁");
                Thread.sleep(15000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("方法执行完成.....");
        return "完成";
    }
}
