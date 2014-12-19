(ns arkham-horror.core)

(def status-message (ref ""))
(defn get-status []
  @status-message)
(defn set-status! [& lines]
  (ref-set status-message (clojure.string/join "\n" lines)))

(def game (ref nil))
(def ancient-one (ref nil))

(defn reset []
  (dosync (set-status! "To begin a game, type (begin <config>)")
          (ref-set ancient-one {})
          (ref-set game {})))
(reset)

(defn begin [config]
  (dosync (set-status! "Welcome to Arkham Horror!"
                       "Type (awaken) to rouse Azathoth.")
          (alter game assoc :begun true)))

(defn awaken []
  (dosync (set-status! "Azathoth has destroyed the world!"
                       "Type (reset) to start over.")
          (alter ancient-one assoc :awakened true)))
