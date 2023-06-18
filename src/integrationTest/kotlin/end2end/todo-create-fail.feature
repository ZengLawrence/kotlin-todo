Feature: When create fails

  Background:
    * url baseUrl

  Scenario: Empty description should return 400 bad request
    Given path 'todos'
    And request { description: ''}
    When method POST
    Then status 400
    And match $ contains { errorDescription: "'description' attribute can not be empty" }

  Scenario: Description longer than 100 characters should return 400 bad request
    * def longDesc = "a".repeat(101)

    Given path 'todos'
    And request { description: '#(longDesc)'}
    When method POST
    Then status 400
    And match $ contains { errorDescription: "'description' attribute can not be longer than 100 characters" }
