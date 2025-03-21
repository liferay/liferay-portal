/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.frontend.source.map.FrontendSourceMapUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.servlet.BufferCacheServletResponse;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.RequestDispatcherUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.minifier.MinifierUtil;
import com.liferay.portal.servlet.filters.dynamiccss.DynamicCSSUtil;
import com.liferay.portal.util.AggregateUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.documentlibrary.constants.DLFriendlyURLConstants;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eduardo Lundgren
 * @author Edward Han
 * @author Zsigmond Rab
 * @author Raymond Augé
 */
public class ComboServlet extends HttpServlet {

	public static void clearCache() {
		_bytesArrayPortalCache.removeAll();
		_fileContentBagPortalCache.removeAll();
	}

	@Override
	public void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			doService(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_log.error(exception);

			PortalUtil.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception,
				httpServletRequest, httpServletResponse);
		}
	}

	protected static String getModulePortletId(String modulePath) {
		int index = modulePath.indexOf(CharPool.COLON);

		if (index > 0) {
			return modulePath.substring(0, index);
		}

		return PortletKeys.PORTAL;
	}

	protected static String getResourcePath(String modulePath) {
		int index = modulePath.indexOf(CharPool.COLON);

		if (index > 0) {
			return HttpComponentsUtil.removePathParameters(
				modulePath.substring(index + 1));
		}

		return HttpComponentsUtil.removePathParameters(modulePath);
	}

	protected void doService(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		Set<String> modulePathsSet = new LinkedHashSet<>();

		Map<String, String[]> parameterMap = HttpComponentsUtil.getParameterMap(
			httpServletRequest.getQueryString());

		Enumeration<String> enumeration = Collections.enumeration(
			parameterMap.keySet());

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			if (_protectedParameters.contains(name)) {
				continue;
			}

			name = HttpComponentsUtil.decodePath(name);

			String modulePortletId = StringPool.BLANK;

			int index = name.indexOf(CharPool.COLON);

			if (index > 0) {
				modulePortletId = name.substring(0, index + 1);

				name = name.substring(index + 1);
			}

			String pathProxy = PortalUtil.getPathProxy();

			if (name.startsWith(pathProxy)) {
				name = name.replaceFirst(pathProxy, StringPool.BLANK);
			}

			if (index < 0) {
				ServletContext servletContext = getServletContext();

				String contextPath = servletContext.getContextPath();

				if (name.startsWith(contextPath)) {
					name = name.replaceFirst(contextPath, StringPool.BLANK);
				}
			}
			else {
				name = modulePortletId.concat(name);
			}

			name = _canonicalizePath(name);

			if (Validator.isNull(name)) {
				continue;
			}

			modulePathsSet.add(name);
		}

		if (modulePathsSet.isEmpty()) {
			PortalUtil.sendError(
				HttpServletResponse.SC_NOT_FOUND,
				new NoSuchLayoutException(
					"Query string translates to an empty module paths set"),
				httpServletRequest, httpServletResponse);

			return;
		}

		if ((PropsValues.COMBO_MAX_FILES > 0) &&
			(modulePathsSet.size() > PropsValues.COMBO_MAX_FILES)) {

			httpServletResponse.setHeader(
				HttpHeaders.CACHE_CONTROL,
				HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE);
			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			if (_log.isWarnEnabled()) {
				_log.warn("Request exceeds maximum number of files");
			}

			return;
		}

		String[] modulePaths = modulePathsSet.toArray(new String[0]);

		String extension = StringPool.BLANK;

		for (String modulePath : modulePaths) {
			String pathExtension = _getModulePathExtension(modulePath);

			if (Validator.isNull(pathExtension)) {
				continue;
			}

			if (Validator.isNull(extension)) {
				extension = pathExtension;
			}

			if (!modulePath.startsWith(
					DLFriendlyURLConstants.PATH_PREFIX_DOCUMENT) &&
				!extension.equals(pathExtension)) {

				httpServletResponse.setHeader(
					HttpHeaders.CACHE_CONTROL,
					HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE);
				httpServletResponse.setStatus(
					HttpServletResponse.SC_BAD_REQUEST);

				return;
			}
		}

		String minifierType = ParamUtil.getString(
			httpServletRequest, "minifierType");

		if (Validator.isNull(minifierType)) {
			minifierType = "js";

			if (StringUtil.equalsIgnoreCase(extension, _CSS_EXTENSION)) {
				minifierType = "css";
			}
		}

		if (!minifierType.equals("css") && !minifierType.equals("js")) {
			minifierType = "js";
		}

		String modulePathsString = null;

		byte[][] bytesArray = null;

		if (!PropsValues.COMBO_CHECK_TIMESTAMP) {
			modulePathsString = Arrays.toString(modulePaths);

			modulePathsString +=
				StringPool.POUND +
					LanguageUtil.getLanguageId(httpServletRequest);

			bytesArray = _bytesArrayPortalCache.get(modulePathsString);
		}

		if (bytesArray == null) {
			bytesArray = new byte[modulePaths.length][];

			boolean cacheEnabled = true;

			for (int i = 0; i < modulePaths.length; i++) {
				String modulePath = modulePaths[i];

				if (!validateModuleExtension(modulePath)) {
					httpServletResponse.setHeader(
						HttpHeaders.CACHE_CONTROL,
						HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE);
					httpServletResponse.setStatus(
						HttpServletResponse.SC_BAD_REQUEST);

					return;
				}

				byte[] bytes = new byte[0];

				if (Validator.isNotNull(modulePath)) {
					RequestDispatcher requestDispatcher =
						getResourceRequestDispatcher(
							httpServletRequest, httpServletResponse,
							modulePath);

					if (requestDispatcher == null) {
						httpServletResponse.setHeader(
							HttpHeaders.CACHE_CONTROL,
							HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE);
						httpServletResponse.setStatus(
							HttpServletResponse.SC_NOT_FOUND);

						return;
					}

					bytes = getResourceContent(
						requestDispatcher, httpServletRequest,
						httpServletResponse, modulePath, minifierType);
				}

				if (bytes == null) {
					cacheEnabled = false;

					bytes = _EMPTY_FILE_CONTENT_BAG._fileContent;

					httpServletResponse.setHeader(
						HttpHeaders.CACHE_CONTROL, "max-age=1, no-cache");
				}
				else if ((PropsValues.COMBO_ALLOWED_FILE_MAX_SIZE > 0) &&
						 (bytes.length >
							 PropsValues.COMBO_ALLOWED_FILE_MAX_SIZE)) {

					cacheEnabled = false;
				}

				bytesArray[i] = bytes;
			}

			if (cacheEnabled && (modulePathsString != null) &&
				!PropsValues.COMBO_CHECK_TIMESTAMP) {

				_bytesArrayPortalCache.put(modulePathsString, bytesArray);
			}
		}

		String contentType = ContentTypes.TEXT_JAVASCRIPT;

		if (StringUtil.equalsIgnoreCase(minifierType, "css")) {
			contentType = ContentTypes.TEXT_CSS_UTF8;
		}

		httpServletResponse.setContentType(contentType);

		ServletResponseUtil.write(httpServletResponse, bytesArray);
	}

	protected byte[] getResourceContent(
			RequestDispatcher requestDispatcher,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String modulePath,
			String minifierType)
		throws Exception {

		String resourcePath = getResourcePath(modulePath);

		String portletId = getModulePortletId(modulePath);

		Portlet portlet = PortletLocalServiceUtil.getPortletById(portletId);

		if (!resourcePath.startsWith(portlet.getContextPath())) {
			resourcePath = portlet.getContextPath() + resourcePath;
		}

		String fileContentKey = StringBundler.concat(
			resourcePath, StringPool.QUESTION, minifierType, "&languageId=",
			ParamUtil.getString(httpServletRequest, "languageId"));

		FileContentBag fileContentBag = _fileContentBagPortalCache.get(
			fileContentKey);

		if ((fileContentBag != null) && !PropsValues.COMBO_CHECK_TIMESTAMP) {
			return fileContentBag._fileContent;
		}

		if ((fileContentBag != null) && PropsValues.COMBO_CHECK_TIMESTAMP) {
			long elapsedTime =
				System.currentTimeMillis() - fileContentBag._lastModified;

			if ((requestDispatcher != null) &&
				(elapsedTime <= PropsValues.COMBO_CHECK_TIMESTAMP_INTERVAL)) {

				long lastModified = RequestDispatcherUtil.getLastModifiedTime(
					requestDispatcher, httpServletRequest, httpServletResponse);

				if (lastModified == fileContentBag._lastModified) {
					return fileContentBag._fileContent;
				}
			}

			_fileContentBagPortalCache.remove(fileContentKey);
		}

		if (requestDispatcher == null) {
			fileContentBag = _EMPTY_FILE_CONTENT_BAG;
		}
		else {
			BufferCacheServletResponse bufferCacheServletResponse =
				RequestDispatcherUtil.getBufferCacheServletResponse(
					requestDispatcher, httpServletRequest, httpServletResponse);

			String cacheControl = GetterUtil.getString(
				bufferCacheServletResponse.getHeader("Cache-Control"));
			String contentType = GetterUtil.getString(
				bufferCacheServletResponse.getContentType());
			int status = bufferCacheServletResponse.getStatus();

			if (status != HttpServletResponse.SC_OK) {
				_log.error(
					StringBundler.concat(
						"Skip ", modulePath, " because it returns HTTP status ",
						status));

				return null;
			}
			else if (!contentType.startsWith("application/javascript") &&
					 !contentType.startsWith("text/css") &&
					 !contentType.startsWith("text/javascript")) {

				_log.error(
					"Skip " + modulePath +
						" because its content type is not CSS or JavaScript");

				return null;
			}
			else if (cacheControl.contains("no-cache") ||
					 cacheControl.contains("no-store")) {

				_log.error(
					"Skip " + modulePath +
						" because it sent no-cache or no-store headers");

				return null;
			}

			String stringFileContent = bufferCacheServletResponse.getString();

			if (_textReplacerBiFunction != null) {
				stringFileContent = _textReplacerBiFunction.apply(
					"ComboServlet#" + modulePath, stringFileContent);
			}

			if (!StringUtil.endsWith(resourcePath, _CSS_MINIFIED_DASH_SUFFIX) &&
				!StringUtil.endsWith(resourcePath, _CSS_MINIFIED_DOT_SUFFIX) &&
				!StringUtil.endsWith(
					resourcePath, _JAVASCRIPT_MINIFIED_DASH_SUFFIX) &&
				!StringUtil.endsWith(
					resourcePath, _JAVASCRIPT_MINIFIED_DOT_SUFFIX)) {

				if (minifierType.equals("css")) {
					try {
						stringFileContent = DynamicCSSUtil.replaceToken(
							getServletContext(), httpServletRequest,
							stringFileContent);
					}
					catch (Exception exception) {
						_log.error(
							"Unable to replace tokens in CSS " + resourcePath,
							exception);

						if (_log.isDebugEnabled()) {
							_log.debug(stringFileContent);
						}

						httpServletResponse.setHeader(
							HttpHeaders.CACHE_CONTROL,
							HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE);
					}

					String baseURL = StringPool.BLANK;

					int slashIndex = resourcePath.lastIndexOf(CharPool.SLASH);

					if (slashIndex != -1) {
						baseURL = resourcePath.substring(0, slashIndex + 1);
					}

					baseURL = PortalUtil.getPathProxy() + baseURL;

					if (StringUtil.contains(
							stringFileContent, _CSS_CHARSET_UTF_8,
							StringPool.BLANK)) {

						stringFileContent = StringUtil.removeSubstring(
							stringFileContent, _CSS_CHARSET_UTF_8);
					}

					stringFileContent = AggregateUtil.updateRelativeURLs(
						stringFileContent, baseURL);

					stringFileContent =
						FrontendSourceMapUtil.stripCSSSourceMapping(
							stringFileContent);

					stringFileContent = MinifierUtil.minifyCss(
						stringFileContent);
				}
				else if (minifierType.equals("js")) {
					Matcher matcher = _importModulePattern.matcher(
						stringFileContent);

					if (matcher.matches()) {
						stringFileContent =
							matcher.group(1) + "../o/" + matcher.group(3);
					}

					stringFileContent =
						FrontendSourceMapUtil.stripJSSourceMapping(
							stringFileContent);

					stringFileContent = stringFileContent.concat(
						StringPool.NEW_LINE);
				}
			}
			else if (StringUtil.endsWith(
						resourcePath, _JAVASCRIPT_MINIFIED_DASH_SUFFIX) ||
					 StringUtil.endsWith(
						 resourcePath, _JAVASCRIPT_MINIFIED_DOT_SUFFIX)) {

				stringFileContent = stringFileContent.concat(
					StringPool.NEW_LINE);
			}

			fileContentBag = new FileContentBag(
				stringFileContent.getBytes(StringPool.UTF8),
				GetterUtil.getLong(
					bufferCacheServletResponse.getHeader(
						HttpHeaders.LAST_MODIFIED),
					-1));
		}

		if (PropsValues.COMBO_CHECK_TIMESTAMP) {
			int timeToLive =
				(int)(PropsValues.COMBO_CHECK_TIMESTAMP_INTERVAL / Time.SECOND);

			_fileContentBagPortalCache.put(
				fileContentKey, fileContentBag, timeToLive);
		}

		return fileContentBag._fileContent;
	}

	protected RequestDispatcher getResourceRequestDispatcher(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String modulePath)
		throws Exception {

		String portletId = getModulePortletId(modulePath);

		Portlet portlet = PortletLocalServiceUtil.getPortletById(portletId);

		if ((portlet == null) || portlet.isUndeployedPortlet()) {
			return null;
		}

		String resourcePath = getResourcePath(modulePath);

		if (!StringUtil.startsWith(resourcePath, CharPool.SLASH) ||
			!PortalUtil.isValidResourceId(resourcePath)) {

			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Invalid resource ", httpServletRequest.getRequestURL(),
						"?", httpServletRequest.getQueryString()));
			}

			return null;
		}

		PortletApp portletApp = portlet.getPortletApp();

		ServletContext servletContext = portletApp.getServletContext();

		return servletContext.getRequestDispatcher(resourcePath);
	}

	protected boolean validateModuleExtension(String moduleName)
		throws Exception {

		moduleName = getResourcePath(moduleName);

		int index = moduleName.indexOf(CharPool.QUESTION);

		if (index != -1) {
			moduleName = moduleName.substring(0, index);
		}

		if (moduleName.startsWith(
				DLFriendlyURLConstants.PATH_PREFIX_DOCUMENT)) {

			return true;
		}

		boolean validModuleExtension = false;

		String[] fileExtensions = PrefsPropsUtil.getStringArray(
			PropsKeys.COMBO_ALLOWED_FILE_EXTENSIONS, StringPool.COMMA);

		for (String fileExtension : fileExtensions) {
			if (StringPool.STAR.equals(fileExtension) ||
				StringUtil.endsWith(moduleName, fileExtension)) {

				validModuleExtension = true;

				break;
			}
		}

		return validModuleExtension;
	}

	private String _canonicalizePath(String path) {
		if (!path.contains(StringPool.PERIOD)) {
			return path;
		}

		List<String> canonicalParts = new ArrayList<>();

		String[] parts = StringUtil.split(path, StringPool.SLASH);

		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];

			if (((i != 0) && Validator.isBlank(part)) ||
				part.equals(StringPool.PERIOD)) {

				continue;
			}

			if (part.equals(StringPool.DOUBLE_PERIOD)) {
				if (canonicalParts.isEmpty()) {
					return null;
				}

				canonicalParts.remove(canonicalParts.size() - 1);

				continue;
			}

			canonicalParts.add(part);
		}

		return StringUtil.merge(canonicalParts, StringPool.SLASH);
	}

	private String _getModulePathExtension(String modulePath) {
		String resourcePath = getResourcePath(modulePath);

		int index = resourcePath.indexOf(CharPool.QUESTION);

		if (index != -1) {
			resourcePath = resourcePath.substring(0, index);
		}

		return FileUtil.getExtension(resourcePath);
	}

	private static final String _CSS_CHARSET_UTF_8 = "@charset \"UTF-8\";";

	private static final String _CSS_EXTENSION = "css";

	private static final String _CSS_MINIFIED_DASH_SUFFIX = "-min.css";

	private static final String _CSS_MINIFIED_DOT_SUFFIX = ".min.css";

	private static final FileContentBag _EMPTY_FILE_CONTENT_BAG =
		new FileContentBag(new byte[0], 0);

	private static final String _JAVASCRIPT_MINIFIED_DASH_SUFFIX = "-min.js";

	private static final String _JAVASCRIPT_MINIFIED_DOT_SUFFIX = ".min.js";

	private static final Log _log = LogFactoryUtil.getLog(ComboServlet.class);

	private static final PortalCache<String, byte[][]> _bytesArrayPortalCache =
		PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.SINGLE_VM, ComboServlet.class.getName());
	private static final PortalCache<String, FileContentBag>
		_fileContentBagPortalCache = PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.SINGLE_VM, FileContentBag.class.getName());
	private static final Pattern _importModulePattern = Pattern.compile(
		"(import\\s*\\*\\s*as\\s*\\w*\\s*from\\s*[\"'])((?:\\.\\./)+)(.*)",
		Pattern.DOTALL);
	private static final BiFunction<String, String, String>
		_textReplacerBiFunction;

	static {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();

		Object instance = null;

		try {
			Class<?> clazz = classLoader.loadClass(
				"com.liferay.portal.tools.jakarta.ee.transformer.function." +
					"TextReplacerBiFunction");

			instance = clazz.newInstance();
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			if (!(reflectiveOperationException instanceof
					ClassNotFoundException)) {

				throw new ExceptionInInitializerError(
					reflectiveOperationException);
			}
		}

		_textReplacerBiFunction = (BiFunction<String, String, String>)instance;
	}

	private final Set<String> _protectedParameters = SetUtil.fromArray(
		"browserId", "minifierType", "languageId", "t", "themeId", "zx");

	private static class FileContentBag implements Serializable {

		public FileContentBag(byte[] fileContent, long lastModified) {
			_fileContent = fileContent;
			_lastModified = lastModified;
		}

		private final byte[] _fileContent;
		private final long _lastModified;

	}

}