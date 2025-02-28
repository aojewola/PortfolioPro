package com.task.portfoliopro.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {
    private String stockName;
    @NotNull
    private String ticker;

    @NotNull
    private int shares;
    
    @NotNull
    private double price;
}
