/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.google.docs.internal.helper;

import com.liferay.document.library.google.docs.internal.util.constants.GoogleDocsConstants;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.util.DefaultDDMStructureHelper;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Iván Zaera
 */
public class GoogleDocsDLFileEntryTypeUtil {

	public static void addGoogleDocsDLFileEntryType(
			Company company, DDMStructureLocalService ddmStructureLocalService,
			DefaultDDMStructureHelper defaultDDMStructureHelper,
			long dlFileEntryMetadataClassNameId,
			DLFileEntryTypeLocalService dlFileEntryTypeLocalService,
			boolean shortcut, UserLocalService userLocalService)
		throws Exception {

		if (shortcut &&
			ddmStructureLocalService.hasStructure(
				company.getGroupId(), dlFileEntryMetadataClassNameId,
				GoogleDocsConstants.DDM_STRUCTURE_KEY_GOOGLE_DOCS)) {

			return;
		}

		DDMStructure ddmStructure = ddmStructureLocalService.fetchStructure(
			company.getGroupId(), dlFileEntryMetadataClassNameId,
			GoogleDocsConstants.DDM_STRUCTURE_KEY_GOOGLE_DOCS);

		if (ddmStructure == null) {
			ddmStructure = _addGoogleDocsDDMStructure(
				company, ddmStructureLocalService, defaultDDMStructureHelper,
				dlFileEntryMetadataClassNameId, userLocalService);
		}

		DLFileEntryType dlFileEntryType =
			dlFileEntryTypeLocalService.fetchDataDefinitionFileEntryType(
				company.getGroupId(), ddmStructure.getStructureId());

		if (dlFileEntryType == null) {
			_addGoogleDocsDLFileEntryType(
				company, ddmStructure.getStructureId(),
				dlFileEntryTypeLocalService, userLocalService);
		}
		else {
			_updateDLFileEntryTypeNameMap(
				dlFileEntryType, dlFileEntryTypeLocalService);
		}
	}

	private static DDMStructure _addGoogleDocsDDMStructure(
			Company company, DDMStructureLocalService ddmStructureLocalService,
			DefaultDDMStructureHelper defaultDDMStructureHelper,
			long dlFileEntryMetadataClassNameId,
			UserLocalService userLocalService)
		throws Exception {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setScopeGroupId(company.getGroupId());

		long guestUserId = userLocalService.getGuestUserId(
			company.getCompanyId());

		serviceContext.setUserId(guestUserId);

		defaultDDMStructureHelper.addDDMStructures(
			guestUserId, company.getGroupId(), dlFileEntryMetadataClassNameId,
			GoogleDocsDLFileEntryTypeUtil.class.getClassLoader(),
			"com/liferay/document/library/google/docs/internal/util" +
				"/dependencies/google-docs-metadata-structure.xml",
			serviceContext);

		DDMStructure ddmStructure = ddmStructureLocalService.getStructure(
			company.getGroupId(), dlFileEntryMetadataClassNameId,
			GoogleDocsConstants.DL_FILE_ENTRY_TYPE_KEY);

		ddmStructure.setNameMap(_updateNameMap(ddmStructure.getNameMap()));
		ddmStructure.setType(DDMStructureConstants.TYPE_AUTO);

		return ddmStructureLocalService.updateDDMStructure(ddmStructure);
	}

	private static void _addGoogleDocsDLFileEntryType(
			Company company, long ddmStructureId,
			DLFileEntryTypeLocalService dlFileEntryTypeLocalService,
			UserLocalService userLocalService)
		throws Exception {

		long guestUserId = userLocalService.getGuestUserId(
			company.getCompanyId());

		Map<Locale, String> descriptionMap = new HashMap<>();

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setScopeGroupId(company.getGroupId());
		serviceContext.setUserId(guestUserId);

		dlFileEntryTypeLocalService.addFileEntryType(
			null, guestUserId, company.getGroupId(), ddmStructureId,
			GoogleDocsConstants.DL_FILE_ENTRY_TYPE_KEY,
			_getGoogleDriveShortcutNameMap(LanguageUtil.getAvailableLocales()),
			descriptionMap,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_SYSTEM,
			serviceContext);
	}

	private static Map<Locale, String> _getGoogleDriveShortcutNameMap(
		Set<Locale> locales) {

		Map<Locale, String> nameMap = new HashMap<>();

		for (Locale locale : locales) {
			nameMap.put(
				locale, LanguageUtil.get(locale, "google-drive-shortcut"));
		}

		return nameMap;
	}

	private static void _updateDLFileEntryTypeNameMap(
		DLFileEntryType dlFileEntryType,
		DLFileEntryTypeLocalService dlFileEntryTypeLocalService) {

		Map<Locale, String> nameMap = dlFileEntryType.getNameMap();

		Set<Locale> availableLocales = LanguageUtil.getAvailableLocales();

		if (nameMap.size() >= availableLocales.size()) {
			return;
		}

		dlFileEntryType.setNameMap(
			_getGoogleDriveShortcutNameMap(availableLocales));

		dlFileEntryTypeLocalService.updateDLFileEntryType(dlFileEntryType);
	}

	private static Map<Locale, String> _updateNameMap(
		Map<Locale, String> nameMap) {

		Map<Locale, String> updatedNameMap = new HashMap<>();

		for (Map.Entry<Locale, String> entry : nameMap.entrySet()) {
			updatedNameMap.put(
				entry.getKey(),
				LanguageUtil.get(entry.getKey(), "google-docs-metadata"));
		}

		return updatedNameMap;
	}

}