/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.Dom4JUtil;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.ReflectionTestUtil;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.PlaywrightAxisTestClassGroup;

import java.net.URL;

import java.util.Collections;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import org.dom4j.Element;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class BaseTestrayContextTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetTestcaseElementAttachment() throws Exception {
		TestrayCaseResult testrayCaseResult = Mockito.mock(
			TestrayCaseResult.class);

		TestrayAttachment testrayAttachment = Mockito.mock(
			TestrayAttachment.class);

		String key = RandomTestUtil.randomString();

		Mockito.doReturn(
			key
		).when(
			testrayAttachment
		).getKey();

		String url = JenkinsResultsParserUtil.combine(
			"https://", RandomTestUtil.randomString(), "/", key);

		Mockito.doReturn(
			new URL(url)
		).when(
			testrayAttachment
		).getURL();

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			testrayCaseResult
		).getName();

		Mockito.doReturn(
			TestrayCaseResult.Status.PASSED
		).when(
			testrayCaseResult
		).getStatus();

		Mockito.doReturn(
			Collections.singletonList(testrayAttachment)
		).when(
			testrayCaseResult
		).getTestrayAttachments();

		Element testcaseElement = _getTestcaseElement(testrayCaseResult);

		Element attachmentsElement = testcaseElement.element("attachments");

		Element fileElement = attachmentsElement.element("file");

		testEquals(key, fileElement.attributeValue("value"));
		testEquals(url + "?authuser=0", fileElement.attributeValue("url"));
	}

	@Test
	public void testGetTestcaseElementName() throws Exception {
		_testGetTestcaseElementName(250, 250);
		_testGetTestcaseElementName(250, 251);
	}

	@Test
	public void testRecordTestrayCaseResultFilePlaywrightPortalLogFailure()
		throws Exception {

		String portalLogErrors = RandomTestUtil.randomString();

		String xmlString = _recordTestrayCaseResultFile(
			Mockito.mock(PlaywrightAxisTestClassGroup.class), portalLogErrors);

		Assert.assertTrue(xmlString, xmlString.contains(portalLogErrors));
	}

	@Test
	public void testRecordTestrayCaseResultFilePlaywrightPortalLogPassing()
		throws Exception {

		String xmlString = _recordTestrayCaseResultFile(
			Mockito.mock(PlaywrightAxisTestClassGroup.class), null);

		Assert.assertFalse(xmlString, xmlString.contains("<failure"));
	}

	@Test
	public void testRecordTestrayCaseResultFilePortalLogFailure()
		throws Exception {

		String portalLogErrors = RandomTestUtil.randomString();

		String xmlString = _recordTestrayCaseResultFile(
			Mockito.mock(AxisTestClassGroup.class), portalLogErrors);

		Assert.assertTrue(xmlString, xmlString.contains(portalLogErrors));
	}

	private Element _getTestcaseElement(TestrayCaseResult testrayCaseResult)
		throws Exception {

		return ReflectionTestUtil.invoke(
			Mockito.mock(BaseTestrayContext.class), "_getTestcaseElement",
			new Class<?>[] {
				TestrayCaseResult.class, String.class, String[].class
			},
			testrayCaseResult, RandomTestUtil.randomString(), null);
	}

	private String _getTestcaseName(String name) throws Exception {
		TestrayCaseResult testrayCaseResult = Mockito.mock(
			TestrayCaseResult.class);

		Mockito.doReturn(
			name
		).when(
			testrayCaseResult
		).getName();

		Mockito.doReturn(
			TestrayCaseResult.Status.PASSED
		).when(
			testrayCaseResult
		).getStatus();

		Element testcaseElement = _getTestcaseElement(testrayCaseResult);

		Element propertiesElement = testcaseElement.element("properties");

		for (Element propertyElement : propertiesElement.elements("property")) {
			if (Objects.equals(
					propertyElement.attributeValue("name"),
					"testray.testcase.name")) {

				return propertyElement.attributeValue("value");
			}
		}

		return null;
	}

	private <T extends TestrayCaseResult> T _mockTestrayCaseResult(
		Class<T> clazz, String errors, TestrayCaseResult.Status status) {

		T testrayCaseResult = Mockito.mock(clazz);

		Mockito.doReturn(
			errors
		).when(
			testrayCaseResult
		).getErrors();

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			testrayCaseResult
		).getName();

		Mockito.doReturn(
			status
		).when(
			testrayCaseResult
		).getStatus();

		return testrayCaseResult;
	}

	private String _recordTestrayCaseResultFile(
			AxisTestClassGroup axisTestClassGroup, String portalLogErrors)
		throws Exception {

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			axisTestClassGroup
		).getAxisName();

		Mockito.doReturn(
			Collections.emptyList()
		).when(
			axisTestClassGroup
		).getTestClasses();

		TestrayServer testrayServer = Mockito.mock(TestrayServer.class);

		TestrayBuild testrayBuild = Mockito.mock(TestrayBuild.class);

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			testrayBuild
		).getName();

		Mockito.doReturn(
			Mockito.mock(TestrayProject.class)
		).when(
			testrayBuild
		).getTestrayProject();

		Mockito.doReturn(
			Mockito.mock(TestrayRoutine.class)
		).when(
			testrayBuild
		).getTestrayRoutine();

		Mockito.doReturn(
			testrayServer
		).when(
			testrayBuild
		).getTestrayServer();

		TestrayRun testrayRun = Mockito.mock(TestrayRun.class);

		Mockito.doReturn(
			Dom4JUtil.getNewElement("environments")
		).when(
			testrayRun
		).getEnvironmentsElement();

		Mockito.doReturn(
			testrayBuild
		).when(
			testrayRun
		).getTestrayBuild();

		Mockito.doReturn(
			testrayRun
		).when(
			axisTestClassGroup
		).getTestrayRun();

		TopLevelBuildReport topLevelBuildReport = Mockito.mock(
			TopLevelBuildReport.class);

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			topLevelBuildReport
		).getTestrayCaseFileName(
			Mockito.anyString()
		);

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			topLevelBuildReport
		).getTestSuiteName();

		BaseTestrayContext baseTestrayContext = Mockito.mock(
			BaseTestrayContext.class);

		Mockito.doCallRealMethod(
		).when(
			baseTestrayContext
		).recordTestrayCaseResultFile(
			Mockito.any(AxisTestClassGroup.class),
			Mockito.any(TestrayCaseResult.class)
		);

		Mockito.doReturn(
			topLevelBuildReport
		).when(
			baseTestrayContext
		).getTopLevelBuildReport();

		TestrayCaseResult testrayCaseResult = _mockTestrayCaseResult(
			TestrayCaseResult.class, null, TestrayCaseResult.Status.PASSED);

		PortalLogBatchBuildTestrayCaseResult
			portalLogBatchBuildTestrayCaseResult = _mockTestrayCaseResult(
				PortalLogBatchBuildTestrayCaseResult.class, portalLogErrors,
				TestrayCaseResult.Status.FAILED);

		try (MockedStatic<TestrayFactory> testrayFactoryMockedStatic =
				Mockito.mockStatic(TestrayFactory.class)) {

			testrayFactoryMockedStatic.when(
				() -> TestrayFactory.newPortalLogTestrayCaseResult(
					Mockito.any(AxisTestClassGroup.class),
					Mockito.any(TestrayBuild.class),
					Mockito.any(TopLevelBuildReport.class))
			).thenReturn(
				portalLogBatchBuildTestrayCaseResult
			);

			baseTestrayContext.recordTestrayCaseResultFile(
				axisTestClassGroup, testrayCaseResult);
		}

		ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(
			String.class);

		Mockito.verify(
			testrayServer
		).writeCaseResult(
			Mockito.anyString(), argumentCaptor.capture()
		);

		return argumentCaptor.getValue();
	}

	private void _testGetTestcaseElementName(int expectedLength, int nameLength)
		throws Exception {

		String name = StringUtils.repeat(RandomTestUtil.randomString(), 8);

		name = name.substring(0, nameLength);

		testEquals(name.substring(0, expectedLength), _getTestcaseName(name));
	}

}