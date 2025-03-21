/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.dynamiccss;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.ThemeLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.net.URLDecoder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Raymond Augé
 * @author Sergio Sánchez
 * @author David Truong
 */
public class DynamicCSSUtil {

	public static String replaceToken(
			ServletContext servletContext,
			HttpServletRequest httpServletRequest, String content)
		throws Exception {

		Theme theme = _getTheme(httpServletRequest);

		if (theme == null) {
			return content;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return replaceToken(
			servletContext, httpServletRequest, themeDisplay, theme, content);
	}

	public static String replaceToken(
			ServletContext servletContext,
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay,
			Theme theme, String parsedContent)
		throws Exception {

		String proxyPath = PortalUtil.getPathProxy();

		String baseURL = proxyPath.concat(servletContext.getContextPath());

		if (baseURL.endsWith(StringPool.SLASH)) {
			baseURL = baseURL.substring(0, baseURL.length() - 1);
		}

		return StringUtil.replace(
			parsedContent,
			new String[] {"@base_url@", "@portal_ctx@", "@theme_image_path@"},
			new String[] {
				baseURL, PortalUtil.getPathContext(),
				_getThemeImagesPath(httpServletRequest, themeDisplay, theme)
			});
	}

	/**
	 * @see com.liferay.portal.servlet.filters.aggregate.AggregateFilter#aggregateCss(
	 *      com.liferay.portal.servlet.filters.aggregate.ServletPaths, String)
	 */
	protected static String propagateQueryString(
		String content, String queryString) {

		StringBundler sb = new StringBundler(content.length());

		int pos = 0;

		while (true) {
			int importX = content.indexOf(_CSS_IMPORT_BEGIN, pos);

			int importY = content.indexOf(
				_CSS_IMPORT_END, importX + _CSS_IMPORT_BEGIN.length());

			if ((importX == -1) || (importY == -1)) {
				sb.append(content.substring(pos));

				break;
			}

			sb.append(content.substring(pos, importX));
			sb.append(_CSS_IMPORT_BEGIN);

			String url = content.substring(
				importX + _CSS_IMPORT_BEGIN.length(), importY);

			char firstChar = url.charAt(0);

			if (firstChar == CharPool.APOSTROPHE) {
				sb.append(CharPool.APOSTROPHE);
			}
			else if (firstChar == CharPool.QUOTE) {
				sb.append(CharPool.QUOTE);
			}

			url = StringUtil.unquote(url);

			sb.append(url);

			if (url.indexOf(CharPool.QUESTION) != -1) {
				sb.append(CharPool.AMPERSAND);
			}
			else {
				sb.append(CharPool.QUESTION);
			}

			sb.append(queryString);

			if (firstChar == CharPool.APOSTROPHE) {
				sb.append(CharPool.APOSTROPHE);
			}
			else if (firstChar == CharPool.QUOTE) {
				sb.append(CharPool.QUOTE);
			}

			sb.append(_CSS_IMPORT_END);

			pos = importY + _CSS_IMPORT_END.length();
		}

		return sb.toString();
	}

	private static Theme _getTheme(HttpServletRequest httpServletRequest)
		throws Exception {

		long companyId = PortalUtil.getCompanyId(httpServletRequest);

		String themeId = ParamUtil.getString(httpServletRequest, "themeId");

		if (Validator.isNotNull(themeId)) {
			try {
				return ThemeLocalServiceUtil.getTheme(companyId, themeId);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		String requestURI = URLDecoder.decode(
			httpServletRequest.getRequestURI(), StringPool.UTF8);

		Matcher portalThemeMatcher = _portalThemePattern.matcher(requestURI);

		if (portalThemeMatcher.find()) {
			String themePathId = portalThemeMatcher.group(1);

			themePathId = StringUtil.replace(
				themePathId, CharPool.UNDERLINE, StringPool.BLANK);

			themeId = PortalUtil.getJsSafePortletId(themePathId);
		}
		else {
			Matcher pluginThemeMatcher = _pluginThemePattern.matcher(
				requestURI);

			if (pluginThemeMatcher.find()) {
				String themePathId = pluginThemeMatcher.group(1);

				themePathId = StringUtil.replace(
					themePathId, CharPool.UNDERLINE, StringPool.BLANK);

				themePathId = StringBundler.concat(
					themePathId, PortletConstants.WAR_SEPARATOR, themePathId,
					"theme");

				themeId = PortalUtil.getJsSafePortletId(themePathId);
			}
		}

		if (Validator.isNull(themeId)) {
			return null;
		}

		try {
			return ThemeLocalServiceUtil.getTheme(companyId, themeId);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return null;
	}

	private static String _getThemeImagesPath(
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay,
			Theme theme)
		throws Exception {

		String themeImagesPath = null;

		if (themeDisplay != null) {
			themeImagesPath = themeDisplay.getPathThemeImages();
		}
		else {
			String cdnHost = PortalUtil.getCDNHost(httpServletRequest);
			String themeStaticResourcePath = theme.getStaticResourcePath();

			themeImagesPath =
				cdnHost + themeStaticResourcePath + theme.getImagesPath();
		}

		return themeImagesPath;
	}

	private static final String _CSS_IMPORT_BEGIN = "@import url(";

	private static final String _CSS_IMPORT_END = ");";

	private static final Log _log = LogFactoryUtil.getLog(DynamicCSSUtil.class);

	private static final Pattern _pluginThemePattern = Pattern.compile(
		"\\/([^\\/]+)-theme\\/", Pattern.CASE_INSENSITIVE);
	private static final Pattern _portalThemePattern = Pattern.compile(
		"themes\\/([^\\/]+)\\/css", Pattern.CASE_INSENSITIVE);

}