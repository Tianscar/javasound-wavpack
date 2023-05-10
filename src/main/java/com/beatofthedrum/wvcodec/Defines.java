/*
** Defines.java
**
** Copyright (c) 2007 - 2013 Peter McQuillan
**
** All Rights Reserved.
**                       
** Distributed under the BSD Software License (see license.txt)  
**
*/

package com.beatofthedrum.wvcodec;

public class Defines
{
    // Change the following value to an even number to reflect the maximum number of samples to be processed
    // per call to WavPackUtils.WavpackUnpackSamples

    public static final int SAMPLE_BUFFER_SIZE = 256;
    
    static final int BITSTREAM_BUFFER_SIZE = 1024;
    
    public static final int FALSE = 0;
    public static final int TRUE = 1;

    // or-values for "flags"

    public static final int BYTES_STORED = 3;       // 1-4 bytes/sample
    public static final int MONO_FLAG  = 4;       // not stereo
    public static final int HYBRID_FLAG = 8;       // hybrid mode
    public static final int FALSE_STEREO = 0x40000000;      // block is stereo, but data is mono

    public static final int SHIFT_LSB = 13;
    public static final long  SHIFT_MASK = (0x1fL << SHIFT_LSB);

    public static final int FLOAT_DATA  = 0x80;    // ieee 32-bit floating point data

    public static final int SRATE_LSB = 23;
    public static final long SRATE_MASK = (0xfL << SRATE_LSB);

    public static final int FINAL_BLOCK = 0x1000;  // final block of multichannel segment


    public static final int MIN_STREAM_VERS = 0x402;       // lowest stream version we'll decode
    public static final int MAX_STREAM_VERS = 0x410;       // highest stream version we'll decode


    public static final short ID_DUMMY            =    0x0;
    public static final short ID_ENCODER_INFO     =    0x1;
    public static final short ID_DECORR_TERMS     =    0x2;
    public static final short ID_DECORR_WEIGHTS   =    0x3;
    public static final short ID_DECORR_SAMPLES   =    0x4;
    public static final short ID_ENTROPY_VARS     =    0x5;
    public static final short ID_HYBRID_PROFILE   =    0x6;
    public static final short ID_SHAPING_WEIGHTS  =    0x7;
    public static final short ID_FLOAT_INFO       =    0x8;
    public static final short ID_INT32_INFO       =    0x9;
    public static final short ID_WV_BITSTREAM     =    0xa;
    public static final short ID_WVC_BITSTREAM    =    0xb;
    public static final short ID_WVX_BITSTREAM    =    0xc;
    public static final short ID_CHANNEL_INFO     =    0xd;

    public static final int JOINT_STEREO  =  0x10;    // joint stereo
    public static final int CROSS_DECORR  =  0x20;    // no-delay cross decorrelation
    public static final int HYBRID_SHAPE  =  0x40;    // noise shape (hybrid mode only)

    public static final int INT32_DATA     = 0x100;   // special extended int handling
    public static final int HYBRID_BITRATE = 0x200;   // bitrate noise (hybrid mode only)
    public static final int HYBRID_BALANCE = 0x400;   // balance noise (hybrid stereo mode only)

    public static final int INITIAL_BLOCK  = 0x800;   // initial block of multichannel segment

    public static final int FLOAT_SHIFT_ONES = 1;      // bits left-shifted into float = '1'
    public static final int FLOAT_SHIFT_SAME = 2;      // bits left-shifted into float are the same
    public static final int FLOAT_SHIFT_SENT = 4;      // bits shifted into float are sent literally
    public static final int FLOAT_ZEROS_SENT = 8;      // "zeros" are not all real zeros
    public static final int FLOAT_NEG_ZEROS  = 0x10;   // contains negative zeros
    public static final int FLOAT_EXCEPTIONS = 0x20;   // contains exceptions (inf, nan, etc.)

    public static final short ID_OPTIONAL_DATA      =  0x20;
    public static final int ID_ODD_SIZE           =  0x40;
    public static final int ID_LARGE              =  0x80;

    public static final int MAX_NTERMS = 16;
    public static final int MAX_TERM = 8;

    public static final int MAG_LSB = 18;
    public static final long MAG_MASK = (0x1fL << MAG_LSB);

    public static final short ID_RIFF_HEADER   = 0x21;
    public static final short ID_RIFF_TRAILER  = 0x22;
    public static final short ID_REPLAY_GAIN   = 0x23;
    public static final short ID_CUESHEET      = 0x24;
    public static final short ID_CONFIG_BLOCK	= 0x25;
    public static final short ID_MD5_CHECKSUM  = 0x26;
    public static final short ID_SAMPLE_RATE   = 0x27;

    public static final long CONFIG_BYTES_STORED    = 3;       // 1-4 bytes/sample
    public static final long CONFIG_MONO_FLAG       = 4;       // not stereo
    public static final long CONFIG_HYBRID_FLAG     = 8;       // hybrid mode
    public static final long CONFIG_JOINT_STEREO    = 0x10;    // joint stereo
    public static final long CONFIG_CROSS_DECORR    = 0x20;    // no-delay cross decorrelation
    public static final long CONFIG_HYBRID_SHAPE    = 0x40;    // noise shape (hybrid mode only)
    public static final long CONFIG_FLOAT_DATA      = 0x80;    // ieee 32-bit floating point data
    public static final long CONFIG_FAST_FLAG       = 0x200;   // fast mode
    public static final long CONFIG_HIGH_FLAG       = 0x800;   // high quality mode
    public static final long CONFIG_VERY_HIGH_FLAG  = 0x1000;  // very high
    public static final long CONFIG_BITRATE_KBPS    = 0x2000;  // bitrate is kbps, not bits / sample
    public static final long CONFIG_AUTO_SHAPING    = 0x4000;  // automatic noise shaping
    public static final long CONFIG_SHAPE_OVERRIDE  = 0x8000;  // shaping mode specified
    public static final long CONFIG_JOINT_OVERRIDE  = 0x10000; // joint-stereo mode specified
    public static final long CONFIG_CREATE_EXE      = 0x40000; // create executable
    public static final long CONFIG_CREATE_WVC      = 0x80000; // create correction file
    public static final long CONFIG_OPTIMIZE_WVC    = 0x100000; // maximize bybrid compression
    public static final long CONFIG_CALC_NOISE      = 0x800000; // calc noise in hybrid mode
    public static final long CONFIG_LOSSY_MODE      = 0x1000000; // obsolete (for information)
    public static final long CONFIG_EXTRA_MODE      = 0x2000000; // extra processing mode
    public static final long CONFIG_SKIP_WVX        = 0x4000000; // no wvx stream w/ floats & big ints
    public static final long CONFIG_MD5_CHECKSUM    = 0x8000000; // compute & store MD5 signature
    public static final long CONFIG_OPTIMIZE_MONO   = 0x80000000; // optimize for mono streams posing as stereo

    public static final int MODE_WVC        = 0x1;
    public static final int MODE_LOSSLESS   = 0x2;
    public static final int MODE_HYBRID     = 0x4;
    public static final int MODE_FLOAT      = 0x8;
    public static final int MODE_VALID_TAG  = 0x10;
    public static final int MODE_HIGH       = 0x20;
    public static final int MODE_FAST       = 0x40;

    public static final int BIT_BUFFER_SIZE = 65536; // This should be carefully chosen for the
    // application and platform. Larger buffers are
    // somewhat more efficient, but the code will
    // allow smaller buffers and simply terminate
    // blocks early. If the hybrid lossless mode
    // (2 file) is not needed then the wvc_buffer
    // can be made very small.
    // or-values for "flags"
    public static final int INPUT_SAMPLES = 65536;
    public static final short CUR_STREAM_VERS = 0x405; // stream version we are writing now

    public static final int HARD_ERROR = 2;
    public static final int IGNORED_FLAGS = 0x18000000; // reserved, but ignore if encountered
    public static final int NEW_SHAPING = 0x20000000; // use IIR filter for negative shaping
    public static final int NO_ERROR = 0;

    public static final int SOFT_ERROR = 1;
    public static final int UNKNOWN_FLAGS = 0x80000000; // also reserved, but refuse decode if
    public static final int WAVPACK_HEADER_SIZE = 32;

}