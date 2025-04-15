/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.item.selector.web.internal.display.context;

import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.item.selector.web.internal.CPDefinitionItemSelectorView;
import com.liferay.commerce.product.item.selector.web.internal.search.CPDefinitionItemSelectorChecker;
import com.liferay.commerce.product.item.selector.web.internal.util.CPItemSelectorViewUtil;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPDefinitionService;
import com.liferay.commerce.product.type.CPType;
import com.liferay.commerce.product.type.CPTypeRegistry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.text.DateFormat;
import java.text.Format;

import java.util.List;
import java.util.Locale;

/**
 * @author Alessio Antonio Rendina
 */
public class CPDefinitionItemSelectorViewDisplayContext
	extends BaseCPItemSelectorViewDisplayContext<CPDefinition> {

	public CPDefinitionItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest, PortletURL portletURL,
		String itemSelectedEventName, CPDefinitionService cpDefinitionService,
		CPTypeRegistry cpTypeRegistry) {

		super(
			httpServletRequest, portletURL, itemSelectedEventName,
			CPDefinitionItemSelectorView.class.getSimpleName());

		_cpDefinitionService = cpDefinitionService;
		_cpTypeRegistry = cpTypeRegistry;

		setDefaultOrderByCol("name");
	}

	public long getCPDefinitionId() {
		return ParamUtil.getLong(httpServletRequest, "cpDefinitionId");
	}

	public CPType getCPType(String name) {
		return _cpTypeRegistry.getCPType(name);
	}

	public String getModifiedDate(
		CPDefinition cpDefinition, ThemeDisplay themeDisplay) {

		Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			DateFormat.MEDIUM, DateFormat.MEDIUM, themeDisplay.getLocale(),
			themeDisplay.getTimeZone());

		return dateTimeFormat.format(cpDefinition.getModifiedDate());
	}

	@Override
	public PortletURL getPortletURL() {
		PortletURL portletURL = super.getPortletURL();

		long cpDefinitionId = getCPDefinitionId();

		if (cpDefinitionId > 0) {
			portletURL.setParameter(
				"cpDefinitionId", String.valueOf(cpDefinitionId));
			portletURL.setParameter(
				"checkedCPDefinitionIds",
				ParamUtil.getString(
					httpServletRequest, "checkedCPDefinitionIds"));
		}

		long commerceChannelGroupId = ParamUtil.getLong(
			httpServletRequest, CPField.COMMERCE_CHANNEL_GROUP_ID);

		portletURL.setParameter(
			CPField.COMMERCE_CHANNEL_GROUP_ID,
			String.valueOf(commerceChannelGroupId));

		portletURL.setParameter(
			"ignoreCommerceAccountGroup",
			Boolean.toString(
				ParamUtil.getBoolean(
					httpServletRequest, "ignoreCommerceAccountGroup")));
		portletURL.setParameter(
			"singleSelection", Boolean.toString(isSingleSelection()));

		return portletURL;
	}

	@Override
	public SearchContainer<CPDefinition> getSearchContainer()
		throws PortalException {

		if (searchContainer != null) {
			return searchContainer;
		}

		searchContainer = new SearchContainer<>(
			liferayPortletRequest, getPortletURL(), null,
			"no-products-were-found");

		searchContainer.setOrderByCol(getOrderByCol());
		searchContainer.setOrderByComparator(
			CPItemSelectorViewUtil.getCPDefinitionOrderByComparator(
				getOrderByCol(), getOrderByType()));
		searchContainer.setOrderByType(getOrderByType());

		Sort sort = CPItemSelectorViewUtil.getCPDefinitionSort(
			getOrderByCol(), getOrderByType());

		BaseModelSearchResult<CPDefinition> cpDefinitionBaseModelSearchResult;

		long commerceChannelGroupId = ParamUtil.getLong(
			httpServletRequest, CPField.COMMERCE_CHANNEL_GROUP_ID);

		boolean ignoreCommerceAccountGroup = ParamUtil.getBoolean(
			httpServletRequest, "ignoreCommerceAccountGroup");

		if (commerceChannelGroupId != 0) {
			cpDefinitionBaseModelSearchResult =
				_cpDefinitionService.searchCPDefinitionsByChannelGroupId(
					cpRequestHelper.getCompanyId(), commerceChannelGroupId,
					getKeywords(), WorkflowConstants.STATUS_APPROVED,
					ignoreCommerceAccountGroup, searchContainer.getStart(),
					searchContainer.getEnd(), sort);
		}
		else {
			cpDefinitionBaseModelSearchResult =
				_cpDefinitionService.searchCPDefinitions(
					cpRequestHelper.getCompanyId(), getKeywords(),
					WorkflowConstants.STATUS_APPROVED,
					ignoreCommerceAccountGroup, searchContainer.getStart(),
					searchContainer.getEnd(), sort);
		}

		searchContainer.setResultsAndTotal(cpDefinitionBaseModelSearchResult);

		if (!isSingleSelection()) {
			searchContainer.setRowChecker(
				new CPDefinitionItemSelectorChecker(
					cpRequestHelper.getRenderResponse(),
					_getCheckedCPDefinitionIds(), getCPDefinitionId()));
		}

		return searchContainer;
	}

	public String getSku(CPDefinition cpDefinition, Locale locale) {
		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		if (cpInstances.size() > 1) {
			return LanguageUtil.get(locale, "multiple-skus");
		}

		if (cpInstances.isEmpty()) {
			return StringPool.BLANK;
		}

		CPInstance cpInstance = cpInstances.get(0);

		return cpInstance.getSku();
	}

	public boolean isSingleSelection() {
		return ParamUtil.getBoolean(httpServletRequest, "singleSelection");
	}

	private long[] _getCheckedCPDefinitionIds() {
		return ParamUtil.getLongValues(
			httpServletRequest, "checkedCPDefinitionIds");
	}

	private final CPDefinitionService _cpDefinitionService;
	private final CPTypeRegistry _cpTypeRegistry;

}