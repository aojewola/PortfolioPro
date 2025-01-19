package com.task.portfoliopro.entities;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

import org.springframework.validation.annotation.Validated;

import com.task.portfoliopro.dto.StockDTO;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Validated
@EqualsAndHashCode(callSuper = false)
public class Stock extends AuditEntity {
    @Id
    private String id  = UUID.randomUUID().toString();

    @Column(name = "stock_name", nullable = false)
    private String stockName;

    @Column(nullable = false)
    private String ticker;

    @Column(nullable = false)
    private int shares;

    @Column(nullable = false)
    private double price;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "total_cost", nullable = false)
    private double totalCost;

    public Stock(final StockDTO stockDto) {
        setStockName(stockDto.getStockName());
        setPrice(stockDto.getPrice());
        setShares(stockDto.getShares());
        setTicker(stockDto.getTicker());
        setTotalCost(stockDto.getPrice() * stockDto.getShares());
    }
}
