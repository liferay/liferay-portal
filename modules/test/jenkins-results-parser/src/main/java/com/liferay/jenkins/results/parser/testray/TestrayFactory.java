/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.Build;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Retryable;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.test.clazz.BaseAntTargetTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.FunctionalAxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.JSUnitAxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.JUnitAxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.ModulesAxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.PlaywrightAxisTestClassGroup;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class TestrayFactory {

	public static TestrayCaseResult newBuildTestrayCaseResult(
		AxisTestClassGroup axisTestClassGroup, TestClass testClass,
		TestClassMethod testClassMethod, TestrayBuild testrayBuild,
		TopLevelBuildReport topLevelBuildReport) {

		if (testrayBuild == null) {
			throw new RuntimeException("Testray build is null");
		}

		if (topLevelBuildReport == null) {
			throw new RuntimeException("Top level build report is null");
		}

		if (axisTestClassGroup == null) {
			throw new RuntimeException("Axis test class group is null");
		}

		if (testClass != null) {
			if (axisTestClassGroup instanceof FunctionalAxisTestClassGroup) {
				return new FunctionalBatchBuildTestrayCaseResult(
					axisTestClassGroup, testClass, testrayBuild,
					topLevelBuildReport);
			}
			else if (axisTestClassGroup instanceof JSUnitAxisTestClassGroup) {
				return new JSUnitBatchBuildTestrayCaseResult(
					axisTestClassGroup, testClass, testrayBuild,
					topLevelBuildReport);
			}
			else if (axisTestClassGroup instanceof JUnitAxisTestClassGroup) {
				return new JUnitBatchBuildTestrayCaseResult(
					axisTestClassGroup, testClass, testrayBuild,
					topLevelBuildReport);
			}
			else if (axisTestClassGroup instanceof ModulesAxisTestClassGroup) {
				if (testClass instanceof BaseAntTargetTestClass) {
					return new AntTargetBatchBuildTestrayCaseResult(
						axisTestClassGroup, testClass, testrayBuild,
						topLevelBuildReport);
				}

				return new ModulesBatchBuildTestrayCaseResult(
					axisTestClassGroup, testClass, testrayBuild,
					topLevelBuildReport);
			}
			else if (axisTestClassGroup instanceof
						PlaywrightAxisTestClassGroup) {

				return new PlaywrightBatchBuildTestrayCaseResult(
					axisTestClassGroup, testClass, testClassMethod,
					testrayBuild, topLevelBuildReport);
			}
		}

		if (Objects.equals(
				topLevelBuildReport.getJobName(),
				"test-portal-source-format")) {

			return new SFBatchBuildTestrayCaseResult(
				axisTestClassGroup, testrayBuild, topLevelBuildReport);
		}

		return new DownstreamBatchBuildTestrayCaseResult(
			axisTestClassGroup, testrayBuild, topLevelBuildReport);
	}

	public static TestrayCaseResult newBuildTestrayCaseResult(
		AxisTestClassGroup axisTestClassGroup, TestClass testClass,
		TestrayBuild testrayBuild, TopLevelBuildReport topLevelBuildReport) {

		return newBuildTestrayCaseResult(
			axisTestClassGroup, testClass, null, testrayBuild,
			topLevelBuildReport);
	}

	public static TestrayCaseResult newBuildTestrayCaseResult(
		AxisTestClassGroup axisTestClassGroup, TestrayBuild testrayBuild,
		TopLevelBuildReport topLevelBuildReport) {

		return newBuildTestrayCaseResult(
			axisTestClassGroup, null, null, testrayBuild, topLevelBuildReport);
	}

	public static TestrayCaseResult newJSONObjectTestrayCaseResult(
		TestrayBuild testrayBuild, JSONObject jsonObject) {

		return new JSONObjectTestrayCaseResult(testrayBuild, jsonObject);
	}

	public static TestrayCaseResult newJSONObjectTestrayCaseResult(
		TestrayServer testrayServer, JSONObject jsonObject) {

		return new JSONObjectTestrayCaseResult(testrayServer, jsonObject);
	}

	public static PortalLogBatchBuildTestrayCaseResult
		newPortalLogTestrayCaseResult(
			AxisTestClassGroup axisTestClassGroup, TestrayBuild testrayBuild,
			TopLevelBuildReport topLevelBuildReport) {

		return new PortalLogBatchBuildTestrayCaseResult(
			axisTestClassGroup, testrayBuild, topLevelBuildReport);
	}

	public static TestrayFactor newRunTestrayFactor(
		TestrayFactor.Option testrayFactorOption, TestrayRun testrayRun) {

		synchronized (_runTestrayFactors) {
			TestrayBuild testrayBuild = testrayRun.getTestrayBuild();

			TestrayFactor.Category testrayFactorCategory =
				testrayFactorOption.getCategory();

			String key = JenkinsResultsParserUtil.combine(
				String.valueOf(testrayBuild.getId()), "__",
				testrayRun.getRunIdString(), "__",
				testrayFactorCategory.getName(), "__",
				testrayFactorOption.getName());

			RunTestrayFactor runTestrayFactor = _runTestrayFactors.get(key);

			if (runTestrayFactor != null) {
				return runTestrayFactor;
			}

			runTestrayFactor = new RunTestrayFactor(
				testrayFactorOption, testrayRun);

			_runTestrayFactors.put(key, runTestrayFactor);

			return runTestrayFactor;
		}
	}

	public static TestrayAttachment newTestrayAttachment(
		TestrayCaseResult testrayCaseResult, String name, String key) {

		return newTestrayAttachment(testrayCaseResult, name, key, null);
	}

	public static TestrayAttachment newTestrayAttachment(
		TestrayCaseResult testrayCaseResult, String name, String key, URL url) {

		return new DefaultTestrayAttachment(testrayCaseResult, name, key, url);
	}

	public static TestrayAttachmentRecorder newTestrayAttachmentRecorder(
		Build build) {

		TestrayAttachmentRecorder testrayAttachmentRecorder =
			_testrayAttachmentRecorders.get(build);

		if (testrayAttachmentRecorder != null) {
			return testrayAttachmentRecorder;
		}

		testrayAttachmentRecorder = new TestrayAttachmentRecorder(build);

		_testrayAttachmentRecorders.put(build, testrayAttachmentRecorder);

		return testrayAttachmentRecorder;
	}

	public static TestrayAttachmentUploader newTestrayAttachmentUploader(
		Build build, URL testrayServerURL) {

		String testrayServerURLString = "";

		if (testrayServerURL != null) {
			testrayServerURLString = String.valueOf(testrayServerURL);
		}

		String key = JenkinsResultsParserUtil.combine(
			build.getBuildURL(), "_", testrayServerURLString);

		TestrayAttachmentUploader testrayAttachmentUploader =
			_testrayAttachmentUploaders.get(key);

		if (testrayAttachmentUploader != null) {
			return testrayAttachmentUploader;
		}

		testrayAttachmentUploader = new CloudObjectTestrayAttachmentUploader(
			build, testrayServerURL);

		_testrayAttachmentUploaders.put(key, testrayAttachmentUploader);

		return testrayAttachmentUploader;
	}

	public static TestrayBuild newTestrayBuild(
		TestrayRoutine testrayRoutine, JSONObject jsonObject) {

		synchronized (_testrayBuilds) {
			long id = jsonObject.getLong("id");

			TestrayBuild testrayBuild = _testrayBuilds.get(id);

			if (testrayBuild != null) {
				return testrayBuild;
			}

			testrayBuild = new TestrayBuild(testrayRoutine, jsonObject);

			_testrayBuilds.put(id, testrayBuild);

			return testrayBuild;
		}
	}

	public static TestrayBuild newTestrayBuild(
		TestrayRoutine testrayRoutine, long id) {

		synchronized (_testrayBuilds) {
			TestrayBuild testrayBuild = _testrayBuilds.get(id);

			if (testrayBuild != null) {
				return testrayBuild;
			}

			String filterString = JenkinsResultsParserUtil.combine(
				"id eq '", String.valueOf(id),
				"' and r_routineToBuilds_c_routineId eq '",
				String.valueOf(testrayRoutine.getId()), "'");

			TestrayServer testrayServer = testrayRoutine.getTestrayServer();

			try {
				Set<JSONObject> entityJSONObjects =
					testrayServer.requestGraphQL(
						"builds", TestrayBuild.FIELD_NAMES, filterString, null,
						1, 1);

				for (JSONObject entityJSONObject : entityJSONObjects) {
					testrayBuild = new TestrayBuild(
						testrayRoutine, entityJSONObject);

					_testrayBuilds.put(id, testrayBuild);

					return testrayBuild;
				}

				return null;
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}
	}

	public static TestrayBuild newTestrayBuild(
		TestrayServer testrayServer, long id) {

		synchronized (_testrayBuilds) {
			TestrayBuild testrayBuild = _testrayBuilds.get(id);

			if (testrayBuild != null) {
				return testrayBuild;
			}

			try {
				Set<JSONObject> entityJSONObjects =
					testrayServer.requestGraphQL(
						"builds", TestrayBuild.FIELD_NAMES,
						JenkinsResultsParserUtil.combine(
							"id eq '", String.valueOf(id), "'"),
						null, 1, 1);

				for (JSONObject entityJSONObject : entityJSONObjects) {
					testrayBuild = new TestrayBuild(
						testrayServer, entityJSONObject);

					_testrayBuilds.put(id, testrayBuild);

					return testrayBuild;
				}

				return null;
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}
	}

	public static TestrayBuild newTestrayBuild(URL url) {
		synchronized (_testrayBuilds) {
			long id = TestrayBuild.getId(url);

			if (id <= 0) {
				return new TestrayBuild(url);
			}

			TestrayBuild testrayBuild = _testrayBuilds.get(id);

			if (testrayBuild != null) {
				return testrayBuild;
			}

			testrayBuild = new TestrayBuild(url);

			_testrayBuilds.put(id, testrayBuild);

			return testrayBuild;
		}
	}

	public static TestrayCase newTestrayCase(
		TestrayProject testrayProject, JSONObject jsonObject) {

		return new TestrayCase(testrayProject, jsonObject);
	}

	public static TestrayCase newTestrayCase(
		final TestrayProject testrayProject, String name,
		TestrayCaseType testrayCaseType) {

		if (JenkinsResultsParserUtil.isNullOrEmpty(name)) {
			return null;
		}

		synchronized (_testrayCases) {
			String key = JenkinsResultsParserUtil.combine(
				String.valueOf(testrayProject.getId()), "__",
				String.valueOf(testrayCaseType.getId()), "__", name);

			TestrayCase testrayCase = _testrayCases.get(key);

			if (testrayCase != null) {
				return testrayCase;
			}

			final String filterString = JenkinsResultsParserUtil.combine(
				"name eq '", name, "' and ",
				"r_caseTypeToCases_c_caseTypeId eq '",
				String.valueOf(testrayCaseType.getId()), "' and ",
				"r_projectToCases_c_projectId eq '",
				String.valueOf(testrayProject.getId()), "'");

			final JSONObject requestJSONObject = new JSONObject();

			requestJSONObject.put(
				"name", name
			).put(
				"r_caseTypeToCases_c_caseTypeId", testrayCaseType.getId()
			).put(
				"r_projectToCases_c_projectId", testrayProject.getId()
			);

			Retryable<TestrayCase> retryable = new Retryable<TestrayCase>(
				true, 3, 5, true) {

				@Override
				public TestrayCase execute() {
					try {
						TestrayServer testrayServer =
							testrayProject.getTestrayServer();

						Set<JSONObject> entityJSONObjects =
							testrayServer.requestGraphQL(
								"cases", TestrayCase.FIELD_NAMES, filterString,
								null, 1, 1);

						for (JSONObject entityJSONObject : entityJSONObjects) {
							return new TestrayCase(
								testrayProject, entityJSONObject);
						}

						long start =
							JenkinsResultsParserUtil.getCurrentTimeMillis();

						JSONObject responseJSONObject = new JSONObject(
							testrayServer.requestPost(
								"/o/c/cases", requestJSONObject.toString()));

						long end =
							JenkinsResultsParserUtil.getCurrentTimeMillis();

						System.out.println(
							JenkinsResultsParserUtil.combine(
								"Testray Case '",
								requestJSONObject.getString("name"),
								"' created in ",
								JenkinsResultsParserUtil.toDurationString(
									end - start)));

						return new TestrayCase(
							testrayProject, responseJSONObject);
					}
					catch (IOException ioException) {
						throw new RuntimeException(ioException);
					}
				}

			};

			testrayCase = retryable.executeWithRetries();

			if (testrayCase != null) {
				_testrayCases.put(key, testrayCase);
			}

			return testrayCase;
		}
	}

	public static TestrayCaseType newTestrayCaseType(
		TestrayServer testrayServer, JSONObject jsonObject) {

		return new TestrayCaseType(testrayServer, jsonObject);
	}

	public static TestrayComponent newTestrayComponent(
		TestrayProject testrayProject, JSONObject jsonObject) {

		return new TestrayComponent(testrayProject, jsonObject);
	}

	public static TestrayFactor.Category newTestrayFactorCategory(
		JSONObject jsonObject, TestrayServer testrayServer) {

		if (jsonObject == null) {
			return null;
		}

		synchronized (_testrayFactorCategoriesNames) {
			long id = jsonObject.getLong("id");

			TestrayFactor.Category testrayFactorCategory =
				_testrayFactorCategoriesIds.get(id);

			if (testrayFactorCategory != null) {
				return testrayFactorCategory;
			}

			testrayFactorCategory = new TestrayFactor.Category(
				jsonObject, testrayServer);

			_testrayFactorCategoriesIds.put(id, testrayFactorCategory);

			_testrayFactorCategoriesNames.put(
				testrayFactorCategory.getName(), testrayFactorCategory);

			return testrayFactorCategory;
		}
	}

	public static TestrayFactor.Category newTestrayFactorCategory(
		long id, TestrayServer testrayServer) {

		if (id <= 0) {
			return null;
		}

		synchronized (_testrayFactorCategoriesNames) {
			TestrayFactor.Category testrayFactorCategory =
				_testrayFactorCategoriesIds.get(id);

			if (testrayFactorCategory != null) {
				return testrayFactorCategory;
			}

			testrayFactorCategory = new TestrayFactor.Category(
				id, testrayServer);

			_testrayFactorCategoriesIds.put(id, testrayFactorCategory);

			String name = testrayFactorCategory.getName();

			if (!JenkinsResultsParserUtil.isNullOrEmpty(name)) {
				_testrayFactorCategoriesNames.put(name, testrayFactorCategory);
			}

			return testrayFactorCategory;
		}
	}

	public static TestrayFactor.Category newTestrayFactorCategory(
		String name, TestrayServer testrayServer) {

		if (JenkinsResultsParserUtil.isNullOrEmpty(name)) {
			return null;
		}

		synchronized (_testrayFactorCategoriesNames) {
			TestrayFactor.Category testrayFactorCategory =
				_testrayFactorCategoriesNames.get(name);

			if (testrayFactorCategory != null) {
				return testrayFactorCategory;
			}

			testrayFactorCategory = new TestrayFactor.Category(
				name, testrayServer);

			_testrayFactorCategoriesNames.put(name, testrayFactorCategory);

			_testrayFactorCategoriesIds.put(
				testrayFactorCategory.getId(), testrayFactorCategory);

			return testrayFactorCategory;
		}
	}

	public static TestrayFactor.Option newTestrayFactorOption(
		JSONObject jsonObject, TestrayServer testrayServer) {

		if (jsonObject == null) {
			return null;
		}

		synchronized (_testrayFactorOptionsNames) {
			long id = jsonObject.getLong("id");

			TestrayFactor.Option testrayFactorOption =
				_testrayFactorOptionsIds.get(id);

			if (testrayFactorOption != null) {
				return testrayFactorOption;
			}

			testrayFactorOption = new TestrayFactor.Option(
				jsonObject, testrayServer);

			_testrayFactorOptionsIds.put(id, testrayFactorOption);

			_testrayFactorOptionsNames.put(
				testrayFactorOption.getName(), testrayFactorOption);

			return testrayFactorOption;
		}
	}

	public static TestrayFactor.Option newTestrayFactorOption(
		Long id, TestrayServer testrayServer) {

		if ((id == null) || (id <= 0)) {
			return null;
		}

		synchronized (_testrayFactorOptionsNames) {
			TestrayFactor.Option testrayFactorOption =
				_testrayFactorOptionsIds.get(id);

			if (testrayFactorOption != null) {
				return testrayFactorOption;
			}

			testrayFactorOption = new TestrayFactor.Option(id, testrayServer);

			_testrayFactorOptionsIds.put(id, testrayFactorOption);

			String name = testrayFactorOption.getName();

			if (!JenkinsResultsParserUtil.isNullOrEmpty(name)) {
				_testrayFactorOptionsNames.put(name, testrayFactorOption);
			}

			return testrayFactorOption;
		}
	}

	public static TestrayFactor.Option newTestrayFactorOption(
		String name, TestrayServer testrayServer) {

		synchronized (_testrayFactorOptionsNames) {
			TestrayFactor.Option testrayFactorOption =
				_testrayFactorOptionsNames.get(name);

			if (testrayFactorOption != null) {
				return testrayFactorOption;
			}

			testrayFactorOption = new TestrayFactor.Option(name, testrayServer);

			_testrayFactorOptionsNames.put(name, testrayFactorOption);

			_testrayFactorOptionsIds.put(
				testrayFactorOption.getId(), testrayFactorOption);

			return testrayFactorOption;
		}
	}

	public static TestrayProductVersion newTestrayProductVersion(
		TestrayProject testrayProject, JSONObject jsonObject) {

		return new TestrayProductVersion(testrayProject, jsonObject);
	}

	public static TestrayProject newTestrayProject(
		TestrayServer testrayServer, JSONObject jsonObject) {

		return new TestrayProject(testrayServer, jsonObject);
	}

	public static TestrayRoutine newTestrayRoutine(String testrayRoutineURL) {
		TestrayRoutine testrayRoutine = _testrayRoutines.get(testrayRoutineURL);

		if (testrayRoutine != null) {
			return testrayRoutine;
		}

		try {
			Matcher testrayURLMatcher = _testrayURLPattern.matcher(
				testrayRoutineURL);

			if (!testrayURLMatcher.find()) {
				throw new RuntimeException(
					"Invalid Testray URL " + testrayRoutineURL);
			}

			testrayRoutine = new TestrayRoutine(new URL(testrayRoutineURL));

			_testrayRoutines.put(testrayRoutineURL, testrayRoutine);

			return testrayRoutine;
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	public static TestrayRoutine newTestrayRoutine(
		TestrayProject testrayProject, JSONObject jsonObject) {

		return new TestrayRoutine(testrayProject, jsonObject);
	}

	public static TestrayRoutine newTestrayRoutine(
		TestrayServer testrayServer, JSONObject jsonObject) {

		return new TestrayRoutine(testrayServer, jsonObject);
	}

	public static TestrayRun newTestrayRun(
		TestrayBuild testrayBuild, JSONObject jsonObject) {

		synchronized (_testrayRuns) {
			String key =
				testrayBuild.getId() + "__" + jsonObject.getString("name");

			TestrayRun testrayRun = _testrayRuns.get(key);

			if (testrayRun != null) {
				return testrayRun;
			}

			testrayRun = new TestrayRun(testrayBuild, jsonObject);

			_testrayRuns.put(key, testrayRun);

			return testrayRun;
		}
	}

	public static TestrayRun newTestrayRun(
		TestrayBuild testrayBuild, String batchName, String testSuiteName,
		Properties properties) {

		synchronized (_testrayRuns) {
			String name = TestrayRun.getRunIdString(
				batchName, testSuiteName, properties);

			if (name == null) {
				throw new RuntimeException(
					"Please set testray.environment.default[master]");
			}

			String key = testrayBuild.getId() + "__" + name;

			TestrayRun testrayRun = _testrayRuns.get(key);

			if (testrayRun != null) {
				return testrayRun;
			}

			testrayRun = new TestrayRun(testrayBuild, name);

			_testrayRuns.put(key, testrayRun);

			return testrayRun;
		}
	}

	public static TestrayRunComparison newTestrayRunComparison(
		TestrayRun testrayRunA, TestrayRun testrayRunB) {

		return new TestrayRunComparison(testrayRunA, testrayRunB);
	}

	public static TestrayServer newTestrayServer(String testrayServerURL) {
		TestrayServer testrayServer = _testrayServers.get(testrayServerURL);

		if (testrayServer != null) {
			return testrayServer;
		}

		Matcher testrayURLMatcher = _testrayURLPattern.matcher(
			testrayServerURL);

		if (!testrayURLMatcher.find()) {
			throw new RuntimeException(
				"Invalid Testray URL " + testrayServerURL);
		}

		testrayServer = new TestrayServer(testrayServerURL);

		_testrayServers.put(testrayServerURL, testrayServer);

		return testrayServer;
	}

	public static TestrayTeam newTestrayTeam(
		TestrayProject testrayProject, JSONObject jsonObject) {

		return new TestrayTeam(testrayProject, jsonObject);
	}

	public static synchronized TopLevelStandaloneBuildTestrayCaseResult
		newTopLevelStandaloneBuildTestrayCaseResult(
			TestrayBuild testrayBuild,
			TopLevelBuildReport topLevelBuildReport) {

		Long testrayBuildId = testrayBuild.getId();

		TopLevelStandaloneBuildTestrayCaseResult
			topLevelStandaloneBuildTestrayCaseResult =
				_topLevelBuildTestrayCaseResults.get(testrayBuildId);

		if (topLevelStandaloneBuildTestrayCaseResult != null) {
			return topLevelStandaloneBuildTestrayCaseResult;
		}

		if (testrayBuild == null) {
			throw new RuntimeException("Please set a Testray build");
		}

		if (topLevelBuildReport == null) {
			throw new RuntimeException("Please set a top level build report");
		}

		_topLevelBuildTestrayCaseResults.put(
			testrayBuildId,
			new TopLevelStandaloneBuildTestrayCaseResult(
				testrayBuild, topLevelBuildReport));

		return _topLevelBuildTestrayCaseResults.get(testrayBuildId);
	}

	private static final Map<String, RunTestrayFactor> _runTestrayFactors =
		new ConcurrentHashMap<>();
	private static final Map<Build, TestrayAttachmentRecorder>
		_testrayAttachmentRecorders = new ConcurrentHashMap<>();
	private static final Map<String, TestrayAttachmentUploader>
		_testrayAttachmentUploaders = new ConcurrentHashMap<>();
	private static final Map<Long, TestrayBuild> _testrayBuilds =
		new ConcurrentHashMap<>();
	private static final Map<String, TestrayCase> _testrayCases =
		new ConcurrentHashMap<>();
	private static final Map<Long, TestrayFactor.Category>
		_testrayFactorCategoriesIds = new ConcurrentHashMap<>();
	private static final Map<String, TestrayFactor.Category>
		_testrayFactorCategoriesNames = new ConcurrentHashMap<>();
	private static final Map<Long, TestrayFactor.Option>
		_testrayFactorOptionsIds = new ConcurrentHashMap<>();
	private static final Map<String, TestrayFactor.Option>
		_testrayFactorOptionsNames = new ConcurrentHashMap<>();
	private static final Map<String, TestrayRoutine> _testrayRoutines =
		new ConcurrentHashMap<>();
	private static final Map<String, TestrayRun> _testrayRuns =
		new ConcurrentHashMap<>();
	private static final Map<String, TestrayServer> _testrayServers =
		new ConcurrentHashMap<>();
	private static final Pattern _testrayURLPattern = Pattern.compile(
		"https://(testray\\.liferay\\.com|webserver-testray2" +
			"(-prd\\d*|-uat\\d*)?.lfr.cloud)");
	private static final Map<Long, TopLevelStandaloneBuildTestrayCaseResult>
		_topLevelBuildTestrayCaseResults = new ConcurrentHashMap<>();

}