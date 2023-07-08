package todo

import arrow.core.Either
import org.assertj.core.api.Assertions.assertThat
import org.mockito.kotlin.*
import kotlin.test.BeforeTest
import kotlin.test.Test

class TodoDomainTest {

    private lateinit var todoDomain: TodoDomain

    private val persistenceMock = mock<TodoPersistence>()

    private val changeNotificationMock = mock<ChangeNotification>()

    @BeforeTest
    fun setUp() {
        todoDomain = TodoDomain(persistenceMock, changeNotificationMock)
    }

    @Test
    fun `add a new todo should return id and notify added`() {
        val desc = "new todo"
        val id = 1
        persistenceMock.stub {
            on { insert(desc, done = false) }.doReturn(id)
        }

        assertThat(todoDomain.add(desc)).isEqualTo(Either.Right(id))

        verify(changeNotificationMock).added(id, desc)
    }

    @Test
    fun `add a new todo with empty description should return empty description error`() {
        assertThat(todoDomain.add("")).isEqualTo(Either.Left(EmptyTodoDescription))

        verifyNoInteractions(persistenceMock, changeNotificationMock)
    }

    @Test
    fun `add a new todo when persistence throws a runtime exception should return runtime error`() {
        val desc = "new todo"
        val errMsg = "a runtime error"
        persistenceMock.stub {
            on { insert(desc, done = false) }.doThrow(RuntimeException(errMsg))
        }

        assertThat(todoDomain.add(desc)).isEqualTo(Either.Left(RuntimeError(errMsg)))

        verifyNoInteractions(changeNotificationMock)
    }

    @Test
    fun `add a new todo when change notification throws runtime exception should return id and ignore runtime exception`() {
        val desc = "new todo"
        val id = 1
        persistenceMock.stub {
            on { insert(desc, done = false) }.doReturn(id)
        }
        changeNotificationMock.stub {
            on { added(id, desc) }.doThrow(RuntimeException())
        }

        assertThat(todoDomain.add(desc)).isEqualTo(Either.Right(id))
    }

    @Test
    fun `find a todo when found should return it and do not notify change`() {
        val id = 1
        val desc = "get milk"
        val done = true
        persistenceMock.stub {
            on { find(id) }.doReturn(PTodo(id, desc, done))
        }

        assertThat(todoDomain.find(id)).isEqualTo(Todo(id, desc, done))

        verifyNoInteractions(changeNotificationMock)
    }

    @Test
    fun `toggle done on a todo to true should persist and notify checked done`() {
        val id = 1
        persistenceMock.stub {
            on { find(id) }.doReturn(PTodo(id, "buy milk", done = false))
        }
        todoDomain.toggleDone(id, done = true)

        verify(persistenceMock).update(id, done = true)
        verify(changeNotificationMock).checkedDone(id)
    }

    @Test
    fun `toggle done on a todo to false should persist and notify unchecked done`() {
        val id = 1
        persistenceMock.stub {
            on { find(id) }.doReturn(PTodo(id, "buy milk", done = false))
        }
        todoDomain.toggleDone(id, done = false)

        verify(persistenceMock).update(id, done = false)
        verify(changeNotificationMock).uncheckedDone(id)
    }

    @Test
    fun `toggle done on a todo when throw exception on persist should return runtime error`() {
        val id = 1
        val errMsg = "a runtime error"
        persistenceMock.stub {
            on { find(id) }.doThrow(RuntimeException(errMsg))
        }

        assertThat(todoDomain.toggleDone(id, done = false))
            .isEqualTo(Either.Left(RuntimeError(errMsg)))

        verifyNoInteractions(changeNotificationMock)
    }

    @Test
    fun `toggle done on a todo to true when throw exception on notify check done should persist and return`() {
        val id = 1
        persistenceMock.stub {
            on { find(id) }.doReturn(PTodo(id, "buy milk", done = false))
        }
        changeNotificationMock.stub {
            on { checkedDone(id) }.doThrow(RuntimeException())
        }

        todoDomain.toggleDone(id, done = true)

        verify(persistenceMock).update(id, done = true)
    }

    @Test
    fun `toggle done on a todo to false when throw exception on notify uncheck done should persist and return`() {
        val id = 1
        persistenceMock.stub {
            on { find(id) }.doReturn(PTodo(id, "buy milk", done = false))
        }
        changeNotificationMock.stub {
            on { uncheckedDone(id) }.doThrow(RuntimeException())
        }

        todoDomain.toggleDone(id, done = false)

        verify(persistenceMock).update(id, done = false)
    }

    @Test
    fun `delete a todo should persist delete and notify deleted`() {
        val id = 1
        todoDomain.delete(id)

        verify(persistenceMock).delete(id)
        verify(changeNotificationMock).deleted(id)
    }

    @Test
    fun `delete a todo when persist throws runtime exception should return runtime error and do not notify`() {
        val id = 1
        persistenceMock.stub {
            on { delete(id) }.doThrow(RuntimeException())
        }

        todoDomain.delete(id)

        verifyNoInteractions(changeNotificationMock)
    }

    @Test
    fun `delete a todo when notify deleted throws runtime exception should persist and ignore exception`() {
        val id = 1
        changeNotificationMock.stub {
            on { deleted(id) }.doThrow(RuntimeException())
        }

        todoDomain.delete(id)

        verify(persistenceMock).delete(id)
    }

}