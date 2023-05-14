Feature: CRUD operations on a todo

  Background:
    * url baseUrl

    * def result = call read('../todo-create.feature')
    * def todo = result.response
    * match todo contains { id: '#number' }

  Scenario: Create a todo should return created 201
    # reuse create feature
    * match todo contains { id: '#number' }

  Scenario: After delete a todo should return not found 404
    Given path 'todos', todo.id
    When method DELETE
    Then status 204

    Given path 'todos', todo.id
    When method GET
    Then status 404
