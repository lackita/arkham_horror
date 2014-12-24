(ns arkham-horror.generators
  (:require [clojure.test.check.generators :as gen]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.ancient-one :as ancient-one]))

(def investigator
  (gen/fmap (fn [[speed fight lore]]
              (investigator/make "Monterey Jack" {:speed speed :fight fight :lore lore}))
            (gen/tuple (gen/choose 1 4) (gen/choose 2 5) (gen/choose 1 4))))

(def ancient-one
  (gen/fmap (fn [name] (ancient-one/make name []))
            (gen/elements ["Cthulu" "Azathoth"])))
