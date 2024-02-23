package net.cliff3.maven.common.util

import org.apache.commons.io.FilenameUtils
import java.io.File
import java.util.*

/**
 * 디렉토리 및 경로 관련 함수 모음
 *
 * @author JoonHo Son
 * @since 0.3.0
 */

enum class DirPathPolicy {
    /**
     * yyyymmdd 형태
     */
    DATE_POLICY_YYYY_MM_DD,

    /**
     * yyyymm 형태
     */
    DATE_POLICY_YYYY_MM,

    /**
     * yyyy 형태
     */
    DATE_POLICY_YYYY
}

/**
 * 인자로 전달되는 [DirPathPolicy]를 기준으로 디렉토리 문자열을 생성 후 반환
 * 두 번째 인자(`split`)이 `true`일 경우 각 하위 경로 표시 방법은 [File.separator]로 처리하며
 * 반대의 경우 단순 문자열 형태로 반환한다
 *
 *
 * * [DirPathPolicy.DATE_POLICY_YYYY] -> 2024
 * * [DirPathPolicy.DATE_POLICY_YYYY_MM] -> 2024(구분자)01. 예)2024/01 혹은 202401
 * * [DirPathPolicy.DATE_POLICY_YYYY_MM_DD] -> 2024(구분자)01(구분자)25. 예)2024/01/25 혹은 20240125
 *
 * @param policy [DirPathPolicy] 참고
 * @return 경로 구분자를 포함하는 디렉토리명
 * @see DirPathPolicy
 */
fun generateDirNameByDatePolicy(policy: DirPathPolicy, split: Boolean = true): String {
    val date = Calendar.getInstance()
    val buffer = StringBuilder()

    buffer.append(date.get(Calendar.YEAR))

    if (policy.ordinal <= DirPathPolicy.DATE_POLICY_YYYY_MM.ordinal) {
        if (!split) {
            buffer.append((date.get(Calendar.MONTH) + 1).toString().padStart(2, '0'))
        } else {
            buffer.append(File.separator)
                .append((date.get(Calendar.MONTH) + 1).toString().padStart(2, '0'))
        }
    }

    if (policy.ordinal <= DirPathPolicy.DATE_POLICY_YYYY_MM_DD.ordinal) {
        if (!split) {
            buffer.append(date.get(Calendar.DATE).toString().padStart(2, '0'))
        } else {
            buffer.append(File.separator)
                .append(date.get(Calendar.DATE).toString().padStart(2, '0'))
        }
    }

    return "$buffer"
}

/**
 * 대상 파일을 이용하여 유일한 파일로 생성. 실제 파일이 생성되지 않는다.
 *
 * @param file 대상 파일
 *
 * @return 신규 파일
 */
fun getUniqueFile(file: File): File {
    if (!file.exists()) {
        return file
    }

    var tempFile = File(file.absolutePath)
    val parentDir = tempFile.parentFile
    val extension = FilenameUtils.getExtension(tempFile.name)
    val basename = FilenameUtils.getBaseName(tempFile.name)
    var count = 1

    do {
        tempFile = File(parentDir, basename + "_" + count++ + "_." + extension)

    } while (tempFile.exists())

    return tempFile
}