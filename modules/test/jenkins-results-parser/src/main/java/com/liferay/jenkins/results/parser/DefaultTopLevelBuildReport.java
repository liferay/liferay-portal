/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class DefaultTopLevelBuildReport extends BaseTopLevelBuildReport {

	@Override
	public JSONObject getBuildReportJSONObject() {
		if (buildReportJSONObject != null) {
			return buildReportJSONObject;
		}

		buildReportJSONObject = _topLevelBuild.getBuildReportJSONObject();

		List<Callable<JSONObject>> callables = new ArrayList<>();

		ParallelExecutor<JSONObject> parallelExecutor = new ParallelExecutor<>(
			callables, _executorService, "getBuildReportJSONObject");

		for (final Build build : _topLevelBuild.getDownstreamBuilds(null)) {
			if (build instanceof BatchBuild) {
				BatchBuild batchBuild = (BatchBuild)build;

				for (final AxisBuild axisBuild :
						batchBuild.getDownstreamAxisBuilds()) {

					JenkinsMaster jenkinsMaster = axisBuild.getJenkinsMaster();

					callables.add(
						new ParallelExecutor.SequentialCallable<JSONObject>(
							jenkinsMaster.getName()) {

							@Override
							public JSONObject call() throws Exception {
								return axisBuild.getBuildReportJSONObject();
							}

						});
				}
			}
			else {
				JenkinsMaster jenkinsMaster = build.getJenkinsMaster();

				callables.add(
					new ParallelExecutor.SequentialCallable<JSONObject>(
						jenkinsMaster.getName()) {

						@Override
						public JSONObject call() throws Exception {
							return build.getBuildReportJSONObject();
						}

					});
			}
		}

		Map<String, List<JSONObject>> downstreamBuildMap = new HashMap<>();

		try {
			for (JSONObject jsonObject : parallelExecutor.execute(_TIMEOUT)) {
				String batchName = "default";

				Matcher matcher = _axisNamePattern.matcher(
					jsonObject.optString("axisName", ""));

				if (matcher.find()) {
					batchName = matcher.group("batchName");
				}

				List<JSONObject> downstreamBuildJSONObjects =
					downstreamBuildMap.getOrDefault(
						batchName, new ArrayList<JSONObject>());

				downstreamBuildJSONObjects.add(jsonObject);

				downstreamBuildMap.put(batchName, downstreamBuildJSONObjects);
			}
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}

		JSONArray batchesJSONArray = new JSONArray();

		for (Map.Entry<String, List<JSONObject>> downstreamBuildEntry :
				downstreamBuildMap.entrySet()) {

			JSONObject batchJSONObject = new JSONObject();

			batchJSONObject.put(
				"batchName", downstreamBuildEntry.getKey()
			).put(
				"builds", downstreamBuildEntry.getValue()
			);

			batchesJSONArray.put(batchJSONObject);
		}

		buildReportJSONObject.put("batches", batchesJSONArray);

		Build controllerBuild = _topLevelBuild.getControllerBuild();

		if (controllerBuild != null) {
			buildReportJSONObject.put(
				"controller", controllerBuild.getBuildReportJSONObject());
		}

		return buildReportJSONObject;
	}

	protected DefaultTopLevelBuildReport(TopLevelBuild topLevelBuild) {
		super(topLevelBuild);

		_topLevelBuild = topLevelBuild;

		_jenkinsConsoleLocalFile = new File(
			System.getenv("WORKSPACE"),
			JenkinsResultsParserUtil.getDistinctTimeStamp());

		try {
			JenkinsResultsParserUtil.write(
				_jenkinsConsoleLocalFile, topLevelBuild.getConsoleText());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Override
	protected JSONObject getBuildJSONObject() {
		return _topLevelBuild.getBuildJSONObject();
	}

	@Override
	protected File getJenkinsConsoleLocalFile() {
		return _jenkinsConsoleLocalFile;
	}

	private static final long _TIMEOUT = 60L * 60L * 6L;

	private static final Pattern _axisNamePattern = Pattern.compile(
		"(?<batchName>[^/]+)/[^/]+/[^/]+");
	private static final ExecutorService _executorService =
		JenkinsResultsParserUtil.getNewThreadPoolExecutor(30, true);

	private final File _jenkinsConsoleLocalFile;
	private final TopLevelBuild _topLevelBuild;

}