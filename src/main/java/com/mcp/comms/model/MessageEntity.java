package com.mcp.comms.model;

import jakarta.persistence.*;

@Entity
@Table(name = "messages")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false, length = 500)
    private String content;

    public MessageEntity() {}

    public MessageEntity(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    public Long getId() { return id; }
    public String getSender() { return sender; }
    public String getContent() { return content; }
}
