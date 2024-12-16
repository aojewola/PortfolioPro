package com.task.portfoliopro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompanyDTO {
    private String description;
    private String displaySymbol;
    private String symbol;
    private String type; 
}
