/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.talend.web.internal.process;

import com.liferay.dispatch.talend.archive.TalendArchive;
import com.liferay.dispatch.talend.archive.TalendArchiveParserUtil;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.FastDateFormatFactoryImpl;
import com.liferay.portal.util.FileImpl;
import com.liferay.portal.util.PortalClassPathUtil;

import java.io.InputStream;

import java.net.URL;

import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Igor Beslic
 */
public class TalendProcessTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		ReflectionTestUtil.setFieldValue(
			FileUtil.class, "_file", FileImpl.getInstance());
		ReflectionTestUtil.setFieldValue(
			FastDateFormatFactoryUtil.class, "_fastDateFormatFactory",
			new FastDateFormatFactoryImpl());

		PortalClassPathUtil.initializeClassPaths(null);
	}

	@Test
	public void testBuilder() throws Exception {
		TalendProcess.Builder talendProcessBuilder =
			new TalendProcess.Builder();

		long companyId = RandomTestUtil.randomLong();

		talendProcessBuilder.companyId(companyId);

		talendProcessBuilder.contextParam("JAVA_OPTS", "-Xint -Xms2G -Xmx512M");

		UnicodeProperties unicodeProperties =
			RandomTestUtil.randomUnicodeProperties(
				RandomTestUtil.randomInt(5, 10),
				RandomTestUtil.randomInt(5, 10),
				RandomTestUtil.randomInt(5, 10));

		for (Map.Entry<String, String> entry : unicodeProperties.entrySet()) {
			talendProcessBuilder.contextParam(entry.getKey(), entry.getValue());
		}

		talendProcessBuilder.lastRunStartDate(null);

		TalendArchive talendArchive = TalendArchiveParserUtil.parse(
			_getInputStream());

		talendProcessBuilder.talendArchive(talendArchive);

		TalendProcess talendProcess = talendProcessBuilder.build();

		List<String> arguments = Arrays.asList(
			talendProcess.getMainMethodArguments());

		Assert.assertEquals(
			arguments.toString(), unicodeProperties.size() + 3,
			arguments.size());

		Assert.assertTrue(
			arguments.contains(
				"--context_param companyId=".concat(
					String.valueOf(companyId))));

		Assert.assertTrue(
			arguments.contains(
				"--context_param jobWorkDirectory=".concat(
					talendArchive.getJobDirectory())));

		for (Map.Entry<String, String> entry : unicodeProperties.entrySet()) {
			Assert.assertTrue(
				arguments.contains(
					StringBundler.concat(
						"--context_param ", entry.getKey(), StringPool.EQUAL,
						entry.getValue())));
		}

		arguments.forEach(
			argument -> Assert.assertFalse(
				argument.startsWith("--context_param lastRunStartDate=")));

		ProcessConfig processConfig = talendProcess.getProcessConfig();

		List<String> processConfigArguments = processConfig.getArguments();

		Assert.assertEquals(
			processConfigArguments.toString(), 3,
			processConfigArguments.size());

		Assert.assertTrue(processConfigArguments.contains("-Xint"));
		Assert.assertTrue(processConfigArguments.contains("-Xms2G"));
		Assert.assertTrue(processConfigArguments.contains("-Xmx512M"));

		Assert.assertTrue(
			Validator.isNotNull(processConfig.getRuntimeClassPath()));

		Date date = RandomTestUtil.nextDate();

		talendProcessBuilder.lastRunStartDate(date);

		talendProcess = talendProcessBuilder.build();

		processConfigArguments = Arrays.asList(
			talendProcess.getMainMethodArguments());

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			TalendProcess.ISO_8601_PATTERN);

		Assert.assertTrue(
			processConfigArguments.contains(
				"--context_param lastRunStartDate=".concat(
					simpleDateFormat.format(date))));
	}

	private InputStream _getInputStream() throws Exception {
		URL url = TalendProcessTest.class.getResource("/jobInfo.properties");

		String urlString = String.valueOf(url);

		URL zipURL = new URL(
			urlString.substring(
				"jar:".length(),
				urlString.length() - "!/jobInfo.properties".length()));

		return zipURL.openStream();
	}

}