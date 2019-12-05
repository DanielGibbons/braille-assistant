//
// Created by dan on 24/11/19.
//

#include "include/native-lib.h"



extern "C" JNIEXPORT jstring JNICALL Java_org_example_brailleassistant_activities_MainActivity_stringFromJNI( JNIEnv* env, jobject obj) {

    return env->NewStringUTF("Hello from JNI!");

}