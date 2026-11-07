/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.document.library.thumbnails.internal.util;

import com.liferay.adaptive.media.exception.AMImageConfigurationException;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.PropsKeys;

import java.io.IOException;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Adolfo Pérez
 */
public class AMCompanyThumbnailConfigurationUtil {

	public static void initializeCompany(
			AMImageConfigurationHelper amImageConfigurationHelper,
			Company company, PrefsProps prefsProps)
		throws AMImageConfigurationException, IOException {

		int dlFileEntryPreviewMaxHeight = prefsProps.getInteger(
			PropsKeys.DL_FILE_ENTRY_PREVIEW_DOCUMENT_MAX_HEIGHT);
		int dlFileEntryPreviewMaxWidth = prefsProps.getInteger(
			PropsKeys.DL_FILE_ENTRY_PREVIEW_DOCUMENT_MAX_WIDTH);

		if ((dlFileEntryPreviewMaxHeight > 0) ||
			(dlFileEntryPreviewMaxWidth > 0)) {

			_createAMDocumentLibraryPreviewConfiguration(
				amImageConfigurationHelper, company,
				dlFileEntryPreviewMaxHeight, dlFileEntryPreviewMaxWidth);
		}

		int dlFileEntryThumbnailMaxHeight = prefsProps.getInteger(
			PropsKeys.DL_FILE_ENTRY_THUMBNAIL_MAX_HEIGHT);
		int dlFileEntryThumbnailMaxWidth = prefsProps.getInteger(
			PropsKeys.DL_FILE_ENTRY_THUMBNAIL_MAX_WIDTH);

		if ((dlFileEntryThumbnailMaxHeight > 0) &&
			(dlFileEntryThumbnailMaxWidth > 0)) {

			_createAMDocumentLibraryThumbnailConfiguration(
				amImageConfigurationHelper, company,
				dlFileEntryThumbnailMaxHeight, dlFileEntryThumbnailMaxWidth);
		}

		int dlFileEntryThumbnailCustom1MaxHeight = prefsProps.getInteger(
			PropsKeys.DL_FILE_ENTRY_THUMBNAIL_CUSTOM_1_MAX_HEIGHT);
		int dlFileEntryThumbnailCustom1MaxWidth = prefsProps.getInteger(
			PropsKeys.DL_FILE_ENTRY_THUMBNAIL_CUSTOM_1_MAX_WIDTH);

		if ((dlFileEntryThumbnailCustom1MaxHeight > 0) &&
			(dlFileEntryThumbnailCustom1MaxWidth > 0)) {

			_createAMDocumentLibraryThumbnailConfiguration(
				amImageConfigurationHelper, company,
				dlFileEntryThumbnailCustom1MaxHeight,
				dlFileEntryThumbnailCustom1MaxWidth);
		}

		int dlFileEntryThumbnailCustom2MaxHeight = prefsProps.getInteger(
			PropsKeys.DL_FILE_ENTRY_THUMBNAIL_CUSTOM_2_MAX_HEIGHT);
		int dlFileEntryThumbnailCustom2MaxWidth = prefsProps.getInteger(
			PropsKeys.DL_FILE_ENTRY_THUMBNAIL_CUSTOM_2_MAX_WIDTH);

		if ((dlFileEntryThumbnailCustom2MaxHeight > 0) &&
			(dlFileEntryThumbnailCustom2MaxWidth > 0)) {

			_createAMDocumentLibraryThumbnailConfiguration(
				amImageConfigurationHelper, company,
				dlFileEntryThumbnailCustom2MaxHeight,
				dlFileEntryThumbnailCustom2MaxWidth);
		}
	}

	private static void _createAMDocumentLibraryConfiguration(
			AMImageConfigurationHelper amImageConfigurationHelper,
			Company company, String name, int maxHeight, int maxWidth)
		throws AMImageConfigurationException, IOException {

		String uuid = _normalize(name);

		if (!_hasConfiguration(
				amImageConfigurationHelper, company.getCompanyId(), name,
				uuid)) {

			amImageConfigurationHelper.addAMImageConfigurationEntry(
				company.getCompanyId(), name,
				"This image resolution was automatically added.", uuid,
				HashMapBuilder.put(
					"max-height", String.valueOf(maxHeight)
				).put(
					"max-width", String.valueOf(maxWidth)
				).build());
		}
	}

	private static void _createAMDocumentLibraryPreviewConfiguration(
			AMImageConfigurationHelper amImageConfigurationHelper,
			Company company, int maxHeight, int maxWidth)
		throws AMImageConfigurationException, IOException {

		String name = String.format("%s %dx%d", "Preview", maxWidth, maxHeight);

		_createAMDocumentLibraryConfiguration(
			amImageConfigurationHelper, company, name, maxHeight, maxWidth);
	}

	private static void _createAMDocumentLibraryThumbnailConfiguration(
			AMImageConfigurationHelper amImageConfigurationHelper,
			Company company, int maxHeight, int maxWidth)
		throws AMImageConfigurationException, IOException {

		String name = String.format(
			"%s %dx%d", "Thumbnail", maxWidth, maxHeight);

		_createAMDocumentLibraryConfiguration(
			amImageConfigurationHelper, company, name, maxHeight, maxWidth);
	}

	private static boolean _hasConfiguration(
		AMImageConfigurationHelper amImageConfigurationHelper, long companyId,
		String name, String uuid) {

		Collection<AMImageConfigurationEntry> amImageConfigurationEntries =
			amImageConfigurationHelper.getAMImageConfigurationEntries(
				companyId,
				amImageConfigurationEntry -> {
					if (name.equals(amImageConfigurationEntry.getName()) ||
						uuid.equals(amImageConfigurationEntry.getUUID())) {

						return true;
					}

					return false;
				});

		return !amImageConfigurationEntries.isEmpty();
	}

	private static String _normalize(String str) {
		Matcher matcher = _pattern.matcher(str);

		return matcher.replaceAll(StringPool.DASH);
	}

	private static final Pattern _pattern = Pattern.compile("[^\\w-]");

}