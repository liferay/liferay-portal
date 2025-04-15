/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Igor Beslic
 */
public class ViewDispatchLogManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public ViewDispatchLogManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<?> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemList.of(
			DropdownItemBuilder.putData(
				"action", "deleteDispatchLogs"
			).putData(
				"deleteDispatchLogsURL",
				PortletURLBuilder.createActionURL(
					liferayPortletResponse
				).setActionName(
					"/dispatch/edit_dispatch_log"
				).setNavigation(
					getNavigation()
				).buildString()
			).setIcon(
				"trash"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "delete")
			).setQuickAction(
				true
			).build());
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"modified-date", "status"};
	}

}