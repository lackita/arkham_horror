(ns arkham-horror.investigator.dice
  (:refer-clojure :exclude [get set])
  (:require [arkham-horror.phase :as phase]
            [arkham-horror.stat :as stat]))

(defn get [investigator]
  (investigator :dice))

(defn set [game dice]
  (assoc-in (assoc game :dice dice) [:phase :current-investigator :dice] dice))

(defn update [g f]
  (update-in g [:phase :current-investigator :dice] f))

(defn make [value]
  {:value (or value :random)})

(defmulti roll :value)
(defmethod roll :random [_]
  (inc (int (rand 6))))
(defmethod roll :default [dice]
  (dice :value))

;; TODO: This is returning maps with :value right now
(defn pending-roll [dice]
  (dice :roll))

(defn accept-roll [dice]
  (dissoc dice :roll))

(defn save-roll [dice roll]
  (assoc dice :roll roll))

(defn reroll-lowest [dice]
  (save-roll dice (conj (rest (sort (pending-roll dice)))
                        (roll dice))))

(defn skill-check [dice investigator skill modifier]
  (save-roll dice (take (max 0 (+ (skill investigator) modifier))
                        (repeatedly #(roll dice)))))

(defn combat-check [dice investigator modifier]
  (skill-check dice investigator stat/fight modifier))
