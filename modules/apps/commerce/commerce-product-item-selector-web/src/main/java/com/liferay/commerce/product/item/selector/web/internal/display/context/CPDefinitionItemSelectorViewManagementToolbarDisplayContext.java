/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.item.selector.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class CPDefinitionItemSelectorViewManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public CPDefinitionItemSelectorViewManagementToolbarDisplayContext(
			CPDefinitionItemSelectorViewDisplayContext
				cpDefinitionItemSelectorViewDisplayContext,
			HttpServletRequest httpServletRequest,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws PortalException {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			cpDefinitionItemSelectorViewDisplayContext.getSearchContainer());

		_cpDefinitionItemSelectorViewDisplayContext =
			cpDefinitionItemSelectorViewDisplayContext;
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).buildString();
	}

	@Override
	public String getSearchContainerId() {
		return "cpDefinitions";
	}

	@Override
	public Boolean isSelectable() {
		return !_cpDefinitionItemSelectorViewDisplayContext.isSingleSelection();
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"list"};
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"name", "modified-date", "display-date"};
	}

	private final CPDefinitionItemSelectorViewDisplayContext
		_cpDefinitionItemSelectorViewDisplayContext;

}