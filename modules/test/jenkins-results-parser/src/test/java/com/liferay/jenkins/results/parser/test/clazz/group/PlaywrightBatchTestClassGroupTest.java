/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.AntException;
import com.liferay.jenkins.results.parser.AntUtil;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.NotificationUtil;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.ReflectionTestUtil;
import com.liferay.jenkins.results.parser.Shell;
import com.liferay.jenkins.results.parser.test.clazz.PlaywrightTestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;

import java.io.File;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class PlaywrightBatchTestClassGroupTest
	extends com.liferay.jenkins.results.parser.Test {

	@After
	@Override
	public void tearDown() {
		super.tearDown();

		AtomicBoolean playwrightJSONObjectsLoaded =
			ReflectionTestUtil.getFieldValue(
				PlaywrightBatchTestClassGroup.class,
				"_playwrightJSONObjectsLoaded");

		playwrightJSONObjectsLoaded.set(false);

		ReflectionTestUtil.setFieldValue(
			PlaywrightBatchTestClassGroup.class, "_playwrightJSONObject", null);
		ReflectionTestUtil.setFieldValue(
			PlaywrightBatchTestClassGroup.class,
			"_playwrightJSONObjectRuntimeException", null);
	}

	@Test
	public void testGetPlaywrightJSONObjectMissingReport() throws Exception {
		PlaywrightBatchTestClassGroup playwrightBatchTestClassGroup =
			Mockito.mock(PlaywrightBatchTestClassGroup.class);

		ReflectionTestUtil.setFieldValue(
			playwrightBatchTestClassGroup, "portalGitWorkingDirectory",
			Mockito.mock(PortalGitWorkingDirectory.class));

		String standardErrorTail = StringUtils.repeat("1", 5000);

		String standardError =
			RandomTestUtil.randomString() + standardErrorTail;

		String standardOutTail = StringUtils.repeat("0", 5000);

		String standardOut = RandomTestUtil.randomString() + standardOutTail;

		Shell.setInstance(
			Mockito.mock(
				Shell.class,
				invocation -> new Shell.ExecutionResult(
					1, standardError, standardOut)));

		String playwrightArgs = RandomTestUtil.randomString();

		try {
			ReflectionTestUtil.invoke(
				playwrightBatchTestClassGroup, "_getPlaywrightJSONObject",
				new Class<?>[] {String.class, File.class}, playwrightArgs,
				null);

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			InvocationTargetException invocationTargetException =
				(InvocationTargetException)runtimeException.getCause();

			Throwable throwable = invocationTargetException.getCause();

			Assert.assertEquals(
				JenkinsResultsParserUtil.combine(
					"Playwright test listing \"", playwrightArgs,
					"\" did not write a JSON report (exit code 1)\n",
					"Standard output:\n", standardOutTail,
					"\nStandard error:\n", standardErrorTail),
				throwable.getMessage());
		}
	}

	@Test
	public void testIsDatabaseTypeSupported() throws Exception {
		_testIsDatabaseTypeSupported(
			"playwright-js-tomcat101", "database.types=mysql", false, null);
		_testIsDatabaseTypeSupported(
			"playwright-js-tomcat101", null, true, null);
		_testIsDatabaseTypeSupported(
			"playwright-js-tomcat101-db2111", "database.types=db2", true,
			"database.types=mysql,postgresql");
		_testIsDatabaseTypeSupported(
			"playwright-js-tomcat101-db2111", "database.types=db2,mysql,oracle",
			true, null);
		_testIsDatabaseTypeSupported(
			"playwright-js-tomcat101-db2111",
			"testray.main.component.name=" + RandomTestUtil.randomString(),
			false, "database.types=mysql,postgresql");
		_testIsDatabaseTypeSupported(
			"playwright-js-tomcat101-db2111", null, false,
			"database.types=mysql,postgresql");
		_testIsDatabaseTypeSupported(
			"playwright-js-tomcat101-mysql84", "database.types=mysql", true,
			null);
		_testIsDatabaseTypeSupported(
			"playwright-js-tomcat101-mysql84-jdk21_zulu",
			"database.types=mysql", true, null);
		_testIsDatabaseTypeSupported(
			"playwright-js-tomcat101-mysql84_stable", "database.types=mysql",
			true, null);
		_testIsDatabaseTypeSupported(
			"playwright-js-tomcat101-oracle193",
			"database.types=db2,mysql,oracle", true, null);
		_testIsDatabaseTypeSupported(
			"playwright-js-tomcat101-postgresql163", "database.types=db2",
			false, "database.types=mysql,postgresql");
		_testIsDatabaseTypeSupported(
			"playwright-js-tomcat101-postgresql163",
			"database.types=db2,mysql,oracle", false, null);
		_testIsDatabaseTypeSupported(
			"playwright-js-tomcat101-postgresql163", "database.types=mysql",
			false, null);
		_testIsDatabaseTypeSupported(
			"playwright-js-tomcat101-postgresql163", null, true,
			"database.types=mysql,postgresql");
		_testIsDatabaseTypeSupported(
			"playwright-js-tomcat101-postgresql163_stable",
			"database.types=mysql", false, null);
	}

	@Test
	public void testIsDatabaseTypeSupportedFailure() throws Exception {
		_testIsDatabaseTypeSupportedFailure(
			"playwright-js-tomcat101-mysql84", "database.types=MySQL");
		_testIsDatabaseTypeSupportedFailure(
			"playwright-js-tomcat101-postgresql163",
			"database.types=MySQL,postgresql");
	}

	@Test
	public void testLoadPlaywrightJSONObjects() throws Exception {
		JSONObject reportJSONObject = new JSONObject(
		).put(
			"config",
			new JSONObject(
			).put(
				"rootDir", RandomTestUtil.randomString()
			)
		);

		String reportJSON = reportJSONObject.toString();

		_testLoadPlaywrightJSONObjects(1, reportJSONObject, reportJSON);
		_testLoadPlaywrightJSONObjects(2, reportJSONObject, "", reportJSON);
		_testLoadPlaywrightJSONObjects(
			2, reportJSONObject, RandomTestUtil.randomString(), reportJSON);
		_testLoadPlaywrightJSONObjects(2, reportJSONObject, null, reportJSON);
		_testLoadPlaywrightJSONObjects(
			3, reportJSONObject, null, "", reportJSON);
	}

	@Test
	public void testLoadPlaywrightJSONObjectsFailure() throws Exception {
		_testLoadPlaywrightJSONObjects(
			3, null, null, RandomTestUtil.randomString(), "");
		_testLoadPlaywrightJSONObjects(3, null, null, null, null);
	}

	@Test
	public void testLoadPlaywrightJSONObjectsSetupYarnFailure() {
		PlaywrightBatchTestClassGroup playwrightBatchTestClassGroup =
			Mockito.mock(PlaywrightBatchTestClassGroup.class);

		ReflectionTestUtil.setFieldValue(
			playwrightBatchTestClassGroup, "portalGitWorkingDirectory",
			Mockito.mock(PortalGitWorkingDirectory.class));

		AntException antException = new AntException();

		List<Throwable> throwables = new ArrayList<>();

		try (MockedStatic<AntUtil> antUtilMockedStatic = Mockito.mockStatic(
				AntUtil.class)) {

			antUtilMockedStatic.when(
				() -> AntUtil.callTarget(null, "build.xml", "setup-yarn")
			).thenThrow(
				antException
			);

			for (int i = 0; i < 2; i++) {
				try {
					ReflectionTestUtil.invoke(
						playwrightBatchTestClassGroup,
						"_loadPlaywrightJSONObjects", new Class<?>[0]);

					Assert.fail();
				}
				catch (RuntimeException runtimeException) {
					InvocationTargetException invocationTargetException =
						(InvocationTargetException)runtimeException.getCause();

					throwables.add(invocationTargetException.getCause());
				}
			}

			antUtilMockedStatic.verify(
				() -> AntUtil.callTarget(null, "build.xml", "setup-yarn"));
		}

		Throwable throwable = throwables.get(0);

		Assert.assertSame(antException, throwable.getCause());
		Assert.assertEquals(
			"Unable to list Playwright tests because the \"setup-yarn\" " +
				"target failed",
			throwable.getMessage());
		Assert.assertSame(throwable, throwables.get(1));
	}

	@Test
	public void testParsePlaywrightJSONObjectsDescribeBlocks() {
		String projectName = RandomTestUtil.randomString();
		File rootDir = new File(RandomTestUtil.randomString());
		String specFilePath = RandomTestUtil.randomString();
		String specTitle = RandomTestUtil.randomString();
		String suiteTitle1 = RandomTestUtil.randomString();
		String suiteTitle2 = RandomTestUtil.randomString();

		Map<String, Map<File, TestClass>> testClassesMaps =
			_parsePlaywrightJSONObjects(
				rootDir,
				new JSONArray(
				).put(
					new JSONObject(
					).put(
						"suites",
						new JSONArray(
						).put(
							_newSuiteJSONObject(
								specFilePath,
								new JSONArray(
								).put(
									_newSpecJSONObject(
										RandomTestUtil.randomString(),
										projectName, specFilePath, specTitle)
								),
								suiteTitle1)
						).put(
							_newSuiteJSONObject(
								specFilePath,
								new JSONArray(
								).put(
									_newSpecJSONObject(
										RandomTestUtil.randomString(),
										projectName, specFilePath, specTitle)
								),
								suiteTitle2)
						)
					)
				));

		Map<File, TestClass> testClassesMap = testClassesMaps.get(projectName);

		Assert.assertEquals(
			Arrays.asList(
				suiteTitle1 + " › " + specTitle,
				suiteTitle2 + " › " + specTitle),
			_getTestNames(testClassesMap.get(new File(rootDir, specFilePath))));
	}

	@Test
	public void testParsePlaywrightJSONObjectsRepeated() {
		String projectName = RandomTestUtil.randomString();
		String specFilePath = RandomTestUtil.randomString();
		String specTitle1 = RandomTestUtil.randomString();
		String specTitle2 = RandomTestUtil.randomString();

		JSONArray suitesJSONArray = new JSONArray(
		).put(
			_newSuiteJSONObject(
				specFilePath,
				new JSONArray(
				).put(
					_newSpecJSONObject(
						RandomTestUtil.randomString(), projectName,
						specFilePath, specTitle1)
				).put(
					_newSpecJSONObject(
						RandomTestUtil.randomString(), projectName,
						specFilePath, specTitle2)
				),
				specFilePath)
		);

		File rootDir = new File(RandomTestUtil.randomString());

		_parsePlaywrightJSONObjects(rootDir, suitesJSONArray);

		Map<String, Map<File, TestClass>> testClassesMaps =
			_parsePlaywrightJSONObjects(rootDir, suitesJSONArray);

		Map<File, TestClass> testClassesMap = testClassesMaps.get(projectName);

		Assert.assertEquals(
			Arrays.asList(specTitle1, specTitle2),
			_getTestNames(testClassesMap.get(new File(rootDir, specFilePath))));
	}

	@Test
	public void testParsePlaywrightJSONObjectsSharedSpec() {
		String projectName1 = RandomTestUtil.randomString();
		String projectName2 = RandomTestUtil.randomString();
		File rootDir = new File(RandomTestUtil.randomString());
		String specFilePath = RandomTestUtil.randomString();
		String specTitle1 = RandomTestUtil.randomString();
		String specTitle2 = RandomTestUtil.randomString();

		Map<String, Map<File, TestClass>> testClassesMaps =
			_parsePlaywrightJSONObjects(
				rootDir,
				new JSONArray(
				).put(
					_newSuiteJSONObject(
						specFilePath,
						new JSONArray(
						).put(
							_newSpecJSONObject(
								"skip", projectName1, specFilePath, specTitle1)
						).put(
							_newSpecJSONObject(
								RandomTestUtil.randomString(), projectName1,
								specFilePath, specTitle2)
						).put(
							_newSpecJSONObject(
								RandomTestUtil.randomString(), projectName2,
								specFilePath, specTitle1)
						),
						specFilePath)
				));

		Map<File, TestClass> testClassesMap1 = testClassesMaps.get(
			projectName1);

		File specFile = new File(rootDir, specFilePath);

		TestClass testClass = testClassesMap1.get(specFile);

		Assert.assertEquals(
			Arrays.asList(specTitle1, specTitle2), _getTestNames(testClass));

		Map<File, TestClass> testClassesMap2 = testClassesMaps.get(
			projectName2);

		Assert.assertSame(testClass, testClassesMap2.get(specFile));

		List<TestClassMethod> testClassMethods =
			testClass.getTestClassMethods();

		TestClassMethod testClassMethod = testClassMethods.get(0);

		Assert.assertTrue(testClassMethod.isIgnored());
	}

	@Test
	public void testSetTestClassesMissingConfig() {
		ReflectionTestUtil.setFieldValue(
			PlaywrightBatchTestClassGroup.class, "_playwrightJSONObject",
			new JSONObject());

		AtomicBoolean playwrightJSONObjectsLoaded =
			ReflectionTestUtil.getFieldValue(
				PlaywrightBatchTestClassGroup.class,
				"_playwrightJSONObjectsLoaded");

		playwrightJSONObjectsLoaded.set(true);

		PlaywrightBatchTestClassGroup playwrightBatchTestClassGroup =
			Mockito.mock(PlaywrightBatchTestClassGroup.class);

		Mockito.doCallRealMethod(
		).when(
			playwrightBatchTestClassGroup
		).setTestClasses();

		try {
			playwrightBatchTestClassGroup.setTestClasses();

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			Assert.assertEquals(
				"Unable to set test classes because the Playwright test " +
					"listing \"test --list --reporter=json\" did not return " +
						"a \"config\"",
				runtimeException.getMessage());
		}
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private List<String> _getTestNames(TestClass testClass) {
		List<String> testNames = new ArrayList<>();

		for (TestClassMethod testClassMethod :
				testClass.getTestClassMethods()) {

			PlaywrightTestClassMethod playwrightTestClassMethod =
				(PlaywrightTestClassMethod)testClassMethod;

			testNames.add(playwrightTestClassMethod.getTestName());
		}

		return testNames;
	}

	private JSONObject _newSpecJSONObject(
		String annotationType, String projectName, String specFilePath,
		String title) {

		return new JSONObject(
		).put(
			"file", specFilePath
		).put(
			"tests",
			new JSONArray(
			).put(
				new JSONObject(
				).put(
					"annotations",
					new JSONArray(
					).put(
						new JSONObject(
						).put(
							"type", annotationType
						)
					)
				).put(
					"projectName", projectName
				)
			)
		).put(
			"title", title
		);
	}

	private JSONObject _newSuiteJSONObject(
		String specFilePath, JSONArray specsJSONArray, String title) {

		return new JSONObject(
		).put(
			"file", specFilePath
		).put(
			"specs", specsJSONArray
		).put(
			"title", title
		);
	}

	private Map<String, Map<File, TestClass>> _parsePlaywrightJSONObjects(
		File rootDir, JSONArray suitesJSONArray) {

		Map<String, Map<File, TestClass>> testClassesMaps = new HashMap<>();

		PlaywrightBatchTestClassGroup playwrightBatchTestClassGroup =
			Mockito.mock(PlaywrightBatchTestClassGroup.class);

		Mockito.doReturn(
			Mockito.mock(PortalGitWorkingDirectory.class)
		).when(
			playwrightBatchTestClassGroup
		).getPortalGitWorkingDirectory();

		ReflectionTestUtil.invoke(
			playwrightBatchTestClassGroup, "_parsePlaywrightJSONObjects",
			new Class<?>[] {File.class, JSONArray.class, Map.class}, rootDir,
			suitesJSONArray, testClassesMaps);

		return testClassesMaps;
	}

	private void _testIsDatabaseTypeSupported(
			String batchName, String configDirTestProperties, boolean expected,
			String specDirTestProperties)
		throws Exception {

		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"jenkins.tmp.dir",
			JenkinsResultsParserUtil.combine(
				JenkinsResultsParserUtil.getCanonicalPath(
					temporaryFolder.getRoot()),
				"/"));

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		File workingDirectory = temporaryFolder.newFolder();

		JenkinsResultsParserUtil.write(
			new File(workingDirectory, "test.properties"),
			JenkinsResultsParserUtil.combine(
				"database.db2.version=11.5\n", "database.mysql.version=8.4\n",
				"database.oracle.version=19.3\n",
				"database.postgresql.version=16.3"));

		File playwrightDir = new File(
			workingDirectory, "modules/test/playwright");

		String projectName = RandomTestUtil.randomString();

		JenkinsResultsParserUtil.write(
			new File(playwrightDir, "tests/config/config.ts"),
			JenkinsResultsParserUtil.combine(
				"export const config = {\n\tname: '", projectName,
				"',\n\ttestDir: 'tests/specs',\n};"));

		if (configDirTestProperties != null) {
			JenkinsResultsParserUtil.write(
				new File(playwrightDir, "tests/config/test.properties"),
				configDirTestProperties);
		}

		if (specDirTestProperties != null) {
			JenkinsResultsParserUtil.write(
				new File(playwrightDir, "tests/specs/test.properties"),
				specDirTestProperties);
		}

		String specFilePath = RandomTestUtil.randomString();

		ReflectionTestUtil.setFieldValue(
			PlaywrightBatchTestClassGroup.class, "_playwrightJSONObject",
			new JSONObject(
			).put(
				"config",
				new JSONObject(
				).put(
					"rootDir", playwrightDir.getPath()
				)
			).put(
				"suites",
				new JSONArray(
				).put(
					_newSuiteJSONObject(
						specFilePath,
						new JSONArray(
						).put(
							_newSpecJSONObject(
								RandomTestUtil.randomString(), projectName,
								specFilePath, RandomTestUtil.randomString())
						),
						specFilePath)
				)
			));

		AtomicBoolean playwrightJSONObjectsLoaded =
			ReflectionTestUtil.getFieldValue(
				PlaywrightBatchTestClassGroup.class,
				"_playwrightJSONObjectsLoaded");

		playwrightJSONObjectsLoaded.set(true);

		setShellCommandOutput(
			"git remote -v", mockShell(),
			"upstream\tgit@github.com:liferay/liferay-portal.git (fetch)\n" +
				"upstream\tgit@github.com:liferay/liferay-portal.git (push)\n");

		mockEnvironment(
			Collections.singletonMap("PLAYWRIGHT_PROJECT_NAME", projectName));

		Properties jobProperties = new Properties();

		jobProperties.setProperty("test.relevant.changes", "false");

		PlaywrightBatchTestClassGroup playwrightBatchTestClassGroup =
			new PlaywrightBatchTestClassGroup(
				batchName,
				BatchTestClassGroupTestUtil.getPortalTestClassJob(
					jobProperties, Collections.emptyList(), workingDirectory));

		List<TestClass> testClasses =
			playwrightBatchTestClassGroup.getTestClasses();

		Assert.assertEquals(batchName, expected, !testClasses.isEmpty());
	}

	private void _testIsDatabaseTypeSupportedFailure(
			String batchName, String configDirTestProperties)
		throws Exception {

		try {
			_testIsDatabaseTypeSupported(
				batchName, configDirTestProperties, false, null);

			Assert.fail(batchName);
		}
		catch (RuntimeException runtimeException) {
			String message = runtimeException.getMessage();

			Assert.assertTrue(
				message, message.startsWith("Invalid database type \"MySQL\""));
		}
	}

	private void _testLoadPlaywrightJSONObjects(
			int expectedExecutionRequestsCount,
			JSONObject expectedPlaywrightJSONObject, String... reports)
		throws Exception {

		AtomicBoolean playwrightJSONObjectsLoaded =
			ReflectionTestUtil.getFieldValue(
				PlaywrightBatchTestClassGroup.class,
				"_playwrightJSONObjectsLoaded");

		playwrightJSONObjectsLoaded.set(false);

		PortalGitWorkingDirectory portalGitWorkingDirectory = Mockito.mock(
			PortalGitWorkingDirectory.class);

		File portalWorkingDirectory = temporaryFolder.newFolder();

		Mockito.doReturn(
			portalWorkingDirectory
		).when(
			portalGitWorkingDirectory
		).getWorkingDirectory();

		List<PlaywrightBatchTestClassGroup> playwrightBatchTestClassGroups =
			new ArrayList<>();

		for (int i = 0; i < 2; i++) {
			PlaywrightBatchTestClassGroup playwrightBatchTestClassGroup =
				Mockito.mock(PlaywrightBatchTestClassGroup.class);

			Mockito.doCallRealMethod(
			).when(
				playwrightBatchTestClassGroup
			).getPlaywrightBaseDir();

			ReflectionTestUtil.setFieldValue(
				playwrightBatchTestClassGroup, "portalGitWorkingDirectory",
				portalGitWorkingDirectory);

			playwrightBatchTestClassGroups.add(playwrightBatchTestClassGroup);
		}

		PlaywrightBatchTestClassGroup firstPlaywrightBatchTestClassGroup =
			playwrightBatchTestClassGroups.get(0);

		JenkinsResultsParserUtil.write(
			new File(
				firstPlaywrightBatchTestClassGroup.getPlaywrightBaseDir(),
				"build.gradle"),
			"task runPlaywright");

		List<Shell.ExecutionRequest> executionRequests = new ArrayList<>();
		Set<File> reportFiles = new HashSet<>();

		Shell.setInstance(
			Mockito.mock(
				Shell.class,
				invocation -> {
					Shell.ExecutionRequest executionRequest =
						invocation.getArgument(0);

					executionRequests.add(executionRequest);

					String[] commands = executionRequest.getCommands();

					Matcher matcher = _playwrightJSONOutputNamePattern.matcher(
						commands[0]);

					Assert.assertTrue(commands[0], matcher.find());

					File reportFile = new File(matcher.group(1));

					reportFiles.add(reportFile);

					String report = reports[executionRequests.size() - 1];

					if (report == null) {
						throw new TimeoutException();
					}

					if (!report.isEmpty()) {
						JenkinsResultsParserUtil.write(reportFile, report);
					}

					return new Shell.ExecutionResult(0, "", "");
				}));

		mockEnvironment(
			Collections.singletonMap(
				"TOP_LEVEL_BUILD_URL",
				JenkinsResultsParserUtil.combine(
					"https://", RandomTestUtil.randomString(), "/job/",
					RandomTestUtil.randomString(), "(release)/",
					String.valueOf(RandomTestUtil.randomInt()))));

		List<RuntimeException> runtimeExceptions = new ArrayList<>();

		try (MockedStatic<AntUtil> antUtilMockedStatic = Mockito.mockStatic(
				AntUtil.class);
			MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic = Mockito.mockStatic(
					JenkinsResultsParserUtil.class, Mockito.CALLS_REAL_METHODS);
			MockedStatic<NotificationUtil> notificationUtilMockedStatic =
				Mockito.mockStatic(NotificationUtil.class)) {

			jenkinsResultsParserUtilMockedStatic.when(
				() -> JenkinsResultsParserUtil.sleep(Mockito.anyLong())
			).thenAnswer(
				invocation -> null
			);

			for (PlaywrightBatchTestClassGroup playwrightBatchTestClassGroup :
					playwrightBatchTestClassGroups) {

				try {
					ReflectionTestUtil.invoke(
						playwrightBatchTestClassGroup,
						"_loadPlaywrightJSONObjects", new Class<?>[0]);
				}
				catch (RuntimeException runtimeException) {
					InvocationTargetException invocationTargetException =
						(InvocationTargetException)runtimeException.getCause();

					runtimeExceptions.add(
						(RuntimeException)invocationTargetException.getCause());
				}
			}

			jenkinsResultsParserUtilMockedStatic.verify(
				() -> JenkinsResultsParserUtil.sleep(1000 * 60),
				Mockito.times(expectedExecutionRequestsCount - 1));

			notificationUtilMockedStatic.verify(
				() -> NotificationUtil.sendSlackNotification(
					Mockito.anyString(), Mockito.anyString(),
					Mockito.anyString(), Mockito.anyString(),
					Mockito.anyString()),
				getVerificationMode(expectedPlaywrightJSONObject == null));
		}

		Assert.assertEquals(
			executionRequests.toString(), expectedExecutionRequestsCount,
			executionRequests.size());

		for (Shell.ExecutionRequest executionRequest : executionRequests) {
			String[] commands = executionRequest.getCommands();

			Assert.assertTrue(
				commands[0].contains(
					JenkinsResultsParserUtil.combine(
						"export npm_config_fetch_retries=5\n",
						"export npm_config_fetch_retry_maxtimeout=120000\n",
						"export npm_config_fetch_retry_mintimeout=20000\n")));

			Assert.assertEquals(1000 * 60 * 30, executionRequest.getTimeout());
		}

		Assert.assertEquals(
			reportFiles.toString(), executionRequests.size(),
			reportFiles.size());

		String portalWorkingDirectoryPath =
			JenkinsResultsParserUtil.getCanonicalPath(portalWorkingDirectory);

		for (File reportFile : reportFiles) {
			String reportFilePath = JenkinsResultsParserUtil.getCanonicalPath(
				reportFile);

			Assert.assertFalse(reportFilePath, reportFile.exists());
			Assert.assertFalse(
				reportFilePath,
				reportFilePath.startsWith(portalWorkingDirectoryPath));
		}

		if (expectedPlaywrightJSONObject == null) {
			Assert.assertEquals(
				runtimeExceptions.toString(), 2, runtimeExceptions.size());

			RuntimeException runtimeException = runtimeExceptions.get(0);

			if (reports[reports.length - 1] == null) {
				Assert.assertTrue(
					runtimeException.getCause() instanceof TimeoutException);
			}
			else {
				Assert.assertEquals(
					"Playwright test listing \"test --list --reporter=json\" " +
						"did not write a JSON report (exit code 0)\nStandard " +
							"output:\n\nStandard error:\n",
					runtimeException.getMessage());
			}

			Assert.assertSame(runtimeException, runtimeExceptions.get(1));

			return;
		}

		Assert.assertTrue(runtimeExceptions.isEmpty());

		JSONObject playwrightJSONObject = ReflectionTestUtil.getFieldValue(
			PlaywrightBatchTestClassGroup.class, "_playwrightJSONObject");

		Assert.assertTrue(
			playwrightJSONObject.toString(),
			expectedPlaywrightJSONObject.similar(playwrightJSONObject));
	}

	private static final Pattern _playwrightJSONOutputNamePattern =
		Pattern.compile("export PLAYWRIGHT_JSON_OUTPUT_NAME=(.+)");

}