package test.kotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LearningTests {
    @Test
    fun `can read CSV into stream`() {
        File("src/test/resources/orders.csv").bufferedReader().useLines {
            it.forEach { println(it) }
        }
    }
}