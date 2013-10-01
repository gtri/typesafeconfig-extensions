# Typesafe Config Extensions

Libraries supporting [Typesafe Config](https://github.com/typesafehub/config).

## Components

### Factory

**typesafeconfig-factory** provides a `ConfigFactory` class with which you can clearly and
concisely express how you want all sources of configuration data to be aggregated.

```
import static edu.gatech.gtri.typesafeconfigextensions.factory.*;
```

A standard configuration loader can be defined as follows.

```
ConfigFactory standard = emptyConfigFactory()
     .bindDefaults()
     .withSources(
         classpathResource("reference"),
         classpathResource("application"),
         systemProperties()
     ).fromLowestToHighestPrecedence();
```

`reference.conf` classpath resources are overridden by `application.conf`, and both are
overridden by system properties.

Suppose we also want to load configuration from `/etc/exampleApp.conf`, sandwiched in the
precedence order between the classpath configs and the system properties. The previous factory
can be adapted to this requirement.

```
java.nio.file.Path path = java.nio.file.Paths.get("/etc/exampleApp");

ConfigFactory custom = standard
    .insertSource(configFile().byPath(path))
    .withHigherPrecedenceThan(classpathResource("application"));
```

Suppose we don't want to use a fixed filesystem path, but would rather let the user of our
application decide where to put their config file.

To support this scenario, `ConfigFactory` repeats its loading process twice so that values
from the first `Config` can be used to load *even more configuration* on the second go.
This allows us to load a file *by key*:

```
ConfigFactory custom2 = standard
    .insertSource(configFile().byKey("exampleApp.config.file"))
    .withHigherPrecedenceThan(classpathResource("application"));
```

Now someone can launch your application with the JVM flag
`-DexampleApp.config.file=$HOME/myAppSettings`, and the contents of
`$HOME/myAppSettings.conf` will be loaded into the configuration.

### JNDI

**typesafeconfig-jndi** converts a JNDI context into a `Config` object so you'll never have
to deal with the `javax.naming` API again.

```
import javax.naming.Context;
import static edu.gatech.gtri.typesafeconfigextensions.jndi.JndiContexts.*;
```

To convert from `javax.naming.Context` to `Config`:

```
Context someContext = /* ... */;
Config config = context(someContext).toConfig();
```

To just retrieve a JNDI variable named `abc` from the initial context:

```
String configValue = jndiContext().toConfig().getString("abc");
```

### For Webapps

**typesafeconfig-for-webapps** defines a standard `ConfigFactory` for servlet-based
web applications.

```
import edu.gatech.gtri.typesafeconfigextensions.forwebapps.WebappConfigs.*;
```

```
ServletContext servletContext = /* ... */;
ConfigFactory factory = webappConfigFactory(servletContext);
```

This factory loads these configuration sources, in order from highest to lowest precedence:

* System properties
* File: `JNDI(config directory)/[servlet context path]`
* File: `${webapp.config.directory}/[servlet context path]`
* File: `JNDI(config file)`
* File: `${webapp.config file}`
* Classpath resource: `application.conf`
* Classpath resource: `resource.conf`

`JNDI(x)` refers to the JNDI variable named `x`, `[servlet context path]` is the value of
`servletContext.getContextPath()`, and `${x}` denotes the value of the `Config` at path `x`.

You might set up `server.xml` in a Tomcat installation like this, for example:

```
<Context path="/myApplication/apiServer" docBase="/users/chris/myAppApiServer">
  <Environment name="config directory" type="java.lang.String" value="${CATALINA_BASE}/conf"/>
</Context>
```

(`CATALINA_BASE` is an environment variable pointing to the path of the Tomcat installation.)

This web application's `ConfigFactory` would then include configuration loaded from
`$CATALINA_BASE/conf/myApplication/apiServer.conf`.

### For Scala

**typesafeconfig-for-scala** adds some sugar to help use the Config library in Scala.

```
import edu.gatech.gtri.typesafeconfigextensions.forscala._
```

```
val a = "A { B: C }, D: E".toConfig          // A { B: C }, D: E
val b = ("A" -> 7).toConfig                  // A: 7
val c = Seq("A" -> 4, "B" -> "C").toConfig   // A: 4, B: C
val d = c ++ b                               // A: 7, B: C
val e = b + ("F" -> "G")                     // A: 7, F: G
val f = a - "A"                              // D: E
```

## Build scripts

`./sbt` to launch the build tool (SBT).

`./checkstyle` to run Checkstyle.
