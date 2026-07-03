/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.petra.sql.dsl;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.Types;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Feliphe Marinho
 */
public class DynamicObjectDefinitionLocalizationTable
	extends BaseTable<DynamicObjectDefinitionLocalizationTable> {

	public DynamicObjectDefinitionLocalizationTable(
		ObjectDefinition objectDefinition, List<ObjectField> objectFields) {

		super(objectDefinition.getLocalizationDBTableName(), () -> null);

		_objectDefinition = objectDefinition;
		_objectFields = objectFields;

		_primaryKeyColumnNames = new String[] {
			objectDefinition.getPKObjectFieldDBColumnName(), "languageId"
		};

		createColumn(
			objectDefinition.getPKObjectFieldDBColumnName(), Long.class,
			Types.BIGINT, Column.FLAG_DEFAULT);
		createColumn(
			"languageId", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

		for (ObjectField objectField : objectFields) {
			createColumn(
				objectField.getDBColumnName(),
				DynamicObjectDefinitionTableUtil.getJavaClass(
					objectField.getDBType()),
				DynamicObjectDefinitionTableUtil.getSQLType(
					objectField.getDBType()),
				Column.FLAG_DEFAULT);
		}

		_objectFieldColumns = ListUtil.filter(
			new ArrayList<>(getColumns()),
			column -> {
				if (column.equals(getForeignKeyColumn()) ||
					column.equals(getLanguageIdColumn())) {

					return false;
				}

				return true;
			});
	}

	@Override
	public DynamicObjectDefinitionLocalizationTable as(String alias) {
		DynamicObjectDefinitionLocalizationTable
			dynamicObjectDefinitionLocalizationTable =
				new DynamicObjectDefinitionLocalizationTable(
					_objectDefinition, _objectFields);

		dynamicObjectDefinitionLocalizationTable.setAlias(alias);

		return dynamicObjectDefinitionLocalizationTable;
	}

	public String getCreateTableSQL() {
		StringBundler sb = new StringBundler();

		sb.append("create table ");
		sb.append(_objectDefinition.getLocalizationDBTableName());
		sb.append(" (");
		sb.append(_objectDefinition.getPKObjectFieldDBColumnName());
		sb.append(" LONG not null, languageId VARCHAR(10) not null");

		for (ObjectField objectField : _objectFields) {
			sb.append(", ");
			sb.append(objectField.getDBColumnName());
			sb.append(" ");
			sb.append(
				DynamicObjectDefinitionTableUtil.getDataType(
					objectField.getBusinessType(), objectField.getDBType()));
		}

		sb.append(", primary key (");
		sb.append(StringUtil.merge(_primaryKeyColumnNames));
		sb.append("));");

		String sql = sb.toString();

		if (_log.isDebugEnabled()) {
			_log.debug("SQL: " + sql);
		}

		return sql;
	}

	public Column<DynamicObjectDefinitionLocalizationTable, Long>
		getForeignKeyColumn() {

		return (Column<DynamicObjectDefinitionLocalizationTable, Long>)
			getColumn(_objectDefinition.getPKObjectFieldDBColumnName());
	}

	public String getForeignKeyColumnName() {
		Column<DynamicObjectDefinitionLocalizationTable, Long>
			foreignKeyColumn = getForeignKeyColumn();

		return foreignKeyColumn.getName();
	}

	public Column<DynamicObjectDefinitionLocalizationTable, String>
		getLanguageIdColumn() {

		return (Column<DynamicObjectDefinitionLocalizationTable, String>)
			getColumn("languageId");
	}

	public ObjectDefinition getObjectDefinition() {
		return _objectDefinition;
	}

	public List<Column<DynamicObjectDefinitionLocalizationTable, ?>>
		getObjectFieldColumns() {

		return _objectFieldColumns;
	}

	public List<ObjectField> getObjectFields() {
		return _objectFields;
	}

	public String[] getPrimaryKeyColumnNames() {
		return _primaryKeyColumnNames;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DynamicObjectDefinitionLocalizationTable.class);

	private final ObjectDefinition _objectDefinition;
	private final List<Column<DynamicObjectDefinitionLocalizationTable, ?>>
		_objectFieldColumns;
	private final List<ObjectField> _objectFields;
	private final String[] _primaryKeyColumnNames;

}