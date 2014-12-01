(ns arkham-horror.combat-test
  (:require [clojure.test :refer :all]
            [arkham-horror.combat :as combat]
            [arkham-horror.game :as game]
            [arkham-horror.doom-track :as doom-track]))

(deftest investigators-attack
  (is (= (doom-track/level (combat/investigators-attack
                            (game/make {:investigators []}) 0))
         0)))
