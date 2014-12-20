(ns arkham-horror.board)

(def status (ref ""))
(def game (ref nil))
(def ancient-one (ref nil))
(def investigator (ref nil))

(def board {:status status
            :game game
            :ancient-one ancient-one
            :investigator investigator})

(defn get-status
  ([] (get-status board))
  ([board] @(board :status)))

(defn make-status [message phase & commands]
  (clojure.string/join "\n" `[~message
                              ~(str "Phase: " phase)
                              "Commands:"
                              ~@(map #(str "\t" %) commands)]))

(defn update-status! [status & args]
  (ref-set status (apply make-status args)))

(defn set-status! [& args]
  (apply update-status! status args))

(defn make []
  {:status (ref (make-status "Game not started." "None" '(begin <config>)))
   :ancient-one (ref {})
   :game (ref {})
   :investigator (ref nil)})

(defn reset []
  (dosync (set-status! "Game not started." "None" '(begin <config>))
          (ref-set ancient-one {})
          (ref-set game {})))
