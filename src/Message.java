public class Message implements Sendable {
    String msg;
    String senderId;
    long timestamp;

    public Message(String msg, String senderId, long timestamp){
        this.msg = msg;
        this.senderId = senderId;
        this.timestamp = timestamp;
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
    public long getTimestamp() {
        return this.timestamp;
    }
}
