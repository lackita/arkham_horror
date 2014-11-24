(ns arkham-horror.game)

(defn make [config]
  (merge {:doom-track 0}
         config))

(defn lost? [game]
  (or (game :lost) (empty? (game :investigators))))
