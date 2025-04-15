/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.portlets.web.internal.search;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletRequest;

/**
 * @author Jorge Ferrer
 */
public class PortletDisplayTerms extends DisplayTerms {

	public static final String CATEGORIES = "categories";

	public static final String NAME = "name";

	public PortletDisplayTerms(PortletRequest portletRequest) {
		super(portletRequest);

		categories = ParamUtil.getString(portletRequest, CATEGORIES);
		name = ParamUtil.getString(portletRequest, NAME);
	}

	public String getCategories() {
		return categories;
	}

	public String getName() {
		return name;
	}

	protected String categories;
	protected String name;

}