/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.layout.display.page;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.friendly.url.info.item.provider.InfoItemFriendlyURLProvider;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.Locale;

/**
 * @author Jürgen Kappler
 */
public class FileEntryLayoutDisplayPageObjectProvider
	implements LayoutDisplayPageObjectProvider<FileEntry> {

	public FileEntryLayoutDisplayPageObjectProvider(
		FileEntry fileEntry,
		InfoItemFriendlyURLProvider<FileEntry> infoItemFriendlyURLProvider,
		Language language) {

		_fileEntry = fileEntry;
		_infoItemFriendlyURLProvider = infoItemFriendlyURLProvider;
		_language = language;

		_assetEntry = _getAssetEntry(fileEntry);
	}

	@Override
	public String getClassName() {
		return FileEntry.class.getName();
	}

	@Override
	public long getClassNameId() {
		return PortalUtil.getClassNameId(FileEntry.class.getName());
	}

	@Override
	public long getClassPK() {
		return _fileEntry.getFileEntryId();
	}

	@Override
	public long getClassTypeId() {
		if (_assetEntry != null) {
			return _assetEntry.getClassTypeId();
		}

		return 0;
	}

	@Override
	public String getDescription(Locale locale) {
		return _fileEntry.getDescription();
	}

	@Override
	public FileEntry getDisplayObject() {
		return _fileEntry;
	}

	@Override
	public String getExternalReferenceCode() {
		return _fileEntry.getExternalReferenceCode();
	}

	@Override
	public long getGroupId() {
		return _fileEntry.getGroupId();
	}

	@Override
	public String getKeywords(Locale locale) {
		return StringPool.BLANK;
	}

	@Override
	public String getTitle(Locale locale) {
		return _fileEntry.getTitle();
	}

	@Override
	public String getURLTitle(Locale locale) {
		return _infoItemFriendlyURLProvider.getFriendlyURL(
			_fileEntry, _language.getLanguageId(locale));
	}

	private AssetEntry _getAssetEntry(FileEntry fileEntry) {
		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
				DLFileEntry.class);

		if (assetRendererFactory == null) {
			return null;
		}

		try {
			AssetRenderer<?> assetRenderer =
				assetRendererFactory.getAssetRenderer(
					fileEntry.getFileEntryId());

			if (assetRenderer == null) {
				return null;
			}

			return assetRendererFactory.getAssetEntry(
				DLFileEntryConstants.getClassName(),
				assetRenderer.getClassPK());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FileEntryLayoutDisplayPageObjectProvider.class);

	private final AssetEntry _assetEntry;
	private final FileEntry _fileEntry;
	private final InfoItemFriendlyURLProvider<FileEntry>
		_infoItemFriendlyURLProvider;
	private final Language _language;

}