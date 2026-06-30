/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.background.task;

import com.liferay.change.tracking.conflict.ConflictInfo;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.constants.PublicationRoleConstants;
import com.liferay.change.tracking.exception.CTPublishConflictException;
import com.liferay.change.tracking.internal.CTServiceRegistry;
import com.liferay.change.tracking.internal.background.task.display.CTPublishBackgroundTaskDisplay;
import com.liferay.change.tracking.internal.configuration.CTEntityCacheConfiguration;
import com.liferay.change.tracking.internal.helper.CTTableMapperHelper;
import com.liferay.change.tracking.internal.notification.CTUserNotificationSender;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.service.CTSchemaVersionLocalService;
import com.liferay.change.tracking.service.persistence.CTEntryPersistence;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatus;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusRegistry;
import com.liferay.portal.kernel.backgroundtask.BaseBackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.SkipReplicationThreadLocal;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Zoltan Csaszi
 * @author Daniel Kocsis
 */
@Component(
	configurationPid = "com.liferay.change.tracking.internal.configuration.CTEntityCacheConfiguration",
	property = "background.task.executor.class.name=com.liferay.change.tracking.internal.background.task.CTPublishBackgroundTaskExecutor",
	service = AopService.class
)
public class CTPublishBackgroundTaskExecutor
	extends BaseBackgroundTaskExecutor implements AopService {

	public CTPublishBackgroundTaskExecutor() {
		setIsolationLevel(BackgroundTaskConstants.ISOLATION_LEVEL_COMPANY);
	}

	@Override
	public BackgroundTaskExecutor clone() {
		return _backgroundTaskExecutor;
	}

	@Override
	@Transactional(
		propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class
	)
	public BackgroundTaskResult execute(BackgroundTask backgroundTask)
		throws Exception {

		Map<String, Serializable> taskContextMap =
			backgroundTask.getTaskContextMap();

		long ctCollectionId = GetterUtil.getLong(
			taskContextMap.get("ctCollectionId"));

		CTCollection ctCollection = _ctCollectionLocalService.getCTCollection(
			ctCollectionId);

		if (!_ctSchemaVersionLocalService.isLatestCTSchemaVersion(
				ctCollection.getSchemaVersionId())) {

			throw new IllegalArgumentException(
				StringBundler.concat(
					"Unable to publish ", ctCollection.getName(),
					" because it is out of date with the current release"));
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			_ctServiceRegistry.onBeforePublish(ctCollectionId);
		}

		Map<Long, List<ConflictInfo>> conflictInfosMap =
			_ctCollectionLocalService.checkConflicts(ctCollection);

		if (!conflictInfosMap.isEmpty()) {
			List<ConflictInfo> unresolvedConflictInfos = new ArrayList<>();

			for (Map.Entry<Long, List<ConflictInfo>> entry :
					conflictInfosMap.entrySet()) {

				unresolvedConflictInfos.addAll(
					TransformUtil.transform(
						entry.getValue(),
						conflictInfo -> {
							if (!conflictInfo.isResolved()) {
								return conflictInfo;
							}

							return null;
						}));
			}

			if (!unresolvedConflictInfos.isEmpty()) {
				throw new CTPublishConflictException(
					StringBundler.concat(
						"Unable to publish ", ctCollection.getName(),
						" because of unresolved conflicts: ",
						unresolvedConflictInfos));
			}
		}

		Map<Long, CTServicePublisher<?>> ctServicePublishers = new HashMap<>();

		List<CTEntry> ctEntries = _ctEntryLocalService.getCTCollectionCTEntries(
			ctCollectionId);

		for (CTEntry ctEntry : ctEntries) {
			CTServicePublisher<?> ctServicePublisher =
				ctServicePublishers.computeIfAbsent(
					ctEntry.getModelClassNameId(),
					modelClassNameId -> {
						CTService<?> ctService =
							_ctServiceRegistry.getCTService(modelClassNameId);

						if (ctService != null) {
							return new CTServicePublisher<>(
								_ctEntryLocalService, ctService,
								modelClassNameId, ctCollectionId,
								CTConstants.CT_COLLECTION_ID_PRODUCTION);
						}

						throw new SystemException(
							StringBundler.concat(
								"Unable to publish ", ctCollection.getName(),
								" because service for ", modelClassNameId,
								" is missing"));
					});

			ctServicePublisher.addCTEntry(ctEntry);
		}

		BackgroundTaskStatus backgroundTaskStatus =
			_backgroundTaskStatusRegistry.getBackgroundTaskStatus(
				backgroundTask.getBackgroundTaskId());

		boolean skipReplication = false;

		if (ClusterExecutorUtil.isEnabled() && (_entityCacheThreshold > 0) &&
			(ctEntries.size() > _entityCacheThreshold)) {

			skipReplication = true;
		}

		try (SafeCloseable safeCloseable =
				SkipReplicationThreadLocal.setEnabledWithSafeCloseable(
					skipReplication)) {

			int i = 0;

			for (CTServicePublisher<?> ctServicePublisher :
					ctServicePublishers.values()) {

				ctServicePublisher.publish();

				backgroundTaskStatus.setAttribute(
					"percentage", ++i / ctServicePublishers.size());
			}
		}

		if (skipReplication) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Clearing the entity cache because ", ctEntries.size(),
						" change tracking entries exceed the threshold of ",
						_entityCacheThreshold));
			}

			_ctEntryPersistence.clearCache();

			for (long modelClassNameId : ctServicePublishers.keySet()) {
				CTService<?> ctService = _ctServiceRegistry.getCTService(
					modelClassNameId);

				CTPersistence<?> ctPersistence = ctService.getCTPersistence();

				ctPersistence.clearCache();
			}
		}

		for (CTTableMapperHelper ctTableMapperHelper :
				_ctServiceRegistry.getCTTableMapperHelpers()) {

			ctTableMapperHelper.publish(
				ctCollectionId, _multiVMPool.getPortalCacheManager());
		}

		CTCollection latestCTCollection =
			_ctCollectionLocalService.getCTCollection(ctCollectionId);

		Date modifiedDate = new Date();

		latestCTCollection.setModifiedDate(modifiedDate);

		latestCTCollection.setStatus(WorkflowConstants.STATUS_APPROVED);
		latestCTCollection.setStatusByUserId(backgroundTask.getUserId());
		latestCTCollection.setStatusDate(modifiedDate);

		_ctCollectionLocalService.updateCTCollection(latestCTCollection);

		_ctServiceRegistry.onAfterPublish(ctCollectionId);

		return BackgroundTaskResult.SUCCESS;
	}

	@Override
	public Class<?>[] getAopInterfaces() {
		return new Class<?>[] {BackgroundTaskExecutor.class};
	}

	@Override
	public BackgroundTaskDisplay getBackgroundTaskDisplay(
		BackgroundTask backgroundTask) {

		return new CTPublishBackgroundTaskDisplay(backgroundTask);
	}

	@Override
	public String handleException(
		BackgroundTask backgroundTask, Exception exception) {

		boolean showConflicts = false;

		if (exception instanceof CTPublishConflictException) {
			showConflicts = true;
		}

		long ctCollectionId = MapUtil.getLong(
			backgroundTask.getTaskContextMap(), "ctCollectionId");

		try {
			CTCollection ctCollection =
				_ctCollectionLocalService.getCTCollection(ctCollectionId);

			_ctUserNotificationSender.sendUserNotificationEvents(
				ctCollection,
				JSONUtil.put(
					"backgroundTaskId", backgroundTask.getBackgroundTaskId()
				).put(
					"ctCollectionId", ctCollectionId
				).put(
					"ctCollectionName", HtmlUtil.escape(ctCollection.getName())
				).put(
					"notificationType",
					UserNotificationDefinition.NOTIFICATION_TYPE_REVIEW_ENTRY
				).put(
					"showConflicts", showConflicts
				),
				_getPublicationRolesUserIds(ctCollection, showConflicts));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return super.handleException(backgroundTask, exception);
	}

	@Override
	public void setAopProxy(Object aopProxy) {
		_backgroundTaskExecutor = (BackgroundTaskExecutor)aopProxy;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		CTEntityCacheConfiguration ctEntityCacheConfiguration =
			ConfigurableUtil.createConfigurable(
				CTEntityCacheConfiguration.class, properties);

		_entityCacheThreshold =
			ctEntityCacheConfiguration.entityCacheThreshold();
	}

	private long[] _getPublicationRolesUserIds(
		CTCollection ctCollection, boolean showConflicts) {

		Set<Long> userIds = SetUtil.fromArray(
			_ctUserNotificationSender.getPublicationRoleUserIds(
				ctCollection, true, PublicationRoleConstants.NAME_ADMIN,
				PublicationRoleConstants.NAME_EDITOR,
				PublicationRoleConstants.NAME_PUBLISHER));

		if (!showConflicts) {
			Role role = _roleLocalService.fetchRole(
				ctCollection.getCompanyId(), RoleConstants.ADMINISTRATOR);

			for (long userId :
					_userLocalService.getRoleUserIds(role.getRoleId())) {

				userIds.add(userId);
			}
		}

		return ArrayUtil.toLongArray(userIds);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CTPublishBackgroundTaskExecutor.class);

	private BackgroundTaskExecutor _backgroundTaskExecutor;

	@Reference
	private BackgroundTaskStatusRegistry _backgroundTaskStatusRegistry;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private CTEntryPersistence _ctEntryPersistence;

	@Reference
	private CTSchemaVersionLocalService _ctSchemaVersionLocalService;

	@Reference
	private CTServiceRegistry _ctServiceRegistry;

	@Reference
	private CTUserNotificationSender _ctUserNotificationSender;

	private volatile int _entityCacheThreshold;

	@Reference
	private MultiVMPool _multiVMPool;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}