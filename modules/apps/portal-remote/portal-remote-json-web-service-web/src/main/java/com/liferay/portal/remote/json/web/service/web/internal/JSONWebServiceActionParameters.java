/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.json.web.service.web.internal;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.kernel.util.CamelCaseUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Igor Spasic
 */
public class JSONWebServiceActionParameters {

	public void collectAll(
		HttpServletRequest httpServletRequest, String parameterPath,
		JSONRPCRequest jsonRPCRequest, Map<String, Object> parameterMap) {

		_jsonRPCRequest = jsonRPCRequest;

		try {
			_serviceContext = ServiceContextFactory.getInstance(
				httpServletRequest);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		_addDefaultParameters();

		_collectDefaultsFromRequestAttributes(httpServletRequest);

		_collectFromPath(parameterPath);
		_collectFromRequestParameters(httpServletRequest);
		_collectFromJSONRPCRequest(jsonRPCRequest);
		_collectFromMap(parameterMap);
	}

	public List<Map.Entry<String, Object>> getInnerParameters(String baseName) {
		return _jsonWebServiceActionParameters.getInnerParameters(baseName);
	}

	public JSONRPCRequest getJSONRPCRequest() {
		return _jsonRPCRequest;
	}

	public Object getParameter(String name) {
		return _jsonWebServiceActionParameters.get(name);
	}

	public String[] getParameterNames() {
		String[] names = new String[_jsonWebServiceActionParameters.size()];

		int i = 0;

		for (String key : _jsonWebServiceActionParameters.keySet()) {
			names[i] = key;

			i++;
		}

		return names;
	}

	public String getParameterTypeName(String name) {
		return _jsonWebServiceActionParameters.getParameterTypeName(name);
	}

	public ServiceContext getServiceContext() {
		return _serviceContext;
	}

	public boolean includeDefaultParameters() {
		return _jsonWebServiceActionParameters.includeDefaultParameters();
	}

	private void _addDefaultParameters() {
		_jsonWebServiceActionParameters.put("serviceContext", Void.TYPE);
	}

	private void _collectDefaultsFromRequestAttributes(
		HttpServletRequest httpServletRequest) {

		Enumeration<String> enumeration =
			httpServletRequest.getAttributeNames();

		while (enumeration.hasMoreElements()) {
			String attributeName = enumeration.nextElement();

			Object value = httpServletRequest.getAttribute(attributeName);

			_jsonWebServiceActionParameters.putDefaultParameter(
				attributeName, value);
		}
	}

	private void _collectFromJSONRPCRequest(JSONRPCRequest jsonRPCRequest) {
		if (jsonRPCRequest == null) {
			return;
		}

		Set<String> parameterNames = jsonRPCRequest.getParameterNames();

		for (String parameterName : parameterNames) {
			String value = jsonRPCRequest.getParameter(parameterName);

			parameterName = CamelCaseUtil.normalizeCamelCase(parameterName);

			_jsonWebServiceActionParameters.put(parameterName, value);
		}
	}

	private void _collectFromMap(Map<String, Object> parameterMap) {
		if (parameterMap == null) {
			return;
		}

		for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
			String parameterName = entry.getKey();

			_jsonWebServiceActionParameters.put(
				parameterName, entry.getValue());
		}
	}

	private void _collectFromPath(String parameterPath) {
		if (parameterPath == null) {
			return;
		}

		if (parameterPath.startsWith(StringPool.SLASH)) {
			parameterPath = parameterPath.substring(1);
		}

		String[] parameterPathParts = StringUtil.split(
			parameterPath, CharPool.SLASH);

		int i = 0;

		while (i < parameterPathParts.length) {
			String name = parameterPathParts[i];

			if (name.length() == 0) {
				i++;

				continue;
			}

			String value = null;

			if (name.startsWith(StringPool.DASH)) {
				name = name.substring(1);
			}
			else if (!name.startsWith(StringPool.PLUS)) {
				i++;

				if (i >= parameterPathParts.length) {
					throw new IllegalArgumentException(
						"Missing value for parameter " + name);
				}

				value = parameterPathParts[i];
			}

			name = CamelCaseUtil.toCamelCase(name);

			_jsonWebServiceActionParameters.put(name, value);

			i++;
		}
	}

	private void _collectFromRequestParameters(
		HttpServletRequest httpServletRequest) {

		Map<String, FileItem[]> multipartParameterMap = null;

		Set<String> parameterNames = new HashSet<>(
			Collections.list(httpServletRequest.getParameterNames()));

		if (httpServletRequest instanceof UploadServletRequest) {
			UploadServletRequest uploadServletRequest =
				(UploadServletRequest)httpServletRequest;

			multipartParameterMap =
				uploadServletRequest.getMultipartParameterMap();

			parameterNames.addAll(multipartParameterMap.keySet());
		}

		for (String parameterName : parameterNames) {
			Object value = null;

			if ((multipartParameterMap != null) &&
				multipartParameterMap.containsKey(parameterName)) {

				FileItem[] fileItems = multipartParameterMap.get(parameterName);

				File[] files = new File[fileItems.length];

				for (int i = 0; i < fileItems.length; i++) {
					FileItem fileItem = fileItems[i];

					if (fileItem.isInMemory()) {
						files[i] = fileItem.getTempFile();

						try {
							FileUtil.write(files[i], fileItem.getInputStream());
						}
						catch (IOException ioException) {
							if (_log.isWarnEnabled()) {
								_log.warn(
									"Unable to write temporary file " +
										files[i].getAbsolutePath(),
									ioException);
							}
						}
					}
					else {
						files[i] = fileItem.getStoreLocation();
					}
				}

				if (files.length == 1) {
					value = files[0];
				}
				else {
					value = files;
				}
			}
			else {
				String[] parameterValues =
					httpServletRequest.getParameterValues(parameterName);

				if (parameterValues.length == 1) {
					value = parameterValues[0];
				}
				else {
					value = parameterValues;
				}
			}

			parameterName = CamelCaseUtil.normalizeCamelCase(parameterName);

			_jsonWebServiceActionParameters.put(parameterName, value);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JSONWebServiceActionParameters.class);

	private JSONRPCRequest _jsonRPCRequest;
	private final JSONWebServiceActionParametersMap
		_jsonWebServiceActionParameters =
			new JSONWebServiceActionParametersMap();
	private ServiceContext _serviceContext;

}