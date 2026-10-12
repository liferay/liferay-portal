/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.web.internal.messaging;

import com.liferay.adaptive.media.processor.AMProcessor;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Saurasish Basak
 */
public class AMMessageListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ServiceTrackerMap<String, List<AMProcessor<Object>>> serviceTrackerMap =
			Mockito.mock(ServiceTrackerMap.class);

		Mockito.when(
			serviceTrackerMap.getService(_CLASS_NAME)
		).thenReturn(
			Collections.singletonList(_amProcessor)
		);

		ReflectionTestUtil.setFieldValue(
			_amMessageListener, "_serviceTrackerMap", serviceTrackerMap);

		Mockito.doAnswer(
			invocation -> _ctCollectionIds.add(
				CTCollectionThreadLocal.getCTCollectionId())
		).when(
			_amProcessor
		).cleanUp(
			Mockito.any()
		);

		Mockito.doAnswer(
			invocation -> _ctCollectionIds.add(
				CTCollectionThreadLocal.getCTCollectionId())
		).when(
			_amProcessor
		).process(
			Mockito.any()
		);
	}

	@Test
	public void testDoReceive() throws Exception {
		_testDoReceive(AMProcessorCommand.CLEAN_UP);
		_testDoReceive(AMProcessorCommand.PROCESS);
	}

	private void _testDoReceive(AMProcessorCommand amProcessorCommand)
		throws Exception {

		_ctCollectionIds.clear();

		long ctCollectionId = CTCollectionThreadLocal.getCTCollectionId();

		Message message = new Message();

		message.put("className", _CLASS_NAME);
		message.put("command", amProcessorCommand);

		long messageCTCollectionId = RandomTestUtil.randomLong();

		message.put("ctCollectionId", messageCTCollectionId);

		message.put("model", new Object());
		message.put("modelId", RandomTestUtil.randomString());

		_amMessageListener.doReceive(message);

		Assert.assertEquals(
			_ctCollectionIds.toString(), 1, _ctCollectionIds.size());
		Assert.assertEquals(
			Long.valueOf(messageCTCollectionId), _ctCollectionIds.get(0));
		Assert.assertEquals(
			ctCollectionId, CTCollectionThreadLocal.getCTCollectionId());
	}

	private static final String _CLASS_NAME = RandomTestUtil.randomString();

	private final AMMessageListener _amMessageListener =
		new AMMessageListener();
	private final AMProcessor<Object> _amProcessor = Mockito.mock(
		AMProcessor.class);
	private final List<Long> _ctCollectionIds = new ArrayList<>();

}