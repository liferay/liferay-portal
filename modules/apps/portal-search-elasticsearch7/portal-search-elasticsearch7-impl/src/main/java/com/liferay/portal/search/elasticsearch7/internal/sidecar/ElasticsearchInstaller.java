/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.SystemProperties;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
public class ElasticsearchInstaller {

	public static Builder builder() {
		return new Builder();
	}

	public ElasticsearchInstaller() {
	}

	public ElasticsearchInstaller(
		ElasticsearchInstaller elasticsearchInstaller) {

		_distributablesDirectoryPath =
			elasticsearchInstaller._distributablesDirectoryPath;
		_distribution = elasticsearchInstaller._distribution;
		_installationDirectoryPath =
			elasticsearchInstaller._installationDirectoryPath;
	}

	public void install() {
		if (_isAlreadyInstalled()) {
			return;
		}

		_createDestinationDirectory();

		try {
			_createTemporaryDownloadDirectory();

			try {
				_installElasticsearch();

				_installPlugins();
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
			finally {
				_deleteTemporaryDownloadDirectory();
			}
		}
		catch (RuntimeException runtimeException) {
			_deleteDestinationDirectory();

			throw runtimeException;
		}
	}

	public static class Builder {

		public ElasticsearchInstaller build() {
			return new ElasticsearchInstaller(_elasticsearchInstaller);
		}

		public Builder distributablesDirectoryPath(
			Path distributablesDirectoryPath) {

			_elasticsearchInstaller._distributablesDirectoryPath =
				distributablesDirectoryPath;

			return this;
		}

		public Builder distribution(Distribution distribution) {
			_elasticsearchInstaller._distribution = distribution;

			return this;
		}

		public Builder installationDirectoryPath(
			Path installationDirectoryPath) {

			_elasticsearchInstaller._installationDirectoryPath =
				installationDirectoryPath;

			return this;
		}

		private final ElasticsearchInstaller _elasticsearchInstaller =
			new ElasticsearchInstaller();

	}

	protected static String getChecksum(Path path) throws IOException {
		try (InputStream inputStream = Files.newInputStream(path)) {
			return DigestUtils.sha512Hex(inputStream);
		}
	}

	protected void createDirectories(Path directoryPath) {
		try {
			Files.createDirectories(directoryPath);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static Path _getTemporaryDirectoryPath() {
		Path path = Paths.get(SystemProperties.get(SystemProperties.TMP_DIR));

		return path.resolve(ElasticsearchInstaller.class.getSimpleName());
	}

	private void _createDestinationDirectory() {
		createDirectories(_installationDirectoryPath);
	}

	private void _createTemporaryDownloadDirectory() {
		createDirectories(_temporaryDirectoryPath);
	}

	private void _deleteDestinationDirectory() {
		PathUtil.deleteDir(_installationDirectoryPath);
	}

	private void _deleteTemporaryDownloadDirectory() {
		PathUtil.deleteDir(_temporaryDirectoryPath);
	}

	private void _installElasticsearch() throws IOException {
		String rootArchiveName = UncompressUtil.unarchive(
			_resolveOrDownload(_distribution.getElasticsearchDistributable()),
			_temporaryDirectoryPath);

		PathUtil.copyDirectory(
			_temporaryDirectoryPath.resolve(rootArchiveName),
			_installationDirectoryPath);

		PathUtil.deleteDir(_installationDirectoryPath.resolve("jdk"));
	}

	private void _installPlugin(Distributable distributable)
		throws IOException {

		Path filePath = _resolveOrDownload(distributable);

		String pluginName = StringUtils.substringBeforeLast(
			String.valueOf(filePath.getFileName()), StringPool.DASH);

		Path extractedDirectoryPath = _temporaryDirectoryPath.resolve(
			pluginName);

		UncompressUtil.unzip(filePath, extractedDirectoryPath);

		Path pluginsDirectoryPath = _installationDirectoryPath.resolve(
			"plugins");

		createDirectories(pluginsDirectoryPath);

		Path pluginDestinationDirectoryPath = pluginsDirectoryPath.resolve(
			pluginName);

		PathUtil.copyDirectory(
			extractedDirectoryPath, pluginDestinationDirectoryPath);
	}

	private void _installPlugins() throws IOException {
		for (Distributable distributable :
				_distribution.getPluginDistributables()) {

			_installPlugin(distributable);
		}
	}

	private boolean _isAlreadyInstalled() {
		return Files.exists(_installationDirectoryPath);
	}

	private Path _resolveOrDownload(Distributable distributable)
		throws IOException {

		String downloadURLString = distributable.getDownloadURLString();

		String fileName = StringUtils.substringAfterLast(
			downloadURLString, StringPool.FORWARD_SLASH);

		Path distributableFilePath = _distributablesDirectoryPath.resolve(
			fileName);

		if (Files.exists(distributableFilePath)) {
			_validateChecksum(
				getChecksum(distributableFilePath), distributable.getChecksum(),
				fileName);

			return distributableFilePath;
		}

		Path downloadedFilePath = _temporaryDirectoryPath.resolve(fileName);

		PathUtil.download(new URL(downloadURLString), downloadedFilePath);

		_validateChecksum(
			getChecksum(downloadedFilePath), distributable.getChecksum(),
			fileName);

		return downloadedFilePath;
	}

	private void _validateChecksum(
			String checksum, String distributableChecksum, String fileName)
		throws IOException {

		if (!checksum.equals(distributableChecksum)) {
			throw new RuntimeException(
				StringBundler.concat(
					"Checksum mismatch for ", fileName, StringPool.COLON,
					StringPool.SPACE, checksum));
		}
	}

	private static final Path _temporaryDirectoryPath =
		_getTemporaryDirectoryPath();

	private Path _distributablesDirectoryPath;
	private Distribution _distribution;
	private Path _installationDirectoryPath;

}