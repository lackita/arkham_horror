(ns arkham-horror.investigator.items
  (:refer-clojure :exclude [get])
  (:require [arkham-horror.investigator.dice :as dice]))

(defn get [investigator]
  (investigator :items))

(defn update [investigator function]
  (update-in investigator [:items] function))

(defn refresh [items]
  (map #(assoc % :exhausted false) items))
