(ns arkham-horror.ancient-one-test
  (:require [clojure.test :refer :all]
            [arkham-horror.ancient-one :refer :all]))

(deftest random-test
  (is (available (random)))
  (is (= (set (loop [ancient-ones []
                     remaining 100]
                (if (zero? remaining)
                  ancient-ones
                  (recur (conj ancient-ones (random))  (dec remaining)))))
         available)))
