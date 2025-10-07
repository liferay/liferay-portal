/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.metrics;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.ParallelExecutor;

import java.io.File;
import java.io.IOException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;

/**
 * @author Kenji Heigel
 */
public class BuildHistoryProcessor {

	public static File getBaseDir() {
		return _baseDir;
	}

	public static ExecutorService getExecutorService() {
		return _executorService;
	}

	public static BuildHistory mergeBuildHistories(
		Collection<BuildHistory> buildHistories, String name) {

		return _mergeBuildHistories(new ArrayList<>(buildHistories), name);
	}

	public static BuildHistory mergeBuildHistories(
		String name, BuildHistory... buildHistories) {

		return _mergeBuildHistories(Arrays.asList(buildHistories), name);
	}

	public static Collection<BuildHistory> newAggregateJobHistories(
		long duration, long startTime) {

		BiConsumer<Set<BuildJSONObject>, Map<String, BuildHistory>> biConsumer =
			new BiConsumer<Set<BuildJSONObject>, Map<String, BuildHistory>>() {

				@Override
				public void accept(
					Set<BuildJSONObject> buildJSONObjects,
					Map<String, BuildHistory> buildHistories) {

					_addToBuildHistoriesMap(
						buildJSONObjects, buildHistories, duration,
						new GroupByCategory(), startTime);
				}

			};

		return _getBuildHistories(biConsumer, duration, null, null, startTime);
	}

	public static Collection<BuildHistory> newDefaultJobHistories(
		long duration, long startTime) {

		BiConsumer<Set<BuildJSONObject>, Map<String, BuildHistory>> biConsumer =
			new BiConsumer<Set<BuildJSONObject>, Map<String, BuildHistory>>() {

				@Override
				public void accept(
					Set<BuildJSONObject> buildJSONObjects,
					Map<String, BuildHistory> buildHistories) {

					_addToBuildHistoriesMap(
						buildJSONObjects, buildHistories, duration,
						new GroupByJobName(), startTime);
				}

			};

		return _getBuildHistories(biConsumer, duration, null, null, startTime);
	}

	public static Collection<BuildHistory> newTestSuiteJobHistories(
		long duration, Pattern jobNamePattern, long startTime) {

		Function<BuildJSONObject, String> groupByTopLevelTestSuite =
			new GroupByTopLevelTestSuite();

		BiConsumer<Set<BuildJSONObject>, Map<String, BuildHistory>> biConsumer =
			new BiConsumer<Set<BuildJSONObject>, Map<String, BuildHistory>>() {

				@Override
				public void accept(
					Set<BuildJSONObject> buildJSONObjects,
					Map<String, BuildHistory> buildHistories) {

					Set<BuildJSONObject> downstreamBuildJSONObjects =
						new HashSet<>();
					Set<BuildJSONObject> topLevelBuildJSONObjects =
						new HashSet<>();

					for (BuildJSONObject buildJSONObject : buildJSONObjects) {
						if (buildJSONObject.isTopLevelBuild()) {
							topLevelBuildJSONObjects.add(buildJSONObject);
						}
						else {
							downstreamBuildJSONObjects.add(buildJSONObject);
						}
					}

					_addToBuildHistoriesMap(
						topLevelBuildJSONObjects, buildHistories, duration,
						groupByTopLevelTestSuite, startTime);

					Map<String, Set<BuildJSONObject>>
						groupedBuildDataJSONObjectsMap =
							_getGroupedBuildDataJSONObjectsMap(
								downstreamBuildJSONObjects,
								groupByTopLevelTestSuite);

					for (Map.Entry<String, Set<BuildJSONObject>> entry :
							groupedBuildDataJSONObjectsMap.entrySet()) {

						String key = entry.getKey();

						if (!buildHistories.containsKey(key)) {
							BuildHistory buildHistory = new BuildHistory(
								duration, key, startTime);

							buildHistories.put(key, buildHistory);
						}

						BuildHistory buildHistory = buildHistories.get(key);

						buildHistory.addBuildJSONObjects(
							groupedBuildDataJSONObjectsMap.get(key));
					}
				}

			};

		return _getBuildHistories(
			biConsumer, duration, null, jobNamePattern, startTime);
	}

	public static Collection<BuildHistory> newTopLevelBuildHistories(
		long duration, long startTime) {

		BiConsumer<Set<BuildJSONObject>, Map<String, BuildHistory>> biConsumer =
			new BiConsumer<Set<BuildJSONObject>, Map<String, BuildHistory>>() {

				@Override
				public void accept(
					Set<BuildJSONObject> buildJSONObjects,
					Map<String, BuildHistory> buildHistories) {

					Set<BuildJSONObject> topLevelBuildJSONObjects =
						new HashSet<>();

					for (BuildJSONObject buildJSONObject : buildJSONObjects) {
						if (buildJSONObject.isTopLevelBuild()) {
							topLevelBuildJSONObjects.add(buildJSONObject);
						}
					}

					_addToBuildHistoriesMap(
						topLevelBuildJSONObjects, buildHistories, duration,
						new GroupByJobName(), startTime);
				}

			};

		return _getBuildHistories(biConsumer, duration, null, null, startTime);
	}

	public static Collection<BuildHistory> newUtilizationBuildHistories(
		long duration, long startTime) {

		BiConsumer<Set<BuildJSONObject>, Map<String, BuildHistory>> biConsumer =
			new BiConsumer<Set<BuildJSONObject>, Map<String, BuildHistory>>() {

				@Override
				public void accept(
					Set<BuildJSONObject> buildJSONObjects,
					Map<String, BuildHistory> buildHistories) {

					_addToBuildHistoriesMap(
						buildJSONObjects, buildHistories, duration,
						new GroupByWeeklyUtilization(), startTime);
				}

			};

		return _getBuildHistories(biConsumer, duration, null, null, startTime);
	}

	public static Collection<BuildHistory> newUtilizationTestTypeBuildHistories(
		long duration, long startTime) {

		BiConsumer<Set<BuildJSONObject>, Map<String, BuildHistory>> biConsumer =
			new BiConsumer<Set<BuildJSONObject>, Map<String, BuildHistory>>() {

				@Override
				public void accept(
					Set<BuildJSONObject> buildJSONObjects,
					Map<String, BuildHistory> buildHistories) {

					_addToBuildHistoriesMap(
						buildJSONObjects, buildHistories, duration,
						new GroupByTestBatchType(), startTime);
				}

			};

		return _getBuildHistories(biConsumer, duration, null, null, startTime);
	}

	public static void setBaseDir(File baseDir) {
		_baseDir = baseDir;
	}

	private static void _addToBuildHistoriesMap(
		Collection<BuildJSONObject> buildJSONObjects,
		Map<String, BuildHistory> buildHistoriesMap, long duration,
		Function<BuildJSONObject, String> groupingFunction, long startTime) {

		Map<String, Set<BuildJSONObject>> groupedBuildDataJSONObjectsMap =
			_getGroupedBuildDataJSONObjectsMap(
				buildJSONObjects, groupingFunction);

		for (Map.Entry<String, Set<BuildJSONObject>> entry :
				groupedBuildDataJSONObjectsMap.entrySet()) {

			if (!buildHistoriesMap.containsKey(entry.getKey())) {
				BuildHistory buildHistory = new BuildHistory(
					duration, entry.getKey(), startTime);

				buildHistoriesMap.put(entry.getKey(), buildHistory);
			}

			BuildHistory buildHistory = buildHistoriesMap.get(entry.getKey());

			buildHistory.addBuildJSONObjects(entry.getValue());
		}
	}

	private static Collection<BuildHistory> _getBuildHistories(
		BiConsumer<Set<BuildJSONObject>, Map<String, BuildHistory>>
			buildHistoryBiConsumer,
		long duration, Pattern jobNameExcludesPattern,
		Pattern jobNameIncludesPattern, long startTime) {

		Map<String, BuildHistory> buildHistoriesMap = new HashMap<>();

		for (String dateString :
				JenkinsResultsParserUtil.getDateStrings(startTime, duration)) {

			Set<BuildJSONObject> buildJSONObjects = new HashSet<>();

			for (BuildJSONObject buildJSONObject :
					_getBuildJSONObjects(dateString)) {

				if (jobNameExcludesPattern != null) {
					Matcher jobNameExcludesMatcher =
						jobNameExcludesPattern.matcher(
							buildJSONObject.getJobName());

					if (jobNameExcludesMatcher.matches()) {
						continue;
					}
				}

				if (jobNameIncludesPattern == null) {
					buildJSONObjects.add(buildJSONObject);

					continue;
				}

				Matcher jobNameIncludesMatcher = jobNameIncludesPattern.matcher(
					buildJSONObject.getJobName());

				if (jobNameIncludesMatcher.matches()) {
					buildJSONObjects.add(buildJSONObject);
				}
			}

			buildHistoryBiConsumer.accept(buildJSONObjects, buildHistoriesMap);
		}

		return _getSortedBuildHistories(buildHistoriesMap.values());
	}

	private static Set<BuildJSONObject> _getBuildJSONObjects(
		String dateString) {

		File dateDir = new File(_baseDir, dateString);

		if (dateDir.listFiles() == null) {
			return Collections.emptySet();
		}

		Set<BuildJSONObject> buildJSONObjects = Collections.synchronizedSet(
			new HashSet<BuildJSONObject>());

		System.out.println("Reading files from: " + dateDir.toPath());

		List<Callable<Void>> callables = new ArrayList<>();

		for (final File jsonFile : dateDir.listFiles()) {
			callables.add(
				new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						try {
							String jsonFileName = jsonFile.getCanonicalPath();

							if (jsonFileName.contains("test-1-0")) {
								return null;
							}

							String content = JenkinsResultsParserUtil.read(
								jsonFile);

							JSONArray jsonArray = new JSONArray(content.trim());

							Set<BuildJSONObject> newBuildJSONObjects =
								new HashSet<>();

							for (int i = 0; i < jsonArray.length(); i++) {
								newBuildJSONObjects.add(
									new BuildJSONObject(
										jsonArray.getJSONObject(i)));
							}

							buildJSONObjects.addAll(newBuildJSONObjects);
						}
						catch (IOException ioException) {
							System.out.println("Unable to read " + jsonFile);
						}

						return null;
					}

				});
		}

		ParallelExecutor<Void> parallelExecutor = new ParallelExecutor<>(
			callables, _executorService, "_getBuildJSONObjects");

		try {
			parallelExecutor.execute();
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}

		return buildJSONObjects;
	}

	private static Map<String, Set<BuildJSONObject>>
		_getGroupedBuildDataJSONObjectsMap(
			Collection<BuildJSONObject> buildJSONObjects,
			Function<BuildJSONObject, String> groupingFunction) {

		Map<String, Set<BuildJSONObject>> groupedBuildDataJSONObjectsMap =
			new HashMap<>();

		for (BuildJSONObject buildJSONObject : buildJSONObjects) {
			String groupName = groupingFunction.apply(buildJSONObject);

			if (!groupedBuildDataJSONObjectsMap.containsKey(groupName)) {
				groupedBuildDataJSONObjectsMap.put(
					groupName, new HashSet<BuildJSONObject>());
			}

			Set<BuildJSONObject> groupedBuildJSONObjects =
				groupedBuildDataJSONObjectsMap.get(groupName);

			groupedBuildJSONObjects.add(buildJSONObject);
		}

		return groupedBuildDataJSONObjectsMap;
	}

	private static List<BuildHistory> _getSortedBuildHistories(
		Collection<BuildHistory> buildHistories) {

		List<BuildHistory> buildHistoryList = new ArrayList<>(buildHistories);

		Collections.sort(
			buildHistoryList,
			new Comparator<BuildHistory>() {

				@Override
				public int compare(
					BuildHistory buildHistory1, BuildHistory buildHistory2) {

					Integer buildCount1 =
						(int)buildHistory1.getInvokedBuildCount();
					Integer buildCount2 =
						(int)buildHistory2.getInvokedBuildCount();

					return buildCount2.compareTo(buildCount1);
				}

			});

		return buildHistoryList;
	}

	private static BuildHistory _mergeBuildHistories(
		List<BuildHistory> buildHistories, String name) {

		BuildHistory mergedBuildHistory = new BuildHistory(
			0, name, System.currentTimeMillis());

		for (BuildHistory buildHistory : buildHistories) {
			mergedBuildHistory.merge(buildHistory);
		}

		return mergedBuildHistory;
	}

	private static final Integer _THREAD_COUNT = 8;

	private static File _baseDir;
	private static final Properties _buildProperties;
	private static final ExecutorService _executorService =
		JenkinsResultsParserUtil.getNewThreadPoolExecutor(_THREAD_COUNT, true);

	static {
		_buildProperties = new Properties() {
			{
				try {
					putAll(JenkinsResultsParserUtil.getBuildProperties());
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
			}
		};

		_baseDir = new File(
			_buildProperties.getProperty("archive.ci.build.data.tmp.dir"),
			"builds");
	}

	private static class GroupByCategory
		implements Function<BuildJSONObject, String> {

		public String apply(BuildJSONObject buildJSONObject) {
			String jobName = buildJSONObject.getJobName();

			jobName = jobName.replace("-batch", "");
			jobName = jobName.replace("-downstream", "");
			jobName = jobName.replace("-validation", "");

			if (jobName.contains("maintenance-") ||
				jobName.contains("mirrors-") ||
				jobName.contains("verification-")) {

				return Category.MAINTENANCE.toString();
			}

			if (jobName.equals("test-portal-acceptance-pullrequest(master)")) {
				return Category.PORTAL_MASTER_PULLREQUEST.toString();
			}

			if (jobName.equals("test-portal-acceptance-upstream(master)") ||
				jobName.equals("test-portal-acceptance-upstream-dxp(master)") ||
				jobName.equals("test-portal-testsuite-upstream(master)")) {

				return Category.PORTAL_MASTER_UPSTREAM.toString();
			}

			if (jobName.equals("test-portal-fixpack-release") ||
				jobName.equals("test-portal-hotfix-release") ||
				jobName.equals("test-portal-release")) {

				return Category.PORTAL_RELEASE.toString();
			}

			if (jobName.contains("test-portal-")) {
				return Category.PORTAL_OTHER.toString();
			}

			return Category.OTHER.toString();
		}

		private enum Category {

			MAINTENANCE("CI Maintenance"), OTHER("Other"),
			PORTAL_MASTER_PULLREQUEST("liferay-portal/master PR's"),
			PORTAL_MASTER_UPSTREAM("liferay-portal/master Upstream"),
			PORTAL_OTHER("liferay-portal-ee PR's & Upstream"),
			PORTAL_OTHER_RELEASE("Portal Fixpack & Hotfix Release"),
			PORTAL_RELEASE("Portal Release");

			@Override
			public String toString() {
				return _string;
			}

			private Category(String string) {
				_string = string;
			}

			private final String _string;

		}

	}

	private static class GroupByJobName
		implements Function<BuildJSONObject, String> {

		public String apply(BuildJSONObject buildJSONObject) {
			String jobName = buildJSONObject.getJobName();

			String name = jobName.replace("-batch", "");

			name = name.replace("-downstream", "");
			name = name.replace("-validation", "");

			return name;
		}

	}

	private static class GroupByTestBatchType
		implements Function<BuildJSONObject, String> {

		public String apply(BuildJSONObject buildJSONObject) {
			String jobName = buildJSONObject.getJobName();

			if (jobName.contains("maintenance-") ||
				jobName.contains("mirrors-") ||
				jobName.contains("verification-")) {

				return TestBatchType.MAINTENANCE.toString();
			}

			if (buildJSONObject.isTopLevelBuild()) {
				return TestBatchType.TOP_LEVEL_BUILD.toString();
			}

			Map<String, String> parameters = buildJSONObject.getParameters();

			if (parameters.containsKey("JOB_VARIANT")) {
				String jobVariant = parameters.get("JOB_VARIANT");

				if (jobVariant.contains("functional")) {
					return TestBatchType.POSHI.toString();
				}

				if (jobVariant.contains("integration")) {
					return TestBatchType.INTEGRATION.toString();
				}

				if (jobVariant.startsWith("build-lib-versions") ||
					jobVariant.startsWith("empty-osgi-core-dir") ||
					jobVariant.startsWith("gogo-shell-client") ||
					jobVariant.startsWith("javadoc-test") ||
					jobVariant.startsWith("jsp-runtime-compile") ||
					jobVariant.startsWith("patching-tool") ||
					jobVariant.startsWith("poshi-validation") ||
					jobVariant.startsWith("source-format") ||
					jobVariant.startsWith("tck")) {

					return TestBatchType.MINIMAL.toString();
				}

				if (jobVariant.contains("playwright")) {
					return TestBatchType.PLAYWRIGHT.toString();
				}

				if ((jobVariant.startsWith("modules-unit") ||
					 jobVariant.startsWith("unit")) &&
					!jobVariant.contains("project-templates")) {

					return TestBatchType.UNIT.toString();
				}
			}

			return TestBatchType.OTHER.toString();
		}

		private enum TestBatchType {

			INTEGRATION("Integration"), MAINTENANCE("Maintenance"),
			MINIMAL("Minimal"), OTHER("Other"), PLAYWRIGHT("Playwright"),
			POSHI("Poshi"), TOP_LEVEL_BUILD("Top Level Build"), UNIT("Unit");

			@Override
			public String toString() {
				return _string;
			}

			private TestBatchType(String string) {
				_string = string;
			}

			private final String _string;

		}

	}

	private static class GroupByTopLevelTestSuite
		implements Function<BuildJSONObject, String> {

		public String apply(BuildJSONObject buildJSONObject) {
			String jobName = buildJSONObject.getJobName();

			if (jobName.contains("acceptance-upstream-dxp")) {
				return "acceptance-dxp";
			}

			if (buildJSONObject.isTopLevelBuild()) {
				Map<String, String> parameters =
					buildJSONObject.getParameters();

				if (parameters.containsKey("CI_TEST_SUITE")) {
					_topLevelBuildTestSuiteMap.put(
						buildJSONObject.getURL(),
						parameters.get("CI_TEST_SUITE"));

					return parameters.get("CI_TEST_SUITE");
				}

				return "[Unknown]";
			}

			String topLevelBuildURL = buildJSONObject.getTopLevelBuildURL();

			if (_topLevelBuildTestSuiteMap.containsKey(topLevelBuildURL)) {
				return _topLevelBuildTestSuiteMap.get(topLevelBuildURL);
			}

			return "[Unknown]";
		}

		private final Map<String, String> _topLevelBuildTestSuiteMap =
			new HashMap<>();

	}

	private static class GroupByWeeklyUtilization
		implements Function<BuildJSONObject, String> {

		public String apply(BuildJSONObject buildJSONObject) {
			String jobName = buildJSONObject.getJobName();

			LocalDate localDate = LocalDate.parse(
				buildJSONObject.getStartDateString(),
				DateTimeFormatter.ofPattern("yyyyMMdd"));

			DayOfWeek dayOfWeek = localDate.getDayOfWeek();

			boolean weekday = false;

			if (dayOfWeek.getValue() <= 5) {
				weekday = true;
			}

			if (jobName.contains("test-portal-acceptance-pullrequest")) {
				if (weekday) {
					return Category.PORTAL_PULLREQUEST_WEEKDAYS.toString();
				}

				return Category.PORTAL_PULLREQUEST_WEEKENDS.toString();
			}

			if (jobName.contains("release") || jobName.contains("upstream")) {
				if (weekday) {
					return Category.PORTAL_RELEASE_AND_UPSTREAM_WEEKDAYS.
						toString();
				}

				return Category.PORTAL_RELEASE_AND_UPSTREAM_WEEKENDS.toString();
			}

			if (weekday) {
				return Category.OTHER_WEEKDAYS.toString();
			}

			return Category.OTHER_WEEKENDS.toString();
		}

		private enum Category {

			OTHER_WEEKDAYS("Other (Weekdays)"),
			OTHER_WEEKENDS("Other (Weekends)"),
			PORTAL_PULLREQUEST_WEEKDAYS("Portal Pull Requests (Weekdays)"),
			PORTAL_PULLREQUEST_WEEKENDS("Portal Pull Requests (Weekends)"),
			PORTAL_RELEASE_AND_UPSTREAM_WEEKDAYS(
				"Portal Release & Upstream (Weekdays)"),
			PORTAL_RELEASE_AND_UPSTREAM_WEEKENDS(
				"Portal Release & Upstream (Weekends)");

			@Override
			public String toString() {
				return _string;
			}

			private Category(String string) {
				_string = string;
			}

			private final String _string;

		}

	}

}