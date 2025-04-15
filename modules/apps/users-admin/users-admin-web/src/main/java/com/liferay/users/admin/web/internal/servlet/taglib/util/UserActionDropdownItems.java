/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.servlet.taglib.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.membershippolicy.OrganizationMembershipPolicyUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.taglib.security.DoAsURLTag;
import com.liferay.taglib.security.PermissionsURLTag;
import com.liferay.users.admin.user.action.contributor.UserActionContributor;
import com.liferay.users.admin.web.internal.display.context.UserActionDisplayContext;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class UserActionDropdownItems {

	public UserActionDropdownItems(
		RenderRequest renderRequest, RenderResponse renderResponse, User user) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_user = user;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() {
		boolean hasActivatePermission = UserPermissionUtil.contains(
			_themeDisplay.getPermissionChecker(), _user.getUserId(),
			ActionKeys.ACTIVATE);
		boolean hasDeactivatePermission = UserPermissionUtil.contains(
			_themeDisplay.getPermissionChecker(), _user.getUserId(),
			ActionKeys.DEACTIVATE);
		boolean hasDeletePermission = UserPermissionUtil.contains(
			_themeDisplay.getPermissionChecker(), _user.getUserId(),
			ActionKeys.DELETE);
		boolean hasUpdatePermission = UserPermissionUtil.contains(
			_themeDisplay.getPermissionChecker(), _user.getUserId(),
			ActionKeys.UPDATE);

		UserActionDisplayContext userActionDisplayContext =
			new UserActionDisplayContext(
				_httpServletRequest,
				PortalUtil.getLiferayPortletRequest(_renderRequest),
				_themeDisplay.getUser(), _user);

		UserActionContributor[] filteredUserActionContributors =
			userActionDisplayContext.getFilteredUserActionContributors();

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> dropdownGroupItem.setDropdownItems(
				DropdownItemListBuilder.add(
					() -> hasUpdatePermission,
					_getEditUserActionUnsafeConsumer()
				).add(
					() -> UserPermissionUtil.contains(
						_themeDisplay.getPermissionChecker(), _user.getUserId(),
						ActionKeys.PERMISSIONS),
					_getPermissionsActionUnsafeConsumer()
				).add(
					() ->
						(PrefsPropsUtil.getBoolean(
							_themeDisplay.getCompanyId(),
							PropsKeys.LAYOUT_USER_PRIVATE_LAYOUTS_ENABLED) ||
						 PrefsPropsUtil.getBoolean(
							 _themeDisplay.getCompanyId(),
							 PropsKeys.LAYOUT_USER_PUBLIC_LAYOUTS_ENABLED)) &&
						hasUpdatePermission,
					_getManagePagesActionUnsafeConsumer()
				).add(
					() ->
						!PropsValues.PORTAL_JAAS_ENABLE &&
						PropsValues.PORTAL_IMPERSONATION_ENABLE &&
						!Objects.equals(
							_user.getScreenName(), "default-service-account") &&
						(_user.getUserId() != _themeDisplay.getUserId()) &&
						_user.isActive() && !_themeDisplay.isImpersonated() &&
						UserPermissionUtil.contains(
							_themeDisplay.getPermissionChecker(),
							_user.getUserId(), ActionKeys.IMPERSONATE),
					_getImpersonateUserActionUnsafeConsumer()
				).add(
					() ->
						(hasActivatePermission || hasDeletePermission) &&
						!_user.isActive(),
					_getActivateActionUnsafeConsumer()
				).add(
					() ->
						(hasDeactivatePermission || hasDeletePermission) &&
						_user.isActive() &&
						(_user.getUserId() != _themeDisplay.getUserId()),
					_getDeactivateActionUnsafeConsumer()
				).add(
					() ->
						hasDeletePermission &&
						(_user.getUserId() != _themeDisplay.getUserId()),
					_getDeleteActionUnsafeConsumer()
				).add(
					() -> {
						Organization organization =
							(Organization)_httpServletRequest.getAttribute(
								"view.jsp-organization");

						if ((organization != null) &&
							!OrganizationMembershipPolicyUtil.
								isMembershipProtected(
									_themeDisplay.getPermissionChecker(),
									_user.getUserId(),
									organization.getOrganizationId()) &&
							!OrganizationMembershipPolicyUtil.
								isMembershipRequired(
									_user.getUserId(),
									organization.getOrganizationId())) {

							return true;
						}

						return false;
					},
					_getRemoveUserActionUnsafeConsumer()
				).build())
		).addGroup(
			() -> filteredUserActionContributors.length > 0,
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					_getUserActionContributorDropdownItems(
						filteredUserActionContributors));
				dropdownGroupItem.setSeparator(
					filteredUserActionContributors.length > 0);
			}
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getActivateActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "activateUser");
			dropdownItem.putData(
				"activateUserURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/users_admin/edit_user"
				).setCMD(
					Constants.RESTORE
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"deleteUserIds", _user.getUserId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "activate"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeactivateActionUnsafeConsumer() {

		if (_user.isActive()) {
			return dropdownItem -> {
				dropdownItem.putData("action", "deactivateUser");
				dropdownItem.putData(
					"deactivateUserURL",
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"/users_admin/edit_user"
					).setCMD(
						Constants.DEACTIVATE
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"deleteUserIds", _user.getUserId()
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "deactivate"));
			};
		}

		return null;
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteActionUnsafeConsumer() {

		if (!_user.isActive() && PropsValues.USERS_DELETE) {
			return dropdownItem -> {
				dropdownItem.putData("action", "deleteUser");
				dropdownItem.putData(
					"deleteUserURL",
					PortletURLBuilder.createActionURL(
						_renderResponse
					).setActionName(
						"/users_admin/edit_user"
					).setCMD(
						Constants.DELETE
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setParameter(
						"deleteUserIds", _user.getUserId()
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "delete"));
			};
		}

		return null;
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getEditUserActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				_renderResponse.createRenderURL(), "mvcRenderCommandName",
				"/users_admin/edit_user", "p_u_i_d", _user.getUserId(),
				"backURL", _themeDisplay.getURLCurrent());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "edit"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getImpersonateUserActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				DoAsURLTag.doTag(_user.getUserId(), _httpServletRequest));
			dropdownItem.setIcon("shortcut");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "impersonate-user"));
			dropdownItem.setTarget("_blank");
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getManagePagesActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletProviderUtil.getPortletURL(
					_httpServletRequest, _user.getGroup(),
					Layout.class.getName(), PortletProvider.Action.EDIT));
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "manage-pages"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getPermissionsActionUnsafeConsumer()
		throws Exception {

		String permissionsURL = PermissionsURLTag.doTag(
			StringPool.BLANK, User.class.getName(), _user.getFullName(), null,
			String.valueOf(_user.getUserId()),
			LiferayWindowState.POP_UP.toString(), null, _httpServletRequest);

		return dropdownItem -> {
			dropdownItem.putData("action", "permissions");
			dropdownItem.putData("permissionsURL", permissionsURL);
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "permissions"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getRemoveUserActionUnsafeConsumer() {

		Organization organization =
			(Organization)_httpServletRequest.getAttribute(
				"view.jsp-organization");

		return dropdownItem -> {
			dropdownItem.putData("action", "removeUser");
			dropdownItem.putData(
				"removeUserURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/users_admin/edit_organization_assignments"
				).setParameter(
					"assignmentsRedirect", _themeDisplay.getURLCurrent()
				).setParameter(
					"organizationId", organization.getOrganizationId()
				).setParameter(
					"removeUserIds", _user.getUserId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "remove"));
		};
	}

	private List<DropdownItem> _getUserActionContributorDropdownItems(
		UserActionContributor[] filteredUserActionContributors) {

		return new DropdownItemList() {
			{
				for (UserActionContributor userActionContributor :
						filteredUserActionContributors) {

					if (userActionContributor.isShowConfirmationMessage(
							_user)) {

						add(
							dropdownItem -> {
								dropdownItem.putData(
									"action", "deleteUserActionContributor");
								dropdownItem.putData(
									"confirmation",
									userActionContributor.
										getConfirmationMessage(_renderRequest));
								dropdownItem.putData(
									"deleteUserActionContributorURL",
									userActionContributor.getURL(
										_renderRequest, _renderResponse,
										_themeDisplay.getUser(), _user));
								dropdownItem.setLabel(
									userActionContributor.getMessage(
										_renderRequest));
							});
					}
					else {
						add(
							dropdownItem -> {
								dropdownItem.setHref(
									userActionContributor.getURL(
										_renderRequest, _renderResponse,
										_themeDisplay.getUser(), _user));
								dropdownItem.setLabel(
									userActionContributor.getMessage(
										_renderRequest));
							});
					}
				}
			}
		};
	}

	private final HttpServletRequest _httpServletRequest;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;
	private final User _user;

}