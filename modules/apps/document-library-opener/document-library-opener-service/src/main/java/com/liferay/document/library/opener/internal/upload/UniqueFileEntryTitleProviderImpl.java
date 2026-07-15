/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.internal.upload;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.opener.upload.UniqueFileEntryTitleProvider;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.upload.UniqueFileNameProvider;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = UniqueFileEntryTitleProvider.class)
public class UniqueFileEntryTitleProviderImpl
	implements UniqueFileEntryTitleProvider {

	@Override
	public String provide(long groupId, long folderId, Locale locale)
		throws PortalException {

		return _provide(
			groupId, folderId, StringPool.BLANK, _getDefaultTitle(locale));
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #provide(long
	 *             groupId, long folderId, String extension, String title)}
	 */
	@Deprecated
	@Override
	public String provide(long groupId, long folderId, String title)
		throws PortalException {

		return _uniqueFileNameProvider.provide(
			title,
			generatedTitle -> _titleExists(groupId, folderId, generatedTitle));
	}

	@Override
	public String provide(
			long groupId, long folderId, String extension, Locale locale)
		throws PortalException {

		return _provide(groupId, folderId, extension, _getDefaultTitle(locale));
	}

	@Override
	public String provide(
			long groupId, long folderId, String extension, String title)
		throws PortalException {

		return _provide(groupId, folderId, extension, title);
	}

	private boolean _exists(
		UnsafeSupplier<FileEntry, PortalException> unsafeSupplier) {

		try {
			FileEntry fileEntry = unsafeSupplier.get();

			if (fileEntry != null) {
				return true;
			}

			return false;
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	private boolean _fileNameExists(
		long groupId, long folderId, String fileName) {

		return _exists(
			() -> _dlAppLocalService.fetchFileEntryByFileName(
				groupId, folderId, fileName));
	}

	private String _getDefaultTitle(Locale locale) {
		return _language.get(locale, "untitled");
	}

	private String _provide(
			long groupId, long folderId, String extension, String title)
		throws PortalException {

		return _uniqueFileNameProvider.provide(
			title,
			generatedTitle ->
				_fileNameExists(
					groupId, folderId, generatedTitle.concat(extension)) ||
				_titleExists(groupId, folderId, generatedTitle));
	}

	private boolean _titleExists(long groupId, long folderId, String title) {
		return _exists(
			() -> _dlAppLocalService.fetchFileEntry(groupId, folderId, title));
	}

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private Language _language;

	@Reference
	private UniqueFileNameProvider _uniqueFileNameProvider;

}