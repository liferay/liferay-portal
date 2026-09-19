/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.store.file.system;

import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.store.file.system.configuration.FileSystemStoreConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 * @author Sten Martinez
 * @author Alexander Chow
 * @author Edward Han
 * @author Manuel de la Peña
 */
public class FileSystemStore implements Store {

	public FileSystemStore(
		FileSystemStoreConfiguration fileSystemStoreConfiguration) {

		String path = fileSystemStoreConfiguration.rootDir();

		File rootDir = new File(path);

		if (!rootDir.isAbsolute()) {
			rootDir = new File(PropsUtil.get(PropsKeys.LIFERAY_HOME), path);
		}

		_rootDir = rootDir;

		_rootDir.mkdirs();

		try {
			FileUtil.write(
				new File(_rootDir, "README.txt"),
				StringUtil.read(
					FileSystemStore.class, "dependencies/README.txt"));
		}
		catch (IOException ioException) {
			ReflectionUtil.throwException(ioException);
		}
	}

	@Override
	public void addFile(
		long companyId, long repositoryId, String fileName, String versionLabel,
		InputStream inputStream) {

		if (Validator.isNull(versionLabel)) {
			versionLabel = getHeadVersionLabel(
				companyId, repositoryId, fileName);
		}

		try {
			FileUtil.write(
				getFileNameVersionFile(
					companyId, repositoryId, fileName, versionLabel),
				inputStream);
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
	}

	@Override
	public void deleteDirectory(long companyId) {
		File companyDir = new File(_rootDir, String.valueOf(companyId));

		if (!companyDir.exists()) {
			return;
		}

		FileUtil.deltree(companyDir);
	}

	@Override
	public void deleteDirectory(
		long companyId, long repositoryId, String dirName) {

		File dirNameDir = null;

		if (Objects.equals(dirName, StringPool.SLASH)) {
			dirNameDir = getRepositoryDir(companyId, repositoryId);
		}
		else {
			dirNameDir = getDirNameDir(companyId, repositoryId, dirName);
		}

		if (!dirNameDir.exists()) {
			return;
		}

		File parentFile = dirNameDir.getParentFile();

		FileUtil.deltree(dirNameDir);

		_deleteEmptyAncestors(parentFile);
	}

	@Override
	public void deleteFile(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		if (Validator.isNull(versionLabel)) {
			versionLabel = getHeadVersionLabel(
				companyId, repositoryId, fileName);
		}

		File fileNameVersionFile = getFileNameVersionFile(
			companyId, repositoryId, fileName, versionLabel);

		if (!fileNameVersionFile.exists()) {
			return;
		}

		File parentFile = fileNameVersionFile.getParentFile();

		fileNameVersionFile.delete();

		_deleteEmptyAncestors(parentFile);
	}

	@Override
	public InputStream getFileAsStream(
			long companyId, long repositoryId, String fileName,
			String versionLabel)
		throws NoSuchFileException {

		if (Validator.isNull(versionLabel)) {
			versionLabel = getHeadVersionLabel(
				companyId, repositoryId, fileName);
		}

		File fileNameVersionFile = getFileNameVersionFile(
			companyId, repositoryId, fileName, versionLabel);

		Path path = fileNameVersionFile.toPath();

		try {
			BasicFileAttributes basicFileAttributes = Files.readAttributes(
				path, BasicFileAttributes.class);

			if (basicFileAttributes.size() == 0) {
				return _unsyncByteArrayInputStream;
			}

			return Files.newInputStream(path);
		}
		catch (IOException ioException) {
			throw new NoSuchFileException(
				companyId, repositoryId, fileName, versionLabel, ioException);
		}
	}

	@Override
	public String[] getFileNames(
		long companyId, long repositoryId, String dirName) {

		File dirNameDir = getDirNameDir(companyId, repositoryId, dirName);

		if (!dirNameDir.exists()) {
			return new String[0];
		}

		List<String> fileNames = new ArrayList<>();

		getFileNames(fileNames, dirName, dirNameDir.getPath());

		Collections.sort(fileNames);

		return fileNames.toArray(new String[0]);
	}

	@Override
	public long getFileSize(
			long companyId, long repositoryId, String fileName,
			String versionLabel)
		throws NoSuchFileException {

		if (Validator.isNull(versionLabel)) {
			versionLabel = getHeadVersionLabel(
				companyId, repositoryId, fileName);
		}

		File fileNameVersionFile = getFileNameVersionFile(
			companyId, repositoryId, fileName, versionLabel);

		if (!fileNameVersionFile.exists()) {
			throw new NoSuchFileException(
				companyId, repositoryId, fileName, versionLabel);
		}

		return fileNameVersionFile.length();
	}

	@Override
	public String[] getFileVersions(
		long companyId, long repositoryId, String fileName) {

		File fileNameDir = getFileNameDir(companyId, repositoryId, fileName);

		if (!fileNameDir.exists()) {
			return StringPool.EMPTY_ARRAY;
		}

		String[] versions = FileUtil.listFiles(fileNameDir);

		Arrays.sort(versions, DLUtil::compareVersions);

		return versions;
	}

	public File getRootDir() {
		return _rootDir;
	}

	@Override
	public boolean hasFile(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		if (Validator.isNull(versionLabel)) {
			versionLabel = getHeadVersionLabel(
				companyId, repositoryId, fileName);
		}

		File fileNameVersionFile = getFileNameVersionFile(
			companyId, repositoryId, fileName, versionLabel);

		return fileNameVersionFile.exists();
	}

	@Override
	public void verifyCompanyStores() {
		File[] storeDirs = _rootDir.listFiles(
			file -> file.isDirectory() && Validator.isNumber(file.getName()));

		if (storeDirs == null) {
			return;
		}

		long[] companyIds = PortalInstancePool.getCompanyIds();

		for (File storeDir : storeDirs) {
			long storeCompanyId = GetterUtil.getLong(storeDir.getName());

			if ((storeCompanyId == 0) ||
				ArrayUtil.contains(companyIds, storeCompanyId)) {

				continue;
			}

			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Manually remove unused store ", storeCompanyId,
						" that belongs to company ", storeCompanyId,
						" if it is no longer used anywhere else"));
			}
		}
	}

	protected File getDirNameDir(
		long companyId, long repositoryId, String dirName) {

		return getFileNameDir(companyId, repositoryId, dirName);
	}

	protected File getFileNameDir(
		long companyId, long repositoryId, String fileName) {

		return new File(getRepositoryDir(companyId, repositoryId), fileName);
	}

	protected File getFileNameVersionFile(
		long companyId, long repositoryId, String fileName, String version) {

		return new File(
			getFileNameDir(companyId, repositoryId, fileName), version);
	}

	protected void getFileNames(
		List<String> fileNames, String dirName, String path) {

		String[] pathDirNames = FileUtil.listDirs(path);

		if (ArrayUtil.isNotEmpty(pathDirNames)) {
			for (String pathDirName : pathDirNames) {
				String subdirName = null;

				if (Validator.isBlank(dirName)) {
					subdirName = pathDirName;
				}
				else {
					subdirName = dirName + StringPool.SLASH + pathDirName;
				}

				getFileNames(
					fileNames, subdirName,
					path + StringPool.SLASH + pathDirName);
			}
		}
		else if (!dirName.isEmpty()) {
			File file = new File(path);

			if (file.isDirectory()) {
				fileNames.add(dirName);
			}
		}
	}

	protected String getHeadVersionLabel(
		long companyId, long repositoryId, String fileName) {

		File fileNameDir = getFileNameDir(companyId, repositoryId, fileName);

		if (!fileNameDir.exists()) {
			return VERSION_DEFAULT;
		}

		String[] versionLabels = FileUtil.listFiles(fileNameDir);

		String headVersionLabel = VERSION_DEFAULT;

		for (String versionLabel : versionLabels) {
			if (DLUtil.compareVersions(versionLabel, headVersionLabel) > 0) {
				headVersionLabel = versionLabel;
			}
		}

		return headVersionLabel;
	}

	protected File getRepositoryDir(long companyId, long repositoryId) {
		File repositoryDir = new File(
			_rootDir, companyId + StringPool.SLASH + repositoryId);

		if (!repositoryDir.exists()) {
			repositoryDir.mkdirs();
		}

		return repositoryDir;
	}

	private void _deleteEmptyAncestors(File file) {
		while ((file != null) && !file.equals(_rootDir)) {
			if (!file.delete()) {
				return;
			}

			file = file.getParentFile();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FileSystemStore.class);

	private static final UnsyncByteArrayInputStream
		_unsyncByteArrayInputStream = new UnsyncByteArrayInputStream(
			new byte[0]);

	private final File _rootDir;

}