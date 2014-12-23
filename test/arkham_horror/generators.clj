(ns arkham-horror.generators
  (:require [clojure.test.check.generators :as gen]
            [arkham-horror.investigator :as investigator]))

(def investigator
  (gen/fmap (fn [[speed fight lore]]
              (investigator/make "Monterey Jack" {:speed speed :fight fight :lore lore}))
            (gen/tuple (gen/choose 1 4) (gen/choose 2 5) (gen/choose 1 4))))
