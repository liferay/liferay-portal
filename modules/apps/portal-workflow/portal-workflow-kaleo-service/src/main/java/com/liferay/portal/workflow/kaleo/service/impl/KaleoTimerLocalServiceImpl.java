/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.workflow.kaleo.definition.Action;
import com.liferay.portal.workflow.kaleo.definition.Assignment;
import com.liferay.portal.workflow.kaleo.definition.DelayDuration;
import com.liferay.portal.workflow.kaleo.definition.DurationScale;
import com.liferay.portal.workflow.kaleo.definition.Notification;
import com.liferay.portal.workflow.kaleo.definition.Timer;
import com.liferay.portal.workflow.kaleo.model.KaleoTimer;
import com.liferay.portal.workflow.kaleo.service.KaleoActionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoNotificationLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskAssignmentLocalService;
import com.liferay.portal.workflow.kaleo.service.base.KaleoTimerLocalServiceBaseImpl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "model.class.name=com.liferay.portal.workflow.kaleo.model.KaleoTimer",
	service = AopService.class
)
public class KaleoTimerLocalServiceImpl extends KaleoTimerLocalServiceBaseImpl {

	@Override
	public KaleoTimer addKaleoTimer(
			String kaleoClassName, long kaleoClassPK, long kaleoDefinitionId,
			long kaleoDefinitionVersionId, Timer timer,
			ServiceContext serviceContext)
		throws PortalException {

		// Kaleo timer

		User user = _userLocalService.getUser(
			serviceContext.getGuestOrUserId());
		Date date = new Date();

		long kaleoTimerId = counterLocalService.increment();

		KaleoTimer kaleoTimer = kaleoTimerPersistence.create(kaleoTimerId);

		kaleoTimer.setCompanyId(user.getCompanyId());
		kaleoTimer.setUserId(user.getUserId());
		kaleoTimer.setUserName(user.getFullName());
		kaleoTimer.setCreateDate(date);
		kaleoTimer.setModifiedDate(date);
		kaleoTimer.setKaleoClassName(kaleoClassName);
		kaleoTimer.setKaleoClassPK(kaleoClassPK);
		kaleoTimer.setKaleoDefinitionId(kaleoDefinitionId);
		kaleoTimer.setKaleoDefinitionVersionId(kaleoDefinitionVersionId);
		kaleoTimer.setName(timer.getName());
		kaleoTimer.setBlocking(timer.isBlocking());

		DelayDuration delayDuration = timer.getDelayDuration();

		kaleoTimer.setDuration(delayDuration.getDuration());

		DurationScale durationScale = delayDuration.getDurationScale();

		kaleoTimer.setScale(durationScale.getValue());

		DelayDuration recurrenceDelayDuration = timer.getRecurrence();

		if (recurrenceDelayDuration != null) {
			kaleoTimer.setRecurrenceDuration(
				recurrenceDelayDuration.getDuration());

			durationScale = recurrenceDelayDuration.getDurationScale();

			kaleoTimer.setRecurrenceScale(durationScale.getValue());
		}

		kaleoTimer = kaleoTimerPersistence.update(kaleoTimer);

		// Kaleo actions

		Set<Action> actions = timer.getActions();

		for (Action action : actions) {
			_kaleoActionLocalService.addKaleoAction(
				KaleoTimer.class.getName(), kaleoTimerId, kaleoDefinitionId,
				kaleoDefinitionVersionId, timer.getName(), action,
				serviceContext);
		}

		// Kaleo assignments

		Set<Assignment> reassignments = timer.getReassignments();

		for (Assignment reassignment : reassignments) {
			_kaleoTaskAssignmentLocalService.addKaleoTaskAssignment(
				KaleoTimer.class.getName(), kaleoTimerId, kaleoDefinitionId,
				kaleoDefinitionVersionId, reassignment, serviceContext);
		}

		// Kaleo notifications

		Set<Notification> notifications = timer.getNotifications();

		for (Notification notification : notifications) {
			_kaleoNotificationLocalService.addKaleoNotification(
				KaleoTimer.class.getName(), kaleoTimerId, kaleoDefinitionId,
				kaleoDefinitionVersionId, timer.getName(), notification,
				serviceContext);
		}

		return kaleoTimer;
	}

	@Override
	public List<KaleoTimer> getKaleoDefinitionVersionKaleoTimers(
		String kaleoClassName, long kaleoDefinitionVersionId) {

		return kaleoTimerPersistence.findByKCN_KDVI(
			kaleoClassName, kaleoDefinitionVersionId);
	}

	@Override
	public List<KaleoTimer> getKaleoTimers(
		String kaleoClassName, long kaleoClassPK) {

		return kaleoTimerPersistence.findByKCN_KCPK(
			kaleoClassName, kaleoClassPK);
	}

	@Reference
	private KaleoActionLocalService _kaleoActionLocalService;

	@Reference
	private KaleoNotificationLocalService _kaleoNotificationLocalService;

	@Reference
	private KaleoTaskAssignmentLocalService _kaleoTaskAssignmentLocalService;

	@Reference
	private UserLocalService _userLocalService;

}