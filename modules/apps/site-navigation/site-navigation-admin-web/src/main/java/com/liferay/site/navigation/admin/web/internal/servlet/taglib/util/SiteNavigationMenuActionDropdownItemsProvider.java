/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.admin.web.internal.servlet.taglib.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.admin.web.internal.security.permission.resource.SiteNavigationMenuPermission;
import com.liferay.site.navigation.constants.SiteNavigationConstants;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Diego Hu
 */
public class SiteNavigationMenuActionDropdownItemsProvider {

	public SiteNavigationMenuActionDropdownItemsProvider(
		boolean hasEditPermission, LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SiteNavigationMenu primarySiteNavigationMenu,
		SiteNavigationMenu siteNavigationMenu) {

		_hasEditPermission = hasEditPermission;
		_liferayPortletResponse = liferayPortletResponse;
		_primarySiteNavigationMenu = primarySiteNavigationMenu;
		_siteNavigationMenu = siteNavigationMenu;

		_httpServletRequest = PortalUtil.getHttpServletRequest(
			liferayPortletRequest);
		_themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		boolean hasUpdatePermission =
			SiteNavigationMenuPermission.contains(
				_themeDisplay.getPermissionChecker(), _siteNavigationMenu,
				ActionKeys.UPDATE) &&
			_hasEditPermission;

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> hasUpdatePermission,
						_getEditActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> hasUpdatePermission,
						_getRenameActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							_hasEditPermission &&
							SiteNavigationMenuPermission.contains(
								_themeDisplay.getPermissionChecker(),
								_siteNavigationMenu, ActionKeys.PERMISSIONS),
						_getPermissionsActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> SiteNavigationMenuPermission.contains(
							_themeDisplay.getPermissionChecker(),
							_siteNavigationMenu, ActionKeys.DELETE),
						_getDeleteActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> hasUpdatePermission,
						_getPrimarySiteNavigationMenuActionUnsafeConsumer()
					).add(
						() -> hasUpdatePermission,
						_getSecondarySiteNavigationMenuActionUnsafeConsumer()
					).add(
						() -> hasUpdatePermission,
						_getSocialNavigationMenuActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "mark-as"));
			}
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteSiteNavigationMenu");
			dropdownItem.putData(
				"deleteSiteNavigationMenuURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/site_navigation_admin/delete_site_navigation_menu"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"siteNavigationMenuId",
					_siteNavigationMenu.getSiteNavigationMenuId()
				).buildString());
			dropdownItem.setIcon("trash");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getEditActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCPath(
					"/edit_site_navigation_menu.jsp"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"siteNavigationMenuId",
					_siteNavigationMenu.getSiteNavigationMenuId()
				).buildString());
			dropdownItem.setIcon("pencil");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "edit"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getPermissionsActionUnsafeConsumer()
		throws Exception {

		String permissionsURL = PermissionsURLTag.doTag(
			StringPool.BLANK, SiteNavigationMenu.class.getName(),
			HtmlUtil.escape(_siteNavigationMenu.getName()), null,
			String.valueOf(_siteNavigationMenu.getSiteNavigationMenuId()),
			LiferayWindowState.POP_UP.toString(), null, _httpServletRequest);

		return dropdownItem -> {
			dropdownItem.putData("action", "permissionsSiteNavigationMenu");
			dropdownItem.putData(
				"permissionsSiteNavigationMenuURL", permissionsURL);
			dropdownItem.setIcon("password-policies");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "permissions"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getPrimarySiteNavigationMenuActionUnsafeConsumer() {

		return dropdownItem -> {
			if (_siteNavigationMenu.getType() ==
					SiteNavigationConstants.TYPE_PRIMARY) {

				dropdownItem.put("symbolRight", "check");
			}

			dropdownItem.putData("action", "markAsPrimary");

			if ((_siteNavigationMenu.getType() !=
					SiteNavigationConstants.TYPE_PRIMARY) &&
				(_primarySiteNavigationMenu != null)) {

				dropdownItem.putData(
					"confirmationMessage",
					LanguageUtil.format(
						_httpServletRequest,
						"do-you-want-to-replace-x-for-x-as-the-primary-" +
							"navigation?-this-action-will-affect-all-the-" +
								"pages-using-primary-navigation",
						new String[] {
							_siteNavigationMenu.getName(),
							_primarySiteNavigationMenu.getName()
						}));
			}

			dropdownItem.putData(
				"markAsPrimaryURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/site_navigation_admin/edit_site_navigation_menu_settings"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"auto", _siteNavigationMenu.isAuto()
				).setParameter(
					"siteNavigationMenuId",
					_siteNavigationMenu.getSiteNavigationMenuId()
				).setParameter(
					"type", SiteNavigationConstants.TYPE_PRIMARY
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "primary-navigation"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getRenameActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "renameSiteNavigationMenu");
			dropdownItem.putData(
				"idFieldValue",
				String.valueOf(_siteNavigationMenu.getSiteNavigationMenuId()));
			dropdownItem.putData(
				"mainFieldValue", _siteNavigationMenu.getName());
			dropdownItem.putData(
				"renameSiteNavigationMenuURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/site_navigation_admin/update_site_navigation_menu"
				).setParameter(
					"siteNavigationMenuId",
					_siteNavigationMenu.getSiteNavigationMenuId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "rename"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getSecondarySiteNavigationMenuActionUnsafeConsumer() {

		return dropdownItem -> {
			if (_siteNavigationMenu.getType() ==
					SiteNavigationConstants.TYPE_SECONDARY) {

				dropdownItem.put("symbolRight", "check");
			}

			dropdownItem.putData("action", "markAsSecondary");
			dropdownItem.putData(
				"markAsSecondaryURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/site_navigation_admin/edit_site_navigation_menu_settings"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"auto", _siteNavigationMenu.isAuto()
				).setParameter(
					"siteNavigationMenuId",
					_siteNavigationMenu.getSiteNavigationMenuId()
				).setParameter(
					"type", SiteNavigationConstants.TYPE_SECONDARY
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "secondary-navigation"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getSocialNavigationMenuActionUnsafeConsumer() {

		return dropdownItem -> {
			if (_siteNavigationMenu.getType() ==
					SiteNavigationConstants.TYPE_SOCIAL) {

				dropdownItem.put("symbolRight", "check");
			}

			dropdownItem.putData("action", "markAsSocial");
			dropdownItem.putData(
				"markAsSocialURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/site_navigation_admin/edit_site_navigation_menu_settings"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"auto", _siteNavigationMenu.isAuto()
				).setParameter(
					"siteNavigationMenuId",
					_siteNavigationMenu.getSiteNavigationMenuId()
				).setParameter(
					"type", SiteNavigationConstants.TYPE_SOCIAL
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "social-navigation"));
		};
	}

	private final boolean _hasEditPermission;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final SiteNavigationMenu _primarySiteNavigationMenu;
	private final SiteNavigationMenu _siteNavigationMenu;
	private final ThemeDisplay _themeDisplay;

}