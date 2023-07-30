package history.domain

import history.domain.TodoDomainDsl.todoDomain
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.ZonedDateTime
import kotlin.test.*

class TodoDomainTest {

    @Test
    fun `add and check done event should show todo with check done and timestamp from done event`() {
        val id = 1
        val update1 = ZonedDateTime.now()
        val update2 = update1.plusMinutes(60)
        val eventSourceMock = mock<EventSource> {
            on { findEvents(id) } doReturn listOf(
                AddEvent(id, "Buy milk", update1),
                CheckDoneEvent(id, update2),
            )
        }
        val todoDomain = todoDomain {
            eventSourceMock
        }

        assertThat(todoDomain.find(id)).isEqualTo(
            Todo(id, description = "Buy milk", done = true, lastUpdatedDateTime = update2)
        )
    }

    @Test
    fun `add, check done, uncheck done event should show todo with not done and timestamp from uncheck done event`() {
        val id = 1
        val update1 = ZonedDateTime.now()
        val update2 = update1.plusMinutes(60)
        val update3 = update1.plusMinutes(90)
        val eventSourceMock = mock<EventSource> {
            on { findEvents(id) } doReturn listOf(
                AddEvent(id, "Buy milk", update1),
                CheckDoneEvent(id, update2),
                UncheckDoneEvent(id, update3)
            )
        }
        val todoDomain = todoDomain {
            eventSourceMock
        }

        assertThat(todoDomain.find(id)).isEqualTo(
            Todo(id, description = "Buy milk", done = false, lastUpdatedDateTime = update3)
        )
    }

    @Test
    fun `event not found should return null todo`() {
        val id = 1
        val eventSourceMock = mock<EventSource> {}
        val todoDomain = todoDomain {
            eventSourceMock
        }

        assertThat(todoDomain.find(id)).isNull()
    }

    @Test
    fun `delete event should return null todo`() {
        val id = 1
        val eventSourceMock = mock<EventSource> {
            val update1 = ZonedDateTime.now()
            on { findEvents(id) } doReturn listOf(
                AddEvent(id, "Buy milk", update1),
                DeleteEvent(id, update1.plusMinutes(60))
            )
        }
        val todoDomain = todoDomain {
            eventSourceMock
        }

        assertThat(todoDomain.find(id)).isNull()
    }
}