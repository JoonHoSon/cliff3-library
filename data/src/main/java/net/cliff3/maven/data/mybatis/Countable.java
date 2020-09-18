package net.cliff3.maven.data.mybatis;

/**
 * 데이터의 노출 순서 처리를 위한 interface
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
public interface Countable {
    /**
     * 출력 번호 반환
     *
     * @return 출력 번호
     */
    int getPositionIdx();

    /**
     * 출력 번호 할당
     *
     * @param positionIdx 출력 번호
     */
    void setPositionIdx(int positionIdx);
}
