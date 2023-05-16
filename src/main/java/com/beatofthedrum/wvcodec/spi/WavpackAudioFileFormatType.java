package com.beatofthedrum.wvcodec.spi;

import javax.sound.sampled.AudioFileFormat;

public class WavpackAudioFileFormatType extends AudioFileFormat.Type {

    public static final AudioFileFormat.Type WAVPACK = new WavpackAudioFileFormatType("WavPack", "wv");

    private WavpackAudioFileFormatType(String name, String extension) {
        super(name, extension);
    }

}
