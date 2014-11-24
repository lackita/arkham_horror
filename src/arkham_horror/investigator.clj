(ns arkham-horror.investigator)

(defn get-smaller-stat [{sanity :maximum-sanity
                         stamina :maximum-stamina}]
  (if (> sanity stamina)
    :maximum-sanity
    :maximum-stamina))

(defn devoured? [player]
  (->> [:maximum-sanity :maximum-stamina]
       (map player)
       (not-any? zero?)))

(defn reduce-sanity-or-stamina [player]
  (update-in player [(get-smaller-stat player)] dec))

(defn remove-devoured [players]
  (filter devoured? players))

(defn resolve-ancient-one-attack [players]
  (->> players
       (map reduce-sanity-or-stamina)
       remove-devoured))

(defn attack [game])

(defn make [config]
  config)
