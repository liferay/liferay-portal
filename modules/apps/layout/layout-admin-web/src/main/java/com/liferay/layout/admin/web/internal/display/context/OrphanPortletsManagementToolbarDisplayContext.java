/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class OrphanPortletsManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public OrphanPortletsManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		OrphanPortletsDisplayContext orphanPortletsDisplayContext) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			orphanPortletsDisplayContext.getOrphanPortletsSearchContainer());

		_orphanPortletsDisplayContext = orphanPortletsDisplayContext;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteOrphanPortlets");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
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
	public String getComponentId() {
		return "portletsManagementToolbar";
	}

	@Override
	protected String getDisplayStyle() {
		return _orphanPortletsDisplayContext.getDisplayStyle();
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"list", "descriptive"};
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"name"};
	}

	private final OrphanPortletsDisplayContext _orphanPortletsDisplayContext;

}