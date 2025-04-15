/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.internal.application.publisher;

import com.liferay.headless.builder.application.APIApplication;
import com.liferay.headless.builder.application.provider.APIApplicationProvider;
import com.liferay.headless.builder.application.publisher.APIApplicationPublisher;
import com.liferay.headless.builder.constants.HeadlessBuilderConstants;
import com.liferay.headless.builder.internal.application.endpoint.EndpointMatcher;
import com.liferay.headless.builder.internal.helper.EndpointHelper;
import com.liferay.headless.builder.internal.resource.HeadlessBuilderResourceImpl;
import com.liferay.headless.builder.internal.resource.OpenAPIResourceImpl;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.cluster.Clusterable;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.vulcan.resource.OpenAPIResource;

import jakarta.ws.rs.core.Application;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luis Miguel Barcos
 */
@Component(service = AopService.class)
public class APIApplicationPublisherImpl
	implements AopService, APIApplicationPublisher, IdentifiableOSGiService {

	@Override
	public String getOSGiServiceIdentifier() {
		return APIApplicationPublisherImpl.class.getName();
	}

	@Clusterable
	@Override
	public void publish(long companyId) throws Exception {
		for (APIApplication apiApplication :
				_apiApplicationProvider.getPublishedAPIApplications(
					companyId)) {

			publish(apiApplication.getBaseURL(), apiApplication.getCompanyId());
		}
	}

	@Clusterable
	@Override
	public void publish(String baseURL, long companyId) throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled("LPS-178642")) {
			throw new UnsupportedOperationException(
				"APIApplicationPublisher not available");
		}

		APIApplication apiApplication =
			_apiApplicationProvider.fetchAPIApplication(baseURL, companyId);

		if (apiApplication == null) {
			return;
		}

		_endpointMatchersMap.put(
			_getEndpointMatcherKey(
				apiApplication.getBaseURL(), apiApplication.getCompanyId()),
			new EndpointMatcher(apiApplication.getEndpoints()));

		ServiceRegistration<Application> applicationServiceRegistration =
			_applicationServiceRegistrationsMap.get(
				apiApplication.getBaseURL());

		if (applicationServiceRegistration != null) {
			applicationServiceRegistration.setProperties(
				_getApplicationProperties(
					apiApplication.getBaseURL(),
					_registerCompanyId(apiApplication)));

			return;
		}

		_serviceRegistrationsMap.put(
			apiApplication.getBaseURL(),
			new ArrayList<ServiceRegistration<?>>() {
				{
					add(_registerAPIApplication(apiApplication));
					add(
						_registerResource(
							apiApplication, new HashMapDictionary<>(),
							HeadlessBuilderResourceImpl.class,
							() -> new HeadlessBuilderResourceImpl(
								_endpointHelper,
								companyId -> _endpointMatchersMap.get(
									_getEndpointMatcherKey(
										apiApplication.getBaseURL(),
										companyId)))));
					add(
						_registerResource(
							apiApplication,
							HashMapDictionaryBuilder.<String, Object>put(
								"openapi.resource", "true"
							).put(
								"openapi.resource.path",
								HeadlessBuilderConstants.BASE_PATH_SUFFIX +
									apiApplication.getBaseURL()
							).build(),
							OpenAPIResourceImpl.class,
							() -> new OpenAPIResourceImpl(_openAPIResource)));
				}
			});
	}

	@Clusterable
	@Override
	public void unpublish(String baseURL, long companyId) {
		if (!FeatureFlagManagerUtil.isEnabled("LPS-178642")) {
			throw new UnsupportedOperationException(
				"APIApplicationPublisher not available");
		}

		_endpointMatchersMap.remove(_getEndpointMatcherKey(baseURL, companyId));

		Set<Long> companyIds = _getCompanyIds(baseURL);

		companyIds.remove(companyId);

		if (SetUtil.isNotEmpty(companyIds)) {
			ServiceRegistration<Application> applicationServiceRegistration =
				_applicationServiceRegistrationsMap.get(baseURL);

			if (applicationServiceRegistration != null) {
				applicationServiceRegistration.setProperties(
					_getApplicationProperties(baseURL, companyIds));
			}

			return;
		}

		List<ServiceRegistration<?>> serviceRegistrations =
			_serviceRegistrationsMap.remove(baseURL);

		if (serviceRegistrations != null) {
			_unregisterServiceRegistrations(serviceRegistrations);
		}

		_applicationServiceRegistrationsMap.remove(baseURL);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Deactivate
	protected void deactivate() {
		for (List<ServiceRegistration<?>> serviceRegistrations :
				_serviceRegistrationsMap.values()) {

			_unregisterServiceRegistrations(serviceRegistrations);
		}

		_applicationServiceRegistrationsMap.clear();
		_companyIdsMap.clear();
		_endpointMatchersMap.clear();
		_serviceRegistrationsMap.clear();
	}

	private HashMapDictionary<String, Object> _getApplicationProperties(
		String baseURL, Set<Long> companyIds) {

		return HashMapDictionaryBuilder.<String, Object>put(
			"companyId",
			() -> TransformUtil.transform(companyIds, String::valueOf)
		).put(
			"liferay.filter.disabled", true
		).put(
			"liferay.headless.builder.application", true
		).put(
			"liferay.jackson", false
		).put(
			"liferay.objects.exception.mapper", true
		).put(
			"osgi.jaxrs.application.base",
			HeadlessBuilderConstants.BASE_PATH_SUFFIX + baseURL
		).put(
			"osgi.jaxrs.extension.select", "(osgi.jaxrs.name=Liferay.Vulcan)"
		).put(
			"osgi.jaxrs.name", baseURL
		).build();
	}

	private Set<Long> _getCompanyIds(String baseURL) {
		return _companyIdsMap.computeIfAbsent(baseURL, key -> new HashSet<>());
	}

	private String _getEndpointMatcherKey(String baseURL, long companyId) {
		return baseURL + StringPool.POUND + companyId;
	}

	private ServiceRegistration<Application> _registerAPIApplication(
		APIApplication apiApplication) {

		ServiceRegistration<Application> applicationServiceRegistration =
			_bundleContext.registerService(
				Application.class, new Application(),
				_getApplicationProperties(
					apiApplication.getBaseURL(),
					_registerCompanyId(apiApplication)));

		_applicationServiceRegistrationsMap.put(
			apiApplication.getBaseURL(), applicationServiceRegistration);

		return applicationServiceRegistration;
	}

	private Set<Long> _registerCompanyId(APIApplication apiApplication) {
		Set<Long> companyIds = _getCompanyIds(apiApplication.getBaseURL());

		companyIds.add(apiApplication.getCompanyId());

		return companyIds;
	}

	private <T> ServiceRegistration<T> _registerResource(
		APIApplication apiApplication, Dictionary<String, Object> properties,
		Class<T> resourceClass, Supplier<T> resourceSupplier) {

		return _bundleContext.registerService(
			resourceClass,
			new PrototypeServiceFactory<T>() {

				@Override
				public T getService(
					Bundle bundle, ServiceRegistration<T> serviceRegistration) {

					return resourceSupplier.get();
				}

				@Override
				public void ungetService(
					Bundle bundle, ServiceRegistration<T> serviceRegistration,
					T t) {
				}

			},
			HashMapDictionaryBuilder.<String, Object>put(
				"osgi.jaxrs.application.select",
				"(osgi.jaxrs.name=" + apiApplication.getBaseURL() + ")"
			).put(
				"osgi.jaxrs.resource", "true"
			).putAll(
				properties
			).build());
	}

	private void _unregisterServiceRegistrations(
		List<ServiceRegistration<?>> serviceRegistrations) {

		for (ServiceRegistration<?> serviceRegistration :
				serviceRegistrations) {

			if (serviceRegistration != null) {
				serviceRegistration.unregister();
			}
		}
	}

	private static BundleContext _bundleContext;

	@Reference
	private APIApplicationProvider _apiApplicationProvider;

	private final Map<String, ServiceRegistration<Application>>
		_applicationServiceRegistrationsMap = new HashMap<>();
	private final Map<String, Set<Long>> _companyIdsMap = new HashMap<>();

	@Reference
	private EndpointHelper _endpointHelper;

	private final Map<String, EndpointMatcher> _endpointMatchersMap =
		new HashMap<>();

	@Reference
	private OpenAPIResource _openAPIResource;

	private final Map<String, List<ServiceRegistration<?>>>
		_serviceRegistrationsMap = new HashMap<>();

}