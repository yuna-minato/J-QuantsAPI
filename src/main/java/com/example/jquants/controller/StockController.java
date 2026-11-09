package com.example.jquants.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.jquants.model.Stock;
import com.example.jquants.model.StockPrice;
import com.example.jquants.service.StockService;

/**
 * SpringBootが呼びつけるとStockServiceを呼び出し、返ってきた結果をVueに返すクラス
 * @author yuna.minato
 */
@RestController
public class StockController {
    /** アプリ側の処理を担当する  */
    private final StockService stockService;

    /**
     * コンストラクタ
     * @param stockService StockService
     * （DI（依存性注入））
     */
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /**
     * 銘柄一覧表示画面用
     * @return 銘柄一覧
     */
    @GetMapping("/api/stocks")
    public List<Stock> getStocks() {
        return stockService.getStocks();
    }

    /**
     * 株価四本値取得画面用
     * @param code 銘柄コード
     * @param startDate 取得開始日
     * @param endDate 取得終了日
     * @return 株価四本値一覧
     */
    @GetMapping("/api/stockPrices")
    public List<StockPrice> getStockPrices(@RequestParam String code, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return stockService.getStockPrices(code, startDate, endDate);
    }
}