public class imageSendable implements Sendable {
    String imageUrl;
    String senderId;
    String recipientId;
    long timestamp;

    public imageSendable(String imageUrl, String senderId, String recipientId, long timestamp) {
        this.imageUrl = imageUrl;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.timestamp = timestamp;
    }

    public imageSendable(String imageUrl, String senderId, String recipientId) {
        this(imageUrl, senderId, recipientId, System.currentTimeMillis());
    }

    @Override
    public String getContent() {
        return this.imageUrl;
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
    
}
