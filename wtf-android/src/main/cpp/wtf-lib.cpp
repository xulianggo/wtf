#include <jni.h>
#include <string>
#include <dlfcn.h>
#include <v8.h>

//TODO
//#include <string.h>
//#include <inttypes.h>
//#include <pthread.h>
//#include <android/log.h>
//#include <assert.h>

extern "C" {

JNIEXPORT jstring JNICALL
Java_wtf_jni_WtfNative_ABI( JNIEnv * env, jobject thiz )
{

#if defined(__arm__)
    #if defined(__ARM_ARCH_7A__)
    #if defined(__ARM_NEON__)
      #if defined(__ARM_PCS_VFP)
        #define ABI "armeabi-v7a/NEON (hard-float)"
      #else
        #define ABI "armeabi-v7a/NEON"
      #endif
    #else
      #if defined(__ARM_PCS_VFP)
        #define ABI "armeabi-v7a (hard-float)"
      #else
        #define ABI "armeabi-v7a"
      #endif
    #endif
  #else
   #define ABI "armeabi"
  #endif
#elif defined(__i386__)
#define ABI "x86"
#elif defined(__x86_64__)
#define ABI "x86_64"
#elif defined(__mips64)  /* mips64el-* toolchain defines __mips__ too */
#define ABI "mips64"
#elif defined(__mips__)
#define ABI "mips"
#elif defined(__aarch64__)
#define ABI "arm64-v8a"
#else
#define ABI "unknown"
#endif

std::string hello = "[" ABI "]";
	
	void *dlWebCoreHandle = dlopen("libwebcore.so", RTLD_NOW);
	//void *v8GetVersion = dlsym(dlWebCoreHandle, "_ZN2v82V810GetVersionEv");
	//if (v8GetVersion == NULL)
		if(dlWebCoreHandle==NULL)
	{
		/* does not appear to be V8 */
		jclass widget = env->FindClass("android/webkit/WebView");
		if(NULL!=widget){
		hello = "android/webkit/WebView." ABI;//TODO now what?
		}
	}else{
		hello = "V8." ABI;
	}

//v8::Isolate* isolate;// = v8::Isolate::GetCurrent();
//v8::Platform* platform = v8::Platform::CreateDefaultPlatform();
//v8::Persistent<v8::Context> context = v8::Context::New();

//	v8::Isolate::CreateParams create_params;
//	create_params.array_buffer_allocator = v8::ArrayBuffer::Allocator::NewDefaultAllocator();

//v8::Persistent<v8::Context> context = v8::Persistent<v8::Context>::New(v8::Context::New());
//context->Enter();

return env->NewStringUTF(hello.c_str());

//return (*env)->NewStringUTF(env, "."ABI);//c

}
//https://github.com/fmtlib/android-ndk-example/blob/master/example/jni/hello-jni.cpp
//TODO https://github.com/googlesamples/android-ndk/blob/master/hello-jniCallback/app/src/main/cpp/hello-jnicallback.c
/*
 * processing one time initialization:
 *     Cache the javaVM into our context
 *     Find class ID for JniHelper
 *     Create an instance of JniHelper
 *     Make global reference since we are using them from a native thread
 * Note:
 *     All resources allocated here are never released by application
 *     we rely on system to free all global refs when it goes away;
 *     the pairing function JNI_OnUnload() never gets called at all.
 */
/*
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    memset(&g_ctx, 0, sizeof(g_ctx));

    g_ctx.javaVM = vm;
    if ((*vm)->GetEnv(vm, (void**)&env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR; // JNI version not supported.
    }

    jclass  clz = (*env)->FindClass(env,
                                    "com/example/hellojnicallback/JniHandler");
    g_ctx.jniHelperClz = (*env)->NewGlobalRef(env, clz);

    jmethodID  jniHelperCtor = (*env)->GetMethodID(env, g_ctx.jniHelperClz,
                                                   "<init>", "()V");
    jobject    handler = (*env)->NewObject(env, g_ctx.jniHelperClz,
                                           jniHelperCtor);
    g_ctx.jniHelperObj = (*env)->NewGlobalRef(env, handler);
    queryRuntimeInfo(env, g_ctx.jniHelperObj);

    g_ctx.done = 0;
    g_ctx.mainActivityObj = NULL;
    return  JNI_VERSION_1_6;
}
*/

}
