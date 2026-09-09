/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.application;

import com.liferay.osgi.service.tracker.collections.ServiceReferenceServiceTuple;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerCustomizerFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.remote.jaxrs.whiteboard.lifecycle.JAXRSLifecycle;
import com.liferay.portal.vulcan.application.HeadlessApplicationProvider;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.lang.reflect.Method;

import java.net.URI;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.runtime.JaxrsServiceRuntime;
import org.osgi.service.jaxrs.runtime.dto.ApplicationDTO;
import org.osgi.service.jaxrs.runtime.dto.ResourceDTO;
import org.osgi.service.jaxrs.runtime.dto.ResourceMethodInfoDTO;
import org.osgi.service.jaxrs.runtime.dto.RuntimeDTO;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Alejandro Tardín
 */
@Component(service = HeadlessApplicationProvider.class)
public class HeadlessApplicationProviderImpl
	implements HeadlessApplicationProvider {

	@Override
	public List<Application> getApplications() {
		List<Application> applications = new ArrayList<>();

		if (_applicationImpls == null) {
			_jaxrsLifecycle.ensureReady();

			JaxrsServiceRuntime jaxrsServiceRuntime =
				_jaxrsServiceRuntimeServiceTracker.getService();

			if (jaxrsServiceRuntime == null) {
				return applications;
			}

			RuntimeDTO runtimeDTO = jaxrsServiceRuntime.getRuntimeDTO();

			_applicationImpls = TransformUtil.transformToList(
				runtimeDTO.applicationDTOs, ApplicationImpl::new);
		}

		List<ApplicationImpl> applicationImpls = _applicationImpls;

		if (applicationImpls == null) {
			return applications;
		}

		long companyId = CompanyThreadLocal.getCompanyId();

		for (ApplicationImpl applicationImpl : applicationImpls) {
			if (_isRegistered(
					companyId,
					_companyIdsServiceTrackerMap.getService(
						applicationImpl._applicationDTO.serviceId))) {

				applications.add(applicationImpl);
			}
		}

		return applications;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_jaxrsServiceRuntimeServiceTracker = ServiceTrackerFactory.open(
			bundleContext, JaxrsServiceRuntime.class,
			new ServiceTrackerCustomizer<>() {

				@Override
				public JaxrsServiceRuntime addingService(
					ServiceReference<JaxrsServiceRuntime> serviceReference) {

					_applicationImpls = null;

					return bundleContext.getService(serviceReference);
				}

				@Override
				public void modifiedService(
					ServiceReference<JaxrsServiceRuntime> serviceReference,
					JaxrsServiceRuntime jaxrsServiceRuntime) {

					_applicationImpls = null;
				}

				@Override
				public void removedService(
					ServiceReference<JaxrsServiceRuntime> serviceReference,
					JaxrsServiceRuntime jaxrsServiceRuntime) {

					_applicationImpls = null;

					bundleContext.ungetService(serviceReference);
				}

			});

		_companyIdsServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, null,
				"(&(objectClass=jakarta.ws.rs.core.Application)(companyId=*))",
				new PropertyServiceReferenceMapper<>(Constants.SERVICE_ID),
				new ServiceTrackerCustomizer<>() {

					@Override
					public ServiceReference<?> addingService(
						ServiceReference<Object> serviceReference) {

						return serviceReference;
					}

					@Override
					public void modifiedService(
						ServiceReference<Object> serviceReference,
						ServiceReference<?> trackedServiceReference) {
					}

					@Override
					public void removedService(
						ServiceReference<Object> serviceReference,
						ServiceReference<?> trackedServiceReference) {
					}

				});

		_openAPIResourceServiceTrackerMap =
			ServiceTrackerMapFactory.openMultiValueMap(
				bundleContext, null, "(openapi.resource=true)",
				new PropertyServiceReferenceMapper<>("openapi.resource.path"),
				ServiceTrackerCustomizerFactory.serviceReferenceServiceTuple(
					bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_jaxrsServiceRuntimeServiceTracker.close();
		_companyIdsServiceTrackerMap.close();
		_openAPIResourceServiceTrackerMap.close();
	}

	private UriInfo _getUriInfo(String baseURL, String version) {
		return new UriInfo() {

			@Override
			public URI getAbsolutePath() {
				return null;
			}

			@Override
			public UriBuilder getAbsolutePathBuilder() {
				return null;
			}

			@Override
			public URI getBaseUri() {
				return URI.create(baseURL + StringPool.SLASH);
			}

			@Override
			public UriBuilder getBaseUriBuilder() {
				return UriBuilder.fromUri(getBaseUri());
			}

			@Override
			public List<Object> getMatchedResources() {
				return new ArrayList<>();
			}

			@Override
			public List<String> getMatchedURIs() {
				return new ArrayList<>();
			}

			@Override
			public List<String> getMatchedURIs(boolean decode) {
				return getMatchedURIs();
			}

			@Override
			public String getPath() {
				if (version == null) {
					return "openapi.json";
				}

				return version + "/openapi.json";
			}

			@Override
			public String getPath(boolean decode) {
				return getPath();
			}

			@Override
			public MultivaluedMap<String, String> getPathParameters() {
				return new MultivaluedHashMap<>();
			}

			@Override
			public MultivaluedMap<String, String> getPathParameters(
				boolean decode) {

				return getPathParameters();
			}

			@Override
			public List<PathSegment> getPathSegments() {
				return new ArrayList<>();
			}

			@Override
			public List<PathSegment> getPathSegments(boolean decode) {
				return getPathSegments();
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters() {
				return new MultivaluedHashMap<>();
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters(
				boolean decode) {

				return getQueryParameters();
			}

			@Override
			public URI getRequestUri() {
				return URI.create(baseURL + StringPool.SLASH + getPath());
			}

			@Override
			public UriBuilder getRequestUriBuilder() {
				return UriBuilder.fromUri(getRequestUri());
			}

			@Override
			public URI relativize(URI uri) {
				return uri;
			}

			@Override
			public URI resolve(URI uri) {
				return uri;
			}

		};
	}

	private boolean _isRegistered(
		long companyId, ServiceReference<?> serviceReference) {

		if (serviceReference == null) {
			return true;
		}

		Object companyIds = serviceReference.getProperty("companyId");

		if (companyIds == null) {
			return true;
		}

		if (companyIds instanceof List companyIdsList) {
			return companyIdsList.contains(String.valueOf(companyId));
		}

		return Objects.equals(
			String.valueOf(companyIds), String.valueOf(companyId));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HeadlessApplicationProviderImpl.class);

	private static final Pattern _versionPattern = Pattern.compile(
		"v[0-9]+\\.[0-9]+");

	private volatile List<ApplicationImpl> _applicationImpls;
	private ServiceTrackerMap<Long, ServiceReference<?>>
		_companyIdsServiceTrackerMap;

	@Reference
	private JAXRSLifecycle _jaxrsLifecycle;

	private ServiceTracker<JaxrsServiceRuntime, JaxrsServiceRuntime>
		_jaxrsServiceRuntimeServiceTracker;
	private ServiceTrackerMap
		<String, List<ServiceReferenceServiceTuple<Object, Object>>>
			_openAPIResourceServiceTrackerMap;

	private static class ResourceMethodImpl implements ResourceMethod {

		@Override
		public String getMethod() {
			return _resourceMethodInfoDTO.method;
		}

		@Override
		public String getPath() {
			return _basePath + _resourceMethodInfoDTO.path;
		}

		@Override
		public String[] getProducingMimeTypes() {
			return _resourceMethodInfoDTO.producingMimeType;
		}

		private ResourceMethodImpl(
			String basePath, ResourceMethodInfoDTO resourceMethodInfoDTO) {

			_basePath = basePath;
			_resourceMethodInfoDTO = resourceMethodInfoDTO;
		}

		private final String _basePath;
		private final ResourceMethodInfoDTO _resourceMethodInfoDTO;

	}

	private class ApplicationImpl implements Application {

		@Override
		public String getBasePath() {
			String base = _applicationDTO.base;

			if (Validator.isNull(base)) {
				return StringPool.BLANK;
			}

			if (base.startsWith(StringPool.SLASH)) {
				return base;
			}

			return StringPool.SLASH + base;
		}

		@Override
		public List<OpenAPIDocument> getOpenAPIDocuments() {
			List<OpenAPIDocument> openAPIDocuments = new ArrayList<>();

			Set<String> paths = new HashSet<>();

			for (ResourceMethodInfoDTO resourceMethodInfoDTO :
					_getResourceMethodInfoDTOs(true)) {

				String path = resourceMethodInfoDTO.path;

				if ((path == null) || !path.contains("/openapi") ||
					!paths.add(path)) {

					continue;
				}

				openAPIDocuments.add(new OpenAPIDocumentImpl(this, path));
			}

			openAPIDocuments.sort(
				Comparator.comparing(
					OpenAPIDocument::getVersion,
					Comparator.nullsFirst(Comparator.naturalOrder())));

			return openAPIDocuments;
		}

		@Override
		public List<ResourceMethod> getResourceMethods() {
			return TransformUtil.transform(
				_getResourceMethodInfoDTOs(false),
				resourceMethodInfoDTO -> new ResourceMethodImpl(
					getBasePath(), resourceMethodInfoDTO));
		}

		private ApplicationImpl(ApplicationDTO applicationDTO) {
			_applicationDTO = applicationDTO;
		}

		private List<ResourceMethodInfoDTO> _getResourceMethodInfoDTOs(
			boolean includeApplicationResourceMethods) {

			List<ResourceMethodInfoDTO> resourceMethodInfoDTOs =
				new ArrayList<>();

			if (_applicationDTO.resourceDTOs != null) {
				for (ResourceDTO resourceDTO : _applicationDTO.resourceDTOs) {
					if (resourceDTO.resourceMethods == null) {
						continue;
					}

					for (ResourceMethodInfoDTO resourceMethodInfoDTO :
							resourceDTO.resourceMethods) {

						resourceMethodInfoDTOs.add(resourceMethodInfoDTO);
					}
				}
			}

			if (includeApplicationResourceMethods &&
				(_applicationDTO.resourceMethods != null)) {

				for (ResourceMethodInfoDTO resourceMethodInfoDTO :
						_applicationDTO.resourceMethods) {

					resourceMethodInfoDTOs.add(resourceMethodInfoDTO);
				}
			}

			return resourceMethodInfoDTOs;
		}

		private final ApplicationDTO _applicationDTO;

	}

	private class OpenAPIDocumentImpl implements OpenAPIDocument {

		@Override
		public Application getApplication() {
			return _applicationImpl;
		}

		@Override
		public Object getContentObject(String serverURL) {
			return _getEntity(serverURL, Type.JSON);
		}

		@Override
		public String getContentString(String serverURL, Type type) {
			Object entity = _getEntity(serverURL, type);

			if (entity == null) {
				return null;
			}

			if (entity instanceof String) {
				return (String)entity;
			}

			return Json.pretty(entity);
		}

		@Override
		public String getDescription() {
			ServiceReferenceServiceTuple<Object, Object>
				serviceReferenceServiceTuple =
					_getServiceReferenceServiceTuple();

			if (serviceReferenceServiceTuple == null) {
				return null;
			}

			Object service = serviceReferenceServiceTuple.getService();

			if (service == null) {
				return null;
			}

			Class<?> serviceClass = service.getClass();

			OpenAPIDefinition openAPIDefinition = serviceClass.getAnnotation(
				OpenAPIDefinition.class);

			if (openAPIDefinition == null) {
				return null;
			}

			Info info = openAPIDefinition.info();

			return info.description();
		}

		@Override
		public String getPath(Type type) {
			String path = StringUtil.replace(
				_path, "{type:json|yaml}", StringUtil.toLowerCase(type.name()));

			return _applicationImpl.getBasePath() + path;
		}

		@Override
		public String getVersion() {
			String path = _path;

			if (path.startsWith(StringPool.SLASH)) {
				path = path.substring(1);
			}

			String version = StringUtil.extractFirst(path, StringPool.SLASH);

			if (version == null) {
				return null;
			}

			Matcher matcher = _versionPattern.matcher(version);

			if (matcher.matches()) {
				return version;
			}

			return null;
		}

		private OpenAPIDocumentImpl(
			ApplicationImpl applicationImpl, String path) {

			_applicationImpl = applicationImpl;
			_path = path;
		}

		private Object _getEntity(String serverURL, Type type) {
			ServiceReferenceServiceTuple<Object, Object>
				serviceReferenceServiceTuple =
					_getServiceReferenceServiceTuple();

			if (serviceReferenceServiceTuple == null) {
				return null;
			}

			try {
				Object service = serviceReferenceServiceTuple.getService();

				Class<?> serviceClass = service.getClass();

				Method method = serviceClass.getMethod(
					"getOpenAPI", HttpServletRequest.class, String.class,
					UriInfo.class);

				Response response = (Response)method.invoke(
					service, null, StringUtil.toLowerCase(type.name()),
					_getUriInfo(
						serverURL + _applicationImpl.getBasePath(),
						getVersion()));

				return response.getEntity();
			}
			catch (Exception exception) {
				_log.error(exception);

				return null;
			}
		}

		private ServiceReferenceServiceTuple<Object, Object>
			_getServiceReferenceServiceTuple() {

			List<ServiceReferenceServiceTuple<Object, Object>>
				serviceReferenceServiceTuples =
					_openAPIResourceServiceTrackerMap.getService(
						_applicationImpl.getBasePath());

			if (serviceReferenceServiceTuples == null) {
				return null;
			}

			long companyId = CompanyThreadLocal.getCompanyId();
			String version = getVersion();

			for (ServiceReferenceServiceTuple<Object, Object>
					serviceReferenceServiceTuple :
						serviceReferenceServiceTuples) {

				ServiceReference<Object> serviceReference =
					serviceReferenceServiceTuple.getServiceReference();

				if (Objects.equals(
						version, serviceReference.getProperty("api.version")) &&
					_isRegistered(companyId, serviceReference)) {

					return serviceReferenceServiceTuple;
				}
			}

			return null;
		}

		private final ApplicationImpl _applicationImpl;
		private final String _path;

	}

}