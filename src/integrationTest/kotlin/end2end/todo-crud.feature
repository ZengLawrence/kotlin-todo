Feature: CRUD operations on a todo

  Background:
    * url baseUrl

    * def result = call read('../todo-create.feature') { todoDescription: "Buy milk" }
    * def todo = result.response
    * match todo contains { id: '#number' }

  Scenario: Create a todo should get it back. Done is default to false.
    # reuse create feature
    * match result.responseStatus == 201
    * match todo contains { id: '#number' }

    * def id = todo.id
    Given path 'todos', id
    When method GET
    Then status 200
    And match $ == { id: '#(id)', description: 'Buy milk', done: false }

  Scenario: After delete a todo should return not found 404
    Given path 'todos', todo.id
    When method DELETE
    Then status 204

    Given path 'todos', todo.id
    When method GET
    Then status 404
