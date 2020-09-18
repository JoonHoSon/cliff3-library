package net.cliff3.maven.security.dao;

import net.cliff3.maven.security.model.DefaultUserLog;

/**
 * 사용자 접속 로그 처리
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
public interface UserLogDao {
    /**
     * 접속 로그 저장
     *
     * @param parameter 로그 정보
     */
    public void insertLog(Class<? extends DefaultUserLog> parameter);
}
