/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.task;

import aQute.bnd.osgi.Constants;

import com.liferay.gogo.shell.client.GogoShellClient;
import com.liferay.gradle.plugins.internal.util.FileUtil;
import com.liferay.gradle.plugins.internal.util.GradleUtil;
import com.liferay.gradle.util.ArrayUtil;
import com.liferay.gradle.util.GUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URI;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gradle.StartParameter;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileType;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.ChangeType;
import org.gradle.work.FileChange;
import org.gradle.work.InputChanges;

import org.osgi.framework.Bundle;
import org.osgi.framework.dto.BundleDTO;

/**
 * @author Gregory Amerson
 * @author Andrea Di Giorgi
 */
@CacheableTask
public class WatchTask extends DefaultTask {

	public WatchTask() {
		classLoaderFileExtensions(".class", ".jsp", ".jspf", ".properties");
		ignoredManifestKeys(Constants.BND_LASTMODIFIED);
	}

	public WatchTask classLoaderFileExtensions(
		Iterable<String> classLoaderFileExtensions) {

		GUtil.addToCollection(
			_classLoaderFileExtensions, classLoaderFileExtensions);

		return this;
	}

	public WatchTask classLoaderFileExtensions(
		String... classLoaderFileExtensions) {

		return classLoaderFileExtensions(
			Arrays.asList(classLoaderFileExtensions));
	}

	@InputDirectory
	@PathSensitive(PathSensitivity.RELATIVE)
	public File getBundleDir() {
		return GradleUtil.toFile(getProject(), _bundleDir);
	}

	@Input
	public String getBundleSymbolicName() {
		return GradleUtil.toString(_bundleSymbolicName);
	}

	@Input
	public Set<String> getClassLoaderFileExtensions() {
		return _classLoaderFileExtensions;
	}

	@InputFiles
	@Optional
	@PathSensitive(PathSensitivity.RELATIVE)
	public FileCollection getFragments() {
		return _fragmentsFileCollection;
	}

	@Input
	public Set<String> getIgnoredManifestKeys() {
		return _ignoredManifestKeys;
	}

	@OutputFile
	public File getOutputFile() {
		Project project = getProject();

		return new File(project.getBuildDir(), "installedBundleId");
	}

	public WatchTask ignoredManifestKeys(Iterable<String> ignoredManifestKeys) {
		GUtil.addToCollection(_ignoredManifestKeys, ignoredManifestKeys);

		return this;
	}

	public WatchTask ignoredManifestKeys(String... ignoredManifestKeys) {
		return ignoredManifestKeys(Arrays.asList(ignoredManifestKeys));
	}

	public void setBundleDir(Object bundleDir) {
		_bundleDir = bundleDir;
	}

	public void setBundleSymbolicName(Object bundleSymbolicName) {
		_bundleSymbolicName = bundleSymbolicName;
	}

	public void setClassLoaderFileExtensions(
		Iterable<String> classLoaderFileExtensions) {

		_classLoaderFileExtensions.clear();

		classLoaderFileExtensions(classLoaderFileExtensions);
	}

	public void setClassLoaderFileExtensions(
		String... classLoaderFileExtensions) {

		setClassLoaderFileExtensions(Arrays.asList(classLoaderFileExtensions));
	}

	public void setFragments(FileCollection fragmentsFileCollection) {
		_fragmentsFileCollection = fragmentsFileCollection;
	}

	public void setIgnoredManifestKeys(Iterable<String> ignoredManifestKeys) {
		_ignoredManifestKeys.clear();

		ignoredManifestKeys(ignoredManifestKeys);
	}

	public void setIgnoredManifestKeys(String... ignoredManifestKeys) {
		setIgnoredManifestKeys(Arrays.asList(ignoredManifestKeys));
	}

	@TaskAction
	public void watch(InputChanges inputChanges) throws IOException {
		Project project = getProject();

		Gradle gradle = project.getGradle();

		StartParameter startParameter = gradle.getStartParameter();

		if (!startParameter.isContinuous()) {
			throw new GradleException(
				"Task must be executed in continuous mode: gradle watch (-t " +
					"| --continuous)");
		}

		Logger logger = getLogger();

		long installedBundleId = -1;

		try (GogoShellClient gogoShellClient = new GogoShellClient()) {
			installedBundleId = _getInstalledBundleId(gogoShellClient);

			if ((installedBundleId < 1) || !inputChanges.isIncremental()) {
				_installOrUpdateBundle(installedBundleId, gogoShellClient);

				return;
			}

			List<File> modifiedFiles = _getModifiedFiles(inputChanges);

			if (_isManifestChanged(modifiedFiles)) {
				_installOrUpdateBundle(installedBundleId, gogoShellClient);

				return;
			}
			else if (_isClassLoaderFileChanged(modifiedFiles)) {
				_refreshBundle(installedBundleId, gogoShellClient);

				if (_isFragmentModule()) {
					_refreshFragmentHostBundle(gogoShellClient);
				}

				return;
			}

			if (logger.isQuietEnabled()) {
				if (modifiedFiles.isEmpty()) {
					logger.quiet("No files changed. Skipping bundle refresh.");
				}
				else {
					logger.quiet(
						"Only resources changed. Skipping bundle refresh.");
				}
			}
		}
	}

	private long _getBundleId(
			String bundleSymbolicName, GogoShellClient gogoShellClient)
		throws IOException {

		String command = String.format("lb -s %s", bundleSymbolicName);

		String response = _sendGogoShellCommand(gogoShellClient, command);

		try (Scanner scanner = new Scanner(response)) {
			List<String> lines = new ArrayList<>();

			while (scanner.hasNextLine()) {
				lines.add(scanner.nextLine());
			}

			if (lines.size() > 2) {
				String gogoLine = lines.get(2);

				BundleDTO bundleDTO = _parseBundleDTO(gogoLine);

				if (bundleDTO != null) {
					String symbolicName = bundleDTO.symbolicName;

					if (symbolicName.indexOf('(') > 0) {
						symbolicName = symbolicName.substring(
							0, symbolicName.indexOf("(") - 1);
					}

					if (bundleSymbolicName.equals(symbolicName)) {
						return bundleDTO.id;
					}
				}
			}
		}

		return -1;
	}

	private <K, V> Map<K, V> _getDifferences(
		Map<? extends K, ? extends V> leftMap,
		Map<? extends K, ? extends V> rightMap) {

		Map<K, V> differences = new HashMap<>();

		differences.putAll(leftMap);
		differences.putAll(rightMap);

		Set<Map.Entry<K, V>> entrySet = differences.entrySet();

		if (leftMap.size() <= rightMap.size()) {
			entrySet.removeAll(leftMap.entrySet());
		}
		else {
			entrySet.removeAll(rightMap.entrySet());
		}

		return differences;
	}

	private String _getFragmentHost() throws IOException {
		Project project = getProject();

		FileCollection fileCollection = project.files("bnd.bnd");

		if (fileCollection != null) {
			File file = fileCollection.getSingleFile();

			Properties properties = FileUtil.readProperties(file);

			return properties.getProperty(Constants.FRAGMENT_HOST);
		}

		return null;
	}

	private String _getFragmentHostName() throws IOException {
		String fragmentHost = _getFragmentHost();

		if (fragmentHost != null) {
			String[] fragmentNames = fragmentHost.split(";");

			if (ArrayUtil.isNotEmpty(fragmentNames)) {
				return fragmentNames[0];
			}
		}

		return null;
	}

	private long _getInstalledBundleId(GogoShellClient gogoShellClient)
		throws IOException {

		File outputFile = getOutputFile();

		if (outputFile.exists()) {
			try {
				String installedBundleID = new String(
					Files.readAllBytes(outputFile.toPath()));

				return Long.parseLong(installedBundleID);
			}
			catch (Exception exception) {
			}
		}

		String bundleSymbolicName = getBundleSymbolicName();

		if (bundleSymbolicName == null) {
			File manifestFile = new File(
				getBundleDir(), "META-INF/MANIFEST.MF");

			bundleSymbolicName = FileUtil.readManifestAttribute(
				manifestFile, Constants.BUNDLE_SYMBOLICNAME);
		}

		return _getBundleId(bundleSymbolicName, gogoShellClient);
	}

	private List<File> _getModifiedFiles(InputChanges inputChanges) {
		List<File> modifiedFiles = new ArrayList<>();

		Iterable<FileChange> fileChanges = inputChanges.getFileChanges(
			getFragments());

		for (FileChange fileChange : fileChanges) {
			FileType fileType = fileChange.getFileType();

			if (fileType == FileType.FILE) {
				ChangeType changeType = fileChange.getChangeType();

				if ((changeType == ChangeType.ADDED) ||
					(changeType == ChangeType.MODIFIED) ||
					(changeType == ChangeType.REMOVED)) {

					modifiedFiles.add(fileChange.getFile());
				}
			}
		}

		return modifiedFiles;
	}

	private String _getReferenceInstallURL(File file) {
		URI uri = file.toURI();

		if (_isWarDir(file)) {
			return String.format(
				"webbundledir:%s?Bundle-SymbolicName=%s&Web-ContextPath=/%s",
				uri.toASCIIString(), getBundleSymbolicName(),
				getBundleSymbolicName());
		}

		return "reference:" + uri.toASCIIString();
	}

	private final int _getState(String state) {
		String bundleState = state.toUpperCase();

		if (Objects.equals(bundleState, "ACTIVE")) {
			return Bundle.ACTIVE;
		}
		else if (Objects.equals(bundleState, "INSTALLED")) {
			return Bundle.INSTALLED;
		}
		else if (Objects.equals(bundleState, "RESOLVED")) {
			return Bundle.RESOLVED;
		}
		else if (Objects.equals(bundleState, "STARTING")) {
			return Bundle.STARTING;
		}
		else if (Objects.equals(bundleState, "STOPPING")) {
			return Bundle.STOPPING;
		}
		else if (Objects.equals(bundleState, "UNINSTALLED")) {
			return Bundle.UNINSTALLED;
		}

		return 0;
	}

	private long _installBundle(
			File file, GogoShellClient gogoShellClient, boolean start)
		throws IOException {

		long bundleId = -1;

		String url = _getReferenceInstallURL(file);

		String command = String.format("install '%s'", url);

		String response = _sendGogoShellCommand(gogoShellClient, command);

		Matcher matcher = _installResponsePattern.matcher(response);

		Logger logger = getLogger();

		if (matcher.matches()) {
			if (logger.isQuietEnabled()) {
				logger.quiet("Installed bundle at {}", file);
			}

			String bundleIdString = matcher.group(1);

			bundleId = Long.parseLong(bundleIdString);
		}

		if (start) {
			_startBundle(bundleId, gogoShellClient);
		}

		if (bundleId < 0) {
			logger.error("Unable to install bundle: {}", response);
		}

		return bundleId;
	}

	private void _installOrUpdateBundle(
			long bundleId, GogoShellClient gogoShellClient)
		throws IOException {

		File bundleDir = getBundleDir();

		if (bundleId > 0) {
			_updateBundle(bundleDir, bundleId, gogoShellClient);
		}
		else {
			bundleId = _installBundle(bundleDir, gogoShellClient, true);
		}

		File manifestFile = new File(bundleDir, "META-INF/MANIFEST.MF");

		try (InputStream inputStream = new FileInputStream(manifestFile)) {
			Manifest manifest = new Manifest(inputStream);

			Attributes attributes = manifest.getMainAttributes();

			_installedAttributes.put(bundleDir, attributes);
		}

		FileCollection fileCollection = getFragments();

		boolean installedFragment = false;

		if (fileCollection != null) {
			Set<File> files = fileCollection.getFiles();

			for (File file : files) {
				if (file.exists()) {
					long fragmentBundleId = _installBundle(
						file, gogoShellClient, false);

					if (fragmentBundleId > 0) {
						installedFragment = true;
					}
				}
			}

			if (_isFragmentModule()) {
				_refreshFragmentHostBundle(gogoShellClient);
			}
		}

		String bundleIdString = String.valueOf(bundleId);
		File outputFile = getOutputFile();

		Files.write(
			outputFile.toPath(),
			bundleIdString.getBytes(StandardCharsets.UTF_8));

		if (installedFragment) {
			_refreshBundle(bundleId, gogoShellClient);
		}
	}

	private boolean _isClassLoaderFileChanged(List<File> modifiedFiles) {
		for (File file : modifiedFiles) {
			if (_classLoaderFileExtensions.contains(
					FileUtil.getExtension(file))) {

				return true;
			}
		}

		return false;
	}

	private boolean _isFragmentModule() throws IOException {
		if (_getFragmentHost() != null) {
			return true;
		}

		return false;
	}

	private boolean _isManifestChanged(List<File> modifiedFiles)
		throws IOException {

		File manifestFile = null;

		for (File file : modifiedFiles) {
			String absolutePath = FileUtil.getAbsolutePath(file);

			if (absolutePath.endsWith("/META-INF/MANIFEST.MF")) {
				manifestFile = file;

				break;
			}
		}

		if (manifestFile == null) {
			return false;
		}

		Map<Object, Object> differences = null;

		try (InputStream inputStream = new FileInputStream(manifestFile)) {
			Manifest manifest = new Manifest(inputStream);

			Attributes attributes = manifest.getMainAttributes();

			differences = _getDifferences(
				attributes, _installedAttributes.get(getBundleDir()));
		}

		Set<Map.Entry<Object, Object>> entrySet = differences.entrySet();

		Iterator<Map.Entry<Object, Object>> iterator = entrySet.iterator();

		while (iterator.hasNext()) {
			Map.Entry<Object, Object> entry = iterator.next();

			String key = String.valueOf(entry.getKey());

			if (_ignoredManifestKeys.contains(key)) {
				iterator.remove();
			}
		}

		if (differences.isEmpty()) {
			return false;
		}

		Logger logger = getLogger();

		if (logger.isQuietEnabled()) {
			logger.quiet("Detected differences in manifest: {}", differences);
		}

		return true;
	}

	private boolean _isWarDir(File file) {
		if (!file.isDirectory()) {
			return false;
		}

		File webInfDir = new File(file, "WEB-INF");

		return webInfDir.exists();
	}

	private final BundleDTO _newBundleDTO(
		Long id, int state, String symbolicName) {

		BundleDTO bundle = new BundleDTO();

		bundle.id = id;
		bundle.state = state;
		bundle.symbolicName = symbolicName;

		return bundle;
	}

	private final BundleDTO _parseBundleDTO(String line) {
		String[] fields = line.split("\\|");

		Long id = Long.parseLong(fields[0].trim());

		int state = _getState(fields[1].trim());

		String symbolicName = fields[3];

		return _newBundleDTO(id, state, symbolicName);
	}

	private void _refreshBundle(long bundleId, GogoShellClient gogoShellClient)
		throws IOException {

		String command = String.format("refresh %s", bundleId);

		_sendGogoShellCommand(gogoShellClient, command);

		Logger logger = getLogger();

		if (logger.isQuietEnabled()) {
			logger.quiet("Refreshed bundle {}", bundleId);
		}
	}

	private void _refreshFragmentHostBundle(GogoShellClient gogoShellClient)
		throws IOException {

		long fragmentHostBundleId = _getBundleId(
			_getFragmentHostName(), gogoShellClient);

		if (fragmentHostBundleId > 0) {
			_refreshBundle(fragmentHostBundleId, gogoShellClient);
		}
	}

	private String _sendGogoShellCommand(
			GogoShellClient gogoShellClient, String command)
		throws IOException {

		String response = gogoShellClient.send(command);

		if (response.startsWith(command)) {
			response = response.substring(command.length());

			response = response.trim();
		}

		return response;
	}

	private void _startBundle(long bundleId, GogoShellClient gogoShellClient)
		throws IOException {

		String command = String.format("start %s", bundleId);

		String response = _sendGogoShellCommand(gogoShellClient, command);

		Logger logger = getLogger();

		if (logger.isQuietEnabled()) {
			logger.quiet("Bundle {} started. {}", bundleId, response);
		}
	}

	private void _updateBundle(
			File bundleDir, long bundleId, GogoShellClient gogoShellClient)
		throws IOException {

		Logger logger = getLogger();

		if (logger.isQuietEnabled()) {
			logger.quiet("Updating bundle {} from {}", bundleId, bundleDir);
		}

		String url = _getReferenceInstallURL(bundleDir);

		String command = String.format("update %s '%s'", bundleId, url);

		String response = _sendGogoShellCommand(gogoShellClient, command);

		if (logger.isQuietEnabled()) {
			logger.quiet("Bundle {} updated.\n{}\n", bundleId, response);
		}
	}

	private static final Pattern _installResponsePattern = Pattern.compile(
		".*Bundle ID: (.*$).*", Pattern.DOTALL | Pattern.MULTILINE);
	private static final Map<File, Attributes> _installedAttributes =
		new HashMap<>();

	private Object _bundleDir;
	private Object _bundleSymbolicName;
	private final Set<String> _classLoaderFileExtensions =
		new LinkedHashSet<>();
	private FileCollection _fragmentsFileCollection;
	private final Set<String> _ignoredManifestKeys = new LinkedHashSet<>();

}