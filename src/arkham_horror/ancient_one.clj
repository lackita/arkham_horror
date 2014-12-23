(ns arkham-horror.ancient-one)

(defn awaken [ancient-one]
  {:pre [(not (:defeated @ancient-one))]}
  nil)

(defn defeat [board]
  (dosync (alter (board :ancient-one) assoc :defeated true)))
