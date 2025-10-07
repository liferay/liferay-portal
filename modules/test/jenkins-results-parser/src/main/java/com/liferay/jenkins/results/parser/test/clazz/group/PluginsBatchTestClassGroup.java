/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.PluginsGitWorkingDirectory;
import com.liferay.jenkins.results.parser.PortalTestClassJob;
import com.liferay.jenkins.results.parser.job.property.JobProperty;
import com.liferay.jenkins.results.parser.test.clazz.TestClassFactory;

import java.io.File;
import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class PluginsBatchTestClassGroup extends BatchTestClassGroup {

	@Override
	public JSONObject getJSONObject() {
		if (jsonObject != null) {
			return jsonObject;
		}

		jsonObject = super.getJSONObject();

		jsonObject.put("exclude_globs", getGlobs(_getExcludesJobProperties()));
		jsonObject.put("include_globs", getGlobs(_getIncludesJobProperties()));

		return jsonObject;
	}

	protected PluginsBatchTestClassGroup(
		JSONObject jsonObject, PortalTestClassJob portalTestClassJob) {

		super(jsonObject, portalTestClassJob);

		_pluginsGitWorkingDirectory =
			portalGitWorkingDirectory.getPluginsGitWorkingDirectory();
	}

	protected PluginsBatchTestClassGroup(
		String batchName, PortalTestClassJob portalTestClassJob) {

		super(batchName, portalTestClassJob);

		if (ignore()) {
			_pluginsGitWorkingDirectory = null;

			return;
		}

		_pluginsGitWorkingDirectory =
			portalGitWorkingDirectory.getPluginsGitWorkingDirectory();

		setTestClasses();

		setAxisTestClassGroups();

		setSegmentTestClassGroups();
	}

	protected void setTestClasses() {
		final List<PathMatcher> includesPathMatchers = getPathMatchers(
			_getIncludesJobProperties());

		if (includesPathMatchers.isEmpty()) {
			return;
		}

		File workingDirectory =
			_pluginsGitWorkingDirectory.getWorkingDirectory();

		final List<PathMatcher> excludesPathMatchers = getPathMatchers(
			_getExcludesJobProperties());

		final List<File> pluginsDirs = new ArrayList<>();

		try {
			Files.walkFileTree(
				workingDirectory.toPath(),
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(
							Path filePath,
							BasicFileAttributes basicFileAttributes)
						throws IOException {

						if (JenkinsResultsParserUtil.isFileExcluded(
								excludesPathMatchers, filePath)) {

							return FileVisitResult.SKIP_SUBTREE;
						}

						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(
							Path filePath,
							BasicFileAttributes basicFileAttributes)
						throws IOException {

						if (JenkinsResultsParserUtil.isFileIncluded(
								excludesPathMatchers, includesPathMatchers,
								filePath)) {

							File file = filePath.toFile();

							pluginsDirs.add(file.getParentFile());
						}

						return FileVisitResult.CONTINUE;
					}

				});
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to search for test file names in " +
					workingDirectory.getPath(),
				ioException);
		}

		for (File pluginsDir : pluginsDirs) {
			addTestClass(TestClassFactory.newTestClass(this, pluginsDir));
		}
	}

	private List<JobProperty> _getExcludesJobProperties() {
		List<JobProperty> excludesJobProperties = new ArrayList<>();

		excludesJobProperties.add(
			getJobProperty(
				"test.batch.plugin.names.excludes",
				_pluginsGitWorkingDirectory.getWorkingDirectory(),
				JobProperty.Type.EXCLUDE_GLOB));

		if (includeStableTestSuite && isStableTestSuiteBatch()) {
			excludesJobProperties.add(
				getJobProperty(
					"test.batch.plugin.names.excludes", NAME_STABLE_TEST_SUITE,
					_pluginsGitWorkingDirectory.getWorkingDirectory(),
					JobProperty.Type.EXCLUDE_GLOB));
		}

		recordJobProperties(excludesJobProperties);

		return excludesJobProperties;
	}

	private List<JobProperty> _getIncludesJobProperties() {
		List<JobProperty> includesJobProperties = new ArrayList<>();

		includesJobProperties.add(
			getJobProperty(
				"test.batch.plugin.names.includes",
				_pluginsGitWorkingDirectory.getWorkingDirectory(),
				JobProperty.Type.INCLUDE_GLOB));

		if (includeStableTestSuite && isStableTestSuiteBatch()) {
			includesJobProperties.add(
				getJobProperty(
					"test.batch.plugin.names.includes", NAME_STABLE_TEST_SUITE,
					_pluginsGitWorkingDirectory.getWorkingDirectory(),
					JobProperty.Type.INCLUDE_GLOB));
		}

		recordJobProperties(includesJobProperties);

		return includesJobProperties;
	}

	private final PluginsGitWorkingDirectory _pluginsGitWorkingDirectory;

}