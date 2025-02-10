(ns reinen-vernunft.wip.constraint-test
  (:require [clojure.test :refer :all]
            [fogus.reinen-vernunft.wip.constraints :as c]))

(deftest solve-tests1
  (let [c1 (c/->constraint [(c/->variable '?x [0 1])
                            (c/->variable '?y [0 1])
                            (c/->variable '?z [0 1])]
                         '(= (+ ?x ?y) ?z))]
    (is (= [(c/map->cpair '{:name ?x :value 0})
            (c/map->cpair '{:name ?y :value 0})
            (c/map->cpair '{:name ?z :value 0})]
           (c/find-sat c1)))))

(deftest solve-tests2
  (let [c1 (c/->constraint [(c/->variable '?x [0 1])
                            (c/->variable '?y [1 2])
                            (c/->variable '?z [2 3])]
                         '(= (+ ?x ?y) ?z))]
    (is (= [(c/map->cpair '{:name ?x :value 1})
            (c/map->cpair '{:name ?y :value 1})
            (c/map->cpair '{:name ?z :value 2})]
           (c/find-sat c1)))))

(deftest solve-tests3
  (let [c1 (c/->constraint [(c/->variable '?x [1 1])
                            (c/->variable '?y [2 2])
                            (c/->variable '?z [3 3])]
                         '(= (+ ?x ?y) ?z))]
    (is (= [(c/map->cpair '{:name ?x :value 1})
            (c/map->cpair '{:name ?y :value 2})
            (c/map->cpair '{:name ?z :value 3})]
           (c/find-sat c1)))))
