/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.display.context;

import com.liferay.account.admin.web.internal.display.AccountEntryDisplay;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Albert Lee
 */
public class SelectAccountEntryManagementToolbarDisplayContext
	extends ViewAccountEntriesManagementToolbarDisplayContext {

	public SelectAccountEntryManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<AccountEntryDisplay> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return null;
	}

	@Override
	public String getDefaultEventHandler() {
		return StringPool.BLANK;
	}

	@Override
	public Boolean isSelectable() {
		return !isSingleSelect();
	}

	@Override
	public Boolean isShowCreationMenu() {
		return false;
	}

	public boolean isSingleSelect() {
		return ParamUtil.getBoolean(liferayPortletRequest, "singleSelect");
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"name"};
	}

}