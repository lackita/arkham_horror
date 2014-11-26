(ns arkham-horror.ancient-one
  (:require [arkham-horror.doom-track :as doom-track]
            [arkham-horror.investigators :as investigators]))

(def available #{:azathoth :cthulu})

(defn random []
  (first (shuffle available)))

(defmulti rouse :ancient-one)
(defmethod rouse :azathoth [game]
  (assoc game :investigators []))
(defmethod rouse :default [game]
  (doom-track/fill game))

(defn awaken [game]
  (assoc (rouse game)
    :awakened true))

(defn awakened? [game]
  (game :awakened))

(defn combat-modifier [game]
  -6)
