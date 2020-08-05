//
// Created by dan on 24/11/19.
//

#include <jni.h>
#include <assert.h>
#include <stdlib.h>
#include <string.h>

#include "liblouis/liblouis/liblouis.h"
#include "liblouis/liblouis/louis.h"


extern "C" JNIEXPORT jboolean JNICALL Java_org_example_brailleassistant_utils_LibLouisWrapper_checkTableNative(JNIEnv* env, jclass obj, jstring tableName) {
    jboolean ret = JNI_FALSE;
    const char *tableNameUtf8 = env->GetStringUTFChars(tableName, nullptr);
    if (lou_getTable(tableNameUtf8) == nullptr) {
        env->ReleaseStringUTFChars(tableName, tableNameUtf8);
        return ret;
    }
    ret = JNI_TRUE;
    env->ReleaseStringUTFChars(tableName, tableNameUtf8);
    return ret;
}

extern "C" JNIEXPORT void JNICALL Java_org_example_brailleassistant_utils_LibLouisWrapper_setTablesDirNative(JNIEnv* env, jclass obj, jstring path) {
    // liblouis has a static buffer, which we don't want to overflow.
    if (env->GetStringUTFLength(path) >= MAXSTRING) {
        //LOGE("Braille table path too long");
        return;
    }
    const char* pathUtf8 = env->GetStringUTFChars(path, nullptr);
    if (!pathUtf8) {
        return;
    }
    // The path gets copied.
    // Cast needed to get rid of const.
    //LOGV("Setting tables path to: %s", pathUtf8);
    lou_setDataPath((char*)pathUtf8);
    env->ReleaseStringUTFChars(path, pathUtf8);
}

extern "C" JNIEXPORT jstring JNICALL Java_org_example_brailleassistant_utils_LibLouisWrapper_backTranslateNative(JNIEnv* env, jclass obj, jbyteArray cells, jstring tableName) {
    jstring ret = nullptr;
    const char* tableNameUtf8 = env->GetStringUTFChars(tableName, nullptr);
    if (!tableNameUtf8) {
        return ret;
    }
    int inlen = env->GetArrayLength(cells);
    jbyte* cellsBytes = env->GetByteArrayElements(cells, nullptr);

    //char *spacing = (char *)malloc(sizeof(char) * inlen);
    widechar* inbuf = (widechar*)malloc(sizeof(widechar) * inlen);
    int i;
    for (i = 0; i < inlen; ++i) {
        // Cast to avoid sign extension.
        inbuf[i] = ((unsigned char) cellsBytes[i]) | 0x8000;
        //spacing[i] = 0x8000;
    }
    env->ReleaseByteArrayElements(cells, cellsBytes, JNI_ABORT);
    int outlen = inlen * 2;
    // TODO: Need to do this in a loop like usual character encoding
    // translations, but for now we assume that double size is good enough.
    jchar* outbuf = (jchar*)malloc(sizeof(jchar) * outlen);
    int result = lou_backTranslateString(tableNameUtf8, inbuf, &inlen, outbuf, &outlen, nullptr/*typeform*/, nullptr, dotsIO);
    free(inbuf);
    if (result == 0) {
        free(outbuf);
        env->ReleaseStringUTFChars(tableName, tableNameUtf8);
        return ret;
    }
    ret = env->NewString(outbuf, outlen);

    free(outbuf);
    env->ReleaseStringUTFChars(tableName, tableNameUtf8);
    return ret;
}
