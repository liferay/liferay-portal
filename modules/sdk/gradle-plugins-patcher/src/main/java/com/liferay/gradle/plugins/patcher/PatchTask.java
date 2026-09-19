/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.patcher;

import com.liferay.gradle.util.FileUtil;
import com.liferay.gradle.util.GUtil;
import com.liferay.gradle.util.GradleUtil;
import com.liferay.gradle.util.OSDetector;
import com.liferay.gradle.util.Validator;
import com.liferay.gradle.util.copy.ReplaceLeadingPathAction;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.tools.ant.filters.FixCrLfFilter;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.UncheckedIOException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.ResolvableDependencies;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.gradle.api.artifacts.ResolvedModuleVersion;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;

/**
 * @author Andrea Di Giorgi
 */
@CacheableTask
public class PatchTask extends DefaultTask {

	public static final String PATCHED_SRC_DIR_MAPPING_DEFAULT_EXTENSION = "*";

	public PatchTask() {
		_originalLibConfigurationName = new Callable<String>() {

			@Override
			public String call() throws Exception {
				Project project = getProject();

				ConfigurationContainer configurationContainer =
					project.getConfigurations();

				Configuration configuration = configurationContainer.findByName(
					"compileClasspath");

				if (configuration != null) {
					return configuration.getName();
				}

				return JavaPlugin.API_CONFIGURATION_NAME;
			}

		};

		_originalLibFile = new Callable<File>() {

			@Override
			public File call() throws Exception {
				return getOriginalLibModuleFile();
			}

		};

		_originalLibSrcFile = new Callable<File>() {

			@Override
			public File call() throws Exception {
				return FileUtil.get(getProject(), getOriginalLibSrcUrl());
			}

		};

		args("--no-backup-if-mismatch", "--strip=1");
	}

	public PatchTask args(Iterable<Object> args) {
		GUtil.addToCollection(_args, args);

		return this;
	}

	public PatchTask args(Object... args) {
		return args(Arrays.asList(args));
	}

	public PatchTask fileNames(Iterable<Object> fileNames) {
		GUtil.addToCollection(_fileNames, fileNames);

		return this;
	}

	public PatchTask fileNames(Object... fileNames) {
		return fileNames(Arrays.asList(fileNames));
	}

	@Input
	@Optional
	public List<String> getArgs() {
		return GradleUtil.toStringList(_args);
	}

	@Input
	public List<String> getFileNames() {
		return GradleUtil.toStringList(_fileNames);
	}

	@Input
	public String getOriginalLibConfigurationName() {
		return GradleUtil.toString(_originalLibConfigurationName);
	}

	@InputFile
	@PathSensitive(PathSensitivity.RELATIVE)
	public File getOriginalLibFile() {
		return GradleUtil.toFile(getProject(), _originalLibFile);
	}

	@Input
	public String getOriginalLibModuleGroup() {
		Dependency dependency = getOriginalLibDependency();

		return dependency.getGroup();
	}

	@Input
	public String getOriginalLibModuleName() {
		return GradleUtil.toString(_originalLibModuleName);
	}

	@Input
	public String getOriginalLibModuleVersion() {
		Dependency dependency = getOriginalLibDependency();

		return dependency.getVersion();
	}

	@Input
	@Optional
	public String getOriginalLibSrcBaseUrl() {
		return GradleUtil.toString(_originalLibSrcBaseUrl);
	}

	@Input
	public String getOriginalLibSrcDirName() {
		return GradleUtil.toString(_originalLibSrcDirName);
	}

	@InputFile
	@PathSensitive(PathSensitivity.RELATIVE)
	public File getOriginalLibSrcFile() {
		return GradleUtil.toFile(getProject(), _originalLibSrcFile);
	}

	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	@SkipWhenEmpty
	public FileCollection getPatchFiles() {
		Project project = getProject();

		if (!_patchFiles.isEmpty()) {
			return project.files(_patchFiles);
		}

		return project.fileTree(_patchesDir);
	}

	@Input
	@Optional
	public Map<String, File> getPatchedSrcDirMappings() {
		Map<String, File> patchedSrcDirMappings = new HashMap<>();

		for (Map.Entry<String, Object> entry :
				_patchedSrcDirMappings.entrySet()) {

			String extension = entry.getKey();
			File dir = GradleUtil.toFile(getProject(), entry.getValue());

			patchedSrcDirMappings.put(extension, dir);
		}

		return patchedSrcDirMappings;
	}

	@OutputFiles
	public FileCollection getPatchedSrcFiles() {
		Project project = getProject();

		Map<File, ConfigurableFileTree> patchedSrcFileTreeMap = new HashMap<>();

		for (String fileName : getFileNames()) {
			File patchedDir = getPatchedSrcDir(fileName);

			ConfigurableFileTree configurableFileTree =
				patchedSrcFileTreeMap.get(patchedDir);

			if (configurableFileTree == null) {
				configurableFileTree = project.fileTree(patchedDir);

				patchedSrcFileTreeMap.put(patchedDir, configurableFileTree);
			}

			configurableFileTree.include(fileName);
		}

		Collection<ConfigurableFileTree> patchedSrcFileTrees =
			patchedSrcFileTreeMap.values();

		return project.files(patchedSrcFileTrees.toArray());
	}

	@InputDirectory
	@PathSensitive(PathSensitivity.RELATIVE)
	public File getPatchesDir() {
		return GradleUtil.toFile(getProject(), _patchesDir);
	}

	@Input
	public boolean isCopyOriginalLibClasses() {
		return _copyOriginalLibClasses;
	}

	@TaskAction
	public void patch() throws Exception {
		Project project = getProject();

		File patchesTemporaryDir = fixPatchFiles();
		final File srcTemporaryDir = fixSrcFiles();

		for (final File patchFile : getSortedFiles(patchesTemporaryDir)) {
			final ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream();

			ExecResult execResult = project.exec(
				new Action<ExecSpec>() {

					@Override
					public void execute(ExecSpec execSpec) {
						execSpec.args(getArgs());

						if (OSDetector.isWindows()) {
							execSpec.args("--binary");
						}

						String relativizePath = FileUtil.relativize(
							patchFile, srcTemporaryDir);

						execSpec.args("--input=" + relativizePath);

						execSpec.setExecutable("patch");
						execSpec.setIgnoreExitValue(true);
						execSpec.setStandardOutput(byteArrayOutputStream);
						execSpec.setWorkingDir(srcTemporaryDir);
					}

				});

			System.out.println(byteArrayOutputStream.toString());

			execResult.rethrowFailure();

			execResult.assertNormalExitValue();
		}

		FileTree fileTree = project.fileTree(srcTemporaryDir);

		for (File file : fileTree) {
			File patchedSrcDir = getPatchedSrcDir(file.getName());

			if (patchedSrcDir == null) {
				continue;
			}

			Path patchedSrcDirPath = patchedSrcDir.toPath();

			String relativePath = FileUtil.relativize(file, srcTemporaryDir);

			patchedSrcDirPath = patchedSrcDirPath.resolve(relativePath);

			Files.createDirectories(patchedSrcDirPath.getParent());

			Files.move(
				file.toPath(), patchedSrcDirPath,
				StandardCopyOption.REPLACE_EXISTING);
		}
	}

	public PatchTask patchFiles(Iterable<Object> patchFiles) {
		GUtil.addToCollection(_patchFiles, patchFiles);

		return this;
	}

	public PatchTask patchFiles(Object... patchFiles) {
		return patchFiles(Arrays.asList(patchFiles));
	}

	public PatchTask patchedSrcDirMapping(String extension, Object dir) {
		_patchedSrcDirMappings.put(extension, dir);

		return this;
	}

	public void setArgs(Iterable<Object> args) {
		_args.clear();

		args(args);
	}

	public void setArgs(Object... args) {
		setArgs(Arrays.asList(args));
	}

	public void setCopyOriginalLibClasses(boolean copyOriginalLibClasses) {
		_copyOriginalLibClasses = copyOriginalLibClasses;
	}

	public void setFileNames(Iterable<Object> fileNames) {
		_fileNames.clear();

		fileNames(fileNames);
	}

	public void setOriginalLibConfigurationName(
		Object originalLibConfigurationName) {

		_originalLibConfigurationName = originalLibConfigurationName;
	}

	public void setOriginalLibFile(Object originalLibFile) {
		_originalLibFile = originalLibFile;
	}

	public void setOriginalLibModuleName(Object originalLibModuleName) {
		_originalLibModuleName = originalLibModuleName;
	}

	public void setOriginalLibSrcBaseUrl(Object originalLibSrcBaseUrl) {
		_originalLibSrcBaseUrl = originalLibSrcBaseUrl;
	}

	public void setOriginalLibSrcDirName(Object originalLibSrcDirName) {
		_originalLibSrcDirName = originalLibSrcDirName;
	}

	public void setOriginalLibSrcFile(Object originalLibSrcFile) {
		_originalLibSrcFile = originalLibSrcFile;
	}

	public void setPatchFiles(Iterable<Object> patchFiles) {
		_patchFiles.clear();

		patchFiles(patchFiles);
	}

	public void setPatchedSrcDirMappings(
		Map<String, Object> patchedSrcDirMappings) {

		_patchedSrcDirMappings.clear();

		_patchedSrcDirMappings.putAll(patchedSrcDirMappings);
	}

	public void setPatchesDir(Object patchesDir) {
		_patchesDir = patchesDir;
	}

	protected File fixPatchFiles() {
		Project project = getProject();

		final File temporaryDir = new File(getTemporaryDir(), "patches");

		project.delete(temporaryDir);

		project.copy(
			new Action<CopySpec>() {

				@Override
				public void execute(CopySpec copySpec) {
					copySpec.filter(_fixCrLfArgs, FixCrLfFilter.class);
					copySpec.from(getPatchFiles());
					copySpec.into(temporaryDir);
					copySpec.setIncludeEmptyDirs(false);
				}

			});

		return temporaryDir;
	}

	protected File fixSrcFiles() {
		final Project project = getProject();

		final File temporaryDir = new File(getTemporaryDir(), "src");

		Map<String, Object> args = new HashMap<>();

		args.put("dir", temporaryDir);
		args.put("excludes", getFileNames());

		project.delete(project.fileTree(args));

		project.copy(
			new Action<CopySpec>() {

				@Override
				public void execute(CopySpec copySpec) {
					String originalLibSrcDirName = getOriginalLibSrcDirName();

					if (!originalLibSrcDirName.equals(".")) {
						Map<Object, Object> leadingPathReplacementsMap =
							new HashMap<>();

						leadingPathReplacementsMap.put(
							originalLibSrcDirName, "");

						copySpec.eachFile(
							new ReplaceLeadingPathAction(
								leadingPathReplacementsMap));
					}

					copySpec.filter(_fixCrLfArgs, FixCrLfFilter.class);
					copySpec.from(project.zipTree(getOriginalLibSrcFile()));
					copySpec.include(getFileNames());
					copySpec.into(temporaryDir);
					copySpec.setIncludeEmptyDirs(false);
				}

			});

		if (!temporaryDir.exists()) {
			try {
				Files.createDirectories(temporaryDir.toPath());
			}
			catch (IOException ioException) {
				throw new UncheckedIOException(ioException);
			}
		}

		return temporaryDir;
	}

	@Internal
	protected Dependency getOriginalLibDependency() {
		Configuration configuration = GradleUtil.getConfiguration(
			getProject(), getOriginalLibConfigurationName());

		ResolvableDependencies resolvableDependencies =
			configuration.getIncoming();

		String moduleName = getOriginalLibModuleName();

		for (Dependency dependency : resolvableDependencies.getDependencies()) {
			if (moduleName.equals(dependency.getName())) {
				return dependency;
			}
		}

		throw new GradleException("Unable to find original lib " + moduleName);
	}

	@InputFile
	@Optional
	@PathSensitive(PathSensitivity.RELATIVE)
	protected File getOriginalLibModuleFile() {
		String configurationName = getOriginalLibConfigurationName();
		String moduleGroup = getOriginalLibModuleGroup();
		String moduleName = getOriginalLibModuleName();
		String moduleVersion = getOriginalLibModuleVersion();

		if (Validator.isNull(configurationName) ||
			Validator.isNull(moduleGroup) || Validator.isNull(moduleName) ||
			Validator.isNull(moduleVersion)) {

			return null;
		}

		Configuration configuration = GradleUtil.getConfiguration(
			getProject(), configurationName);

		ResolvedConfiguration resolvedConfiguration =
			configuration.getResolvedConfiguration();

		for (ResolvedArtifact resolvedArtifact :
				resolvedConfiguration.getResolvedArtifacts()) {

			ResolvedModuleVersion resolvedModuleVersion =
				resolvedArtifact.getModuleVersion();

			ModuleVersionIdentifier moduleVersionIdentifier =
				resolvedModuleVersion.getId();

			if (moduleGroup.equals(moduleVersionIdentifier.getGroup()) &&
				moduleName.equals(moduleVersionIdentifier.getName()) &&
				moduleVersion.equals(moduleVersionIdentifier.getVersion())) {

				return resolvedArtifact.getFile();
			}
		}

		return null;
	}

	@Input
	protected String getOriginalLibSrcUrl() {
		StringBuilder sb = new StringBuilder();

		String baseUrl = getOriginalLibSrcBaseUrl();

		if (Validator.isNotNull(baseUrl)) {
			sb.append(baseUrl);

			if (baseUrl.charAt(baseUrl.length() - 1) != '/') {
				sb.append('/');
			}
		}
		else {
			sb.append(_BASE_URL);

			String moduleGroup = getOriginalLibModuleGroup();

			sb.append(moduleGroup.replace('.', '/'));

			sb.append('/');
		}

		sb.append(getOriginalLibModuleName());
		sb.append('/');
		sb.append(getOriginalLibModuleVersion());
		sb.append('/');
		sb.append(getOriginalLibModuleName());
		sb.append('-');
		sb.append(getOriginalLibModuleVersion());
		sb.append("-sources.jar");

		return sb.toString();
	}

	protected File getPatchedSrcDir(String fileName) {
		String extension = PATCHED_SRC_DIR_MAPPING_DEFAULT_EXTENSION;

		int pos = fileName.indexOf('.');

		if (pos != -1) {
			extension = fileName.substring(pos + 1);
		}

		Object patchedSrcDir = _patchedSrcDirMappings.get(extension);

		if (patchedSrcDir == null) {
			patchedSrcDir = _patchedSrcDirMappings.get(
				PATCHED_SRC_DIR_MAPPING_DEFAULT_EXTENSION);
		}

		return GradleUtil.toFile(getProject(), patchedSrcDir);
	}

	protected List<File> getSortedFiles(File dir) {
		List<File> sortedFiles = new ArrayList<>();

		Project project = getProject();

		FileTree fileTree = project.fileTree(dir);

		GUtil.addToCollection(sortedFiles, fileTree);

		Collections.sort(sortedFiles);

		return sortedFiles;
	}

	private static final String _BASE_URL =
		"https://repo.maven.apache.org/maven2/";

	private static final Map<String, Object> _fixCrLfArgs =
		new HashMap<String, Object>() {
			{
				put("eof", FixCrLfFilter.AddAsisRemove.newInstance("remove"));
				put("eol", FixCrLfFilter.CrLf.newInstance("lf"));
				put("fixlast", false);
			}
		};

	private final List<Object> _args = new ArrayList<>();
	private boolean _copyOriginalLibClasses = true;
	private final List<Object> _fileNames = new ArrayList<>();
	private Object _originalLibConfigurationName;
	private Object _originalLibFile;
	private Object _originalLibModuleName;
	private Object _originalLibSrcBaseUrl;
	private Object _originalLibSrcDirName = ".";
	private Object _originalLibSrcFile;
	private final List<Object> _patchFiles = new ArrayList<>();
	private final Map<String, Object> _patchedSrcDirMappings = new HashMap<>();
	private Object _patchesDir = "patches";

}