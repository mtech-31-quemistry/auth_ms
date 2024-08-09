do
$$
DECLARE
    postClass integer;
    putClass integer;
    getClass integer;
	postStudentProfile integer;
	postStudentInvite integer;
 	student integer;
 	tutor integer;
 	adminRole integer;
BEGIN

-- Api Resource

INSERT INTO qms_auth.api_resource(method, path)
VALUES( 'GET', '/class');

INSERT INTO qms_auth.api_resource(method, path)
VALUES( 'PUT', '/class');

INSERT INTO qms_auth.api_resource(method, path)
VALUES( 'POST', '/class');

INSERT INTO qms_auth.api_resource(method, path)
VALUES( 'POST', '/student/profile');

INSERT INTO qms_auth.api_resource(method, path)
VALUES( 'POST', '/student/send-invitation');

-- Grants
SELECT id into getClass FROM qms_auth.api_resource WHERE path = '/class' and method = 'GET';
SELECT id into postClass FROM qms_auth.api_resource WHERE path = '/class' and method = 'POST';
SELECT id into putClass FROM qms_auth.api_resource WHERE path = '/class' and method = 'PUT';

SELECT id into postStudentProfile FROM qms_auth.api_resource WHERE path = '/student/profile' and method = 'POST';
SELECT id into postStudentInvite FROM qms_auth.api_resource WHERE path = '/student/send-invitation' and method = 'POST';

select id into student FROM qms_auth.role where name = 'student';
select id into tutor FROM qms_auth.role where name = 'tutor';
select id into adminRole FROM qms_auth.role where name = 'admin';

/*get class*/
INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(getClass, student);
INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(getClass, tutor);
INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(getClass, adminRole);

/*post class*/
INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(postClass, tutor);

/*put class*/
INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(putClass, tutor);

/*post student profile*/
INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(postStudentProfile, student);

/*post student profile*/
INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(postStudentInvite, tutor);

END;
$$;