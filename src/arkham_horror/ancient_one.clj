(ns arkham-horror.ancient-one
  (:refer-clojure :exclude [get set])
  (:require [arkham-horror.doom-track :as doom-track]
            [arkham-horror.investigators :as investigators]))

(def available #{:azathoth :cthulu})

(defn get [game]
  (game :ancient-one))

(defn set [game ancient-one]
  (assoc game :ancient-one ancient-one))

(defn update [game fn]
  (update-in game [:ancient-one] fn))

(defn make [name]
  {:name name})

(defn valid? [ancient-one]
  (available ancient-one))

(defn random []
  (first (shuffle available)))

(defmulti rouse #(-> % :ancient-one :name))
(defmethod rouse :azathoth [game]
  (investigators/devour-all game))
(defmethod rouse :default [game]
  (doom-track/fill game))

(defn awaken [game]
  (assoc-in (rouse game)
    [:ancient-one :awakened] true))

(defn awakened? [ancient-one]
  (ancient-one :awakened))

(defn combat-modifier [ancient-one]
  -6)

(defn defeat [game]
  (doom-track/empty game))

(defn defeated? [game]
  (and (awakened? (get game))
       (= (doom-track/level game) 0)))
