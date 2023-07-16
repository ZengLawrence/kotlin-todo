Feature: Go through life cycles of a todo

  Background:
    * url baseUrl
    * def QueueConsumer = Java.type('changenotification.MockQueueConsumer')
    * def queue = new QueueConsumer()
    * queue.init()
    * queue.drain()

  Scenario: Add, check done, uncheck done, then delete
    * def result = call read('classpath:todo-create.feature') { todoDescription: "Buy milk" }
    * def todo = result.response
    * def id = todo.id
    * json notice = queue.poll()
    * match notice == { id: '#(id)', description: 'Buy milk', op: 'ADD' }

    # check done
    Given path 'todos', todo.id
    And request { done: true }
    When method PATCH
    Then status 204
    * json notice = queue.poll()
    * match notice == { id: '#(id)', op: 'CHECKED_DONE' }

    # uncheck done
    Given path 'todos', todo.id
    And request { done: false }
    When method PATCH
    Then status 204
    * json notice = queue.poll()
    * match notice == { id: '#(id)', op: 'UNCHECKED_DONE' }

    Given path 'todos', todo.id
    When method DELETE
    Then status 204
    * json notice = queue.poll()
    * match notice == { id: '#(id)', op: 'DELETED' }

    * queue.close()
