/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.catalog.web.internal.search;

import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;

import jakarta.portlet.PortletResponse;

/**
 * @author Alec Sloan
 */
public class CommerceCatalogChecker extends EmptyOnClickRowChecker {

	public CommerceCatalogChecker(PortletResponse portletResponse) {
		super(portletResponse);
	}

	@Override
	public boolean isDisabled(Object object) {
		CommerceCatalog commerceCatalog = (CommerceCatalog)object;

		return commerceCatalog.isSystem();
	}

}