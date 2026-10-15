/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;
import com.liferay.jenkins.results.parser.testray.TestrayBuild;
import com.liferay.jenkins.results.parser.testray.TestrayContext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import java.net.URL;

import java.util.Arrays;
import java.util.Collections;

import org.dom4j.Element;

import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Brittney Nguyen
 */
public class BaseDownstreamBuildTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetAxisName() {
		String axisVariable = RandomTestUtil.randomString();
		String jobVariant = RandomTestUtil.randomString();

		_testGetAxisName(
			axisVariable, jobVariant + "/" + axisVariable, jobVariant);

		_testGetAxisName(axisVariable, null, null);
		_testGetAxisName(null, null, jobVariant);
	}

	@Test
	public void testGetBuildReportJSONObject() throws Exception {
		BaseDownstreamBuild baseDownstreamBuild = Mockito.mock(
			BaseDownstreamBuild.class);

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getBuildReportJSONObject();

		JSONObject buildReportJSONObject =
			baseDownstreamBuild.getBuildReportJSONObject();

		Assert.assertFalse(buildReportJSONObject.has("testrayCaseResultURL"));

		URL testrayCaseResultURL = new URL(
			"https://" + RandomTestUtil.randomString());

		ReflectionTestUtil.setFieldValue(
			baseDownstreamBuild, "_testrayCaseResultURL", testrayCaseResultURL);

		buildReportJSONObject = baseDownstreamBuild.getBuildReportJSONObject();

		Assert.assertEquals(
			String.valueOf(testrayCaseResultURL),
			buildReportJSONObject.getString("testrayCaseResultURL"));
	}

	@Test
	public void testGetDisplayName() {
		BaseDownstreamBuild baseDownstreamBuild = Mockito.mock(
			BaseDownstreamBuild.class);

		String jobName = RandomTestUtil.randomString();

		Mockito.when(
			baseDownstreamBuild.getJobName()
		).thenReturn(
			jobName
		);

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getDisplayName();

		Assert.assertEquals(jobName, baseDownstreamBuild.getDisplayName());

		String axisName = RandomTestUtil.randomString();

		Mockito.when(
			baseDownstreamBuild.getAxisName()
		).thenReturn(
			axisName
		);

		Assert.assertEquals(axisName, baseDownstreamBuild.getDisplayName());
	}

	@Test
	public void testGetDownstreamBuildReport() {
		BaseDownstreamBuild cachedBaseDownstreamBuild = Mockito.mock(
			BaseDownstreamBuild.class);

		Mockito.doCallRealMethod(
		).when(
			cachedBaseDownstreamBuild
		).getDownstreamBuildReport();

		DownstreamBuildReport cachedDownstreamBuildReport = Mockito.mock(
			DownstreamBuildReport.class);

		Mockito.when(
			cachedBaseDownstreamBuild.getCachedDownstreamBuildReport()
		).thenReturn(
			cachedDownstreamBuildReport
		);

		Assert.assertSame(
			cachedDownstreamBuildReport,
			cachedBaseDownstreamBuild.getDownstreamBuildReport());

		Mockito.verify(
			cachedBaseDownstreamBuild, Mockito.never()
		).getBuildReportJSONObject();

		BaseDownstreamBuild baseDownstreamBuild = Mockito.mock(
			BaseDownstreamBuild.class);

		Mockito.when(
			baseDownstreamBuild.getBatchName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getDownstreamBuildReport();

		JSONObject buildReportJSONObject = new JSONObject();

		Mockito.when(
			baseDownstreamBuild.getBuildReportJSONObject()
		).thenReturn(
			buildReportJSONObject
		);

		Mockito.when(
			baseDownstreamBuild.getBuildURL()
		).thenReturn(
			"https://test-1-1.liferay.com/job/" +
				RandomTestUtil.randomString() + "/1/"
		);

		Mockito.when(
			baseDownstreamBuild.isCompleted()
		).thenReturn(
			false
		).thenReturn(
			true
		);

		Assert.assertNull(baseDownstreamBuild.getDownstreamBuildReport());

		DownstreamBuildReport downstreamBuildReport =
			baseDownstreamBuild.getDownstreamBuildReport();

		Assert.assertSame(
			buildReportJSONObject,
			downstreamBuildReport.getBuildReportJSONObject());
		Assert.assertSame(
			downstreamBuildReport,
			baseDownstreamBuild.getDownstreamBuildReport());
	}

	@Test
	public void testGetGitHubMessageElement() {
		BaseDownstreamBuild baseDownstreamBuild = Mockito.mock(
			BaseDownstreamBuild.class);

		Mockito.when(
			baseDownstreamBuild.getBatchName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			baseDownstreamBuild.getBuildURL()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			baseDownstreamBuild.getDisplayName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			baseDownstreamBuild.getResult()
		).thenReturn(
			"UNSTABLE"
		);

		Mockito.when(
			baseDownstreamBuild.getStatus()
		).thenReturn(
			"completed"
		);

		String uniqueFailureMarker = RandomTestUtil.randomString();
		String upstreamJobFailureMarker = RandomTestUtil.randomString();

		Element uniqueFailureElement = Dom4JUtil.getNewElement(
			"code", null, uniqueFailureMarker);
		Element upstreamJobFailureElement = Dom4JUtil.getNewElement(
			"code", null, upstreamJobFailureMarker);

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getGitHubMessageElement();

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getGitHubMessageUpstreamJobFailureElement();

		Mockito.when(
			baseDownstreamBuild.getTestResultGitHubElements(
				Mockito.anyList(), Mockito.eq(false))
		).thenReturn(
			Arrays.asList(upstreamJobFailureElement)
		);

		Mockito.when(
			baseDownstreamBuild.getTestResultGitHubElements(
				Mockito.anyList(), Mockito.eq(true))
		).thenReturn(
			Arrays.asList(uniqueFailureElement)
		);

		Element gitHubMessageElement =
			baseDownstreamBuild.getGitHubMessageElement();

		String gitHubMessageXML = gitHubMessageElement.asXML();

		Assert.assertFalse(gitHubMessageXML.contains(upstreamJobFailureMarker));
		Assert.assertTrue(gitHubMessageXML.contains(uniqueFailureMarker));

		Element gitHubMessageUpstreamJobFailureElement =
			baseDownstreamBuild.getGitHubMessageUpstreamJobFailureElement();

		String gitHubMessageUpstreamJobFailureXML =
			gitHubMessageUpstreamJobFailureElement.asXML();

		Assert.assertFalse(
			gitHubMessageUpstreamJobFailureXML.contains(uniqueFailureMarker));
		Assert.assertTrue(
			gitHubMessageUpstreamJobFailureXML.contains(
				upstreamJobFailureMarker));
	}

	@Test
	public void testGetTestrayCaseResultURL() throws Exception {
		BaseDownstreamBuild heldBaseDownstreamBuild =
			_mockGetTestrayCaseResultURL();

		URL testrayCaseResultURL = new URL(
			"https://" + RandomTestUtil.randomString());

		ReflectionTestUtil.setFieldValue(
			heldBaseDownstreamBuild, "_testrayCaseResultURL",
			testrayCaseResultURL);

		Assert.assertSame(
			testrayCaseResultURL,
			heldBaseDownstreamBuild.getTestrayCaseResultURL());

		Mockito.verify(
			heldBaseDownstreamBuild, Mockito.never()
		).getTopLevelBuild();

		mockEnvironment(
			Collections.singletonMap("TESTRAY_IMPORT_DISABLED", "true"));

		BaseDownstreamBuild disabledBaseDownstreamBuild =
			_mockGetTestrayCaseResultURL();

		Assert.assertNull(
			disabledBaseDownstreamBuild.getTestrayCaseResultURL());

		Mockito.verify(
			disabledBaseDownstreamBuild, Mockito.never()
		).getTopLevelBuild();

		mockEnvironment(Collections.<String, String>emptyMap());

		BaseDownstreamBuild baseDownstreamBuild =
			_mockGetTestrayCaseResultURL();

		Assert.assertNull(baseDownstreamBuild.getTestrayCaseResultURL());

		Mockito.verify(
			baseDownstreamBuild
		).getTopLevelBuild();

		TopLevelBuild topLevelBuild = Mockito.mock(TopLevelBuild.class);

		Mockito.when(
			baseDownstreamBuild.getTopLevelBuild()
		).thenReturn(
			topLevelBuild
		);

		Assert.assertNull(baseDownstreamBuild.getTestrayCaseResultURL());

		String message = RandomTestUtil.randomString();

		Mockito.when(
			topLevelBuild.getTestrayContext()
		).thenThrow(
			new RuntimeException(message)
		);

		PrintStream printStream = System.out;

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		System.setOut(new PrintStream(byteArrayOutputStream));

		try {
			Assert.assertNull(baseDownstreamBuild.getTestrayCaseResultURL());
		}
		finally {
			System.setOut(printStream);
		}

		String output = byteArrayOutputStream.toString();

		Assert.assertTrue(output.contains(baseDownstreamBuild.getBuildURL()));
		Assert.assertTrue(output.contains(message));

		mockEnvironment(
			Collections.singletonMap(
				"WORKSPACE", RandomTestUtil.randomString()));

		AxisTestClassGroup axisTestClassGroup = Mockito.mock(
			AxisTestClassGroup.class);

		File testBaseDir = new File(RandomTestUtil.randomString());

		Mockito.when(
			axisTestClassGroup.getTestBaseDir()
		).thenReturn(
			testBaseDir
		);

		BaseDownstreamBuild createdBaseDownstreamBuild =
			_mockGetTestrayCaseResultURL();

		Mockito.when(
			createdBaseDownstreamBuild.getAxisTestClassGroup()
		).thenReturn(
			axisTestClassGroup
		);

		DownstreamBuildReport downstreamBuildReport = Mockito.mock(
			DownstreamBuildReport.class);

		Mockito.when(
			createdBaseDownstreamBuild.getDownstreamBuildReport()
		).thenReturn(
			downstreamBuildReport
		);

		Mockito.when(
			downstreamBuildReport.getTestrayCaseResultURL()
		).thenReturn(
			testrayCaseResultURL
		);

		TestrayContext testrayContext = Mockito.mock(TestrayContext.class);

		Mockito.when(
			testrayContext.getTestrayBuild(testBaseDir)
		).thenReturn(
			Mockito.mock(TestrayBuild.class)
		);

		TopLevelBuild createdTopLevelBuild = Mockito.mock(TopLevelBuild.class);

		Mockito.when(
			createdBaseDownstreamBuild.getTopLevelBuild()
		).thenReturn(
			createdTopLevelBuild
		);

		Mockito.when(
			createdTopLevelBuild.getTestrayContext()
		).thenReturn(
			testrayContext
		);

		Mockito.when(
			createdTopLevelBuild.getTopLevelBuildReport()
		).thenReturn(
			Mockito.mock(TopLevelBuildReport.class)
		);

		Assert.assertSame(
			testrayCaseResultURL,
			createdBaseDownstreamBuild.getTestrayCaseResultURL());
	}

	@Test
	public void testReset() throws Exception {
		String axisName = RandomTestUtil.randomString();

		BaseDownstreamBuild baseDownstreamBuild = Mockito.mock(
			BaseDownstreamBuild.class);

		Mockito.when(
			baseDownstreamBuild.getAxisName()
		).thenReturn(
			axisName
		);

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).reset();

		BaseParentBuild baseParentBuild = Mockito.mock(BaseParentBuild.class);

		Mockito.when(
			baseDownstreamBuild.getParentBuild()
		).thenReturn(
			baseParentBuild
		);

		ReflectionTestUtil.setFieldValue(
			baseDownstreamBuild, "_buildJSONObject", new JSONObject());
		ReflectionTestUtil.setFieldValue(
			baseDownstreamBuild, "_downstreamBuildReport",
			Mockito.mock(DownstreamBuildReport.class));
		ReflectionTestUtil.setFieldValue(
			baseDownstreamBuild, "_testrayCaseResultURL",
			new URL("https://" + RandomTestUtil.randomString()));

		baseDownstreamBuild.reset();

		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				baseDownstreamBuild, "_buildJSONObject"));
		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				baseDownstreamBuild, "_downstreamBuildReport"));
		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				baseDownstreamBuild, "_testrayCaseResultURL"));

		Mockito.verify(
			baseParentBuild
		).removeTestrayCaseResultURL(
			axisName
		);
	}

	@Test
	public void testSaveBadBuildURLsInBuildDatabase() {
		String axisVariable = RandomTestUtil.randomString();
		String buildURL = RandomTestUtil.randomString();
		String jobVariant = RandomTestUtil.randomString();

		BaseDownstreamBuild baseDownstreamBuild = Mockito.mock(
			BaseDownstreamBuild.class);
		BuildDatabase buildDatabase = Mockito.mock(BuildDatabase.class);

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getAxisName();

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getBadBuildCount();

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getBadBuildURLs();

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).saveBuildURLInBuildDatabase();

		Mockito.when(
			baseDownstreamBuild.getAxisVariable()
		).thenReturn(
			axisVariable
		);

		Mockito.when(
			baseDownstreamBuild.getBuildDatabase()
		).thenReturn(
			buildDatabase
		);

		Mockito.when(
			baseDownstreamBuild.getBuildURL()
		).thenReturn(
			buildURL
		);

		Mockito.when(
			baseDownstreamBuild.getJobVariant()
		).thenReturn(
			jobVariant
		);

		String firstBuildURL = "https://test-1-1.liferay.com/job/test/1";

		Build.Invocation firstInvocation = new Build.Invocation(
			baseDownstreamBuild);

		firstInvocation.setBuildURL(firstBuildURL);

		ReflectionTestUtil.setFieldValue(
			baseDownstreamBuild, "_invocations",
			Arrays.asList(
				firstInvocation, new Build.Invocation(baseDownstreamBuild),
				new Build.Invocation(baseDownstreamBuild)));

		baseDownstreamBuild.saveBuildURLInBuildDatabase();

		Mockito.verify(
			buildDatabase
		).putProperty(
			BaseBuild.BAD_BUILD_URLS_PROPERTIES_KEY,
			jobVariant + "/" + axisVariable, firstBuildURL + ",unknown", false
		);
	}

	@Test
	public void testSaveBuildURLInBuildDatabase() {
		_testSaveBuildURLInBuildDatabase(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true);
		_testSaveBuildURLInBuildDatabase(
			RandomTestUtil.randomString(), null, false);
		_testSaveBuildURLInBuildDatabase(
			null, RandomTestUtil.randomString(), false);
	}

	private BaseDownstreamBuild _mockGetTestrayCaseResultURL() {
		BaseDownstreamBuild baseDownstreamBuild = Mockito.mock(
			BaseDownstreamBuild.class);

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getTestrayCaseResultURL();

		Mockito.when(
			baseDownstreamBuild.getBuildURL()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		return baseDownstreamBuild;
	}

	private void _testGetAxisName(
		String axisVariable, String expectedAxisName, String jobVariant) {

		BaseDownstreamBuild baseDownstreamBuild = Mockito.mock(
			BaseDownstreamBuild.class);

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getAxisName();

		Mockito.when(
			baseDownstreamBuild.getAxisVariable()
		).thenReturn(
			axisVariable
		);

		Mockito.when(
			baseDownstreamBuild.getJobVariant()
		).thenReturn(
			jobVariant
		);

		Assert.assertEquals(
			expectedAxisName, baseDownstreamBuild.getAxisName());
	}

	private void _testSaveBuildURLInBuildDatabase(
		String buildURL, String jobVariant, boolean saved) {

		String axisVariable = RandomTestUtil.randomString();

		BaseDownstreamBuild baseDownstreamBuild = Mockito.mock(
			BaseDownstreamBuild.class);
		BuildDatabase buildDatabase = Mockito.mock(BuildDatabase.class);

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getAxisName();

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).saveBuildURLInBuildDatabase();

		Mockito.when(
			baseDownstreamBuild.getAxisVariable()
		).thenReturn(
			axisVariable
		);

		Mockito.when(
			baseDownstreamBuild.getBuildDatabase()
		).thenReturn(
			buildDatabase
		);

		Mockito.when(
			baseDownstreamBuild.getBuildURL()
		).thenReturn(
			buildURL
		);

		Mockito.when(
			baseDownstreamBuild.getJobVariant()
		).thenReturn(
			jobVariant
		);

		baseDownstreamBuild.saveBuildURLInBuildDatabase();

		if (saved) {
			Mockito.verify(
				buildDatabase
			).putProperty(
				BaseBuild.BUILD_URLS_PROPERTIES_KEY,
				jobVariant + "/" + axisVariable, buildURL, false
			);
		}
		else {
			Mockito.verifyNoInteractions(buildDatabase);
		}
	}

}