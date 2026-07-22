/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.dto.v1_0;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Carlos Correa
 */
public class ObjectEntryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void test() throws Exception {
		AtomicInteger atomicInteger1 = new AtomicInteger();
		AtomicInteger atomicInteger2 = new AtomicInteger();
		AtomicInteger atomicInteger3 = new AtomicInteger();
		AtomicInteger atomicInteger4 = new AtomicInteger();
		String nullString = null;

		ObjectEntry objectEntry = new ObjectEntry() {
			{
				setProperties(
					HashMapBuilder.<String, Object>put(
						"property1",
						new AtomicIntegerUnsafeSupplier(
							atomicInteger1, "value1")
					).put(
						"property2",
						new AtomicIntegerUnsafeSupplier(
							atomicInteger2, "value2")
					).put(
						"property3",
						new AtomicIntegerUnsafeSupplier(
							atomicInteger3, "value3")
					).put(
						"property4",
						new AtomicIntegerUnsafeSupplier(
							atomicInteger4, nullString)
					).put(
						"property5", nullString
					).build());
			}
		};

		Runtime runtime = Runtime.getRuntime();

		ExecutorService executorService = Executors.newFixedThreadPool(
			runtime.availableProcessors());

		try {
			List<Future<Map<String, Object>>> futures = new ArrayList<>();

			for (int i = 0; i < 100; i++) {
				futures.add(
					executorService.submit(
						() -> new HashMap<>(objectEntry.getProperties())));
			}

			for (Future<?> future : futures) {
				Map<String, Object> properties =
					(Map<String, Object>)future.get();

				Assert.assertEquals("value1", properties.get("property1"));
				Assert.assertEquals(
					properties.toString(), "value2",
					properties.get("property2"));
				Assert.assertEquals(
					properties.toString(), "value3",
					properties.get("property3"));
				Assert.assertNull(properties.get("property4"));
				Assert.assertNull(properties.get("property5"));
				Assert.assertEquals(
					properties.toString(), 5, properties.size());

				Assert.assertEquals(1, atomicInteger1.get());
				Assert.assertEquals(1, atomicInteger2.get());
				Assert.assertEquals(1, atomicInteger3.get());
				Assert.assertEquals(1, atomicInteger4.get());
			}
		}
		finally {
			executorService.shutdown();
		}
	}

	private class AtomicIntegerUnsafeSupplier
		implements UnsafeSupplier<Object, Exception> {

		public AtomicIntegerUnsafeSupplier(
			AtomicInteger atomicInteger, String value) {

			_atomicInteger = atomicInteger;
			_value = value;
		}

		@Override
		public Object get() {
			_atomicInteger.incrementAndGet();

			return _value;
		}

		private final AtomicInteger _atomicInteger;
		private final String _value;

	}

}