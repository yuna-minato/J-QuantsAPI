package com.example.jquants.model;

/**
 * 株クラス
 * @author yuna.minato
 */
public class Stock {
    /** 銘柄コード */
    private final String code;
    /** 企業名 */
    private final String coName;

    /**
     * コンストラクタ
     * @param code 銘柄コード
     * @param coName 企業名
     */
    public Stock(String code, String coName) {
        this.code = code;
        this.coName = coName;
    }

    /**
     * 銘柄コードのgetter
     * @return 銘柄コード
     */
    public String getCode() {
        return this.code;
    }

    /**
     * 企業名のgetter
     * @return 企業名
     */
    public String getCoName() {
        return this.coName;
    }
}