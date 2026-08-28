/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.omni.search.web.internal.display.context;

import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.product.navigation.omni.search.web.internal.constants.ProductNavigationOmniSearchPortletKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Marcos Castro
 * @author Thiago Buarque
 */
public class OmniSearchDisplayContext {

	public OmniSearchDisplayContext(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	public String getResultsURL() {
		LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
			_httpServletRequest,
			ProductNavigationOmniSearchPortletKeys.
				PRODUCT_NAVIGATION_OMNI_SEARCH,
			PortletRequest.RESOURCE_PHASE);

		liferayPortletURL.setResourceID("/omni_search/omni_search_results");

		return liferayPortletURL.toString();
	}

	private final HttpServletRequest _httpServletRequest;

}