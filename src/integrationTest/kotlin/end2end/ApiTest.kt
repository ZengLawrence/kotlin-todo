package end2end

import app.App.Companion.app
import com.intuit.karate.junit5.Karate
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll


class ApiTest {

    @Karate.Test
    fun testAll(): Karate? {
        return Karate.run().relativeTo(javaClass)
    }

    companion object {

        private val app = app{}.build()

        @JvmStatic
        @BeforeAll
        fun setUp() {
            app.start(7070)
        }

        @JvmStatic
        @AfterAll
        fun shutDown() {
            app.stop()
        }
    }

}