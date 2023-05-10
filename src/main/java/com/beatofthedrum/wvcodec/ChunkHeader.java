package com.beatofthedrum.wvcodec;

/*
** ChunkHeader.java
**
** Copyright (c) 2008 - 2009 Peter McQuillan
**
** All Rights Reserved.
**
** Distributed under the BSD Software License (see license.txt)
*/
class ChunkHeader
{
    char[] ckID = new char[4];
    long ckSize; // was uint32_t in C
}
