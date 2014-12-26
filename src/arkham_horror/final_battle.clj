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
               (investigator/decrement-maximum-stamina investigator)))))
  (ancient-one/advance-doom-track ancient-one))

(defn investigator-attack [investigator ancient-one successes]
  {:pre [(>= successes 0)]}
  (dotimes [n (/ (+ successes (or (@ancient-one :remainder) 0))
                 (ancient-one/difficulty ancient-one))]
    (ancient-one/retract-doom-track ancient-one))
  (alter ancient-one assoc :remainder (rem (max 0 successes)
                                           (ancient-one/difficulty ancient-one))))
