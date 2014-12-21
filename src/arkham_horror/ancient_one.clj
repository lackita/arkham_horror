(ns arkham-horror.ancient-one)

(defn awaken [board]
  {:pre [(not (:defeated @(board :ancient-one)))]}
  nil)

(defn defeat [board]
  (dosync (alter (board :ancient-one) assoc :defeated true)))
