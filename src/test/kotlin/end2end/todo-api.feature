Feature: Todo APIs

  Background:
    * url 'http://localhost:7070/'

  Scenario: Add a new todo and get it back

    Given path 'todos'
    And request { description: 'Buy milk'}
    When method POST
    Then status 201
    And match $ contains { id: '#number' }

    * def id = response.id
    * def expected = { id:  0, description: 'Buy milk', done: false }
    * set expected.id = id
    Given path 'todos', id
    When method GET
    Then status 200
    And match $ == expected
