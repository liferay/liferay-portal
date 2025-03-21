/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author Alicia García
 */
public class KBArticleItemSelectorViewManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public KBArticleItemSelectorViewManagementToolbarDisplayContext(
			HttpServletRequest httpServletRequest,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			KBArticleItemSelectorViewDisplayContext
				kbArticleItemSelectorViewDisplayContext)
		throws Exception {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			kbArticleItemSelectorViewDisplayContext.getSearchContainer());

		_kbArticleItemSelectorViewDisplayContext =
			kbArticleItemSelectorViewDisplayContext;
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).setParameter(
			"scope", StringPool.BLANK
		).buildString();
	}

	@Override
	public String getSortingOrder() {
		if (Objects.equals(getOrderByCol(), "priority")) {
			return null;
		}

		return super.getSortingOrder();
	}

	@Override
	public Boolean isSelectable() {
		return false;
	}

	@Override
	protected String getDefaultDisplayStyle() {
		return "descriptive";
	}

	@Override
	protected String[] getOrderByKeys() {
		String[] orderColumns = {
			"priority", "modified-date", "title", "view-count"
		};

		if (_kbArticleItemSelectorViewDisplayContext.isSearch()) {
			orderColumns = ArrayUtil.append(orderColumns, "relevance");
		}

		return orderColumns;
	}

	private final KBArticleItemSelectorViewDisplayContext
		_kbArticleItemSelectorViewDisplayContext;

}