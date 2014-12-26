(ns arkham-horror.final-battle
  (:require [arkham-horror.investigator :as investigator]
            [arkham-horror.ancient-one  :as ancient-one]))

(defn ancient-one-attack [ancient-one investigators]
  {:pre [(not-any? investigator/pending-decision investigators)
         (not-every? investigator/defeated? investigators)]}
  (doseq [investigator (filter (complement investigator/defeated?) investigators)]
    (alter investigator assoc :decision
           (fn [investigator decision]
             (when (= decision :sanity)
               (investigator/decrement-maximum-sanity investigator))
             (when (= decision :stamina)
               (investigator/decrement-maximum-stamina investigator))))))

(defn investigator-attack [investigator ancient-one successes]
  (dotimes [n (max 0 (- successes 6))]
    (ancient-one/retract-doom-track ancient-one)))

(defn refresh [ancient-one]
  (ancient-one/advance-doom-track ancient-one))
