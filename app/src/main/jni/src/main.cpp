#include <pthread.h>
#include <dlfcn.h>
#include <jni.h>
#include <memory.h>
#include <cstdio>
#include <cstdlib>
#include <iostream>
#include <sstream>
#include <thread>
#include <unistd.h>
#include <vector>
#include <list>
#include <locale>
#include <string>
#include <stdint.h>
#include <cstring>
#include <string.h>
#include <wchar.h>
#include <endian.h>
#include <sys/socket.h>
#include <ctime>
#include <iomanip>
#include <netinet/in.h>
#include <arpa/inet.h>
#include "json.hpp"
#include "Includes/obfuscaterr.h"
#include "FLog.h"
#include "curl/curl.h"

FLog *logger;

using namespace std;
using namespace nlohmann;

bool contains(std::string in, std::string target) {
    if(strstr(in.c_str(), target.c_str())) {
        return true;
    }
    return false;
}

bool equals(std::string first, std::string second) {
    if (first == second) {
        return true;
    }
    return false;
}

std::string getMins() {
    std::time_t now = std::time(nullptr);
    std::tm* localTime = std::localtime(&now);
    std::ostringstream oss;
    oss << std::put_time(localTime, (char*)OBFUSCATE("%M"));
    return oss.str();
}

std::string getTime() {
    std::time_t now = std::time(nullptr);
    std::tm* localTime = std::localtime(&now);
    std::ostringstream oss;
    oss << std::put_time(localTime, (char*)OBFUSCATE("%H"));
    std::string val = oss.str();
    if (equals(val, OBFUSCATE("09"))) {
        val = (std::string("1-") + std::string("9:00"));
    } else if (equals(val, OBFUSCATE("10"))) {
        val = (std::string("2-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("11"))) {
        val = (std::string("3-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("12"))) {
        val = (std::string("4-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("13"))) {
        val = (std::string("5-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("14"))) {
        val = (std::string("6-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("15"))) {
        val = (std::string("7-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("16"))) {
        val = (std::string("8-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("17"))) {
        val = (std::string("9-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("18"))) {
        val = (std::string("10-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("19"))) {
        val = (std::string("11-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("20"))) {
        val = (std::string("12-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("21"))) {
        val = (std::string("13-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("22"))) {
        val = (std::string("14-") + val + std::string(":00"));
    }
    return val;
}

std::string getNextTime() {
    std::time_t now = std::time(nullptr);
    std::tm* localTime = std::localtime(&now);
    std::ostringstream oss;
    oss << std::put_time(localTime, (char*)OBFUSCATE("%H"));
    std::string val = std::to_string((atoi(oss.str().c_str()) + 1));
    if (equals(oss.str(), OBFUSCATE("08"))) {
        val = (std::string("1-") + std::string("9:00"));
    } else if (equals(oss.str(), OBFUSCATE("09"))) {
        val = (std::string("2-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("11"))) {
        val = (std::string("3-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("12"))) {
        val = (std::string("4-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("13"))) {
        val = (std::string("5-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("14"))) {
        val = (std::string("6-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("15"))) {
        val = (std::string("7-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("16"))) {
        val = (std::string("8-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("17"))) {
        val = (std::string("9-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("18"))) {
        val = (std::string("10-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("19"))) {
        val = (std::string("11-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("20"))) {
        val = (std::string("12-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("21"))) {
        val = (std::string("13-") + val + std::string(":00"));
    } else if (equals(val, OBFUSCATE("22"))) {
        val = (std::string("14-") + val + std::string(":00"));
    }
    return val;
}


std::string getDay() {
    std::time_t now = std::time(nullptr);
    std::tm* localTime = std::localtime(&now);
    const char* days[] = {
        OBFUSCATE("კვი./Sun."),
        OBFUSCATE("ორშ./Mon."),
        OBFUSCATE("სამშ./Tues."),
        OBFUSCATE("ოთხშ./Wed."),
        OBFUSCATE("ხუთშ./Thurs."),
        OBFUSCATE("პარ./Fri."),
        OBFUSCATE("შაბ./Sat.")
    };
    int dayIndex = localTime->tm_wday;
    return days[dayIndex];
}

JavaVM* jvm;

jobject GetActivityContext(JNIEnv* env) {
    jclass uplayer = env->FindClass(OBFUSCATE("ge/nikka/gtutable/MainActivity"));
    jfieldID cmeth = env->GetStaticFieldID(uplayer, OBFUSCATE("currentActivity"), OBFUSCATE("Landroid/app/Activity;"));
    jobject currt = env->NewGlobalRef(env->GetStaticObjectField(uplayer, cmeth));
    return currt;
}

jobject GetContext(JNIEnv* globalEnv) {
    jclass activityThread = globalEnv->FindClass(OBFUSCATE("android/app/ActivityThread"));
    jmethodID currentActivityThread = globalEnv->GetStaticMethodID(activityThread, OBFUSCATE("currentActivityThread"), OBFUSCATE("()Landroid/app/ActivityThread;"));
    jobject at = globalEnv->CallStaticObjectMethod(activityThread, currentActivityThread);
    jmethodID getApplication = globalEnv->GetMethodID(activityThread, OBFUSCATE("getApplication"), OBFUSCATE("()Landroid/app/Application;"));
    jobject context = globalEnv->CallObjectMethod(at, getApplication);
    return context;
}

void Toast(JNIEnv *env, jobject thiz, const char *text, int length) {
    jstring jstr = env->NewStringUTF(text);
    jclass toast = env->FindClass(OBFUSCATE("android/widget/Toast"));
    jmethodID methodMakeText =env->GetStaticMethodID(toast,OBFUSCATE("makeText"),OBFUSCATE("(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;"));
    jobject toastobj = env->CallStaticObjectMethod(toast, methodMakeText, thiz, jstr, length);
    jmethodID methodShow = env->GetMethodID(toast, OBFUSCATE("show"), OBFUSCATE("()V"));
    env->CallVoidMethod(toastobj, methodShow);
}

void UpdateProg(JNIEnv *env, const char *text) {
    jstring jstr = env->NewStringUTF(text);
    jclass mact = env->FindClass(OBFUSCATE("ge/nikka/gtutable/MainActivity"));
    jmethodID mupd = env->GetStaticMethodID(mact, OBFUSCATE("setProg") ,OBFUSCATE("(Ljava/lang/String;)V"));
    env->CallStaticVoidMethod(mact, mupd, jstr);
}

void UpdateLoad(JNIEnv *env, const char *text) {
    jstring jstr = env->NewStringUTF(text);
    jclass mact = env->FindClass(OBFUSCATE("ge/nikka/gtutable/MainActivity"));
    jmethodID mupd = env->GetStaticMethodID(mact, OBFUSCATE("setDone"), OBFUSCATE("(Ljava/lang/String;)V"));
    env->CallStaticVoidMethod(mact, mupd, jstr);
}

std::string getCacheDir(JNIEnv *env) {
  jclass activityClass = env->FindClass(OBFUSCATE("ge/nikka/gtutable/MainActivity"));
  jmethodID getFilesDirMethod = env->GetMethodID(activityClass, OBFUSCATE("getCacheDir"), OBFUSCATE("()Ljava/io/File;"));
  jobject filesDirObj = env->CallObjectMethod(GetContext(env), getFilesDirMethod);
  jclass fileClass = env->FindClass(OBFUSCATE("java/io/File"));
  jmethodID getPathMethod = env->GetMethodID(fileClass, OBFUSCATE("getAbsolutePath"), OBFUSCATE("()Ljava/lang/String;"));
  jstring pathObj = (jstring) env->CallObjectMethod(filesDirObj, getPathMethod);
  return std::string(env->GetStringUTFChars(pathObj, NULL));
}

std::string getApkSign(JNIEnv *env) {
    jclass activityThread = env->FindClass(OBFUSCATE("android/app/ActivityThread"));
    jmethodID currentActivityThread = env->GetStaticMethodID(activityThread, OBFUSCATE("currentActivityThread"), OBFUSCATE("()Landroid/app/ActivityThread;"));
    jobject at = env->CallStaticObjectMethod(activityThread, currentActivityThread);
    jmethodID getApplication = env->GetMethodID(activityThread, OBFUSCATE("getApplication"), OBFUSCATE("()Landroid/app/Application;"));
    jobject context = env->CallObjectMethod(at, getApplication);
    jclass versionClass = env->FindClass(OBFUSCATE("android/os/Build$VERSION"));
    jfieldID sdkIntFieldID = env->GetStaticFieldID(versionClass, OBFUSCATE("SDK_INT"), OBFUSCATE("I"));
    int sdkInt = env->GetStaticIntField(versionClass, sdkIntFieldID);
    jclass contextClass = env->FindClass(OBFUSCATE("android/content/Context"));
    jmethodID pmMethod = env->GetMethodID(contextClass, OBFUSCATE("getPackageManager"), OBFUSCATE("()Landroid/content/pm/PackageManager;"));
    jobject pm = env->CallObjectMethod(context, pmMethod);
    jclass pmClass = env->GetObjectClass(pm);
    jmethodID piMethod = env->GetMethodID(pmClass, OBFUSCATE("getPackageInfo"), OBFUSCATE("(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;"));
    jmethodID pnMethod = env->GetMethodID(contextClass, OBFUSCATE("getPackageName"), OBFUSCATE("()Ljava/lang/String;"));
    jstring packageName = (jstring) env->CallObjectMethod(context, pnMethod);
    int flags;
    if (sdkInt >= atoi(OBFUSCATE("28"))) {
        flags = 0x08000000; // PackageManager.GET_SIGNING_CERTIFICATES
    } else {
        flags = 0x00000040; // PackageManager.GET_SIGNATURES
    }
    jobject packageInfo = env->CallObjectMethod(pm, piMethod, packageName, flags);
    jclass piClass = env->GetObjectClass(packageInfo);
    jobjectArray signatures;
    if (sdkInt >= atoi(OBFUSCATE("28"))) {
        jfieldID signingInfoField = env->GetFieldID(piClass, OBFUSCATE("signingInfo"), OBFUSCATE("Landroid/content/pm/SigningInfo;"));
        jobject signingInfoObject = env->GetObjectField(packageInfo, signingInfoField);
        jclass signingInfoClass = env->GetObjectClass(signingInfoObject);
        jmethodID signaturesMethod = env->GetMethodID(signingInfoClass, OBFUSCATE("getApkContentsSigners"), OBFUSCATE("()[Landroid/content/pm/Signature;"));
        jobject signaturesObject = env->CallObjectMethod(signingInfoObject, signaturesMethod);
        signatures = (jobjectArray) (signaturesObject);
    } else {
        jfieldID signaturesField = env->GetFieldID(piClass, OBFUSCATE("signatures"), OBFUSCATE("[Landroid/content/pm/Signature;"));
        jobject signaturesObject = env->GetObjectField(packageInfo, signaturesField);
        if (env->IsSameObject(signaturesObject, nullptr)) {
            return OBFUSCATE("");
        }
        signatures = (jobjectArray) (signaturesObject);
    }
    jobject firstSignature = env->GetObjectArrayElement(signatures, 0);
    jclass signatureClass = env->GetObjectClass(firstSignature);
    jmethodID signatureByteMethod = env->GetMethodID(signatureClass, OBFUSCATE("toByteArray"), OBFUSCATE("()[B"));
    jobject signatureByteArray = (jobject) env->CallObjectMethod(firstSignature, signatureByteMethod);
    jclass mdClass = env->FindClass((OBFUSCATE("java/security/MessageDigest")));
    jmethodID mdMethod = env->GetStaticMethodID(mdClass, OBFUSCATE("getInstance"), OBFUSCATE("(Ljava/lang/String;)Ljava/security/MessageDigest;"));
    jobject md5Object = env->CallStaticObjectMethod(mdClass, mdMethod, env->NewStringUTF(OBFUSCATE("MD5")));
    jmethodID mdUpdateMethod = env->GetMethodID(mdClass, OBFUSCATE("update"), OBFUSCATE("([B)V"));// The return value of this function is void, write V
    env->CallVoidMethod(md5Object, mdUpdateMethod, signatureByteArray);
    jmethodID mdDigestMethod = env->GetMethodID(mdClass, OBFUSCATE("digest"), OBFUSCATE("()[B"));
    jobject fingerPrintByteArray = env->CallObjectMethod(md5Object, mdDigestMethod);
    jsize byteArrayLength = env->GetArrayLength(static_cast<jarray>(fingerPrintByteArray));
    jbyte *fingerPrintByteArrayElements = env->GetByteArrayElements(static_cast<jbyteArray>(fingerPrintByteArray), JNI_FALSE);
    char *charArray = (char *) fingerPrintByteArrayElements;
    char *md5 = (char *) calloc(2 * byteArrayLength + 1, sizeof(char));
    int k;
    for (k = 0; k < byteArrayLength; k++) {
        sprintf(&md5[2 * k], OBFUSCATE("%02X"), charArray[k]);
    }
    return std::string(md5);
}

void setDialogMD(jobject ctx, JNIEnv *env, const char *title, const char *msg){
    jclass Alert = env->FindClass(OBFUSCATE("android/app/AlertDialog$Builder"));
    jmethodID AlertCons = env->GetMethodID(Alert, OBFUSCATE("<init>"), OBFUSCATE("(Landroid/content/Context;)V"));
    jobject MainAlert = env->NewObject(Alert, AlertCons, ctx);
    jmethodID setTitle = env->GetMethodID(Alert, OBFUSCATE("setTitle"), OBFUSCATE("(Ljava/lang/CharSequence;)Landroid/app/AlertDialog$Builder;"));
    env->CallObjectMethod(MainAlert, setTitle, env->NewStringUTF(title));
    jmethodID setMsg = env->GetMethodID(Alert, OBFUSCATE("setMessage"), OBFUSCATE("(Ljava/lang/CharSequence;)Landroid/app/AlertDialog$Builder;"));
    env->CallObjectMethod(MainAlert, setMsg, env->NewStringUTF(msg));
    jmethodID setCa = env->GetMethodID(Alert, OBFUSCATE("setCancelable"), OBFUSCATE("(Z)Landroid/app/AlertDialog$Builder;"));
    env->CallObjectMethod(MainAlert, setCa, false);
    jmethodID create = env->GetMethodID(Alert, OBFUSCATE("create"), OBFUSCATE("()Landroid/app/AlertDialog;"));
    jobject creaetob = env->CallObjectMethod(MainAlert, create);
    jclass AlertN = env->FindClass(OBFUSCATE("android/app/AlertDialog"));
    jmethodID show = env->GetMethodID(AlertN, OBFUSCATE("show"), OBFUSCATE("()V"));
    env->CallVoidMethod(creaetob, show);
}

void setDialogM3(jobject ctx, JNIEnv *env, const char *title, const char *msg){
    jclass MaterialAlert = env->FindClass(OBFUSCATE("com/google/android/material/dialog/MaterialAlertDialogBuilder"));
    jmethodID MaterialAlertCons = env->GetMethodID(MaterialAlert, OBFUSCATE("<init>"), OBFUSCATE("(Landroid/content/Context;)V"));
    jobject MainAlert = env->NewObject(MaterialAlert, MaterialAlertCons, ctx);
    
    jmethodID setTitle = env->GetMethodID(MaterialAlert, OBFUSCATE("setTitle"), OBFUSCATE("(Ljava/lang/CharSequence;)Lcom/google/android/material/dialog/MaterialAlertDialogBuilder;"));
    env->CallObjectMethod(MainAlert, setTitle, env->NewStringUTF(title));
    
    jmethodID setMsg = env->GetMethodID(MaterialAlert, OBFUSCATE("setMessage"), OBFUSCATE("(Ljava/lang/CharSequence;)Lcom/google/android/material/dialog/MaterialAlertDialogBuilder;"));
    env->CallObjectMethod(MainAlert, setMsg, env->NewStringUTF(msg));
    
    jmethodID setCa = env->GetMethodID(MaterialAlert, OBFUSCATE("setCancelable"), OBFUSCATE("(Z)Lcom/google/android/material/dialog/MaterialAlertDialogBuilder;"));
    env->CallObjectMethod(MainAlert, setCa, false);
    
    jmethodID create = env->GetMethodID(MaterialAlert, OBFUSCATE("create"), OBFUSCATE("()Landroid/app/AlertDialog;"));
    jobject createdDialog = env->CallObjectMethod(MainAlert, create);
    
    jclass AlertN = env->FindClass(OBFUSCATE("android/app/AlertDialog"));
    jmethodID show = env->GetMethodID(AlertN, OBFUSCATE("show"), OBFUSCATE("()V"));
    env->CallVoidMethod(createdDialog, show);
}

float dwn;
std::string progs;

static size_t writebytes(void *data, size_t size, size_t nmemb, void *userp) {
    size_t realsize = size * nmemb;
    dwn = (dwn + realsize);
    std::string *str = static_cast<std::string*>(userp);
    str->append(static_cast<char*>(data), realsize);
    std::ostringstream trs;
    trs << dwn;
    float percentage = (atof(trs.str().c_str()) / 7085162.0f) * 100.0f;
    std::ostringstream strm;
    char* per = new char[4];
    sprintf(per, OBFUSCATE("%.f"), percentage);
    strm << per;
    delete[] per;
    progs = strm.str();
    return realsize;
}

static size_t writebytes2(void *data, size_t size, size_t nmemb, void *userp) {
    size_t realsize = size * nmemb;
    std::string *str = static_cast<std::string*>(userp);
    str->append(static_cast<char*>(data), realsize);
    return realsize;
}

std::string get_url(const char* site) {
    CURL *curl = curl_easy_init();
    std::string datastr;
    if (curl) {
        curl_easy_setopt(curl, CURLOPT_URL, site);
        curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, 1L);
        curl_easy_setopt(curl, CURLOPT_DEFAULT_PROTOCOL, std::string(OBFUSCATE("https")).c_str());
        if (!contains(site, OBFUSCATE("pastebin")))
            curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, &writebytes);
        else
            curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, &writebytes2);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, &datastr);
        curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0L);
        curl_easy_setopt(curl, CURLOPT_SSL_VERIFYHOST, 0L);
        CURLcode res = curl_easy_perform(curl);
		char *url = NULL;
        curl_easy_getinfo(curl, CURLINFO_EFFECTIVE_URL, &url);
        if (!equals(url, site)) return std::string(OBFUSCATE("0"));
        curl_easy_cleanup(curl);
    }
    return datastr;
}

std::string ReplaceString(std::string subject, const std::string& search, const std::string& replace) {
    size_t pos = 0;
    while ((pos = subject.find(search, pos)) != std::string::npos) {
        subject.replace(pos, search.length(), replace);
        pos += replace.length();
    }
    return subject;
}

std::string RPB(std::string str) {
	return ReplaceString(ReplaceString(str, "\"", ""), "\"", "");
}

std::string xorEncryptDecrypt(const char* input) {
    std::string output = input;
    for (size_t i = 0; i < output.length(); ++i) {
        output[i] ^= (char)0x023;
    }
    return output;
}

std::string df;
std::string status(OBFUSCATE("wait"));

void collect(JavaVM* javavm) {
    while (df.length() == 0) {
        std::string furl(get_url(OBFUSCATE_KEY("https://pastebin.com/raw/DWZ542Dz", '&')));
        df = get_url(furl.c_str());
        break;
    }
    status = std::string(OBFUSCATE("done"));
}

extern "C" {
    
JNIEXPORT void JNICALL
Java_ge_nikka_gtutable_MainActivity_collect(JNIEnv *env, jobject clazz) {
    JavaVM *javaVM;
    jint result = env->GetJavaVM(&javaVM);
    std::thread(collect, javaVM).detach();
}

JNIEXPORT jstring JNICALL
Java_ge_nikka_gtutable_MainActivity_checkk(JNIEnv *env, jobject clazz, jstring md) {
    jstring ret = env->NewStringUTF(status.c_str());
    if (contains(status, OBFUSCATE("done"))) {
        if (equals(env->GetStringUTFChars(md, 0), OBFUSCATE("T"))) {
            status = std::string(OBFUSCATE("wait"));
            dwn = 0;
        }
    }
    return ret;
}

JNIEXPORT jstring JNICALL
Java_ge_nikka_gtutable_MainActivity_getPrg(JNIEnv *env, jobject clazz) {
    jstring ret = env->NewStringUTF(progs.c_str());
    return ret;
}

JNIEXPORT jstring JNICALL
Java_ge_nikka_gtutable_MainActivity_getData(JNIEnv *env, jobject clazz, jstring tid) {
    std::string tuid(env->GetStringUTFChars(tid, 0));
    if (df.find(tuid + std::string(OBFUSCATE("</th></tr>"))) == -1) {
        return env->NewStringUTF(OBFUSCATE("NOT_FOUND"));
    }
    std::string dff(df.substr(df.find(tuid + std::string(OBFUSCATE("</th></tr>"))) - 129, df.length()));
    std::string dfff(dff.substr(0, dff.find(std::string(OBFUSCATE("#top"))) - 31));
    std::string ret(OBFUSCATE("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"ge\" xml:lang=\"ge\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\"><style>@import url(https://fonts.googleapis.com/css?family=Open+Sans);.inf{zoom: 2.5; display: flex;}.fa{padding: 0px;font-size: 36px;width: auto;height: auto;text-align: center;text-decoration: none;border-radius: 50%; margin-left: 8px;}.fa-facebook{width: 30px; height: 30px; border-radius: 50%; align-items: center; display: flex; justify-content: center;background: #3B5998;color: white;}.fa-telegram{width: 30px; height: 30px; border-radius: 50%; align-items: center; display: flex; justify-content: center;background: #25AFE2;color: white;}.fa-instagram{width: 30px; height: 30px; border-radius: 50%; align-items: center; display: flex; justify-content: center;background: #FF3ED8;color: white;}.fa-github{width: 30px; height: 30px;border-radius: 50%;align-items: center; display: flex; justify-content: center;background: #666666;color: white;}.center-div {align-items: center; justify-content: center; display: flex; font-size: 30px; font-family: 'Open Sans'; max-width: fit-content; margin-left: auto;margin-right: auto;background-color: #ffffff;}table {border-collapse: collapse;font-family: 'Open Sans';}table td {padding: 8px;}table thead td {background-color: #54585d;color: #ffffff;font-weight: bold;font-size: 14px;border: 1px solid #54585d;}table tbody td {color: #000000;border: 1px solid #dddfe1;}table tbody tr {background-color: #f9fafb;}table tbody tr:nth-child(odd) {background-color: #ffffff;}.lecture {background-color: #1c7cd0;}.lab {background-color: #52aa35;}.practical {background-color: #de8f18;}.practic {background-color: #c65316;}.seminar {background-color: #761e8f;}.course {background-color: #bc2a2a;}</style></head><body>"));
    std::string tval(dfff);
    if (atoi(getMins().c_str()) >= atoi((char*)OBFUSCATE("45")))
        tval = ReplaceString(tval, (std::string(OBFUSCATE("<th class=\"yAxis\">")) + getTime() + (char*)OBFUSCATE("</th>")), (std::string(OBFUSCATE("<th class=\"yAxis\"><font color=\"orange\">")) + getTime() + (char*)OBFUSCATE("<br>(ENDING SOON)</font></th>")));
    else
        tval = ReplaceString(tval, (std::string(OBFUSCATE("<th class=\"yAxis\">")) + getTime() + (char*)OBFUSCATE("</th>")), (std::string(OBFUSCATE("<th class=\"yAxis\"><font color=\"red\">")) + getTime() + (char*)OBFUSCATE("<br>(NOW)</font></th>")));
        
    if (atoi(getMins().c_str()) >= atoi((char*)OBFUSCATE("55")))   
        tval = ReplaceString(tval, (std::string(OBFUSCATE("<th class=\"yAxis\">")) + getNextTime() + (char*)OBFUSCATE("</th>")), (std::string(OBFUSCATE("<th class=\"yAxis\"><font color=\"#009DD7\">")) + getNextTime() + (char*)OBFUSCATE("<br>(STARTING SOON)</font></th>")));
    else    
        tval = ReplaceString(tval, (std::string(OBFUSCATE("<th class=\"yAxis\">")) + getNextTime() + (char*)OBFUSCATE("</th>")), (std::string(OBFUSCATE("<th class=\"yAxis\"><font color=\"green\">")) + getNextTime() + (char*)OBFUSCATE("<br>(NEXT)</font></th>")));
    tval = ReplaceString(tval, (std::string(OBFUSCATE("<th class=\"xAxis\">")) + getDay() + (char*)OBFUSCATE("</th>")), (std::string(OBFUSCATE("<th class=\"xAxis\"><font color=\"red\">")) + getDay() + (char*)OBFUSCATE(" (TODAY)</font></th>")));
    ret.append(tval); //table content
    ret.append(std::string(OBFUSCATE("<br><br><div class=\"center-div\"><b>App Creator: </b><br><br><div class=\"inf\"><a href=\"https://t.me/NikkaGamesOfficial\" class=\"fa fa-telegram\"/><a href=\"https://facebook.com/sunflower.thrust\" class=\"fa fa-facebook\" /><a href=\"https://www.instagram.com/nikka_oqro/\" class=\"fa fa-instagram\"/><a href=\"https://github.com/NikkaGames\" class=\"fa fa-github\"/></div></div>")));
    ret.append(std::string(OBFUSCATE("</body></html>")));
    //logger->append(ret);
    return env->NewStringUTF(ret.c_str());
}

}

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *globalEnv;
    vm->GetEnv((void **) &globalEnv, JNI_VERSION_1_6);
    jvm = vm;
    std::string logpath(getCacheDir(globalEnv));
    logpath.append((char*)OBFUSCATE("/gtulogs.txt"));
    logger = new FLog(logpath);
    logger->append_arg(OBFUSCATE("beggining of jvm %p"), vm);
    /*if (getApkSign(globalEnv).find((char*)OBFUSCATE_KEY("09DC8B31922E2CA26F0EA3C42E54DD84", (char)0x023)) == -1) {
        //Toast(globalEnv, GetContext(globalEnv), "Signature verified!", 1);
        setDialogMD(GetActivityContext(globalEnv), globalEnv, OBFUSCATE("Error"), OBFUSCATE("\nInvalid signature!\n"));
    }*/
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL
JNI_OnUnload(JavaVM *vm, void *reserved) {
    exit(0);
}



