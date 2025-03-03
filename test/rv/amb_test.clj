(ns rv.amb-test
  (:require [clojure.test :refer :all]
            [fogus.rv.amb :as rv]))

(deftest test-amb-null-body
  (is (nil? (rv/amb))))

(deftest test-amb-simple-binding
  (is (= 3 (rv/amb [x (range 10)]
                   (rv/accept (= x 3)
                              x))))
  (is (nil? (rv/amb [x (range 10)]
                    (rv/accept false
                               x))))
  (is (nil? (rv/amb [x (range 10)]
                    (rv/accept (> x 10)
                               x)))))

(deftest test-amb-complex-binding
  (testing "that a complex binding passes as expected."
    (is (= ["that" "thing" "grows" "slowly"]
           (rv/amb [A ["the" "that" "a"]
                    B ["frog" "elephant" "thing"]
                    C ["walked" "treaded" "grows"]
                    D ["slowly" "quickly"]]
                   
                   (rv/accept (and (= (last A) (first B))
                                   (= (last B) (first C))
                                   (= (last C) (first D)))
                              [A B C D])))))

  (testing "that a complex binding fails when an imposible condition is present."
    (is (nil? (rv/amb [A ["the" "that" "a"]
                       B ["frog" "elephant" "thing"]
                       C ["walked" "treaded" "grows"]
                       D ["slowly" "quickly"]]
                      
                      (rv/accept false [A B C D]))))))
