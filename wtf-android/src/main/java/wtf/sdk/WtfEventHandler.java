package wtf.sdk;

public interface WtfEventHandler {
    void onCall(String eventName, JSO extraData);
}
