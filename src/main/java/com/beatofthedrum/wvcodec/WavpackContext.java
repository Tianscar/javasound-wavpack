/*
** WavpackContext.java
**
** Copyright (c) 2007 - 2013 Peter McQuillan
**
** All Rights Reserved.
**                       
** Distributed under the BSD Software License (see license.txt)  
**
*/

package com.beatofthedrum.wvcodec;

public class WavpackContext {
    public WavpackConfig config = new WavpackConfig();
    public WavpackStream stream = new WavpackStream();

    public byte read_buffer[] = new byte[1024];	// was uchar in C
    //public String error_message = "";
    public Exception error_message = null;
    public boolean error;

    public java.io.DataInputStream infile;
    public long total_samples, crc_errors, first_flags;		// was uint32_t in C
    public int open_flags, norm_offset;
    public int reduced_channels = 0;
    public int lossy_blocks;
    public int status = 0;	// 0 ok, 1 error

    public java.io.FileOutputStream outfile;
    public java.io.FileOutputStream correction_outfile;
    public int wvc_flag;
    public long block_samples;
    public long acc_samples;
    public long filelen;
    public long file2len;
    public short stream_version;
    public int byte_idx = 0; // holds the current buffer position for the input WAV data
}