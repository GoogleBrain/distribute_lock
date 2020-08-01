package cn.k.distributelock.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class UnLockTestController {

    @RequestMapping("/king")
    public  String nowTime(){
        try(AutounLockTest test = new AutounLockTest("asd")){
          if(test.getLock().equals("asd")){
              System.out.println("我获取到了名字>>..........");
          }else{
              int b=10/0;
          }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "当前时间>>>>"+new Date();
    }
}
