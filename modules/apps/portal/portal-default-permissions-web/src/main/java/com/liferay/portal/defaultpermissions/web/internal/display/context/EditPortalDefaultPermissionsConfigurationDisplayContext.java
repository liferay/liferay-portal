/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.defaultpermissions.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.defaultpermissions.configuration.manager.PortalDefaultPermissionsConfigurationManager;
import com.liferay.portal.kernel.defaultpermissions.resource.PortalDefaultPermissionsModelResource;
import com.liferay.portal.kernel.defaultpermissions.resource.PortalDefaultPermissionsModelResourceRegistry;
import com.liferay.portal.kernel.defaultpermissions.resource.PortalDefaultPermissionsModelResourceRegistryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletQName;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;
import com.liferay.roles.admin.role.type.contributor.provider.RoleTypeContributorProvider;
import com.liferay.roles.admin.search.RoleSearch;
import com.liferay.roles.admin.search.RoleSearchTerms;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Stefano Motta
 */
public class EditPortalDefaultPermissionsConfigurationDisplayContext {

	public EditPortalDefaultPermissionsConfigurationDisplayContext(
			HttpServletRequest httpServletRequest,
			PortalDefaultPermissionsConfigurationManager
				portalDefaultPermissionsConfigurationManager,
			RenderRequest renderRequest,
			RoleTypeContributorProvider roleTypeContributorProvider)
		throws PortalException {

		_httpServletRequest = httpServletRequest;
		_portalDefaultPermissionsConfigurationManager =
			portalDefaultPermissionsConfigurationManager;
		_renderRequest = renderRequest;
		_roleTypeContributorProvider = roleTypeContributorProvider;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_group = GroupLocalServiceUtil.getGroup(_getResourceGroupId());
	}

	public String getActionLabel(
		HttpServletRequest httpServletRequest, String resourceName,
		String actionId) {

		String actionLabel = null;

		if (actionId.equals("ADD_STRUCTURE") &&
			resourceName.equals("com.liferay.document.library")) {

			actionLabel = LanguageUtil.get(
				httpServletRequest, "add-metadata-set");
		}

		if (Validator.isNull(actionLabel)) {
			actionLabel = ResourceActionsUtil.getAction(
				httpServletRequest, actionId);
		}

		return actionLabel;
	}

	public List<String> getActions() {
		if (_actions != null) {
			return _actions;
		}

		_actions = ResourceActionsUtil.getResourceActions(
			_getPortletResource(), getModelResource());

		return _actions;
	}

	public String getClearResultsURL() throws Exception {
		return PortletURLBuilder.create(
			getIteratorURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	public List<String> getCurrentActions(Role role) {
		Map<String, String[]> defaultPermissions = _getDefaultPermissions();

		if (defaultPermissions == null) {
			defaultPermissions = new HashMap<>();
		}

		String[] actions = defaultPermissions.get(role.getName());

		if (actions == null) {
			if (RoleConstants.GUEST.equals(role.getName())) {
				return ResourceActionsUtil.getModelResourceGuestDefaultActions(
					getModelResource());
			}
			else if (RoleConstants.OWNER.equals(role.getName())) {
				return ResourceActionsUtil.getModelResourceOwnerDefaultActions(
					getModelResource());
			}
			else if (RoleConstants.SITE_MEMBER.equals(role.getName())) {
				return ResourceActionsUtil.getModelResourceGroupDefaultActions(
					getModelResource());
			}

			return Collections.emptyList();
		}

		return Arrays.asList(actions);
	}

	public List<String> getGroupDisabledActions() {
		if (_groupDisabledActions != null) {
			return _groupDisabledActions;
		}

		if (_isAllowOverridePermissions()) {
			_groupDisabledActions = Collections.emptyList();
		}
		else {
			_groupDisabledActions =
				ResourceActionsUtil.getModelResourceGroupDefaultActions(
					getModelResource());
		}

		return _groupDisabledActions;
	}

	public List<String> getGuestDisabledActions() {
		if (_guestDisabledActions != null) {
			return _guestDisabledActions;
		}

		_guestDisabledActions =
			ResourceActionsUtil.getModelResourceGuestUnsupportedActions(
				getModelResource());

		if (!_isAllowOverridePermissions()) {
			_guestDisabledActions.addAll(
				ResourceActionsUtil.getModelResourceGuestDefaultActions(
					getModelResource()));
		}

		return _guestDisabledActions;
	}

	public PortletURL getIteratorURL() throws Exception {
		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_httpServletRequest,
				"com_liferay_portlet_configuration_web_portlet_" +
					"PortletConfigurationPortlet",
				ActionRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/configuration/edit_portal_default_permissions_configuration"
		).setParameter(
			PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE + "backURL",
			ParamUtil.getString(
				_httpServletRequest, "currentUrl",
				PortalUtil.getCurrentURL(_httpServletRequest))
		).setParameter(
			"modelResource", getModelResource()
		).setParameter(
			"resourceGroupId", _getResourceGroupId()
		).setParameter(
			"roleTypes", _getRoleTypesParam()
		).setParameter(
			"scope", _portalDefaultPermissionsConfigurationManager.getScope()
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

	public List<String> getOwnerDisabledActions() {
		if (_ownerDisabledActions != null) {
			return _ownerDisabledActions;
		}

		if (_isAllowOverridePermissions()) {
			_ownerDisabledActions = Collections.emptyList();
		}
		else {
			_ownerDisabledActions =
				ResourceActionsUtil.getModelResourceOwnerDefaultActions(
					getModelResource());
		}

		return _ownerDisabledActions;
	}

	public SearchContainer<Role> getRoleSearchContainer() throws Exception {
		if (_roleSearchContainer != null) {
			return _roleSearchContainer;
		}

		SearchContainer<Role> roleSearchContainer = new RoleSearch(
			_renderRequest, getIteratorURL());

		RoleSearchTerms searchTerms =
			(RoleSearchTerms)roleSearchContainer.getSearchTerms();

		boolean filterGroupRoles = !ResourceActionsUtil.isPortalModelResource(
			getModelResource());

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

		List<String> excludedRoleNames = ListUtil.fromCollection(
			excludedRoleNamesSet);

		long teamGroupId = _group.getGroupId();

		if (_group.isLayout()) {
			teamGroupId = _group.getParentGroupId();
		}

		long roleTeamGroupId = teamGroupId;

		if (Validator.isNull(searchTerms.getKeywords())) {
			roleSearchContainer.setResultsAndTotal(
				() -> RoleLocalServiceUtil.getGroupRolesAndTeamRoles(
					_themeDisplay.getCompanyId(), null, excludedRoleNames, null,
					null, _getRoleTypes(), 0, roleTeamGroupId,
					roleSearchContainer.getStart(),
					roleSearchContainer.getEnd()),
				RoleLocalServiceUtil.getGroupRolesAndTeamRolesCount(
					_themeDisplay.getCompanyId(), null, excludedRoleNames, null,
					null, _getRoleTypes(), 0, roleTeamGroupId));
		}
		else {
			roleSearchContainer.setResultsAndTotal(
				RoleLocalServiceUtil.getGroupRolesAndTeamRoles(
					_themeDisplay.getCompanyId(), searchTerms.getKeywords(),
					excludedRoleNames, searchTerms.getKeywords(), null,
					_getRoleTypes(), 0, roleTeamGroupId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS));
		}

		_roleSearchContainer = roleSearchContainer;

		return _roleSearchContainer;
	}

	public String getSearchActionURL() throws Exception {
		PortletURL searchActionURL = getIteratorURL();

		return searchActionURL.toString();
	}

	public String getUpdateRolePermissionsURL() {
		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				_httpServletRequest,
				"com_liferay_portlet_configuration_web_portlet_" +
					"PortletConfigurationPortlet",
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/configuration/edit_portal_default_permissions_configuration"
		).setParameter(
			"modelResource", getModelResource()
		).setParameter(
			"portletConfiguration", true
		).setParameter(
			"resourceGroupId", _getResourceGroupId()
		).setParameter(
			"roleTypes", _getRoleTypesParam()
		).setParameter(
			"scope", _portalDefaultPermissionsConfigurationManager.getScope()
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private Map<String, String[]> _getDefaultPermissions() {
		if (_defaultPermissions != null) {
			return _defaultPermissions;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_defaultPermissions =
			_portalDefaultPermissionsConfigurationManager.getDefaultPermissions(
				themeDisplay.getCompanyId(), themeDisplay.getSiteGroupId(),
				getModelResource());

		return _defaultPermissions;
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

	private int[] _getRoleTypes() {
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

	private String _getRoleTypesParam() {
		if (_roleTypesParam != null) {
			return _roleTypesParam;
		}

		_roleTypesParam = ParamUtil.getString(_httpServletRequest, "roleTypes");

		return _roleTypesParam;
	}

	private boolean _isAllowOverridePermissions() {
		if (_portalDefaultPermissionsModelResource != null) {
			return _portalDefaultPermissionsModelResource.
				isAllowOverridePermissions();
		}

		PortalDefaultPermissionsModelResourceRegistry
			portalDefaultPermissionsModelResourceRegistry =
				PortalDefaultPermissionsModelResourceRegistryUtil.
					getPortalDefaultPermissionsModelResourceRegistry();

		_portalDefaultPermissionsModelResource =
			portalDefaultPermissionsModelResourceRegistry.
				getPortalDefaultPermissionsModelResource(getModelResource());

		return _portalDefaultPermissionsModelResource.
			isAllowOverridePermissions();
	}

	private static final int[] _TYPES_DEPOT_AND_REGULAR = {
		RoleConstants.TYPE_DEPOT, RoleConstants.TYPE_REGULAR
	};

	private List<String> _actions;
	private Map<String, String[]> _defaultPermissions;
	private final Group _group;
	private List<String> _groupDisabledActions;
	private List<String> _guestDisabledActions;
	private final HttpServletRequest _httpServletRequest;
	private String _modelResource;
	private List<String> _ownerDisabledActions;
	private final PortalDefaultPermissionsConfigurationManager
		_portalDefaultPermissionsConfigurationManager;
	private PortalDefaultPermissionsModelResource
		_portalDefaultPermissionsModelResource;
	private String _portletResource;
	private final RenderRequest _renderRequest;
	private Long _resourceGroupId;
	private SearchContainer<Role> _roleSearchContainer;
	private final RoleTypeContributorProvider _roleTypeContributorProvider;
	private int[] _roleTypes;
	private String _roleTypesParam;
	private final ThemeDisplay _themeDisplay;

}