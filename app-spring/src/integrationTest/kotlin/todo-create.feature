Feature: Create a new todo. This script is for reuse.

  Background:
    * url baseUrl

  Scenario: Add a new todo

    Given path 'todos'
    And request { description: '#(todoDescription ? todoDescription : "Default description")'}
    When method POST
    Then status 201
    And match $ contains { id: '#number' }
