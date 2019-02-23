# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.6.0)

#enable_language(CXX)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add.library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.

add_library( # Specifies the name of the library.
             wtf-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             #src/main/cpp/wtf-lib.cpp
             wtf-lib.cpp
             )
find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)
# Specifies a path to native header files.
include_directories(include/)

target_link_libraries(wtf-lib
android
-Lwebcore
${log-lib})