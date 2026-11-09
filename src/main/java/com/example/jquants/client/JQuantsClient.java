package com.example.jquants.client;

import com.example.jquants.model.Stock;
import com.example.jquants.model.StockPrice;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * J-Quantsとの通信を行うクラス
 * @author yuna.minato
 */
@Component
public class JQuantsClient {
    /** J-Quants APIキー */
    private final String apiKey;
    private static final String STOCK_URL = "https://api.jquants.com/v2/equities/master";
    private static final String STOCK_PRICE_URL = "https://api.jquants.com/v2/equities/bars/daily";
    private static final String API_KEY_PARAM = "x-api-key";
    private static final String CODE_PARAM = "?code=";
    private static final String FROM_PARAM = "&from=";
    private static final String TO_PARAM = "&to=";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public JQuantsClient(@Value("${jquants.api-key}") String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 銘柄一覧を取得する
     * @return 銘柄一覧
     */
    public List<Stock> fetchStocks() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(STOCK_URL)).header(API_KEY_PARAM, apiKey).GET().build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("J-Quants APIの呼び出しに失敗しました" + System.lineSeparator() + "HTTPステータス：" + response.statusCode());
            }

            return convertToStocks(response.body());
        } catch (IOException e) {
            throw new RuntimeException("J-Quants APIのレスポンス処理に失敗しました", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new RuntimeException("J-Quants APIとの通信が中断されました", e);
        }
    }

    /**
     * 株価四本値を取得する
     * @param code 銘柄コード
     * @param startDate 取得開始日
     * @param endDate 取得終了日
     * @return 株価四本値一覧
     */
    public List<StockPrice> fetchStockPrices(String code, LocalDate startDate, LocalDate endDate) {
        // 日付の文字列を整形する
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String startDateStr = startDate.format(formatter);
        String endDateStr = endDate.format(formatter);

        String url = STOCK_PRICE_URL + CODE_PARAM + code + FROM_PARAM + startDateStr + TO_PARAM + endDateStr;

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header(API_KEY_PARAM, apiKey).GET().build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("J-Quants APIの呼び出しに失敗しました" + System.lineSeparator() + "HTTPステータス：" + response.statusCode());
            }

            return convertToStockPrices(response.body());
        } catch (IOException e) {
            throw new RuntimeException("J-Quants APIのレスポンス処理に失敗しました", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new RuntimeException("J-Quants APIとの通信が中断されました", e);
        }
    }

    /**
     * J-QuantsのJSONをStockのリストへ変換する
     * @param json J-QuantsのJSON
     * @return Stockのリスト
     * @throws IOException 変換処理に失敗した場合に発生
     */
    private List<Stock> convertToStocks(String json) throws IOException {
        List<Stock> stocks = new ArrayList<>();

        JsonNode rootNode = objectMapper.readTree(json);
        JsonNode dataNode = rootNode.get("data");

        if (dataNode == null || !dataNode.isArray()) {
            return stocks;
        }

        for (JsonNode item : dataNode) {
            String code = item.get("Code").asText();
            String coName = item.get("CoName").asText();

            Stock stock = new Stock(code, coName);

            stocks.add(stock);
        }

        return stocks;
    }

    /**
     * J-QuantsのJSONをStockPriceのリストへ変換する
     * @param json J-QuantsのJSON
     * @return StockPriceのリスト
     * @throws IOException 変換処理に失敗した場合に発生
     */
    private List<StockPrice> convertToStockPrices(String json) throws IOException {
        List<StockPrice> stockPrices = new ArrayList<>();

        JsonNode rootNode = objectMapper.readTree(json);
        JsonNode dataNode = rootNode.get("data");

        if (dataNode == null || !dataNode.isArray()) {
            return stockPrices;
        }

        for (JsonNode item : dataNode) {
            LocalDate date = LocalDate.parse(item.get("Date").asText());
            String code = item.get("Code").asText();
            BigDecimal high = item.get("H").decimalValue();
            BigDecimal low = item.get("L").decimalValue();
            BigDecimal open = item.get("O").decimalValue();
            BigDecimal close = item.get("C").decimalValue();
            BigDecimal volume = item.get("Vo").decimalValue();
            StockPrice stockPrice = new StockPrice(date, code, high, low, open, close, volume);

            stockPrices.add(stockPrice);
        }

        return stockPrices;
    }
}