Feature: Todo APIs

  Scenario: Add a new todo

  Given url 'http://localhost:7070/todos'
  And request { description: 'Buy milk'}
  When method POST
  Then status 201
    And match $ contains { id: 1 }
