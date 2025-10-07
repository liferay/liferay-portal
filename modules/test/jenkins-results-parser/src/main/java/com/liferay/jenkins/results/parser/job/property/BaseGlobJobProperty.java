/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.job.property;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Job;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.PortalTestClassJob;

import java.io.File;

import java.nio.file.PathMatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseGlobJobProperty
	extends BaseTestDirJobProperty implements GlobJobProperty {

	public Map<String, List<String>> getGlobTestClassMethodNamesMap() {
		return _globTestClassMethodNamesMap;
	}

	@Override
	public List<PathMatcher> getPathMatchers() {
		List<PathMatcher> pathMatchers =
			JenkinsResultsParserUtil.toPathMatchers(
				_getWorkingDirectory() + "/", getRelativeGlobs());

		pathMatchers.removeAll(Collections.singleton(null));

		return pathMatchers;
	}

	@Override
	public List<String> getRelativeGlobs() {
		List<String> relativeGlobs = new ArrayList<>();

		String value = getValue();

		if (JenkinsResultsParserUtil.isNullOrEmpty(value)) {
			return relativeGlobs;
		}

		String relativePath = getRelativePath();

		for (String glob : value.split(",(?![^{}]*})")) {
			String relativeGlob = relativePath + "/" + glob;

			relativeGlob = relativeGlob.replaceAll("/+", "/");

			if (relativeGlob.startsWith("/")) {
				relativeGlob = relativeGlob.substring(1);
			}

			Matcher matcher = _globClassMethodPattern.matcher(relativeGlob);

			if (matcher.matches()) {
				String testClassGlob = matcher.group("testClassGlob");
				String testClassMethodName = matcher.group(
					"testClassMethodName");

				relativeGlob = testClassGlob;

				List<String> testClassMethodNames;

				if (_globTestClassMethodNamesMap.containsKey(testClassGlob)) {
					testClassMethodNames = _globTestClassMethodNamesMap.get(
						testClassGlob);

					testClassMethodNames.add(testClassMethodName);

					_globTestClassMethodNamesMap.replace(
						relativeGlob, testClassMethodNames);
				}
				else {
					testClassMethodNames = new ArrayList<>();

					testClassMethodNames.add(testClassMethodName);

					_globTestClassMethodNamesMap.put(
						relativeGlob, testClassMethodNames);
				}
			}

			relativeGlobs.add(relativeGlob);
		}

		return relativeGlobs;
	}

	@Override
	public File getTestBaseDir() {
		File testBaseDir = super.getTestBaseDir();

		if (testBaseDir != null) {
			return testBaseDir;
		}

		Job job = getJob();

		if (job instanceof PortalTestClassJob) {
			PortalTestClassJob portalTestClassJob = (PortalTestClassJob)job;

			PortalGitWorkingDirectory portalGitWorkingDirectory =
				portalTestClassJob.getPortalGitWorkingDirectory();

			return portalGitWorkingDirectory.getWorkingDirectory();
		}

		return null;
	}

	protected BaseGlobJobProperty(
		Job job, Type type, File testBaseDir, String basePropertyName,
		boolean useBasePropertyName, String testSuiteName,
		String testBatchName) {

		super(
			job, type, testBaseDir, basePropertyName, useBasePropertyName,
			testSuiteName, testBatchName);
	}

	protected BaseGlobJobProperty(
		Job job, Type type, File testBaseDir, String basePropertyName,
		boolean useBasePropertyName, String testSuiteName, String testBatchName,
		String ruleName) {

		super(
			job, type, testBaseDir, basePropertyName, useBasePropertyName,
			testSuiteName, testBatchName, ruleName);
	}

	protected String getRelativePath() {
		File gitWorkingDirectory = _getWorkingDirectory();
		File testBaseDir = getTestBaseDir();

		if (Objects.equals(gitWorkingDirectory, testBaseDir)) {
			return "";
		}

		return JenkinsResultsParserUtil.getPathRelativeTo(
			testBaseDir, gitWorkingDirectory);
	}

	private File _getWorkingDirectory() {
		File testBaseDir = getTestBaseDir();

		if (testBaseDir != null) {
			return getWorkingDirectory(testBaseDir);
		}

		Job job = getJob();

		if (!(job instanceof PortalTestClassJob)) {
			return new File(".");
		}

		PortalTestClassJob portalTestClassJob = (PortalTestClassJob)job;

		PortalGitWorkingDirectory portalGitWorkingDirectory =
			portalTestClassJob.getPortalGitWorkingDirectory();

		return portalGitWorkingDirectory.getWorkingDirectory();
	}

	private static final Pattern _globClassMethodPattern = Pattern.compile(
		"(?<testClassGlob>[^#]+)#(?<testClassMethodName>.+)");

	private final Map<String, List<String>> _globTestClassMethodNamesMap =
		new HashMap<>();

}