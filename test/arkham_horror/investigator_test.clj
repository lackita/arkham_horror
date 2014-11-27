(ns arkham-horror.investigator-test
  (:require [clojure.test :refer :all]
            [arkham-horror.investigator :as investigator]))

(def speed-1 (investigator/make {:speed 1}))

(deftest speed-test
  (is (= (investigator/speed speed-1) 1))
  (is (= (investigator/speed (investigator/speed-sneak-slider speed-1 -1)) 0)))
