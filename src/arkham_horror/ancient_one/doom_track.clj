(ns arkham-horror.ancient-one.doom-track
  (:refer-clojure :exclude [get empty]))

(defn get [ancient-one]
  (ancient-one :doom-track))

(defn update [ancient-one function]
  (update-in ancient-one [:doom-track] function))

(defn make
  ([level capacity] {:level level
                     :capacity capacity})
  ([level] {:level level}))

(defn capacity [doom-track]
  (doom-track :capacity))

(defn level [doom-track]
  (doom-track :level))

(defn move [game direction bound]
  (if (= (level (-> game :ancient-one :doom-track)) bound)
    game
    (update-in game [:ancient-one :doom-track :level] direction)))

(defn advance [game]
  (move game inc (capacity (-> game :ancient-one :doom-track))))

(defn retract [game]
  (move game dec 0))

(defn fill [doom-track]
  (assoc doom-track :level (capacity doom-track)))

(defn empty [doom-track]
  (assoc doom-track :level 0))
