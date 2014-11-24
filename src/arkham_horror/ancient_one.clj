(ns arkham-horror.ancient-one
  (:require [arkham-horror.player :as player]
            [arkham-horror.players :as players]
            [arkham-horror.doom-track :as doom-track]))

(def available #{:azathoth :cthulu})

(defn random []
  (first (shuffle available)))

(defmulti awaken :ancient-one)
(defmethod awaken :azathoth [game]
  (assoc game :players []))
(defmethod awaken :default [game]
  (doom-track/fill game))

(defn attack [game]
  (-> game
      players/resolve-ancient-one-attack
      doom-track/advance))
