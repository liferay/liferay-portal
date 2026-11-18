SET ROLE ALL;

SET @grant_database = CONCAT('GRANT ALL PRIVILEGES ON `', @database_name, '`.* TO ', QUOTE(@liferay_database_username), '@''%''');

PREPARE grant_database FROM @grant_database;

EXECUTE grant_database;

DEALLOCATE PREPARE grant_database;