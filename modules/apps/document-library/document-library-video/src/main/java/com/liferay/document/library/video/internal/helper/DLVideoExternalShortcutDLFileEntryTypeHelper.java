/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.video.internal.helper;

import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.video.internal.constants.DLVideoConstants;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.util.DefaultDDMStructureHelper;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author Iván Zaera
 * @author Alejandro Tardín
 */
public class DLVideoExternalShortcutDLFileEntryTypeHelper {

	public DLVideoExternalShortcutDLFileEntryTypeHelper(
		Company company, DefaultDDMStructureHelper defaultDDMStructureHelper,
		long dlFileEntryMetadataClassNameId,
		DDMStructureLocalService ddmStructureLocalService,
		DLFileEntryTypeLocalService dlFileEntryTypeLocalService,
		UserLocalService userLocalService) {

		_company = company;
		_defaultDDMStructureHelper = defaultDDMStructureHelper;
		_dlFileEntryMetadataClassNameId = dlFileEntryMetadataClassNameId;
		_ddmStructureLocalService = ddmStructureLocalService;
		_dlFileEntryTypeLocalService = dlFileEntryTypeLocalService;
		_userLocalService = userLocalService;
	}

	public void addDLVideoExternalShortcutDLFileEntryType(boolean shortcut)
		throws Exception {

		if (shortcut &&
			_ddmStructureLocalService.hasStructure(
				_company.getGroupId(), _dlFileEntryMetadataClassNameId,
				DLVideoConstants.
					DDM_STRUCTURE_KEY_DL_VIDEO_EXTERNAL_SHORTCUT)) {

			return;
		}

		DDMStructure ddmStructure = _ddmStructureLocalService.fetchStructure(
			_company.getGroupId(), _dlFileEntryMetadataClassNameId,
			DLVideoConstants.DDM_STRUCTURE_KEY_DL_VIDEO_EXTERNAL_SHORTCUT);

		if (ddmStructure == null) {
			ddmStructure = _addDLVideoExternalShortcutDDMStructure();

			_addDLVideoExternalShortcutDLFileEntryType(
				ddmStructure.getStructureId());
		}
		else {
			DLFileEntryType dlFileEntryType =
				_dlFileEntryTypeLocalService.fetchDataDefinitionFileEntryType(
					_company.getGroupId(), ddmStructure.getStructureId());

			if (dlFileEntryType == null) {
				_addDLVideoExternalShortcutDLFileEntryType(
					ddmStructure.getStructureId());
			}
			else {
				_updateDLFileEntryTypeNameMap(dlFileEntryType);
			}
		}
	}

	private DDMStructure _addDLVideoExternalShortcutDDMStructure()
		throws Exception {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setScopeGroupId(_company.getGroupId());

		long guestUserId = _userLocalService.getGuestUserId(
			_company.getCompanyId());

		serviceContext.setUserId(guestUserId);

		Class<?> clazz = getClass();

		_defaultDDMStructureHelper.addDDMStructures(
			guestUserId, _company.getGroupId(), _dlFileEntryMetadataClassNameId,
			clazz.getClassLoader(),
			"com/liferay/document/library/video/internal/util/dependencies" +
				"/dl-video-external-shortcut-metadata-structure.xml",
			serviceContext);

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			_company.getGroupId(), _dlFileEntryMetadataClassNameId,
			DLVideoConstants.DL_FILE_ENTRY_TYPE_KEY);

		ddmStructure.setNameMap(_updateNameMap(ddmStructure.getNameMap()));
		ddmStructure.setDescriptionMap(
			_updateDescriptionMap(ddmStructure.getDescriptionMap()));
		ddmStructure.setType(DDMStructureConstants.TYPE_AUTO);

		return _ddmStructureLocalService.updateDDMStructure(ddmStructure);
	}

	private void _addDLVideoExternalShortcutDLFileEntryType(long ddmStructureId)
		throws Exception {

		long guestUserId = _userLocalService.getGuestUserId(
			_company.getCompanyId());

		Map<Locale, String> descriptionMap = new HashMap<>();

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setScopeGroupId(_company.getGroupId());
		serviceContext.setUserId(guestUserId);

		_dlFileEntryTypeLocalService.addFileEntryType(
			null, guestUserId, _company.getGroupId(), ddmStructureId,
			DLVideoConstants.DL_FILE_ENTRY_TYPE_KEY,
			_getExternalVideoShortcutNameMap(
				LanguageUtil.getAvailableLocales()),
			descriptionMap,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_SYSTEM,
			serviceContext);
	}

	private Map<Locale, String> _getExternalVideoShortcutNameMap(
		Set<Locale> locales) {

		Map<Locale, String> nameMap = new HashMap<>();

		for (Locale locale : locales) {
			nameMap.put(
				locale, LanguageUtil.get(locale, "external-video-shortcut"));
		}

		return nameMap;
	}

	private void _updateDLFileEntryTypeNameMap(
		DLFileEntryType dlFileEntryType) {

		Map<Locale, String> nameMap = dlFileEntryType.getNameMap();

		Set<Locale> availableLocales = LanguageUtil.getAvailableLocales();

		if (nameMap.size() >= availableLocales.size()) {
			return;
		}

		dlFileEntryType.setNameMap(
			_getExternalVideoShortcutNameMap(availableLocales));

		_dlFileEntryTypeLocalService.updateDLFileEntryType(dlFileEntryType);
	}

	private Map<Locale, String> _updateDescriptionMap(
		Map<Locale, String> descriptionMap) {

		Map<Locale, String> updatedDescriptionMap = new HashMap<>();

		for (Map.Entry<Locale, String> entry : descriptionMap.entrySet()) {
			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				entry.getKey(),
				DLVideoExternalShortcutDLFileEntryTypeHelper.class);

			updatedDescriptionMap.put(
				entry.getKey(),
				LanguageUtil.get(resourceBundle, entry.getValue()));
		}

		return updatedDescriptionMap;
	}

	private Map<Locale, String> _updateNameMap(Map<Locale, String> nameMap) {
		Map<Locale, String> updatedNameMap = new HashMap<>();

		for (Map.Entry<Locale, String> entry : nameMap.entrySet()) {
			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				entry.getKey(),
				DLVideoExternalShortcutDLFileEntryTypeHelper.class);

			updatedNameMap.put(
				entry.getKey(),
				LanguageUtil.get(
					resourceBundle, "dl-video-external-shortcut-metadata"));
		}

		return updatedNameMap;
	}

	private final Company _company;
	private final DDMStructureLocalService _ddmStructureLocalService;
	private final DefaultDDMStructureHelper _defaultDDMStructureHelper;
	private final long _dlFileEntryMetadataClassNameId;
	private final DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;
	private final UserLocalService _userLocalService;

}