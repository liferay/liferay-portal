/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.related.models;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.exception.RequiredObjectRelationshipException;
import com.liferay.object.internal.entry.util.ObjectEntrySearchUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTable;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;

import java.io.Serializable;

import java.util.List;
import java.util.Objects;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public class SystemObject1toMObjectRelatedModelsProviderImpl
	<T extends BaseModel<T>>
		implements ObjectRelatedModelsProvider<T> {

	public SystemObject1toMObjectRelatedModelsProviderImpl(
		ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		SystemObjectDefinitionManager systemObjectDefinitionManager,
		SystemObjectDefinitionManagerRegistry
			systemObjectDefinitionManagerRegistry) {

		_objectDefinition = objectDefinition;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectFieldLocalService = objectFieldLocalService;
		_objectRelationshipLocalService = objectRelationshipLocalService;
		_systemObjectDefinitionManager = systemObjectDefinitionManager;
		_systemObjectDefinitionManagerRegistry =
			systemObjectDefinitionManagerRegistry;

		_table = systemObjectDefinitionManager.getTable();
	}

	@Override
	public void deleteRelatedModel(
			long userId, long groupId, long objectRelationshipId,
			long primaryKey, String deletionType)
		throws PortalException {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				objectRelationshipId);

		List<T> relatedModels = getRelatedModels(
			groupId, objectRelationshipId, primaryKey, null, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		if (relatedModels.isEmpty()) {
			return;
		}

		if (Objects.equals(
				deletionType,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE)) {

			SystemObjectDefinitionManager systemObjectDefinitionManager =
				_systemObjectDefinitionManagerRegistry.
					getSystemObjectDefinitionManager(
						_objectDefinition.getName());

			for (BaseModel<T> baseModel : relatedModels) {
				systemObjectDefinitionManager.deleteBaseModel(baseModel);
			}
		}
		else if (Objects.equals(
					deletionType,
					ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE)) {

			ObjectField objectField = _objectFieldLocalService.getObjectField(
				objectRelationship.getObjectFieldId2());

			for (BaseModel<T> baseModel : relatedModels) {
				_objectEntryLocalService.insertIntoOrUpdateExtensionTable(
					userId, objectRelationship.getObjectDefinitionId2(),
					GetterUtil.getLong(baseModel.getPrimaryKeyObj()),
					HashMapBuilder.<String, Serializable>put(
						objectField.getName(), 0
					).build());
			}
		}
		else if (Objects.equals(
					deletionType,
					ObjectRelationshipConstants.DELETION_TYPE_PREVENT)) {

			throw new RequiredObjectRelationshipException(objectRelationship);
		}
	}

	@Override
	public void disassociateRelatedModels(
			long userId, long objectRelationshipId, long primaryKey1,
			long primaryKey2)
		throws PortalException {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				objectRelationshipId);

		_objectEntryLocalService.insertIntoOrUpdateExtensionTable(
			userId, objectRelationship.getObjectDefinitionId2(),
			GetterUtil.getLong(primaryKey2),
			HashMapBuilder.<String, Serializable>put(
				() -> {
					ObjectField objectField =
						_objectFieldLocalService.getObjectField(
							objectRelationship.getObjectFieldId2());

					return objectField.getName();
				},
				0
			).build());
	}

	@Override
	public T fetchRelatedModel(
			long groupId, long objectRelationshipId, long primaryKey)
		throws PortalException {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				objectRelationshipId);

		ObjectDefinition relatedObjectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2());

		if (relatedObjectDefinition.isUnmodifiableSystemObject()) {
			throw new UnsupportedOperationException();
		}

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(
					_systemObjectDefinitionManager.getModelClassName());

		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			_getDynamicObjectDefinitionTable(
				relatedObjectDefinition.getObjectDefinitionId());

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId1());

		Column<DynamicObjectDefinitionTable, Long> column =
			(Column<DynamicObjectDefinitionTable, Long>)
				dynamicObjectDefinitionTable.getColumn(
					StringBundler.concat(
						"r_", objectRelationship.getName(), "_",
						objectDefinition.getPKObjectFieldName()));

		if (column == null) {
			dynamicObjectDefinitionTable =
				_getExtensionDynamicObjectDefinitionTable(
					relatedObjectDefinition.getObjectDefinitionId());
		}

		FromStep fromStep = DSLQueryFactoryUtil.selectDistinct(_table);
		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());
		Column<DynamicObjectDefinitionTable, Long> primaryKeyColumn =
			dynamicObjectDefinitionTable.getPrimaryKeyColumn();

		List<T> relatedModels = persistedModelLocalService.dslQuery(
			fromStep.from(
				_table
			).innerJoinON(
				dynamicObjectDefinitionTable,
				_systemObjectDefinitionManager.getPrimaryKeyColumn(
				).eq(
					(Expression<Long>)dynamicObjectDefinitionTable.getColumn(
						objectField.getDBColumnName())
				)
			).where(
				primaryKeyColumn.eq(
					primaryKey
				).and(
					_table.getColumn(
						"companyId"
					).eq(
						groupId
					)
				)
			));

		if (relatedModels.isEmpty()) {
			return null;
		}

		return relatedModels.get(0);
	}

	@Override
	public String getClassName() {
		return _systemObjectDefinitionManager.getModelClassName();
	}

	@Override
	public long getCompanyId() {
		return _objectDefinition.getCompanyId();
	}

	@Override
	public String getObjectRelationshipType() {
		return ObjectRelationshipConstants.TYPE_ONE_TO_MANY;
	}

	@Override
	public List<T> getRelatedModels(
			long groupId, long objectRelationshipId, long primaryKey,
			String search, int start, int end)
		throws PortalException {

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(
					_systemObjectDefinitionManager.getModelClassName());

		DSLQuery dslQuery = _getGroupByStep(
			_getDynamicObjectDefinitionTable(),
			DSLQueryFactoryUtil.selectDistinct(_table), groupId,
			objectRelationshipId, primaryKey, search
		).orderBy(
			_systemObjectDefinitionManager.getPrimaryKeyColumn(
			).ascending()
		).limit(
			start, end
		);

		return persistedModelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int getRelatedModelsCount(
			long groupId, long objectRelationshipId, long primaryKey,
			String search)
		throws PortalException {

		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			_getDynamicObjectDefinitionTable();

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(
					_systemObjectDefinitionManager.getModelClassName());

		return persistedModelLocalService.dslQueryCount(
			_getGroupByStep(
				dynamicObjectDefinitionTable,
				DSLQueryFactoryUtil.countDistinct(
					dynamicObjectDefinitionTable.getPrimaryKeyColumn()),
				groupId, objectRelationshipId, primaryKey, search));
	}

	@Override
	public List<T> getUnrelatedModels(
			long companyId, long groupId, ObjectDefinition objectDefinition,
			long objectEntryId, long objectRelationshipId, String search,
			int start, int end)
		throws PortalException {

		DSLQuery dslQuery = _getUnrelatedModelsGroupByStep(
			companyId, DSLQueryFactoryUtil.select(_table), groupId,
			objectDefinition, objectRelationshipId
		).orderBy(
			_systemObjectDefinitionManager.getPrimaryKeyColumn(
			).ascending()
		).limit(
			start, end
		);

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(objectDefinition.getClassName());

		return persistedModelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int getUnrelatedModelsCount(
			long companyId, long groupId, ObjectDefinition objectDefinition,
			long objectEntryId, long objectRelationshipId, String search)
		throws PortalException {

		DSLQuery dslQuery = _getUnrelatedModelsGroupByStep(
			companyId, DSLQueryFactoryUtil.count(), groupId, objectDefinition,
			objectRelationshipId);

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(objectDefinition.getClassName());

		return persistedModelLocalService.dslQueryCount(dslQuery);
	}

	private DynamicObjectDefinitionTable _getDynamicObjectDefinitionTable()
		throws PortalException {

		// TODO Cache this across the cluster with proper invalidation when the
		// object definition or its object fields are updated

		return new DynamicObjectDefinitionTable(
			_objectDefinition,
			_objectFieldLocalService.getObjectFields(
				_objectDefinition.getObjectDefinitionId(),
				_objectDefinition.getExtensionDBTableName()),
			_objectDefinition.getExtensionDBTableName());
	}

	private DynamicObjectDefinitionTable _getDynamicObjectDefinitionTable(
			long objectDefinitionId)
		throws PortalException {

		// TODO Cache this across the cluster with proper invalidation when the
		// object definition or its object fields are updated

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectDefinitionId);

		return new DynamicObjectDefinitionTable(
			objectDefinition,
			_objectFieldLocalService.getObjectFields(
				objectDefinitionId, objectDefinition.getDBTableName()),
			objectDefinition.getDBTableName());
	}

	private DynamicObjectDefinitionTable
			_getExtensionDynamicObjectDefinitionTable(long objectDefinitionId)
		throws PortalException {

		// TODO Cache this across the cluster with proper invalidation when the
		// object definition or its object fields are updated

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectDefinitionId);

		return new DynamicObjectDefinitionTable(
			objectDefinition,
			_objectFieldLocalService.getObjectFields(
				objectDefinitionId, objectDefinition.getExtensionDBTableName()),
			objectDefinition.getExtensionDBTableName());
	}

	private GroupByStep _getGroupByStep(
			DynamicObjectDefinitionTable dynamicObjectDefinitionTable,
			FromStep fromStep, long groupId, long objectRelationshipId,
			long primaryKey, String search)
		throws PortalException {

		Column<?, Long> primaryKeyColumn = null;

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				objectRelationshipId);

		ObjectDefinition objectDefinition1 =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId1());
		ObjectDefinition objectDefinition2 =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2());

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		if (Objects.equals(objectField.getDBTableName(), _table)) {
			primaryKeyColumn = (Column<?, Long>)_table.getColumn(
				objectField.getDBColumnName());
		}
		else {
			primaryKeyColumn =
				(Column<DynamicObjectDefinitionTable, Long>)
					dynamicObjectDefinitionTable.getColumn(
						objectField.getDBColumnName());
		}

		return fromStep.from(
			_table
		).innerJoinON(
			dynamicObjectDefinitionTable,
			dynamicObjectDefinitionTable.getPrimaryKeyColumn(
			).eq(
				_systemObjectDefinitionManager.getPrimaryKeyColumn()
			)
		).where(
			primaryKeyColumn.eq(
				primaryKey
			).and(
				() -> {
					Column<?, Long> groupIdColumn = _table.getColumn("groupId");

					if ((groupIdColumn == null) ||
						Objects.equals(
							ObjectDefinitionConstants.SCOPE_COMPANY,
							objectDefinition1.getScope()) ||
						Objects.equals(
							ObjectDefinitionConstants.SCOPE_COMPANY,
							objectDefinition2.getScope())) {

						return null;
					}

					return groupIdColumn.eq(groupId);
				}
			).and(
				() -> {
					Column<?, Long> companyIdColumn = _table.getColumn(
						"companyId");

					if (companyIdColumn == null) {
						return null;
					}

					return companyIdColumn.eq(objectField.getCompanyId());
				}
			).and(
				ObjectEntrySearchUtil.getRelatedModelsPredicate(
					objectDefinition2, _objectFieldLocalService, search,
					dynamicObjectDefinitionTable)
			)
		);
	}

	private GroupByStep _getUnrelatedModelsGroupByStep(
			long companyId, FromStep fromStep, long groupId,
			ObjectDefinition objectDefinition, long objectRelationshipId)
		throws PortalException {

		Column<?, Long> companyIdColumn = (Column<?, Long>)_table.getColumn(
			"companyId");

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				objectRelationshipId);

		ObjectDefinition objectDefinition1 =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId1());

		return fromStep.from(
			_table
		).where(
			companyIdColumn.eq(
				companyId
			).and(
				() -> {
					Column<?, Long> groupIdColumn = _table.getColumn("groupId");

					if ((groupIdColumn == null) ||
						Objects.equals(
							ObjectDefinitionConstants.SCOPE_COMPANY,
							objectDefinition1.getScope())) {

						return null;
					}

					ObjectDefinition objectDefinition2 =
						_objectDefinitionLocalService.getObjectDefinition(
							objectRelationship.getObjectDefinitionId2());

					if (Objects.equals(
							ObjectDefinitionConstants.SCOPE_COMPANY,
							objectDefinition2.getScope())) {

						return null;
					}

					return groupIdColumn.eq(groupId);
				}
			).and(
				() -> {
					Column<?, Long> primaryKeyColumn = _table.getColumn(
						objectDefinition.getPKObjectFieldDBColumnName());

					DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
						_getDynamicObjectDefinitionTable();
					ObjectField objectField =
						_objectFieldLocalService.getObjectField(
							objectRelationship.getObjectFieldId2());

					Column<DynamicObjectDefinitionTable, Long>
						foreignKeyColumn =
							(Column<DynamicObjectDefinitionTable, Long>)
								dynamicObjectDefinitionTable.getColumn(
									objectField.getDBColumnName());

					return primaryKeyColumn.notIn(
						DSLQueryFactoryUtil.select(
							dynamicObjectDefinitionTable.getPrimaryKeyColumn()
						).from(
							dynamicObjectDefinitionTable
						).where(
							foreignKeyColumn.neq(0L)
						));
				}
			)
		);
	}

	private final ObjectDefinition _objectDefinition;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;
	private final SystemObjectDefinitionManager _systemObjectDefinitionManager;
	private final SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;
	private final Table _table;

}