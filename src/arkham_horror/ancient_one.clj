(ns arkham-horror.ancient-one
  (:require [arkham-horror.player :as player]))

(defmulti awaken :ancient-one)
(defmethod awaken :azathoth [game]
  (assoc game :players []))
(defmethod awaken :default [game]
  game)

(defn attack [{players :players
               :as game}]
  (assoc game
    :players (player/resolve-ancient-one-attack players)))
