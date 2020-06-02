;   Copyright (c) Fogus. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns fogus.reinen-vernunft.amb
  "Provides an implementation of McCarthy's `amb` operator with
   binding forms and acceptance test operator."
  (:require [fogus.reinen-vernunft.util :as util]))

(defn cart [colls]
  (if (empty? colls)
    '(())
    (for [more (cart (rest colls))
          x (first colls)]
      (cons x more))))

(defmacro accept [condition ret]
  `(do (when (not ~condition)
         (throw (ex-info "Failing" {::failure   '~condition
                                    ::backtrack true})))
       ~ret))

(defmacro amb
  "A macro that provides a non-deterministic way to traverse a space
   and find a single solution amongst potentially many. If the search
   space is exhausted then `amb` will return `nil`. The general form
   of `amb` is as follows:

      (amb <bindings> <execution body>)

   Where `<bindings>` is a typical Clojure bindings form:

      [<name1> <value1> ... <nameN> <valueN>]

   And `<execution body>` is one or more Clojure expressions.

   Within the execution body the `(accept <condition> <expression>)`
   form is used to test some combination of the bindings for adherence
   to a `<condition>` and return an `<expression>` that serves as the
   return value of the call to `amb`."
  [& [binds & body]]
  (when (and binds body)
    (let [{:keys [names values]} (util/process-bindings binds)]
      `(let [proc# (fn [[~@names]]
                     (try (do ~@body)
                          (catch clojure.lang.ExceptionInfo e#
                            (ex-data e#))))
             vals# (cart ~values)]
         (loop [[v# & vs#] vals#]
           (let [result# (proc# v#)]
             (if (::backtrack result#)
               (when (seq vs#)
                 (recur vs#))
               result#)))))))


