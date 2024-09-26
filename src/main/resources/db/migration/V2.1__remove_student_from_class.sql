do
$$
DECLARE
remove_student integer;
    tutor integer;
BEGIN

-- Api Resource
INSERT INTO qms_auth.api_resource(method, path)
VALUES( 'DELETE', '/class/(\d+)/student/(\d+)');

-- Grants
SELECT id into remove_student FROM qms_auth.api_resource WHERE path = '/class/(\d+)/student/(\d+)' and method = 'DELETE';

select id into tutor FROM qms_auth.role where name = 'tutor';

/*generate question from ai*/
INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(remove_student, tutor);

END;
$$;
