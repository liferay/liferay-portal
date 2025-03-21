/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class PortletConfigurationTemplatesManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public PortletConfigurationTemplatesManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		PortletConfigurationTemplatesDisplayContext
			portletConfigurationTemplatesDisplayContext) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			portletConfigurationTemplatesDisplayContext.
				getArchivedSettingsSearchContainer());

		_portletConfigurationTemplatesDisplayContext =
			portletConfigurationTemplatesDisplayContext;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteArchivedSettings");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	@Override
	public String getComponentId() {
		return "archivedSettingsManagementToolbar";
	}

	@Override
	public String getDefaultEventHandler() {
		return "PORTLET_CONFIGURATION_TEMPLATES_MANAGEMENT_TOOLBAR_DEFAULT_" +
			"EVENT_HANDLER";
	}

	@Override
	public String getSearchContainerId() {
		return "archivedSettings";
	}

	@Override
	protected String getDisplayStyle() {
		return _portletConfigurationTemplatesDisplayContext.getDisplayStyle();
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"list", "descriptive", "icon"};
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"name", "modified-date"};
	}

	private final PortletConfigurationTemplatesDisplayContext
		_portletConfigurationTemplatesDisplayContext;

}