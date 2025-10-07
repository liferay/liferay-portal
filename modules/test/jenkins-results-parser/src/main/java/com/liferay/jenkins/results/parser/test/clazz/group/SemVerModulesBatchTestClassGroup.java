/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.PortalTestClassJob;

import java.io.File;
import java.io.IOException;

import java.nio.file.PathMatcher;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Leslie Wong
 */
public class SemVerModulesBatchTestClassGroup
	extends ModulesBatchTestClassGroup {

	protected SemVerModulesBatchTestClassGroup(
		JSONObject jsonObject, PortalTestClassJob portalTestClassJob) {

		super(jsonObject, portalTestClassJob);
	}

	protected SemVerModulesBatchTestClassGroup(
		String batchName, PortalTestClassJob portalTestClassJob) {

		super(batchName, portalTestClassJob);
	}

	@Override
	protected boolean ignore() {
		if (!isStableTestSuiteBatch() && testRelevantJUnitTestsOnly) {
			return true;
		}

		if ((isStableTestSuiteBatch() && testRelevantJUnitTestsOnlyInStable) ||
			isQuarterlyReleaseBranch()) {

			return true;
		}

		return false;
	}

	protected boolean isQuarterlyReleaseBranch() {
		Matcher quarterlyReleaseNameMatcher =
			_quarterlyReleaseNamePattern.matcher(
				portalGitWorkingDirectory.getUpstreamBranchName());

		return quarterlyReleaseNameMatcher.find();
	}

	@Override
	protected void setTestClasses() throws IOException {
		Set<File> moduleDirs = new HashSet<>();

		List<PathMatcher> excludesPathMatchers = getPathMatchers(
			getExcludesJobProperties());
		List<PathMatcher> includesPathMatchers = getIncludesPathMatchers();

		PortalGitWorkingDirectory portalGitWorkingDirectory =
			getPortalGitWorkingDirectory();

		File portalModulesBaseDir = new File(
			portalGitWorkingDirectory.getWorkingDirectory(), "modules");

		if (testRelevantChanges &&
			!(includeStableTestSuite && isStableTestSuiteBatch())) {

			moduleDirs.addAll(
				portalGitWorkingDirectory.getModifiedModuleDirsList(
					excludesPathMatchers, includesPathMatchers));
		}
		else if (isRootCauseAnalysis()) {
			moduleDirs.addAll(
				portalGitWorkingDirectory.getModuleDirsList(
					excludesPathMatchers, includesPathMatchers));
		}
		else {
			moduleDirs.addAll(
				portalGitWorkingDirectory.getModuleDirsList(
					excludesPathMatchers, includesPathMatchers));

			List<File> semVerMarkerFiles = JenkinsResultsParserUtil.findFiles(
				portalModulesBaseDir, "\\.lfrbuild-semantic-versioning");

			semVerMarkerFiles = JenkinsResultsParserUtil.getIncludedFiles(
				excludesPathMatchers, includesPathMatchers, semVerMarkerFiles);

			for (File semVerMarkerFile : semVerMarkerFiles) {
				moduleDirs.add(semVerMarkerFile.getParentFile());
			}
		}

		for (File moduleDir : moduleDirs) {
			List<File> bndBndFiles = JenkinsResultsParserUtil.findFiles(
				moduleDir, "bnd.bnd");

			boolean exportPackageModule = false;

			for (File bndBndFile : bndBndFiles) {
				String bndBndFileContent = JenkinsResultsParserUtil.read(
					bndBndFile);

				if ((bndBndFileContent == null) ||
					!bndBndFileContent.contains("Export-Package:")) {

					continue;
				}

				exportPackageModule = true;
			}

			if (!exportPackageModule) {
				continue;
			}

			moduleDirsList.add(moduleDir);
		}

		addTestClasses(moduleDirsList);
	}

	private static final Pattern _quarterlyReleaseNamePattern = Pattern.compile(
		"(release-\\d{4}.[qQ](.\\d)?)");

}