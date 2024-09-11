do
$$
DECLARE
    post_OpenAI_MCQByTopics integer;
    post_Gemini_MCQByTopics integer;
 	tutor integer;
 	adminRole integer;
BEGIN

-- Api Resource

INSERT INTO qms_auth.api_resource(method, path)
VALUES( 'POST', '/genai/openaimcqbytopic/(.*)');

INSERT INTO qms_auth.api_resource(method, path)
VALUES( 'POST', '/genai/geminimcqbytopic/(.*)');

-- Grants
SELECT id into post_OpenAI_MCQByTopics FROM qms_auth.api_resource WHERE path = '/genai/openaimcqbytopic/(.*)' and method = 'POST';
SELECT id into post_Gemini_MCQByTopics FROM qms_auth.api_resource WHERE path = '/genai/geminimcqbytopic/(.*)' and method = 'POST';

select id into tutor FROM qms_auth.role where name = 'tutor';

/*generate question from ai*/
INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(post_OpenAI_MCQByTopics, tutor);
INSERT INTO qms_auth.role_api_resource(resource_id, role_id)
VALUES(post_Gemini_MCQByTopics, tutor);

END;
$$;