/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.internal.info.item.provider;

import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.capability.InfoItemCapability;
import com.liferay.info.item.provider.InfoItemCapabilitiesProvider;
import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.reflect.GenericUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.template.info.item.capability.TemplateInfoItemCapability;
import com.liferay.template.internal.info.item.renderer.TemplateInfoItemTemplatedRenderer;
import com.liferay.template.service.TemplateEntryLocalService;
import com.liferay.template.transformer.TemplateNodeFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = {})
public class TemplateInfoItemCapabilitiesProviderTracker {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext,
			(Class<InfoItemCapabilitiesProvider<?>>)
				(Class<?>)InfoItemCapabilitiesProvider.class,
			new InfoItemCapabilitiesProviderServiceTrackerCustomizer(
				bundleContext, _serviceRegistrations));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();

		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations.values()) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();
	}

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	private final Map<String, ServiceRegistration<?>> _serviceRegistrations =
		new ConcurrentHashMap<>();
	private ServiceTracker
		<InfoItemCapabilitiesProvider<?>, InfoItemCapabilitiesProvider<?>>
			_serviceTracker;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

	@Reference
	private TemplateEntryLocalService _templateEntryLocalService;

	@Reference(
		target = "(info.item.capability.key=" + TemplateInfoItemCapability.KEY + ")"
	)
	private InfoItemCapability _templateInfoItemCapability;

	@Reference
	private TemplateNodeFactory _templateNodeFactory;

	private class InfoItemCapabilitiesProviderServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<InfoItemCapabilitiesProvider<?>, InfoItemCapabilitiesProvider<?>> {

		public InfoItemCapabilitiesProviderServiceTrackerCustomizer(
			BundleContext bundleContext,
			Map<String, ServiceRegistration<?>> serviceRegistrations) {

			_bundleContext = bundleContext;
			_serviceRegistrations = serviceRegistrations;
		}

		@Override
		public InfoItemCapabilitiesProvider<?> addingService(
			ServiceReference<InfoItemCapabilitiesProvider<?>>
				serviceReference) {

			InfoItemCapabilitiesProvider<?> infoItemCapabilitiesProvider =
				_bundleContext.getService(serviceReference);

			String className = GetterUtil.getString(
				serviceReference.getProperty("item.class.name"));

			if (Validator.isNull(className)) {
				className = GenericUtil.getGenericClassName(
					infoItemCapabilitiesProvider);
			}

			if (Validator.isNull(className) ||
				_serviceRegistrations.containsKey(className)) {

				return infoItemCapabilitiesProvider;
			}

			List<InfoItemCapability> infoItemCapabilities =
				infoItemCapabilitiesProvider.getInfoItemCapabilities();

			if (!infoItemCapabilities.contains(_templateInfoItemCapability)) {
				return infoItemCapabilitiesProvider;
			}

			try {
				_serviceRegistrations.put(
					className,
					_bundleContext.registerService(
						(Class<InfoItemRenderer<?>>)
							(Class<?>)InfoItemRenderer.class,
						new TemplateInfoItemTemplatedRenderer<>(
							className, _ddmTemplateLocalService,
							_infoItemServiceRegistry, _stagingGroupHelper,
							_templateEntryLocalService, _templateNodeFactory),
						HashMapDictionaryBuilder.<String, Object>put(
							Constants.SERVICE_RANKING, 200
						).put(
							"item.class.name", className
						).build()));
			}
			catch (Throwable throwable) {
				_bundleContext.ungetService(serviceReference);

				throw throwable;
			}

			return infoItemCapabilitiesProvider;
		}

		@Override
		public void modifiedService(
			ServiceReference<InfoItemCapabilitiesProvider<?>> serviceReference,
			InfoItemCapabilitiesProvider<?> infoItemCapabilitiesProvider) {

			removedService(serviceReference, infoItemCapabilitiesProvider);

			addingService(serviceReference);
		}

		@Override
		public void removedService(
			ServiceReference<InfoItemCapabilitiesProvider<?>> serviceReference,
			InfoItemCapabilitiesProvider<?> infoItemCapabilitiesProvider) {

			String className = GenericUtil.getGenericClassName(
				infoItemCapabilitiesProvider);

			if (Validator.isNull(className)) {
				return;
			}

			ServiceRegistration<?> serviceRegistration =
				_serviceRegistrations.remove(className);

			if (serviceRegistration != null) {
				serviceRegistration.unregister();
			}
		}

		private final BundleContext _bundleContext;
		private final Map<String, ServiceRegistration<?>> _serviceRegistrations;

	}

}