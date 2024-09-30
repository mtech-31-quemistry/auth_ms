do
$$
DECLARE
    get_class_details integer;
    tutor integer;
BEGIN

-- Api Resource
INSERT INTO qms_auth.api_resource(method, path)
VALUES( 'GET', '/class/(.*)');

-- Grants
SELECT id into get_class_details FROM qms_auth.api_resource WHERE path = '/class/(.*)' and method = 'GET';

select id into tutor FROM qms_auth.role where name = 'tutor';

/*generate question from ai*/
INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(get_class_details, tutor);

END;
$$;