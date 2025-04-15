/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.discovery.internal.jaxrs.resource.v1_0;

import com.liferay.headless.discovery.internal.jaxrs.application.HeadlessDiscoveryOpenAPIApplication;
import com.liferay.osgi.service.tracker.collections.EagerServiceTrackerCustomizer;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.openapi.OpenAPIContext;
import com.liferay.portal.vulcan.resource.OpenAPIResource;
import com.liferay.portal.vulcan.util.UriInfoUtil;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.net.URI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;
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

/**
 * @author Carlos Correa
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Discovery.OpenAPI)",
		"osgi.jaxrs.resource=true"
	},
	service = HeadlessDiscoveryOpenAPIResourceImpl.class
)
public class HeadlessDiscoveryOpenAPIResourceImpl {

	@GET
	@Path("/openapi.{type:json|yaml}")
	@Produces({"application/json", "application/xml"})
	public Response getGlobalOpenAPI(@PathParam("type") String type)
		throws Exception {

		Map<OpenAPIContext, Response> responses = new HashMap<>();

		Map<String, List<String>> openAPIMap = _getOpenAPIMap(null);

		for (Map.Entry<String, List<String>> entry : openAPIMap.entrySet()) {
			String path = entry.getKey();

			for (String openAPIPath : entry.getValue()) {
				String version = _getVersion(path, openAPIPath);

				Object resource = _getOpenAPIResource(path, version);

				if (resource == null) {
					continue;
				}

				Class<?> resourceClass = resource.getClass();

				for (Field field : resourceClass.getDeclaredFields()) {
					Class<?> fieldClass = field.getType();

					if (fieldClass.isAssignableFrom(OpenAPIResource.class)) {
						field.setAccessible(true);

						field.set(resource, _openAPIResource);
					}
				}

				Method method = resourceClass.getDeclaredMethod(
					"getOpenAPI", HttpServletRequest.class, String.class,
					UriInfo.class);

				Response response = (Response)method.invoke(
					resource, _httpServletRequest, "json",
					_getUriInfo(path, version));

				OpenAPIContext openAPIContext = new OpenAPIContext();

				openAPIContext.setPath(path);
				openAPIContext.setVersion(version);

				responses.put(openAPIContext, response);
			}
		}

		return _openAPIResource.mergeOpenAPIs(
			"OpenAPI Specification of All Liferay REST APIs", responses,
			StringUtil.removeLast(
				UriInfoUtil.getBasePath(_uriInfo),
				HeadlessDiscoveryOpenAPIApplication.BASE_PATH + CharPool.SLASH),
			"Global REST API - OpenAPI", type);
	}

	@GET
	@Produces({"application/json", "application/xml"})
	public Map<String, List<String>> openAPI(
		@HeaderParam("Accept") String accept) {

		return _getOpenAPIMap(accept);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, Application.class, "companyId",
			new EagerServiceTrackerCustomizer<Application, Application>() {

				@Override
				public Application addingService(
					ServiceReference<Application> serviceReference) {

					_populateCompanyIds(serviceReference);

					return bundleContext.getService(serviceReference);
				}

				@Override
				public void modifiedService(
					ServiceReference<Application> serviceReference,
					Application application) {

					_populateCompanyIds(serviceReference);
				}

				@Override
				public void removedService(
					ServiceReference<Application> serviceReference,
					Application application) {

					Object osgiJaxRsApplicationBase =
						serviceReference.getProperty(
							"osgi.jaxrs.application.base");

					if (osgiJaxRsApplicationBase instanceof String) {
						_companyIds.remove(osgiJaxRsApplicationBase);
					}

					bundleContext.ungetService(serviceReference);
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private void _addPaths(
		String basePath, List<String> paths,
		ResourceMethodInfoDTO[] resourceMethodInfoDTOS, String serverURL) {

		for (ResourceMethodInfoDTO resourceMethodInfoDTO :
				resourceMethodInfoDTOS) {

			String path = resourceMethodInfoDTO.path;

			if (path.contains("/openapi")) {
				String openAPIPath = StringUtil.replace(
					resourceMethodInfoDTO.path, "{type:json|yaml}", "yaml");

				paths.add(serverURL + basePath + openAPIPath);
			}
		}
	}

	private ApplicationDTO[] _getApplicationDTOs(
		ApplicationDTO[] applicationDTOS) {

		return ArrayUtil.filter(
			applicationDTOS,
			applicationDTO -> {
				if (StringUtil.equals(
						HeadlessDiscoveryOpenAPIApplication.BASE_PATH,
						applicationDTO.base)) {

					return false;
				}

				if (_companyIds.containsKey(applicationDTO.base)) {
					List<String> companyIds = _companyIds.get(
						applicationDTO.base);

					return companyIds.contains(
						String.valueOf(CompanyThreadLocal.getCompanyId()));
				}

				return true;
			});
	}

	private Map<String, List<String>> _getOpenAPIMap(String accept) {
		Map<String, List<String>> openAPIMap = new TreeMap<>();

		String serverURL =
			_portal.getPortalURL(_httpServletRequest) +
				_portal.getPathContext() + Portal.PATH_MODULE;

		JaxrsServiceRuntime jaxrsServiceRuntime =
			_jaxrsServiceRuntimeSnapshot.get();

		RuntimeDTO runtimeDTO = jaxrsServiceRuntime.getRuntimeDTO();

		for (ApplicationDTO applicationDTO :
				_getApplicationDTOs(runtimeDTO.applicationDTOs)) {

			List<String> paths = new ArrayList<>();

			String base = applicationDTO.base;

			if (!base.startsWith(StringPool.FORWARD_SLASH)) {
				base = StringPool.FORWARD_SLASH + base;
			}

			for (ResourceDTO resourceDTO : applicationDTO.resourceDTOs) {
				_addPaths(base, paths, resourceDTO.resourceMethods, serverURL);
			}

			_addPaths(base, paths, applicationDTO.resourceMethods, serverURL);

			if (paths.isEmpty()) {
				continue;
			}

			String baseURL = base;

			if ((accept != null) &&
				accept.contains(MediaType.APPLICATION_XML)) {

				baseURL = baseURL.substring(1);
			}

			openAPIMap.put(baseURL, paths);
		}

		return openAPIMap;
	}

	private Object _getOpenAPIResource(String path, String version) {
		try {
			String filterString = "(api.version=" + version + ")";

			if (version == null) {
				filterString = "(!(api.version=*))";
			}

			ServiceReference<?>[] serviceReferences =
				_bundleContext.getServiceReferences(
					(String)null,
					StringBundler.concat(
						"(&(openapi.resource=true)(openapi.resource.path=",
						path, ")", filterString, ")"));

			for (ServiceReference<?> serviceReference : serviceReferences) {
				long companyId = GetterUtil.get(
					serviceReference.getProperty("companyId"),
					_company.getCompanyId());

				if (companyId == _company.getCompanyId()) {
					return _bundleContext.getService(serviceReference);
				}
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return null;
	}

	private UriInfo _getUriInfo(String basePath, String version) {
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
				URI uri = _uriInfo.getBaseUri();

				return URI.create(
					StringUtil.replace(
						uri.toString(),
						HeadlessDiscoveryOpenAPIApplication.BASE_PATH,
						basePath));
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
				return new ArrayList<>();
			}

			@Override
			public String getPath() {
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

				return new MultivaluedHashMap<>();
			}

			@Override
			public List<PathSegment> getPathSegments() {
				return new ArrayList<>();
			}

			@Override
			public List<PathSegment> getPathSegments(boolean decode) {
				return new ArrayList<>();
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters() {
				return new MultivaluedHashMap<>();
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters(
				boolean decode) {

				return new MultivaluedHashMap<>();
			}

			@Override
			public URI getRequestUri() {
				return null;
			}

			@Override
			public UriBuilder getRequestUriBuilder() {
				return null;
			}

			@Override
			public URI relativize(URI uri) {
				return null;
			}

			@Override
			public URI resolve(URI uri) {
				return null;
			}

		};
	}

	private String _getVersion(String basePath, String openAPIPath) {
		String subpath = openAPIPath.substring(
			openAPIPath.indexOf(basePath) + basePath.length() + 1);

		String version = StringUtil.extractFirst(subpath, StringPool.SLASH);

		if (version == null) {
			return null;
		}

		Matcher versionMatcher = _versionPattern.matcher(version);

		if (versionMatcher.matches()) {
			return version;
		}

		return null;
	}

	private void _populateCompanyIds(
		ServiceReference<Application> serviceReference) {

		Object companyIds = serviceReference.getProperty("companyId");
		Object osgiJaxRsApplicationBase = serviceReference.getProperty(
			"osgi.jaxrs.application.base");

		if ((companyIds instanceof List) &&
			(osgiJaxRsApplicationBase instanceof String)) {

			_companyIds.put(
				(String)osgiJaxRsApplicationBase, (List<String>)companyIds);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HeadlessDiscoveryOpenAPIResourceImpl.class);

	private static final Snapshot<JaxrsServiceRuntime>
		_jaxrsServiceRuntimeSnapshot = new Snapshot<>(
			HeadlessDiscoveryOpenAPIResourceImpl.class,
			JaxrsServiceRuntime.class);
	private static final Pattern _versionPattern = Pattern.compile(
		"v[0-9]+\\.[0-9]+");

	private BundleContext _bundleContext;

	@Context
	private Company _company;

	private final Map<String, List<String>> _companyIds = new HashMap<>();

	@Context
	private HttpServletRequest _httpServletRequest;

	@Reference
	private OpenAPIResource _openAPIResource;

	@Reference
	private Portal _portal;

	private ServiceTrackerMap<String, Application> _serviceTrackerMap;

	@Context
	private UriInfo _uriInfo;

}