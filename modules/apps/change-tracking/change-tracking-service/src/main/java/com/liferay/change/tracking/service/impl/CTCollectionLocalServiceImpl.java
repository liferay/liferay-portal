/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.impl;

import com.liferay.change.tracking.closure.CTClosure;
import com.liferay.change.tracking.closure.CTClosureFactory;
import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.conflict.CTEntryConflictHelper;
import com.liferay.change.tracking.conflict.ConflictInfo;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.exception.CTCollectionDescriptionException;
import com.liferay.change.tracking.exception.CTCollectionNameException;
import com.liferay.change.tracking.exception.CTCollectionStatusException;
import com.liferay.change.tracking.exception.CTEnclosureException;
import com.liferay.change.tracking.exception.CTLocalizedException;
import com.liferay.change.tracking.exception.CTPublishConflictException;
import com.liferay.change.tracking.internal.CTEnclosureUtil;
import com.liferay.change.tracking.internal.CTServiceCopier;
import com.liferay.change.tracking.internal.CTServiceRegistry;
import com.liferay.change.tracking.internal.conflict.CTConflictChecker;
import com.liferay.change.tracking.internal.conflict.ConstraintResolverConflictInfo;
import com.liferay.change.tracking.internal.conflict.ModificationConflictInfo;
import com.liferay.change.tracking.internal.helper.CTTableMapperHelper;
import com.liferay.change.tracking.internal.reference.TableReferenceDefinitionManager;
import com.liferay.change.tracking.internal.resolver.ConstraintResolverKey;
import com.liferay.change.tracking.mapping.CTMappingTableInfo;
import com.liferay.change.tracking.model.CTAutoResolutionInfo;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTCollectionTable;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.model.CTEntryTable;
import com.liferay.change.tracking.model.CTPreferences;
import com.liferay.change.tracking.model.CTSchemaVersion;
import com.liferay.change.tracking.model.CTScore;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.service.CTPreferencesLocalService;
import com.liferay.change.tracking.service.CTSchemaVersionLocalService;
import com.liferay.change.tracking.service.base.CTCollectionLocalServiceBaseImpl;
import com.liferay.change.tracking.service.persistence.CTAutoResolutionInfoPersistence;
import com.liferay.change.tracking.service.persistence.CTCommentPersistence;
import com.liferay.change.tracking.service.persistence.CTEntryPersistence;
import com.liferay.change.tracking.service.persistence.CTMessagePersistence;
import com.liferay.change.tracking.service.persistence.CTPreferencesPersistence;
import com.liferay.change.tracking.service.persistence.CTScorePersistence;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.resolver.ConstraintResolver;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.dao.jdbc.CurrentConnection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.model.uid.UIDFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Daniel Kocsis
 * @author Preston Crary
 */
@Component(
	configurationPid = "com.liferay.change.tracking.configuration.CTSettingsConfiguration",
	property = "model.class.name=com.liferay.change.tracking.model.CTCollection",
	service = AopService.class
)
public class CTCollectionLocalServiceImpl
	extends CTCollectionLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CTCollection addCTCollection(
			String externalReferenceCode, long companyId, long userId,
			long ctRemoteId, String name, String description)
		throws PortalException {

		_validate(name, description);

		long ctCollectionId = counterLocalService.increment(
			CTCollection.class.getName());

		CTCollection ctCollection = ctCollectionPersistence.create(
			ctCollectionId);

		ctCollection.setExternalReferenceCode(externalReferenceCode);
		ctCollection.setCompanyId(companyId);
		ctCollection.setUserId(userId);
		ctCollection.setCtRemoteId(ctRemoteId);

		CTSchemaVersion latestCTSchemaVersion =
			_ctSchemaVersionLocalService.getLatestCTSchemaVersion(companyId);

		ctCollection.setSchemaVersionId(
			latestCTSchemaVersion.getSchemaVersionId());

		ctCollection.setName(name);
		ctCollection.setDescription(description);
		ctCollection.setShareable(false);
		ctCollection.setStatus(WorkflowConstants.STATUS_DRAFT);

		ctCollection = ctCollectionPersistence.update(ctCollection);

		CTScore ctScore = _ctScorePersistence.create(
			counterLocalService.increment(CTScore.class.getName()));

		ctScore.setCompanyId(companyId);
		ctScore.setCtCollectionId(ctCollectionId);

		_ctScorePersistence.update(ctScore);

		_resourceLocalService.addResources(
			ctCollection.getCompanyId(), 0, ctCollection.getUserId(),
			CTCollection.class.getName(), ctCollection.getCtCollectionId(),
			false, false, false);

		CTSettingsConfiguration ctSettingsConfiguration =
			_configurationProvider.getCompanyConfiguration(
				CTSettingsConfiguration.class, ctCollection.getCompanyId());

		if (ArrayUtil.isNotEmpty(
				ctSettingsConfiguration.defaultOwnerActionIds())) {

			Role role = _roleLocalService.getRole(
				ctCollection.getCompanyId(), RoleConstants.OWNER);

			_resourcePermissionLocalService.setResourcePermissions(
				ctCollection.getCompanyId(), CTCollection.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(ctCollection.getCtCollectionId()),
				role.getRoleId(),
				ctSettingsConfiguration.defaultOwnerActionIds());
		}

		return ctCollection;
	}

	@Override
	public Map<Long, List<ConflictInfo>> checkConflicts(
			CTCollection ctCollection)
		throws PortalException {

		List<CTEntry> ctEntries = _ctEntryPersistence.findByCtCollectionId(
			ctCollection.getCtCollectionId());

		return checkConflicts(
			ctCollection.getCompanyId(), ctEntries,
			ctCollection.getCtCollectionId(), ctCollection.getName(),
			CTConstants.CT_COLLECTION_ID_PRODUCTION, "Production");
	}

	@Override
	public Map<Long, List<ConflictInfo>> checkConflicts(
			long companyId, List<CTEntry> ctEntries, long fromCTCollectionId,
			String fromCTCollectionName, long toCTCollectionId,
			String toCTCollectionName)
		throws PortalException {

		Map<Long, List<ConflictInfo>> conflictInfosMap = new HashMap<>();

		Map<Long, CTConflictChecker<?>> ctConflictCheckers = new HashMap<>();
		CTSettingsConfiguration ctSettingsConfiguration =
			_configurationProvider.getCompanyConfiguration(
				CTSettingsConfiguration.class, companyId);

		for (CTEntry ctEntry : ctEntries) {
			CTConflictChecker<?> ctConflictChecker =
				ctConflictCheckers.computeIfAbsent(
					ctEntry.getModelClassNameId(),
					modelClassNameId -> {
						CTService<?> ctService =
							_ctServiceRegistry.getCTService(modelClassNameId);

						if (ctService == null) {
							throw new SystemException(
								StringBundler.concat(
									"Unable to check conflicts for ",
									fromCTCollectionName, " to ",
									toCTCollectionName, " because service for ",
									modelClassNameId, " is missing"));
						}

						return new CTConflictChecker<>(
							_classNameLocalService,
							_constraintResolverServiceTrackerMap,
							_ctDisplayRendererServiceTrackerMap,
							_ctEntryConflictHelperServiceTrackerMap,
							_ctEntryLocalService, ctService,
							ctSettingsConfiguration, modelClassNameId,
							fromCTCollectionId,
							_tableReferenceDefinitionManager, toCTCollectionId);
					});

			ctConflictChecker.addCTEntry(ctEntry);
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					fromCTCollectionId)) {

			for (Map.Entry<Long, CTConflictChecker<?>> entry :
					ctConflictCheckers.entrySet()) {

				CTConflictChecker<?> ctConflictChecker = entry.getValue();

				List<ConflictInfo> conflictInfos = ctConflictChecker.check();

				if (!conflictInfos.isEmpty()) {
					conflictInfosMap.put(entry.getKey(), conflictInfos);
				}
			}
		}

		if (toCTCollectionId != CTConstants.CT_COLLECTION_ID_PRODUCTION) {
			return conflictInfosMap;
		}

		// Exclude created CTAutoResolutionInfos

		List<CTAutoResolutionInfo> ctAutoResolutionInfos =
			_ctAutoResolutionInfoPersistence.findByCtCollectionId(
				fromCTCollectionId);

		for (Map.Entry<Long, List<ConflictInfo>> entry :
				conflictInfosMap.entrySet()) {

			for (ConflictInfo conflictInfo : entry.getValue()) {
				if (!conflictInfo.isResolved()) {
					continue;
				}

				CTAutoResolutionInfo ctAutoResolutionInfo =
					_ctAutoResolutionInfoPersistence.create(
						counterLocalService.increment(
							CTAutoResolutionInfo.class.getName()));

				ctAutoResolutionInfo.setCompanyId(companyId);
				ctAutoResolutionInfo.setCreateDate(new Date());
				ctAutoResolutionInfo.setCtCollectionId(fromCTCollectionId);
				ctAutoResolutionInfo.setModelClassNameId(entry.getKey());
				ctAutoResolutionInfo.setSourceModelClassPK(
					conflictInfo.getSourcePrimaryKey());
				ctAutoResolutionInfo.setTargetModelClassPK(
					conflictInfo.getTargetPrimaryKey());

				if (conflictInfo instanceof ConstraintResolverConflictInfo) {
					ConstraintResolverConflictInfo
						constraintResolverConflictInfo =
							(ConstraintResolverConflictInfo)conflictInfo;

					ConstraintResolver<?> constraintResolver =
						constraintResolverConflictInfo.getConstraintResolver();

					ctAutoResolutionInfo.setConflictIdentifier(
						StringUtil.merge(
							constraintResolver.getUniqueIndexColumnNames(),
							StringPool.COMMA));

					constraintResolverConflictInfo.setCtAutoResolutionInfoId(
						ctAutoResolutionInfo.getCtAutoResolutionInfoId());
				}
				else if (conflictInfo instanceof ModificationConflictInfo) {
					ModificationConflictInfo resolvedModificationConflictInfo =
						(ModificationConflictInfo)conflictInfo;

					resolvedModificationConflictInfo.setCtAutoResolutionInfoId(
						ctAutoResolutionInfo.getCtAutoResolutionInfoId());

					ctAutoResolutionInfo.setConflictIdentifier(
						ModificationConflictInfo.class.getName());
				}

				_ctAutoResolutionInfoPersistence.update(ctAutoResolutionInfo);
			}
		}

		for (CTAutoResolutionInfo ctAutoResolutionInfo :
				ctAutoResolutionInfos) {

			List<ConflictInfo> conflictInfos = conflictInfosMap.computeIfAbsent(
				ctAutoResolutionInfo.getModelClassNameId(),
				key -> new ArrayList<>());

			if (Objects.equals(
					ctAutoResolutionInfo.getConflictIdentifier(),
					ModificationConflictInfo.class.getName())) {

				ModificationConflictInfo resolvedModificationConflictInfo =
					new ModificationConflictInfo(
						ctAutoResolutionInfo.getSourceModelClassPK(), true);

				resolvedModificationConflictInfo.setCtAutoResolutionInfoId(
					ctAutoResolutionInfo.getCtAutoResolutionInfoId());

				conflictInfos.add(resolvedModificationConflictInfo);
			}
			else {
				List<String> uniqueIndexes = StringUtil.split(
					ctAutoResolutionInfo.getConflictIdentifier(),
					CharPool.COMMA);

				ClassName className = _classNameLocalService.getClassName(
					ctAutoResolutionInfo.getModelClassNameId());

				ConstraintResolver<?> constraintResolver =
					_constraintResolverServiceTrackerMap.getService(
						new ConstraintResolverKey(
							className.getValue(),
							uniqueIndexes.toArray(new String[0])));

				if (constraintResolver != null) {
					ConstraintResolverConflictInfo
						constraintResolverConflictInfo =
							new ConstraintResolverConflictInfo(
								constraintResolver, true,
								ctAutoResolutionInfo.getSourceModelClassPK(),
								ctAutoResolutionInfo.getTargetModelClassPK());

					constraintResolverConflictInfo.setCtAutoResolutionInfoId(
						ctAutoResolutionInfo.getCtAutoResolutionInfoId());

					conflictInfos.add(constraintResolverConflictInfo);
				}
			}
		}

		return conflictInfosMap;
	}

	@Override
	public Map<Long, List<ConflictInfo>> checkConflicts(
			long companyId, long[] ctEntryIds, long fromCTCollectionId,
			String fromCTCollectionName, long toCTCollectionId,
			String toCTCollectionName)
		throws PortalException {

		List<CTEntry> ctEntries = getRelatedCTEntries(
			fromCTCollectionId, ctEntryIds);

		return checkConflicts(
			companyId, ctEntries, fromCTCollectionId, fromCTCollectionName,
			toCTCollectionId, toCTCollectionName);
	}

	@Override
	public void deleteCompanyCTCollections(long companyId)
		throws PortalException {

		List<CTCollection> ctCollections =
			ctCollectionPersistence.findByCompanyId(companyId);

		for (CTCollection ctCollection : ctCollections) {
			deleteCTCollection(ctCollection);
		}
	}

	@Override
	public void deleteCTAutoResolutionInfo(long ctAutoResolutionInfoId) {
		CTAutoResolutionInfo ctAutoResolutionInfo =
			_ctAutoResolutionInfoPersistence.fetchByPrimaryKey(
				ctAutoResolutionInfoId);

		if (ctAutoResolutionInfo != null) {
			_ctAutoResolutionInfoPersistence.remove(ctAutoResolutionInfo);
		}
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public CTCollection deleteCTCollection(CTCollection ctCollection)
		throws PortalException {

		_ctServiceRegistry.onBeforeRemove(ctCollection.getCtCollectionId());

		try {
			for (CTTableMapperHelper ctTableMapperHelper :
					_ctServiceRegistry.getCTTableMapperHelpers()) {

				ctTableMapperHelper.delete(ctCollection.getCtCollectionId());
			}
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}

		List<CTEntry> ctEntries = _ctEntryPersistence.findByCtCollectionId(
			ctCollection.getCtCollectionId());

		Map<Long, List<Long>> modelClassPKsMap = new HashMap<>();

		for (CTEntry ctEntry : ctEntries) {
			List<Long> modelClassPKs = modelClassPKsMap.get(
				ctEntry.getModelClassNameId());

			if (modelClassPKs == null) {
				modelClassPKs = new ArrayList<>();

				modelClassPKsMap.put(
					ctEntry.getModelClassNameId(), modelClassPKs);
			}

			modelClassPKs.add(ctEntry.getModelClassPK());
		}

		for (Map.Entry<Long, List<Long>> entry : modelClassPKsMap.entrySet()) {
			CTService<?> ctService = _ctServiceRegistry.getCTService(
				entry.getKey());

			if (ctService == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"No CT service found for class name ID " +
							entry.getKey());
				}

				continue;
			}

			ctService.updateWithUnsafeFunction(
				ctPersistence -> {
					Set<String> primaryKeyNames =
						ctPersistence.getCTColumnNames(
							CTColumnResolutionType.PK);

					if (primaryKeyNames.size() != 1) {
						throw new IllegalArgumentException(
							StringBundler.concat(
								"{primaryKeyNames=", primaryKeyNames,
								", tableName=", ctPersistence.getTableName(),
								"}"));
					}

					Iterator<String> iterator = primaryKeyNames.iterator();

					String primaryKeyName = iterator.next();

					StringBundler sb = new StringBundler(7);

					sb.append("delete from ");
					sb.append(ctPersistence.getTableName());
					sb.append(" where ctCollectionId = ");
					sb.append(ctCollection.getCtCollectionId());
					sb.append(" and ");
					sb.append(primaryKeyName);
					sb.append(" = ?");

					Connection connection = _currentConnection.getConnection(
						ctPersistence.getDataSource());

					try (PreparedStatement preparedStatement =
							AutoBatchPreparedStatementUtil.autoBatch(
								connection, sb.toString())) {

						for (long modelClassPK : entry.getValue()) {
							preparedStatement.setLong(1, modelClassPK);

							preparedStatement.addBatch();
						}

						return preparedStatement.executeBatch();
					}
					catch (Exception exception) {
						throw new SystemException(exception);
					}
				});
		}

		_ctAutoResolutionInfoPersistence.removeByCtCollectionId(
			ctCollection.getCtCollectionId());

		_ctCommentPersistence.removeByCtCollectionId(
			ctCollection.getCtCollectionId());

		for (CTEntry ctEntry : ctEntries) {
			_ctEntryPersistence.remove(ctEntry);
		}

		_ctMessagePersistence.removeByCtCollectionId(
			ctCollection.getCtCollectionId());

		CTScore ctScore = _ctScorePersistence.fetchByCtCollectionId(
			ctCollection.getCtCollectionId());

		if (ctScore != null) {
			_ctScorePersistence.remove(ctScore);
		}

		Group group = _groupLocalService.fetchGroup(
			ctCollection.getCompanyId(),
			_classNameLocalService.getClassNameId(CTCollection.class),
			ctCollection.getCtCollectionId());

		if (group != null) {
			_groupLocalService.deleteGroup(group);
		}

		_resourceLocalService.deleteResource(
			ctCollection.getCompanyId(), CTCollection.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			ctCollection.getCtCollectionId());

		int count = ctCollectionPersistence.countByC_SVI(
			ctCollection.getCompanyId(), ctCollection.getSchemaVersionId());

		if (count == 1) {
			CTSchemaVersion ctSchemaVersion =
				_ctSchemaVersionLocalService.fetchCTSchemaVersion(
					ctCollection.getSchemaVersionId());

			if ((ctSchemaVersion != null) &&
				!_ctSchemaVersionLocalService.isLatestCTSchemaVersion(
					ctSchemaVersion, true)) {

				_ctSchemaVersionLocalService.deleteCTSchemaVersion(
					ctSchemaVersion);
			}
		}

		Indexer<CTEntry> indexer = _indexerRegistry.getIndexer(CTEntry.class);

		if (indexer != null) {
			_indexWriterHelper.deleteDocuments(
				ctCollection.getCompanyId(),
				TransformUtil.transform(
					ctEntries, ctEntry -> _uidFactory.getUID(ctEntry)),
				indexer.isCommitImmediately());
		}

		return ctCollectionPersistence.remove(ctCollection);
	}

	@Override
	public void discardCTEntry(
			long ctCollectionId, List<CTEntry> ctEntries, boolean force)
		throws PortalException {

		CTCollection ctCollection = ctCollectionPersistence.findByPrimaryKey(
			ctCollectionId);

		if (!force && ctCollection.isReadOnly()) {
			throw new PortalException(
				"Change tracking collection " + ctCollection + " is read only");
		}

		Map<Long, List<CTEntry>> relateCTEntriesMap = getRelatedCTEntriesMap(
			ctCollection.getCtCollectionId(), ctEntries);

		for (Map.Entry<Long, List<CTEntry>> entry :
				relateCTEntriesMap.entrySet()) {

			_discardCTEntries(
				ctCollection, entry.getKey(), entry.getValue(), force);
		}

		_ctClosureFactory.clearCache(ctCollection.getCtCollectionId());
	}

	@Override
	public void discardCTEntry(
			long ctCollectionId, long modelClassNameId, long modelClassPK,
			boolean force)
		throws PortalException {

		CTEntry ctEntry = _ctEntryPersistence.findByC_MCNI_MCPK(
			ctCollectionId, modelClassNameId, modelClassPK);

		discardCTEntry(
			ctCollectionId, Collections.singletonList(ctEntry), force);
	}

	@Override
	public List<CTCollection> getCTCollections(
		long companyId, int status, int start, int end,
		OrderByComparator<CTCollection> orderByComparator) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return ctCollectionPersistence.findByCompanyId(
				companyId, start, end, orderByComparator);
		}

		return ctCollectionPersistence.findByC_S(
			companyId, status, start, end, orderByComparator);
	}

	@Override
	public List<CTCollection> getCTCollections(
		long companyId, int[] statuses, int start, int end,
		OrderByComparator<CTCollection> orderByComparator) {

		if (ArrayUtil.contains(statuses, WorkflowConstants.STATUS_ANY)) {
			return ctCollectionPersistence.findByCompanyId(
				companyId, start, end, orderByComparator);
		}

		return ctCollectionPersistence.findByC_S(
			companyId, statuses, start, end, orderByComparator);
	}

	@Override
	public List<CTMappingTableInfo> getCTMappingTableInfos(
		long ctCollectionId) {

		List<CTMappingTableInfo> ctMappingTableInfos = new ArrayList<>();

		for (CTTableMapperHelper ctTableMapperHelper :
				_ctServiceRegistry.getCTTableMapperHelpers()) {

			CTMappingTableInfo ctMappingTableInfo =
				ctTableMapperHelper.getCTMappingTableInfo(ctCollectionId);

			if (ctMappingTableInfo != null) {
				ctMappingTableInfos.add(ctMappingTableInfo);
			}
		}

		return ctMappingTableInfos;
	}

	@Override
	public List<CTCollection> getExclusivePublishedCTCollections(
			long modelClassNameId, long modelClassPK)
		throws PortalException {

		return ctCollectionPersistence.dslQuery(
			DSLQueryFactoryUtil.select(
				CTCollectionTable.INSTANCE
			).from(
				CTCollectionTable.INSTANCE
			).innerJoinON(
				CTEntryTable.INSTANCE,
				CTEntryTable.INSTANCE.ctCollectionId.eq(
					CTCollectionTable.INSTANCE.ctCollectionId
				).and(
					CTEntryTable.INSTANCE.modelClassNameId.eq(
						modelClassNameId
					).and(
						CTEntryTable.INSTANCE.modelClassPK.eq(modelClassPK)
					)
				)
			).where(
				CTCollectionTable.INSTANCE.ctCollectionId.neq(
					CTCollectionThreadLocal.getCTCollectionId()
				).and(
					CTCollectionTable.INSTANCE.status.neq(
						WorkflowConstants.STATUS_EXPIRED)
				)
			).orderBy(
				CTCollectionTable.INSTANCE.status.descending(),
				CTCollectionTable.INSTANCE.statusDate.descending()
			));
	}

	@Override
	public List<CTEntry> getRelatedCTEntries(
			long ctCollectionId, long[] ctEntryIds)
		throws PortalException {

		Set<CTEntry> relatedCTEntries = new HashSet<>();

		Map<Long, List<CTEntry>> relatedCTEntriesMap = getRelatedCTEntriesMap(
			ctCollectionId, _ctEntryLocalService.getCTEntries(ctEntryIds));

		for (List<CTEntry> ctEntries : relatedCTEntriesMap.values()) {
			relatedCTEntries.addAll(ctEntries);
		}

		return new ArrayList<>(relatedCTEntries);
	}

	@Override
	public Map<Long, List<CTEntry>> getRelatedCTEntriesMap(
			long ctCollectionId, List<CTEntry> ctEntries)
		throws PortalException {

		Map<Long, List<CTEntry>> relatedCTEntriesMap = new HashMap<>();

		CTCollection ctCollection = ctCollectionPersistence.findByPrimaryKey(
			ctCollectionId);

		for (CTEntry ctEntry : ctEntries) {
			Map<Long, List<CTEntry>> currentRelatedCTEntriesMap =
				_getRelatedCTEntriesMap(
					ctCollection, ctEntry.getModelClassNameId(),
					ctEntry.getModelClassPK());

			currentRelatedCTEntriesMap.forEach(
				(key, value) -> relatedCTEntriesMap.merge(
					key, value,
					(value1, value2) -> ListUtil.concat(value1, value2)));
		}

		for (CTEntry ctEntry : ctEntries) {
			List<CTEntry> peerCTEntries = _ctEntryLocalService.getCTEntries(
				ctCollectionId, ctEntry.getModelClassNameId());

			for (CTEntry peerCTEntry : peerCTEntries) {
				List<CTEntry> relatedCTEntries = relatedCTEntriesMap.get(
					peerCTEntry.getModelClassNameId());

				if ((relatedCTEntries != null) &&
					relatedCTEntries.contains(peerCTEntry)) {

					continue;
				}

				Map<Long, List<CTEntry>> peerRelatedCTEntriesMap =
					_getRelatedCTEntriesMap(
						ctCollection, peerCTEntry.getModelClassNameId(),
						peerCTEntry.getModelClassPK());

				peerRelatedCTEntriesMap.forEach(
					(key1, peerRelatedCTEntries) ->
						relatedCTEntriesMap.computeIfPresent(
							key1,
							(key2, currentCTEntries) -> ListUtil.remove(
								currentCTEntries, peerRelatedCTEntries)));
			}
		}

		return relatedCTEntriesMap;
	}

	public Map<Long, List<CTEntry>> getRelatedCTEntriesMap(
			long ctCollectionId, long modelClassNameId, long modelClassPK)
		throws PortalException {

		CTEntry ctEntry = _ctEntryPersistence.findByC_MCNI_MCPK(
			ctCollectionId, modelClassNameId, modelClassPK);

		return getRelatedCTEntriesMap(
			ctCollectionId, Collections.singletonList(ctEntry));
	}

	public Map<Long, List<CTEntry>> getRelatedCTEntriesMap(
			long ctCollectionId, long[] ctEntryIds)
		throws PortalException {

		return getRelatedCTEntriesMap(
			ctCollectionId, _ctEntryLocalService.getCTEntries(ctEntryIds));
	}

	@Override
	public boolean hasUnapprovedChanges(long ctCollectionId)
		throws SQLException {

		Map<Long, CTPersistence<?>> ctPersistences = new HashMap<>();
		Set<Long> modelClassNameIds = new HashSet<>();

		for (CTEntry ctEntry :
				_ctEntryLocalService.getCTCollectionCTEntries(ctCollectionId)) {

			long modelClassNameId = ctEntry.getModelClassNameId();

			if (ctPersistences.containsKey(modelClassNameId) ||
				modelClassNameIds.contains(modelClassNameId)) {

				continue;
			}

			CTService<?> ctService = _ctServiceRegistry.getCTService(
				modelClassNameId);

			if (ctService == null) {
				throw new SystemException(
					StringBundler.concat(
						"Unable to check for unapproved changes for change ",
						"tracking collection ", ctCollectionId,
						" because the service for ", modelClassNameId,
						" is missing"));
			}

			CTPersistence<?> ctPersistence = ctService.getCTPersistence();

			Map<String, Integer> tableColumnsMap =
				ctPersistence.getTableColumnsMap();

			if (tableColumnsMap.containsKey("status") &&
				tableColumnsMap.containsKey("statusByUserId")) {

				ctPersistences.putIfAbsent(
					modelClassNameId, ctService.getCTPersistence());
			}
			else {
				modelClassNameIds.add(modelClassNameId);
			}
		}

		for (Map.Entry<Long, CTPersistence<?>> entry :
				ctPersistences.entrySet()) {

			CTPersistence<?> ctPersistence = entry.getValue();

			DataSource dataSource = ctPersistence.getDataSource();

			try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement =
					connection.prepareStatement(
						StringBundler.concat(
							"select count(*) from ",
							ctPersistence.getTableName(),
							" where ctCollectionId = ", ctCollectionId,
							" and status not in (",
							StringUtil.merge(
								_getStatuses(
									ctCollectionId, ctPersistence, entry),
								StringPool.COMMA),
							")"));
				ResultSet resultSet = preparedStatement.executeQuery()) {

				if (resultSet.next()) {
					int count = resultSet.getInt(1);

					if (count > 0) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public boolean isCTEntryEnclosed(
		long ctCollectionId, long modelClassNameId, long modelClassPK) {

		CTClosure ctClosure = _ctClosureFactory.create(ctCollectionId);

		Map<Long, Set<Long>> enclosureMap = CTEnclosureUtil.getEnclosureMap(
			ctClosure, modelClassNameId, modelClassPK);

		for (Map.Entry<Long, Long> entry :
				CTEnclosureUtil.getEnclosureParentEntries(
					ctClosure, enclosureMap)) {

			int count = _ctEntryPersistence.countByC_MCNI_MCPK(
				ctCollectionId, entry.getKey(), entry.getValue());

			if (count > 0) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void moveCTEntries(
			long fromCTCollectionId, long toCTCollectionId,
			List<CTEntry> ctEntries)
		throws PortalException {

		CTCollection fromCTCollection =
			ctCollectionPersistence.findByPrimaryKey(fromCTCollectionId);

		if ((fromCTCollection.getStatus() != WorkflowConstants.STATUS_DRAFT) &&
			(fromCTCollection.getStatus() !=
				WorkflowConstants.STATUS_EXPIRED) &&
			(fromCTCollection.getStatus() !=
				WorkflowConstants.STATUS_PENDING)) {

			throw new CTCollectionStatusException(
				"Change tracking collection " + fromCTCollection +
					" is read only");
		}

		CTCollection toCTCollection = ctCollectionPersistence.findByPrimaryKey(
			toCTCollectionId);

		if (toCTCollection.isReadOnly()) {
			throw new CTCollectionStatusException(
				"Change tracking collection " + toCTCollection +
					" is read only");
		}

		Map<Long, List<CTEntry>> relatedCTEntriesMap = getRelatedCTEntriesMap(
			fromCTCollection.getCtCollectionId(), ctEntries);

		List<CTEntry> relatedCTEntries = new ArrayList<>();

		for (List<CTEntry> value : relatedCTEntriesMap.values()) {
			relatedCTEntries.addAll(value);
		}

		Map<Long, List<ConflictInfo>> conflictInfosMap = checkConflicts(
			fromCTCollection.getCompanyId(), relatedCTEntries,
			fromCTCollectionId, fromCTCollection.getName(), toCTCollectionId,
			toCTCollection.getName());

		if (!conflictInfosMap.isEmpty()) {
			throw new CTPublishConflictException("Conflict detected");
		}

		for (Map.Entry<Long, List<CTEntry>> entry :
				relatedCTEntriesMap.entrySet()) {

			_moveCTEntries(
				fromCTCollection.getCompanyId(), fromCTCollectionId,
				toCTCollectionId, entry.getKey(), entry.getValue());
		}

		conflictInfosMap = checkConflicts(
			toCTCollection.getCompanyId(),
			getRelatedCTEntries(
				toCTCollection.getCtCollectionId(),
				TransformUtil.transformToLongArray(
					ctEntries, CTEntry::getCtEntryId)),
			toCTCollectionId, toCTCollection.getName(),
			CTConstants.CT_COLLECTION_ID_PRODUCTION, "Production");

		for (Map.Entry<Long, List<ConflictInfo>> entry :
				conflictInfosMap.entrySet()) {

			List<ConflictInfo> conflictInfos = entry.getValue();

			for (ConflictInfo conflictInfo : conflictInfos) {
				if (!conflictInfo.isResolved()) {
					throw new CTPublishConflictException("Conflict detected");
				}
			}
		}

		_ctClosureFactory.clearCache(fromCTCollectionId);
		_ctClosureFactory.clearCache(toCTCollectionId);
	}

	@Override
	public void moveCTEntry(
			long fromCTCollectionId, long toCTCollectionId,
			long modelClassNameId, long modelClassPK)
		throws PortalException {

		moveCTEntries(
			fromCTCollectionId, toCTCollectionId,
			Collections.singletonList(
				_ctEntryLocalService.fetchCTEntry(
					fromCTCollectionId, modelClassNameId, modelClassPK)));
	}

	@Override
	public CTCollection undoCTCollection(
			long ctCollectionId, long userId, String name, String description)
		throws PortalException {

		CTCollection undoCTCollection =
			ctCollectionPersistence.findByPrimaryKey(ctCollectionId);

		if (undoCTCollection.getStatus() != WorkflowConstants.STATUS_APPROVED) {
			throw new CTLocalizedException(
				StringBundler.concat(
					"Unable to undo ", undoCTCollection.getName(),
					" because it is not published"),
				"unable-to-revert-x-because-it-is-not-published",
				undoCTCollection.getName());
		}

		if (!_ctSchemaVersionLocalService.isLatestCTSchemaVersion(
				undoCTCollection.getSchemaVersionId())) {

			throw new CTLocalizedException(
				StringBundler.concat(
					"Unable to undo ", undoCTCollection.getName(),
					" because it is out of date with the current release"),
				"unable-to-revert-x-because-it-is-out-of-date-with-the-" +
					"current-release",
				undoCTCollection.getName());
		}

		CTCollection newCTCollection = addCTCollection(
			null, undoCTCollection.getCompanyId(), userId,
			undoCTCollection.getCtRemoteId(), name, description);

		CTPreferences ctPreferences =
			_ctPreferencesLocalService.getCTPreferences(
				undoCTCollection.getCompanyId(), userId);

		ctPreferences.setCtCollectionId(newCTCollection.getCtCollectionId());
		ctPreferences.setPreviousCtCollectionId(
			CTConstants.CT_COLLECTION_ID_PRODUCTION);

		_ctPreferencesPersistence.update(ctPreferences);

		List<CTEntry> publishedCTEntries =
			_ctEntryPersistence.findByCtCollectionId(
				undoCTCollection.getCtCollectionId());

		Map<Long, CTServiceCopier<?>> ctServiceCopiers = new HashMap<>();

		long batchCounter = counterLocalService.increment(
			CTEntry.class.getName(), publishedCTEntries.size());

		batchCounter -= publishedCTEntries.size();

		for (CTEntry publishedCTEntry : publishedCTEntries) {
			long modelClassNameId = publishedCTEntry.getModelClassNameId();

			CTServiceCopier<?> ctServiceCopier = ctServiceCopiers.get(
				modelClassNameId);

			if (ctServiceCopier == null) {
				CTService<?> ctService = _ctServiceRegistry.getCTService(
					modelClassNameId);

				if (ctService == null) {
					throw new CTLocalizedException(
						StringBundler.concat(
							"Unable to undo ", undoCTCollection.getName(),
							" because service for ", modelClassNameId,
							" is missing"),
						"unable-to-revert-x-because-service-for-x-is-missing",
						undoCTCollection.getName(),
						publishedCTEntry.getModelClassNameId());
				}

				ctServiceCopier = new CTServiceCopier<>(
					ctService, undoCTCollection.getCtCollectionId(),
					newCTCollection.getCtCollectionId());

				ctServiceCopiers.put(modelClassNameId, ctServiceCopier);
			}

			CTEntry ctEntry = _ctEntryPersistence.create(++batchCounter);

			ctEntry.setCompanyId(newCTCollection.getCompanyId());
			ctEntry.setUserId(newCTCollection.getUserId());
			ctEntry.setCtCollectionId(newCTCollection.getCtCollectionId());
			ctEntry.setModelClassNameId(modelClassNameId);
			ctEntry.setModelClassPK(publishedCTEntry.getModelClassPK());
			ctEntry.setModelMvccVersion(publishedCTEntry.getModelMvccVersion());

			int changeType = publishedCTEntry.getChangeType();

			if (changeType == CTConstants.CT_CHANGE_TYPE_ADDITION) {
				changeType = CTConstants.CT_CHANGE_TYPE_DELETION;
			}
			else if (changeType == CTConstants.CT_CHANGE_TYPE_DELETION) {
				changeType = CTConstants.CT_CHANGE_TYPE_ADDITION;
			}

			ctEntry.setChangeType(changeType);

			ctServiceCopier.addCTEntry(
				_ctEntryLocalService.updateCTEntry(ctEntry));
		}

		try {
			for (CTServiceCopier<?> ctServiceCopier :
					ctServiceCopiers.values()) {

				ctServiceCopier.copy();
			}

			for (CTTableMapperHelper ctTableMapperHelper :
					_ctServiceRegistry.getCTTableMapperHelpers()) {

				ctTableMapperHelper.undo(
					undoCTCollection.getCtCollectionId(),
					newCTCollection.getCtCollectionId());
			}
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}

		List<CTEntry> newCTEntries =
			_ctEntryLocalService.getCTCollectionCTEntries(
				newCTCollection.getCtCollectionId());

		if (newCTEntries.size() != publishedCTEntries.size()) {
			throw new SystemException(
				StringBundler.concat(
					"Expected ", publishedCTEntries.size(),
					" change tracking entries instead of ",
					newCTEntries.size()));
		}

		_ctServiceRegistry.onAfterCopy(undoCTCollection, newCTCollection);

		return newCTCollection;
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public CTCollection updateCTCollection(
			long userId, long ctCollectionId, String name, String description)
		throws PortalException {

		_validate(name, description);

		CTCollection ctCollection = ctCollectionPersistence.findByPrimaryKey(
			ctCollectionId);

		Date modifiedDate = new Date();

		ctCollection.setModifiedDate(modifiedDate);

		ctCollection.setName(name);
		ctCollection.setDescription(description);
		ctCollection.setStatusByUserId(userId);
		ctCollection.setStatusDate(modifiedDate);

		return ctCollectionPersistence.update(ctCollection);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_constraintResolverServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext,
				(Class<ConstraintResolver<?>>)
					(Class<?>)ConstraintResolver.class,
				null,
				(serviceReference, emitter) -> {
					ConstraintResolver<?> constraintResolver =
						bundleContext.getService(serviceReference);

					emitter.emit(
						new ConstraintResolverKey(
							constraintResolver.getModelClass(),
							constraintResolver.getUniqueIndexColumnNames()));
				});

		_ctDisplayRendererServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext,
				(Class<CTDisplayRenderer<?>>)(Class<?>)CTDisplayRenderer.class,
				null,
				(serviceReference, emitter) -> {
					CTDisplayRenderer<?> ctDisplayRenderer =
						bundleContext.getService(serviceReference);

					Class<?> modelClass = ctDisplayRenderer.getModelClass();

					emitter.emit(modelClass.getName());
				});

		_ctEntryConflictHelperServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, CTEntryConflictHelper.class, null,
				(serviceReference, emitter) -> {
					CTEntryConflictHelper ctEntryConflictHelper =
						bundleContext.getService(serviceReference);

					Class<?> modelClass = ctEntryConflictHelper.getModelClass();

					emitter.emit(modelClass.getName());
				});
	}

	@Deactivate
	@Override
	protected void deactivate() {
		super.deactivate();

		_constraintResolverServiceTrackerMap.close();

		_ctDisplayRendererServiceTrackerMap.close();

		_ctEntryConflictHelperServiceTrackerMap.close();
	}

	private void _discardCTEntries(
			CTCollection ctCollection, long classNameId,
			List<CTEntry> ctEntries, boolean force)
		throws PortalException {

		if (ListUtil.isEmpty(ctEntries)) {
			return;
		}

		CTService<?> ctService = _ctServiceRegistry.getCTService(classNameId);

		ctService.updateWithUnsafeFunction(
			ctPersistence -> {
				Set<String> primaryKeyNames = ctPersistence.getCTColumnNames(
					CTColumnResolutionType.PK);

				if (primaryKeyNames.size() != 1) {
					throw new IllegalArgumentException(
						StringBundler.concat(
							"{primaryKeyNames=", primaryKeyNames,
							", tableName=", ctPersistence.getTableName(), "}"));
				}

				Iterator<String> iterator = primaryKeyNames.iterator();

				String primaryKeyName = iterator.next();

				_processDiscardEntriesQuery(
					ctCollection.getCtCollectionId(), ctEntries, ctPersistence,
					primaryKeyName);

				return null;
			});

		List<Long> modelClassPKs = new ArrayList<>(ctEntries.size());

		for (CTEntry ctEntry : ctEntries) {
			modelClassPKs.add(ctEntry.getModelClassPK());

			_ctEntryLocalService.deleteCTEntry(ctEntry, force);
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			CTPersistence<?> ctPersistence = ctService.getCTPersistence();

			ctPersistence.clearCache(new HashSet<>(modelClassPKs));
		}

		int processedClassPKs = 0;

		while (processedClassPKs < modelClassPKs.size()) {
			int batchSize = Math.min(
				modelClassPKs.size() - processedClassPKs, _BATCH_SIZE);

			for (CTAutoResolutionInfo ctAutoResolutionInfo :
					_ctAutoResolutionInfoPersistence.findByC_MCNI_SMCPK(
						ctCollection.getCtCollectionId(), classNameId,
						ArrayUtil.toLongArray(
							modelClassPKs.subList(
								processedClassPKs,
								processedClassPKs + batchSize)))) {

				_ctAutoResolutionInfoPersistence.remove(ctAutoResolutionInfo);
			}

			processedClassPKs += batchSize;
		}

		Indexer<?> indexer = _indexerRegistry.getIndexer(
			ctService.getModelClass());

		if (indexer != null) {
			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					List<String> uids = new ArrayList<>(ctEntries.size());

					for (CTEntry ctEntry : ctEntries) {
						if (ctEntry.getChangeType() !=
								CTConstants.CT_CHANGE_TYPE_DELETION) {

							uids.add(
								_uidFactory.getUID(
									indexer.getClassName(),
									ctEntry.getModelClassPK(),
									ctEntry.getCtCollectionId()));
						}
					}

					_indexWriterHelper.deleteDocuments(
						ctCollection.getCompanyId(), uids,
						indexer.isCommitImmediately());

					return null;
				});
		}
	}

	private Map<Long, List<CTEntry>> _getRelatedCTEntriesMap(
			CTCollection ctCollection, long modelClassNameId, long modelClassPK)
		throws PortalException {

		int count = _ctEntryPersistence.countByC_MCNI_MCPK(
			ctCollection.getCtCollectionId(), modelClassNameId, modelClassPK);

		if (count == 0) {
			throw new CTEnclosureException(
				StringBundler.concat(
					"Unable to find CTEntry for {classNameId=",
					modelClassNameId, ", classPK=", modelClassPK,
					", ctCollectionId=", ctCollection.getCtCollectionId(),
					"}"));
		}

		CTClosure ctClosure = _ctClosureFactory.create(
			ctCollection.getCtCollectionId(), modelClassNameId);

		Map<Long, Set<Long>> enclosureMap = CTEnclosureUtil.getEnclosureMap(
			ctClosure, modelClassNameId, modelClassPK);

		Map<Long, List<CTEntry>> relatedEntriesMap = new HashMap<>();

		for (Map.Entry<Long, Set<Long>> enclosureEntry :
				enclosureMap.entrySet()) {

			long classNameId = enclosureEntry.getKey();

			Set<Long> classPKs = enclosureEntry.getValue();

			List<CTEntry> ctEntries = new ArrayList<>(classPKs.size());

			for (long classPK : classPKs) {
				CTEntry ctEntry = _ctEntryPersistence.fetchByC_MCNI_MCPK(
					ctCollection.getCtCollectionId(), classNameId, classPK);

				if (ctEntry != null) {
					ctEntries.add(ctEntry);
				}
			}

			if (ctEntries.isEmpty()) {
				continue;
			}

			relatedEntriesMap.put(classNameId, ctEntries);
		}

		return relatedEntriesMap;
	}

	private int[] _getStatuses(
		long ctCollectionId, CTPersistence ctPersistence,
		Map.Entry<Long, CTPersistence<?>> entry) {

		CTCollection ctCollection = ctCollectionPersistence.fetchByPrimaryKey(
			ctCollectionId);

		long groupId = 0;

		if (entry instanceof GroupedModel) {
			GroupedModel groupedModel = (GroupedModel)entry;

			groupId = groupedModel.getGroupId();
		}

		Class<?> clazz = ctPersistence.getModelClass();

		if (!_workflowDefinitionLinkLocalService.hasWorkflowDefinitionLink(
				ctCollection.getCompanyId(), groupId, clazz.getName())) {

			return new int[] {
				WorkflowConstants.STATUS_APPROVED,
				WorkflowConstants.STATUS_DRAFT,
				WorkflowConstants.STATUS_EXPIRED,
				WorkflowConstants.STATUS_IN_TRASH,
				WorkflowConstants.STATUS_SCHEDULED
			};
		}

		return new int[] {
			WorkflowConstants.STATUS_APPROVED, WorkflowConstants.STATUS_EXPIRED,
			WorkflowConstants.STATUS_IN_TRASH,
			WorkflowConstants.STATUS_SCHEDULED
		};
	}

	private void _moveCTEntries(
		long companyId, long fromCTCollectionId, long toCTCollectionId,
		long classNameId, List<CTEntry> ctEntries) {

		if (ListUtil.isEmpty(ctEntries)) {
			return;
		}

		CTService<?> ctService = _ctServiceRegistry.getCTService(classNameId);

		ctService.updateWithUnsafeFunction(
			ctPersistence -> {
				Set<String> primaryKeyNames = ctPersistence.getCTColumnNames(
					CTColumnResolutionType.PK);

				if (primaryKeyNames.size() != 1) {
					throw new IllegalArgumentException(
						StringBundler.concat(
							"{primaryKeyNames=", primaryKeyNames,
							", tableName=", ctPersistence.getTableName(), "}"));
				}

				Iterator<String> iterator = primaryKeyNames.iterator();

				String primaryKeyName = iterator.next();

				_processMoveCTEntriesQuery(
					fromCTCollectionId, toCTCollectionId, ctEntries,
					ctPersistence, primaryKeyName);

				return null;
			});

		List<Long> modelClassPKs = new ArrayList<>(ctEntries.size());

		for (CTEntry ctEntry : ctEntries) {
			modelClassPKs.add(ctEntry.getModelClassPK());

			ctEntry.setCtCollectionId(toCTCollectionId);

			_ctEntryLocalService.updateCTEntry(ctEntry);
		}

		CTPersistence<?> ctPersistence = ctService.getCTPersistence();

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					fromCTCollectionId)) {

			ctPersistence.clearCache(new HashSet<>(modelClassPKs));
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					toCTCollectionId)) {

			ctPersistence.clearCache(new HashSet<>(modelClassPKs));
		}

		int processedClassPKs = 0;

		while (processedClassPKs < modelClassPKs.size()) {
			int batchSize = Math.min(
				modelClassPKs.size() - processedClassPKs, _BATCH_SIZE);

			for (CTAutoResolutionInfo ctAutoResolutionInfo :
					_ctAutoResolutionInfoPersistence.findByC_MCNI_SMCPK(
						fromCTCollectionId, classNameId,
						ArrayUtil.toLongArray(
							modelClassPKs.subList(
								processedClassPKs,
								processedClassPKs + batchSize)))) {

				ctAutoResolutionInfo.setCtCollectionId(toCTCollectionId);

				_ctAutoResolutionInfoPersistence.update(ctAutoResolutionInfo);
			}

			processedClassPKs += batchSize;
		}

		Indexer<?> indexer = _indexerRegistry.getIndexer(
			ctService.getModelClass());

		if (indexer != null) {
			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					List<String> uids = new ArrayList<>(ctEntries.size());

					for (CTEntry ctEntry : ctEntries) {
						if (ctEntry.getChangeType() !=
								CTConstants.CT_CHANGE_TYPE_DELETION) {

							uids.add(
								_uidFactory.getUID(
									indexer.getClassName(),
									ctEntry.getModelClassPK(),
									fromCTCollectionId));

							try (SafeCloseable safeCloseable =
									CTCollectionThreadLocal.
										setCTCollectionIdWithSafeCloseable(
											toCTCollectionId)) {

								indexer.reindex(
									indexer.getClassName(),
									ctEntry.getModelClassPK());
							}
						}
					}

					_indexWriterHelper.deleteDocuments(
						companyId, uids, indexer.isCommitImmediately());

					return null;
				});
		}
	}

	private void _processDiscardEntriesQuery(
		long ctCollectionId, List<CTEntry> ctEntries,
		CTPersistence<?> ctPersistence, String primaryKeyName) {

		StringBundler sb = new StringBundler(7);

		sb.append("delete from ");
		sb.append(ctPersistence.getTableName());
		sb.append(" where ctCollectionId = ");
		sb.append(ctCollectionId);
		sb.append(" and ");
		sb.append(primaryKeyName);
		sb.append(" = ?");

		Connection connection = _currentConnection.getConnection(
			ctPersistence.getDataSource());

		try (PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection, sb.toString())) {

			for (CTEntry ctEntry : ctEntries) {
				preparedStatement.setLong(1, ctEntry.getModelClassPK());

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}

		for (String mappingTableName : ctPersistence.getMappingTableNames()) {
			sb.setStringAt(mappingTableName, 1);

			try (PreparedStatement preparedStatement =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection, sb.toString())) {

				for (CTEntry ctEntry : ctEntries) {
					preparedStatement.setLong(1, ctEntry.getModelClassPK());

					preparedStatement.addBatch();
				}

				preparedStatement.executeBatch();
			}
			catch (Exception exception) {
				throw new SystemException(exception);
			}
		}
	}

	private void _processMoveCTEntriesQuery(
		long fromCTCollectionId, long toCTCollectionId, List<CTEntry> ctEntries,
		CTPersistence<?> ctPersistence, String primaryKeyName) {

		StringBundler sb = new StringBundler(9);

		sb.append("update ");
		sb.append(ctPersistence.getTableName());
		sb.append(" set ctCollectionId = ");
		sb.append(toCTCollectionId);
		sb.append(" where ctCollectionId = ");
		sb.append(fromCTCollectionId);
		sb.append(" and ");
		sb.append(primaryKeyName);
		sb.append(" = ?");

		Connection connection = _currentConnection.getConnection(
			ctPersistence.getDataSource());

		try (PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection, sb.toString())) {

			for (CTEntry ctEntry : ctEntries) {
				preparedStatement.setLong(1, ctEntry.getModelClassPK());

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}

		for (String mappingTableName : ctPersistence.getMappingTableNames()) {
			sb.setStringAt(mappingTableName, 1);

			try (PreparedStatement preparedStatement =
					AutoBatchPreparedStatementUtil.autoBatch(
						connection, sb.toString())) {

				for (CTEntry ctEntry : ctEntries) {
					preparedStatement.setLong(1, ctEntry.getModelClassPK());

					preparedStatement.addBatch();
				}

				preparedStatement.executeBatch();
			}
			catch (Exception exception) {
				throw new SystemException(exception);
			}
		}
	}

	private void _validate(String name, String description)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new CTCollectionNameException();
		}

		int nameMaxLength = ModelHintsUtil.getMaxLength(
			CTCollection.class.getName(), "name");

		if (name.length() > nameMaxLength) {
			throw new CTCollectionNameException("Name is too long");
		}

		int descriptionMaxLength = ModelHintsUtil.getMaxLength(
			CTCollection.class.getName(), "description");

		if ((description != null) &&
			(description.length() > descriptionMaxLength)) {

			throw new CTCollectionDescriptionException(
				"Description is too long");
		}
	}

	private static final int _BATCH_SIZE = 1000;

	private static final Log _log = LogFactoryUtil.getLog(
		CTCollectionLocalServiceImpl.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	private ServiceTrackerMap<ConstraintResolverKey, ConstraintResolver<?>>
		_constraintResolverServiceTrackerMap;

	@Reference
	private CTAutoResolutionInfoPersistence _ctAutoResolutionInfoPersistence;

	@Reference
	private CTClosureFactory _ctClosureFactory;

	@Reference
	private CTCommentPersistence _ctCommentPersistence;

	private ServiceTrackerMap<String, CTDisplayRenderer<?>>
		_ctDisplayRendererServiceTrackerMap;
	private ServiceTrackerMap<String, CTEntryConflictHelper>
		_ctEntryConflictHelperServiceTrackerMap;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private CTEntryPersistence _ctEntryPersistence;

	@Reference
	private CTMessagePersistence _ctMessagePersistence;

	@Reference
	private CTPreferencesLocalService _ctPreferencesLocalService;

	@Reference
	private CTPreferencesPersistence _ctPreferencesPersistence;

	@Reference
	private CTSchemaVersionLocalService _ctSchemaVersionLocalService;

	@Reference
	private CTScorePersistence _ctScorePersistence;

	@Reference
	private CTServiceRegistry _ctServiceRegistry;

	@Reference
	private CurrentConnection _currentConnection;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private IndexerRegistry _indexerRegistry;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private TableReferenceDefinitionManager _tableReferenceDefinitionManager;

	@Reference
	private UIDFactory _uidFactory;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}