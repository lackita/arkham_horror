(ns arkham-horror.combat-test
  (:require [clojure.test :refer :all]
            [arkham-horror.phase :as phase]
            [arkham-horror.setup :as setup]
            [arkham-horror.combat :as combat]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.investigator.dice :as dice]))

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

(def rigged-game (update-in started-attack-game [:phase :current-investigator]
                            #(dice/set % (dice/make 6))))
(deftest investigator-attack-test
  (is (phase/investigator (combat/investigator-attack started-attack-game)))
  (is (= (doom-track/level (doom-track/get (ancient-one/get (combat/investigator-attack rigged-game))))
         13)))

(deftest pending-roll-test
  (is (= (dice/pending-roll (dice/get (phase/investigator (combat/investigator-attack rigged-game)))) [6 6 6])))

(defn roll-badly-but-lucky [game]
  (dice/update (combat/investigator-attack (dice/update game #(merge % {:value 1})))
               #(merge % {:value 6})))

(def bad-roll-game (roll-badly-but-lucky started-attack-game))
(deftest bullwhip-test
  (is (= (sort (dice/pending-roll (dice/get (phase/investigator (combat/bullwhip bad-roll-game)))))
         [1 1 6]))
  (is (= (sort (dice/pending-roll (dice/get
                                   (phase/investigator
                                    (combat/bullwhip
                                     (roll-badly-but-lucky
                                      (combat/start-attack
                                       (combat/end-attack
                                        (combat/bullwhip bad-roll-game)))))))))
         [1 1 6]))
  (is (= (sort (dice/pending-roll
                (dice/get
                 (phase/investigator
                  (combat/bullwhip
                   (combat/bullwhip
                    (update-in bad-roll-game
                               [:phase :current-investigator]
                               #(update-in % [:items]
                                           (fn [items]
                                             (conj items {:name "Bullwhip"
                                                          :combat-modifier 1}))))))))))
         [1 6 6])))
