(ns arkham-horror.help)

(defn make []
  {})

(defn get-message [game]
  (-> game :help :message))

(defn set-message [game message]
  (assoc-in game [:help :message] message))

(defn get-available-actions [game]
  (-> game :help :actions))

(defn set-available-actions [game actions]
  (assoc-in game [:help :actions] actions))

(defn save-actions [game]
  (assoc-in game [:help :previous-actions] (-> game :help :actions)))

(defn restore-actions [game]
  (set-available-actions game (-> game :help :previous-actions)))
