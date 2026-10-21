/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.search.index;

import com.liferay.mcp.server.rest.internal.util.ToolSetUtil;
import com.liferay.petra.string.StringBundler;

/**
 * @author Petteri Karttunen
 */
public class MCPTool {

	public MCPTool(
		String collection, boolean deprecated, String description,
		String entityName, String[] expansions, String intent, String method,
		String operationVariant, String[] parameterNames, String path,
		String[] requiredPropertyCollections, String[] schemaPropertyNames,
		String toolName, String toolSetName) {

		_collection = collection;
		_deprecated = deprecated;
		_description = description;
		_entityName = entityName;
		_expansions = expansions;
		_intent = intent;
		_method = method;
		_operationVariant = operationVariant;
		_parameterNames = parameterNames;
		_path = path;
		_requiredPropertyCollections = requiredPropertyCollections;
		_schemaPropertyNames = schemaPropertyNames;
		_toolName = toolName;
		_toolSetName = toolSetName;
	}

	public String getCollection() {
		return _collection;
	}

	public String getDescription() {
		return _description;
	}

	public String getEntityName() {
		return _entityName;
	}

	public String[] getExpansions() {
		return _expansions;
	}

	public String getIntent() {
		return _intent;
	}

	public String getMethod() {
		return _method;
	}

	public String getOperationVariant() {
		return _operationVariant;
	}

	public String[] getParameterNames() {
		return _parameterNames;
	}

	public String getPath() {
		return _path;
	}

	public String[] getRequiredPropertyCollections() {
		return _requiredPropertyCollections;
	}

	public String[] getSchemaPropertyNames() {
		return _schemaPropertyNames;
	}

	public String getToolKey() {
		return ToolSetUtil.getToolKey(_toolName, _toolSetName);
	}

	public String getToolName() {
		return _toolName;
	}

	public String getToolSetName() {
		return _toolSetName;
	}

	public boolean isDeprecated() {
		return _deprecated;
	}

	@Override
	public String toString() {
		return StringBundler.concat(
			"{method=", _method, ", path=", _path, ", toolKey=", getToolKey(),
			"}");
	}

	private final String _collection;
	private final boolean _deprecated;
	private final String _description;
	private final String _entityName;
	private final String[] _expansions;
	private final String _intent;
	private final String _method;
	private final String _operationVariant;
	private final String[] _parameterNames;
	private final String _path;
	private final String[] _requiredPropertyCollections;
	private final String[] _schemaPropertyNames;
	private final String _toolName;
	private final String _toolSetName;

}