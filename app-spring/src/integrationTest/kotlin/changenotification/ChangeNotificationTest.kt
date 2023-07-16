package changenotification

import com.intuit.karate.junit5.Karate

class ChangeNotificationTest {

    @Karate.Test
    fun testChangeNotification(): Karate =
        Karate.run().relativeTo(javaClass)

}