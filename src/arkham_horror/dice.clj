(ns arkham-horror.dice)

(defn make [pips]
  {:value pips})

(defn roll [dice]
  (if (= (dice :value) :random)
    (inc (int (rand 6)))
    (dice :value)))

(defn roll-and-save [dice amount]
  (assoc dice :rolls (take amount (repeatedly #(roll dice)))))
