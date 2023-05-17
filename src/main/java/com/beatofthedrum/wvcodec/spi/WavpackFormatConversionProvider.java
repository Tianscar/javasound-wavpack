package com.beatofthedrum.wvcodec.spi;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.spi.FormatConversionProvider;
import java.io.IOException;

import static com.beatofthedrum.wvcodec.spi.WavpackAudioFormat.Encoding.WAVPACK;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.NOT_SPECIFIED;

public class WavpackFormatConversionProvider extends FormatConversionProvider {

    private static final AudioFormat.Encoding[] SOURCE_ENCODINGS = new AudioFormat.Encoding[] { WAVPACK };
    private static final AudioFormat.Encoding[] TARGET_ENCODINGS = new AudioFormat.Encoding[] { PCM_SIGNED };

    @Override
    public AudioFormat.Encoding[] getSourceEncodings() {
        return SOURCE_ENCODINGS.clone();
    }

    @Override
    public AudioFormat.Encoding[] getTargetEncodings() {
        return TARGET_ENCODINGS.clone();
    }

    @Override
    public AudioFormat.Encoding[] getTargetEncodings(AudioFormat sourceFormat) {
        if (sourceFormat.getEncoding().equals(WAVPACK)) return TARGET_ENCODINGS.clone();
        else return new AudioFormat.Encoding[0];
    }

    @Override
    public AudioFormat[] getTargetFormats(AudioFormat.Encoding targetEncoding, AudioFormat sourceFormat) {
        if (sourceFormat.getEncoding().equals(WAVPACK)) {
            return new AudioFormat[] { getTargetFormat(sourceFormat) };
        }
        else return new AudioFormat[0];
    }

    private static AudioFormat getTargetFormat(AudioFormat sourceFormat) {
        return new AudioFormat(
                PCM_SIGNED,
                NOT_SPECIFIED,
                sourceFormat.getSampleSizeInBits(),
                sourceFormat.getChannels(),
                NOT_SPECIFIED,
                NOT_SPECIFIED,
                sourceFormat.isBigEndian()
        );
    }

    @Override
    public AudioInputStream getAudioInputStream(AudioFormat.Encoding targetEncoding, AudioInputStream sourceStream) {
        return getAudioInputStream(getTargetFormat(sourceStream.getFormat()), sourceStream);
    }

    @Override
    public AudioInputStream getAudioInputStream(AudioFormat targetFormat, AudioInputStream sourceStream) {
        if (targetFormat.getEncoding() == PCM_SIGNED && sourceStream instanceof WavpackAudioInputStream) {
            AudioFormat sourceFormat = sourceStream.getFormat();
            if (sourceFormat.isBigEndian() == targetFormat.isBigEndian() &&
            sourceFormat.getChannels() == targetFormat.getChannels() &&
            sourceFormat.getSampleSizeInBits() == targetFormat.getSampleSizeInBits()) {
                try {
                    return new Wavpack2PcmAudioInputStream((WavpackAudioInputStream) sourceStream);
                }
                catch (IOException ignored) {}
            }
            throw new IllegalArgumentException("unable to convert "
                    + sourceFormat + " to "
                    + targetFormat);
        }
        else throw new IllegalArgumentException("conversion not supported");
    }

}
