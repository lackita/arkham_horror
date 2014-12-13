(ns arkham-horror.help)

(defn get-message [game]
  (game :message))

(defn set-message [game message]
  (assoc game :message message))
