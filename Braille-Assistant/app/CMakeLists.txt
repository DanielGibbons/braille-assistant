# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add_library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.

add_library( # Specifies the name of the library.
        liblouis

        # Sets the library as a shared library.
        STATIC

        # Provides a relative path to your source file(s).
        src/main/cpp/liblouis/liblouis/compileTranslationTable.c
        src/main/cpp/liblouis/liblouis/logging.c
        src/main/cpp/liblouis/liblouis/lou_backTranslateString.c
        src/main/cpp/liblouis/liblouis/lou_translateString.c
        src/main/cpp/liblouis/liblouis/wrappers.c)

add_library( # Specifies the name of the library.
             liblouiswrapper

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/lib-louis-wrapper.cpp)

find_library(log-lib log)
find_library(android-lib android)
target_link_libraries(liblouis ${log-lib})
target_link_libraries(liblouis ${android-lib})
target_link_libraries(liblouiswrapper ${log-lib})
target_link_libraries(liblouiswrapper ${android-lib})
target_link_libraries(liblouiswrapper liblouis)