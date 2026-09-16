/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.petra.sql.dsl;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.math.BigDecimal;

import java.sql.Blob;
import java.sql.Timestamp;
import java.sql.Types;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Feliphe Marinho
 */
public class DynamicObjectDefinitionTableUtil {

	/**
	 * @see com.liferay.portal.dao.db.BaseDB#alterTableAddColumn(
	 *      java.sql.Connection, String, String, String)
	 */
	public static String getAlterTableAddColumnSQL(
		String tableName, String businessType, String columnName,
		String dbType) {

		String sql = StringBundler.concat(
			"alter table ", tableName, " add ", columnName, StringPool.SPACE,
			getDataType(businessType, dbType), getSQLColumnNull(dbType));

		if (_log.isDebugEnabled()) {
			_log.debug("SQL: " + sql);
		}

		return sql;
	}

	public static String getDataType(String businessType, String dbType) {
		String dataType = _dataTypes.get(dbType);

		if (!StringUtil.equals(dataType, "VARCHAR")) {
			return dataType;
		}

		return StringBundler.concat(
			dataType, "(", getMaxLength(businessType), ")");
	}

	public static DynamicObjectDefinitionTable getDynamicObjectDefinitionTable(
		boolean extension, ObjectDefinition objectDefinition,
		List<ObjectField> objectFields) {

		String dbTableName = objectDefinition.getDBTableName();

		if (extension) {
			dbTableName = objectDefinition.getExtensionDBTableName();
		}

		String finalDBTableName = dbTableName;

		return new DynamicObjectDefinitionTable(
			objectDefinition,
			ListUtil.filter(
				objectFields,
				objectField -> Objects.equals(
					finalDBTableName, objectField.getDBTableName())),
			dbTableName);
	}

	public static DynamicObjectDefinitionTable getDynamicObjectDefinitionTable(
			boolean extension, ObjectDefinition objectDefinition,
			ObjectFieldLocalService objectFieldLocalService)
		throws PortalException {

		// TODO Cache this across the cluster with proper invalidation when the
		// object definition or its object fields are updated

		String dbTableName = objectDefinition.getDBTableName();

		if (extension) {
			dbTableName = objectDefinition.getExtensionDBTableName();
		}

		return new DynamicObjectDefinitionTable(
			objectDefinition,
			objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId(), dbTableName),
			dbTableName);
	}

	public static String getInsertMissingExtensionTableRowsSQL(
		String extensionTableName, String primaryKeyColumnName,
		String tableName) {

		String sql = StringBundler.concat(
			"insert into ", extensionTableName, " (", primaryKeyColumnName,
			") select ", tableName, StringPool.PERIOD, primaryKeyColumnName,
			" from ", tableName, " left join ", extensionTableName, " on ",
			extensionTableName, StringPool.PERIOD, primaryKeyColumnName, " = ",
			tableName, StringPool.PERIOD, primaryKeyColumnName, " where ",
			extensionTableName, StringPool.PERIOD, primaryKeyColumnName,
			" is null");

		if (_log.isDebugEnabled()) {
			_log.debug("SQL: " + sql);
		}

		return sql;
	}

	public static Class<?> getJavaClass(String dbType) {
		return _javaClasses.get(dbType);
	}

	public static int getMaxLength(String businessType) {
		if (StringUtil.equals(
				businessType,
				ObjectFieldConstants.BUSINESS_TYPE_EMAIL_ADDRESS)) {

			return 254;
		}
		else if (StringUtil.equals(
					businessType,
					ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT)) {

			return 65000;
		}
		else if (StringUtil.equals(
					businessType,
					ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

			return 75;
		}

		return 280;
	}

	public static String getSQLColumnNull(String dbType) {
		if (dbType.equals("BigDecimal") || dbType.equals("Double") ||
			dbType.equals("Integer") || dbType.equals("Long")) {

			return " default 0";
		}
		else if (dbType.equals("Boolean")) {
			return " default FALSE";
		}
		else if (dbType.equals("Date") || dbType.equals("DateTime")) {
			return " null";
		}

		return StringPool.BLANK;
	}

	public static Integer getSQLType(String dbType) {
		return _sqlTypes.get(dbType);
	}

	public static String getUpdateDefaultValueSQL(
		String columnName, String dbType, Object defaultValue,
		String tableName) {

		String sql = StringPool.BLANK;

		if (dbType.equals(ObjectFieldConstants.DB_TYPE_STRING)) {
			sql = StringBundler.concat(
				"update ", tableName, " set ", columnName, " = '", defaultValue,
				"' where ", columnName, " is null");
		}

		if (_log.isDebugEnabled()) {
			_log.debug("SQL: " + sql);
		}

		return sql;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DynamicObjectDefinitionTableUtil.class);

	private static final Map<String, String> _dataTypes = HashMapBuilder.put(
		"BigDecimal", "BIGDECIMAL"
	).put(
		"Blob", "BLOB"
	).put(
		"Boolean", "BOOLEAN"
	).put(
		"Clob", "TEXT"
	).put(
		"Date", "DATE"
	).put(
		"DateTime", "DATE"
	).put(
		"Double", "DOUBLE"
	).put(
		"Integer", "INTEGER"
	).put(
		"Long", "LONG"
	).put(
		"String", "VARCHAR"
	).build();
	private static final Map<String, Class<?>> _javaClasses =
		HashMapBuilder.<String, Class<?>>put(
			"BigDecimal", BigDecimal.class
		).put(
			"Blob", Blob.class
		).put(
			"Boolean", Boolean.class
		).put(
			"Clob", String.class
		).put(
			"Date", Date.class
		).put(
			"DateTime", Timestamp.class
		).put(
			"Double", Double.class
		).put(
			"Integer", Integer.class
		).put(
			"Long", Long.class
		).put(
			"String", String.class
		).build();
	private static final Map<String, Integer> _sqlTypes = HashMapBuilder.put(
		"BigDecimal", Types.DECIMAL
	).put(
		"Blob", Types.BLOB
	).put(
		"Boolean", Types.BOOLEAN
	).put(
		"Clob", Types.CLOB
	).put(
		"Date", Types.DATE
	).put(
		"DateTime", Types.TIMESTAMP
	).put(
		"Double", Types.DOUBLE
	).put(
		"Integer", Types.INTEGER
	).put(
		"Long", Types.BIGINT
	).put(
		"String", Types.VARCHAR
	).build();

}