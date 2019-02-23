package wtf.jni;

public class WtfNative {

    static {
        System.loadLibrary("wtf-lib");
    }

    static public native String ABI();

}
