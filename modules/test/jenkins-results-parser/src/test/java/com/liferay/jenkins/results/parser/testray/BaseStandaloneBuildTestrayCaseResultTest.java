/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.Dom4JUtil;
import com.liferay.jenkins.results.parser.Job;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import org.dom4j.Document;
import org.dom4j.Element;

import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Peter Yoo
 */
public class BaseStandaloneBuildTestrayCaseResultTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testRecordTestrayCaseResult() throws Exception {
		_testRecordTestrayCaseResult(250, 250);
		_testRecordTestrayCaseResult(250, 251);
	}

	private String _getTestcaseName(String name) throws Exception {
		BaseStandaloneBuildTestrayCaseResult
			baseStandaloneBuildTestrayCaseResult = Mockito.mock(
				BaseStandaloneBuildTestrayCaseResult.class);

		Mockito.doCallRealMethod(
		).when(
			baseStandaloneBuildTestrayCaseResult
		).addPropertyElements(
			Mockito.any(Element.class), Mockito.<Map<String, String>>any()
		);

		Mockito.doReturn(
			name
		).when(
			baseStandaloneBuildTestrayCaseResult
		).getName();

		Mockito.doReturn(
			TestrayCaseResult.Status.PASSED
		).when(
			baseStandaloneBuildTestrayCaseResult
		).getStatus();

		TestrayBuild testrayBuild = Mockito.mock(TestrayBuild.class);

		Mockito.doReturn(
			testrayBuild
		).when(
			baseStandaloneBuildTestrayCaseResult
		).getTestrayBuild();

		TestrayRun testrayRun = Mockito.mock(TestrayRun.class);

		Mockito.doReturn(
			testrayRun
		).when(
			baseStandaloneBuildTestrayCaseResult
		).getTestrayRun();

		Mockito.doReturn(
			Mockito.mock(TopLevelBuildReport.class)
		).when(
			baseStandaloneBuildTestrayCaseResult
		).getTopLevelBuildReport();

		Mockito.doCallRealMethod(
		).when(
			baseStandaloneBuildTestrayCaseResult
		).recordTestrayCaseResult(
			Mockito.any(Job.class)
		);

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

		Mockito.doReturn(
			Dom4JUtil.getNewElement("environments")
		).when(
			testrayRun
		).getEnvironmentsElement();

		baseStandaloneBuildTestrayCaseResult.recordTestrayCaseResult(
			Mockito.mock(Job.class));

		ArgumentCaptor<String> fileContentArgumentCaptor =
			ArgumentCaptor.forClass(String.class);

		Mockito.verify(
			testrayServer
		).writeCaseResult(
			Mockito.any(), fileContentArgumentCaptor.capture()
		);

		Document document = Dom4JUtil.parse(
			fileContentArgumentCaptor.getValue());

		Element rootElement = document.getRootElement();

		Element testcaseElement = rootElement.element("testcase");

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

	private void _testRecordTestrayCaseResult(
			int expectedLength, int nameLength)
		throws Exception {

		String name = StringUtils.repeat(RandomTestUtil.randomString(), 8);

		name = name.substring(0, nameLength);

		testEquals(name.substring(0, expectedLength), _getTestcaseName(name));
	}

}