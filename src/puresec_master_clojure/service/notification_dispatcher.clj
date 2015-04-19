(ns puresec-master-clojure.service.notification-dispatcher
  (:require [puresec-master-clojure.db.core :as db]
            [clj-http.client :refer [post]]))

(defn notify-trigger [trigger detector]
  (post (str (:url trigger) "/notify") {:form-params
                                         {:detector_name (:detector_name detector)
                                          :detector_description (:detector_description detector)}})
  (:id trigger))

(defn dispatch-alarm-notification [detector_id]
  (let [detector (first (db/load-detector-by-id {:detector_id detector_id}))
        triggers (db/load-matching-triggers {:detector_id detector_id})]
    (map (fn [trigger] (notify-trigger trigger detector)) triggers)))
