/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.async.advice.internal;

import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.async.Async;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Method;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class AsyncAdviceTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testCreateMethodContext() throws Exception {
		AsyncAdvice asyncAdvice = new AsyncAdvice();

		asyncAdvice.activate(
			HashMapBuilder.put(
				"targetClassNamesToDestinationNames",
				TestService.class.getName() + "=liferay/test"
			).build());

		Assert.assertEquals("liferay/test", _createMethodContext(asyncAdvice));

		asyncAdvice.activate(Collections.emptyMap());

		Assert.assertEquals(
			DestinationNames.ASYNC_SERVICE, _createMethodContext(asyncAdvice));
	}

	private Object _createMethodContext(AsyncAdvice asyncAdvice)
		throws Exception {

		Method method = TestService.class.getMethod("run");

		return asyncAdvice.createMethodContext(
			new TestService(), method,
			Collections.singletonMap(
				Async.class, method.getAnnotation(Async.class)));
	}

	private static class TestService {

		@Async
		public void run() {
		}

	}

}