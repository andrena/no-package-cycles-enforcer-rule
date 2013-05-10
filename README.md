[![Build Status](https://buildhive.cloudbees.com/job/andrena/job/no-package-cycles-enforcer-rule/badge/icon)](https://buildhive.cloudbees.com/job/andrena/job/no-package-cycles-enforcer-rule/)

Usage: Build and add the following plugin:

```
<plugin>
	<artifactId>maven-enforcer-plugin</artifactId>
	<version>1.2</version>
	<dependencies>
		<dependency>
			<groupId>de.andrena.tools.nopackagecycles</groupId>
			<artifactId>no-package-cycles-enforcer-rule</artifactId>
			<version>1.0.0</version>
		</dependency>
	</dependencies>
	<executions>
		<execution>
			<id>enforce-no-package-cycles</id>
			<goals>
				<goal>enforce</goal>
			</goals>
			<phase>verify</phase>
			<configuration>
				<rules>
					<NoPackageCyclesRule implementation="de.andrena.tools.nopackagecycles.NoPackageCyclesRule" />
				</rules>
			</configuration>
		</execution>
	</executions>
</plugin>
```

See also: original version at http://stackoverflow.com/questions/3416547/maven-jdepend-fail-build-with-cycles

Improved by showing all packages afflicted with cycles and the corresponding classes importing the conflicting packages.
