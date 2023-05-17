package com.beatofthedrum.wvcodec.spi;

import com.beatofthedrum.wvcodec.*;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.spi.AudioFileWriter;
import java.io.*;
import java.nio.file.Files;

import static com.beatofthedrum.wvcodec.spi.WavpackAudioFileFormat.Type.WAVPACK;
import static java.nio.file.StandardOpenOption.WRITE;

public class WavpackAudioFileWriter extends AudioFileWriter {

    @Override
    public AudioFileFormat.Type[] getAudioFileTypes() {
        return new AudioFileFormat.Type[] { WAVPACK };
    }

    @Override
    public AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream stream) {
        if (isAudioFormatSupported(stream.getFormat())) return getAudioFileTypes();
        else return new AudioFileFormat.Type[0];
    }

    private static boolean isAudioFormatSupported(AudioFormat format) {
        return format.getEncoding() == AudioFormat.Encoding.PCM_SIGNED
                && format.getSampleSizeInBits() >= 8
                && format.getSampleSizeInBits() <= 24
                && (format.getChannels() == 1 || format.getChannels() == 2);
    }

    @Override
    public int write(AudioInputStream stream, AudioFileFormat.Type fileType, OutputStream out) throws IOException {
        if (fileType == WAVPACK) {
            if (!isAudioFormatSupported(stream.getFormat())) throw new IllegalArgumentException("Data Format not supported");
            WavpackConfig config = new WavpackConfig();
            config.bitrate = (int) (stream.getFormat().getSampleRate() * stream.getFormat().getSampleSizeInBits());
            try {
                return (int) pack_file(stream, out, config);
            }
            finally {
                out.close();
            }
        }
        else throw new IllegalArgumentException("File type " + fileType + " not supported.");
    }

    @Override
    public int write(AudioInputStream stream, AudioFileFormat.Type fileType, File out) throws IOException {
        return write(stream, fileType, Files.newOutputStream(out.toPath(), WRITE));
    }

    private static long pack_file(AudioInputStream infile, OutputStream wv_file, WavpackConfig config) throws IOException {

        WavpackContext wpc = new WavpackContext();
        java.io.DataInputStream in = new java.io.DataInputStream(infile);

        wpc.outfile = wv_file;

        AudioFormat audioFormat = infile.getFormat();
        config.bits_per_sample = audioFormat.getSampleSizeInBits();
        config.bytes_per_sample = audioFormat.getSampleSizeInBits() / 8;
        config.num_channels = audioFormat.getChannels();
        config.sample_rate = (long) audioFormat.getSampleRate();

        WavpackUtils.WavpackSetConfiguration(wpc, config, infile.getFrameLength());

        // pack the audio portion of the file now
        pack_audio(wpc, in);

        // we're now done with any WavPack blocks, so flush any remaining data
        if (wpc.error && WavpackUtils.WavpackFlushSamples(wpc) == 0)
        {
            throw wpc.error_message;
        }

        // At this point we're done writing to the output files. However, in some
        // situations we might have to back up and re-write the initial blocks.
        // Currently the only case is if we're ignoring length.
        if (WavpackUtils.WavpackGetNumSamples(wpc) != WavpackUtils.WavpackGetSampleIndex(wpc)) {
            throw new IOException("couldn't read all samples, file may be corrupt!!");
        }

        return wpc.filelen;
    }

    // This function handles the actual audio data compression. It assumes that the
    // input file is positioned at the beginning of the audio data and that the
    // WavPack configuration has been set. This is where the conversion from RIFF
    // little-endian standard the executing processor's format is done.
    private static void pack_audio(WavpackContext wpc, java.io.DataInputStream in) throws IOException {

        long samples_remaining;
        int bytes_per_sample;

        WavpackUtils.WavpackPackInit(wpc);

        bytes_per_sample = WavpackUtils.WavpackGetBytesPerSample(wpc) * WavpackUtils.WavpackGetNumChannels(wpc);

        samples_remaining = WavpackUtils.WavpackGetNumSamples(wpc);

        byte[] input_buffer = new byte[Defines.INPUT_SAMPLES * bytes_per_sample];
        long[] sample_buffer = new long[(Defines.INPUT_SAMPLES * 4 * WavpackUtils.WavpackGetNumChannels(wpc))];

        int temp = 0;

        //while (temp < 1)
        while (true)
        {
            long sample_count;
            long bytes_read = 0;
            int bytes_to_read;

            temp = temp + 1;

            if (samples_remaining > Defines.INPUT_SAMPLES)
            {
                bytes_to_read = Defines.INPUT_SAMPLES * bytes_per_sample;
            }
            else
            {
                bytes_to_read = (int) (samples_remaining * bytes_per_sample);
            }

            samples_remaining -= (bytes_to_read / bytes_per_sample);
            bytes_read = DoReadFile(in, input_buffer, bytes_to_read);
            sample_count = bytes_read / bytes_per_sample;

            if (sample_count == 0)
            {
                break;
            }

            if (sample_count > 0)
            {
                int cnt = (int) (sample_count * WavpackUtils.WavpackGetNumChannels(wpc));

                byte[] sptr = input_buffer;
                long[] dptr = sample_buffer;
                int loopBps = 0;

                loopBps = WavpackUtils.WavpackGetBytesPerSample(wpc);

                if (loopBps == 1)
                {
                    int intermalCount = 0;

                    while (cnt > 0)
                    {
                        dptr[intermalCount] = (sptr[intermalCount] & 0xff) - 128;
                        intermalCount++;
                        cnt--;
                    }
                }
                else if (loopBps == 2)
                {
                    int dcounter = 0;
                    int scounter = 0;

                    while (cnt > 0)
                    {
                        dptr[dcounter] = (sptr[scounter] & 0xff) | (sptr[scounter + 1] << 8);
                        scounter = scounter + 2;
                        dcounter++;
                        cnt--;
                    }
                }
                else if (loopBps == 3)
                {
                    int dcounter = 0;
                    int scounter = 0;

                    while (cnt > 0)
                    {
                        dptr[dcounter] = (sptr[scounter] & 0xff) |
                                ((sptr[scounter + 1] & 0xff) << 8) | (sptr[scounter + 2] << 16);
                        scounter = scounter + 3;
                        dcounter++;
                        cnt--;
                    }
                }
            }

            wpc.byte_idx = 0; // new WAV buffer data so reset the buffer index to zero

            if (WavpackUtils.WavpackPackSamples(wpc, sample_buffer, sample_count) == 0)
            {
                throw wpc.error_message;
            }
        }

        if (WavpackUtils.WavpackFlushSamples(wpc) == 0)
        {
            throw wpc.error_message;
        }

    }

    //////////////////////////// File I/O Wrapper ////////////////////////////////
    private static long DoReadFile(java.io.DataInputStream hFile, byte[] lpBuffer, int nNumberOfBytesToRead) throws IOException {
        long bcount;
        byte[] tempBuffer = new byte[(int) (nNumberOfBytesToRead + (long) 1)];
        long bufferCounter = 0;
        long lpNumberOfBytesRead = 0;

        while (nNumberOfBytesToRead > 0)
        {
            bcount = hFile.read(tempBuffer, 0, nNumberOfBytesToRead);

            if (bcount > 0)
            {
                for (long i = 0; i < nNumberOfBytesToRead; i++)
                {
                    lpBuffer[(int) (bufferCounter + i)] = tempBuffer[(int) i];
                }

                lpNumberOfBytesRead += bcount;
                nNumberOfBytesToRead -= bcount;
            }
            else
            {
                break;
            }
        }

        return lpNumberOfBytesRead;
    }

}
