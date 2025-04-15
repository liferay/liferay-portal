/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.search;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletRequest;

/**
 * @author Andrea Di Giorgi
 */
public class CommerceOrderItemDisplayTerms extends DisplayTerms {

	public static final String NAME = "name";

	public static final String SKU = "sku";

	public CommerceOrderItemDisplayTerms(PortletRequest portletRequest) {
		super(portletRequest);

		sku = ParamUtil.getString(portletRequest, SKU);
		name = ParamUtil.getString(portletRequest, NAME);
	}

	public String getName() {
		return name;
	}

	public String getSku() {
		return sku;
	}

	protected String name;
	protected String sku;

}