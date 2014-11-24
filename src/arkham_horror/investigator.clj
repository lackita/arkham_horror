(ns arkham-horror.investigator)

(defn get-smaller-stat [{sanity :maximum-sanity
                         stamina :maximum-stamina}]
  (if (> sanity stamina)
    :maximum-sanity
    :maximum-stamina))

(defn devoured? [investigator]
  (->> [:maximum-sanity :maximum-stamina]
       (map investigator)
       (not-any? zero?)))

(defn reduce-sanity-or-stamina [investigator]
  (update-in investigator [(get-smaller-stat investigator)] dec))

(defn remove-devoured [investigators]
  (filter devoured? investigators))

(defn resolve-ancient-one-attack [investigators]
  (->> investigators
       (map reduce-sanity-or-stamina)
       remove-devoured))

(defn make [config]
  config)
