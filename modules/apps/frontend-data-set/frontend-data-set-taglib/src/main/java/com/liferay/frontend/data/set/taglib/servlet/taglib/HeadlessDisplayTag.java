/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.taglib.servlet.taglib;

import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSSortItem;
import com.liferay.frontend.data.set.model.FDSSortItemList;
import com.liferay.frontend.data.set.taglib.internal.servlet.ServletContextUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class HeadlessDisplayTag extends BaseDisplayTag {

	@Override
	public int doStartTag() throws JspException {
		try {
			_appURL =
				PortalUtil.getPortalURL(getRequest()) +
					"/o/frontend-data-set-taglib/app";

			if (_creationMenu == null) {
				_creationMenu = new CreationMenu();
			}

			_setFiltersJSONArray();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		String randomKey = PortalUtil.generateRandomKey(
			getRequest(), "taglib_frontend_data_set_headless_display_page");

		setRandomNamespace(randomKey + StringPool.UNDERLINE);

		return super.doStartTag();
	}

	public String getActionParameterName() {
		return _actionParameterName;
	}

	public String getApiURL() {
		return _apiURL;
	}

	public List<DropdownItem> getBulkActionDropdownItems() {
		return _bulkActionDropdownItems;
	}

	public CreationMenu getCreationMenu() {
		return _creationMenu;
	}

	public List<FDSActionDropdownItem> getFdsActionDropdownItems() {
		return _fdsActionDropdownItems;
	}

	public List<FDSFilter> getFdsFilters() {
		return _fdsFilters;
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

	public boolean isSnapshotsEnabled() {
		return _snapshotsEnabled;
	}

	public void setActionParameterName(String actionParameterName) {
		_actionParameterName = actionParameterName;
	}

	public void setApiURL(String apiURL) {
		_apiURL = apiURL;
	}

	public void setBulkActionDropdownItems(
		List<DropdownItem> bulkActionDropdownItems) {

		_bulkActionDropdownItems = bulkActionDropdownItems;
	}

	public void setCreationMenu(CreationMenu creationMenu) {
		_creationMenu = creationMenu;
	}

	public void setFdsActionDropdownItems(
		List<FDSActionDropdownItem> fdsActionDropdownItems) {

		_fdsActionDropdownItems = fdsActionDropdownItems;
	}

	public void setFdsFilters(List<FDSFilter> fdsFilters) {
		_fdsFilters = fdsFilters;
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

	public void setSnapshotsEnabled(boolean snapshotsEnabled) {
		_snapshotsEnabled = snapshotsEnabled;
	}

	public void setStyle(String style) {
		_style = style;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_actionParameterName = null;
		_apiURL = null;
		_appURL = null;
		_bulkActionDropdownItems = new ArrayList<>();
		_creationMenu = new CreationMenu();
		_fdsActionDropdownItems = new ArrayList<>();
		_fdsFilters = new ArrayList<>();
		_fdsSortItemList = new FDSSortItemList();
		_filtersJSONArray = null;
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
		_snapshotsEnabled = false;
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
				"filters", _filtersJSONArray
			).put(
				"formId", _validateDataAttribute(_formId)
			).put(
				"formName", _validateDataAttribute(_formName)
			).put(
				"id", getId()
			).put(
				"itemsActions", _fdsActionDropdownItems
			).put(
				"nestedItemsKey", _validateDataAttribute(_nestedItemsKey)
			).put(
				"nestedItemsReferenceKey",
				_validateDataAttribute(_nestedItemsReferenceKey)
			).put(
				"portletId", PortalUtil.getPortletId(getRequest())
			).put(
				"selectedItemsKey", _validateDataAttribute(_selectedItemsKey)
			).put(
				"selectionType", _validateDataAttribute(_selectionType)
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
				"snapshotsEnabled", _snapshotsEnabled
			).put(
				"sorts", _fdsSortItemList
			).put(
				"style", _validateDataAttribute(_style)
			).build());
	}

	private void _setFiltersJSONArray() {
		_filtersJSONArray = fdsSerializer.serializeFilters(
			getFdsFilters(), getId(), getRequest());
	}

	private Object _validateDataAttribute(Object object) {
		if (Validator.isNull(object)) {
			return null;
		}

		return object;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HeadlessDisplayTag.class);

	private String _actionParameterName;
	private String _apiURL;
	private String _appURL;
	private List<DropdownItem> _bulkActionDropdownItems = new ArrayList<>();
	private CreationMenu _creationMenu = new CreationMenu();
	private List<FDSActionDropdownItem> _fdsActionDropdownItems =
		new ArrayList<>();
	private List<FDSFilter> _fdsFilters = new ArrayList<>();
	private FDSSortItemList _fdsSortItemList = new FDSSortItemList();
	private JSONArray _filtersJSONArray;
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
	private boolean _snapshotsEnabled;
	private String _style = "default";

}