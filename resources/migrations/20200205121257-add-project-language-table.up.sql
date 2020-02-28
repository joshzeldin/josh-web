CREATE TABLE IF NOT EXISTS project_language
(projId BIGINT REFERENCES projects(id),
 langId BIGINT REFERENCES languages(id),
 CONSTRAINT projectLanguageId PRIMARY KEY (projId, langId));