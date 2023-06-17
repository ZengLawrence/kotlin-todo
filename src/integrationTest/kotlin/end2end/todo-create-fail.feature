Feature: When create fails

  Background:
    * url baseUrl

  Scenario: Empty description should return 400 bad request
    Given path 'todos'
    And request { description: ''}
    When method POST
    Then status 400
