(ns arkham-horror.ancient-one-test
  (:require [clojure.test :refer :all]
            [arkham-horror.game :as game]
            [arkham-horror.ancient-one :refer :all]))

(deftest random-test
  (is (available (random)))
  (is (= (set (loop [ancient-ones []
                     remaining 100]
                (if (zero? remaining)
                  ancient-ones
                  (recur (conj ancient-ones (random))  (dec remaining)))))
         available)))

(deftest awakened?-test
  (is (awakened? (awaken (game/make {:ancient-one :cthulu}))))
  (is (not (awakened? (game/make {:ancient-one :cthulu}))))
  (is (awakened? (awaken (game/make {:ancient-one :azathoth}))))
  (is (not (awakened? (game/make {:ancient-one :azathoth})))))
