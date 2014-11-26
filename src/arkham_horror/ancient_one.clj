(ns arkham-horror.ancient-one
  (:require [arkham-horror.doom-track :as doom-track]
            [arkham-horror.investigators :as investigators]))

(def available #{:azathoth :cthulu})

(defn valid? [game]
  (available (game :ancient-one)))

(defn random []
  (first (shuffle available)))

(defmulti rouse :ancient-one)
(defmethod rouse :azathoth [game]
  (investigators/devour-all game))
(defmethod rouse :default [game]
  (doom-track/fill game))

(defn awaken [game]
  (assoc (rouse game)
    :awakened true))

(defn awakened? [game]
  (game :awakened))

(defn combat-modifier [game]
  -6)

(defn defeat [game]
  (doom-track/empty game))

(defn defeated? [game]
  (and (awakened? game)
       (= (doom-track/level game) 0)))
