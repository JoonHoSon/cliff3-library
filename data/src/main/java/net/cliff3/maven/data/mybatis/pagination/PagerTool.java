package net.cliff3.maven.data.mybatis.pagination;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import net.cliff3.maven.data.mybatis.AbstractPageable;

/**
 * 페이징 처리
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
@Slf4j
public class PagerTool {
    int totalPageLinkCnt = -1;

    int curPageScope = -1;

    int startPage = -1;

    int endPage = -1;

    /**
     * 현재 페이지 번호를 반환.
     *
     * @return 페이지 번호
     */
    public int getCurrentPage() {
        return Pagination.currentPage.get();
    }

    /**
     * 한 페이지당 데이타 개수를 반환.
     *
     * @return 데이터 개수
     */
    public int getDataPerPage() {
        return Pagination.dataPerPage.get();
    }

    /**
     * 총 데이터 개수 반환.
     * <p>
     * 해당 조건의 총 데이터 개수를 반환한다.
     * </p>
     *
     * @return 데이터 개수
     */
    public int getTotalCnt() {
        int totalPage = 0;
        try {
            totalPage = Pagination.totalCount.get();
        } catch (Exception ignore) {
        }

        return totalPage;
    }

    /**
     * 한 페이지당 페이지 링크 개수 반환.
     *
     * @return 페이지 링크 개수
     */
    public int getPageLinkCnt() {
        return Pagination.linkPerPage.get();
    }

    public int getPrevPage() {
        int prevPage = 1;
        int currentPage = getCurrentPage();
        int pageLinkCnt = getPageLinkCnt();
        if (currentPage > pageLinkCnt) {
            prevPage = ((currentPage - 1) / pageLinkCnt) * pageLinkCnt;
        }
        return prevPage;
    }

    public int getNextPage() {
        int nextPage = getTotalPageLinkCnt();
        int currentPage = getCurrentPage();
        int pageLinkCnt = getPageLinkCnt();
        if (getTotalPageLinkCnt() > getEndPage()) {
            nextPage = (((currentPage - 1) / pageLinkCnt) + 1) * pageLinkCnt + 1;
        }
        return nextPage;
    }

    /**
     * 총 페이지 링크 개수
     *
     * @return 페이지 링크 개수
     */
    public int getTotalPageLinkCnt() {
        if (totalPageLinkCnt == -1) {
            totalPageLinkCnt = (getTotalCnt() - 1) / getDataPerPage() + 1;
        }
        return totalPageLinkCnt;
    }

    public int getCurrentPageScope() {
        if (curPageScope == -1) {
            curPageScope = (getCurrentPage() - 1) / getPageLinkCnt() + 1;
        }
        return curPageScope;
    }

    /**
     * 현재페이지 리스트의 시작 페이지 번호
     *
     * @return 페이지 번호
     */
    public int getStartPage() {
        if (startPage == -1) {
            startPage = (getCurrentPageScope() - 1) * getPageLinkCnt() + 1;
        }
        return startPage;
    }

    /**
     * 현재페이지 리스트의 끝 페이지 번호
     *
     * @return 페이지 번호
     */
    public int getEndPage() {
        log.debug("PagerTool.getEndPage >>>>>>>>>>>>>");

        if (endPage == -1) {
            log.debug("getCurrentPageScope() : {}", getCurrentPageScope());
            log.debug("getPageLinkCnt : {}", getPageLinkCnt());

            endPage = getCurrentPageScope() * getPageLinkCnt();

            log.debug("endPage : {}", endPage);

            if (endPage > getTotalPageLinkCnt()) {
                endPage = getTotalPageLinkCnt();
            }
        }
        return endPage;
    }

    /**
     * 현재 페이지의 페이지 번호의 리스트
     *
     * @return 번호 목록
     */
    public List<Integer> getPageList() {
        List<Integer> list = new ArrayList<Integer>();
        for (int idx = getStartPage(); idx <= getEndPage(); idx++) {
            list.add(idx);
        }
        return list;
    }

    /**
     * 파라미터로 부터 query string 을 추출한다.
     *
     * @return Query string
     */
    public String getQueryString() {
        return Pagination.queryString.get();
    }

    /**
     * 페이징 할때 필요한 queryString 을 반환
     * <pre>
     * {@code
     * ex) currentPage=3&dataPerPage=10
     * }
     * </pre>
     *
     * @return Query string
     */
    public String getPageQuery() {
        StringBuffer sb = new StringBuffer();
        sb.append(AbstractPageable.NG_CURRENT_PAGE_KEY).append("=").append(getCurrentPage());
        sb.append("&amp;").append(AbstractPageable.NG_DATA_PER_PAGE_KEY).append("=").append(getDataPerPage());
        return sb.toString();
    }

    /**
     * 이전 페이지(referer) 반환
     *
     * @return 이전 페이지
     */
    public String getReferer() {
        return Pagination.referer.get();
    }
}
