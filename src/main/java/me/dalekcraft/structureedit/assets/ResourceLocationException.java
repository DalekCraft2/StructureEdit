package me.dalekcraft.structureedit.assets;

public class ResourceLocationException extends RuntimeException {

    public ResourceLocationException(String message) {
        super(message);
    }

    public ResourceLocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
