/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.address.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemList;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Pei-Jung Lan
 */
public class CountriesManagementAdminManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public CountriesManagementAdminManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<Country> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemList.of(
			() -> {
				if (Objects.equals(getNavigation(), "inactive")) {
					return null;
				}

				return DropdownItemBuilder.putData(
					"action", "deactivateCountries"
				).putData(
					"deactivateCountriesURL",
					PortletURLBuilder.createActionURL(
						liferayPortletResponse
					).setActionName(
						"/address/update_country_status"
					).setCMD(
						Constants.DEACTIVATE
					).setNavigation(
						getNavigation()
					).buildString()
				).setIcon(
					"hidden"
				).setLabel(
					LanguageUtil.get(httpServletRequest, "deactivate")
				).setQuickAction(
					true
				).build();
			},
			() -> {
				if (Objects.equals(getNavigation(), "active")) {
					return null;
				}

				return DropdownItemBuilder.putData(
					"action", "activateCountries"
				).putData(
					"activateCountriesURL",
					PortletURLBuilder.createActionURL(
						liferayPortletResponse
					).setActionName(
						"/address/update_country_status"
					).setCMD(
						Constants.RESTORE
					).setNavigation(
						getNavigation()
					).buildString()
				).setIcon(
					"undo"
				).setLabel(
					LanguageUtil.get(httpServletRequest, "activate")
				).setQuickAction(
					true
				).build();
			},
			() -> {
				if (Objects.equals(getNavigation(), "active")) {
					return null;
				}

				return DropdownItemBuilder.putData(
					"action", "deleteCountries"
				).putData(
					"deleteCountriesURL",
					PortletURLBuilder.createActionURL(
						liferayPortletResponse
					).setActionName(
						"/address/delete_country"
					).setNavigation(
						getNavigation()
					).buildString()
				).setIcon(
					"times-circle"
				).setLabel(
					LanguageUtil.get(httpServletRequest, "delete")
				).setQuickAction(
					true
				).build();
			});
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).setNavigation(
			(String)null
		).buildString();
	}

	@Override
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					liferayPortletResponse.createRenderURL(),
					"mvcRenderCommandName", "/address/edit_country", "backURL",
					currentURLObj.toString());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "add-country"));
			}
		).build();
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		return new LabelItemList() {
			{
				if (!Objects.equals(getNavigation(), "all")) {
					add(
						labelItem -> {
							labelItem.putData(
								"removeLabelURL",
								PortletURLBuilder.create(
									getPortletURL()
								).setNavigation(
									(String)null
								).buildString());
							labelItem.setCloseable(true);
							labelItem.setLabel(
								String.format(
									"%s: %s",
									LanguageUtil.get(
										httpServletRequest, "status"),
									LanguageUtil.get(
										httpServletRequest, getNavigation())));
						});
				}
			}
		};
	}

	@Override
	public PortletURL getPortletURL() {
		try {
			return PortletURLUtil.clone(currentURLObj, liferayPortletResponse);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return liferayPortletResponse.createRenderURL();
		}
	}

	@Override
	public Boolean isShowCreationMenu() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return PortalPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), ActionKeys.MANAGE_COUNTRIES);
	}

	@Override
	protected String getNavigation() {
		return ParamUtil.getString(
			liferayPortletRequest, getNavigationParam(), "all");
	}

	@Override
	protected String[] getNavigationKeys() {
		return new String[] {"all", "active", "inactive"};
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"name", "priority"};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CountriesManagementAdminManagementToolbarDisplayContext.class);

}