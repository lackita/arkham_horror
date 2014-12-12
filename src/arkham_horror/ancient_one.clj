(ns arkham-horror.ancient-one
  (:refer-clojure :exclude [get set])
  (:require [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.investigator :as investigator]))

(def available #{"Azathoth" "Cthulu"})

(defn get [game]
  (game :ancient-one))

(defn set [game ancient-one]
  (assoc game :ancient-one ancient-one))

(defn update [game fn]
  (update-in game [:ancient-one] fn))

(defmulti rouse #(-> % :ancient-one :name))
(defmethod rouse "Azathoth" [game]
  (assoc game :investigators (map investigator/devour (game :investigators))))
(defmethod rouse :default [game]
  (update game #(doom-track/update % doom-track/fill)))

(defn awaken [game]
  (assoc-in (rouse game)
            [:ancient-one :awakened] true))

(defmulti make identity)
(defmethod make "Cthulu" [name]
  {:name name
   :doom-track (doom-track/make 0 13)})
(defmethod make "Azathoth" [name]
  {:name name
   :doom-track (doom-track/make 0 14)})

(defn valid? [ancient-one]
  (available ancient-one))

(defn random []
  (first (shuffle available)))

(defn awakened? [ancient-one]
  (ancient-one :awakened))

(defn combat-modifier [ancient-one]
  -6)

(defn defeat [ancient-one]
  (doom-track/update ancient-one doom-track/empty))

(defn defeated? [ancient-one]
  (and (awakened? ancient-one)
       (= (doom-track/level (doom-track/get ancient-one)) 0)))
