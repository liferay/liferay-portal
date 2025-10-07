/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.internal.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.mcp.server.internal.constants.MCPServerConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import io.modelcontextprotocol.common.McpTransportContext;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.server.transport.HttpServletSseServerTransportProvider;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
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
		"osgi.http.whiteboard.servlet.pattern=/mcp/*"
	},
	service = Servlet.class
)
public class MCPServerServlet extends HttpServlet {

	@Override
	public void destroy() {
		for (Map.Entry<Long, Servlet> entry : _servlets.entrySet()) {
			Servlet servlet = entry.getValue();

			servlet.destroy();
		}

		_servlets.clear();
	}

	@Override
	public void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		long companyId = _portal.getCompanyId(httpServletRequest);

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-63311")) {
			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		Servlet servlet = _getServlet(
			_portal.getPortalURL(httpServletRequest) + _portal.getPathModule(),
			companyId);

		servlet.service(httpServletRequest, httpServletResponse);
	}

	@Deactivate
	protected void deactivate() {
		destroy();
	}

	private Servlet _buildServlet(String baseURL, long companyId) {
		HttpServletSseServerTransportProvider
			httpServletSseServerTransportProvider =
				HttpServletSseServerTransportProvider.builder(
				).baseUrl(
					baseURL + "/mcp"
				).contextExtractor(
					request -> McpTransportContext.create(
						HashMapBuilder.<String, Object>put(
							"authorization", request.getHeader("Authorization")
						).build())
				).messageEndpoint(
					"/message"
				).build();

		JSONObject toolsJSONObject = _getToolsJSONObject(baseURL);

		McpSyncServer mcpSyncServer = McpServer.sync(
			httpServletSseServerTransportProvider
		).capabilities(
			McpSchema.ServerCapabilities.builder(
			).tools(
				true
			).prompts(
				true
			).build()
		).tool(
			_getTool("call-http-endpoint", toolsJSONObject),
			(mcpSyncServerExchange, monos) -> {
				String path = String.valueOf(monos.get("path"));

				if (!path.startsWith("/")) {
					path = "/" + path;
				}

				return _call(
					String.valueOf(monos.get("payload")), baseURL + path,
					mcpSyncServerExchange, String.valueOf(monos.get("method")));
			}
		).tool(
			_getTool("get-openapi", toolsJSONObject),
			(mcpSyncServerExchange, monos) -> _call(
				null, String.valueOf(monos.get("url")), mcpSyncServerExchange,
				"GET")
		).tool(
			_getTool("get-openapis", toolsJSONObject),
			(mcpSyncServerExchange, monos) -> _call(
				null, baseURL + "/openapi", mcpSyncServerExchange, "GET")
		).prompts(
			_getSyncPromptSpecifications(companyId)
		).build();

		return new GenericServlet() {

			@Override
			public void destroy() {
				mcpSyncServer.closeGracefully();
			}

			@Override
			public void service(
					ServletRequest servletRequest,
					ServletResponse servletResponse)
				throws IOException, ServletException {

				httpServletSseServerTransportProvider.service(
					servletRequest, servletResponse);
			}

		};
	}

	private McpSchema.CallToolResult _call(
		String body, String location,
		McpSyncServerExchange mcpSyncServerExchange, String method) {

		Http.Options options = new Http.Options();

		if (Validator.isNotNull(body)) {
			options.setBody(
				body, ContentTypes.APPLICATION_JSON, StringPool.UTF8);
		}

		options.setHeaders(
			HashMapBuilder.put(
				"Authorization",
				() -> {
					McpTransportContext mcpTransportContext =
						mcpSyncServerExchange.transportContext();

					Object authorization = mcpTransportContext.get(
						"authorization");

					if (authorization == null) {
						return null;
					}

					return String.valueOf(authorization);
				}
			).build());

		options.setLocation(location);
		options.setMethod(Http.Method.valueOf(StringUtil.toUpperCase(method)));

		try {
			String content = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			int responseCode = response.getResponseCode();

			if (responseCode < 300) {
				return new McpSchema.CallToolResult(content, false);
			}

			return new McpSchema.CallToolResult(
				StringBundler.concat(
					"Status code: ", responseCode, ", Content:\n", content),
				true);
		}
		catch (IOException ioException) {
			_log.error(ioException);

			return new McpSchema.CallToolResult(ioException.getMessage(), true);
		}
	}

	private Servlet _getServlet(String baseURL, long companyId) {
		Servlet servlet = _servlets.get(companyId);

		if (servlet != null) {
			return servlet;
		}

		synchronized (this) {
			servlet = _servlets.get(companyId);

			if (servlet != null) {
				return servlet;
			}

			servlet = _buildServlet(baseURL, companyId);

			_servlets.put(companyId, servlet);

			return servlet;
		}
	}

	private List<McpServerFeatures.SyncPromptSpecification>
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

				return new McpServerFeatures.SyncPromptSpecification(
					new McpSchema.Prompt(
						(String)values.get("name"),
						(String)values.get("description"),
						Collections.emptyList()),
					(mcpSyncServerExchange, request) ->
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

	private static final Log _log = LogFactoryUtil.getLog(
		MCPServerServlet.class);

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private Portal _portal;

	private final Map<Long, Servlet> _servlets = new ConcurrentHashMap<>();

}