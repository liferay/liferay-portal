/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.antisamy.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pedro Victor Silvestre
 */
@RunWith(Arquillian.class)
public class AntiSamySanitizerImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testSanitize() throws Exception {
		_testSanitize(
			StringPool.BLANK,
			"<p><a href=\"test\" rel=\"noopener noreferrer\" " +
				"target=\"_blank\"></a></p>",
			"<p><a href=\"test\" rel=\"noopener noreferrer\" " +
				"target=\"_blank\"></a></p>");
		_testSanitize(
			StringPool.BLANK,
			"This little text should not have a space removed but it happens " +
				"right here.",
			"This little text should not have a space removed but it happens " +
				"right here.");
	}

	@Test
	@TestInfo("LPD-103042")
	public void testSanitizeWithConfiguredClassName() throws Exception {
		String className = RandomTestUtil.randomString();
		String factoryPid =
			"com.liferay.portal.security.antisamy.configuration." +
				"AntiSamyClassNameConfiguration";

		String pid = ConfigurationTestUtil.createFactoryConfiguration(
			factoryPid,
			HashMapDictionaryBuilder.<String, Object>put(
				"className", className
			).put(
				"configurationFileURL",
				"/META-INF/resources/fragment-sanitizer-configuration.xml"
			).build());

		String svg = "<svg><circle cx=\"1\"></circle></svg>";

		try {
			Assert.assertEquals(svg, _sanitize(className, svg));
			Assert.assertEquals(
				StringPool.BLANK, _sanitize(className + "Rel", svg));
		}
		finally {
			ConfigurationTestUtil.deleteFactoryConfiguration(pid, factoryPid);
		}

		Assert.assertEquals(StringPool.BLANK, _sanitize(className, svg));
	}

	@Test
	@TestInfo("LPD-103042")
	public void testSanitizeWithNullClassName() throws Exception {
		String value = RandomTestUtil.randomString();

		_testSanitize(null, value, value);
	}

	private String _sanitize(String className, String value) throws Exception {
		return _sanitizer.sanitize(
			TestPropsValues.getCompanyId(), 0, 0, className, 0,
			ContentTypes.TEXT_HTML, new String[0], value, new HashMap<>());
	}

	private void _testSanitize(
			String className, String expectedValue, String value)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.security.antisamy.internal." +
					"AntiSamySanitizerImpl",
				LoggerTestUtil.WARN)) {

			Assert.assertEquals(expectedValue, _sanitize(className, value));

			Assert.assertTrue(ListUtil.isEmpty(logCapture.getLogEntries()));
		}
	}

	@Inject(
		filter = "component.name=com.liferay.portal.security.antisamy.internal.AntiSamySanitizerImpl"
	)
	private Sanitizer _sanitizer;

}