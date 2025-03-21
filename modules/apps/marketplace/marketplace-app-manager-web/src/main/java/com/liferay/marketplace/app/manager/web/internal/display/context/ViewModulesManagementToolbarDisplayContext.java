/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.app.manager.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.marketplace.app.manager.web.internal.constants.BundleStateConstants;
import com.liferay.marketplace.app.manager.web.internal.util.AppDisplay;
import com.liferay.marketplace.app.manager.web.internal.util.AppDisplayFactoryUtil;
import com.liferay.marketplace.app.manager.web.internal.util.BundleUtil;
import com.liferay.marketplace.app.manager.web.internal.util.comparator.BundleComparator;
import com.liferay.marketplace.util.BundleManagerUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;

/**
 * @author Pei-Jung Lan
 */
public class ViewModulesManagementToolbarDisplayContext
	extends BaseAppManagerManagementToolbarDisplayContext {

	public ViewModulesManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse);
	}

	public String getApp() {
		return ParamUtil.getString(httpServletRequest, "app");
	}

	public AppDisplay getAppDisplay() {
		AppDisplay appDisplay = null;

		String app = ParamUtil.getString(httpServletRequest, "app");

		if (Validator.isNumber(app)) {
			appDisplay = AppDisplayFactoryUtil.getAppDisplay(
				BundleManagerUtil.getBundles(), GetterUtil.getLong(app));
		}

		if (appDisplay == null) {
			appDisplay = AppDisplayFactoryUtil.getAppDisplay(
				BundleManagerUtil.getBundles(), app,
				httpServletRequest.getLocale());
		}

		return appDisplay;
	}

	@Override
	public List<DropdownItem> getFilterDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(getStatusDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(httpServletRequest, "status"));
			}
		).build();
	}

	@Override
	public PortletURL getPortletURL() {
		PortletURL portletURL = PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCPath(
			"/view_modules.jsp"
		).setParameter(
			"app", getApp()
		).setParameter(
			"orderByType", getOrderByType()
		).setParameter(
			"state", getState()
		).buildPortletURL();

		if (_searchContainer != null) {
			portletURL.setParameter(
				_searchContainer.getCurParam(),
				String.valueOf(_searchContainer.getCur()));
			portletURL.setParameter(
				_searchContainer.getDeltaParam(),
				String.valueOf(_searchContainer.getDelta()));
		}

		return portletURL;
	}

	@Override
	public SearchContainer<Object> getSearchContainer() throws Exception {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		SearchContainer<Object> searchContainer = new SearchContainer(
			liferayPortletRequest, getPortletURL(), null,
			"no-modules-were-found");

		searchContainer.setOrderByCol(getOrderByCol());
		searchContainer.setOrderByType(getOrderByType());

		AppDisplay appDisplay = getAppDisplay();

		List<Bundle> bundles = appDisplay.getBundles();

		BundleUtil.filterBundles(
			bundles, BundleStateConstants.getState(getState()));

		searchContainer.setResultsAndTotal(
			new ArrayList<>(
				ListUtil.sort(
					bundles, new BundleComparator(getOrderByType()))));

		_searchContainer = searchContainer;

		return _searchContainer;
	}

	private SearchContainer<Object> _searchContainer;

}