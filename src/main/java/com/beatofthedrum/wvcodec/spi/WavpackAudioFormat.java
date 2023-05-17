package com.beatofthedrum.wvcodec.spi;

import com.beatofthedrum.wvcodec.WavpackContext;
import com.beatofthedrum.wvcodec.WavpackUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import java.util.HashMap;
import java.util.Map;

import static com.beatofthedrum.wvcodec.spi.WavpackAudioFormat.Encoding.WAVPACK;
import static javax.sound.sampled.AudioSystem.NOT_SPECIFIED;

public class WavpackAudioFormat extends AudioFormat {

    public static class Encoding extends AudioFormat.Encoding {
        public static final AudioFormat.Encoding WAVPACK = new Encoding("WavPack");
        private Encoding(String name) {
            super(name);
        }
    }

    public WavpackAudioFormat(WavpackContext wpc) {
        super(
                WAVPACK,
                WavpackUtils.WavpackGetSampleRate(wpc),
                WavpackUtils.WavpackGetBitsPerSample(wpc),
                WavpackUtils.WavpackGetReducedChannels(wpc),
                frameSize(WavpackUtils.WavpackGetReducedChannels(wpc), WavpackUtils.WavpackGetBitsPerSample(wpc)),
                WavpackUtils.WavpackGetSampleRate(wpc),
                false,
                generateProperties(wpc)
        );
    }

    private static Map<String, Object> generateProperties(WavpackContext wpc) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("vbr", true);
        properties.put("bitrate", WavpackUtils.WavpackGetBitsPerSample(wpc) * WavpackUtils.WavpackGetSampleRate(wpc));
        return properties;
    }

    private static int frameSize(int channels, int sampleSizeInBits) {
        return (channels == NOT_SPECIFIED || sampleSizeInBits == NOT_SPECIFIED)?
                NOT_SPECIFIED:
                ((sampleSizeInBits + 7) / 8) * channels;
    }

    @Override
    public String toString() {
        String sEndian = "";
        if (getEncoding().equals(WAVPACK) && ((getSampleSizeInBits() > 8)
                || (getSampleSizeInBits() == AudioSystem.NOT_SPECIFIED))) {
            if (isBigEndian()) {
                sEndian = "big-endian";
            } else {
                sEndian = "little-endian";
            }
        }
        return super.toString() + sEndian;
    }

}
