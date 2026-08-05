/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Kenji Heigel
 */
public class BaseBuildTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testBuildDisplayNameComparator() {
		BaseDownstreamBuild firstBaseDownstreamBuild = _mockDownstreamBuild(
			"mock-downstream-1");
		BaseDownstreamBuild lastBaseDownstreamBuild = _mockDownstreamBuild(
			"mock-downstream-2");

		List<Build> builds = new ArrayList<>(
			Arrays.asList(lastBaseDownstreamBuild, firstBaseDownstreamBuild));

		Collections.sort(builds, new BaseBuild.BuildDisplayNameComparator());

		Assert.assertEquals(
			Arrays.asList(firstBaseDownstreamBuild, lastBaseDownstreamBuild),
			builds);
	}

	@Test
	public void testGetBadBuildURLs() {
		BaseBuild baseBuild = Mockito.mock(BaseBuild.class);

		Build.Invocation firstInvocation = new Build.Invocation(baseBuild);

		String firstBuildURL = "https://test-1-1.liferay.com/job/test/1";

		firstInvocation.setBuildURL(firstBuildURL);

		Build.Invocation lastInvocation = new Build.Invocation(baseBuild);

		lastInvocation.setBuildURL("https://test-1-1.liferay.com/job/test/3");

		ReflectionTestUtil.setFieldValue(
			baseBuild, "_invocations",
			Arrays.asList(
				firstInvocation, new Build.Invocation(baseBuild),
				lastInvocation));

		Mockito.doCallRealMethod(
		).when(
			baseBuild
		).getBadBuildURLs();

		Assert.assertEquals(
			Arrays.asList(firstBuildURL), baseBuild.getBadBuildURLs());
	}

	@Test
	public void testLoadParametersFromQueryString() {
		Map<String, String> parameters = _loadParametersFromQueryString(
			JenkinsResultsParserUtil.combine(
				"token=abc123&PORTAL_BATCH_TEST_SELECTOR=PortalSmoke%23Smoke",
				"&TESTRAY_PROJECT_NAME=AWS%20%26%20CI",
				"&PORTAL_BUILD_NOTES=100%25%20pass&PORTAL_UPSTREAM=master",
				"&AXIS_VARIABLE=&PORTAL_QUERY=a%3Db",
				"&PORTAL%20BUILD%20NOTES=encoded%20name"));

		Assert.assertEquals("", parameters.get("AXIS_VARIABLE"));
		Assert.assertEquals(
			"PortalSmoke#Smoke", parameters.get("PORTAL_BATCH_TEST_SELECTOR"));
		Assert.assertEquals("100% pass", parameters.get("PORTAL_BUILD_NOTES"));
		Assert.assertEquals("a=b", parameters.get("PORTAL_QUERY"));
		Assert.assertEquals("master", parameters.get("PORTAL_UPSTREAM"));
		Assert.assertEquals(
			"encoded name", parameters.get("PORTAL BUILD NOTES"));
		Assert.assertEquals("AWS & CI", parameters.get("TESTRAY_PROJECT_NAME"));
	}

	@Test
	public void testLoadParametersFromQueryStringWithIllegalEscape() {
		Map<String, String> parameters = _loadParametersFromQueryString(
			"PORTAL_BUILD_NOTES=100% pass&PORTAL_UPSTREAM=master");

		Assert.assertEquals("100% pass", parameters.get("PORTAL_BUILD_NOTES"));
		Assert.assertEquals("master", parameters.get("PORTAL_UPSTREAM"));
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

	@Test
	public void testSetInvocationURL() throws Exception {
		BaseBuild baseBuild = Mockito.mock(BaseBuild.class);

		ReflectionTestUtil.setFieldValue(
			baseBuild, "_parameters", new HashMap<String, String>());

		Mockito.doCallRealMethod(
		).when(
			baseBuild
		).loadParametersFromQueryString(
			Mockito.anyString()
		);

		Mockito.doCallRealMethod(
		).when(
			baseBuild
		).setJobName(
			Mockito.anyString()
		);

		Method method = BaseBuild.class.getDeclaredMethod(
			"_setInvocationURL", String.class);

		method.setAccessible(true);

		method.invoke(
			baseBuild,
			JenkinsResultsParserUtil.combine(
				"https://test-1.liferay.com/job/",
				"test-portal-acceptance-pullrequest%28master%29",
				"/buildWithParameters?",
				"PORTAL_BATCH_TEST_SELECTOR=PortalSmoke%23Smoke",
				"&TESTRAY_PROJECT_NAME=AWS%20%26%20CI",
				"&PORTAL_BUILD_NOTES=100%25%20pass"));

		Map<String, String> parameters = ReflectionTestUtil.getFieldValue(
			baseBuild, "_parameters");

		Assert.assertEquals(
			"PortalSmoke#Smoke", parameters.get("PORTAL_BATCH_TEST_SELECTOR"));
		Assert.assertEquals("100% pass", parameters.get("PORTAL_BUILD_NOTES"));
		Assert.assertEquals("AWS & CI", parameters.get("TESTRAY_PROJECT_NAME"));

		Assert.assertEquals(
			"test-portal-acceptance-pullrequest(master)",
			ReflectionTestUtil.getFieldValue(baseBuild, "_jobName"));
	}

	private Map<String, String> _loadParametersFromQueryString(
		String queryString) {

		BaseBuild baseBuild = Mockito.mock(BaseBuild.class);

		ReflectionTestUtil.setFieldValue(
			baseBuild, "_parameters", new HashMap<String, String>());

		Mockito.doCallRealMethod(
		).when(
			baseBuild
		).loadParametersFromQueryString(
			Mockito.anyString()
		);

		baseBuild.loadParametersFromQueryString(queryString);

		return ReflectionTestUtil.getFieldValue(baseBuild, "_parameters");
	}

	private BaseDownstreamBuild _mockDownstreamBuild(String jobName) {
		BaseDownstreamBuild baseDownstreamBuild = Mockito.mock(
			BaseDownstreamBuild.class);

		Mockito.when(
			baseDownstreamBuild.getJobName()
		).thenReturn(
			jobName
		);

		Mockito.doCallRealMethod(
		).when(
			baseDownstreamBuild
		).getDisplayName();

		return baseDownstreamBuild;
	}

	private void _testSaveBuildURLInBuildDatabase(
		String buildURL, String jobVariant, boolean saved) {

		BaseBuild baseBuild = Mockito.mock(BaseBuild.class);
		BuildDatabase buildDatabase = Mockito.mock(BuildDatabase.class);

		Mockito.doCallRealMethod(
		).when(
			baseBuild
		).saveBuildURLInBuildDatabase();

		Mockito.when(
			baseBuild.getBuildDatabase()
		).thenReturn(
			buildDatabase
		);

		Mockito.when(
			baseBuild.getBuildURL()
		).thenReturn(
			buildURL
		);

		Mockito.when(
			baseBuild.getJobVariant()
		).thenReturn(
			jobVariant
		);

		baseBuild.saveBuildURLInBuildDatabase();

		if (saved) {
			Mockito.verify(
				buildDatabase
			).putProperty(
				BaseBuild.BUILD_URLS_PROPERTIES_KEY, jobVariant, buildURL, false
			);
		}
		else {
			Mockito.verifyNoInteractions(buildDatabase);
		}
	}

}