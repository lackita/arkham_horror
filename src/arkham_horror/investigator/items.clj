(ns arkham-horror.investigator.items
  (:refer-clojure :exclude [get]))

(defn get [investigator]
  (investigator :items))

(defn update [investigator function]
  (update-in investigator [:items] function))

(defn refresh [items]
  (map #(assoc % :exhausted false) items))

(defn unexhausted-named [items name]
  (filter #(and (= name (:name %)) (not (:exhausted %))) items))

(defn exhaust-first-named [[item & items] name]
  (cond (and (= (item :name) name) (not (:exhausted item))) (cons (assoc item :exhausted true) items)
        (empty? items) (throw (Throwable. "No items named " name))
        :else (cons item (exhaust-first-named items name))))
