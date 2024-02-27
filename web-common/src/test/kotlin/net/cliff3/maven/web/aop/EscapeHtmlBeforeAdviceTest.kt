package net.cliff3.maven.web.aop

import org.junit.jupiter.api.*

@TestMethodOrder(MethodOrderer.MethodName::class)
class EscapeHtmlBeforeAdviceTest {
    @Test
    @Order(1)
    @DisplayName("EscapeHTML test")
    fun escapeHtmlTest() {
        val source = """
            <script type="text/javascript">
            alert('test');
            </script>
        """.trimIndent()
        val advice = EscapeHtmlBeforeAdvice()
    }
}