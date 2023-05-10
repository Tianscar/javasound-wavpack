/*
** WavpackConfig.java
**
** Copyright (c) 2007 - 2013 Peter McQuillan
**
** All Rights Reserved.
**                       
** Distributed under the BSD Software License (see license.txt)  
**
*/

package com.beatofthedrum.wvcodec;

public class WavpackConfig
{
    public int bits_per_sample, bytes_per_sample;
    public int num_channels, float_norm_exp;
    public long flags, sample_rate, channel_mask;	// was uint32_t in C

    public int bitrate;
    public int shaping_weight;
    public int block_samples;
}