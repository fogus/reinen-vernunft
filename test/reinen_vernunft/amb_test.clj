(ns reinen-vernunft.amb-test
  (:require [clojure.test :refer :all]
            [fogus.reinen-vernunft.amb :as rv]))

(deftest test-amb-simple-binding
  (is (= 3 (rv/amb [x (range 10)]
                   (rv/accept (= x 3)
                     x)))))

(deftest test-amb-complex-binding
  (is (= ["that" "thing" "grows" "slowly"]
         (rv/amb [A ["the" "that" "a"]
                  B ["frog" "elephant" "thing"]
                  C ["walked" "treaded" "grows"]
                  D ["slowly" "quickly"]]
                 
                 (rv/accept (and (= (last A) (first B))
                                 (= (last B) (first C))
                                 (= (last C) (first D)))
                   [A B C D])))))
