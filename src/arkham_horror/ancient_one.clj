(ns arkham-horror.ancient-one
  (:refer-clojure :exclude [get set])
  (:require [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.investigator :as investigator]))

(def available #{:azathoth :cthulu})

(defn get [game]
  (game :ancient-one))

(defn set [game ancient-one]
  (assoc game :ancient-one ancient-one))

(defn update [game fn]
  (update-in game [:ancient-one] fn))

(defmulti rouse #(-> % :ancient-one :name))
(defmethod rouse :azathoth [game]
  (assoc game :investigators (map investigator/devour (game :investigators))))
(defmethod rouse :default [game]
  (doom-track/fill game))

(defn awaken [game]
  (assoc-in (rouse game)
            [:ancient-one :awakened] true))

(defn make [name]
  {:name name
   :doom-track (doom-track/make 0)})

(defn valid? [ancient-one]
  (available ancient-one))

(defn random []
  (first (shuffle available)))

(defn awakened? [ancient-one]
  (ancient-one :awakened))

(defn combat-modifier [ancient-one]
  -6)

(defn defeat [game]
  (update-in game [:ancient-one] #(doom-track/update % doom-track/empty)))

(defn defeated? [game]
  (and (awakened? (get game))
       (= (doom-track/level game) 0)))
