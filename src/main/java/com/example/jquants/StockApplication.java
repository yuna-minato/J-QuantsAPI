package com.example.jquants;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SpringBoot起動クラス
 * @author yuna.minato
 */
@SpringBootApplication
public class StockApplication {
    /**
     * メインメソッド
     * @param args 実行時引数
     */
    public static void main(String[] args) {
        SpringApplication.run(StockApplication.class, args);
    }
}