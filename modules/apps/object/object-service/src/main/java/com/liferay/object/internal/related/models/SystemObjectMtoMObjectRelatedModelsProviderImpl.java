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
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTable;
import com.liferay.object.petra.sql.dsl.DynamicObjectRelationshipMappingTable;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author Marcela Cunha
 */
public class SystemObjectMtoMObjectRelatedModelsProviderImpl
	<T extends BaseModel<T>>
		implements ObjectRelatedModelsProvider<T> {

	public SystemObjectMtoMObjectRelatedModelsProviderImpl(
		ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		SystemObjectDefinitionManager systemObjectDefinitionManager,
		SystemObjectDefinitionManagerRegistry
			systemObjectDefinitionManagerRegistry) {

		_objectDefinition = objectDefinition;
		_objectDefinitionLocalService = objectDefinitionLocalService;
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

		List<T> relatedModels = getRelatedModels(
			groupId, objectRelationshipId, primaryKey, null, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		if (relatedModels.isEmpty()) {
			return;
		}

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				objectRelationshipId);

		if (Objects.equals(
				deletionType,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT) &&
			!objectRelationship.isReverse()) {

			throw new RequiredObjectRelationshipException(objectRelationship);
		}

		_objectRelationshipLocalService.
			deleteObjectRelationshipMappingTableValues(
				objectRelationshipId, primaryKey);

		if (Objects.equals(
				deletionType,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE) &&
			!objectRelationship.isReverse()) {

			SystemObjectDefinitionManager systemObjectDefinitionManager =
				_systemObjectDefinitionManagerRegistry.
					getSystemObjectDefinitionManager(
						_objectDefinition.getName());

			for (BaseModel<T> baseModel : relatedModels) {
				systemObjectDefinitionManager.deleteBaseModel(baseModel);
			}
		}
	}

	@Override
	public void disassociateRelatedModels(
			long userId, long objectRelationshipId, long primaryKey1,
			long primaryKey2)
		throws PortalException {

		_objectRelationshipLocalService.
			deleteObjectRelationshipMappingTableValues(
				objectRelationshipId, primaryKey1, primaryKey2);
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
		return ObjectRelationshipConstants.TYPE_MANY_TO_MANY;
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

		return persistedModelLocalService.dslQuery(
			_getGroupByStep(
				DSLQueryFactoryUtil.selectDistinct(_table), groupId,
				objectRelationshipId, primaryKey, search
			).orderBy(
				_systemObjectDefinitionManager.getPrimaryKeyColumn(
				).ascending()
			).limit(
				start, end
			));
	}

	@Override
	public int getRelatedModelsCount(
			long groupId, long objectRelationshipId, long primaryKey,
			String search)
		throws PortalException {

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(
					_systemObjectDefinitionManager.getModelClassName());

		return persistedModelLocalService.dslQueryCount(
			_getGroupByStep(
				DSLQueryFactoryUtil.countDistinct(
					_table.getColumn(
						_objectDefinition.getPKObjectFieldDBColumnName())),
				groupId, objectRelationshipId, primaryKey, search));
	}

	@Override
	public List<T> getUnrelatedModels(
			long companyId, long groupId, ObjectDefinition objectDefinition,
			long objectEntryId, long objectRelationshipId, String search,
			int start, int end)
		throws PortalException {

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(objectDefinition.getClassName());

		return persistedModelLocalService.dslQuery(
			_getUnrelatedModelsGroupByStep(
				companyId, DSLQueryFactoryUtil.selectDistinct(_table), groupId,
				objectDefinition, objectEntryId, objectRelationshipId
			).orderBy(
				_systemObjectDefinitionManager.getPrimaryKeyColumn(
				).ascending()
			).limit(
				start, end
			));
	}

	@Override
	public int getUnrelatedModelsCount(
			long companyId, long groupId, ObjectDefinition objectDefinition,
			long objectEntryId, long objectRelationshipId, String search)
		throws PortalException {

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(objectDefinition.getClassName());

		return persistedModelLocalService.dslQueryCount(
			_getUnrelatedModelsGroupByStep(
				companyId,
				DSLQueryFactoryUtil.countDistinct(
					_table.getColumn(
						_objectDefinition.getPKObjectFieldDBColumnName())),
				groupId, objectDefinition, objectEntryId,
				objectRelationshipId));
	}

	private GroupByStep _getGroupByStep(
			FromStep fromStep, long groupId, long objectRelationshipId,
			long primaryKey, String search)
		throws PortalException {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				objectRelationshipId);

		ObjectDefinition objectDefinition1 =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId1());

		ObjectDefinition objectDefinition2 =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2());

		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			new DynamicObjectDefinitionTable(
				objectDefinition2,
				_objectFieldLocalService.getObjectFields(
					objectRelationship.getObjectDefinitionId2(),
					objectDefinition2.getDBTableName()),
				objectDefinition2.getDBTableName());

		DynamicObjectRelationshipMappingTable
			dynamicObjectRelationshipMappingTable =
				new DynamicObjectRelationshipMappingTable(
					objectDefinition1.getPKObjectFieldDBColumnName(),
					objectDefinition2.getPKObjectFieldDBColumnName(),
					objectRelationship.getDBTableName());

		Column<DynamicObjectRelationshipMappingTable, Long> primaryKeyColumn1 =
			dynamicObjectRelationshipMappingTable.getPrimaryKeyColumn1();
		Column<DynamicObjectRelationshipMappingTable, Long> primaryKeyColumn2 =
			dynamicObjectRelationshipMappingTable.getPrimaryKeyColumn2();

		return fromStep.from(
			dynamicObjectDefinitionTable
		).innerJoinON(
			dynamicObjectRelationshipMappingTable,
			primaryKeyColumn2.eq(
				dynamicObjectDefinitionTable.getPrimaryKeyColumn())
		).where(
			primaryKeyColumn1.eq(
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

					return companyIdColumn.eq(
						objectRelationship.getCompanyId());
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
			ObjectDefinition objectDefinition2, long objectEntryId,
			long objectRelationshipId)
		throws PortalException {

		Column<?, Long> companyIdColumn = (Column<?, Long>)_table.getColumn(
			"companyId");

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				objectRelationshipId);

		ObjectDefinition objectDefinition1 =
			_objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId1());

		DynamicObjectRelationshipMappingTable
			dynamicObjectRelationshipMappingTable =
				new DynamicObjectRelationshipMappingTable(
					objectDefinition1.getPKObjectFieldDBColumnName(),
					objectDefinition2.getPKObjectFieldDBColumnName(),
					objectRelationship.getDBTableName());

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
					Column<DynamicObjectRelationshipMappingTable, Long>
						primaryKeyColumn1 =
							(Column
								<DynamicObjectRelationshipMappingTable, Long>)
									dynamicObjectRelationshipMappingTable.
										getColumn(
											objectDefinition1.
												getPKObjectFieldDBColumnName());

					Column<?, Long> primaryKeyColumn2 = _table.getColumn(
						objectDefinition2.getPKObjectFieldDBColumnName());

					return primaryKeyColumn2.notIn(
						DSLQueryFactoryUtil.select(
							dynamicObjectRelationshipMappingTable.getColumn(
								objectDefinition2.
									getPKObjectFieldDBColumnName())
						).from(
							dynamicObjectRelationshipMappingTable
						).where(
							primaryKeyColumn1.eq(objectEntryId)
						));
				}
			)
		);
	}

	private final ObjectDefinition _objectDefinition;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;
	private final SystemObjectDefinitionManager _systemObjectDefinitionManager;
	private final SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;
	private final Table _table;

}