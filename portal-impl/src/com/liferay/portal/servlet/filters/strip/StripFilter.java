/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.strip;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.cache.key.CacheKeyGenerator;
import com.liferay.portal.kernel.cache.key.CacheKeyGeneratorUtil;
import com.liferay.portal.kernel.io.OutputStreamWriter;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.servlet.BufferCacheServletResponse;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.KMPSearch;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.minifier.MinifierUtil;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Writer;

import java.nio.CharBuffer;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Shuyang Zhou
 */
public class StripFilter extends BasePortalFilter {

	public static final String SKIP_FILTER =
		StripFilter.class.getName() + "#SKIP_FILTER";

	public StripFilter() {
		if (PropsValues.MINIFIER_INLINE_CONTENT_CACHE_ENABLED) {
			_minifierPortalCache = PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.SINGLE_VM, StripFilter.class.getName());
		}
		else {
			_minifierPortalCache = null;
		}
	}

	@Override
	public void init(FilterConfig filterConfig) {
		super.init(filterConfig);

		for (String ignorePath : PropsValues.STRIP_IGNORE_PATHS) {
			_ignorePaths.add(ignorePath);
		}
	}

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (isStrip(httpServletRequest) && !isInclude(httpServletRequest) &&
			!isAlreadyFiltered(httpServletRequest)) {

			return true;
		}

		return false;
	}

	protected void extractAndMinifyCSS(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, CharBuffer charBuffer,
			Writer writer, char[] openTag)
		throws Exception {

		outputOpenTag(charBuffer, writer, openTag);

		int length = KMPSearch.search(
			charBuffer, _MARKER_STYLE_CLOSE, _MARKER_STYLE_CLOSE_NEXTS);

		if (length == -1) {
			if (_log.isWarnEnabled()) {
				_log.warn("Missing </style>");
			}

			return;
		}

		if (length == 0) {
			outputCloseTag(charBuffer, writer, _MARKER_STYLE_CLOSE);

			return;
		}

		String content = extractContent(charBuffer, length);

		String minifiedContent = content;

		if (PropsValues.MINIFIER_INLINE_CONTENT_CACHE_ENABLED) {
			CacheKeyGenerator cacheKeyGenerator =
				CacheKeyGeneratorUtil.getCacheKeyGenerator(
					StripFilter.class.getName());

			String key = String.valueOf(cacheKeyGenerator.getCacheKey(content));

			minifiedContent = _minifierPortalCache.get(key);

			if (minifiedContent == null) {
				minifiedContent = MinifierUtil.minifyCss(content);

				boolean skipCache = false;

				for (String skipCss :
						PropsValues.MINIFIER_INLINE_CONTENT_CACHE_SKIP_CSS) {

					if (minifiedContent.contains(skipCss)) {
						skipCache = true;

						break;
					}
				}

				if (!skipCache) {
					_minifierPortalCache.put(key, minifiedContent);
				}
			}
		}
		else {
			minifiedContent = MinifierUtil.minifyCss(content);
		}

		if (Validator.isNotNull(minifiedContent)) {
			writer.write(minifiedContent);
		}

		outputCloseTag(charBuffer, writer, _MARKER_STYLE_CLOSE);
	}

	protected void extractAndMinifyJavaScript(
			String resourceName, CharBuffer charBuffer, Writer writer,
			char[] openTag)
		throws Exception {

		int endPos = openTag.length + 1;

		char c = charBuffer.charAt(openTag.length);

		if (c == CharPool.SPACE) {
			int startPos = openTag.length + 1;

			for (int i = startPos; i < charBuffer.length(); i++) {
				c = charBuffer.charAt(i);

				if (c == CharPool.GREATER_THAN) {

					// Open script tag complete

					endPos = i + 1;

					int length = i - startPos;

					int searchValue = KMPSearch.search(
						charBuffer, startPos, length, _MARKER_TYPE_JAVASCRIPT,
						_MARKER_TYPE_JAVASCRIPT_NEXTS);

					int expectedLength = _MARKER_TYPE_JAVASCRIPT.length();

					if (searchValue == -1) {
						searchValue = KMPSearch.search(
							charBuffer, startPos, length, _MARKER_TYPE_MODULE,
							_MARKER_TYPE_MODULE_NEXTS);

						expectedLength = _MARKER_TYPE_MODULE.length();
					}

					if ((length < expectedLength) || (searchValue == -1)) {

						// We have just determined that this is an open script
						// tag that does not have the attribute
						// type="text/javascript". Now check to see if it has
						// the attribute language="JavaScript". If it does not,
						// then we skip stripping.

						if (!hasLanguageAttribute(
								charBuffer, startPos, length)) {

							return;
						}
					}

					// Open script tag has no attribute or has attribute
					// type="text/javascript". Start stripping.

					break;
				}
				else if (c == CharPool.LESS_THAN) {

					// Illegal open script tag. Found a '<' before seeing a '>'.

					return;
				}
			}

			if (endPos == charBuffer.length()) {

				// Illegal open script tag. Unable to find a '>'.

				return;
			}
		}
		else if (c != CharPool.GREATER_THAN) {

			// Illegal open script tag. Not followed by a '>' or a ' '.

			return;
		}

		writer.append(charBuffer, 0, endPos);

		charBuffer.position(charBuffer.position() + endPos);

		int length = KMPSearch.search(
			charBuffer, _MARKER_SCRIPT_CLOSE, _MARKER_SCRIPT_CLOSE_NEXTS);

		if (length == -1) {
			if (_log.isWarnEnabled()) {
				_log.warn("Missing </script>");
			}

			return;
		}

		if (length == 0) {
			outputCloseTag(charBuffer, writer, _MARKER_SCRIPT_CLOSE);

			return;
		}

		String content = extractContent(charBuffer, length);

		String minifiedContent = content;

		if (PropsValues.MINIFIER_INLINE_CONTENT_CACHE_ENABLED) {
			CacheKeyGenerator cacheKeyGenerator =
				CacheKeyGeneratorUtil.getCacheKeyGenerator(
					StripFilter.class.getName());

			String key = String.valueOf(cacheKeyGenerator.getCacheKey(content));

			minifiedContent = _minifierPortalCache.get(key);

			if (minifiedContent == null) {
				minifiedContent = content;

				boolean skipCache = false;

				for (String skipJavaScript :
						PropsValues.
							MINIFIER_INLINE_CONTENT_CACHE_SKIP_JAVASCRIPT) {

					if (minifiedContent.contains(skipJavaScript)) {
						skipCache = true;

						break;
					}
				}

				if (!skipCache) {
					_minifierPortalCache.put(key, minifiedContent);
				}
			}
		}

		if (Validator.isNotNull(minifiedContent)) {
			writer.write(minifiedContent);
		}

		outputCloseTag(charBuffer, writer, _MARKER_SCRIPT_CLOSE);
	}

	protected void extractAndTrimInput(CharBuffer oldCharBuffer, Writer writer)
		throws Exception {

		int length = KMPSearch.search(
			oldCharBuffer, _MARKER_INPUT_OPEN.length, _MARKER_INPUT_CLOSE,
			_MARKER_INPUT_CLOSE_NEXTS);

		if (length == -1) {
			if (_log.isWarnEnabled()) {
				_log.warn("Missing >");
			}

			outputOpenTag(oldCharBuffer, writer, _MARKER_INPUT_OPEN);

			return;
		}

		length += _MARKER_INPUT_CLOSE.length();

		String content = extractContent(oldCharBuffer, length);

		writer.write(content);

		skipWhiteSpace(oldCharBuffer, writer, true);
	}

	protected void extractAndTrimPre(CharBuffer oldCharBuffer, Writer writer)
		throws Exception {

		int length = KMPSearch.search(
			oldCharBuffer, _MARKER_PRE_OPEN.length + 1, _MARKER_PRE_CLOSE,
			_MARKER_PRE_CLOSE_NEXTS);

		if (length == -1) {
			if (_log.isWarnEnabled()) {
				_log.warn("Missing </pre>");
			}

			outputOpenTag(oldCharBuffer, writer, _MARKER_PRE_OPEN);

			return;
		}

		length += _MARKER_PRE_CLOSE.length();

		String content = extractContent(oldCharBuffer, length);

		writer.write(content);

		skipWhiteSpace(oldCharBuffer, writer, true);
	}

	protected void extractAndTrimTextArea(
			CharBuffer oldCharBuffer, Writer writer)
		throws Exception {

		int length = KMPSearch.search(
			oldCharBuffer, _MARKER_TEXTAREA_OPEN.length + 1,
			_MARKER_TEXTAREA_CLOSE, _MARKER_TEXTAREA_CLOSE_NEXTS);

		if (length == -1) {
			if (_log.isWarnEnabled()) {
				_log.warn("Missing </textArea>");
			}

			outputOpenTag(oldCharBuffer, writer, _MARKER_TEXTAREA_OPEN);

			return;
		}

		length += _MARKER_TEXTAREA_CLOSE.length();

		String content = extractContent(oldCharBuffer, length);

		writer.write(content);

		skipWhiteSpace(oldCharBuffer, writer, true);
	}

	protected String extractContent(CharBuffer charBuffer, int length) {

		// See LPS-10545

		/*String content = charBuffer.subSequence(0, length).toString();

		int position = charBuffer.position();

		charBuffer.position(position + length);*/

		CharBuffer duplicateCharBuffer = charBuffer.duplicate();

		int position = duplicateCharBuffer.position() + length;

		String content = String.valueOf(duplicateCharBuffer.limit(position));

		charBuffer.position(position);

		return content;
	}

	protected boolean hasLanguageAttribute(
		CharBuffer charBuffer, int startPos, int length) {

		if (!PropsValues.STRIP_JS_LANGUAGE_ATTRIBUTE_SUPPORT_ENABLED) {
			return false;
		}

		int searchValue = KMPSearch.search(
			charBuffer, startPos, length, _MARKER_LANGUAGE,
			_MARKER_LANGUAGE_NEXTS);

		if (searchValue == -1) {
			return false;
		}

		Matcher matcher = _javaScriptPattern.matcher(charBuffer);

		return matcher.find();
	}

	protected boolean hasMarker(CharBuffer charBuffer, char[] marker) {
		int position = charBuffer.position();

		if ((position + marker.length) >= charBuffer.limit()) {
			return false;
		}

		for (int i = 0; i < marker.length; i++) {
			char c = marker[i];

			char oldC = charBuffer.charAt(i);

			if ((c != oldC) && (Character.toUpperCase(c) != oldC)) {
				return false;
			}
		}

		return true;
	}

	protected boolean isAlreadyFiltered(HttpServletRequest httpServletRequest) {
		if (httpServletRequest.getAttribute(SKIP_FILTER) != null) {
			return true;
		}

		return false;
	}

	protected boolean isInclude(HttpServletRequest httpServletRequest) {
		String uri = (String)httpServletRequest.getAttribute(
			JavaConstants.JAKARTA_SERVLET_INCLUDE_REQUEST_URI);

		if (uri == null) {
			return false;
		}

		return true;
	}

	protected boolean isStrip(HttpServletRequest httpServletRequest) {
		if (!ParamUtil.getBoolean(httpServletRequest, _STRIP, true)) {
			return false;
		}

		String path = httpServletRequest.getPathInfo();

		if (_ignorePaths.contains(path)) {
			if (_log.isDebugEnabled()) {
				_log.debug("Ignore path " + path);
			}

			return false;
		}

		// Modifying binary content through a servlet filter under certain
		// conditions is bad on performance the user will not start downloading
		// the content until the entire content is modified.

		String lifecycle = ParamUtil.getString(
			httpServletRequest, "p_p_lifecycle");

		if ((lifecycle.equals("1") &&
			 LiferayWindowState.isExclusive(httpServletRequest)) ||
			lifecycle.equals("2")) {

			return false;
		}

		return true;
	}

	protected boolean isStripContentType(String contentType) {
		for (String stripContentType : PropsValues.STRIP_MIME_TYPES) {
			if (stripContentType.endsWith(StringPool.STAR)) {
				stripContentType = stripContentType.substring(
					0, stripContentType.length() - 1);

				if (contentType.startsWith(stripContentType)) {
					return true;
				}
			}
			else {
				if (contentType.equals(stripContentType)) {
					return true;
				}
			}
		}

		return false;
	}

	protected void outputCloseTag(
			CharBuffer charBuffer, Writer writer, String closeTag)
		throws Exception {

		writer.write(closeTag);

		charBuffer.position(charBuffer.position() + closeTag.length());

		skipWhiteSpace(charBuffer, writer, true);
	}

	protected void outputOpenTag(
			CharBuffer charBuffer, Writer writer, char[] openTag)
		throws Exception {

		writer.write(openTag);

		charBuffer.position(charBuffer.position() + openTag.length);
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		if (_log.isDebugEnabled()) {
			String completeURL = HttpComponentsUtil.getCompleteURL(
				httpServletRequest);

			_log.debug("Stripping " + completeURL);
		}

		httpServletRequest.setAttribute(SKIP_FILTER, Boolean.TRUE);

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(httpServletResponse);

		processFilter(
			StripFilter.class.getName(), httpServletRequest,
			bufferCacheServletResponse, filterChain);

		String contentType = GetterUtil.getString(
			bufferCacheServletResponse.getContentType());

		contentType = StringUtil.toLowerCase(contentType);

		if (_log.isDebugEnabled()) {
			_log.debug("Stripping content of type " + contentType);
		}

		httpServletResponse.setContentType(contentType);

		if (isStripContentType(contentType) &&
			(bufferCacheServletResponse.getStatus() ==
				HttpServletResponse.SC_OK)) {

			CharBuffer oldCharBuffer =
				bufferCacheServletResponse.getCharBuffer();

			boolean ensureContentLength = ParamUtil.getBoolean(
				httpServletRequest, _ENSURE_CONTENT_LENGTH);

			if (ensureContentLength) {
				UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
					new UnsyncByteArrayOutputStream();

				strip(
					httpServletRequest, httpServletResponse, oldCharBuffer,
					new OutputStreamWriter(unsyncByteArrayOutputStream));

				httpServletResponse.setContentLength(
					unsyncByteArrayOutputStream.size());

				unsyncByteArrayOutputStream.writeTo(
					httpServletResponse.getOutputStream());
			}
			else if (!httpServletResponse.isCommitted()) {
				strip(
					httpServletRequest, httpServletResponse, oldCharBuffer,
					httpServletResponse.getWriter());
			}
		}
		else {
			ServletResponseUtil.write(
				httpServletResponse, bufferCacheServletResponse);
		}
	}

	protected boolean skipWhiteSpace(
			CharBuffer charBuffer, Writer writer, boolean appendSeparator)
		throws Exception {

		boolean skipped = false;

		for (int i = charBuffer.position(); i < charBuffer.limit(); i++) {
			char c = charBuffer.get();

			if ((c == CharPool.SPACE) || (c == CharPool.TAB) ||
				(c == CharPool.RETURN) || (c == CharPool.NEW_LINE)) {

				skipped = true;

				continue;
			}

			charBuffer.position(i);

			break;
		}

		if (skipped && appendSeparator) {
			writer.write(CharPool.SPACE);
		}

		return skipped;
	}

	protected void strip(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, CharBuffer charBuffer,
			Writer writer)
		throws Exception {

		skipWhiteSpace(charBuffer, writer, false);

		while (charBuffer.hasRemaining()) {
			char c = charBuffer.get();

			writer.write(c);

			if (c == CharPool.LESS_THAN) {
				if (hasMarker(charBuffer, _MARKER_INPUT_OPEN)) {
					extractAndTrimInput(charBuffer, writer);

					continue;
				}
				else if (hasMarker(charBuffer, _MARKER_PRE_OPEN)) {
					extractAndTrimPre(charBuffer, writer);

					continue;
				}
				else if (hasMarker(charBuffer, _MARKER_TEXTAREA_OPEN)) {
					extractAndTrimTextArea(charBuffer, writer);

					continue;
				}
				else if (hasMarker(charBuffer, _MARKER_SCRIPT_OPEN)) {
					extractAndMinifyJavaScript(
						String.valueOf(httpServletRequest.getRequestURL()),
						charBuffer, writer, _MARKER_SCRIPT_OPEN);

					continue;
				}
				else if (hasMarker(charBuffer, _MARKER_STYLE_OPEN)) {
					extractAndMinifyCSS(
						httpServletRequest, httpServletResponse, charBuffer,
						writer, _MARKER_STYLE_OPEN);

					continue;
				}
				else if (hasMarker(
							charBuffer,
							_MARKER_STYLE_DATA_SENNA_TRACK_PERMANENT)) {

					extractAndMinifyCSS(
						httpServletRequest, httpServletResponse, charBuffer,
						writer, _MARKER_STYLE_DATA_SENNA_TRACK_PERMANENT);

					continue;
				}
				else if (hasMarker(
							charBuffer,
							_MARKER_STYLE_DATA_SENNA_TRACK_TEMPORARY)) {

					extractAndMinifyCSS(
						httpServletRequest, httpServletResponse, charBuffer,
						writer, _MARKER_STYLE_DATA_SENNA_TRACK_TEMPORARY);

					continue;
				}
			}
			else if (c == CharPool.GREATER_THAN) {
				skipWhiteSpace(charBuffer, writer, true);
			}

			skipWhiteSpace(charBuffer, writer, true);
		}

		writer.flush();
	}

	private static final String _ENSURE_CONTENT_LENGTH = "ensureContentLength";

	private static final String _MARKER_INPUT_CLOSE = ">";

	private static final int[] _MARKER_INPUT_CLOSE_NEXTS =
		KMPSearch.generateNexts(_MARKER_INPUT_CLOSE);

	private static final char[] _MARKER_INPUT_OPEN = "input".toCharArray();

	private static final String _MARKER_LANGUAGE = "language=";

	private static final int[] _MARKER_LANGUAGE_NEXTS = KMPSearch.generateNexts(
		_MARKER_LANGUAGE);

	private static final String _MARKER_PRE_CLOSE = "/pre>";

	private static final int[] _MARKER_PRE_CLOSE_NEXTS =
		KMPSearch.generateNexts(_MARKER_PRE_CLOSE);

	private static final char[] _MARKER_PRE_OPEN = "pre".toCharArray();

	private static final String _MARKER_SCRIPT_CLOSE = "</script>";

	private static final int[] _MARKER_SCRIPT_CLOSE_NEXTS =
		KMPSearch.generateNexts(_MARKER_SCRIPT_CLOSE);

	private static final char[] _MARKER_SCRIPT_OPEN = "script".toCharArray();

	private static final String _MARKER_STYLE_CLOSE = "</style>";

	private static final int[] _MARKER_STYLE_CLOSE_NEXTS =
		KMPSearch.generateNexts(_MARKER_STYLE_CLOSE);

	private static final char[] _MARKER_STYLE_DATA_SENNA_TRACK_PERMANENT =
		"style data-senna-track=\"permanent\" type=\"text/css\">".toCharArray();

	private static final char[] _MARKER_STYLE_DATA_SENNA_TRACK_TEMPORARY =
		"style data-senna-track=\"temporary\" type=\"text/css\">".toCharArray();

	private static final char[] _MARKER_STYLE_OPEN =
		"style type=\"text/css\">".toCharArray();

	private static final String _MARKER_TEXTAREA_CLOSE = "/textarea>";

	private static final int[] _MARKER_TEXTAREA_CLOSE_NEXTS =
		KMPSearch.generateNexts(_MARKER_TEXTAREA_CLOSE);

	private static final char[] _MARKER_TEXTAREA_OPEN =
		"textarea ".toCharArray();

	private static final String _MARKER_TYPE_JAVASCRIPT =
		"type=\"text/javascript\"";

	private static final int[] _MARKER_TYPE_JAVASCRIPT_NEXTS =
		KMPSearch.generateNexts(_MARKER_TYPE_JAVASCRIPT);

	private static final String _MARKER_TYPE_MODULE = "type=\"module\"";

	private static final int[] _MARKER_TYPE_MODULE_NEXTS =
		KMPSearch.generateNexts(_MARKER_TYPE_MODULE);

	private static final String _STRIP = "strip";

	private static final Log _log = LogFactoryUtil.getLog(StripFilter.class);

	private static final Pattern _javaScriptPattern = Pattern.compile(
		"[Jj][aA][vV][aA][sS][cC][rR][iI][pP][tT]");

	private final Set<String> _ignorePaths = new HashSet<>();
	private final PortalCache<String, String> _minifierPortalCache;

}