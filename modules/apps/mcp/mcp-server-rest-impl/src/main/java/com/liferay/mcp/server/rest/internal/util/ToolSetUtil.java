/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.mcp.server.rest.dto.v1_0.Tool;
import com.liferay.mcp.server.rest.dto.v1_0.ToolSet;
import com.liferay.mcp.server.rest.dto.v1_0.ToolSummary;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
import com.liferay.portal.vulcan.application.HeadlessApplicationProvider;
import com.liferay.portal.vulcan.http.VulcanRequestForwarder;
import com.liferay.portal.vulcan.jackson.databind.ObjectMapperProviderUtil;
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.Response;

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

	public static void clearOpenAPIJSONObjectCache(long companyId) {
		Set<String> keys = _openAPIJSONObjects.keySet();

		keys.removeIf(key -> key.startsWith(companyId + StringPool.POUND));
	}

	public static Tool getTool(
		HttpServletRequest httpServletRequest, String toolName,
		String toolSetName) {

		return OpenAPIUtil.getTool(
			!Objects.equals(toolSetName, _TOOL_SET_NAME),
			_getOpenAPIJSONObject(
				httpServletRequest, _getOpenAPIDocument(toolSetName),
				toolSetName),
			toolName);
	}

	public static Map<String, ?> getToolOutputSchema(
		HttpServletRequest httpServletRequest, String toolName,
		String toolSetName) {

		return OpenAPIUtil.getOutputSchema(
			_getOpenAPIJSONObject(
				httpServletRequest, _getOpenAPIDocument(toolSetName),
				toolSetName),
			toolName);
	}

	public static Page<ToolSet> getToolSetsPage() {
		Map<String, HeadlessApplicationProvider.OpenAPIDocument>
			openAPIDocuments = _getOpenAPIDocuments();

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
				_getOpenAPIJSONObject(
					httpServletRequest, _getOpenAPIDocument(toolSetName),
					toolSetName)));
	}

	public static Response invokeTool(
			List<String> dataMaskExternalReferenceCodes,
			HttpServletRequest httpServletRequest, Object inputObject,
			String toolName, String toolSetName)
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

		if (Objects.equals(toolSetName, _TOOL_SET_NAME)) {
			if (Objects.equals(toolName, "getToolSetToolSetNameTool")) {
				return _getResponse(
					getTool(
						httpServletRequest,
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
				return _getResponse(getToolSetsPage());
			}

			if (Objects.equals(toolName, "postToolSetToolSetNameToolInvoke")) {
				return invokeTool(
					dataMaskExternalReferenceCodes, httpServletRequest,
					inputJSONObject.opt("body"),
					inputJSONObject.getString("toolName"),
					inputJSONObject.getString("toolSetName"));
			}
		}

		VulcanRequestForwarder vulcanRequestForwarder =
			_vulcanRequestForwarderSnapshot.get();

		HeadlessApplicationProvider.OpenAPIDocument openAPIDocument =
			_getOpenAPIDocument(toolSetName);

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
					_getOpenAPIJSONObject(
						httpServletRequest, openAPIDocument, toolSetName),
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
		_getOpenAPIDocument(String toolSetName) {

		Map<String, HeadlessApplicationProvider.OpenAPIDocument>
			openAPIDocuments = _getOpenAPIDocuments();

		HeadlessApplicationProvider.OpenAPIDocument openAPIDocument =
			openAPIDocuments.get(toolSetName);

		if (openAPIDocument == null) {
			throw new IllegalArgumentException(
				"No tool-set was found with name \"" + toolSetName + "\"");
		}

		return openAPIDocument;
	}

	private static Map<String, HeadlessApplicationProvider.OpenAPIDocument>
		_getOpenAPIDocuments() {

		Map<String, HeadlessApplicationProvider.OpenAPIDocument>
			openAPIDocuments = new TreeMap<>();

		HeadlessApplicationProvider headlessApplicationProvider =
			_headlessApplicationProviderSnapshot.get();

		for (HeadlessApplicationProvider.Application application :
				headlessApplicationProvider.getApplications()) {

			if (Validator.isNull(application.getBasePath())) {
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

	private static JSONObject _getOpenAPIJSONObject(
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
							toolSetName + "\" tool-set");
				}

				try {
					return JSONFactoryUtil.createJSONObject(content);
				}
				catch (Exception exception) {
					throw new RuntimeException(exception);
				}
			});
	}

	private static Response _getResponse(Object value) throws Exception {
		ObjectMapper objectMapper = ObjectMapperProviderUtil.getObjectMapper();

		return Response.ok(
			objectMapper.writeValueAsString(value), ContentTypes.TEXT_PLAIN_UTF8
		).build();
	}

	private static final String _TOOL_SET_NAME = "mcp-server-v1.0";

	private static final Log _log = LogFactoryUtil.getLog(ToolSetUtil.class);

	private static final Snapshot<HeadlessApplicationProvider>
		_headlessApplicationProviderSnapshot = new Snapshot<>(
			ToolSetUtil.class, HeadlessApplicationProvider.class);
	private static final Map<String, JSONObject> _openAPIJSONObjects =
		new ConcurrentHashMap<>();
	private static final Snapshot<VulcanRequestForwarder>
		_vulcanRequestForwarderSnapshot = new Snapshot<>(
			ToolSetUtil.class, VulcanRequestForwarder.class);

}