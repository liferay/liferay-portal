/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.json.web.service.web.internal.action;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.data.FileData;
import com.liferay.portal.json.transformer.BeanAnalyzerTransformer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONSerializable;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.MethodParameter;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.remote.json.web.service.JSONWebServiceAction;
import com.liferay.portal.remote.json.web.service.JSONWebServiceActionMapping;
import com.liferay.portal.remote.json.web.service.JSONWebServiceActionsManager;
import com.liferay.portal.remote.json.web.service.web.internal.JSONWebServiceNamingUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.Serializable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import jodd.util.ClassUtil;

/**
 * @author Igor Spasic
 * @author Raymond Augé
 */
public class JSONWebServiceDiscoverAction implements JSONWebServiceAction {

	public JSONWebServiceDiscoverAction(
		JSONWebServiceActionsManager jsonWebServiceActionsManager,
		HttpServletRequest httpServletRequest) {

		_jsonWebServiceActionsManager = jsonWebServiceActionsManager;

		_basePath = httpServletRequest.getServletPath();
		_baseURL = String.valueOf(httpServletRequest.getRequestURL());

		ServletContext servletContext = httpServletRequest.getServletContext();

		_contextName = GetterUtil.getString(
			ParamUtil.getString(
				httpServletRequest, "contextName",
				servletContext.getServletContextName()));
	}

	@Override
	public Object invoke() throws Exception {
		return new DiscoveryContent(
			LinkedHashMapBuilder.<String, Object>put(
				"contextName", _contextName
			).put(
				"basePath", _basePath
			).put(
				"baseURL", _baseURL
			).put(
				"services", _buildJsonWebServiceActionMappingMaps()
			).put(
				"types", _buildTypes()
			).put(
				"version", ReleaseInfo.getVersion()
			).build());
	}

	public static class DiscoveryContent implements JSONSerializable {

		public DiscoveryContent(Map<String, Object> resultsMap) {
			_resultsMap = resultsMap;
		}

		@Override
		public String toJSONString() {
			JSONSerializer jsonSerializer =
				JSONFactoryUtil.createJSONSerializer();

			jsonSerializer.include("types");

			return jsonSerializer.serializeDeep(_resultsMap);
		}

		private final Map<String, Object> _resultsMap;

	}

	private List<Map<String, Object>> _buildJsonWebServiceActionMappingMaps() {
		List<JSONWebServiceActionMapping> jsonWebServiceActionMappings =
			_jsonWebServiceActionsManager.getJSONWebServiceActionMappings(
				_contextName);

		List<Map<String, Object>> jsonWebServiceActionMappingMaps =
			new ArrayList<>(jsonWebServiceActionMappings.size());

		for (JSONWebServiceActionMapping jsonWebServiceActionMapping :
				jsonWebServiceActionMappings) {

			String path = jsonWebServiceActionMapping.getPath();

			Map<String, Object> jsonWebServiceActionMappingMap =
				new LinkedHashMap<>();

			if (jsonWebServiceActionMapping.isDeprecated()) {
				jsonWebServiceActionMappingMap.put("deprecated", Boolean.TRUE);
			}

			jsonWebServiceActionMappingMap.put(
				"method", jsonWebServiceActionMapping.getMethod());

			jsonWebServiceActionMappingMap.put(
				"name", _getName(jsonWebServiceActionMapping));

			MethodParameter[] methodParameters =
				jsonWebServiceActionMapping.getMethodParameters();

			List<Map<String, String>> parametersList = new ArrayList<>(
				methodParameters.length);

			for (MethodParameter methodParameter : methodParameters) {
				parametersList.add(
					HashMapBuilder.put(
						"name", methodParameter.getName()
					).put(
						"type",
						_formatType(
							methodParameter.getType(),
							methodParameter.getGenericTypes(), false)
					).build());
			}

			jsonWebServiceActionMappingMap.put("parameters", parametersList);

			jsonWebServiceActionMappingMap.put("path", path);

			Method actionMethod = jsonWebServiceActionMapping.getActionMethod();

			jsonWebServiceActionMappingMap.put(
				"returns",
				Collections.singletonMap(
					"type",
					_formatType(
						actionMethod.getReturnType(),
						_getGenericReturnTypes(jsonWebServiceActionMapping),
						true)));

			jsonWebServiceActionMappingMaps.add(jsonWebServiceActionMappingMap);
		}

		return jsonWebServiceActionMappingMaps;
	}

	private List<Map<String, String>> _buildPropertiesList(Class<?> type) {
		try {
			BeanAnalyzerTransformer beanAnalyzerTransformer =
				new BeanAnalyzerTransformer(type) {

					@Override
					protected String getTypeName(Class<?> type) {
						return _formatType(type, null, false);
					}

				};

			return beanAnalyzerTransformer.collect();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	private List<Map<String, Object>> _buildTypes() {
		_completeTypes();

		List<Map<String, Object>> types = new ArrayList<>();

		for (Class<?> type : _types) {
			Map<String, Object> map = new LinkedHashMap<>();

			types.add(map);

			Class<?> modelType = _getInterfaceType(type);

			if (modelType.isInterface() ||
				Modifier.isAbstract(modelType.getModifiers())) {

				map.put("interface", Boolean.TRUE);
			}

			List<Map<String, String>> propertiesList = _buildPropertiesList(
				modelType);

			if (propertiesList != null) {
				map.put("properties", propertiesList);
			}

			map.put("type", type.getName());
		}

		return types;
	}

	private void _completeTypes() {
		while (true) {
			int typesSize = _types.size();

			for (Class<?> type : new ArrayList<>(_types)) {
				Class<?> modelType = _getInterfaceType(type);

				_buildPropertiesList(modelType);
			}

			if (typesSize == _types.size()) {
				break;
			}
		}
	}

	private String _formatType(
		Class<?> type, Class<?>[] genericTypes, boolean returnType) {

		if (type.isArray()) {
			String typeName = _formatType(
				type.getComponentType(), genericTypes, returnType);

			return typeName + "[]";
		}

		if (type.isPrimitive()) {
			return type.getSimpleName();
		}

		if (type.equals(Boolean.class)) {
			return "boolean";
		}
		else if (type.equals(Class.class)) {
			if (!returnType) {
				return "string";
			}
		}
		else if (type.equals(Date.class)) {
			return "long";
		}
		else if (type.equals(File.class)) {
			if (!returnType) {
				return "file";
			}

			type = FileData.class;
		}
		else if (type.equals(Locale.class) || type.equals(String.class) ||
				 type.equals(TimeZone.class)) {

			return "string";
		}
		else if (type.equals(Object.class) || type.equals(Serializable.class)) {
			return "map";
		}
		else if (ClassUtil.isTypeOf(type, Number.class)) {
			String typeName = null;

			if (type == Character.class) {
				typeName = "char";
			}
			else if (type == Integer.class) {
				typeName = "int";
			}
			else {
				typeName = StringUtil.toLowerCase(type.getSimpleName());
			}

			return typeName;
		}

		String typeName = type.getName();

		if ((type == Collection.class) ||
			ClassUtil.isTypeOf(type, List.class)) {

			typeName = "list";
		}
		else if (ClassUtil.isTypeOf(type, Map.class)) {
			typeName = "map";
		}
		else {
			if (!_types.contains(type)) {
				_types.add(type);
			}
		}

		if (genericTypes == null) {
			return typeName;
		}

		StringBundler sb = new StringBundler((genericTypes.length * 2) + 1);

		sb.append(StringPool.LESS_THAN);

		for (int i = 0; i < genericTypes.length; i++) {
			Class<?> genericType = genericTypes[i];

			if (i != 0) {
				sb.append(StringPool.COMMA);
			}

			if (genericType == null) {
				sb.append(StringPool.STAR);
			}
			else {
				sb.append(_formatType(genericType, null, returnType));
			}
		}

		sb.append(StringPool.GREATER_THAN);

		return typeName + sb.toString();
	}

	private Class<?>[] _getGenericReturnTypes(
		JSONWebServiceActionMapping jsonWebServiceActionMapping) {

		Method realActionMethod =
			jsonWebServiceActionMapping.getRealActionMethod();

		Type genericReturnType = realActionMethod.getGenericReturnType();

		if (!(genericReturnType instanceof ParameterizedType)) {
			return null;
		}

		ParameterizedType parameterizedType =
			(ParameterizedType)genericReturnType;

		Type[] genericTypes = parameterizedType.getActualTypeArguments();

		Class<?>[] genericReturnTypes = new Class<?>[genericTypes.length];

		for (int i = 0; i < genericTypes.length; i++) {
			Type genericType = genericTypes[i];

			genericReturnTypes[i] = ClassUtil.getRawType(
				genericType, jsonWebServiceActionMapping.getActionClass());
		}

		return genericReturnTypes;
	}

	private Class<?> _getInterfaceType(Class<?> type) {
		Class<?> modelType = type;

		if (type.isInterface()) {
			try {
				Class<?> clazz = getClass();

				ClassLoader classLoader = clazz.getClassLoader();

				String modelImplClassName =
					JSONWebServiceNamingUtil.convertModelClassToImplClassName(
						type);

				modelType = classLoader.loadClass(modelImplClassName);
			}
			catch (ClassNotFoundException classNotFoundException) {
				if (_log.isDebugEnabled()) {
					_log.debug(classNotFoundException);
				}
			}
		}

		return modelType;
	}

	private String _getName(
		JSONWebServiceActionMapping jsonWebServiceActionMapping) {

		Class<?> clazz = jsonWebServiceActionMapping.getActionClass();

		String className =
			JSONWebServiceNamingUtil.convertServiceClassToSimpleName(clazz);

		Method method = jsonWebServiceActionMapping.getRealActionMethod();

		return StringBundler.concat(
			className, StringPool.POUND, method.getName());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JSONWebServiceDiscoverAction.class);

	private final String _basePath;
	private final String _baseURL;
	private final String _contextName;
	private final JSONWebServiceActionsManager _jsonWebServiceActionsManager;
	private final List<Class<?>> _types = new ArrayList<>();

}