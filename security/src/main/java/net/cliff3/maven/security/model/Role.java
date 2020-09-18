package net.cliff3.maven.security.model;

import java.io.Serializable;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * 권한
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
public class Role implements GrantedAuthority, Serializable {
    /**
     * ROLE_ 이름
     */
    @Getter
    private String roleCode;

    /**
     * ROLE 설명
     */
    @Getter
    @Setter
    private String description;

    /**
     * 사용 여부
     */
    @Getter
    @Setter
    private Boolean use;

    /**
     * Constructor
     */
    public Role() {
    }

    /**
     * Constructor
     *
     * @param roleCode ROLE_ 이름
     */
    public Role(final String roleCode) {
        if (StringUtils.isNotEmpty(roleCode)) {
            this.roleCode = roleCode;
        }
    }

    /**
     * Constructor
     *
     * @param roleCode ROLE_ 이름
     * @param use      사용여부
     */
    public Role(final String roleCode, final Boolean use) {
        if (StringUtils.isNotEmpty(roleCode) && use != null) {
            this.roleCode = roleCode;
            this.use = use;
        }
    }

    /**
     * roleCode 설정. ROLE의 prefix인 <strong>ROLE_</strong>가 존재할 경우 제외한다.
     *
     * @param roleCode ROLE 이름
     */
    public void setRoleCode(String roleCode) {
        if (StringUtils.isEmpty(roleCode)) {
            throw new NullPointerException("ROLE 이름이 없습니다.");
        }

        String replaceName = roleCode.toLowerCase();

        if (replaceName.indexOf("role_") == 0) {
            this.roleCode = replaceName.toUpperCase();
        } else {
            this.roleCode = "ROLE_" + StringUtils.replace(replaceName, "role_", "");
        }
    }

    @Override
    public String getAuthority() {
        return this.getRoleCode();
    }
}
