/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.cache;

import com.liferay.gradle.plugins.cache.util.FileUtil;
import com.liferay.gradle.util.GradleUtil;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.gradle.api.UncheckedIOException;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;

/**
 * @author Andrea Di Giorgi
 */
@CacheableTask
public class WriteDigestTask extends SourceTask {

	@Input
	public String getDigest() {
		return FileUtil.getDigest(
			getProject(), getSource(), isExcludeIgnoredFiles());
	}

	@OutputFile
	public File getDigestFile() {
		return GradleUtil.toFile(getProject(), _digestFile);
	}

	@Input
	@Optional
	public String getOldDigest() {
		try {
			File digestFile = getDigestFile();

			if (!digestFile.exists()) {
				return null;
			}

			return new String(
				Files.readAllBytes(digestFile.toPath()),
				StandardCharsets.UTF_8);
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}
	}

	@Input
	public boolean isExcludeIgnoredFiles() {
		return _excludeIgnoredFiles;
	}

	public void setDigestFile(Object digestFile) {
		_digestFile = digestFile;
	}

	public void setExcludeIgnoredFiles(boolean excludeIgnoredFiles) {
		_excludeIgnoredFiles = excludeIgnoredFiles;
	}

	@TaskAction
	public void writeDigest() {
		try {
			Logger logger = getLogger();

			String digest = getDigest();
			File digestFile = getDigestFile();

			Files.write(
				digestFile.toPath(), digest.getBytes(StandardCharsets.UTF_8));

			if (logger.isInfoEnabled()) {
				logger.info("Updated {} to {}", digestFile, digest);
			}
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}
	}

	private Object _digestFile = ".digest";
	private boolean _excludeIgnoredFiles = true;

}