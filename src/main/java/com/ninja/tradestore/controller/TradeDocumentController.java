package com.ninja.tradestore.controller;

import com.ninja.tradestore.model.TradeDocument;
import com.ninja.tradestore.service.TradeHazelcastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/trade-documents")
public class TradeDocumentController {

    private final TradeHazelcastService tradeDocumentService;

    @Autowired
    public TradeDocumentController(TradeHazelcastService tradeDocumentService) {
        this.tradeDocumentService = tradeDocumentService;
    }

    @PostMapping
    public ResponseEntity<TradeDocument> createTradeDocument(@RequestBody TradeDocument tradeDocument) {
        if (tradeDocumentService.tradeDocumentExists(tradeDocument.getTradeId(), tradeDocument.getVersion())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        TradeDocument created = tradeDocumentService.createTradeDocument(tradeDocument);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<TradeDocument>> getAllTradeDocuments() {
        List<TradeDocument> documents = tradeDocumentService.getAllTrades();
        return ResponseEntity.ok(documents);
    }

}
