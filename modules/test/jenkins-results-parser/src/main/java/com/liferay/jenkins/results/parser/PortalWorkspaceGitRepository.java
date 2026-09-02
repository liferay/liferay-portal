/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.test.batch.TestBatch;
import com.liferay.jenkins.results.parser.test.suite.RelevantTestSuite;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class PortalWorkspaceGitRepository extends BaseWorkspaceGitRepository {

	public boolean bypassCITestRelevant() {
		setUp();

		Properties testProperties = JenkinsResultsParserUtil.getProperties(
			new File(getDirectory(), "test.properties"));

		boolean relevantEngineEnabled = Boolean.parseBoolean(
			testProperties.getProperty("relevant.engine.enabled"));

		if (relevantEngineEnabled) {
			RelevantTestSuite relevantTestSuite = new RelevantTestSuite(
				_getRelevantPortalAcceptancePullRequestJob());

			List<TestBatch> testBatches = relevantTestSuite.getTestBatches(
				true);

			return testBatches.isEmpty();
		}

		Properties ciProperties = JenkinsResultsParserUtil.getProperties(
			new File(getDirectory(), "ci.properties"));

		String ciTestRelevantBypassFilePathPatterns = ciProperties.getProperty(
			"ci.test.relevant.bypass.file.path.patterns", getName());

		if (JenkinsResultsParserUtil.isNullOrEmpty(
				ciTestRelevantBypassFilePathPatterns)) {

			return false;
		}

		MultiPattern multiPattern = new MultiPattern(
			ciTestRelevantBypassFilePathPatterns.split("\\s*,\\s*"));

		List<String> filePaths = new ArrayList<>();

		GitWorkingDirectory gitWorkingDirectory = getGitWorkingDirectory();

		for (File modifiedFile : gitWorkingDirectory.getModifiedFilesList()) {
			filePaths.add(
				JenkinsResultsParserUtil.getCanonicalPath(modifiedFile));
		}

		for (File deletedFile : gitWorkingDirectory.getDeletedFilesList()) {
			filePaths.add(
				JenkinsResultsParserUtil.getCanonicalPath(deletedFile));
		}

		return multiPattern.matchesAll(filePaths.toArray(new String[0]));
	}

	public Properties getAppServerProperties() {
		if (_appServerProperties != null) {
			return _appServerProperties;
		}

		_appServerProperties = JenkinsResultsParserUtil.getProperties(
			new File(getDirectory(), "app.server.properties"));

		return _appServerProperties;
	}

	public String getLiferayFacesAlloyURL() {
		return _getLiferayFacesURL(
			"liferay-faces-alloy", "liferay.faces.alloy.branch");
	}

	public String getLiferayFacesBridgeImplURL() {
		return _getLiferayFacesURL(
			"liferay-faces-bridge-impl", "liferay.faces.bridge.impl.branch");
	}

	public String getLiferayFacesPortalURL() {
		return _getLiferayFacesURL(
			"liferay-faces-portal", "liferay.faces.portal.branch");
	}

	public String getLiferayFacesShowcaseURL() {
		return _getLiferayFacesURL(
			"liferay-faces-showcase", "liferay.faces.showcase.branch");
	}

	public String getPluginsRepositoryDirName() {
		try {
			String lpPluginsDirString = JenkinsResultsParserUtil.getProperty(
				JenkinsResultsParserUtil.getBuildProperties(),
				"portal.release.properties", "lp.plugins.dir",
				getUpstreamBranchName());

			if (JenkinsResultsParserUtil.isNullOrEmpty(lpPluginsDirString)) {
				return null;
			}

			return lpPluginsDirString.replaceAll(".*/([^/]+)", "$1");
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public String getPortalPrivateRepositoryDirName() {
		return JenkinsResultsParserUtil.getGitDirectoryName(
			"liferay-portal-ee", "master-private");
	}

	public void setUpPortalProfile() {
		String setupProfileDXPBranchNamesString = null;

		try {
			setupProfileDXPBranchNamesString =
				JenkinsResultsParserUtil.getBuildProperty(
					"portal.setup.profile.dxp.branch.names");

			if (JenkinsResultsParserUtil.isNullOrEmpty(
					setupProfileDXPBranchNamesString)) {

				return;
			}
		}
		catch (IOException ioException) {
			return;
		}

		List<String> setupProfileDXPBranchNames = Arrays.asList(
			setupProfileDXPBranchNamesString.split(","));

		if (!setupProfileDXPBranchNames.contains(getUpstreamBranchName())) {
			return;
		}

		Retryable<Object> retryable = new Retryable<Object>(true, 2, 5, true) {

			@Override
			public Object execute() {
				try {
					AntUtil.callTarget(
						getDirectory(), "build.xml", "setup-profile-dxp");
				}
				catch (AntException antException) {
					throw new RuntimeException(antException);
				}

				return null;
			}

		};

		retryable.executeWithRetries();
	}

	public void setUpTCKHome() {
		Map<String, String> parameters = new HashMap<>();

		String tckHome = JenkinsResultsParserUtil.getProperty(
			getPortalTestProperties(), "tck.home");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(tckHome)) {
			parameters.put("tck.home", tckHome);
		}

		try {
			AntUtil.callTarget(
				getDirectory(), "build-test-tck.xml", "prepare-tck",
				parameters);
		}
		catch (AntException antException) {
			throw new RuntimeException(antException);
		}
	}

	@Override
	public void writePropertiesFiles() {
		_writeAppServerPropertiesFile();
		_writeBuildPropertiesFile();
		_writeReleasePropertiesFile();
		_writeSQLPropertiesFile();
		_writeTestPropertiesFile();
	}

	protected PortalWorkspaceGitRepository(JSONObject jsonObject) {
		super(jsonObject);
	}

	protected PortalWorkspaceGitRepository(
		PullRequest pullRequest, String upstreamBranchName) {

		super(pullRequest, upstreamBranchName);
	}

	protected PortalWorkspaceGitRepository(
		RemoteGitRef remoteGitRef, String upstreamBranchName) {

		super(remoteGitRef, upstreamBranchName);
	}

	protected void downloadYarnCache() {
		String yarnCacheS3ObjectPath = _getYarnCacheS3ObjectPath();

		if (!CloudBucketUtil.isS3ObjectPathAvailable(yarnCacheS3ObjectPath)) {
			throw new RuntimeException(
				"Unable to download " + yarnCacheS3ObjectPath);
		}

		File yarnCacheFile = null;

		try {
			yarnCacheFile = File.createTempFile(
				"yarn-cache", ".zip", getDirectory());

			CloudBucketUtil.downloadS3File(
				yarnCacheFile, yarnCacheS3ObjectPath);

			JenkinsResultsParserUtil.unzip(yarnCacheFile, getDirectory());

			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Successfully unzipped ", yarnCacheS3ObjectPath, " to ",
					JenkinsResultsParserUtil.getCanonicalPath(getDirectory())));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
		finally {
			if (yarnCacheFile != null) {
				JenkinsResultsParserUtil.delete(yarnCacheFile);
			}
		}
	}

	protected Properties getPortalTestProperties() {
		Properties testProperties = getProperties("portal.test.properties");

		String companyDefaultLocale = Environment.get(
			"TEST_COMPANY_DEFAULT_LOCALE");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(companyDefaultLocale)) {
			testProperties.setProperty(
				"test.company.default.locale", companyDefaultLocale);
		}

		String portalLatestBundleVersion = Environment.get(
			"PORTAL_LATEST_BUNDLE_VERSION");

		if (JenkinsResultsParserUtil.isNullOrEmpty(portalLatestBundleVersion)) {
			try {
				portalLatestBundleVersion =
					JenkinsResultsParserUtil.getBuildProperty(
						"portal.latest.bundle.version",
						getUpstreamBranchName());
			}
			catch (IOException ioException) {
				System.out.println(
					"WARNING: Unable to get \"portal.latest.bundle.version\"");
			}
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(
				portalLatestBundleVersion)) {

			testProperties.put(
				"test.released.release.bundle.version",
				portalLatestBundleVersion);

			Properties buildProperties = null;

			try {
				buildProperties = JenkinsResultsParserUtil.getBuildProperties();
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}

			String portalBundleTomcatURL = JenkinsResultsParserUtil.getProperty(
				buildProperties, "portal.bundle.tomcat",
				portalLatestBundleVersion);

			if (!JenkinsResultsParserUtil.isNullOrEmpty(
					portalBundleTomcatURL)) {

				testProperties.put(
					"test.released.test.portal.bundle.zip.url",
					portalBundleTomcatURL);
			}
		}

		return testProperties;
	}

	@Override
	protected Set<String> getPropertyOptions() {
		Set<String> propertyOptions = new HashSet<>(super.getPropertyOptions());

		propertyOptions.add(getUpstreamBranchName());

		return propertyOptions;
	}

	protected boolean isBinariesCacheEnabled() {
		try {
			return Boolean.parseBoolean(
				JenkinsResultsParserUtil.getBuildProperty(
					"binaries.cache.enabled", Environment.get("CI_TEST_SUITE"),
					Environment.get("JOB_NAME"), getUpstreamBranchName()));
		}
		catch (IOException ioException) {
			return true;
		}
	}

	protected boolean isYarnCacheAvailable() {
		if (!JenkinsResultsParserUtil.isCloudCINode() ||
			!CloudBucketUtil.isS3ObjectPathAvailable(
				_getYarnCacheS3ObjectPath())) {

			return false;
		}

		return true;
	}

	protected boolean isYarnCacheEnabled() {
		try {
			return Boolean.parseBoolean(
				JenkinsResultsParserUtil.getBuildProperty(
					"yarn.cache.enabled", Environment.get("CI_TEST_SUITE"),
					Environment.get("JOB_NAME"), getUpstreamBranchName()));
		}
		catch (IOException ioException) {
			return true;
		}
	}

	@Override
	protected void setUpAdditionalCaches() throws IOException {
		if (isBinariesCacheEnabled()) {
			setUpBinariesCache();
			setUpWorkspaceYarnMirrors();
		}

		if (isYarnCacheEnabled()) {
			setUpYarnCache();
		}
	}

	protected void setUpBinariesCache() {
		if (!JenkinsResultsParserUtil.isCloudCINode() || _setUpBinariesCache) {
			return;
		}

		String binariesCacheS3Path;

		try {
			binariesCacheS3Path = JenkinsResultsParserUtil.getBuildProperty(
				"binaries.cache.s3.path", getUpstreamBranchName());
		}
		catch (IOException ioException) {
			System.out.println(
				"WARNING: Unable to get \"binaries.cache.s3.path\"");

			_setUpBinariesCache = true;

			return;
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(binariesCacheS3Path)) {
			return;
		}

		File binariesCacheTarGzipFile = new File(
			getDirectory(), "binaries-cache.tar.gz");

		try {
			CloudBucketUtil.downloadS3File(
				binariesCacheTarGzipFile, binariesCacheS3Path);
		}
		catch (IOException ioException) {
			System.out.println(
				"WARNING: Unable to download " + binariesCacheS3Path);

			_setUpBinariesCache = true;

			return;
		}

		try {
			JenkinsResultsParserUtil.unTarGzip(
				binariesCacheTarGzipFile, getDirectory());

			System.out.println(
				"Successfully untared " + binariesCacheS3Path + " to " +
					getDirectory());
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
		finally {
			JenkinsResultsParserUtil.delete(binariesCacheTarGzipFile);

			_setUpBinariesCache = true;
		}
	}

	protected void setUpWorkspaceYarnMirrors() {
		File workspacesDirectory = new File(getDirectory(), "workspaces");

		File[] files = workspacesDirectory.listFiles();

		if (files == null) {
			return;
		}

		File nodeModulesCacheDirectory = new File(
			workspacesDirectory, "node_modules_cache");

		nodeModulesCacheDirectory.mkdirs();

		for (File file : files) {
			File yarnRCFile = new File(file, ".yarnrc");

			if (!yarnRCFile.exists()) {
				continue;
			}

			Path path = Paths.get(
				file.getPath(), nodeModulesCacheDirectory.getName());

			if (Files.exists(path) || Files.isSymbolicLink(path)) {
				continue;
			}

			try {
				Files.createSymbolicLink(
					path, Paths.get("..", nodeModulesCacheDirectory.getName()));

				System.out.println(
					"Created Yarn mirror symbolic link at " + path);
			}
			catch (IOException ioException) {
				System.out.println("WARNING: Unable to create " + path);
			}
		}
	}

	protected synchronized void setUpYarn() {
		if (_setUpYarn || isSnapshot()) {
			return;
		}

		PortalGitWorkingDirectory portalGitWorkingDirectory =
			(PortalGitWorkingDirectory)getGitWorkingDirectory();

		portalGitWorkingDirectory.setUpYarn();

		_setUpYarn = true;
	}

	protected synchronized void setUpYarnCache() {
		String upstreamBranchName = getUpstreamBranchName();

		if (!JenkinsResultsParserUtil.isCloudCINode() || _setUpYarnCache ||
			upstreamBranchName.startsWith("ee-")) {

			return;
		}

		if (isSnapshot()) {
			downloadYarnCache();

			_setUpYarnCache = true;

			return;
		}

		if (isYarnCacheAvailable()) {
			downloadYarnCache();

			touchYarnCache();

			_setUpYarnCache = true;

			return;
		}

		setUpYarn();

		uploadYarnCache();

		_setUpYarnCache = true;
	}

	protected void touchYarnCache() {
		try {
			CloudBucketUtil.touchS3File(_getYarnCacheS3ObjectPath());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	protected synchronized void uploadYarnCache() {
		String yarnCacheS3ObjectPath = _getYarnCacheS3ObjectPath();

		if (!CloudBucketUtil.isValidS3ObjectPath(yarnCacheS3ObjectPath)) {
			return;
		}

		File yarnCacheFile = null;

		try {
			yarnCacheFile = File.createTempFile(
				"yarn-cache", ".zip", getDirectory());

			JenkinsResultsParserUtil.delete(yarnCacheFile);

			PortalGitWorkingDirectory portalGitWorkingDirectory =
				(PortalGitWorkingDirectory)getGitWorkingDirectory();

			yarnCacheFile = portalGitWorkingDirectory.createYarnCache(
				yarnCacheFile.getName());

			CloudBucketUtil.uploadS3File(yarnCacheS3ObjectPath, yarnCacheFile);

			System.out.println(
				"Successfully uploaded to " + yarnCacheS3ObjectPath);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
		finally {
			if (yarnCacheFile != null) {
				JenkinsResultsParserUtil.delete(yarnCacheFile);
			}
		}
	}

	private String _getLiferayFacesURL(
		String repositoryName, String propertyName) {

		try {
			String branchName = JenkinsResultsParserUtil.getProperty(
				JenkinsResultsParserUtil.getBuildProperties(),
				"portal.test.properties", propertyName,
				getUpstreamBranchName());

			if (JenkinsResultsParserUtil.isNullOrEmpty(branchName)) {
				branchName = "master";
			}

			return JenkinsResultsParserUtil.combine(
				"https://github.com/liferay/", repositoryName, "/tree/",
				branchName);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private PortalAcceptancePullRequestJob
		_getRelevantPortalAcceptancePullRequestJob() {

		String upstreamBranchName = getUpstreamBranchName();

		PortalGitWorkingDirectory portalGitWorkingDirectory =
			(PortalGitWorkingDirectory)getGitWorkingDirectory();

		portalGitWorkingDirectory.getGitRepositoryName();

		return (PortalAcceptancePullRequestJob)JobFactory.newJob(
			Job.BuildProfile.DXP, "test-portal-acceptance-pullrequest(master)",
			null, portalGitWorkingDirectory, upstreamBranchName, null,
			portalGitWorkingDirectory.getGitRepositoryName(), "relevant",
			upstreamBranchName);
	}

	private String _getYarnCacheS3ObjectPath() {
		String yarnLockDigest = _getYarnLockDigest();

		if (JenkinsResultsParserUtil.isNullOrEmpty(yarnLockDigest)) {
			return null;
		}

		try {
			return JenkinsResultsParserUtil.combine(
				JenkinsResultsParserUtil.getBuildProperty(
					"cloud.ci.s3.bucket.yarn.caches.path"),
				"/", getName(), "/", yarnLockDigest, "/yarn-cache.zip");
		}
		catch (IOException ioException) {
			System.out.println(
				"WARNING: Unable to get " +
					"\"cloud.ci.s3.bucket.yarn.caches.path\"");

			return null;
		}
	}

	private synchronized String _getYarnLockDigest() {
		if (_yarnLockDigest != null) {
			return _yarnLockDigest;
		}

		File yarnLockFile = new File(getDirectory(), "modules/yarn.lock");

		if (!yarnLockFile.exists()) {
			_yarnLockDigest = "";

			return _yarnLockDigest;
		}

		try {
			String yarnLockFileContent = JenkinsResultsParserUtil.read(
				yarnLockFile);

			String nodejsNpmCiRegistry =
				JenkinsResultsParserUtil.getBuildProperty(
					"portal.build.properties[nodejs.npm.ci.registry]");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(nodejsNpmCiRegistry)) {
				yarnLockFileContent = yarnLockFileContent.replace(
					"https://registry.yarnpkg.com", nodejsNpmCiRegistry);
			}

			_yarnLockDigest = DigestUtils.sha256Hex(yarnLockFileContent);
		}
		catch (IOException ioException) {
			_yarnLockDigest = "";

			return _yarnLockDigest;
		}

		return _yarnLockDigest;
	}

	private void _writeAppServerPropertiesFile() {
		JenkinsResultsParserUtil.writePropertiesFile(
			new File(
				getDirectory(),
				JenkinsResultsParserUtil.combine(
					"app.server.", Environment.get("HOSTNAME"), ".properties")),
			getProperties("portal.app.server.properties"), true);
	}

	private void _writeBuildPropertiesFile() {
		JenkinsResultsParserUtil.writePropertiesFile(
			new File(
				getDirectory(),
				JenkinsResultsParserUtil.combine(
					"build.", Environment.get("HOSTNAME"), ".properties")),
			getProperties("portal.build.properties"), true);
	}

	private void _writeReleasePropertiesFile() {
		JenkinsResultsParserUtil.writePropertiesFile(
			new File(
				getDirectory(),
				JenkinsResultsParserUtil.combine(
					"release.", Environment.get("HOSTNAME"), ".properties")),
			getProperties("portal.release.properties"), true);
	}

	private void _writeSQLPropertiesFile() {
		JenkinsResultsParserUtil.writePropertiesFile(
			new File(
				getDirectory(),
				JenkinsResultsParserUtil.combine(
					"sql/sql.", Environment.get("HOSTNAME"), ".properties")),
			getProperties("portal.sql.properties"), true);
	}

	private void _writeTestPropertiesFile() {
		JenkinsResultsParserUtil.writePropertiesFile(
			new File(
				getDirectory(),
				JenkinsResultsParserUtil.combine(
					"test.", Environment.get("HOSTNAME"), ".properties")),
			getPortalTestProperties(), true);
	}

	private Properties _appServerProperties;
	private boolean _setUpBinariesCache;
	private boolean _setUpYarn;
	private boolean _setUpYarnCache;
	private String _yarnLockDigest;

}