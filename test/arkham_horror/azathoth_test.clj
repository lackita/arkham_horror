(ns arkham-horror.azathoth-test
  (:require [clojure.test :refer :all]
            [arkham-horror.game :as game]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.player :as player]))

(def azathoth-game (game/make {:ancient-one :azathoth
                               :players [(player/make {})]}))

(deftest ends-world-test
  (is (game/lost? (ancient-one/awaken azathoth-game)))
  (is (not (game/lost? azathoth-game))))
