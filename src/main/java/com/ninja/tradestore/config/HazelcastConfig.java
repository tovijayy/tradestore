package com.ninja.tradestore.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.IndexConfig;
import com.hazelcast.config.IndexType;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import com.ninja.tradestore.model.TradeDocument;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {


    @Bean
    public Config hazelcastConfiguration() {
        Config config = new Config();
        config.setClusterName("trade-cluster");

        // Configure the Trade map
        MapConfig tradeMapConfig = new MapConfig();
        tradeMapConfig.setName("trades");
        tradeMapConfig.setBackupCount(1);
        tradeMapConfig.setAsyncBackupCount(0);
        tradeMapConfig.setTimeToLiveSeconds(0); // No TTL
        tradeMapConfig.setMaxIdleSeconds(0);    // No max idle

        // Add indexes for better query performance
        tradeMapConfig.addIndexConfig(new IndexConfig(IndexType.HASH, "tradeId"));
        tradeMapConfig.addIndexConfig(new IndexConfig(IndexType.HASH, "counterPartyId"));
        tradeMapConfig.addIndexConfig(new IndexConfig(IndexType.HASH, "bookId"));
        tradeMapConfig.addIndexConfig(new IndexConfig(IndexType.HASH, "expired"));
        tradeMapConfig.addIndexConfig(new IndexConfig(IndexType.SORTED, "maturityDate"));
        tradeMapConfig.addIndexConfig(new IndexConfig(IndexType.SORTED, "createdDate"));
        tradeMapConfig.addIndexConfig(new IndexConfig(IndexType.SORTED, "version"));

        config.addMapConfig(tradeMapConfig);


        return config;
    }


    @Bean
    public HazelcastInstance hazelcastInstance(Config config) {
        return Hazelcast.newHazelcastInstance(config);
    }

    @Bean
    public IMap<String, TradeDocument> tradeMap(HazelcastInstance hazelcastInstance) {
        return hazelcastInstance.getMap("trades");
    }



}
