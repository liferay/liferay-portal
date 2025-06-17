/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.internal.servlet.taglib.BaseContainerTag;
import com.liferay.frontend.taglib.clay.internal.servlet.taglib.display.context.ManagementToolbarDefaults;
import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.ManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItem;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.TagResourceBundleUtil;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author Marko Cikos
 */
public class ManagementToolbarTag extends BaseContainerTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		String searchValue = getSearchValue();
		String searchInputName = getSearchInputName();

		if ((searchValue == null) && (searchInputName != null)) {
			String searchValueParamValue = ParamUtil.getString(
				getRequest(), searchInputName);

			if (!searchValueParamValue.equals(StringPool.BLANK)) {
				setSearchValue(searchValueParamValue);
			}
		}

		List<LabelItem> filterLabelItems = getFilterLabelItems();

		if ((getSearchValue() != null) ||
			((filterLabelItems != null) && !filterLabelItems.isEmpty())) {

			setShowResultsBar(true);
		}

		return super.doStartTag();
	}

	public List<DropdownItem> getActionDropdownItems() {
		if ((_actionDropdownItems == null) &&
			(_managementToolbarDisplayContext != null)) {

			return _managementToolbarDisplayContext.getActionDropdownItems();
		}

		return _actionDropdownItems;
	}

	public String getCheckboxStatus() {
		return _checkboxStatus;
	}

	public String getClearResultsURL() {
		if ((_clearResultsURL == null) &&
			(_managementToolbarDisplayContext != null)) {

			return _managementToolbarDisplayContext.getClearResultsURL();
		}

		return _clearResultsURL;
	}

	public String getClearSelectionURL() {
		return _clearSelectionURL;
	}

	public CreationMenu getCreationMenu() {
		if ((_creationMenu == null) &&
			(_managementToolbarDisplayContext != null)) {

			return _managementToolbarDisplayContext.getCreationMenu();
		}

		return _creationMenu;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by
	 * {@link #getManagementToolbarDisplayContext()}
	 */
	@Deprecated
	public ManagementToolbarDisplayContext getDisplayContext() {
		return getManagementToolbarDisplayContext();
	}

	public List<DropdownItem> getFilterDropdownItems() {
		if ((_filterDropdownItems == null) &&
			(_managementToolbarDisplayContext != null)) {

			return _managementToolbarDisplayContext.getFilterDropdownItems();
		}

		return _filterDropdownItems;
	}

	public List<LabelItem> getFilterLabelItems() {
		if ((_filterLabelItems == null) &&
			(_managementToolbarDisplayContext != null)) {

			return _managementToolbarDisplayContext.getFilterLabelItems();
		}

		return _filterLabelItems;
	}

	public String getInfoPanelId() {
		if ((_infoPanelId == null) &&
			(_managementToolbarDisplayContext != null)) {

			return _managementToolbarDisplayContext.getInfoPanelId();
		}

		return _infoPanelId;
	}

	public Integer getItemsTotal() {
		if (_itemsTotal == null) {
			if (_managementToolbarDisplayContext != null) {
				return _managementToolbarDisplayContext.getItemsTotal();
			}

			return ManagementToolbarDefaults.getItemsTotal();
		}

		return _itemsTotal;
	}

	public String getItemsType() {
		return _itemsType;
	}

	public ManagementToolbarDisplayContext
		getManagementToolbarDisplayContext() {

		return _managementToolbarDisplayContext;
	}

	@Override
	public String getNamespace() {
		if (_namespace != null) {
			return _namespace;
		}

		if (_managementToolbarDisplayContext != null) {
			return _managementToolbarDisplayContext.getNamespace();
		}

		HttpServletRequest httpServletRequest = getRequest();

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		if (portletResponse != null) {
			_namespace = portletResponse.getNamespace();
		}

		return _namespace;
	}

	public List<DropdownItem> getOrderDropdownItems() {
		if ((_orderDropdownItems == null) &&
			(_managementToolbarDisplayContext != null)) {

			return _managementToolbarDisplayContext.getOrderDropdownItems();
		}

		return _orderDropdownItems;
	}

	public String getSearchActionURL() {
		if ((_searchActionURL == null) &&
			(_managementToolbarDisplayContext != null)) {

			return _managementToolbarDisplayContext.getSearchActionURL();
		}

		return _searchActionURL;
	}

	public String getSearchContainerId() {
		if ((_searchContainerId == null) &&
			(_managementToolbarDisplayContext != null)) {

			return _managementToolbarDisplayContext.getSearchContainerId();
		}

		return _searchContainerId;
	}

	public String getSearchFormMethod() {
		if (_searchFormMethod == null) {
			if (_managementToolbarDisplayContext != null) {
				return _managementToolbarDisplayContext.getSearchFormMethod();
			}

			return ManagementToolbarDefaults.getSearchFormMethod();
		}

		return _searchFormMethod;
	}

	public String getSearchFormName() {
		if ((_searchFormName == null) &&
			(_managementToolbarDisplayContext != null)) {

			return _managementToolbarDisplayContext.getSearchFormName();
		}

		return _searchFormName;
	}

	public String getSearchInputName() {
		if (_searchInputName == null) {
			if (_managementToolbarDisplayContext != null) {
				return _managementToolbarDisplayContext.getSearchInputName();
			}

			return ManagementToolbarDefaults.getSearchInputName();
		}

		return _searchInputName;
	}

	public String getSearchResultsTitle() {
		String searchResultsTitle = _searchResultsTitle;

		if (searchResultsTitle == null) {
			if (_managementToolbarDisplayContext != null) {
				searchResultsTitle =
					_managementToolbarDisplayContext.getSearchResultsTitle();
			}

			if (searchResultsTitle == null) {
				searchResultsTitle =
					ManagementToolbarDefaults.getSearchResultsTitle();
			}
		}

		return LanguageUtil.get(
			TagResourceBundleUtil.getResourceBundle(pageContext),
			searchResultsTitle);
	}

	public String getSearchValue() {
		if ((_searchValue == null) &&
			(_managementToolbarDisplayContext != null)) {

			return _managementToolbarDisplayContext.getSearchValue();
		}

		return _searchValue;
	}

	public String getSelectAllURL() {
		return _selectAllURL;
	}

	public Integer getSelectedItems() {
		if (_selectedItems == null) {
			if (_managementToolbarDisplayContext != null) {
				return _managementToolbarDisplayContext.getSelectedItems();
			}

			return ManagementToolbarDefaults.getSelectedItems();
		}

		return _selectedItems;
	}

	public String getSortingOrder() {
		if ((_sortingOrder == null) &&
			(_managementToolbarDisplayContext != null)) {

			return _managementToolbarDisplayContext.getSortingOrder();
		}

		return _sortingOrder;
	}

	public String getSortingURL() {
		if ((_sortingURL == null) &&
			(_managementToolbarDisplayContext != null)) {

			return _managementToolbarDisplayContext.getSortingURL();
		}

		return _sortingURL;
	}

	public List<ViewTypeItem> getViewTypeItems() {
		if ((_viewTypeItems == null) &&
			(_managementToolbarDisplayContext != null)) {

			return _managementToolbarDisplayContext.getViewTypeItems();
		}

		return _viewTypeItems;
	}

	public Boolean isDisabled() {
		if (_disabled == null) {
			if (_managementToolbarDisplayContext != null) {
				return _managementToolbarDisplayContext.isDisabled();
			}

			return ManagementToolbarDefaults.isDisabled();
		}

		return _disabled;
	}

	public Boolean isSearchInputAutoFocus() {
		if (_searchInputAutoFocus == null) {
			if (_managementToolbarDisplayContext != null) {
				return _managementToolbarDisplayContext.
					isSearchInputAutoFocus();
			}

			return ManagementToolbarDefaults.isSearchInputAutoFocus();
		}

		return _searchInputAutoFocus;
	}

	public Boolean isSelectable() {
		if (_selectable == null) {
			if (_managementToolbarDisplayContext != null) {
				return _managementToolbarDisplayContext.isSelectable();
			}

			return ManagementToolbarDefaults.isSelectable();
		}

		return _selectable;
	}

	public Boolean isShowCreationMenu() {
		if (_showCreationMenu == null) {
			if (_managementToolbarDisplayContext != null) {
				return _managementToolbarDisplayContext.isShowCreationMenu();
			}

			return ManagementToolbarDefaults.isShowCreationMenu(
				getCreationMenu());
		}

		return _showCreationMenu;
	}

	public Boolean isShowInfoButton() {
		if (_showInfoButton == null) {
			if (_managementToolbarDisplayContext != null) {
				return _managementToolbarDisplayContext.isShowInfoButton();
			}

			return ManagementToolbarDefaults.isShowInfoButton(getInfoPanelId());
		}

		return _showInfoButton;
	}

	public Boolean isShowResultsBar() {
		return _showResultsBar;
	}

	public Boolean isShowSearch() {
		if (_showSearch == null) {
			if (_managementToolbarDisplayContext != null) {
				return _managementToolbarDisplayContext.isShowSearch();
			}

			return ManagementToolbarDefaults.isShowSearch();
		}

		return _showSearch;
	}

	public Boolean isShowSelectAllButton() {
		return _showSelectAllButton;
	}

	public Boolean isSupportsBulkActions() {
		if (_supportsBulkActions == null) {
			if (_managementToolbarDisplayContext != null) {
				return _managementToolbarDisplayContext.
					getSupportsBulkActions();
			}

			return ManagementToolbarDefaults.isSupportsBulkActions();
		}

		return _supportsBulkActions;
	}

	public void setActionDropdownItems(List<DropdownItem> actionDropdownItems) {
		_actionDropdownItems = actionDropdownItems;
	}

	public void setCheckboxStatus(String checkboxStatus) {
		_checkboxStatus = checkboxStatus;
	}

	public void setClearResultsURL(String clearResultsURL) {
		_clearResultsURL = clearResultsURL;
	}

	public void setClearSelectionURL(String clearSelectionURL) {
		_clearSelectionURL = clearSelectionURL;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	public void setContentRenderer(String contentRenderer) {
		_contentRenderer = contentRenderer;
	}

	public void setCreationMenu(CreationMenu creationMenu) {
		_creationMenu = creationMenu;
	}

	public void setDisabled(Boolean disabled) {
		_disabled = disabled;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by
	 * {@link #setManagementToolbarDisplayContext()}
	 */
	@Deprecated
	public void setDisplayContext(
		ManagementToolbarDisplayContext managementToolbarDisplayContext) {

		setManagementToolbarDisplayContext(managementToolbarDisplayContext);
	}

	public void setFilterDropdownItems(List<DropdownItem> filterDropdownItems) {
		_filterDropdownItems = filterDropdownItems;
	}

	public void setFilterLabelItems(List<LabelItem> filterLabelItems) {
		_filterLabelItems = filterLabelItems;
	}

	public void setInfoPanelId(String infoPanelId) {
		_infoPanelId = infoPanelId;
	}

	public void setItemsTotal(Integer itemsTotal) {
		_itemsTotal = itemsTotal;
	}

	public void setItemsType(String itemsType) {
		_itemsType = itemsType;
	}

	public void setManagementToolbarDisplayContext(
		ManagementToolbarDisplayContext managementToolbarDisplayContext) {

		_managementToolbarDisplayContext = managementToolbarDisplayContext;
	}

	@Override
	public void setNamespace(String namespace) {
		_namespace = namespace;
	}

	public void setOrderDropdownItems(List<DropdownItem> orderDropdownItems) {
		_orderDropdownItems = orderDropdownItems;
	}

	public void setSearchActionURL(String searchActionURL) {
		_searchActionURL = searchActionURL;
	}

	public void setSearchContainerId(String searchContainerId) {
		_searchContainerId = searchContainerId;
	}

	public void setSearchFormMethod(String searchFormMethod) {
		_searchFormMethod = searchFormMethod;
	}

	public void setSearchFormName(String searchFormName) {
		_searchFormName = searchFormName;
	}

	public void setSearchInputAutoFocus(Boolean searchInputAutoFocus) {
		_searchInputAutoFocus = searchInputAutoFocus;
	}

	public void setSearchInputName(String searchInputName) {
		_searchInputName = searchInputName;
	}

	public void setSearchResultsTitle(String searchResultsTitle) {
		_searchResultsTitle = searchResultsTitle;
	}

	public void setSearchValue(String searchValue) {
		_searchValue = searchValue;
	}

	public void setSelectable(Boolean selectable) {
		_selectable = selectable;
	}

	public void setSelectAllURL(String selectAllURL) {
		_selectAllURL = selectAllURL;
	}

	public void setSelectedItems(Integer selectedItems) {
		_selectedItems = selectedItems;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	public void setShowAdvancedSearch(Boolean showAdvancedSearch) {
		_showAdvancedSearch = showAdvancedSearch;
	}

	public void setShowCreationMenu(Boolean showCreationMenu) {
		_showCreationMenu = showCreationMenu;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	public void setShowFiltersDoneButton(Boolean showFiltersDoneButton) {
		_showFiltersDoneButton = showFiltersDoneButton;
	}

	public void setShowInfoButton(Boolean showInfoButton) {
		_showInfoButton = showInfoButton;
	}

	public void setShowResultsBar(Boolean showResultsBar) {
		_showResultsBar = showResultsBar;
	}

	public void setShowSearch(Boolean showSearch) {
		_showSearch = showSearch;
	}

	public void setShowSelectAllButton(Boolean showSelectAllButton) {
		_showSelectAllButton = showSelectAllButton;
	}

	public void setSortingOrder(String sortingOrder) {
		_sortingOrder = sortingOrder;
	}

	public void setSortingURL(String sortingURL) {
		_sortingURL = sortingURL;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	public void setSpritemap(String spritemap) {
		_spritemap = spritemap;
	}

	public void setSupportsBulkActions(Boolean supportsBulkActions) {
		_supportsBulkActions = supportsBulkActions;
	}

	public void setViewTypeItems(List<ViewTypeItem> viewTypeItems) {
		_viewTypeItems = viewTypeItems;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_actionDropdownItems = null;
		_checkboxStatus = "unchecked";
		_clearResultsURL = null;
		_clearSelectionURL = null;
		_contentRenderer = null;
		_creationMenu = null;
		_disabled = null;
		_filterDropdownItems = null;
		_filterLabelItems = null;
		_infoPanelId = null;
		_itemsTotal = null;
		_itemsType = "items";
		_managementToolbarDisplayContext = null;
		_namespace = null;
		_orderDropdownItems = null;
		_searchActionURL = null;
		_searchContainerId = null;
		_searchFormMethod = null;
		_searchFormName = null;
		_searchInputAutoFocus = null;
		_searchInputName = null;
		_searchResultsTitle = null;
		_searchValue = null;
		_selectable = null;
		_selectAllURL = null;
		_selectedItems = null;
		_showAdvancedSearch = null;
		_showCreationMenu = null;
		_showFiltersDoneButton = null;
		_showInfoButton = null;
		_showResultsBar = false;
		_showSearch = null;
		_showSelectAllButton = false;
		_sortingOrder = null;
		_sortingURL = null;
		_spritemap = null;
		_supportsBulkActions = null;
		_viewTypeItems = null;
	}

	@Override
	protected String getHydratedModuleName() {
		return "{ManagementToolbar} from frontend-taglib-clay";
	}

	@Override
	protected Map<String, Object> prepareProps(Map<String, Object> props) {
		props.put("clearSelectionURL", getClearSelectionURL());
		props.put("clearResultsURL", getClearResultsURL());
		props.put("creationMenu", getCreationMenu());
		props.put("disabled", isDisabled());
		props.put("filterDropdownItems", getFilterDropdownItems());
		props.put("filterLabelItems", getFilterLabelItems());

		String namespace = getNamespace();

		props.put("infoPanelId", _namespace(namespace, getInfoPanelId()));

		props.put("initialActionDropdownItems", getActionDropdownItems());
		props.put("initialCheckboxStatus", getCheckboxStatus());
		props.put("initialSelectAllButtonVisible", isShowSelectAllButton());
		props.put("initialSelectedItems", getSelectedItems());
		props.put("itemsTotal", getItemsTotal());
		props.put("itemsType", _getLocalizedItemsType());
		props.put("orderDropdownItems", getOrderDropdownItems());

		String searchActionURL = getSearchActionURL();

		props.put("searchActionURL", searchActionURL);

		props.put(
			"searchContainerId", _namespace(namespace, getSearchContainerId()));

		String searchFormMethod = getSearchFormMethod();

		if (searchFormMethod.equals("GET") && (searchActionURL != null)) {
			props.put("searchData", _getParamsMap(searchActionURL));
		}

		props.put("searchFormMethod", searchFormMethod);
		props.put("searchFormName", _namespace(namespace, getSearchFormName()));
		props.put("searchInputAutoFocus", isSearchInputAutoFocus());
		props.put(
			"searchInputName", _namespace(namespace, getSearchInputName()));
		props.put("searchResultsTitle", getSearchResultsTitle());
		props.put("searchValue", getSearchValue());
		props.put("selectAllURL", getSelectAllURL());
		props.put("selectable", isSelectable());
		props.put("showCreationMenu", isShowCreationMenu());
		props.put("showInfoButton", isShowInfoButton());
		props.put("showResultsBar", isShowResultsBar());
		props.put("showSearch", isShowSearch());
		props.put("sortingOrder", getSortingOrder());
		props.put("sortingURL", getSortingURL());
		props.put("supportsBulkActions", isSupportsBulkActions());
		props.put("viewTypeItems", getViewTypeItems());

		Map<String, Object> preparedProps = super.prepareProps(props);

		if (!preparedProps.containsKey("additionalProps") &&
			(_managementToolbarDisplayContext != null)) {

			Map<String, Object> additionalProps =
				_managementToolbarDisplayContext.getAdditionalProps();

			if (additionalProps != null) {
				preparedProps.put("additionalProps", additionalProps);
			}
		}

		return preparedProps;
	}

	@Override
	protected int processStartTag() throws Exception {
		super.processStartTag();

		JspWriter jspWriter = pageContext.getOut();

		Boolean active = !Objects.equals(getCheckboxStatus(), "unchecked");

		jspWriter.write("<nav class=\"management-bar navbar navbar-expand-md");

		if (active) {
			jspWriter.write(" management-bar-primary navbar-nowrap");
		}
		else {
			jspWriter.write(" management-bar-light");
		}

		jspWriter.write("\"><div class=\"container-fluid ");
		jspWriter.write("container-fluid-max-xxxl\"><ul class=\"navbar-nav\">");

		ResourceBundle resourceBundle = TagResourceBundleUtil.getResourceBundle(
			pageContext);

		Boolean disabled = isDisabled();

		Integer itemsTotal = getItemsTotal();
		String localizedItemsType = _getLocalizedItemsType();
		Integer selectedItems = getSelectedItems();

		if (isSelectable()) {
			jspWriter.write("<li class=\"nav-item\"><div class=\"");
			jspWriter.write(
				"custom-control custom-checkbox\"><label><input disabled");

			if (active) {
				jspWriter.write(" checked");
			}

			jspWriter.write(" aria-label=\"");

			if (active) {
				jspWriter.write(
					LanguageUtil.format(
						resourceBundle,
						"clear-selection.-there-are-currently-x-of-x-x-" +
							"selected",
						new Object[] {
							selectedItems, itemsTotal, localizedItemsType
						}));
			}
			else {
				jspWriter.write(
					LanguageUtil.format(
						resourceBundle, "select-all-x-on-the-page",
						new Object[] {localizedItemsType}));
			}

			jspWriter.write(
				"\" class=\"custom-control-input\" type=\"checkbox");
			jspWriter.write("\" /><span class=\"custom-control-label\">");
			jspWriter.write("</span></label></div></li>");
		}

		IconTag iconTag;

		if (active) {
			jspWriter.write("<li class=\"nav-item\"><span class=\"navbar-text");
			jspWriter.write("\">");

			String selectedItemslabel = LanguageUtil.format(
				resourceBundle, "x-of-x-x-selected",
				new Object[] {selectedItems, itemsTotal, localizedItemsType});

			if (itemsTotal == selectedItems) {
				jspWriter.write(
					LanguageUtil.get(resourceBundle, "all-selected"));
				jspWriter.write(" (");
				jspWriter.write(selectedItemslabel);
				jspWriter.write(")");
			}
			else {
				jspWriter.write(selectedItemslabel);
			}

			jspWriter.write("</span></li>");

			if (isSupportsBulkActions()) {
				jspWriter.write("<li class=\"nav-item nav-item-shrink\">");

				String clearSelectionURL = getClearSelectionURL();

				if (clearSelectionURL == null) {
					jspWriter.write("<button class=\"btn btn-unstyled");
					jspWriter.write(" nav-link\" type=\"button\"><span class=");
					jspWriter.write("\"text-truncate-inline\"><span class=\"");
					jspWriter.write("text-truncate\">");

					jspWriter.write(LanguageUtil.get(resourceBundle, "clear"));

					jspWriter.write("</span></span></button>");
				}
				else {
					LinkTag linkTag = new LinkTag();

					linkTag.setCssClass("nav-link");
					linkTag.setHref(clearSelectionURL);
					linkTag.setLabel("clear");

					linkTag.doTag(pageContext);
				}

				jspWriter.write("</li>");

				if (isShowSelectAllButton()) {
					jspWriter.write("<li class=\"nav-item nav-item-shrink\">");

					String selectAllURL = getSelectAllURL();

					if (selectAllURL == null) {
						jspWriter.write("<button class=\"btn btn-unstyled");
						jspWriter.write(" nav-link\" type=\"button\"><span");
						jspWriter.write(" class=\"text-truncate-inline\"><");
						jspWriter.write("span class=\"text-truncate\">");

						jspWriter.write(
							LanguageUtil.get(resourceBundle, "select-all"));

						jspWriter.write("</span></span></button>");
					}
					else {
						LinkTag linkTag = new LinkTag();

						linkTag.setCssClass("nav-link");
						linkTag.setHref(selectAllURL);
						linkTag.setLabel("select-all");

						linkTag.doTag(pageContext);
					}

					jspWriter.write("</li>");
				}
			}
		}

		if (!active && ListUtil.isNotEmpty(getFilterDropdownItems())) {
			jspWriter.write("<li class=\"nav-item\"><div class=\"dropdown\">");
			jspWriter.write("<button class=\"btn btn-unstyled ");
			jspWriter.write("dropdown-toggle ml-2 mr-2 nav-link\"");

			if (disabled) {
				jspWriter.write(" disabled");
			}

			jspWriter.write(" type=\"button\"><span class=\"");
			jspWriter.write("navbar-breakpoint-down-d-none\"><span class=\"");
			jspWriter.write("inline-item inline-item-before\">");

			iconTag = new IconTag();

			iconTag.setSymbol("filter");

			iconTag.doTag(pageContext);

			jspWriter.write("</span><span class=\"navbar-text-truncate\">");
			jspWriter.write(LanguageUtil.get(resourceBundle, "filter"));
			jspWriter.write("</span>");

			iconTag = new IconTag();

			iconTag.setCssClass("inline-item inline-item-after");
			iconTag.setSymbol("caret-bottom");

			iconTag.doTag(pageContext);

			jspWriter.write("</span><span class=\"navbar-breakpoint-d-none\">");

			iconTag = new IconTag();

			iconTag.setSymbol("filter");

			iconTag.doTag(pageContext);

			jspWriter.write("</span></button></div></li>");
		}

		List<DropdownItem> orderDropdownItems = getOrderDropdownItems();

		if (!active && (orderDropdownItems != null) &&
			(orderDropdownItems.size() > 1)) {

			jspWriter.write("<li class=\"nav-item\"><div class=\"dropdown\">");
			jspWriter.write("<button class=\"btn btn-unstyled dropdown-toggle");
			jspWriter.write(" ml-2 mr-2 nav-link\"");

			if (disabled) {
				jspWriter.write(" disabled");
			}

			jspWriter.write(" type=\"button\"><span class=\"");
			jspWriter.write("navbar-breakpoint-down-d-none\"><span class=\"");
			jspWriter.write("inline-item inline-item-before\">");

			iconTag = new IconTag();

			String orderSymbol = "order-list-down";

			if (Objects.equals(getSortingOrder(), "asc")) {
				orderSymbol = "order-list-up";
			}

			iconTag.setSymbol(orderSymbol);

			iconTag.doTag(pageContext);

			jspWriter.write("</span><span class=\"navbar-text-truncate\">");
			jspWriter.write(LanguageUtil.get(resourceBundle, "order[sort]"));
			jspWriter.write("</span>");

			iconTag = new IconTag();

			iconTag.setCssClass("inline-item inline-item-after");
			iconTag.setSymbol("caret-bottom");

			iconTag.doTag(pageContext);

			jspWriter.write("</span><span class=\"navbar-breakpoint-d-none\">");

			iconTag = new IconTag();

			iconTag.setSymbol(orderSymbol);

			iconTag.doTag(pageContext);

			jspWriter.write("</span></button></div></li>");
		}

		Boolean showOrderToggle =
			((orderDropdownItems != null) &&
			 (orderDropdownItems.size() == 1)) ||
			ListUtil.isEmpty(orderDropdownItems);

		if ((getSortingURL() != null) && showOrderToggle) {
			jspWriter.write("<li class=\"nav-item\">");

			LinkTag linkTag = new LinkTag();

			linkTag.setCssClass("nav-link nav-link-monospaced");
			linkTag.setDisplayType("unstyled");

			if (Objects.equals(getSortingOrder(), "desc")) {
				linkTag.setIcon("order-list-down");
			}
			else {
				linkTag.setIcon("order-list-up");
			}

			linkTag.doTag(pageContext);

			jspWriter.write("</li>");
		}

		jspWriter.write("</ul>");

		List<DropdownItem> actionDropdownItems = getActionDropdownItems();

		if (active && (actionDropdownItems != null) &&
			!actionDropdownItems.isEmpty()) {

			jspWriter.write("<ul class=\"navbar-nav\">");

			for (DropdownItem actionDropdownItem : actionDropdownItems) {
				jspWriter.write(
					"<li class=\"nav-item navbar-breakpoint-down-d-none\">");

				LinkTag linkTag = new LinkTag();

				linkTag.setCssClass("nav-link nav-link-monospaced");
				linkTag.setDisplayType("unstyled");
				linkTag.setIcon((String)actionDropdownItem.get("icon"));

				linkTag.doTag(pageContext);

				jspWriter.write("</li>");
			}

			jspWriter.write("<li class=\"nav-item\"><div class=\"dropdown\">");
			jspWriter.write("<button class=\"dropdown-toggle nav-link");
			jspWriter.write(" nav-link-monospaced btn btn-monospaced");
			jspWriter.write(" btn-unstyled\" type=\"button\">");

			IconTag icon = new IconTag();

			icon.setSymbol("ellipsis-v");

			icon.doTag(pageContext);

			jspWriter.write("</div></li></ul>");
		}

		String searchValue = getSearchValue();

		if (!active && isShowSearch()) {
			jspWriter.write("<div class=\"navbar-form navbar-form-autofit ");
			jspWriter.write(" navbar-overlay navbar-overlay-sm-down\"><div");
			jspWriter.write(" class=\"container-fluid ");
			jspWriter.write("container-fluid-max-xxxl\"><form");

			String searchActionURL = getSearchActionURL();

			if (searchActionURL != null) {
				jspWriter.write(" action=\"");
				jspWriter.write(HtmlUtil.escapeAttribute(searchActionURL));
				jspWriter.write("\"");
			}

			String searchFormMethod = getSearchFormMethod();

			if (searchFormMethod != null) {
				jspWriter.write(" method=\"");
				jspWriter.write(searchFormMethod);
				jspWriter.write("\"");
			}

			String searchFormName = getSearchFormName();
			String namespace = getNamespace();

			if (searchFormName != null) {
				jspWriter.write(" name=\"");
				jspWriter.write(namespace + searchFormName);
				jspWriter.write("\"");
			}

			jspWriter.write(" role=\"search\"><div class=\"input-group\"><div");
			jspWriter.write(" class=\"input-group-item\"><input class=\"");
			jspWriter.write("form-control form-control input-group-inset");
			jspWriter.write(" input-group-inset-after\" disabled");

			String searchInputName = getSearchInputName();

			if (searchInputName != null) {
				jspWriter.write(" name=\"");
				jspWriter.write(namespace + searchInputName);
				jspWriter.write("\"");
			}

			jspWriter.write(" placeholder=\"");
			jspWriter.write(
				LanguageUtil.get(
					resourceBundle,
					FeatureFlagManagerUtil.isEnabled("LPD-11313") ? "search" :
						"search-for"));
			jspWriter.write("\" type=\"text\"");

			if (searchValue != null) {
				jspWriter.write(" value=\"");
				jspWriter.write(HtmlUtil.escapeAttribute(searchValue));
				jspWriter.write("\"");
			}

			jspWriter.write(" /><span class=\"input-group-inset-item");
			jspWriter.write(" input-group-inset-item-after\"><button class=\"");
			jspWriter.write(" navbar-breakpoint-d-none btn btn-monospaced");
			jspWriter.write(" btn-unstyled\" disabled type=\"button\">");

			iconTag = new IconTag();

			iconTag.setSymbol("times");

			iconTag.doTag(pageContext);

			jspWriter.write("</button><button aria-label=\"");
			jspWriter.write(LanguageUtil.get(resourceBundle, "search"));
			jspWriter.write("\" class=\"btn btn-monospaced");
			jspWriter.write(" btn-unstyled\" disabled type=\"submit\">");

			iconTag = new IconTag();

			iconTag.setSymbol("search");

			iconTag.doTag(pageContext);

			jspWriter.write("</button></span></div></div></form></div></div>");
		}

		if (!active) {
			jspWriter.write("<ul class=\"navbar-nav\"><li class=\"nav-item");
			jspWriter.write(" navbar-breakpoint-d-none\"><button class=\"");
			jspWriter.write("nav-link nav-link-monospaced btn btn-monospaced");
			jspWriter.write(" btn-unstyled\" type=\"button\">");

			iconTag = new IconTag();

			iconTag.setSymbol("search");

			iconTag.doTag(pageContext);

			jspWriter.write("</button></li>");

			if (getViewTypeItems() != null) {
				jspWriter.write("<li class=\"nav-item\"><div class=\"dropdown");
				jspWriter.write("\"><button aria-label=\"");
				jspWriter.write(
					LanguageUtil.get(resourceBundle, "show-view-options"));
				jspWriter.write("\" class=\"dropdown-toggle nav-link");
				jspWriter.write(" btn btn-unstyled\" type=\"button\">");

				for (ViewTypeItem viewTypeItem : getViewTypeItems()) {
					if ((Boolean)viewTypeItem.get("active")) {
						iconTag = new IconTag();

						iconTag.setSymbol((String)viewTypeItem.get("icon"));

						iconTag.doTag(pageContext);

						iconTag = new IconTag();

						iconTag.setCssClass("inline-item inline-item-after");
						iconTag.setSymbol("caret-double-l");

						iconTag.doTag(pageContext);

						break;
					}
				}

				jspWriter.write("</button></div></li>");
			}

			if (isShowCreationMenu()) {
				jspWriter.write("<li class=\"nav-item\">");

				LinkTag linkTag = new LinkTag();

				linkTag.setCssClass(
					"d-md-none nav-btn nav-btn-monospaced btn btn-primary");

				linkTag.setIcon("plus");

				linkTag.doTag(pageContext);

				jspWriter.write("</li><li class=\"nav-item\">");

				linkTag = new LinkTag();

				linkTag.setCssClass(
					"nav-btn d-md-flex d-none pl-4 pr-4 btn btn-primary");
				linkTag.setLabel(LanguageUtil.get(resourceBundle, "new"));

				linkTag.doTag(pageContext);

				jspWriter.write("</li>");
			}

			if (isShowInfoButton()) {
				jspWriter.write("<li class=\"nav-item\"><button class=\"");
				jspWriter.write(" nav-link nav-link-monospaced btn");
				jspWriter.write(" btn-monospaced btn-unstyled\" type=\"button");
				jspWriter.write("\">");

				iconTag = new IconTag();

				iconTag.setSymbol("info-circle-open");

				iconTag.doTag(pageContext);

				jspWriter.write("</button></li>");
			}

			jspWriter.write("</ul>");
		}

		jspWriter.write("</div></nav>");

		if (isShowResultsBar()) {
			jspWriter.write("<nav class=\"subnav-tbar subnav-tbar-primary");
			jspWriter.write(" tbar tbar-inline-xs-down\"><div class=\"");
			jspWriter.write("container-fluid container-fluid-max-xxxl\">");
			jspWriter.write("<ul class=\"tbar-nav tbar-nav-wrap\"><li ");
			jspWriter.write("class=\"tbar-item");

			List<LabelItem> filterLabelItems = getFilterLabelItems();

			if (ListUtil.isEmpty(filterLabelItems)) {
				jspWriter.write(" tbar-item-expand");
			}

			jspWriter.write("\"><div class=\"tbar-section\"><span class=\"");
			jspWriter.write("component-text text-truncate-inline\"><span");
			jspWriter.write(" class=\"text-truncate\">");

			String searchValueHTML =
				"<strong>\"" + HtmlUtil.escape(searchValue) + "\"</strong>";

			jspWriter.write(
				LanguageUtil.format(
					resourceBundle,
					_getResultsLanguageKey(
						ListUtil.isNotEmpty(filterLabelItems), getItemsTotal(),
						searchValue),
					new Object[] {getItemsTotal(), searchValueHTML}));

			jspWriter.write("</span></span></div></li>");

			if (filterLabelItems != null) {
				for (int i = 0; i < filterLabelItems.size(); i++) {
					_writeLabelItem(
						jspWriter, filterLabelItems.get(i),
						i == (filterLabelItems.size() - 1));
				}
			}

			jspWriter.write("<li class=\"tbar-item\"><div class=\"");
			jspWriter.write("tbar-section\">");

			LinkTag linkTag = new LinkTag();

			linkTag.setCssClass("component-link tbar-link");
			linkTag.setHref(HtmlUtil.escapeAttribute(getClearResultsURL()));
			linkTag.setLabel(LanguageUtil.get(resourceBundle, "clear"));

			linkTag.doTag(pageContext);

			jspWriter.write("</div></li></ul></div></nav>");
		}

		String searchResultsTitle = getSearchResultsTitle();

		if (isShowResultsBar() && Validator.isNotNull(searchResultsTitle)) {
			jspWriter.write("<div class=\"c-mt-4 container-fluid ");
			jspWriter.write("container-fluid-max-xl\"><h3>");
			jspWriter.write(searchResultsTitle);
			jspWriter.write("<h3></div>");
		}

		return SKIP_BODY;
	}

	private String _getLocalizedItemsType() {
		String itemsType = getItemsType();

		if (Validator.isNotNull(itemsType)) {
			return LanguageUtil.get(
				TagResourceBundleUtil.getResourceBundle(pageContext),
				itemsType);
		}

		return itemsType;
	}

	private Map<String, List<String>> _getParamsMap(String url) {
		Map<String, List<String>> searchData = new HashMap<>();

		String[] parameters = StringUtil.split(
			HttpComponentsUtil.getQueryString(url), CharPool.AMPERSAND);

		for (String parameter : parameters) {
			if (parameter.length() == 0) {
				continue;
			}

			String[] parameterParts = StringUtil.split(
				parameter, CharPool.EQUAL);

			if (ArrayUtil.isEmpty(parameterParts)) {
				continue;
			}

			String parameterName = parameterParts[0];

			String parameterValue = StringPool.BLANK;

			if (parameterParts.length > 1) {
				parameterValue = parameterParts[1];
			}

			parameterValue = HttpComponentsUtil.decodeURL(parameterValue);

			List<String> parameterValues = searchData.get(parameterName);

			if (parameterValues == null) {
				parameterValues = new LinkedList<>();

				searchData.put(parameterName, parameterValues);
			}

			parameterValues.add(parameterValue);
		}

		return searchData;
	}

	private String _getResultsLanguageKey(
		boolean hasFilters, int itemsTotal, String searchValue) {

		if (Validator.isNull(searchValue)) {
			if (hasFilters) {
				if (itemsTotal == 1) {
					return "x-result-found-with-filters";
				}

				return "x-results-found-with-filters";
			}

			if (itemsTotal == 1) {
				return "x-result-found";
			}

			return "x-results-found";
		}

		if (hasFilters) {
			if (itemsTotal == 1) {
				return "x-result-found-for-x-with-filters";
			}

			return "x-results-found-for-x-with-filters";
		}

		if (itemsTotal == 1) {
			return "x-result-found-for-x";
		}

		return "x-results-found-for-x";
	}

	private String _namespace(String namespace, String prop) {
		if (prop == null) {
			return null;
		}

		if (namespace == null) {
			return prop;
		}

		return namespace + prop;
	}

	private void _writeLabelItem(
			JspWriter jspWriter, LabelItem labelItem, boolean expand)
		throws Exception {

		jspWriter.write("<li class=\"tbar-item");

		if (expand) {
			jspWriter.write(" tbar-item-expand");
		}

		jspWriter.write("\"><div class=\"tbar-section\">");

		LabelTag labelTag = new LabelTag();

		labelTag.setCssClass("component-label tbar-label");
		labelTag.setDismissible((boolean)labelItem.get("dismissible"));
		labelTag.setDisplayType("unstyled");
		labelTag.setLabel((String)labelItem.get("label"));

		labelTag.doTag(pageContext);

		jspWriter.write("</div></li>");
	}

	private static final String _ATTRIBUTE_NAMESPACE =
		"clay:management-toolbar:";

	private List<DropdownItem> _actionDropdownItems;
	private String _checkboxStatus = "unchecked";
	private String _clearResultsURL;
	private String _clearSelectionURL;
	private String _contentRenderer;
	private CreationMenu _creationMenu;
	private Boolean _disabled;
	private List<DropdownItem> _filterDropdownItems;
	private List<LabelItem> _filterLabelItems;
	private String _infoPanelId;
	private Integer _itemsTotal;
	private String _itemsType = "items";
	private ManagementToolbarDisplayContext _managementToolbarDisplayContext;
	private String _namespace;
	private List<DropdownItem> _orderDropdownItems;
	private String _searchActionURL;
	private String _searchContainerId;
	private String _searchFormMethod;
	private String _searchFormName;
	private Boolean _searchInputAutoFocus;
	private String _searchInputName;
	private String _searchResultsTitle;
	private String _searchValue;
	private Boolean _selectable;
	private String _selectAllURL;
	private Integer _selectedItems;
	private Boolean _showAdvancedSearch;
	private Boolean _showCreationMenu;
	private Boolean _showFiltersDoneButton;
	private Boolean _showInfoButton;
	private Boolean _showResultsBar = false;
	private Boolean _showSearch;
	private Boolean _showSelectAllButton = false;
	private String _sortingOrder;
	private String _sortingURL;
	private String _spritemap;
	private Boolean _supportsBulkActions;
	private List<ViewTypeItem> _viewTypeItems;

}