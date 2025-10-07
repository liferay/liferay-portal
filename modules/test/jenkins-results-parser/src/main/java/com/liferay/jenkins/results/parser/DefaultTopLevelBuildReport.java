/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.net.URL;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class DefaultTopLevelBuildReport extends BaseTopLevelBuildReport {

	@Override
	public void addTestrayAttachmentURL(URL testrayAttachmentURL) {
		if (_testrayAttachmentURLs.contains(testrayAttachmentURL)) {
			return;
		}

		_testrayAttachmentURLs.add(testrayAttachmentURL);
	}

	@Override
	public JSONObject getBuildReportJSONObject() {
		JSONObject buildReportJSONObject =
			_topLevelBuild.getBuildReportJSONObject();

		Map<String, Set<DownstreamBuildReport>> downstreamBuildReportsMap =
			new HashMap<>();

		for (DownstreamBuildReport downstreamBuildReport :
				getDownstreamBuildReports()) {

			String batchName = downstreamBuildReport.getBatchName();

			Set<DownstreamBuildReport> downstreamBuildReports =
				downstreamBuildReportsMap.getOrDefault(
					batchName, new HashSet<>());

			downstreamBuildReports.add(downstreamBuildReport);

			downstreamBuildReportsMap.put(batchName, downstreamBuildReports);
		}

		JSONArray batchesJSONArray = new JSONArray();

		for (Map.Entry<String, Set<DownstreamBuildReport>> entry :
				downstreamBuildReportsMap.entrySet()) {

			JSONArray buildsJSONArray = new JSONArray();

			for (DownstreamBuildReport downstreamBuildReport :
					entry.getValue()) {

				buildsJSONArray.put(
					downstreamBuildReport.getBuildReportJSONObject());
			}

			JSONObject batchJSONObject = new JSONObject();

			batchJSONObject.put(
				"batchName", entry.getKey()
			).put(
				"builds", buildsJSONArray
			);

			batchesJSONArray.put(batchJSONObject);
		}

		buildReportJSONObject.put("batches", batchesJSONArray);

		Build controllerBuild = _topLevelBuild.getControllerBuild();

		if (controllerBuild != null) {
			buildReportJSONObject.put(
				"controller", controllerBuild.getBuildReportJSONObject());
		}

		buildReportJSONObject.put(
			"testrayAttachmentURLs", _getTestrayAttachmentURLStrings());

		return buildReportJSONObject;
	}

	@Override
	public List<DownstreamBuildReport> getDownstreamBuildReports() {
		Set<DownstreamBuildReport> downstreamBuildReports = new HashSet<>(
			super.getDownstreamBuildReports());

		for (Build build : _topLevelBuild.getDownstreamBuilds()) {
			if (!build.isCompleted()) {
				continue;
			}

			String batchName = _getBatchName(build);

			if (JenkinsResultsParserUtil.isNullOrEmpty(batchName)) {
				continue;
			}

			downstreamBuildReports.add(
				BuildReportFactory.newDownstreamBuildReport(
					batchName, build.getBuildReportJSONObject(), this));
		}

		return new ArrayList<>(downstreamBuildReports);
	}

	@Override
	public Date getStartDate() {
		return new Date(_topLevelBuild.getStartTime());
	}

	protected DefaultTopLevelBuildReport(TopLevelBuild topLevelBuild) {
		super(topLevelBuild.getBuildURL());

		_topLevelBuild = topLevelBuild;
	}

	private String _getBatchName(Build build) {
		if (build instanceof AxisBuild) {
			AxisBuild axisBuild = (AxisBuild)build;

			return axisBuild.getBatchName();
		}
		else if (build instanceof BatchBuild) {
			BatchBuild batchBuild = (BatchBuild)build;

			return batchBuild.getBatchName();
		}
		else if (build instanceof DownstreamBuild) {
			DownstreamBuild downstreamBuild = (DownstreamBuild)build;

			return downstreamBuild.getBatchName();
		}

		return null;
	}

	private List<String> _getTestrayAttachmentURLStrings() {
		List<String> testrayAttachmentURLStrings = new ArrayList<>();

		List<URL> testrayAttachmentURLs = new ArrayList<>();

		testrayAttachmentURLs.addAll(_testrayAttachmentURLs);
		testrayAttachmentURLs.addAll(_topLevelBuild.getTestrayAttachmentURLs());

		for (URL testrayAttachmentURL : testrayAttachmentURLs) {
			String testrayAttachmentURLString = String.valueOf(
				testrayAttachmentURL);

			if (testrayAttachmentURLStrings.contains(
					testrayAttachmentURLString)) {

				continue;
			}

			testrayAttachmentURLStrings.add(testrayAttachmentURLString);
		}

		return testrayAttachmentURLStrings;
	}

	private final List<URL> _testrayAttachmentURLs = new ArrayList<>();
	private final TopLevelBuild _topLevelBuild;

}