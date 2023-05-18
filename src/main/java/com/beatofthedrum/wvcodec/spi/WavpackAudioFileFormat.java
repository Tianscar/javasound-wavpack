package com.beatofthedrum.wvcodec.spi;

import com.beatofthedrum.wvcodec.Defines;
import com.beatofthedrum.wvcodec.WavpackContext;
import com.beatofthedrum.wvcodec.WavpackUtils;

import javax.sound.sampled.AudioFileFormat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.beatofthedrum.wvcodec.spi.WavpackAudioFileFormat.Type.WAVPACK;

public class WavpackAudioFileFormat extends AudioFileFormat {

    public static class Type extends AudioFileFormat.Type {
        public static final AudioFileFormat.Type WAVPACK = new Type("WavPack", "wv");
        private Type(String name, String extension) {
            super(name, extension);
        }
    }

    private final HashMap<String, Object> props;

    public WavpackAudioFileFormat(WavpackContext wpc, long byteLength) {
        super(WAVPACK, (int) byteLength, new WavpackAudioFormat(wpc), (int) WavpackUtils.WavpackGetNumSamples(wpc));

        props = new HashMap<>();
        //props.put("duration", (long) (((double) WavpackUtils.WavpackGetNumSamples(wpc) / (double) WavpackUtils.WavpackGetSampleRate(wpc)) * 1_000_000L));
        props.put("wv.channels", WavpackUtils.WavpackGetNumChannels(wpc));
        props.put("wv.lossless", wpc.lossy_blocks == Defines.FALSE);
        props.put("wv.hybrid", (wpc.config.flags & Defines.CONFIG_HYBRID_FLAG) != 0);
    }

    /**
     * Java 5.0 compatible method to get the full map of properties. The
     * properties use the KEY_ keys defined in this class.
     */
    public Map<String, Object> properties() {
        Map<String, Object> ret;
        if (props == null) {
            ret = new HashMap<>(0);
        } else {
            ret = (Map<String, Object>) props.clone();
        }
        return Collections.unmodifiableMap(ret);
    }

    /**
     * Java 5.0 compatible method to get a property. As key use the KEY_ constants defined in this class.
     */
    public Object getProperty(String key) {
        if (props == null) {
            return null;
        }
        return props.get(key);
    }

}
