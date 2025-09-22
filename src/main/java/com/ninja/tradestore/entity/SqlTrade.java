package com.ninja.tradestore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(TradeId.class)
public class SqlTrade {

    @Id
    private String tradeId;
    @Id
    private int version;

    private String counterPartyId;
    private String bookId;
    private LocalDate maturityDate;
    private LocalDate createdDate;
    private char expired;


}

// Composite Key Class
class TradeId implements Serializable {
    private String tradeId;
    private int version;
}
