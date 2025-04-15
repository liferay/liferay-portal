/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.servlet.taglib.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.users.admin.item.selector.UserOrganizationItemSelectorCriterion;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class OrganizationActionDropdownItems {

	public OrganizationActionDropdownItems(
		Organization organization, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_organization = organization;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		_organizationGroup = organization.getGroup();

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() throws PortalException {
		PermissionChecker permissionChecker =
			_themeDisplay.getPermissionChecker();

		boolean hasUpdatePermission = OrganizationPermissionUtil.contains(
			permissionChecker, _organization, ActionKeys.UPDATE);

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> dropdownGroupItem.setDropdownItems(
				DropdownItemListBuilder.add(
					() -> hasUpdatePermission,
					_getEditOrganizationActionUnsafeConsumer()
				).add(
					() ->
						_organizationGroup.isSite() &&
						(GroupPermissionUtil.contains(
							permissionChecker, _organizationGroup,
							ActionKeys.MANAGE_STAGING) ||
						 hasUpdatePermission),
					_getManageSiteActionUnsafeConsumer()
				).add(
					() ->
						permissionChecker.isGroupOwner(
							_organizationGroup.getGroupId()) ||
						OrganizationPermissionUtil.contains(
							permissionChecker, _organization,
							ActionKeys.ASSIGN_USER_ROLES),
					_getAssignOrganizationRolesActionUnsafeConsumer()
				).add(
					() -> OrganizationPermissionUtil.contains(
						permissionChecker, _organization,
						ActionKeys.ASSIGN_MEMBERS),
					_getAssignUsersActionUnsafeConsumer()
				).add(
					() -> OrganizationPermissionUtil.contains(
						permissionChecker, _organization,
						ActionKeys.MANAGE_USERS),
					_getAddUserActionUnsafeConsumer()
				).addAll(
					_getAddChildrenTypesDropdownItems()
				).add(
					() -> OrganizationPermissionUtil.contains(
						permissionChecker, _organization, ActionKeys.DELETE),
					_getDeleteActionUnsafeConsumer()
				).add(
					() -> {
						long parentOrganizationId = GetterUtil.getLong(
							_httpServletRequest.getAttribute(
								"view.jsp-organizationId"));

						if ((parentOrganizationId > 0) && hasUpdatePermission) {
							return true;
						}

						return false;
					},
					_getRemoveOrganizationActionUnsafeConsumer()
				).build())
		).build();
	}

	private List<DropdownItem> _getAddChildrenTypesDropdownItems()
		throws PortalException {

		if (!_organization.isParentable() ||
			!OrganizationPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(), _organization,
				ActionKeys.ADD_ORGANIZATION)) {

			return Collections.emptyList();
		}

		return new DropdownItemList() {
			{
				for (String childrenType : _organization.getChildrenTypes()) {
					add(
						dropdownItem -> {
							dropdownItem.setHref(
								_renderResponse.createRenderURL(),
								"mvcRenderCommandName",
								"/users_admin/edit_organization",
								"parentOrganizationId",
								_organization.getOrganizationId(), "backURL",
								_themeDisplay.getURLCurrent(), "type",
								childrenType);
							dropdownItem.setLabel(
								LanguageUtil.format(
									_httpServletRequest, "add-x",
									childrenType));
						});
				}
			}
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getAddUserActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				_renderResponse.createRenderURL(), "mvcRenderCommandName",
				"/users_admin/edit_user",
				"organizationsSearchContainerPrimaryKeys",
				_organization.getOrganizationId(), "backURL",
				_themeDisplay.getURLCurrent());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "add-user"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getAssignOrganizationRolesActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "assignOrganizationRoles");
			dropdownItem.putData(
				"assignOrganizationRolesURL",
				PortletURLBuilder.create(
					PortletProviderUtil.getPortletURL(
						_httpServletRequest, UserGroupRole.class.getName(),
						PortletProvider.Action.EDIT)
				).setParameter(
					"className", User.class.getName()
				).setParameter(
					"groupId", _organizationGroup.getGroupId()
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString());
			dropdownItem.putData(
				"label",
				LanguageUtil.get(
					_httpServletRequest, "assign-organization-roles"));
			dropdownItem.setLabel(
				LanguageUtil.get(
					_httpServletRequest, "assign-organization-roles"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getAssignUsersActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "assignUsers");
			dropdownItem.putData(
				"basePortletURL",
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).buildString());
			dropdownItem.putData(
				"organizationId",
				String.valueOf(_organization.getOrganizationId()));
			dropdownItem.putData("selectUsersURL", _getSelectUsersURL());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "assign-users"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteActionUnsafeConsumer() {

		String getActiveUsersURL = ResourceURLBuilder.createResourceURL(
			_renderResponse
		).setParameter(
			"className", Organization.class.getName()
		).setParameter(
			"status", String.valueOf(WorkflowConstants.STATUS_APPROVED)
		).setResourceID(
			"/users_admin/get_users_count"
		).buildString();
		String getInactiveUsersURL = ResourceURLBuilder.createResourceURL(
			_renderResponse
		).setParameter(
			"className", Organization.class.getName()
		).setParameter(
			"status", String.valueOf(WorkflowConstants.STATUS_INACTIVE)
		).setResourceID(
			"/users_admin/get_users_count"
		).buildString();

		return dropdownItem -> {
			dropdownItem.putData(Constants.CMD, Constants.DELETE);
			dropdownItem.putData("action", "deleteOrganization");
			dropdownItem.putData(
				"deleteOrganizationURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/users_admin/edit_organization"
				).setCMD(
					Constants.DELETE
				).buildString());
			dropdownItem.putData("getActiveUsersURL", getActiveUsersURL);
			dropdownItem.putData("getInactiveUsersURL", getInactiveUsersURL);
			dropdownItem.putData(
				"organizationId",
				String.valueOf(_organization.getOrganizationId()));
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getEditOrganizationActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				_renderResponse.createRenderURL(), "mvcRenderCommandName",
				"/users_admin/edit_organization", "organizationId",
				_organization.getOrganizationId(), "backURL",
				_themeDisplay.getURLCurrent());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "edit"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getManageSiteActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				PortletProviderUtil.getPortletURL(
					_httpServletRequest, _organizationGroup,
					Group.class.getName(), PortletProvider.Action.EDIT),
				"viewOrganizationsRedirect", _themeDisplay.getURLCurrent());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "manage-site"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getRemoveOrganizationActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "removeOrganization");
			dropdownItem.putData(
				"removeOrganizationURL",
				PortletURLBuilder.create(
					_renderResponse.createActionURL()
				).setActionName(
					"/users_admin/edit_organization_assignments"
				).setParameter(
					"assignmentsRedirect", _themeDisplay.getURLCurrent()
				).setParameter(
					"removeOrganizationIds", _organization.getOrganizationId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "remove"));
		};
	}

	private String _getSelectUsersURL() {
		ItemSelector itemSelector =
			(ItemSelector)_httpServletRequest.getAttribute(
				ItemSelector.class.getName());

		UserOrganizationItemSelectorCriterion
			userOrganizationItemSelectorCriterion =
				new UserOrganizationItemSelectorCriterion();

		userOrganizationItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new UUIDItemSelectorReturnType());
		userOrganizationItemSelectorCriterion.setOrganizationId(
			_organization.getOrganizationId());

		return String.valueOf(
			itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				_renderResponse.getNamespace() + "selectUsers",
				userOrganizationItemSelectorCriterion));
	}

	private final HttpServletRequest _httpServletRequest;
	private final Organization _organization;
	private final Group _organizationGroup;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}