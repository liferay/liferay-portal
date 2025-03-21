/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth.verifier.internal.tracker;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.osgi.util.StringPlus;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.access.control.AccessControlThreadLocal;
import com.liferay.portal.kernel.servlet.TryFilter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.servlet.filters.authverifier.AuthVerifierFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Dictionary;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Marta Medio
 */
@Component(
	property = {
		"default.registration.property=filter.init.auth.verifier.BasicAuthHeaderAuthVerifier.urls.includes=*",
		"default.registration.property=filter.init.auth.verifier.OAuth2RESTAuthVerifier.urls.includes=*",
		"default.registration.property=filter.init.auth.verifier.PortalSessionAuthVerifier.urls.includes=*",
		"default.registration.property=filter.init.guest.allowed=true",
		"default.remote.access.filter.service.ranking:Integer=-10",
		"default.whiteboard.property=" + HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_DISPATCHER + "=" + HttpWhiteboardConstants.DISPATCHER_FORWARD,
		"default.whiteboard.property=" + HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_DISPATCHER + "=" + HttpWhiteboardConstants.DISPATCHER_REQUEST,
		"default.whiteboard.property=" + HttpWhiteboardConstants.HTTP_WHITEBOARD_FILTER_SERVLET + "=cxf-servlet",
		"servlet.context.helper.select.filter=(!(liferay.auth.verifier=false))"
	},
	service = {}
)
public class AuthVerifierFilterTracker {

	public static final String AUTH_VERIFIER_PROPERTY_PREFIX = "auth.verifier.";

	public static final int AUTH_VERIFIER_PROPERTY_PREFIX_LENGTH =
		AUTH_VERIFIER_PROPERTY_PREFIX.length();

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;

		_defaultRegistrationProperties = _toDictionary(
			StringPlus.asList(properties.get("default.registration.property")));
		_defaultRemoteAccessFilterServiceRanking = MapUtil.getInteger(
			properties, "default.remote.access.filter.service.ranking", -10);
		_defaultWhiteboardProperties = _toDictionary(
			StringPlus.asList(properties.get("default.whiteboard.property")));

		String servletContextHelperSelectFilterString = MapUtil.getString(
			properties, "servlet.context.helper.select.filter");

		String filterString = StringBundler.concat(
			"(&", servletContextHelperSelectFilterString, "(",
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, "=*)",
			"(objectClass=", ServletContextHelper.class.getName(), "))");

		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext, filterString,
			new ServletContextHelperServiceTrackerCustomizer());
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private Dictionary<String, Object> _toDictionary(
		List<String> propertiesList) {

		Dictionary<String, Object> dictionary = new HashMapDictionary<>();

		for (String property : propertiesList) {
			int index = property.indexOf(StringPool.EQUAL);

			if (index == -1) {
				if (_log.isWarnEnabled()) {
					_log.warn("Invalid property " + property);
				}

				continue;
			}

			String propertyKey = property.substring(0, index);

			String propertyValue = StringPool.BLANK;

			if (index < property.length()) {
				propertyValue = property.substring(index + 1);
			}

			Object existingPropertyValue = dictionary.get(propertyKey);

			if (existingPropertyValue != null) {
				List<String> strings = StringUtil.asList(existingPropertyValue);

				strings.add(propertyValue);

				dictionary.put(propertyKey, strings);
			}
			else {
				dictionary.put(propertyKey, propertyValue);
			}
		}

		return dictionary;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AuthVerifierFilterTracker.class);

	private BundleContext _bundleContext;
	private Dictionary<String, Object> _defaultRegistrationProperties;
	private int _defaultRemoteAccessFilterServiceRanking;
	private Dictionary<String, Object> _defaultWhiteboardProperties;
	private ServiceTracker<?, ?> _serviceTracker;

	private static class AuditFilter implements Filter {

		@Override
		public void destroy() {
		}

		@Override
		public void doFilter(
				ServletRequest servletRequest, ServletResponse servletResponse,
				FilterChain filterChain)
			throws IOException, ServletException {

			Filter auditFilter = _auditFilterSnapshot.get();

			if (auditFilter instanceof TryFilter) {
				TryFilter tryFilter = (TryFilter)auditFilter;

				try {
					tryFilter.doFilterTry(
						(HttpServletRequest)servletRequest,
						(HttpServletResponse)servletResponse);
				}
				catch (Exception exception) {
					throw new ServletException(exception);
				}
			}

			filterChain.doFilter(servletRequest, servletResponse);
		}

		@Override
		public void init(FilterConfig filterConfig) {
		}

		private static final Snapshot<Filter> _auditFilterSnapshot =
			new Snapshot<>(
				AuditFilter.class, Filter.class,
				"(servlet-filter-name=Audit Filter)", true);

	}

	private static class RemoteAccessFilter implements Filter {

		@Override
		public void destroy() {
		}

		@Override
		public void doFilter(
				ServletRequest servletRequest, ServletResponse servletResponse,
				FilterChain filterChain)
			throws IOException, ServletException {

			boolean remoteAccess = AccessControlThreadLocal.isRemoteAccess();

			AccessControlThreadLocal.setRemoteAccess(true);

			try {
				filterChain.doFilter(servletRequest, servletResponse);
			}
			finally {
				AccessControlThreadLocal.setRemoteAccess(remoteAccess);
			}
		}

		@Override
		public void init(FilterConfig filterConfig) {
		}

	}

	private static class ServiceRegistrations {

		public ServiceRegistrations(
			ServiceRegistration<Filter> authVerifierFilterServiceRegistration,
			ServiceRegistration<Filter> auditFilterServiceRegistration,
			ServiceRegistration<Filter> remoteAccessFilterServiceRegistration) {

			_authVerifierFilterServiceRegistration =
				authVerifierFilterServiceRegistration;
			_auditFilterServiceRegistration = auditFilterServiceRegistration;
			_remoteAccessFilterServiceRegistration =
				remoteAccessFilterServiceRegistration;
		}

		public ServiceRegistration<Filter> getAuditFilterServiceRegistration() {
			return _auditFilterServiceRegistration;
		}

		public ServiceRegistration<Filter>
			getAuthVerifierFilterServiceRegistration() {

			return _authVerifierFilterServiceRegistration;
		}

		public ServiceRegistration<Filter>
			getRemoteAccessFilterServiceRegistration() {

			return _remoteAccessFilterServiceRegistration;
		}

		private final ServiceRegistration<Filter>
			_auditFilterServiceRegistration;
		private final ServiceRegistration<Filter>
			_authVerifierFilterServiceRegistration;
		private final ServiceRegistration<Filter>
			_remoteAccessFilterServiceRegistration;

	}

	private class ServletContextHelperServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<ServletContextHelper, ServiceRegistrations> {

		@Override
		public ServiceRegistrations addingService(
			ServiceReference<ServletContextHelper> serviceReference) {

			return new ServiceRegistrations(
				_bundleContext.registerService(
					Filter.class, new AuthVerifierFilter(),
					_buildPropertiesForAuthVerifierFilter(serviceReference)),
				_bundleContext.registerService(
					Filter.class, new AuditFilter(),
					_buildPropertiesForAuditFilter(serviceReference)),
				_bundleContext.registerService(
					Filter.class, new RemoteAccessFilter(),
					_buildPropertiesForRemoteAccessFilter(serviceReference)));
		}

		@Override
		public void modifiedService(
			ServiceReference<ServletContextHelper> serviceReference,
			ServiceRegistrations serviceRegistrations) {

			ServiceRegistration<Filter> authVerifierFilterServiceRegistration =
				serviceRegistrations.getAuthVerifierFilterServiceRegistration();

			authVerifierFilterServiceRegistration.setProperties(
				_buildPropertiesForAuthVerifierFilter(serviceReference));

			ServiceRegistration<Filter> auditFilterServiceRegistration =
				serviceRegistrations.getAuditFilterServiceRegistration();

			auditFilterServiceRegistration.setProperties(
				_buildPropertiesForAuditFilter(serviceReference));

			ServiceRegistration<Filter> remoteAccessFilterServiceRegistration =
				serviceRegistrations.getRemoteAccessFilterServiceRegistration();

			remoteAccessFilterServiceRegistration.setProperties(
				_buildPropertiesForRemoteAccessFilter(serviceReference));
		}

		@Override
		public void removedService(
			ServiceReference<ServletContextHelper> serviceReference,
			ServiceRegistrations serviceRegistrations) {

			ServiceRegistration<Filter> authVerifierFilterServiceRegistration =
				serviceRegistrations.getAuthVerifierFilterServiceRegistration();

			authVerifierFilterServiceRegistration.unregister();

			ServiceRegistration<Filter> auditFilterServiceRegistration =
				serviceRegistrations.getAuditFilterServiceRegistration();

			auditFilterServiceRegistration.unregister();

			ServiceRegistration<Filter> remoteAccessFilterServiceRegistration =
				serviceRegistrations.getRemoteAccessFilterServiceRegistration();

			remoteAccessFilterServiceRegistration.unregister();
		}

		private Dictionary<String, Object> _buildPropertiesForAuditFilter(
			ServiceReference<ServletContextHelper> serviceReference) {

			HashMapDictionaryBuilder.HashMapDictionaryWrapper<String, Object>
				properties =
					new HashMapDictionaryBuilder.HashMapDictionaryWrapper<>();

			_putWhiteboardProperties(properties, serviceReference);

			return properties.build();
		}

		private Dictionary<String, Object>
			_buildPropertiesForAuthVerifierFilter(
				ServiceReference<ServletContextHelper> serviceReference) {

			HashMapDictionaryBuilder.HashMapDictionaryWrapper<String, Object>
				properties = HashMapDictionaryBuilder.<String, Object>putAll(
					_defaultRegistrationProperties);

			for (String key : serviceReference.getPropertyKeys()) {
				if (key.startsWith(AUTH_VERIFIER_PROPERTY_PREFIX)) {
					properties.put(
						"filter.init." +
							key.substring(AUTH_VERIFIER_PROPERTY_PREFIX_LENGTH),
						serviceReference.getProperty(key));
				}
			}

			_putWhiteboardProperties(properties, serviceReference);

			return properties.build();
		}

		private Dictionary<String, Object>
			_buildPropertiesForRemoteAccessFilter(
				ServiceReference<ServletContextHelper> serviceReference) {

			HashMapDictionaryBuilder.HashMapDictionaryWrapper<String, Object>
				properties =
					new HashMapDictionaryBuilder.HashMapDictionaryWrapper<>();

			_putWhiteboardProperties(properties, serviceReference);

			Object serviceRanking = _defaultWhiteboardProperties.get(
				"remote.access.filter.service.ranking");

			if (serviceRanking != null) {
				properties.put("service.ranking", serviceRanking);
			}
			else {
				properties.put(
					"service.ranking",
					_defaultRemoteAccessFilterServiceRanking);
			}

			return properties.build();
		}

		private void _putWhiteboardProperties(
			HashMapDictionaryBuilder.HashMapDictionaryWrapper<String, Object>
				properties,
			ServiceReference<ServletContextHelper> serviceReference) {

			properties.putAll(_defaultWhiteboardProperties);

			for (String key : serviceReference.getPropertyKeys()) {
				if (key.startsWith("osgi.http.whiteboard")) {
					properties.put(key, serviceReference.getProperty(key));
				}
			}

			properties.put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
				GetterUtil.getString(
					serviceReference.getProperty(
						HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME)));
		}

	}

}