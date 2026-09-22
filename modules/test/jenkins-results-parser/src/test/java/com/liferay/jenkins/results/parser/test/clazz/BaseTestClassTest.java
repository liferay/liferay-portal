/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;

import java.io.File;

import org.json.JSONObject;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class BaseTestClassTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetJSONObject() {
		BaseTestClass baseTestClass = Mockito.mock(BaseTestClass.class);

		Mockito.doCallRealMethod(
		).when(
			baseTestClass
		).getJSONObject();

		File testClassFile = new File(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		Mockito.doReturn(
			testClassFile
		).when(
			baseTestClass
		).getTestClassFile();

		JSONObject baseTestClassJSONObject = baseTestClass.getJSONObject();

		testEquals(
			JenkinsResultsParserUtil.getCanonicalPath(testClassFile),
			baseTestClassJSONObject.get("file"));
	}

}