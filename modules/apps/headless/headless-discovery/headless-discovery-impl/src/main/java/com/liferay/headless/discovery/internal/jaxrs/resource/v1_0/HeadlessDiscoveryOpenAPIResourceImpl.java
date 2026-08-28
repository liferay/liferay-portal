/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.discovery.internal.jaxrs.resource.v1_0;

import com.liferay.headless.discovery.internal.jaxrs.application.HeadlessDiscoveryOpenAPIApplication;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.application.HeadlessApplicationProvider;
import com.liferay.portal.vulcan.openapi.OpenAPIContext;
import com.liferay.portal.vulcan.resource.OpenAPIResource;
import com.liferay.portal.vulcan.util.UriInfoUtil;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
	public Response getGlobalOpenAPI(@PathParam("type") String type) {
		Map<OpenAPIContext, Response> responses = new HashMap<>();

		Map<String, List<HeadlessApplicationProvider.OpenAPIDocument>>
			openAPIDocumentsMap = _getOpenAPIDocumentsMap();

		for (Map.Entry
				<String, List<HeadlessApplicationProvider.OpenAPIDocument>>
					entry : openAPIDocumentsMap.entrySet()) {

			String path = entry.getKey();

			for (HeadlessApplicationProvider.OpenAPIDocument openAPIDocument :
					entry.getValue()) {

				Object openAPI = openAPIDocument.getContentObject(
					_getServerURL());

				if (openAPI == null) {
					continue;
				}

				OpenAPIContext openAPIContext = new OpenAPIContext();

				openAPIContext.setPath(path);
				openAPIContext.setVersion(openAPIDocument.getVersion());

				responses.put(
					openAPIContext,
					Response.ok(
						openAPI
					).build());
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

	private Map<String, List<HeadlessApplicationProvider.OpenAPIDocument>>
		_getOpenAPIDocumentsMap() {

		Map<String, List<HeadlessApplicationProvider.OpenAPIDocument>>
			openAPIDocumentsMap = new TreeMap<>();

		for (HeadlessApplicationProvider.Application application :
				_headlessApplicationProvider.getApplications()) {

			if (StringUtil.equals(
					HeadlessDiscoveryOpenAPIApplication.BASE_PATH,
					application.getBasePath())) {

				continue;
			}

			List<HeadlessApplicationProvider.OpenAPIDocument> openAPIDocuments =
				application.getOpenAPIDocuments();

			if (openAPIDocuments.isEmpty()) {
				continue;
			}

			openAPIDocumentsMap.put(
				application.getBasePath(), openAPIDocuments);
		}

		return openAPIDocumentsMap;
	}

	private Map<String, List<String>> _getOpenAPIMap(String accept) {
		Map<String, List<String>> openAPIMap = new TreeMap<>();

		String serverURL = _getServerURL();

		Map<String, List<HeadlessApplicationProvider.OpenAPIDocument>>
			openAPIDocumentsMap = _getOpenAPIDocumentsMap();

		for (Map.Entry
				<String, List<HeadlessApplicationProvider.OpenAPIDocument>>
					entry : openAPIDocumentsMap.entrySet()) {

			List<String> paths = new ArrayList<>();

			for (HeadlessApplicationProvider.OpenAPIDocument openAPIDocument :
					entry.getValue()) {

				String path = openAPIDocument.getPath(
					HeadlessApplicationProvider.OpenAPIDocument.Type.YAML);

				paths.add(serverURL + path);
			}

			String baseURL = entry.getKey();

			if ((accept != null) &&
				accept.contains(MediaType.APPLICATION_XML)) {

				baseURL = baseURL.substring(1);
			}

			openAPIMap.put(baseURL, paths);
		}

		return openAPIMap;
	}

	private String _getServerURL() {
		return _portal.getPortalURL(_httpServletRequest) +
			_portal.getPathContext() + Portal.PATH_MODULE;
	}

	@Reference
	private HeadlessApplicationProvider _headlessApplicationProvider;

	@Context
	private HttpServletRequest _httpServletRequest;

	@Reference
	private OpenAPIResource _openAPIResource;

	@Reference
	private Portal _portal;

	@Context
	private UriInfo _uriInfo;

}