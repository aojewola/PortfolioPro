package com.task.portfoliopro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioUpdateDTO {

    private double initialPortolioValue;

    private double currentPortolioValue;

}
