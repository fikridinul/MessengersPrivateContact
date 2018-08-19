package com.thesisproject.fikri.messengers.messages;

public class ChatMessage {

    private int messageID;
    private String messageWith;
    private String messageText;
    private String userType;
    private String messageStatus;
    private long messageTime;
    private String messageNumber;

    public ChatMessage (int mId, String with, String type, String message, String status, long time, String number) {
        messageID = mId;
        messageWith = with;
        userType = type;
        messageText = message;
        messageStatus = status;
        messageTime = time;
        messageNumber = number;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public void setMessageWith(String messageWith) {
        this.messageWith = messageWith;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setMessageNumber(String messageNumber) {
        this.messageNumber = messageNumber;
    }

    public int getMessageID() {
        return messageID;
    }

    public String getMessageWith() {
        return messageWith;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getUserType() {
        return userType;
    }

    public String getMessageNumber() {
        return messageNumber;
    }

}
