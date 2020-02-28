(ns josh-web.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [josh-web.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[josh-web started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[josh-web has shut down successfully]=-"))
   :middleware wrap-dev})
