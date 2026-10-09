/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.defaults.internal.util;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.LinkedList;
import java.util.Queue;

import org.gradle.BuildAdapter;
import org.gradle.BuildResult;
import org.gradle.api.UncheckedIOException;

/**
 * @author Andrea Di Giorgi
 */
public class BackupFilesBuildAdapter extends BuildAdapter {

	public void backUp(Path path) {
		try {
			Path backupPath = _getBackupPath(path);

			Files.copy(path, backupPath, StandardCopyOption.REPLACE_EXISTING);

			_paths.add(path);
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}
	}

	@Override
	public void buildFinished(BuildResult buildResult) {
		try {
			while (!_paths.isEmpty()) {
				Path path = _paths.remove();

				Path backupPath = _getBackupPath(path);

				if (!Files.exists(backupPath)) {
					continue;
				}

				Files.move(
					backupPath, path, StandardCopyOption.REPLACE_EXISTING);
			}
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}
	}

	private Path _getBackupPath(Path path) {
		return Paths.get(path.toString() + ".backup");
	}

	private final Queue<Path> _paths = new LinkedList<>();

}