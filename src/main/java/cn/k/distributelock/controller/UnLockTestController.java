package cn.k.distributelock.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * try()在小括号里实现某个类，这个类实现了AutoCloseable接口，程序执行完了就不用主动调用关闭方法了，程序可以自动实现。
 */
@RestController
public class UnLockTestController {

    @RequestMapping("/king")
    public  String nowTime(){
        try(AutounLockTest test = new AutounLockTest("aaaa")){
          if(test.getLock().equals("asd")){
              System.out.println("我获取到了名字>>..........");
          }else{
              System.out.println("程序出错了。。。。。。。");
              int b=10/0;
          }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "当前时间>>>>"+new Date();
    }
}
