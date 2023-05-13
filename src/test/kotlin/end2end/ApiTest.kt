package end2end

import App
import com.intuit.karate.junit5.Karate
import org.junit.jupiter.api.BeforeAll


class ApiTest {

    @Karate.Test
    fun testAll(): Karate? {
        return Karate.run().relativeTo(javaClass)
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUp() {
            App().instance.start(7070)
        }
    }

}