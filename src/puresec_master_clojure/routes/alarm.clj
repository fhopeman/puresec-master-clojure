(ns puresec-master-clojure.routes.alarm
  (:require [puresec-master-clojure.layout :as layout]
            [compojure.core :refer [defroutes GET POST context]]
            [ring.util.response :refer [response status redirect content-type]]
            [puresec-master-clojure.service.detector :as detector-service]
            [puresec-master-clojure.service.trigger :as trigger-service]
            [puresec-master-clojure.utils.response :as response-utils]
            [puresec-master-clojure.service.notification-dispatcher :as dispatcher]))

(defn api-register-slave! [request fn-register-slave]
  (let [name (:name (:params request))
        description (:description (:params request))]
    (if (and name description)
      (response (fn-register-slave name description))
      (status (response (response-utils/create-error-result "missing parameter name or description")) 400))))

(defn api-notify-alarm [request]
  (let [id (:id (:params request))]
    (dispatcher/dispatch-alarm-notification id)
    (response (response-utils/create-successful-result id))))

(defroutes alarm-routes
  (context "/alarm" []
    (GET  "/home" [] (layout/render "home.html" {:detectors (detector-service/get-detectors)}))
    (POST "/register/detector" request (api-register-slave! request detector-service/register-detector))
    (POST "/register/trigger" request (api-register-slave! request trigger-service/register-trigger))
    (POST "/notify" request (api-notify-alarm request))))
