/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class SiteAdminManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public SiteAdminManagementToolbarDisplayContext(
			HttpServletRequest httpServletRequest,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			SiteAdminDisplayContext siteAdminDisplayContext)
		throws PortalException {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			siteAdminDisplayContext.getGroupSearch());

		_siteAdminDisplayContext = siteAdminDisplayContext;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteSites");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public String getAvailableActions(Group group) throws PortalException {
		if (_hasDeleteGroupPermission(group)) {
			return "deleteSites";
		}

		return StringPool.BLANK;
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).setParameter(
			"orderByCol", getOrderByCol()
		).setParameter(
			"orderByType", getOrderByType()
		).buildString();
	}

	@Override
	public String getComponentId() {
		return "siteAdminWebManagementToolbar";
	}

	@Override
	public CreationMenu getCreationMenu() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!PortalPermissionUtil.contains(
				themeDisplay.getPermissionChecker(),
				ActionKeys.ADD_COMMUNITY)) {

			return null;
		}

		try {
			return CreationMenuBuilder.addPrimaryDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(
						PortletURLBuilder.createRenderURL(
							liferayPortletResponse
						).setMVCRenderCommandName(
							"/site_admin/select_site_initializer"
						).setRedirect(
							themeDisplay.getURLCurrent()
						).setParameter(
							"backURLTitle",
							() -> {
								Group group =
									_siteAdminDisplayContext.getGroup();

								if (group != null) {
									return group.getDescriptiveName(
										themeDisplay.getLocale());
								}

								PortletDisplay portletDisplay =
									themeDisplay.getPortletDisplay();

								return portletDisplay.getPortletDisplayName();
							}
						).setParameter(
							"parentGroupId",
							() -> {
								Group group =
									_siteAdminDisplayContext.getGroup();

								if ((group != null) &&
									_siteAdminDisplayContext.
										hasAddChildSitePermission(group)) {

									return group.getGroupId();
								}

								return null;
							}
						).buildString());
					dropdownItem.setLabel(
						LanguageUtil.get(httpServletRequest, "add-site"));
				}
			).build();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	@Override
	public String getDefaultEventHandler() {
		return "sitesManagementToolbarDefaultEventHandler";
	}

	@Override
	public String getInfoPanelId() {
		return "infoPanelId";
	}

	@Override
	public String getSearchActionURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"orderByCol", getOrderByCol()
		).setParameter(
			"orderByType", getOrderByType()
		).buildString();
	}

	@Override
	public String getSearchContainerId() {
		return "sites";
	}

	@Override
	public Boolean isShowCreationMenu() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return PortalPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), ActionKeys.ADD_COMMUNITY);
	}

	@Override
	protected String getDisplayStyle() {
		return _siteAdminDisplayContext.getDisplayStyle();
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"list", "descriptive", "icon"};
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"descriptive-name"};
	}

	private boolean _hasDeleteGroupPermission(Group group)
		throws PortalException {

		if (group.isCompany()) {
			return false;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!GroupPermissionUtil.contains(
				themeDisplay.getPermissionChecker(), group,
				ActionKeys.DELETE) ||
			PortalUtil.isSystemGroup(group.getGroupKey())) {

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SiteAdminManagementToolbarDisplayContext.class);

	private final SiteAdminDisplayContext _siteAdminDisplayContext;

}