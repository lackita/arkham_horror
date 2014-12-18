(ns arkham-horror.setup
  (:require [arkham-horror.phase :as phase]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.combat :as combat]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.investigator.dice :as dice]
            [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.structure :as structure]))

(defn focus [{investigators :investigators :as active-game} deltas]
  (assoc active-game :investigators (map investigator/focus investigators deltas)))

(defn exhaust-item [game n]
  (structure/update-path game [phase investigator]
                         #(investigator/exhaust-item % n)))
