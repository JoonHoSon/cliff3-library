package net.cliff3.maven.data.mybatis;

/**
 * 페이징 처리 정보를 제공하는 {@link ThreadLocal} 인스턴스
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
public class Pagination {
    /**
     * 현재 페이지 번호
     */
    public static ThreadLocal<Integer> currentPage = new ThreadLocal<>();

    /**
     * 페이지당 출력 게시물 개수
     */
    public static ThreadLocal<Integer> dataPerPage = new ThreadLocal<>();

    /**
     * 페이지 출력 개수
     */
    public static ThreadLocal<Integer> linkPerPage = new ThreadLocal<>();

    /**
     * 전체 게시물 개수
     */
    public static ThreadLocal<Integer> totalCount = new ThreadLocal<>();

    /**
     * Query string
     */
    public static ThreadLocal<String> queryString = new ThreadLocal<>();

    /**
     * 이전 페이지
     */
    public static ThreadLocal<String> referer = new ThreadLocal<>();

    public static void resetAll() {
        Pagination.currentPage.set(null);
        Pagination.dataPerPage.set(null);
        Pagination.linkPerPage.set(null);
        Pagination.totalCount.set(null);
        Pagination.queryString.set(null);
        Pagination.referer.set(null);
    }
}
