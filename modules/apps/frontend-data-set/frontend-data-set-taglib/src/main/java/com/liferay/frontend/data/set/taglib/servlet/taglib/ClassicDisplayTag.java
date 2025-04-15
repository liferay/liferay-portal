/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.taglib.servlet.taglib;

import com.liferay.frontend.data.set.model.FDSSortItem;
import com.liferay.frontend.data.set.model.FDSSortItemList;
import com.liferay.frontend.data.set.taglib.internal.servlet.ServletContextUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class ClassicDisplayTag extends BaseDisplayTag {

	@Override
	public int doStartTag() throws JspException {
		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		try {
			_appURL =
				PortalUtil.getPortalURL(httpServletRequest) +
					PortalUtil.getPathContext() +
						"/o/frontend-data-set-taglib/app";

			StringBundler sb = new StringBundler(
				11 + (_contextParams.size() * 4));

			sb.append(_appURL);
			sb.append("/data-set/");
			sb.append(getId());
			sb.append(StringPool.FORWARD_SLASH);
			sb.append(_dataProviderKey);
			sb.append("?groupId=");
			sb.append(themeDisplay.getScopeGroupId());
			sb.append("&plid=");
			sb.append(layout.getPlid());
			sb.append("&portletId=");
			sb.append(portletDisplay.getId());

			for (Map.Entry<String, String> entry : _contextParams.entrySet()) {
				sb.append(StringPool.AMPERSAND);
				sb.append(entry.getKey());
				sb.append(StringPool.EQUAL);
				sb.append(entry.getValue());
			}

			_apiURL = sb.toString();

			if (_creationMenu == null) {
				_creationMenu = new CreationMenu();
			}

			_setActiveViewSettingsJSON();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		String randomKey = PortalUtil.generateRandomKey(
			getRequest(), "taglib_frontend_data_set_classic_display_page");

		setRandomNamespace(randomKey + StringPool.UNDERLINE);

		return super.doStartTag();
	}

	public String getActionParameterName() {
		return _actionParameterName;
	}

	public List<DropdownItem> getBulkActionDropdownItems() {
		return _bulkActionDropdownItems;
	}

	public Map<String, String> getContextParams() {
		return _contextParams;
	}

	public CreationMenu getCreationMenu() {
		return _creationMenu;
	}

	public String getDataProviderKey() {
		return _dataProviderKey;
	}

	public String getDeltaParam() {
		return _deltaParam;
	}

	public List<FDSSortItem> getFdsSortItemList() {
		return _fdsSortItemList;
	}

	public String getFormId() {
		return _formId;
	}

	public String getFormName() {
		return _formName;
	}

	public String getNestedItemsKey() {
		return _nestedItemsKey;
	}

	public String getNestedItemsReferenceKey() {
		return _nestedItemsReferenceKey;
	}

	public String getSelectedItemsKey() {
		return _selectedItemsKey;
	}

	public String getSelectionType() {
		return _selectionType;
	}

	public String getStyle() {
		return _style;
	}

	public boolean isShowBulkActionsManagementBar() {
		return _showBulkActionsManagementBar;
	}

	public boolean isShowBulkActionsManagementBarActions() {
		return _showBulkActionsManagementBarActions;
	}

	public boolean isShowManagementBar() {
		return _showManagementBar;
	}

	public boolean isShowPagination() {
		return _showPagination;
	}

	public boolean isShowSearch() {
		return _showSearch;
	}

	public boolean isShowSelectAll() {
		return _showSelectAll;
	}

	public void setActionParameterName(String actionParameterName) {
		_actionParameterName = actionParameterName;
	}

	public void setBulkActionDropdownItems(
		List<DropdownItem> bulkActionDropdownItems) {

		_bulkActionDropdownItems = bulkActionDropdownItems;
	}

	public void setContextParams(Map<String, String> contextParams) {
		_contextParams = contextParams;
	}

	public void setCreationMenu(CreationMenu creationMenu) {
		_creationMenu = creationMenu;
	}

	public void setDataProviderKey(String dataProviderKey) {
		_dataProviderKey = dataProviderKey;
	}

	public void setDeltaParam(String deltaParam) {
		_deltaParam = deltaParam;
	}

	public void setFdsSortItemList(FDSSortItemList fdsSortItemList) {
		_fdsSortItemList = fdsSortItemList;
	}

	public void setFormId(String formId) {
		_formId = formId;
	}

	public void setFormName(String formName) {
		_formName = formName;
	}

	public void setNestedItemsKey(String nestedItemsKey) {
		_nestedItemsKey = nestedItemsKey;
	}

	public void setNestedItemsReferenceKey(String nestedItemsReferenceKey) {
		_nestedItemsReferenceKey = nestedItemsReferenceKey;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setSelectedItemsKey(String selectedItemsKey) {
		_selectedItemsKey = selectedItemsKey;
	}

	public void setSelectionType(String selectionType) {
		_selectionType = selectionType;
	}

	public void setShowBulkActionsManagementBar(
		boolean showBulkActionsManagementBar) {

		_showBulkActionsManagementBar = showBulkActionsManagementBar;
	}

	public void setShowBulkActionsManagementBarActions(
		boolean showBulkActionsManagementBarActions) {

		_showBulkActionsManagementBarActions =
			showBulkActionsManagementBarActions;
	}

	public void setShowManagementBar(boolean showManagementBar) {
		_showManagementBar = showManagementBar;
	}

	public void setShowPagination(boolean showPagination) {
		_showPagination = showPagination;
	}

	public void setShowSearch(boolean showSearch) {
		_showSearch = showSearch;
	}

	public void setShowSelectAll(boolean showSelectAll) {
		_showSelectAll = showSelectAll;
	}

	public void setStyle(String style) {
		_style = style;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_actionParameterName = null;
		_activeViewSettingsJSON = null;
		_apiURL = null;
		_appURL = null;
		_bulkActionDropdownItems = new ArrayList<>();
		_contextParams = new HashMap<>();
		_creationMenu = new CreationMenu();
		_dataProviderKey = null;
		_deltaParam = null;
		_fdsSortItemList = new FDSSortItemList();
		_formId = null;
		_formName = null;
		_nestedItemsKey = null;
		_nestedItemsReferenceKey = null;
		_selectedItemsKey = null;
		_selectionType = null;
		_showBulkActionsManagementBar = true;
		_showBulkActionsManagementBarActions = true;
		_showManagementBar = true;
		_showPagination = true;
		_showSearch = true;
		_showSelectAll = false;
		_style = "default";
	}

	@Override
	protected Map<String, Object> prepareProps(Map<String, Object> props) {
		return super.prepareProps(
			HashMapBuilder.<String, Object>putAll(
				props
			).put(
				"actionParameterName",
				GetterUtil.getString(_actionParameterName)
			).put(
				"activeViewSettings", _activeViewSettingsJSON
			).put(
				"apiURL", _apiURL
			).put(
				"appURL", _appURL
			).put(
				"bulkActions", _bulkActionDropdownItems
			).put(
				"creationMenu", _creationMenu
			).put(
				"currentURL", PortalUtil.getCurrentURL(getRequest())
			).put(
				"dataProviderKey", _dataProviderKey
			).put(
				"formId", _toNullOrObject(_formId)
			).put(
				"formName", _toNullOrObject(_formName)
			).put(
				"id", getId()
			).put(
				"nestedItemsKey", _toNullOrObject(_nestedItemsKey)
			).put(
				"nestedItemsReferenceKey",
				_toNullOrObject(_nestedItemsReferenceKey)
			).put(
				"portletId", PortalUtil.getPortletId(getRequest())
			).put(
				"selectedItemsKey", _toNullOrObject(_selectedItemsKey)
			).put(
				"selectionType", _toNullOrObject(_selectionType)
			).put(
				"showBulkActionsManagementBar", _showBulkActionsManagementBar
			).put(
				"showBulkActionsManagementBarActions",
				_showBulkActionsManagementBarActions
			).put(
				"showManagementBar", _showManagementBar
			).put(
				"showPagination", _showPagination
			).put(
				"showSearch", _showSearch
			).put(
				"showSelectAll", _showSelectAll
			).put(
				"sorts", _fdsSortItemList
			).put(
				"style", _toNullOrObject(_style)
			).build());
	}

	private void _setActiveViewSettingsJSON() {
		HttpServletRequest httpServletRequest = getRequest();

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				httpServletRequest);

		_activeViewSettingsJSON = portalPreferences.getValue(
			ServletContextUtil.getFDSSettingsNamespace(
				httpServletRequest, getId()),
			"activeViewSettingsJSON");
	}

	private Object _toNullOrObject(Object object) {
		if (Validator.isNull(object)) {
			return null;
		}

		return object;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ClassicDisplayTag.class);

	private String _actionParameterName;
	private String _activeViewSettingsJSON;
	private String _apiURL;
	private String _appURL;
	private List<DropdownItem> _bulkActionDropdownItems = new ArrayList<>();
	private Map<String, String> _contextParams = new HashMap<>();
	private CreationMenu _creationMenu = new CreationMenu();
	private String _dataProviderKey;
	private String _deltaParam;
	private FDSSortItemList _fdsSortItemList = new FDSSortItemList();
	private String _formId;
	private String _formName;
	private String _nestedItemsKey;
	private String _nestedItemsReferenceKey;
	private String _selectedItemsKey;
	private String _selectionType;
	private boolean _showBulkActionsManagementBar = true;
	private boolean _showBulkActionsManagementBarActions = true;
	private boolean _showManagementBar = true;
	private boolean _showPagination = true;
	private boolean _showSearch = true;
	private boolean _showSelectAll;
	private String _style = "default";

}