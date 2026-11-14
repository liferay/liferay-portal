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
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import org.dom4j.Document;
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
	public void testDeleteTestrayCaseResultFile() {
		AxisTestClassGroup axisTestClassGroup = Mockito.mock(
			AxisTestClassGroup.class);
		String testrayCaseFileName = RandomTestUtil.randomString();
		TestrayServer testrayServer = Mockito.mock(TestrayServer.class);

		BaseTestrayContext baseTestrayContext = _mockBaseTestrayContext(
			axisTestClassGroup, testrayCaseFileName, testrayServer);

		Mockito.doCallRealMethod(
		).when(
			baseTestrayContext
		).deleteTestrayCaseResultFile(
			Mockito.any(AxisTestClassGroup.class)
		);

		baseTestrayContext.deleteTestrayCaseResultFile(axisTestClassGroup);

		Mockito.verify(
			testrayServer
		).deleteCaseResultFile(
			testrayCaseFileName
		);
	}

	@Test
	public void testGetTestcaseElementAttachment() throws Exception {
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

		TestrayCaseResult testrayCaseResult = Mockito.mock(
			TestrayCaseResult.class);

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
	public void testHasTestrayCaseResultFile() {
		_testHasTestrayCaseResultFile(false);
		_testHasTestrayCaseResultFile(true);
	}

	@Test
	public void testRecordTestrayCaseResultFilePlaywrightPortalLogFailure()
		throws Exception {

		String portalLogErrors = RandomTestUtil.randomString();

		String xmlString = _recordTestrayCaseResultFile(
			Mockito.mock(PlaywrightAxisTestClassGroup.class), portalLogErrors,
			false);

		Assert.assertTrue(xmlString, xmlString.contains(portalLogErrors));
	}

	@Test
	public void testRecordTestrayCaseResultFilePlaywrightPortalLogPassing()
		throws Exception {

		String xmlString = _recordTestrayCaseResultFile(
			Mockito.mock(PlaywrightAxisTestClassGroup.class), null, false);

		Assert.assertFalse(xmlString, xmlString.contains("<failure"));
	}

	@Test
	public void testRecordTestrayCaseResultFilePortalLogFailure()
		throws Exception {

		String portalLogErrors = RandomTestUtil.randomString();

		String xmlString = _recordTestrayCaseResultFile(
			Mockito.mock(AxisTestClassGroup.class), portalLogErrors, false);

		Assert.assertTrue(xmlString, xmlString.contains(portalLogErrors));
	}

	@Test
	public void testRecordTestrayCaseResultFileTopLevelBuildCompleted()
		throws Exception {

		Element propertiesElement = _getPropertiesElement(
			_recordTestrayCaseResultFile(
				Mockito.mock(AxisTestClassGroup.class), null, true));

		testEquals(
			JenkinsResultsParserUtil.toDurationString(_DURATION),
			_getPropertyValue(propertiesElement, "testray.build.time"));
		testEquals(
			JenkinsResultsParserUtil.toDurationString(_TOTAL_ACTUAL_DURATION),
			_getPropertyValue(propertiesElement, "testray.total.cpu.use.time"));
	}

	@Test
	public void testRecordTestrayCaseResultFileTopLevelBuildRunning()
		throws Exception {

		Element propertiesElement = _getPropertiesElement(
			_recordTestrayCaseResultFile(
				Mockito.mock(AxisTestClassGroup.class), null, false));

		Assert.assertNull(
			_getPropertyValue(propertiesElement, "testray.build.time"));
		Assert.assertNull(
			_getPropertyValue(propertiesElement, "testray.total.cpu.use.time"));
	}

	@Test
	public void testUpdateTestrayCaseResultFileDurations() throws Exception {
		AxisTestClassGroup axisTestClassGroup = Mockito.mock(
			AxisTestClassGroup.class);

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			axisTestClassGroup
		).getAxisName();

		TestrayServer testrayServer = Mockito.mock(TestrayServer.class);

		String testrayBuildName = RandomTestUtil.randomString();
		String testcaseName = RandomTestUtil.randomString();

		Mockito.doReturn(
			JenkinsResultsParserUtil.combine(
				"<testsuite><properties>",
				"<property name=\"testray.build.name\" value=\"",
				testrayBuildName, "\"/>",
				"<property name=\"testray.build.time\" value=\"1 ms\"/>",
				"<property name=\"testray.total.cpu.use.time\" ",
				"value=\"2 ms\"/></properties><testcase name=\"", testcaseName,
				"\"/></testsuite>")
		).when(
			testrayServer
		).readCaseResultFile(
			Mockito.anyString()
		);

		TestrayBuild testrayBuild = Mockito.mock(TestrayBuild.class);

		Mockito.doReturn(
			testrayServer
		).when(
			testrayBuild
		).getTestrayServer();

		TestrayRun testrayRun = Mockito.mock(TestrayRun.class);

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

		String testrayCaseFileName = RandomTestUtil.randomString();

		TopLevelBuildReport topLevelBuildReport = _mockTopLevelBuildReport(
			testrayCaseFileName);

		BaseTestrayContext baseTestrayContext = Mockito.mock(
			BaseTestrayContext.class);

		Mockito.doCallRealMethod(
		).when(
			baseTestrayContext
		).updateTestrayCaseResultFileDurations(
			Mockito.any(AxisTestClassGroup.class)
		);

		Mockito.doReturn(
			topLevelBuildReport
		).when(
			baseTestrayContext
		).getTopLevelBuildReport();

		baseTestrayContext.updateTestrayCaseResultFileDurations(
			axisTestClassGroup);

		ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(
			String.class);

		Mockito.verify(
			testrayServer
		).writeCaseResult(
			Mockito.eq(testrayCaseFileName), argumentCaptor.capture()
		);

		String xmlString = argumentCaptor.getValue();

		Element propertiesElement = _getPropertiesElement(xmlString);

		List<Element> propertyElements = propertiesElement.elements("property");

		testEquals(3, propertyElements.size());

		testEquals(
			JenkinsResultsParserUtil.toDurationString(_DURATION),
			_getPropertyValue(propertiesElement, "testray.build.time"));
		testEquals(
			JenkinsResultsParserUtil.toDurationString(_TOTAL_ACTUAL_DURATION),
			_getPropertyValue(propertiesElement, "testray.total.cpu.use.time"));
		testEquals(
			testrayBuildName,
			_getPropertyValue(propertiesElement, "testray.build.name"));

		Assert.assertTrue(xmlString, xmlString.contains(testcaseName));
	}

	private Element _getPropertiesElement(String xmlString) throws Exception {
		Document document = Dom4JUtil.parse(xmlString);

		Element rootElement = document.getRootElement();

		return rootElement.element("properties");
	}

	private String _getPropertyValue(
		Element propertiesElement, String propertyName) {

		for (Element propertyElement : propertiesElement.elements("property")) {
			if (Objects.equals(
					propertyElement.attributeValue("name"), propertyName)) {

				return propertyElement.attributeValue("value");
			}
		}

		return null;
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

	private BaseTestrayContext _mockBaseTestrayContext(
		AxisTestClassGroup axisTestClassGroup, String testrayCaseFileName,
		TestrayServer testrayServer) {

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			axisTestClassGroup
		).getAxisName();

		TestrayBuild testrayBuild = Mockito.mock(TestrayBuild.class);

		Mockito.doReturn(
			testrayServer
		).when(
			testrayBuild
		).getTestrayServer();

		TestrayRun testrayRun = Mockito.mock(TestrayRun.class);

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

		BaseTestrayContext baseTestrayContext = Mockito.mock(
			BaseTestrayContext.class);

		Mockito.doReturn(
			_mockTopLevelBuildReport(testrayCaseFileName)
		).when(
			baseTestrayContext
		).getTopLevelBuildReport();

		return baseTestrayContext;
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

	private TopLevelBuildReport _mockTopLevelBuildReport(
		String testrayCaseFileName) {

		TopLevelBuildReport topLevelBuildReport = Mockito.mock(
			TopLevelBuildReport.class);

		Mockito.doReturn(
			_DURATION
		).when(
			topLevelBuildReport
		).getDuration();

		Mockito.doReturn(
			testrayCaseFileName
		).when(
			topLevelBuildReport
		).getTestrayCaseResultFileName(
			Mockito.anyString()
		);

		Mockito.doReturn(
			_TOTAL_ACTUAL_DURATION
		).when(
			topLevelBuildReport
		).getTotalActualDuration();

		return topLevelBuildReport;
	}

	private String _recordTestrayCaseResultFile(
			AxisTestClassGroup axisTestClassGroup, String portalLogErrors,
			boolean topLevelBuildCompleted)
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

		TestrayServer testrayServer = Mockito.mock(TestrayServer.class);

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

		TopLevelBuildReport topLevelBuildReport = _mockTopLevelBuildReport(
			RandomTestUtil.randomString());

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

		Mockito.doReturn(
			topLevelBuildCompleted
		).when(
			baseTestrayContext
		).isTopLevelBuildCompleted();

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

		Mockito.verify(
			portalLogBatchBuildTestrayCaseResult,
			getVerificationMode(portalLogErrors != null)
		).setParentTestrayCaseResult(
			testrayCaseResult
		);

		Mockito.verify(
			portalLogBatchBuildTestrayCaseResult,
			getVerificationMode(portalLogErrors != null)
		).setTestrayRun(
			testrayRun
		);

		ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(
			String.class);

		Mockito.verify(
			testrayServer
		).writeCaseResult(
			Mockito.anyString(), argumentCaptor.capture()
		);

		String xmlString = argumentCaptor.getValue();

		Document document = Dom4JUtil.parse(xmlString);

		Element rootElement = document.getRootElement();

		Assert.assertNotNull(rootElement.element("environments"));

		return xmlString;
	}

	private void _testGetTestcaseElementName(int expectedLength, int nameLength)
		throws Exception {

		String name = StringUtils.repeat(RandomTestUtil.randomString(), 8);

		name = name.substring(0, nameLength);

		testEquals(name.substring(0, expectedLength), _getTestcaseName(name));
	}

	private void _testHasTestrayCaseResultFile(
		boolean caseResultFileAvailable) {

		AxisTestClassGroup axisTestClassGroup = Mockito.mock(
			AxisTestClassGroup.class);
		String testrayCaseFileName = RandomTestUtil.randomString();
		TestrayServer testrayServer = Mockito.mock(TestrayServer.class);

		BaseTestrayContext baseTestrayContext = _mockBaseTestrayContext(
			axisTestClassGroup, testrayCaseFileName, testrayServer);

		Mockito.doCallRealMethod(
		).when(
			baseTestrayContext
		).hasTestrayCaseResultFile(
			Mockito.any(AxisTestClassGroup.class)
		);

		Mockito.doReturn(
			caseResultFileAvailable
		).when(
			testrayServer
		).isCaseResultFileAvailable(
			testrayCaseFileName
		);

		testEquals(
			caseResultFileAvailable,
			baseTestrayContext.hasTestrayCaseResultFile(axisTestClassGroup));
	}

	private static final long _DURATION = 5400000L;

	private static final long _TOTAL_ACTUAL_DURATION = 86400000L;

}