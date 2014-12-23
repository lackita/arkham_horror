(ns arkham-horror.use-cases.cthulu
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer :all]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.generators :as gen]
            [arkham-horror.board :as board]))

;; (defspec investigators-max-sanity-and-stamina-reduced-primary-course
;;   (prop/for-all [speed (gen/choose 1 4)
;;                  fight (gen/choose 2 5)
;;                  lore  (gen/choose 1 4)
;;                  decision (gen/vector (gen/choose 0 2) 2)]
;;                 (let [monterey-jack (investigator/make
;;                                      "Monterey Jack"
;;                                      {:speed speed
;;                                       :fight fight
;;                                       :lore  lore})
;;                       board (board/make {:ancient-one "Cthulu"
;;                                          :investigators [monterey-jack]})]
;;                   (is (= monterey-jack)))))
