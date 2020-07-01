package net.cliff3.maven.common.util;

import static org.testng.Assert.*;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

/**
 * net.cliff3.maven.common.util.StringUtilTest
 *
 * @author JoonHo Son
 * @version 1.0.0 2020-06-26 최초 작성
 * @since 1.0.0
 */
@Slf4j
@Test(groups = "stringUtilTest")
public class StringUtilTest {
    @Test
    public void addThousandSeparatorTest() {
        final String target1 = null;
        final String target2 = "abc";
        final String target3 = "72300";
        final String target4 = "-123000";

        Optional<String> result = StringUtil.addThousandSeparator(target1);

        assertFalse(result.isPresent(), "검증 실패");

        result = StringUtil.addThousandSeparator(target2);

        assertFalse(result.isPresent(), "잘못된 숫자 사용 처리 실패");

        result = StringUtil.addThousandSeparator(target3);

        result.ifPresent(t -> assertEquals(t, "72,300", "세 자리 쉼표 처리 실패"));

        result = StringUtil.addThousandSeparator(target4);

        result.ifPresent(t -> assertEquals(t, "-123,000", "세 자리 쉼표(음수) 처리 실패"));
    }

    @Test
    public void stringToHexTest() {
        final String target1 = null;
        final String target2 = "sk489dhskdf";
        final String target3 = "이명박근혜out";

        Optional<String> result = StringUtil.stringToHex(target1, false);

        assertFalse(result.isPresent(), "검증 실패");

        result = StringUtil.stringToHex(target2, false);

        result.ifPresent(t -> {
            log.debug("16진수 변환 결과 : {}", t);
            assertEquals(t, "736b3438396468736b6466", "16진수 변환 실패");
        });

        result = StringUtil.stringToHex(target2, true);

        result.ifPresent(t -> {
            log.debug("16진수 변환 결과 : {}", t);
            assertEquals(t, "0x736b3438396468736b6466", "16진수 변환 실패");
        });

        result = StringUtil.stringToHex(target3, false);

        result.ifPresent(t -> assertEquals(t, "c774ba85bc15adfcd61c6f7574", "한글포함 16진수 변환 실패"));
    }

    @Test
    public void generateRandomStringTest() {
        Optional<String> result = StringUtil.generateRandomString(-1);

        assertFalse(result.isPresent(), "검증 실패");

        result = StringUtil.generateRandomString(15);

        result.ifPresent(t -> {
            log.debug("생성된 임의의 문자열 : {}", t);
            assertEquals(t.length(), 15, "임의의 문자열 생성 실패");
        });
    }

    @Test
    public void checkValueInfDefaultListTest() {
        final String[] defaultList1 = {"a", "B", "c"};
        final String targetValue1 = "b";
        final String defaultValue1 = "없음";

        Optional<String> result = StringUtil.checkValueInDefaultList(defaultList1, targetValue1, defaultValue1, false);

        // 대소문자 구분시
        result.ifPresent(t -> {
            log.debug("반환된 결과값(모든 인자 정상, 대소문자 구분) : {}", t);
            assertEquals(t, defaultValue1, "기본값 반환 실패");
        });

        result = StringUtil.checkValueInDefaultList(defaultList1, targetValue1, defaultValue1, true);

        // 대소문자 구분하지 않을 경우
        result.ifPresent(t -> {
            log.debug("반환된 결과값(모든 인자 정상, 대소문자 구분하지 않음) : {}", t);
            assertEquals(t, "B", "기본값 반환 실패");
        });

        result = StringUtil.checkValueInDefaultList(null, targetValue1, defaultValue1, false);

        // 기준 문자열 배열이 없을 경우 지정된 기본 값을 반환
        result.ifPresent(t -> {
            log.debug("반환된 결과값(기준 문자열 배열이 없을 경우) : {}", t);
            assertEquals(t, defaultValue1, "배열값이 null일 경우 기본값 반환 실패");
        });

        result = StringUtil.checkValueInDefaultList(defaultList1, null, defaultValue1, false);

        // 비교 문자열이 없을 경우
        result.ifPresent(t -> {
            log.debug("반환된 결과값(비교 문자열이 없을 경우) : {}", t);
            assertEquals(t, defaultValue1, "대상이 null일 경우 기본값 반환 실패");
        });

        result = StringUtil.checkValueInDefaultList(defaultList1, targetValue1, null, false);

        assertEquals(result, Optional.empty(), "기본값 미지정 테스트 실패");
    }

    @Test
    public void maskingTest() {
        final String target = "010-1234-5678";

        Optional<String> result = StringUtil.masking(target, 3, "#");

        result.ifPresent(t -> {
            log.debug("마스킹 결과 : {}", t);
            assertEquals(t, "010##########", "마스킹 처리 실패");
        });

        result = StringUtil.masking(null, 3, "#");

        assertEquals(result, Optional.empty(), "대상 미지정 처리 실패");

        result = StringUtil.masking(target, -1, "*");

        assertEquals(result, Optional.empty(), "마스킹 시작 인덱스 지정 오류 처리 실패");

        result = StringUtil.masking(target, 30, "#");

        assertEquals(result, Optional.empty(), "마스킹 시작 인덱스 범위 오류 처리 실패");

        result = StringUtil.masking(target, 4, 4, "*");

        result.ifPresent(t -> {
            log.debug("마스킹 결과 : {}", t);
            assertEquals(t, "010-****-5678", "부분 마스킹 처리 실패");
        });

        result = StringUtil.masking("", 4, 4, "*");

        assertEquals(result, Optional.empty(), "대상 문자열 검증 실패");

        result = StringUtil.masking(target, -1, 4, null);

        assertEquals(result, Optional.empty(), "시작위치 검증 실패");

        result = StringUtil.masking(target, 4, 200, "*");

        assertEquals(result, Optional.empty(), "전체 길이 검증 실패");
    }

    @Test
    public void extractInitialConsonantsTest() {
        final String target = "세종대왕(世宗大王)";
        Optional<String> result = StringUtil.extractInitialConsonants(target);

        result.ifPresent(t -> {
            log.debug("초성 추출 결과 : {}", t);
            assertEquals(t, "ㅅㅈㄷㅇ(世宗大王)", "외국어를 포함한 한글 초성 추출 실패");
        });

        result = StringUtil.extractInitialConsonants(null);

        assertEquals(result, Optional.empty(), "대상 문자열 검증 실패");
    }

    @Test
    public void separateKoreanConsonantVowelTest() {
        final String target = "한글자음모음분리많이";
        Optional<String> result = StringUtil.separateKoreanConsonantVowel(target);

        result.ifPresent(t -> {
            log.debug("초/중/종성 분리 결과 : {}", t);
            assertEquals(t, "ㅎㅏㄴㄱㅡㄹㅈㅏㅇㅡㅁㅁㅗㅇㅡㅁㅂㅜㄴㄹㅣㅁㅏㄶㅇㅣ");
        });

        result = StringUtil.separateKoreanConsonantVowelCompletely(target);

        result.ifPresent(t -> {
            log.debug("초/중/종성 분리 결과 : {}", t);
            assertEquals(t, "ㅎㅏㄴㄱㅡㄹㅈㅏㅇㅡㅁㅁㅗㅇㅡㅁㅂㅜㄴㄹㅣㅁㅏㄴㅎㅇㅣ");
        });
    }
}
