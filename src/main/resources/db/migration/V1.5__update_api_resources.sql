
UPDATE  qms_auth.api_resource
SET path = '/quizzes/(\d+)(.*)'
WHERE path = '/quizzes/(\\d+)' and method = 'GET';

UPDATE  qms_auth.api_resource
SET path = '"/quizzes/mcqs/(\d+)/attempt"'
WHERE path = '"/quizzes/mcqs/(\\d+)/attempt"' and method = 'PUT';