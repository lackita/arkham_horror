(ns arkham-horror.ancient-one
  (:refer-clojure :exclude [name])
  (:require [arkham-horror.investigator :as investigator]))

(defn make [name investigators]
  (when (= name "Cthulu")
    (doseq [investigator investigators]
      (investigator/decrement-maximum-sanity investigator)
      (investigator/decrement-maximum-stamina investigator)))
  (ref {:name name}))

(defn name [ancient-one]
  (@ancient-one :name))

(defn awakened? [ancient-one]
  (@ancient-one :awakened))

(defn awaken [ancient-one]
  {:pre [(not (:defeated @ancient-one))
         (not (awakened? ancient-one))]}
  (alter ancient-one assoc :awakened true))

(defn defeat [ancient-one]
  (alter ancient-one assoc :defeated true))

(defn attack [ancient-one investigators]
  (doseq [investigator investigators]
    (alter investigator assoc :decision [nil])))
