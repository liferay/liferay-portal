/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.site.cms.site.initializer.internal.configuration.CMSSiteInitializerConfiguration;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Sam Ziemer
 */
public class FilesSectionDisplayContext extends BaseSectionDisplayContext {

	public FilesSectionDisplayContext(
		CMSSiteInitializerConfiguration cmsSiteInitializerConfiguration,
		HttpServletRequest httpServletRequest, Language language,
		ObjectDefinitionService objectDefinitionService) {

		super(cmsSiteInitializerConfiguration, httpServletRequest);

		_language = language;
		_objectDefinitionService = objectDefinitionService;
	}

	@Override
	public List<DropdownItem> getBulkActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				"#", "document", "sampleBulkAction",
				LanguageUtil.get(httpServletRequest, "label"), null, null,
				null));
	}

	@Override
	public CreationMenu getCreationMenu() {
		return new CreationMenu() {
			{
				addPrimaryDropdownItem(
					dropdownItem -> {
						dropdownItem.putData("action", "createFolder");
						dropdownItem.setIcon("folder");
						dropdownItem.setLabel(
							_language.get(httpServletRequest, "folder"));
					});

				for (ObjectDefinition objectDefinition :
						_objectDefinitionService.getCMSObjectDefinitions(
							themeDisplay.getCompanyId(),
							getObjectDefinitionFolderExternalReferenceCodes())) {

					addPrimaryDropdownItem(
						dropdownItem -> {
							dropdownItem.setHref(
								getAddStructuredContentItemURL(
									objectDefinition.getObjectDefinitionId()));
							dropdownItem.setIcon("forms");
							dropdownItem.setLabel(
								objectDefinition.getLabel(
									themeDisplay.getLocale()));
						});
				}
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
	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				_language.get(
					httpServletRequest,
					"are-you-sure-you-want-to-delete-this-entry"),
				null, "trash", "delete",
				_language.get(httpServletRequest, "delete"), "delete", "delete",
				"headless"));
	}

	@Override
	public String[] getObjectDefinitionFolderExternalReferenceCodes() {
		return cmsSiteInitializerConfiguration.
			filesObjectDefinitionFolderExternalReferenceCodes();
	}

	@Override
	protected String getCMSSectionFilterString() {
		return "cmsSection eq 'files'";
	}

	private final Language _language;
	private final ObjectDefinitionService _objectDefinitionService;

}