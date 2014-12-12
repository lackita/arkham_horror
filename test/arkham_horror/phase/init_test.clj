(ns arkham-horror.phase.init-test
  (:require [clojure.test :refer :all]
            [arkham-horror.game :as game]
            [arkham-horror.phase :as phase]
            [arkham-horror.phase.init :as init]))

(deftest start
  (is (phase/get (init/start (game/make {:ancient-one "Cthulu"
                                         :investigators ["Monterey Jack"]})))))
