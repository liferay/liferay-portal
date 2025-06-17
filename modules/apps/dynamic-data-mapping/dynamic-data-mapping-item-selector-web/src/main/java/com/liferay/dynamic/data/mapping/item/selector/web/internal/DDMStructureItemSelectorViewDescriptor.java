/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.item.selector.web.internal;

import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.dynamic.data.mapping.item.selector.DDMStructureItemSelectorCriterion;
import com.liferay.dynamic.data.mapping.item.selector.DDMStructureItemSelectorReturnType;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureServiceUtil;
import com.liferay.dynamic.data.mapping.util.DDMUtil;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.TableItemView;
import com.liferay.item.selector.constants.ItemSelectorPortletKeys;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class DDMStructureItemSelectorViewDescriptor
	implements ItemSelectorViewDescriptor<DDMStructure> {

	public DDMStructureItemSelectorViewDescriptor(
		DDMStructureItemSelectorCriterion ddmStructureItemSelectorCriterion,
		GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, PortletURL portletURL) {

		_ddmStructureItemSelectorCriterion = ddmStructureItemSelectorCriterion;
		_groupLocalService = groupLocalService;
		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public String getDefaultDisplayStyle() {
		return "list";
	}

	@Override
	public String[] getDisplayViews() {
		return new String[] {"list"};
	}

	@Override
	public ItemDescriptor getItemDescriptor(DDMStructure ddmStructure) {
		return new DDMStructureItemDescriptor(
			ddmStructure, _groupLocalService, _httpServletRequest);
	}

	@Override
	public ItemSelectorReturnType getItemSelectorReturnType() {
		return new DDMStructureItemSelectorReturnType();
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, ItemSelectorPortletKeys.ITEM_SELECTOR,
			"select-ddm-structure-order-by-col", "modified-date");

		return _orderByCol;
	}

	@Override
	public String[] getOrderByKeys() {
		return new String[] {"modified-date"};
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, ItemSelectorPortletKeys.ITEM_SELECTOR,
			"select-ddm-structure-order-by-type", "desc");

		return _orderByType;
	}

	@Override
	public SearchContainer<DDMStructure> getSearchContainer()
		throws PortalException {

		String emptyResultsMessage = "there-are-no-structures";

		if (Validator.isNotNull(_getKeywords())) {
			emptyResultsMessage = "no-structures-were-found";
		}

		SearchContainer<DDMStructure> ddmStructureSearchContainer =
			new SearchContainer<>(
				(PortletRequest)_httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_REQUEST),
				_portletURL, null, emptyResultsMessage);

		ddmStructureSearchContainer.setOrderByCol(getOrderByCol());
		ddmStructureSearchContainer.setOrderByComparator(
			DDMUtil.getStructureOrderByComparator(
				getOrderByCol(), getOrderByType()));
		ddmStructureSearchContainer.setOrderByType(getOrderByType());

		long[] groupIds;

		if (_ddmStructureItemSelectorCriterion.isSelectAncestorScopes()) {
			groupIds =
				SiteConnectedGroupGroupProviderUtil.
					getCurrentAndAncestorSiteAndDepotGroupIds(
						_themeDisplay.getScopeGroupId(), false, true);
		}
		else {
			groupIds = new long[] {_themeDisplay.getScopeGroupId()};
		}

		if (Validator.isNotNull(_getKeywords())) {
			ddmStructureSearchContainer.setResultsAndTotal(
				() -> DDMStructureServiceUtil.search(
					_themeDisplay.getCompanyId(), groupIds,
					_ddmStructureItemSelectorCriterion.getClassNameId(),
					_getKeywords(), WorkflowConstants.STATUS_ANY,
					ddmStructureSearchContainer.getStart(),
					ddmStructureSearchContainer.getEnd(),
					ddmStructureSearchContainer.getOrderByComparator()),
				DDMStructureServiceUtil.searchCount(
					_themeDisplay.getCompanyId(), groupIds,
					_ddmStructureItemSelectorCriterion.getClassNameId(),
					_getKeywords(), WorkflowConstants.STATUS_ANY));
		}
		else {
			ddmStructureSearchContainer.setResultsAndTotal(
				() -> DDMStructureServiceUtil.getStructures(
					_themeDisplay.getCompanyId(), groupIds,
					_ddmStructureItemSelectorCriterion.getClassNameId(),
					ddmStructureSearchContainer.getStart(),
					ddmStructureSearchContainer.getEnd(),
					ddmStructureSearchContainer.getOrderByComparator()),
				DDMStructureServiceUtil.getStructuresCount(
					_themeDisplay.getCompanyId(), groupIds,
					_ddmStructureItemSelectorCriterion.getClassNameId()));
		}

		return ddmStructureSearchContainer;
	}

	@Override
	public TableItemView getTableItemView(DDMStructure ddmStructure) {
		return new DDMStructureItemTableItemView(
			ddmStructure, _groupLocalService, _themeDisplay);
	}

	@Override
	public boolean isMultipleSelection() {
		return _ddmStructureItemSelectorCriterion.isMultiSelection();
	}

	@Override
	public boolean isShowBreadcrumb() {
		return false;
	}

	@Override
	public boolean isShowManagementToolbar() {
		return true;
	}

	@Override
	public boolean isShowSearch() {
		return true;
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	private final DDMStructureItemSelectorCriterion
		_ddmStructureItemSelectorCriterion;
	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private final PortletURL _portletURL;
	private final ThemeDisplay _themeDisplay;

}