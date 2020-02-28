-- :name create-company! :! :n
-- :doc creates a new company record
INSERT INTO companies
(name, website)
VALUES (:name, :website)

-- :name create-language! :! :n
-- :doc creates a new language record
INSERT INTO languages
(name, website)
VALUES (:name, :website)

-- :name create-job! :! :n
-- :doc creates a new job record
INSERT INTO jobs
(title, startdate, enddate, companyid)
VALUES (:title, :startdate, :enddate, (SELECT id FROM companies WHERE name = :companyname));

-- :name create-project! :! :n
-- :doc creates a new project record
INSERT INTO projects
(name, startdate, enddate, description, jobid)
VALUES (:name, :startdate, :enddate, :description, 
    (SELECT id FROM 
    jobs INNER JOIN companies
    WHERE jobs.startdate >= :startdate AND  companies.name = :company));

-- :name create-trip! :! :n
-- :doc creates a new trip record
INSERT INTO trips
(title, description, startdate, enddate, parentId)
VALUES (:title, :description, :startdate, :enddate, :parentId);

-- :name get-companies :? :*
-- :doc retrieves all company records
SELECT * FROM companies

-- :name get-company :? :1
-- :doc retrieves a company record given the id
SELECT * FROM companies
WHERE id = :id

-- :name get-language :? :1
-- :doc retrieves a language record given the id
SELECT * FROM languages
WHERE id = :id

-- :name get-jobs :? :*
-- :doc retrieves all job records
SELECT * FROM jobs LEFT JOIN companies ON jobs.companyid = companies.id ORDER BY startdate

-- :name get-projects :? :*
-- :doc retrieves all project records
SELECT * FROM projects ORDER BY startdate

-- :name get-project :? :*
-- :doc retrieves a project record given the job
SELECT * FROM projects
WHERE jobid = :jobid

-- :name get-trips :? :*
-- :doc retrieves all trip records
SELECT * FROM trips ORDER BY startdate