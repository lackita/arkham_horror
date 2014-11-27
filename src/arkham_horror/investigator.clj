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

(defn make [config]
  config)

(def speed :speed)
(def sneak :sneak)
(def fight :fight)
(def will  :will)
(def lore  :lore)
(def luck  :luck)

(defn slider [ascending descending]
  #(merge-with + %1 {ascending %2
                     descending (- %2)}))
(def speed-sneak-slider (slider :speed :sneak))
(def fight-will-slider  (slider :fight :will))
(def lore-luck-slider   (slider :lore :luck))
