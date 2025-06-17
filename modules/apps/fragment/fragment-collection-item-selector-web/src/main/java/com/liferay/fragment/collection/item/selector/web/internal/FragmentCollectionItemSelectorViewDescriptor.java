/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.collection.item.selector.web.internal;

import com.liferay.fragment.collection.item.selector.FragmentCollectionItemSelectorCriterion;
import com.liferay.fragment.collection.item.selector.FragmentCollectionItemSelectorReturnType;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionServiceUtil;
import com.liferay.fragment.util.comparator.FragmentCollectionNameComparator;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Rubén Pulido
 */
public class FragmentCollectionItemSelectorViewDescriptor
	implements ItemSelectorViewDescriptor<FragmentCollection> {

	public FragmentCollectionItemSelectorViewDescriptor(
		FragmentCollectionItemSelectorCriterion
			fragmentCollectionItemSelectorCriterion,
		long groupId, HttpServletRequest httpServletRequest,
		PortletURL portletURL) {

		_fragmentCollectionItemSelectorCriterion =
			fragmentCollectionItemSelectorCriterion;
		_groupId = groupId;
		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;
	}

	@Override
	public ItemDescriptor getItemDescriptor(
		FragmentCollection fragmentCollection) {

		return new FragmentCollectionItemDescriptor(fragmentCollection);
	}

	@Override
	public ItemSelectorReturnType getItemSelectorReturnType() {
		return new FragmentCollectionItemSelectorReturnType();
	}

	@Override
	public String[] getOrderByKeys() {
		return new String[] {"name"};
	}

	@Override
	public SearchContainer<FragmentCollection> getSearchContainer() {
		SearchContainer<FragmentCollection> searchContainer =
			new SearchContainer<>(
				_getPortletRequest(), _portletURL, null,
				"there-are-no-items-to-display");

		searchContainer.setOrderByCol(
			ParamUtil.getString(_httpServletRequest, "orderByCol", "name"));

		boolean orderByAsc = true;

		String orderByType = ParamUtil.getString(
			_httpServletRequest, "orderByType", "asc");

		if (orderByType.equals("desc")) {
			orderByAsc = false;
		}

		searchContainer.setOrderByType(orderByType);

		FragmentCollectionNameComparator fragmentCollectionNameComparator =
			FragmentCollectionNameComparator.getInstance(orderByAsc);

		String keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		if (Validator.isNull(keywords)) {
			searchContainer.setResultsAndTotal(
				() -> FragmentCollectionServiceUtil.getFragmentCollections(
					_fragmentCollectionItemSelectorCriterion.getGroupId(),
					searchContainer.getStart(), searchContainer.getEnd(),
					fragmentCollectionNameComparator),
				FragmentCollectionServiceUtil.getFragmentCollectionsCount(
					_fragmentCollectionItemSelectorCriterion.getGroupId()));
		}
		else {
			searchContainer.setResultsAndTotal(
				() -> FragmentCollectionServiceUtil.getFragmentCollections(
					new long[] {_groupId}, keywords, searchContainer.getStart(),
					searchContainer.getEnd(), fragmentCollectionNameComparator),
				FragmentCollectionServiceUtil.getFragmentCollectionsCount(
					_groupId, keywords));
		}

		return searchContainer;
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

	private PortletRequest _getPortletRequest() {
		return (PortletRequest)_httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST);
	}

	private final FragmentCollectionItemSelectorCriterion
		_fragmentCollectionItemSelectorCriterion;
	private final long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private final PortletURL _portletURL;

}