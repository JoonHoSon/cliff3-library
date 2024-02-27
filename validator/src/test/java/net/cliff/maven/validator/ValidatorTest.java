package net.cliff.maven.validator;

import static org.junit.jupiter.api.Assertions.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import net.cliff3.maven.validator.AtLeastCheck;
import net.cliff3.maven.validator.CascadeNotEmpty;
import net.cliff3.maven.validator.CellularCheck;
import net.cliff3.maven.validator.CompareValue;
import net.cliff3.maven.validator.Insert;
import net.cliff3.maven.validator.Update;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * ValidatorTest
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class ValidatorTest {
    @Autowired
    private Validator validator;

    /**
     * {@link AtLeastCheck} test
     */
    @Test
    @Order(1)
    @DisplayName("최소 입력값 유효성 검사 테스트")
    public void atLeastTest() {
        // value1, value2, value3 모두 미입력
        SampleUserAtLeast user = new SampleUserAtLeast();
        Set<ConstraintViolation<Object>> violations = validator.validate(user, Insert.class);

        assertEquals(violations.toArray().length, 1, "최소 입력값 체크 실패(입력 안함)");

        user = new SampleUserAtLeast();

        // 1개의 값 입력
        user.setValue2("test");

        violations = validator.validate(user, Insert.class);

        assertEquals(violations.toArray().length, 0, "최소 입력값 체크 실패(한개 입력)");
    }

    /**
     * {@link CompareValue} test
     */
    @Test
    @Order(2)
    @DisplayName("비교 유효성 검사 테스트")
    public void compareValueTest() {
        SampleUserCompareValue user = new SampleUserCompareValue();

        user.setPassword("1111");
        user.setConfirmPassword("2222");

        Set<ConstraintViolation<Object>> violations = validator.validate(user, Insert.class);

        assertEquals(violations.toArray().length, 1, "비밀번호 비교 테스트 실패(불일치)");

        user.setConfirmPassword("1111");

        violations = validator.validate(user, Insert.class);

        assertEquals(violations.toArray().length, 0, "비밀번호 비교 테스트 실패(일치)");
    }

    /**
     * {@link CascadeNotEmpty} test
     */
    @Test
    @Order(3)
    @DisplayName("하위 인스턴스 유효성 검사 테스트")
    public void cascadeNotEmptyTest() {
        // field null
        SampleParent parent = new SampleParent();
        Set<ConstraintViolation<Object>> violations = validator.validate(parent, Update.class);

        assertEquals(violations.toArray().length, 1, "하위 객체 필드 not empty check 실패(null)");

        // child field null
        SampleUserAtLeast child = new SampleUserAtLeast();

        parent.setUser(child);

        violations = validator.validate(parent, Update.class);

        assertEquals(violations.toArray().length, 1, "하위 객체 필드 not empty check 실패(child field null)");

        child.setValue2("살려줘");

        parent.setUser(child);

        violations = validator.validate(parent, Update.class);

        assertEquals(violations.toArray().length, 0, "하위 객체 필드 not empty check 실패(입력값 있음)");
    }

    /**
     * {@link CellularCheck} test
     */
    @Test
    @Order(4)
    @DisplayName("휴대전화번호 유효성 검사 테스트")
    public void cellularCheckTest() {
        SimplePojo pojo = new SimplePojo();
        Set<ConstraintViolation<Object>> violations = validator.validate(pojo, Insert.class);

        assertEquals(violations.toArray().length, 0, "휴대전화번호 테스트 실패(미입력. 필수입력 아님)");

        pojo.setCellularNumber("010-12-3456");

        violations = validator.validate(pojo, Insert.class);

        assertEquals(violations.toArray().length, 1, "휴대전화번호 테스트 실패(가운데 2자리)");

        pojo.setCellularNumber("012-123-4567");
        violations = validator.validate(pojo, Insert.class);

        assertEquals(violations.toArray().length, 1, "휴대전화번호 테스트 실패(012)");

        pojo.setCellularNumber("    ");
        violations = validator.validate(pojo, Insert.class);

        assertEquals(violations.toArray().length, 0, "휴대전화번호 테스트 실패(공백. 필수입력 아님)");

        violations = validator.validate(pojo, Update.class);

        assertEquals(violations.toArray().length, 1, "휴대전화번호 테스트 실패(미입력. 필수)");

        pojo.setRequiredNumber("  ");

        violations = validator.validate(pojo, Update.class);

        assertEquals(violations.toArray().length, 1, "휴대전화번호 테스트 실패(공백. 필수)");
    }

    @Test
    @Order(5)
    @DisplayName("이메일 유효성 검사 테스트")
    public void emailCheckTest() {
        SimpleEmail email = new SimpleEmail();

        // email2(not required) 확인
        Set<ConstraintViolation<Object>> violations = validator.validate(email, Update.class);

        assertEquals(violations.toArray().length, 0, "필수가 아닌 입력값에 대한 테스트 실패");

        violations = validator.validate(email, Insert.class); // reuqired test

        assertEquals(violations.toArray().length, 1, "필수 입력값에 대한 테스트 실패");

        email.setEmail1("wrong_email@test");

        violations = validator.validate(email, Insert.class);

        assertEquals(violations.toArray().length, 1, String.format("잘못된 이메일 주소 테스트 실패(%s)", email.getEmail1()));

        email.setEmail1("valid-email@apple.com");

        violations = validator.validate(email, Insert.class);

        assertEquals(violations.toArray().length, 0, String.format("유효한 이메일 주소 테스트 실패(%s)", email.getEmail1()));
    }

    @Test
    @Order(6)
    @DisplayName("URL 유효성 검사 테스트")
    public void urlCheckTest() {
        SampleUrl url = new SampleUrl();
        Set<ConstraintViolation<Object>> violations = validator.validate(url, Insert.class);

        // url1, required
        assertEquals(violations.toArray().length, 1, "필수 입력값 테스트 실패(url1)");

        // url1, wrong pattern
        url.setUrl1("http://with_underscore.dmain.com");

        violations = validator.validate(url, Insert.class);

        assertEquals(violations.toArray().length, 1, String.format("잘못된 URL 주소 테스트 실패(%s)", url.getUrl1()));

        // url1. valid pattern
        url.setUrl1("http://test@1111.daum.net");

        violations = validator.validate(url, Insert.class);

        assertEquals(violations.toArray().length, 0, String.format("유효한 URL 주소 테스트 실패(%s)", url.getUrl1()));

        // url2, blank string
        url.setUrl2("    ");

        violations = validator.validate(url, Update.class);

        assertEquals(violations.toArray().length, 1, "빈 문자열 형식 URL 주소 테스트 실패");

        // url1, url2 valid pattern
        url.setUrl2(
            "https://www.google.com/search?q=jsr-303+java&client=safari&rls=en&sxsrf=ALeKk038NKP01RI1K3pfl08o4KhNGVh03g:1620368991762&source=lnt&tbs=li:1&sa=X&ved=2ahUKEwjhu6Lx-LbwAhWrGKYKHSGIAQQQpwV6BAgBEDA&biw=1680&bih=917");

        violations = validator.validate(url, Insert.class, Update.class);

        assertEquals(violations.toArray().length, 0, "URL 유효성 검사 실패");
    }
}
