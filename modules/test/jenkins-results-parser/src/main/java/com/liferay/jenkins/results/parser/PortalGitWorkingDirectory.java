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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 * @author Peter Yoo
 */
public class PortalGitWorkingDirectory extends GitWorkingDirectory {

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
			findFiles(null, "describe\\( -- '*.js' '*.jsx' '*.ts' '*.tsx'"));

		return _jsUnitFiles;
	}

	public String getMajorPortalVersion() {
		return JenkinsResultsParserUtil.getProperty(
			getReleaseProperties(), "lp.version.major");
	}

	public List<File> getModifiedModuleDirsList() throws IOException {
		return getModifiedModuleDirsList(null, null);
	}

	public List<File> getModifiedModuleDirsList(
			List<PathMatcher> excludesPathMatchers,
			List<PathMatcher> includesPathMatchers)
		throws IOException {

		return JenkinsResultsParserUtil.getDirectoriesContainingFiles(
			getModuleDirsList(excludesPathMatchers, includesPathMatchers),
			getModifiedFilesList());
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
		String lpPluginsDir = JenkinsResultsParserUtil.getProperty(
			getReleaseProperties(), "lp.plugins.dir");

		GitWorkingDirectory pluginsGitWorkingDirectory =
			GitWorkingDirectoryFactory.newGitWorkingDirectory(
				getUpstreamBranchName(), new File(lpPluginsDir),
				"liferay-plugins-ee");

		if (pluginsGitWorkingDirectory instanceof PluginsGitWorkingDirectory) {
			return (PluginsGitWorkingDirectory)pluginsGitWorkingDirectory;
		}

		throw new RuntimeException(
			"Unable to find a plugins Git working directory");
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

	private Properties _appServerProperties;
	private List<File> _jsUnitFiles;
	private Properties _releaseProperties;
	private Properties _testProperties;

}