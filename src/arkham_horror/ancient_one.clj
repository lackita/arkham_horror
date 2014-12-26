(ns arkham-horror.ancient-one
  (:refer-clojure :exclude [name])
  (:require [arkham-horror.investigator :as investigator]))

(defn use-power [name investigators]
  (when (= name "Cthulu")
    (doseq [investigator investigators]
      (investigator/decrement-maximum-sanity investigator)
      (investigator/decrement-maximum-stamina investigator))))

(defn make [name investigators]
  (use-power name investigators)
  (ref {:name name
        :doom-track 0
        :difficulty (count investigators)}))

(defn name [ancient-one]
  (@ancient-one :name))

(defn maximum-doom-track [ancient-one]
  (case (name ancient-one)
    "Cthulu"   13
    "Azathoth" 14))

(defn doom-track [ancient-one]
  (@ancient-one :doom-track))

(defn advance-doom-track [ancient-one]
  (when (< (doom-track ancient-one) (maximum-doom-track ancient-one))
    (alter ancient-one update-in [:doom-track] inc)))

(defn retract-doom-track [ancient-one]
  (when (> (doom-track ancient-one) 0)
    (alter ancient-one update-in [:doom-track] dec)))

(defn difficulty [ancient-one]
  (@ancient-one :difficulty))

(defn awakened? [ancient-one]
  (@ancient-one :awakened))

(defn awaken [ancient-one]
  {:pre [(not (:defeated @ancient-one))
         (not (awakened? ancient-one))]}
  (alter ancient-one assoc :awakened true)
  (alter ancient-one assoc :doom-track (maximum-doom-track ancient-one)))

(defn victorious? [ancient-one]
  (and (= (name ancient-one) "Azathoth")
       (awakened? ancient-one)))

(defn defeat [ancient-one]
  (alter ancient-one assoc :defeated true))
