/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.parser;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.JavaMethodParameter;
import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.JavaMethodSignature;
import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.parser.util.OpenAPIParserUtil;
import com.liferay.portal.tools.rest.builder.internal.yaml.config.ConfigYAML;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Content;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Info;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.OpenAPIYAML;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Operation;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.RequestBody;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Peter Shin
 */
public class ResourceTestCaseOpenAPIParser {

	public static List<JavaMethodSignature> getJavaMethodSignatures(
		ConfigYAML configYAML, OpenAPIYAML openAPIYAML, String schemaName) {

		List<JavaMethodSignature> javaMethodSignatures = new ArrayList<>();

		List<JavaMethodSignature> resourceJavaMethodSignatures =
			ResourceOpenAPIParser.getJavaMethodSignatures(
				configYAML, openAPIYAML, schemaName);

		for (JavaMethodSignature resourceJavaMethodSignature :
				resourceJavaMethodSignatures) {

			javaMethodSignatures.add(
				new JavaMethodSignature(
					resourceJavaMethodSignature.getPath(),
					resourceJavaMethodSignature.getPathItem(),
					resourceJavaMethodSignature.getOperation(),
					resourceJavaMethodSignature.getRequestBodyMediaTypes(),
					resourceJavaMethodSignature.getSchemaName(),
					resourceJavaMethodSignature.getJavaMethodParameters(),
					_getMethodName(resourceJavaMethodSignature),
					_getReturnType(
						configYAML.getApiPackagePath(),
						resourceJavaMethodSignature.getReturnType(),
						_getVersion(openAPIYAML)),
					resourceJavaMethodSignature.getParentSchemaName()));
		}

		return javaMethodSignatures;
	}

	public static String getParameters(
		ConfigYAML configYAML, List<JavaMethodParameter> javaMethodParameters,
		Operation operation, Map<String, Schema> schemas, boolean annotation) {

		return ResourceOpenAPIParser.getParameters(
			configYAML, javaMethodParameters, operation, schemas, annotation);
	}

	private static String _getMethodName(
		JavaMethodSignature javaMethodSignature) {

		Operation operation = javaMethodSignature.getOperation();
		Set<String> requestBodyMediaTypes =
			javaMethodSignature.getRequestBodyMediaTypes();

		List<String> mediaTypes = new ArrayList<>();

		if (operation.getRequestBody() != null) {
			RequestBody requestBody = operation.getRequestBody();

			if (requestBody.getContent() != null) {
				Map<String, Content> contents = requestBody.getContent();

				mediaTypes.addAll(contents.keySet());
			}
		}

		if (operation.getOperationId() != null) {
			String operationId = operation.getOperationId();

			if (!requestBodyMediaTypes.contains("multipart/form-data") ||
				(mediaTypes.size() < 2)) {

				return operationId;
			}

			int index = 0;

			for (int i = 0; i < operationId.length(); i++) {
				if (Character.isUpperCase(operationId.charAt(i))) {
					index = i;

					break;
				}
			}

			return StringBundler.concat(
				operationId.substring(0, index), "FormData",
				operationId.substring(index));
		}

		String methodName = javaMethodSignature.getMethodName();

		if (requestBodyMediaTypes.contains("multipart/form-data") &&
			(mediaTypes.size() > 1)) {

			String httpMethod = OpenAPIParserUtil.getHTTPMethod(operation);

			return StringUtil.replaceFirst(
				methodName, httpMethod, httpMethod + "FormData");
		}

		return methodName;
	}

	private static String _getReturnType(
		String apiPackage, String returnType, String version) {

		String versionPackage = StringUtil.replace(version, '.', '_');

		if (returnType.startsWith(
				"com.liferay.portal.vulcan.pagination.Page<")) {

			String itemType = returnType.substring(
				returnType.indexOf("<") + 1, returnType.indexOf(">"));

			if (itemType.contains(".") &&
				!itemType.startsWith("com.liferay.portal.vulcan") &&
				!itemType.startsWith("java.lang") &&
				!itemType.startsWith("java.util") &&
				!itemType.startsWith(apiPackage)) {

				return StringBundler.concat(
					"com.liferay.portal.vulcan.pagination.Page<", apiPackage,
					".dto.", versionPackage, ".",
					itemType.substring(itemType.lastIndexOf(".") + 1), ">");
			}
		}
		else if (returnType.contains(".") &&
				 !returnType.equals("com.liferay.portal.vulcan") &&
				 !returnType.equals("jakarta.ws.rs.core.Response") &&
				 !returnType.startsWith("java.lang") &&
				 !returnType.startsWith("java.util") &&
				 !returnType.startsWith(apiPackage)) {

			return StringBundler.concat(
				apiPackage, ".dto.", versionPackage, ".",
				returnType.substring(returnType.lastIndexOf(".") + 1));
		}

		return returnType;
	}

	private static String _getVersion(OpenAPIYAML openAPIYAML) {
		Info info = openAPIYAML.getInfo();

		return info.getVersion();
	}

}