/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.portlet.InvokerFilterContainer;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.model.impl.PortletFilterImpl;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.PortletFilterFactory;

import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.filter.ActionFilter;
import jakarta.portlet.filter.EventFilter;
import jakarta.portlet.filter.FilterConfig;
import jakarta.portlet.filter.HeaderFilter;
import jakarta.portlet.filter.PortletFilter;
import jakarta.portlet.filter.RenderFilter;
import jakarta.portlet.filter.ResourceFilter;

import java.io.Closeable;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Raymond Augé
 * @author Neil Griffin
 */
public class InvokerFilterContainerImpl
	implements Closeable, InvokerFilterContainer {

	public static final InvokerFilterContainer EMPTY_INVOKER_FILTER_CONTAINER =
		new EmptyInvokerFilterContainer();

	public InvokerFilterContainerImpl(
			Portlet portlet, PortletContext portletContext)
		throws PortletException {

		String rootPortletId = portlet.getRootPortletId();

		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			_bundleContext, PortletFilter.class,
			"(jakarta.portlet.name=" + rootPortletId + ")",
			(serviceReference, emitter) -> {
				PortletFilter portletFilter = _bundleContext.getService(
					serviceReference);

				Set<String> lifecycles =
					(Set<String>)serviceReference.getProperty(
						"filter.lifecycles");

				if ((portletFilter instanceof ActionFilter) &&
					_isDeclaredLifecycle(
						PortletRequest.ACTION_PHASE, lifecycles)) {

					emitter.emit(ActionFilter.class);
				}

				if ((portletFilter instanceof EventFilter) &&
					_isDeclaredLifecycle(
						PortletRequest.EVENT_PHASE, lifecycles)) {

					emitter.emit(EventFilter.class);
				}

				if ((portletFilter instanceof HeaderFilter) &&
					_isDeclaredLifecycle(
						PortletRequest.HEADER_PHASE, lifecycles)) {

					emitter.emit(HeaderFilter.class);
				}

				if ((portletFilter instanceof RenderFilter) &&
					_isDeclaredLifecycle(
						PortletRequest.RENDER_PHASE, lifecycles)) {

					emitter.emit(RenderFilter.class);
				}

				if ((portletFilter instanceof ResourceFilter) &&
					_isDeclaredLifecycle(
						PortletRequest.RESOURCE_PHASE, lifecycles)) {

					emitter.emit(ResourceFilter.class);
				}

				_bundleContext.ungetService(serviceReference);
			},
			new ServiceTrackerCustomizer<PortletFilter, PortletFilter>() {

				@Override
				public PortletFilter addingService(
					ServiceReference<PortletFilter> serviceReference) {

					PortletFilter portletFilter = _bundleContext.getService(
						serviceReference);

					boolean preinitializedFilter = GetterUtil.getBoolean(
						serviceReference.getProperty("preinitialized.filter"));

					if (!preinitializedFilter) {
						String filterName = GetterUtil.getString(
							serviceReference.getProperty("service.id"),
							ClassUtil.getClassName(portletFilter));

						Map<String, String> params = new HashMap<>();

						for (String key : serviceReference.getPropertyKeys()) {
							if (!key.startsWith(
									"jakarta.portlet.init-param.")) {

								continue;
							}

							params.put(
								key.substring(
									"jakarta.portlet.init-param.".length()),
								GetterUtil.getString(
									serviceReference.getProperty(key)));
						}

						FilterConfig filterConfig = new FilterConfigImpl(
							filterName, portletContext, params);

						try {
							portletFilter.init(filterConfig);
						}
						catch (PortletException portletException) {
							_log.error(portletException);

							_bundleContext.ungetService(serviceReference);

							return null;
						}
					}

					return portletFilter;
				}

				@Override
				public void modifiedService(
					ServiceReference<PortletFilter> serviceReference,
					PortletFilter portletFilter) {
				}

				@Override
				public void removedService(
					ServiceReference<PortletFilter> serviceReference,
					PortletFilter portletFilter) {

					_bundleContext.ungetService(serviceReference);

					boolean preinitializedFilter = GetterUtil.getBoolean(
						serviceReference.getProperty("preinitialized.filter"));

					if (preinitializedFilter) {
						return;
					}

					portletFilter.destroy();
				}

			});

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"jakarta.portlet.name", rootPortletId
			).put(
				"preinitialized.filter", Boolean.TRUE
			).build();

		Map<String, com.liferay.portal.kernel.model.PortletFilter>
			portletFilters = portlet.getPortletFilters();

		for (Map.Entry<String, com.liferay.portal.kernel.model.PortletFilter>
				entry : portletFilters.entrySet()) {

			com.liferay.portal.kernel.model.PortletFilter portletFilterModel =
				entry.getValue();

			PortletFilter portletFilter = PortletFilterFactory.create(
				portletFilterModel, portletContext);

			ServiceRegistration<PortletFilter> serviceRegistration =
				_bundleContext.registerService(
					PortletFilter.class, portletFilter,
					HashMapDictionaryBuilder.<String, Object>putAll(
						properties
					).put(
						"filter.lifecycles", portletFilterModel.getLifecycles()
					).build());

			ServiceRegistrationTuple serviceRegistrationTuple =
				new ServiceRegistrationTuple(
					portletFilterModel, serviceRegistration);

			_serviceRegistrationTuples.add(serviceRegistrationTuple);
		}

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				PortalClassLoaderUtil.getClassLoader())) {

			for (String portletFilterClassName :
					PropsValues.PORTLET_FILTERS_SYSTEM) {

				com.liferay.portal.kernel.model.PortletFilter
					portletFilterModel = new PortletFilterImpl(
						portletFilterClassName, portletFilterClassName,
						Collections.<String>emptySet(),
						Collections.<String, String>emptyMap(),
						portlet.getPortletApp());

				PortletFilter portletFilter = PortletFilterFactory.create(
					portletFilterModel, portletContext);

				ServiceRegistration<PortletFilter> serviceRegistration =
					_bundleContext.registerService(
						PortletFilter.class, portletFilter, properties);

				_serviceRegistrationTuples.add(
					new ServiceRegistrationTuple(
						portletFilterModel, serviceRegistration));
			}
		}
	}

	@Override
	public void close() {
		for (ServiceRegistrationTuple serviceRegistrationTuple :
				_serviceRegistrationTuples) {

			PortletFilterFactory.destroy(
				serviceRegistrationTuple.getPortletFilterModel());

			ServiceRegistration<PortletFilter> serviceRegistration =
				serviceRegistrationTuple.getServiceRegistration();

			serviceRegistration.unregister();
		}

		_serviceRegistrationTuples.clear();

		_serviceTrackerMap.close();
	}

	@Override
	public List<ActionFilter> getActionFilters() {
		return _getPortletFilters(ActionFilter.class);
	}

	@Override
	public List<EventFilter> getEventFilters() {
		return _getPortletFilters(EventFilter.class);
	}

	@Override
	public List<HeaderFilter> getHeaderFilters() {
		return _getPortletFilters(HeaderFilter.class);
	}

	@Override
	public List<RenderFilter> getRenderFilters() {
		return _getPortletFilters(RenderFilter.class);
	}

	@Override
	public List<ResourceFilter> getResourceFilters() {
		return _getPortletFilters(ResourceFilter.class);
	}

	private <T extends PortletFilter> List<T> _getPortletFilters(
		Class<T> clazz) {

		List<PortletFilter> portletFilters = _serviceTrackerMap.getService(
			clazz);

		if (portletFilters == null) {
			return Collections.emptyList();
		}

		return (List<T>)portletFilters;
	}

	private boolean _isDeclaredLifecycle(
		String lifecycle, Set<String> lifecycles) {

		if ((lifecycles == null) || lifecycles.isEmpty()) {
			return true;
		}

		return lifecycles.contains(lifecycle);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		InvokerFilterContainerImpl.class);

	private final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private final List<ServiceRegistrationTuple> _serviceRegistrationTuples =
		new CopyOnWriteArrayList<>();
	private final ServiceTrackerMap
		<Class<? extends PortletFilter>, List<PortletFilter>>
			_serviceTrackerMap;

	private static class EmptyInvokerFilterContainer
		implements Closeable, InvokerFilterContainer {

		@Override
		public void close() {
		}

		@Override
		public List<ActionFilter> getActionFilters() {
			return Collections.emptyList();
		}

		@Override
		public List<EventFilter> getEventFilters() {
			return Collections.emptyList();
		}

		@Override
		public List<HeaderFilter> getHeaderFilters() {
			return Collections.emptyList();
		}

		@Override
		public List<RenderFilter> getRenderFilters() {
			return Collections.emptyList();
		}

		@Override
		public List<ResourceFilter> getResourceFilters() {
			return Collections.emptyList();
		}

	}

	private static class ServiceRegistrationTuple {

		public ServiceRegistrationTuple(
			com.liferay.portal.kernel.model.PortletFilter portletFilterModel,
			ServiceRegistration<PortletFilter> serviceRegistration) {

			_portletFilterModel = portletFilterModel;
			_serviceRegistration = serviceRegistration;
		}

		public com.liferay.portal.kernel.model.PortletFilter
			getPortletFilterModel() {

			return _portletFilterModel;
		}

		public ServiceRegistration<PortletFilter> getServiceRegistration() {
			return _serviceRegistration;
		}

		private final com.liferay.portal.kernel.model.PortletFilter
			_portletFilterModel;
		private final ServiceRegistration<PortletFilter> _serviceRegistration;

	}

}