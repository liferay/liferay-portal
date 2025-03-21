/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.taglib.internal.display.context;

import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.staging.LayoutStagingUtil;
import com.liferay.layout.util.LayoutsTree;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetBranchLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.DateRange;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.SessionTreeJSClicks;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.staging.configuration.StagingConfiguration;
import com.liferay.staging.taglib.internal.servlet.ServletContextUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Eudaldo Alonso
 */
public class LayoutsTreeDisplayContext {

	public LayoutsTreeDisplayContext(
		Group group, long groupId, HttpServletRequest httpServletRequest,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_group = group;
		_groupId = groupId;
		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAction() {
		if (_action != null) {
			return _action;
		}

		_action = GetterUtil.getString(
			_httpServletRequest.getAttribute(
				"liferay-staging:select-pages:action"));

		return _action;
	}

	public String getChildPageHelpMessage() throws ConfigurationException {
		String childPageHelpMessage = "child-page-export-process-warning";

		if (Objects.equals(getAction(), Constants.PUBLISH)) {
			childPageHelpMessage = "child-page-publish-process-warning";

			StagingConfiguration stagingConfiguration =
				ConfigurationProviderUtil.getCompanyConfiguration(
					StagingConfiguration.class, _themeDisplay.getCompanyId());

			if (!stagingConfiguration.publishParentLayoutsByDefault()) {
				childPageHelpMessage = null;
			}
		}

		return childPageHelpMessage;
	}

	public String getLayoutsCountMessageKey() throws PortalException {
		int messageKeyLayoutsCount = LayoutLocalServiceUtil.getLayoutsCount(
			getSelectPagesGroup(), isSelectPagesPrivateLayout(),
			ArrayUtil.toLongArray(_getSelectedLayoutIdsArray()));

		int totalLayoutsCount = LayoutLocalServiceUtil.getLayoutsCount(
			_getSelectPagesGroupId(), isSelectPagesPrivateLayout());

		if (messageKeyLayoutsCount > totalLayoutsCount) {
			messageKeyLayoutsCount = totalLayoutsCount;
		}

		if (totalLayoutsCount == 0) {
			return LanguageUtil.get(_themeDisplay.getLocale(), "none");
		}

		return LanguageUtil.format(
			_themeDisplay.getLocale(), "x-of-x",
			new String[] {
				"<strong>" + messageKeyLayoutsCount + "</strong>",
				String.valueOf(totalLayoutsCount)
			});
	}

	public List<LayoutSetBranch> getLayoutSetBranches() throws PortalException {
		List<LayoutSetBranch> layoutSetBranches = null;

		long layoutSetBranchId = getLayoutSetBranchId();

		if (isDisableInputs() && (layoutSetBranchId > 0)) {
			layoutSetBranches = new ArrayList<>(1);

			layoutSetBranches.add(
				LayoutSetBranchLocalServiceUtil.getLayoutSetBranch(
					layoutSetBranchId));
		}
		else {
			layoutSetBranches =
				LayoutSetBranchLocalServiceUtil.getLayoutSetBranches(
					_getSelectPagesGroupId(), isSelectPagesPrivateLayout());
		}

		return layoutSetBranches;
	}

	public long getLayoutSetBranchId() {
		if (_layoutSetBranchId != null) {
			return _layoutSetBranchId;
		}

		_layoutSetBranchId = MapUtil.getLong(
			getParameterMap(), "layoutSetBranchId");

		return _layoutSetBranchId;
	}

	public Map<String, Object> getPagesTreeData() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"config",
			HashMapBuilder.<String, Object>put(
				"changeItemSelectionURL",
				_themeDisplay.getPathMain() + "/portal/session_tree_js_click"
			).put(
				"loadMoreItemsURL",
				_themeDisplay.getPathMain() + "/portal/get_layouts_tree"
			).put(
				"maxPageSize",
				GetterUtil.getInteger(
					PropsValues.LAYOUT_MANAGE_PAGES_INITIAL_CHILDREN)
			).put(
				"namespace", _renderResponse.getNamespace()
			).build()
		).put(
			"groupId", _getSelectPagesGroupId()
		).put(
			"items", _getLayoutsJSONArray()
		).put(
			"privateLayout", isSelectPagesPrivateLayout()
		).put(
			"selectedLayoutIds", _getSelectedLayoutIdsArray()
		).put(
			"treeId", _getTreeId()
		).build();
	}

	public Map<String, String[]> getParameterMap() {
		if (_parameterMap != null) {
			return _parameterMap;
		}

		Map<String, String[]> parameterMap = Collections.emptyMap();

		Map<String, Serializable> settingsMap = getSettingsMap();

		if ((_getExportImportConfiguration() != null) &&
			MapUtil.isNotEmpty(settingsMap)) {

			parameterMap = (Map<String, String[]>)settingsMap.get(
				"parameterMap");
		}

		_parameterMap = parameterMap;

		return _parameterMap;
	}

	public PortletDataContext getPortletDataContext() throws PortalException {
		DateRange dateRange = null;

		String range = ParamUtil.getString(
			_httpServletRequest, ExportImportDateUtil.RANGE, null);

		ExportImportConfiguration exportImportConfiguration =
			_getExportImportConfiguration();

		if ((range != null) || (exportImportConfiguration == null)) {
			dateRange = ExportImportDateUtil.getDateRange(
				_renderRequest, _getSelectPagesGroupId(),
				isSelectPagesPrivateLayout(), 0, null,
				ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE);
		}
		else {
			dateRange = ExportImportDateUtil.getDateRange(
				exportImportConfiguration);

			range = ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE;
		}

		return PortletDataContextFactoryUtil.createPreparePortletDataContext(
			_themeDisplay.getCompanyId(), _getSelectPagesGroupId(), range,
			dateRange.getStartDate(), dateRange.getEndDate());
	}

	public Group getSelectPagesGroup() {
		if (_selectPagesGroup != null) {
			return _selectPagesGroup;
		}

		Group selectPagesGroup = _group;

		if (_groupId != _getSelectPagesGroupId()) {
			selectPagesGroup = GroupLocalServiceUtil.fetchGroup(_groupId);

			if (selectPagesGroup == null) {
				selectPagesGroup = _group;
			}
		}

		_selectPagesGroup = selectPagesGroup;

		return _selectPagesGroup;
	}

	public Map<String, Serializable> getSettingsMap() {
		if (_settingsMap != null) {
			return _settingsMap;
		}

		Map<String, Serializable> settingsMap = Collections.emptyMap();

		ExportImportConfiguration exportImportConfiguration =
			_getExportImportConfiguration();

		if (exportImportConfiguration != null) {
			settingsMap = exportImportConfiguration.getSettingsMap();
		}

		_settingsMap = settingsMap;

		return _settingsMap;
	}

	public boolean isDisableInputs() {
		if (_disableInputs != null) {
			return _disableInputs;
		}

		_disableInputs = GetterUtil.getBoolean(
			_httpServletRequest.getAttribute(
				"liferay-staging:select-pages:disableInputs"));

		return _disableInputs;
	}

	public boolean isPrivateLayoutsEnabled() {
		Group selectPagesGroup = getSelectPagesGroup();

		return selectPagesGroup.isPrivateLayoutsEnabled();
	}

	public boolean isSelectPagesPrivateLayout() {
		if (_selectPagesPrivateLayout != null) {
			return _selectPagesPrivateLayout;
		}

		_selectPagesPrivateLayout = GetterUtil.getBoolean(
			_httpServletRequest.getAttribute(
				"liferay-staging:select-pages:privateLayout"));

		return _selectPagesPrivateLayout;
	}

	private ExportImportConfiguration _getExportImportConfiguration() {
		if (_exportImportConfiguration != null) {
			return _exportImportConfiguration;
		}

		_exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				fetchExportImportConfiguration(
					_getExportImportConfigurationId());

		return _exportImportConfiguration;
	}

	private long _getExportImportConfigurationId() {
		if (_exportImportConfigurationId != null) {
			return _exportImportConfigurationId;
		}

		_exportImportConfigurationId = GetterUtil.getLong(
			_httpServletRequest.getAttribute(
				"liferay-staging:select-pages:exportImportConfigurationId"));

		return _exportImportConfigurationId;
	}

	private JSONArray _getLayoutsJSONArray() throws Exception {
		LayoutsTree layoutsTree = ServletContextUtil.getLayoutsTree();

		return JSONUtil.putAll(
			JSONUtil.put(
				"children",
				layoutsTree.getLayoutsJSONArray(
					_getSelectedLayoutIdsArray(), _getSelectPagesGroupId(),
					_httpServletRequest, false, _isIncomplete(), false,
					LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
					isSelectPagesPrivateLayout(), _getTreeId())
			).put(
				"hasChildren", true
			).put(
				"hasGuestViewPermission",
				() -> {
					Role role = RoleLocalServiceUtil.getRole(
						_themeDisplay.getCompanyId(), RoleConstants.GUEST);

					return ResourcePermissionLocalServiceUtil.
						hasResourcePermission(
							_themeDisplay.getCompanyId(),
							Layout.class.getName(),
							ResourceConstants.SCOPE_INDIVIDUAL,
							String.valueOf(_themeDisplay.getPlid()),
							role.getRoleId(), ActionKeys.VIEW);
				}
			).put(
				"id", LayoutConstants.DEFAULT_PARENT_LAYOUT_ID
			).put(
				"name",
				_group.getLayoutRootNodeName(
					isSelectPagesPrivateLayout(), _themeDisplay.getLocale())
			).put(
				"paginated",
				() -> {
					if (PropsValues.LAYOUT_MANAGE_PAGES_INITIAL_CHILDREN <= 0) {
						return false;
					}

					int layoutsCount = LayoutServiceUtil.getLayoutsCount(
						_getSelectPagesGroupId(), isSelectPagesPrivateLayout(),
						LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

					if (layoutsCount >
							PropsValues.LAYOUT_MANAGE_PAGES_INITIAL_CHILDREN) {

						return true;
					}

					return false;
				}
			));
	}

	private Set<Long> _getSelectedLayoutIdsArray() throws PortalException {
		if (_selectedLayoutIdsArray != null) {
			return _selectedLayoutIdsArray;
		}

		long[] selectedLayoutIdsArray = null;

		ExportImportConfiguration exportImportConfiguration =
			_getExportImportConfiguration();

		if (exportImportConfiguration != null) {
			Map<String, Serializable> settingsMap = getSettingsMap();

			if (exportImportConfiguration.getType() ==
					ExportImportConfigurationConstants.
						TYPE_PUBLISH_LAYOUT_REMOTE) {

				Map<Long, Boolean> layoutIdMap =
					(Map<Long, Boolean>)settingsMap.get("layoutIdMap");

				selectedLayoutIdsArray = ExportImportHelperUtil.getLayoutIds(
					layoutIdMap);
			}
			else {
				selectedLayoutIdsArray = GetterUtil.getLongValues(
					settingsMap.get("layoutIds"));
			}
		}
		else {
			String openNodes = SessionTreeJSClicks.getOpenNodes(
				_httpServletRequest, _getTreeId() + "SelectedNode");

			if (openNodes == null) {
				selectedLayoutIdsArray = ExportImportHelperUtil.getAllLayoutIds(
					_getSelectPagesGroupId(), isSelectPagesPrivateLayout());

				SessionTreeJSClicks.openNodes(
					_httpServletRequest, _getTreeId() + "SelectedNode",
					ArrayUtil.toStringArray(selectedLayoutIdsArray));
			}
			else {
				selectedLayoutIdsArray = GetterUtil.getLongValues(
					StringUtil.split(openNodes, ','));
			}
		}

		_selectedLayoutIdsArray = SetUtil.fromArray(selectedLayoutIdsArray);

		return _selectedLayoutIdsArray;
	}

	private long _getSelectPagesGroupId() {
		if (_selectPagesGroupId != null) {
			return _selectPagesGroupId;
		}

		_selectPagesGroupId = GetterUtil.getLong(
			_httpServletRequest.getAttribute(
				"liferay-staging:select-pages:groupId"));

		return _selectPagesGroupId;
	}

	private String _getTreeId() {
		if (_treeId != null) {
			return _treeId;
		}

		_treeId = GetterUtil.getString(
			_httpServletRequest.getAttribute(
				"liferay-staging:select-pages:treeId"));

		return _treeId;
	}

	private boolean _isIncomplete() {
		return LayoutStagingUtil.isBranchingLayoutSet(
			getSelectPagesGroup(), isSelectPagesPrivateLayout());
	}

	private String _action;
	private Boolean _disableInputs;
	private ExportImportConfiguration _exportImportConfiguration;
	private Long _exportImportConfigurationId;
	private final Group _group;
	private final long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private Long _layoutSetBranchId;
	private Map<String, String[]> _parameterMap;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private Set<Long> _selectedLayoutIdsArray;
	private Group _selectPagesGroup;
	private Long _selectPagesGroupId;
	private Boolean _selectPagesPrivateLayout;
	private Map<String, Serializable> _settingsMap;
	private final ThemeDisplay _themeDisplay;
	private String _treeId;

}