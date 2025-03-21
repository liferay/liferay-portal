/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.memberships.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.memberships.web.internal.util.GroupUtil;
import com.liferay.user.groups.admin.item.selector.UserGroupSiteMembershipItemSelectorCriterion;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class UserGroupsManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public UserGroupsManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		UserGroupsDisplayContext userGroupsDisplayContext) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			userGroupsDisplayContext.getUserGroupSearchContainer());

		_userGroupsDisplayContext = userGroupsDisplayContext;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return new DropdownItemList() {
			{
				try {
					if (GroupPermissionUtil.contains(
							themeDisplay.getPermissionChecker(),
							_userGroupsDisplayContext.getGroupId(),
							ActionKeys.ASSIGN_MEMBERS)) {

						add(
							dropdownItem -> {
								dropdownItem.putData(
									"action", "deleteSelectedUserGroups");
								dropdownItem.setIcon("times-circle");
								dropdownItem.setLabel(
									LanguageUtil.get(
										httpServletRequest, "delete"));
								dropdownItem.setQuickAction(true);
							});
					}
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}

				try {
					if (GroupPermissionUtil.contains(
							themeDisplay.getPermissionChecker(),
							_userGroupsDisplayContext.getGroupId(),
							ActionKeys.ASSIGN_USER_ROLES)) {

						add(
							dropdownItem -> {
								dropdownItem.putData("action", "selectRole");

								dropdownItem.putData(
									"editUserGroupsRolesURL",
									PortletURLBuilder.createActionURL(
										liferayPortletResponse
									).setActionName(
										"editUserGroupsRoles"
									).setTabs1(
										"user-groups"
									).buildString());

								dropdownItem.putData(
									"selectRoleURL",
									_getSelectorURL("/site_roles.jsp"));
								dropdownItem.setIcon("add-role");
								dropdownItem.setLabel(
									LanguageUtil.get(
										httpServletRequest, "assign-roles"));
								dropdownItem.setQuickAction(true);
							});

						Role role = _userGroupsDisplayContext.getRole();

						if (role != null) {
							String label = LanguageUtil.format(
								httpServletRequest, "remove-role-x",
								role.getTitle(themeDisplay.getLocale()), false);

							add(
								dropdownItem -> {
									dropdownItem.putData(
										"action", "removeUserGroupRole");
									dropdownItem.putData(
										"message",
										LanguageUtil.format(
											httpServletRequest,
											"are-you-sure-you-want-to-remove-" +
												"x-role-to-selected-user-" +
													"groups",
											role.getTitle(
												themeDisplay.getLocale())));

									dropdownItem.putData(
										"removeUserGroupRoleURL",
										PortletURLBuilder.create(
											liferayPortletResponse.
												createActionURL()
										).setActionName(
											"removeUserGroupRole"
										).buildString());

									dropdownItem.setIcon("remove-role");
									dropdownItem.setLabel(label);
									dropdownItem.setQuickAction(true);
								});
						}
					}
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}
			}
		};
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).setNavigation(
			"all"
		).setParameter(
			"roleId", "0"
		).buildString();
	}

	@Override
	public String getComponentId() {
		return "userGroupsManagementToolbar";
	}

	@Override
	public CreationMenu getCreationMenu() {
		try {
			return CreationMenuBuilder.addDropdownItem(
				dropdownItem -> {
					dropdownItem.putData("action", "selectUserGroups");

					ThemeDisplay themeDisplay =
						(ThemeDisplay)httpServletRequest.getAttribute(
							WebKeys.THEME_DISPLAY);

					dropdownItem.putData(
						"groupTypeLabel",
						GroupUtil.getGroupTypeLabel(
							_userGroupsDisplayContext.getGroupId(),
							themeDisplay.getLocale()));

					dropdownItem.putData(
						"selectUserGroupsURL", _getSelectUserGroupURL());
					dropdownItem.setLabel(
						LanguageUtil.get(httpServletRequest, "add"));
				}
			).build();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Role role = _userGroupsDisplayContext.getRole();

		return LabelItemListBuilder.add(
			() -> role != null,
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						PortletURLUtil.clone(
							currentURLObj, liferayPortletResponse)
					).setParameter(
						"roleId", "0"
					).buildString());

				labelItem.setCloseable(true);
				labelItem.setLabel(role.getTitle(themeDisplay.getLocale()));
			}
		).build();
	}

	@Override
	public String getSearchContainerId() {
		return "userGroups";
	}

	@Override
	public Boolean isShowCreationMenu() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			if (GroupPermissionUtil.contains(
					themeDisplay.getPermissionChecker(),
					_userGroupsDisplayContext.getGroupId(),
					ActionKeys.ASSIGN_MEMBERS)) {

				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	@Override
	protected String getDisplayStyle() {
		return _userGroupsDisplayContext.getDisplayStyle();
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"list", "descriptive"};
	}

	@Override
	protected List<DropdownItem> getFilterNavigationDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(Objects.equals(getNavigation(), "all"));
				dropdownItem.setHref(
					getPortletURL(), "navigation", "all", "roleId", "0");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "all"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.putData("action", "selectRoles");
				dropdownItem.putData(
					"selectRolesURL", _getSelectorURL("/select_site_role.jsp"));
				dropdownItem.setActive(
					Objects.equals(getNavigation(), "roles"));

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				dropdownItem.putData(
					"viewRoleURL",
					PortletURLBuilder.createRenderURL(
						liferayPortletResponse
					).setMVCPath(
						"/view.jsp"
					).setRedirect(
						themeDisplay.getURLCurrent()
					).setNavigation(
						"roles"
					).setTabs1(
						"user-groups"
					).setParameter(
						"groupId", _userGroupsDisplayContext.getGroupId()
					).buildString());

				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "roles"));
			}
		).build();
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"name", "description"};
	}

	private String _getSelectorURL(String mvcPath) throws Exception {
		PortletURL selectURL = PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCPath(
			mvcPath
		).setParameter(
			"groupId", _userGroupsDisplayContext.getGroupId()
		).buildPortletURL();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group scopeGroup = themeDisplay.getScopeGroup();

		if (scopeGroup.isDepot()) {
			selectURL.setParameter(
				"roleType", String.valueOf(RoleConstants.TYPE_DEPOT));
		}

		selectURL.setWindowState(LiferayWindowState.POP_UP);

		return selectURL.toString();
	}

	private String _getSelectUserGroupURL() {
		ItemSelector itemSelector =
			(ItemSelector)httpServletRequest.getAttribute(
				ItemSelector.class.getName());

		UserGroupSiteMembershipItemSelectorCriterion
			userGroupSiteMembershipItemSelectorCriterion =
				new UserGroupSiteMembershipItemSelectorCriterion();

		userGroupSiteMembershipItemSelectorCriterion.
			setDesiredItemSelectorReturnTypes(new UUIDItemSelectorReturnType());

		return String.valueOf(
			itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
				liferayPortletResponse.getNamespace() + "selectUserGroup",
				userGroupSiteMembershipItemSelectorCriterion));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserGroupsManagementToolbarDisplayContext.class);

	private final UserGroupsDisplayContext _userGroupsDisplayContext;

}