(ns arkham-horror.game
  (:require [arkham-horror.doom-track :as doom-track]
            [arkham-horror.ancient-one :as ancient-one]))

(defn make [config]
  (merge {:doom-track 0}
         config))

(defn lost? [game]
  (or (game :lost) (empty? (game :investigators))))

(defn won? [game]
  (ancient-one/defeated? game))

(defn over? [game]
  (or (lost? game)
      (won? game)))

(defn ending-message [game]
  (cond (lost? game) (str (game :ancient-one) " has ended the world")
        (won? game)  (str (game :ancient-one) " has been defeated")
        :else        (str game " ended unexpectedly")))
