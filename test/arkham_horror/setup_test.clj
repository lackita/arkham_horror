(ns arkham-horror.setup-test
  (:require [clojure.test :refer :all]
            [arkham-horror.phase :as phase]
            [arkham-horror.setup :as setup]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.game :as game]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.combat :as combat]
            [arkham-horror.investigator.dice :as dice]
            [arkham-horror.stat :as stat]))

(def active-game (game/make {:ancient-one "Cthulu"
                             :investigators ["Monterey Jack"]}))
(deftest begin-test
  (is (= (:investigators active-game) [(investigator/make "Monterey Jack")])))

(def init-game (phase/end
                (game/init-investigator (phase/start active-game)
                                        {:speed 2 :fight 2 :lore 2})))
(deftest init-test
  (is (= (:investigators init-game)
         [(investigator/init (investigator/make "Monterey Jack")
                             {:speed 2 :fight 2 :lore 2})])))

(def awakened-game (ancient-one/awaken init-game))
(def focused-game (setup/focus awakened-game [{:fight-will 2}]))
(deftest focus-test
  (is (= (map stat/fight (:investigators focused-game)) [4])))
