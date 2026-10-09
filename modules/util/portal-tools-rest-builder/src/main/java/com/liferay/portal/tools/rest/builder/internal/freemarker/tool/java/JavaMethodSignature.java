/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java;

import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.FreeMarkerTool;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Operation;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.PathItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Peter Shin
 */
public class JavaMethodSignature {

	public JavaMethodSignature(
		String path, PathItem pathItem, Operation operation,
		Set<String> requestBodyMediaTypes, String schemaName,
		List<JavaMethodParameter> javaMethodParameters, String methodName,
		String returnType, String parentSchemaName) {

		_path = path;
		_pathItem = pathItem;
		_operation = operation;
		_requestBodyMediaTypes = requestBodyMediaTypes;
		_schemaName = schemaName;
		_javaMethodParameters = javaMethodParameters;
		_methodName = methodName;
		_returnType = returnType;

		for (JavaMethodParameter javaMethodParameter : _javaMethodParameters) {
			FreeMarkerTool freeMarkerTool = FreeMarkerTool.getInstance();

			if (!freeMarkerTool.isPathParameter(
					javaMethodParameter, operation)) {

				continue;
			}

			_pathJavaMethodParameters.add(javaMethodParameter);
		}

		_parentSchemaName = parentSchemaName;
	}

	public List<JavaMethodParameter> getJavaMethodParameters() {
		return _javaMethodParameters;
	}

	public String getMethodName() {
		return _methodName;
	}

	public Operation getOperation() {
		return _operation;
	}

	public String getParentSchemaName() {
		return _parentSchemaName;
	}

	public String getPath() {
		return _path;
	}

	public PathItem getPathItem() {
		return _pathItem;
	}

	public List<JavaMethodParameter> getPathJavaMethodParameters() {
		return _pathJavaMethodParameters;
	}

	public Set<String> getRequestBodyMediaTypes() {
		return _requestBodyMediaTypes;
	}

	public String getReturnType() {
		return _returnType;
	}

	public String getSchemaName() {
		return _schemaName;
	}

	private final List<JavaMethodParameter> _javaMethodParameters;
	private final String _methodName;
	private final Operation _operation;
	private final String _parentSchemaName;
	private final String _path;
	private final PathItem _pathItem;
	private final List<JavaMethodParameter> _pathJavaMethodParameters =
		new ArrayList<>();
	private final Set<String> _requestBodyMediaTypes;
	private final String _returnType;
	private final String _schemaName;

}