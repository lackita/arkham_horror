(ns arkham-horror.setup
  (:require [arkham-horror.phase :as phase]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.structure :as structure]))

(defn focus [{investigators :investigators :as active-game} deltas]
  (assoc active-game :investigators (map investigator/focus investigators deltas)))

(defn exhaust-item [game n]
  (structure/update-path game [phase investigator]
                         #(investigator/exhaust-item % n)))
