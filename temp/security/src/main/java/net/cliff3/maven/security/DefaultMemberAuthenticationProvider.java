package net.cliff3.maven.security;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.cliff3.maven.security.dao.UserLogDao;
import net.cliff3.maven.security.model.AbstractUser;
import net.cliff3.maven.security.model.Role;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 아이디/비밀번호 기반 인증 처리
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
@Slf4j
public class DefaultMemberAuthenticationProvider implements AuthenticationProvider, ApplicationContextAware {
    /**
     * 로그인 성공
     */
    public static final String RESULT_KEY_SUCCESS = "s";

    /**
     * 존재하지 않는 사용자
     */
    public static final String RESULT_KEY_FAIL_NOT_EXIST = "ne";

    /**
     * 비밀번호 오류
     */
    public static final String RESULT_KEY_FAIL_WRONG_PASSWORD = "wp";

    /**
     * 서버 오류
     */
    public static final String RESULT_KEY_SERVER_ERROR = "se";

    /**
     * ApplicationContext
     */
    private ApplicationContext context;

    /**
     * NGUserDetailManager를 구현한 사용자 관련 manager
     */
    @Setter
    private AbstractUserDetailService userDetailService;

    /**
     * 접속 기록 처리 DAO
     */
    @Setter
    private UserLogDao userLogDAO = null;

    /**
     * Password encoder
     */
    @Setter
    private PasswordEncoder passwordEncoder;

    /**
     * 비밀번호 암호화화 비교 여부
     */
    @Setter
    private Boolean useEncryptPassword = true;

    /**
     * 사용자 유형을 정의하는 필드명
     */
    @Setter
    private String userTypeFieldName;

    /**
     * 사용자 유형과 권한 코드 정보
     */
    @Setter
    private Map<String, Object> userTypeWithRoleCode;

    /**
     * 사용자가 존재하지 않을 경우 메세지
     */
    @Setter
    private String notFoundMessage = "계정이 존재하지 않거나, 비밀번호가 일치하지 않습니다.";

    /**
     * 비밀번호가 일치하지 않을 경우 메세지
     */
    @Setter
    private String invalidPasswordMessage = "계정이 존재하지 않거나, 비밀번호가 일치하지 않습니다.";

    /**
     * Constructor
     */
    public DefaultMemberAuthenticationProvider() {
        log.info("Create DefaultMemberAuthenticationProvider");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        initUserDetailManager();

        String userName = authentication.getName();
        String password = (String)authentication.getCredentials();

        if (StringUtils.isBlank(userName)) {
            throw new BadCredentialsException("아이디를 입력하여 주십시오.");
        }

        if (StringUtils.isBlank(password)) {
            throw new BadCredentialsException("비밀번호를 입력하여 주십시오.");
        }

        AbstractUser user = userDetailService.getUser(userName);

        log.debug("조회 대상 : {}", userName);

        UsernamePasswordAuthenticationToken token;

        try {

            log.debug("target user : {}", user);

            // 인증 처리
            password = checkUserAndPassword(password, user);

            Collection<? extends GrantedAuthority> authorities = null;

            if (StringUtils.isNotEmpty(userTypeFieldName) && MapUtils.isNotEmpty(userTypeWithRoleCode)) {
                log.debug("권한 지정 필드와 정보를 이용하여 권한 코드 처리 시작 >>>");

                // 지정된 필드가 실제 NGAbstractUser를 구현한 객체에 존재하는지 확인
                Field field = user.getClass().getDeclaredField(userTypeFieldName);

                // 지정된 필드에서 현재 사용자의 유형값을 추출한다.
                field.setAccessible(true);
                Object currentUserType = field.get(user);

                // Map 형태로 전달된 기준 권한 코드와 비교하여 권한정보를 설정한다.

                for (Object key : userTypeWithRoleCode.keySet()) {
                    if (currentUserType.equals(key)) {
                        // 유형이 일치하면 value에 저장된 권한 코드를 이용하여 authorities 처리
                        Set<Role> createdAuthorities = new HashSet<Role>();
                        Role role = new Role((String)userTypeWithRoleCode.get(key), true);

                        createdAuthorities.add(role);

                        authorities = createdAuthorities;
                    }
                }
            } else {
                authorities = user.getAuthorities();
            }


            if (log.isDebugEnabled()) {
                assert authorities != null;

                for (GrantedAuthority aaa : authorities) {
                    log.debug("role : {}", aaa.getAuthority());
                }
            }

            token = new UsernamePasswordAuthenticationToken(user, password, authorities);

            if (log.isDebugEnabled()) {
                for (GrantedAuthority bbb : token.getAuthorities()) {
                    log.debug("save role : {}", bbb.getAuthority());
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (NoSuchFieldException e) {
            log.error("사용자 구분 지정 필드가 존재하지 않습니다.", e);

            throw new RuntimeException();
        } catch (IllegalAccessException e) {
            log.error("사용자 구분 지정 필드에 접근할 수 없습니다.", e);

            throw new RuntimeException();
        } finally {
            if (userLogDAO != null) {
                // TODO: 접속 기록 저장
            }
        }

        return token;
    }

    /**
     * 기본적인 검증 처리
     * <p>
     * 입력한 아이디를 이용한 사용자 조회와, 비밀번호 비교 처리
     * </p>
     *
     * @param password 입력한 비밀번호
     * @param user     저장된 사용자 객체
     *
     * @return 인증이 통과된 사용자가 입력한 비빌번호
     */
    private String checkUserAndPassword(String password, AbstractUser user) {
        if (useEncryptPassword && StringUtils.isNotEmpty(password)) {
            password = getPasswordEncoder().encode(password);
            log.debug("Encrypted password : {}", password);
        }

        if (user == null) {
            log.debug("사용자가 존재하지 않음");

            throw new BadCredentialsException(notFoundMessage);
        } else {
            boolean isImplementation = AuthenticationCustomComparePassword.class.isAssignableFrom(
                userDetailService.getClass());

            log.debug("NGAuthenticationCustomComparePassword 구현 여부 : {}", isImplementation);

            if (isImplementation) {
                // 비밀번호를 조회하는 메서드를 지정할 경우
                log.debug("비교 메서드 시작");

                Boolean compareResult = ((AuthenticationCustomComparePassword)userDetailService).comparePassword(
                    password,
                    user.getPassword());

                log.debug("비교 메서드 실행 결과 : " + compareResult);

                if (!compareResult) {
                    throw new BadCredentialsException(invalidPasswordMessage);
                }
            } else {
                if (!password.equals(user.getPassword())) {
                    log.debug("비밀번호가 일치하지 않음");

                    throw new BadCredentialsException(invalidPasswordMessage);
                }
            }
        }
        return password;
    }

    /**
     * 이미 로그인된 사용자의 권한 정보 변경을 위한 메서드.
     * <p>
     * 로그인시 사용할 수 없음.
     * </p>
     *
     * @param authentication 로그인 처리가 된 사용자
     *
     * @return 새로운 권한 정보를 포함하는
     */
    public Authentication authenticateWithEncryptedPassword(Authentication authentication) {
        initUserDetailManager();

        // FIXME: 로직 개선할 것.
        String userName = null;
        String password = (String)authentication.getCredentials();

        if (UserDetails.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            userName = ((UserDetails)authentication.getPrincipal()).getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            userName = (String)authentication.getPrincipal();
        } else {
            // TODO: 예외처리
        }

        AbstractUser user = userDetailService.getUser(userName, password);

        if (user == null) {
            throw new BadCredentialsException("사용자 아이디와 비밀번호가 불일치 합니다.");
        }

        return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    /**
     * NGUserDetailService type의 user manager를 {@link #userDetailService}에 적용한다.
     * <p>
     * {@link #userDetailService}를 이용한 경우 context loading 시점에 적용
     * 되므로 상관 없으나, {@literal @Service} 등 annotation 방식으로 지정할 경우 등록된 bean을 조회하여 적용한다.
     * </p>
     *
     * @throws NullPointerException                                                        NGUserDetailManager 구현체 검색 실패
     * @throws org.springframework.beans.factory.support.BeanDefinitionValidationException NGUserDetailManager 구현체가 두 개 이상일 경우 발생
     */
    protected void initUserDetailManager() {
        if (userDetailService == null) {
            // detail manager가 지정되어있지 않을 경우 context에 등록된 bean을 검사하여 추출한다.
            String[] beanNames = context.getBeanNamesForType(AbstractUserDetailService.class);

            if (beanNames == null) {
                throw new NullPointerException("NGUserDetailManager를 구현한 bean이 없음");
            }

            if (beanNames.length > 1) {
                throw new BeanDefinitionValidationException("NGUserDetailManager를 구현한 bean이 두 개 이상임");
            }

            userDetailService = (AbstractUserDetailService)context.getBean(beanNames[0]);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public PasswordEncoder getPasswordEncoder() {
        if (passwordEncoder == null) {
            log.info("Return default password encoder");

            passwordEncoder = new DefaultPasswordEncoder();
        }

        return passwordEncoder;
    }
}
