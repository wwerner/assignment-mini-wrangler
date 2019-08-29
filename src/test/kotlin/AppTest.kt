package test.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppTest {
    @Test
    fun `sample test`() {
        assertThat(true).isEqualTo(true)
    }
}