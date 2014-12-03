(ns arkham-horror.combat-test
  (:require [clojure.test :refer :all]
            [arkham-horror.setup :as setup]
            [arkham-horror.combat :as combat]))

(def awakened-game (setup/awaken (setup/begin :cthulu ["Monterey Jack"])))
(def started-attack-game (combat/start-attack awakened-game))
(def ended-attack-game (combat/accept-roll
                        (combat/investigator-attack started-attack-game)))
(deftest in-combat?-test
  (is (not (combat/in-combat? awakened-game)))
  (is (combat/in-combat? started-attack-game))
  (is (combat/in-combat? ended-attack-game))
  (is (not (combat/in-combat? (combat/end-attack ended-attack-game)))))
