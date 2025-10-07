# Mule Liferay Connector

This project contains the Liferay Mule connector, which connects Liferay to other platforms and services in the Mulesoft ecosystem using Anypoint Studio or Anypoint Design Center.

## Prerequisites

- JDK 17+
- Apache Maven, version 3.3.9+
- Anypoint Studio, version 7.4+

Download Anypoint Studio: https://www.mulesoft.com/platform/studio

## Using the Liferay Connector

The Liferay connector is available on [Anypoint Exchange](https://www.mulesoft.com/exchange/com.liferay/com.liferay.mule) and can be accessed both from Anypoint Studio and Anypoint Design Center.

## Building and Deploying the Liferay Connector

1. Import the source code as a new Maven project to the IDE of your choice.

1. Pass the command line parameter `-parameters` to the Java compiler. For example, on IntelliJ, go to Preferences &rarr; Build, Execution, Deployment &rarr; Compiler &rarr; Java Compiler and add `-parameters` in the Additional Command Line Parameters section.

1. Execute `mvn clean install` from the `modules/integrations/mulesoft` folder.

Mule SDK instructions: https://docs.mulesoft.com/mule-sdk/1.1/getting-started

### Deploy to Anypoint Studio

Once you have built the connector, add this dependency to the `pom.xml` file in the root of your Anypoint Studio project:

```xml
<groupId>com.liferay</groupId>
<artifactId>com.liferay.mule</artifactId>
<version>CONNECTOR_VERSION</version>
<classifier>mule-plugin</classifier>
```

## License

`SPDX-License-Identifier: (LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06)`

See `LICENSE.md` for details.