/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.job.property;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Job;
import com.liferay.jenkins.results.parser.TestSuiteJob;

import java.io.File;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Hashimoto
 */
public class JobPropertyFactory {

	public static JobProperty newJobProperty(String basePropertyName, Job job) {
		return newJobProperty(
			basePropertyName, null, null, job, null, null, true);
	}

	public static JobProperty newJobProperty(
		String basePropertyName, Job job, File testBaseDir,
		JobProperty.Type type) {

		return newJobProperty(
			basePropertyName, null, null, job, testBaseDir, type, true);
	}

	public static JobProperty newJobProperty(
		String basePropertyName, String testBatchName, Job job) {

		return newJobProperty(
			basePropertyName, null, testBatchName, job, null, null, true);
	}

	public static JobProperty newJobProperty(
		String basePropertyName, String testBatchName, Job job,
		File testBaseDir, JobProperty.Type type) {

		return newJobProperty(
			basePropertyName, null, testBatchName, job, testBaseDir, type,
			true);
	}

	public static JobProperty newJobProperty(
		String basePropertyName, String testSuiteName, String testBatchName,
		Job job, File testBaseDir, JobProperty.Type type,
		boolean useBasePropertyName) {

		StringBuilder sb = new StringBuilder();

		sb.append(basePropertyName);
		sb.append("_");

		if (JenkinsResultsParserUtil.isNullOrEmpty(testSuiteName) &&
			(job instanceof TestSuiteJob)) {

			TestSuiteJob testSuiteJob = (TestSuiteJob)job;

			testSuiteName = testSuiteJob.getTestSuiteName();
		}

		sb.append(testSuiteName);
		sb.append("_");
		sb.append(testBatchName);
		sb.append("_");
		sb.append(job.getJobName());

		if (testBaseDir != null) {
			sb.append("_");
			sb.append(JenkinsResultsParserUtil.getCanonicalPath(testBaseDir));
		}

		if (type == null) {
			if (testBaseDir != null) {
				type = JobProperty.Type.DEFAULT_TEST_DIR;
			}
			else {
				type = JobProperty.Type.DEFAULT;
			}
		}

		sb.append("_");
		sb.append(type);
		sb.append("_");
		sb.append(useBasePropertyName);

		String key = sb.toString();

		JobProperty jobProperty = _jobProperties.get(key);

		if (jobProperty != null) {
			return jobProperty;
		}

		if (type == JobProperty.Type.DEFAULT_TEST_DIR) {
			jobProperty = new DefaultTestDirProperty(
				job, type, testBaseDir, basePropertyName, useBasePropertyName,
				testSuiteName, testBatchName);
		}
		else if ((type == JobProperty.Type.EXCLUDE_GLOB) ||
				 (type == JobProperty.Type.FILTER_GLOB) ||
				 (type == JobProperty.Type.INCLUDE_GLOB)) {

			if (testBatchName.equals("modules-integration-analytics-cloud") &&
				(testBaseDir == null)) {

				testBaseDir = new File(
					"/opt/dev/projects/github/com-liferay-osb-asah-private");
			}

			jobProperty = new DefaultGlobJobProperty(
				job, type, testBaseDir, basePropertyName, useBasePropertyName,
				testSuiteName, testBatchName);
		}
		else if ((type == JobProperty.Type.MODULE_EXCLUDE_GLOB) ||
				 (type == JobProperty.Type.MODULE_INCLUDE_GLOB)) {

			jobProperty = new ModuleGlobJobProperty(
				job, type, testBaseDir, basePropertyName, useBasePropertyName,
				testSuiteName, testBatchName);
		}
		else if (type == JobProperty.Type.MODULE_TEST_DIR) {
			jobProperty = new ModuleTestDirJobProperty(
				job, type, testBaseDir, basePropertyName, useBasePropertyName,
				testSuiteName, testBatchName);
		}
		else if (type == JobProperty.Type.PLUGIN_TEST_DIR) {
			jobProperty = new PluginTestDirProperty(
				job, type, testBaseDir, basePropertyName, useBasePropertyName,
				testSuiteName, testBatchName);
		}
		else if (type == JobProperty.Type.QA_WEBSITES_TEST_DIR) {
			jobProperty = new QAWebsitesTestDirJobProperty(
				job, type, testBaseDir, basePropertyName, useBasePropertyName,
				testSuiteName, testBatchName);
		}
		else {
			jobProperty = new DefaultJobProperty(
				job, type, basePropertyName, useBasePropertyName, testSuiteName,
				testBatchName);
		}

		_jobProperties.put(key, jobProperty);

		return _jobProperties.get(key);
	}

	public static JobProperty newJobProperty(
		String basePropertyName, String testSuiteName, String testBatchName,
		String ruleName, Job job, File testBaseDir, JobProperty.Type type,
		boolean useBasePropertyName) {

		StringBuilder sb = new StringBuilder();

		sb.append(basePropertyName);
		sb.append("_");

		if (JenkinsResultsParserUtil.isNullOrEmpty(testSuiteName) &&
			(job instanceof TestSuiteJob)) {

			TestSuiteJob testSuiteJob = (TestSuiteJob)job;

			testSuiteName = testSuiteJob.getTestSuiteName();
		}

		sb.append(testSuiteName);
		sb.append("_");

		if (testBatchName != null) {
			sb.append(testBatchName);
			sb.append("_");
		}

		sb.append(ruleName);
		sb.append("_");
		sb.append(job.getJobName());

		if (testBaseDir != null) {
			sb.append("_");
			sb.append(JenkinsResultsParserUtil.getCanonicalPath(testBaseDir));
		}

		if (type == null) {
			if (testBaseDir != null) {
				type = JobProperty.Type.DEFAULT_TEST_DIR;
			}
			else {
				type = JobProperty.Type.DEFAULT;
			}
		}

		sb.append("_");
		sb.append(type);
		sb.append("_");
		sb.append(useBasePropertyName);

		String key = sb.toString();

		JobProperty jobProperty = _jobProperties.get(key);

		if (jobProperty != null) {
			return jobProperty;
		}

		if (type == JobProperty.Type.DEFAULT_TEST_DIR) {
			jobProperty = new DefaultTestDirProperty(
				job, type, testBaseDir, basePropertyName, useBasePropertyName,
				testSuiteName, testBatchName, ruleName);
		}
		else if ((type == JobProperty.Type.EXCLUDE_GLOB) ||
				 (type == JobProperty.Type.FILTER_GLOB) ||
				 (type == JobProperty.Type.INCLUDE_GLOB)) {

			if (testBatchName.equals("modules-integration-analytics-cloud") &&
				(testBaseDir == null)) {

				testBaseDir = new File(
					"/opt/dev/projects/github/com-liferay-osb-asah-private");
			}

			jobProperty = new DefaultGlobJobProperty(
				job, type, testBaseDir, basePropertyName, useBasePropertyName,
				testSuiteName, testBatchName, ruleName);
		}
		else if ((type == JobProperty.Type.MODULE_EXCLUDE_GLOB) ||
				 (type == JobProperty.Type.MODULE_INCLUDE_GLOB)) {

			jobProperty = new ModuleGlobJobProperty(
				job, type, testBaseDir, basePropertyName, useBasePropertyName,
				testSuiteName, testBatchName, ruleName);
		}
		else if (type == JobProperty.Type.MODULE_TEST_DIR) {
			jobProperty = new ModuleTestDirJobProperty(
				job, type, testBaseDir, basePropertyName, useBasePropertyName,
				testSuiteName, testBatchName, ruleName);
		}
		else if (type == JobProperty.Type.PLUGIN_TEST_DIR) {
			jobProperty = new PluginTestDirProperty(
				job, type, testBaseDir, basePropertyName, useBasePropertyName,
				testSuiteName, testBatchName, ruleName);
		}
		else if (type == JobProperty.Type.QA_WEBSITES_TEST_DIR) {
			jobProperty = new QAWebsitesTestDirJobProperty(
				job, type, testBaseDir, basePropertyName, useBasePropertyName,
				testSuiteName, testBatchName, ruleName);
		}
		else {
			jobProperty = new DefaultJobProperty(
				job, type, basePropertyName, useBasePropertyName, testSuiteName,
				testBatchName, ruleName);
		}

		_jobProperties.put(key, jobProperty);

		return _jobProperties.get(key);
	}

	private static final Map<String, JobProperty> _jobProperties =
		new HashMap<>();

}