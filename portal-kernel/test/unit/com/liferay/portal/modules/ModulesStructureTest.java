/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.modules;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.modules.util.GradleDependency;
import com.liferay.portal.modules.util.Module;
import com.liferay.portal.modules.util.ModulesStructureTestUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 * @author Andrea Di Giorgi
 */
public class ModulesStructureTest {

	@BeforeClass
	public static void setUpClass() throws IOException {
		_buildProperties = new Properties();

		try (InputStream inputStream = Files.newInputStream(
				Paths.get("build.properties"))) {

			_buildProperties.load(inputStream);
		}

		_branchName = _buildProperties.getProperty("git.working.branch.name");

		if (_branchName.contains("master")) {
			_masterBranch = true;
		}

		_modulesDirPath = Paths.get("modules");

		_checkoutPrivateAppsDirs = new HashSet<>();

		if (Files.exists(Paths.get("working.dir.properties"))) {
			Properties properties = new Properties();

			try (InputStream inputStream = Files.newInputStream(
					Paths.get("working.dir.properties"))) {

				properties.load(inputStream);
			}

			String projectNames = properties.getProperty(
				"working.dir.checkout.private.apps.project.names");

			String[] projectNamesArray = StringUtil.split(projectNames);

			for (String projectName : projectNamesArray) {
				String dirs = properties.getProperty(
					"working.dir.checkout.private.apps." + projectName +
						".dirs");

				Collections.addAll(
					_checkoutPrivateAppsDirs, StringUtil.split(dirs));
			}
		}
	}

	@Test
	public void testScanArchivedProjects() throws IOException {
		Path archivedPath = _modulesDirPath.resolve("apps/archived");

		if (!Files.exists(archivedPath)) {
			return;
		}

		Files.walkFileTree(
			archivedPath, EnumSet.noneOf(FileVisitOption.class), 2,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
						Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					if (Files.isDirectory(path)) {
						return FileVisitResult.CONTINUE;
					}

					String fileName = String.valueOf(path.getFileName());

					if (!StringUtil.startsWith(fileName, ".lfrbuild-portal") ||
						StringUtil.endsWith(fileName, "-deprecated")) {

						return FileVisitResult.CONTINUE;
					}

					Files.move(
						path,
						path.resolveSibling(".lfrbuild-portal-deprecated"));

					Assert.fail(
						StringBundler.concat(
							"Renamed archived module build marker to ",
							"'.lfrbuild-portal-deprecated' ", path));

					return FileVisitResult.CONTINUE;
				}

			});
	}

	@Test
	public void testScanBuildScripts() throws IOException {
		final String gitRepoBuildGradleTemplate = _getGradleTemplate(
			"dependencies/git_repo_build_gradle.tmpl");
		final String gitRepoSettingsGradleTemplate = _getGradleTemplate(
			"dependencies/git_repo_settings_gradle.tmpl");

		Files.walkFileTree(
			_modulesDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					if (dirPath.equals(_modulesDirPath)) {
						return FileVisitResult.CONTINUE;
					}

					String dirName = String.valueOf(dirPath.getFileName());

					if ((dirName.charAt(0) == '.') ||
						_excludedDirNames.contains(dirName)) {

						return FileVisitResult.SKIP_SUBTREE;
					}

					Path appBndLocalizationDirPath = dirPath.resolve(
						"app.bnd-localization");

					if (Files.exists(appBndLocalizationDirPath)) {
						Path parentPath = appBndLocalizationDirPath.getParent();

						Assert.assertTrue(
							"Forbidden " + appBndLocalizationDirPath,
							Files.exists(parentPath.resolve("app.bnd")));
					}

					Path buildGradlePath = dirPath.resolve("build.gradle");
					Path buildXMLPath = dirPath.resolve("build.xml");
					Path ivyXmlPath = dirPath.resolve("ivy.xml");

					boolean gitRepo = Files.exists(
						dirPath.resolve(_GIT_REPO_FILE_NAME));

					if (gitRepo) {
						_testGitRepoBuildScripts(
							dirPath, gitRepoBuildGradleTemplate,
							gitRepoSettingsGradleTemplate);
					}
					else {
						if (!dirName.endsWith("playwright") &&
							!dirName.endsWith("poshi-standalone")) {

							Path gradlePropertiesPath = dirPath.resolve(
								"gradle.properties");

							Assert.assertFalse(
								"Forbidden " + gradlePropertiesPath,
								Files.deleteIfExists(gradlePropertiesPath));

							Path settingsGradlePath = dirPath.resolve(
								"settings.gradle");

							Assert.assertFalse(
								"Forbidden " + settingsGradlePath,
								Files.deleteIfExists(settingsGradlePath));
						}

						Path appBndPath = dirPath.resolve("app.bnd");

						if (Files.exists(appBndPath)) {
							_testDirWithOnlyTests(
								appBndPath, buildGradlePath, dirPath);

							_testEquals(buildGradlePath, _APP_BUILD_GRADLE);

							_testRelengAppProperties(dirPath);
						}
						else {
							Assert.assertFalse(
								"Forbidden " + buildGradlePath,
								Files.exists(buildGradlePath) &&
								ModulesStructureTestUtil.contains(
									buildGradlePath, _APP_BUILD_GRADLE,
									"apply plugin: " +
										"\"com.liferay.defaults.plugin\"",
									"apply plugin: " +
										"\"com.liferay.root.defaults." +
											"plugin\""));
						}

						Path buildExtGradlePath = dirPath.resolve(
							"build-ext.gradle");

						Assert.assertFalse(
							"Forbidden " + buildExtGradlePath,
							Files.deleteIfExists(buildExtGradlePath));
					}

					if (Files.exists(dirPath.resolve("package.json")) &&
						!Files.exists(_modulesDirPath.resolve("yarn.lock"))) {

						String dirAbsolutePath =
							ModulesStructureTestUtil.getAbsolutePath(dirPath);
						Path packageJSONPath = dirPath.resolve("package.json");

						if (!ModulesStructureTestUtil.contains(
								packageJSONPath, "\"liferay-theme-tasks\":") &&
							!dirAbsolutePath.contains("/project-templates/")) {

							Path packageLockJSONPath = dirPath.resolve(
								"package-lock.json");

							Assert.assertTrue(
								"Missing " + packageLockJSONPath,
								Files.exists(packageLockJSONPath));
						}
					}

					if (Files.exists(dirPath.resolve("bnd.bnd"))) {
						Assert.assertTrue(
							"Missing " + buildGradlePath,
							Files.exists(buildGradlePath));
						Assert.assertFalse(
							"Forbidden " + buildXMLPath,
							Files.deleteIfExists(buildXMLPath));

						Assert.assertFalse(
							"Forbidden " + ivyXmlPath,
							Files.deleteIfExists(ivyXmlPath));

						_testJSONVersion(dirPath);

						return FileVisitResult.SKIP_SUBTREE;
					}

					if (Files.exists(buildXMLPath)) {
						Assert.assertFalse(
							"Forbidden " + buildGradlePath,
							Files.notExists(ivyXmlPath) &&
							Files.exists(buildGradlePath));

						return FileVisitResult.SKIP_SUBTREE;
					}

					if (Files.exists(dirPath.resolve("package.json"))) {
						Path packageJSONPath = dirPath.resolve("package.json");

						if (ModulesStructureTestUtil.contains(
								packageJSONPath, "\"liferay-theme-tasks\":")) {

							_testThemeBuildScripts(dirPath);
						}

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	@Test
	public void testScanCircularProjectDependencies() throws IOException {
		Map<String, Module> modules = new TreeMap<>();

		for (String includeDirName : _includedDirNames) {
			Files.walkFileTree(
				_modulesDirPath.resolve(includeDirName),
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(
							Path dirPath,
							BasicFileAttributes basicFileAttributes)
						throws IOException {

						Path buildGradlePath = dirPath.resolve("build.gradle");

						if (!Files.exists(buildGradlePath) ||
							!Files.exists(dirPath.resolve("src"))) {

							return FileVisitResult.CONTINUE;
						}

						String relativePathString = String.valueOf(
							_modulesDirPath.relativize(dirPath));

						Module module = new Module(
							":".concat(
								StringUtil.replace(
									relativePathString, '/', ':')),
							ModulesStructureTestUtil.getProjectDependencyIds(
								buildGradlePath));

						modules.put(module.getId(), module);

						return FileVisitResult.SKIP_SUBTREE;
					}

				});
		}

		Set<String> circularPaths = new TreeSet<>();

		for (Module module : modules.values()) {
			Deque<String> scanDeque = new LinkedList<>();

			scanDeque.add(module.getId());

			List<String> pathsList = new ArrayList<>();

			String dependencyId = null;

			while ((dependencyId = scanDeque.pollFirst()) != null) {
				if (Objects.equals(dependencyId, "REMOVE_LAST_HOLDER")) {
					pathsList.remove(pathsList.size() - 1);

					continue;
				}

				int index = pathsList.indexOf(dependencyId);

				if (index == -1) {
					pathsList.add(dependencyId);

					scanDeque.push("REMOVE_LAST_HOLDER");

					Module dependencyModule = modules.get(dependencyId);

					List<String> dependencyIdList = new ArrayList<>(
						dependencyModule.getDependencyIds());

					ListIterator<String> listIterator =
						dependencyIdList.listIterator(dependencyIdList.size());

					while (listIterator.hasPrevious()) {
						scanDeque.push(listIterator.previous());
					}
				}
				else {
					pathsList = pathsList.subList(index, pathsList.size());

					String minPath = Collections.min(pathsList);

					int minIndex = pathsList.indexOf(minPath);

					StringBundler sb = new StringBundler(
						((pathsList.size() - index) * 2) + 1);

					for (int i = minIndex; i < (minIndex + pathsList.size());
						 i++) {

						sb.append(pathsList.get(i % pathsList.size()));
						sb.append(" -> ");
					}

					sb.append(minPath);

					circularPaths.add(sb.toString());

					break;
				}
			}
		}

		Assert.assertTrue(circularPaths.toString(), circularPaths.isEmpty());
	}

	@Test
	public void testScanGitHub() throws IOException {
		Files.walkFileTree(
			_modulesDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
					Path dirPath, BasicFileAttributes basicFileAttributes) {

					String dirName = String.valueOf(dirPath.getFileName());

					if (_excludedDirNames.contains(dirName)) {
						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
					Path path, BasicFileAttributes basicFileAttributes) {

					String fileName = String.valueOf(path.getFileName());

					if (fileName.equals("CODEOWNERS")) {
						_testGitHubCodeOwners(path);
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	@Test
	public void testScanGradleFiles() throws IOException {
		Files.walkFileTree(
			_modulesDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
					Path dirPath, BasicFileAttributes basicFileAttributes) {

					String dirName = String.valueOf(dirPath.getFileName());

					if (_excludedDirNames.contains(dirName) ||
						dirName.equals("archetype-resources") ||
						dirName.equals("gradleTest")) {

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
						Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String fileName = String.valueOf(path.getFileName());

					if (StringUtil.endsWith(fileName, ".gradle")) {
						_testGradleFile(path);
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	@Test
	public void testScanIgnoreFiles() throws IOException {
		String gitRepoGitIgnoreTemplate = StringUtil.read(
			ModulesStructureTest.class, "dependencies/git_repo_gitignore.tmpl");
		final String themeGitIgnoreTemplate = StringUtil.read(
			ModulesStructureTest.class, "dependencies/theme_gitignore.tmpl");
		final String themeNpmIgnoreTemplate = StringUtil.read(
			ModulesStructureTest.class, "dependencies/theme_npmignore.tmpl");

		final Set<String> gitRepoGitIgnoreTemplateLines = SetUtil.fromString(
			gitRepoGitIgnoreTemplate);

		Files.walkFileTree(
			_modulesDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String dirName = String.valueOf(dirPath.getFileName());

					if (dirName.equals("gradleTest") ||
						dirName.equals("project-templates") ||
						_excludedDirNames.contains(dirName)) {

						return FileVisitResult.SKIP_SUBTREE;
					}

					if (dirPath.equals(_modulesDirPath) ||
						Files.exists(dirPath.resolve(_GIT_REPO_FILE_NAME))) {

						_testGitRepoIgnoreFiles(
							dirPath, gitRepoGitIgnoreTemplateLines);
					}
					else if (!dirPath.equals(_modulesDirPath) &&
							 Files.exists(dirPath.resolve("build.xml"))) {

						_testAntPluginIgnoreFiles(dirPath);
					}
					else if (StringUtil.startsWith(
								dirName, "frontend-theme-") &&
							 Files.exists(dirPath.resolve("gulpfile.js")) &&
							 !Files.exists(
								 dirPath.resolve(".lfrbuild-releng-ignore"))) {

						_testThemeIgnoreFiles(
							dirPath, themeGitIgnoreTemplate,
							themeNpmIgnoreTemplate);
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
						Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String fileName = String.valueOf(path.getFileName());

					if (fileName.equals(".gitignore")) {
						_testGitIgnoreFile(path, gitRepoGitIgnoreTemplateLines);
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	@Test
	public void testScanLog4JConfigurationXML() throws IOException {
		final Map<String, String> renameMap = HashMapBuilder.put(
			"src/main/resources/META-INF/portal-log4j-ext.xml",
			"module-log4j-ext.xml"
		).put(
			"src/main/resources/META-INF/portal-log4j.xml", "module-log4j.xml"
		).put(
			"src/META-INF/portal-log4j-ext.xml", "module-log4j-ext.xml"
		).put(
			"src/META-INF/portal-log4j.xml", "module-log4j.xml"
		).build();

		Files.walkFileTree(
			_modulesDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
					Path dirPath, BasicFileAttributes basicFileAttributes) {

					String dirName = String.valueOf(dirPath.getFileName());

					if (_excludedDirNames.contains(dirName)) {
						return FileVisitResult.SKIP_SUBTREE;
					}

					if (Files.exists(dirPath.resolve("bnd.bnd"))) {
						for (Map.Entry<String, String> entry :
								renameMap.entrySet()) {

							Path path = dirPath.resolve(entry.getKey());

							Assert.assertFalse(
								StringBundler.concat(
									"Please rename ", path, " to ",
									path.resolveSibling(entry.getValue())),
								Files.exists(path));
						}
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	@Test
	public void testScanReadmeFiles() throws IOException {
		Files.walkFileTree(
			_modulesDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String dirName = String.valueOf(dirPath.getFileName());

					if (_excludedDirNames.contains(dirName)) {
						return FileVisitResult.SKIP_SUBTREE;
					}

					Path path = dirPath.resolve("README.md");

					if (Files.exists(path)) {
						BasicFileAttributes readmeBasicFileAttributes =
							Files.readAttributes(
								path, BasicFileAttributes.class);

						Assert.assertNotEquals(
							"Please delete the empty readme file " + path, 0,
							readmeBasicFileAttributes.size());
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	private void _addGradlePluginNames(
			Set<String> pluginNames, String pluginNamePrefix,
			Path buildGradlePath, String pluginIdPrefix,
			String[] pluginIdSuffixes)
		throws IOException {

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(
					new FileReader(buildGradlePath.toFile()))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				line = StringUtil.trim(line);

				for (String pluginIdSuffix : pluginIdSuffixes) {
					String pluginLine = StringBundler.concat(
						"apply plugin: \"", pluginIdPrefix, pluginIdSuffix,
						"\"");

					if (line.equals(pluginLine)) {
						pluginNames.add(pluginNamePrefix + pluginIdSuffix);
					}
				}
			}
		}
	}

	private GradleDependency _getActiveGradleDependency(
		List<GradleDependency> gradleDependencies,
		GradleDependency gradleDependency) {

		int configurationPos = _gradleConfigurations.indexOf(
			gradleDependency.getConfiguration());
		String moduleGroup = gradleDependency.getModuleGroup();
		String moduleName = gradleDependency.getModuleName();
		String moduleVersion = gradleDependency.getModuleVersion();

		for (GradleDependency curGradleDependency : gradleDependencies) {
			if (!moduleGroup.equals(curGradleDependency.getModuleGroup()) ||
				!moduleName.equals(curGradleDependency.getModuleName()) ||
				!_gradleConfigurations.contains(
					curGradleDependency.getConfiguration())) {

				continue;
			}

			int curConfigurationPos = _gradleConfigurations.indexOf(
				curGradleDependency.getConfiguration());

			int value = ModulesStructureTestUtil.compare(
				moduleVersion, curGradleDependency.getModuleVersion());

			if (((curConfigurationPos == configurationPos) && (value < 0)) ||
				((curConfigurationPos < configurationPos) && (value <= 0))) {

				return curGradleDependency;
			}
		}

		return gradleDependency;
	}

	private String _getAntPluginsGitIgnore(
			final Path dirPath, SortedSet<String> gitIgnoreLines)
		throws IOException {

		if (dirPath.equals(_modulesDirPath)) {
			return StringUtil.merge(gitIgnoreLines, StringPool.NEW_LINE);
		}

		final Set<String> pluginDirNames = new TreeSet<>();

		Files.walkFileTree(
			dirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
					Path pluginDirPath,
					BasicFileAttributes basicFileAttributes) {

					if (Files.exists(pluginDirPath.resolve("build.xml")) &&
						Files.exists(
							pluginDirPath.resolve(
								"docroot/WEB-INF/service.xml"))) {

						String pluginDirName = String.valueOf(
							dirPath.relativize(pluginDirPath));

						if (File.separatorChar != CharPool.SLASH) {
							pluginDirName = StringUtil.replace(
								pluginDirName, File.separatorChar,
								CharPool.SLASH);
						}

						pluginDirNames.add(pluginDirName);

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

			});

		if (pluginDirNames.isEmpty()) {
			return StringUtil.merge(gitIgnoreLines, StringPool.NEW_LINE);
		}

		StringBundler sb = new StringBundler(
			(gitIgnoreLines.size() * 2) + (pluginDirNames.size() * 14));

		if (SetUtil.isNotEmpty(gitIgnoreLines)) {
			for (String line : gitIgnoreLines) {
				sb.append(line);
				sb.append(CharPool.NEW_LINE);
			}

			sb.append(CharPool.NEW_LINE);
		}

		boolean first = true;

		for (String pluginDirName : pluginDirNames) {
			if (!first) {
				sb.append(CharPool.NEW_LINE);
			}

			first = false;

			sb.append("!/");
			sb.append(pluginDirName);
			sb.append("/docroot/WEB-INF/lib");

			sb.append(CharPool.NEW_LINE);

			sb.append(CharPool.SLASH);
			sb.append(pluginDirName);
			sb.append("/docroot/WEB-INF/lib/*");

			sb.append(CharPool.NEW_LINE);

			sb.append("!/");
			sb.append(pluginDirName);
			sb.append("/docroot/WEB-INF/lib/");
			sb.append(pluginDirName);
			sb.append("-service.jar");
		}

		return sb.toString();
	}

	private String _getGitRepoBuildGradle(
			Path dirPath, String buildGradleTemplate)
		throws IOException {

		List<String> sortedBuildExtGradleFileNames = new ArrayList<>();

		if (Files.exists(dirPath.resolve("build-ext.gradle"))) {
			sortedBuildExtGradleFileNames.add("build-ext.gradle");
		}

		Set<String> buildExtGradleFileNames = new TreeSet<>(
			String.CASE_INSENSITIVE_ORDER);

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				dirPath)) {

			for (Path path : directoryStream) {
				if (Files.isDirectory(path)) {
					continue;
				}

				String fileName = String.valueOf(path.getFileName());

				if (!fileName.endsWith(".gradle") ||
					!fileName.startsWith("build-ext-")) {

					continue;
				}

				buildExtGradleFileNames.add(fileName);
			}
		}

		sortedBuildExtGradleFileNames.addAll(buildExtGradleFileNames);

		if (!sortedBuildExtGradleFileNames.isEmpty()) {
			StringBundler sb = new StringBundler(
				(sortedBuildExtGradleFileNames.size() * 3) + 2);

			sb.append(buildGradleTemplate);
			sb.append(StringPool.NEW_LINE);

			for (String fileName : sortedBuildExtGradleFileNames) {
				sb.append("\napply from: \"");
				sb.append(fileName);
				sb.append("\"");
			}

			buildGradleTemplate = sb.toString();
		}

		final Set<String> pluginNames = new TreeSet<>();

		pluginNames.add("com.liferay.gradle.plugins.defaults");

		Files.walkFileTree(
			dirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					Path buildGradlePath = dirPath.resolve("build.gradle");

					if (Files.exists(buildGradlePath) &&
						Files.notExists(dirPath.resolve(_GIT_REPO_FILE_NAME))) {

						_addGradlePluginNames(
							pluginNames, "com.liferay.gradle.plugins.",
							buildGradlePath, "com.liferay.",
							new String[] {"maven.plugin.builder"});

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

			});

		StringBundler sb = new StringBundler((pluginNames.size() * 4) - 1);

		int i = 0;

		for (String pluginName : pluginNames) {
			if (i > 0) {
				sb.append(CharPool.NEW_LINE);
			}

			sb.append("\t\tclasspath group: \"com.liferay\", name: \"");
			sb.append(pluginName);
			sb.append("\", version: \"latest.release\"");

			i++;
		}

		return StringUtil.replace(
			buildGradleTemplate, "[$BUILDSCRIPT_DEPENDENCIES$]", sb.toString());
	}

	private Path _getGitRepoPath(Path dirPath) {
		while (dirPath != null) {
			Path gitRepoPath = dirPath.resolve(_GIT_REPO_FILE_NAME);

			if (Files.exists(gitRepoPath)) {
				return gitRepoPath;
			}

			dirPath = dirPath.getParent();
		}

		return null;
	}

	private String _getGradleTemplate(String name) {
		String template = StringUtil.read(ModulesStructureTest.class, name);

		return StringUtil.replace(
			template, "[$REPOSITORY_URL$]", _REPOSITORY_URL);
	}

	private String _getProjectPathPrefix(Path dirPath) {
		String projectPathPrefix = String.valueOf(
			_modulesDirPath.relativize(dirPath));

		projectPathPrefix = StringUtil.replace(
			projectPathPrefix, File.separatorChar, CharPool.COLON);

		return ":" + projectPathPrefix;
	}

	private boolean _hasGitCommitMarkerFile(Path dirPath) {
		while (dirPath != null) {
			if (dirPath.equals(_modulesDirPath)) {
				return false;
			}

			Path gitRepoPath = dirPath.resolve(
				"git-commit-" + String.valueOf(dirPath.getFileName()));

			if (Files.exists(gitRepoPath)) {
				return true;
			}

			dirPath = dirPath.getParent();
		}

		return false;
	}

	private boolean _isEmptyGitRepo(Path dirPath) {
		File dir = dirPath.toFile();

		String[] fileNames = dir.list();

		if ((fileNames.length == 1) &&
			_GIT_REPO_FILE_NAME.equals(fileNames[0])) {

			return true;
		}

		return false;
	}

	private boolean _isInDXPModulesDir(Path dirPath) {
		return _isInModulesRootDir(dirPath, "dxp");
	}

	private boolean _isInGitRepoReadOnly(Path dirPath) throws IOException {
		Path gitRepoPath = _getGitRepoPath(dirPath);

		if (gitRepoPath == null) {
			return false;
		}

		if (ModulesStructureTestUtil.contains(gitRepoPath, "mode = pull")) {
			return true;
		}

		return false;
	}

	private boolean _isInModulesRootDir(Path dirPath, String... rootDirNames) {
		Path relativePath = _modulesDirPath.relativize(dirPath);

		for (String rootDirName : rootDirNames) {
			if (relativePath.startsWith(rootDirName)) {
				return true;
			}
		}

		return false;
	}

	private boolean _isInPrivateModulesCheckoutDir(Path dirPath) {
		if (!_isInPrivateModulesDir(dirPath)) {
			return false;
		}

		String absolutePath = ModulesStructureTestUtil.getAbsolutePath(dirPath);

		for (String checkoutPrivateAppsDir : _checkoutPrivateAppsDirs) {
			if (absolutePath.contains(checkoutPrivateAppsDir)) {
				return true;
			}
		}

		return false;
	}

	private boolean _isInPrivateModulesDir(Path dirPath) {
		return _isInModulesRootDir(dirPath, "private");
	}

	private boolean _isUnusedGradleConfiguration(
		String configuration, boolean hasSrcTestDir,
		boolean hasSrcTestIntegrationDir) {

		if (configuration.equals("testImplementation") && !hasSrcTestDir &&
			!hasSrcTestIntegrationDir) {

			return true;
		}

		if (configuration.equals("testRuntimeOnly") && !hasSrcTestDir &&
			!hasSrcTestIntegrationDir) {

			return true;
		}

		if (configuration.startsWith("testIntegration") &&
			!hasSrcTestIntegrationDir) {

			return true;
		}

		return false;
	}

	private boolean _shouldBecomeProjectDependency(
			GradleDependency gradleDependency, Path dirPath)
		throws IOException {

		if (dirPath.equals(_modulesDirPath) ||
			gradleDependency.isProjectDependency()) {

			return false;
		}

		Path lfrbuildRelengSkipUpdateFileVersionsPath = dirPath.resolve(
			".lfrbuild-releng-skip-update-file-versions");

		if (!Files.exists(lfrbuildRelengSkipUpdateFileVersionsPath) &&
			!_masterBranch) {

			return false;
		}

		String group = gradleDependency.getModuleGroup();

		if (!group.equals("com.liferay") &&
			!group.equals("com.liferay.portal")) {

			return false;
		}

		String dirName = String.valueOf(dirPath.getFileName());
		String name = gradleDependency.getModuleName();

		if (dirName.endsWith("poshi-standalone") ||
			name.equals("com.liferay.ant.bnd") ||
			name.equals("com.liferay.arquillian.extension.junit.bridge") ||
			name.equals("com.liferay.gradle.plugins.app.docker") ||
			name.equals("com.liferay.gradle.plugins.defaults") ||
			name.equals("com.liferay.portal.cache.test.util") ||
			name.equals("com.liferay.poshi.core") ||
			name.equals("com.liferay.whip") ||
			name.startsWith("com.liferay.faces.") ||
			!name.startsWith("com.liferay.") ||
			_isInModulesRootDir(dirPath, "sdk", "third-party", "util") ||
			Files.exists(dirPath.resolve("settings.gradle")) ||
			Files.exists(dirPath.resolve(".lfrbuild-ci")) ||
			_hasGitCommitMarkerFile(dirPath) || _isInGitRepoReadOnly(dirPath) ||
			_isInPrivateModulesCheckoutDir(dirPath)) {

			return false;
		}

		return true;
	}

	private void _testAntPluginIgnoreFiles(Path dirPath) throws IOException {
		if (_getGitRepoPath(dirPath) == null) {
			Path parentDirPath = dirPath.getParent();

			_testEquals(
				parentDirPath.resolve(".gitignore"),
				_getAntPluginsGitIgnore(
					parentDirPath, Collections.emptySortedSet()));
		}
	}

	private void _testDirWithOnlyTests(
		Path appBndPath, Path buildGradlePath, Path dirPath) {

		File dirPathFile = dirPath.toFile();

		File[] dirPathFiles = dirPathFile.listFiles();

		if (dirPathFiles == null) {
			return;
		}

		for (File file : dirPathFiles) {
			if (file.isDirectory()) {
				String modulePath = file.toString();

				if (!modulePath.endsWith("-test") ||
					!modulePath.endsWith("-test-util")) {

					return;
				}
			}
		}

		Assert.assertFalse(
			"Unexpected file " + appBndPath, Files.exists(appBndPath));

		Assert.assertFalse(
			"Unexpected file " + buildGradlePath,
			Files.exists(buildGradlePath));
	}

	private void _testEquals(Path path, String expected) throws IOException {
		if (Validator.isNotNull(expected)) {
			String actual = ModulesStructureTestUtil.read(path);

			Assert.assertEquals("Incorrect " + path, expected, actual);
		}
		else {
			Assert.assertFalse("Forbidden " + path, Files.exists(path));
		}
	}

	private void _testGitHubCodeOwners(Path path) {
		Path dirPath = path.getParent();

		Assert.assertEquals(
			"Forbidden " + path, ".github",
			String.valueOf(dirPath.getFileName()));

		Path rootDirPath = dirPath.getParent();

		Assert.assertTrue(
			"Forbidden " + path,
			Files.exists(rootDirPath.resolve(_GIT_REPO_FILE_NAME)));
	}

	private void _testGitIgnoreFile(
			Path path, Set<String> gitRepoGitIgnoreTemplateLines)
		throws IOException {

		Path dirPath = path.getParent();

		boolean gitRepoGitIgnore = Files.exists(
			dirPath.resolve(_GIT_REPO_FILE_NAME));

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new FileReader(path.toFile()))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				line = StringUtil.trim(line);

				if (!gitRepoGitIgnore && !dirPath.equals(_modulesDirPath)) {
					Assert.assertFalse(
						"Forbidden \"" + line + "\" in " + path,
						gitRepoGitIgnoreTemplateLines.contains(line));
				}

				if (!StringUtil.startsWith(line, "!/")) {
					continue;
				}

				int end = line.indexOf(CharPool.SLASH, 2);

				if (end == -1) {
					end = line.length();
				}

				String name = line.substring(2, end);

				Assert.assertTrue(
					StringBundler.concat("Incorrect \"", line, "\" in ", path),
					Files.exists(dirPath.resolve(name)));
			}
		}
	}

	private void _testGitRepoBuildScripts(
			Path dirPath, String buildGradleTemplate,
			String settingsGradleTemplate)
		throws IOException {

		if (_isEmptyGitRepo(dirPath)) {
			return;
		}

		boolean dxpRepo = _isInDXPModulesDir(dirPath);
		boolean privateRepo = _isInPrivateModulesDir(dirPath);
		boolean readOnlyRepo = _isInGitRepoReadOnly(dirPath);

		Path buildGradlePath = dirPath.resolve("build.gradle");
		Path gradlePropertiesPath = dirPath.resolve("gradle.properties");
		Path settingsGradlePath = dirPath.resolve("settings.gradle");

		if (!Files.exists(buildGradlePath) &&
			!Files.exists(gradlePropertiesPath) &&
			!Files.exists(settingsGradlePath)) {

			return;
		}

		if (!dxpRepo && !privateRepo && !readOnlyRepo) {
			String buildGradle = ModulesStructureTestUtil.read(buildGradlePath);

			Assert.assertEquals(
				"Incorrect " + buildGradlePath,
				_getGitRepoBuildGradle(dirPath, buildGradleTemplate),
				buildGradle);
		}

		if (Files.exists(gradlePropertiesPath)) {
			_testGradleBuildProperties(
				dirPath, gradlePropertiesPath, dxpRepo, privateRepo);
		}

		if (Files.notExists(dirPath.resolve("settings-ext.gradle"))) {
			settingsGradleTemplate = StringUtil.removeSubstring(
				settingsGradleTemplate,
				"\n\napply from: \"settings-ext.gradle\"");
		}

		if (!dxpRepo && !privateRepo && !readOnlyRepo) {
			String settingsGradle = ModulesStructureTestUtil.read(
				settingsGradlePath);

			Assert.assertEquals(
				"Incorrect " + settingsGradlePath, settingsGradleTemplate,
				settingsGradle);
		}
	}

	private void _testGitRepoIgnoreFiles(
			Path dirPath, Set<String> gitIgnoreTemplateLines)
		throws IOException {

		if (_isEmptyGitRepo(dirPath)) {
			return;
		}

		Path gitIgnorePath = dirPath.resolve(".gitignore");

		String gitIgnore = ModulesStructureTestUtil.read(gitIgnorePath);

		String[] gitIgnoreLines = StringUtil.splitLines(gitIgnore);

		SortedSet<String> validGitIgnoreLines = new TreeSet<>(
			gitIgnoreTemplateLines);

		for (String line : gitIgnoreLines) {
			for (String prefix : _GIT_IGNORE_LINE_PREFIXES) {
				if (line.startsWith(prefix)) {
					validGitIgnoreLines.add(line);
				}
			}
		}

		for (String line : _GIT_IGNORE_OPTIONAL_LINES) {
			if (!ArrayUtil.contains(gitIgnoreLines, line)) {
				validGitIgnoreLines.remove(line);
			}
		}

		Assert.assertEquals(
			"Incorrect " + gitIgnorePath,
			_getAntPluginsGitIgnore(dirPath, validGitIgnoreLines), gitIgnore);
	}

	private void _testGitRepoProjectGroup(
		String messagePrefix, String projectGroup) {

		if (Validator.isNull(projectGroup)) {
			return;
		}

		for (String prefix : _GIT_REPO_GRADLE_PROJECT_GROUP_RESERVED_PREFIXES) {
			Assert.assertFalse(
				StringBundler.concat(
					messagePrefix, " cannot start with the reserved prefix \"",
					prefix, "\""),
				StringUtil.startsWith(projectGroup, prefix));
		}

		Matcher matcher = _gitRepoGradleProjectGroupPattern.matcher(
			projectGroup);

		Assert.assertTrue(
			StringBundler.concat(
				messagePrefix, " must match pattern \"",
				_gitRepoGradleProjectGroupPattern.pattern(), "\""),
			matcher.matches());
	}

	private void _testGradleBuildProperties(
			Path dirPath, Path gradlePropertiesPath, boolean dxpRepo,
			boolean privateRepo)
		throws IOException {

		String gradleProperties = ModulesStructureTestUtil.read(
			gradlePropertiesPath);

		Assert.assertEquals(
			"Forbidden leading or trailing whitespaces in " +
				gradlePropertiesPath,
			gradleProperties.trim(), gradleProperties);

		String gradlePropertiesPrefix = StringUtil.replace(
			String.valueOf(dirPath.getFileName()), CharPool.DASH,
			CharPool.PERIOD);

		gradlePropertiesPrefix = "com.liferay." + gradlePropertiesPrefix;

		Pattern gradlePropertiesPattern = Pattern.compile(
			StringUtil.replace(gradlePropertiesPrefix, CharPool.PERIOD, "\\.") +
				"(\\.[a-z0-9]+)+");

		String previousKey = null;
		String projectGroup = null;
		String projectPathPrefix = null;
		String repositoryPrivatePassword = null;
		String repositoryPrivateUrl = null;
		String repositoryPrivateUserName = null;

		String[] lines = StringUtil.split(gradleProperties, CharPool.NEW_LINE);

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];

			Assert.assertEquals(
				StringBundler.concat(
					"Forbidden leading or trailing whitespaces in line ", i + 1,
					" of ", gradlePropertiesPath),
				line.trim(), line);

			Assert.assertFalse(
				"Forbidden empty line in " + gradlePropertiesPath,
				Validator.isNull(line));

			int pos = line.indexOf(CharPool.EQUAL);

			Assert.assertTrue(
				StringBundler.concat(
					"Incorrect line \"", line, "\" in ", gradlePropertiesPath),
				pos != -1);

			String key = line.substring(0, pos);
			String value = line.substring(pos + 1);

			Assert.assertTrue(
				gradlePropertiesPath +
					" contains duplicate lines or is not sorted",
				(previousKey == null) || (key.compareTo(previousKey) > 0));

			if (key.equals(_GIT_REPO_GRADLE_PROJECT_GROUP_KEY)) {
				projectGroup = value;
			}
			else if (key.equals(_GIT_REPO_GRADLE_PROJECT_PATH_PREFIX_KEY)) {
				projectPathPrefix = value;
			}
			else if ((dxpRepo || privateRepo) &&
					 key.equals(_GIT_REPO_GRADLE_REPOSITORY_PRIVATE_PASSWORD)) {

				repositoryPrivatePassword = value;
			}
			else if ((dxpRepo || privateRepo) &&
					 key.equals(_GIT_REPO_GRADLE_REPOSITORY_PRIVATE_URL)) {

				repositoryPrivateUrl = value;
			}
			else if ((dxpRepo || privateRepo) &&
					 key.equals(_GIT_REPO_GRADLE_REPOSITORY_PRIVATE_USERNAME)) {

				repositoryPrivateUserName = value;
			}
			else {
				Matcher matcher = gradlePropertiesPattern.matcher(key);

				StringBundler sb = new StringBundler(
					((_gitRepoGradlePropertiesKeys.size() + 5) * 3) + 8);

				sb.append("Incorrect key \"");
				sb.append(key);
				sb.append("\" in ");
				sb.append(gradlePropertiesPath);
				sb.append(". Allowed keys are: ");

				List<String> allowedKeys = new ArrayList<>(
					_gitRepoGradlePropertiesKeys);

				allowedKeys.add(_GIT_REPO_GRADLE_PROJECT_GROUP_KEY);
				allowedKeys.add(_GIT_REPO_GRADLE_PROJECT_PATH_PREFIX_KEY);

				if (dxpRepo || privateRepo) {
					allowedKeys.add(
						_GIT_REPO_GRADLE_REPOSITORY_PRIVATE_PASSWORD);
					allowedKeys.add(_GIT_REPO_GRADLE_REPOSITORY_PRIVATE_URL);
					allowedKeys.add(
						_GIT_REPO_GRADLE_REPOSITORY_PRIVATE_USERNAME);
				}

				Collections.sort(allowedKeys);

				for (String allowedKey : allowedKeys) {
					sb.append(CharPool.QUOTE);
					sb.append(allowedKey);
					sb.append("\", ");
				}

				sb.append(", keys ending with \".version\", and keys ");
				sb.append("matching the pattern \"");
				sb.append(gradlePropertiesPattern.pattern());
				sb.append("\".");

				Assert.assertFalse(
					sb.toString(),
					!_gitRepoGradlePropertiesKeys.contains(key) &&
					!key.endsWith(".ignore.local") &&
					!key.endsWith(".version") && !matcher.matches());
			}

			previousKey = key;
		}

		_testGitRepoProjectGroup(
			StringBundler.concat(
				"Property \"", _GIT_REPO_GRADLE_PROJECT_GROUP_KEY, "\" in ",
				gradlePropertiesPath),
			projectGroup);

		// TODO Remove workaround for 7.1 after commerce is merged

		String expectedProjectPathPrefix = _getProjectPathPrefix(dirPath);

		if (!Objects.equals(expectedProjectPathPrefix, ":dxp:apps:commerce")) {
			Assert.assertEquals(
				StringBundler.concat(
					"Incorrect \"", _GIT_REPO_GRADLE_PROJECT_PATH_PREFIX_KEY,
					"\" in ", gradlePropertiesPath),
				expectedProjectPathPrefix, projectPathPrefix);
		}

		// TODO Remove the check for 7.0 once osb-loop and osb-testray are fixed

		if (!_branchName.startsWith("7.0") && (dxpRepo || privateRepo)) {
			_testGradleBuildProperty(
				gradlePropertiesPath,
				_GIT_REPO_GRADLE_REPOSITORY_PRIVATE_PASSWORD,
				repositoryPrivatePassword, "build.repository.private.password");
			_testGradleBuildProperty(
				gradlePropertiesPath, _GIT_REPO_GRADLE_REPOSITORY_PRIVATE_URL,
				repositoryPrivateUrl, "build.repository.private.url");
			_testGradleBuildProperty(
				gradlePropertiesPath,
				_GIT_REPO_GRADLE_REPOSITORY_PRIVATE_USERNAME,
				repositoryPrivateUserName, "build.repository.private.username");
		}
	}

	private void _testGradleBuildProperty(
		Path gradlePropertiesPath, String key, String value,
		String buildPropertyKey) {

		String expectedValue = _buildProperties.getProperty(buildPropertyKey);

		if (Validator.isNotNull(expectedValue) && Validator.isNotNull(value)) {
			Assert.assertEquals(
				StringBundler.concat(
					"Incorrect \"", key, "\" in ", gradlePropertiesPath),
				expectedValue, value);
		}
	}

	private void _testGradleFile(Path path) throws IOException {
		String content = ModulesStructureTestUtil.read(path);

		Assert.assertFalse(
			StringBundler.concat(
				"Incorrect repository URL in ", path, ", please use ",
				_REPOSITORY_URL, " instead"),
			content.contains("plugins.gradle.org/m2"));

		Assert.assertFalse(
			"Plugins DSL forbidden in " + path +
				", please use \"apply plugin:\" instead",
			!content.contains("pluginBundle {") &&
			content.contains("plugins {"));

		List<GradleDependency> gradleDependencies =
			ModulesStructureTestUtil.getGradleDependencies(
				content, path, _modulesDirPath);

		Path dirPath = path.getParent();

		boolean hasSrcMainDir = Files.exists(dirPath.resolve("src/main"));
		boolean hasSrcTestDir = Files.exists(dirPath.resolve("src/test"));
		boolean hasSrcTestIntegrationDir = Files.exists(
			dirPath.resolve("src/testIntegration"));

		boolean mainConfigurationsAllowed = false;

		if ((_branchName.startsWith("7.0") || _branchName.startsWith("7.1")) &&
			content.contains("copyLibs {\n\tenabled = true")) {

			mainConfigurationsAllowed = true;
		}

		if (hasSrcMainDir ||
			(!hasSrcMainDir && !hasSrcTestDir && !hasSrcTestIntegrationDir)) {

			mainConfigurationsAllowed = true;
		}

		Map<String, Boolean> allowedConfigurationsMap = TreeMapBuilder.put(
			"api", mainConfigurationsAllowed
		).put(
			"compileOnly", mainConfigurationsAllowed
		).put(
			"provided", mainConfigurationsAllowed
		).put(
			"testImplementation",
			hasSrcTestDir ||
			Objects.equals(path.toString(), "modules/build.gradle")
		).put(
			"testIntegrationImplementation", hasSrcTestIntegrationDir
		).put(
			"testIntegrationRuntimeOnly", hasSrcTestIntegrationDir
		).put(
			"testRuntimeOnly", hasSrcTestDir
		).build();

		for (GradleDependency gradleDependency : gradleDependencies) {
			StringBundler sb = new StringBundler(9);

			sb.append("Artifact dependency {");
			sb.append(gradleDependency);
			sb.append("} in ");
			sb.append(path);
			sb.append(" not permitted on ");
			sb.append(_branchName);
			sb.append(" branch. Use ");

			String moduleGroup = gradleDependency.getModuleGroup();

			if (moduleGroup.equals("com.liferay.portal")) {
				sb.append("version \"default\"");
			}
			else {
				sb.append("a project dependency");
			}

			sb.append(" instead.");

			Assert.assertFalse(
				sb.toString(),
				_shouldBecomeProjectDependency(gradleDependency, dirPath));

			String configuration = gradleDependency.getConfiguration();

			Boolean allowed = allowedConfigurationsMap.get(configuration);

			if ((allowed != null) && !allowed.booleanValue()) {
				if (_isUnusedGradleConfiguration(
						configuration, hasSrcTestDir,
						hasSrcTestIntegrationDir)) {

					sb = new StringBundler(4);

					sb.append("Please delete the unused ");
					sb.append(configuration);
					sb.append(" dependencies in ");
					sb.append(path);
				}
				else {
					sb = new StringBundler(
						(allowedConfigurationsMap.size() * 4) + 4);

					sb.append("Incorrect configuration of dependency {");
					sb.append(gradleDependency);
					sb.append("} in ");
					sb.append(path);
					sb.append(", use one of these instead: ");

					boolean first = true;

					for (Map.Entry<String, Boolean> entry :
							allowedConfigurationsMap.entrySet()) {

						if (!entry.getValue()) {
							continue;
						}

						if (!first) {
							sb.append(StringPool.COMMA_AND_SPACE);
						}

						first = false;

						sb.append(CharPool.QUOTE);
						sb.append(entry.getKey());
						sb.append(CharPool.QUOTE);
					}
				}

				Assert.assertFalse(sb.toString(), !allowed);
			}

			Assert.assertEquals(
				"Redundant dependency detected in " + path,
				_getActiveGradleDependency(
					gradleDependencies, gradleDependency),
				gradleDependency);
		}
	}

	private void _testJSONVersion(Path dirPath) throws IOException {
		String projectVersion = null;

		try (InputStream inputStream = Files.newInputStream(
				dirPath.resolve("bnd.bnd"))) {

			Properties properties = new Properties();

			properties.load(inputStream);

			projectVersion = properties.getProperty("Bundle-Version");
		}

		for (String jsonFileName : _JSON_VERSION_FILE_NAMES) {
			Path jsonPath = dirPath.resolve(jsonFileName);

			if (Files.exists(jsonPath)) {
				Matcher matcher = _jsonVersionPattern.matcher(
					ModulesStructureTestUtil.read(jsonPath));

				if (matcher.find()) {
					String jsonVersion = matcher.group(2);

					Assert.assertTrue(
						StringBundler.concat(
							"Version must match the project version (",
							projectVersion, ") ", jsonPath),
						jsonVersion.equals(projectVersion));
				}
			}
		}
	}

	private void _testRelengAppProperties(Path dirPath) throws IOException {
		if (_branchName.contains("master")) {
			return;
		}

		Path relengIgnorePath = dirPath.resolve(".lfrbuild-releng-ignore");

		if (Files.exists(relengIgnorePath)) {
			return;
		}

		Path appBndPath = dirPath.resolve("app.bnd");

		if (!Files.exists(appBndPath)) {
			return;
		}

		try (InputStream inputStream = Files.newInputStream(appBndPath)) {
			Properties properties = new Properties();

			properties.load(inputStream);

			String liferayRelengBundle = properties.getProperty(
				"Liferay-Releng-Bundle");

			if (!GetterUtil.getBoolean(liferayRelengBundle)) {
				return;
			}

			StringBundler sb = new StringBundler(5);

			sb.append(".releng/");

			if (_isInDXPModulesDir(dirPath)) {
				sb.append("dxp/");
			}
			else if (_isInPrivateModulesDir(dirPath)) {
				sb.append("private/");
			}

			sb.append("apps/");
			sb.append(String.valueOf(dirPath.getFileName()));
			sb.append("/app.properties");

			Path appPropertiesPath = _modulesDirPath.resolve(sb.toString());

			Assert.assertTrue(
				"Missing " + appPropertiesPath,
				Files.exists(appPropertiesPath));
		}
	}

	private void _testThemeBuildScripts(Path dirPath) throws IOException {
		if (!ModulesStructureTestUtil.contains(
				dirPath.resolve("package.json"), "\"liferay-theme-tasks\":")) {

			return;
		}

		Path gulpfileJsPath = dirPath.resolve("gulpfile.js");

		Assert.assertTrue(
			"Missing " + gulpfileJsPath, Files.exists(gulpfileJsPath));
	}

	private void _testThemeIgnoreFiles(
			Path dirPath, String gitIgnoreTemplate, String npmIgnoreTemplate)
		throws IOException {

		Path resourcesImporterDirPath = dirPath.resolve("resources-importer");

		if (Files.exists(resourcesImporterDirPath)) {
			Path resourcesImporterIgnorePath = resourcesImporterDirPath.resolve(
				_SOURCE_FORMATTER_IGNORE_FILE_NAME);

			Assert.assertTrue(
				"Missing " + resourcesImporterIgnorePath,
				Files.exists(resourcesImporterIgnorePath));
		}

		Path gitIgnorePath = dirPath.resolve(".gitignore");

		String gitIgnore = ModulesStructureTestUtil.read(gitIgnorePath);

		Assert.assertEquals(
			"Incorrect " + gitIgnorePath, gitIgnoreTemplate, gitIgnore);

		Path npmIgnorePath = dirPath.resolve(".npmignore");

		String npmIgnore = ModulesStructureTestUtil.read(npmIgnorePath);

		Assert.assertEquals(
			"Incorrect " + npmIgnorePath, npmIgnoreTemplate, npmIgnore);
	}

	private static final String _APP_BUILD_GRADLE =
		"apply plugin: \"com.liferay.app.defaults.plugin\"";

	private static final String[] _GIT_IGNORE_LINE_PREFIXES = {"/wedeploy/"};

	private static final String[] _GIT_IGNORE_OPTIONAL_LINES = {
		".tsc/", "gradle-ext.properties", "node_modules_cache/"
	};

	private static final String _GIT_REPO_FILE_NAME = ".gitrepo";

	private static final String _GIT_REPO_GRADLE_PROJECT_GROUP_KEY =
		"project.group";

	private static final String[]
		_GIT_REPO_GRADLE_PROJECT_GROUP_RESERVED_PREFIXES = {
			"com.liferay.plugins", "com.liferay.portal"
		};

	private static final String _GIT_REPO_GRADLE_PROJECT_PATH_PREFIX_KEY =
		"project.path.prefix";

	private static final String _GIT_REPO_GRADLE_REPOSITORY_PRIVATE_PASSWORD =
		"systemProp.repository.private.password";

	private static final String _GIT_REPO_GRADLE_REPOSITORY_PRIVATE_URL =
		"systemProp.repository.private.url";

	private static final String _GIT_REPO_GRADLE_REPOSITORY_PRIVATE_USERNAME =
		"systemProp.repository.private.username";

	private static final String[] _JSON_VERSION_FILE_NAMES = {
		"npm-shrinkwrap.json", "package-lock.json", "package.json"
	};

	private static final String _REPOSITORY_URL =
		"https://repository-cdn.liferay.com/nexus/content/groups/public";

	private static final String _SOURCE_FORMATTER_IGNORE_FILE_NAME =
		"source_formatter.ignore";

	private static String _branchName;
	private static Properties _buildProperties;
	private static Set<String> _checkoutPrivateAppsDirs;
	private static final Set<String> _excludedDirNames = SetUtil.fromList(
		Arrays.asList(
			"_node-scripts", "bin", "build", "classes", "ext-test-impl",
			"node_modules", "test-classes", "tmp"));
	private static final Pattern _gitRepoGradleProjectGroupPattern =
		Pattern.compile("com\\.liferay(?:\\.[a-z]+)+");
	private static final Set<String> _gitRepoGradlePropertiesKeys =
		SetUtil.fromList(
			Arrays.asList(
				"jira.project.keys", "org.gradle.daemon", "org.gradle.jvmargs",
				"org.gradle.parallel", "pom.scm.connection",
				"pom.scm.developerConnection", "pom.scm.url"));
	private static final List<String> _gradleConfigurations = Arrays.asList(
		"api", "compileOnly", "provided", "runtimeOnly", "testImplementation",
		"testIntegrationImplementation", "testIntegrationRuntimeOnly",
		"testRuntimeOnly");
	private static final List<String> _includedDirNames = Arrays.asList(
		"apps", "core", "dxp", "test");
	private static final Pattern _jsonVersionPattern = Pattern.compile(
		"\\n(\\t|  )\"version\": \"(.+)\"");
	private static boolean _masterBranch;
	private static Path _modulesDirPath;

}