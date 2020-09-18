package net.cliff3.maven.data.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.springframework.web.filter.OncePerRequestFilter;

import lombok.Setter;
import net.cliff3.maven.data.mybatis.PagerTool;
import net.cliff3.maven.data.mybatis.Pagination;
import org.apache.commons.lang3.StringUtils;

/**
 * 페이징 처리 정보를 {@link HttpServletRequest}에 저장하는 필터
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
public class DefaultPagingFilter extends OncePerRequestFilter {
    /**
     * 현재 페이지번호 변수명
     */
    @Setter
    private String currentPage = "currentPage";

    /**
     * 페이지당 데이터 출력 건수
     */
    private String DATA_PER_PAGE = "dataPerPage";

    /**
     *
     */
    private String LINK_PER_PAGE = "linkPerPage";

    private Integer DEFAULT_DATA_PER_PAGE = 10;

    private Integer DEFAULT_PAGE_LINK_COUNT = 10;

    @Setter
    private String encoding = "UTF-8";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String contentType = request.getContentType();

        Pagination.resetAll();

        if (contentType == null || !contentType.contains("multipart")) {
            // contentType 이 파일 첨부라면 스킵한다.
            String currentPage = request.getParameter(this.currentPage);
            String dataPerPage = request.getParameter(DATA_PER_PAGE);
            String linkPerPage = request.getParameter(LINK_PER_PAGE);

            if (StringUtils.isEmpty(currentPage)) {
                Pagination.currentPage.set(1);
            } else {
                try {
                    Pagination.currentPage.set(new Integer(currentPage));
                } catch (Exception ignore) {
                    Pagination.currentPage.set(1);
                }
            }

            if (StringUtils.isEmpty(dataPerPage)) {
                Pagination.dataPerPage.set(DEFAULT_DATA_PER_PAGE);
            } else {
                try {
                    Pagination.dataPerPage.set(new Integer(dataPerPage));
                } catch (Exception ignore) {
                    Pagination.dataPerPage.set(DEFAULT_DATA_PER_PAGE);
                }
            }

            if (StringUtils.isEmpty(linkPerPage)) {
                Pagination.linkPerPage.set(DEFAULT_PAGE_LINK_COUNT);
            } else {
                try {
                    Pagination.linkPerPage.set(new Integer(linkPerPage));
                } catch (Exception ignore) {
                    Pagination.linkPerPage
                        .set(DEFAULT_PAGE_LINK_COUNT);
                }
            }

            Pagination.queryString.set(extractQueryString(request));
            request.setAttribute("pager", new PagerTool());
        }

        filterChain.doFilter(request, response);
    }

    private String extractQueryString(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        Map<String, String[]> parameterMap = request.getParameterMap();
        String[] values;

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            if (StringUtils.equals(currentPage, entry.getKey()) || StringUtils.equals(DATA_PER_PAGE, entry.getKey())) {
                continue;
            }

            values = entry.getValue();

            if (values == null || values.length == 0) {
                continue;
            }

            for (String value : values) {
                try {
                    if (StringUtils.isNotBlank(value)) {
                        if (StringUtils.isNotEmpty(builder.toString())) {
                            builder.append("&");
                        }

                        builder.append(entry.getKey()).append("=").append(URLEncoder.encode(value, encoding));
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        return builder.toString();
    }
}
