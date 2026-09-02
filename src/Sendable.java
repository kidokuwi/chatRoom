public interface Sendable {
    String getContent();
    String getSenderId();
    String getRecipientId();
    long getTimestamp();
}

