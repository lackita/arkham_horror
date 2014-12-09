(ns arkham-horror.combat-test
  (:require [clojure.test :refer :all]
            [arkham-horror.phase :as phase]
            [arkham-horror.setup :as setup]
            [arkham-horror.combat :as combat]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.investigator.dice :as dice]
            [arkham-horror.structure :as structure]))

(def awakened-game (setup/awaken (setup/init (setup/begin :cthulu ["Monterey Jack"])
                                             [{:speed 2 :fight 5 :lore 2}])))
(def started-attack-game (combat/start-attack awakened-game))
(def ended-attack-game (combat/accept-roll
                        (combat/investigator-attack started-attack-game)))
(deftest in-combat?-test
  (is (not (combat/in-combat? awakened-game)))
  (is (combat/in-combat? started-attack-game))
  (is (combat/in-combat? ended-attack-game))
  (is (not (combat/in-combat? (combat/end-attack ended-attack-game)))))

(def rigged-game (phase/update started-attack-game (fn [phase]
                                                     (update-in phase [:current-investigator]
                                                                #(dice/set % (dice/make 6))))))
(deftest investigator-attack-test
  (is (structure/get-path (combat/investigator-attack started-attack-game) [phase investigator]))
  (is (= (doom-track/level (structure/get-path (combat/investigator-attack rigged-game)
                                               [ancient-one doom-track]))
         13)))

(deftest pending-roll-test
  (is (= (dice/pending-roll (structure/get-path (combat/investigator-attack rigged-game)
                                                [phase investigator dice]))
         [6 6 6])))

(defn roll-badly-but-lucky [game]
  (phase/update (combat/investigator-attack (phase/update game (fn [phase] (update-in phase
                                                                                      [:current-investigator]
                                                                                      (fn [investigator]
                                                                                        (dice/update investigator #(merge % {:value 1})))))))
                (fn [phase]
                  (update-in phase [:current-investigator]
                             (fn [investigator]
                               (dice/update investigator #(merge % {:value 6})))))))
