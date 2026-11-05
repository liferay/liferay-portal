DO $$
BEGIN
	IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'liferay_shared_owner')
	THEN
		CREATE ROLE liferay_shared_owner NOLOGIN;
	END IF;
END $$;

GRANT liferay_shared_owner TO CURRENT_USER;

ALTER DATABASE :"database_name" OWNER TO liferay_shared_owner;

GRANT CONNECT, TEMPORARY ON DATABASE :"database_name" TO :"liferay_database_username";

GRANT liferay_shared_owner TO :"liferay_database_username";

ALTER ROLE :"liferay_database_username" SET ROLE liferay_shared_owner;