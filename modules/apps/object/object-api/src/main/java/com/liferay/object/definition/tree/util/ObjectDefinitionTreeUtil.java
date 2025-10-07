/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.definition.tree.util;

import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.persistence.ObjectActionPersistence;
import com.liferay.object.service.persistence.ObjectDefinitionPersistence;
import com.liferay.object.service.persistence.ObjectFieldPersistence;
import com.liferay.object.service.persistence.ObjectRelationshipPersistence;
import com.liferay.object.tree.Node;
import com.liferay.object.tree.ObjectDefinitionTreeFactory;
import com.liferay.object.tree.Tree;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.CurrentConnectionUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

/**
 * @author Feliphe Marinho
 */
public class ObjectDefinitionTreeUtil {

	public static void bindObjectDefinitions(
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectDefinitionPersistence objectDefinitionPersistence,
			ObjectDefinitionSettingLocalService
				objectDefinitionSettingLocalService,
			ObjectEntryLocalService objectEntryLocalService,
			ObjectRelationship objectRelationship,
			ObjectRelationshipLocalService objectRelationshipLocalService)
		throws PortalException {

		objectRelationship.setDeletionType(
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE);
		objectRelationship.setEdge(true);

		objectRelationship =
			objectRelationshipLocalService.updateObjectRelationship(
				objectRelationship);

		ObjectDefinition objectDefinition1 =
			objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId1());

		if (ArrayUtil.isEmpty(objectDefinition1.getRootObjectDefinitionIds())) {
			_setRootObjectDefinitionIds(
				new long[] {objectDefinition1.getObjectDefinitionId()},
				objectDefinition1, objectDefinitionSettingLocalService,
				new long[0]);
		}

		ObjectDefinition objectDefinition2 =
			objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId2());

		if (objectDefinition1.isApproved() == objectDefinition2.isApproved()) {
			if (objectDefinition1.isApproved()) {
				objectDefinitionLocalService.deployObjectDefinition(
					objectDefinition1);

				if (objectDefinition2.isApproved() &&
					!objectRelationship.isNew()) {

					objectEntryLocalService.updateRootObjectEntryIds(
						objectDefinition1, objectDefinition2,
						objectRelationship);
				}
			}

			ObjectDefinitionTreeFactory objectDefinitionTreeFactory =
				new ObjectDefinitionTreeFactory(
					objectDefinitionLocalService,
					objectRelationshipLocalService);

			Tree tree = objectDefinitionTreeFactory.create(
				false, true, objectDefinition2.getObjectDefinitionId());

			Iterator<Node> iterator = tree.iterator();

			while (iterator.hasNext()) {
				Node node = iterator.next();

				ObjectDefinition nodeObjectDefinition =
					objectDefinitionPersistence.findByPrimaryKey(
						node.getPrimaryKey());

				_setRootObjectDefinitionIds(
					objectDefinition1.getRootObjectDefinitionIds(),
					nodeObjectDefinition, objectDefinitionSettingLocalService,
					new long[] {objectDefinition2.getObjectDefinitionId()});

				if (nodeObjectDefinition.isApproved() &&
					objectDefinition1.isApproved()) {

					objectDefinitionLocalService.deployObjectDefinition(
						nodeObjectDefinition);
				}
			}
		}
		else {
			if (ArrayUtil.isNotEmpty(
					objectDefinition2.getRootObjectDefinitionIds())) {

				return;
			}

			_setRootObjectDefinitionIds(
				new long[] {objectDefinition2.getObjectDefinitionId()},
				objectDefinition2, objectDefinitionSettingLocalService,
				new long[0]);

			if (objectDefinition2.isApproved()) {
				objectDefinitionLocalService.deployObjectDefinition(
					objectDefinition2);
			}
		}

		for (long rootObjectDefinitionId :
				objectDefinition1.getRootObjectDefinitionIds()) {

			ObjectDefinition rootObjectDefinition =
				objectDefinitionPersistence.findByPrimaryKey(
					rootObjectDefinitionId);

			if (rootObjectDefinition.isApproved()) {
				objectDefinitionLocalService.deployObjectDefinition(
					rootObjectDefinition);
			}
		}
	}

	public static long[] getRootObjectDefinitionIds(
		long objectDefinitionId,
		ObjectDefinitionSettingLocalService
			objectDefinitionSettingLocalService) {

		long[] rootObjectDefinitionIds =
			_rootObjectDefinitionIds.computeIfAbsent(
				objectDefinitionId,
				key -> {
					ObjectDefinitionSetting objectDefinitionSetting =
						objectDefinitionSettingLocalService.
							fetchObjectDefinitionSetting(
								key,
								ObjectDefinitionSettingConstants.
									NAME_ROOT_OBJECT_DEFINITION_IDS);

					if (objectDefinitionSetting == null) {
						return null;
					}

					return ListUtil.toLongArray(
						Arrays.asList(
							StringUtil.split(
								objectDefinitionSetting.getValue())),
						GetterUtil::getLong);
				});

		if (ArrayUtil.isEmpty(rootObjectDefinitionIds)) {
			return new long[0];
		}

		return rootObjectDefinitionIds;
	}

	public static void invalidate() {
		if (!PortalRunMode.isTestMode()) {
			return;
		}

		_rootObjectDefinitionIds.clear();
	}

	public static void populateRootObjectDefinitionIds(
		List<ObjectDefinition> objectDefinitions,
		Map<Long, ObjectDefinitionSetting> objectDefinitionSettingsMap) {

		for (Map.Entry<Long, ObjectDefinitionSetting> entry :
				objectDefinitionSettingsMap.entrySet()) {

			ObjectDefinitionSetting objectDefinitionSetting = entry.getValue();

			_rootObjectDefinitionIds.put(
				entry.getKey(),
				ListUtil.toLongArray(
					Arrays.asList(
						StringUtil.split(objectDefinitionSetting.getValue())),
					GetterUtil::getLong));
		}

		for (ObjectDefinition objectDefinition : objectDefinitions) {
			_rootObjectDefinitionIds.putIfAbsent(
				objectDefinition.getObjectDefinitionId(), new long[0]);
		}
	}

	public static void unbindObjectDefinitions(
			ObjectActionPersistence objectActionPersistence,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectDefinitionPersistence objectDefinitionPersistence,
			ObjectDefinitionSettingLocalService
				objectDefinitionSettingLocalService,
			ObjectEntryLocalService objectEntryLocalService,
			ObjectFieldPersistence objectFieldPersistence,
			ObjectRelationship objectRelationship,
			ObjectRelationshipLocalService objectRelationshipLocalService,
			ObjectRelationshipPersistence objectRelationshipPersistence)
		throws PortalException {

		objectRelationship.setEdge(false);

		objectRelationship =
			objectRelationshipLocalService.updateObjectRelationship(
				objectRelationship);

		ObjectDefinition objectDefinition1 =
			objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId1());

		long[] actualObjectDefinition1RootObjectDefinitionIds =
			objectDefinition1.getRootObjectDefinitionIds();

		long count = objectRelationshipPersistence.countByODI1_E(
			objectDefinition1.getObjectDefinitionId(), true);

		if ((count == 0) &&
			Arrays.equals(
				actualObjectDefinition1RootObjectDefinitionIds,
				new long[] {objectDefinition1.getObjectDefinitionId()})) {

			_setRootObjectDefinitionIds(
				new long[0], objectDefinition1,
				objectDefinitionSettingLocalService,
				actualObjectDefinition1RootObjectDefinitionIds);

			if (objectDefinition1.isApproved()) {
				objectDefinitionLocalService.deployObjectDefinition(
					objectDefinition1);
			}
		}

		_updateObjectEntries(
			objectDefinition1, objectEntryLocalService,
			actualObjectDefinition1RootObjectDefinitionIds);

		if (ArrayUtil.isEmpty(objectDefinition1.getRootObjectDefinitionIds())) {
			for (ObjectAction objectAction :
					objectActionPersistence.findByO_A_OATK(
						objectDefinition1.getObjectDefinitionId(), true,
						ObjectActionTriggerConstants.
							KEY_ON_AFTER_ROOT_UPDATE)) {

				objectAction.setActive(false);
				objectAction.setObjectActionTriggerKey(
					ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE);

				objectActionPersistence.update(objectAction);
			}
		}

		ObjectDefinition objectDefinition2 =
			objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId2());

		long[] addRootObjectDefinitionIds = new long[0];
		long[] removeRootObjectDefinitionIds =
			actualObjectDefinition1RootObjectDefinitionIds;

		count = objectRelationshipPersistence.countByODI1_E(
			objectDefinition2.getObjectDefinitionId(), true);

		if ((count > 0) &&
			Arrays.equals(
				actualObjectDefinition1RootObjectDefinitionIds,
				objectDefinition2.getRootObjectDefinitionIds())) {

			addRootObjectDefinitionIds = new long[] {
				objectDefinition2.getObjectDefinitionId()
			};
		}
		else if ((count == 0) &&
				 (objectDefinition1.isApproved() !=
					 objectDefinition2.isApproved())) {

			removeRootObjectDefinitionIds = ArrayUtil.append(
				actualObjectDefinition1RootObjectDefinitionIds,
				new long[] {objectDefinition2.getObjectDefinitionId()});
		}

		long[] actualObjectDefinition2RootObjectDefinitionIds =
			objectDefinition2.getRootObjectDefinitionIds();

		_setRootObjectDefinitionIds(
			addRootObjectDefinitionIds, objectDefinition2,
			objectDefinitionSettingLocalService, removeRootObjectDefinitionIds);

		if (objectDefinition2.isApproved()) {
			objectDefinitionLocalService.deployObjectDefinition(
				objectDefinition2);
		}

		_updateObjectEntries(
			objectDefinition2, objectEntryLocalService,
			actualObjectDefinition2RootObjectDefinitionIds);

		_updateObjectDefinitionTree(
			addRootObjectDefinitionIds, objectDefinition2,
			objectDefinitionLocalService, objectDefinitionPersistence,
			objectDefinitionSettingLocalService, objectEntryLocalService,
			objectFieldPersistence, objectRelationshipLocalService,
			objectRelationshipPersistence,
			actualObjectDefinition1RootObjectDefinitionIds);

		if (objectDefinition2.isRootNode() && objectDefinition2.isApproved()) {
			objectDefinitionLocalService.deployObjectDefinition(
				objectDefinition2);
		}
	}

	public static void updateNodeObjectDefinition(
			ObjectDefinition objectDefinition,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectDefinitionPersistence objectDefinitionPersistence,
			ObjectDefinitionSettingLocalService
				objectDefinitionSettingLocalService,
			ObjectRelationshipLocalService objectRelationshipLocalService,
			ObjectRelationshipPersistence objectRelationshipPersistence)
		throws PortalException {

		long[] oldRootObjectDefinitionIds =
			objectDefinition.getRootObjectDefinitionIds();

		_updateNodeObjectDefinition(
			objectDefinition, objectDefinitionPersistence,
			objectDefinitionSettingLocalService, objectRelationshipPersistence,
			oldRootObjectDefinitionIds);
		_updateDescendantNodeObjectDefinitions(
			objectDefinition, objectDefinitionLocalService,
			objectDefinitionPersistence, objectDefinitionSettingLocalService,
			objectRelationshipLocalService, objectRelationshipPersistence,
			oldRootObjectDefinitionIds);
	}

	private static void _performActions(
			boolean addRootObjectEntryIdRestrictions,
			ObjectDefinition objectDefinition,
			ObjectEntryLocalService objectEntryLocalService, boolean parallel,
			ActionableDynamicQuery.PerformActionMethod<ObjectEntry>
				performActionMethod)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			objectEntryLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				dynamicQuery.add(
					RestrictionsFactoryUtil.eq(
						"objectDefinitionId",
						objectDefinition.getObjectDefinitionId()));

				if (!addRootObjectEntryIdRestrictions) {
					return;
				}

				dynamicQuery.add(
					RestrictionsFactoryUtil.ne("rootObjectEntryId", 0L));

				if (ArrayUtil.isEmpty(
						objectDefinition.getRootObjectDefinitionIds())) {

					return;
				}

				dynamicQuery.add(
					RestrictionsFactoryUtil.sqlRestriction(
						StringBundler.concat(
							"not exists (select 1 from ObjectEntry where ",
							"objectEntryId = this_.rootObjectEntryId and ",
							"objectDefinitionId in (",
							StringUtil.merge(
								objectDefinition.getRootObjectDefinitionIds()),
							"))")));
			});
		actionableDynamicQuery.setParallel(parallel);
		actionableDynamicQuery.setPerformActionMethod(performActionMethod);

		actionableDynamicQuery.performActions();
	}

	private static void _runSQL(
		ObjectRelationshipPersistence objectRelationshipPersistence,
		String sql) {

		DataSource dataSource = objectRelationshipPersistence.getDataSource();

		Connection currentConnection = CurrentConnectionUtil.getConnection(
			dataSource);

		try {
			DB db = DBManagerUtil.getDB();

			if (currentConnection != null) {
				db.runSQL(currentConnection, new String[] {sql});

				return;
			}

			try (Connection connection = dataSource.getConnection()) {
				db.runSQL(connection, new String[] {sql});
			}
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	private static void _setRootObjectDefinitionIds(
			long[] addRootObjectDefinitionIds,
			ObjectDefinition objectDefinition,
			ObjectDefinitionSettingLocalService
				objectDefinitionSettingLocalService,
			long[] removeRootObjectDefinitionIds)
		throws PortalException {

		_rootObjectDefinitionIds.put(
			objectDefinition.getObjectDefinitionId(),
			_updateRootObjectDefinitionIds(
				addRootObjectDefinitionIds, objectDefinition,
				objectDefinitionSettingLocalService,
				removeRootObjectDefinitionIds));
	}

	private static void _updateDescendantNodeObjectDefinitions(
			ObjectDefinition objectDefinition1,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectDefinitionPersistence objectDefinitionPersistence,
			ObjectDefinitionSettingLocalService
				objectDefinitionSettingLocalService,
			ObjectRelationshipLocalService objectRelationshipLocalService,
			ObjectRelationshipPersistence objectRelationshipPersistence,
			long[] oldRootObjectDefinitionIds)
		throws PortalException {

		List<ObjectRelationship> objectRelationships =
			objectRelationshipPersistence.findByODI1_E(
				objectDefinition1.getObjectDefinitionId(), true);

		if (objectRelationships.isEmpty()) {
			return;
		}

		objectDefinitionLocalService.deployObjectDefinition(objectDefinition1);

		ObjectDefinitionTreeFactory objectDefinitionTreeFactory =
			new ObjectDefinitionTreeFactory(
				objectDefinitionPersistence, objectRelationshipLocalService);

		for (ObjectRelationship objectRelationship : objectRelationships) {
			Tree tree = objectDefinitionTreeFactory.create(
				false, true, objectRelationship.getObjectDefinitionId2());

			Iterator<Node> iterator = tree.iterator();

			while (iterator.hasNext()) {
				Node node = iterator.next();

				ObjectDefinition nodeObjectDefinition =
					objectDefinitionPersistence.findByPrimaryKey(
						node.getPrimaryKey());

				if (nodeObjectDefinition.isApproved()) {
					_setRootObjectDefinitionIds(
						objectDefinition1.getRootObjectDefinitionIds(),
						nodeObjectDefinition,
						objectDefinitionSettingLocalService,
						new long[] {
							objectRelationship.getObjectDefinitionId2()
						});

					objectDefinitionLocalService.deployObjectDefinition(
						nodeObjectDefinition);
				}
				else {
					_setRootObjectDefinitionIds(
						new long[] {
							objectRelationship.getObjectDefinitionId2()
						},
						nodeObjectDefinition,
						objectDefinitionSettingLocalService,
						oldRootObjectDefinitionIds);
				}
			}
		}
	}

	private static void _updateNodeObjectDefinition(
			ObjectDefinition objectDefinition2,
			ObjectDefinitionPersistence objectDefinitionPersistence,
			ObjectDefinitionSettingLocalService
				objectDefinitionSettingLocalService,
			ObjectRelationshipPersistence objectRelationshipPersistence,
			long[] oldRootObjectDefinitionIds)
		throws PortalException {

		List<ObjectRelationship> objectRelationships =
			objectRelationshipPersistence.findByODI2_E(
				objectDefinition2.getObjectDefinitionId(), true);

		if (objectRelationships.isEmpty()) {
			return;
		}

		List<Long> addRootObjectDefinitionIds = new ArrayList<>();

		for (ObjectRelationship objectRelationship : objectRelationships) {
			ObjectDefinition objectDefinition1 =
				objectDefinitionPersistence.findByPrimaryKey(
					objectRelationship.getObjectDefinitionId1());

			if (objectDefinition1.isApproved()) {
				Collections.addAll(
					addRootObjectDefinitionIds,
					ArrayUtil.toArray(
						objectDefinition1.getRootObjectDefinitionIds()));
			}
		}

		if (addRootObjectDefinitionIds.isEmpty()) {
			addRootObjectDefinitionIds.add(
				objectDefinition2.getObjectDefinitionId());
		}

		_setRootObjectDefinitionIds(
			ListUtil.toLongArray(
				addRootObjectDefinitionIds, GetterUtil::getLong),
			objectDefinition2, objectDefinitionSettingLocalService,
			oldRootObjectDefinitionIds);
	}

	private static void _updateObjectDefinitionTree(
			long[] addRootObjectDefinitionIds,
			ObjectDefinition objectDefinition1,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectDefinitionPersistence objectDefinitionPersistence,
			ObjectDefinitionSettingLocalService
				objectDefinitionSettingLocalService,
			ObjectEntryLocalService objectEntryLocalService,
			ObjectFieldPersistence objectFieldPersistence,
			ObjectRelationshipLocalService objectRelationshipLocalService,
			ObjectRelationshipPersistence objectRelationshipPersistence,
			long[] removeRootObjectDefinitionIds)
		throws PortalException {

		for (ObjectRelationship objectRelationship :
				objectRelationshipLocalService.getObjectRelationships(
					objectDefinition1.getObjectDefinitionId(), true)) {

			ObjectDefinition objectDefinition2 =
				objectDefinitionPersistence.findByPrimaryKey(
					objectRelationship.getObjectDefinitionId2());

			_setRootObjectDefinitionIds(
				addRootObjectDefinitionIds, objectDefinition2,
				objectDefinitionSettingLocalService,
				removeRootObjectDefinitionIds);

			if (objectDefinition2.isApproved()) {
				objectDefinitionLocalService.deployObjectDefinition(
					objectDefinition2);

				ObjectField objectField =
					objectFieldPersistence.findByPrimaryKey(
						objectRelationship.getObjectFieldId2());

				_performActions(
					false, objectDefinition1, objectEntryLocalService, true,
					objectEntry -> _runSQL(
						objectRelationshipPersistence,
						StringBundler.concat(
							"update ObjectEntry set rootObjectEntryId = ",
							objectEntry.getRootObjectEntryId(),
							" where objectEntryId in (select ",
							objectDefinition2.getPKObjectFieldDBColumnName(),
							" from ", objectField.getDBTableName(), " where ",
							objectField.getDBColumnName(), " = ",
							objectEntry.getObjectEntryId(), ")")));

				if (objectDefinition2.isEnableIndexSearch()) {
					Indexer<ObjectEntry> indexer =
						IndexerRegistryUtil.getIndexer(
							objectDefinition2.getClassName());

					_performActions(
						false, objectDefinition2, objectEntryLocalService, true,
						indexer::reindex);
				}
			}

			_updateObjectDefinitionTree(
				addRootObjectDefinitionIds, objectDefinition2,
				objectDefinitionLocalService, objectDefinitionPersistence,
				objectDefinitionSettingLocalService, objectEntryLocalService,
				objectFieldPersistence, objectRelationshipLocalService,
				objectRelationshipPersistence, removeRootObjectDefinitionIds);
		}
	}

	private static void _updateObjectEntries(
			ObjectDefinition objectDefinition,
			ObjectEntryLocalService objectEntryLocalService,
			long[] oldRootObjectDefinitionIds)
		throws PortalException {

		if (!objectDefinition.isApproved() ||
			Arrays.equals(
				oldRootObjectDefinitionIds,
				objectDefinition.getRootObjectDefinitionIds())) {

			return;
		}

		_performActions(
			true, objectDefinition, objectEntryLocalService, false,
			objectEntry -> {
				if (Arrays.equals(
						new long[] {objectEntry.getObjectDefinitionId()},
						objectDefinition.getRootObjectDefinitionIds())) {

					objectEntry.setRootObjectEntryId(
						objectEntry.getObjectEntryId());
				}
				else {
					objectEntry.setRootObjectEntryId(0);
				}

				objectEntryLocalService.updateObjectEntry(objectEntry);
			});
	}

	private static long[] _updateRootObjectDefinitionIds(
			long[] addRootObjectDefinitionIds,
			ObjectDefinition objectDefinition,
			ObjectDefinitionSettingLocalService
				objectDefinitionSettingLocalService,
			long[] removeRootObjectDefinitionIds)
		throws PortalException {

		ObjectDefinitionSetting objectDefinitionSetting =
			objectDefinitionSettingLocalService.fetchObjectDefinitionSetting(
				objectDefinition.getObjectDefinitionId(),
				ObjectDefinitionSettingConstants.
					NAME_ROOT_OBJECT_DEFINITION_IDS);

		if (objectDefinitionSetting == null) {
			objectDefinitionSettingLocalService.addObjectDefinitionSetting(
				objectDefinition.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectDefinitionSettingConstants.
					NAME_ROOT_OBJECT_DEFINITION_IDS,
				StringUtil.merge(addRootObjectDefinitionIds));

			return addRootObjectDefinitionIds;
		}

		List<String> rootObjectDefinitionIds = ListUtil.fromArray(
			StringUtil.split(objectDefinitionSetting.getValue()));

		for (long addRootObjectDefinitionId : addRootObjectDefinitionIds) {
			rootObjectDefinitionIds.add(
				String.valueOf(addRootObjectDefinitionId));
		}

		for (long removeRootObjectDefinitionId :
				removeRootObjectDefinitionIds) {

			rootObjectDefinitionIds.remove(
				String.valueOf(removeRootObjectDefinitionId));
		}

		objectDefinitionSetting.setValue(
			StringUtil.merge(rootObjectDefinitionIds));

		objectDefinitionSettingLocalService.updateObjectDefinitionSetting(
			objectDefinitionSetting);

		return ListUtil.toLongArray(
			rootObjectDefinitionIds, GetterUtil::getLong);
	}

	private static final Map<Long, long[]> _rootObjectDefinitionIds =
		new ConcurrentHashMap<>();

}