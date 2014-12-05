(ns arkham-horror.game
  (:require [arkham-horror.doom-track :as doom-track]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.dice :as dice]
            [arkham-horror.investigator :as investigator]))

(defn make [config]
  (ancient-one/set (dice/set (merge {:doom-track 0} config)
                             (dice/make (or (config :dice) :random)))
                   (ancient-one/make (config :ancient-one))))

(defn lost? [{investigators :investigators}]
  (every? investigator/devoured? investigators))

(defn won? [game]
  (and (not (lost? game))
       (ancient-one/defeated? game)))

(defn over? [game]
  (or (lost? game)
      (won? game)))
