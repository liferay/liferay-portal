/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify;

import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.IOException;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author István András Dézsi
 */
public class PreupgradeVerifyStoreFileSystemStructure
	extends PreupgradeVerifyProcess {

	@Override
	protected void doVerify() throws Exception {
		if (PropsValues.UPGRADE_DATABASE_DL_STORAGE_CHECK_DISABLED ||
			StartupHelperUtil.isDBNew()) {

			return;
		}

		boolean advancedFileSystemStore = StringUtil.equals(
			PropsValues.DL_STORE_IMPL,
			"com.liferay.portal.store.file.system.AdvancedFileSystemStore");
		boolean fileSystemStore = StringUtil.equals(
			PropsValues.DL_STORE_IMPL,
			"com.liferay.portal.store.file.system.FileSystemStore");

		if (!advancedFileSystemStore && !fileSystemStore) {
			return;
		}

		Set<Long> companyIds = SetUtil.fromArray(
			PortalInstancePool.getCompanyIds());

		Path rootDirPath = _getRootDirPath();

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				rootDirPath)) {

			for (Path companyIdPath : directoryStream) {
				String fileName = String.valueOf(companyIdPath.getFileName());

				long companyId = GetterUtil.getLong(fileName);

				if (!companyIds.remove(companyId)) {
					continue;
				}

				if (!Files.isDirectory(companyIdPath)) {
					throw new VerifyException(
						companyIdPath + " is not a directory");
				}

				if (advancedFileSystemStore &&
					_hasAdvancedFileSystemStructureCompanyIdPath(
						companyIdPath)) {

					continue;
				}

				if (fileSystemStore &&
					_hasFileSystemStructureCompanyIdPath(companyIdPath)) {

					continue;
				}

				throw new VerifyException(
					StringBundler.concat(
						advancedFileSystemStore ? "Advanced file" : "File",
						" system store directory structure ",
						rootDirPath.toString(), " is invalid"));
			}
		}
		catch (IOException ioException) {
			throw new VerifyException(
				StringBundler.concat(
					"Unable to verify ",
					advancedFileSystemStore ? "advanced" : "",
					" system store directory structure ",
					rootDirPath.toString()),
				ioException);
		}

		if (!companyIds.isEmpty()) {
			throw new VerifyException(
				StringBundler.concat(
					"Missing directories in ", rootDirPath.toString(),
					" for companies: ", companyIds.toString()));
		}
	}

	@Override
	protected boolean isSkipDBPartitions() {
		return true;
	}

	private Path _getRootDirPath() throws Exception {
		File rootDir = null;

		try {
			BundleContext bundleContext = SystemBundleUtil.getBundleContext();

			for (ServiceReference<Store> serviceReference :
					bundleContext.getServiceReferences(
						Store.class,
						"(store.type=" + PropsValues.DL_STORE_IMPL + ")")) {

				rootDir = (File)serviceReference.getProperty("rootDir");

				break;
			}
		}
		catch (Exception exception) {
			throw new VerifyException(
				"Unable to get root directory", exception);
		}

		if ((rootDir == null) || !rootDir.exists()) {
			throw new VerifyException(
				"Root directory does not exist: " + rootDir);
		}

		return rootDir.toPath();
	}

	private boolean _hasAdvancedFileSystemStructureCompanyIdPath(
			Path companyIdPath)
		throws IOException {

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				companyIdPath)) {

			for (Path repositoryIdPath : directoryStream) {
				if (_isExcludedPath(repositoryIdPath) ||
					_isSystemCompanyRepositoryIdPath(repositoryIdPath)) {

					continue;
				}

				if (!Files.isDirectory(repositoryIdPath)) {
					_log.error(
						"Unexpected file " + repositoryIdPath +
							" in advanced file system structure");

					return false;
				}

				if (!_hasAdvancedFileSystemStructureRepositoryIdPath(
						repositoryIdPath)) {

					return false;
				}
			}

			return true;
		}
	}

	private boolean _hasAdvancedFileSystemStructureRepositoryIdPath(
			Path repositoryIdPath)
		throws IOException {

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				repositoryIdPath)) {

			for (Path fileNamePath : directoryStream) {
				if (_isExcludedPath(fileNamePath)) {
					continue;
				}

				if (!Files.isDirectory(fileNamePath)) {
					_log.error(
						"Unexpected file " + fileNamePath +
							" in advanced file system structure");

					return false;
				}

				String fileName = String.valueOf(fileNamePath.getFileName());

				if (fileName.equals("DLFE")) {
					if (!_hasAdvancedFileSystemStructureRepositoryIdPath(
							fileNamePath)) {

						return false;
					}
				}
				else if ((fileName.length() > 2) &&
						 Validator.isNull(FileUtil.getExtension(fileName))) {

					_log.error(
						StringBundler.concat(
							"File ", fileNamePath.toString(),
							" name has more than 2 characters and no ",
							"extension in advanced file system structure"));

					return false;
				}
			}

			return true;
		}
	}

	private boolean _hasFileSystemStructureCompanyIdPath(Path companyIdPath)
		throws IOException {

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				companyIdPath)) {

			for (Path repositoryIdPath : directoryStream) {
				if (_isExcludedPath(repositoryIdPath) ||
					_isSystemCompanyRepositoryIdPath(repositoryIdPath)) {

					continue;
				}

				if (!Files.isDirectory(repositoryIdPath)) {
					_log.error(
						"Unexpected file " + repositoryIdPath +
							" in file system structure");

					return false;
				}

				if (!_hasFileSystemStructureRepositoryIdPath(
						repositoryIdPath)) {

					return false;
				}
			}

			return true;
		}
	}

	private boolean _hasFileSystemStructureFileNamePath(Path fileNamePath)
		throws IOException {

		String fileName = String.valueOf(fileNamePath.getFileName());

		if (fileName.contains(StringPool.PERIOD)) {
			_log.error(
				StringBundler.concat(
					"Unexpected file name directory ", fileNamePath.toString(),
					" with extension in file system structure"));

			return false;
		}

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				fileNamePath)) {

			for (Path versionLabelPath : directoryStream) {
				if (_isExcludedPath(versionLabelPath) ||
					Files.isDirectory(versionLabelPath)) {

					continue;
				}

				String versionLabel = String.valueOf(
					versionLabelPath.getFileName());

				if (StringUtil.startsWith(
						versionLabel,
						DLFileEntryConstants.PRIVATE_WORKING_COPY_VERSION)) {

					continue;
				}

				if (!versionLabel.matches("\\d+\\.\\d+.*")) {
					_log.error(
						"Unexpected file " + versionLabelPath +
							" not matching version label pattern");

					return false;
				}
			}

			return true;
		}
	}

	private boolean _hasFileSystemStructureRepositoryIdPath(
			Path repositoryIdPath)
		throws IOException {

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				repositoryIdPath)) {

			for (Path fileNamePath : directoryStream) {
				if (_isExcludedPath(fileNamePath)) {
					continue;
				}

				if (!Files.isDirectory(fileNamePath)) {
					_log.error(
						"Unexpected file " + fileNamePath +
							" in file system structure");

					return false;
				}

				if (!_hasFileSystemStructureFileNamePath(fileNamePath)) {
					return false;
				}
			}

			return true;
		}
	}

	private boolean _isExcludedPath(Path path) {
		return _excludedFileNames.contains(String.valueOf(path.getFileName()));
	}

	private boolean _isSystemCompanyRepositoryIdPath(Path repositoryIdPath) {
		return StringUtil.equals(
			String.valueOf(repositoryIdPath.getFileName()),
			CompanyConstants.SYSTEM_STRING);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PreupgradeVerifyStoreFileSystemStructure.class);

	private static final Set<String> _excludedFileNames = new HashSet<>(
		Arrays.asList(".DS_Store"));

}