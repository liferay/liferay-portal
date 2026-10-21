/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.scheduler;

import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.constants.NotificationQueueEntryConstants;
import com.liferay.notification.internal.configuration.NotificationQueueConfiguration;
import com.liferay.notification.service.NotificationQueueEntryLocalService;
import com.liferay.notification.type.NotificationType;
import com.liferay.notification.type.NotificationTypeServiceTracker;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.util.Time;

import java.util.Date;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gustavo Lima
 */
@Component(
	configurationPid = "com.liferay.notification.internal.configuration.NotificationQueueConfiguration",
	service = SchedulerJobConfiguration.class
)
public class CheckNotificationQueueEntrySchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> {
			NotificationType notificationType =
				_notificationTypeServiceTracker.getNotificationType(
					NotificationConstants.TYPE_EMAIL);

			if (notificationType != null) {
				notificationType.resendNotifications(
					NotificationQueueEntryConstants.STATUS_FAILED,
					NotificationConstants.TYPE_EMAIL);
				notificationType.resendNotifications(
					NotificationQueueEntryConstants.STATUS_UNSENT,
					NotificationConstants.TYPE_EMAIL);
			}

			long deleteInterval =
				_notificationQueueConfiguration.deleteInterval() * Time.MINUTE;

			_notificationQueueEntryLocalService.deleteNotificationQueueEntries(
				new Date(System.currentTimeMillis() - deleteInterval));
		};
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return TriggerConfiguration.createTriggerConfiguration(
			15, TimeUnit.MINUTE);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_notificationQueueConfiguration = ConfigurableUtil.createConfigurable(
			NotificationQueueConfiguration.class, properties);
	}

	private volatile NotificationQueueConfiguration
		_notificationQueueConfiguration;

	@Reference
	private NotificationQueueEntryLocalService
		_notificationQueueEntryLocalService;

	@Reference
	private NotificationTypeServiceTracker _notificationTypeServiceTracker;

}