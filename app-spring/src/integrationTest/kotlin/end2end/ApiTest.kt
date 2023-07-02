package end2end

import com.intuit.karate.junit5.Karate


class ApiTest {

    @Karate.Test
    fun testAll(): Karate? {
        return Karate.run().relativeTo(javaClass)
    }

}