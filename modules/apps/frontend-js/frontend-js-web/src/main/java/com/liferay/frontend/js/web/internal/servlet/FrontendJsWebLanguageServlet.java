/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.servlet;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLUtil;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.net.URL;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=Language Resources Servlet",
		"osgi.http.whiteboard.servlet.pattern=/js/language/*",
		"service.ranking:Integer=" + (Integer.MAX_VALUE - 1000)
	},
	service = Servlet.class
)
public class FrontendJsWebLanguageServlet extends HttpServlet {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_eTags.clear();

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ServletContext.class, null,
			(serviceReference, emitter) -> {
				ServletContext servletContext = bundleContext.getService(
					serviceReference);

				try {
					emitter.emit(servletContext.getContextPath());
				}
				finally {
					bundleContext.ungetService(serviceReference);
				}
			});
	}

	@Deactivate
	protected void deactivate() {
		_eTags.clear();

		_serviceTrackerMap.close();

		_serviceTrackerMap = null;
	}

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		String pathInfo = httpServletRequest.getPathInfo();

		// Check if path is valid

		String[] parts = pathInfo.split(StringPool.SLASH);

		if ((parts.length != 4) || !parts[3].equals("all.js")) {
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		// Check if browser cache can be used

		String ifNoneMatch = httpServletRequest.getHeader(
			HttpHeaders.IF_NONE_MATCH);

		if (ifNoneMatch != null) {
			String eTag = _eTags.get(pathInfo);

			if ((eTag != null) && eTag.equals(ifNoneMatch)) {
				httpServletResponse.setStatus(
					HttpServletResponse.SC_NOT_MODIFIED);
				httpServletResponse.setContentLength(0);

				return;
			}
		}

		// Check if servlet context exists

		String webContextPath = parts[2];

		ServletContext servletContext = _serviceTrackerMap.getService(
			Portal.PATH_MODULE + StringPool.SLASH + webContextPath);

		if (servletContext == null) {
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		// Send response

		Locale locale = LocaleUtil.fromLanguageId(parts[1]);

		String content = _getContent(locale, servletContext);

		if (content == null) {
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		String etag =
			StringPool.QUOTE + DigesterUtil.digestBase64("SHA-1", content) +
				StringPool.QUOTE;

		_eTags.put(pathInfo, etag);

		httpServletResponse.setCharacterEncoding(StringPool.UTF8);
		httpServletResponse.setContentType(ContentTypes.TEXT_JAVASCRIPT_UTF8);
		httpServletResponse.setHeader(HttpHeaders.ETAG, etag);

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.write(content);
	}

	private static String _loadTemplate(String name) {
		try (InputStream inputStream =
				FrontendJsWebLanguageServlet.class.getResourceAsStream(
					"dependencies/" + name)) {

			return StringUtil.read(inputStream);
		}
		catch (Exception exception) {
			_log.error("Unable to read template " + name, exception);
		}

		return StringPool.BLANK;
	}

	private String _getContent(Locale locale, ServletContext servletContext)
		throws IOException {

		JSONArray languageKeysJSONArray = _getLanguageKeysJSONArray(
			servletContext);

		if (languageKeysJSONArray == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < languageKeysJSONArray.length(); i++) {
			String key = languageKeysJSONArray.getString(i);

			String label = _language.get(locale, key);

			sb.append(StringPool.APOSTROPHE);
			sb.append(key.replaceAll("'", "\\\\'"));
			sb.append("':'");
			sb.append(label.replaceAll("'", "\\\\'"));
			sb.append("',\n");
		}

		return StringUtil.replace(
			_TPL_JAVA_SCRIPT, new String[] {"[$LABELS$]"},
			new String[] {sb.toString()});
	}

	private JSONArray _getLanguageKeysJSONArray(ServletContext servletContext)
		throws IOException {

		URL url = servletContext.getResource("/language.json");

		if (url == null) {
			return null;
		}

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				URLUtil.toString(url));

			return jsonObject.getJSONArray("keys");
		}
		catch (JSONException jsonException) {
			throw new IOException(
				"Invalid language JSON file " + url, jsonException);
		}
	}

	private static final String _TPL_JAVA_SCRIPT;

	private static final Log _log = LogFactoryUtil.getLog(
		FrontendJsWebLanguageServlet.class);

	static {
		_TPL_JAVA_SCRIPT = _loadTemplate("all.js.tpl");
	}

	private final ConcurrentHashMap<String, String> _eTags =
		new ConcurrentHashMap<>();

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	private ServiceTrackerMap<String, ServletContext> _serviceTrackerMap;

}