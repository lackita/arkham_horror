(ns arkham-horror.setup-test
  (:require [clojure.test :refer :all]
            [arkham-horror.setup :as setup]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.game :as game]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.combat :as combat]
            [arkham-horror.investigator.dice :as dice]
            [arkham-horror.stat :as stat]))

(def active-game (setup/begin :cthulu ["Monterey Jack"]))
(deftest begin-test
  (is (= (:investigators active-game) [(investigator/make "Monterey Jack")])))

(def init-game (setup/init active-game [{:speed 2 :fight 2 :lore 2}]))
(deftest init-test
  (is (= (:investigators init-game)
         [(investigator/init (investigator/make "Monterey Jack")
                             {:speed 2 :fight 2 :lore 2})])))

(def awakened-game (setup/awaken (setup/begin :cthulu ["Monterey Jack"])))
(deftest awaken-test
  (is (ancient-one/awakened? (ancient-one/get awakened-game))))

(def focused-game (setup/focus awakened-game [{:fight-will 2}]))
(deftest focus-test
  (is (= (map stat/fight (:investigators focused-game)) [4])))

(def attack-started-game (combat/start-attack awakened-game))
(def rigged-game (combat/start-attack
                  (ancient-one/awaken
                   (setup/init
                    (game/make {:ancient-one :cthulu
                                :investigators ["Monterey Jack"]
                                :dice (dice/make 6)})
                    [{:speed 0 :fight 19 :lore 0}]))))
(deftest game-status-test
  (is (= (setup/game-status active-game) "Initialize investigators"))
  (is (= (setup/game-status init-game) "Awaken ancient one"))
  (is (= (setup/game-status awakened-game) "Refresh investigators"))
  (is (= (setup/game-status attack-started-game) "Attack\nDoom track: 13"))
  (is (= (setup/game-status (combat/investigator-attack rigged-game))
         "Roll: 6 6 6 6 6 6 6 6 6 6 6 6 6 6 6 6 6"))
  ;; (is (= (setup/game-status (combat/accept-roll
  ;;                            (combat/investigator-attack attack-started-game))) "Defend"))
  ;; (is (= (setup/game-status (ancient-one/awaken (setup/begin :azathoth []))) "You lose"))
  ;; (is (= (setup/game-status (combat/accept-roll
  ;;                            (combat/investigator-attack rigged-game))) "You win"))
  )
