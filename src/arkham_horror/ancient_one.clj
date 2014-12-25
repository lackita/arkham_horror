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
  (ref {:name name}))

(defn name [ancient-one]
  (@ancient-one :name))

(defn awakened? [ancient-one]
  (@ancient-one :awakened))

(defn awaken [ancient-one]
  {:pre [(not (:defeated @ancient-one))
         (not (awakened? ancient-one))]}
  (alter ancient-one assoc :awakened true))

(defn victorious? [ancient-one]
  (and (= (name ancient-one) "Azathoth")
       (awakened? ancient-one)))

(defn defeat [ancient-one]
  (alter ancient-one assoc :defeated true))

(defn attack [ancient-one investigators]
  {:pre [(not-any? investigator/pending-decision investigators)
         (not-every? investigator/defeated? investigators)]}
  (doseq [investigator investigators]
    (alter investigator assoc :decision
           (fn [investigator decision]
             (when (= decision :sanity)
               (investigator/decrement-maximum-sanity investigator))
             (when (= decision :stamina)
               (investigator/decrement-maximum-stamina investigator))))))
