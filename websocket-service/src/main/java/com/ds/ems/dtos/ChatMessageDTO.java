package com.ds.ems.dtos;

public class ChatMessageDTO {
    private String sender;
    private String content;
    private String receiverId;

    public ChatMessageDTO() {}

    public ChatMessageDTO(String sender, String content, String receiverId) {
        this.sender = sender;
        this.content = content;
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}
