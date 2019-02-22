package wtf.jni;

public class WtfNative {
    public native String stringFromJNI();

    static {
        System.loadLibrary("wtf-lib");
    }

}
