do
$$
DECLARE
remove_student integer;
    tutor integer;
BEGIN

-- Api Resource
INSERT INTO qms_auth.api_resource(method, path)
VALUES( 'POST', '/class/remove-student');

-- Grants
SELECT id into remove_student FROM qms_auth.api_resource WHERE path = '/class/remove-student' and method = 'POST';

select id into tutor FROM qms_auth.role where name = 'tutor';


INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(remove_student, tutor);

END;
$$;
