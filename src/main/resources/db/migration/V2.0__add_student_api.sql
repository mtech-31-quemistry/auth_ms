do
$$
DECLARE
    search_student integer;
    tutor integer;
BEGIN

-- Api Resource
INSERT INTO qms_auth.api_resource(method, path)
VALUES( 'POST', '/student/search');

-- Grants
SELECT id into search_student FROM qms_auth.api_resource WHERE path = '/student/search' and method = 'POST';

select id into tutor FROM qms_auth.role where name = 'tutor';

/*generate question from ai*/
INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(search_student, tutor);

END;
$$;
