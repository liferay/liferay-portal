/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.search.index;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.CreateIndexRequest;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;
import com.liferay.portal.search.index.IndexNameBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(service = MCPToolIndexCreator.class)
public class MCPToolIndexCreator {

	public void createIfNotExists(long companyId) {
		if (indexExists(companyId)) {
			return;
		}

		CreateIndexRequest createIndexRequest = new CreateIndexRequest(
			getIndexName(companyId));

		createIndexRequest.setMappings(_read(_FILE_NAME_MAPPINGS));
		createIndexRequest.setSettings(_getSettingsJSONString());

		try {
			_searchEngineAdapter.execute(createIndexRequest);
		}
		catch (RuntimeException runtimeException) {
			if (!indexExists(companyId)) {
				throw runtimeException;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(runtimeException);
			}
		}
	}

	public void deleteIfExists(long companyId) {
		if (!indexExists(companyId)) {
			return;
		}

		try {
			_searchEngineAdapter.execute(
				new DeleteIndexRequest(getIndexName(companyId)));
		}
		catch (RuntimeException runtimeException) {
			if (indexExists(companyId)) {
				throw runtimeException;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(runtimeException);
			}
		}
	}

	public String getIndexName(long companyId) {
		return _indexNameBuilder.getIndexName(companyId) + "-mcp-tools";
	}

	public boolean indexExists(long companyId) {
		IndicesExistsIndexRequest indicesExistsIndexRequest =
			new IndicesExistsIndexRequest(getIndexName(companyId));

		IndicesExistsIndexResponse indicesExistsIndexResponse =
			_searchEngineAdapter.execute(indicesExistsIndexRequest);

		return indicesExistsIndexResponse.isExists();
	}

	private String _getSettingsJSONString() {
		try {
			JSONObject settingsJSONObject = _jsonFactory.createJSONObject(
				_read(_FILE_NAME_SETTINGS));

			JSONObject synonymFilterJSONObject = JSONUtil.getValueAsJSONObject(
				settingsJSONObject, "JSONObject/analysis", "JSONObject/filter",
				"JSONObject/mcp_tool_synonym");

			synonymFilterJSONObject.put("synonyms", _getSynonymsJSONArray());

			return settingsJSONObject.toString();
		}
		catch (JSONException jsonException) {
			throw new IllegalStateException(
				"Unable to parse " + _FILE_NAME_SETTINGS, jsonException);
		}
	}

	private JSONArray _getSynonymsJSONArray() {
		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (String line : StringUtil.splitLines(_read(_FILE_NAME_SYNONYMS))) {
			line = line.trim();

			if (Validator.isNull(line) || line.startsWith(StringPool.POUND)) {
				continue;
			}

			jsonArray.put(line);
		}

		return jsonArray;
	}

	private String _read(String fileName) {
		return StringUtil.read(
			MCPToolIndexCreator.class, "/META-INF/search/" + fileName);
	}

	private static final String _FILE_NAME_MAPPINGS =
		"liferay-mcp-tools-mappings.json";

	private static final String _FILE_NAME_SETTINGS =
		"liferay-mcp-tools-settings.json";

	private static final String _FILE_NAME_SYNONYMS =
		"liferay-mcp-tools-synonyms.txt";

	private static final Log _log = LogFactoryUtil.getLog(
		MCPToolIndexCreator.class);

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

}