/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.background.task;

import com.liferay.launch.set.publisher.LaunchSetPublisher;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BaseBackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jose Luis Navarro
 */
@Component(
	property = "background.task.executor.class.name=com.liferay.launch.internal.background.task.LaunchSetScheduledPublishBackgroundTaskExecutor",
	service = BackgroundTaskExecutor.class
)
public class LaunchSetScheduledPublishBackgroundTaskExecutor
	extends BaseBackgroundTaskExecutor {

	public LaunchSetScheduledPublishBackgroundTaskExecutor() {
		setIsolationLevel(BackgroundTaskConstants.ISOLATION_LEVEL_COMPANY);
	}

	@Override
	public BackgroundTaskExecutor clone() {
		return this;
	}

	@Override
	public BackgroundTaskResult execute(BackgroundTask backgroundTask)
		throws Exception {

		ObjectEntry launchSetObjectEntry =
			_objectEntryLocalService.fetchObjectEntry(
				MapUtil.getLong(
					backgroundTask.getTaskContextMap(), "launchSetId"));

		if ((launchSetObjectEntry == null) ||
			(launchSetObjectEntry.getStatus() !=
				WorkflowConstants.STATUS_SCHEDULED)) {

			return BackgroundTaskResult.SUCCESS;
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
					_launchSetPublisher.publish(
						launchSetObjectEntry.getObjectEntryId());

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

	private static final Log _log = LogFactoryUtil.getLog(
		LaunchSetScheduledPublishBackgroundTaskExecutor.class);

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
	private LaunchSetPublisher _launchSetPublisher;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private UserLocalService _userLocalService;

}