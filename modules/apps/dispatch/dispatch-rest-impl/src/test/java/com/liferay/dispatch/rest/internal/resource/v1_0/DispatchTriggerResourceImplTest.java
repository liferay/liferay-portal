/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.rest.internal.resource.v1_0;

import com.liferay.dispatch.constants.DispatchConstants;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerService;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Magdalena Jedraszak
 */
public class DispatchTriggerResourceImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test() throws Exception {
		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			_dispatchTrigger.getCompanyId()
		).thenReturn(
			companyId
		);

		long dispatchTriggerId = RandomTestUtil.randomLong();

		Mockito.when(
			_dispatchTriggerService.getDispatchTrigger(dispatchTriggerId)
		).thenReturn(
			_dispatchTrigger
		);

		_dispatchTriggerResourceImpl.postDispatchTriggerRun(dispatchTriggerId);

		ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(
			Message.class);

		Mockito.verify(
			_messageBus
		).sendMessage(
			Mockito.eq(DispatchConstants.EXECUTOR_DESTINATION_NAME),
			messageArgumentCaptor.capture()
		);

		Message message = messageArgumentCaptor.getValue();

		Assert.assertEquals(companyId, message.getLong("companyId"));
	}

	@Mock
	private DispatchTrigger _dispatchTrigger;

	@Mock
	private ModelResourcePermission<DispatchTrigger>
		_dispatchTriggerModelResourcePermission;

	@InjectMocks
	private final DispatchTriggerResourceImpl _dispatchTriggerResourceImpl =
		new DispatchTriggerResourceImpl();

	@Mock
	private DispatchTriggerService _dispatchTriggerService;

	@Mock
	private MessageBus _messageBus;

}