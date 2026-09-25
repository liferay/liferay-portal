/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.ReflectionTestUtil;

import java.io.File;

import org.json.JSONObject;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class JUnitTestClassTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetJSONObject() throws Exception {
		JUnitTestClass jUnitTestClass = Mockito.mock(JUnitTestClass.class);

		Mockito.doCallRealMethod(
		).when(
			jUnitTestClass
		).getJSONObject();

		Mockito.doReturn(
			new File(RandomTestUtil.randomString())
		).when(
			jUnitTestClass
		).getTestClassFile();

		File testPropertiesFile = temporaryFolder.newFile();

		ReflectionTestUtil.setFieldValue(
			jUnitTestClass, "_testPropertiesFile", testPropertiesFile);

		JSONObject jUnitTestClassJSONObject = jUnitTestClass.getJSONObject();

		testEquals(
			JenkinsResultsParserUtil.getCanonicalPath(testPropertiesFile),
			jUnitTestClassJSONObject.get("test_properties_file"));
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

}