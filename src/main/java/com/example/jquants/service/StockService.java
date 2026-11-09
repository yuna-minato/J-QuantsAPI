package com.example.jquants.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.jquants.client.JQuantsClient;
import com.example.jquants.model.Stock;
import com.example.jquants.model.StockPrice;

/**
 * アプリ側の処理を担当するクラス
 * @author yuna.minato
 */
@Service
public class StockService {
    /** J-Quantsとの通信を行う */
    private final JQuantsClient jQuantsClient;

    /**
     * コンストラクタ
     * @param jQuantsClient JQuantsClient
     */
    public StockService(JQuantsClient jQuantsClient) {
        this.jQuantsClient = jQuantsClient;
    }

    /**
     * 銘柄一覧表示画面用
     * @return 銘柄一覧
     */
    public List<Stock> getStocks() {
        return jQuantsClient.fetchStocks();
    }

    /**
     * 株価四本値取得画面用
     * @param code 銘柄コード
     * @param startDate 取得開始日
     * @param endDate 取得終了日
     * @return 株価四本値一覧
     * @throws IllegalArgumentException 取得可能日に問題がある場合に発生
     */
    public List<StockPrice> getStockPrices(String code, LocalDate startDate, LocalDate endDate) {

        // 開始日が終了日よりも前かどうかチェック
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("開始日は終了日以前の日付を指定してください");
        }

        // 取得可能期間を計算
        // 取得可能最終日
        LocalDate latestDate = LocalDate.now().minusWeeks(12);
        // 取得可能初日
        LocalDate earliestDate = latestDate.minusYears(2);

        // 取得可能期間外を選択された場合
        if (startDate.isBefore(earliestDate) || endDate.isAfter(latestDate)) {
            throw new IllegalArgumentException("取得可能期間内の日付を指定してください");
        }

        // J-Quantsから取得
        return jQuantsClient.fetchStockPrices(code, startDate, endDate);
    }
}