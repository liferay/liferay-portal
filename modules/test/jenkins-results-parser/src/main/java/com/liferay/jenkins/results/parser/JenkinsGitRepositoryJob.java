/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.nio.file.PathMatcher;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JenkinsGitRepositoryJob extends GitRepositoryJob {

	@Override
	public Set<String> getAppServerTypes() {
		return new HashSet<>();
	}

	@Override
	public String getBranchName() {
		return "master";
	}

	@Override
	public GitWorkingDirectory getGitWorkingDirectory() {
		return gitWorkingDirectory;
	}

	public List<String> getInvokedJobNames() {
		List<String> modifiedFileNames = _getModifiedFileNames();

		List<String> modifiedJarEntryNames = new ArrayList<>();

		if (modifiedFileNames.contains(_JAR_FILE_NAME)) {
			try {
				modifiedJarEntryNames = _getModifiedEntryNames(
					_getUpstreamJarFile(), _getJarFile());

				modifiedFileNames.remove(_JAR_FILE_NAME);
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}

		Properties buildProperties = _getBuildProperties();

		Set<String> invokedJobNames = new TreeSet<>();

		String testSuiteName = getTestSuiteName();

		for (String propertyName : buildProperties.stringPropertyNames()) {
			if (!propertyName.startsWith("jenkins.pull.request.jobs[")) {
				continue;
			}

			List<String> propertyOptions =
				JenkinsResultsParserUtil.getPropertyOptions(propertyName);

			if ((propertyOptions.size() != 2) ||
				!Objects.equals(propertyOptions.get(1), testSuiteName) ||
				!_matchesRule(
					buildProperties, modifiedFileNames, modifiedJarEntryNames,
					propertyOptions.get(0), testSuiteName)) {

				continue;
			}

			invokedJobNames.addAll(_getInvokedJobNames(propertyName));
		}

		if (invokedJobNames.isEmpty()) {
			invokedJobNames.addAll(
				_getInvokedJobNames(
					JenkinsResultsParserUtil.combine(
						"jenkins.pull.request.jobs[", testSuiteName, "]")));
		}

		return new ArrayList<>(invokedJobNames);
	}

	@Override
	public JSONObject getJSONObject() {
		if (jsonObject != null) {
			return jsonObject;
		}

		jsonObject = super.getJSONObject();

		jsonObject.put("test_suite_name", _testSuiteName);

		return jsonObject;
	}

	@Override
	public Set<String> getRawBatchNames() {
		return new HashSet<>();
	}

	@Override
	public String getRepositoryName() {
		return "liferay-jenkins-ee";
	}

	@Override
	public String getTestSuiteName() {
		return _testSuiteName;
	}

	protected JenkinsGitRepositoryJob(
		BuildProfile buildProfile, String jobName, String testSuiteName) {

		super(buildProfile, jobName);

		_testSuiteName = testSuiteName;

		_initialize();
	}

	protected JenkinsGitRepositoryJob(JSONObject jsonObject) {
		super(jsonObject);

		_testSuiteName = jsonObject.getString("test_suite_name");

		_initialize();
	}

	private Properties _getBuildProperties() {
		try {
			return JenkinsResultsParserUtil.getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private Map<String, Long> _getChecksumMap(File jarFile) throws IOException {
		Map<String, Long> checksumMap = new HashMap<>();

		try (ZipFile zipFile = new ZipFile(jarFile)) {
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = enumeration.nextElement();

				if (zipEntry.isDirectory()) {
					continue;
				}

				checksumMap.put(zipEntry.getName(), zipEntry.getCrc());
			}
		}

		return checksumMap;
	}

	private List<String> _getInvokedJobNames(String propertyName) {
		try {
			return JenkinsResultsParserUtil.getBuildPropertyAsList(
				true, propertyName);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private File _getJarFile() {
		return new File(gitRepositoryDir, _JAR_FILE_NAME);
	}

	private File _getJenkinsGitRepositoryDir() {
		Properties buildProperties = _getBuildProperties();

		String jenkinsDirPath = JenkinsResultsParserUtil.getProperty(
			buildProperties, "jenkins.dir", getBranchName());

		if (JenkinsResultsParserUtil.isNullOrEmpty(jenkinsDirPath)) {
			String baseRepositoryDirPath = JenkinsResultsParserUtil.getProperty(
				buildProperties, "base.repository.dir");

			jenkinsDirPath = baseRepositoryDirPath + "/" + getRepositoryName();
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(jenkinsDirPath)) {
			throw new RuntimeException("Unable to find Jenkins directory path");
		}

		File jenkinsDir = new File(jenkinsDirPath);

		if (!jenkinsDir.exists()) {
			throw new RuntimeException("Unable to find Jenkins directory");
		}

		return jenkinsDir;
	}

	private List<String> _getModifiedEntryNames(File jarFile1, File jarFile2)
		throws IOException {

		Map<String, Long> checksumMap1 = _getChecksumMap(jarFile1);
		Map<String, Long> checksumMap2 = _getChecksumMap(jarFile2);

		Set<String> entryNames = new HashSet<>(checksumMap1.keySet());

		entryNames.addAll(checksumMap2.keySet());

		Set<String> modifiedEntryNames = new TreeSet<>();

		for (String entryName : entryNames) {
			if (entryName.startsWith("META-INF/") ||
				Objects.equals(
					checksumMap1.get(entryName), checksumMap2.get(entryName))) {

				continue;
			}

			modifiedEntryNames.add(
				entryName.replaceAll("\\$[^/]*\\.class", ".class"));
		}

		return new ArrayList<>(modifiedEntryNames);
	}

	private List<String> _getModifiedFileNames() {
		List<String> modifiedFileNames = new ArrayList<>();

		for (File modifiedFile : gitWorkingDirectory.getModifiedFilesList()) {
			modifiedFileNames.add(
				JenkinsResultsParserUtil.getPathRelativeTo(
					modifiedFile, gitRepositoryDir));
		}

		return modifiedFileNames;
	}

	private File _getUpstreamJarFile() throws IOException {
		String upstreamBranchName = Environment.get(
			"GITHUB_UPSTREAM_BRANCH_NAME");

		if (JenkinsResultsParserUtil.isNullOrEmpty(upstreamBranchName)) {
			throw new IOException("Please set GITHUB_UPSTREAM_BRANCH_NAME");
		}

		File upstreamJarFile = File.createTempFile("upstream-", ".jar");

		upstreamJarFile.deleteOnExit();

		GitUtil.ExecutionResult executionResult =
			gitWorkingDirectory.executeBashCommands(
				GitUtil.RETRIES_SIZE_MAX, GitUtil.MILLIS_RETRY_DELAY,
				GitUtil.MILLIS_TIMEOUT,
				JenkinsResultsParserUtil.combine(
					"git show ", upstreamBranchName, ":", _JAR_FILE_NAME, " > ",
					JenkinsResultsParserUtil.getCanonicalPath(
						upstreamJarFile)));

		if (executionResult.getExitValue() != 0) {
			throw new IOException(executionResult.getStandardError());
		}

		return upstreamJarFile;
	}

	private void _initialize() {
		gitWorkingDirectory = GitWorkingDirectoryFactory.newGitWorkingDirectory(
			getBranchName(), _getJenkinsGitRepositoryDir(),
			getRepositoryName());

		setGitRepositoryDir(gitWorkingDirectory.getWorkingDirectory());

		checkGitRepositoryDir();

		jobPropertiesFiles.add(new File(gitRepositoryDir, "test.properties"));
	}

	private boolean _matchesModifiedFileNames(
		List<String> modifiedFileNames, String propertyValue) {

		if (JenkinsResultsParserUtil.isNullOrEmpty(propertyValue)) {
			return false;
		}

		for (PathMatcher pathMatcher :
				JenkinsResultsParserUtil.toPathMatchers(
					"",
					JenkinsResultsParserUtil.getGlobsFromProperty(
						propertyValue))) {

			for (String modifiedFileName : modifiedFileNames) {
				if (pathMatcher.matches(Paths.get(modifiedFileName))) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean _matchesRule(
		Properties buildProperties, List<String> modifiedFileNames,
		List<String> modifiedJarEntryNames, String ruleName,
		String testSuiteName) {

		if (_matchesModifiedFileNames(
				modifiedFileNames,
				JenkinsResultsParserUtil.getProperty(
					buildProperties, "modified.files.includes", false, ruleName,
					testSuiteName)) ||
			_matchesModifiedFileNames(
				modifiedJarEntryNames,
				JenkinsResultsParserUtil.getProperty(
					buildProperties, "modified.jar.entries.includes", false,
					ruleName, testSuiteName))) {

			return true;
		}

		return false;
	}

	private static final String _JAR_FILE_NAME =
		"lib/jenkins/com.liferay.jenkins.results.parser.jar";

	private final String _testSuiteName;

}