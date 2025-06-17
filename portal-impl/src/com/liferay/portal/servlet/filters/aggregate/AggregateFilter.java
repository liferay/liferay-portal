/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.aggregate;

import com.liferay.petra.concurrent.NoticeableExecutorService;
import com.liferay.petra.concurrent.NoticeableFuture;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.internal.minifier.MinifierThreadLocal;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.servlet.BufferCacheServletResponse;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.PortalWebResourceConstants;
import com.liferay.portal.kernel.servlet.PortalWebResourcesUtil;
import com.liferay.portal.kernel.servlet.RequestDispatcherUtil;
import com.liferay.portal.kernel.servlet.ResourceUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.minifier.MinifierUtil;
import com.liferay.portal.servlet.BrowserSnifferUtil;
import com.liferay.portal.servlet.filters.IgnoreModuleRequestFilter;
import com.liferay.portal.servlet.filters.dynamiccss.DynamicCSSUtil;
import com.liferay.portal.servlet.filters.util.CacheFileNameGenerator;
import com.liferay.portal.util.AggregateUtil;
import com.liferay.portal.util.JavaScriptBundleUtil;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java.net.URL;
import java.net.URLConnection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Eduardo Lundgren
 */
public class AggregateFilter extends IgnoreModuleRequestFilter {

	/**
	 * @see DynamicCSSUtil#propagateQueryString(String, String)
	 */
	public static String aggregateCss(ServletPaths servletPaths, String content)
		throws IOException {

		StringBundler sb = new StringBundler();

		int pos = 0;

		while (true) {
			int commentX = content.indexOf(_CSS_COMMENT_BEGIN, pos);

			int commentY = content.indexOf(
				_CSS_COMMENT_END, commentX + _CSS_COMMENT_BEGIN.length());

			int importX = content.indexOf(_CSS_IMPORT_BEGIN, pos);

			int importY = content.indexOf(
				_CSS_IMPORT_END, importX + _CSS_IMPORT_BEGIN.length());

			if ((importX == -1) || (importY == -1)) {
				sb.append(content.substring(pos));

				break;
			}
			else if ((commentX != -1) && (commentY != -1) &&
					 (commentX < importX) && (commentY > importX)) {

				commentY += _CSS_COMMENT_END.length();

				sb.append(content.substring(pos, commentY));

				pos = commentY;
			}
			else {
				sb.append(content.substring(pos, importX));

				String mediaQuery = StringPool.BLANK;

				int mediaQueryImportX = content.indexOf(
					CharPool.CLOSE_PARENTHESIS,
					importX + _CSS_IMPORT_BEGIN.length());
				int mediaQueryImportY = content.indexOf(
					CharPool.SEMICOLON, importX + _CSS_IMPORT_BEGIN.length());

				String importFileName = null;

				if (importY != mediaQueryImportX) {
					mediaQuery = content.substring(
						mediaQueryImportX + 1, mediaQueryImportY);

					importFileName = content.substring(
						importX + _CSS_IMPORT_BEGIN.length(),
						mediaQueryImportX);
				}
				else {
					importFileName = content.substring(
						importX + _CSS_IMPORT_BEGIN.length(), importY);
				}

				String normalizedImportFileName = importFileName;

				if (!importFileName.isEmpty()) {
					char firstCharacter = importFileName.charAt(0);

					if ((firstCharacter == CharPool.APOSTROPHE) ||
						(firstCharacter == CharPool.QUOTE)) {

						normalizedImportFileName = importFileName.substring(
							1, importFileName.length() - 1);
					}
				}

				String importContent = null;

				if (Validator.isUrl(normalizedImportFileName)) {
					ServletPaths downServletPaths = servletPaths.down(
						normalizedImportFileName);

					importContent = downServletPaths.getContent();

					if (importContent == null) {
						importContent =
							_CSS_IMPORT_BEGIN + importFileName +
								_CSS_IMPORT_END;
					}
				}
				else {
					int queryPos = importFileName.indexOf(CharPool.QUESTION);

					if (queryPos != -1) {
						importFileName = importFileName.substring(0, queryPos);
					}

					ServletPaths importFileServletPaths = servletPaths.down(
						importFileName);

					importContent = importFileServletPaths.getContent();

					if (importContent == null) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								"File " +
									importFileServletPaths.getResourcePath() +
										" does not exist");
						}

						importContent = StringPool.BLANK;
					}

					String importDirName = StringPool.BLANK;

					int slashPos = importFileName.lastIndexOf(CharPool.SLASH);

					if (slashPos != -1) {
						importDirName = importFileName.substring(
							0, slashPos + 1);
					}

					ServletPaths importDirServletPaths = servletPaths.down(
						importDirName);

					importContent = aggregateCss(
						importDirServletPaths, importContent);

					// LEP-7540

					String baseURL = _BASE_URL.concat(
						importDirServletPaths.getResourcePath());

					if (!baseURL.endsWith(StringPool.SLASH)) {
						baseURL += StringPool.SLASH;
					}

					importContent = AggregateUtil.updateRelativeURLs(
						importContent, baseURL);
				}

				if (Validator.isNotNull(mediaQuery)) {
					sb.append(_CSS_MEDIA_QUERY);
					sb.append(CharPool.SPACE);
					sb.append(mediaQuery);
					sb.append(CharPool.OPEN_CURLY_BRACE);
					sb.append(importContent);
					sb.append(CharPool.CLOSE_CURLY_BRACE);

					pos = mediaQueryImportY + 1;
				}
				else {
					sb.append(importContent);

					pos = importY + _CSS_IMPORT_END.length();
				}
			}
		}

		return sb.toString();
	}

	public static String aggregateJavaScript(
		ServletPaths servletPaths, String[] fileNames) {

		StringBundler sb = new StringBundler(fileNames.length * 2);

		for (String fileName : fileNames) {
			ServletPaths fileServletPaths = servletPaths.down(fileName);

			String content = fileServletPaths.getContent();

			if (Validator.isNull(content)) {
				continue;
			}

			sb.append(content);
			sb.append(StringPool.NEW_LINE);
		}

		return sb.toString();
	}

	@Override
	public void init(FilterConfig filterConfig) {
		super.init(filterConfig);

		_servletContext = filterConfig.getServletContext();

		File tempDir = (File)_servletContext.getAttribute(
			JavaConstants.JAKARTA_SERVLET_CONTEXT_TEMPDIR);

		_tempDir = new File(tempDir, _TEMP_DIR);

		_tempDir.mkdirs();
	}

	protected Object getBundleContent(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		String minifierType = ParamUtil.getString(
			httpServletRequest, "minifierType");
		String bundleId = ParamUtil.getString(
			httpServletRequest, "bundleId",
			ParamUtil.getString(httpServletRequest, "minifierBundleId"));

		if (Validator.isNull(minifierType) || Validator.isNull(bundleId) ||
			!ArrayUtil.contains(PropsValues.JAVASCRIPT_BUNDLE_IDS, bundleId)) {

			return null;
		}

		String bundleDirName = PropsUtil.get(
			PropsKeys.JAVASCRIPT_BUNDLE_DIR, new Filter(bundleId));

		ServletContext jsServletContext =
			PortalWebResourcesUtil.getServletContext(
				PortalWebResourceConstants.RESOURCE_TYPE_JS);

		URL bundleDirURL = jsServletContext.getResource(bundleDirName);

		if (bundleDirURL == null) {
			return null;
		}

		String cacheFileName = bundleId;

		File cacheFile = new File(_tempDir, cacheFileName);

		if (cacheFile.exists()) {
			long lastModified = PortalWebResourcesUtil.getLastModified(
				PortalWebResourceConstants.RESOURCE_TYPE_JS);

			long fileLastModifiedTime = -1;

			try (Reader reader = new FileReader(cacheFile);
				UnsyncBufferedReader unsyncBufferedReader =
					new UnsyncBufferedReader(reader)) {

				String line = unsyncBufferedReader.readLine();

				if ((line != null) &&
					line.startsWith(StringPool.DOUBLE_SLASH)) {

					fileLastModifiedTime = GetterUtil.getLong(
						line.substring(2), -1);
				}
			}

			if (lastModified == fileLastModifiedTime) {
				httpServletResponse.setContentType(
					ContentTypes.TEXT_JAVASCRIPT);

				return cacheFile;
			}
		}

		if (_log.isInfoEnabled()) {
			_log.info("Aggregating JavaScript bundle " + bundleId);
		}

		String content = null;

		String[] fileNames = JavaScriptBundleUtil.getFileNames(bundleId);

		if (fileNames.length == 0) {
			content = StringPool.BLANK;
		}
		else {
			content = aggregateJavaScript(
				new ServletPaths(jsServletContext, bundleDirName), fileNames);
		}

		content = StringBundler.concat(
			StringPool.DOUBLE_SLASH,
			PortalWebResourcesUtil.getLastModified(
				PortalWebResourceConstants.RESOURCE_TYPE_JS),
			StringPool.NEW_LINE, content);

		httpServletResponse.setContentType(ContentTypes.TEXT_JAVASCRIPT);

		FileUtil.write(cacheFile, content);

		return content;
	}

	protected String getCacheFileName(HttpServletRequest httpServletRequest) {
		return CacheFileNameGenerator.getCacheFileName(
			httpServletRequest, AggregateFilter.class.getName());
	}

	protected Object getContent(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		String minifierType = ParamUtil.getString(
			httpServletRequest, "minifierType");
		String minifierBundleId = ParamUtil.getString(
			httpServletRequest, "minifierBundleId");
		String minifierBundleDirName = ParamUtil.getString(
			httpServletRequest, "minifierBundleDir");

		if (Validator.isNull(minifierType) ||
			Validator.isNotNull(minifierBundleId) ||
			Validator.isNotNull(minifierBundleDirName)) {

			return null;
		}

		String resourcePath = httpServletRequest.getRequestURI();

		String contextPath = httpServletRequest.getContextPath();

		if (!contextPath.equals(StringPool.SLASH)) {
			resourcePath = resourcePath.substring(contextPath.length());
		}

		URL resourceURL = ResourceUtil.getResourceURL(
			resourcePath, httpServletRequest.getRequestURI(), _servletContext);

		if (resourceURL == null) {
			return null;
		}

		String cacheCommonFileName = getCacheFileName(httpServletRequest);

		File cacheContentTypeFile = new File(
			_tempDir, cacheCommonFileName + "_E_CTYPE");
		File cacheDataFile = new File(
			_tempDir, cacheCommonFileName + "_E_DATA");

		if (cacheDataFile.exists()) {
			long fileLastModifiedTime = -1;

			try (Reader reader = new FileReader(cacheDataFile);
				UnsyncBufferedReader unsyncBufferedReader =
					new UnsyncBufferedReader(reader)) {

				String line = unsyncBufferedReader.readLine();

				if ((line != null) && line.startsWith(_CSS_COMMENT_BEGIN) &&
					line.endsWith(_CSS_COMMENT_END)) {

					fileLastModifiedTime = GetterUtil.getLong(
						line.substring(2, line.length() - 2), -1);
				}
			}

			if (URLUtil.getLastModifiedTime(resourceURL) ==
					fileLastModifiedTime) {

				if (cacheContentTypeFile.exists()) {
					httpServletResponse.setContentType(
						FileUtil.read(cacheContentTypeFile));
				}
				else if (resourcePath.endsWith(_CSS_EXTENSION)) {
					httpServletResponse.setContentType(
						ContentTypes.TEXT_CSS_UTF8);
				}
				else if (resourcePath.endsWith(_JAVASCRIPT_EXTENSION)) {
					httpServletResponse.setContentType(
						ContentTypes.TEXT_JAVASCRIPT);
				}

				return cacheDataFile;
			}
		}

		try (Closeable closeable = MinifierThreadLocal.disable()) {
			String content = null;

			if (resourcePath.endsWith(_CSS_EXTENSION)) {
				if (_log.isInfoEnabled()) {
					_log.info("Minifying CSS " + resourcePath);
				}

				content = getCssContent(
					httpServletRequest, httpServletResponse, resourcePath);

				httpServletResponse.setContentType(ContentTypes.TEXT_CSS_UTF8);

				FileUtil.write(
					cacheContentTypeFile, ContentTypes.TEXT_CSS_UTF8);
			}
			else if (resourcePath.endsWith(_JAVASCRIPT_EXTENSION)) {
				if (_log.isInfoEnabled()) {
					_log.info("Minifying JavaScript " + resourcePath);
				}

				content = _readResource(
					httpServletRequest, httpServletResponse, resourcePath);

				httpServletResponse.setContentType(
					ContentTypes.TEXT_JAVASCRIPT);

				FileUtil.write(
					cacheContentTypeFile, ContentTypes.TEXT_JAVASCRIPT);
			}
			else if (resourcePath.endsWith(_JSP_EXTENSION)) {
				if (_log.isInfoEnabled()) {
					_log.info("Minifying JSP " + resourcePath);
				}

				BufferCacheServletResponse bufferCacheServletResponse =
					new BufferCacheServletResponse(httpServletResponse);

				processFilter(
					AggregateFilter.class.getName(), httpServletRequest,
					bufferCacheServletResponse, filterChain);

				content = bufferCacheServletResponse.getString();

				if (minifierType.equals("css")) {
					content = getCssContent(
						httpServletRequest, httpServletResponse, resourcePath,
						content);
				}

				FileUtil.write(
					cacheContentTypeFile,
					bufferCacheServletResponse.getContentType());
			}
			else {
				return null;
			}

			content = StringBundler.concat(
				_CSS_COMMENT_BEGIN, URLUtil.getLastModifiedTime(resourceURL),
				_CSS_COMMENT_END, StringPool.NEW_LINE, content);

			FileUtil.write(cacheDataFile, content);

			if (!PropsValues.MINIFIER_ENABLED) {
				return content;
			}

			httpServletResponse.setHeader(
				HttpHeaders.CACHE_CONTROL, HttpHeaders.PRAGMA_NO_CACHE_VALUE);

			String finalContent = content;

			NoticeableFuture<String> noticeableFuture =
				_noticeableFutures.computeIfAbsent(
					cacheCommonFileName,
					key -> {
						PortalExecutorManager portalExecutorManager =
							_portalExecutorManagerSnapshot.get();

						NoticeableExecutorService noticeableExecutorService =
							portalExecutorManager.getPortalExecutor(
								AggregateFilter.class.getName());

						return noticeableExecutorService.submit(
							() -> {
								String minifiedContent = finalContent;

								if (minifierType.equals("css")) {
									minifiedContent = MinifierUtil.minifyCss(
										finalContent);
								}

								minifiedContent = StringBundler.concat(
									_CSS_COMMENT_BEGIN,
									URLUtil.getLastModifiedTime(resourceURL),
									_CSS_COMMENT_END, StringPool.NEW_LINE,
									minifiedContent);

								File tempFile = FileUtil.createTempFile();

								FileUtil.write(tempFile, minifiedContent);

								FileUtil.move(tempFile, cacheDataFile);

								return minifiedContent;
							});
					});

			noticeableFuture.addFutureListener(
				future -> _noticeableFutures.remove(cacheCommonFileName));

			return content;
		}
	}

	protected String getCssContent(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		ServletContext cssServletContext, String resourcePath, String content) {

		try {
			content = DynamicCSSUtil.replaceToken(
				cssServletContext, httpServletRequest, content);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to replace tokens in CSS " + resourcePath, exception);

			if (_log.isDebugEnabled()) {
				_log.debug(content);
			}

			httpServletResponse.setHeader(
				HttpHeaders.CACHE_CONTROL,
				HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE);
		}

		String browserId = ParamUtil.getString(httpServletRequest, "browserId");

		if (!browserId.equals(BrowserSnifferUtil.BROWSER_ID_IE)) {
			Matcher matcher = _pattern.matcher(content);

			content = matcher.replaceAll(StringPool.BLANK);
		}

		return MinifierUtil.minifyCss(content);
	}

	protected String getCssContent(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String resourcePath)
		throws Exception {

		String resourcePathRoot = null;

		String requestURI = httpServletRequest.getRequestURI();

		ServletContext cssServletContext = ResourceUtil.getPathServletContext(
			resourcePath, requestURI, _servletContext);

		if (PortalWebResourcesUtil.hasContextPath(requestURI)) {
			resourcePathRoot = PortalWebResourcesUtil.stripContextPath(
				cssServletContext, resourcePath);

			resourcePathRoot = ServletPaths.getParentPath(resourcePathRoot);

			if (resourcePathRoot.equals(StringPool.BLANK)) {
				resourcePathRoot = "/";
			}
		}
		else {
			resourcePathRoot = ServletPaths.getParentPath(resourcePath);
		}

		String content = _readResource(
			httpServletRequest, httpServletResponse, resourcePath);

		content = aggregateCss(
			new ServletPaths(cssServletContext, resourcePathRoot), content);

		return getCssContent(
			httpServletRequest, httpServletResponse, cssServletContext,
			resourcePath, content);
	}

	protected String getCssContent(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String resourcePath,
		String content) {

		try {
			ServletContext cssServletContext =
				ResourceUtil.getPathServletContext(
					resourcePath, httpServletRequest.getRequestURI(),
					_servletContext);

			return getCssContent(
				httpServletRequest, httpServletResponse, cssServletContext,
				resourcePath, content);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to detect servlet context " + resourcePath, exception);

			if (_log.isDebugEnabled()) {
				_log.debug(content);
			}

			httpServletResponse.setHeader(
				HttpHeaders.CACHE_CONTROL,
				HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE);

			return content;
		}
	}

	@Override
	protected boolean isModuleRequest(HttpServletRequest httpServletRequest) {
		if (PortalWebResourcesUtil.hasContextPath(
				httpServletRequest.getRequestURI())) {

			return false;
		}

		return super.isModuleRequest(httpServletRequest);
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		Object minifiedContent = getContent(
			httpServletRequest, httpServletResponse, filterChain);

		if (minifiedContent == null) {
			minifiedContent = getBundleContent(
				httpServletRequest, httpServletResponse);
		}

		if (minifiedContent == null) {
			processFilter(
				AggregateFilter.class.getName(), httpServletRequest,
				httpServletResponse, filterChain);
		}
		else {
			if (minifiedContent instanceof File) {
				ServletResponseUtil.write(
					httpServletResponse, (File)minifiedContent);
			}
			else if (minifiedContent instanceof String) {
				ServletResponseUtil.write(
					httpServletResponse, (String)minifiedContent);
			}
		}
	}

	private String _readResource(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String resourcePath)
		throws Exception {

		URL url = _servletContext.getResource(resourcePath);

		if (url == null) {
			ObjectValuePair<String, Long> objectValuePair =
				RequestDispatcherUtil.
					getContentAndLastModifiedTimeObjectValuePair(
						httpServletRequest.getRequestDispatcher(resourcePath),
						httpServletRequest, httpServletResponse);

			return objectValuePair.getKey();
		}

		URLConnection urlConnection = url.openConnection();

		return StringUtil.read(urlConnection.getInputStream());
	}

	private static final String _BASE_URL = "@base_url@";

	private static final String _CSS_COMMENT_BEGIN = "/*";

	private static final String _CSS_COMMENT_END = "*/";

	private static final String _CSS_EXTENSION = ".css";

	private static final String _CSS_IMPORT_BEGIN = "@import url(";

	private static final String _CSS_IMPORT_END = ");";

	private static final String _CSS_MEDIA_QUERY = "@media";

	private static final String _JAVASCRIPT_EXTENSION = ".js";

	private static final String _JSP_EXTENSION = ".jsp";

	private static final String _TEMP_DIR = "aggregate";

	private static final Log _log = LogFactoryUtil.getLog(
		AggregateFilter.class);

	private static final Pattern _pattern = Pattern.compile(
		"^(\\.ie|\\.js\\.ie)([^}]*)}", Pattern.MULTILINE);
	private static final Snapshot<PortalExecutorManager>
		_portalExecutorManagerSnapshot = new Snapshot<>(
			AggregateFilter.class, PortalExecutorManager.class);

	private final Map<String, NoticeableFuture<String>> _noticeableFutures =
		new ConcurrentHashMap<>();
	private ServletContext _servletContext;
	private File _tempDir;

}