package wtf.sdk;

//@see SimpleHybridWebViewUi

abstract public class HybridApi {
    private HybridUi __callerUi = null;

    public HybridUi getCallerUi() {
        return __callerUi;
    }

    public void setCallerUi(HybridUi callerUi) {
        __callerUi = callerUi;
    }

    abstract public void handler(JSO jso, HybridCallback cbFunc);
}
