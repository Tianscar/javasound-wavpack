package com.beatofthedrum.wvcodec.test;

import com.beatofthedrum.wvcodec.cli.WvEncode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CLITest {

    @Test
    @DisplayName("wav -> wavpack")
    public void encode() {
        WvEncode.main(new String[] {"src/test/resources/fbodemo1.wav", "fbodemo1.wv"});
    }

    @Test
    @DisplayName("wav -> wavpack, with correction file")
    public void encodeWithCorrectionFile() {
        WvEncode.main(new String[] {"-b8", "-cc", "src/test/resources/fbodemo1.wav", "fbodemo1.wv", "fbodemo1.wvc"});
    }

}
