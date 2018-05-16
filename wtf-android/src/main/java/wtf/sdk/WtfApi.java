package wtf.sdk;

//@see SimpleHybridWebViewUi

abstract public class WtfApi {
    private WtfUi __callerUi = null;

    public WtfUi getCallerUi() {
        return __callerUi;
    }

    public void setCallerUi(WtfUi callerUi) {
        __callerUi = callerUi;
    }

    abstract public void handler(JSO jso, WtfCallback cbFunc);
}
