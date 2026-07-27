/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.display.template.internal.exportimport.portlet.preferences.processor;

import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessorHelper;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portlet.display.template.PortletDisplayTemplate;
import com.liferay.portlet.display.template.constants.PortletDisplayTemplateConstants;
import com.liferay.portlet.display.template.exportimport.portlet.preferences.processor.PortletDisplayTemplateRegister;

import java.util.Dictionary;
import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Lance Ji
 */
@Component(service = {})
public class PortletDisplayTemplateServiceTracker {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = new ServiceTracker<>(
			bundleContext, PortletDisplayTemplateRegister.class,
			_getServiceTrackerCustomizer(bundleContext));

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private Dictionary<String, Object> _getProperties(
		ServiceReference<?> serviceReference) {

		Dictionary<String, Object> dictionary = new HashMapDictionary<>();

		for (String key : serviceReference.getPropertyKeys()) {
			if (key.equals(Constants.OBJECTCLASS) ||
				key.equals(ComponentConstants.COMPONENT_ID) ||
				key.equals(ComponentConstants.COMPONENT_NAME) ||
				key.equals(Constants.SERVICE_BUNDLEID) ||
				key.equals(Constants.SERVICE_ID) ||
				key.equals(Constants.SERVICE_SCOPE)) {

				continue;
			}

			dictionary.put(key, serviceReference.getProperty(key));
		}

		return dictionary;
	}

	private ServiceTrackerCustomizer
		<PortletDisplayTemplateRegister, ServiceRegistration<Capability>>
			_getServiceTrackerCustomizer(BundleContext bundleContext) {

		return new ServiceTrackerCustomizer
			<PortletDisplayTemplateRegister,
			 ServiceRegistration<Capability>>() {

			@Override
			public ServiceRegistration<Capability> addingService(
				ServiceReference<PortletDisplayTemplateRegister>
					serviceReference) {

				Dictionary<String, Object> dictionary = _getProperties(
					serviceReference);

				Object type = dictionary.remove("type");

				if (Objects.equals(
						PortletDisplayTemplateConstants.DISPLAY_TEMPLATE_IMPORT,
						type)) {

					return bundleContext.registerService(
						Capability.class,
						new PortletDisplayTemplateImportCapability(
							_portal, _portletLocalService,
							_portletDisplayTemplate,
							bundleContext.getService(serviceReference)),
						dictionary);
				}

				if (Objects.equals(
						PortletDisplayTemplateConstants.DISPLAY_TEMPLATE_EXPORT,
						type)) {

					return bundleContext.registerService(
						Capability.class,
						new PortletDisplayTemplateExportCapability(
							_exportImportPortletPreferencesProcessorHelper,
							_portal, _portletLocalService,
							_portletDisplayTemplate,
							bundleContext.getService(serviceReference)),
						dictionary);
				}

				PortletDisplayTemplateRegister portletDisplayTemplateRegister =
					bundleContext.getService(serviceReference);

				try {
					_log.error(
						StringBundler.concat(
							"Unknow type = ", type, " from ",
							portletDisplayTemplateRegister));
				}
				finally {
					bundleContext.ungetService(serviceReference);
				}

				return null;
			}

			@Override
			public void modifiedService(
				ServiceReference<PortletDisplayTemplateRegister>
					serviceReference,
				ServiceRegistration<Capability> serviceRegistration) {

				serviceRegistration.setProperties(
					_getProperties(serviceReference));
			}

			@Override
			public void removedService(
				ServiceReference<PortletDisplayTemplateRegister>
					serviceReference,
				ServiceRegistration<Capability> serviceRegistration) {

				serviceRegistration.unregister();

				bundleContext.ungetService(serviceReference);
			}

		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletDisplayTemplateServiceTracker.class);

	@Reference(unbind = "-")
	private ExportImportPortletPreferencesProcessorHelper
		_exportImportPortletPreferencesProcessorHelper;

	@Reference(unbind = "-")
	private Portal _portal;

	@Reference(unbind = "-")
	private PortletDisplayTemplate _portletDisplayTemplate;

	@Reference(unbind = "-")
	private PortletLocalService _portletLocalService;

	private ServiceTracker
		<PortletDisplayTemplateRegister, ServiceRegistration<Capability>>
			_serviceTracker;

}