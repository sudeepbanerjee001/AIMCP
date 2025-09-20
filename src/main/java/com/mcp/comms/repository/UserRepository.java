package com.mcp.comms.repository;

import com.mcp.comms.model.MCPUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<MCPUser, String> {
}