/*
 * Adopted from https://github.com/umjammer/JAADec/blob/0.8.9/src/test/java/net/sourceforge/jaad/spi/javasound/AacFormatConversionProviderTest.java
 *
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 * Copyright (c) 2023 by Karstian Lee, All rights reserved.
 *
 * Originally programmed by Naohide Sano
 * Modifications by Karstian Lee
 */

package com.beatofthedrum.wvcodec.test;

import com.beatofthedrum.wvcodec.spi.WavpackAudioFileFormat;
import com.beatofthedrum.wvcodec.spi.WavpackAudioFileReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WavPackTest {

    @Test
    @DisplayName("unsupported exception is able to detect in 3 ways")
    public void unsupported() {

        Path path = Paths.get("src/test/resources/fbodemo1_vorbis.ogg");

        assertThrows(UnsupportedAudioFileException.class, () -> {
            // don't replace with Files#newInputStream(Path)
            new WavpackAudioFileReader().getAudioInputStream(new BufferedInputStream(Files.newInputStream(path.toFile().toPath())));
        });

        assertThrows(UnsupportedAudioFileException.class, () -> {
            new WavpackAudioFileReader().getAudioInputStream(path.toFile());
        });

        assertThrows(UnsupportedAudioFileException.class, () -> {
            new WavpackAudioFileReader().getAudioInputStream(path.toUri().toURL());
        });
    }

    private void play(AudioInputStream pcmAis) throws LineUnavailableException, IOException {
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, pcmAis.getFormat());
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(pcmAis.getFormat());
        line.start();

        byte[] buf = new byte[128 * 6];
        while (true) {
            int r = pcmAis.read(buf, 0, buf.length);
            if (r < 0) {
                break;
            }
            line.write(buf, 0, r);
        }
        line.drain();
        line.stop();
        line.close();
    }

    private AudioInputStream decode(AudioInputStream wavpackAis) {
        AudioFormat inAudioFormat = wavpackAis.getFormat();
        AudioFormat decodedAudioFormat = new AudioFormat(
                AudioSystem.NOT_SPECIFIED,
                inAudioFormat.getSampleSizeInBits(),
                inAudioFormat.getChannels(),
                true,
                inAudioFormat.isBigEndian());
        return AudioSystem.getAudioInputStream(decodedAudioFormat, wavpackAis);
    }

    @Test
    @DisplayName("wavpack -> pcm, play via SPI")
    public void convertWavPackToPCMAndPlay() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File file = new File("src/test/resources/fbodemo1.wv");
        System.out.println("in file: " + file.getAbsolutePath());
        AudioInputStream wavpackAis = AudioSystem.getAudioInputStream(file);
        System.out.println("in stream: " + wavpackAis);
        AudioFormat inAudioFormat = wavpackAis.getFormat();
        System.out.println("in audio format: " + inAudioFormat);

        AudioFormat decodedAudioFormat = new AudioFormat(
                AudioSystem.NOT_SPECIFIED,
                inAudioFormat.getSampleSizeInBits(),
                inAudioFormat.getChannels(),
                true,
                inAudioFormat.isBigEndian());

        assertTrue(AudioSystem.isConversionSupported(decodedAudioFormat, inAudioFormat));

        wavpackAis = AudioSystem.getAudioInputStream(decodedAudioFormat, wavpackAis);
        decodedAudioFormat = wavpackAis.getFormat();
        System.out.println("decoded in stream: " + wavpackAis);
        System.out.println("decoded audio format: " + decodedAudioFormat);

        AudioFormat outAudioFormat = new AudioFormat(
            decodedAudioFormat.getSampleRate(),
            16,
            decodedAudioFormat.getChannels(),
            true,
            false);

        assertTrue(AudioSystem.isConversionSupported(outAudioFormat, decodedAudioFormat));

        AudioInputStream pcmAis = AudioSystem.getAudioInputStream(outAudioFormat, wavpackAis);
        System.out.println("out stream: " + pcmAis);
        System.out.println("out audio format: " + pcmAis.getFormat());

        play(pcmAis);
        pcmAis.close();
    }

    @Test
    @DisplayName("play WavPack from InputStream via SPI")
    public void playWavPackInputStream() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("fbodemo1.wv");
        AudioInputStream wavpackAis = decode(AudioSystem.getAudioInputStream(stream));
        play(wavpackAis);
        wavpackAis.close();
    }

    @Test
    @DisplayName("play WavPack from URL via SPI")
    public void playWavPackURL() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        URL url = new URL("https://github.com/Tianscar/fbodemo1/raw/main/fbodemo1.wv");
        AudioInputStream wavpackAis = decode(AudioSystem.getAudioInputStream(url));
        play(wavpackAis);
        wavpackAis.close();
    }

    @Test
    @DisplayName("pcm -> wavpack via SPI")
    public void encodeWavPackFile() throws UnsupportedAudioFileException, IOException {
        AudioInputStream pcmAis = AudioSystem.getAudioInputStream(new File("src/test/resources/fbodemo1.wav"));
        File outFile = new File("fbodemo1.wv");
        if (!outFile.exists()) assertTrue(outFile.createNewFile());
        AudioSystem.write(pcmAis, WavpackAudioFileFormat.Type.WAVPACK, outFile);
        pcmAis.close();
    }

    @Test
    @DisplayName("list WavPack properties")
    public void listWavPackProperties() throws UnsupportedAudioFileException, IOException {
        File file = new File("src/test/resources/fbodemo1.wv");
        AudioFileFormat wavpackAff = AudioSystem.getAudioFileFormat(file);
        for (Map.Entry<String, Object> entry : wavpackAff.properties().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        for (Map.Entry<String, Object> entry : wavpackAff.getFormat().properties().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("framelength: " + wavpackAff.getFrameLength());
        System.out.println("duration: " + (long) (((double) wavpackAff.getFrameLength() / (double) wavpackAff.getFormat().getFrameRate()) * 1_000_000L));
    }

    @Test
    @DisplayName("can play wav")
    public void checkCanPlayWAV() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        InputStream stream = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("fbodemo1.wav"));
        stream.mark(Integer.MAX_VALUE);
        AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(stream);
        System.out.println(audioFileFormat);
        stream.reset();
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(stream);
        play(audioInputStream);
        audioInputStream.close();
    }

}
