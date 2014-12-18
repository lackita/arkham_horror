(ns arkham-horror.game
  (:require [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.investigator.dice :as dice]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.phase :as phase]
            [arkham-horror.structure :as structure]
            [arkham-horror.investigator.dice :as dice]
            [arkham-horror.combat :as combat]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.ancient-one.doom-track :as doom-track]))

(defn make [config]
  (-> config
      (ancient-one/set (ancient-one/make (or (config :ancient-one)
                                             (ancient-one/random))))
      (investigator/set-all (config :investigators) (config :dice))))

(defn lost? [game]
  (every? investigator/devoured? (if (phase/get game)
                                   (phase/all-investigators (phase/get game))
                                   (game :investigators))))

(defn won? [game]
  (and (not (lost? game))
       (ancient-one/defeated? (ancient-one/get game))))

(defn over? [game]
  (or (lost? game)
      (won? game)))

(defn init-investigator [game stats]
  (phase/update game #(phase/init-investigator % stats)))

(defn focus-investigator [game deltas]
  (phase/update game #(phase/focus-investigator % deltas)))

(defn advance-phase [game]
  (phase/update game phase/advance))
