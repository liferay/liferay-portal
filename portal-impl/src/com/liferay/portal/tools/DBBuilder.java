/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools;

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.IOException;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.sql.SQLException;

import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author Charles May
 * @author Alexander Chow
 * @author Raymond Augé
 */
public class DBBuilder {

	public static void main(String[] args) throws Exception {
		ToolDependencies.wireBasic();

		Map<String, String> arguments = ArgumentsUtil.parseArguments(args);

		String databaseName = arguments.get("db.database.name");

		String databaseTypesString = arguments.get("db.database.types");

		DBType[] dbTypes = DBType.values();

		if (databaseTypesString != null) {
			String[] databaseTypeValues = StringUtil.split(databaseTypesString);

			dbTypes = new DBType[databaseTypeValues.length];

			for (int i = 0; i < dbTypes.length; i++) {
				dbTypes[i] = DBType.valueOf(
					StringUtil.toUpperCase(databaseTypeValues[i]));
			}
		}

		String sqlDir = arguments.get("db.sql.dir");

		try {
			new DBBuilder(databaseName, dbTypes, sqlDir);
		}
		catch (Exception exception) {
			ArgumentsUtil.processMainException(arguments, exception);
		}
	}

	public DBBuilder(String databaseName, DBType[] dbTypes, String sqlDir)
		throws Exception {

		_databaseName = databaseName;
		_dbTypes = dbTypes;

		if (!sqlDir.endsWith("/META-INF/sql") &&
			!sqlDir.endsWith("/WEB-INF/sql")) {

			_buildSQLFile(sqlDir, "portal");
			_buildSQLFile(sqlDir, "portal-tables");
		}
		else {
			_buildSQLFile(sqlDir, "tables");
		}

		_buildSQLFile(sqlDir, "indexes");
		_buildSQLFile(sqlDir, "sequences");
		_buildSQLFile(sqlDir, "update-6.1.0-6.1.1");
		_buildSQLFiles(sqlDir, "update-6.1.1-6.2.0*");
		_buildSQLFiles(sqlDir, "update-6.2.0-7.0.0*");
		_buildSQLFiles(sqlDir, "update-7.0.0-7.0.1*");

		_buildCreateFile(sqlDir);
	}

	private void _appendFile(
			StringBundler sb, String sqlDir, String pathPrefix, DBType dbType)
		throws Exception {

		String fileName = StringBundler.concat(
			sqlDir, pathPrefix, dbType, ".sql");

		if (FileUtil.exists(fileName)) {
			sb.append(FileUtil.read(fileName));
		}
	}

	private void _buildCreateFile(String sqlDir) throws Exception {
		for (DBType dbType : _dbTypes) {
			if (dbType == DBType.HYPERSONIC) {
				continue;
			}

			DB db = DBManagerUtil.getDB(dbType, null);

			if (db == null) {
				continue;
			}

			String recreateSQL = db.getRecreateSQL(_databaseName);

			if (!sqlDir.endsWith("/WEB-INF/sql")) {
				FileUtil.write(
					StringBundler.concat(
						sqlDir, "/create-bare/create-bare-", db.getDBType(),
						".sql"),
					recreateSQL);
			}

			StringBundler sb = new StringBundler(6);

			String tablesPrefix = "/portal/portal-";

			if (sqlDir.endsWith("/WEB-INF/sql")) {
				tablesPrefix = "/tables/tables-";
			}

			_appendFile(sb, sqlDir, tablesPrefix, db.getDBType());

			sb.append("\n\n");

			_appendFile(sb, sqlDir, "/indexes/indexes-", db.getDBType());

			sb.append("\n\n");

			_appendFile(sb, sqlDir, "/sequences/sequences-", db.getDBType());

			sb.append("\n");

			String content = db.getPopulateSQL(_databaseName, sb.toString());

			if (content.isEmpty()) {
				continue;
			}

			FileUtil.write(
				StringBundler.concat(
					sqlDir, "/create/create-", db.getDBType(), ".sql"),
				recreateSQL.concat(content));
		}
	}

	private void _buildSQLFile(String sqlDir, String fileName)
		throws Exception {

		if (!FileUtil.exists(
				StringBundler.concat(sqlDir, "/", fileName, ".sql"))) {

			return;
		}

		_generateSQLFile(sqlDir, fileName);
	}

	private void _buildSQLFiles(String sqlDir, String regex) throws Exception {
		try (DirectoryStream<Path> paths = Files.newDirectoryStream(
				Paths.get(sqlDir), regex)) {

			for (Path path : paths) {
				String fileName = String.valueOf(path.getFileName());

				_generateSQLFile(
					sqlDir, StringUtil.removeSubstring(fileName, ".sql"));
			}
		}
	}

	private void _generateSQLFile(String sqlDir, String fileName)
		throws IOException, SQLException {

		for (DBType dbType : _dbTypes) {
			DB db = DBManagerUtil.getDB(dbType, null);

			if (db == null) {
				continue;
			}

			String template = FileUtil.read(
				StringBundler.concat(sqlDir, "/", fileName, ".sql"));

			if (fileName.equals("portal")) {
				StringBundler sb = new StringBundler();

				try (UnsyncBufferedReader unsyncBufferedReader =
						new UnsyncBufferedReader(
							new UnsyncStringReader(template))) {

					String line = null;

					while ((line = unsyncBufferedReader.readLine()) != null) {
						if (line.startsWith("@include ")) {
							int pos = line.indexOf(" ");

							String includeFileName = line.substring(pos + 1);

							File includeFile = new File(
								sqlDir + "/" + includeFileName);

							if (!includeFile.exists()) {
								continue;
							}

							sb.append(FileUtil.read(includeFile));

							sb.append("\n\n");
						}
						else {
							sb.append(line);
							sb.append("\n");
						}
					}
				}

				template = sb.toString();
			}

			if (Validator.isNull(template)) {
				return;
			}

			template = db.buildSQL(template);

			FileUtil.write(
				StringBundler.concat(
					sqlDir, "/", fileName, "/", fileName, "-", db.getDBType(),
					".sql"),
				template);
		}
	}

	private final String _databaseName;
	private final DBType[] _dbTypes;

}