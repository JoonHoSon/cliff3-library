package net.cliff3.maven.security.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자 DTO. Spring security 연동시 추상 메서드를 구현하여 사용한다.
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
@Slf4j
@ToString
public abstract class AbstractUser implements UserDetails, Serializable {
    /**
     * 인증 만료 여부
     */
    private boolean credentialsExpired = false;

    /**
     * 계정 만료 여부
     */
    private boolean accountExpired = false;

    /**
     * 계정 잠김 여부
     */
    private boolean accountLocked = false;

    /**
     * 계정 사용 가능 여부
     */
    private boolean enabled = false;

    /**
     * 권한 목록
     *
     * @see Role
     */
    @Setter
    private Set<Role> authorities = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * 사용자 권한 추가 지정
     *
     * @param authority 권한 정보
     *
     * @see Role
     */
    public void addAuthority(Role authority) {
        if (authorities == null) {
            authorities = new HashSet<>();
        }

        authorities.add(authority);
    }

    /**
     * 해당 사용자가 특정 권한이 있는지 확인한다.
     *
     * @param authority 확인하고자 하는 권한
     *
     * @return 해당 권한의 소유 여부
     * @see Role
     */
    public Boolean hasAuthority(Role authority) {
        for (Role currentAuthority : authorities) {
            if (authority == currentAuthority) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !accountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    /**
     * 사용자의 일련번호 등의 키 값을 반환
     *
     * @return 키
     */
    public abstract Object getUserKey();

    /**
     * 사용자의 일련번호 등의 키 값을 지정
     *
     * @param key 키
     */
    public abstract void setUserKey(Object key);
}
