package com.task.portfoliopro.dto;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockPriceDTO {
    private double openPrice;

    private double closePrice;

    private double highPrice;

    private double lowPrice;
    

    public StockPriceDTO(final JsonNode response) {
        String latestTimestamp = response.fieldNames().next();
        setOpenPrice(response.path(latestTimestamp).path("1. open").asDouble());
        setClosePrice(response.path(latestTimestamp).path("4. close").asDouble());
        setHighPrice(response.path(latestTimestamp).path("2. high").asDouble());
        setLowPrice(response.path(latestTimestamp).path("3. low").asDouble());
    }
}
