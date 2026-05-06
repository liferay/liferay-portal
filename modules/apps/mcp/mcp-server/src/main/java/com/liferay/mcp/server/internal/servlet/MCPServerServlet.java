/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.internal.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.mcp.server.internal.configuration.MCPServerConfiguration;
import com.liferay.mcp.server.internal.constants.MCPServerConstants;
import com.liferay.mcp.server.internal.util.OpenAPIUtil;
import com.liferay.oauth2.provider.constants.OAuth2AuthorizationConstants;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import io.modelcontextprotocol.common.McpTransportContext;
import io.modelcontextprotocol.json.jackson2.JacksonMcpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpStatelessServerFeatures;
import io.modelcontextprotocol.server.McpStatelessSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletStatelessServerTransport;
import io.modelcontextprotocol.spec.McpSchema;

import jakarta.servlet.GenericServlet;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leandro Aguiar
 * @author Vendel Toreki
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"osgi.http.whiteboard.context.path=/mcp",
		"osgi.http.whiteboard.servlet.name=com.liferay.mcp.server.internal.servlet.MCPServerServlet",
		"osgi.http.whiteboard.servlet.pattern=/mcp",
		"osgi.http.whiteboard.servlet.pattern=/mcp/*"
	},
	service = Servlet.class
)
public class MCPServerServlet extends HttpServlet {

	@Override
	public void destroy() {
		synchronized (this) {
			for (Map.Entry<String, Servlet> entry : _servlets.entrySet()) {
				Servlet servlet = entry.getValue();

				servlet.destroy();
			}

			_servlets.clear();
		}
	}

	public void invalidate(long companyId, String profileName) {
		synchronized (this) {
			Servlet servlet = _servlets.remove(
				_getServletKey(companyId, profileName));

			if (servlet != null) {
				servlet.destroy();
			}
		}
	}

	public void invalidateAll(long companyId) {
		synchronized (this) {
			String companyIdString = String.valueOf(companyId);

			for (String servletKey : new ArrayList<>(_servlets.keySet())) {
				if (servletKey.equals(companyIdString) ||
					servletKey.startsWith(
						companyIdString + StringPool.UNDERLINE)) {

					Servlet servlet = _servlets.remove(servletKey);

					if (servlet != null) {
						servlet.destroy();
					}
				}
			}
		}
	}

	@Override
	public void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		long companyId = _portal.getCompanyId(httpServletRequest);

		if (!_isEnabled(companyId)) {
			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		if (!_authenticate(httpServletRequest, httpServletResponse)) {
			return;
		}

		MCPServerProfile mcpServerProfile = null;

		String profileName = _getProfileName(httpServletRequest);

		if (profileName != null) {
			mcpServerProfile = _getMCPServerProfile(companyId, profileName);

			if (mcpServerProfile == null) {
				httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

				return;
			}
		}

		Servlet servlet = _getServlet(
			httpServletRequest.getHeader("Authorization"),
			_portal.getPortalURL(httpServletRequest) + _portal.getPathModule(),
			companyId, mcpServerProfile);

		servlet.service(httpServletRequest, httpServletResponse);
	}

	private boolean _authenticate(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		String authorization = httpServletRequest.getHeader("Authorization");

		if (Validator.isBlank(authorization)) {
			_sendInvalidTokenChallenge(
				httpServletRequest, httpServletResponse,
				"Missing Authorization header");

			return false;
		}

		String[] parts = authorization.split("\\s+");

		if ((parts.length != 2) ||
			!StringUtil.equalsIgnoreCase(parts[0], "Bearer")) {

			_sendInvalidTokenChallenge(
				httpServletRequest, httpServletResponse,
				"Authorization header is not a Bearer token");

			return false;
		}

		String accessToken = parts[1];

		OAuth2Authorization oAuth2Authorization =
			_oAuth2AuthorizationLocalService.
				fetchOAuth2AuthorizationByAccessTokenContent(accessToken);

		if ((oAuth2Authorization == null) ||
			OAuth2AuthorizationConstants.ACCESS_TOKEN_CONTENT_EXPIRED_TOKEN.
				equals(oAuth2Authorization.getAccessTokenContent())) {

			_sendInvalidTokenChallenge(
				httpServletRequest, httpServletResponse,
				"Access token is unknown or revoked");

			return false;
		}

		Date expirationDate =
			oAuth2Authorization.getAccessTokenExpirationDate();

		if ((expirationDate != null) && expirationDate.before(new Date())) {
			_sendInvalidTokenChallenge(
				httpServletRequest, httpServletResponse,
				"Access token has expired");

			return false;
		}

		String mcpResourceURI =
			_portal.getPortalURL(httpServletRequest) + _MCP_PATH;

		List<String> audiences = oAuth2Authorization.getAudiencesList();

		if (!audiences.contains(mcpResourceURI)) {
			_sendInsufficientScopeChallenge(
				httpServletRequest, httpServletResponse,
				"Access token is not bound to this MCP server");

			return false;
		}

		return true;
	}

	private Servlet _buildServlet(
		String baseURL, long companyId, String authorization,
		MCPServerProfile mcpServerProfile) {

		HttpServletStatelessServerTransport
			httpServletStatelessServerTransport =
				HttpServletStatelessServerTransport.builder(
				).messageEndpoint(
					(mcpServerProfile != null) ?
						"/mcp/" + mcpServerProfile._name : "/mcp"
				).contextExtractor(
					request -> McpTransportContext.create(
						HashMapBuilder.<String, Object>put(
							"authorization", request.getHeader("Authorization")
						).put(
							"liferayAIHubCellOnBehalfOf",
							request.getHeader(
								"Liferay-AI-Hub-Cell-On-Behalf-Of")
						).build())
				).build();

		List<McpStatelessServerFeatures.SyncToolSpecification>
			syncToolSpecifications = new ArrayList<>();

		if (mcpServerProfile != null) {
			Map<String, String> openAPIJSONStringCache = new HashMap<>();

			syncToolSpecifications.addAll(
				TransformUtil.transformToList(
					mcpServerProfile._endpoints,
					endpoint ->
						new McpStatelessServerFeatures.SyncToolSpecification(
							OpenAPIUtil.getTool(
								endpoint,
								openAPIJSONStringCache.computeIfAbsent(
									OpenAPIUtil.getOpenAPIURL(endpoint),
									key -> _getOpenAPIJSONString(
										baseURL, authorization, key))),
							(mcpTransportContext, callToolRequest) -> {
								OpenAPIUtil.HttpCallArguments
									httpCallArguments =
										OpenAPIUtil.getHttpCallArguments(
											callToolRequest.arguments(),
											baseURL, endpoint);

								return _call(
									httpCallArguments.getBody(),
									httpCallArguments.getURL(),
									mcpTransportContext,
									httpCallArguments.getMethod());
							})));
		}
		else {
			JSONObject toolsJSONObject = _getToolsJSONObject(baseURL);

			syncToolSpecifications.add(
				new McpStatelessServerFeatures.SyncToolSpecification(
					_getTool("call-http-endpoint", toolsJSONObject),
					(mcpTransportContext, callToolRequest) -> {
						Map<String, Object> arguments =
							callToolRequest.arguments();

						String path = String.valueOf(arguments.get("path"));

						if (!path.startsWith("/")) {
							path = "/" + path;
						}

						return _call(
							String.valueOf(arguments.get("payload")),
							baseURL + path, mcpTransportContext,
							String.valueOf(arguments.get("method")));
					}));
			syncToolSpecifications.add(
				new McpStatelessServerFeatures.SyncToolSpecification(
					_getTool("get-openapi", toolsJSONObject),
					(mcpTransportContext, callToolRequest) -> _call(
						null,
						String.valueOf(
							callToolRequest.arguments(
							).get(
								"url"
							)),
						mcpTransportContext, "GET")));
			syncToolSpecifications.add(
				new McpStatelessServerFeatures.SyncToolSpecification(
					_getTool("get-openapis", toolsJSONObject),
					(mcpTransportContext, callToolRequest) -> _call(
						null, baseURL + "/openapi", mcpTransportContext,
						"GET")));
		}

		McpStatelessSyncServer mcpStatelessSyncServer = McpServer.sync(
			httpServletStatelessServerTransport
		).capabilities(
			McpSchema.ServerCapabilities.builder(
			).tools(
				true
			).prompts(
				true
			).build()
		).tools(
			syncToolSpecifications
		).prompts(
			_getSyncPromptSpecifications(companyId)
		).build();

		return new GenericServlet() {

			@Override
			public void destroy() {
				mcpStatelessSyncServer.closeGracefully();
			}

			@Override
			public void service(
					ServletRequest servletRequest,
					ServletResponse servletResponse)
				throws IOException, ServletException {

				HttpServletRequest httpServletRequest =
					(HttpServletRequest)servletRequest;

				if (Objects.equals(httpServletRequest.getMethod(), "GET")) {
					HttpServletResponse httpServletResponse =
						(HttpServletResponse)servletResponse;

					httpServletResponse.setContentType("text/event-stream");
					httpServletResponse.setHeader("Cache-Control", "no-cache");
					httpServletResponse.setStatus(HttpServletResponse.SC_OK);

					httpServletResponse.flushBuffer();

					return;
				}

				httpServletStatelessServerTransport.service(
					servletRequest, servletResponse);
			}

		};
	}

	private McpSchema.CallToolResult _call(
		String body, String location, McpTransportContext mcpTransportContext,
		String method) {

		Http.Options options = new Http.Options();

		if (Validator.isNotNull(body)) {
			options.setBody(
				body, ContentTypes.APPLICATION_JSON, StringPool.UTF8);
		}

		Object liferayAIHubCellOnBehalfOf = mcpTransportContext.get(
			"liferayAIHubCellOnBehalfOf");

		options.setHeaders(
			HashMapBuilder.put(
				"Authorization",
				() -> {
					Object authorization = mcpTransportContext.get(
						"authorization");

					if ((authorization == null) ||
						(liferayAIHubCellOnBehalfOf != null)) {

						return null;
					}

					return String.valueOf(authorization);
				}
			).put(
				"Content-Type",
				() ->
					Validator.isNotNull(body) ? ContentTypes.APPLICATION_JSON :
						null
			).put(
				"Liferay-AI-Hub-Cell-On-Behalf-Of",
				() -> {
					if (liferayAIHubCellOnBehalfOf == null) {
						return null;
					}

					return String.valueOf(liferayAIHubCellOnBehalfOf);
				}
			).build());

		options.setLocation(location);
		options.setMethod(Http.Method.valueOf(StringUtil.toUpperCase(method)));

		try {
			String content = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			int responseCode = response.getResponseCode();

			if (responseCode < 300) {
				return McpSchema.CallToolResult.builder(
				).addTextContent(
					content
				).isError(
					false
				).build();
			}

			return McpSchema.CallToolResult.builder(
			).addTextContent(
				StringBundler.concat(
					"Status code: ", responseCode, ", Content:\n", content)
			).isError(
				true
			).build();
		}
		catch (IOException ioException) {
			_log.error(ioException);

			return McpSchema.CallToolResult.builder(
			).addTextContent(
				ioException.getMessage()
			).isError(
				true
			).build();
		}
	}

	private MCPServerProfile _getMCPServerProfile(
		long companyId, String profileName) {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					MCPServerConstants.
						EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE,
					companyId);

		if (objectDefinition == null) {
			return null;
		}

		for (ObjectEntry objectEntry :
				_objectEntryLocalService.getObjectEntries(
					0, objectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Map<String, Serializable> values = objectEntry.getValues();

			if (profileName.equals(values.get("name"))) {
				return new MCPServerProfile(
					profileName,
					StringUtil.splitLines((String)values.get("endpoints")));
			}
		}

		return null;
	}

	private String _getOpenAPIJSONString(
		String baseURL, String authorization, String openAPIURL) {

		Http.Options options = new Http.Options();

		if (authorization != null) {
			options.setHeaders(
				HashMapBuilder.put(
					"Authorization", authorization
				).build());
		}

		options.setLocation(baseURL + openAPIURL);

		try {
			return _http.URLtoString(options);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private String _getProfileName(HttpServletRequest httpServletRequest) {
		String pathInfo = httpServletRequest.getPathInfo();

		if (Validator.isNull(pathInfo) || pathInfo.equals("/")) {
			return null;
		}

		String profileName = pathInfo.substring(1);

		int index = profileName.indexOf('/');

		if (index > 0) {
			profileName = profileName.substring(0, index);
		}

		if (profileName.isEmpty()) {
			return null;
		}

		return profileName;
	}

	private Servlet _getServlet(
		String authorization, String baseURL, long companyId,
		MCPServerProfile mcpServerProfile) {

		synchronized (this) {
			return _servlets.computeIfAbsent(
				_getServletKey(
					companyId,
					(mcpServerProfile != null) ? mcpServerProfile._name : null),
				servletKey -> _buildServlet(
					baseURL, companyId, authorization, mcpServerProfile));
		}
	}

	private String _getServletKey(long companyId, String profileName) {
		String servletKey = String.valueOf(companyId);

		if (profileName != null) {
			servletKey = servletKey + StringPool.UNDERLINE + profileName;
		}

		return servletKey;
	}

	private List<McpStatelessServerFeatures.SyncPromptSpecification>
		_getSyncPromptSpecifications(long companyId) {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					MCPServerConstants.
						EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROMPT,
					companyId);

		if (objectDefinition == null) {
			return Collections.emptyList();
		}

		return TransformUtil.transform(
			_objectEntryLocalService.getObjectEntries(
				0, objectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			objectEntry -> {
				Map<String, Serializable> values = objectEntry.getValues();

				return new McpStatelessServerFeatures.SyncPromptSpecification(
					new McpSchema.Prompt(
						(String)values.get("name"),
						(String)values.get("description"),
						Collections.emptyList()),
					(mcpTransportContext, request) ->
						new McpSchema.GetPromptResult(
							(String)values.get("description"),
							List.of(
								new McpSchema.PromptMessage(
									McpSchema.Role.USER,
									new McpSchema.TextContent(
										(String)values.get("prompt"))))));
			});
	}

	private McpSchema.Tool _getTool(String name, JSONObject toolsJSONObject) {
		JSONObject toolJSONObject = toolsJSONObject.getJSONObject(name);

		return McpSchema.Tool.builder(
		).name(
			name
		).description(
			toolJSONObject.getString("description")
		).inputSchema(
			new JacksonMcpJsonMapper(new ObjectMapper()),
			toolJSONObject.getJSONObject(
				"schema"
			).toString()
		).build();
	}

	private JSONObject _getToolsJSONObject(String baseURL) {
		try {
			return _jsonFactory.createJSONObject(
				StringUtil.replace(
					StringUtil.read(
						MCPServerServlet.class, "dependencies/tools.json"),
					"[$BASE_URL$]", baseURL));
		}
		catch (JSONException jsonException) {
			throw new RuntimeException(jsonException);
		}
	}

	private boolean _isEnabled(long companyId) {
		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-63311")) {
			return false;
		}

		try {
			MCPServerConfiguration mcpServerConfiguration =
				_configurationProvider.getCompanyConfiguration(
					MCPServerConfiguration.class, companyId);

			return mcpServerConfiguration.enabled();
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException);
		}
	}

	private void _sendInsufficientScopeChallenge(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String description)
		throws IOException {

		httpServletResponse.setHeader(
			HttpHeaders.WWW_AUTHENTICATE,
			StringBundler.concat(
				"Bearer realm=\"mcp\", resource_metadata=\"",
				_portal.getPortalURL(httpServletRequest),
				"/.well-known/oauth-protected-resource\", ",
				"error=\"insufficient_scope\", error_description=\"",
				description, "\""));
		httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
	}

	private void _sendInvalidTokenChallenge(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String description)
		throws IOException {

		httpServletResponse.setHeader(
			HttpHeaders.WWW_AUTHENTICATE,
			StringBundler.concat(
				"Bearer realm=\"mcp\", resource_metadata=\"",
				_portal.getPortalURL(httpServletRequest),
				"/.well-known/oauth-protected-resource\", ",
				"error=\"invalid_token\", error_description=\"", description,
				"\""));
		httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

	private static final String _MCP_PATH = "/mcp";

	private static final Log _log = LogFactoryUtil.getLog(
		MCPServerServlet.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private OAuth2AuthorizationLocalService _oAuth2AuthorizationLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private Portal _portal;

	private final Map<String, Servlet> _servlets = new ConcurrentHashMap<>();

	private static class MCPServerProfile {

		public MCPServerProfile(String name, String[] endpoints) {
			_name = name;
			_endpoints = endpoints;
		}

		private final String[] _endpoints;
		private final String _name;

	}

}