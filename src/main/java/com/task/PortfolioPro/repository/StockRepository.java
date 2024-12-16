package com.task.portfoliopro.repository;


import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.task.portfoliopro.entities.Stock;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository()
public interface StockRepository extends R2dbcRepository<Stock, String> {

    @Query("SELECT * FROM stock WHERE id = :id AND is_deleted = false")
    Mono<Stock> findByIdAndIsDeletedFalse(@Param("id") String id);

    @Query("SELECT * FROM stock WHERE is_deleted = false")
    Flux<Stock> findAllByIsDeletedFalse();

    @Modifying
    @Query("UPDATE stock SET is_deleted = true WHERE id = :id")
    Mono<Integer> softDeleteById(@Param("id") String id);

}
