(ns arkham-horror.dice)

(defn roll [times]
  (take times (repeatedly #(inc (int (rand 6))))))

(defn count-successes [rolls]
  (count (filter #{5 6} rolls)))
