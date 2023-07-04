Feature: List all todos

  Background:
    * url baseUrl

  Scenario: List all todos that are created
    * table newTodos
      | todoDescription |
      | 'Buy bread'     |
      | 'Take out trash'|
      | 'Eat lunch'     |

    * def result = call read('classpath:todo-create.feature') newTodos
    * def created = $result[*].response
    * match each created == { id: '#number' }

    Given path 'todos'
    When method GET
    # Match only ones created here. There might be other in the list, but we should not care.
    Then match each $ contains { id: '#number', description: '#string', done: '#boolean'}
    And match response[*].description contains ['Buy bread', 'Take out trash', 'Eat lunch']