/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 * @author Peter Yoo
 */
public class PortalGitWorkingDirectory extends GitWorkingDirectory {

	@Override
	public File archive(String fileName) {
		File archiveFile = super.archive(fileName);

		String upstreamBranchName = getUpstreamBranchName();

		if (!JenkinsResultsParserUtil.isCloudCINode() ||
			upstreamBranchName.startsWith("ee-") ||
			!_isGitArchiveYarnCacheEnabled()) {

			return archiveFile;
		}

		createYarnCache(fileName);

		return archiveFile;
	}

	public File createYarnCache(String fileName) {
		setUpYarn();

		StringBuilder sb = new StringBuilder();

		for (String excludeRegex : _BINARIES_CACHE_EXCLUDE_REGEXES) {
			sb.append(" | grep -v '");
			sb.append(excludeRegex);
			sb.append("'");
		}

		GitUtil.ExecutionResult executionResult = executeBashCommands(
			3, GitUtil.MILLIS_RETRY_DELAY, 1000 * 60 * 10,
			JenkinsResultsParserUtil.combine(
				"zip -q -r -y ", fileName,
				" $(git ls-files --directory --no-empty-directory --others ",
				sb.toString(), ") modules/yarn.lock"));

		if (executionResult.getExitValue() != 0) {
			throw new GitWorkingDirectoryRuntimeException(
				this,
				JenkinsResultsParserUtil.combine(
					"Unable to create the yarn cache ", fileName, "\n",
					executionResult.getStandardError()));
		}

		return new File(getWorkingDirectory(), fileName);
	}

	public Properties getAppServerProperties() {
		if (_appServerProperties != null) {
			return _appServerProperties;
		}

		_appServerProperties = JenkinsResultsParserUtil.getProperties(
			new File(getWorkingDirectory(), "app.server.properties"));

		return _appServerProperties;
	}

	public List<File> getJSUnitFiles() {
		if (_jsUnitFiles != null) {
			return _jsUnitFiles;
		}

		_jsUnitFiles = new ArrayList<>(
			findFiles(null, _FILE_CONTENT_SNIPPET_JS_UNIT));

		File portalPrivateDir = getPortalPrivateDir();

		if (portalPrivateDir != null) {
			String standardOut = null;

			try {
				Process process = JenkinsResultsParserUtil.executeBashCommands(
					false, portalPrivateDir, 60 * 1000,
					"git grep " + _FILE_CONTENT_SNIPPET_JS_UNIT);

				standardOut = JenkinsResultsParserUtil.readInputStream(
					process.getInputStream());
			}
			catch (IOException | TimeoutException exception) {
				throw new GitWorkingDirectoryRuntimeException(
					this, "Unable to run: git grep in " + portalPrivateDir,
					exception);
			}

			Matcher matcher = _jsUnitFilePathPattern.matcher(standardOut);

			while (matcher.find()) {
				String filePath = matcher.group("filePath");

				_jsUnitFiles.add(new File(portalPrivateDir, filePath));
			}
		}

		return _jsUnitFiles;
	}

	public String getMajorPortalVersion() {
		return JenkinsResultsParserUtil.getProperty(
			getReleaseProperties(), "lp.version.major");
	}

	public List<File> getModifiedModuleDirsList() throws IOException {
		if (_modifiedModuleDirs != null) {
			return _modifiedModuleDirs;
		}

		_modifiedModuleDirs = getModifiedModuleDirsList(null, null);

		return _modifiedModuleDirs;
	}

	public List<File> getModifiedModuleDirsList(
			List<PathMatcher> excludesPathMatchers,
			List<PathMatcher> includesPathMatchers)
		throws IOException {

		if ((excludesPathMatchers == null) && (includesPathMatchers == null) &&
			(_modifiedModuleDirs != null)) {

			return _modifiedModuleDirs;
		}

		List<File> modifiedModuleDirsList =
			JenkinsResultsParserUtil.getDirectoriesContainingFiles(
				getModuleDirsList(excludesPathMatchers, includesPathMatchers),
				getModifiedFilesList());

		if ((excludesPathMatchers == null) && (includesPathMatchers == null)) {
			_modifiedModuleDirs = modifiedModuleDirsList;
		}

		return modifiedModuleDirsList;
	}

	public List<File> getModifiedNonposhiModules() throws IOException {
		List<File> modifiedFilesList = getModifiedFilesList();

		List<File> modifiedNonposhiFilesList = new ArrayList<>();

		for (File modifiedFile : modifiedFilesList) {
			if (!JenkinsResultsParserUtil.isPoshiFile(modifiedFile)) {
				modifiedNonposhiFilesList.add(modifiedFile);
			}
		}

		return JenkinsResultsParserUtil.getDirectoriesContainingFiles(
			getModuleDirsList(null, null), modifiedNonposhiFilesList);
	}

	public List<File> getModifiedNPMTestModuleDirsList() throws IOException {
		List<File> modifiedModuleDirsList = getModifiedModuleDirsList();

		List<File> modifiedNPMTestModuleDirsList = new ArrayList<>(
			modifiedModuleDirsList.size());

		for (File modifiedModuleDir : modifiedModuleDirsList) {
			if (_isNPMTestModuleDir(modifiedModuleDir)) {
				modifiedNPMTestModuleDirsList.add(modifiedModuleDir);
			}
		}

		return modifiedNPMTestModuleDirsList;
	}

	public List<File> getModifiedPoshiModules() throws IOException {
		List<File> modifiedFilesList = getModifiedFilesList();

		List<File> modifiedPoshiFilesList = new ArrayList<>();

		for (File modifiedFile : modifiedFilesList) {
			if (JenkinsResultsParserUtil.isPoshiFile(modifiedFile)) {
				modifiedPoshiFilesList.add(modifiedFile);
			}
		}

		return JenkinsResultsParserUtil.getDirectoriesContainingFiles(
			getModuleDirsList(null, null), modifiedPoshiFilesList);
	}

	public List<File> getModuleAppDirs() {
		List<File> moduleAppDirs = new ArrayList<>();

		List<File> moduleAppBndFiles = JenkinsResultsParserUtil.findFiles(
			new File(getWorkingDirectory(), "modules"), "app\\.bnd");

		for (File moduleAppBndFile : moduleAppBndFiles) {
			moduleAppDirs.add(moduleAppBndFile.getParentFile());
		}

		return moduleAppDirs;
	}

	public List<File> getModuleDirs() {
		List<File> moduleAppDirs = new ArrayList<>();

		List<File> moduleAppBndFiles = JenkinsResultsParserUtil.findFiles(
			new File(getWorkingDirectory(), "modules"), "bnd\\.bnd");

		for (File moduleAppBndFile : moduleAppBndFiles) {
			String moduleAppBndFilePath = moduleAppBndFile.toString();

			if (moduleAppBndFilePath.contains("node_modules")) {
				continue;
			}

			moduleAppDirs.add(moduleAppBndFile.getParentFile());
		}

		return moduleAppDirs;
	}

	public List<File> getModuleDirsList() throws IOException {
		return getModuleDirsList(null, null);
	}

	public List<File> getModuleDirsList(
			List<PathMatcher> excludesPathMatchers,
			List<PathMatcher> includesPathMatchers)
		throws IOException {

		File modulesDir = new File(getWorkingDirectory(), "modules");

		if (!modulesDir.exists()) {
			return new ArrayList<>();
		}

		final List<PathMatcher> excludedModulesPathMatchers =
			excludesPathMatchers;
		final List<PathMatcher> includedModulesPathMatchers =
			includesPathMatchers;

		final List<File> moduleDirsList = new ArrayList<>();

		Files.walkFileTree(
			modulesDir.toPath(),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult postVisitDirectory(
					Path filePath, IOException ioException) {

					if (_module == null) {
						return FileVisitResult.CONTINUE;
					}

					Module currentModule = Module.getModule(filePath);

					if (currentModule == null) {
						return FileVisitResult.CONTINUE;
					}

					File currentFile = currentModule.getFile();

					if (currentFile.equals(_module.getFile())) {
						moduleDirsList.add(currentFile);

						_module = null;
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(
					Path filePath, BasicFileAttributes basicFileAttributes) {

					if (!JenkinsResultsParserUtil.isFileIncluded(
							excludedModulesPathMatchers,
							includedModulesPathMatchers, filePath)) {

						return FileVisitResult.CONTINUE;
					}

					Module currentModule = Module.getModule(filePath);

					if (currentModule == null) {
						return FileVisitResult.CONTINUE;
					}

					if (_module == null) {
						_module = currentModule;

						return FileVisitResult.CONTINUE;
					}

					int currentPriority = currentModule.getPriority();

					if (currentPriority < _module.getPriority()) {
						_module = currentModule;
					}

					return FileVisitResult.CONTINUE;
				}

				private Module _module;

			});

		Collections.sort(moduleDirsList);

		return moduleDirsList;
	}

	public List<File> getModulePullSubrepoDirs() {
		File modulesDir = new File(getWorkingDirectory(), "modules");

		if (!modulesDir.exists()) {
			return new ArrayList<>();
		}

		List<File> moduleSubrepoDirs = new ArrayList<>();

		List<File> gitrepoFiles = JenkinsResultsParserUtil.findFiles(
			modulesDir, "\\.gitrepo");

		for (File gitrepoFile : gitrepoFiles) {
			Properties gitrepoProperties =
				JenkinsResultsParserUtil.getProperties(gitrepoFile);

			String mode = gitrepoProperties.getProperty("mode", "push");

			if (mode.equals("pull")) {
				moduleSubrepoDirs.add(gitrepoFile.getParentFile());
			}
		}

		return moduleSubrepoDirs;
	}

	public List<File> getNPMTestModuleDirsList() throws IOException {
		List<File> npmModuleDirsList = new ArrayList<>();

		for (File moduleDir : getModuleDirsList()) {
			if (_isNPMTestModuleDir(moduleDir)) {
				npmModuleDirsList.add(moduleDir);
			}
		}

		return npmModuleDirsList;
	}

	public PluginsGitWorkingDirectory getPluginsGitWorkingDirectory() {
		Properties buildProperties = null;

		try {
			buildProperties = JenkinsResultsParserUtil.getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		String pluginsDir = JenkinsResultsParserUtil.getProperty(
			buildProperties, "plugins.dir", getUpstreamBranchName());

		GitWorkingDirectory pluginsGitWorkingDirectory =
			GitWorkingDirectoryFactory.newGitWorkingDirectory(
				getUpstreamBranchName(), new File(pluginsDir),
				"liferay-plugins-ee");

		if (pluginsGitWorkingDirectory instanceof PluginsGitWorkingDirectory) {
			return (PluginsGitWorkingDirectory)pluginsGitWorkingDirectory;
		}

		throw new RuntimeException(
			"Unable to find a plugins Git working directory");
	}

	public File getPortalPrivateDir() {
		String portalPrivateDirPath = JenkinsResultsParserUtil.getProperty(
			getTestProperties(), "liferay.portal.private.dir");

		if (JenkinsResultsParserUtil.isNullOrEmpty(portalPrivateDirPath)) {
			return null;
		}

		File portalPrivateDir = new File(portalPrivateDirPath);

		if (!portalPrivateDir.isAbsolute()) {
			portalPrivateDir = new File(
				getWorkingDirectory(), portalPrivateDirPath);
		}

		if (!portalPrivateDir.exists()) {
			return null;
		}

		return JenkinsResultsParserUtil.getCanonicalFile(portalPrivateDir);
	}

	public Properties getReleaseProperties() {
		if (_releaseProperties != null) {
			return _releaseProperties;
		}

		_releaseProperties = JenkinsResultsParserUtil.getProperties(
			new File(getWorkingDirectory(), "release.properties"));

		return _releaseProperties;
	}

	public Properties getTestProperties() {
		if (_testProperties != null) {
			return _testProperties;
		}

		File testPropertiesFile = new File(
			getWorkingDirectory(), "test.properties");

		if (!testPropertiesFile.exists()) {
			return _testProperties;
		}

		_testProperties = JenkinsResultsParserUtil.getProperties(
			testPropertiesFile);

		return _testProperties;
	}

	public synchronized void setUpYarn() {
		if (_setUpYarn) {
			return;
		}

		File workingDirectory = getWorkingDirectory();

		try {
			Map<String, String> filteredEnv = getFilteredEnvironment();

			Properties properties = new Properties();

			String[] propertyNames = {
				"build.binaries.cache.dir",
				"build.binaries.cache.repository.name",
				"nodejs.npm.ci.registry", "nodejs.node.env", "nodejs.npm.args",
				"nodejs.npm.ci.sass.binary.site"
			};

			for (String propertyName : propertyNames) {
				properties.put(
					propertyName,
					JenkinsResultsParserUtil.getBuildProperty(
						"portal.build.properties[" + propertyName + "]",
						getUpstreamBranchName()));
			}

			JenkinsResultsParserUtil.writePropertiesFile(
				new File(
					getWorkingDirectory(),
					JenkinsResultsParserUtil.combine(
						"build.", Environment.get("HOSTNAME"), ".properties")),
				properties, true);

			AntUtil.callTarget(
				workingDirectory, "build.xml", "setup-sdk setup-yarn", null,
				filteredEnv);

			File nodeModulesCacheDir = new File(
				workingDirectory, "modules/node_modules_cache");

			if (!nodeModulesCacheDir.exists()) {
				_setUpYarn = true;

				return;
			}

			for (File file :
					nodeModulesCacheDir.listFiles(
						JenkinsResultsParserUtil.newFilenameFilter(
							"@esbuild-linux-.*(arm64|x64).*"))) {

				Matcher matcher = _esBuildFileNamePattern.matcher(
					file.getName());

				if (matcher.find()) {
					File esBuildDir = new File(
						getWorkingDirectory(),
						"modules/node_modules/@esbuild/" + matcher.group(1));

					if (esBuildDir.exists()) {
						continue;
					}

					File tmpDir = new File(getWorkingDirectory(), "tmp");

					JenkinsResultsParserUtil.unTarGzip(file, tmpDir);

					JenkinsResultsParserUtil.move(
						new File(tmpDir, "package"), esBuildDir);

					File esBuildBinFile = new File(esBuildDir, "bin/esbuild");

					JenkinsResultsParserUtil.executeBashCommands(
						"chmod +x " + esBuildBinFile);

					JenkinsResultsParserUtil.delete(tmpDir);
				}
			}
		}
		catch (AntException | IOException | TimeoutException exception) {
			throw new GitWorkingDirectoryRuntimeException(
				this, "Failed to run setup-yarn in " + workingDirectory);
		}

		_setUpYarn = true;
	}

	public static class Module {

		public static Module getModule(Path path) {
			File file = path.toFile();

			if (!file.isDirectory()) {
				return null;
			}

			for (int i = 0; i < _markerFileNames.size(); i++) {
				for (String markerFileName : _markerFileNames.get(i)) {
					File markerFile = new File(file, markerFileName);

					if (markerFile.exists()) {
						return new Module(file, i);
					}
				}
			}

			return null;
		}

		public File getFile() {
			return _file;
		}

		public int getPriority() {
			return _priority;
		}

		@Override
		public String toString() {
			return JenkinsResultsParserUtil.combine(
				String.valueOf(_priority), " ", _file.toString());
		}

		private Module(File file, int priority) {
			_file = file;
			_priority = priority;
		}

		private static Map<Integer, String[]> _markerFileNames =
			new HashMap<Integer, String[]>() {
				{
					put(0, new String[] {".lfrbuild-release-src", ".gitrepo"});
					put(1, new String[] {"app.bnd"});
					put(2, new String[] {"bnd.bnd"});
					put(
						3,
						new String[] {"build.gradle", "build.xml", "pom.xml"});
				}
			};

		private final File _file;
		private final int _priority;

	}

	protected PortalGitWorkingDirectory(
			String upstreamBranchName, String workingDirectoryPath)
		throws IOException {

		super(upstreamBranchName, workingDirectoryPath);
	}

	protected PortalGitWorkingDirectory(
			String upstreamBranchName, String workingDirectoryPath,
			String gitRepositoryName)
		throws IOException {

		super(upstreamBranchName, workingDirectoryPath, gitRepositoryName);
	}

	protected Map<String, String> getFilteredEnvironment() throws IOException {
		Map<String, String> filteredEnv = new HashMap<>();

		Map<String, String> env = Environment.getAll();

		for (Map.Entry<String, String> entry : env.entrySet()) {
			String key = entry.getKey();

			if (!key.startsWith("ANT_") && !key.startsWith("JAVA_") &&
				!key.startsWith("JENKINS_HOME")) {

				continue;
			}

			filteredEnv.put(key, entry.getValue());
		}

		String antOptsDefault = JenkinsResultsParserUtil.getBuildProperty(
			"ant.opts.default", getUpstreamBranchName());

		if (!JenkinsResultsParserUtil.isNullOrEmpty(antOptsDefault)) {
			filteredEnv.put("ANT_OPTS", antOptsDefault);
			filteredEnv.put("JAVA_OPTS", antOptsDefault);
		}

		String javaJDKDefaultRuntime =
			JenkinsResultsParserUtil.getBuildProperty(
				"java.jdk.default.runtime", getUpstreamBranchName());

		if (!JenkinsResultsParserUtil.isNullOrEmpty(javaJDKDefaultRuntime)) {
			filteredEnv.put("JAVA_HOME", javaJDKDefaultRuntime);
		}

		return filteredEnv;
	}

	private boolean _isGitArchiveYarnCacheEnabled() {
		String gitArchiveYarnCacheEnabled = null;

		try {
			gitArchiveYarnCacheEnabled =
				JenkinsResultsParserUtil.getBuildProperty(
					"git.archive.yarn.cache.enabled",
					Environment.get("CI_TEST_SUITE"),
					Environment.get("JOB_NAME"), getUpstreamBranchName());
		}
		catch (IOException ioException) {
			return true;
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(
				gitArchiveYarnCacheEnabled)) {

			return true;
		}

		return Boolean.parseBoolean(gitArchiveYarnCacheEnabled);
	}

	private boolean _isNPMTestModuleDir(File moduleDir) {
		List<File> packageJSONFiles = JenkinsResultsParserUtil.findFiles(
			moduleDir, "package\\.json");

		for (File packageJSONFile : packageJSONFiles) {
			JSONObject jsonObject = null;

			try {
				jsonObject = JenkinsResultsParserUtil.createJSONObject(
					JenkinsResultsParserUtil.read(packageJSONFile));
			}
			catch (IOException ioException) {
				System.out.println(
					"Unable to read invalid JSON " + packageJSONFile.getPath());

				continue;
			}
			catch (JSONException jsonException) {
				System.out.println(
					"Invalid JSON file " + packageJSONFile.getPath());

				continue;
			}

			if (!jsonObject.has("scripts")) {
				continue;
			}

			JSONObject scriptsJSONObject = jsonObject.getJSONObject("scripts");

			if (!scriptsJSONObject.has("test")) {
				continue;
			}

			return true;
		}

		return false;
	}

	private static final String[] _BINARIES_CACHE_EXCLUDE_REGEXES = {
		"\\.gradle/", "\\.yarn/", "modules/\\.tsc/", "node_modules_cache"
	};

	private static final String _FILE_CONTENT_SNIPPET_JS_UNIT =
		"describe\\( -- '*.js' '*.jsx' '*.ts' '*.tsx'";

	private static final Pattern _esBuildFileNamePattern = Pattern.compile(
		"@esbuild-(linux-.*?)-.*");
	private static final Pattern _jsUnitFilePathPattern = Pattern.compile(
		"(?<filePath>[^:]+):.+");

	private Properties _appServerProperties;
	private List<File> _jsUnitFiles;
	private List<File> _modifiedModuleDirs;
	private Properties _releaseProperties;
	private boolean _setUpYarn;
	private Properties _testProperties;

}