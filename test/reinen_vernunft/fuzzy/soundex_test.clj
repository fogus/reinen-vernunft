(ns reinen-vernunft.fuzzy.soundex-test
  (:require [clojure.test :refer :all]
            [fogus.reinen-vernunft.fuzzy.soundex :as fuzzy]))

(def encodings {
                "Ashcraft" "A261"
                "Ashcroft" "A261"
                "Burroughs" "B620"
                "Burrows" "B620"
                "Clojure" "C426"
                "Ellery" "E460"
                "Euler" "E460"
                "Gauss" "G200"
                "Ghosh" "G200"
                "Gutierrez" "G362"
                "Heilbronn" "H416"
                "Hilbert" "H416"
                "Honeyman" "H555"
                "Jackson" "J250"
                "Johnson" "J525"
                "KAMAS" "K520"
                "Kant" "K530"
                "Knuth" "K530"
                "Ladd" "L300"
                "Lee" "L000"
                "Lissajous" "L222"
                "Lloyd" "L300"
                "Lukasiewicz" "L222"
                "Michael" "M240"
                "O'Hara" "O600"
                "Pfister" "P236"
                "Rubin" "R150"
                "Soundex" "S532"
                "Sownteks" "S532"
                "Tymczak" "T522"
                "VanDeusen" "V532"
                "Washington" "W252"
                "Wheaton" "W350"
                })

(deftest test-single-words
  (is (= "A000" (fuzzy/encode "A")))
  (is (= "A100" (fuzzy/encode "Ab")))
  (is (= "A200" (fuzzy/encode "Ac")))
  (is (= "C123" (fuzzy/encode "CAaEeIiOoUuHhYybcd")))
  (is (= "C123" (fuzzy/encode "CAaEeIiOoUuHhYybcdkfsdjklsfdjkfsdjfsdkjfsdkfjsdkfsjk;sflajoweiuiowejrwekllksnwhjksdfnmsdfkpwipwj'kwer'dsfjkldfjsp")))

  (doseq [[k v] encodings]
    (is (= (fuzzy/encode k) v))))

(deftest test-similar-sounds
  (is (= ["S532" "S532"] (map fuzzy/encode ["Soundex" "Sownteks"])))

  (is (= ["R163" "R163"] (map fuzzy/encode ["Robert" "Rupert"])))
  
  (is (= ["E251" "E251" "E251" "E251" "E251"]
         (map fuzzy/encode ["Example" "Ekzampul" "Ekzampull" "exzampull" "exzampull    "])))

  (is (= ["M200" "M200" "M200" "M200" "M200"] (map fuzzy/encode ["Mike" "Maick" "Maiku" "Mike," " ,Mike"]))))

(deftest test-numeric-results
  (is (apply not= (map fuzzy/encode ["Fogus" "Phogus" "Phogas" "Foegas"])))
  (is (apply = (map #(fuzzy/encode % :numeric? true) ["Fogus" "Phogus" "Phogas" "Foegas"]))))
