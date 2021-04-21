package net.cliff.maven.validator;

import static org.testng.Assert.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import net.cliff3.maven.validator.AtLeastCheck;
import net.cliff3.maven.validator.CascadeNotEmpty;
import net.cliff3.maven.validator.CellularCheck;
import net.cliff3.maven.validator.CompareValue;
import net.cliff3.maven.validator.Insert;
import net.cliff3.maven.validator.Update;
import org.testng.annotations.Test;

/**
 * ValidatorTest
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
@Test(groups = "validatorTest")
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class ValidatorTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private Validator validator;

    /**
     * {@link AtLeastCheck} test
     */
    @Test
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
}
