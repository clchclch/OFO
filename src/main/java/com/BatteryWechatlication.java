package com;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@ServletComponentScan
@MapperScan("com.*.*.dao")
@SpringBootApplication
public class BatteryWechatlication {
	
    public static void main(String[] args) {
        SpringApplication.run(BatteryWechatlication.class, args);
        System.out.println("ヾ(◍°∇°◍)ﾉﾞ   HUABANG启动成功      ヾ(◍°∇°◍)ﾉﾞ\n");
    }
 

}