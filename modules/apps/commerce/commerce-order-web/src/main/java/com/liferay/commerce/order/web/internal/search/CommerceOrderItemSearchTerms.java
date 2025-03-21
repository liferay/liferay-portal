/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.search;

import com.liferay.portal.kernel.dao.search.DAOParamUtil;

import jakarta.portlet.PortletRequest;

/**
 * @author Andrea Di Giorgi
 */
public class CommerceOrderItemSearchTerms
	extends CommerceOrderItemDisplayTerms {

	public CommerceOrderItemSearchTerms(PortletRequest portletRequest) {
		super(portletRequest);

		sku = DAOParamUtil.getString(portletRequest, SKU);
		name = DAOParamUtil.getString(portletRequest, NAME);
	}

}