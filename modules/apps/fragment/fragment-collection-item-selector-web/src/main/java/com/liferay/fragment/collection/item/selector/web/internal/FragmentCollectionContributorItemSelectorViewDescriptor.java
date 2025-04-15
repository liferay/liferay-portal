/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.collection.item.selector.web.internal;

import com.liferay.fragment.collection.item.selector.FragmentCollectionItemSelectorReturnType;
import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.util.comparator.FragmentCollectionContributorNameComparator;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Rubén Pulido
 */
public class FragmentCollectionContributorItemSelectorViewDescriptor
	implements ItemSelectorViewDescriptor<FragmentCollectionContributor> {

	public FragmentCollectionContributorItemSelectorViewDescriptor(
		FragmentCollectionContributorRegistry
			fragmentCollectionContributorRegistry,
		HttpServletRequest httpServletRequest, PortletURL portletURL) {

		_fragmentCollectionContributorRegistry =
			fragmentCollectionContributorRegistry;
		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;
	}

	@Override
	public ItemDescriptor getItemDescriptor(
		FragmentCollectionContributor fragmentCollectionContributor) {

		return new FragmentCollectionContributorItemDescriptor(
			fragmentCollectionContributor);
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
	public SearchContainer<FragmentCollectionContributor> getSearchContainer()
		throws PortalException {

		SearchContainer<FragmentCollectionContributor> searchContainer =
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

		List<FragmentCollectionContributor> fragmentCollectionContributors =
			_getFragmentCollectionContributors(orderByAsc);

		String keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		if (Validator.isNull(keywords)) {
			searchContainer.setResultsAndTotal(fragmentCollectionContributors);
		}
		else {
			searchContainer.setResultsAndTotal(
				ListUtil.filter(
					fragmentCollectionContributors,
					fragmentCollectionContributor -> {
						String lowerCaseName = StringUtil.toLowerCase(
							fragmentCollectionContributor.getName());

						return lowerCaseName.contains(
							StringUtil.toLowerCase(keywords));
					}));
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

	private List<FragmentCollectionContributor>
		_getFragmentCollectionContributors(boolean orderByAsc) {

		if (_fragmentCollectionContributorRegistry == null) {
			return Collections.emptyList();
		}

		List<FragmentCollectionContributor> fragmentCollectionContributors =
			_fragmentCollectionContributorRegistry.
				getFragmentCollectionContributors();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Collections.sort(
			fragmentCollectionContributors,
			new FragmentCollectionContributorNameComparator(
				themeDisplay.getLocale(), orderByAsc));

		return fragmentCollectionContributors;
	}

	private PortletRequest _getPortletRequest() {
		return (PortletRequest)_httpServletRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST);
	}

	private final FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;
	private final HttpServletRequest _httpServletRequest;
	private final PortletURL _portletURL;

}