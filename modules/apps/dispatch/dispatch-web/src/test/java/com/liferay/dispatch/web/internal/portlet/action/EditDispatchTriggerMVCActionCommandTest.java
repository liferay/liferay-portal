/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.web.internal.portlet.action;

import com.liferay.dispatch.constants.DispatchConstants;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Magdalena Jedraszak
 */
public class EditDispatchTriggerMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_editDispatchTriggerMVCActionCommand,
			"_dispatchTriggerLocalService", _dispatchTriggerLocalService);
		ReflectionTestUtil.setFieldValue(
			_editDispatchTriggerMVCActionCommand, "_messageBus", _messageBus);
	}

	@Test
	public void testSendMessagePropagatesCompanyId() throws Exception {
		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			_dispatchTrigger.getCompanyId()
		).thenReturn(
			companyId
		);

		long dispatchTriggerId = RandomTestUtil.randomLong();

		Mockito.when(
			_dispatchTriggerLocalService.getDispatchTrigger(dispatchTriggerId)
		).thenReturn(
			_dispatchTrigger
		);

		ReflectionTestUtil.invoke(
			_editDispatchTriggerMVCActionCommand, "_sendMessage",
			new Class<?>[] {long.class}, dispatchTriggerId);

		ArgumentCaptor<Message> argumentCaptor = ArgumentCaptor.forClass(
			Message.class);

		Mockito.verify(
			_messageBus
		).sendMessage(
			Mockito.eq(DispatchConstants.EXECUTOR_DESTINATION_NAME),
			argumentCaptor.capture()
		);

		Message message = argumentCaptor.getValue();

		Assert.assertEquals(companyId, message.getLong("companyId"));
	}

	private final DispatchTrigger _dispatchTrigger = Mockito.mock(
		DispatchTrigger.class);
	private final DispatchTriggerLocalService _dispatchTriggerLocalService =
		Mockito.mock(DispatchTriggerLocalService.class);
	private final EditDispatchTriggerMVCActionCommand
		_editDispatchTriggerMVCActionCommand =
			new EditDispatchTriggerMVCActionCommand();
	private final MessageBus _messageBus = Mockito.mock(MessageBus.class);

}