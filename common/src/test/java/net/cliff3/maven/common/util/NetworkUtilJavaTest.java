package net.cliff3.maven.common.util;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * NetworkUtilJavaTest
 *
 * @author JoonHo Son
 * @version 1.0.0 2019-09-07 최초 작성
 * @since 1.0.0
 */
public class NetworkUtilJavaTest {
    @Test
    public void ipCheckTest() {
        final String validIp = "192.168.2.5";
        final String wrongIp = "128.278.33.5";

        Assert.assertTrue(NetworkUtil.checkValidIpV4Address(validIp), String.format("유효성 검사 실패 [%s]", validIp));
    }
}
