package com.beatofthedrum.wvcodec.spi;

import com.beatofthedrum.wvcodec.Defines;
import com.beatofthedrum.wvcodec.WavpackUtils;
import com.beatofthedrum.wvcodec.WavpackContext;
import com.beatofthedrum.wvcodec.WavpackException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static com.beatofthedrum.wvcodec.spi.WavpackAudioFileFormatType.WAVPACK;
import static java.nio.file.StandardOpenOption.READ;
import static javax.sound.sampled.AudioSystem.NOT_SPECIFIED;

public class WavpackAudioFileReader extends AudioFileReader {

    @Override
    public AudioFileFormat getAudioFileFormat(InputStream stream) throws UnsupportedAudioFileException, IOException {
        final WavpackContext sc;
        if (stream instanceof java.io.DataInputStream) sc = WavpackUtils.WavpackOpenFileInput((java.io.DataInputStream) stream);
        else {
            stream.mark(1000);
            sc = WavpackUtils.WavpackOpenFileInput(new DataInputStream(stream));
        }
        if (sc.error) {
            if (!(stream instanceof java.io.DataInputStream)) stream.reset();
            throwExceptions(sc);
        }
        return getAudioFileFormat(sc, new HashMap<>(), new HashMap<>());
    }

    @Override
    public AudioFileFormat getAudioFileFormat(URL url) throws UnsupportedAudioFileException, IOException {
        WavpackContext sc = WavpackUtils.WavpackOpenFileInput(new DataInputStream(url.openStream()));
        throwExceptions(sc);
        try {
            return getAudioFileFormat(sc, new HashMap<>(), new HashMap<>());
        }
        finally {
            WavpackUtils.WavpackCloseFile(sc);
        }
    }

    @Override
    public AudioFileFormat getAudioFileFormat(File file) throws UnsupportedAudioFileException, IOException {
        WavpackContext sc = WavpackUtils.WavpackOpenFileInput(new DataInputStream(Files.newInputStream(file.toPath(), READ)));
        throwExceptions(sc);
        try {
            return getAudioFileFormat(sc, new HashMap<>(), new HashMap<>());
        }
        finally {
            WavpackUtils.WavpackCloseFile(sc);
        }
    }

    @Override
    public AudioInputStream getAudioInputStream(InputStream stream) throws UnsupportedAudioFileException, IOException {
        if (stream instanceof java.io.DataInputStream) {
            WavpackContext wpc = WavpackUtils.WavpackOpenFileInput((java.io.DataInputStream) stream);
            throwExceptions(wpc);
            return new WavpackAudioInputStream(wpc, getAudioFormat(wpc, new HashMap<>()), WavpackUtils.WavpackGetNumSamples(wpc));
        }
        stream.mark(1000);
        try {
            WavpackContext sc = WavpackUtils.WavpackOpenFileInput(new DataInputStream(stream));
            throwExceptions(sc);
            return new WavpackAudioInputStream(sc, getAudioFormat(sc, new HashMap<>()), WavpackUtils.WavpackGetNumSamples(sc));
        }
        catch (UnsupportedAudioFileException | IOException e) {
            stream.reset();
            throw e;
        }
    }

    @Override
    public AudioInputStream getAudioInputStream(URL url) throws UnsupportedAudioFileException, IOException {
        InputStream stream = url.openStream();
        try {
            WavpackContext wpc = WavpackUtils.WavpackOpenFileInput(new DataInputStream(stream));
            throwExceptions(wpc);
            return new WavpackAudioInputStream(wpc, getAudioFormat(wpc, new HashMap<>()), WavpackUtils.WavpackGetNumSamples(wpc));
        }
        catch (UnsupportedAudioFileException | IOException e) {
            stream.close();
            throw e;
        }
    }

    @Override
    public AudioInputStream getAudioInputStream(File file) throws UnsupportedAudioFileException, IOException {
        InputStream stream = Files.newInputStream(file.toPath(), READ);
        try {
            WavpackContext wpc = WavpackUtils.WavpackOpenFileInput(new DataInputStream(stream));
            throwExceptions(wpc);
            return new WavpackAudioInputStream(wpc, getAudioFormat(wpc, new HashMap<>()), WavpackUtils.WavpackGetNumSamples(wpc));
        }
        catch (UnsupportedAudioFileException | IOException e) {
            stream.close();
            throw e;
        }
    }

    private static void throwExceptions(WavpackContext wpc) throws UnsupportedAudioFileException, IOException {
        if (wpc.error) {
            if (wpc.error_message instanceof WavpackException) throw new UnsupportedAudioFileException();
            else if (wpc.error_message instanceof IOException) throw (IOException) wpc.error_message;
            else throw new IOException(wpc.error_message);
        }
    }

    private static AudioFileFormat getAudioFileFormat(WavpackContext wpc,
                                                      Map<String, Object> fileProperties,
                                                      Map<String, Object> formatProperties) {
        long samples = WavpackUtils.WavpackGetNumSamples(wpc);
        long sample_rate = WavpackUtils.WavpackGetSampleRate(wpc);
        int channels = WavpackUtils.WavpackGetReducedChannels(wpc);
        int bytes_per_sample = WavpackUtils.WavpackGetBytesPerSample(wpc);
        int bits_per_sample = WavpackUtils.WavpackGetBitsPerSample(wpc);
        formatProperties.put("samples", samples);
        formatProperties.put("samplerate", sample_rate);
        formatProperties.put("samplesizeinbytes", bytes_per_sample);
        formatProperties.put("samplesizeinbits", bits_per_sample);
        formatProperties.put("channels", channels);
        formatProperties.put("bigendian", false);
        fileProperties.put("wv.channels", WavpackUtils.WavpackGetNumChannels(wpc));
        fileProperties.put("wv.lossyblocks", wpc.lossy_blocks == Defines.TRUE);
        return new AudioFileFormat(WAVPACK,
                new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sample_rate, bits_per_sample,
                        channels, frameSize(channels, bits_per_sample),
                        sample_rate, false, formatProperties), 
                (int) Math.min(Integer.MAX_VALUE, samples), fileProperties);
    }

    private static AudioFormat getAudioFormat(WavpackContext wpc, Map<String, Object> formatProperties) {
        long samples = WavpackUtils.WavpackGetNumSamples(wpc);
        long sample_rate = WavpackUtils.WavpackGetSampleRate(wpc);
        int channels = WavpackUtils.WavpackGetReducedChannels(wpc);
        int bytes_per_sample = WavpackUtils.WavpackGetBytesPerSample(wpc);
        int bits_per_sample = WavpackUtils.WavpackGetBitsPerSample(wpc);
        formatProperties.put("samples", samples);
        formatProperties.put("samplerate", sample_rate);
        formatProperties.put("samplesizeinbytes", bytes_per_sample);
        formatProperties.put("samplesizeinbits", bits_per_sample);
        formatProperties.put("channels", channels);
        formatProperties.put("bigendian", false);
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sample_rate, bits_per_sample,
                channels, frameSize(channels, bits_per_sample),
                sample_rate, false, formatProperties);
    }

    private static int frameSize(int channels, int sampleSizeInBits) {
        return (channels == NOT_SPECIFIED || sampleSizeInBits == NOT_SPECIFIED)?
                NOT_SPECIFIED:
                ((sampleSizeInBits + 7) / 8) * channels;
    }

}
