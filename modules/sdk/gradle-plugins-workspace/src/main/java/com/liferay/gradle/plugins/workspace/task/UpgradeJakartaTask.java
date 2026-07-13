/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.task;

import com.liferay.gradle.plugins.source.formatter.FormatSourceTask;
import com.liferay.gradle.plugins.workspace.WorkspaceExtension;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;
import com.liferay.gradle.plugins.workspace.internal.util.StringUtil;
import com.liferay.release.util.ReleaseEntry;
import com.liferay.release.util.ReleaseUtil;

import java.io.File;
import java.io.InputStream;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.logging.Logger;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;

/**
 * @author Kyle Miho
 */
public class UpgradeJakartaTask extends FormatSourceTask {

	public UpgradeJakartaTask() {
		Project project = getProject();

		ObjectFactory objectFactory = project.getObjects();

		_jakartaTransformDependenciesFileProperty =
			objectFactory.fileProperty();

		_workspaceExtension = GradleUtil.getExtension(
			(ExtensionAware)project.getGradle(), WorkspaceExtension.class);
	}

	@Override
	public void exec() {
		ReleaseEntry releaseEntry = _getReleaseEntry();

		String productVersion = _workspaceExtension.getProduct();

		if (StringUtil.isBlank(productVersion)) {
			productVersion = _workspaceExtension.getTargetPlatformVersion();
		}

		if (releaseEntry != null) {
			List<String> tags = releaseEntry.getTags();

			if (!tags.contains("jakarta")) {
				throw new GradleException(
					StringUtil.concat(
						"Target liferay version ", productVersion,
						" is not compatible with Jakarta dependencies"));
			}
		}

		try {
			File file = _getJakartaTransformDependenciesFile();

			if ((file != null) && file.exists()) {
				addSourceFormatterProperty(
					"jakarta.transform.dependencies.file.path",
					StringUtil.normalizeFilePath(file.getAbsolutePath()));
			}
		}
		catch (Exception exception) {
			Logger logger = getLogger();

			if (logger.isWarnEnabled()) {
				logger.warn(
					"Unable to find Jakarta dependencies file for product " +
						"version '{}'",
					productVersion);
			}
		}

		super.exec();
	}

	@InputFile
	@Optional
	public RegularFileProperty getJakartaTransformDependenciesFile() {
		return _jakartaTransformDependenciesFileProperty;
	}

	private File _downloadJakartaTransformDependenciesFile() throws Exception {
		ReleaseEntry releaseEntry = _getReleaseEntry();

		if (releaseEntry == null) {
			throw new GradleException(
				"Unable to find a valid product version or target platform " +
					"version");
		}

		Project project = getProject();

		ProjectLayout projectLayout = project.getLayout();

		DirectoryProperty buildDirectory = projectLayout.getBuildDirectory();

		Provider<RegularFile> regularFileProvider = buildDirectory.file(
			"upgradeJakarta/" + _JAKARTA_TRANSFORM_DEPENDENCIES_FILE_NAME);

		RegularFile regularFile = regularFileProvider.get();

		File file = regularFile.getAsFile();

		Stream<ReleaseEntry> releaseEntryStream =
			ReleaseUtil.getReleaseEntryStream();

		List<ReleaseEntry> releaseEntries = new ArrayList<>(
			releaseEntryStream.filter(
				releaseEntry1 -> Objects.equals(
					releaseEntry.getProductGroupVersion(),
					releaseEntry1.getProductGroupVersion())
			).toList());

		ReleaseEntry lastReleaseEntry = releaseEntries.get(
			releaseEntries.size() - 1);

		URL url = new URL(
			lastReleaseEntry.getURL() + "/" +
				_JAKARTA_TRANSFORM_DEPENDENCIES_FILE_NAME);

		try (InputStream inputStream = url.openStream()) {
			File parentDirectory = file.getParentFile();

			Files.createDirectories(parentDirectory.toPath());

			Files.copy(
				inputStream, file.toPath(),
				StandardCopyOption.REPLACE_EXISTING);
		}

		return file;
	}

	private File _getJakartaTransformDependenciesFile() throws Exception {
		RegularFile regularFile =
			_jakartaTransformDependenciesFileProperty.getOrNull();

		if (regularFile != null) {
			return regularFile.getAsFile();
		}

		return _downloadJakartaTransformDependenciesFile();
	}

	private ReleaseEntry _getReleaseEntry() {
		for (ReleaseEntry releaseEntry : ReleaseUtil.getReleaseEntries()) {
			if (Objects.equals(
					_workspaceExtension.getProduct(),
					releaseEntry.getReleaseKey()) ||
				Objects.equals(
					_workspaceExtension.getTargetPlatformVersion(),
					releaseEntry.getTargetPlatformVersion())) {

				return releaseEntry;
			}
		}

		return null;
	}

	private static final String _JAKARTA_TRANSFORM_DEPENDENCIES_FILE_NAME =
		"jakarta-transform-dependencies.txt";

	private final RegularFileProperty _jakartaTransformDependenciesFileProperty;
	private final WorkspaceExtension _workspaceExtension;

}