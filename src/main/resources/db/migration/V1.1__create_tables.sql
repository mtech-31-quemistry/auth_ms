-- Table: qms_auth.role
--DROP TABLE IF EXISTS qms_auth.role;

CREATE TABLE IF NOT EXISTS qms_auth.role
(
    id SERIAL NOT NULL,
    name character varying(100) COLLATE pg_catalog."default",
    CONSTRAINT role_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS qms_auth.role
    OWNER to qms_auth;

-- Table: qms_auth.api_resource

--DROP TABLE IF EXISTS qms_auth.api_resource;

CREATE TABLE IF NOT EXISTS qms_auth.api_resource
(
    id SERIAL NOT NULL,
    method character varying(50) COLLATE pg_catalog."default",
    path character varying(500) COLLATE pg_catalog."default",
    CONSTRAINT api_resource_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS qms_auth.api_resource
    OWNER to qms_auth;
	
-- Table: qms_auth.role_api_resource

--DROP TABLE IF EXISTS qms_auth.role_api_resource;

CREATE TABLE IF NOT EXISTS qms_auth.role_api_resource
(
    resource_id bigint NOT NULL,
    role_id bigint NOT NULL,
    CONSTRAINT role_api_resource_pkey PRIMARY KEY (resource_id, role_id),
    CONSTRAINT fk_role_role_id FOREIGN KEY (role_id)
        REFERENCES qms_auth.role (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_api_resource_resource_id FOREIGN KEY (resource_id)
        REFERENCES qms_auth.api_resource (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS qms_auth.role_api_resource
    OWNER to qms_auth;
	
