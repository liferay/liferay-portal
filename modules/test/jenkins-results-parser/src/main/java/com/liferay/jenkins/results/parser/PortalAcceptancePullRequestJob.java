/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.job.property.JobProperty;
import com.liferay.jenkins.results.parser.job.property.JobPropertyFactory;
import com.liferay.jenkins.results.parser.test.batch.TestBatch;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.suite.RelevantTestSuite;

import java.io.File;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class PortalAcceptancePullRequestJob
	extends PortalAcceptanceTestSuiteJob implements PortalWorkspaceJob {

	@Override
	public List<BatchTestClassGroup> getBatchTestClassGroups() {
		synchronized (jobProperties) {
			if (batchTestClassGroups != null) {
				return batchTestClassGroups;
			}

			if ((jsonObject != null) && jsonObject.has("batches")) {
				batchTestClassGroups = super.getBatchTestClassGroups();

				return batchTestClassGroups;
			}

			PortalGitWorkingDirectory portalGitWorkingDirectory =
				getPortalGitWorkingDirectory();

			Properties testProperties = JenkinsResultsParserUtil.getProperties(
				new File(
					portalGitWorkingDirectory.getWorkingDirectory(),
					"test.properties"));

			boolean relevantEngineEnabled = Boolean.parseBoolean(
				testProperties.getProperty("relevant.engine.enabled"));

			if (_isRelevantTestSuite() && relevantEngineEnabled) {
				System.out.println("Relevant engine is enabled");

				batchTestClassGroups = Collections.synchronizedList(
					new ArrayList<BatchTestClassGroup>());

				batchTestClassGroups.addAll(
					getBatchTestClassGroups(getTestBatches()));

				return batchTestClassGroups;
			}

			if (Objects.equals(getTestSuiteName(), "stable") &&
				_isRelevantTestSuite() && relevantEngineEnabled) {

				batchTestClassGroups = Collections.synchronizedList(
					getBatchTestClassGroups(getStableRuleBatchNames()));

				return batchTestClassGroups;
			}

			return super.getBatchTestClassGroups();
		}
	}

	public boolean isCentralMergePullRequest() {
		if (_centralMergePullRequest != null) {
			return _centralMergePullRequest;
		}

		GitWorkingDirectory gitWorkingDirectory = getGitWorkingDirectory();

		List<File> currentBranchModifiedFiles =
			gitWorkingDirectory.getModifiedFilesList();

		if (currentBranchModifiedFiles.size() == 1) {
			File modifiedFile = currentBranchModifiedFiles.get(0);

			String modifiedFileName = modifiedFile.getName();

			if (modifiedFileName.equals("ci-merge")) {
				_centralMergePullRequest = true;

				return _centralMergePullRequest;
			}
		}

		_centralMergePullRequest = false;

		return _centralMergePullRequest;
	}

	@Override
	public boolean isStandaloneBatchEnabled() {
		return true;
	}

	protected PortalAcceptancePullRequestJob(
		BuildProfile buildProfile, String jobName,
		PortalGitWorkingDirectory portalGitWorkingDirectory,
		String testSuiteName, String upstreamBranchName) {

		super(
			buildProfile, jobName, portalGitWorkingDirectory, testSuiteName,
			upstreamBranchName);
	}

	protected PortalAcceptancePullRequestJob(JSONObject jsonObject) {
		super(jsonObject);
	}

	@Override
	protected Set<String> getRawBatchNames() {
		Set<String> batchNames = super.getRawBatchNames();

		if (_isRelevantTestSuite() &&
			!_hasMatchingFiles(_restBuilderFilePathMatchers)) {

			batchNames.remove("rest-builder");
		}

		if (_isRelevantTestSuite() && _hasOnlyFilesInDirectory("modules")) {
			batchNames.remove("semantic-versioning");
		}

		if (_isRelevantTestSuite() && _hasOnlyFilesInDirectory("portal-web")) {
			String[] portalWebOnlyBatchNameMarkers = {
				"compile-jsp", "functional", "portal-web", "source-format"
			};

			Set<String> portalWebOnlyBatchNames = new TreeSet<>();

			for (String batchName : batchNames) {
				for (String portalWebOnlyBatchNameMarker :
						portalWebOnlyBatchNameMarkers) {

					if (batchName.contains(portalWebOnlyBatchNameMarker)) {
						portalWebOnlyBatchNames.add(batchName);

						break;
					}
				}
			}

			return portalWebOnlyBatchNames;
		}

		return batchNames;
	}

	protected Set<String> getStableRuleBatchNames() {
		PortalGitWorkingDirectory portalGitWorkingDirectory =
			getPortalGitWorkingDirectory();

		File baseDir = portalGitWorkingDirectory.getWorkingDirectory();

		JobProperty jobProperty = JobPropertyFactory.newJobProperty(
			"test.batch.names", "relevant", null, "stable-rule", this, baseDir,
			JobProperty.Type.DEFAULT_TEST_DIR, true);

		recordJobProperty(jobProperty);

		return getSetFromString(jobProperty.getValue());
	}

	@Override
	protected List<TestBatch> getTestBatches() {
		if (!_isRelevantTestSuite()) {
			return super.getTestBatches();
		}

		RelevantTestSuite relevantTestSuite = new RelevantTestSuite(this);

		List<TestBatch> testBatches = relevantTestSuite.getTestBatches(true);

		recordJobProperties(relevantTestSuite.getTestBatchNamesJobProperties());

		return testBatches;
	}

	private boolean _hasMatchingFiles(List<PathMatcher> pathMatchers) {
		GitWorkingDirectory gitWorkingDirectory = getGitWorkingDirectory();

		for (File modifiedFile : gitWorkingDirectory.getModifiedFilesList()) {
			for (PathMatcher pathMatcher : pathMatchers) {
				if (pathMatcher.matches(modifiedFile.toPath())) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean _hasOnlyFilesInDirectory(String directoryName) {
		GitWorkingDirectory gitWorkingDirectory = getGitWorkingDirectory();

		File directory = new File(
			gitWorkingDirectory.getWorkingDirectory(), directoryName);

		for (File modifiedFile : gitWorkingDirectory.getModifiedFilesList()) {
			if (!JenkinsResultsParserUtil.isFileInDirectory(
					directory, modifiedFile)) {

				return false;
			}
		}

		return true;
	}

	private boolean _isRelevantTestSuite() {
		String testSuiteName = getTestSuiteName();

		return testSuiteName.equals("relevant");
	}

	private static List<PathMatcher> _restBuilderFilePathMatchers;

	static {
		FileSystem fs = FileSystems.getDefault();

		_restBuilderFilePathMatchers = Arrays.asList(
			fs.getPathMatcher("glob:**/portal-tools-rest-builder/**"),
			fs.getPathMatcher("glob:**/rest-config*.yaml"),
			fs.getPathMatcher("glob:**/rest-openapi*.yaml"));
	}

	private Boolean _centralMergePullRequest;

}