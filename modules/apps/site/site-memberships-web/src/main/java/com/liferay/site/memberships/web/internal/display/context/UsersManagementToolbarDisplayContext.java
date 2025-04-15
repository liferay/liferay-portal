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
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.User;
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
import com.liferay.portal.security.membershippolicy.SiteMembershipPolicyUtil;
import com.liferay.site.memberships.web.internal.util.GroupUtil;
import com.liferay.site.teams.item.selector.SiteTeamsItemSelectorCriterion;
import com.liferay.users.admin.item.selector.UserSiteMembershipItemSelectorCriterion;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class UsersManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public UsersManagementToolbarDisplayContext(
			HttpServletRequest httpServletRequest,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			UsersDisplayContext usersDisplayContext)
		throws PortalException {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			usersDisplayContext.getUserSearchContainer());

		_usersDisplayContext = usersDisplayContext;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return new DropdownItemList() {
			{
				add(
					dropdownItem -> {
						dropdownItem.putData("action", "deleteSelectedUsers");
						dropdownItem.setIcon("times-circle");
						dropdownItem.setLabel(
							LanguageUtil.get(httpServletRequest, "delete"));
						dropdownItem.setQuickAction(true);
					});

				try {
					if (GroupPermissionUtil.contains(
							_themeDisplay.getPermissionChecker(),
							_usersDisplayContext.getGroupId(),
							ActionKeys.ASSIGN_USER_ROLES)) {

						add(
							dropdownItem -> {
								dropdownItem.putData("action", "selectRole");
								dropdownItem.putData(
									"editUsersRolesURL",
									PortletURLBuilder.createActionURL(
										liferayPortletResponse
									).setActionName(
										"editUsersRoles"
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

						Role role = _usersDisplayContext.getRole();

						if (role != null) {
							String label = LanguageUtil.format(
								httpServletRequest, "remove-role-x",
								role.getTitle(_themeDisplay.getLocale()),
								false);

							add(
								dropdownItem -> {
									dropdownItem.putData(
										"action", "removeUserRole");
									dropdownItem.putData(
										"message",
										LanguageUtil.format(
											httpServletRequest,
											"are-you-sure-you-want-to-remove-" +
												"x-role-to-selected-users",
											role.getTitle(
												_themeDisplay.getLocale())));

									dropdownItem.putData(
										"removeUserRoleURL",
										PortletURLBuilder.create(
											liferayPortletResponse.
												createActionURL()
										).setActionName(
											"removeUserRole"
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

	public String getAvailableActions(User user) throws PortalException {
		List<String> availableActions = new ArrayList<>();

		if (GroupPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(),
				_usersDisplayContext.getGroupId(), ActionKeys.ASSIGN_MEMBERS) &&
			!SiteMembershipPolicyUtil.isMembershipProtected(
				_themeDisplay.getPermissionChecker(), user.getUserId(),
				_usersDisplayContext.getGroupId()) &&
			!SiteMembershipPolicyUtil.isMembershipRequired(
				user.getUserId(), _usersDisplayContext.getGroupId())) {

			availableActions.add("deleteSelectedUsers");
		}

		if (GroupPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getSiteGroupIdOrLiveGroupId(),
				ActionKeys.ASSIGN_USER_ROLES)) {

			availableActions.add("selectRole");

			Role role = _usersDisplayContext.getRole();

			if (role != null) {
				availableActions.add("removeUserRole");
			}
		}

		return StringUtil.merge(availableActions, StringPool.COMMA);
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
		return "usersManagementToolbar";
	}

	@Override
	public CreationMenu getCreationMenu() {
		try {
			return CreationMenuBuilder.addDropdownItem(
				dropdownItem -> {
					dropdownItem.putData("action", "selectUsers");
					dropdownItem.putData(
						"groupTypeLabel",
						GroupUtil.getGroupTypeLabel(
							_usersDisplayContext.getGroupId(),
							_themeDisplay.getLocale()));
					dropdownItem.putData(
						"selectUsersURL", _getSelectUsersURL());
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
		Role role = _usersDisplayContext.getRole();

		Team team = _usersDisplayContext.getTeam();

		return LabelItemListBuilder.add(
			() -> role != null,
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						PortletURLUtil.clone(
							currentURLObj, liferayPortletResponse)
					).setNavigation(
						"all"
					).setParameter(
						"roleId", "0"
					).buildString());

				labelItem.setCloseable(true);
				labelItem.setLabel(role.getTitle(_themeDisplay.getLocale()));
			}
		).add(
			() -> team != null,
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						PortletURLUtil.clone(
							currentURLObj, liferayPortletResponse)
					).setNavigation(
						"all"
					).setParameter(
						"teamId", "0"
					).buildString());

				labelItem.setCloseable(true);
				labelItem.setLabel(team.getName());
			}
		).build();
	}

	@Override
	public String getSearchContainerId() {
		return "users";
	}

	@Override
	public Boolean isShowCreationMenu() {
		try {
			if (GroupPermissionUtil.contains(
					_themeDisplay.getPermissionChecker(),
					_usersDisplayContext.getGroupId(),
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
	protected String getDefaultDisplayStyle() {
		return "icon";
	}

	@Override
	protected String getDisplayStyle() {
		return _usersDisplayContext.getDisplayStyle();
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"list", "descriptive", "icon"};
	}

	@Override
	protected List<DropdownItem> getFilterNavigationDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(Objects.equals(getNavigation(), "all"));
				dropdownItem.setHref(
					getPortletURL(), "navigation", "all", "roleId", "0",
					"teamId", "0");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "all"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.putData("action", "selectRoles");
				dropdownItem.putData(
					"selectRolesURL", _getSelectorURL("/select_site_role.jsp"));
				dropdownItem.putData(
					"viewRoleURL",
					PortletURLBuilder.createRenderURL(
						liferayPortletResponse
					).setMVCPath(
						"/view.jsp"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setNavigation(
						"roles"
					).setTabs1(
						"users"
					).setParameter(
						"groupId", _usersDisplayContext.getGroupId()
					).buildString());

				dropdownItem.setActive(
					Objects.equals(getNavigation(), "roles"));
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "roles"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.putData("action", "selectTeams");
				dropdownItem.putData("selectTeamsURL", _getSelectTeamsURL());
				dropdownItem.putData(
					"viewTeamURL",
					PortletURLBuilder.createRenderURL(
						liferayPortletResponse
					).setMVCPath(
						"/view.jsp"
					).setRedirect(
						_themeDisplay.getURLCurrent()
					).setNavigation(
						"teams"
					).setTabs1(
						"users"
					).setParameter(
						"groupId", _usersDisplayContext.getGroupId()
					).buildString());

				dropdownItem.setActive(
					Objects.equals(getNavigation(), "teams"));
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "teams"));
			}
		).build();
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"first-name", "screen-name"};
	}

	private String _getSelectorURL(String mvcPath) {
		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCPath(
			mvcPath
		).setParameter(
			"groupId", _usersDisplayContext.getGroupId()
		).setParameter(
			"roleType",
			() -> {
				Group scopeGroup = _themeDisplay.getScopeGroup();

				if (scopeGroup.isDepot()) {
					return String.valueOf(RoleConstants.TYPE_DEPOT);
				}

				return null;
			}
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private String _getSelectTeamsURL() {
		ItemSelector itemSelector =
			(ItemSelector)httpServletRequest.getAttribute(
				ItemSelector.class.getName());

		SiteTeamsItemSelectorCriterion siteTeamsItemSelectorCriterion =
			new SiteTeamsItemSelectorCriterion();

		siteTeamsItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new UUIDItemSelectorReturnType());

		return String.valueOf(
			itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
				liferayPortletResponse.getNamespace() + "selectTeams",
				siteTeamsItemSelectorCriterion));
	}

	private String _getSelectUsersURL() {
		ItemSelector itemSelector =
			(ItemSelector)httpServletRequest.getAttribute(
				ItemSelector.class.getName());

		UserSiteMembershipItemSelectorCriterion
			userSiteMembershipItemSelectorCriterion =
				new UserSiteMembershipItemSelectorCriterion();

		userSiteMembershipItemSelectorCriterion.
			setDesiredItemSelectorReturnTypes(new UUIDItemSelectorReturnType());
		userSiteMembershipItemSelectorCriterion.setGroupId(
			_usersDisplayContext.getGroupId());

		return String.valueOf(
			itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
				liferayPortletResponse.getNamespace() + "selectUsers",
				userSiteMembershipItemSelectorCriterion));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UsersManagementToolbarDisplayContext.class);

	private final ThemeDisplay _themeDisplay;
	private final UsersDisplayContext _usersDisplayContext;

}