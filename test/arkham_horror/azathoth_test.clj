(ns arkham-horror.azathoth-test
  (:require [clojure.test :refer :all]
            [arkham-horror.game :as game]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.structure :as structure]))

(def azathoth-game (game/make {:ancient-one "Azathoth"
                               :investigators ["Monterey Jack"]}))

(deftest ends-world-test
  (is (game/lost? (ancient-one/awaken azathoth-game)))
  (is (not (game/lost? azathoth-game))))

(deftest doom-track-test
  (is (= (doom-track/capacity (structure/get-path azathoth-game [ancient-one doom-track])) 14)))
