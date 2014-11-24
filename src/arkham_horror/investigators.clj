(ns arkham-horror.investigators
  (:require [arkham-horror.investigator :as investigator]))

(defn resolve-ancient-one-attack [{investigators :investigators
                                   :as game}]
  (assoc game :investigators (investigator/resolve-ancient-one-attack investigators)))
