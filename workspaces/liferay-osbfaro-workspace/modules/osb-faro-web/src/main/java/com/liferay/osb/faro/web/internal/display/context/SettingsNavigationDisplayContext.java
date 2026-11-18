/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.service.FaroProjectLocalServiceUtil;
import com.liferay.osb.faro.service.FaroUserLocalServiceUtil;
import com.liferay.osb.faro.web.internal.util.FaroNavigationUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class SettingsNavigationDisplayContext {

	public SettingsNavigationDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public NavigationItemList getNavigationItems() throws Exception {
		Layout layout = _themeDisplay.getLayout();

		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.put(
					"navigationItems",
					NavigationItemListBuilder.add(
						() -> {
							FaroUser faroUser =
								FaroUserLocalServiceUtil.fetchFaroUser(
									_themeDisplay.getScopeGroupId(),
									_themeDisplay.getUserId());

							if (faroUser == null) {
								return false;
							}

							Role role = RoleLocalServiceUtil.fetchRole(
								faroUser.getRoleId());

							if ((role != null) &&
								(Objects.equals(
									role.getName(),
									RoleConstants.SITE_ADMINISTRATOR) ||
								 Objects.equals(
									 role.getName(),
									 RoleConstants.SITE_OWNER))) {

								return true;
							}

							return false;
						},
						childNavigationItem -> {
							childNavigationItem.put("icon", "ac_api");
							childNavigationItem.setActive(
								Objects.equals(
									layout.getFriendlyURL(), "/tokens"));
							childNavigationItem.setHref(
								FaroNavigationUtil.fetchLayoutURL(
									"/tokens", _themeDisplay));
							childNavigationItem.setLabel(
								LanguageUtil.get(_httpServletRequest, "apis"));
						}
					).add(
						childNavigationItem -> {
							childNavigationItem.put("icon", "definitions");
							childNavigationItem.setActive(
								ArrayUtil.contains(
									new String[] {
										"/behaviors", "/definitions",
										"/definitions-search",
										"/event-attribute", "/event-attributes",
										"/event-definition",
										"/event-definitions",
										"/individual-attributes",
										"/interest-topics"
									},
									layout.getFriendlyURL()));
							childNavigationItem.setHref(
								FaroNavigationUtil.fetchLayoutURL(
									"/definitions", _themeDisplay));
							childNavigationItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "definitions"));
						}
					).add(
						childNavigationItem -> {
							childNavigationItem.put(
								"icon", "data_privacy_lock");
							childNavigationItem.setActive(
								Objects.equals(
									layout.getFriendlyURL(), "/data-privacy"));
							childNavigationItem.setHref(
								FaroNavigationUtil.fetchLayoutURL(
									"/data-privacy", _themeDisplay));
							childNavigationItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest,
									"data-control-&-privacy"));
						}
					).add(
						childNavigationItem -> {
							childNavigationItem.put("icon", "faro_data_source");
							childNavigationItem.setActive(
								ArrayUtil.contains(
									new String[] {
										"/configure-csv", "/data-source",
										"/data-source-onboarding",
										"/data-sources", "/delete-data-source",
										"/edit-data-source"
									},
									layout.getFriendlyURL()));
							childNavigationItem.setHref(
								FaroNavigationUtil.fetchLayoutURL(
									"/data-sources", _themeDisplay));
							childNavigationItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "data-sources"));
						}
					).add(
						() -> {
							FaroProject faroProject =
								FaroProjectLocalServiceUtil.
									fetchFaroProjectByGroupId(
										_themeDisplay.getScopeGroupId());

							if ((faroProject != null) &&
								faroProject.isRecommendationsEnabled()) {

								return true;
							}

							return false;
						},
						childNavigationItem -> {
							childNavigationItem.put("icon", "ac_star");
							childNavigationItem.setActive(
								ArrayUtil.contains(
									new String[] {
										"/edit-recommendation",
										"/recommendation", "/recommendations"
									},
									layout.getFriendlyURL()));
							childNavigationItem.setHref(
								FaroNavigationUtil.fetchLayoutURL(
									"/recommendations", _themeDisplay));
							childNavigationItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "recommendations"));
						}
					).build());
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "workspace-data"));
			}
		).add(
			navigationItem -> {
				navigationItem.put(
					"navigationItems",
					NavigationItemListBuilder.add(
						childNavigationItem -> {
							childNavigationItem.put("icon", "user_management");
							childNavigationItem.setActive(
								ArrayUtil.contains(
									new String[] {"/access-requests", "/users"},
									layout.getFriendlyURL()));
							childNavigationItem.setHref(
								FaroNavigationUtil.fetchLayoutURL(
									"/users", _themeDisplay));
							childNavigationItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "user-management"));
						}
					).add(
						childNavigationItem -> {
							childNavigationItem.put("icon", "ac_page");
							childNavigationItem.setActive(
								ArrayUtil.contains(
									new String[] {"/properties", "/property"},
									layout.getFriendlyURL()));
							childNavigationItem.setHref(
								FaroNavigationUtil.fetchLayoutURL(
									"/properties", _themeDisplay));
							childNavigationItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "properties"));
						}
					).add(
						childNavigationItem -> {
							childNavigationItem.put("icon", "usage");
							childNavigationItem.setActive(
								Objects.equals(
									layout.getFriendlyURL(), "/usage"));
							childNavigationItem.setHref(
								FaroNavigationUtil.fetchLayoutURL(
									"/usage", _themeDisplay));
							childNavigationItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest,
									"subscription-&-usage"));
						}
					).add(
						childNavigationItem -> {
							childNavigationItem.put("icon", "cog");
							childNavigationItem.setActive(
								Objects.equals(
									layout.getFriendlyURL(),
									"/workspace-settings"));
							childNavigationItem.setHref(
								FaroNavigationUtil.fetchLayoutURL(
									"/workspace-settings", _themeDisplay));
							childNavigationItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "workspace"));
						}
					).add(
						childNavigationItem -> {
							childNavigationItem.put("icon", "flag-full");
							childNavigationItem.putData("developerMode", true);
							childNavigationItem.setActive(
								Objects.equals(
									layout.getFriendlyURL(), "/feature-flags"));
							childNavigationItem.setHref(
								FaroNavigationUtil.fetchLayoutURL(
									"/feature-flags", _themeDisplay));
							childNavigationItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "feature-flags"));
						}
					).build());
				navigationItem.setLabel(
					LanguageUtil.get(
						_httpServletRequest, "workspace-settings"));
			}
		).build();
	}

	private final HttpServletRequest _httpServletRequest;
	private final ThemeDisplay _themeDisplay;

}