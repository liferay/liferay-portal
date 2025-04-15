/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.discovery.internal.jaxrs.application;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.headless.discovery.internal.configuration.HeadlessDiscoveryConfiguration;
import com.liferay.headless.discovery.internal.dto.Hint;
import com.liferay.headless.discovery.internal.dto.Resource;
import com.liferay.headless.discovery.internal.dto.Resources;
import com.liferay.learn.LearnMessageUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.ws.rs.core.UriInfo;

import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.runtime.JaxrsServiceRuntime;
import org.osgi.service.jaxrs.runtime.dto.ApplicationDTO;
import org.osgi.service.jaxrs.runtime.dto.ResourceDTO;
import org.osgi.service.jaxrs.runtime.dto.ResourceMethodInfoDTO;
import org.osgi.service.jaxrs.runtime.dto.RuntimeDTO;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

/**
 * @author Javier Gamarra
 */
@Component(
	configurationPid = "com.liferay.headless.discovery.internal.configuration.HeadlessDiscoveryConfiguration",
	property = {
		JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE + "=/api",
		JaxrsWhiteboardConstants.JAX_RS_EXTENSION_SELECT + "=(osgi.jaxrs.name=Liferay.Vulcan)",
		JaxrsWhiteboardConstants.JAX_RS_NAME + "=Liferay.Headless.Discovery.API",
		"auth.verifier.auth.verifier.PortalSessionAuthVerifier.check.csrf.token=false"
	},
	service = Application.class
)
public class HeadlessDiscoveryAPIApplication extends Application {

	@GET
	@Produces({"application/json", "application/xml", "text/html"})
	public Response discovery(
			@HeaderParam("Accept") String accept,
			@Context HttpServletRequest httpServletRequest,
			@Context HttpServletResponse httpServletResponse)
		throws Exception {

		if (!_headlessDiscoveryConfiguration.enableAPIExplorer()) {
			return Response.status(
				404
			).build();
		}

		if ((accept != null) && accept.contains(MediaType.TEXT_HTML)) {
			URL url = _getURL("index.html");

			if (url == null) {
				return Response.serverError(
				).build();
			}

			try (InputStream urlInputStream = url.openStream();
				Scanner scanner = new Scanner(urlInputStream, "UTF-8")) {

				scanner.useDelimiter("\\A");

				String html = StringUtil.replace(
					scanner.next(), "%CSRF-TOKEN%",
					AuthTokenUtil.getToken(httpServletRequest));

				html = StringUtil.replace(
					html, "href=\"headless-discovery-web-min.css\"",
					"href=\"" + _portal.getPathContext() +
						"/o/api/headless-discovery-web-min.css\"");
				html = StringUtil.replace(
					html, "href=\"main.css\"",
					"href=\"" + _portal.getPathContext() + "/o/api/main.css\"");
				html = StringUtil.replace(
					html, "src=\"headless-discovery-web-min.js\"",
					"src=\"" + _portal.getPathContext() +
						"/o/api/headless-discovery-web-min.js\"");
				html = StringUtil.replace(
					html, "</head>",
					StringBundler.concat(
						"<script>window.learnResources = ",
						LearnMessageUtil.getJSONObject(
							"headless-discovery-web"),
						";</script></head>"));

				String finalHtml = html;

				return Response.ok(
					(StreamingOutput)outputStream -> outputStream.write(
						finalHtml.getBytes())
				).build();
			}
		}

		Map<String, List<ResourceMethodInfoDTO>> resourceMethodInfoDTOsMap =
			_getResourceMethodInfoDTOsMap();

		Map<String, Resource> resourcesMap = new TreeMap<>();

		for (Map.Entry<String, List<ResourceMethodInfoDTO>> entry :
				resourceMethodInfoDTOsMap.entrySet()) {

			resourcesMap.put(entry.getKey(), _getResource(entry.getValue()));
		}

		Resources resources = new Resources(resourcesMap);

		if ((accept != null) &&
			accept.contains(MediaType.APPLICATION_XHTML_XML)) {

			ObjectMapper objectMapper = new ObjectMapper();

			return Response.ok(
				objectMapper.writerWithDefaultPrettyPrinter(
				).writeValueAsString(
					resources
				),
				MediaType.APPLICATION_JSON_TYPE
			).build();
		}

		return Response.ok(
			resources
		).build();
	}

	@GET
	@Path("/{parameter}")
	@Produces({"text/css", "text/javascript"})
	public Response discoveryParameter(
			@HeaderParam("Accept") String accept,
			@Context HttpServletRequest httpServletRequest,
			@Context HttpServletResponse httpServletResponse,
			@PathParam("parameter") String parameter)
		throws Exception {

		if (parameter.contains("..")) {
			return Response.status(
				Response.Status.FORBIDDEN
			).build();
		}

		URL url = _getURL(parameter);

		if (url == null) {
			return Response.serverError(
			).build();
		}

		Response.ResponseBuilder responseBuilder = Response.ok(
			(StreamingOutput)outputStream -> {
				try (InputStream urlInputStream = url.openStream()) {
					StreamUtil.transfer(urlInputStream, outputStream);
				}
			});

		if (parameter.endsWith(".css")) {
			responseBuilder.type("text/css");
		}
		else {
			responseBuilder.type("text/javascript");
		}

		return responseBuilder.build();
	}

	public Set<Object> getSingletons() {
		return Collections.singleton(this);
	}

	@Activate
	@Modified
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;
		_headlessDiscoveryConfiguration = ConfigurableUtil.createConfigurable(
			HeadlessDiscoveryConfiguration.class, properties);
	}

	private Resource _getResource(
		List<ResourceMethodInfoDTO> resourceMethodInfoDTOS) {

		Resource resource = new Resource();

		ResourceMethodInfoDTO resourceMethodInfoDTO =
			resourceMethodInfoDTOS.get(0);

		resource.setHint(
			new Hint(
				TransformUtil.transformToArray(
					resourceMethodInfoDTOS, dto -> dto.method, String.class),
				resourceMethodInfoDTO.producingMimeType));

		String resourcePath = resourceMethodInfoDTO.path;

		if (resourcePath.contains("{")) {
			resource.setHrefTemplate(resourcePath);
		}
		else {
			resource.setHref(resourcePath);
		}

		return resource;
	}

	private Map<String, List<ResourceMethodInfoDTO>>
		_getResourceMethodInfoDTOsMap() {

		Map<String, List<ResourceMethodInfoDTO>> resourcesMap = new TreeMap<>();

		String absolutePath = String.valueOf(_uriInfo.getAbsolutePath());

		String serverURL = StringUtil.removeSubstring(absolutePath, "/api/");

		JaxrsServiceRuntime jaxrsServiceRuntime =
			_jaxrsServiceRuntimeSnapshot.get();

		RuntimeDTO runtimeDTO = jaxrsServiceRuntime.getRuntimeDTO();

		for (ApplicationDTO applicationDTO : runtimeDTO.applicationDTOs) {
			for (ResourceDTO resourceDTO : applicationDTO.resourceDTOs) {
				for (ResourceMethodInfoDTO resourceMethodInfoDTO :
						resourceDTO.resourceMethods) {

					resourceMethodInfoDTO.path =
						applicationDTO.base + resourceMethodInfoDTO.path;

					String path = serverURL + resourceMethodInfoDTO.path;

					List<ResourceMethodInfoDTO> resourceMethodInfoDTOS =
						resourcesMap.get(path);

					if (resourceMethodInfoDTOS == null) {
						resourceMethodInfoDTOS = new ArrayList<>();
					}

					resourceMethodInfoDTOS.add(resourceMethodInfoDTO);

					resourcesMap.put(path, resourceMethodInfoDTOS);
				}
			}
		}

		return resourcesMap;
	}

	private URL _getURL(String parameter) {
		Bundle bundle = BundleUtil.getBundle(
			_bundleContext, "com.liferay.headless.discovery.web");

		if (bundle == null) {
			return null;
		}

		return bundle.getEntry("META-INF/resources/" + parameter);
	}

	private static final Snapshot<JaxrsServiceRuntime>
		_jaxrsServiceRuntimeSnapshot = new Snapshot<>(
			HeadlessDiscoveryAPIApplication.class, JaxrsServiceRuntime.class);

	private volatile BundleContext _bundleContext;
	private volatile HeadlessDiscoveryConfiguration
		_headlessDiscoveryConfiguration;

	@Reference
	private Portal _portal;

	@Context
	private UriInfo _uriInfo;

}