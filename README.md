# rv

Explorations in pure reasoning algorithms with Clojure.

## Including

### deps.edn

    me.fogus/rv {:mvn/version "0.0.5"}

OR

    io.github.fogus/rv {:git/tag "v0.0.5" :git/sha "86e2de4e363e8f0abd3b8df2a4baf2360c537e68"}

### Leiningen

Modify your [Leiningen](http://github.com/technomancy/leiningen) dependencies to include:

    :dependencies [[me.fogus/rv "0.0.5"] ...]    

### Maven

Add the following to your `pom.xml` file:

    <dependency>
      <groupId>me.fogus</groupId>
      <artifactId>rv</artifactId>
      <version>0.0.5</version>
    </dependency>

## Dev

Namespaces under the wip sub-ns are works in progress and should only be used for experimentation. It is expected that these implementations will change frequently and may disappear altogether.

    clj -X:dev:test

## License

Copyright © 2017-2025 Fogus

Distributed under the Eclipse Public License version 2.0
