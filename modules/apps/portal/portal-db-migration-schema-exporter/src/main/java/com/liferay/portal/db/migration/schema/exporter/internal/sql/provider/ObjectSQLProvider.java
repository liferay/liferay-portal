/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.migration.schema.exporter.internal.sql.provider;

import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionLocalizationTable;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionLocalizationTableFactory;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTable;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTableFactory;
import com.liferay.object.petra.sql.dsl.DynamicObjectRelationshipMappingTable;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.sql.Connection;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

/**
 * @author Mariano Álvaro Sáiz
 */
public class ObjectSQLProvider implements SQLProvider {

	public ObjectSQLProvider(long companyId, DB db) throws Exception {
		this(new long[] {companyId}, db);
	}

	public ObjectSQLProvider(long[] companyIds, DB db) throws Exception {
		_companyIds = companyIds;
		_db = db;

		_appendSQL();
	}

	@Override
	public String getIndexesSQL() {
		return _indexesSQLSB.toString();
	}

	@Override
	public String getTablesSQL() {
		return _tablesSQLSB.toString();
	}

	private void _appendIndexesSQL() throws Exception {
		DataSource dataSource = InfrastructureUtil.getDataSource();

		// LPD-25786 Do not reuse _db

		DB sourceDB = DBManagerUtil.getDB();

		try (Connection connection = dataSource.getConnection()) {
			for (String tableName : _tableNames) {
				for (IndexMetadata indexMetadata :
						sourceDB.getIndexMetadatas(
							connection, tableName, null, false)) {

					_indexesSQLSB.append(indexMetadata.getCreateSQL(null));
					_indexesSQLSB.append(StringPool.NEW_LINE);
				}
			}
		}
	}

	private void _appendRelationshipTablesSQL(
			DBInspector dbInspector, ObjectDefinition objectDefinition)
		throws Exception {

		List<ObjectRelationship> objectRelationships =
			ObjectRelationshipLocalServiceUtil.getAllObjectRelationships(
				objectDefinition.getObjectDefinitionId());

		for (ObjectRelationship objectRelationship : objectRelationships) {
			if (!StringUtil.equalsIgnoreCase(
					objectRelationship.getType(),
					ObjectRelationshipConstants.TYPE_MANY_TO_MANY) ||
				_tableNames.contains(objectRelationship.getDBTableName())) {

				continue;
			}

			Map<String, String> pkObjectFieldDBColumnNames =
				ObjectRelationshipUtil.getPKObjectFieldDBColumnNames(
					ObjectDefinitionLocalServiceUtil.getObjectDefinition(
						objectRelationship.getObjectDefinitionId1()),
					ObjectDefinitionLocalServiceUtil.getObjectDefinition(
						objectRelationship.getObjectDefinitionId2()),
					false);

			String pkObjectFieldDBColumnName1 = pkObjectFieldDBColumnNames.get(
				"pkObjectFieldDBColumnName1");
			String pkObjectFieldDBColumnName2 = pkObjectFieldDBColumnNames.get(
				"pkObjectFieldDBColumnName2");

			DynamicObjectRelationshipMappingTable
				dynamicObjectRelationshipMappingTable =
					new DynamicObjectRelationshipMappingTable(
						pkObjectFieldDBColumnName1, pkObjectFieldDBColumnName2,
						objectRelationship.getDBTableName());

			_appendTableSQL(
				dbInspector,
				dynamicObjectRelationshipMappingTable.getCreateTableSQL(),
				dynamicObjectRelationshipMappingTable.getTableName());
		}
	}

	private void _appendSQL() throws Exception {
		DataSource dataSource = InfrastructureUtil.getDataSource();

		try (Connection connection = dataSource.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			for (long companyId : _companyIds) {
				List<ObjectDefinition> objectDefinitions =
					ObjectDefinitionLocalServiceUtil.getObjectDefinitions(
						companyId, WorkflowConstants.STATUS_APPROVED);

				for (ObjectDefinition objectDefinition : objectDefinitions) {
					_appendTablesSQL(dbInspector, objectDefinition);

					_appendRelationshipTablesSQL(dbInspector, objectDefinition);
				}
			}
		}

		if (_tablesSQLSB.index() > 0) {
			_tablesSQLSB.setIndex(_tablesSQLSB.index() - 1);
		}

		_appendIndexesSQL();
	}

	private void _appendTableSQL(
			DBInspector dbInspector, String sql, String tableName)
		throws Exception {

		if (!dbInspector.hasTable(tableName)) {
			return;
		}

		_tableNames.add(tableName);

		_tablesSQLSB.append(_db.buildSQL(sql));
		_tablesSQLSB.append(StringPool.NEW_LINE);
	}

	private void _appendTablesSQL(
			DBInspector dbInspector, ObjectDefinition objectDefinition)
		throws Exception {

		DynamicObjectDefinitionLocalizationTable
			dynamicObjectDefinitionLocalizationTable =
				DynamicObjectDefinitionLocalizationTableFactory.create(
					objectDefinition, ObjectFieldLocalServiceUtil.getService());

		if (dynamicObjectDefinitionLocalizationTable != null) {
			_appendTableSQL(
				dbInspector,
				dynamicObjectDefinitionLocalizationTable.getCreateTableSQL(),
				dynamicObjectDefinitionLocalizationTable.getTableName());
		}

		if (!objectDefinition.isUnmodifiableSystemObject()) {
			DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
				DynamicObjectDefinitionTableFactory.create(
					objectDefinition, ObjectFieldLocalServiceUtil.getService());

			_appendTableSQL(
				dbInspector, dynamicObjectDefinitionTable.getCreateTableSQL(),
				dynamicObjectDefinitionTable.getTableName());
		}

		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			DynamicObjectDefinitionTableFactory.createExtension(
				objectDefinition, ObjectFieldLocalServiceUtil.getService());

		_appendTableSQL(
			dbInspector, dynamicObjectDefinitionTable.getCreateTableSQL(),
			dynamicObjectDefinitionTable.getTableName());
	}

	private final long[] _companyIds;
	private final DB _db;
	private final StringBundler _indexesSQLSB = new StringBundler();
	private final Set<String> _tableNames = new HashSet<>();
	private final StringBundler _tablesSQLSB = new StringBundler();

}