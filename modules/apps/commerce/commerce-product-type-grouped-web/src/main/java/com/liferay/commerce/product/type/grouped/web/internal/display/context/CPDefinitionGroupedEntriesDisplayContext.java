/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.grouped.web.internal.display.context;

import com.liferay.commerce.product.display.context.BaseCPDefinitionsSearchContainerDisplayContext;
import com.liferay.commerce.product.item.selector.criterion.CPDefinitionItemSelectorCriterion;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.type.CPType;
import com.liferay.commerce.product.type.grouped.model.CPDefinitionGroupedEntry;
import com.liferay.commerce.product.type.grouped.service.CPDefinitionGroupedEntryService;
import com.liferay.commerce.product.type.grouped.web.internal.util.GroupedCPTypeUtil;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Andrea Di Giorgi
 */
public class CPDefinitionGroupedEntriesDisplayContext
	extends BaseCPDefinitionsSearchContainerDisplayContext
		<CPDefinitionGroupedEntry> {

	public CPDefinitionGroupedEntriesDisplayContext(
		ActionHelper actionHelper, HttpServletRequest httpServletRequest,
		CPDefinitionGroupedEntryService cpDefinitionGroupedEntryService,
		ItemSelector itemSelector) {

		super(
			actionHelper, httpServletRequest,
			CPDefinitionGroupedEntry.class.getSimpleName());

		_cpDefinitionGroupedEntryService = cpDefinitionGroupedEntryService;
		_itemSelector = itemSelector;

		setDefaultOrderByCol("priority");
		setDefaultOrderByType("asc");
	}

	public CPDefinitionGroupedEntry getCPDefinitionGroupedEntry()
		throws PortalException {

		if (_cpDefinitionGroupedEntry != null) {
			return _cpDefinitionGroupedEntry;
		}

		_cpDefinitionGroupedEntry =
			_cpDefinitionGroupedEntryService.getCPDefinitionGroupedEntry(
				ParamUtil.getLong(
					cpRequestHelper.getRenderRequest(),
					"cpDefinitionGroupedEntryId"));

		return _cpDefinitionGroupedEntry;
	}

	public Sort getCPDefinitionGroupedEntrySort(
		String orderByCol, String orderByType) {

		boolean reverse = true;

		if (orderByType.equals("asc")) {
			reverse = false;
		}

		Sort sort = null;

		if (orderByCol.equals("priority")) {
			sort = SortFactoryUtil.create("priority_Number_sortable", reverse);
		}
		else if (orderByCol.equals("quantity")) {
			sort = SortFactoryUtil.create("quantity_Number_sortable", reverse);
		}

		return sort;
	}

	public String getItemSelectorUrl() throws PortalException {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(
				cpRequestHelper.getRenderRequest());

		CPDefinitionItemSelectorCriterion cpDefinitionItemSelectorCriterion =
			new CPDefinitionItemSelectorCriterion();

		cpDefinitionItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			Collections.<ItemSelectorReturnType>singletonList(
				new UUIDItemSelectorReturnType()));

		PortletURL itemSelectorURL = _itemSelector.getItemSelectorURL(
			requestBackedPortletURLFactory, "productDefinitionsSelectItem",
			cpDefinitionItemSelectorCriterion);

		long cpDefinitionId = getCPDefinitionId();

		if (cpDefinitionId > 0) {
			itemSelectorURL.setParameter(
				"cpDefinitionId", String.valueOf(cpDefinitionId));

			String checkedCPDefinitionIds = StringUtil.merge(
				_getCheckedCPDefinitionIds(cpDefinitionId));

			String disabledCPDefinitionIds = StringUtil.merge(
				_getDisabledCPDefinitionIds(cpDefinitionId));

			itemSelectorURL.setParameter(
				"checkedCPDefinitionIds", checkedCPDefinitionIds);
			itemSelectorURL.setParameter(
				"disabledCPDefinitionIds", disabledCPDefinitionIds);
		}

		return itemSelectorURL.toString();
	}

	public String getLabel(Locale locale, String key) {
		return ResourceBundleUtil.getString(_getResourceBundle(locale), key);
	}

	@Override
	public PortletURL getPortletURL() throws PortalException {
		return PortletURLBuilder.create(
			super.getPortletURL()
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_definition"
		).setParameter(
			"screenNavigationCategoryKey", getScreenNavigationCategoryKey()
		).buildPortletURL();
	}

	@Override
	public String getScreenNavigationCategoryKey() {
		CPType cpType = null;

		try {
			cpType = getCPType();
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		if (cpType != null) {
			return cpType.getName();
		}

		return super.getScreenNavigationCategoryKey();
	}

	@Override
	public SearchContainer<CPDefinitionGroupedEntry> getSearchContainer()
		throws PortalException {

		if (searchContainer != null) {
			return searchContainer;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		searchContainer = new SearchContainer<>(
			liferayPortletRequest, getPortletURL(), null,
			"no-grouped-entries-were-found");

		searchContainer.setOrderByCol(getOrderByCol());
		searchContainer.setOrderByComparator(
			GroupedCPTypeUtil.getCPDefinitionGroupedEntryOrderByComparator(
				getOrderByCol(), getOrderByType()));
		searchContainer.setOrderByType(getOrderByType());
		searchContainer.setResultsAndTotal(
			() ->
				_cpDefinitionGroupedEntryService.getCPDefinitionGroupedEntries(
					themeDisplay.getCompanyId(), getCPDefinitionId(),
					getKeywords(), searchContainer.getStart(),
					searchContainer.getEnd(),
					getCPDefinitionGroupedEntrySort(
						getOrderByCol(), getOrderByType())),
			_cpDefinitionGroupedEntryService.getCPDefinitionGroupedEntriesCount(
				themeDisplay.getCompanyId(), getCPDefinitionId(),
				getKeywords()));
		searchContainer.setRowChecker(getRowChecker());

		return searchContainer;
	}

	private long[] _getCheckedCPDefinitionIds(long cpDefinitionId)
		throws PortalException {

		List<Long> cpDefinitionIdsList = new ArrayList<>();

		List<CPDefinitionGroupedEntry> cpDefinitionGroupedEntries =
			_getCPDefinitionGroupedEntries(cpDefinitionId);

		for (CPDefinitionGroupedEntry cpDefinitionGroupedEntry :
				cpDefinitionGroupedEntries) {

			cpDefinitionIdsList.add(
				cpDefinitionGroupedEntry.getEntryCPDefinitionId());
		}

		if (!cpDefinitionIdsList.isEmpty()) {
			return ArrayUtil.toLongArray(cpDefinitionIdsList);
		}

		return new long[0];
	}

	private List<CPDefinitionGroupedEntry> _getCPDefinitionGroupedEntries(
			long cpDefinitionId)
		throws PortalException {

		int total =
			_cpDefinitionGroupedEntryService.getCPDefinitionGroupedEntriesCount(
				cpDefinitionId);

		return _cpDefinitionGroupedEntryService.getCPDefinitionGroupedEntries(
			cpDefinitionId, 0, total, null);
	}

	private long[] _getDisabledCPDefinitionIds(long cpDefinitionId)
		throws PortalException {

		List<Long> cpDefinitionIdsList = new ArrayList<>();

		List<CPDefinitionGroupedEntry> cpDefinitionGroupedEntries =
			_getCPDefinitionGroupedEntries(cpDefinitionId);

		for (CPDefinitionGroupedEntry cpDefinitionGroupedEntry :
				cpDefinitionGroupedEntries) {

			cpDefinitionIdsList.add(
				cpDefinitionGroupedEntry.getCPDefinitionId());
		}

		if (!cpDefinitionIdsList.isEmpty()) {
			return ArrayUtil.toLongArray(cpDefinitionIdsList);
		}

		return new long[0];
	}

	private ResourceBundle _getResourceBundle(Locale locale) {
		return ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPDefinitionGroupedEntriesDisplayContext.class);

	private CPDefinitionGroupedEntry _cpDefinitionGroupedEntry;
	private final CPDefinitionGroupedEntryService
		_cpDefinitionGroupedEntryService;
	private final ItemSelector _itemSelector;

}