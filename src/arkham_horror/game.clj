(ns arkham-horror.game
  (:require [arkham-horror.ancient-one :as ancient-one]))

(defn lost? [board]
  (and (= (ancient-one/name (board :ancient-one)) "Azathoth")
       (ancient-one/awakened? (board :ancient-one))))
