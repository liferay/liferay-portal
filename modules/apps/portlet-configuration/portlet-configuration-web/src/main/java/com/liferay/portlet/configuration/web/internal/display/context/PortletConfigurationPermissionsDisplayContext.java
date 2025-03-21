/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.ResourcePrimKeyException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.Resource;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourceLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.configuration.web.internal.configuration.RoleVisibilityConfiguration;
import com.liferay.portlet.configuration.web.internal.constants.PortletConfigurationPortletKeys;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;
import com.liferay.roles.admin.role.type.contributor.provider.RoleTypeContributorProvider;
import com.liferay.roles.admin.search.RoleSearch;
import com.liferay.roles.admin.search.RoleSearchTerms;

import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Eudaldo Alonso
 */
public class PortletConfigurationPermissionsDisplayContext {

	public PortletConfigurationPermissionsDisplayContext(
			HttpServletRequest httpServletRequest, RenderRequest renderRequest,
			RoleTypeContributorProvider roleTypeContributorProvider)
		throws PortalException {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_roleTypeContributorProvider = roleTypeContributorProvider;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = _getResourceGroupId();

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		Layout selLayout = null;

		if (Objects.equals(getModelResource(), Layout.class.getName())) {
			selLayout = LayoutLocalServiceUtil.getLayout(
				GetterUtil.getLong(getResourcePrimKey()));

			group = selLayout.getGroup();

			groupId = group.getGroupId();
		}

		_selLayout = selLayout;
		_group = group;
		_groupId = groupId;
	}

	public Map<String, List<String>> getActionIdResourcePrimKeysMap(Role role)
		throws PortalException {

		Map<String, List<String>> actionIdResourcePrimKeysMap = new HashMap<>();

		for (Resource resource : getResources()) {
			List<String> availableResourcePermissionActionIds =
				ResourcePermissionLocalServiceUtil.
					getAvailableResourcePermissionActionIds(
						resource.getCompanyId(), resource.getName(),
						resource.getScope(), resource.getPrimKey(),
						role.getRoleId(), getActions());

			for (String actionId : availableResourcePermissionActionIds) {
				List<String> resourcePrimKeys =
					actionIdResourcePrimKeysMap.getOrDefault(
						actionId, new ArrayList<>());

				resourcePrimKeys.add(resource.getPrimKey());

				actionIdResourcePrimKeysMap.put(actionId, resourcePrimKeys);
			}
		}

		return actionIdResourcePrimKeysMap;
	}

	public List<String> getActions() throws PortalException {
		if (_actions != null) {
			return _actions;
		}

		List<String> resourceActions = ResourceActionsUtil.getResourceActions(
			_getPortletResource(), getModelResource());

		if (Objects.equals(getModelResource(), Group.class.getName())) {
			Group modelResourceGroup = GroupLocalServiceUtil.getGroup(
				GetterUtil.getLong(getResourcePrimKey()));

			if (modelResourceGroup.isLayoutPrototype() ||
				modelResourceGroup.isLayoutSetPrototype() ||
				modelResourceGroup.isUserGroup()) {

				resourceActions = new ArrayList<>(resourceActions);

				resourceActions.remove(ActionKeys.ADD_LAYOUT_BRANCH);
				resourceActions.remove(ActionKeys.ADD_LAYOUT_SET_BRANCH);
				resourceActions.remove(ActionKeys.ASSIGN_MEMBERS);
				resourceActions.remove(ActionKeys.ASSIGN_USER_ROLES);
				resourceActions.remove(ActionKeys.MANAGE_ANNOUNCEMENTS);
				resourceActions.remove(ActionKeys.MANAGE_STAGING);
				resourceActions.remove(ActionKeys.MANAGE_TEAMS);
				resourceActions.remove(ActionKeys.PUBLISH_STAGING);
				resourceActions.remove(ActionKeys.VIEW_MEMBERS);
				resourceActions.remove(ActionKeys.VIEW_STAGING);
			}
		}
		else if (Objects.equals(getModelResource(), Role.class.getName())) {
			Role modelResourceRole = RoleLocalServiceUtil.getRole(
				GetterUtil.getLong(getResourcePrimKey()));

			String name = modelResourceRole.getName();

			if (name.equals(RoleConstants.GUEST) ||
				name.equals(RoleConstants.USER)) {

				resourceActions = new ArrayList<>(resourceActions);

				resourceActions.remove(ActionKeys.ASSIGN_MEMBERS);
				resourceActions.remove(ActionKeys.DELETE);
				resourceActions.remove(ActionKeys.UPDATE);
			}

			PermissionChecker permissionChecker =
				_themeDisplay.getPermissionChecker();

			if (!permissionChecker.isCompanyAdmin()) {
				resourceActions.remove(ActionKeys.DEFINE_PERMISSIONS);
			}
		}

		_actions = resourceActions;

		return _actions;
	}

	public String getClearResultsURL() throws Exception {
		return PortletURLBuilder.create(
			getIteratorURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	public PortletURL getDefinePermissionsURL() throws Exception {
		LiferayPortletURL liferayPortletURL =
			(LiferayPortletURL)PortletProviderUtil.getPortletURL(
				_httpServletRequest, Role.class.getName(),
				PortletProvider.Action.MANAGE);

		liferayPortletURL.setParameter(Constants.CMD, Constants.VIEW);
		liferayPortletURL.setParameter(
			"backURL", _themeDisplay.getURLCurrent());
		liferayPortletURL.setPortletMode(PortletMode.VIEW);
		liferayPortletURL.setRefererPlid(_themeDisplay.getPlid());

		liferayPortletURL.setWindowState(LiferayWindowState.POP_UP);

		return liferayPortletURL;
	}

	public String getGroupDescriptiveName() throws PortalException {
		return _group.getDescriptiveName(_themeDisplay.getLocale());
	}

	public long getGroupId() {
		return _groupId;
	}

	public List<String> getGuestUnsupportedActions() {
		if (_guestUnsupportedActions != null) {
			return _guestUnsupportedActions;
		}

		List<String> guestUnsupportedActions =
			ResourceActionsUtil.getResourceGuestUnsupportedActions(
				_getPortletResource(), getModelResource());

		// LPS-32515

		if ((_selLayout != null) && _group.isGuest() &&
			_isFirstLayout(
				_selLayout.getGroupId(), _selLayout.isPrivateLayout(),
				_selLayout.getLayoutId())) {

			guestUnsupportedActions = new ArrayList<>(guestUnsupportedActions);

			guestUnsupportedActions.add(ActionKeys.VIEW);
		}

		_guestUnsupportedActions = guestUnsupportedActions;

		return _guestUnsupportedActions;
	}

	public PortletURL getIteratorURL() throws Exception {
		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				_httpServletRequest,
				PortletConfigurationPortletKeys.PORTLET_CONFIGURATION,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_permissions.jsp"
		).setPortletResource(
			_getPortletResource()
		).setParameter(
			"modelResource", getModelResource()
		).setParameter(
			"portletConfiguration", true
		).setParameter(
			"resourceGroupId", _getResourceGroupId()
		).setParameter(
			"resourcePrimKey",
			StringUtil.merge(getResourcePrimKeys(), StringPool.COMMA)
		).setParameter(
			"returnToFullPageURL", _getReturnToFullPageURL()
		).setParameter(
			"roleTypes", _getRoleTypesParam()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildPortletURL();
	}

	public String getModelResource() {
		if (_modelResource != null) {
			return _modelResource;
		}

		_modelResource = ParamUtil.getString(
			_httpServletRequest, "modelResource");

		return _modelResource;
	}

	public String getModelResourceDescription() {
		if (_modelResourceDescription != null) {
			return _modelResourceDescription;
		}

		_modelResourceDescription = ParamUtil.getString(
			_httpServletRequest, "modelResourceDescription");

		return _modelResourceDescription;
	}

	public String getResourcePrimKey() throws ResourcePrimKeyException {
		String[] resourcePrimKeys = getResourcePrimKeys();

		return resourcePrimKeys[0];
	}

	public String[] getResourcePrimKeys() throws ResourcePrimKeyException {
		if (_resourcePrimKeys != null) {
			return _resourcePrimKeys;
		}

		_resourcePrimKeys = ParamUtil.getStringValues(
			_httpServletRequest, "resourcePrimKey");

		if (ArrayUtil.isEmpty(_resourcePrimKeys)) {
			throw new ResourcePrimKeyException();
		}

		return _resourcePrimKeys;
	}

	public List<Resource> getResources() throws PortalException {
		if (ListUtil.isNotEmpty(_resources)) {
			return _resources;
		}

		for (String resourcePrimKey : getResourcePrimKeys()) {
			int count =
				ResourcePermissionLocalServiceUtil.getResourcePermissionsCount(
					_themeDisplay.getCompanyId(), getSelResource(),
					ResourceConstants.SCOPE_INDIVIDUAL, resourcePrimKey);

			if (count == 0) {
				boolean portletActions = Validator.isNull(getModelResource());

				ResourceLocalServiceUtil.addResources(
					_themeDisplay.getCompanyId(), getGroupId(), 0,
					getSelResource(), resourcePrimKey, portletActions, true,
					true);
			}

			_resources.add(
				ResourceLocalServiceUtil.getResource(
					_themeDisplay.getCompanyId(), getSelResource(),
					ResourceConstants.SCOPE_INDIVIDUAL, resourcePrimKey));
		}

		return _resources;
	}

	public SearchContainer<Role> getRoleSearchContainer() throws Exception {
		if (_roleSearchContainer != null) {
			return _roleSearchContainer;
		}

		SearchContainer<Role> roleSearchContainer = new RoleSearch(
			_renderRequest, getIteratorURL());

		RoleSearchTerms searchTerms =
			(RoleSearchTerms)roleSearchContainer.getSearchTerms();

		_keywords = searchTerms.getKeywords();

		boolean filterGroupRoles = !ResourceActionsUtil.isPortalModelResource(
			getModelResource());

		if (Objects.equals(getModelResource(), Role.class.getName())) {
			Role modelResourceRole = RoleLocalServiceUtil.getRole(
				GetterUtil.getLong(getResourcePrimKey()));

			RoleTypeContributor roleTypeContributor =
				_roleTypeContributorProvider.getRoleTypeContributor(
					modelResourceRole.getType());

			if (ArrayUtil.isNotEmpty(
					roleTypeContributor.getExcludedRoleNames())) {

				filterGroupRoles = true;
			}
		}

		long modelResourceRoleId = 0;

		if (Objects.equals(getModelResource(), Role.class.getName())) {
			modelResourceRoleId = GetterUtil.getLong(getResourcePrimKey());
		}

		boolean filterGuestRole = false;

		if (Objects.equals(getModelResource(), Layout.class.getName())) {
			Layout resourceLayout = LayoutLocalServiceUtil.getLayout(
				GetterUtil.getLong(getResourcePrimKey()));

			if (resourceLayout.isPrivateLayout()) {
				Group resourceLayoutGroup = resourceLayout.getGroup();

				if (!resourceLayoutGroup.isLayoutSetPrototype() &&
					!PropsValues.PERMISSIONS_CHECK_GUEST_ENABLED) {

					filterGuestRole = true;
				}
			}
		}
		else if (Validator.isNotNull(_getPortletResource())) {
			String resourcePrimKey = getResourcePrimKey();

			int pos = resourcePrimKey.indexOf(
				PortletConstants.LAYOUT_SEPARATOR);

			if (pos > 0) {
				Layout resourceLayout = LayoutLocalServiceUtil.getLayout(
					GetterUtil.getLong(resourcePrimKey.substring(0, pos)));

				if (resourceLayout.isPrivateLayout()) {
					Group resourceLayoutGroup = resourceLayout.getGroup();

					if (!resourceLayoutGroup.isLayoutPrototype() &&
						!resourceLayoutGroup.isLayoutSetPrototype() &&
						!PropsValues.PERMISSIONS_CHECK_GUEST_ENABLED) {

						filterGuestRole = true;
					}
				}
			}
		}

		Set<String> excludedRoleNamesSet = new HashSet<String>() {
			{
				add(RoleConstants.ADMINISTRATOR);
			}
		};

		if (filterGroupRoles) {
			for (RoleTypeContributor roleTypeContributor :
					_roleTypeContributorProvider.getRoleTypeContributors()) {

				Collections.addAll(
					excludedRoleNamesSet,
					roleTypeContributor.getExcludedRoleNames());
			}
		}

		if (filterGuestRole) {
			excludedRoleNamesSet.add(RoleConstants.GUEST);
		}

		List<String> excludedRoleNames = ListUtil.fromCollection(
			excludedRoleNamesSet);

		long teamGroupId = _group.getGroupId();

		if (_group.isLayout()) {
			teamGroupId = _group.getParentGroupId();
		}

		long roleModelResourceRoleId = modelResourceRoleId;

		long roleTeamGroupId = teamGroupId;

		RoleVisibilityConfiguration stricterRoleVisibilityConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				RoleVisibilityConfiguration.class,
				_themeDisplay.getCompanyId());

		if (Validator.isNull(_getKeywords())) {
			if (stricterRoleVisibilityConfiguration.
					restrictPermissionSelectorRoleVisibility()) {

				roleSearchContainer.setResultsAndTotal(
					() -> RoleServiceUtil.getGroupRolesAndTeamRoles(
						_themeDisplay.getCompanyId(), null, excludedRoleNames,
						null, null, getRoleTypes(), roleModelResourceRoleId,
						roleTeamGroupId, roleSearchContainer.getStart(),
						roleSearchContainer.getEnd()),
					RoleServiceUtil.getGroupRolesAndTeamRolesCount(
						_themeDisplay.getCompanyId(), null, excludedRoleNames,
						null, null, getRoleTypes(), roleModelResourceRoleId,
						roleTeamGroupId));
			}
			else {
				roleSearchContainer.setResultsAndTotal(
					() -> RoleLocalServiceUtil.getGroupRolesAndTeamRoles(
						_themeDisplay.getCompanyId(), null, excludedRoleNames,
						null, null, getRoleTypes(), roleModelResourceRoleId,
						roleTeamGroupId, roleSearchContainer.getStart(),
						roleSearchContainer.getEnd()),
					RoleLocalServiceUtil.getGroupRolesAndTeamRolesCount(
						_themeDisplay.getCompanyId(), null, excludedRoleNames,
						null, null, getRoleTypes(), roleModelResourceRoleId,
						roleTeamGroupId));
			}
		}
		else {
			if (stricterRoleVisibilityConfiguration.
					restrictPermissionSelectorRoleVisibility()) {

				roleSearchContainer.setResultsAndTotal(
					RoleServiceUtil.getGroupRolesAndTeamRoles(
						_themeDisplay.getCompanyId(), _getKeywords(),
						excludedRoleNames, _getKeywords(), null, getRoleTypes(),
						modelResourceRoleId, teamGroupId, QueryUtil.ALL_POS,
						QueryUtil.ALL_POS));
			}
			else {
				roleSearchContainer.setResultsAndTotal(
					RoleLocalServiceUtil.getGroupRolesAndTeamRoles(
						_themeDisplay.getCompanyId(), _getKeywords(),
						excludedRoleNames, _getKeywords(), null, getRoleTypes(),
						modelResourceRoleId, teamGroupId, QueryUtil.ALL_POS,
						QueryUtil.ALL_POS));
			}
		}

		_roleSearchContainer = roleSearchContainer;

		return _roleSearchContainer;
	}

	public int[] getRoleTypes() {
		if (_roleTypes != null) {
			return _roleTypes;
		}

		String roleTypesParam = _getRoleTypesParam();

		if (Validator.isNotNull(roleTypesParam)) {
			_roleTypes = StringUtil.split(roleTypesParam, 0);
		}

		if (_roleTypes != null) {
			return _roleTypes;
		}

		_roleTypes = RoleConstants.TYPES_REGULAR_AND_SITE;

		if ((_group != null) && _group.isDepot()) {
			_roleTypes = _TYPES_DEPOT_AND_REGULAR;
		}

		if (ResourceActionsUtil.isPortalModelResource(getModelResource())) {
			if (Objects.equals(
					getModelResource(), Organization.class.getName()) ||
				Objects.equals(getModelResource(), User.class.getName())) {

				_roleTypes = RoleConstants.TYPES_ORGANIZATION_AND_REGULAR;
			}
			else {
				_roleTypes = RoleConstants.TYPES_REGULAR;
			}

			return _roleTypes;
		}

		if (_group == null) {
			return _roleTypes;
		}

		Group parentGroup = null;

		if (_group.isLayout()) {
			parentGroup = GroupLocalServiceUtil.fetchGroup(
				_group.getParentGroupId());
		}

		if (parentGroup != null) {
			_roleTypes = _getGroupRoleTypes(parentGroup, _roleTypes);
		}
		else {
			_roleTypes = _getGroupRoleTypes(_group, _roleTypes);
		}

		return _roleTypes;
	}

	public String getSearchActionURL() throws Exception {
		PortletURL searchActionURL = getIteratorURL();

		return searchActionURL.toString();
	}

	public String getSelResource() {
		_selResource = getModelResource();

		if (Validator.isNotNull(_selResource)) {
			return _selResource;
		}

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			_themeDisplay.getCompanyId(), _getPortletResource());

		_selResource = portlet.getRootPortletId();

		return _selResource;
	}

	public String getSelResourceDescription() {
		_selResourceDescription = getModelResourceDescription();

		if (Validator.isNotNull(_selResourceDescription)) {
			return _selResourceDescription;
		}

		HttpSession httpSession = _httpServletRequest.getSession();

		_selResourceDescription = PortalUtil.getPortletTitle(
			PortletLocalServiceUtil.getPortletById(
				_themeDisplay.getCompanyId(), _getPortletResource()),
			httpSession.getServletContext(), _themeDisplay.getLocale());

		return _selResourceDescription;
	}

	public PortletURL getUpdateRolePermissionsURL()
		throws ResourcePrimKeyException, WindowStateException {

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				_httpServletRequest,
				PortletConfigurationPortletKeys.PORTLET_CONFIGURATION,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"updateRolePermissions"
		).setMVCPath(
			"/edit_permissions.jsp"
		).setKeywords(
			_getKeywords()
		).setPortletResource(
			_getPortletResource()
		).setParameter(
			"cur",
			ParamUtil.getInteger(
				_httpServletRequest, SearchContainer.DEFAULT_CUR_PARAM)
		).setParameter(
			"delta",
			ParamUtil.getInteger(
				_httpServletRequest, SearchContainer.DEFAULT_DELTA_PARAM)
		).setParameter(
			"modelResource", getModelResource()
		).setParameter(
			"modelResourceDescription", getModelResourceDescription()
		).setParameter(
			"portletConfiguration", true
		).setParameter(
			"resourceGroupId", _getResourceGroupId()
		).setParameter(
			"resourcePrimKey",
			StringUtil.merge(getResourcePrimKeys(), StringPool.COMMA)
		).setParameter(
			"returnToFullPageURL", _getReturnToFullPageURL()
		).setParameter(
			"roleTypes", _getRoleTypesParam()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildPortletURL();
	}

	private int[] _getGroupRoleTypes(Group group, int[] defaultRoleTypes) {
		if (group == null) {
			return defaultRoleTypes;
		}

		if (group.isOrganization()) {
			return RoleConstants.TYPES_ORGANIZATION_AND_REGULAR_AND_SITE;
		}

		if (group.isCompany() || group.isUser() || group.isUserGroup()) {
			return RoleConstants.TYPES_REGULAR;
		}

		return defaultRoleTypes;
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	private String _getPortletResource() {
		if (_portletResource != null) {
			return _portletResource;
		}

		_portletResource = ParamUtil.getString(
			_httpServletRequest, "portletResource");

		return _portletResource;
	}

	private long _getResourceGroupId() {
		if (_resourceGroupId != null) {
			return _resourceGroupId;
		}

		_resourceGroupId = ParamUtil.getLong(
			_httpServletRequest, "resourceGroupId");

		if (_resourceGroupId == 0) {
			_resourceGroupId = _themeDisplay.getScopeGroupId();
		}

		return _resourceGroupId;
	}

	private String _getReturnToFullPageURL() {
		if (_returnToFullPageURL != null) {
			return _returnToFullPageURL;
		}

		_returnToFullPageURL = ParamUtil.getString(
			_httpServletRequest, "returnToFullPageURL");

		return _returnToFullPageURL;
	}

	private String _getRoleTypesParam() {
		if (_roleTypesParam != null) {
			return _roleTypesParam;
		}

		_roleTypesParam = ParamUtil.getString(_httpServletRequest, "roleTypes");

		return _roleTypesParam;
	}

	private boolean _isFirstLayout(
		long groupId, boolean privateLayout, long layoutId) {

		Layout firstLayout = LayoutLocalServiceUtil.fetchFirstLayout(
			groupId, privateLayout, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

		if ((firstLayout != null) && (firstLayout.getLayoutId() == layoutId)) {
			return true;
		}

		return false;
	}

	private static final int[] _TYPES_DEPOT_AND_REGULAR = {
		RoleConstants.TYPE_DEPOT, RoleConstants.TYPE_REGULAR
	};

	private List<String> _actions;
	private final Group _group;
	private final long _groupId;
	private List<String> _guestUnsupportedActions;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private String _modelResource;
	private String _modelResourceDescription;
	private String _portletResource;
	private final RenderRequest _renderRequest;
	private Long _resourceGroupId;
	private String[] _resourcePrimKeys;
	private final List<Resource> _resources = new ArrayList<>();
	private String _returnToFullPageURL;
	private SearchContainer<Role> _roleSearchContainer;
	private final RoleTypeContributorProvider _roleTypeContributorProvider;
	private int[] _roleTypes;
	private String _roleTypesParam;
	private final Layout _selLayout;
	private String _selResource;
	private String _selResourceDescription;
	private final ThemeDisplay _themeDisplay;

}