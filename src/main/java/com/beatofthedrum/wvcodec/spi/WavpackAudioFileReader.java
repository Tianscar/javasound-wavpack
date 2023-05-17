package com.beatofthedrum.wvcodec.spi;

import com.beatofthedrum.wvcodec.WavpackContext;
import com.beatofthedrum.wvcodec.WavpackException;
import com.beatofthedrum.wvcodec.WavpackUtils;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

import static java.nio.file.StandardOpenOption.READ;
import static javax.sound.sampled.AudioSystem.NOT_SPECIFIED;

public class WavpackAudioFileReader extends AudioFileReader {

    @Override
    public AudioFileFormat getAudioFileFormat(InputStream stream) throws UnsupportedAudioFileException, IOException {
        final WavpackContext wpc;
        if (stream instanceof java.io.DataInputStream) wpc = WavpackUtils.WavpackOpenFileInput((java.io.DataInputStream) stream);
        else {
            stream.mark(1000);
            wpc = WavpackUtils.WavpackOpenFileInput(new DataInputStream(stream));
        }
        if (wpc.error) {
            if (!(stream instanceof java.io.DataInputStream)) stream.reset();
            throwExceptions(wpc);
        }
        return new WavpackAudioFileFormat(wpc, NOT_SPECIFIED);
    }

    @Override
    public AudioFileFormat getAudioFileFormat(URL url) throws UnsupportedAudioFileException, IOException {
        URLConnection connection = url.openConnection();
        WavpackContext wpc = WavpackUtils.WavpackOpenFileInput(new DataInputStream(connection.getInputStream()));
        throwExceptions(wpc);
        try {
            return new WavpackAudioFileFormat(wpc, connection.getContentLengthLong());
        }
        finally {
            WavpackUtils.WavpackCloseFile(wpc);
        }
    }

    @Override
    public AudioFileFormat getAudioFileFormat(File file) throws UnsupportedAudioFileException, IOException {
        WavpackContext wpc = WavpackUtils.WavpackOpenFileInput(new DataInputStream(Files.newInputStream(file.toPath(), READ)));
        throwExceptions(wpc);
        try {
            return new WavpackAudioFileFormat(wpc, file.length());
        }
        finally {
            WavpackUtils.WavpackCloseFile(wpc);
        }
    }

    @Override
    public AudioInputStream getAudioInputStream(InputStream stream) throws UnsupportedAudioFileException, IOException {
        if (stream instanceof java.io.DataInputStream) {
            WavpackContext wpc = WavpackUtils.WavpackOpenFileInput((java.io.DataInputStream) stream);
            throwExceptions(wpc);
            return new WavpackAudioInputStream(wpc, NOT_SPECIFIED);
        }
        stream.mark(1000);
        try {
            WavpackContext wpc = WavpackUtils.WavpackOpenFileInput(new DataInputStream(stream));
            throwExceptions(wpc);
            return new WavpackAudioInputStream(wpc, NOT_SPECIFIED);
        }
        catch (UnsupportedAudioFileException | IOException e) {
            stream.reset();
            throw e;
        }
    }

    @Override
    public AudioInputStream getAudioInputStream(URL url) throws UnsupportedAudioFileException, IOException {
        URLConnection connection = url.openConnection();
        InputStream stream = connection.getInputStream();
        try {
            WavpackContext wpc = WavpackUtils.WavpackOpenFileInput(new DataInputStream(stream));
            throwExceptions(wpc);
            return new WavpackAudioInputStream(wpc, connection.getContentLengthLong());
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
            return new WavpackAudioInputStream(wpc, file.length());
        }
        catch (UnsupportedAudioFileException | IOException e) {
            stream.close();
            throw e;
        }
    }

    private static void throwExceptions(WavpackContext wpc) throws UnsupportedAudioFileException, IOException {
        if (wpc.error) {
            if (wpc.error_message instanceof WavpackException) throw new UnsupportedAudioFileException();
            else throw wpc.error_message;
        }
    }

}
