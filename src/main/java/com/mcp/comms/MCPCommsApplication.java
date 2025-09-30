package com.mcp.comms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

// MCPCommsApplication.java
@SpringBootApplication
@EnableAsync
public class MCPCommsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MCPCommsApplication.class, args);
    }
}

