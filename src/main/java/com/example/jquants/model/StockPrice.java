package com.example.jquants.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 株価四本値クラス
 * @author yuna.minato
 */
public class StockPrice {
     /** 株価四本値取得日 */
    private final LocalDate date;
    /** 銘柄コード */
    private final String code;
    /** 高値 */
    private final BigDecimal high;
    /** 安値 */
    private final BigDecimal low;
    /** 始値 */
    private final BigDecimal open;
    /** 終値 */
    private final BigDecimal close;
    /** 出来高 */
    private final BigDecimal volume;

    /**
     * コンストラクタ
     * @param date 株価四本値取得日
     * @param code 銘柄コード
     * @param high 高値
     * @param low 安値
     * @param open 始値
     * @param close 終値
     * @param volume 出来高
     */
    public StockPrice(LocalDate date, String code, BigDecimal high, BigDecimal low, BigDecimal open, BigDecimal close, BigDecimal volume) {
        this.date = date;
        this.code = code;
        this.high = high;
        this.low = low;
        this.open = open;
        this.close = close;
        this.volume = volume;
    }

    /**
     * 株価四本値取得日のgetter
     * @return 株価四本値取得日
     */
    public LocalDate getDate() {
        return this.date;
    }

    /**
     * 銘柄コードのgetter
     * @return 銘柄コード
     */
    public String getCode() {
        return this.code;
    }

    /**
     * 高値のgetter
     * @return 高値
     */
    public BigDecimal getHigh() {
        return this.high;
    }

    /**
     * 安値のgetter
     * @return 低値
     */
    public BigDecimal getLow() {
        return this.low;
    }

    /**
     * 始値のgetter
     * @return 始値
     */
    public BigDecimal getOpen() {
        return this.open;
    }

    /**
     * 終値のgetter
     * @return 終値
     */
    public BigDecimal getClose() {
        return this.close;
    }

    /**
     * 出来高のgetter
     * @return 出来高
     */
    public BigDecimal getVolume() {
        return this.volume;
    }
}