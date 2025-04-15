/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list.adapter;

import com.liferay.application.list.PanelApp;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PortletCategoryUtil;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Adolfo Pérez
 */
public class PortletPanelAppAdapterServiceTrackerCustomizer
	implements ServiceTrackerCustomizer<Portlet, PanelApp> {

	public PortletPanelAppAdapterServiceTrackerCustomizer(
		BundleContext bundleContext,
		Map<ServiceReference<Portlet>, ServiceRegistration<PanelApp>>
			serviceRegistrations) {

		_bundleContext = bundleContext;
		_serviceRegistrations = serviceRegistrations;
	}

	@Override
	public PanelApp addingService(ServiceReference<Portlet> serviceReference) {
		String portletId = (String)serviceReference.getProperty(
			"jakarta.portlet.name");

		if (Validator.isNull(portletId)) {
			return null;
		}

		String controlPanelCategory = (String)serviceReference.getProperty(
			"com.liferay.portlet.control-panel-entry-category");

		if (Validator.isNull(controlPanelCategory)) {
			return null;
		}

		PanelApp portletPanelAppAdapter = new PortletPanelAppAdapter(
			portletId, () -> _bundleContext.getService(serviceReference));

		ServiceRegistration<PanelApp> serviceRegistration =
			_bundleContext.registerService(
				PanelApp.class, portletPanelAppAdapter,
				HashMapDictionaryBuilder.<String, Object>put(
					"panel.category.key",
					PortletCategoryUtil.getPortletCategoryKey(
						controlPanelCategory)
				).put(
					"service.ranking", () -> getServiceRanking(serviceReference)
				).build());

		_serviceRegistrations.put(serviceReference, serviceRegistration);

		return portletPanelAppAdapter;
	}

	@Override
	public void modifiedService(
		ServiceReference<Portlet> serviceReference, PanelApp panelApp) {

		removedService(serviceReference, panelApp);

		addingService(serviceReference);
	}

	@Override
	public void removedService(
		ServiceReference<Portlet> serviceReference, PanelApp panelApp) {

		ServiceRegistration<PanelApp> serviceRegistration =
			_serviceRegistrations.remove(serviceReference);

		serviceRegistration.unregister();
	}

	protected Integer getServiceRanking(
		ServiceReference<Portlet> serviceReference) {

		String controlPanelEntryWeight = (String)serviceReference.getProperty(
			"com.liferay.portlet.control-panel-entry-weight");

		if (Validator.isNull(controlPanelEntryWeight)) {
			return null;
		}

		return (int)Math.ceil(
			GetterUtil.getDouble(controlPanelEntryWeight) * 100);
	}

	private final BundleContext _bundleContext;
	private final Map<ServiceReference<Portlet>, ServiceRegistration<PanelApp>>
		_serviceRegistrations;

}