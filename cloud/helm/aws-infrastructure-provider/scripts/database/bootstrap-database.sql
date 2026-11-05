DO $$
BEGIN
	IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'liferay_shared_owner')
	THEN
		CREATE ROLE liferay_shared_owner NOLOGIN;
	END IF;
END $$;

GRANT liferay_shared_owner TO :"bootstrap_username";

ALTER DATABASE :"database_name" OWNER TO liferay_shared_owner;

GRANT USAGE, CREATE ON SCHEMA public TO liferay_shared_owner;

SELECT format('CREATE ROLE %I LOGIN', :'liferay_database_username')
WHERE NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = :'liferay_database_username')
\gexec

ALTER ROLE :"liferay_database_username" WITH LOGIN PASSWORD :'liferay_database_password';

GRANT CONNECT, TEMPORARY ON DATABASE :"database_name" TO :"liferay_database_username";

GRANT liferay_shared_owner TO :"liferay_database_username";

ALTER ROLE :"liferay_database_username" SET ROLE liferay_shared_owner;