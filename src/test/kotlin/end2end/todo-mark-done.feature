Feature: Mark a todo done

  Background:
    * url baseUrl

    * def todo = call read('todo-create.feature')
    * assert !todo.done

  Scenario:
    Given path 'todos', todo.id
    And request { done: true }
    When method PATCH
    Then status 204