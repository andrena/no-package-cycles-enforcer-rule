[![Build Status](https://travis-ci.org/andrena/no-package-cycles-enforcer-rule.svg)](https://travis-ci.org/andrena/no-package-cycles-enforcer-rule)

This Maven Enforcer Rule checks your project for package cycles. It fails the build if any package cycle is found, showing you the packages and classes involved in the cycle.

Usage: Add the following plugin to your POM:

```xml
<plugin>
	<artifactId>maven-enforcer-plugin</artifactId>
	<version>1.4.1</version>
	<dependencies>
		<dependency>
			<groupId>de.andrena.tools.nopackagecycles</groupId>
			<artifactId>no-package-cycles-enforcer-rule</artifactId>
			<version>1.0.6</version>
		</dependency>
	</dependencies>
	<executions>
		<execution>
			<id>enforce-no-package-cycles</id>
			<goals>
				<goal>enforce</goal>
			</goals>
			<phase>test</phase>
			<configuration>
				<rules>
					<NoPackageCyclesRule implementation="de.andrena.tools.nopackagecycles.NoPackageCyclesRule" />
				</rules>
			</configuration>
		</execution>
	</executions>
</plugin>
```

See also:
* The original version by Daniel Seidewitz on [Stackoverflow](http://stackoverflow.com/questions/3416547/maven-jdepend-fail-build-with-cycles). Improved by showing all packages afflicted with cycles and the corresponding classes importing the conflicting packages.
* [JDepend](https://github.com/clarkware/jdepend), the library being used to detect package cycles.
* For more information about package cycles, see ["The Acyclic Dependencies Principle" by Robert C. Martin (Page 6)](http://www.objectmentor.com/resources/articles/granularity.pdf). 
