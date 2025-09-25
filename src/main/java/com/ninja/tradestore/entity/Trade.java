package com.ninja.tradestore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "trades", uniqueConstraints = @UniqueConstraint(columnNames = {"tradeId", "version"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tradeId;
    private int version;
    private String counterPartyId;
    private String bookId;
    private LocalDate maturityDate;
    private LocalDate createdDate;
    private char expired;
    @Version
    private Long revision;

}


