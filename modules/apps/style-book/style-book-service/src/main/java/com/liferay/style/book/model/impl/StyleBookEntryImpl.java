/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.model.impl;

import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.zip.ZipWriter;

/**
 * @author Eudaldo Alonso
 */
public class StyleBookEntryImpl extends StyleBookEntryBaseImpl {

	@Override
	public String getImagePreviewURL(ThemeDisplay themeDisplay) {
		if (getPreviewFileEntryId() <= 0) {
			return StringPool.BLANK;
		}

		try {
			FileEntry fileEntry = PortletFileRepositoryUtil.getPortletFileEntry(
				getPreviewFileEntryId());

			if (fileEntry == null) {
				return StringPool.BLANK;
			}

			return DLURLHelperUtil.getImagePreviewURL(fileEntry, themeDisplay);
		}
		catch (Exception exception) {
			_log.error("Unable to get image preview URL", exception);
		}

		return StringPool.BLANK;
	}

	@Override
	public void populateZipWriter(ZipWriter zipWriter, String path)
		throws Exception {

		String frontendTokenDefinition = getFrontendTokenDefinition();
		FileEntry previewFileEntry = _getPreviewFileEntry();

		JSONObject jsonObject = JSONUtil.put(
			"frontendTokenDefinitionPath",
			() -> {
				if (Validator.isBlank(frontendTokenDefinition)) {
					return null;
				}

				return _FRONTEND_TOKEN_DEFINITION_FILE_NAME;
			}
		).put(
			"frontendTokensValuesPath", _FRONTEND_TOKENS_VALUES_FILE_NAME
		).put(
			"name", getName()
		).put(
			"themeId", getThemeId()
		).put(
			"thumbnailPath", () -> _getThumbnailFileName(previewFileEntry)
		);

		path = path + StringPool.SLASH + getStyleBookEntryKey();

		zipWriter.addEntry(
			path + "/style-book.json", JSONUtil.toString(jsonObject));

		if (!Validator.isBlank(frontendTokenDefinition)) {
			zipWriter.addEntry(
				path + StringPool.SLASH + _FRONTEND_TOKEN_DEFINITION_FILE_NAME,
				frontendTokenDefinition);
		}

		zipWriter.addEntry(
			path + StringPool.SLASH + _FRONTEND_TOKENS_VALUES_FILE_NAME,
			getFrontendTokensValues());

		if (previewFileEntry != null) {
			zipWriter.addEntry(
				path + StringPool.SLASH +
					_getThumbnailFileName(previewFileEntry),
				previewFileEntry.getContentStream());
		}
	}

	private FileEntry _getPreviewFileEntry() {
		if (getPreviewFileEntryId() <= 0) {
			return null;
		}

		try {
			return PortletFileRepositoryUtil.getPortletFileEntry(
				getPreviewFileEntryId());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get file entry preview ", portalException);
			}
		}

		return null;
	}

	private String _getThumbnailFileName(FileEntry previewFileEntry) {
		if (previewFileEntry == null) {
			return null;
		}

		return "thumbnail." + previewFileEntry.getExtension();
	}

	private static final String _FRONTEND_TOKEN_DEFINITION_FILE_NAME =
		"frontend-token-definition.json";

	private static final String _FRONTEND_TOKENS_VALUES_FILE_NAME =
		"frontend-tokens-values.json";

	private static final Log _log = LogFactoryUtil.getLog(
		StyleBookEntryImpl.class);

}