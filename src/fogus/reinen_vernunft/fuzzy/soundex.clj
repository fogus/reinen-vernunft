(ns fogus.reinen-vernunft.fuzzy.soundex
  "I came across the Soundex algorithm when researching the retro KAMAS outlining application.
  Soundex is a phonetic algorithm for indexing words by sound."
  (:require [clojure.string :as string]))

(def ^:private ^:const IGNORE Long/MAX_VALUE)
(def ^:private ^:const SKIP Long/MIN_VALUE)

;; TODO: is it better to convert to upcase or account for lowercase below?

(def ^:private en-ch->code
  {\B 1, \F 1, \P 1, \V 1
   \C 2, \G 2, \J 2, \K 2, \Q 2, \S 2, \X 2, \Z 2
   \D 3, \T 3
   \L 4
   \M 5, \N 5
   \R 6
   \A IGNORE \E IGNORE \I IGNORE \O IGNORE \U IGNORE \Y IGNORE
   \H SKIP \W SKIP})

(defn- en-alpha-only [x]
  (clojure.string/replace x #"[^A-Za-z]" ""))

(defn- handle-skips
  [processed-word]
  (filter #(not= SKIP %) processed-word))

(defn- drop-similar-head
  [encoding first-letter]
  (drop-while #(= % (en-ch->code first-letter)) encoding))

(defn- drop-ignore-codes [encoding]
  (filter #(not= IGNORE %) encoding))

(defn- assemble-soundex [first-letter preprocessed-code]
  (->> (-> preprocessed-code
           handle-skips
           (drop-similar-head first-letter)
           dedupe
           drop-ignore-codes
           (concat (repeat 0)))
       (take 3)
       (apply str)
       (str first-letter)))

(defn encode
  "Soundex is an algorithm for creating indices for words based on their
  English pronunciation. Homophones are encoded such that words can be matched
  despite minor differences in spelling. Example, the words \"Ashcraft\" and
  \"Ashcroft\" are both encoded as the same soundex code \"A261\".

  This function accepts the following keyword arguments:

  :numeric? -> true numerically encodes the entire word rather than using
  the default soundex letter prefix."
  [word & {:keys [numeric?] :as opts}]
  (let [word (string/upper-case (en-alpha-only word))
        first-letter (first word)
        result (assemble-soundex first-letter (map en-ch->code (rest word)))]
    (if numeric?
      (apply str (en-ch->code first-letter) (rest result))
      result)))

