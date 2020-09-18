package net.cliff3.maven.data.mybatis;

import java.util.Arrays;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * AbstractPageable
 *
 * @author JoonHo Son
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractPageable {
    /**
     * Paging 처리 여부를 지정하는 key
     */
    public static final String NG_SKIP_PAGING_KEY = "SKIP_PAGING";

    /**
     * 한 페이지당 출력 게시물 개수
     */
    public static final Integer NG_DEFAULT_DATA_PER_PAGE = 10;

    /**
     * 페이지 블럭 이동 개수
     */
    public static final Integer NG_DEFAULT_PAGE_LINK_COUNT = 10;

    /**
     * 한 페이지당 출력 게시물 개수를 지정하는 key
     */
    public static final String NG_DATA_PER_PAGE_KEY = "dataPerPage";

    /**
     * 현재 페이지를 지정하는 key
     */
    public static final String NG_CURRENT_PAGE_KEY = "currentPage";

    /**
     * 페이지 블럭 이동 개수를 지정하는 key
     */
    public static final String NG_LINK_PER_PAGE_KEY = "linkPerPage";

    /**
     * Paging 처리 여부를 확인
     *
     * @param annotation {@link Pageable}
     * @param args       목록 조회 메서드에 전달되는 인자 배열
     *
     * @return Paging 처리 여부
     */
    @SuppressWarnings("unchecked")
    protected boolean isPageable(Pageable annotation, Object[] args) {
        log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        log.debug("check pageable.");
        log.debug("annotation : {}", annotation);
        log.debug("args : {}", Arrays.toString(args));

        if (args == null || args.length == 0 || annotation == null) {
            log.debug("args is invalidate!");
            return false;
        }

        Map<String, Object> parameter = (Map<String, Object>)args[0];

        log.debug("contains SKIP_PAGING_KEY : {}", parameter.containsKey(NG_SKIP_PAGING_KEY));

        if (!parameter.containsKey(NG_SKIP_PAGING_KEY)) {
            parameter.put(NG_SKIP_PAGING_KEY, false);
        }

        return !(Boolean)parameter.get(NG_SKIP_PAGING_KEY);
    }
}
