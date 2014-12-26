(ns arkham-horror.game
  (:require [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.investigator :as investigator]))

(defn lost? [board]
  (or (ancient-one/victorious? (board :ancient-one))
      (every? investigator/defeated? (board :investigators))))

(defn won? [{ancient-one :ancient-one}]
  (and (ancient-one/awakened? ancient-one)
       (= (ancient-one/doom-track ancient-one) 0)))
