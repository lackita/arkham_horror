(ns arkham-horror.doom-track
  (:refer-clojure :exclude [empty]))

(defmulti capacity :ancient-one)
(defmethod capacity :cthulu [game] 13)
(defmethod capacity :azathoth [game] 14)

(defn level [game]
  (game :doom-track))

(defn move [game direction bound]
  (if (= (level game) bound)
    game
    (update-in game [:doom-track] direction)))

(defn advance [game]
  (move game inc (capacity game)))

(defn retract [game]
  (move game dec 0))

(defn fill [game]
  (assoc game :doom-track (capacity game)))

(defn empty [game]
  (assoc game :doom-track 0))
