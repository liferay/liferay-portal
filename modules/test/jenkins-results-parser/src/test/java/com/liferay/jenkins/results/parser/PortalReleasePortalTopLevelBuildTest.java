/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Brittney Nguyen
 */
public class PortalReleasePortalTopLevelBuildTest
	extends com.liferay.jenkins.results.parser.Test {

	@After
	@Override
	public void tearDown() {
		super.tearDown();

		Map<String, PortalRelease> portalReleases =
			ReflectionTestUtil.getFieldValue(
				PortalReleaseFactory.class, "_portalReleases");

		portalReleases.clear();
	}

	@Test
	public void testGetPortalReleaseNotStandalone() throws Exception {
		_testGetPortalReleaseNotStandalone("");
		_testGetPortalReleaseNotStandalone("false");
		_testGetPortalReleaseNotStandalone(null);
	}

	@Test
	public void testGetPortalReleaseStandalone() throws Exception {
		Properties buildProperties = new Properties();

		String[] propertyNames = {
			"plugins.war.zip.url", "portal.bundle.glassfish",
			"portal.bundle.jboss", "portal.bundle.tomcat",
			"portal.bundle.wildfly", "portal.dependencies.zip.url",
			"portal.osgi.zip.url", "portal.sql.zip.url", "portal.tools.zip.url",
			"portal.war.url"
		};

		for (String propertyName : propertyNames) {
			buildProperties.setProperty(
				propertyName + "[7.4.13.u154]",
				"https://" + RandomTestUtil.randomString());
		}

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		BuildDatabaseUtil.setBuildDatabase(Mockito.mock(BuildDatabase.class));

		BuildDatabase buildDatabase = Mockito.mock(BuildDatabase.class);

		PortalReleasePortalTopLevelBuild portalReleasePortalTopLevelBuild =
			_mockPortalReleasePortalTopLevelBuild(buildDatabase);

		Mockito.doReturn(
			"true"
		).when(
			portalReleasePortalTopLevelBuild
		).getParameterValue(
			"TEST_PORTAL_RELEASE_STANDALONE"
		);

		String tomcatURLString = JenkinsResultsParserUtil.combine(
			"https://", RandomTestUtil.randomString(),
			"/liferay-dxp-tomcat-7.4.13-u154-cms-standalone-1791273379.7z");

		Mockito.doReturn(
			tomcatURLString
		).when(
			portalReleasePortalTopLevelBuild
		).getParameterValue(
			"TEST_PORTAL_RELEASE_TOMCAT_URL"
		);

		PortalRelease portalRelease =
			portalReleasePortalTopLevelBuild.getPortalRelease();

		testSame(
			portalRelease, portalReleasePortalTopLevelBuild.getPortalRelease());

		Mockito.verify(
			buildDatabase
		).putPortalRelease(
			"7.4.13-u154", portalRelease
		);

		testEquals(
			tomcatURLString,
			String.valueOf(portalRelease.getPortalBundleTomcatURL()));
		testEquals(true, portalRelease.isStandalone());

		JSONObject portalReleaseJSONObject = portalRelease.getJSONObject();

		String[] urlFieldNames = {
			"plugins_war_zip_url_string", "portal_bundle_glassfish_url_string",
			"portal_bundle_jboss_url_string",
			"portal_bundle_wildfly_url_string",
			"portal_dependencies_zip_url_string", "portal_osgi_zip_url_string",
			"portal_sql_zip_url_string", "portal_tools_zip_url_string",
			"portal_war_url_string"
		};

		for (String urlFieldName : urlFieldNames) {
			testEquals(
				null, portalReleaseJSONObject.optString(urlFieldName, null));
		}
	}

	@Test
	public void testGetPortalReleaseStandaloneFailure() {
		String[] releaseFileParameterNames = {
			"TEST_PORTAL_RELEASE_DEPENDENCIES_URL",
			"TEST_PORTAL_RELEASE_OSGI_URL", "TEST_PORTAL_RELEASE_SQL_URL",
			"TEST_PORTAL_RELEASE_TOOLS_URL", "TEST_PORTAL_RELEASE_WAR_URL"
		};
		String tomcatURLString = JenkinsResultsParserUtil.combine(
			"https://", RandomTestUtil.randomString(),
			"/liferay-dxp-tomcat-7.4.13-u154-cms-standalone-1791273379.7z");

		_testGetPortalReleaseStandaloneFailure(
			JenkinsResultsParserUtil.combine(
				"A standalone release takes only ",
				"TEST_PORTAL_RELEASE_TOMCAT_URL. Remove ",
				JenkinsResultsParserUtil.join(", ", releaseFileParameterNames),
				"."),
			releaseFileParameterNames, tomcatURLString);

		for (String releaseFileParameterName : releaseFileParameterNames) {
			_testGetPortalReleaseStandaloneFailure(
				JenkinsResultsParserUtil.combine(
					"A standalone release takes only ",
					"TEST_PORTAL_RELEASE_TOMCAT_URL. Remove ",
					releaseFileParameterName, "."),
				new String[] {releaseFileParameterName}, tomcatURLString);
		}

		_testGetPortalReleaseStandaloneFailure(
			"TEST_PORTAL_RELEASE_TOMCAT_URL is required when " +
				"TEST_PORTAL_RELEASE_STANDALONE is true",
			new String[0], RandomTestUtil.randomString());
		_testGetPortalReleaseStandaloneFailure(
			"TEST_PORTAL_RELEASE_TOMCAT_URL is required when " +
				"TEST_PORTAL_RELEASE_STANDALONE is true",
			new String[0], null);

		String regularTomcatURLString = JenkinsResultsParserUtil.combine(
			"https://", RandomTestUtil.randomString(),
			"/liferay-dxp-tomcat-7.4.13-u154-1791054167.7z");

		_testGetPortalReleaseStandaloneFailure(
			JenkinsResultsParserUtil.combine(
				"Invalid standalone bundle URL ", regularTomcatURLString,
				". The file name must match liferay-dxp-tomcat-<version>-",
				"<build profile>-<timestamp>.(7z|tar.gz|zip), where the ",
				"build profile ends in -standalone."),
			new String[0], regularTomcatURLString);
	}

	private PortalReleasePortalTopLevelBuild
		_mockPortalReleasePortalTopLevelBuild(BuildDatabase buildDatabase) {

		PortalReleasePortalTopLevelBuild portalReleasePortalTopLevelBuild =
			Mockito.mock(PortalReleasePortalTopLevelBuild.class);

		Mockito.doReturn(
			buildDatabase
		).when(
			portalReleasePortalTopLevelBuild
		).getBuildDatabase();

		Mockito.doCallRealMethod(
		).when(
			portalReleasePortalTopLevelBuild
		).getPortalRelease();

		return portalReleasePortalTopLevelBuild;
	}

	private void _testGetPortalReleaseNotStandalone(String standalone)
		throws Exception {

		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"portal.bundle.tomcat[7.4.13.u154]",
			"https://" + RandomTestUtil.randomString());

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		BuildDatabaseUtil.setBuildDatabase(Mockito.mock(BuildDatabase.class));

		PortalReleasePortalTopLevelBuild portalReleasePortalTopLevelBuild =
			_mockPortalReleasePortalTopLevelBuild(
				Mockito.mock(BuildDatabase.class));

		String osgiURLString = "https://" + RandomTestUtil.randomString();

		Mockito.doReturn(
			osgiURLString
		).when(
			portalReleasePortalTopLevelBuild
		).getParameterValue(
			"TEST_PORTAL_RELEASE_OSGI_URL"
		);

		Mockito.doReturn(
			standalone
		).when(
			portalReleasePortalTopLevelBuild
		).getParameterValue(
			"TEST_PORTAL_RELEASE_STANDALONE"
		);

		String tomcatURLString = JenkinsResultsParserUtil.combine(
			"https://", RandomTestUtil.randomString(),
			"/liferay-dxp-tomcat-7.4.13-u154-1791054167.7z");

		Mockito.doReturn(
			tomcatURLString
		).when(
			portalReleasePortalTopLevelBuild
		).getParameterValue(
			"TEST_PORTAL_RELEASE_TOMCAT_URL"
		);

		PortalRelease portalRelease =
			portalReleasePortalTopLevelBuild.getPortalRelease();

		testEquals(false, portalRelease.isStandalone());
		testEquals(
			osgiURLString, String.valueOf(portalRelease.getPortalOSGiZipURL()));
		testEquals(
			tomcatURLString,
			String.valueOf(portalRelease.getPortalBundleTomcatURL()));
	}

	private void _testGetPortalReleaseStandaloneFailure(
		String expectedMessage, String[] releaseFileParameterNames,
		String tomcatURLString) {

		PortalReleasePortalTopLevelBuild portalReleasePortalTopLevelBuild =
			_mockPortalReleasePortalTopLevelBuild(
				Mockito.mock(BuildDatabase.class));

		for (String releaseFileParameterName : releaseFileParameterNames) {
			Mockito.doReturn(
				"https://" + RandomTestUtil.randomString()
			).when(
				portalReleasePortalTopLevelBuild
			).getParameterValue(
				releaseFileParameterName
			);
		}

		Mockito.doReturn(
			"true"
		).when(
			portalReleasePortalTopLevelBuild
		).getParameterValue(
			"TEST_PORTAL_RELEASE_STANDALONE"
		);

		Mockito.doReturn(
			tomcatURLString
		).when(
			portalReleasePortalTopLevelBuild
		).getParameterValue(
			"TEST_PORTAL_RELEASE_TOMCAT_URL"
		);

		try {
			portalReleasePortalTopLevelBuild.getPortalRelease();

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			testEquals(expectedMessage, runtimeException.getMessage());
		}
	}

}