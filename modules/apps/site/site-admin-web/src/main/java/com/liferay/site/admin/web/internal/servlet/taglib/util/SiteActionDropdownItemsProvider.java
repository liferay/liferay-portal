/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.servlet.taglib.util;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.membershippolicy.SiteMembershipPolicyUtil;
import com.liferay.site.admin.web.internal.constants.SiteAdminPortletKeys;
import com.liferay.site.admin.web.internal.display.context.SiteAdminDisplayContext;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class SiteActionDropdownItemsProvider {

	public SiteActionDropdownItemsProvider(
		Group group, LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SiteAdminDisplayContext siteAdminDisplayContext) {

		_group = group;
		_liferayPortletResponse = liferayPortletResponse;
		_siteAdminDisplayContext = siteAdminDisplayContext;

		_httpServletRequest = PortalUtil.getHttpServletRequest(
			liferayPortletRequest);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		int count = GroupLocalServiceUtil.getGroupsCount(
			_themeDisplay.getCompanyId(), _group.getGroupId(), true);
		boolean hasUpdatePermission = GroupPermissionUtil.contains(
			_themeDisplay.getPermissionChecker(), _group, ActionKeys.UPDATE);

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							hasUpdatePermission && !_group.isActive() &&
							!_group.isCompany(),
						_getActivateSiteActionUnsafeConsumer()
					).add(
						() ->
							hasUpdatePermission && _group.isActive() &&
							!_group.isCompany() && !_group.isGuest(),
						_getDeactivateSiteActionUnsafeConsumer()
					).add(
						() -> _hasEditAssignmentsPermission(),
						_getLeaveSiteActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							hasUpdatePermission &&
							_siteAdminDisplayContext.hasAddChildSitePermission(
								_group),
						_getAddChildSiteActionUnsafeConsumer()
					).add(
						() -> hasUpdatePermission && (count > 0),
						_getViewChildSitesActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							_group.isActive() &&
							(_group.getPrivateLayoutsPageCount() > 0),
						_getViewSitePrivatePagesActionUnsafeConsumer()
					).add(
						() ->
							_group.isActive() &&
							(_group.getPublicLayoutsPageCount() > 0),
						_getViewSitePublicPagesActionUnsafeConsumer()
					).add(
						() -> hasUpdatePermission,
						_getViewSiteSettingsActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> _hasDeleteGroupPermission(),
						_getDeleteSiteActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getActivateSiteActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "activateSite");
			dropdownItem.putData(
				"activateSiteURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/site_admin/activate_group"
				).setRedirect(
					_getRedirect()
				).setParameter(
					"groupId", _group.getGroupId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "activate"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getAddChildSiteActionUnsafeConsumer() {

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		return dropdownItem -> {
			String backURLTitle = portletDisplay.getPortletDisplayName();

			if (_group != null) {
				backURLTitle = _group.getDescriptiveName(
					_themeDisplay.getLocale());
			}

			dropdownItem.setHref(
				_liferayPortletResponse.createRenderURL(), "backURLTitle",
				backURLTitle, "mvcRenderCommandName",
				"/site_admin/select_site_initializer", "redirect",
				_themeDisplay.getURLCurrent(), "parentGroupId",
				String.valueOf(_group.getGroupId()));
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "add-child-site"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeactivateSiteActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deactivateSite");
			dropdownItem.putData(
				"deactivateSiteURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/site_admin/deactivate_group"
				).setRedirect(
					_getRedirect()
				).setParameter(
					"groupId", _group.getGroupId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "deactivate"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteSiteActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "deleteSite");
			dropdownItem.putData(
				"deleteSiteURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/site_admin/delete_groups"
				).setRedirect(
					() -> {
						if (_themeDisplay.getScopeGroupId() ==
								_group.getGroupId()) {

							PortletURL redirectURL =
								PortalUtil.getControlPanelPortletURL(
									_httpServletRequest,
									GroupLocalServiceUtil.fetchCompanyGroup(
										_themeDisplay.getCompanyId()),
									SiteAdminPortletKeys.SITE_ADMIN, 0, 0,
									PortletRequest.RENDER_PHASE);

							return redirectURL.toString();
						}

						return _getRedirect();
					}
				).setParameter(
					"groupId", _group.getGroupId()
				).buildString());
			dropdownItem.setIcon("trash");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getLeaveSiteActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "leaveSite");
			dropdownItem.putData(
				"leaveSiteURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/site_admin/edit_group_assignments"
				).setRedirect(
					_getRedirect()
				).setParameter(
					"groupId", _group.getGroupId()
				).setParameter(
					"removeUserIds", _themeDisplay.getUserId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "leave-site"));
		};
	}

	private String _getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(
			_httpServletRequest, "redirect", _themeDisplay.getURLCurrent());

		return _redirect;
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getViewChildSitesActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				_liferayPortletResponse.createRenderURL(), "backURL",
				_getRedirect(), "groupId", String.valueOf(_group.getGroupId()));
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "view-child-sites"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getViewSitePrivatePagesActionUnsafeConsumer() {

		return dropdownItem -> {
			String href = _group.getDisplayURL(_themeDisplay, true, true);

			if (Validator.isNull(href)) {
				dropdownItem.setHref(
					PortalUtil.getControlPanelPortletURL(
						_httpServletRequest, _group,
						LayoutAdminPortletKeys.GROUP_PAGES, 0, 0,
						PortletRequest.RENDER_PHASE),
					"privateLayout", Boolean.TRUE.toString());
			}
			else {
				dropdownItem.setHref(href);
			}

			dropdownItem.setIcon("shortcut");
			dropdownItem.setLabel(
				LanguageUtil.format(
					_httpServletRequest, "go-to-x",
					_group.getLayoutRootNodeName(
						true, _themeDisplay.getLocale())));
			dropdownItem.setTarget("_blank");
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getViewSitePublicPagesActionUnsafeConsumer() {

		return dropdownItem -> {
			String href = _group.getDisplayURL(_themeDisplay, false, true);

			if (Validator.isNull(href)) {
				href = String.valueOf(
					PortalUtil.getControlPanelPortletURL(
						_httpServletRequest, _group,
						LayoutAdminPortletKeys.GROUP_PAGES, 0, 0,
						PortletRequest.RENDER_PHASE));
			}

			dropdownItem.setHref(href);

			dropdownItem.setIcon("shortcut");
			dropdownItem.setLabel(
				LanguageUtil.format(
					_httpServletRequest, "go-to-x",
					_group.getLayoutRootNodeName(
						false, _themeDisplay.getLocale())));
			dropdownItem.setTarget("_blank");
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getViewSiteSettingsActionUnsafeConsumer() {

		PortletURL viewSiteSettingsURL = PortalUtil.getControlPanelPortletURL(
			_httpServletRequest, _group,
			ConfigurationAdminPortletKeys.SITE_SETTINGS, 0, 0,
			PortletRequest.RENDER_PHASE);

		return dropdownItem -> {
			dropdownItem.setHref(viewSiteSettingsURL);
			dropdownItem.setIcon("shortcut");
			dropdownItem.setLabel(
				LanguageUtil.format(
					_httpServletRequest, "go-to-x", "site-settings"));
			dropdownItem.setTarget("_blank");
		};
	}

	private boolean _hasDeleteGroupPermission() throws PortalException {
		if (_group.isCompany() ||
			!GroupPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(), _group,
				ActionKeys.DELETE) ||
			PortalUtil.isSystemGroup(_group.getGroupKey())) {

			return false;
		}

		return true;
	}

	private boolean _hasEditAssignmentsPermission() throws Exception {
		if (_group.isCompany()) {
			return false;
		}

		List<Organization> organizations =
			OrganizationLocalServiceUtil.getGroupUserOrganizations(
				_group.getGroupId(), _themeDisplay.getUserId());

		if (!organizations.isEmpty()) {
			return false;
		}

		List<UserGroup> userGroups =
			UserGroupLocalServiceUtil.getGroupUserUserGroups(
				_group.getGroupId(), _themeDisplay.getUserId());

		if (!userGroups.isEmpty() ||
			((_group.getType() != GroupConstants.TYPE_SITE_OPEN) &&
			 (_group.getType() != GroupConstants.TYPE_SITE_RESTRICTED))) {

			return false;
		}

		if (!GroupLocalServiceUtil.hasUserGroup(
				_themeDisplay.getUserId(), _group.getGroupId()) ||
			SiteMembershipPolicyUtil.isMembershipRequired(
				_themeDisplay.getUserId(), _group.getGroupId())) {

			return false;
		}

		return true;
	}

	private final Group _group;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _redirect;
	private final SiteAdminDisplayContext _siteAdminDisplayContext;
	private final ThemeDisplay _themeDisplay;

}