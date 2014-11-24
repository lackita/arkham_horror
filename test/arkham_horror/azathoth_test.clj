(ns arkham-horror.azathoth-test
  (:require [clojure.test :refer :all]
            [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.doom-track :as doom-track]))

(def azathoth-game (game/make {:ancient-one :azathoth
                               :investigators [(investigator/make {})]}))

(deftest ends-world-test
  (is (game/lost? (ancient-one/awaken azathoth-game)))
  (is (not (game/lost? azathoth-game))))

(deftest doom-track-test
  (is (= (doom-track/capacity azathoth-game) 14)))
