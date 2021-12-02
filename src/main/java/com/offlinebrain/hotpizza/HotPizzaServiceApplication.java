package com.offlinebrain.hotpizza;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class HotPizzaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotPizzaServiceApplication.class, args);
    }

}
