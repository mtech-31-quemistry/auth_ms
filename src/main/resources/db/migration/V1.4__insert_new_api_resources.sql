do
$$
DECLARE
    getQuizzDetail integer;
	postQuizz integer;
    getQuizzInProg integer;
	getMCQAttempt integer;
 	student integer;
BEGIN


INSERT INTO qms_auth.api_resource(method, path)
VALUES( 'POST', '/quizzes');

INSERT INTO qms_auth.api_resource(method, path)
VALUES( 'GET', '/quizzes/(\\d+)');

INSERT INTO qms_auth.api_resource(method, path)
VALUES( 'GET', '/quizzes/me/in-progress');

INSERT INTO qms_auth.api_resource(method, path)
VALUES( 'PUT', '/quizzes/mcqs/(\\d+)/attempt');

-- Grants
SELECT id into getQuizzDetail FROM qms_auth.api_resource WHERE path = '/quizzes/(\\d+)' and method = 'GET';
SELECT id into postQuizz FROM qms_auth.api_resource WHERE path = '/quizzes' and method = 'POST';
SELECT id into getQuizzInProg FROM qms_auth.api_resource WHERE path = '/quizzes/me/in-progress' and method = 'GET';
SELECT id into getMCQAttempt FROM qms_auth.api_resource WHERE path = '/quizzes/mcqs/(\\d+)/attempt' and method = 'PUT';

select id into student FROM qms_auth.role where name = 'student';

/*GET questions - student*/
INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(getQuizzDetail, student);

INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(postQuizz, student);

INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(getQuizzInProg, student);

INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(getMCQAttempt, student);

END;
$$;