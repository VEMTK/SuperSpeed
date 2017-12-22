//
// Created by wxmylife on 2017/3/7.
//

#include <jni.h>
#include <string.h>
#include <stdio.h>
#include "com_xxm_sublibrary_jni_Ja.h"

#include <android/log.h> //导入log.h

#define LOG_TAG "love"  //指定打印到logcat的Tag
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

/**
 *这个key是和服务器之间通信的秘钥
 */

/**
 * 发布的app 签名,只有和本签名一致的app 才会返回 AUTH_KEY
 * 这个RELEASE_SIGN的值是上一步用java代码获取的值
 */
const char* RELEASE_SIGN = "308203653082024da00302010202045d364963300d06092a864886f70d01010b05003063310d300b06035504061304636f64653111300f0603550408130870726f76696e63653111300f060355040713086c6f63616c697479310f300d060355040a13066f7267616e69310d300b060355040b1304756e6974310c300a0603550403130361646e301e170d3137313130343031333530335a170d3432313032393031333530335a3063310d300b06035504061304636f64653111300f0603550408130870726f76696e63653111300f060355040713086c6f63616c697479310f300d060355040a13066f7267616e69310d300b060355040b1304756e6974310c300a0603550403130361646e30820122300d06092a864886f70d01010105000382010f003082010a02820101008a9601807dfe908a9b81ae9a74d4abb1212e3d441667416ed9689e57d7dcdb64ebca5736e89bbb82d86db59821dd29d835f11a4f4644f3975463713701e5f0c9ced112b72db736d31f0fd1d37611076384192ed9ca332eb5b78ff7a618013500673cb06ea438af6121b49b24328e2b1aed909d87298f8894629139be289e006741f17480169fad245aa065524f3430579d79c103a2410f58ae1969710014e6a3baf9bff263c74843cbe4c5bda2fed47e25da552babcff560ac2f96c1da8567c394b6fa60759fe55ffda26b4163937b5fcd28335ba03815e1ed4f6ee1a0d8f293db321b4c5b474263bdb103901970373983ca8047617b1fe864d9a833d62bc1850203010001a321301f301d0603551d0e0416041458ce1e9c3090132e9ebdfd2f9379264e82fcd64f300d06092a864886f70d01010b0500038201010046a744e6bb0c602daef0c6bbb5ad4e72e0b6ae314bf8aadae54f84665296e3960bee0de1ee53ee3cab12bfa7bcd2174b0e2e0698f62c6786ac10287bb39aee7619157dbea8649929b88ae3698f5a780febb4111d5f6b38f1f503429002b556fdeba1c281e0c3285108668c23258c9e367d5c29a43d622780189b4bbefea67f669117f476b6b798a75fbf063c606462c806709bc3a0a1ea8c9f4ac39e150213a901484a7b7275e8ff397735db4393a925d3dbfe25e51bb859aafbee5f701c5a7fbf1edd61e0736940bef57191aa086e3c0a0024874a86ba0f361275cc028beb2c87d3fc4b70db5512b98ad96753273cbcff469805ca003732032ba6283f7afb68";
const char* AUTH_KEY = "5d4d629bfe85709f";
/**
 * 发布的app 签名 的HashCode
 */
const int RELEASE_SIGN_HASHCODE = -332752192;

JNIEXPORT jstring JNICALL Java_w_c_s_jni_Ja_getPublicKey
  (JNIEnv *env, jclass jclazz, jobject contextObject){

    jclass native_class = env->GetObjectClass(contextObject);
    jmethodID pm_id = env->GetMethodID(native_class, "getPackageManager", "()Landroid/content/pm/PackageManager;");
    jobject pm_obj = env->CallObjectMethod(contextObject, pm_id);
    jclass pm_clazz = env->GetObjectClass(pm_obj);
    // 得到 getPackageInfo 方法的 ID
    jmethodID package_info_id = env->GetMethodID(pm_clazz, "getPackageInfo","(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    jclass native_classs = env->GetObjectClass(contextObject);
    jmethodID mId = env->GetMethodID(native_classs, "getPackageName", "()Ljava/lang/String;");
    jstring pkg_str = static_cast<jstring>(env->CallObjectMethod(contextObject, mId));
    // 获得应用包的信息
    jobject pi_obj = env->CallObjectMethod(pm_obj, package_info_id, pkg_str, 64);
    // 获得 PackageInfo 类
    jclass pi_clazz = env->GetObjectClass(pi_obj);
    // 获得签名数组属性的 ID
    jfieldID signatures_fieldId = env->GetFieldID(pi_clazz, "signatures", "[Landroid/content/pm/Signature;");
    jobject signatures_obj = env->GetObjectField(pi_obj, signatures_fieldId);
    jobjectArray signaturesArray = (jobjectArray)signatures_obj;
    jsize size = env->GetArrayLength(signaturesArray);
    jobject signature_obj = env->GetObjectArrayElement(signaturesArray, 0);
    jclass signature_clazz = env->GetObjectClass(signature_obj);

    //第一种方式--检查签名字符串的方式
    jmethodID string_id = env->GetMethodID(signature_clazz, "toCharsString", "()Ljava/lang/String;");
    jstring str = static_cast<jstring>(env->CallObjectMethod(signature_obj, string_id));
    char *c_msg = (char*)env->GetStringUTFChars(str,0);

    if(strcmp(c_msg,RELEASE_SIGN)==0)//签名一致  返回合法的 api key，否则返回错误
    {

        return (env)->NewStringUTF(AUTH_KEY);

    }else
    {
        return (env)->NewStringUTF("error");
    }

    //第二种方式--检查签名的hashCode的方式
    /*
    jmethodID int_hashcode = env->GetMethodID(signature_clazz, "hashCode", "()I");
    jint hashCode = env->CallIntMethod(signature_obj, int_hashcode);
    if(hashCode == RELEASE_SIGN_HASHCODE)
    {
        return (env)->NewStringUTF(AUTH_KEY);
    }else{
        return (env)->NewStringUTF("错误");
    }
     */
}