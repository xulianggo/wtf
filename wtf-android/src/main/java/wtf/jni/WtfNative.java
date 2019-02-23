package wtf.jni;

public class WtfNative {
    static public native String ABI();

    static {
        System.loadLibrary("wtf-lib");
    }

}
