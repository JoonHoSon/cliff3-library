package net.cliff3.maven.web.aop

import com.nhncorp.lucy.security.xss.XssFilter
import net.cliff3.maven.common.topLogger
import org.apache.commons.lang3.StringUtils
import org.aspectj.lang.JoinPoint
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

const val DEFAULT_LUCY_CONFIG: String = "lucy-xss-cliff3-default.xml"

/**
 * [EscapeHtml] annotation을 이용하여 허용된 HTML tag 이외의 문자열을 escape 처리한다.
 *
 * @author JoonHo son
 * @since 0.2.0
 * @see [EscapeHtml]
 */
class EscapeHtmlBeforeAdvice {
    /**
     * Filter 설정 기본값
     */
    var configFileName: String = DEFAULT_LUCY_CONFIG

    /**
     * [XssFilter]
     */
    var filter: XssFilter? = null

    /**
     * [XssFilter]를 이용하여 escape 처리
     *
     * @param point [JoinPoint]
     *
     * @throws [NoSuchMethodException] 대상 필드의 `getter/setter` 메서드가 존재하지 않을 경우 발생
     * @throws [InvocationTargetException] 대상 필드의 'getter/setter method invoke` 실패
     * @throws [IllegalAccessException] 대상 필드의 'getter/setter method invoke` 실패(접근 오류)
     */
    @Throws(NoSuchMethodException::class, InvocationTargetException::class, IllegalAccessException::class)
    fun processHtmlEscape(point: JoinPoint) {
        checkFilter()

        topLogger.debug("------------------------------------------------------------------------")
        topLogger.debug("Start escape html aspect!")
        topLogger.debug("{}", point.signature)

        val parameters: Array<Any>? = point.args

        parameters?.let {
            it.forEach { p ->
                doEscape(p)
            }
        }
    }

    /**
     * 대상 인스턴스의 필드를 추출하여 [doEscapeField] 호출
     *
     * @param parameter [EscapeHtml] 필드를 포함하는 인스턴스
     *
     * @throws [NoSuchMethodException] 대상 필드의 `getter/setter` 메서드가 존재하지 않을 경우 발생
     * @throws [InvocationTargetException] 대상 필드의 'getter/setter method invoke` 실패
     * @throws [IllegalAccessException] 대상 필드의 'getter/setter method invoke` 실패(접근 오류)
     */
    @Throws(NoSuchMethodException::class, InvocationTargetException::class, IllegalAccessException::class)
    private fun doEscape(parameter: Any) {

        parameter.javaClass.`package`?.let {
            val fields: Array<out Field> = parameter.javaClass.declaredFields ?: return
            val fieldList: MutableList<Field> = fields.toMutableList()
            var current: Class<Any> = parameter.javaClass

            while (current.superclass != null) {
                current = current.superclass
                val superClassFields: Array<Field> = current.declaredFields

                fieldList.addAll(superClassFields.toMutableList())
            }

            fieldList.forEach { doEscapeField(it, parameter) }
        }
    }

    /**
     * 인자로 전달된 [Field]가 [EscapeHtml] annotation을포함할 경우 escape 처리
     *
     * @param field 대상 필드
     * @param parameter 대상 인스턴스
     *
     * @throws [NoSuchMethodException] 대상 필드의 `getter/setter` 메서드가 존재하지 않을 경우 발생
     * @throws [InvocationTargetException] 대상 필드의 'getter/setter method invoke` 실패
     * @throws [IllegalAccessException] 대상 필드의 'getter/setter method invoke` 실패(접근 오류)
     */
    @Throws(NoSuchMethodException::class, InvocationTargetException::class, IllegalAccessException::class)
    private fun doEscapeField(field: Field, parameter: Any) {
        val annotation: EscapeHtml? = field.getAnnotation(EscapeHtml::class.java)

        if (annotation != null) {
            topLogger.debug("***")
            topLogger.debug("field name : {}", field.name)
            topLogger.debug("field type : {}", field.type)

            if (String::class.java == field.type) {
                // 해당 필드의 유형이 문자열(String)일 경우에만 처리
                val getMethodName = "get" + field.name.substring(0, 1).uppercase() + field.name.substring(1)
                val setMethodName = "set" + field.name.substring(0, 1).uppercase() + field.name.substring(1)
                field.isAccessible = true

                // getter/setter method 존재하지 않을 경우 NoSuchMethodException 발생
                val getMethod: Method = parameter.javaClass.getMethod(getMethodName)
                val setMethod: Method = parameter.javaClass.getMethod(setMethodName)

                getMethod.invoke(parameter)?.let { m ->
                    {
                        topLogger.debug("before field value : {}", m)

                        var replaceString = filter!!.doFilter(m as String?)

                        // lucy filter에서 생성한 주석 제거 확인
                        if (annotation.removeCommentTag) {
                            replaceString = StringUtils.replace(
                                StringUtils.replace(replaceString, COMMENT_TAG, ""),
                                COMMENT_ATTRIBUTE, ""
                            )
                        }

                        setMethod.invoke(parameter, replaceString)

                        topLogger.debug("after field value : {}", replaceString)
                    }
                }
            } else if (Array::class == field.type) {
                // 해당 필드의 유형이 배열일 경우
                field.isAccessible = true

                field.get(parameter)?.let { p ->
                    (p as Array<*>).forEach { pp ->
                        run {
                            if (pp != null) {
                                doEscape(pp)
                            }
                        }
                    }
                }
            } else if (List::class.java.isAssignableFrom(field.type)) {
                field.isAccessible = true

                field.get(parameter)?.let { p ->
                    (p as List<*>).forEach { pp ->
                        run {
                            if (pp != null) {
                                doEscape(pp)
                            }
                        }
                    }

                }
            }
        }
    }

    private fun checkFilter() {
        if (filter != null) {
            return
        }

        filter = if (StringUtils.isNotBlank(configFileName)) {
            XssFilter.getInstance(configFileName)
        } else {
            XssFilter.getInstance(DEFAULT_LUCY_CONFIG, true)
        }
    }
}