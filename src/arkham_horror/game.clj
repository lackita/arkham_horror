(ns arkham-horror.game
  (:require [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.investigator :as investigator]))

(defn lost? [board]
  (or (and (= (ancient-one/name (board :ancient-one)) "Azathoth")
           (ancient-one/awakened? (board :ancient-one)))
      (every? #(or (zero? (investigator/maximum-sanity %))
                   (zero? (investigator/maximum-stamina %)))
              (board :investigators))))
