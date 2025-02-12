/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.definition.util.ObjectDefinitionUtil;
import com.liferay.object.exception.DuplicateObjectRelationshipException;
import com.liferay.object.exception.DuplicateObjectRelationshipExternalReferenceCodeException;
import com.liferay.object.exception.NoSuchObjectRelationshipException;
import com.liferay.object.exception.ObjectDefinitionScopeException;
import com.liferay.object.exception.ObjectRelationshipDeletionTypeException;
import com.liferay.object.exception.ObjectRelationshipEdgeException;
import com.liferay.object.exception.ObjectRelationshipNameException;
import com.liferay.object.exception.ObjectRelationshipParameterObjectFieldIdException;
import com.liferay.object.exception.ObjectRelationshipReverseException;
import com.liferay.object.exception.ObjectRelationshipSystemException;
import com.liferay.object.exception.ObjectRelationshipTypeException;
import com.liferay.object.internal.dao.db.ObjectDBManagerUtil;
import com.liferay.object.internal.info.collection.provider.RelatedInfoCollectionProviderFactory;
import com.liferay.object.internal.security.permission.resource.util.ObjectDefinitionResourcePermissionUtil;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectActionModel;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectFolderItem;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.model.ObjectRelationshipTable;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTableUtil;
import com.liferay.object.petra.sql.dsl.DynamicObjectRelationshipMappingTable;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectFolderItemLocalService;
import com.liferay.object.service.base.ObjectRelationshipLocalServiceBaseImpl;
import com.liferay.object.service.persistence.ObjectActionPersistence;
import com.liferay.object.service.persistence.ObjectDefinitionPersistence;
import com.liferay.object.service.persistence.ObjectFieldPersistence;
import com.liferay.object.service.persistence.ObjectLayoutTabPersistence;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.object.tree.Node;
import com.liferay.object.tree.ObjectDefinitionTreeFactory;
import com.liferay.object.tree.Tree;
import com.liferay.object.tree.constants.TreeConstants;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.jdbc.CurrentConnection;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.RandomUtil;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.object.model.ObjectRelationship",
	service = AopService.class
)
public class ObjectRelationshipLocalServiceImpl
	extends ObjectRelationshipLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectRelationship addObjectRelationship(
			String externalReferenceCode, long userId, long objectDefinitionId1,
			long objectDefinitionId2, long parameterObjectFieldId,
			String deletionType, boolean edge, Map<Locale, String> labelMap,
			String name, boolean system, String type, ObjectField objectField)
		throws PortalException {

		return _addObjectRelationship(
			externalReferenceCode, userId, objectDefinitionId1,
			objectDefinitionId2, parameterObjectFieldId, deletionType, edge,
			labelMap, name, false, system, type, objectField);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectRelationship addObjectRelationship(
			String externalReferenceCode, long userId, long objectDefinitionId1,
			long objectDefinitionId2, ObjectField objectField)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		_validateExternalReferenceCode(
			externalReferenceCode, 0L, user.getCompanyId(),
			objectDefinitionId1);

		return _addObjectRelationship(
			externalReferenceCode, user,
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId1),
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId2),
			0, ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
			LocalizedMapUtil.getLocalizedMap(externalReferenceCode),
			externalReferenceCode, false, false,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY, objectField);
	}

	@Override
	public void addObjectRelationshipMappingTableValues(
			long userId, long objectRelationshipId, long primaryKey1,
			long primaryKey2, ServiceContext serviceContext)
		throws PortalException {

		ObjectRelationship objectRelationship =
			objectRelationshipPersistence.findByPrimaryKey(
				objectRelationshipId);

		ObjectDefinition objectDefinition1 =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId1());

		_validateObjectEntryId(objectDefinition1, primaryKey1);

		ObjectDefinition objectDefinition2 =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId2());

		if (Objects.equals(
				objectRelationship.getType(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY)) {

			if (_hasManyToManyObjectRelationshipMappingTableValues(
					objectDefinition1, objectDefinition2, objectRelationship,
					primaryKey1, primaryKey2)) {

				return;
			}

			Map<String, String> pkObjectFieldDBColumnNames =
				ObjectRelationshipUtil.getPKObjectFieldDBColumnNames(
					objectDefinition1, objectDefinition2,
					objectRelationship.isReverse());

			runSQL(
				StringBundler.concat(
					"insert into ", objectRelationship.getDBTableName(), " (",
					pkObjectFieldDBColumnNames.get(
						"pkObjectFieldDBColumnName1"),
					", ",
					pkObjectFieldDBColumnNames.get(
						"pkObjectFieldDBColumnName2"),
					") values (", primaryKey1, ", ", primaryKey2, ")"));

			FinderCacheUtil.clearDSLQueryCache(
				objectRelationship.getDBTableName());

			return;
		}

		ObjectField objectField2 = _objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		if (objectDefinition2.isUnmodifiableSystemObject()) {
			_objectEntryLocalService.insertIntoOrUpdateExtensionTable(
				userId, objectRelationship.getObjectDefinitionId2(),
				primaryKey2,
				HashMapBuilder.<String, Serializable>put(
					objectField2.getName(), primaryKey1
				).build());
		}
		else {
			_objectEntryLocalService.updateObjectEntry(
				userId, primaryKey2,
				HashMapBuilder.<String, Serializable>putAll(
					_objectEntryLocalService.getValues(primaryKey2)
				).put(
					objectField2.getName(), primaryKey1
				).build(),
				serviceContext);
		}
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectRelationship createManyToManyObjectRelationshipTable(
			long userId, ObjectRelationship objectRelationship)
		throws PortalException {

		if (Validator.isNotNull(objectRelationship.getDBTableName())) {
			return objectRelationshipLocalService.updateObjectRelationship(
				objectRelationship);
		}

		ObjectDefinition objectDefinition1 =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId1());
		ObjectDefinition objectDefinition2 =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId2());

		if (!objectDefinition1.isApproved() ||
			!objectDefinition2.isApproved()) {

			return objectRelationshipLocalService.updateObjectRelationship(
				objectRelationship);
		}

		String dbTableName = null;

		while (true) {
			StringBuilder sb = new StringBuilder(5);

			sb.append("R_");
			sb.append(StringUtil.toUpperCase(StringUtil.randomId(1)));
			sb.append(RandomUtil.nextInt(10));
			sb.append(StringUtil.toUpperCase(StringUtil.randomId(1)));
			sb.append(RandomUtil.nextInt(10));

			ObjectRelationship existingObjectRelationship =
				objectRelationshipPersistence.fetchByDTN_R(
					sb.toString(), false);

			if (existingObjectRelationship == null) {
				dbTableName = sb.toString();

				break;
			}
		}

		objectRelationship.setDBTableName(dbTableName);

		objectRelationship =
			objectRelationshipLocalService.updateObjectRelationship(
				objectRelationship);

		ObjectRelationship reverseObjectRelationship =
			fetchReverseObjectRelationship(objectRelationship, true);

		reverseObjectRelationship.setDBTableName(
			objectRelationship.getDBTableName());

		objectRelationshipLocalService.updateObjectRelationship(
			reverseObjectRelationship);

		Map<String, String> pkObjectFieldDBColumnNames =
			ObjectRelationshipUtil.getPKObjectFieldDBColumnNames(
				objectDefinition1, objectDefinition2, false);

		String pkObjectFieldDBColumnName1 = pkObjectFieldDBColumnNames.get(
			"pkObjectFieldDBColumnName1");
		String pkObjectFieldDBColumnName2 = pkObjectFieldDBColumnNames.get(
			"pkObjectFieldDBColumnName2");

		DynamicObjectRelationshipMappingTable
			dynamicObjectRelationshipMappingTable =
				new DynamicObjectRelationshipMappingTable(
					pkObjectFieldDBColumnName1, pkObjectFieldDBColumnName2,
					objectRelationship.getDBTableName());

		runSQL(dynamicObjectRelationshipMappingTable.getCreateTableSQL());

		Connection connection = _currentConnection.getConnection(
			objectRelationshipPersistence.getDataSource());

		ObjectDBManagerUtil.createIndexMetadata(
			connection, objectRelationship.getDBTableName(), false,
			pkObjectFieldDBColumnName1);
		ObjectDBManagerUtil.createIndexMetadata(
			connection, objectRelationship.getDBTableName(), false,
			pkObjectFieldDBColumnName2);

		return objectRelationship;
	}

	@Override
	public ObjectRelationship deleteObjectRelationship(
			long objectRelationshipId)
		throws PortalException {

		ObjectRelationship objectRelationship =
			objectRelationshipPersistence.findByPrimaryKey(
				objectRelationshipId);

		return objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public ObjectRelationship deleteObjectRelationship(
			ObjectRelationship objectRelationship)
		throws PortalException {

		// TODO When should we allow an object relationship to be deleted?

		if (objectRelationship.isEdge()) {
			throw new ObjectRelationshipEdgeException(
				"Edge object relationships cannot be deleted",
				"edge-object-relationships-cannot-be-deleted");
		}

		if (objectRelationship.isReverse()) {
			throw new ObjectRelationshipReverseException(
				"Reverse object relationships cannot be deleted");
		}

		_validateInvokerBundle(
			"Only allowed bundles can delete system object relationships",
			objectRelationship.isSystem());

		objectRelationship = objectRelationshipPersistence.remove(
			objectRelationship);

		_deleteObjectFields(
			objectRelationship.getObjectDefinitionId1(), objectRelationship);
		_deleteObjectFields(
			objectRelationship.getObjectDefinitionId2(), objectRelationship);

		ObjectDefinition objectDefinition1 =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId1());

		_objectFolderItemLocalService.deleteObjectFolderItem(
			objectRelationship.getObjectDefinitionId2(),
			objectDefinition1.getObjectFolderId());

		ObjectDefinition objectDefinition2 =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId2());

		_objectFolderItemLocalService.deleteObjectFolderItem(
			objectRelationship.getObjectDefinitionId1(),
			objectDefinition2.getObjectFolderId());

		_objectLayoutTabPersistence.removeByObjectRelationshipId(
			objectRelationship.getObjectRelationshipId());

		if (Objects.equals(
				objectRelationship.getType(),
				ObjectRelationshipConstants.TYPE_ONE_TO_ONE) ||
			Objects.equals(
				objectRelationship.getType(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

			_objectFieldLocalService.deleteRelationshipTypeObjectField(
				objectRelationship.getObjectFieldId2());

			for (ObjectRelationship parameterObjectFieldIdObjectRelationship :
					objectRelationshipPersistence.findByParameterObjectFieldId(
						objectRelationship.getObjectFieldId2())) {

				objectRelationshipLocalService.deleteObjectRelationship(
					parameterObjectFieldIdObjectRelationship);
			}
		}
		else if (Objects.equals(
					objectRelationship.getType(),
					ObjectRelationshipConstants.TYPE_MANY_TO_MANY)) {

			if (Validator.isNotNull(objectRelationship.getDBTableName())) {
				runSQL("drop table " + objectRelationship.getDBTableName());
			}

			ObjectRelationship reverseObjectRelationship =
				fetchReverseObjectRelationship(objectRelationship, true);

			_objectLayoutTabPersistence.removeByObjectRelationshipId(
				reverseObjectRelationship.getObjectRelationshipId());

			objectRelationshipPersistence.remove(
				reverseObjectRelationship.getObjectRelationshipId());

			ServiceRegistration<?> serviceRegistration =
				_serviceRegistrations.get(
					_getServiceRegistrationKey(reverseObjectRelationship));

			if (serviceRegistration != null) {
				serviceRegistration.unregister();

				_serviceRegistrations.remove(
					_getServiceRegistrationKey(reverseObjectRelationship));
			}

			Indexer<ObjectRelationship> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(
					ObjectRelationship.class);

			indexer.delete(reverseObjectRelationship);
		}

		ServiceRegistration<?> serviceRegistration = _serviceRegistrations.get(
			_getServiceRegistrationKey(objectRelationship));

		if (serviceRegistration != null) {
			serviceRegistration.unregister();

			_serviceRegistrations.remove(
				_getServiceRegistrationKey(objectRelationship));
		}

		return objectRelationship;
	}

	@Override
	public void deleteObjectRelationshipMappingTableValues(
			long objectRelationshipId, long primaryKey1)
		throws PortalException {

		ObjectRelationship objectRelationship =
			objectRelationshipPersistence.findByPrimaryKey(
				objectRelationshipId);

		if (Objects.equals(
				objectRelationship.getType(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY)) {

			Map<String, String> pkObjectFieldDBColumnNames =
				ObjectRelationshipUtil.getPKObjectFieldDBColumnNames(
					_objectDefinitionPersistence.findByPrimaryKey(
						objectRelationship.getObjectDefinitionId1()),
					_objectDefinitionPersistence.findByPrimaryKey(
						objectRelationship.getObjectDefinitionId2()),
					objectRelationship.isReverse());

			runSQL(
				StringBundler.concat(
					"delete from ", objectRelationship.getDBTableName(),
					" where ",
					pkObjectFieldDBColumnNames.get(
						"pkObjectFieldDBColumnName1"),
					" = ", primaryKey1));

			FinderCacheUtil.clearDSLQueryCache(
				objectRelationship.getDBTableName());
		}
	}

	@Override
	public void deleteObjectRelationshipMappingTableValues(
			long objectRelationshipId, long primaryKey1, long primaryKey2)
		throws PortalException {

		ObjectRelationship objectRelationship =
			objectRelationshipPersistence.findByPrimaryKey(
				objectRelationshipId);

		if (Objects.equals(
				objectRelationship.getType(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY)) {

			ObjectDefinition objectDefinition1 =
				_objectDefinitionPersistence.findByPrimaryKey(
					objectRelationship.getObjectDefinitionId1());
			ObjectDefinition objectDefinition2 =
				_objectDefinitionPersistence.findByPrimaryKey(
					objectRelationship.getObjectDefinitionId2());

			Map<String, String> pkObjectFieldDBColumnNames =
				ObjectRelationshipUtil.getPKObjectFieldDBColumnNames(
					objectDefinition1, objectDefinition2,
					objectRelationship.isReverse());

			runSQL(
				StringBundler.concat(
					"delete from ", objectRelationship.getDBTableName(),
					" where ",
					pkObjectFieldDBColumnNames.get(
						"pkObjectFieldDBColumnName1"),
					" = ", primaryKey1, " and ",
					pkObjectFieldDBColumnNames.get(
						"pkObjectFieldDBColumnName2"),
					" = ", primaryKey2));

			FinderCacheUtil.clearDSLQueryCache(
				objectRelationship.getDBTableName());
		}
	}

	@Override
	public void deleteObjectRelationships(long objectDefinitionId1)
		throws PortalException {

		for (ObjectRelationship objectRelationship :
				objectRelationshipPersistence.findByObjectDefinitionId1(
					objectDefinitionId1)) {

			objectRelationshipLocalService.deleteObjectRelationship(
				objectRelationship);
		}
	}

	@Override
	public void deleteObjectRelationships(
			long objectDefinitionId1, boolean reverse)
		throws PortalException {

		for (ObjectRelationship objectRelationship :
				objectRelationshipPersistence.findByODI1_R(
					objectDefinitionId1, reverse)) {

			objectRelationshipLocalService.deleteObjectRelationship(
				objectRelationship);
		}
	}

	@Override
	public void disableEdge(long objectDefinitionId2) throws PortalException {
		ObjectDefinition objectDefinition2 =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId2);

		for (ObjectRelationship objectRelationship :
				getObjectRelationshipsByObjectDefinitionId2(
					objectDefinitionId2)) {

			if (!objectRelationship.isEdge()) {
				continue;
			}

			ObjectDefinition objectDefinition1 =
				_objectDefinitionPersistence.findByPrimaryKey(
					objectRelationship.getObjectDefinitionId1());

			if (objectDefinition1.getRootObjectDefinitionId() !=
					objectDefinition2.getRootObjectDefinitionId()) {

				objectRelationship.setEdge(false);

				objectRelationshipPersistence.update(objectRelationship);
			}
		}
	}

	@Override
	public ObjectRelationship fetchObjectRelationshipByExternalReferenceCode(
		String externalReferenceCode, long objectDefinitionId1) {

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.fetchByPrimaryKey(objectDefinitionId1);

		if (objectDefinition == null) {
			return null;
		}

		return objectRelationshipPersistence.fetchByERC_C_ODI1(
			externalReferenceCode, objectDefinition.getCompanyId(),
			objectDefinitionId1);
	}

	@Override
	public ObjectRelationship fetchObjectRelationshipByExternalReferenceCode(
		String externalReferenceCode, long companyId,
		long objectDefinitionId1) {

		return objectRelationshipPersistence.fetchByERC_C_ODI1(
			externalReferenceCode, companyId, objectDefinitionId1);
	}

	@Override
	public ObjectRelationship fetchObjectRelationshipByObjectDefinitionId(
		long objectDefinitionId, String name) {

		List<ObjectRelationship> objectRelationships = dslQuery(
			DSLQueryFactoryUtil.select(
			).from(
				ObjectRelationshipTable.INSTANCE
			).where(
				Predicate.withParentheses(
					ObjectRelationshipTable.INSTANCE.objectDefinitionId1.eq(
						objectDefinitionId
					).or(
						ObjectRelationshipTable.INSTANCE.objectDefinitionId2.eq(
							objectDefinitionId)
					)
				).and(
					ObjectRelationshipTable.INSTANCE.name.eq(name)
				).and(
					ObjectRelationshipTable.INSTANCE.reverse.eq(false)
				)
			));

		if (objectRelationships.isEmpty()) {
			return null;
		}

		return objectRelationships.get(0);
	}

	@Override
	public ObjectRelationship fetchObjectRelationshipByObjectDefinitionId1(
		long objectDefinitionId1, String name) {

		return objectRelationshipPersistence.fetchByODI1_N_First(
			objectDefinitionId1, name, null);
	}

	@Override
	public ObjectRelationship fetchObjectRelationshipByObjectFieldId2(
		long objectFieldId2) {

		return objectRelationshipPersistence.fetchByObjectFieldId2(
			objectFieldId2);
	}

	@Override
	public ObjectRelationship fetchReverseObjectRelationship(
		ObjectRelationship objectRelationship, boolean reverse) {

		return objectRelationshipPersistence.fetchByODI1_ODI2_N_R_T(
			objectRelationship.getObjectDefinitionId2(),
			objectRelationship.getObjectDefinitionId1(),
			objectRelationship.getName(), reverse,
			objectRelationship.getType());
	}

	@Override
	public List<ObjectRelationship> getAllObjectRelationships(
		long objectDefinitionId) {

		return dslQuery(
			DSLQueryFactoryUtil.select(
			).from(
				ObjectRelationshipTable.INSTANCE
			).where(
				Predicate.withParentheses(
					ObjectRelationshipTable.INSTANCE.objectDefinitionId1.eq(
						objectDefinitionId
					).or(
						ObjectRelationshipTable.INSTANCE.objectDefinitionId2.eq(
							objectDefinitionId)
					)
				).and(
					ObjectRelationshipTable.INSTANCE.reverse.eq(false)
				)
			));
	}

	@Override
	public ObjectRelationship getObjectRelationship(
			long objectDefinitionId1, String name)
		throws PortalException {

		try {
			return ObjectRelationshipUtil.getObjectRelationship(
				objectRelationshipPersistence.findByODI1_N(
					objectDefinitionId1, name));
		}
		catch (NoSuchObjectRelationshipException
					noSuchObjectRelationshipException) {

			throw new NoSuchObjectRelationshipException(
				String.format(
					"No ObjectRelationship exists with the key " +
						"{objectDefinitionId1=%s, name=%s}",
					objectDefinitionId1, name),
				noSuchObjectRelationshipException);
		}
	}

	@Override
	public ObjectRelationship getObjectRelationshipByExternalReferenceCode(
			String externalReferenceCode, long companyId,
			long objectDefinitionId1)
		throws PortalException {

		return objectRelationshipPersistence.findByERC_C_ODI1(
			externalReferenceCode, companyId, objectDefinitionId1);
	}

	@Override
	public ObjectRelationship getObjectRelationshipByObjectDefinitionId(
			long objectDefinitionId, String name)
		throws PortalException {

		List<ObjectRelationship> objectRelationships = dslQuery(
			DSLQueryFactoryUtil.select(
			).from(
				ObjectRelationshipTable.INSTANCE
			).where(
				Predicate.withParentheses(
					ObjectRelationshipTable.INSTANCE.objectDefinitionId1.eq(
						objectDefinitionId
					).or(
						ObjectRelationshipTable.INSTANCE.objectDefinitionId2.eq(
							objectDefinitionId)
					)
				).and(
					ObjectRelationshipTable.INSTANCE.name.eq(name)
				).and(
					ObjectRelationshipTable.INSTANCE.reverse.eq(false)
				)
			));

		if (objectRelationships.isEmpty()) {
			throw new NoSuchObjectRelationshipException(
				"No object relationship exists with the name " + name);
		}

		return objectRelationships.get(0);
	}

	@Override
	public List<ObjectRelationship> getObjectRelationships(
		long objectDefinitionId1) {

		return objectRelationshipPersistence.findByObjectDefinitionId1(
			objectDefinitionId1);
	}

	@Override
	public List<ObjectRelationship> getObjectRelationships(
		long objectDefinitionId1, boolean edge) {

		return objectRelationshipPersistence.findByODI1_E(
			objectDefinitionId1, edge);
	}

	@Override
	public List<ObjectRelationship> getObjectRelationships(
		long objectDefinitionId1, int start, int end) {

		return objectRelationshipPersistence.findByObjectDefinitionId1(
			objectDefinitionId1, start, end);
	}

	@Override
	public List<ObjectRelationship> getObjectRelationships(
		long objectDefinitionId1, long objectDefinition2, String type) {

		return objectRelationshipPersistence.findByODI1_ODI2_T(
			objectDefinitionId1, objectDefinition2, type);
	}

	@Override
	public List<ObjectRelationship> getObjectRelationships(
		long objectDefinitionId, String type) {

		Set<ObjectRelationship> objectRelationships = SetUtil.fromList(
			objectRelationshipPersistence.findByODI1_R_T(
				objectDefinitionId, false, type));

		objectRelationships.addAll(
			objectRelationshipPersistence.findByODI2_R_T(
				objectDefinitionId, false, type));

		return ListUtil.fromCollection(objectRelationships);
	}

	@Override
	public List<ObjectRelationship> getObjectRelationships(
		long objectDefinitionId1, String deletionType, boolean reverse) {

		return objectRelationshipPersistence.findByODI1_DT_R(
			objectDefinitionId1, deletionType, reverse);
	}

	@Override
	public List<ObjectRelationship> getObjectRelationshipsByObjectDefinitionId2(
		long objectDefinitionId2) {

		return objectRelationshipPersistence.findByObjectDefinitionId2(
			objectDefinitionId2);
	}

	@Override
	public Map<Long, List<ObjectRelationship>> getObjectRelationshipsMap(
		long companyId) {

		Map<Long, List<ObjectRelationship>> objectRelationshipsMap =
			new HashMap<>();

		for (ObjectRelationship objectRelationship :
				objectRelationshipPersistence.findByCompanyId(companyId)) {

			List<ObjectRelationship> objectRelationships =
				objectRelationshipsMap.computeIfAbsent(
					objectRelationship.getObjectDefinitionId1(),
					objectDefinitionId -> new ArrayList<>());

			objectRelationships.add(objectRelationship);
		}

		return objectRelationshipsMap;
	}

	@Override
	public void registerObjectRelationshipsRelatedInfoCollectionProviders(
		ObjectDefinition objectDefinition1,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		List<ObjectRelationship> objectRelationships) {

		if (objectRelationships == null) {
			objectRelationships =
				objectRelationshipLocalService.getObjectRelationships(
					objectDefinition1.getObjectDefinitionId());
		}

		for (ObjectRelationship objectRelationship : objectRelationships) {
			if (!objectRelationship.isAllowedObjectRelationshipType(
					objectRelationship.getType())) {

				continue;
			}

			try {
				ObjectDefinition objectDefinition2 =
					objectDefinitionLocalService.getObjectDefinition(
						objectRelationship.getObjectDefinitionId2());

				_registerRelatedInfoItemCollectionProvider(
					objectDefinition1, objectDefinition2, objectRelationship);

				if (Objects.equals(
						objectRelationship.getType(),
						ObjectRelationshipConstants.TYPE_MANY_TO_MANY)) {

					_registerRelatedInfoItemCollectionProvider(
						objectDefinition2, objectDefinition1,
						objectRelationshipLocalService.getObjectRelationship(
							objectRelationship.getObjectDefinitionId2(),
							objectRelationship.getName()));
				}
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public ObjectRelationship updateObjectRelationship(
			String externalReferenceCode, long objectRelationshipId,
			long parameterObjectFieldId, String deletionType, boolean edge,
			Map<Locale, String> labelMap, ObjectField objectField)
		throws PortalException {

		if (Validator.isNull(deletionType)) {
			deletionType = ObjectRelationshipConstants.DELETION_TYPE_PREVENT;
		}

		ObjectRelationship objectRelationship =
			objectRelationshipPersistence.findByPrimaryKey(
				objectRelationshipId);

		if (objectRelationship.isSystem() &&
			!ObjectDefinitionUtil.isInvokerBundleAllowed()) {

			objectRelationship.setLabelMap(labelMap);

			return objectRelationshipPersistence.update(objectRelationship);
		}

		_validateExternalReferenceCode(
			externalReferenceCode, objectRelationshipId,
			objectRelationship.getCompanyId(),
			objectRelationship.getObjectDefinitionId1());

		if (objectRelationship.isReverse()) {
			objectRelationship.setExternalReferenceCode(externalReferenceCode);

			return objectRelationshipPersistence.update(objectRelationship);
		}

		_validateParameterObjectFieldId(
			_objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId1()),
			_objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId2()),
			parameterObjectFieldId, objectRelationship.getType());
		_validateDeletionType(deletionType, edge, objectRelationship);

		ObjectDefinition objectDefinition1 =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId1());
		ObjectDefinition objectDefinition2 =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId2());

		_validateEdge(
			edge, objectDefinition1, objectDefinition2, objectRelationship,
			objectRelationship.getType());

		if (objectRelationship.compareType(
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY)) {

			ObjectRelationship reverseObjectRelationship =
				fetchReverseObjectRelationship(objectRelationship, true);

			_updateObjectRelationship(
				reverseObjectRelationship.getExternalReferenceCode(),
				parameterObjectFieldId, deletionType, labelMap,
				reverseObjectRelationship);

			Indexer<ObjectRelationship> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(
					ObjectRelationship.class);

			indexer.reindex(reverseObjectRelationship);
		}
		else if ((objectField != null) &&
				 (objectRelationship.compareType(
					 ObjectRelationshipConstants.TYPE_ONE_TO_ONE) ||
				  objectRelationship.compareType(
					  ObjectRelationshipConstants.TYPE_ONE_TO_MANY))) {

			ObjectField existingObjectField =
				_objectFieldLocalService.getObjectField(
					objectRelationship.getObjectFieldId2());

			_objectFieldLocalService.updateObjectField(
				objectField.getExternalReferenceCode(),
				existingObjectField.getObjectFieldId(),
				existingObjectField.getUserId(),
				existingObjectField.getListTypeDefinitionId(),
				existingObjectField.getObjectDefinitionId(),
				existingObjectField.getBusinessType(),
				existingObjectField.getDBColumnName(),
				existingObjectField.getDBTableName(),
				existingObjectField.getDBType(),
				existingObjectField.isIndexed(),
				existingObjectField.isIndexedAsKeyword(),
				existingObjectField.getIndexedLanguageId(),
				objectField.getLabelMap(), existingObjectField.isLocalized(),
				existingObjectField.getName(), objectField.getReadOnly(),
				objectField.getReadOnlyConditionExpression(),
				objectField.isRequired(), existingObjectField.isState(),
				existingObjectField.isSystem(),
				existingObjectField.getObjectFieldSettings());
		}

		objectRelationship = _updateObjectRelationship(
			externalReferenceCode, parameterObjectFieldId, deletionType,
			labelMap, objectRelationship);

		if ((objectRelationship.getObjectFieldId2() != 0) &&
			StringUtil.equals(
				deletionType,
				ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE)) {

			_objectFieldLocalService.updateRequired(
				objectRelationship.getObjectFieldId2(), false);
		}

		if (edge && !objectRelationship.isEdge() &&
			FeatureFlagManagerUtil.isEnabled(
				objectRelationship.getCompanyId(), "LPD-34594")) {

			_bindObjectDefinitions(objectRelationship);
		}
		else if (!edge && objectRelationship.isEdge() &&
				 FeatureFlagManagerUtil.isEnabled(
					 objectRelationship.getCompanyId(), "LPD-34594")) {

			_unbindObjectDefinitions(objectRelationship);
		}

		return objectRelationship;
	}

	@Override
	public void updateUserId(long companyId, long oldUserId, long newUserId)
		throws PortalException {

		for (ObjectRelationship objectRelationship :
				objectRelationshipPersistence.findByC_U(companyId, oldUserId)) {

			objectRelationship.setUserId(newUserId);

			objectRelationshipPersistence.update(objectRelationship);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Override
	protected void runSQL(String sql) {
		if (_log.isDebugEnabled()) {
			_log.debug("SQL: " + sql);
		}

		super.runSQL(sql);
	}

	private ObjectField _addObjectField(
			String externalReferenceCode, User user,
			ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2, String dbColumnName,
			Map<Locale, String> labelMap, String name, String readOnly,
			String readOnlyConditionExpression, String relationshipType,
			boolean required, boolean system)
		throws PortalException {

		_objectFieldLocalService.validateExternalReferenceCode(
			externalReferenceCode, 0, objectDefinition2.getCompanyId(),
			objectDefinition2.getObjectDefinitionId());
		_objectFieldLocalService.validateReadOnlyAndReadOnlyConditionExpression(
			ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP, readOnly,
			readOnlyConditionExpression, required);
		_objectFieldLocalService.validateRequired(
			ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP,
			objectDefinition2.isApproved(), null, required);

		ObjectField objectField = _objectFieldPersistence.create(
			counterLocalService.increment());

		objectField.setExternalReferenceCode(externalReferenceCode);
		objectField.setCompanyId(user.getCompanyId());
		objectField.setUserId(user.getUserId());
		objectField.setUserName(user.getFullName());
		objectField.setListTypeDefinitionId(0);
		objectField.setObjectDefinitionId(
			objectDefinition2.getObjectDefinitionId());
		objectField.setBusinessType(
			ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP);
		objectField.setSystem(system);

		if (Validator.isNull(dbColumnName)) {
			dbColumnName = StringBundler.concat(
				"r_", name, "_", objectDefinition1.getPKObjectFieldName());
		}

		objectField.setDBColumnName(dbColumnName);

		String dbTableName = objectDefinition2.getDBTableName();

		if (objectDefinition2.isApproved()) {
			dbTableName = objectDefinition2.getExtensionDBTableName();
		}

		objectField.setDBTableName(dbTableName);

		objectField.setDBType(ObjectFieldConstants.DB_TYPE_LONG);
		objectField.setIndexed(true);
		objectField.setIndexedAsKeyword(false);
		objectField.setIndexedLanguageId(null);
		objectField.setLabelMap(labelMap, LocaleUtil.getSiteDefault());
		objectField.setName(dbColumnName);
		objectField.setReadOnly(readOnly);
		objectField.setReadOnlyConditionExpression(readOnlyConditionExpression);
		objectField.setRelationshipType(relationshipType);
		objectField.setRequired(required);

		objectField = _objectFieldLocalService.updateObjectField(objectField);

		_objectFieldSettingLocalService.addObjectFieldSetting(
			user.getUserId(), objectField.getObjectFieldId(),
			ObjectFieldSettingConstants.NAME_OBJECT_DEFINITION_1_SHORT_NAME,
			objectDefinition1.getShortName());

		_objectFieldSettingLocalService.addObjectFieldSetting(
			user.getUserId(), objectField.getObjectFieldId(),
			ObjectFieldSettingConstants.
				NAME_OBJECT_RELATIONSHIP_ERC_OBJECT_FIELD_NAME,
			StringUtil.replaceLast(objectField.getName(), "Id", "ERC"));

		if (!objectDefinition2.isApproved()) {
			return objectField;
		}

		runSQL(
			DynamicObjectDefinitionTableUtil.getAlterTableAddColumnSQL(
				dbTableName, objectField.getBusinessType(),
				objectField.getDBColumnName(), "Long"));

		ObjectDBManagerUtil.createIndexMetadata(
			_currentConnection.getConnection(
				objectRelationshipPersistence.getDataSource()),
			dbTableName, false, objectField.getDBColumnName());

		ObjectDefinitionLocalService objectDefinitionLocalService =
			_objectDefinitionLocalServiceSnapshot.get();

		if (objectDefinitionLocalService != null) {
			objectDefinitionLocalService.deployObjectDefinition(
				objectDefinition2);
		}

		return objectField;
	}

	private void _addObjectFolderItem(
			long userId, long objectDefinitionId, long objectFolderId)
		throws PortalException {

		ObjectFolderItem objectFolderItem =
			_objectFolderItemLocalService.fetchObjectFolderItem(
				objectDefinitionId, objectFolderId);

		if (objectFolderItem != null) {
			return;
		}

		_objectFolderItemLocalService.addObjectFolderItem(
			userId, objectDefinitionId, objectFolderId, 0, 0);
	}

	private ObjectRelationship _addObjectRelationship(
			String externalReferenceCode, long userId, long objectDefinitionId1,
			long objectDefinitionId2, long parameterObjectFieldId,
			String deletionType, boolean edge, Map<Locale, String> labelMap,
			String name, boolean reverse, boolean system, String type,
			ObjectField objectField)
		throws PortalException {

		_validateInvokerBundle(
			"Only allowed bundles can add system object relationships", system);

		User user = _userLocalService.getUser(userId);

		_validateExternalReferenceCode(
			externalReferenceCode, 0L, user.getCompanyId(),
			objectDefinitionId1);

		ObjectDefinition objectDefinition1 =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId1);
		ObjectDefinition objectDefinition2 =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId2);

		_validateEdge(edge, objectDefinition1, objectDefinition2, null, type);
		_validateName(objectDefinition1, objectDefinition2, name);
		_validateType(
			objectDefinition1, objectDefinition2, name, parameterObjectFieldId,
			type);

		return _addObjectRelationship(
			externalReferenceCode, user, objectDefinition1, objectDefinition2,
			parameterObjectFieldId, deletionType, edge, labelMap, name, reverse,
			system, type, objectField);
	}

	private ObjectRelationship _addObjectRelationship(
			String externalReferenceCode, User user,
			ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2, long parameterObjectFieldId,
			String deletionType, boolean edge, Map<Locale, String> labelMap,
			String name, boolean reverse, boolean system, String type,
			ObjectField objectField)
		throws PortalException {

		_validateScope(objectDefinition1, objectDefinition2);

		ObjectRelationship objectRelationship =
			objectRelationshipPersistence.create(
				counterLocalService.increment());

		objectRelationship.setExternalReferenceCode(externalReferenceCode);
		objectRelationship.setCompanyId(user.getCompanyId());
		objectRelationship.setUserId(user.getUserId());
		objectRelationship.setUserName(user.getFullName());
		objectRelationship.setObjectDefinitionId1(
			objectDefinition1.getObjectDefinitionId());
		objectRelationship.setObjectDefinitionId2(
			objectDefinition2.getObjectDefinitionId());
		objectRelationship.setParameterObjectFieldId(parameterObjectFieldId);
		objectRelationship.setDeletionType(
			GetterUtil.getString(
				deletionType,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT));
		objectRelationship.setEdge(edge);
		objectRelationship.setLabelMap(labelMap);
		objectRelationship.setName(name);
		objectRelationship.setReverse(reverse);
		objectRelationship.setSystem(system);
		objectRelationship.setType(type);

		_addObjectFolderItem(
			user.getUserId(), objectDefinition1.getObjectDefinitionId(),
			objectDefinition2.getObjectFolderId());
		_addObjectFolderItem(
			user.getUserId(), objectDefinition2.getObjectDefinitionId(),
			objectDefinition1.getObjectFolderId());

		if (Objects.equals(type, ObjectRelationshipConstants.TYPE_ONE_TO_ONE) ||
			Objects.equals(
				type, ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

			if (objectField != null) {
				Map<Locale, String> objectFieldLabelMap =
					objectField.getLabelMap();

				if (objectFieldLabelMap.isEmpty()) {
					objectFieldLabelMap = objectRelationship.getLabelMap();
				}

				objectField = _addObjectField(
					objectField.getExternalReferenceCode(), user,
					objectDefinition1, objectDefinition2, objectField.getName(),
					objectFieldLabelMap, name, objectField.getReadOnly(),
					objectField.getReadOnlyConditionExpression(), type,
					objectField.isRequired(), system);
			}
			else {
				objectField = _addObjectField(
					null, user, objectDefinition1, objectDefinition2, null,
					objectRelationship.getLabelMap(), name,
					ObjectFieldConstants.READ_ONLY_FALSE, StringPool.BLANK,
					type, false, system);
			}

			objectRelationship.setObjectFieldId2(
				objectField.getObjectFieldId());
		}
		else if (Objects.equals(
					type, ObjectRelationshipConstants.TYPE_MANY_TO_MANY) &&
				 !reverse) {

			_registerRelatedInfoItemCollectionProvider(
				objectDefinition1, objectDefinition2, objectRelationship);

			_addObjectRelationship(
				null, user.getUserId(),
				objectDefinition2.getObjectDefinitionId(),
				objectDefinition1.getObjectDefinitionId(),
				parameterObjectFieldId, deletionType, false, labelMap, name,
				true, system, type, objectField);

			return objectRelationshipLocalService.
				createManyToManyObjectRelationshipTable(
					user.getUserId(), objectRelationship);
		}

		_registerRelatedInfoItemCollectionProvider(
			objectDefinition1, objectDefinition2, objectRelationship);

		if (edge &&
			FeatureFlagManagerUtil.isEnabled(
				objectRelationship.getCompanyId(), "LPD-34594")) {

			_bindObjectDefinitions(objectRelationship);
		}

		return objectRelationshipLocalService.updateObjectRelationship(
			objectRelationship);
	}

	private void _bindObjectDefinitions(ObjectRelationship objectRelationship)
		throws PortalException {

		objectRelationship.setDeletionType(
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE);
		objectRelationship.setEdge(true);

		objectRelationship =
			objectRelationshipLocalService.updateObjectRelationship(
				objectRelationship);

		_objectFieldLocalService.updateRequired(
			objectRelationship.getObjectFieldId2(), true);

		ObjectDefinition objectDefinition1 =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId1());

		String objectDefinition1PreviousRESTContextPath =
			objectDefinition1.getRESTContextPath();

		if (objectDefinition1.getRootObjectDefinitionId() == 0) {
			objectDefinition1.setRootObjectDefinitionId(
				objectDefinition1.getObjectDefinitionId());
		}

		ObjectDefinitionLocalService objectDefinitionLocalService =
			_objectDefinitionLocalServiceSnapshot.get();

		objectDefinition1 = objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition1);

		ObjectDefinition objectDefinition2 =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId2());

		if (objectDefinition1.isApproved() == objectDefinition2.isApproved()) {
			if (objectDefinition1.isApproved()) {
				objectDefinition1.setPreviousRESTContextPath(
					objectDefinition1PreviousRESTContextPath);

				objectDefinitionLocalService.deployObjectDefinition(
					objectDefinition1);

				if (objectDefinition2.isApproved() &&
					!objectRelationship.isNew()) {

					_objectEntryLocalService.updateRootObjectEntryIds(
						objectDefinition1, objectDefinition2,
						objectRelationship);
				}
			}

			ObjectDefinitionTreeFactory objectDefinitionTreeFactory =
				new ObjectDefinitionTreeFactory(
					objectDefinitionLocalService,
					objectRelationshipLocalService);

			Tree tree = objectDefinitionTreeFactory.create(
				objectDefinition2.getObjectDefinitionId());

			Iterator<Node> iterator = tree.iterator();

			while (iterator.hasNext()) {
				Node node = iterator.next();

				ObjectDefinition nodeObjectDefinition =
					objectDefinitionLocalService.fetchObjectDefinition(
						node.getPrimaryKey());

				String nodeObjectDefinitionPreviousRESTContextPath =
					nodeObjectDefinition.getRESTContextPath();

				nodeObjectDefinition =
					objectDefinitionLocalService.
						updateRootDescendantNodeObjectDefinition(
							nodeObjectDefinition,
							objectDefinition1.getRootObjectDefinitionId());

				if (nodeObjectDefinition.isApproved() &&
					objectDefinition1.isApproved()) {

					nodeObjectDefinition.setPreviousRESTContextPath(
						nodeObjectDefinitionPreviousRESTContextPath);

					objectDefinitionLocalService.deployObjectDefinition(
						nodeObjectDefinition);
				}
			}
		}
		else {
			if (objectDefinition2.isRootNode()) {
				return;
			}

			objectDefinition2.setRootObjectDefinitionId(
				objectDefinition2.getObjectDefinitionId());

			objectDefinition2 =
				objectDefinitionLocalService.updateObjectDefinition(
					objectDefinition2);

			if (objectDefinition2.isApproved()) {
				objectDefinitionLocalService.deployObjectDefinition(
					objectDefinition2);
			}
		}

		ObjectDefinition rootObjectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectDefinition1.getRootObjectDefinitionId());

		if (rootObjectDefinition.isApproved()) {
			objectDefinitionLocalService.deployObjectDefinition(
				rootObjectDefinition);
		}
	}

	private void _copyResourcePermissions(
			List<ResourceAction> sourceResourceActions,
			List<ResourcePermission> sourceResourcePermissions,
			String targetName, List<String> targetObjectActionNames,
			String targetPrimKey)
		throws PortalException {

		for (ResourcePermission sourceResourcePermission :
				sourceResourcePermissions) {

			List<String> targetResourceActionIds = new ArrayList<>();

			for (ResourceAction sourceResourceAction : sourceResourceActions) {
				long bitwiseValue = sourceResourceAction.getBitwiseValue();

				if ((sourceResourcePermission.getActionIds() & bitwiseValue) !=
						bitwiseValue) {

					continue;
				}

				targetResourceActionIds.add(sourceResourceAction.getActionId());
			}

			if (ListUtil.isNotEmpty(targetObjectActionNames)) {
				targetResourceActionIds.addAll(
					_resourcePermissionLocalService.
						getAvailableResourcePermissionActionIds(
							sourceResourcePermission.getCompanyId(), targetName,
							sourceResourcePermission.getScope(), targetPrimKey,
							sourceResourcePermission.getRoleId(),
							targetObjectActionNames));
			}

			_resourcePermissionLocalService.setResourcePermissions(
				sourceResourcePermission.getCompanyId(), targetName,
				sourceResourcePermission.getScope(), targetPrimKey,
				sourceResourcePermission.getRoleId(),
				targetResourceActionIds.toArray(new String[0]));
		}
	}

	private void _copyResourcePermissions(
			long companyId, String sourceName, String targetName,
			List<String> targetObjectActionNames)
		throws PortalException {

		List<ResourceAction> resourceActions =
			_resourceActionLocalService.getResourceActions(sourceName);

		_copyResourcePermissions(
			resourceActions,
			_resourcePermissionLocalService.getResourcePermissions(
				companyId, sourceName, ResourceConstants.SCOPE_COMPANY,
				String.valueOf(companyId)),
			targetName, targetObjectActionNames, String.valueOf(companyId));
		_copyResourcePermissions(
			resourceActions,
			_resourcePermissionLocalService.getResourcePermissions(
				companyId, sourceName, ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID)),
			targetName, targetObjectActionNames,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID));
	}

	private void _copyResourcePermissions(
			ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2)
		throws PortalException {

		if (!objectDefinition1.isApproved() ||
			!objectDefinition2.isApproved()) {

			return;
		}

		List<String> objectActionNames = TransformUtil.transform(
			_objectActionPersistence.findByO_A_OATK(
				objectDefinition2.getObjectDefinitionId(), true,
				ObjectActionTriggerConstants.KEY_STANDALONE),
			ObjectActionModel::getName);
		List<ResourceAction> resourceActions =
			_resourceActionLocalService.getResourceActions(
				objectDefinition1.getClassName());

		_performActions(
			objectDefinition2.getObjectDefinitionId(), true,
			(ObjectEntry objectEntry) -> _copyResourcePermissions(
				resourceActions,
				_resourcePermissionLocalService.getResourcePermissions(
					objectDefinition1.getCompanyId(),
					objectDefinition1.getClassName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(objectEntry.getRootObjectEntryId())),
				objectDefinition2.getClassName(), objectActionNames,
				String.valueOf(objectEntry.getObjectEntryId())));

		_copyResourcePermissions(
			objectDefinition1.getCompanyId(), objectDefinition1.getClassName(),
			objectDefinition2.getClassName(), objectActionNames);

		_copyResourcePermissions(
			objectDefinition1.getCompanyId(), objectDefinition1.getPortletId(),
			objectDefinition2.getPortletId(), null);
		_copyResourcePermissions(
			objectDefinition1.getCompanyId(),
			objectDefinition1.getResourceName(),
			objectDefinition2.getResourceName(), null);
	}

	private void _deleteObjectFields(
			long objectDefinitionId, ObjectRelationship objectRelationship)
		throws PortalException {

		for (ObjectField objectField :
				_objectFieldPersistence.findByObjectDefinitionId(
					objectDefinitionId)) {

			ObjectFieldSetting objectFieldSetting =
				_objectFieldSettingLocalService.fetchObjectFieldSetting(
					objectField.getObjectFieldId(), "objectRelationshipName");

			if ((objectFieldSetting != null) &&
				StringUtil.equals(
					objectFieldSetting.getValue(),
					objectRelationship.getName())) {

				_objectFieldLocalService.deleteObjectField(
					objectField.getObjectFieldId());
			}
		}
	}

	private void _deployObjectDefinition(ObjectDefinition objectDefinition)
		throws PortalException {

		if (!objectDefinition.isApproved()) {
			return;
		}

		ObjectDefinitionLocalService objectDefinitionLocalService =
			_objectDefinitionLocalServiceSnapshot.get();

		objectDefinitionLocalService.deployObjectDefinition(objectDefinition);
	}

	private long _getRootObjectDefinitionId(ObjectDefinition objectDefinition)
		throws PortalException {

		long count = objectRelationshipPersistence.countByODI1_E(
			objectDefinition.getObjectDefinitionId(), true);

		if (count == 0) {
			return 0;
		}

		return objectDefinition.getObjectDefinitionId();
	}

	private String _getServiceRegistrationKey(
		ObjectRelationship objectRelationship) {

		return StringBundler.concat(
			objectRelationship.getCompanyId(), StringPool.POUND,
			objectRelationship.getObjectRelationshipId());
	}

	private boolean _hasManyToManyObjectRelationshipMappingTableValues(
		ObjectDefinition objectDefinition1, ObjectDefinition objectDefinition2,
		ObjectRelationship objectRelationship, long primaryKey1,
		long primaryKey2) {

		Map<String, String> pkObjectFieldDBColumnNames =
			ObjectRelationshipUtil.getPKObjectFieldDBColumnNames(
				objectDefinition1, objectDefinition2,
				objectRelationship.isReverse());

		DynamicObjectRelationshipMappingTable
			dynamicObjectRelationshipMappingTable =
				new DynamicObjectRelationshipMappingTable(
					pkObjectFieldDBColumnNames.get(
						"pkObjectFieldDBColumnName1"),
					pkObjectFieldDBColumnNames.get(
						"pkObjectFieldDBColumnName2"),
					objectRelationship.getDBTableName());

		Column<DynamicObjectRelationshipMappingTable, Long> primaryKeyColumn1 =
			dynamicObjectRelationshipMappingTable.getPrimaryKeyColumn1();
		Column<DynamicObjectRelationshipMappingTable, Long> primaryKeyColumn2 =
			dynamicObjectRelationshipMappingTable.getPrimaryKeyColumn2();

		int count = dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				dynamicObjectRelationshipMappingTable
			).where(
				primaryKeyColumn1.eq(
					primaryKey1
				).and(
					primaryKeyColumn2.eq(primaryKey2)
				)
			));

		if (count > 0) {
			return true;
		}

		return false;
	}

	private void _performActions(
			long objectDefinitionId, boolean parallel,
			ActionableDynamicQuery.PerformActionMethod<?> performActionMethod)
		throws PortalException {

		ActionableDynamicQuery actionableDynamicQuery =
			_objectEntryLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.eq(
					"objectDefinitionId", objectDefinitionId)));
		actionableDynamicQuery.setParallel(parallel);
		actionableDynamicQuery.setPerformActionMethod(performActionMethod);

		actionableDynamicQuery.performActions();
	}

	private void _registerRelatedInfoItemCollectionProvider(
			ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2,
			ObjectRelationship objectRelationship)
		throws PortalException {

		if (!objectDefinition1.isApproved() ||
			!objectDefinition2.isApproved()) {

			return;
		}

		RelatedInfoItemCollectionProvider relatedInfoItemCollectionProvider =
			_relatedInfoCollectionProviderFactory.create(
				objectDefinition1, objectDefinition2, objectRelationship);

		if (relatedInfoItemCollectionProvider == null) {
			return;
		}

		_serviceRegistrations.computeIfAbsent(
			_getServiceRegistrationKey(objectRelationship),
			serviceRegistrationKey -> _bundleContext.registerService(
				RelatedInfoItemCollectionProvider.class,
				relatedInfoItemCollectionProvider,
				HashMapDictionaryBuilder.<String, Object>put(
					"company.id", objectDefinition1.getCompanyId()
				).put(
					"item.class.name", objectDefinition1.getClassName()
				).build()));
	}

	private void _unbindObjectDefinitions(ObjectRelationship objectRelationship)
		throws PortalException {

		objectRelationship.setEdge(false);

		objectRelationship =
			objectRelationshipLocalService.updateObjectRelationship(
				objectRelationship);

		ObjectDefinition objectDefinition1 =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId1());

		if (objectDefinition1.isRootDescendantNode()) {
			objectDefinition1 = _objectDefinitionPersistence.findByPrimaryKey(
				objectDefinition1.getRootObjectDefinitionId());
		}

		long oldRootObjectDefinitionId1 =
			objectDefinition1.getRootObjectDefinitionId();
		long newRootObjectDefinitionId1 = _getRootObjectDefinitionId(
			objectDefinition1);

		_updateRootObjectDefinitionId(
			objectDefinition1, oldRootObjectDefinitionId1,
			newRootObjectDefinitionId1);

		_updateObjectEntries(
			objectDefinition1, oldRootObjectDefinitionId1,
			newRootObjectDefinitionId1);

		if (newRootObjectDefinitionId1 == 0) {
			for (ObjectAction objectAction :
					_objectActionPersistence.findByO_A_OATK(
						objectDefinition1.getObjectDefinitionId(), true,
						ObjectActionTriggerConstants.
							KEY_ON_AFTER_ROOT_UPDATE)) {

				objectAction.setActive(false);
				objectAction.setObjectActionTriggerKey(
					ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE);

				_objectActionPersistence.update(objectAction);
			}
		}

		ObjectDefinition objectDefinition2 =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId2());

		objectDefinition2.setScope(objectDefinition1.getScope());

		long oldRootObjectDefinitionId2 =
			objectDefinition2.getRootObjectDefinitionId();
		long newRootObjectDefinitionId2 = _getRootObjectDefinitionId(
			objectDefinition2);

		_updateRootObjectDefinitionId(
			objectDefinition2, oldRootObjectDefinitionId2,
			newRootObjectDefinitionId2);

		_copyResourcePermissions(objectDefinition1, objectDefinition2);

		_updateObjectEntries(
			objectDefinition2, oldRootObjectDefinitionId2,
			newRootObjectDefinitionId2);

		_updateObjectDefinitionTree(
			objectDefinition2, oldRootObjectDefinitionId2,
			newRootObjectDefinitionId2);

		if (objectDefinition2.isRootNode()) {
			_deployObjectDefinition(objectDefinition2);
		}
	}

	private void _updateObjectDefinitionTree(
			ObjectDefinition objectDefinition1, long oldRootObjectDefinitionId,
			long newRootObjectDefinitionId)
		throws PortalException {

		try {
			ObjectDefinitionResourcePermissionUtil.
				populateRootDescendantNodeModelResources(
					_objectActionPersistence, _objectDefinitionPersistence,
					_resourceActions, objectDefinition1,
					newRootObjectDefinitionId);

			ObjectDefinitionResourcePermissionUtil.
				removeRootDescendantNodeModelResources(
					_objectDefinitionPersistence, _resourceActions,
					objectDefinition1, oldRootObjectDefinitionId);
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}

		if (newRootObjectDefinitionId == 0) {
			return;
		}

		for (ObjectRelationship objectRelationship :
				objectRelationshipLocalService.getObjectRelationships(
					objectDefinition1.getObjectDefinitionId(), true)) {

			ObjectDefinition objectDefinition2 =
				_objectDefinitionPersistence.findByPrimaryKey(
					objectRelationship.getObjectDefinitionId2());

			if (oldRootObjectDefinitionId !=
					objectDefinition2.getRootObjectDefinitionId()) {

				continue;
			}

			_updateRootObjectDefinitionId(
				objectDefinition2, oldRootObjectDefinitionId,
				newRootObjectDefinitionId);

			if (objectDefinition2.isApproved()) {
				ObjectField objectField =
					_objectFieldPersistence.findByPrimaryKey(
						objectRelationship.getObjectFieldId2());

				_performActions(
					objectDefinition1.getObjectDefinitionId(), true,
					(ObjectEntry objectEntry) -> runSQL(
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
						objectDefinition2.getObjectDefinitionId(), true,
						(ObjectEntry objectEntry) -> indexer.reindex(
							objectEntry));
				}
			}

			_updateObjectDefinitionTree(
				objectDefinition2, oldRootObjectDefinitionId,
				newRootObjectDefinitionId);
		}
	}

	private void _updateObjectEntries(
			ObjectDefinition objectDefinition, long oldRootObjectDefinitionId,
			long newRootObjectDefinitionId)
		throws PortalException {

		if (!objectDefinition.isApproved() ||
			(oldRootObjectDefinitionId == newRootObjectDefinitionId)) {

			return;
		}

		_performActions(
			objectDefinition.getObjectDefinitionId(), false,
			(ObjectEntry objectEntry) -> {
				if (newRootObjectDefinitionId == 0) {
					objectEntry.setRootObjectEntryId(0);
				}
				else {
					objectEntry.setRootObjectEntryId(
						objectEntry.getObjectEntryId());
				}

				_objectEntryLocalService.updateObjectEntry(objectEntry);
			});
	}

	private ObjectRelationship _updateObjectRelationship(
		String externalReferenceCode, long parameterObjectFieldId,
		String deletionType, Map<Locale, String> labelMap,
		ObjectRelationship objectRelationship) {

		objectRelationship.setExternalReferenceCode(externalReferenceCode);
		objectRelationship.setParameterObjectFieldId(parameterObjectFieldId);
		objectRelationship.setDeletionType(deletionType);
		objectRelationship.setLabelMap(labelMap);

		return objectRelationshipPersistence.update(objectRelationship);
	}

	private void _updateRootObjectDefinitionId(
			ObjectDefinition objectDefinition, long oldRootObjectDefinitionId,
			long newRootObjectDefinitionId)
		throws PortalException {

		if (oldRootObjectDefinitionId == newRootObjectDefinitionId) {
			_deployObjectDefinition(objectDefinition);

			return;
		}

		String previousRESTContextPath = objectDefinition.getRESTContextPath();

		objectDefinition.setRootObjectDefinitionId(newRootObjectDefinitionId);

		objectDefinition = _objectDefinitionPersistence.update(
			objectDefinition);

		objectDefinition.setPreviousRESTContextPath(previousRESTContextPath);

		_deployObjectDefinition(objectDefinition);
	}

	private void _validateDeletionType(
			String deletionType, boolean edge,
			ObjectRelationship objectRelationship)
		throws PortalException {

		if (edge && objectRelationship.isEdge() &&
			!StringUtil.equals(
				deletionType,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE)) {

			throw new ObjectRelationshipDeletionTypeException.
				MustHaveCascadeDeletionType();
		}
	}

	private void _validateEdge(
			boolean edge, ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2,
			ObjectRelationship objectRelationship, String type)
		throws PortalException {

		if (!edge ||
			((objectRelationship != null) && objectRelationship.isEdge()) ||
			!FeatureFlagManagerUtil.isEnabled(
				objectDefinition1.getCompanyId(), "LPD-34594")) {

			return;
		}

		if (!Objects.equals(
				type, ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

			throw new ObjectRelationshipEdgeException(
				"Object relationship must be one to many to be an edge of a " +
					"root context",
				"object-relationship-must-be-one-to-many-to-be-an-edge-of-a-" +
					"root-context");
		}

		if (objectDefinition1.getObjectDefinitionId() ==
				objectDefinition2.getObjectDefinitionId()) {

			throw new ObjectRelationshipEdgeException(
				"Object relationship must not be a self-relationship to be " +
					"an edge of a root context",
				"object-relationship-must-not-be-a-self-relationship-to-be-" +
					"an-edge-of-a-root-context");
		}

		if ((objectDefinition1.isModifiableAndSystem() &&
			 !objectDefinition2.isSystem()) ||
			(objectDefinition2.isModifiableAndSystem() &&
			 !objectDefinition1.isSystem())) {

			throw new ObjectRelationshipEdgeException(
				"Inheritance between modifiable system and custom object " +
					"definitions is not allowed",
				"inheritance-between-modifiable-system-and-custom-object-" +
					"definitions-is-not-allowed");
		}
		else if (objectDefinition1.isUnmodifiableSystemObject() ||
				 objectDefinition2.isUnmodifiableSystemObject()) {

			throw new ObjectRelationshipEdgeException(
				"System object definitions cannot inherit configurations",
				"system-object-definitions-cannot-inherit-configurations");
		}

		// Circular reference in a root context must be validated before
		// the tree maximum height

		if ((objectDefinition1.getRootObjectDefinitionId() != 0) &&
			(objectDefinition1.getRootObjectDefinitionId() ==
				objectDefinition2.getObjectDefinitionId())) {

			throw new ObjectRelationshipEdgeException(
				"The object relationship must not create a circular " +
					"reference in a root context",
				"the-object-relationship-must-not-create-a-circular-" +
					"reference-in-a-root-context");
		}

		int treeHeight = 1;

		ObjectDefinitionTreeFactory objectDefinitionTreeFactory =
			new ObjectDefinitionTreeFactory(
				_objectDefinitionPersistence, objectRelationshipLocalService);

		if (objectDefinition1.getRootObjectDefinitionId() != 0) {
			Tree tree = objectDefinitionTreeFactory.create(
				objectDefinition1.getRootObjectDefinitionId());

			Node node = tree.getNode(objectDefinition1.getObjectDefinitionId());

			treeHeight += node.getDepth();
		}

		if (objectDefinition2.getRootObjectDefinitionId() != 0) {
			Tree tree = objectDefinitionTreeFactory.create(
				objectDefinition2.getRootObjectDefinitionId());

			treeHeight += tree.getHeight(tree.getRootNode());
		}

		if (treeHeight > TreeConstants.MAX_HEIGHT) {
			throw new ObjectRelationshipEdgeException(
				"The object relationship cannot be an edge in the root " +
					"context because it would exceed the tree's maximum height",
				"the-object-relationship-cannot-be-an-edge-in-the-root-" +
					"context-because-it-would-exceed-the-tree's-maximum-" +
						"height");
		}

		if (objectDefinition1.isApproved() && objectDefinition2.isApproved() &&
			(objectRelationship != null) &&
			(objectRelationship.getObjectRelationshipId() != 0)) {

			int relatedObjectEntriesCount =
				_objectEntryLocalService.getOneToManyObjectEntriesCount(
					0, objectRelationship.getObjectRelationshipId(), 0L, false,
					null);

			if (relatedObjectEntriesCount > 0) {
				throw new ObjectRelationshipEdgeException(
					StringBundler.concat(
						"There must be no unrelated object entries when both ",
						"object definitions are published so that the object ",
						"relationship can be an edge to a root context"),
					"there-must-be-no-unrelated-object-entries-when-both-" +
						"object-definitions-are-published-so-that-the-object-" +
							"relationship-can-be-an-edge-to-a-root-context");
			}
		}

		long objectDefinition2RootObjectDefinitionId =
			objectDefinition2.getRootObjectDefinitionId();

		if ((objectDefinition2RootObjectDefinitionId != 0) &&
			(objectDefinition2RootObjectDefinitionId !=
				objectDefinition2.getObjectDefinitionId())) {

			throw new ObjectRelationshipEdgeException(
				"Unable to bind the object definitions when the child object " +
					"definition is bound to another object definition",
				"unable-to-bind-the-object-definitions-when-the-child-object-" +
					"definition-is-bound-to-another-object-definition");
		}

		if (!StringUtil.equals(
				objectDefinition1.getScope(), objectDefinition2.getScope())) {

			throw new ObjectRelationshipEdgeException(
				Arrays.asList(
					objectDefinition1.getShortName(),
					objectDefinition2.getShortName()),
				String.format(
					"The scope of \"%s\" is not the same as \"%s\". To " +
						"enable inheritance, the object definitions must " +
							"have the same scope",
					objectDefinition1.getShortName(),
					objectDefinition2.getShortName()),
				"the-scope-of-x-is-not-the-same-as-x-to-enable-inheritance-" +
					"the-object-definitions-must-have-the-same-scope");
		}
	}

	private void _validateExternalReferenceCode(
		String externalReferenceCode, long objectRelationshipId, long companyId,
		long objectDefinitionId1) {

		if (Validator.isNull(externalReferenceCode)) {
			return;
		}

		ObjectRelationship objectRelationship =
			objectRelationshipPersistence.fetchByERC_C_ODI1(
				externalReferenceCode, companyId, objectDefinitionId1);

		if ((objectRelationship != null) &&
			(objectRelationship.getObjectRelationshipId() !=
				objectRelationshipId)) {

			throw new DuplicateObjectRelationshipExternalReferenceCodeException();
		}
	}

	private void _validateInvokerBundle(String message, boolean system)
		throws PortalException {

		if (!system || ObjectDefinitionUtil.isInvokerBundleAllowed()) {
			return;
		}

		throw new ObjectRelationshipSystemException(message);
	}

	private void _validateName(
			ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2, String name)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new ObjectRelationshipNameException("Name is null");
		}

		char[] nameCharArray = name.toCharArray();

		for (char c : nameCharArray) {
			if (!Validator.isChar(c) && !Validator.isDigit(c)) {
				throw new ObjectRelationshipNameException(
					"Name must only contain letters and digits");
			}
		}

		if (!Character.isLowerCase(nameCharArray[0])) {
			throw new ObjectRelationshipNameException(
				"The first character of a name must be a lower case letter");
		}

		if (nameCharArray.length > 41) {
			throw new ObjectRelationshipNameException(
				"Name must be less than 41 characters");
		}

		int count = objectRelationshipPersistence.countByODI1_N(
			objectDefinition1.getObjectDefinitionId(), name);

		if (count > 0) {
			throw new DuplicateObjectRelationshipException(
				StringBundler.concat(
					"There is already an object relationship with this name ",
					"in the object definition \"",
					objectDefinition1.getShortName(), "\""));
		}

		_validateNameObjectFieldName(name, objectDefinition1);
		_validateNameObjectFieldName(name, objectDefinition2);
		_validateNameObjectRelationshipName(name, objectDefinition1);
		_validateNameObjectRelationshipName(name, objectDefinition2);
	}

	private void _validateNameObjectFieldName(
			String name, ObjectDefinition objectDefinition)
		throws PortalException {

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			objectDefinition.getObjectDefinitionId(), name);

		if (objectField == null) {
			return;
		}

		throw new ObjectRelationshipNameException(
			StringBundler.concat(
				"There is already an object field with this name in the ",
				"object definition \"", objectDefinition.getShortName(),
				".\" Object fields and object relationships cannot have the ",
				"same name."));
	}

	private void _validateNameObjectRelationshipName(
			String name, ObjectDefinition objectDefinition)
		throws PortalException {

		ObjectRelationship objectRelationship =
			objectRelationshipLocalService.
				fetchObjectRelationshipByObjectDefinitionId(
					objectDefinition.getObjectDefinitionId(), name);

		if (objectRelationship == null) {
			return;
		}

		if (objectRelationship.getObjectDefinitionId1() !=
				objectDefinition.getObjectDefinitionId()) {

			objectDefinition = _objectDefinitionPersistence.findByPrimaryKey(
				objectRelationship.getObjectDefinitionId1());
		}

		throw new ObjectRelationshipNameException(
			StringBundler.concat(
				"There is already an object relationship with this name in ",
				"the object definition \"", objectDefinition.getShortName(),
				".\" Parent and child object definitions cannot have the same ",
				"name."));
	}

	private void _validateObjectEntryId(
			ObjectDefinition objectDefinition, long primaryKey)
		throws PortalException {

		if (objectDefinition.isUnmodifiableSystemObject()) {
			SystemObjectDefinitionManager systemObjectDefinitionManager =
				_systemObjectDefinitionManagerRegistry.
					getSystemObjectDefinitionManager(
						objectDefinition.getName());

			systemObjectDefinitionManager.getBaseModelExternalReferenceCode(
				primaryKey);
		}
		else {
			_objectEntryLocalService.getObjectEntry(primaryKey);
		}
	}

	private void _validateParameterObjectFieldId(
			ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2, long parameterObjectFieldId,
			String type)
		throws PortalException {

		String restContextPath = StringPool.BLANK;

		if (!objectDefinition1.isUnmodifiableSystemObject()) {
			restContextPath = objectDefinition1.getRESTContextPath();
		}
		else {
			SystemObjectDefinitionManager systemObjectDefinitionManager =
				_systemObjectDefinitionManagerRegistry.
					getSystemObjectDefinitionManager(
						objectDefinition1.getName());

			if (systemObjectDefinitionManager != null) {
				JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
					systemObjectDefinitionManager.
						getJaxRsApplicationDescriptor();

				restContextPath =
					jaxRsApplicationDescriptor.getRESTContextPath();
			}
		}

		boolean parameterRequired = restContextPath.matches(".*/\\{\\w+}/.*");

		if ((parameterObjectFieldId == 0) && parameterRequired) {
			throw new ObjectRelationshipParameterObjectFieldIdException(
				"Object definition " + objectDefinition1.getName() +
					" requires a parameter object field ID");
		}

		if (parameterObjectFieldId > 0) {
			if (!Objects.equals(
					type, ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

				throw new ObjectRelationshipParameterObjectFieldIdException(
					"Object relationship type " + type +
						" does not allow a parameter object field ID");
			}

			if (!parameterRequired) {
				throw new ObjectRelationshipParameterObjectFieldIdException(
					"Object definition " + objectDefinition1.getName() +
						" does not allow a parameter object field ID");
			}

			ObjectField objectField = _objectFieldLocalService.fetchObjectField(
				parameterObjectFieldId);

			if (objectField == null) {
				throw new ObjectRelationshipParameterObjectFieldIdException(
					"Parameter object field ID " + parameterObjectFieldId +
						" does not exist");
			}

			if (objectDefinition2.getObjectDefinitionId() !=
					objectField.getObjectDefinitionId()) {

				throw new ObjectRelationshipParameterObjectFieldIdException(
					StringBundler.concat(
						"Parameter object field ID ", parameterObjectFieldId,
						" does not belong to object definition ",
						objectDefinition2.getName()));
			}

			if (!Objects.equals(
					objectField.getBusinessType(),
					ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

				throw new ObjectRelationshipParameterObjectFieldIdException(
					"Parameter object field ID " + parameterObjectFieldId +
						" does not belong to a relationship object field");
			}
		}
	}

	private void _validateScope(
			ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2)
		throws PortalException {

		if (StringUtil.equals(
				objectDefinition1.getScope(),
				ObjectDefinitionConstants.SCOPE_DEPOT) ^
			StringUtil.equals(
				objectDefinition2.getScope(),
				ObjectDefinitionConstants.SCOPE_DEPOT)) {

			throw new ObjectDefinitionScopeException(
				"An object definition scoped by depot can only be related to " +
					"object definitions of the same scope");
		}
	}

	private void _validateType(
			ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2, String name,
			long parameterObjectFieldId, String type)
		throws PortalException {

		Set<String> defaultObjectRelationshipTypes =
			ObjectRelationshipUtil.getDefaultObjectRelationshipTypes();

		if (!defaultObjectRelationshipTypes.contains(type)) {
			throw new ObjectRelationshipTypeException("Invalid type " + type);
		}

		if (Objects.equals(
				type, ObjectRelationshipConstants.TYPE_MANY_TO_MANY) ||
			Objects.equals(type, ObjectRelationshipConstants.TYPE_ONE_TO_ONE)) {

			int count = objectRelationshipPersistence.countByODI1_ODI2_N_T(
				objectDefinition2.getObjectDefinitionId(),
				objectDefinition1.getObjectDefinitionId(), name, type);

			if (count > 0) {
				throw new ObjectRelationshipTypeException(
					"Inverse type already exists");
			}
		}

		if (objectDefinition1.isUnmodifiableSystemObject()) {
			if (objectDefinition2.isUnmodifiableSystemObject()) {
				throw new ObjectRelationshipTypeException(
					"Relationships are not allowed between system objects");
			}

			SystemObjectDefinitionManager systemObjectDefinitionManager =
				_systemObjectDefinitionManagerRegistry.
					getSystemObjectDefinitionManager(
						objectDefinition1.getName());

			Set<String> allowedObjectRelationshipTypes =
				systemObjectDefinitionManager.
					getAllowedObjectRelationshipTypes();

			if (!allowedObjectRelationshipTypes.contains(type)) {
				throw new ObjectRelationshipTypeException(
					"Invalid type for system object definition " +
						objectDefinition1.getObjectDefinitionId());
			}
		}

		_validateParameterObjectFieldId(
			objectDefinition1, objectDefinition2, parameterObjectFieldId, type);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectRelationshipLocalServiceImpl.class);

	private static final Snapshot<ObjectDefinitionLocalService>
		_objectDefinitionLocalServiceSnapshot = new Snapshot<>(
			ObjectRelationshipLocalServiceImpl.class,
			ObjectDefinitionLocalService.class, null, true);

	private BundleContext _bundleContext;

	@Reference
	private CurrentConnection _currentConnection;

	@Reference
	private ObjectActionPersistence _objectActionPersistence;

	@Reference
	private ObjectDefinitionPersistence _objectDefinitionPersistence;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectFieldPersistence _objectFieldPersistence;

	@Reference
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Reference
	private ObjectFolderItemLocalService _objectFolderItemLocalService;

	@Reference
	private ObjectLayoutTabPersistence _objectLayoutTabPersistence;

	@Reference
	private RelatedInfoCollectionProviderFactory
		_relatedInfoCollectionProviderFactory;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourceActions _resourceActions;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private final Map<String, ServiceRegistration<?>> _serviceRegistrations =
		new ConcurrentHashMap<>();

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	@Reference
	private UserLocalService _userLocalService;

}