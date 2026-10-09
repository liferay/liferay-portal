/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.db;

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.sql.Connection;
import java.sql.Types;

import java.util.Map;

/**
 * @author Alexander Chow
 * @author Sandeep Soni
 * @author Ganesh Ram
 */
public class HypersonicDB extends BaseDB {

	public HypersonicDB(int majorVersion, int minorVersion) {
		super(DBType.HYPERSONIC, majorVersion, minorVersion);
	}

	@Override
	public String buildSQL(String template) throws IOException {
		template = replaceTemplate(template);

		template = reword(template);
		template = StringUtil.replace(template, "\\'", "''");

		return template;
	}

	@Override
	public String getCharacterSet(Connection connection) {
		return "UTF-8";
	}

	@Override
	public String getDefaultValue(String columnDef) {
		String defaultValue = super.getDefaultValue(columnDef);

		if (defaultValue.equals("NULL")) {
			return null;
		}

		return defaultValue;
	}

	@Override
	public String getPopulateSQL(String databaseName, String sqlContent) {
		return StringPool.BLANK;
	}

	@Override
	public String getRecreateSQL(String databaseName) {
		return StringPool.BLANK;
	}

	@Override
	public boolean isSupportsCharacterSet(Connection connection) {
		return true;
	}

	@Override
	protected void createSyncDeleteTrigger(
			Connection connection, String sourceTableName,
			String targetTableName, String triggerName,
			String[] sourcePrimaryKeyColumnNames,
			String[] targetPrimaryKeyColumnNames)
		throws Exception {

		StringBundler sb = new StringBundler();

		sb.append("create trigger ");
		sb.append(triggerName);
		sb.append(" after delete on ");
		sb.append(sourceTableName);
		sb.append(" referencing old row as old for each row delete from ");
		sb.append(targetTableName);
		sb.append(" where ");

		for (int i = 0; i < sourcePrimaryKeyColumnNames.length; i++) {
			if (i > 0) {
				sb.append(" and ");
			}

			sb.append(targetPrimaryKeyColumnNames[i]);
			sb.append(" = old.");
			sb.append(sourcePrimaryKeyColumnNames[i]);
		}

		runSQL(connection, sb.toString());
	}

	@Override
	protected void createSyncInsertTrigger(
			Connection connection, String sourceTableName,
			String targetTableName, String triggerName,
			String[] sourceColumnNames, String[] targetColumnNames,
			String[] sourcePrimaryKeyColumnNames,
			String[] targetPrimaryKeyColumnNames,
			Map<String, String> defaultValuesMap)
		throws Exception {

		StringBundler sb = new StringBundler();

		sb.append("create trigger ");
		sb.append(triggerName);
		sb.append(" after insert on ");
		sb.append(sourceTableName);
		sb.append(" referencing new row as new for each row insert into ");
		sb.append(targetTableName);
		sb.append(" (");
		sb.append(StringUtil.merge(targetColumnNames, ", "));
		sb.append(") values (");

		for (int i = 0; i < sourceColumnNames.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}

			String defaultValue = defaultValuesMap.get(targetColumnNames[i]);

			if (defaultValue != null) {
				sb.append("COALESCE(");
			}

			sb.append("new.");
			sb.append(sourceColumnNames[i]);

			if (defaultValue == null) {
				continue;
			}

			sb.append(", ");
			sb.append(defaultValue);
			sb.append(")");
		}

		sb.append(")");

		runSQL(connection, sb.toString());
	}

	@Override
	protected void createSyncUpdateTrigger(
			Connection connection, String sourceTableName,
			String targetTableName, String triggerName,
			String[] sourceColumnNames, String[] targetColumnNames,
			String[] sourcePrimaryKeyColumnNames,
			String[] targetPrimaryKeyColumnNames,
			Map<String, String> defaultValuesMap)
		throws Exception {

		StringBundler sb = new StringBundler();

		sb.append("create trigger ");
		sb.append(triggerName);
		sb.append(" after update on ");
		sb.append(sourceTableName);
		sb.append(" referencing new row as new old row as old for each row ");
		sb.append("update ");
		sb.append(targetTableName);
		sb.append(" set ");

		for (int i = 0; i < sourceColumnNames.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}

			sb.append(targetColumnNames[i]);
			sb.append(" = ");

			String defaultValue = defaultValuesMap.get(targetColumnNames[i]);

			if (defaultValue != null) {
				sb.append("COALESCE(");
			}

			sb.append("new.");
			sb.append(sourceColumnNames[i]);

			if (defaultValue == null) {
				continue;
			}

			sb.append(", ");
			sb.append(defaultValue);
			sb.append(")");
		}

		sb.append(" where ");

		for (int i = 0; i < sourcePrimaryKeyColumnNames.length; i++) {
			if (i > 0) {
				sb.append(" and ");
			}

			sb.append(targetPrimaryKeyColumnNames[i]);
			sb.append(" = old.");
			sb.append(sourcePrimaryKeyColumnNames[i]);
		}

		runSQL(connection, sb.toString());
	}

	@Override
	protected String getCopyTableStructureSQL(
		String tableName, String newTableName) {

		return StringBundler.concat(
			"create table ", newTableName, " (like ", tableName, ")");
	}

	@Override
	protected int[] getSQLTypes() {
		return _SQL_TYPES;
	}

	@Override
	protected Map<String, Integer> getSQLVarcharSizes() {
		return HashMapBuilder.put(
			"STRING", SQL_VARCHAR_MAX_SIZE
		).put(
			"TEXT", SQL_VARCHAR_MAX_SIZE
		).build();
	}

	@Override
	protected String[] getTemplate() {
		return _HYPERSONIC;
	}

	@Override
	protected boolean isSupportsDDLRollback() {
		return false;
	}

	@Override
	protected boolean isSupportsDuplicatedIndexName() {
		return false;
	}

	@Override
	protected String reword(String data) throws IOException {
		if (Validator.isNull(data)) {
			return null;
		}

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(data))) {

			StringBundler sb = new StringBundler();

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				if (line.startsWith(ALTER_COLUMN_NAME)) {
					String[] template = buildColumnNameTokens(line);

					line = StringUtil.replace(
						"alter table @table@ alter column @old-column@ " +
							"rename to @new-column@;",
						REWORD_TEMPLATE, template);
				}
				else if (line.startsWith(ALTER_COLUMN_TYPE)) {
					String[] template = buildColumnTypeTokens(line);

					line = StringUtil.replace(
						"alter table @table@ alter column @old-column@ @type@;",
						REWORD_TEMPLATE, template);

					String defaultValue = template[template.length - 2];

					if (Validator.isBlank(defaultValue)) {
						line = line.concat(
							StringUtil.replace(
								"alter table @table@ alter column " +
									"@old-column@ set default null;",
								REWORD_TEMPLATE, template));
					}
					else {
						line = line.concat(
							StringUtil.replace(
								"alter table @table@ alter column " +
									"@old-column@ set default @default@;",
								REWORD_TEMPLATE, template));
					}

					String nullable = template[template.length - 1];

					if (!Validator.isBlank(nullable)) {
						line = line.concat(
							StringUtil.replace(
								"alter table @table@ alter column " +
									"@old-column@ set @nullable@;",
								REWORD_TEMPLATE, template));
					}
					else {
						line = line.concat(
							StringUtil.replace(
								"alter table @table@ alter column " +
									"@old-column@ set null;",
								REWORD_TEMPLATE, template));
					}
				}
				else if (line.startsWith(ALTER_TABLE_NAME)) {
					String[] template = buildTableNameTokens(line);

					line = StringUtil.replace(
						"alter table @old-table@ rename to @new-table@;",
						RENAME_TABLE_TEMPLATE, template);
				}
				else if (line.contains(DROP_INDEX)) {
					String[] tokens = StringUtil.split(line, ' ');

					line = StringUtil.replace(
						"drop index @index@;", "@index@", tokens[2]);
				}

				sb.append(line);
				sb.append("\n");
			}

			return sb.toString();
		}
	}

	private static final String[] _HYPERSONIC = {
		"//", "true", "false", "'1970-01-01 00:00:00'", "now()", " blob",
		" blob", " decimal(30, 16)", " bit", " timestamp", " double", " int",
		" bigint", " longvarchar", " longvarchar", " varchar", "", "commit"
	};

	private static final int[] _SQL_TYPES = {
		Types.BLOB, Types.BLOB, Types.DECIMAL, Types.BIT, Types.TIMESTAMP,
		Types.DOUBLE, Types.INTEGER, Types.BIGINT, Types.VARCHAR, Types.VARCHAR,
		Types.VARCHAR
	};

}