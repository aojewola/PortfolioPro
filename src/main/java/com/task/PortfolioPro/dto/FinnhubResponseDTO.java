package com.task.portfoliopro.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinnhubResponseDTO {
    private int count;
    private List<CompanyDTO> result;

}
