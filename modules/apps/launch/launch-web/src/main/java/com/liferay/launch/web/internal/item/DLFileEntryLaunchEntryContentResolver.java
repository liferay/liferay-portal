/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.item;

import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(
	property = "launch.entry.content.resolver.class.name=com.liferay.document.library.kernel.model.DLFileEntry",
	service = LaunchEntryContentResolver.class
)
public class DLFileEntryLaunchEntryContentResolver
	implements LaunchEntryContentResolver {

	@Override
	public LaunchEntryContent resolve(
			long classPK, String classVersion, Locale locale)
		throws PortalException {

		DLFileVersion dlFileVersion = _dlFileVersionLocalService.getFileVersion(
			classPK, classVersion);

		return new LaunchEntryContent(
			dlFileVersion.getGroupId(), dlFileVersion.getModifiedDate(),
			dlFileVersion.getStatus(), dlFileVersion.getTitle(),
			_getTypeName(dlFileVersion.getFileEntryTypeId(), locale),
			dlFileVersion.getUserName());
	}

	private String _getTypeName(long fileEntryTypeId, Locale locale) {
		if (fileEntryTypeId ==
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT) {

			return LanguageUtil.get(
				locale, DLFileEntryTypeConstants.NAME_BASIC_DOCUMENT);
		}

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.fetchDLFileEntryType(fileEntryTypeId);

		if (dlFileEntryType == null) {
			return LanguageUtil.get(
				locale, DLFileEntryTypeConstants.NAME_BASIC_DOCUMENT);
		}

		return dlFileEntryType.getName(locale);
	}

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private DLFileVersionLocalService _dlFileVersionLocalService;

}