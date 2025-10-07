/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.json.jabsorb.serializer;

import com.liferay.petra.lang.ClassLoaderPool;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.json.JSONObject;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Michael Bowerman
 */
public class LiferayJSONSerializerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetClassFromHint() throws Exception {
		ClassLoaderPool.register(
			"TestClassLoader",
			new ClassLoader() {

				@Override
				public Class<?> loadClass(String name)
					throws ClassNotFoundException {

					if (name.equals(ServiceContext.class.getName())) {
						throw new ClassNotFoundException();
					}

					return super.loadClass(name);
				}

			});

		LiferayJSONSerializer liferayJSONSerializer = new LiferayJSONSerializer(
			new LiferayJSONDeserializationWhitelist());

		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"contextName", "TestClassLoader"
		).put(
			"javaClass", ServiceContext.class.getName()
		);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				LiferayJSONSerializer.class.getName(), LoggerTestUtil.WARN)) {

			Assert.assertEquals(
				ServiceContext.class,
				liferayJSONSerializer.getClassFromHint(jsonObject));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to load class " + ServiceContext.class.getName() +
					" in context TestClassLoader",
				logEntry.getMessage());
		}

		ClassLoaderPool.unregister("TestClassLoader");
	}

}