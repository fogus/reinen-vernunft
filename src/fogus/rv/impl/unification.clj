;   Copyright (c) Fogus. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns fogus.rv.impl.unification
  "Provides internal unification functions.
  DO NOT USE THIS NS.
  There is no guarantee that it will remain stable or at all."
  (:require [fogus.rv.core :as core]
            clojure.core.unify))

(def subst (clojure.core.unify/make-occurs-subst-fn core/lv?))
