/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.notification;

import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.workflow.kaleo.definition.NotificationReceptionType;
import com.liferay.portal.workflow.kaleo.definition.RecipientType;
import com.liferay.portal.workflow.kaleo.model.KaleoNotificationRecipient;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Victor Kammerer
 */
public class BaseNotificationSenderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetNotificationRecipientsMap() throws Exception {
		ExecutionContext executionContext = Mockito.mock(
			ExecutionContext.class);

		BaseNotificationSender baseNotificationSender = Mockito.spy(
			BaseNotificationSender.class);

		List<KaleoNotificationRecipient> kaleoNotificationRecipients =
			new ArrayList<>();

		KaleoNotificationRecipient kaleoNotificationRecipient = Mockito.mock(
			KaleoNotificationRecipient.class);

		Mockito.when(
			kaleoNotificationRecipient.getNotificationReceptionType()
		).thenReturn(
			String.valueOf(NotificationReceptionType.TO)
		);

		Mockito.when(
			kaleoNotificationRecipient.getRecipientClassName()
		).thenReturn(
			String.valueOf(RecipientType.SCRIPT)
		);

		kaleoNotificationRecipients.add(kaleoNotificationRecipient);

		Map<NotificationReceptionType, Set<NotificationRecipient>>
			notificationRecipientsMap =
				baseNotificationSender.getNotificationRecipientsMap(
					kaleoNotificationRecipients, executionContext);

		Assert.assertTrue(notificationRecipientsMap.isEmpty());
	}

}