(ns arkham-horror.combat-test
  (:require [clojure.test :refer :all]
            [arkham-horror.phase :as phase]
            [arkham-horror.setup :as setup]
            [arkham-horror.combat :as combat]
            [arkham-horror.doom-track :as doom-track]
            [arkham-horror.dice :as dice]))

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

(def rigged-game (assoc started-attack-game
                   :dice (dice/loaded 6)))
(deftest investigator-attack-test
  (is (phase/investigator (combat/investigator-attack started-attack-game)))
  (is (= (doom-track/level (combat/investigator-attack rigged-game))
         13)))

(deftest pending-roll-test
  (is (= (combat/pending-roll (combat/investigator-attack rigged-game)) [6 6 6])))

(defn roll-badly-but-lucky [game]
  (assoc (combat/investigator-attack (assoc game :dice (dice/loaded 1)))
    :dice (dice/loaded 6)))

(def bad-roll-game (roll-badly-but-lucky started-attack-game))
(deftest bullwhip-test
  (is (= (sort (combat/pending-roll (combat/bullwhip bad-roll-game)))
         [1 1 6]))
  (is (= (sort (combat/pending-roll (combat/bullwhip
                                     (roll-badly-but-lucky
                                      (combat/start-attack
                                       (combat/end-attack
                                        (combat/bullwhip bad-roll-game)))))))
         [1 1 6]))
  (is (= (sort (combat/pending-roll (combat/bullwhip (combat/bullwhip (update-in bad-roll-game
                                                                                 [:investigators]
                                                                                 #(map (fn [investigator]
                                                                                         (update-in investigator
                                                                                                    [:items]
                                                                                                    (fn [items]
                                                                                                      (conj items {:name "Bullwhip" :combat-modifier 1}))))
                                                                                       %))))))
         [1 6 6])))
