/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.memberships.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class SelectRoleVerticalCard implements VerticalCard {

	public SelectRoleVerticalCard(Role role, RenderRequest renderRequest) {
		_role = role;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
	}

	@Override
	public String getCssClass() {
		return "card-interactive card-interactive-secondary selector-button";
	}

	@Override
	public Map<String, String> getDynamicAttributes() {
		return HashMapBuilder.put(
			"data-id", String.valueOf(_role.getRoleId())
		).build();
	}

	@Override
	public String getIcon() {
		return "users";
	}

	@Override
	public String getSubtitle() {
		return LanguageUtil.get(_httpServletRequest, _role.getTypeLabel());
	}

	@Override
	public String getTitle() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _role.getTitle(themeDisplay.getLocale());
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	private final HttpServletRequest _httpServletRequest;
	private final Role _role;

}