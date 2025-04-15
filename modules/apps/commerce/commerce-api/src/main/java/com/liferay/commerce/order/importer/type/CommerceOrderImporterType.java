/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.importer.type;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.importer.item.CommerceOrderImporterItem;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.List;
import java.util.Locale;

/**
 * @author Alessio Antonio Rendina
 */
public interface CommerceOrderImporterType {

	public Object getCommerceOrderImporterItem(
			HttpServletRequest httpServletRequest)
		throws PortalException;

	public String getCommerceOrderImporterItemParamName();

	public List<CommerceOrderImporterItem> getCommerceOrderImporterItems(
			CommerceOrder commerceOrder, FDSPagination fdsPagination,
			Object object)
		throws Exception;

	public int getCommerceOrderImporterItemsCount(Object object)
		throws Exception;

	public String getKey();

	public String getLabel(Locale locale);

	public default boolean isActive(CommerceOrder commerceOrder)
		throws PortalException {

		return true;
	}

	public void render(
			CommerceOrder commerceOrder, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException;

	public void renderCommerceOrderPreview(
			CommerceOrder commerceOrder, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException;

}