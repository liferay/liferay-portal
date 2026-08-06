/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.mcp.server.rest.dto.v1_0.Tool;
import com.liferay.mcp.server.rest.internal.constants.MCPServerConstants;
import com.liferay.mcp.server.rest.internal.util.ToolSetUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import io.modelcontextprotocol.common.McpTransportContext;
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

import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
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
		"osgi.http.whiteboard.servlet.name=com.liferay.mcp.server.rest.internal.servlet.MCPServerServlet",
		"osgi.http.whiteboard.servlet.pattern=/mcp",
		"osgi.http.whiteboard.servlet.pattern=/mcp/*"
	},
	service = Servlet.class
)
public class MCPServerServlet extends HttpServlet {

	@Override
	public void destroy() {
		synchronized (this) {
			for (Servlet servlet : _servlets.values()) {
				servlet.destroy();
			}

			_servlets.clear();
		}
	}

	public void invalidate(long companyId, String mcpServerProfileName) {
		synchronized (this) {
			_destroy(_getServletKey(companyId, mcpServerProfileName));
		}
	}

	public void invalidateAll(long companyId) {
		synchronized (this) {
			String companyIdString = String.valueOf(companyId);

			for (String servletKey : new ArrayList<>(_servlets.keySet())) {
				if (servletKey.equals(companyIdString) ||
					servletKey.startsWith(
						companyIdString + StringPool.UNDERLINE)) {

					_destroy(servletKey);
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

		ObjectEntry mcpServerProfileObjectEntry =
			_getMCPServerProfileObjectEntry(companyId, httpServletRequest);

		if (mcpServerProfileObjectEntry == null) {
			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		Servlet servlet = _getServlet(
			httpServletRequest, companyId, mcpServerProfileObjectEntry);

		servlet.service(httpServletRequest, httpServletResponse);
	}

	private Servlet _buildServlet(
		HttpServletRequest httpServletRequest, long companyId,
		ObjectEntry mcpServerProfileObjectEntry) {

		Map<String, Serializable> values =
			mcpServerProfileObjectEntry.getValues();

		String mcpServerProfileName = (String)values.get("name");

		String mcpServerProfileExternalReferenceCode =
			mcpServerProfileObjectEntry.getExternalReferenceCode();

		HttpServletStatelessServerTransport
			httpServletStatelessServerTransport =
				HttpServletStatelessServerTransport.builder(
				).contextExtractor(
					request -> McpTransportContext.create(
						HashMapBuilder.<String, Object>put(
							"httpServletRequest", request
						).build())
				).messageEndpoint(
					(!Objects.equals(mcpServerProfileName, "default")) ?
						"/mcp/" + mcpServerProfileName : "/mcp"
				).build();

		List<McpStatelessServerFeatures.SyncToolSpecification>
			syncToolSpecifications = TransformUtil.transform(
				_getMCPServerProfileToolObjectEntries(
					mcpServerProfileObjectEntry),
				mcpServerProfileToolObjectEntry -> {
					Map<String, Serializable> mcpServerProfileToolValues =
						mcpServerProfileToolObjectEntry.getValues();

					String toolName = MapUtil.getString(
						mcpServerProfileToolValues, "toolName");
					String toolSetName = MapUtil.getString(
						mcpServerProfileToolValues, "toolSetName");

					try {
						return new McpStatelessServerFeatures.
							SyncToolSpecification(
								_getTool(
									httpServletRequest, toolName, toolSetName),
								(mcpTransportContext, callToolRequest) -> _call(
									mcpTransportContext,
									callToolRequest.arguments(), companyId,
									mcpServerProfileExternalReferenceCode,
									toolName, toolSetName));
					}
					catch (Exception exception) {
						_log.error(
							StringBundler.concat(
								"Skipping MCP tool \"", toolName,
								"\" from tool set \"", toolSetName, "\""),
							exception);

						return null;
					}
				});

		McpStatelessSyncServer mcpStatelessSyncServer = McpServer.sync(
			httpServletStatelessServerTransport
		).capabilities(
			McpSchema.ServerCapabilities.builder(
			).prompts(
				true
			).tools(
				true
			).build()
		).immediateExecution(
			true
		).prompts(
			_getSyncPromptSpecifications(companyId)
		).tools(
			syncToolSpecifications
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
		McpTransportContext mcpTransportContext, Object inputObject,
		long companyId, String mcpServerProfileExternalReferenceCode,
		String toolName, String toolSetName) {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)mcpTransportContext.get("httpServletRequest");

		try {
			Response response = ToolSetUtil.invokeTool(
				_getDataMaskExternalReferenceCodes(
					companyId, mcpServerProfileExternalReferenceCode),
				httpServletRequest, inputObject, toolName, toolSetName);

			int responseCode = response.getStatus();
			String content = (String)response.getEntity();

			if (responseCode < 300) {
				if (content == null) {
					content = "Status code: " + responseCode;
				}

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
		catch (Exception exception) {
			_log.error(exception);

			return McpSchema.CallToolResult.builder(
			).addTextContent(
				exception.getMessage()
			).isError(
				true
			).build();
		}
	}

	private void _destroy(String servletKey) {
		Servlet servlet = _servlets.remove(servletKey);

		if (servlet != null) {
			servlet.destroy();
		}
	}

	private List<String> _getDataMaskExternalReferenceCodes(
			long companyId, String mcpServerProfileExternalReferenceCode)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					MCPServerConstants.
						EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE_DATA_MASK,
					companyId);

		if (objectDefinition == null) {
			return null;
		}

		return TransformUtil.transform(
			_objectEntryLocalService.getValuesList(
				0, companyId, objectDefinition.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				_filterFactory.create(
					"mcpServerProfileExternalReferenceCode eq '" +
						mcpServerProfileExternalReferenceCode + "'",
					objectDefinition),
				null, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				new Sort[] {new Sort("executionOrder", Sort.INT_TYPE, false)}),
			values -> MapUtil.getString(
				values, "dataMaskExternalReferenceCode"));
	}

	private String _getMCPServerProfileName(
		HttpServletRequest httpServletRequest) {

		String pathInfo = httpServletRequest.getPathInfo();

		if (Validator.isNull(pathInfo) || pathInfo.equals("/")) {
			return null;
		}

		String mcpServerProfileName = pathInfo.substring(1);

		int index = mcpServerProfileName.indexOf('/');

		if (index > 0) {
			mcpServerProfileName = mcpServerProfileName.substring(0, index);
		}

		if (mcpServerProfileName.isEmpty()) {
			return null;
		}

		return mcpServerProfileName;
	}

	private ObjectEntry _getMCPServerProfileObjectEntry(
		long companyId, HttpServletRequest httpServletRequest) {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					MCPServerConstants.
						EXTERNAL_REFERENCE_CODE_MCP_SERVER_PROFILE,
					companyId);

		if (objectDefinition == null) {
			return null;
		}

		String mcpServerProfileName = GetterUtil.getString(
			_getMCPServerProfileName(httpServletRequest), "default");

		for (ObjectEntry objectEntry :
				_objectEntryLocalService.getObjectEntries(
					0, objectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			Map<String, Serializable> values = objectEntry.getValues();

			if (mcpServerProfileName.equals(values.get("name")) &&
				Objects.equals(values.get("profileStatus"), "active")) {

				return objectEntry;
			}
		}

		return null;
	}

	private List<ObjectEntry> _getMCPServerProfileToolObjectEntries(
		ObjectEntry mcpServerProfileObjectEntry) {

		try {
			ObjectRelationship objectRelationship =
				_objectRelationshipLocalService.getObjectRelationship(
					mcpServerProfileObjectEntry.getObjectDefinitionId(),
					"mcpServerProfileToTools");

			return _objectEntryLocalService.getOneToManyObjectEntries(
				0, objectRelationship.getObjectRelationshipId(), null, false,
				mcpServerProfileObjectEntry.getObjectEntryId(), true, null,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	private Servlet _getServlet(
		HttpServletRequest httpServletRequest, long companyId,
		ObjectEntry mcpServerProfileObjectEntry) {

		String mcpServerProfileName = MapUtil.getString(
			mcpServerProfileObjectEntry.getValues(), "name");

		synchronized (this) {
			return _servlets.computeIfAbsent(
				_getServletKey(companyId, mcpServerProfileName),
				servletKey -> _buildServlet(
					httpServletRequest, companyId,
					mcpServerProfileObjectEntry));
		}
	}

	private String _getServletKey(long companyId, String mcpServerProfileName) {
		return companyId + StringPool.UNDERLINE + mcpServerProfileName;
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

				if (!Objects.equals(values.get("promptStatus"), "active")) {
					return null;
				}

				return new McpStatelessServerFeatures.SyncPromptSpecification(
					new McpSchema.Prompt(
						(String)values.get("identifier"),
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

	private McpSchema.Tool _getTool(
		HttpServletRequest httpServletRequest, String toolName,
		String toolSetName) {

		try {
			Tool tool = ToolSetUtil.getTool(
				httpServletRequest, toolName, toolSetName);

			return McpSchema.Tool.builder(
			).description(
				tool.getDescription()
			).inputSchema(
				_objectMapper.convertValue(
					tool.getInputSchema(), McpSchema.JsonSchema.class)
			).name(
				toolName
			).build();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MCPServerServlet.class);

	private static final ObjectMapper _objectMapper = new ObjectMapper();

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private Portal _portal;

	private final Map<String, Servlet> _servlets = new ConcurrentHashMap<>();

}