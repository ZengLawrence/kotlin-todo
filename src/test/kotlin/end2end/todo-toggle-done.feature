Feature: Toggle done on a todo

  Background:
    * url baseUrl

    * def result = call read('todo-mark-done.feature')
    * def todo = result.response
    * assert todo.done

  Scenario: Mark a done todo undone
    Given path 'todos', todo.id
    And request { done: false }
    When method PATCH
    Then status 204

    Given path 'todos', todo.id
    When method GET
    Then status 200
    And match $ contains { done: false }

  Scenario: Does not provide 'done' attribute return Bad Request 400
    Given path 'todos', todo.id
    And request { id: '#(todo.id)' }
    When method PATCH
    Then status 400
    And match $ contains { errorDescription: "'done' attribute is not provided" }
