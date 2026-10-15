/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.collection.item.selector.web.internal;

import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.util.comparator.FragmentCollectionNameComparator;
import com.liferay.portal.kernel.util.OrderByComparator;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Rubén Pulido
 * @author Thiago Buarque
 */
public class SiteFragmentCollectionItemSelectorViewDescriptor
	extends BaseFragmentCollectionItemSelectorViewDescriptor {

	public SiteFragmentCollectionItemSelectorViewDescriptor(
		long[] groupIds, HttpServletRequest httpServletRequest,
		PortletURL portletURL) {

		super(groupIds, httpServletRequest, portletURL);
	}

	@Override
	public ItemDescriptor getItemDescriptor(
		FragmentCollection fragmentCollection) {

		return new FragmentCollectionItemDescriptor(fragmentCollection);
	}

	@Override
	protected OrderByComparator<FragmentCollection> getOrderByComparator(
		boolean orderByAsc) {

		return FragmentCollectionNameComparator.getInstance(orderByAsc);
	}

}