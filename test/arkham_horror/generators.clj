(ns arkham-horror.generators
  (:require [clojure.test.check.generators :as gen]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.ancient-one :as ancient-one]))

(defn make-investigator [bounds]
  (gen/fmap (fn [[speed fight lore decision]]
              (dosync
               (let [investigator (investigator/make "Monterey Jack" {:speed speed
                                                                      :fight fight
                                                                      :lore lore})]
                 (investigator/make-decision investigator decision)
                 investigator)))
            (gen/tuple (gen/choose (+ (or (bounds :minimum-speed) 0) 1)
                                   (+ (or (bounds :maximum-speed) 0) 4))
                       (gen/choose (+ (or (bounds :minimum-fight) 0) 2)
                                   (+ (or (bounds :maximum-fight) 0) 5))
                       (gen/choose (+ (or (bounds :minimum-lore)  0) 1)
                                   (+ (or (bounds :maximum-lore)  0) 4))
                       (gen/vector (gen/choose 0 2) 2))))

(def investigator (make-investigator {}))

(def ancient-one
  (gen/fmap (fn [name] (ancient-one/make name []))
            (gen/elements ["Cthulu" "Azathoth"])))
