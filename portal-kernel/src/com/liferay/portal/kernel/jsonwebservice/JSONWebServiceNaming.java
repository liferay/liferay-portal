/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.jsonwebservice;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.util.CamelCaseUtil;
import com.liferay.portal.kernel.util.MethodParameter;
import com.liferay.portal.kernel.util.MethodParametersResolverUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.InputStream;
import java.io.OutputStream;

import java.lang.reflect.Method;

import java.util.Set;

/**
 * @author Igor Spasic
 */
public class JSONWebServiceNaming {

	public String convertMethodToHttpMethod(Method method) {
		String methodName = method.getName();

		String methodNamePrefix = getMethodNamePrefix(methodName);

		if (prefixes.contains(methodNamePrefix)) {
			return HttpMethods.GET;
		}

		return HttpMethods.POST;
	}

	public String convertMethodToPath(Method method) {
		return CamelCaseUtil.fromCamelCase(method.getName());
	}

	public String convertModelClassToImplClassName(Class<?> clazz) {
		ImplementationClassName implementationClassName = clazz.getAnnotation(
			ImplementationClassName.class);

		if (implementationClassName != null) {
			return implementationClassName.value();
		}

		String className = clazz.getName();

		className = StringUtil.replace(className, ".kernel.", ".");
		className =
			StringUtil.replace(className, ".model.", ".model.impl.") + "Impl";

		return className;
	}

	public String convertServiceClassToPath(Class<?> clazz) {
		String className = convertServiceClassToSimpleName(clazz);

		return StringUtil.toLowerCase(className);
	}

	public String convertServiceClassToSimpleName(Class<?> clazz) {
		String className = clazz.getSimpleName();

		className = StringUtil.replace(className, "Impl", StringPool.BLANK);
		className = StringUtil.replace(className, "Service", StringPool.BLANK);

		return className;
	}

	public String convertServiceImplClassToUtilClassName(Class<?> clazz) {
		String className = clazz.getName();

		if (className.endsWith("Impl")) {
			className = className.substring(0, className.length() - 4);
		}

		return StringUtil.replace(
			className + "Util", ".impl.", StringPool.PERIOD);
	}

	public boolean isIncludedMethod(Method method) {
		if ((excludedMethodNames != null) &&
			excludedMethodNames.contains(method.getName())) {

			return false;
		}

		if (excludedTypesNames == null) {
			return true;
		}

		Class<?> returnType = method.getReturnType();

		if (returnType.isArray()) {
			returnType = returnType.getComponentType();
		}

		String returnTypeName = returnType.getName();

		for (String excludedTypesName : excludedTypesNames) {
			if (excludedTypesName.startsWith(returnTypeName)) {
				return false;
			}
		}

		MethodParameter[] methodParameters =
			MethodParametersResolverUtil.resolveMethodParameters(method);

		Class<?>[] parameterTypes = method.getParameterTypes();

		for (int i = 0; i < parameterTypes.length; i++) {
			MethodParameter methodParameter = methodParameters[i];

			Class<?> parameterType = parameterTypes[i];

			if (parameterType.isArray()) {
				parameterType = parameterType.getComponentType();
			}

			String parameterTypeName = parameterType.getName();

			for (String excludedTypesName : excludedTypesNames) {
				if (parameterTypeName.startsWith(excludedTypesName)) {
					return false;
				}

				Class<?>[] genericTypes = methodParameter.getGenericTypes();

				if (genericTypes != null) {
					for (Class<?> genericType : genericTypes) {
						String genericName = genericType.getName();

						if (genericName.startsWith(excludedTypesName)) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	public boolean isIncludedPath(String contextPath, String path) {
		String portalContextPath = PortalUtil.getPathContext();

		if (!contextPath.equals(portalContextPath)) {
			path = contextPath + StringPool.PERIOD + path.substring(1);
		}

		for (String excludedPath : excludedPaths) {
			if (StringUtil.wildcardMatches(
					path, excludedPath, '?', '*', '\\', false)) {

				return false;
			}
		}

		if (includedPaths.length == 0) {
			return true;
		}

		for (String includedPath : includedPaths) {
			if (StringUtil.wildcardMatches(
					path, includedPath, '?', '*', '\\', false)) {

				return true;
			}
		}

		return false;
	}

	public boolean isValidHttpMethod(String httpMethod) {
		if (invalidHttpMethods.contains(httpMethod)) {
			return false;
		}

		return true;
	}

	protected String getMethodNamePrefix(String methodName) {
		int i = 0;

		while (i < methodName.length()) {
			if (Character.isUpperCase(methodName.charAt(i))) {
				break;
			}

			i++;
		}

		return methodName.substring(0, i);
	}

	protected Set<String> excludedMethodNames = SetUtil.fromArray(
		PropsUtil.getArray(PropsKeys.JSON_SERVICE_INVALID_METHOD_NAMES));
	protected String[] excludedPaths = PropsUtil.getArray(
		PropsKeys.JSONWS_WEB_SERVICE_PATHS_EXCLUDES);
	protected String[] excludedTypesNames =
		{InputStream.class.getName(), OutputStream.class.getName(), "javax."};
	protected String[] includedPaths = PropsUtil.getArray(
		PropsKeys.JSONWS_WEB_SERVICE_PATHS_INCLUDES);
	protected Set<String> invalidHttpMethods = SetUtil.fromArray(
		PropsUtil.getArray(PropsKeys.JSONWS_WEB_SERVICE_INVALID_HTTP_METHODS));
	protected Set<String> prefixes = SetUtil.fromArray(
		new String[] {"get", "has", "is"});

}