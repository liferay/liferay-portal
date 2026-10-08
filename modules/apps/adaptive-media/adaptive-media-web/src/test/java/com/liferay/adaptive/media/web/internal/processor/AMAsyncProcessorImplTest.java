/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.web.internal.processor;

import com.liferay.adaptive.media.web.internal.constants.AMDestinationNames;
import com.liferay.adaptive.media.web.internal.messaging.AMProcessorCommand;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
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
 * @author Saurasish Basak
 */
public class AMAsyncProcessorImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_amAsyncProcessorImpl = new AMAsyncProcessorImpl<>(
			Object.class, _messageBus);
	}

	@Test
	public void testTriggerCleanUp() throws Exception {
		long ctCollectionId = RandomTestUtil.randomLong();
		String modelId = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			_amAsyncProcessorImpl.triggerCleanUp(new Object(), modelId);
		}

		AMAsyncProcessorImpl.cleanQueue(AMProcessorCommand.CLEAN_UP, modelId);

		_assertMessage(AMProcessorCommand.CLEAN_UP, ctCollectionId, modelId);
	}

	@Test
	public void testTriggerProcess() throws Exception {
		long ctCollectionId = RandomTestUtil.randomLong();
		String modelId = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			_amAsyncProcessorImpl.triggerProcess(new Object(), modelId);
		}

		AMAsyncProcessorImpl.cleanQueue(AMProcessorCommand.PROCESS, modelId);

		_assertMessage(AMProcessorCommand.PROCESS, ctCollectionId, modelId);
	}

	private void _assertMessage(
		AMProcessorCommand amProcessorCommand, long ctCollectionId,
		String modelId) {

		ArgumentCaptor<Message> argumentCaptor = ArgumentCaptor.forClass(
			Message.class);

		Mockito.verify(
			_messageBus
		).sendMessage(
			Mockito.eq(AMDestinationNames.ADAPTIVE_MEDIA_PROCESSOR),
			argumentCaptor.capture()
		);

		Message message = argumentCaptor.getValue();

		Assert.assertEquals(amProcessorCommand, message.get("command"));
		Assert.assertEquals(ctCollectionId, message.getLong("ctCollectionId"));
		Assert.assertEquals(modelId, message.getString("modelId"));
	}

	private AMAsyncProcessorImpl<Object, ?> _amAsyncProcessorImpl;
	private final MessageBus _messageBus = Mockito.mock(MessageBus.class);

}