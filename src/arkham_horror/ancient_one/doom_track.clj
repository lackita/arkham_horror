(ns arkham-horror.ancient-one.doom-track
  (:refer-clojure :exclude [empty]))

(defn update [ancient-one function]
  (update-in ancient-one [:doom-track] function))

(defn make [level]
  level)

(defmulti capacity #(-> % :ancient-one :name))
(defmethod capacity :cthulu [game] 13)
(defmethod capacity :azathoth [game] 14)

(defn level [game]
  (-> game :ancient-one :doom-track))

(defn move [game direction bound]
  (if (= (level game) bound)
    game
    (update-in game [:ancient-one :doom-track] direction)))

(defn advance [game]
  (move game inc (capacity game)))

(defn retract [game]
  (move game dec 0))

(defn fill [game]
  (assoc-in game [:ancient-one :doom-track] (capacity game)))

(defn empty [_]
  0)
