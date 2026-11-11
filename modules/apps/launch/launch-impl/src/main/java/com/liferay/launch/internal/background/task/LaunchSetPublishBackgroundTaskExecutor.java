/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.background.task;

import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.launch.entry.manager.LaunchEntryManagerRegistry;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BaseBackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jose Luis Navarro
 */
@Component(
	property = "background.task.executor.class.name=com.liferay.launch.internal.background.task.LaunchSetPublishBackgroundTaskExecutor",
	service = BackgroundTaskExecutor.class
)
public class LaunchSetPublishBackgroundTaskExecutor
	extends BaseBackgroundTaskExecutor {

	public LaunchSetPublishBackgroundTaskExecutor() {
		setIsolationLevel(BackgroundTaskConstants.ISOLATION_LEVEL_COMPANY);
	}

	@Override
	public BackgroundTaskExecutor clone() {
		return this;
	}

	@Override
	public BackgroundTaskResult execute(BackgroundTask backgroundTask)
		throws Exception {

		long launchSetId = MapUtil.getLong(
			backgroundTask.getTaskContextMap(), "launchSetId");

		ObjectEntry launchSetObjectEntry = null;

		try {
			launchSetObjectEntry = _objectEntryLocalService.getObjectEntry(
				launchSetId);
		}
		catch (PortalException portalException) {
			String message = StringBundler.concat(
				"Unable to publish launch set ", launchSetId, ": ",
				portalException.getMessage());

			_log.error(message, portalException);

			return new BackgroundTaskResult(
				BackgroundTaskConstants.STATUS_FAILED, message);
		}

		if (launchSetObjectEntry.getStatus() ==
				WorkflowConstants.STATUS_APPROVED) {

			String message =
				"Launch set " + launchSetId + " was already published";

			_log.error(message);

			return new BackgroundTaskResult(
				BackgroundTaskConstants.STATUS_FAILED, message);
		}

		if (MapUtil.getBoolean(
				backgroundTask.getTaskContextMap(), "scheduled") &&
			(launchSetObjectEntry.getStatus() !=
				WorkflowConstants.STATUS_SCHEDULED)) {

			String message =
				"Launch set " + launchSetId + " is no longer scheduled";

			_log.error(message);

			return new BackgroundTaskResult(
				BackgroundTaskConstants.STATUS_FAILED, message);
		}

		long userId = backgroundTask.getUserId();

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(launchSetObjectEntry.getCompanyId());
		serviceContext.setUserId(userId);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			Group group = _groupLocalService.getGroup(
				launchSetObjectEntry.getCompanyId(), GroupConstants.GUEST);

			serviceContext.setScopeGroupId(group.getGroupId());

			PermissionThreadLocal.setPermissionChecker(
				_permissionCheckerFactory.create(
					_userLocalService.getUser(userId)));
			PrincipalThreadLocal.setName(userId);

			TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					_publish(
						_objectEntryLocalService.getObjectEntry(launchSetId),
						serviceContext);

					return null;
				});
		}
		catch (Throwable throwable) {
			_log.error(
				"Unable to publish launch set " +
					launchSetObjectEntry.getObjectEntryId(),
				throwable);

			long statusByUserId = userId;

			if (_userLocalService.fetchUser(userId) == null) {
				statusByUserId = _userLocalService.getGuestUserId(
					launchSetObjectEntry.getCompanyId());
			}

			_objectEntryLocalService.updateStatus(
				statusByUserId, launchSetObjectEntry.getObjectEntryId(),
				WorkflowConstants.STATUS_DRAFT, serviceContext);

			return new BackgroundTaskResult(
				BackgroundTaskConstants.STATUS_FAILED, throwable.getMessage());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		return BackgroundTaskResult.SUCCESS;
	}

	@Override
	public BackgroundTaskDisplay getBackgroundTaskDisplay(
		BackgroundTask backgroundTask) {

		return null;
	}

	private void _publish(
			ObjectEntry launchSetObjectEntry, ServiceContext serviceContext)
		throws PortalException {

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				launchSetObjectEntry.getObjectDefinitionId(),
				"launchSetToLaunchEntries");

		for (ObjectEntry launchEntryObjectEntry :
				_objectEntryLocalService.getOneToManyObjectEntries(
					launchSetObjectEntry.getGroupId(),
					objectRelationship.getObjectRelationshipId(), null, false,
					launchSetObjectEntry.getObjectEntryId(), true, null,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			Map<String, Serializable> values =
				launchEntryObjectEntry.getValues();

			String className = MapUtil.getString(values, "className");

			LaunchEntryManager launchEntryManager =
				_launchEntryManagerRegistry.getLaunchEntryManager(className);

			if (launchEntryManager == null) {
				throw new PortalException(
					"No launch entry manager exists for " + className);
			}

			LaunchEntryManager.Version version =
				launchEntryManager.publishDraft(
					MapUtil.getLong(values, "classPK"),
					MapUtil.getLong(values, "draftId"));

			values.put("draftId", 0L);
			values.put("versionKey", version.getVersionKey());

			_objectEntryLocalService.updateObjectEntry(
				serviceContext.getUserId(),
				launchEntryObjectEntry.getObjectEntryId(),
				launchEntryObjectEntry.getObjectEntryFolderId(), values,
				serviceContext);
		}

		_objectEntryLocalService.updateStatus(
			serviceContext.getUserId(), launchSetObjectEntry.getObjectEntryId(),
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LaunchSetPublishBackgroundTaskExecutor.class);

	private static final TransactionConfig _transactionConfig;

	static {
		TransactionConfig.Builder builder = new TransactionConfig.Builder();

		builder.setPropagation(Propagation.REQUIRED);
		builder.setRollbackForClasses(Exception.class);

		_transactionConfig = builder.build();
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LaunchEntryManagerRegistry _launchEntryManagerRegistry;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private UserLocalService _userLocalService;

}