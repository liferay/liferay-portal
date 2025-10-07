/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
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
public class TCKJunitBatchTestClassGroup extends BatchTestClassGroup {

	@Override
	public JSONObject getJSONObject() {
		if (jsonObject != null) {
			return jsonObject;
		}

		jsonObject = super.getJSONObject();

		jsonObject.put("exclude_globs", getGlobs(_getExcludesJobProperties()));
		jsonObject.put("include_globs", getGlobs(_getIncludesJobProperties()));
		jsonObject.put("tck_home_dir", _tckHomeDir);

		return jsonObject;
	}

	protected TCKJunitBatchTestClassGroup(
		JSONObject jsonObject, PortalTestClassJob portalTestClassJob) {

		super(jsonObject, portalTestClassJob);

		_tckHomeDir = new File(jsonObject.getString("tck_home_dir"));
	}

	protected TCKJunitBatchTestClassGroup(
		String batchName, PortalTestClassJob portalTestClassJob) {

		super(batchName, portalTestClassJob);

		if (ignore()) {
			_tckHomeDir = null;

			return;
		}

		_tckHomeDir = _getTCKHomeDir();

		setTestClasses();

		setAxisTestClassGroups();

		setSegmentTestClassGroups();
	}

	protected void setTestClasses() {
		final List<PathMatcher> includesPathMatchers = getPathMatchers(
			_getIncludesJobProperties());
		final List<PathMatcher> excludesPathMatchers = getPathMatchers(
			_getExcludesJobProperties());

		final List<File> tckTestFiles = new ArrayList<>();

		try {
			Files.walkFileTree(
				_tckHomeDir.toPath(),
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult visitFile(
							Path filePath,
							BasicFileAttributes basicFileAttributes)
						throws IOException {

						if (JenkinsResultsParserUtil.isFileExcluded(
								excludesPathMatchers, filePath.toFile())) {

							return FileVisitResult.SKIP_SUBTREE;
						}

						if (JenkinsResultsParserUtil.isFileIncluded(
								excludesPathMatchers, includesPathMatchers,
								filePath)) {

							tckTestFiles.add(filePath.toFile());
						}

						return FileVisitResult.CONTINUE;
					}

				});
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to search for test file names in " +
					_tckHomeDir.getPath(),
				ioException);
		}

		for (File tckTestFile : tckTestFiles) {
			addTestClass(TestClassFactory.newTestClass(this, tckTestFile));
		}

		if (!containsTestClasses()) {
			File buildTestBatchFile = new File(
				portalGitWorkingDirectory.getWorkingDirectory(),
				"build-test-batch.xml");

			addTestClass(
				TestClassFactory.newTestClass(this, buildTestBatchFile));
		}
	}

	private List<JobProperty> _getExcludesJobProperties() {
		List<JobProperty> excludesJobProperties = new ArrayList<>();

		excludesJobProperties.add(
			getJobProperty(
				"test.batch.class.names.excludes", _tckHomeDir,
				JobProperty.Type.EXCLUDE_GLOB));

		if (includeStableTestSuite && isStableTestSuiteBatch()) {
			excludesJobProperties.add(
				getJobProperty(
					"test.batch.class.names.excludes", NAME_STABLE_TEST_SUITE,
					_tckHomeDir, JobProperty.Type.EXCLUDE_GLOB));
		}

		recordJobProperties(excludesJobProperties);

		return excludesJobProperties;
	}

	private List<JobProperty> _getIncludesJobProperties() {
		List<JobProperty> includesJobProperties = new ArrayList<>();

		includesJobProperties.add(
			getJobProperty(
				"test.batch.class.names.includes", _tckHomeDir,
				JobProperty.Type.INCLUDE_GLOB));

		if (includeStableTestSuite && isStableTestSuiteBatch()) {
			includesJobProperties.add(
				getJobProperty(
					"test.batch.class.names.includes", NAME_STABLE_TEST_SUITE,
					_tckHomeDir, JobProperty.Type.INCLUDE_GLOB));
		}

		recordJobProperties(includesJobProperties);

		return includesJobProperties;
	}

	private File _getTCKHomeDir() {
		JobProperty jobProperty = getJobProperty("tck.home");

		String tckHome = jobProperty.getValue();

		if (JenkinsResultsParserUtil.isNullOrEmpty(tckHome)) {
			File jenkinsDir = new File(
				"/opt/dev/projects/github/liferay-jenkins-ee");

			if (jenkinsDir.exists()) {
				tckHome = JenkinsResultsParserUtil.getProperty(
					JenkinsResultsParserUtil.getProperties(
						new File(
							jenkinsDir,
							"commands/dependencies/test.properties")),
					"tck.home");
			}
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(tckHome)) {
			try {
				tckHome = JenkinsResultsParserUtil.getBuildProperty(
					"portal.test.properties[tck.home]");
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(tckHome)) {
			throw new RuntimeException("Unable find the TCK home");
		}

		return new File(tckHome);
	}

	private final File _tckHomeDir;

}