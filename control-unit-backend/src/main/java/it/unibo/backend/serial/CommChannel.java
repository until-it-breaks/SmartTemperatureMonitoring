package it.unibo.backend.serial;

public interface CommChannel {
    
    /**
     * Sends a message.
     * 
     * @param msg The message to be sent
     */
    void sendMsg(String msg);

    /**
     * Receives a message.
     * 
     * @return The received message.
     * @throws InterruptedException 
     */
    String receiveMsg() throws InterruptedException;

    /**
     * Checks if a message is available.
     * 
     * @return {@code true} if available, {@code false} otherwise.
     */
    boolean isMsgAvailable();

    /**
     * Closes the communication channel.
     */
    void close();
}