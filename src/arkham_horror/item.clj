(ns arkham-horror.item
  (:refer-clojure :exclude [name]))

(defn make [deck name]
  {:deck deck
   :name name})

(defn name [item]
  (item :name))

(defn deck [item]
  (item :deck))
