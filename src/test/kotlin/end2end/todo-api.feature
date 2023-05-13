Feature: Todo APIs

Scenario: Add a new todo

Given url 'http://localhost:7070/todos'
And request { description: 'Eat lunch'}
When method POST
Then status 201