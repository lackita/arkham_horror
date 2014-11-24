(ns arkham-horror.ancient-one
  (:require [arkham-horror.doom-track :as doom-track]))

(def available #{:azathoth :cthulu})

(defn random []
  (first (shuffle available)))

(defmulti awaken :ancient-one)
(defmethod awaken :azathoth [game]
  (assoc game :investigators []))
(defmethod awaken :default [game]
  (doom-track/fill game))

(defn combat-modifier [game]
  -6)
