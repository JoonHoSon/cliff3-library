package net.cliff3.maven.common.util;

import static org.testng.Assert.*;

import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * NumberUtilTest
 *
 * @author JoonHo Son
 * @since 1.0.0
 */
@Slf4j
public class NumberUtilTest {
    @Test
    public void getRandomIntegerTest() {
    	final int min = 11;
    	final int max = 30;
    	int result;

        for (int i = min; i <= max; i++) {
            result = NumberUtil.getRandomInteger(min, max);

            assertTrue(result >= min && result <= max, "임의의 정수 추출 실패");
        }
    }
}
