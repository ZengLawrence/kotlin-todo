Feature: Mark a todo done

  Background:
    * url baseUrl

    * def result = call read('../todo-create.feature')
    * def todo = result.response
    * assert !todo.done

  Scenario: Mark a todo done
    Given path 'todos', todo.id
    And request { done: true }
    When method PATCH
    Then status 204

    Given path 'todos', todo.id
    When method GET
    Then status 200
    And match $ contains { done: true }
