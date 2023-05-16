package com.beatofthedrum.wvcodec.spi;

import com.beatofthedrum.wvcodec.Defines;
import com.beatofthedrum.wvcodec.WavpackUtils;
import com.beatofthedrum.wvcodec.WavpackContext;

import javax.sound.sampled.AudioFormat;
import java.io.EOFException;
import java.io.IOException;

class WavpackAudioInputStream extends AsynchronousAudioInputStream {

    private final int[] temp_buffer;
    private final byte[] pcm_buffer;
    private final int bps;
    private final int num_channels;

    private final WavpackContext wpc;

    WavpackAudioInputStream(WavpackContext wpc, AudioFormat format, long length) throws IOException {
        super(wpc.infile, format, length);
        this.wpc = wpc;
        this.temp_buffer = new int[Defines.SAMPLE_BUFFER_SIZE];
        this.pcm_buffer = new byte[4 * Defines.SAMPLE_BUFFER_SIZE];
        this.bps = WavpackUtils.WavpackGetBytesPerSample(wpc);
        this.num_channels = WavpackUtils.WavpackGetReducedChannels(wpc);
    }

    @Override
    public void execute() {
        try {
            long samples_unpacked; // was uint32_t in C

            samples_unpacked = WavpackUtils.WavpackUnpackSamples(wpc, temp_buffer, Defines.SAMPLE_BUFFER_SIZE / num_channels);
            if (wpc.error) {
                if (wpc.error_message instanceof IOException) throw (IOException) wpc.error_message;
                else throw new IOException(wpc.error_message);
            }

            if (samples_unpacked > 0)
            {
                samples_unpacked = samples_unpacked * num_channels;

                format_samples(pcm_buffer, bps, temp_buffer, samples_unpacked);
                buffer.write(pcm_buffer, 0, (int) samples_unpacked * bps);
            }
            else if (samples_unpacked == 0) throw new EOFException();
        }
        catch (IOException e) {
            buffer.close();
        }
    }

    // Reformat samples from longs in processor's native endian mode to
    // little-endian data with (possibly) less than 4 bytes / sample.

    static void format_samples(byte[] pcm_buffer, final int bps, int src[], long samcnt)
    {
        int temp;
        int counter = 0;
        int counter2 = 0;

        switch (bps)
        {
            case 1:
                while (samcnt > 0)
                {
                    pcm_buffer[counter] = (byte) (0x00FF & (src[counter] + 128));
                    counter++;
                    samcnt--;
                }
                break;

            case 2:
                while (samcnt > 0)
                {
                    temp = src[counter2];
                    pcm_buffer[counter] = (byte) temp;
                    counter++;
                    pcm_buffer[counter] = (byte) (temp >>> 8);
                    counter++;
                    counter2++;
                    samcnt--;
                }

                break;

            case 3:
                while (samcnt > 0)
                {
                    temp = src[counter2];
                    pcm_buffer[counter] = (byte) temp;
                    counter++;
                    pcm_buffer[counter] = (byte) (temp >>> 8);
                    counter++;
                    pcm_buffer[counter] = (byte) (temp >>> 16);
                    counter++;
                    counter2++;
                    samcnt--;
                }

                break;

            case 4:
                while (samcnt > 0)
                {
                    temp = src[counter2];
                    pcm_buffer[counter] = (byte) temp;
                    counter++;
                    pcm_buffer[counter] = (byte) (temp >>> 8);
                    counter++;
                    pcm_buffer[counter] = (byte) (temp >>> 16);
                    counter++;
                    pcm_buffer[counter] = (byte) (temp >>> 24);
                    counter++;
                    counter2++;
                    samcnt--;
                }

                break;
        }
    }

}
