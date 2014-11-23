(ns arkham-horror.game)

(defn make [config]
  config)

(defn lost? [game]
  (or (game :lost) (empty? (game :players))))
