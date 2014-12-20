(ns arkham-horror.board)

(def status (ref ""))
(def game (ref nil))
(def ancient-one (ref nil))
(def investigator (ref nil))

(defn get-status []
  @status)

(defn set-status! [message phase & commands]
  (ref-set status
           (clojure.string/join "\n" `[~message
                                       ~(str "Phase: " phase)
                                       "Commands:"
                                       ~@(map #(str "\t" %) commands)])))

(defn reset []
  (dosync (ref-set status "Game not started.\nPhase: None\nCommands:\n\t(begin <config>)")
          (ref-set ancient-one {})
          (ref-set game {})))
