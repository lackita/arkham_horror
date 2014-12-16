(ns arkham-horror.investigator
  (:refer-clojure :exclude [get])
  (:require [arkham-horror.stat :as stat]
            [arkham-horror.investigator.dice :as dice]
            [arkham-horror.investigator.items :as items]))

(defn get [phase]
  (phase :current-investigator))

(defn update [phase function]
  (update-in phase [:current-investigator] function))

(defn make
  ([name] (make name :random))
  ([name pips]
     {:name name
      :speed (stat/make :speed 1 4 :ascending)
      :sneak (stat/make :sneak 0 3 :descending)
      :fight (stat/make :fight 2 5 :ascending)
      :will  (stat/make :will 0 3 :descending)
      :lore  (stat/make :lore 1 4 :ascending)
      :luck  (stat/make :luck 2 5 :descending)
      :focus 2
      :items [{:name ".38 Revolver" :combat-modifier 3}
              {:name "Bullwhip" :combat-modifier 1}]
      :maximum-sanity 3
      :maximum-stamina 7
      :dice (dice/make pips)}))

(defn describe [investigator]
  (clojure.string/join "\n" (cons (str (investigator :name))
                                  (map (comp stat/describe investigator)
                                       [:speed :sneak :fight :will :lore :luck]))))

(defn make-all [investigators dice]
  (map #(make % (or dice :random)) investigators))

(defn set-all [game investigators dice]
  (assoc game :investigators (make-all investigators dice)))

(defn update-all [game function]
  (update-in game [:investigators] #(map function %)))

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

(defn init [investigator config]
  (-> investigator
      (stat/set-speed (config :speed))
      (stat/set-sneak (- 4 (config :speed)))
      (stat/set-fight (config :fight))
      (stat/set-will  (- 5 (config :fight)))
      (stat/set-lore  (config :lore))
      (stat/set-luck  (- 6 (config :lore)))))

(defn devoured? [investigator]
  (or (->> [:maximum-sanity :maximum-stamina]
           (map investigator)
           (some zero?))
      (investigator :devoured)))

(defn devour [investigator]
  (assoc investigator :devoured true))

(defn refresh [investigator]
  (items/update investigator items/refresh))

(defn exhaust-item [investigator n]
  {:pre [(not (:exhausted (nth (items/get investigator) n)))]}
  (-> investigator
      (items/update #(let [[before [current & after]] (split-at n %)]
                       (concat before [(assoc current :exhausted true)] after)))
      (dice/update dice/reroll-lowest)))
