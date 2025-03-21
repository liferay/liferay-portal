/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.action;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONSerializable;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.struts.JSONAction;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brian Wing Shun Chan
 * @author Karthik Sudarshan
 * @author Julio Camarero
 * @author Eduardo Lundgren
 */
public class JSONServiceAction extends JSONAction {

	public JSONServiceAction() {
		_invalidClassNames = SetUtil.fromArray(
			PropsValues.JSON_SERVICE_INVALID_CLASS_NAMES);

		_invalidMethodNames = SetUtil.fromArray(
			PropsValues.JSON_SERVICE_INVALID_METHOD_NAMES);

		if (_log.isDebugEnabled()) {
			for (String invalidClassName : _invalidClassNames) {
				_log.debug("Invalid class name " + invalidClassName);
			}

			for (String invalidMethodName : _invalidMethodNames) {
				_log.debug("Invalid method name " + invalidMethodName);
			}
		}
	}

	@Override
	public String getJSON(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (!isValidRequest(httpServletRequest)) {
			return null;
		}

		String[] serviceParameters = getStringArrayFromJSON(
			httpServletRequest, "serviceParameters");
		String[] serviceParameterTypes = getStringArrayFromJSON(
			httpServletRequest, "serviceParameterTypes");

		String className = ParamUtil.getString(
			httpServletRequest, "serviceClassName");
		String methodName = ParamUtil.getString(
			httpServletRequest, "serviceMethodName");

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		Class<?> clazz = contextClassLoader.loadClass(className);

		Object[] methodAndParameterTypes = getMethodAndParameterTypes(
			clazz, methodName, serviceParameters, serviceParameterTypes);

		if (methodAndParameterTypes == null) {
			return null;
		}

		Method method = (Method)methodAndParameterTypes[0];

		Type[] parameterTypes = (Type[])methodAndParameterTypes[1];
		Object[] args = new Object[serviceParameters.length];

		for (int i = 0; i < serviceParameters.length; i++) {
			args[i] = getArgValue(
				httpServletRequest, clazz, methodName, serviceParameters[i],
				parameterTypes[i]);
		}

		try {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Invoking ", clazz, " on method ", method.getName(),
						" with args ", Arrays.toString(args)));
			}

			Object returnObject = null;

			boolean remoteAccess = AccessControlThreadLocal.isRemoteAccess();

			try {
				AccessControlThreadLocal.setRemoteAccess(true);

				returnObject = method.invoke(clazz, args);
			}
			finally {
				AccessControlThreadLocal.setRemoteAccess(remoteAccess);
			}

			if (returnObject != null) {
				return getReturnValue(returnObject);
			}

			return JSONFactoryUtil.getNullJSON();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Invoked ", clazz, " on method ", method.getName(),
						" with args ", Arrays.toString(args)),
					exception);
			}

			if (PropsValues.JSON_SERVICE_SERIALIZE_THROWABLE) {
				return JSONFactoryUtil.serializeThrowable(exception);
			}

			PortalUtil.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception,
				httpServletRequest, httpServletResponse);

			return JSONFactoryUtil.getNullJSON();
		}
	}

	protected Object getArgValue(
			HttpServletRequest httpServletRequest, Class<?> clazz,
			String methodName, String parameter, Type parameterType)
		throws Exception {

		String typeNameOrClassDescriptor = getTypeNameOrClassDescriptor(
			parameterType);

		String value = ParamUtil.getString(httpServletRequest, parameter);

		if (Validator.isNull(value) &&
			!typeNameOrClassDescriptor.equals("[Ljava.lang.String;")) {

			return null;
		}
		else if (typeNameOrClassDescriptor.equals("boolean") ||
				 typeNameOrClassDescriptor.equals(Boolean.class.getName())) {

			return Boolean.valueOf(
				ParamUtil.getBoolean(httpServletRequest, parameter));
		}
		else if (typeNameOrClassDescriptor.equals("double") ||
				 typeNameOrClassDescriptor.equals(Double.class.getName())) {

			return Double.valueOf(
				ParamUtil.getDouble(httpServletRequest, parameter));
		}
		else if (typeNameOrClassDescriptor.equals("int") ||
				 typeNameOrClassDescriptor.equals(Integer.class.getName())) {

			return Integer.valueOf(
				ParamUtil.getInteger(httpServletRequest, parameter));
		}
		else if (typeNameOrClassDescriptor.equals("long") ||
				 typeNameOrClassDescriptor.equals(Long.class.getName())) {

			return Long.valueOf(
				ParamUtil.getLong(httpServletRequest, parameter));
		}
		else if (typeNameOrClassDescriptor.equals("short") ||
				 typeNameOrClassDescriptor.equals(Short.class.getName())) {

			return Short.valueOf(
				ParamUtil.getShort(httpServletRequest, parameter));
		}
		else if (typeNameOrClassDescriptor.equals(Calendar.class.getName())) {
			Calendar cal = Calendar.getInstance(LocaleUtil.getDefault());

			cal.setTimeInMillis(
				ParamUtil.getLong(httpServletRequest, parameter));

			return cal;
		}
		else if (typeNameOrClassDescriptor.equals(Date.class.getName())) {
			return new Date(ParamUtil.getLong(httpServletRequest, parameter));
		}
		else if (typeNameOrClassDescriptor.equals(
					ServiceContext.class.getName())) {

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(value);

			jsonObject.put("javaClass", ServiceContext.class.getName());

			return ServiceContextUtil.deserialize(jsonObject);
		}
		else if (typeNameOrClassDescriptor.equals(String.class.getName())) {
			return value;
		}
		else if (typeNameOrClassDescriptor.equals("[Z")) {
			return ParamUtil.getBooleanValues(httpServletRequest, parameter);
		}
		else if (typeNameOrClassDescriptor.equals("[D")) {
			return ParamUtil.getDoubleValues(httpServletRequest, parameter);
		}
		else if (typeNameOrClassDescriptor.equals("[F")) {
			return ParamUtil.getFloatValues(httpServletRequest, parameter);
		}
		else if (typeNameOrClassDescriptor.equals("[I")) {
			return ParamUtil.getIntegerValues(httpServletRequest, parameter);
		}
		else if (typeNameOrClassDescriptor.equals("[J")) {
			return ParamUtil.getLongValues(httpServletRequest, parameter);
		}
		else if (typeNameOrClassDescriptor.equals("[S")) {
			return ParamUtil.getShortValues(httpServletRequest, parameter);
		}
		else if (typeNameOrClassDescriptor.equals("[Ljava.lang.String;")) {
			return ParamUtil.getParameterValues(httpServletRequest, parameter);
		}
		else if (typeNameOrClassDescriptor.equals("[[Z")) {
			String[] values = httpServletRequest.getParameterValues(parameter);

			if (ArrayUtil.isNotEmpty(values)) {
				String[] values0 = StringUtil.split(values[0]);

				boolean[][] doubleArray =
					new boolean[values.length][values0.length];

				for (int i = 0; i < values.length; i++) {
					String[] curValues = StringUtil.split(values[i]);

					for (int j = 0; j < curValues.length; j++) {
						doubleArray[i][j] = GetterUtil.getBoolean(curValues[j]);
					}
				}

				return doubleArray;
			}

			return new boolean[0][0];
		}
		else if (typeNameOrClassDescriptor.equals("[[D")) {
			String[] values = httpServletRequest.getParameterValues(parameter);

			if (ArrayUtil.isNotEmpty(values)) {
				String[] values0 = StringUtil.split(values[0]);

				double[][] doubleArray =
					new double[values.length][values0.length];

				for (int i = 0; i < values.length; i++) {
					String[] curValues = StringUtil.split(values[i]);

					for (int j = 0; j < curValues.length; j++) {
						doubleArray[i][j] = GetterUtil.getDouble(curValues[j]);
					}
				}

				return doubleArray;
			}

			return new double[0][0];
		}
		else if (typeNameOrClassDescriptor.equals("[[F")) {
			String[] values = httpServletRequest.getParameterValues(parameter);

			if (ArrayUtil.isNotEmpty(values)) {
				String[] values0 = StringUtil.split(values[0]);

				float[][] doubleArray =
					new float[values.length][values0.length];

				for (int i = 0; i < values.length; i++) {
					String[] curValues = StringUtil.split(values[i]);

					for (int j = 0; j < curValues.length; j++) {
						doubleArray[i][j] = GetterUtil.getFloat(curValues[j]);
					}
				}

				return doubleArray;
			}

			return new float[0][0];
		}
		else if (typeNameOrClassDescriptor.equals("[[I")) {
			String[] values = httpServletRequest.getParameterValues(parameter);

			if (ArrayUtil.isNotEmpty(values)) {
				String[] values0 = StringUtil.split(values[0]);

				int[][] doubleArray = new int[values.length][values0.length];

				for (int i = 0; i < values.length; i++) {
					String[] curValues = StringUtil.split(values[i]);

					for (int j = 0; j < curValues.length; j++) {
						doubleArray[i][j] = GetterUtil.getInteger(curValues[j]);
					}
				}

				return doubleArray;
			}

			return new int[0][0];
		}
		else if (typeNameOrClassDescriptor.equals("[[J")) {
			String[] values = httpServletRequest.getParameterValues(parameter);

			if (ArrayUtil.isNotEmpty(values)) {
				String[] values0 = StringUtil.split(values[0]);

				long[][] doubleArray = new long[values.length][values0.length];

				for (int i = 0; i < values.length; i++) {
					String[] curValues = StringUtil.split(values[i]);

					for (int j = 0; j < curValues.length; j++) {
						doubleArray[i][j] = GetterUtil.getLong(curValues[j]);
					}
				}

				return doubleArray;
			}

			return new long[0][0];
		}
		else if (typeNameOrClassDescriptor.equals("[[S")) {
			String[] values = httpServletRequest.getParameterValues(parameter);

			if (ArrayUtil.isNotEmpty(values)) {
				String[] values0 = StringUtil.split(values[0]);

				short[][] doubleArray =
					new short[values.length][values0.length];

				for (int i = 0; i < values.length; i++) {
					String[] curValues = StringUtil.split(values[i]);

					for (int j = 0; j < curValues.length; j++) {
						doubleArray[i][j] = GetterUtil.getShort(curValues[j]);
					}
				}

				return doubleArray;
			}

			return new short[0][0];
		}
		else if (typeNameOrClassDescriptor.equals("[[Ljava.lang.String")) {
			String[] values = httpServletRequest.getParameterValues(parameter);

			if (ArrayUtil.isNotEmpty(values)) {
				String[] values0 = StringUtil.split(values[0]);

				String[][] doubleArray =
					new String[values.length][values0.length];

				for (int i = 0; i < values.length; i++) {
					doubleArray[i] = StringUtil.split(values[i]);
				}

				return doubleArray;
			}

			return new String[0][0];
		}
		else if (typeNameOrClassDescriptor.equals(
					"java.util.Map<java.util.Locale, java.lang.String>")) {

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(value);

			return LocalizationUtil.deserialize(jsonObject);
		}

		try {
			return JSONFactoryUtil.looseDeserialize(value);
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat(
					"Unsupported parameter type for class ", clazz, ", method ",
					methodName, ", parameter ", parameter, ", and type ",
					typeNameOrClassDescriptor),
				exception);

			return null;
		}
	}

	/**
	 * @see com.liferay.portal.jsonwebservice.JSONWebServiceServiceAction#getCSRFOrigin(
	 *      HttpServletRequest)
	 */
	@Override
	protected String getCSRFOrigin(HttpServletRequest httpServletRequest) {
		StringBundler sb = new StringBundler(6);

		sb.append(ClassUtil.getClassName(this));
		sb.append(StringPool.COLON);
		sb.append(StringPool.SLASH);

		String serviceClassName = ParamUtil.getString(
			httpServletRequest, "serviceClassName");

		sb.append(serviceClassName);

		sb.append(StringPool.POUND);

		String serviceMethodName = ParamUtil.getString(
			httpServletRequest, "serviceMethodName");

		sb.append(serviceMethodName);

		return sb.toString();
	}

	protected Object[] getMethodAndParameterTypes(
			Class<?> clazz, String methodName, String[] parameters,
			String[] parameterTypes)
		throws Exception {

		StringBundler sb = new StringBundler(5);

		sb.append(clazz.getName());
		sb.append("_METHOD_NAME_");
		sb.append(methodName);
		sb.append("_PARAMETERS_");

		String parameterTypesNames = StringUtil.merge(parameterTypes);

		if (Validator.isNull(parameterTypesNames)) {
			sb.append(parameters.length);
		}
		else {
			sb.append(parameterTypesNames);
		}

		String key = sb.toString();

		Object[] methodAndParameterTypes = _methodCache.get(key);

		if (methodAndParameterTypes != null) {
			return methodAndParameterTypes;
		}

		Method method = null;
		Type[] methodParameterTypes = null;

		Method[] methods = clazz.getMethods();

		for (Method curMethod : methods) {
			String curMethodName = curMethod.getName();

			if (!curMethodName.equals(methodName)) {
				continue;
			}

			Type[] curParameterTypes = curMethod.getGenericParameterTypes();

			if (curParameterTypes.length != parameters.length) {
				continue;
			}

			if ((parameterTypes.length > 0) &&
				(parameterTypes.length == curParameterTypes.length)) {

				boolean match = true;

				for (int j = 0; j < parameterTypes.length; j++) {
					String t1 = parameterTypes[j];
					String t2 = getTypeNameOrClassDescriptor(
						curParameterTypes[j]);

					if (!t1.equals(t2)) {
						match = false;
					}
				}

				if (match) {
					method = curMethod;
					methodParameterTypes = curParameterTypes;

					break;
				}
			}
			else if (method != null) {
				String parametersString = StringUtil.merge(parameters);

				_log.error(
					StringBundler.concat(
						"Obscure method name for class ", clazz, ", method ",
						methodName, ", and parameters ", parametersString));

				return null;
			}
			else {
				method = curMethod;
				methodParameterTypes = curParameterTypes;
			}
		}

		if (method != null) {
			methodAndParameterTypes = new Object[] {
				method, methodParameterTypes
			};

			_methodCache.put(key, methodAndParameterTypes);

			return methodAndParameterTypes;
		}

		String parametersString = StringUtil.merge(parameters);

		_log.error(
			StringBundler.concat(
				"No method found for class ", clazz, ", method ", methodName,
				", and parameters ", parametersString));

		return null;
	}

	@Override
	protected String getReroutePath() {
		return _REROUTE_PATH;
	}

	protected String getReturnValue(Object returnObject) throws Exception {
		if (returnObject instanceof JSONSerializable) {
			JSONSerializable jsonSerializable = (JSONSerializable)returnObject;

			return jsonSerializable.toJSONString();
		}

		JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();

		jsonSerializer.exclude("*.class");

		return jsonSerializer.serializeDeep(returnObject);
	}

	protected String[] getStringArrayFromJSON(
			HttpServletRequest httpServletRequest, String param)
		throws JSONException {

		String json = ParamUtil.getString(httpServletRequest, param, "[]");

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(json);

		return ArrayUtil.toStringArray(jsonArray);
	}

	protected String getTypeNameOrClassDescriptor(Type type) {
		String typeName = type.toString();

		if (typeName.contains("class ")) {
			return typeName.substring(6);
		}

		Matcher matcher = _fieldDescriptorPattern.matcher(typeName);

		while (matcher.find()) {
			String dimensions = matcher.group(2);
			String fieldDescriptor = matcher.group(1);

			if (Validator.isNull(dimensions)) {
				return fieldDescriptor;
			}

			dimensions = StringUtil.replace(
				dimensions, CharPool.CLOSE_BRACKET, StringPool.BLANK);

			if (fieldDescriptor.equals("boolean")) {
				fieldDescriptor = "Z";
			}
			else if (fieldDescriptor.equals("byte")) {
				fieldDescriptor = "B";
			}
			else if (fieldDescriptor.equals("char")) {
				fieldDescriptor = "C";
			}
			else if (fieldDescriptor.equals("double")) {
				fieldDescriptor = "D";
			}
			else if (fieldDescriptor.equals("float")) {
				fieldDescriptor = "F";
			}
			else if (fieldDescriptor.equals("int")) {
				fieldDescriptor = "I";
			}
			else if (fieldDescriptor.equals("long")) {
				fieldDescriptor = "J";
			}
			else if (fieldDescriptor.equals("short")) {
				fieldDescriptor = "S";
			}
			else {
				fieldDescriptor = StringBundler.concat(
					"L", fieldDescriptor, StringPool.SEMICOLON);
			}

			return dimensions.concat(fieldDescriptor);
		}

		throw new IllegalArgumentException(type.toString() + " is invalid");
	}

	protected boolean isValidRequest(HttpServletRequest httpServletRequest) {
		String className = ParamUtil.getString(
			httpServletRequest, "serviceClassName");
		String methodName = ParamUtil.getString(
			httpServletRequest, "serviceMethodName");

		if (className.contains(".service.") &&
			className.endsWith("ServiceUtil") &&
			!className.endsWith("LocalServiceUtil") &&
			!_invalidClassNames.contains(className) &&
			!_invalidMethodNames.contains(methodName)) {

			return true;
		}

		return false;
	}

	private static final String _REROUTE_PATH = "/api/json";

	private static final Log _log = LogFactoryUtil.getLog(
		JSONServiceAction.class);

	private static final Pattern _fieldDescriptorPattern = Pattern.compile(
		"^(.*?)((\\[\\])*)$", Pattern.DOTALL);

	private final Set<String> _invalidClassNames;
	private final Set<String> _invalidMethodNames;
	private final Map<String, Object[]> _methodCache = new HashMap<>();

}