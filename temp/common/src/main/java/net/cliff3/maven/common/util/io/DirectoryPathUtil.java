package net.cliff3.maven.common.util.io;

import java.io.File;
import java.util.Calendar;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 디렉토리 및 경로 관련 유틸리티
 *
 * @author JoonHo Son
 * @since 0.2.0
 */
public class DirectoryPathUtil {
    public enum DIR_DATE_TYPE {DATE_POLICY_YYYY_MM_DD, DATE_POLICY_YYYY_MM, DATE_POLICY_YYYY}

    /**
     * 디렉토리명 생성
     *
     * @param policy 출력 규칙
     *
     * @return 디렉토리명
     */
    public static String getDirectoryPathByDateType(DIR_DATE_TYPE policy) {

        Calendar calendar = Calendar.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(calendar.get(Calendar.YEAR)).append(File.separator);
        if (policy.ordinal() <= DIR_DATE_TYPE.DATE_POLICY_YYYY_MM.ordinal()) {
            sb.append(
                StringUtils.leftPad(String.valueOf(calendar
                                                       .get(Calendar.MONTH) + 1), 2, '0')).append(
                File.separator);
        }

        if (policy.ordinal() <= DIR_DATE_TYPE.DATE_POLICY_YYYY_MM_DD.ordinal()) {
            sb.append(
                StringUtils.leftPad(String.valueOf(calendar
                                                       .get(Calendar.DATE)), 2, '0')).append(
                File.separator);
        }

        return sb.toString();
    }

    /**
     * 대상 파일을 이용하여 유일한 파일로 생성
     *
     * @param file 대상 파일
     *
     * @return 신규 파일
     */
    public static File getUniqueFile(final File file) {
        if (!file.exists()) {
            return file;
        }

        File tmpFile = new File(file.getAbsolutePath());
        File parentDir = tmpFile.getParentFile();
        int count = 1;
        String extension = FilenameUtils.getExtension(tmpFile.getName());
        String baseName = FilenameUtils.getBaseName(tmpFile.getName());

        do {
            tmpFile = new File(parentDir, baseName + "_" + count++ + "_."
                + extension);
        } while (tmpFile.exists());

        return tmpFile;
    }
}
