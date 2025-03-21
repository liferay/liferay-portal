/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.item.selector.web.internal.frontend.taglib.clay.servlet;

import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.frontend.taglib.clay.servlet.taglib.HorizontalCard;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;

import jakarta.portlet.PortletURL;

/**
 * @author Víctor Galán
 */
public class FragmentCollectionContributorHorizontalCard
	implements HorizontalCard {

	public FragmentCollectionContributorHorizontalCard(
		FragmentCollectionContributor fragmentCollectionContributor,
		PortletURL portletURL) {

		_fragmentCollectionContributor = fragmentCollectionContributor;
		_portletURL = portletURL;
	}

	@Override
	public String getHref() {
		return PortletURLBuilder.create(
			_portletURL
		).setParameter(
			"fragmentCollectionKey",
			_fragmentCollectionContributor.getFragmentCollectionKey()
		).buildString();
	}

	@Override
	public String getIcon() {
		return "box-container";
	}

	@Override
	public String getTitle() {
		return _fragmentCollectionContributor.getName();
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	private final FragmentCollectionContributor _fragmentCollectionContributor;
	private final PortletURL _portletURL;

}