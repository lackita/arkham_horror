(ns arkham-horror.combat-test
  (:require [clojure.test :refer :all]
            [arkham-horror.setup :as setup]
            [arkham-horror.combat :as combat]))

(def awakened-game (setup/awaken (setup/begin :cthulu ["Monterey Jack"])))
(def started-combat-game (combat/start-attack awakened-game))
(def ended-combat-game (combat/investigator-attack started-combat-game))
(deftest in-combat?-test
  (is (not (combat/in-combat? awakened-game)))
  (is (combat/in-combat? started-combat-game))
  (is (not (combat/in-combat? ended-combat-game))))
