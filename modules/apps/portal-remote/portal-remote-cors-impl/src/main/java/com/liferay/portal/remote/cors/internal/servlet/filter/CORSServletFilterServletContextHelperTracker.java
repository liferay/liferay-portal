/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.cors.internal.servlet.filter;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.remote.cors.configuration.WebContextCORSConfiguration;
import com.liferay.portal.remote.cors.internal.CORSSupport;

import jakarta.servlet.Filter;

import java.util.Dictionary;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Carlos Sierra Andrés
 * @author Marta Medio
 */
@Component(
	configurationPid = "com.liferay.portal.remote.cors.configuration.WebContextCORSConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE, service = {}
)
public class CORSServletFilterServletContextHelperTracker {

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;

		WebContextCORSConfiguration webContextCORSConfiguration =
			ConfigurableUtil.createConfigurable(
				WebContextCORSConfiguration.class, properties);

		_corsHeaders = CORSSupport.buildCORSHeaders(
			webContextCORSConfiguration.headers());
		_filterMappingUrlPatterns =
			webContextCORSConfiguration.filterMappingURLPatterns();
		_serviceTracker = ServiceTrackerFactory.open(
			_bundleContext,
			StringBundler.concat(
				"(&",
				webContextCORSConfiguration.servletContextHelperSelectFilter(),
				"(", HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME,
				"=*)(objectClass=", ServletContextHelper.class.getName(), "))"),
			new ServletContextHelperServiceTrackerCustomizer());
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CORSServletFilterServletContextHelperTracker.class);

	private BundleContext _bundleContext;
	private Map<String, String> _corsHeaders;
	private String[] _filterMappingUrlPatterns;
	private ServiceTracker<ServletContextHelper, ServiceRegistration<?>>
		_serviceTracker;

	private class ServletContextHelperServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<ServletContextHelper, ServiceRegistration<?>> {

		@Override
		public ServiceRegistration<?> addingService(
			ServiceReference<ServletContextHelper> serviceReference) {

			CORSServletFilter corsServletFilter = new CORSServletFilter();

			corsServletFilter.setCORSHeaders(_corsHeaders);

			return _bundleContext.registerService(
				Filter.class, corsServletFilter,
				_buildProperties(serviceReference));
		}

		@Override
		public void modifiedService(
			ServiceReference<ServletContextHelper> serviceReference,
			ServiceRegistration<?> serviceRegistration) {

			serviceRegistration.setProperties(
				_buildProperties(serviceReference));
		}

		@Override
		public void removedService(
			ServiceReference<ServletContextHelper> serviceReference,
			ServiceRegistration<?> serviceRegistration) {

			try {
				serviceRegistration.unregister();
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		private Dictionary<String, Object> _buildProperties(
			ServiceReference<ServletContextHelper> serviceReference) {

			Dictionary<String, Object> properties =
				HashMapDictionaryBuilder.<String, Object>put(
					Constants.SERVICE_RANKING, -1
				).put(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
					GetterUtil.getString(
						serviceReference.getProperty(
							HttpWhiteboardConstants.
								HTTP_WHITEBOARD_CONTEXT_NAME))
				).put(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_NAME,
					CORSServletFilter.class.getName()
				).build();

			if (ArrayUtil.isEmpty(_filterMappingUrlPatterns) ||
				ArrayUtil.contains(
					_filterMappingUrlPatterns, StringPool.STAR)) {

				properties.put(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET,
					"cxf-servlet");
			}
			else {
				properties.put(
					HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_PATTERN,
					_filterMappingUrlPatterns);
			}

			return properties;
		}

	}

}