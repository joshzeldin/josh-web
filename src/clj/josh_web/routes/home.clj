(ns josh-web.routes.home
  (:require
   [josh-web.layout :as layout]
   [josh-web.db.core :as db]
   [clojure.java.io :as io]
   [josh-web.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]
   [struct.core :as st]
   ))

(def company-schema
[[:name
  st/required
  st/string]

  [:website
  st/required
  st/string
  {:message "website must contain at least a prefix (http, https) and a suffix (.com, .edu, .org, etc.)"
    :validate #(re-find #"^http(.*)\.[a-zA-Z0-9-.]+$" %)}]])
  
(defn validate-company [params]
  (first (st/validate params company-schema)))

(defn create-company! [{:keys [params]}]
  (if-let [errors (validate-company params)]
    (-> (response/found "/work")
        (assoc :flash (assoc params :errors errors)))
    (do
      (db/create-company!
        (assoc params :timestamp (java.util.Date.)))
      (response/found "/work"))))

(defn work-page [{:keys [flash] :as request}]
  (layout/render
    request
    "work.html"
    (merge {:companies (db/get-companies) :jobs (db/get-jobs) :projects (db/get-projects)}
          (select-keys flash [:name :website :errors]))))

(defn prismic-api [] (io.prismic.Api/get "https://josh-web.prismic.io/api" (:prismic-token josh-web.config/env)))
(defn link-resolver [resolveLink]
  (reify io.prismic.LinkResolver
          (^String resolve [this ^io.prismic.Fragment$DocumentLink link] (resolveLink link))
          (^String resolve [this ^io.prismic.Document document] (resolveLink (.asDocumentLink document)))
          ))

(defn render
  ([fragment linkResolver] (.asHtml fragment (link-resolver linkResolver)))
  ([fragment] (.asHtml fragment))
  )

(defn extract-article [prismic-article]
  {
    :lang (-> (.getLang prismic-article))
    :tags (-> (.getTags prismic-article))
    :date (-> (.getFragments prismic-article) (get "articles.date") (.getValue))
    :title (-> (.getFragments prismic-article) (get "articles.title") (.getTitle) (.getText))
    :tripid (-> (.getFragments prismic-article) (get "articles.tripid") (.getValue) (int))
    :url (clojure.string/join ["travel/article/" 
      (-> (.getFragments prismic-article) (get "articles.title") (.getTitle) (.getText) (clojure.string/replace " " "-") (clojure.string/lower-case))])
    :title-image (-> (.getFragments prismic-article) (get "articles.title_image") (render link-resolver))
    :location [(-> (.getFragments prismic-article) (get "articles.location") (.getLatitude)) (-> (.getFragments prismic-article) (get "articles.location") (.getLongitude))]
    :preview (-> (.getFragments prismic-article) (get "articles.preview") (.getValue)) 
    :body (-> (.getFragments prismic-article) (get "articles.body") (render link-resolver))
  }
  )

;;stupid query for now. returns all types and assumes they are articles.
;;this should be refined to only return articles
(defn get-articles [prismic-api]
  (let [prismic-articles (-> (.query prismic-api) (.submit) (.getResults))]
  (sort-by :date (map extract-article prismic-articles)))
 )

 (defn get-article [prismic-api title]
  (let [prismic-articles (get-articles prismic-api)]
  (last 
    (filter (fn [article] (-> (get article :title) (clojure.string/replace " " "-") (clojure.string/lower-case) (= title))) prismic-articles)))
 )

(defn travel-page [{:keys [flash] :as request}]
  (layout/render 
    request
    "travel.html"
    {:request request :trips (db/get-trips) :articles (get-articles (prismic-api))}))

(defn travel-article-page [{:keys [flash] :as request}]
  (layout/render 
    request
    "travel/article.html"
    {:article (get-article (prismic-api) (:title (get request :path-params)))}))

(defn home-routes []
  [""
    {:middleware [middleware/wrap-csrf
                  middleware/wrap-formats]}
    ["/" {:get (fn [request] (ring.util.response/redirect "/travel"))}]
    ["/travel" {:get travel-page}]
    ["/travel/article/:title" {:get travel-article-page}]
    ["/work" {:get work-page}]
    ["/work/create-company" {:post create-company!}]])

