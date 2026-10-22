/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.mcp.server.rest.dto.v1_0.Tool;
import com.liferay.mcp.server.rest.dto.v1_0.ToolSearchResult;
import com.liferay.mcp.server.rest.dto.v1_0.ToolSet;
import com.liferay.mcp.server.rest.dto.v1_0.ToolSummary;
import com.liferay.mcp.server.rest.internal.configuration.MCPServerConfiguration;
import com.liferay.mcp.server.rest.internal.search.exception.ToolSearchUnavailableException;
import com.liferay.mcp.server.rest.internal.search.index.MCPToolIndexReader;
import com.liferay.mcp.server.rest.internal.search.index.MCPToolIndexWriter;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.vulcan.application.HeadlessApplicationProvider;
import com.liferay.portal.vulcan.http.VulcanRequestForwarder;
import com.liferay.portal.vulcan.jackson.databind.ObjectMapperProviderUtil;
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.Response;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alejandro Tardín
 */
public class ToolSetUtil {

	public static final String TOOL_SET_NAME = "mcp-server-v1.0";

	public static void clearOpenAPIJSONObjectCache(long companyId) {
		if (companyId == CompanyConstants.SYSTEM) {
			_openAPIJSONObjects.clear();

			return;
		}

		Set<String> keys = _openAPIJSONObjects.keySet();

		keys.removeIf(key -> key.startsWith(companyId + StringPool.POUND));
	}

	public static Map<String, HeadlessApplicationProvider.OpenAPIDocument>
		getLatestOpenAPIDocuments(long companyId) {

		Map<String, HeadlessApplicationProvider.OpenAPIDocument>
			openAPIDocuments = _getOpenAPIDocuments(companyId);

		Collection<HeadlessApplicationProvider.OpenAPIDocument> values =
			openAPIDocuments.values();

		values.removeIf(ToolSetUtil::_isSuperseded);

		return openAPIDocuments;
	}

	public static JSONObject getOpenAPIJSONObject(
		HttpServletRequest httpServletRequest,
		HeadlessApplicationProvider.OpenAPIDocument openAPIDocument,
		String toolSetName) {

		return _openAPIJSONObjects.computeIfAbsent(
			StringBundler.concat(
				PortalUtil.getCompanyId(httpServletRequest), StringPool.POUND,
				openAPIDocument.getPath(
					HeadlessApplicationProvider.OpenAPIDocument.Type.JSON)),
			key -> {
				String content = openAPIDocument.getContentString(
					PortalUtil.getPortalURL(httpServletRequest) +
						PortalUtil.getPathContext() + Portal.PATH_MODULE,
					HeadlessApplicationProvider.OpenAPIDocument.Type.JSON);

				if (Validator.isNull(content)) {
					throw new IllegalStateException(
						"Unable to read the OpenAPI document of the \"" +
							toolSetName + "\" tool set");
				}

				try {
					return JSONFactoryUtil.createJSONObject(content);
				}
				catch (JSONException jsonException) {
					throw new IllegalStateException(
						StringBundler.concat(
							"Unable to parse the OpenAPI document of the \"",
							toolSetName, "\" tool set"),
						jsonException);
				}
			});
	}

	public static Tool getTool(
		HttpServletRequest httpServletRequest, boolean requiredInputSchemaOnly,
		Map<String, String> restrictFieldsMap, String toolName,
		String toolSetName) {

		return OpenAPIUtil.getTool(
			!Objects.equals(toolSetName, TOOL_SET_NAME),
			getOpenAPIJSONObject(
				httpServletRequest,
				_getOpenAPIDocument(
					PortalUtil.getCompanyId(httpServletRequest), toolSetName),
				toolSetName),
			requiredInputSchemaOnly,
			_getRestrictFields(restrictFieldsMap, toolName, toolSetName),
			toolName);
	}

	public static String getToolKey(String toolName, String toolSetName) {
		return toolSetName + StringPool.POUND + toolName;
	}

	public static Map<String, ?> getToolOutputSchema(
		HttpServletRequest httpServletRequest, String toolName,
		String toolSetName) {

		return OpenAPIUtil.getOutputSchema(
			getOpenAPIJSONObject(
				httpServletRequest,
				_getOpenAPIDocument(
					PortalUtil.getCompanyId(httpServletRequest), toolSetName),
				toolSetName),
			toolName);
	}

	public static Page<ToolSearchResult> getToolSearchResultsPage(
		Set<String> allowedToolKeys, HttpServletRequest httpServletRequest,
		boolean includeRequiredInputSchema,
		Map<String, String> restrictFieldsMap, String search) {

		long companyId = PortalUtil.getCompanyId(httpServletRequest);

		FeatureFlagManagerUtil.checkEnabled(companyId, "LPD-63311");

		SearchCapabilities searchCapabilities =
			_searchCapabilitiesSnapshot.get();

		if ((searchCapabilities == null) ||
			!searchCapabilities.isMCPToolSearchSupported()) {

			throw new ToolSearchUnavailableException(
				StringBundler.concat(
					"Tool search is unavailable on this instance because it ",
					"needs Elasticsearch or OpenSearch. Browse instead: ",
					"\"getToolSetsPage\" lists every tool set and ",
					"\"getToolSetToolSetNameToolSummariesPage\" lists the ",
					"operations of one."));
		}

		_validateSearch(search);

		try {
			MCPToolIndexWriter mcpToolIndexWriter =
				_mcpToolIndexWriterSnapshot.get();

			mcpToolIndexWriter.updateIfStale(companyId, httpServletRequest);

			return Page.of(
				_getToolSearchResults(
					allowedToolKeys, companyId, httpServletRequest,
					includeRequiredInputSchema, restrictFieldsMap, search));
		}
		catch (RuntimeException runtimeException) {
			_log.error("Unable to search the tool index", runtimeException);

			throw new ToolSearchUnavailableException(
				StringBundler.concat(
					"Tool search is unavailable on this instance. Browse ",
					"instead: \"getToolSetsPage\" lists every tool set and ",
					"\"getToolSetToolSetNameToolSummariesPage\" lists the ",
					"operations of one."));
		}
	}

	public static String getToolSetName(
		long companyId, String restContextPath) {

		if (Validator.isNull(restContextPath)) {
			return null;
		}

		Map<String, HeadlessApplicationProvider.OpenAPIDocument>
			openAPIDocuments = getLatestOpenAPIDocuments(companyId);

		for (Map.Entry<String, HeadlessApplicationProvider.OpenAPIDocument>
				entry : openAPIDocuments.entrySet()) {

			HeadlessApplicationProvider.OpenAPIDocument value =
				entry.getValue();

			HeadlessApplicationProvider.Application application =
				value.getApplication();

			String basePath = application.getBasePath();

			if (restContextPath.equals(basePath) ||
				restContextPath.startsWith(basePath + StringPool.SLASH)) {

				return entry.getKey();
			}
		}

		return null;
	}

	public static Page<ToolSet> getToolSetsPage(long companyId) {
		Map<String, HeadlessApplicationProvider.OpenAPIDocument>
			openAPIDocuments = _getOpenAPIDocuments(companyId);

		return Page.of(
			TransformUtil.transform(
				openAPIDocuments.entrySet(),
				entry -> new ToolSet() {
					{
						setDescription(
							() -> {
								HeadlessApplicationProvider.OpenAPIDocument
									openAPIDocument = entry.getValue();

								return openAPIDocument.getDescription();
							});

						setName(entry::getKey);
					}
				}));
	}

	public static Page<ToolSummary> getToolSummariesPage(
		HttpServletRequest httpServletRequest, String toolSetName) {

		return Page.of(
			OpenAPIUtil.getToolSummaries(
				getOpenAPIJSONObject(
					httpServletRequest,
					_getOpenAPIDocument(
						PortalUtil.getCompanyId(httpServletRequest),
						toolSetName),
					toolSetName)));
	}

	public static Response invokeTool(
			Set<String> allowedToolKeys,
			List<String> dataMaskExternalReferenceCodes,
			HttpServletRequest httpServletRequest, Object inputObject,
			Map<String, String> restrictFieldsMap, String toolName,
			String toolSetName)
		throws Exception {

		JSONObject inputJSONObject = null;

		if (inputObject instanceof JSONObject) {
			inputJSONObject = (JSONObject)inputObject;
		}
		else if (inputObject instanceof Map) {
			inputJSONObject = JSONFactoryUtil.createJSONObject(
				(Map<String, ?>)inputObject);
		}
		else {
			inputJSONObject = JSONFactoryUtil.createJSONObject();
		}

		if (Objects.equals(toolSetName, TOOL_SET_NAME)) {
			if (Objects.equals(
					toolName, "getToolSearchToolSearchResultsPage")) {

				return _getResponse(
					getToolSearchResultsPage(
						allowedToolKeys, httpServletRequest,
						inputJSONObject.getBoolean(
							"includeRequiredInputSchema"),
						restrictFieldsMap,
						inputJSONObject.getString("search")));
			}

			if (Objects.equals(toolName, "getToolSetToolSetNameTool")) {
				return _getResponse(
					getTool(
						httpServletRequest,
						inputJSONObject.getBoolean("requiredInputSchemaOnly"),
						restrictFieldsMap,
						inputJSONObject.getString("toolName"),
						inputJSONObject.getString("toolSetName")));
			}

			if (Objects.equals(
					toolName, "getToolSetToolSetNameToolSummariesPage")) {

				return _getResponse(
					getToolSummariesPage(
						httpServletRequest,
						inputJSONObject.getString("toolSetName")));
			}

			if (Objects.equals(toolName, "getToolSetsPage")) {
				return _getResponse(
					getToolSetsPage(
						PortalUtil.getCompanyId(httpServletRequest)));
			}

			if (Objects.equals(toolName, "postToolSetToolSetNameToolInvoke")) {
				return invokeTool(
					allowedToolKeys, dataMaskExternalReferenceCodes,
					httpServletRequest, inputJSONObject.opt("body"),
					restrictFieldsMap, inputJSONObject.getString("toolName"),
					inputJSONObject.getString("toolSetName"));
			}
		}

		VulcanRequestForwarder vulcanRequestForwarder =
			_vulcanRequestForwarderSnapshot.get();

		HeadlessApplicationProvider.OpenAPIDocument openAPIDocument =
			_getOpenAPIDocument(
				PortalUtil.getCompanyId(httpServletRequest), toolSetName);

		HeadlessApplicationProvider.Application application =
			openAPIDocument.getApplication();

		VulcanRequestForwarder.Response response =
			vulcanRequestForwarder.forward(
				httpServletRequest,
				OpenAPIUtil.getRequest(
					application.getBasePath(),
					HashMapBuilder.put(
						"X-Liferay-Data-Masks",
						() -> StringUtil.merge(
							dataMaskExternalReferenceCodes, StringPool.COMMA)
					).build(),
					inputJSONObject,
					getOpenAPIJSONObject(
						httpServletRequest, openAPIDocument, toolSetName),
					_getRestrictFields(
						restrictFieldsMap, toolName, toolSetName),
					toolName,
					UserLocalServiceUtil.fetchUser(
						GetterUtil.getLong(
							httpServletRequest.getAttribute(
								WebKeys.USER_ID)))));

		String content = response.getContent();

		return Response.status(
			response.getStatusCode()
		).entity(
			Validator.isNull(content) ? null : _getContent(content)
		).type(
			ContentTypes.TEXT_PLAIN_UTF8
		).build();
	}

	private static String _getContent(String content) {
		if (Validator.isNull(content) || (content.charAt(0) != '{') ||
			!content.contains("\"actions\"")) {

			return content;
		}

		try {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(content);

			if (!jsonObject.has("actions")) {
				return content;
			}

			jsonObject.remove("actions");

			return jsonObject.toString();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return content;
		}
	}

	private static HeadlessApplicationProvider.OpenAPIDocument
		_getOpenAPIDocument(long companyId, String toolSetName) {

		Map<String, HeadlessApplicationProvider.OpenAPIDocument>
			openAPIDocuments = _getOpenAPIDocuments(companyId);

		HeadlessApplicationProvider.OpenAPIDocument openAPIDocument =
			openAPIDocuments.get(toolSetName);

		if (openAPIDocument == null) {
			throw new IllegalArgumentException(
				"No tool set was found with name \"" + toolSetName + "\"");
		}

		return openAPIDocument;
	}

	private static Map<String, HeadlessApplicationProvider.OpenAPIDocument>
		_getOpenAPIDocuments(long companyId) {

		Map<String, HeadlessApplicationProvider.OpenAPIDocument>
			openAPIDocuments = new TreeMap<>();

		HeadlessApplicationProvider headlessApplicationProvider =
			_headlessApplicationProviderSnapshot.get();

		for (HeadlessApplicationProvider.Application application :
				headlessApplicationProvider.getApplications(companyId)) {

			if (Validator.isNull(application.getBasePath()) ||
				Objects.equals(application.getBasePath(), "/openapi")) {

				continue;
			}

			for (HeadlessApplicationProvider.OpenAPIDocument openAPIDocument :
					application.getOpenAPIDocuments()) {

				String apiPath = application.getBasePath();

				String version = openAPIDocument.getVersion();

				if (version != null) {
					apiPath += StringPool.SLASH + version;
				}

				openAPIDocuments.putIfAbsent(
					StringUtil.replace(
						apiPath.substring(1), CharPool.SLASH, CharPool.DASH),
					openAPIDocument);
			}
		}

		return openAPIDocuments;
	}

	private static Map<String, ?> _getRequiredInputSchema(
		HttpServletRequest httpServletRequest,
		Map<String, String> restrictFieldsMap, String toolName,
		String toolSetName) {

		try {
			Tool tool = getTool(
				httpServletRequest, true, restrictFieldsMap, toolName,
				toolSetName);

			return tool.getInputSchema();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to get the required input schema of tool ",
						toolName, " of tool set ", toolSetName),
					exception);
			}

			return null;
		}
	}

	private static Response _getResponse(Object value) throws Exception {
		ObjectMapper objectMapper = ObjectMapperProviderUtil.getObjectMapper();

		return Response.ok(
			objectMapper.writeValueAsString(value), ContentTypes.TEXT_PLAIN_UTF8
		).build();
	}

	private static String _getRestrictFields(
		Map<String, String> restrictFieldsMap, String toolName,
		String toolSetName) {

		if (restrictFieldsMap == null) {
			return null;
		}

		return restrictFieldsMap.get(getToolKey(toolName, toolSetName));
	}

	private static int _getSearchToolMaxResultsCount(long companyId) {
		try {
			ConfigurationProvider configurationProvider =
				_configurationProviderSnapshot.get();

			MCPServerConfiguration mcpServerConfiguration =
				configurationProvider.getCompanyConfiguration(
					MCPServerConfiguration.class, companyId);

			int searchToolMaxResultsCount =
				mcpServerConfiguration.searchToolMaxResultsCount();

			if (searchToolMaxResultsCount > 0) {
				return Math.min(searchToolMaxResultsCount, 100);
			}
		}
		catch (Exception exception) {
			_log.error("Unable to get the MCP server configuration", exception);
		}

		return 10;
	}

	private static List<ToolSearchResult> _getToolSearchResults(
		Set<String> allowedToolKeys, long companyId,
		HttpServletRequest httpServletRequest,
		boolean includeRequiredInputSchema,
		Map<String, String> restrictFieldsMap, String search) {

		MCPToolIndexReader mcpToolIndexReader =
			_mcpToolIndexReaderSnapshot.get();

		List<ToolSearchResult> toolSearchResults = mcpToolIndexReader.search(
			allowedToolKeys, companyId, includeRequiredInputSchema, search,
			_getSearchToolMaxResultsCount(companyId));

		if (includeRequiredInputSchema && !toolSearchResults.isEmpty()) {
			ToolSearchResult toolSearchResult = toolSearchResults.getFirst();

			toolSearchResult.setRequiredInputSchema(
				() -> _getRequiredInputSchema(
					httpServletRequest, restrictFieldsMap,
					toolSearchResult.getToolName(),
					toolSearchResult.getToolSetName()));
		}

		return toolSearchResults;
	}

	private static Version _getVersion(
		HeadlessApplicationProvider.OpenAPIDocument openAPIDocument) {

		return Version.parseVersion(
			StringUtil.removeFirst(openAPIDocument.getVersion(), "v"));
	}

	private static boolean _isSuperseded(
		HeadlessApplicationProvider.OpenAPIDocument openAPIDocument) {

		HeadlessApplicationProvider.Application application =
			openAPIDocument.getApplication();

		Version version = _getVersion(openAPIDocument);

		for (HeadlessApplicationProvider.OpenAPIDocument
				siblingOpenAPIDocument : application.getOpenAPIDocuments()) {

			if (version.compareTo(_getVersion(siblingOpenAPIDocument)) < 0) {
				return true;
			}
		}

		return false;
	}

	private static void _validateSearch(String search) {
		if (Validator.isBlank(search)) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"The search is empty. Say what the user wants to do, such ",
					"as \"create a blog entry\", and search for one action at ",
					"a time."));
		}

		if (search.length() > _SEARCH_MAX_LENGTH) {
			throw new IllegalArgumentException(
				StringBundler.concat(
					"The search is ", search.length(),
					" characters long and the limit is ", _SEARCH_MAX_LENGTH,
					". Search for one action at a time, phrased the way a ",
					"person would ask for it, such as \"create a blog ",
					"entry\". A request made of several steps needs one ",
					"search for each."));
		}
	}

	private static final int _SEARCH_MAX_LENGTH = 500;

	private static final Log _log = LogFactoryUtil.getLog(ToolSetUtil.class);

	private static final Snapshot<ConfigurationProvider>
		_configurationProviderSnapshot = new Snapshot<>(
			ToolSetUtil.class, ConfigurationProvider.class);
	private static final Snapshot<HeadlessApplicationProvider>
		_headlessApplicationProviderSnapshot = new Snapshot<>(
			ToolSetUtil.class, HeadlessApplicationProvider.class);
	private static final Snapshot<MCPToolIndexReader>
		_mcpToolIndexReaderSnapshot = new Snapshot<>(
			ToolSetUtil.class, MCPToolIndexReader.class);
	private static final Snapshot<MCPToolIndexWriter>
		_mcpToolIndexWriterSnapshot = new Snapshot<>(
			ToolSetUtil.class, MCPToolIndexWriter.class);
	private static final Map<String, JSONObject> _openAPIJSONObjects =
		new ConcurrentHashMap<>();
	private static final Snapshot<SearchCapabilities>
		_searchCapabilitiesSnapshot = new Snapshot<>(
			ToolSetUtil.class, SearchCapabilities.class);
	private static final Snapshot<VulcanRequestForwarder>
		_vulcanRequestForwarderSnapshot = new Snapshot<>(
			ToolSetUtil.class, VulcanRequestForwarder.class);

}