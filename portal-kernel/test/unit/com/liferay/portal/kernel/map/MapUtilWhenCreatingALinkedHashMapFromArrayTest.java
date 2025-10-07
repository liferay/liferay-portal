/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.map;

import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Sampsa Sohlman
 * @author Manuel de la Peña
 * @author Péter Borkuti
 */
public class MapUtilWhenCreatingALinkedHashMapFromArrayTest {

	@Test
	public void testShouldReturnEmptyMapWithParamsInvalid() {
		Map<String, Object> map = MapUtil.toLinkedHashMap(new String[] {"one"});

		Assert.assertTrue(map.toString(), map.isEmpty());

		map = MapUtil.toLinkedHashMap(new String[] {"one:two:three:four"});

		Assert.assertTrue(map.toString(), map.isEmpty());
	}

	@Test
	public void testShouldReturnEmptyMapWithParamsNull() {
		Map<String, Object> map = MapUtil.toLinkedHashMap(null);

		Assert.assertTrue(map.toString(), map.isEmpty());
	}

	@Test
	public void testShouldReturnEmptyMapWithParamsTypeObject() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				MapUtil.class.getName(), LoggerTestUtil.ERROR)) {

			Map<String, Object> map = MapUtil.toLinkedHashMap(
				new String[] {"one:1:" + Object.class.getName()});

			Assert.assertTrue(map.toString(), map.isEmpty());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"java.lang.Object.<init>(java.lang.String)",
				logEntry.getMessage());

			Throwable throwable = logEntry.getThrowable();

			Assert.assertSame(
				NoSuchMethodException.class, throwable.getClass());
		}
	}

	@Test
	public void testShouldReturnEmptyMapWithParamsZeroLength() {
		Map<String, String> map = MapUtil.toLinkedHashMap(new String[0]);

		Assert.assertTrue(map.toString(), map.isEmpty());
	}

	@Test
	public void testShouldReturnMapWithDelimiterCustom() {
		Map<String, String> map = MapUtil.toLinkedHashMap(new String[0], ",");

		Assert.assertTrue(map.toString(), map.isEmpty());

		map = MapUtil.toLinkedHashMap(new String[] {"one,1"}, ",");

		Assert.assertEquals(map.toString(), 1, map.size());
		Assert.assertTrue(map.containsKey("one"));
		Assert.assertTrue(map.containsValue("1"));

		map = MapUtil.toLinkedHashMap(new String[] {"one,1", "two,2"}, ",");

		Assert.assertEquals(map.toString(), 2, map.size());
	}

	@Test
	public void testShouldReturnMapWithDelimiterDefault() {
		Map<String, String> map = MapUtil.toLinkedHashMap(new String[0]);

		Assert.assertTrue(map.toString(), map.isEmpty());

		map = MapUtil.toLinkedHashMap(new String[] {"one:1"});

		Assert.assertEquals(map.toString(), 1, map.size());
		Assert.assertTrue(map.containsKey("one"));
		Assert.assertTrue(map.containsValue("1"));

		map = MapUtil.toLinkedHashMap(new String[] {"one:1", "two:2"});

		Assert.assertEquals(map.toString(), 2, map.size());
	}

}