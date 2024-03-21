
#include <jni.h>
#include <string>

extern "C"
jstring

Java_com_growthskyinfotech_vidostatus_network_APIClient_baseUrlFromJNI(JNIEnv *env, jclass clazz) {
    {

        //TODO Change Your URl
        std::string baseURL = "/";
        return env->NewStringUTF(baseURL.c_str());
    }
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_growthskyinfotech_vidostatus_network_APIClient_API_1KEY_1AI_1GENERATE(JNIEnv *env, jclass clazz) {


    std::string baseURL = "";
    return env->NewStringUTF(baseURL.c_str());

}