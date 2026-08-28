/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.application;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
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
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.jaxrs.runtime.JaxrsServiceRuntime;
import org.osgi.service.jaxrs.runtime.dto.ApplicationDTO;
import org.osgi.service.jaxrs.runtime.dto.ResourceDTO;
import org.osgi.service.jaxrs.runtime.dto.ResourceMethodInfoDTO;
import org.osgi.service.jaxrs.runtime.dto.RuntimeDTO;

/**
 * @author Alejandro Tardín
 */
@Component(service = HeadlessApplicationProvider.class)
public class HeadlessApplicationProviderImpl
	implements HeadlessApplicationProvider {

	@Override
	public List<Application> getApplications() {
		JaxrsServiceRuntime jaxrsServiceRuntime =
			_jaxrsServiceRuntimeSnapshot.get();

		RuntimeDTO runtimeDTO = jaxrsServiceRuntime.getRuntimeDTO();

		return TransformUtil.transform(
			ListUtil.filter(
				ListUtil.fromArray(runtimeDTO.applicationDTOs),
				applicationDTO -> _isRegisteredForCompany(
					applicationDTO.serviceId)),
			applicationDTO -> new ApplicationImpl(
				_bundleContext, applicationDTO));
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	private ServiceReference<?> _getServiceReference(long serviceId) {
		try {
			ServiceReference<?>[] serviceReferences =
				_bundleContext.getAllServiceReferences(
					null, "(service.id=" + serviceId + ")");

			if (ArrayUtil.isEmpty(serviceReferences)) {
				return null;
			}

			return serviceReferences[0];
		}
		catch (InvalidSyntaxException invalidSyntaxException) {
			_log.error(invalidSyntaxException);

			return null;
		}
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

	private boolean _isRegisteredForCompany(long serviceId) {
		ServiceReference<?> serviceReference = _getServiceReference(serviceId);

		if (serviceReference == null) {
			return true;
		}

		return _isRegisteredForCompany(
			CompanyThreadLocal.getCompanyId(), serviceReference);
	}

	private boolean _isRegisteredForCompany(
		long companyId, ServiceReference<?> serviceReference) {

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

	private static final Snapshot<JaxrsServiceRuntime>
		_jaxrsServiceRuntimeSnapshot = new Snapshot<>(
			HeadlessApplicationProviderImpl.class, JaxrsServiceRuntime.class);
	private static final Pattern _versionPattern = Pattern.compile(
		"v[0-9]+\\.[0-9]+");

	private BundleContext _bundleContext;

	private static class ApplicationImpl implements Application {

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

		private ApplicationImpl(
			BundleContext bundleContext, ApplicationDTO applicationDTO) {

			_bundleContext = bundleContext;
			_applicationDTO = applicationDTO;
		}

		private BundleContext _getBundleContext() {
			return _bundleContext;
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
		private final BundleContext _bundleContext;

	}

	private static class OpenAPIDocumentImpl implements OpenAPIDocument {

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
			ServiceReference<?> serviceReference = _getServiceReference();

			if (serviceReference == null) {
				return null;
			}

			BundleContext bundleContext = _applicationImpl._getBundleContext();

			try {
				return _getDescription(
					bundleContext.getService(serviceReference));
			}
			finally {
				bundleContext.ungetService(serviceReference);
			}
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

		private String _getDescription(Object service) {
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

		private Object _getEntity(String serverURL, Type type) {
			ServiceReference<?> serviceReference = _getServiceReference();

			if (serviceReference == null) {
				return null;
			}

			BundleContext bundleContext = _applicationImpl._getBundleContext();

			try {
				Object service = bundleContext.getService(serviceReference);

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
			finally {
				bundleContext.ungetService(serviceReference);
			}
		}

		private ServiceReference<?> _getServiceReference() {
			BundleContext bundleContext = _applicationImpl._getBundleContext();

			String filterString = "(!(api.version=*))";

			String version = getVersion();

			if (version != null) {
				filterString = "(api.version=" + version + ")";
			}

			try {
				ServiceReference<?>[] serviceReferences =
					bundleContext.getAllServiceReferences(
						null,
						StringBundler.concat(
							"(&(openapi.resource=true)(openapi.resource.path=",
							_applicationImpl.getBasePath(), ")", filterString,
							")"));

				if (ArrayUtil.isEmpty(serviceReferences)) {
					return null;
				}

				long companyId = CompanyThreadLocal.getCompanyId();

				for (ServiceReference<?> serviceReference : serviceReferences) {
					long serviceCompanyId = GetterUtil.getLong(
						serviceReference.getProperty("companyId"), companyId);

					if (serviceCompanyId == companyId) {
						return serviceReference;
					}
				}

				return null;
			}
			catch (InvalidSyntaxException invalidSyntaxException) {
				_log.error(invalidSyntaxException);

				return null;
			}
		}

		private final ApplicationImpl _applicationImpl;
		private final String _path;

	}

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

}