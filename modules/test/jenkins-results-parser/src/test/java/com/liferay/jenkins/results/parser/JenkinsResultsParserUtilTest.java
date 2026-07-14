/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import java.net.InetAddress;
import java.net.ServerSocket;

import java.util.HashMap;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Peter Yoo
 */
public class JenkinsResultsParserUtilTest
	extends com.liferay.jenkins.results.parser.Test {

	@After
	public void tearDown() {
		Environment.setInstance(new Environment());
	}

	@Test(timeout = 30000)
	public void testExecuteJenkinsScriptReadTimeout() throws Exception {
		try (ServerSocket serverSocket = _createServerSocket()) {
			int port = serverSocket.getLocalPort();

			long startTime = System.currentTimeMillis();

			String result = JenkinsResultsParserUtil.executeJenkinsScript(
				"localhost:" + port, "println 'hello'", true, 2000);

			long duration = System.currentTimeMillis() - startTime;

			testEquals(null, result);

			if (duration < 1500) {
				errorCollector.addError(
					new Throwable(
						JenkinsResultsParserUtil.combine(
							"The read timeout was not reached after ",
							JenkinsResultsParserUtil.toDurationString(
								duration))));
			}
		}
	}

	@Test
	public void testExpandSlaveRange() {
		testEquals(
			"cloud-10-50-0-151,cloud-10-50-0-152,cloud-10-50-0-153," +
				"cloud-10-50-0-154,cloud-10-50-0-155,cloud-10-50-0-156",
			JenkinsResultsParserUtil.expandSlaveRange(
				"cloud-10-50-0-151..156"));
		testEquals(
			"cloud-10-50-0-47,cloud-10-50-0-0,cloud-10-50-0-1," +
				"cloud-10-50-0-2,cloud-10-50-0-49,cloud-10-50-0-50",
			JenkinsResultsParserUtil.expandSlaveRange(
				"cloud-10-50-0-47, cloud-10-50-0-0..2, cloud-10-50-0-49..50"));
	}

	@Test
	public void testFixJSON() {
		testEquals("ABC&#09;123", JenkinsResultsParserUtil.fixJSON("ABC\t123"));
		testEquals("ABC&#34;123", JenkinsResultsParserUtil.fixJSON("ABC\"123"));
		testEquals("ABC&#39;123", JenkinsResultsParserUtil.fixJSON("ABC'123"));
		testEquals("ABC&#40;123", JenkinsResultsParserUtil.fixJSON("ABC(123"));
		testEquals("ABC&#41;123", JenkinsResultsParserUtil.fixJSON("ABC)123"));
		testEquals("ABC&#60;123", JenkinsResultsParserUtil.fixJSON("ABC<123"));
		testEquals("ABC&#62;123", JenkinsResultsParserUtil.fixJSON("ABC>123"));
		testEquals("ABC&#91;123", JenkinsResultsParserUtil.fixJSON("ABC[123"));
		testEquals("ABC&#92;123", JenkinsResultsParserUtil.fixJSON("ABC\\123"));
		testEquals("ABC&#93;123", JenkinsResultsParserUtil.fixJSON("ABC]123"));
		testEquals("ABC&#123;123", JenkinsResultsParserUtil.fixJSON("ABC{123"));
		testEquals("ABC&#125;123", JenkinsResultsParserUtil.fixJSON("ABC}123"));
		testEquals(
			"ABC<br />123", JenkinsResultsParserUtil.fixJSON("ABC\n123"));
	}

	@Test
	public void testFixURL() {
		testEquals("ABC%28123", _fixURLMultipleTimes("ABC(123"));
		testEquals("ABC%29123", _fixURLMultipleTimes("ABC)123"));
		testEquals("ABC%5B123", _fixURLMultipleTimes("ABC[123"));
		testEquals("ABC%5D123", _fixURLMultipleTimes("ABC]123"));
		testEquals("!master", _fixURLMultipleTimes("!master"));
		testEquals("0%201%202", _fixURLMultipleTimes("0 1 2"));
		testEquals(
			"https://test-1-1.liferay.com/job(master)?" +
				"AXIS_VARIABLE=0%201&label_exp=!master&job=test%287.2.x%29",
			_fixURLMultipleTimes(
				"https://test-1-1.liferay.com/job(master)?" +
					"AXIS_VARIABLE=0 1&label_exp=!master&job=test(7.2.x)"));
	}

	@Test
	public void testGetCohortName() {
		Environment environment = mockEnvironment();

		Mockito.when(
			environment.doGet("JENKINS_URL")
		).thenReturn(
			"https://test-1-1.liferay.com"
		);

		testEquals("test-1", JenkinsResultsParserUtil.getCohortName());
	}

	@Test
	public void testGetJobVariant() {
		String jobVariant = RandomTestUtil.randomString();

		_testGetJobVariant(jobVariant, "JOB_VARIANT", jobVariant);
		_testGetJobVariant("", "JENKINS_GITHUB_BRANCH_NAME", jobVariant);
	}

	@Test
	public void testGetLocalURL() {
		testEquals(
			"http://test-1-20/ABC?123=456&xyz=abc",
			JenkinsResultsParserUtil.getLocalURL(
				"https://test-1-20.liferay.com/ABC?123=456&xyz=abc"));
		testEquals(
			"http://test-4-1/ABC?123=456&xyz=abc",
			JenkinsResultsParserUtil.getLocalURL(
				"http://test-4-1/ABC?123=456&xyz=abc"));
		testEquals(
			"http://mirrors.lax.liferay.com/files.liferay.com/private/",
			JenkinsResultsParserUtil.getLocalURL(
				"http://mirrors.lax.liferay.com/files.liferay.com/private/"));
		testEquals(
			"http://mirrors.lax.liferay.com/files.liferay.com/private/",
			JenkinsResultsParserUtil.getLocalURL(
				"http://mirrors.dlc.liferay.com/files.liferay.com/private/"));
		testEquals(
			"http://mirrors.lax.liferay.com/files.liferay.com/private/",
			JenkinsResultsParserUtil.getLocalURL(
				"http://mirrors/files.liferay.com/private/"));
		testEquals(
			"http://mirrors.lax.liferay.com/files.liferay.com/private/",
			JenkinsResultsParserUtil.getLocalURL(
				"https://files.liferay.com/private/"));
		testEquals(
			"http://mirrors.lax.liferay.com/releases.liferay.com/portal/",
			JenkinsResultsParserUtil.getLocalURL(
				"http://mirrors.lax.liferay.com/releases.liferay.com/portal/"));
		testEquals(
			"http://mirrors.lax.liferay.com/releases.liferay.com/portal/",
			JenkinsResultsParserUtil.getLocalURL(
				"http://mirrors.dlc.liferay.com/releases.liferay.com/portal/"));
		testEquals(
			"http://mirrors.lax.liferay.com/releases.liferay.com/portal/",
			JenkinsResultsParserUtil.getLocalURL(
				"http://mirrors/releases.liferay.com/portal/"));
		testEquals(
			"http://mirrors.lax.liferay.com/releases.liferay.com/portal/",
			JenkinsResultsParserUtil.getLocalURL(
				"https://releases.liferay.com/portal/"));
	}

	@Test
	public void testGetProperty() {
		Properties properties = new Properties();

		properties.setProperty("base", "0");
		properties.setProperty("base[opt0]", "1");
		properties.setProperty("base[opt0][opt2]", "2");
		properties.setProperty("base[opt0][opt3]", "3");
		properties.setProperty("base[opt1]", "4");
		properties.setProperty("base0[opt[0]]", "5");
		properties.setProperty("base0[opt[1][1][1]]", "6");
		properties.setProperty("base0[opt[1][1][1]][opt[2][2][2]]", "7");
		properties.setProperty("base1[opt1]", "8");
		properties.setProperty("base1[opt1][opt2]", "");

		_testGetProperty("0", properties, "base");
		_testGetProperty(null, properties, "invalid");
		_testGetProperty("1", properties, "base", "opt0", "invalid");
		_testGetProperty("2", properties, "base[opt0]", "opt2");
		_testGetProperty("3", properties, "base", "opt0", "opt3");
		_testGetProperty("4", properties, "base", "opt1", null, "invalid");
		_testGetProperty("5", properties, "base0", "opt[0]");
		_testGetProperty("6", properties, "base0", "opt[1][1][1]", "invalid");
		_testGetProperty(
			"7", properties, "base0", "opt[2][2][2]", "invalid", "opt[1][1][1]",
			null);
		_testGetProperty("", properties, "base1", "opt1", "opt2");

		testEquals(
			"1",
			JenkinsResultsParserUtil.getProperty(properties, "base[opt0]"));
		testEquals(
			"1",
			JenkinsResultsParserUtil.getProperty(
				properties, "base[opt0]", true, "invalid"));
		testEquals(
			null,
			JenkinsResultsParserUtil.getProperty(
				properties, "base[opt0]", false, "invalid"));
	}

	@Test
	public void testGetPropertyName() {
		Properties properties = new Properties();

		properties.setProperty("base", "0");
		properties.setProperty("base[opt0]", "1");
		properties.setProperty("base[opt0][opt2]", "2");
		properties.setProperty("base[opt0][opt3]", "3");
		properties.setProperty("base[opt1]", "4");
		properties.setProperty("base0[opt[0]]", "5");
		properties.setProperty("base0[opt[1][1][1]]", "6");
		properties.setProperty("base0[opt[1][1][1]][opt[2][2][2]]", "7");
		properties.setProperty("base1[opt1]", "8");
		properties.setProperty("base1[opt1][opt2]", "");

		_testGetPropertyName("base", "0", properties, "base");
		_testGetPropertyName("invalid", null, properties, "invalid");
		_testGetPropertyName(
			"base[opt0]", "1", properties, "base", "opt0", "invalid");
		_testGetPropertyName(
			"base[opt0][opt2]", "2", properties, "base[opt0]", "opt2");
		_testGetPropertyName(
			"base[opt0][opt3]", "3", properties, "base", "opt0", "opt3");
		_testGetPropertyName(
			"base[opt1]", "4", properties, "base", "opt1", null, "invalid");
		_testGetPropertyName(
			"base0[opt[0]]", "5", properties, "base0", "opt[0]");
		_testGetPropertyName(
			"base0[opt[1][1][1]]", "6", properties, "base0", "opt[1][1][1]",
			"invalid");
		_testGetPropertyName(
			"base0[opt[1][1][1]][opt[2][2][2]]", "7", properties, "base0",
			"opt[2][2][2]", "invalid", "opt[1][1][1]", null);
		_testGetPropertyName(
			"base1[opt1][opt2]", "", properties, "base1", "opt1", "opt2");
	}

	@Test
	public void testGetPropertyNameWithWildcards() {
		Properties properties = new Properties();

		properties.setProperty("build.caching.enabled", "false");
		properties.setProperty(
			"build.caching.enabled[test-portal-acceptance-pullrequest(*)]",
			"true");
		properties.setProperty(
			"build.caching.enabled[test-portal-acceptance-pullrequest(master)]",
			"false");

		_testGetPropertyName(
			"build.caching.enabled", "false", properties,
			"build.caching.enabled", "test-portal-source-format");
		_testGetPropertyName(
			"build.caching.enabled[test-portal-acceptance-pullrequest(*)]",
			"true", properties, "build.caching.enabled",
			"test-portal-acceptance-pullrequest(ee-7.4.x)");
		_testGetPropertyName(
			"build.caching.enabled[test-portal-acceptance-pullrequest(master)]",
			"false", properties, "build.caching.enabled",
			"test-portal-acceptance-pullrequest(master)");
	}

	@Test
	public void testGetPropertyWithBuildAwsProperties() {
		Properties properties = _getBuildAwsProperties();

		_testGetProperty(
			"false", properties, "binaries.cache.enabled",
			"forward-pullrequest");
		_testGetProperty(
			"true", properties, "binaries.cache.enabled",
			"test-portal-release");
		_testGetProperty(
			"false", properties, "binaries.cache.enabled",
			"test-portal-source-format");
		_testGetProperty(
			"false", properties, "build.caching.enabled",
			"forward-pullrequest");
		_testGetProperty(
			"true", properties, "build.caching.enabled",
			"test-portal-fixpack-release");
		_testGetProperty(
			"true", properties, "build.caching.enabled",
			"test-portal-hotfix-release");
		_testGetProperty(
			"true", properties, "build.caching.enabled", "test-portal-release");
		_testGetProperty(
			"false", properties, "build.caching.enabled",
			"test-portal-source-format");
		_testGetProperty(
			"false", properties, "git.archive.enabled", "forward-pullrequest");
		_testGetProperty(
			"true", properties, "git.archive.enabled", "test-portal-release");
		_testGetProperty(
			"false", properties, "git.archive.enabled",
			"test-portal-source-format");
	}

	@Test
	public void testGetRemoteURL() {
		testEquals(
			"https://test-1-20.liferay.com/ABC?123=456&xyz=abc",
			JenkinsResultsParserUtil.getRemoteURL(
				"http://test-1-20/ABC?123=456&xyz=abc"));
		testEquals(
			"https://test-4-1.liferay.com/ABC?123=456&xyz=abc",
			JenkinsResultsParserUtil.getRemoteURL(
				"https://test-4-1.liferay.com/ABC?123=456&xyz=abc"));
		testEquals(
			"https://files.liferay.com/private/",
			JenkinsResultsParserUtil.getRemoteURL(
				"http://mirrors.lax.liferay.com/files.liferay.com/private/"));
		testEquals(
			"https://files.liferay.com/private/",
			JenkinsResultsParserUtil.getRemoteURL(
				"http://mirrors.dlc.liferay.com/files.liferay.com/private/"));
		testEquals(
			"https://files.liferay.com/private/",
			JenkinsResultsParserUtil.getRemoteURL(
				"http://mirrors/files.liferay.com/private/"));
		testEquals(
			"https://files.liferay.com/private/",
			JenkinsResultsParserUtil.getRemoteURL(
				"https://files.liferay.com/private/"));
		testEquals(
			"https://releases.liferay.com/portal/",
			JenkinsResultsParserUtil.getRemoteURL(
				"http://mirrors.lax.liferay.com/releases.liferay.com/portal/"));
		testEquals(
			"https://releases.liferay.com/portal/",
			JenkinsResultsParserUtil.getRemoteURL(
				"http://mirrors.dlc.liferay.com/releases.liferay.com/portal/"));
		testEquals(
			"https://releases.liferay.com/portal/",
			JenkinsResultsParserUtil.getRemoteURL(
				"http://mirrors/releases.liferay.com/portal/"));
		testEquals(
			"https://releases.liferay.com/portal/",
			JenkinsResultsParserUtil.getRemoteURL(
				"https://releases.liferay.com/portal/"));
	}

	@Test(timeout = 30000)
	public void testInvokeJenkinsBuildReadTimeout() throws Exception {
		try (ServerSocket serverSocket = _createServerSocket()) {
			JenkinsMaster jenkinsMaster = Mockito.mock(JenkinsMaster.class);

			Mockito.when(
				jenkinsMaster.getRemoteURL()
			).thenReturn(
				"http://localhost:" + serverSocket.getLocalPort() + "/"
			);

			long startTime = System.currentTimeMillis();

			try {
				JenkinsResultsParserUtil.invokeJenkinsBuild(
					jenkinsMaster, "test-job", new HashMap<>(), 2000);

				errorCollector.addError(
					new Throwable("A RuntimeException was not thrown"));
			}
			catch (RuntimeException runtimeException) {
			}

			long duration = System.currentTimeMillis() - startTime;

			if (duration < 1500) {
				errorCollector.addError(
					new Throwable(
						JenkinsResultsParserUtil.combine(
							"The read timeout was not reached after ",
							JenkinsResultsParserUtil.toDurationString(
								duration))));
			}
		}
	}

	@Test
	public void testIsBuildCachingEnabledCloudCINode() {
		Environment environment = mockEnvironment();

		JenkinsResultsParserUtil.clearCache();

		Mockito.when(
			environment.doGet("BUILD_CACHING_ENABLED")
		).thenReturn(
			"true"
		);

		Mockito.when(
			environment.doGet("MASTER_NETWORK_NAME")
		).thenReturn(
			"aws-network"
		);

		Assert.assertTrue(
			JenkinsResultsParserUtil.isBuildCachingEnabled(
				"test-portal-release", "default"));

		Mockito.when(
			environment.doGet("BUILD_CACHING_ENABLED")
		).thenReturn(
			"false"
		);

		Assert.assertFalse(
			JenkinsResultsParserUtil.isBuildCachingEnabled(
				"test-portal-release", "default"));
	}

	@Test
	public void testIsBuildCachingEnabledNonCINode() {
		Environment environment = mockEnvironment();

		JenkinsResultsParserUtil.clearCache();

		Mockito.when(
			environment.doGet("BUILD_CACHING_ENABLED")
		).thenReturn(
			"true"
		);

		Assert.assertFalse(
			JenkinsResultsParserUtil.isBuildCachingEnabled(
				"test-portal-release", "default"));
	}

	@Test
	public void testIsJSONArrayEqual() {
		JSONArray expectedJSONArray = new JSONArray();

		expectedJSONArray.put(true);
		expectedJSONArray.put(1.1);
		expectedJSONArray.put(1);
		expectedJSONArray.put("value");

		JSONArray actualJSONArray = new JSONArray();

		actualJSONArray.put(true);
		actualJSONArray.put(1.1);
		actualJSONArray.put(1);
		actualJSONArray.put("value");

		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"boolean", true
		).put(
			"double", 1.1
		).put(
			"int", 1
		).put(
			"string", "value"
		);

		expectedJSONArray.put(jsonObject);
		actualJSONArray.put(jsonObject);

		JSONArray jsonArray = new JSONArray();

		jsonArray.put(true);
		jsonArray.put(1.1);
		jsonArray.put(1);
		jsonArray.put("value");

		expectedJSONArray.put(jsonArray);
		actualJSONArray.put(jsonArray);

		if (!JenkinsResultsParserUtil.isJSONArrayEqual(
				expectedJSONArray, actualJSONArray)) {

			errorCollector.addError(
				new Throwable(
					JenkinsResultsParserUtil.combine(
						"Expected does not match actual\nExpected: ",
						expectedJSONArray.toString(), "\nActual:   ",
						actualJSONArray.toString())));
		}

		actualJSONArray.put("string2");

		if (JenkinsResultsParserUtil.isJSONArrayEqual(
				expectedJSONArray, actualJSONArray)) {

			errorCollector.addError(
				new Throwable(
					JenkinsResultsParserUtil.combine(
						"Expected should not match actual\nExpected: ",
						expectedJSONArray.toString(), "\nActual:   ",
						actualJSONArray.toString())));
		}
	}

	@Test
	public void testIsJSONObjectEqual() {
		JSONObject expectedJSONObject = new JSONObject();

		expectedJSONObject.put(
			"boolean", true
		).put(
			"double", 1.1
		).put(
			"int", 1
		).put(
			"string", "value"
		);

		JSONObject actualJSONObject = new JSONObject();

		actualJSONObject.put(
			"boolean", true
		).put(
			"double", 1.1
		).put(
			"int", 1
		).put(
			"string", "value"
		);

		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"boolean", true
		).put(
			"double", 1.1
		).put(
			"int", 1
		).put(
			"string", "value"
		);

		expectedJSONObject.put("json_object", jsonObject);
		actualJSONObject.put("json_object", jsonObject);

		JSONArray jsonArray = new JSONArray();

		jsonArray.put(true);
		jsonArray.put(1.1);
		jsonArray.put(1);
		jsonArray.put("value");

		expectedJSONObject.put("json_array", jsonArray);
		actualJSONObject.put("json_array", jsonArray);

		if (!JenkinsResultsParserUtil.isJSONObjectEqual(
				expectedJSONObject, actualJSONObject)) {

			errorCollector.addError(
				new Throwable(
					JenkinsResultsParserUtil.combine(
						"Expected does not match actual\nExpected: ",
						expectedJSONObject.toString(), "\nActual:   ",
						actualJSONObject.toString())));
		}

		actualJSONObject.put("string", "value2");

		if (JenkinsResultsParserUtil.isJSONObjectEqual(
				expectedJSONObject, actualJSONObject)) {

			errorCollector.addError(
				new Throwable(
					JenkinsResultsParserUtil.combine(
						"Expected should not match actual\nExpected: ",
						expectedJSONObject.toString(), "\nActual:   ",
						actualJSONObject.toString())));
		}
	}

	protected Environment mockEnvironment() {
		Environment environment = Mockito.mock(Environment.class);

		Environment.setInstance(environment);

		return environment;
	}

	private ServerSocket _createServerSocket() throws Exception {
		return new ServerSocket(0, 1, InetAddress.getByName("localhost"));
	}

	private String _fixURLMultipleTimes(String urlString) {
		return JenkinsResultsParserUtil.fixURL(
			JenkinsResultsParserUtil.fixURL(
				JenkinsResultsParserUtil.fixURL(urlString)));
	}

	private Properties _getBuildAwsProperties() {
		File jenkinsRepositoryDir =
			JenkinsResultsParserUtil.getJenkinsRepositoryDir();

		File buildAwsPropertiesFile = new File(
			jenkinsRepositoryDir, "commands/build-aws.properties");

		Assume.assumeTrue(
			JenkinsResultsParserUtil.getCanonicalPath(buildAwsPropertiesFile) +
				" does not exist",
			buildAwsPropertiesFile.exists());

		return JenkinsResultsParserUtil.getProperties(buildAwsPropertiesFile);
	}

	private void _testGetJobVariant(
		String expectedJobVariant, String name, String value) {

		JSONObject parameterJSONObject = new JSONObject();

		parameterJSONObject.put(
			"name", name
		).put(
			"value", value
		);

		JSONObject actionJSONObject = new JSONObject();

		actionJSONObject.put(
			"parameters",
			new JSONArray(
			).put(
				parameterJSONObject
			));

		JSONObject jsonObject = new JSONObject();

		jsonObject.put(
			"actions",
			new JSONArray(
			).put(
				actionJSONObject
			));

		testEquals(
			expectedJobVariant,
			JenkinsResultsParserUtil.getJobVariant(jsonObject));
	}

	private void _testGetProperty(
		String expectedPropertyValue, Properties properties,
		String basePropertyName, String... propertyOpts) {

		testEquals(
			expectedPropertyValue,
			JenkinsResultsParserUtil.getProperty(
				properties, basePropertyName, propertyOpts));
	}

	private void _testGetPropertyName(
		String expectedPropertyName, String expectedPropertyValue,
		Properties properties, String basePropertyName,
		String... propertyOpts) {

		String actualPropertyName = JenkinsResultsParserUtil.getPropertyName(
			properties, basePropertyName, propertyOpts);

		testEquals(expectedPropertyName, actualPropertyName);

		testEquals(
			expectedPropertyValue,
			JenkinsResultsParserUtil.getProperty(
				properties, actualPropertyName));
	}

}