(ns arkham-horror.use-cases.cthulu
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer :all]
            [arkham-horror.generators :as gen]
            [arkham-horror.game :as game]
            [arkham-horror.board :as board]
            [arkham-horror.ancient-one :as ancient-one]
            [arkham-horror.investigator :as investigator]
            [arkham-horror.final-battle :as final-battle]))

(deftest investigators-max-sanity-and-stamina-reduced
  (checking "Primary Course" [investigator gen/investigator]
    (dosync
     (board/make {:ancient-one "Cthulu" :investigators [investigator]})
     (is (= (investigator/maximum-sanity investigator)
            (dec (investigator/initial-maximum-sanity (investigator/name investigator)))))
     (is (= (investigator/maximum-stamina investigator)
            (dec (investigator/initial-maximum-stamina (investigator/name investigator)))))))

  (checking "Exceptional Course: Ancient One Not Cthulu" [investigator gen/investigator]
    (dosync
     (board/make {:ancient-one "Azathoth" :investigators [investigator]})
     (is (= (investigator/maximum-sanity investigator)
            (investigator/initial-maximum-sanity (investigator/name investigator)))))))

(defn defeat-investigator [ancient-one investigator]
  (dotimes [n (investigator/maximum-stamina investigator)]
    (final-battle/ancient-one-attack ancient-one [investigator])
    (investigator/make-decision investigator :stamina)))

(deftest destroys-world
  (checking "Primary Course: Sanity Reduced" [investigator gen/investigator]
    (dosync
     (let [board (board/make {:ancient-one "Cthulu" :investigators [investigator]})]
       (ancient-one/awaken (board :ancient-one))
       (is (not (investigator/pending-decision investigator)))
       (final-battle/ancient-one-attack (board :ancient-one) (board :investigators))
       (is (investigator/pending-decision investigator))
       (let [old-maximum-sanity (investigator/maximum-sanity investigator)
             old-maximum-stamina (investigator/maximum-stamina investigator)]
         (investigator/make-decision investigator :sanity)
         (is (= (investigator/maximum-sanity investigator) (dec old-maximum-sanity)))
         (is (= (investigator/maximum-stamina investigator) old-maximum-stamina)))
       (final-battle/ancient-one-attack (board :ancient-one) (board :investigators))
       (let [old-maximum-sanity (investigator/maximum-sanity investigator)
             old-maximum-stamina (investigator/maximum-stamina investigator)]
         (investigator/make-decision investigator :stamina)
         (is (= (investigator/maximum-sanity investigator) old-maximum-sanity))
         (is (= (investigator/maximum-stamina investigator) (dec old-maximum-stamina))))
       (dotimes [n (investigator/maximum-sanity investigator)]
         (final-battle/ancient-one-attack (board :ancient-one) (board :investigators))
         (investigator/make-decision investigator :sanity))
       (is (game/lost? board)))))

  (checking "Primary Course: Stamina Reduced" [investigator gen/investigator]
    (dosync
     (let [board (board/make {:ancient-one "Cthulu" :investigators [investigator]})]
       (ancient-one/awaken (board :ancient-one))
       (dotimes [n (investigator/maximum-stamina investigator)]
         (final-battle/ancient-one-attack (board :ancient-one) (board :investigators))
         (investigator/make-decision investigator :stamina))
       (is (game/lost? board)))))

  (checking "Exceptional Course: Attacking Without Decision" [investigator gen/investigator]
    (dosync
     (let [board (board/make {:ancient-one "Cthulu" :investigators [investigator]})]
       (ancient-one/awaken (board :ancient-one))
       (final-battle/ancient-one-attack (board :ancient-one) (board :investigators))
       (is (thrown? AssertionError (final-battle/ancient-one-attack (board :ancient-one)
                                                       (board :investigators))))
       (is true))))

  (checking "Exceptional Course: Attacking After Defeat" [investigator gen/investigator]
    (dosync
     (let [board (board/make {:ancient-one "Cthulu" :investigators [investigator]})]
       (ancient-one/awaken (board :ancient-one))
       (defeat-investigator (board :ancient-one) investigator)
       (is (thrown? AssertionError (final-battle/ancient-one-attack (board :ancient-one)
                                                                    [investigator])))
       (is true))))

  (checking "Exceptional Course: Only Undefeated Investigators Attacked"
    [investigator-1 gen/investigator investigator-2 gen/investigator]
    (dosync
     (let [board (board/make {:ancient-one "Cthulu"
                              :investigators [investigator-1 investigator-2]})]
       (ancient-one/awaken (board :ancient-one))
       (defeat-investigator (board :ancient-one) investigator-1)
       (final-battle/ancient-one-attack (board :ancient-one) (board :investigators))
       (is (not (investigator/pending-decision investigator-1)))))))

(deftest doom-track
  (testing "Primary Course"
    (is (= (ancient-one/maximum-doom-track (ancient-one/make "Cthulu" [])) 13))))

(deftest defeated
  (checking "Primary Course: Single Investigator" [investigator gen/investigator]
    (dosync
     (let [board (board/make {:ancient-one "Cthulu" :investigators [investigator]})]
       (is (not (game/won? board)))
       (ancient-one/awaken (board :ancient-one))
       (is (not (game/won? board)))
       (final-battle/investigator-attack investigator (board :ancient-one) 1)
       (is (= (ancient-one/doom-track (board :ancient-one)) 12))
       (final-battle/investigator-attack investigator (board :ancient-one) 2)
       (is (= (ancient-one/doom-track (board :ancient-one)) 10))
       (final-battle/investigator-attack investigator (board :ancient-one) 10)
       (is (game/won? board)))))

  (checking "Primary Course: Multiple Investigator"
    [investigator-1 gen/investigator investigator-2 gen/investigator]
    (dosync
     (let [board (board/make {:ancient-one "Cthulu"
                              :investigators [investigator-1 investigator-2]})]
       (ancient-one/awaken (board :ancient-one))
       (final-battle/investigator-attack investigator-1 (board :ancient-one) 2)
       (is (= (ancient-one/doom-track (board :ancient-one)) 12)))))

  (checking "Exceptional Course: Negative Successes" [investigator gen/investigator]
    (dosync
     (let [board (board/make {:ancient-one "Cthulu" :investigators [investigator]})]
       (ancient-one/awaken (board :ancient-one))
       (final-battle/investigator-attack investigator (board :ancient-one) -1)
       (is (= (ancient-one/doom-track (board :ancient-one)) 13))))))

(deftest refresh
  (checking "Primary Course" [investigator gen/investigator]
    (dosync
     (let [board (board/make {:ancient-one "Cthulu" :investigators [investigator]})]
       (ancient-one/awaken (board :ancient-one))
       (ancient-one/retract-doom-track (board :ancient-one))
       (final-battle/refresh (board :ancient-one))
       (is (= (ancient-one/doom-track (board :ancient-one)) 13))
       (is true)))))
