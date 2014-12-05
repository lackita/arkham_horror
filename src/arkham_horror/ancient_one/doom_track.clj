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

(defn move [doom-track direction bound]
  (if (= (level doom-track) bound)
    doom-track
    (update-in doom-track [:level] direction)))

(defn advance [doom-track]
  (move doom-track inc (capacity doom-track)))

(defn retract [doom-track]
  (move doom-track dec 0))

(defn fill [doom-track]
  (assoc doom-track :level (capacity doom-track)))

(defn empty [doom-track]
  (assoc doom-track :level 0))
