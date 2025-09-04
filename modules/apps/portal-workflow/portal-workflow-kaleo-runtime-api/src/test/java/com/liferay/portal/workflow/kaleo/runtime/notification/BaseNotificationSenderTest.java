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
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Victor Kammerer
 */
public class BaseNotificationSenderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetNotificationRecipientsMap() throws Exception {
		List<KaleoNotificationRecipient> kaleoNotificationRecipients =
			new ArrayList<>();

		Mockito.when(
			_kaleoNotificationRecipient.getNotificationReceptionType()
		).thenReturn(
			String.valueOf(RecipientType.SCRIPT)
		);

		Mockito.when(
			_kaleoNotificationRecipient.getRecipientClassName()
		).thenReturn(
			String.valueOf(RecipientType.SCRIPT)
		);

		kaleoNotificationRecipients.add(_kaleoNotificationRecipient);

		ExecutionContext executionContext = Mockito.mock(
			ExecutionContext.class);

		Map<NotificationReceptionType, Set<NotificationRecipient>>
			notificationRecipientsMap =
			_baseNotificationSender.getNotificationRecipientsMap(
				kaleoNotificationRecipients, executionContext);

		Assert.assertTrue(notificationRecipientsMap.isEmpty());
	}

	private final BaseNotificationSender _baseNotificationSender = Mockito.spy(
		BaseNotificationSender.class);

	@Mock
	private KaleoNotificationRecipient _kaleoNotificationRecipient;

}