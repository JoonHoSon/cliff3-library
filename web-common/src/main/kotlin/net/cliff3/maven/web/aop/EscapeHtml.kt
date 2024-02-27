package net.cliff3.maven.web.aop

/**
 * 태그에 대한 주석
 */
val COMMENT_TAG: String = "<!-- Not Allowed Tag Filtered -->"

/**
 * 속성에 대한 주석
 */
val COMMENT_ATTRIBUTE: String = "<!-- Not Allowed Attribute Filtered -->"

/**
 * Escape HTML 처리용 annotation
 *
 * @author JoonHo Son
 * @since 0.2.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.TYPE, AnnotationTarget.FIELD)
annotation class EscapeHtml(val required: Boolean = true, val removeCommentTag: Boolean = true)
