/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.site.cms.site.initializer.internal.configuration.CMSSiteInitializerConfiguration;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Jürgen Kappler
 */
public class AllSectionDisplayContext extends BaseSectionDisplayContext {

	public AllSectionDisplayContext(
		CMSSiteInitializerConfiguration cmsSiteInitializerConfiguration,
		HttpServletRequest httpServletRequest) {

		super(cmsSiteInitializerConfiguration, httpServletRequest);
	}

	@Override
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setIcon("forms");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "basic-content"));
			}
		).addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setIcon("upload");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "single-file"));
			}
		).addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setIcon("upload-multiple");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "multiple-files"));
			}
		).addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setIcon("blogs");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "blog"));
			}
		).addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setIcon("wiki");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "knowledge-base"));
			}
		).addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setIcon("video");
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, "external-video-shortcut"));
			}
		).build();
	}

	@Override
	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(
				httpServletRequest, "click-new-to-create-your-first-asset")
		).put(
			"image", "/states/cms_empty_state.svg"
		).put(
			"title", LanguageUtil.get(httpServletRequest, "no-assets-yet")
		).build();
	}

}