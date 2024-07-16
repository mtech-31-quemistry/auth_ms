do
$$
DECLARE 
	getQuestion integer;
	postQuestion integer;
 	student integer;
 	teacher integer;
 	adminRole integer;
BEGIN
	-- ROLE
	INSERT INTO qms_auth.role(name)
	VALUES( 'admin');

	INSERT INTO qms_auth.role(name)
	VALUES('teacher');

	INSERT INTO qms_auth.role(name)
	VALUES('student');


	-- Api Resource

	INSERT INTO qms_auth.api_resource(method, path)
	VALUES( 'GET', '/questions');

	INSERT INTO qms_auth.api_resource(method, path)
	VALUES( 'POST', '/questions');

	-- Grants
	SELECT id into getQuestion FROM qms_auth.api_resource WHERE path = '/questions' and method = 'GET';
	SELECT id into postQuestion FROM qms_auth.api_resource WHERE path = '/questions' and method = 'POST';

	select id into student FROM qms_auth.role where name = 'student';
	select id into teacher FROM qms_auth.role where name = 'teacher';
	select id into adminRole FROM qms_auth.role where name = 'admin';

	/*GET questions - student*/
	INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
	VALUES(getQuestion, student);

	/*GET questions - admin*/
	INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
	VALUES(getQuestion, adminRole);

	/*GET questions - teacher*/
	INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
	VALUES(getQuestion, teacher);

	/*POST questions - admin*/
	INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
	VALUES(postQuestion, adminRole);

	/*POST questions - teacher*/
	INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
	VALUES(postQuestion, teacher);
END;
$$;