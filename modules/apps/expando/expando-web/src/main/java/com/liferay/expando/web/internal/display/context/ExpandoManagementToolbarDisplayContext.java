/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.web.internal.display.context;

import com.liferay.expando.constants.ExpandoPortletKeys;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class ExpandoManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public ExpandoManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<?> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);

		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteCustomFields");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	@Override
	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"deleteExpandosURL",
			() -> PortletURLBuilder.createActionURL(
				_liferayPortletResponse
			).setActionName(
				"deleteExpandos"
			).buildString()
		).build();
	}

	@Override
	public CreationMenu getCreationMenu() {
		try {
			if (!PortletPermissionUtil.contains(
					_themeDisplay.getPermissionChecker(),
					ExpandoPortletKeys.EXPANDO, ActionKeys.ADD_EXPANDO)) {

				return null;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}

		return CreationMenuBuilder.addDropdownItem(
			dropdownItem -> {
				String modelResource = ParamUtil.getString(
					_httpServletRequest, "modelResource");

				dropdownItem.setHref(
					_liferayPortletResponse.createRenderURL(), "mvcPath",
					"/edit/select_field_type.jsp", "redirect", currentURLObj,
					"modelResource", modelResource, "backTitle",
					ResourceActionsUtil.getModelResource(
						_httpServletRequest, modelResource));

				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "add-custom-field"));
			}
		).build();
	}

	@Override
	public String getSearchContainerId() {
		return "customFields";
	}

	@Override
	public String getSortingURL() {
		return null;
	}

	@Override
	public Boolean isShowSearch() {
		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExpandoManagementToolbarDisplayContext.class);

	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ThemeDisplay _themeDisplay;

}