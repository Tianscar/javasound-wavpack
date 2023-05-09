# Java Implementation of WavPack Decoder

This is a fork of [Java-WavPack-Decoder](https://github.com/soiaf/Java-WavPack-Decoder), with JavaSound SPI support.

This library is a Java implementation of the tiny version of the WavPack 4.40 decoder.

This decoder will not handle "correction" files, plays only the first two channels of multi-channel files, and is limited in resolution in some large integer or floating point files (but always provides at least 24 bits of resolution). It also will not accept WavPack files from before version 4.0.

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
    implementation 'com.tianscar.javasound:javasound-wavpack:0.4.5'
}
```

## Usage
[Tests and Examples](/src/test/java/com/beatofthedrum/alacdecoder/test)

## License
[BSD 3-Clause](/LICENSE)  
[audios for test](/src/test/resources) originally created by [ProHonor](https://github.com/Aislandz), authorized [me](https://github.com/Tianscar) to use. 2023 (c) ProHonor, all rights reserved.
