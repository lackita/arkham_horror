(ns arkham-horror.combat-test
  (:require [clojure.test :refer :all]
            [arkham-horror.game :as game]
            [arkham-horror.phase :as phase]
            [arkham-horror.setup :as setup]
            [arkham-horror.combat :as combat]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.investigator.dice :as dice]
            [arkham-horror.structure :as structure]))

(def started-attack-game (-> (game/make {:ancient-one "Cthulu"
                                         :investigators ["Monterey Jack"]})
                             (setup/init [{:speed 2 :fight 5 :lore 2}])
                             ancient-one/awaken
                             combat/start))
(def rigged-game (structure/update-path started-attack-game [phase investigator dice]
                                        #(merge % {:value 6})))
(deftest investigator-attack-test
  (is (structure/get-path (combat/investigator-attack started-attack-game) [phase investigator]))
  (is (= (doom-track/level (structure/get-path (combat/investigator-attack rigged-game)
                                               [ancient-one doom-track]))
         13)))

(deftest pending-roll-test
  (is (= (dice/pending-roll (structure/get-path (combat/investigator-attack rigged-game)
                                                [phase investigator dice]))
         [6 6 6])))
