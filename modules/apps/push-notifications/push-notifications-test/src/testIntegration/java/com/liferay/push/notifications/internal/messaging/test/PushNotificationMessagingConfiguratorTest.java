/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.push.notifications.internal.messaging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerRegistry;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.push.notifications.constants.PushNotificationsDestinationNames;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class PushNotificationMessagingConfiguratorTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testActivate() {
		_assertDestination(PushNotificationsDestinationNames.PUSH_NOTIFICATION);
		_assertDestination(
			PushNotificationsDestinationNames.PUSH_NOTIFICATION_RESPONSE);
	}

	private void _assertDestination(String destinationName) {
		Assert.assertNotNull(_messageBus.getDestination(destinationName));

		List<MessageListener> messageListeners =
			_messageListenerRegistry.getMessageListeners(destinationName);

		Assert.assertFalse(messageListeners.isEmpty());
	}

	@Inject
	private MessageBus _messageBus;

	@Inject
	private MessageListenerRegistry _messageListenerRegistry;

}