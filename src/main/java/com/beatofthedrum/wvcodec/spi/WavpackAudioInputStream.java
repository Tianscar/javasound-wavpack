package com.beatofthedrum.wvcodec.spi;

import com.beatofthedrum.wvcodec.WavpackContext;

import javax.sound.sampled.AudioInputStream;

public class WavpackAudioInputStream extends AudioInputStream {

    private final WavpackContext wpc;

    WavpackAudioInputStream(WavpackContext wpc, long length) {
        super(wpc.infile, new WavpackAudioFormat(wpc), length);
        this.wpc = wpc;
    }

    public WavpackContext getWavpackContext() {
        return wpc;
    }

}
