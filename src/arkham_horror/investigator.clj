(ns arkham-horror.investigator
  (:require [arkham-horror.stat :as stat]))

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

(defn make [config]
  config)

(defn focus [investigator deltas]
  {:pre [(<= (apply + (vals deltas))
             (investigator :focus))]}
  (-> investigator
      (stat/speed-sneak-slider (or (deltas :speed-sneak) 0))
      (stat/fight-will-slider  (or (deltas :fight-will)  0))
      (stat/lore-luck-slider   (or (deltas :lore-luck)   0))))
