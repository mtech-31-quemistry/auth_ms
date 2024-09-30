do
$$
DECLARE
    get_MCQStats integer;
    get_TopicSkillStats integer;
    student integer;
 	tutor integer;
BEGIN

-- Api Resource

INSERT INTO qms_auth.api_resource(method, path)
VALUES( 'GET', '/stats/mcq(.*)');

INSERT INTO qms_auth.api_resource(method, path)
VALUES( 'GET', '/stats/topic-skill(.*)');

-- Grants
SELECT id into get_MCQStats FROM qms_auth.api_resource WHERE path = '/stats/mcq(.*)' and method = 'GET';
SELECT id into get_TopicSkillStats FROM qms_auth.api_resource WHERE path = '/stats/topic-skill(.*)' and method = 'GET';

select id into tutor FROM qms_auth.role where name = 'tutor';
select id into student FROM qms_auth.role where name = 'student';

/*generate question from ai*/
INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(get_MCQStats, tutor);
INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(get_TopicSkillStats, tutor);

INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(get_MCQStats, student);
INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(get_TopicSkillStats, student);

END;
$$;