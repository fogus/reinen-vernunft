;   Copyright (c) Fogus. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns fogus.reinen-vernunft.amb
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
  [binds & body]
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
             result#))))))


