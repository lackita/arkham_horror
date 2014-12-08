(ns arkham-horror.investigator.items
  (:refer-clojure :exclude [get])
  (:require [arkham-horror.investigator.dice :as dice]))

(defn get [investigator]
  (investigator :items))

(defn update [investigator function]
  (update-in investigator [:items] function))

(defn refresh [items]
  (map #(assoc % :exhausted false) items))

(defn unexhausted-named [items name]
  (filter #(and (= name (:name %)) (not (:exhausted %))) items))

(defn exhauster [item]
  #(dice/update % dice/reroll-lowest))

(defn exhaust-first [items pred]
  {:items (loop [processed []
                 current (first items)
                 remaining (rest items)]
            (cond (and (pred current) (not (:exhausted current)))
                    (concat processed [(assoc current :exhausted true)] remaining)
                  (empty? remaining)
                    (throw (Throwable. "No items found"))
                  :else
                    (recur (conj processed current) (first remaining) (rest remaining))))
   :change #(dice/update % dice/reroll-lowest)})

(defn exhaust-first-named [items name]
  (:items (exhaust-first items #(= (:name %) name))))
