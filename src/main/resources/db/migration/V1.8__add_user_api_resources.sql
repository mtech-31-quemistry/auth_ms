do
$$
DECLARE
    get_tutor_profile integer;
    post_tutor_profile integer;
    accept_invite integer;
 	tutor integer;
    student integer;
BEGIN

-- Api Resource

INSERT INTO qms_auth.api_resource(method, path)
VALUES( 'GET', '/users/tutor/profile');

INSERT INTO qms_auth.api_resource(method, path)
VALUES( 'POST', '/users/tutor/profile');

INSERT INTO qms_auth.api_resource(method, path)
VALUES( 'POST', '/student/accept-invitation');

-- Grants
SELECT id into get_tutor_profile FROM qms_auth.api_resource WHERE path = '/users/tutor/profile' and method = 'GET';
SELECT id into post_tutor_profile FROM qms_auth.api_resource WHERE path = '/users/tutor/profile' and method = 'POST';
SELECT id into accept_invite FROM qms_auth.api_resource WHERE path = '/student/accept-invitation' and method = 'POST';

select id into tutor FROM qms_auth.role where name = 'tutor';
select id into student FROM qms_auth.role where name = 'student';

/*generate question from ai*/
INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES( get_tutor_profile, tutor);
INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(post_tutor_profile, tutor);
INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(accept_invite, student);

END;
$$;