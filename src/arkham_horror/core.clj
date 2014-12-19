(ns arkham-horror.core)

(def status-message (ref ""))
(defn get-status []
  @status-message)
(defn set-status! [message & commands]
  (ref-set status-message
           (clojure.string/join "\n" `[~message
                                       "Commands:"
                                       ~@(map #(str "\t" %) commands)])))

(def game (ref nil))
(def ancient-one (ref nil))

(defn reset []
  (dosync (set-status! "Game not started."
                       "(begin <config>)")
          (ref-set ancient-one {})
          (ref-set game {})))
(reset)

(defn begin [config]
  (dosync (set-status! "Welcome to Arkham Horror!"
                       "(def investigator (investigator/make <name> {:speed <speed> :fight <fight> :lore <lore>}))"
                       "(awaken)")
          (alter game assoc :begun true)
          (alter ancient-one assoc :name (config :ancient-one))))

(defn awaken []
  (dosync (if (= (@ancient-one :name) "Azathoth")
            (set-status! "Azathoth has destroyed the world!"
                         "(reset)")
            (set-status! "Cthulu has been awakened.\nPhase: Upkeep"
                         "(investigator/focus <investigator> {:speed-sneak <speed-delta> :fight-will <fight-delta> :lore-luck <lore-delta>})"
                         "(end-upkeep)"))
          (alter ancient-one assoc :awakened true)))