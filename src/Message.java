public class Message implements Sendable {
    String msg;
    String senderId;
    String recipientId;
    long timestamp;

    public Message(String msg, String senderId, String recipientId, long timestamp){
        this.msg = msg;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.timestamp = timestamp;
    }

    public Message(String msg, String senderId, String recipientId){
        this(msg, senderId, recipientId, System.currentTimeMillis());
    }

    @Override
    public String getContent() {
        return this.msg;
    }

    @Override
    public String getSenderId() {
        return this.senderId;
    }

    @Override
    public String getRecipientId() {
        return this.recipientId;
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    public String toJson() {
        return "{\"senderId\":\"" + JsonUtil.escape(senderId) + "\",\"recipientId\":\"" + JsonUtil.escape(recipientId) + "\",\"msg\":\"" + JsonUtil.escape(msg) + "\",\"timestamp\":" + timestamp + "}";
    }

    public static Message fromJson(String json) {
        String senderId = JsonUtil.extractField(json, "senderId");
        String recipientId = JsonUtil.extractField(json, "recipientId");
        String msg = JsonUtil.extractField(json, "msg");
        String tsStr = JsonUtil.extractField(json, "timestamp");
        long timestamp = System.currentTimeMillis();
        try {
            if (tsStr != null && !tsStr.isEmpty()) {
                timestamp = Long.parseLong(tsStr);
            }
        } catch (NumberFormatException e) {}
        return new Message(msg, senderId, recipientId, timestamp);
    }

    @Override
    public String toString() {
        return "[" + senderId + " -> " + recipientId + "]: " + msg;
    }
}

