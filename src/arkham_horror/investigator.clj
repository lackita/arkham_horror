(ns arkham-horror.investigator
  (:require [arkham-horror.stat :as stat]))

(defn devoured? [investigator]
  (->> [:maximum-sanity :maximum-stamina]
       (map investigator)
       (not-any? zero?)))

(defn reduce-max-sanity-or-stamina [investigator]
  (update-in investigator [(stat/get-smaller investigator)] dec))

(defn focus [investigator deltas]
  {:pre [(<= (apply + (vals deltas))
             (investigator :focus))]}
  (-> investigator
      (stat/speed-sneak-slider (or (deltas :speed-sneak) 0))
      (stat/fight-will-slider  (or (deltas :fight-will)  0))
      (stat/lore-luck-slider   (or (deltas :lore-luck)   0))
      (update-in [:focus] #(apply - % (vals deltas)))))

(defn reset-focus [investigator]
  (assoc investigator :focus 2))

(defn items [investigator]
  (investigator :items))

(defn init [investigator config]
  (-> investigator
      (stat/set-speed (config :speed))
      (stat/set-sneak (- 4 (config :speed)))
      (stat/set-fight (config :fight))
      (stat/set-will  (- 5 (config :fight)))
      (stat/set-lore  (config :lore))
      (stat/set-luck  (- 6 (config :lore)))))

(defn make [name]
  {:speed (stat/make 1 1 4)
   :sneak (stat/make 0 0 3)
   :fight (stat/make 2 2 5)
   :will  (stat/make 0 0 3)
   :lore  (stat/make 1 1 4)
   :luck  (stat/make 2 2 5)
   :focus 2
   :items [{:name ".38 Revolver" :combat-modifier 3}
           {:name "Bullwhip" :combat-modifier 1}]
   :maximum-sanity 3
   :maximum-stamina 7})
