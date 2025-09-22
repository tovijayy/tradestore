package com.ninja.tradestore.utils;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class HazelcastChecker implements CommandLineRunner {

    private final HazelcastInstance hazelcastInstance;

    public HazelcastChecker(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Hazelcast instance name: " + hazelcastInstance.getName());
        System.out.println("Distributed maps: " + hazelcastInstance.getDistributedObjects());
        System.out.println("Cluster size: " + hazelcastInstance.getCluster().getMembers().size());
    }
}