/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.counter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class CounterLocalServiceThreadTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testConcurrentIncrement() throws Exception {
		PropsUtil.set(PropsKeys.COUNTER_INCREMENT_PREFIX + _COUNTER_NAME, "1");

		CounterLocalServiceUtil.reset(_COUNTER_NAME);

		CounterLocalServiceUtil.reset(_COUNTER_NAME, 0);

		ExecutorService executorService = Executors.newFixedThreadPool(
			_THREAD_COUNT);

		List<Future<Long[]>> futures = new ArrayList<>();

		for (int i = 0; i < _THREAD_COUNT; i++) {
			futures.add(
				executorService.submit(
					new CompanyInheritableThreadLocalCallable<>(
						() -> {
							List<Long> ids = new ArrayList<>();

							for (int j = 0; j < _INCREMENT_COUNT; j++) {
								ids.add(
									CounterLocalServiceUtil.increment(
										_COUNTER_NAME));
							}

							return ids.toArray(new Long[0]);
						})));
		}

		int total = _THREAD_COUNT * _INCREMENT_COUNT;

		List<Long> ids = new ArrayList<>(total);

		for (Future<Long[]> future : futures) {
			Collections.addAll(ids, future.get());
		}

		executorService.shutdown();

		Assert.assertEquals(ids.toString(), total, ids.size());

		Collections.sort(ids);

		for (int i = 0; i < total; i++) {
			Long id = ids.get(i);

			Assert.assertEquals(i + 1, id.intValue());
		}
	}

	private static final String _COUNTER_NAME =
		CounterLocalServiceThreadTest.class.getName();

	private static final int _INCREMENT_COUNT = 10000;

	private static final int _THREAD_COUNT = 8;

}