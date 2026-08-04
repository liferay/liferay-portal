/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.scheduler;

import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.constants.CTDestinationNames;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.scheduler.PublishScheduler;
import com.liferay.change.tracking.scheduler.ScheduledPublishInfo;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTPreferencesLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(service = PublishScheduler.class)
public class PublishSchedulerImpl implements PublishScheduler {

	@Override
	public ScheduledPublishInfo getScheduledPublishInfo(
			CTCollection ctCollection)
		throws PortalException {

		SchedulerResponse schedulerResponse =
			_schedulerEngineHelper.getScheduledJob(
				_getSchedulerJobName(ctCollection),
				CTDestinationNames.CT_COLLECTION_SCHEDULED_PUBLISH,
				StorageType.PERSISTED);

		if (schedulerResponse == null) {
			return null;
		}

		Message message = schedulerResponse.getMessage();

		return new ScheduledPublishInfo(
			ctCollection, schedulerResponse.getJobName(),
			_schedulerEngineHelper.getStartDate(schedulerResponse),
			message.getLong("userId"));
	}

	public List<ScheduledPublishInfo> getScheduledPublishInfos()
		throws PortalException {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		return TransformUtil.transform(
			_schedulerEngineHelper.getScheduledJobs(
				CTDestinationNames.CT_COLLECTION_SCHEDULED_PUBLISH,
				StorageType.PERSISTED),
			schedulerResponse -> {
				Message message = schedulerResponse.getMessage();

				long ctCollectionId = message.getLong("ctCollectionId");

				CTCollection ctCollection =
					_ctCollectionLocalService.fetchCTCollection(ctCollectionId);

				if ((ctCollection == null) ||
					!_ctCollectionModelResourcePermission.contains(
						permissionChecker, ctCollection, ActionKeys.VIEW)) {

					return null;
				}

				return new ScheduledPublishInfo(
					ctCollection, schedulerResponse.getJobName(),
					_schedulerEngineHelper.getStartDate(schedulerResponse),
					message.getLong("userId"));
			});
	}

	@Override
	public void schedulePublish(
			long ctCollectionId, long userId, Date startDate)
		throws PortalException {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> _schedulePublish(ctCollectionId, userId, startDate));
		}
		catch (PortalException portalException) {
			throw portalException;
		}
		catch (Throwable throwable) {
			ReflectionUtil.throwException(throwable);
		}
	}

	@Override
	public void unschedulePublish(long ctCollectionId) throws Exception {
		CTCollection ctCollection = _ctCollectionLocalService.fetchCTCollection(
			ctCollectionId);

		if ((ctCollection == null) ||
			(ctCollection.getStatus() == WorkflowConstants.STATUS_APPROVED)) {

			return;
		}

		String jobName = _getSchedulerJobName(ctCollection);

		SchedulerResponse schedulerResponse =
			_schedulerEngineHelper.getScheduledJob(
				jobName, CTDestinationNames.CT_COLLECTION_SCHEDULED_PUBLISH,
				StorageType.PERSISTED);

		if (schedulerResponse == null) {
			return;
		}

		_ctCollectionModelResourcePermission.check(
			PermissionThreadLocal.getPermissionChecker(), ctCollection,
			CTActionKeys.PUBLISH);

		if (_ctCollectionLocalService.hasUnapprovedChanges(ctCollectionId)) {
			ctCollection.setStatus(WorkflowConstants.STATUS_INCOMPLETE);
		}
		else {
			ctCollection.setStatus(WorkflowConstants.STATUS_DRAFT);
		}

		ctCollection.setScheduledDate(null);
		ctCollection.setStatusByUserId(UserConstants.USER_ID_DEFAULT);

		_ctCollectionLocalService.updateCTCollection(ctCollection);

		_schedulerEngineHelper.delete(
			jobName, CTDestinationNames.CT_COLLECTION_SCHEDULED_PUBLISH,
			StorageType.PERSISTED);
	}

	private String _getSchedulerJobName(CTCollection ctCollection) {
		return StringBundler.concat(
			ctCollection.getCtCollectionId(), StringPool.AT,
			ctCollection.getCompanyId());
	}

	private Void _schedulePublish(
			long ctCollectionId, long userId, Date startDate)
		throws PortalException {

		CTCollection ctCollection = _ctCollectionLocalService.getCTCollection(
			ctCollectionId);

		SchedulerResponse schedulerResponse =
			_schedulerEngineHelper.getScheduledJob(
				_getSchedulerJobName(ctCollection),
				CTDestinationNames.CT_COLLECTION_SCHEDULED_PUBLISH,
				StorageType.PERSISTED);

		if (schedulerResponse != null) {
			return null;
		}

		_ctCollectionModelResourcePermission.check(
			PermissionThreadLocal.getPermissionChecker(), ctCollection,
			CTActionKeys.PUBLISH);

		ctCollection.setScheduledDate(startDate);
		ctCollection.setStatus(WorkflowConstants.STATUS_SCHEDULED);
		ctCollection.setStatusByUserId(userId);

		ctCollection = _ctCollectionLocalService.updateCTCollection(
			ctCollection);

		_ctPreferencesLocalService.resetCTPreferences(ctCollectionId);

		Message message = new Message();

		message.put("companyId", ctCollection.getCompanyId());
		message.put("ctCollectionId", ctCollectionId);
		message.put("userId", userId);

		_schedulerEngineHelper.schedule(
			_triggerFactory.createTrigger(
				_getSchedulerJobName(ctCollection),
				CTDestinationNames.CT_COLLECTION_SCHEDULED_PUBLISH, startDate,
				null, 0, null),
			StorageType.PERSISTED, String.valueOf(ctCollectionId),
			CTDestinationNames.CT_COLLECTION_SCHEDULED_PUBLISH, message);

		return null;
	}

	private static final TransactionConfig _transactionConfig;

	static {
		TransactionConfig.Builder builder = new TransactionConfig.Builder();

		builder.setPropagation(Propagation.REQUIRES_NEW);
		builder.setRollbackForClasses(Exception.class);

		_transactionConfig = builder.build();
	}

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.change.tracking.model.CTCollection)"
	)
	private ModelResourcePermission<CTCollection>
		_ctCollectionModelResourcePermission;

	@Reference
	private CTPreferencesLocalService _ctPreferencesLocalService;

	@Reference
	private SchedulerEngineHelper _schedulerEngineHelper;

	@Reference
	private TriggerFactory _triggerFactory;

}