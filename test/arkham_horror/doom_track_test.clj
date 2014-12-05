(ns arkham-horror.doom-track-test
  (:require [clojure.test :refer :all]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.ancient-one.doom-track :as doom-track]
            [arkham-horror.setup :as setup]))

(deftest empty-test
  (is (= (doom-track/level (ancient-one/update (setup/begin :cthulu [])
                                               #(doom-track/update % doom-track/empty)))
         0))
  (is (= (doom-track/level (ancient-one/update (-> (setup/begin :cthulu [])
                                                   doom-track/advance)
                                               #(doom-track/update % doom-track/empty)))
         0)))
