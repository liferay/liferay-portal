/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.BaseManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Drew Brokke
 */
public class UADExportProcessManagementToolbarDisplayContext
	extends BaseManagementToolbarDisplayContext {

	public UADExportProcessManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<BackgroundTask> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse);

		_liferayPortletResponse = liferayPortletResponse;
		_searchContainer = searchContainer;

		_currentURL = PortletURLUtil.getCurrent(
			liferayPortletRequest, liferayPortletResponse);
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setNavigation(
			(String)null
		).buildString();
	}

	@Override
	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				User selectedUser = PortalUtil.getSelectedUser(
					httpServletRequest);

				dropdownItem.setHref(
					liferayPortletResponse.createRenderURL(),
					"mvcRenderCommandName",
					"/user_associated_data/add_uad_export_processes", "backURL",
					PortalUtil.getCurrentURL(httpServletRequest), "p_u_i_d",
					String.valueOf(selectedUser.getUserId()));

				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, "add-export-processes"));
			}
		).build();
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		String navigation = getNavigation();

		return LabelItemListBuilder.add(
			() -> !navigation.equals("all"),
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						getPortletURL()
					).setNavigation(
						(String)null
					).buildString());

				labelItem.setCloseable(true);
				labelItem.setLabel(
					String.format(
						"%s: %s",
						LanguageUtil.get(httpServletRequest, "status"),
						LanguageUtil.get(httpServletRequest, navigation)));
			}
		).build();
	}

	@Override
	public int getItemsTotal() {
		return _searchContainer.getTotal();
	}

	@Override
	public PortletURL getPortletURL() {
		try {
			return PortletURLUtil.clone(_currentURL, _liferayPortletResponse);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return _liferayPortletResponse.createRenderURL();
		}
	}

	@Override
	public Boolean isSelectable() {
		return false;
	}

	@Override
	protected String[] getNavigationKeys() {
		return new String[] {"all", "in-progress", "successful", "failed"};
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"create-date", "name"};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UADExportProcessManagementToolbarDisplayContext.class);

	private final PortletURL _currentURL;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final SearchContainer<BackgroundTask> _searchContainer;

}