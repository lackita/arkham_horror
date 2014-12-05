(ns arkham-horror.game
  (:require [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.dice :as dice]
            [arkham-horror.investigator :as investigator]))

(defn make [config]
  (-> config
      (dice/set (dice/make (config :dice)))
      (ancient-one/set (ancient-one/make (config :ancient-one)))))

(defn lost? [{investigators :investigators}]
  (every? investigator/devoured? investigators))

(defn won? [game]
  (and (not (lost? game))
       (ancient-one/defeated? (ancient-one/get game))))

(defn over? [game]
  (or (lost? game)
      (won? game)))
