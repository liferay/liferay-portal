/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.audiences.web.internal.util;

import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.AdditionalAnswers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Iván Zaera Avellón
 */
public class BootstrapJavaScriptUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {

		// Stop the static initializer of BootstrapJavaScriptUtil from reaching
		// DigesterUtil and PropsValues, which are unwired in a unit test

		_hashedFilesUtilMockedStatic = Mockito.mockStatic(
			HashedFilesUtil.class);

		_htmlUtilMockedStatic = Mockito.mockStatic(HtmlUtil.class);

		_htmlUtilMockedStatic.when(
			() -> HtmlUtil.escapeJS(Mockito.anyString())
		).thenAnswer(
			AdditionalAnswers.returnsFirstArg()
		);
	}

	@After
	public void tearDown() {
		_htmlUtilMockedStatic.close();
		_hashedFilesUtilMockedStatic.close();
	}

	@Test
	public void testGetContentEscapesAudiencesDefinitionHash() {
		String audiencesDefinitionHash = RandomTestUtil.randomString(8);

		BootstrapJavaScriptUtil.getContent(audiencesDefinitionHash, 5000, true);

		_htmlUtilMockedStatic.verify(
			() -> HtmlUtil.escapeJS(audiencesDefinitionHash));
	}

	@Test
	public void testGetContentReplacesEveryTemplateToken() {
		String content = BootstrapJavaScriptUtil.getContent(
			RandomTestUtil.randomString(8), 5000, true);

		// All three known tokens are replaced

		Assert.assertFalse(content.contains("[$AUDIENCES_DEFINITION_HASH$]"));
		Assert.assertFalse(content.contains("[$DETECTION_TIMEOUT$]"));
		Assert.assertFalse(content.contains("[$ENABLE_LOG$]"));

		// No stale tokens remain

		Assert.assertFalse(
			_tokenPattern.matcher(
				content
			).find());
	}

	@Test
	public void testGetContentSubstitutesAudiencesDefinitionHash() {
		String audiencesDefinitionHash = RandomTestUtil.randomString(8);

		String content = BootstrapJavaScriptUtil.getContent(
			audiencesDefinitionHash, 5000, true);

		Assert.assertTrue(
			content,
			content.contains("definition.(" + audiencesDefinitionHash + ")"));
	}

	@Test
	public void testGetContentSubstitutesDetectionTimeout() {
		String content = BootstrapJavaScriptUtil.getContent(
			RandomTestUtil.randomString(8), 5000, true);

		Assert.assertTrue(content, content.contains("timeout: 5000"));
	}

	@Test
	public void testGetContentSubstitutesEnableLog() {
		String content = BootstrapJavaScriptUtil.getContent(
			RandomTestUtil.randomString(8), 5000, true);

		Assert.assertTrue(content, content.contains("setLogEnabled(true)"));

		content = BootstrapJavaScriptUtil.getContent(
			RandomTestUtil.randomString(8), 5000, false);

		Assert.assertTrue(content, content.contains("setLogEnabled(false)"));
	}

	private static final Pattern _tokenPattern = Pattern.compile(
		"\\[\\$[A-Z0-9_]+\\$\\]");

	private MockedStatic<HashedFilesUtil> _hashedFilesUtilMockedStatic;
	private MockedStatic<HtmlUtil> _htmlUtilMockedStatic;

}