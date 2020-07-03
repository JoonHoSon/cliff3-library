package net.cliff3.maven.common.util;

import java.util.Random;

import lombok.extern.slf4j.Slf4j;

/**
 * 숫자 관련 유틸리티 클래스
 *
 * @author JoonHo Son
 * @since 0.2.0
 */
@Slf4j
public class NumberUtil {
    /**
     * 지정된 범위 내에서 임의의 정수형 숫자를 반환한다.
     *
     * @param min 범위 최소값
     * @param max 범위 최대값
     *
     * @return 생성된 임의의 정수
     */
    public static int getRandomInteger(int min, int max) {
        // seed를 System.currentTimeMillis()로 지정할 경우 loop에서 같은 timestamp일 경우 동일한 값이 출력될 수 있음
        // 따라서 seed를 지정하지 않고 처리하도록 수정

        Random random = new Random();
        int result = random.nextInt(max - min) + min + 1;

        log.debug("랜덤 추출 최소값 [{}], 최대값[{}] ==> {}", min, max, result);

        return result;
    }
}
