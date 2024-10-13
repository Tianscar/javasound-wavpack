WARNING: THIS PROJECT IS NO LONGER MAINTAINED. MOVED TO: https://github.com/jseproject/jse-spi

# Java Implementation of WavPack Codec

This is a fork of [Java-WavPack-Decoder](https://github.com/soiaf/Java-WavPack-Decoder) and [Java-WavPack-Encoder](https://www.wavpack.com/files/JavaWavPackEncoder_v1.1.zip), with JavaSound SPI support.

This library contains a Java implementation of the tiny version of the WavPack 4.40 decoder. 
It will not handle "correction" files, plays only the first two channels of multi-channel files, and is limited in resolution in some large integer or floating point files (but always provides at least 24 bits of resolution). It also will not accept WavPack files from before version 4.0.

This library also contains a Java implementation of the tiny version of the WavPack 4.40 encoder. 
It accepts file specifications for a RIFF WAV file source and a WavPack file (.wv) destination and optionally a correction file (.wvc) for demonstrating the hybrid lossless mode.  
It does not handle placing the WAV RIFF header into the WavPack file. The regular WavPack unpacker (4.40) and the "tiny decoder" will generate the RIFF header automatically on unpacking and plugins do not generally use the RIFF header information because all relevant information is stored natively. However, older versions of the command-line program will complain about this and require unpacking in "raw" mode.

## Add the library to your project (gradle)
1. Add the Maven Central repository (if not exist) to your build file:
```groovy
repositories {
    ...
    mavenCentral()
}
```

2. Add the dependency:
```groovy
dependencies {
    ...
    implementation 'com.tianscar.javasound:javasound-wavpack:1.4.1'
}
```

## Usage
[Tests and Examples](/src/test/java/com/beatofthedrum/wvcodec/test/)  
[Command-line Interfaces](/src/test/java/com/beatofthedrum/wvcodec/cli/)

Note you need to download test audios [here](https://github.com/Tianscar/fbodemo1) and put them to /src/test/resources to run the test code properly!

## License
[BSD 3-Clause](/LICENSE)
