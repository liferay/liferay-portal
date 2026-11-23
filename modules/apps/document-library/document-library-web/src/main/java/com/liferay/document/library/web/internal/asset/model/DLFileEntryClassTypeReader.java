/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.asset.model;

import com.liferay.asset.kernel.model.ClassType;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.document.library.asset.DLFileEntryClassType;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.List;
import java.util.Locale;

/**
 * @author Adolfo Pérez
 */
public class DLFileEntryClassTypeReader implements ClassTypeReader {

	@Override
	public List<ClassType> getAvailableClassTypes(
		long[] groupIds, Locale locale) {

		String languageId = LocaleUtil.toLanguageId(locale);

		return TransformUtil.transform(
			DLFileEntryTypeServiceUtil.getFileEntryTypes(groupIds),
			dlFileEntryType -> new DLFileEntryClassType(
				dlFileEntryType.getFileEntryTypeId(),
				dlFileEntryType.getName(locale), languageId));
	}

	@Override
	public ClassType getClassType(long classTypeId, Locale locale)
		throws PortalException {

		if (classTypeId ==
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT) {

			return getBasicDocumentClassType(locale);
		}

		DLFileEntryType dlFileEntryType =
			DLFileEntryTypeServiceUtil.getFileEntryType(classTypeId);

		return new DLFileEntryClassType(
			dlFileEntryType.getFileEntryTypeId(),
			dlFileEntryType.getName(locale), LocaleUtil.toLanguageId(locale));
	}

	protected ClassType getBasicDocumentClassType(Locale locale) {
		DLFileEntryType basicDocumentDLFileEntryType =
			DLFileEntryTypeLocalServiceUtil.fetchDLFileEntryType(
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);

		return new DLFileEntryClassType(
			basicDocumentDLFileEntryType.getFileEntryTypeId(),
			LanguageUtil.get(
				locale, DLFileEntryTypeConstants.NAME_BASIC_DOCUMENT),
			LocaleUtil.toLanguageId(locale));
	}

}