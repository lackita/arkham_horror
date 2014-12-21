(ns arkham-horror.investigator
  (:refer-clojure :exclude [name]))

(defn make [name stats]
  stats)

(defn name [investigator]
  "Monterey Jack")

(defn speed [investigator]
  2)

(defn sneak [investigator]
  2)

(defn fight [investigator]
  2)

(defn will [investigator]
  3)

(defn lore [investigator]
  2)

(defn luck [investigator]
  4)
