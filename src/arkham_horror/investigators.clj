(ns arkham-horror.investigators
  (:require [arkham-horror.investigator :as investigator]
            [arkham-horror.doom-track :as doom-track]))

(defn remove-devoured [investigators]
  (filter investigator/devoured? investigators))

(defn resolve-ancient-one-attack [{investigators :investigators
                                   :as game}]
  (assoc game :investigators (->> investigators
                                  (map investigator/reduce-max-sanity-or-stamina)
                                  remove-devoured)))

(defn devour-all [game]
  (assoc game :investigators []))
