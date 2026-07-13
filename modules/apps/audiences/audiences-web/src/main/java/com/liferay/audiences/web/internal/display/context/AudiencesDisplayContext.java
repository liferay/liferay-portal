/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.web.internal.display.context;

import com.liferay.audiences.constants.AudiencesActionKeys;
import com.liferay.audiences.web.internal.security.permission.resource.AudiencesResourcePermission;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class AudiencesDisplayContext {

	public AudiencesDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public CreationMenu getCreationMenu() {
		if (!AudiencesResourcePermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(),
				AudiencesActionKeys.MANAGE_AUDIENCES_ENTRIES)) {

			return null;
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					_renderResponse.createRenderURL(), "mvcRenderCommandName",
					"/audiences/edit_audiences_entry");
				dropdownItem.setLabel(
					LanguageUtil.get(_themeDisplay.getLocale(), "new"));
			}
		).build();
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(
				_themeDisplay.getLocale(),
				"click-new-to-create-your-first-audience")
		).put(
			"image", "/states/empty_state.svg"
		).put(
			"title",
			LanguageUtil.get(_themeDisplay.getLocale(), "no-audiences-yet")
		).build();
	}

	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}