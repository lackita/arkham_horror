(ns arkham-horror.setup-test
  (:require [clojure.test :refer :all]
            [arkham-horror.setup :as setup]
            [arkham-horror.ancient-one :as ancient-one]))

(deftest begin-test
  (ancient-one/awakened? (setup/begin)))
