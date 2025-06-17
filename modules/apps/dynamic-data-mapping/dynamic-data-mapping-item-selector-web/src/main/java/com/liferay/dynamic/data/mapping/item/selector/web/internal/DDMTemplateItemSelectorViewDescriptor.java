/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.item.selector.web.internal;

import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.dynamic.data.mapping.item.selector.DDMTemplateItemSelectorCriterion;
import com.liferay.dynamic.data.mapping.item.selector.DDMTemplateItemSelectorReturnType;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateServiceUtil;
import com.liferay.dynamic.data.mapping.util.DDMUtil;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.constants.ItemSelectorPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class DDMTemplateItemSelectorViewDescriptor
	implements ItemSelectorViewDescriptor<DDMTemplate> {

	public DDMTemplateItemSelectorViewDescriptor(
		DDMTemplateItemSelectorCriterion ddmTemplateItemSelectorCriterion,
		HttpServletRequest httpServletRequest, PortletURL portletURL) {

		_ddmTemplateItemSelectorCriterion = ddmTemplateItemSelectorCriterion;
		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public ItemDescriptor getItemDescriptor(DDMTemplate ddmTemplate) {
		return new DDMTemplateItemDescriptor(ddmTemplate, _httpServletRequest);
	}

	@Override
	public ItemSelectorReturnType getItemSelectorReturnType() {
		return new DDMTemplateItemSelectorReturnType();
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, ItemSelectorPortletKeys.ITEM_SELECTOR,
			"select-ddm-template-order-by-col", "modified-date");

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
			"select-ddm-template-order-by-type", "desc");

		return _orderByType;
	}

	@Override
	public SearchContainer<DDMTemplate> getSearchContainer()
		throws PortalException {

		SearchContainer<DDMTemplate> ddmTemplateSearchContainer =
			new SearchContainer<>(
				(PortletRequest)_httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_REQUEST),
				_portletURL, null, "there-are-no-templates");

		if (ddmTemplateSearchContainer.isSearch()) {
			ddmTemplateSearchContainer.setEmptyResultsMessage(
				"no-templates-were-found");
		}

		ddmTemplateSearchContainer.setOrderByCol(getOrderByCol());
		ddmTemplateSearchContainer.setOrderByComparator(
			DDMUtil.getTemplateOrderByComparator(
				getOrderByCol(), getOrderByType()));
		ddmTemplateSearchContainer.setOrderByType(getOrderByType());

		long[] groupIds =
			SiteConnectedGroupGroupProviderUtil.
				getCurrentAndAncestorSiteAndDepotGroupIds(
					_themeDisplay.getScopeGroupId(), false, true);

		ddmTemplateSearchContainer.setResultsAndTotal(
			() -> DDMTemplateServiceUtil.search(
				_themeDisplay.getCompanyId(), groupIds,
				new long[] {PortalUtil.getClassNameId(DDMStructure.class)},
				new long[] {
					_ddmTemplateItemSelectorCriterion.getDDMStructureId()
				},
				_ddmTemplateItemSelectorCriterion.getClassNameId(),
				_getKeywords(), StringPool.BLANK, StringPool.BLANK,
				WorkflowConstants.STATUS_ANY,
				ddmTemplateSearchContainer.getStart(),
				ddmTemplateSearchContainer.getEnd(),
				ddmTemplateSearchContainer.getOrderByComparator()),
			DDMTemplateServiceUtil.searchCount(
				_themeDisplay.getCompanyId(), groupIds,
				new long[] {PortalUtil.getClassNameId(DDMStructure.class)},
				new long[] {
					_ddmTemplateItemSelectorCriterion.getDDMStructureId()
				},
				_ddmTemplateItemSelectorCriterion.getClassNameId(),
				_getKeywords(), StringPool.BLANK, StringPool.BLANK,
				WorkflowConstants.STATUS_ANY));

		return ddmTemplateSearchContainer;
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

	private final DDMTemplateItemSelectorCriterion
		_ddmTemplateItemSelectorCriterion;
	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private String _orderByCol;
	private String _orderByType;
	private final PortletURL _portletURL;
	private final ThemeDisplay _themeDisplay;

}