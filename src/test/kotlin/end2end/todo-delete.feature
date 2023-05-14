Feature: Delete a todo

  Background:
    * url 'http://localhost:7070/'

    * def todo = call read('todo-create.feature')

  Scenario: Delete a todo should return not found 404
    Given path 'todos', todo.id
    When method DELETE
    Then status 204

    Given path 'todos', todo.id
    When method GET
    Then status 404
