/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.app.manager.web.internal.display.context;

import com.liferay.marketplace.app.manager.web.internal.util.AppDisplay;
import com.liferay.marketplace.app.manager.web.internal.util.AppDisplayFactoryUtil;
import com.liferay.marketplace.app.manager.web.internal.util.comparator.ModuleServiceReferenceComparator;
import com.liferay.marketplace.util.BundleManagerUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Pei-Jung Lan
 */
public class ViewModuleManagementToolbarDisplayContext
	extends BaseAppManagerManagementToolbarDisplayContext {

	public ViewModuleManagementToolbarDisplayContext(
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

	public Bundle getBundle() {
		return BundleManagerUtil.getBundle(
			ParamUtil.getString(httpServletRequest, "symbolicName"),
			ParamUtil.getString(httpServletRequest, "version"));
	}

	public String getPluginType() {
		return ParamUtil.getString(
			httpServletRequest, "pluginType", "components");
	}

	@Override
	public PortletURL getPortletURL() {
		Bundle bundle = getBundle();

		PortletURL portletURL = PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCPath(
			"/view_module.jsp"
		).setParameter(
			"app", getApp()
		).setParameter(
			"orderByType", getOrderByType()
		).setParameter(
			"pluginType", getPluginType()
		).setParameter(
			"symbolicName", bundle.getSymbolicName()
		).setParameter(
			"version", bundle.getVersion()
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

		String emptyResultsMessage = "no-portlets-were-found";

		String pluginType = getPluginType();

		if (pluginType.equals("components")) {
			emptyResultsMessage = "no-components-were-found";
		}

		SearchContainer<Object> searchContainer = new SearchContainer(
			liferayPortletRequest, getPortletURL(), null, emptyResultsMessage);

		searchContainer.setOrderByCol(getOrderByCol());
		searchContainer.setOrderByType(getOrderByType());

		Bundle bundle = getBundle();

		BundleContext bundleContext = bundle.getBundleContext();

		List<ServiceReference<?>> serviceReferences =
			Collections.<ServiceReference<?>>emptyList();

		if (pluginType.equals("portlets")) {
			serviceReferences = ListUtil.sort(
				new ArrayList<>(
					bundleContext.getServiceReferences(
						Portlet.class,
						"(service.bundleid=" + bundle.getBundleId() + ")")),
				new ModuleServiceReferenceComparator(
					"jakarta.portlet.display-name", getOrderByType()));
		}
		else {
			serviceReferences = ListUtil.sort(
				ListUtil.fromArray(
					(ServiceReference<?>[])bundleContext.getServiceReferences(
						(String)null,
						"(&(component.id=*)(service.bundleid=" +
							bundle.getBundleId() + "))")),
				new ModuleServiceReferenceComparator(
					"component.name", getOrderByType()));
		}

		searchContainer.setResultsAndTotal(new ArrayList<>(serviceReferences));

		_searchContainer = searchContainer;

		return _searchContainer;
	}

	private SearchContainer<Object> _searchContainer;

}