(ns arkham-horror.setup
  (:require [arkham-horror.phase :as phase]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.combat :as combat]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.investigator.dice :as dice]
            [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.structure :as structure]
            [arkham-horror.help :as help]))

(defn focus [{investigators :investigators :as active-game} deltas]
  (assoc active-game :investigators (map investigator/focus investigators deltas)))

(defn exhaust-item [game n]
  (let [game (structure/update-path game [phase investigator]
                                    #(investigator/exhaust-item % n))]
    (help/set-message game (->> (structure/get-path game [phase investigator dice])
                                dice/pending-roll
                                sort
                                (clojure.string/join " ")
                                (str "Roll: ")))))
