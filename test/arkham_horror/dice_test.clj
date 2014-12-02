(ns arkham-horror.dice-test
  (:require [clojure.test :refer :all]
            [arkham-horror.dice :as dice]
            [arkham-horror.game :as game]))

(defn loaded-game [pips]
  (game/make {:ancient-one :cthulu
              :dice (dice/loaded pips)}))
(deftest combat-check-test
  (is (= (dice/combat-check (loaded-game 6) 1) [6]))
  (is (= (dice/combat-check (loaded-game 5) 1) [5]))
  (is (= (dice/combat-check (loaded-game 4) 1) [4])))
