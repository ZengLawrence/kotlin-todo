package history.domain

import history.domain.TodoDomainDsl.todoDomain
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.ZonedDateTime
import kotlin.test.*

class TodoDomainTest {

    @Test
    fun `add and checked done event should show todo with checked done and timestamp from done event`() {
        val id = 1
        val update1 = ZonedDateTime.now()
        val update2 = update1.plusMinutes(60)
        val eventSourceMock = mock<EventSource> {
            on { findEvents(id) } doReturn listOf(
                Event("ADD", update1),
                Event("CHECKED_DONE", update2),
            )
        }
        val todoDomain = todoDomain {
            eventSourceMock
        }

        assertThat(todoDomain.find(id)).isEqualTo(
            Todo(id, description = "", done = true, lastUpdatedDateTime = update2)
        )
    }

}