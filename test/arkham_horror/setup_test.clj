(ns arkham-horror.setup-test
  (:require [clojure.test :refer :all]
            [arkham-horror.setup :as setup]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.game :as game]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.combat :as combat]
            [arkham-horror.investigator.dice :as dice]
            [arkham-horror.stat :as stat]))

(def active-game (game/make {:ancient-one :cthulu
                             :investigators ["Monterey Jack"]}))
(deftest begin-test
  (is (= (:investigators active-game) [(investigator/make "Monterey Jack")])))

(def init-game (setup/init active-game [{:speed 2 :fight 2 :lore 2}]))
(deftest init-test
  (is (= (:investigators init-game)
         [(investigator/init (investigator/make "Monterey Jack")
                             {:speed 2 :fight 2 :lore 2})])))

(def awakened-game (setup/awaken active-game))
(deftest awaken-test
  (is (ancient-one/awakened? (ancient-one/get awakened-game))))

(def focused-game (setup/focus awakened-game [{:fight-will 2}]))
(deftest focus-test
  (is (= (map stat/fight (:investigators focused-game)) [4])))
