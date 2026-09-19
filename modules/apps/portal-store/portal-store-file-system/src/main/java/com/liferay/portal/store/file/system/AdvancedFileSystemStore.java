/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.store.file.system;

import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.store.file.system.configuration.AdvancedFileSystemStoreConfiguration;

import java.io.File;

import java.util.List;
import java.util.ListIterator;

/**
 * <p>
 * See https://issues.liferay.com/browse/LPS-1976.
 * </p>
 *
 * @author Jorge Ferrer
 * @author Ryan Park
 * @author Brian Wing Shun Chan
 * @author Manuel de la Peña
 */
public class AdvancedFileSystemStore extends FileSystemStore {

	public AdvancedFileSystemStore(
		AdvancedFileSystemStoreConfiguration
			advancedFileSystemStoreConfiguration) {

		super(advancedFileSystemStoreConfiguration);
	}

	@Override
	public String[] getFileVersions(
		long companyId, long repositoryId, String fileName) {

		String[] versions = super.getFileVersions(
			companyId, repositoryId, fileName);

		for (int i = 0; i < versions.length; i++) {
			int x = versions[i].lastIndexOf(CharPool.UNDERLINE);

			if (x > -1) {
				int y = versions[i].lastIndexOf(CharPool.PERIOD);

				versions[i] = versions[i].substring(x + 1, y);
			}
		}

		return versions;
	}

	protected void buildPath(StringBundler sb, String fileNameFragment) {
		int fileNameFragmentLength = fileNameFragment.length();

		if (fileNameFragmentLength <= 2) {
			return;
		}

		for (int i = 0; (i + 2) < fileNameFragmentLength; i += 2) {
			sb.append(fileNameFragment.substring(i, i + 2));
			sb.append(StringPool.SLASH);

			if (getDepth(sb.toString()) > 3) {
				return;
			}
		}
	}

	protected int getDepth(String path) {
		String[] fragments = StringUtil.split(path, CharPool.SLASH);

		return fragments.length;
	}

	@Override
	protected File getDirNameDir(
		long companyId, long repositoryId, String dirName) {

		File repositoryDir = getRepositoryDir(companyId, repositoryId);

		return new File(repositoryDir + StringPool.SLASH + dirName);
	}

	@Override
	protected File getFileNameDir(
		long companyId, long repositoryId, String fileName) {

		if (fileName.indexOf(CharPool.SLASH) != -1) {
			return getDirNameDir(companyId, repositoryId, fileName);
		}

		String ext = StringPool.PERIOD + FileUtil.getExtension(fileName);

		if (ext.equals(StringPool.PERIOD)) {
			ext += _HOOK_EXTENSION;
		}

		StringBundler sb = new StringBundler();

		String fileNameFragment = FileUtil.stripExtension(fileName);

		if (fileNameFragment.startsWith("DLFE-")) {
			fileNameFragment = fileNameFragment.substring(5);

			sb.append("DLFE/");
		}

		buildPath(sb, fileNameFragment);

		File repositoryDir = getRepositoryDir(companyId, repositoryId);

		StringBundler pathSB = new StringBundler(6);

		pathSB.append(repositoryDir);
		pathSB.append(StringPool.SLASH);
		pathSB.append(sb.toString());

		FileUtil.mkdirs(pathSB.toString());

		pathSB.append(StringPool.SLASH);
		pathSB.append(fileNameFragment);
		pathSB.append(ext);

		return new File(pathSB.toString());
	}

	@Override
	protected File getFileNameVersionFile(
		long companyId, long repositoryId, String fileName, String version) {

		String ext = StringPool.PERIOD + FileUtil.getExtension(fileName);

		if (ext.equals(StringPool.PERIOD)) {
			ext += _HOOK_EXTENSION;
		}

		int pos = fileName.lastIndexOf(CharPool.SLASH);

		if (pos == -1) {
			StringBundler sb = new StringBundler();

			String fileNameFragment = FileUtil.stripExtension(fileName);

			if (fileNameFragment.startsWith("DLFE-")) {
				fileNameFragment = fileNameFragment.substring(5);

				sb.append("DLFE/");
			}

			buildPath(sb, fileNameFragment);

			File repositoryDir = getRepositoryDir(companyId, repositoryId);

			return new File(
				StringBundler.concat(
					repositoryDir, StringPool.SLASH, sb, StringPool.SLASH,
					fileNameFragment, ext, StringPool.SLASH, fileNameFragment,
					StringPool.UNDERLINE, version, ext));
		}

		File fileNameDir = getDirNameDir(companyId, repositoryId, fileName);

		String fileNameFragment = FileUtil.stripExtension(
			fileName.substring(pos + 1));

		return new File(
			StringBundler.concat(
				fileNameDir, StringPool.SLASH, fileNameFragment,
				StringPool.UNDERLINE, version, ext));
	}

	@Override
	protected void getFileNames(
		List<String> fileNames, String dirName, String path) {

		super.getFileNames(fileNames, dirName, path);

		ListIterator<String> iterator = fileNames.listIterator();

		while (iterator.hasNext()) {
			String shortFileName = iterator.next();

			if (path.endsWith(_HOOK_EXTENSION)) {
				shortFileName = FileUtil.stripExtension(shortFileName);
			}

			iterator.set(unbuildPath(shortFileName));
		}
	}

	@Override
	protected String getHeadVersionLabel(
		long companyId, long repositoryId, String fileName) {

		File fileNameDir = getFileNameDir(companyId, repositoryId, fileName);

		if (!fileNameDir.exists()) {
			return VERSION_DEFAULT;
		}

		String[] versionLabels = FileUtil.listFiles(fileNameDir);

		String headVersionLabel = VERSION_DEFAULT;

		for (String versionLabelFragment : versionLabels) {
			int x = versionLabelFragment.lastIndexOf(CharPool.UNDERLINE);

			if (x > -1) {
				int y = versionLabelFragment.lastIndexOf(CharPool.PERIOD);

				versionLabelFragment = versionLabelFragment.substring(x + 1, y);
			}

			String versionLabel = versionLabelFragment;

			if (DLUtil.compareVersions(versionLabel, headVersionLabel) > 0) {
				headVersionLabel = versionLabel;
			}
		}

		return headVersionLabel;
	}

	protected String unbuildPath(String path) {
		if (path.startsWith("DLFE/")) {
			path = path.substring(5);
		}

		if (path.length() <= 2) {
			return path;
		}

		String[] parts = StringUtil.split(path, CharPool.SLASH);

		StringBundler sb = new StringBundler(parts.length - 1);

		for (int i = 0; i < (parts.length - 1); i++) {
			sb.append(parts[i]);
		}

		String simpleName = parts[parts.length - 1];

		if (simpleName.startsWith(sb.toString())) {
			return simpleName;
		}

		return path;
	}

	private static final String _HOOK_EXTENSION = "afsh";

}