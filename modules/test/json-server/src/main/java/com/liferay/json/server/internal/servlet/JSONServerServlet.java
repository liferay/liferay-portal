/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.json.server.internal.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;

import java.net.URI;
import java.net.URL;

import java.rmi.ServerException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Shuyang Zhou
 */
@Component(
	property = {
		"osgi.http.whiteboard.context.path=/json-server",
		"osgi.http.whiteboard.servlet.name=com.liferay.json.server.internal.servlet.JSONServerServlet",
		"osgi.http.whiteboard.servlet.pattern=/json-server/*"
	},
	service = Servlet.class
)
public class JSONServerServlet extends HttpServlet {

	@Activate
	protected void activate() throws IOException {
		_objectMapper = new ObjectMapper();

		_objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

		File repositoryDir = new File(
			PropsValues.LIFERAY_HOME, "data/json_server");

		if (!repositoryDir.exists()) {
			repositoryDir.mkdirs();

			return;
		}

		for (File file : repositoryDir.listFiles()) {
			String fileName = file.getName();

			if (fileName.endsWith(".json")) {
				URI uri = file.toURI();

				_load(
					fileName.substring(0, fileName.length() - 5), uri.toURL());
			}
		}
	}

	@Override
	protected void doDelete(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		Request request = new Request(httpServletRequest);

		Object response = _process(HttpMethods.DELETE, request);

		if (response != null) {
			_objectMapper.writeValue(httpServletResponse.getWriter(), response);

			return;
		}

		if (request.getId() == -1) {
			throw new IllegalArgumentException(
				"Missing ID in path " + httpServletRequest.getPathInfo());
		}

		List<Map<String, Object>> models = request.getModels();

		Iterator<Map<String, Object>> iterator = models.iterator();

		while (iterator.hasNext()) {
			Map<String, Object> model = iterator.next();

			if (_getId(model) == request.getId()) {
				iterator.remove();

				return;
			}
		}

		throw new ServletException("Unknown ID " + request.getId());
	}

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		Request request = new Request(httpServletRequest);

		Object response = _process(HttpMethods.GET, request);

		if (response != null) {
			_objectMapper.writeValue(httpServletResponse.getWriter(), response);

			return;
		}

		List<Map<String, Object>> models = request.getModels();

		if (request.getId() > 0) {
			for (Map<String, Object> model : models) {
				if (Objects.equals(_getId(model), request.getId())) {
					_objectMapper.writeValue(
						httpServletResponse.getWriter(), model);

					return;
				}
			}

			throw new ServletException(
				"Unknown ID in path " + httpServletRequest.getPathInfo());
		}

		_objectMapper.writeValue(httpServletResponse.getWriter(), models);
	}

	@Override
	protected void doPost(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		Request request = new Request(httpServletRequest);

		Object response = _process(HttpMethods.POST, request);

		if (response != null) {
			_objectMapper.writeValue(httpServletResponse.getWriter(), response);

			return;
		}

		List<Map<String, Object>> models = request.getModels();

		Map<String, Object> model = request.getParameters();

		_fixId(model, models);

		models.add(model);
	}

	@Override
	protected void doPut(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		Request request = new Request(httpServletRequest);

		Object response = _process(HttpMethods.PUT, request);

		if (response != null) {
			_objectMapper.writeValue(httpServletResponse.getWriter(), response);

			return;
		}

		Map<String, Object> model = request.getParameters();

		Long id = _getId(model);

		if (id == null) {
			throw new ServletException("Missing ID " + model);
		}

		List<Map<String, Object>> models = request.getModels();

		Iterator<Map<String, Object>> iterator = models.iterator();

		while (iterator.hasNext()) {
			Map<String, Object> curModel = iterator.next();

			if (_getId(curModel) == id) {
				iterator.remove();

				models.add(model);

				return;
			}
		}

		throw new ServletException("Unknown ID " + id);
	}

	@Override
	protected void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			super.service(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_log.error(exception);

			httpServletResponse.setStatus(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

			_objectMapper.writeValue(
				httpServletResponse.getWriter(),
				HashMapBuilder.<String, Object>put(
					"code", HttpServletResponse.SC_INTERNAL_SERVER_ERROR
				).put(
					"message", exception.getMessage()
				).build());
		}
	}

	private void _fixId(
		Map<String, Object> model, List<Map<String, Object>> models) {

		try {
			GetterUtil.getLongStrict(String.valueOf(model.get("id")));
		}
		catch (NumberFormatException numberFormatException) {
			long maxId = -1;

			for (Map<String, Object> curModel : models) {
				Long id = _getId(curModel);

				if ((id != null) && (id > maxId)) {
					maxId = id;
				}
			}

			model.put("id", maxId + 1);
		}
	}

	private Long _getId(Map<String, Object> model) {
		Object idObject = model.get("id");

		if (idObject == null) {
			return null;
		}

		return GetterUtil.getLongStrict(String.valueOf(idObject));
	}

	private boolean _isMatch(
		Map<String, Object> actualParameters,
		Map<String, Object> expectedParameters) {

		if (expectedParameters == null) {
			return true;
		}

		if (actualParameters == null) {
			return false;
		}

		for (Map.Entry<String, Object> entry : expectedParameters.entrySet()) {
			if (!Objects.equals(
					entry.getValue(), actualParameters.get(entry.getKey()))) {

				return false;
			}
		}

		return true;
	}

	private void _load(String applicationName, URL url) throws IOException {
		_applicationMaps.put(
			applicationName,
			(Map<String, Object>)_objectMapper.readValue(url, HashMap.class));
	}

	private Object _process(String method, Request request)
		throws IOException, ServletException {

		List<Map<String, Object>> methodCalls = request.getMethodCalls(method);

		if (methodCalls == null) {
			return null;
		}

		Map<String, Object> parameters = request.getParameters();

		for (Map<String, Object> methodCall : methodCalls) {
			if (_isMatch(
					parameters,
					(Map<String, Object>)methodCall.get("request"))) {

				Object response = methodCall.get("response");

				if (response instanceof Map) {
					Map<String, Object> responseMap =
						(Map<String, Object>)response;

					Object exception = responseMap.get("!exception");

					if (exception != null) {
						throw new ServletException(String.valueOf(exception));
					}
				}

				return methodCall.get("response");
			}
		}

		throw new ServerException("Unable to match " + parameters);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JSONServerServlet.class);

	private final Map<String, Map<String, Object>> _applicationMaps =
		new HashMap<>();
	private ObjectMapper _objectMapper;

	private class Request {

		public long getId() {
			return _id;
		}

		public List<Map<String, Object>> getMethodCalls(String method) {
			Object methodCalls = _applicationMap.get(
				StringBundler.concat(_relativePath, StringPool.AT, method));

			if ((methodCalls == null) || !(methodCalls instanceof List)) {
				return null;
			}

			return (List<Map<String, Object>>)methodCalls;
		}

		public List<Map<String, Object>> getModels() throws ServletException {
			Object models = _applicationMap.get(_modelName);

			if ((models == null) || !(models instanceof List)) {
				throw new ServletException("Unknown model name " + _modelName);
			}

			return (List<Map<String, Object>>)models;
		}

		public Map<String, Object> getParameters() {
			return _parameters;
		}

		private Request(HttpServletRequest httpServletRequest)
			throws IOException {

			String path = httpServletRequest.getPathInfo();

			String queryString = httpServletRequest.getQueryString();

			if ((queryString != null) && !queryString.isEmpty()) {
				path = StringBundler.concat(path, "?", queryString);
			}

			List<String> parts = StringUtil.split(path, '/');

			if (parts.isEmpty()) {
				throw new IllegalArgumentException(
					"Missing application name in path " + path);
			}

			if (parts.size() < 2) {
				throw new IllegalArgumentException(
					"Missing model name in path " + path);
			}

			String applicationName = parts.get(0);

			_applicationMap = _applicationMaps.get(applicationName);

			if (_applicationMap == null) {
				throw new IllegalArgumentException(
					"Unknown application name " + applicationName);
			}

			long id = -1;
			String modelName = parts.get(1);

			if (parts.size() > 2) {
				try {
					id = GetterUtil.getLongStrict(parts.get(2));
				}
				catch (IllegalArgumentException illegalArgumentException) {
					modelName = path.substring(applicationName.length() + 2);
				}
			}

			_id = id;
			_modelName = modelName;

			byte[] bytes = StreamUtil.toByteArray(
				httpServletRequest.getInputStream());

			if (bytes.length == 0) {
				_parameters = null;
			}
			else {
				_parameters = _objectMapper.readValue(bytes, HashMap.class);
			}

			_relativePath = StringUtil.merge(
				parts.subList(1, parts.size()), StringPool.SLASH);
		}

		private final Map<String, Object> _applicationMap;
		private final long _id;
		private final String _modelName;
		private final Map<String, Object> _parameters;
		private final String _relativePath;

	}

}