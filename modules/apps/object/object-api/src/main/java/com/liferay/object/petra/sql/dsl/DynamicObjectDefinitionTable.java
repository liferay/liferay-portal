/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.petra.sql.dsl;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.sql.Types;

import java.util.List;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public class DynamicObjectDefinitionTable
	extends BaseTable<DynamicObjectDefinitionTable> {

	public DynamicObjectDefinitionTable(
		ObjectDefinition objectDefinition, List<ObjectField> objectFields,
		String tableName) {

		super(tableName, () -> null);

		_objectDefinition = objectDefinition;
		_objectFields = objectFields;
		_tableName = tableName;

		_primaryKeyColumnName = objectDefinition.getPKObjectFieldDBColumnName();

		createColumn(
			_primaryKeyColumnName, Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);

		for (ObjectField objectField : objectFields) {
			if (!objectField.hasInsertValues() || objectField.isLocalized()) {
				continue;
			}

			for (String dbColumnName : objectField.getDBColumnNames()) {
				_createColumn(dbColumnName, objectField.getDBType());
			}

			if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_AUTO_INCREMENT)) {

				_createColumn(
					objectField.getSortableDBColumnName(),
					ObjectFieldConstants.DB_TYPE_LONG);
			}
		}
	}

	@Override
	public DynamicObjectDefinitionTable as(String alias) {
		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			new DynamicObjectDefinitionTable(
				_objectDefinition, _objectFields, _tableName);

		dynamicObjectDefinitionTable.setAlias(alias);

		return dynamicObjectDefinitionTable;
	}

	/**
	 * @see com.liferay.portal.tools.service.builder.ServiceBuilder#_getCreateTableSQL(
	 *      com.liferay.portal.tools.service.builder.Entity)
	 */
	public String getCreateTableSQL() {
		StringBundler sb = new StringBundler();

		sb.append("create table ");
		sb.append(_tableName);
		sb.append(" (");
		sb.append(_primaryKeyColumnName);
		sb.append(" LONG not null primary key");

		for (ObjectField objectField : _objectFields) {
			if (!objectField.hasInsertValues() || objectField.isLocalized()) {
				continue;
			}

			for (String dbColumnName : objectField.getDBColumnNames()) {
				_append(
					sb, objectField.getBusinessType(), dbColumnName,
					objectField.getDBType());
			}

			if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_AUTO_INCREMENT)) {

				_append(
					sb, objectField.getBusinessType(),
					objectField.getSortableDBColumnName(),
					ObjectFieldConstants.DB_TYPE_LONG);
			}
		}

		sb.append(");");

		String sql = sb.toString();

		if (_log.isDebugEnabled()) {
			_log.debug("SQL: " + sql);
		}

		return sql;
	}

	public ObjectDefinition getObjectDefinition() {
		return _objectDefinition;
	}

	public List<ObjectField> getObjectFields() {
		return _objectFields;
	}

	public Column<DynamicObjectDefinitionTable, Long> getPrimaryKeyColumn() {
		return (Column<DynamicObjectDefinitionTable, Long>)getColumn(
			_primaryKeyColumnName);
	}

	public String getPrimaryKeyColumnName() {
		return _primaryKeyColumnName;
	}

	@Override
	protected <C> Column<DynamicObjectDefinitionTable, C> createColumn(
		String name, Class<C> javaClass, int sqlType, int flags) {

		return super.createColumn(name, javaClass, sqlType, flags);
	}

	private void _append(
		StringBundler sb, String businessType, String dbColumnName,
		String dbType) {

		sb.append(", ");
		sb.append(dbColumnName);
		sb.append(" ");
		sb.append(
			DynamicObjectDefinitionTableUtil.getDataType(businessType, dbType));
		sb.append(DynamicObjectDefinitionTableUtil.getSQLColumnNull(dbType));
	}

	private void _createColumn(String dbColumnName, String dbType) {
		createColumn(
			dbColumnName, DynamicObjectDefinitionTableUtil.getJavaClass(dbType),
			DynamicObjectDefinitionTableUtil.getSQLType(dbType),
			Column.FLAG_DEFAULT);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DynamicObjectDefinitionTable.class);

	private final ObjectDefinition _objectDefinition;
	private final List<ObjectField> _objectFields;
	private final String _primaryKeyColumnName;
	private final String _tableName;

}