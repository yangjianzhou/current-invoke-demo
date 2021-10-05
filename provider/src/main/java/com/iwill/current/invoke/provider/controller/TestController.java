package com.iwill.current.invoke.provider.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping("get-name")
    public String getName(){
        try{
            Thread.sleep(500L);
        }catch (Exception exp){

        }
        return "zhangSan";
    }

    @GetMapping("get-address")
    public String getAddress(){
        try{
            Thread.sleep(400L);
        }catch (Exception exp){

        }
        return "shanghai";
    }

    @GetMapping("get-age")
    public int getAge(){
        try{
            Thread.sleep(350L);
        }catch (Exception exp){

        }
        return 20;
    }

    @GetMapping("get-mobile")
    public String getMobile(){
        try{
            Thread.sleep(350L);
        }catch (Exception exp){

        }
        return "13800138000";
    }

}
