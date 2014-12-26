(ns arkham-horror.dice)

(defn roll [times]
  (take times (repeatedly #(inc (int (rand 6))))))
