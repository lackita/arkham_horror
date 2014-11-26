(ns arkham-horror.investigators
  (:require [arkham-horror.investigator :as investigator]
            [arkham-horror.doom-track :as doom-track]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.dice :as dice]))

(defn remove-devoured [investigators]
  (filter investigator/devoured? investigators))

(defn resolve-ancient-one-attack [{investigators :investigators
                                   :as game}]
  (assoc game :investigators (->> investigators
                                  (map investigator/reduce-sanity-or-stamina)
                                  remove-devoured)))
