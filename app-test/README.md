# app-test

This module is used to share end-to-end tests that are implemented in karate.

## How to use
1. Add configuration `karate-config.js` in `src/integrationTest/kotlin` directory.

```javascript
function fn() {
  var config = { // base config JSON
    baseUrl: 'http://localhost:7070/',
  };
  return config;
}
```

2. Add below class `ApiTest` in `end2end` directory, so that JUnit can pick up the tests.

```kotlin
package end2end

import com.intuit.karate.junit5.Karate


class ApiTest {

    @Karate.Test
    fun testAll(): Karate {
        return Karate.run().relativeTo(javaClass)
    }

}
```