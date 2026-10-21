/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.scheduler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.constants.NotificationQueueEntryConstants;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.service.NotificationQueueEntryLocalService;
import com.liferay.notification.test.util.NotificationTemplateUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class CheckNotificationQueueEntrySchedulerJobConfigurationTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetJobExecutorUnsafeRunnable() throws Exception {
		NotificationQueueEntry notificationQueueEntry =
			_notificationQueueEntryLocalService.addNotificationQueueEntry(
				NotificationTemplateUtil.createNotificationContext(
					NotificationConstants.TYPE_EMAIL));

		notificationQueueEntry.setSentDate(
			new Date(System.currentTimeMillis() - Time.HOUR));
		notificationQueueEntry.setStatus(
			NotificationQueueEntryConstants.STATUS_SENT);

		notificationQueueEntry =
			_notificationQueueEntryLocalService.updateNotificationQueueEntry(
				notificationQueueEntry);

		UnsafeRunnable<Exception> unsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

		unsafeRunnable.run();

		Assert.assertNotNull(
			_notificationQueueEntryLocalService.fetchNotificationQueueEntry(
				notificationQueueEntry.getNotificationQueueEntryId()));

		try {
			ConfigurationTestUtil.saveConfiguration(
				_PID,
				HashMapDictionaryBuilder.<String, Object>put(
					"deleteInterval", 1
				).build());

			unsafeRunnable.run();
		}
		finally {
			ConfigurationTestUtil.deleteConfiguration(_PID);
		}

		Assert.assertNull(
			_notificationQueueEntryLocalService.fetchNotificationQueueEntry(
				notificationQueueEntry.getNotificationQueueEntryId()));
	}

	private static final String _PID =
		"com.liferay.notification.internal.configuration." +
			"NotificationQueueConfiguration";

	@Inject
	private NotificationQueueEntryLocalService
		_notificationQueueEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.notification.internal.scheduler.CheckNotificationQueueEntrySchedulerJobConfiguration"
	)
	private SchedulerJobConfiguration _schedulerJobConfiguration;

}