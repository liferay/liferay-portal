/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Sam Ziemer
 */
public class FilesSectionDisplayContext extends BaseSectionDisplayContext {

	public FilesSectionDisplayContext(
		DepotEntryLocalService depotEntryLocalService,
		GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, Language language,
		ObjectDefinitionService objectDefinitionService,
		ObjectDefinitionSettingLocalService
			objectDefinitionSettingLocalService) {

		super(
			depotEntryLocalService, groupLocalService, httpServletRequest,
			language, objectDefinitionService,
			objectDefinitionSettingLocalService);

		_depotEntryLocalService = depotEntryLocalService;
	}

	@Override
	public CreationMenu getCreationMenu() {
		return new CreationMenu() {
			{
				addPrimaryDropdownItem(
					dropdownItem -> {
						dropdownItem.putData("action", "createFolder");
						dropdownItem.putData(
							"assetLibraries",
							getDepotEntriesJSONArray(
								_depotEntryLocalService.getDepotEntries(
									QueryUtil.ALL_POS, QueryUtil.ALL_POS)));
						dropdownItem.setIcon("folder");
						dropdownItem.setLabel(
							language.get(httpServletRequest, "folder"));
					});

				addStructureContentDropdownItems(this);
			}
		};
	}

	@Override
	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(
				httpServletRequest, "click-new-to-create-your-first-file")
		).put(
			"image", "/states/cms_empty_state_files.svg"
		).put(
			"title", LanguageUtil.get(httpServletRequest, "no-files-yet")
		).build();
	}

	@Override
	public String[] getObjectFolderExternalReferenceCodes() {
		return new String[] {
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES
		};
	}

	@Override
	protected String getCMSSectionFilterString() {
		return "cmsSection eq 'files'";
	}

	private final DepotEntryLocalService _depotEntryLocalService;

}