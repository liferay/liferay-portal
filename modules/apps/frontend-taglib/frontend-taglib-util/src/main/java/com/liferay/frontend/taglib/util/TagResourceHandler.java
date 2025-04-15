/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.util;

import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolver;
import com.liferay.frontend.taglib.util.internal.NPMResolverRef;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.util.OutputData;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

import java.io.IOException;

import java.net.URL;

import java.util.Dictionary;
import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicLong;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Iván Zaera Avellón
 */
public class TagResourceHandler {

	public TagResourceHandler(Class<?> tagClass, TagAccessor tagAccessor) {
		_tagAccessor = tagAccessor;

		_bundle = FrameworkUtil.getBundle(tagClass);
		_log = LogFactoryUtil.getLog(tagClass);

		Dictionary<String, String> headers = _bundle.getHeaders(
			StringPool.BLANK);

		_webContextPath = headers.get("Web-ContextPath");
	}

	public void outputBundleStyleSheet(String bundleCssPath) {
		outputResource(
			Position.TOP,
			StringBundler.concat(
				"<link data-senna-track=\"temporary\" href=\"",
				PortalUtil.getPathModule(), _webContextPath, StringPool.SLASH,
				bundleCssPath, StringPool.QUOTE,
				ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
					_getHttpServletRequest()),
				" rel=\"stylesheet\">"));
	}

	public void outputNPMResource(String npmResourcePath) {
		try (NPMResolverRef npmResolverRef = new NPMResolverRef(_tagAccessor)) {
			NPMResolver npmResolver = npmResolverRef.getNPMResolver();

			String resourcePath = npmResolver.resolveModuleName(
				npmResourcePath);

			URL url = _bundle.getEntry(
				"META-INF/resources/node_modules/" + resourcePath);

			outputResource(Position.BOTTOM, URLUtil.toString(url));
		}
		catch (Exception exception) {
			_log.error(
				"Unable to output NPM resource " + npmResourcePath, exception);
		}
	}

	public void outputNPMStyleSheet(String npmCssPath) {
		try (NPMResolverRef npmResolverRef = new NPMResolverRef(_tagAccessor)) {
			NPMResolver npmResolver = npmResolverRef.getNPMResolver();

			String cssPath = npmResolver.resolveModuleName(npmCssPath);

			outputResource(
				Position.TOP,
				StringBundler.concat(
					"<link href=\"", PortalUtil.getPathModule(),
					_webContextPath, "/node_modules/", cssPath,
					StringPool.QUOTE,
					ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
						_getHttpServletRequest()),
					" rel=\"stylesheet\">"));
		}
		catch (Exception exception) {
			_log.error(
				"Unable to output NPM style sheet " + npmCssPath, exception);
		}
	}

	public void outputResource(Position position, String html) {
		HttpServletRequest httpServletRequest = _getHttpServletRequest();

		boolean xPjax = GetterUtil.getBoolean(
			httpServletRequest.getHeader("X-PJAX"));

		ThemeDisplay themeDisplay = _getThemeDisplay();

		if (themeDisplay.isIsolated() || themeDisplay.isLifecycleResource() ||
			themeDisplay.isStateExclusive() || xPjax) {

			try {
				PageContext pageContext = _tagAccessor.getPageContext();

				JspWriter jspWriter = pageContext.getOut();

				jspWriter.write(html);
			}
			catch (IOException ioException) {
				_log.error("Unable to output resource", ioException);
			}
		}
		else {
			OutputData outputData = _getOutputData();

			Long key = _nextKey.getAndIncrement();

			outputData.setDataSB(
				key.toString(), _webKeysEnumMap.get(position),
				new StringBundler(html));
		}
	}

	public enum Position {

		BOTTOM, TOP

	}

	private HttpServletRequest _getHttpServletRequest() {
		return _tagAccessor.getRequest();
	}

	private OutputData _getOutputData() {
		HttpServletRequest httpServletRequest = _getHttpServletRequest();

		OutputData outputData = (OutputData)httpServletRequest.getAttribute(
			WebKeys.OUTPUT_DATA);

		if (outputData == null) {
			outputData = new OutputData();

			httpServletRequest.setAttribute(WebKeys.OUTPUT_DATA, outputData);
		}

		return outputData;
	}

	private ThemeDisplay _getThemeDisplay() {
		ServletRequest servletRequest = _getHttpServletRequest();

		return (ThemeDisplay)servletRequest.getAttribute(WebKeys.THEME_DISPLAY);
	}

	private static final EnumMap<Position, String> _webKeysEnumMap =
		new EnumMap<Position, String>(Position.class) {
			{
				put(Position.BOTTOM, WebKeys.PAGE_BODY_BOTTOM);
				put(Position.TOP, WebKeys.PAGE_TOP);
			}
		};

	private final Bundle _bundle;
	private final Log _log;
	private final AtomicLong _nextKey = new AtomicLong();
	private final TagAccessor _tagAccessor;
	private final String _webContextPath;

}