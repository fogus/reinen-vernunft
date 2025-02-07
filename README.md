# reinen-vernunft

Explorations and conversations regarding pure reason using Clojure.  Currently, I've implemented the following:

* [fogus.reinen-vernunft.rules](https://github.com/fogus/reinen-vernunft/blob/master/src/fogus/reinen_vernunft/rules.clj) - simplest possible production rules (more information in [Read-Eval-Print-λove v004](https://leanpub.com/readevalprintlove004))
* McCarthy's amb

## Including

### deps.edn

    me.fogus/reinen-vernunft {:mvn/version "0.0.2"}

### Leiningen

Modify your [Leiningen](http://github.com/technomancy/leiningen) dependencies to include [reinen-vernunft](http://fogus.me/fun/reinen-vernunft/):

    :dependencies [[me.fogus/reinen-vernunft "0.0.2"] ...]    

### Maven

Add the following to your `pom.xml` file:

    <dependency>
      <groupId>me.fogus</groupId>
      <artifactId>reinen-vernunft</artifactId>
      <version>0.0.2</version>
    </dependency>

## Dev Testing

    clj -X:dev:test

## License

Copyright © 2017-2025 Fogus

Distributed under the Eclipse Public License version 1.0.
