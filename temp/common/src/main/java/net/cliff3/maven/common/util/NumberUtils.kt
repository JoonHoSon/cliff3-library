package net.cliff3.maven.common.util

import java.util.*

/**
 * 지정된 범위 내에서 임의의 정수형 숫자를 반환한다.
 *
 * @param min 범위 최소값
 * @param max 범위 최대값
 *
 * @return 생성된 임의의 정수
 */
fun getRandomInteger(min: Int, max: Int): Int {
    val random = Random()

    return random.nextInt(max - min) + min + 1
}