## Version: [v2.0.2](https://github.com/newrelic-experimental/newrelic-java-reactor/releases/tag/v2.0.2) | Created: 2024-06-11
### Bug Fixes
- Ixes potential null pointer error

## Version: [v2.0.1](https://github.com/newrelic-experimental/newrelic-java-reactor/releases/tag/v2.0.1) | Created: 2023-09-22


## Version: [v2.0.0](https://github.com/newrelic-experimental/newrelic-java-reactor/releases/tag/v2.0.0) | Created: 2023-09-22

### Bug Fixes
- Build fixes
- Merge pull request #2 from newrelic-experimental/buildfixes

### Enhancements
- Enhanced Distributed Tracing: With the latest update, transaction traces are now seamlessly integrated into distributed tracing. We've refactored our Reactor instrumentation to use DT headers, reducing overhead and ensuring a more accurate representation of the transaction flow, aligning with open telemetry best practices.
- 
## Installation

To install:

1. Download the latest release jar files.
2. In the New Relic Java directory (the one containing newrelic.jar), create a directory named extensions if it does not already exist.
3. Copy the downloaded jars into the extensions directory.
4. Restart the application.   

