/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.store.db;

import com.liferay.document.library.content.exception.NoSuchContentException;
import com.liferay.document.library.content.model.DLContent;
import com.liferay.document.library.content.service.DLContentLocalService;
import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.petra.string.StringPool;

import java.io.InputStream;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 * @author Tina Tian
 */
@Component(
	property = {
		"ct.aware=true", "store.type=com.liferay.portal.store.db.DBStore"
	},
	service = Store.class
)
public class DBStore implements Store {

	@Override
	public void addFile(
		long companyId, long repositoryId, String fileName, String versionLabel,
		InputStream inputStream) {

		_dlContentLocalService.addContent(
			companyId, repositoryId, fileName, versionLabel, inputStream);
	}

	@Override
	public void deleteDirectory(
		long companyId, long repositoryId, String dirName) {

		_dlContentLocalService.deleteContentsByDirectory(
			companyId, repositoryId, dirName);
	}

	@Override
	public void deleteFile(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		_dlContentLocalService.deleteContent(
			companyId, repositoryId, fileName, versionLabel);
	}

	@Override
	public InputStream getFileAsStream(
			long companyId, long repositoryId, String fileName,
			String versionLabel)
		throws NoSuchFileException {

		try {
			DLContent dlContent = _dlContentLocalService.getContent(
				companyId, repositoryId, fileName, versionLabel);

			return dlContent.getData(
			).getBinaryStream();
		}
		catch (Exception noSuchContentException) {
			throw new NoSuchFileException(
				companyId, repositoryId, fileName, versionLabel,
				noSuchContentException);
		}
	}

	@Override
	public String[] getFileNames(
		long companyId, long repositoryId, String dirName) {

		List<DLContent> dlContents =
			_dlContentLocalService.getContentsByDirectory(
				companyId, repositoryId, dirName);

		String[] fileNames = new String[dlContents.size()];

		for (int i = 0; i < dlContents.size(); i++) {
			DLContent dlContent = dlContents.get(i);

			fileNames[i] = dlContent.getPath();
		}

		return fileNames;
	}

	@Override
	public long getFileSize(
			long companyId, long repositoryId, String fileName,
			String versionLabel)
		throws NoSuchFileException {

		DLContent dlContent = null;

		try {
			dlContent = _dlContentLocalService.getContent(
				companyId, repositoryId, fileName, versionLabel);
		}
		catch (NoSuchContentException noSuchContentException) {
			throw new NoSuchFileException(
				companyId, repositoryId, fileName, noSuchContentException);
		}

		return dlContent.getSize();
	}

	@Override
	public String[] getFileVersions(
		long companyId, long repositoryId, String fileName) {

		List<DLContent> dlContents = _dlContentLocalService.getContents(
			companyId, repositoryId, fileName);

		if (dlContents.isEmpty()) {
			return StringPool.EMPTY_ARRAY;
		}

		String[] versions = new String[dlContents.size()];

		for (int i = 0; i < dlContents.size(); i++) {
			DLContent dlContent = dlContents.get(i);

			versions[i] = dlContent.getVersion();
		}

		Arrays.sort(versions, DLUtil::compareVersions);

		return versions;
	}

	@Override
	public boolean hasFile(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		return _dlContentLocalService.hasContent(
			companyId, repositoryId, fileName, versionLabel);
	}

	@Reference
	private DLContentLocalService _dlContentLocalService;

}