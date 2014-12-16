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
            [arkham-horror.structure :as structure]
            [arkham-horror.help :as help]))

(def started-attack-game (-> (game/make {:ancient-one "Cthulu"
                                         :investigators ["Monterey Jack"]})
                             phase/start-init
                             (game/init-investigator {:speed 2 :fight 5 :lore 2})
                             phase/end-init
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

(deftest message-test
  (is (= (help/get-message rigged-game) "Attack\nDoom track: 13"))
  (is (= (help/get-message (combat/accept-roll (combat/investigator-attack rigged-game)))
         "Attack\nDoom track: 10")))
