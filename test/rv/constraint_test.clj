(ns rv.constraint-test
  (:require [clojure.test :refer :all]
            [fogus.rv.core :as core]
            [fogus.rv.constraints :as c]))

(deftest test-satisfy1-no-answer
  (let [?x (core/->LVar 'x [0 1])
        ?y (core/->LVar 'y [0 1])
        ?z (core/->LVar 'z [0 1])
        c1 {:variables [?x ?y ?z]
            :formula   `(= (+ ~?x ~?y 10000) ~?z)}]
    (is (= {}
           (c/satisfy1 c1)))))

(deftest test-satisfy1-single-answer
  (let [?x (core/->LVar 'x [0 1])
        ?y (core/->LVar 'y [0 1])
        ?z (core/->LVar 'z [0 1])
        c1 {:variables [?x ?y ?z]
            :formula   `(= (+ ~?x ~?y) ~?z)}]
    (is (= {?x 0 ?y 0 ?z 0}
           (c/satisfy1 c1)))))

(deftest test-satisfy1-2
  (let [?x (core/->LVar 'x [0 1])
        ?y (core/->LVar 'y [1 2])
        ?z (core/->LVar 'z [2 3])
        c1 {:variables [?x ?y ?z]
            :formula   `(= (+ ~?x ~?y) ~?z)}]
    (is (= {?x 1 ?y 1 ?z 2}
           (c/satisfy1 c1)))))

(deftest test-satisfy1-3
  (let [?x (core/->LVar 'x [1 1])
        ?y (core/->LVar 'y [2 2])
        ?z (core/->LVar 'z [3 3])
        c1 {:variables [?x ?y ?z]
            :formula   `(= (+ ~?x ~?y) ~?z)}]
    (is (= {?x 1 ?y 2 ?z 3}
           (c/satisfy1 c1)))))

(deftest test-satisfy1-range
  (let [?x (core/->LVar 'x (range 1 7))
        ?y (core/->LVar 'y (range 3 8))
        c1 {:variables [?x ?y]
            :formula   `(= (+ ~?x ~?y) 10)}]
    (is (= {?x 6 ?y 4}
           (c/satisfy1 c1)))))

(deftest test-satisfy*-range
  (let [?x (core/->LVar 'x (range 1 7))
        ?y (core/->LVar 'y (range 3 8))
        c1 {:variables [?x ?y]
            :formula   `(= (+ ~?x ~?y) 10)}]
    (is (= #{{?x 5, ?y 5} {?x 3, ?y 7} {?x 6, ?y 4} {?x 4, ?y 6}}
           (set (c/satisfy* c1))))))

(deftest test-satisfy*-range-no-answer
  (let [?x (core/->LVar 'x (range 1 7))
        ?y (core/->LVar 'y (range 3 8))
        c1 {:variables [?x ?y]
            :formula   `(= (+ ~?x ~?y) 1000000)}]
    (is (empty? (c/satisfy* c1)))))
