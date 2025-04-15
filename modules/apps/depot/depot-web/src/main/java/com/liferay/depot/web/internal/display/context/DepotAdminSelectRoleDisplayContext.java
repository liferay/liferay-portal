/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.display.context;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.RolePermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.membershippolicy.SiteMembershipPolicyUtil;
import com.liferay.roles.admin.search.RoleSearch;
import com.liferay.roles.admin.search.RoleSearchTerms;
import com.liferay.site.search.GroupSearch;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author Cristina González
 */
public class DepotAdminSelectRoleDisplayContext {

	public DepotAdminSelectRoleDisplayContext(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortalException {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_user = PortalUtil.getSelectedUser(renderRequest);

		int step = ParamUtil.getInteger(renderRequest, "step", Step1.TYPE);

		if (step == Step2.TYPE) {
			_step = new Step2(null, renderRequest, renderResponse, _user);
		}
		else {
			String keywords = ParamUtil.getString(renderRequest, "keywords");

			if (Validator.isNotNull(keywords)) {
				_step = new Step1(renderRequest, renderResponse, _user);
			}
			else {
				long[] classNameIds = {
					PortalUtil.getClassNameId(DepotEntry.class.getName())
				};

				LinkedHashMap<String, Object> groupParams =
					LinkedHashMapBuilder.<String, Object>put(
						"inherit", Boolean.FALSE
					).put(
						"types",
						Collections.singletonList(GroupConstants.TYPE_DEPOT)
					).put(
						"usersGroups", _user.getUserId()
					).build();

				int searchCount = GroupServiceUtil.searchCount(
					_user.getCompanyId(), classNameIds, keywords, groupParams);

				if (searchCount == 1) {
					List<Group> groups = GroupServiceUtil.search(
						_user.getCompanyId(), classNameIds, keywords,
						groupParams, 0, 1, null);

					_step = new Step2(
						groups.get(0), renderRequest, renderResponse, _user);
				}
				else {
					_step = new Step1(renderRequest, renderResponse, _user);
				}
			}
		}
	}

	public PortletURL getPortletURL() throws PortalException {
		return _getPortletURL(
			_renderRequest, _renderResponse,
			PortalUtil.getUser(_renderRequest));
	}

	public Step getStep() {
		return _step;
	}

	public boolean isStep1() {
		if (_step.getType() == Step1.TYPE) {
			return true;
		}

		return false;
	}

	public boolean isStep2() {
		if (_step.getType() == Step2.TYPE) {
			return true;
		}

		return false;
	}

	public static class Step1 implements Step {

		public static final int TYPE = 1;

		public Step1(
				RenderRequest renderRequest, RenderResponse renderResponse,
				User user)
			throws PortalException {

			_renderRequest = renderRequest;
			_renderResponse = renderResponse;
			_user = user;
		}

		@Override
		public SearchContainer<Group> getSearchContainer()
			throws PortalException {

			if (_groupSearch != null) {
				return _groupSearch;
			}

			GroupSearch groupSearch = new GroupSearch(
				_renderRequest,
				_getPortletURL(_renderRequest, _renderResponse, _user));

			groupSearch.setEmptyResultsMessage("no-asset-libraries-were-found");
			groupSearch.setResultsAndTotal(_getDepotGroups());

			_groupSearch = groupSearch;

			return groupSearch;
		}

		public PortletURL getSelectRolePortletURL() {
			return PortletURLBuilder.create(
				_getPortletURL(_renderRequest, _renderResponse, _user)
			).setParameter(
				"resetCur", true
			).setParameter(
				"step", Step2.TYPE
			).buildPortletURL();
		}

		public int getType() {
			return TYPE;
		}

		private List<Group> _getDepotGroups() {
			if (_user == null) {
				return Collections.emptyList();
			}

			String keywords = ParamUtil.getString(_renderRequest, "keywords");

			if (Validator.isNull(keywords)) {
				return ListUtil.filter(_user.getGroups(), Group::isDepot);
			}

			return GroupLocalServiceUtil.search(
				_user.getCompanyId(),
				new long[] {
					ClassNameLocalServiceUtil.getClassNameId(DepotEntry.class)
				},
				GroupConstants.ANY_PARENT_GROUP_ID, keywords,
				LinkedHashMapBuilder.<String, Object>put(
					"inherit", Boolean.FALSE
				).put(
					"types",
					Collections.singletonList(GroupConstants.TYPE_DEPOT)
				).put(
					"usersGroups", _user.getUserId()
				).build(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}

		private GroupSearch _groupSearch;
		private final RenderRequest _renderRequest;
		private final RenderResponse _renderResponse;
		private final User _user;

	}

	public static class Step2 implements Step {

		public static final int TYPE = 2;

		public Step2(
				Group group, RenderRequest renderRequest,
				RenderResponse renderResponse, User user)
			throws PortalException {

			_renderRequest = renderRequest;
			_renderResponse = renderResponse;
			_user = user;

			if (group == null) {
				_group = _getGroup(renderRequest);
			}
			else {
				_group = group;
			}

			_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
				WebKeys.THEME_DISPLAY);
		}

		public String getBreadCrumbs() throws PortalException {
			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				_themeDisplay.getLocale(), getClass());

			return StringBundler.concat(
				"<a href=\"",
				PortletURLBuilder.create(
					_getPortletURL(_renderRequest, _renderResponse, _user)
				).setParameter(
					"step", Step1.TYPE
				).buildString(),
				"\">",
				ResourceBundleUtil.getString(resourceBundle, "asset-libraries"),
				"</a> &raquo; ",
				HtmlUtil.escape(
					_group.getDescriptiveName(_themeDisplay.getLocale())));
		}

		public Map<String, Object> getData(Role role) throws PortalException {
			return HashMapBuilder.<String, Object>put(
				"entityid",
				_group.getGroupId() + StringPool.DASH + role.getRoleId()
			).put(
				"groupdescriptivename",
				_group.getDescriptiveName(_themeDisplay.getLocale())
			).put(
				"iconcssclass", role.getIconCssClass()
			).put(
				"rolename", role.getTitle(_themeDisplay.getLocale())
			).build();
		}

		public String getEventName() {
			return _renderResponse.getNamespace() + "selectDepotRole";
		}

		public SearchContainer<Role> getSearchContainer()
			throws PortalException {

			if (_roleSearch != null) {
				return _roleSearch;
			}

			RoleSearch roleSearch = new RoleSearch(
				_renderRequest,
				_getPortletURL(_renderRequest, _renderResponse, _user));

			RoleSearchTerms searchTerms =
				(RoleSearchTerms)roleSearch.getSearchTerms();

			List<Role> roles = RoleLocalServiceUtil.search(
				_themeDisplay.getCompanyId(), searchTerms.getKeywords(),
				new Integer[] {RoleConstants.TYPE_DEPOT}, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, roleSearch.getOrderByComparator());

			if (_group != null) {
				roles = _filterGroupRoles(roles);
			}

			roleSearch.setResultsAndTotal(roles);

			_roleSearch = roleSearch;

			return _roleSearch;
		}

		public String getSyncEntitiesEventName() {
			return _renderResponse.getNamespace() + "syncDepotRoles";
		}

		public int getType() {
			return TYPE;
		}

		public boolean isDisabled(Role role) {
			if ((_user == null) || (_group == null)) {
				return false;
			}

			for (UserGroupRole userGroupRole :
					UserGroupRoleLocalServiceUtil.getUserGroupRoles(
						_user.getUserId())) {

				if ((_group.getGroupId() == userGroupRole.getGroupId()) &&
					(role.getRoleId() == userGroupRole.getRoleId())) {

					return true;
				}
			}

			return false;
		}

		public boolean isRoleAllowed(Role role) throws PortalException {
			long groupId = 0;

			if (_group != null) {
				groupId = _group.getGroupId();
			}

			long userId = 0;

			if (_user != null) {
				userId = _user.getUserId();
			}

			return SiteMembershipPolicyUtil.isRoleAllowed(
				userId, groupId, role.getRoleId());
		}

		/**
		 * @see com.liferay.site.memberships.web.internal.display.context.UserRolesDisplayContext#_filterGroupRoles(
		 *      PermissionChecker, long, List)
		 */
		private List<Role> _filterGroupRoles(List<Role> roles)
			throws PortalException {

			PermissionChecker permissionChecker =
				_themeDisplay.getPermissionChecker();

			if (permissionChecker.isCompanyAdmin() ||
				permissionChecker.isGroupOwner(_group.getGroupId())) {

				return ListUtil.filter(
					roles,
					role ->
						!Objects.equals(
							DepotRolesConstants.
								ASSET_LIBRARY_CONNECTED_SITE_MEMBER,
							role.getName()) &&
						!Objects.equals(
							DepotRolesConstants.ASSET_LIBRARY_MEMBER,
							role.getName()) &&
						!Objects.equals(
							DepotRolesConstants.CMS_CONSUMER, role.getName()));
			}

			if (!GroupPermissionUtil.contains(
					permissionChecker, _group, ActionKeys.ASSIGN_USER_ROLES)) {

				return Collections.emptyList();
			}

			return ListUtil.filter(
				roles,
				role ->
					!Objects.equals(
						DepotRolesConstants.ASSET_LIBRARY_CONNECTED_SITE_MEMBER,
						role.getName()) &&
					!Objects.equals(
						DepotRolesConstants.ASSET_LIBRARY_MEMBER,
						role.getName()) &&
					!Objects.equals(
						DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR,
						role.getName()) &&
					!Objects.equals(
						DepotRolesConstants.ASSET_LIBRARY_OWNER,
						role.getName()) &&
					!Objects.equals(
						DepotRolesConstants.CMS_CONSUMER, role.getName()) &&
					RolePermissionUtil.contains(
						permissionChecker, _group.getGroupId(),
						role.getRoleId(), ActionKeys.ASSIGN_MEMBERS));
		}

		private Group _getGroup(RenderRequest renderRequest)
			throws PortalException {

			long groupId = ParamUtil.getLong(renderRequest, "groupId");

			if (groupId <= 0) {
				return null;
			}

			return GroupServiceUtil.getGroup(groupId);
		}

		private final Group _group;
		private final RenderRequest _renderRequest;
		private final RenderResponse _renderResponse;
		private RoleSearch _roleSearch;
		private final ThemeDisplay _themeDisplay;
		private final User _user;

	}

	public interface Step {

		public SearchContainer<?> getSearchContainer() throws PortalException;

		public int getType();

	}

	private static PortletURL _getPortletURL(
		RenderRequest renderRequest, RenderResponse renderResponse, User user) {

		PortletURL portletURL = renderResponse.createRenderURL();

		if (user != null) {
			portletURL.setParameter(
				"p_u_i_d", String.valueOf(user.getUserId()));
		}

		portletURL.setParameter(
			"mvcRenderCommandName", "/depot/select_depot_role");

		String[] keywords = ParamUtil.getStringValues(
			renderRequest, "keywords");

		if (ArrayUtil.isNotEmpty(keywords)) {
			portletURL.setParameter("keywords", keywords[keywords.length - 1]);
		}

		portletURL.setParameter(
			"step",
			String.valueOf(
				ParamUtil.getInteger(renderRequest, "step", Step1.TYPE)));

		return portletURL;
	}

	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final Step _step;
	private final User _user;

}