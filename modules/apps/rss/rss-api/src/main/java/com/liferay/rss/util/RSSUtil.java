/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.rss.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ResourceURL;

/**
 * @author Brian Wing Shun Chan
 * @author Eduardo García
 */
public class RSSUtil {

	public static final String ATOM = "atom";

	public static final String DISPLAY_STYLE_ABSTRACT = "abstract";

	public static final String DISPLAY_STYLE_DEFAULT =
		_getDisplayStyleDefault();

	public static final String DISPLAY_STYLE_FULL_CONTENT = "full-content";

	public static final String DISPLAY_STYLE_TITLE = "title";

	public static final String[] DISPLAY_STYLES = {
		DISPLAY_STYLE_ABSTRACT, DISPLAY_STYLE_FULL_CONTENT, DISPLAY_STYLE_TITLE
	};

	public static final String ENTRY_TYPE_DEFAULT = "html";

	public static final String FEED_TYPE_DEFAULT = _getFeedTypeDefault();

	public static final String[] FEED_TYPES = _getFeedTypes();

	public static final String FORMAT_DEFAULT = getFeedTypeFormat(
		FEED_TYPE_DEFAULT);

	public static final String RSS = "rss";

	public static final double VERSION_DEFAULT = getFeedTypeVersion(
		FEED_TYPE_DEFAULT);

	public static String getFeedType(String type, double version) {
		return type + StringPool.UNDERLINE + version;
	}

	public static String getFeedTypeFormat(String feedType) {
		if (Validator.isNotNull(feedType)) {
			String[] parts = StringUtil.split(feedType, StringPool.UNDERLINE);

			if (parts.length == 2) {
				return GetterUtil.getString(parts[0], FORMAT_DEFAULT);
			}
		}

		return FORMAT_DEFAULT;
	}

	public static String getFeedTypeName(String feedType) {
		String type = getFeedTypeFormat(feedType);

		if (type.equals(ATOM)) {
			type = "Atom";
		}
		else if (type.equals(RSS)) {
			type = "RSS";
		}

		double version = getFeedTypeVersion(feedType);

		return type + StringPool.SPACE + version;
	}

	public static double getFeedTypeVersion(String feedType) {
		if (Validator.isNotNull(feedType)) {
			String[] parts = StringUtil.split(feedType, StringPool.UNDERLINE);

			if (parts.length == 2) {
				return GetterUtil.getDouble(parts[1], VERSION_DEFAULT);
			}
		}

		return VERSION_DEFAULT;
	}

	public static String getFormatType(String format) {
		if (format == null) {
			return FORMAT_DEFAULT;
		}

		int x = format.indexOf(ATOM);

		if (x >= 0) {
			return ATOM;
		}

		int y = format.indexOf(RSS);

		if (y >= 0) {
			return RSS;
		}

		return FORMAT_DEFAULT;
	}

	public static double getFormatVersion(String format) {
		if (format == null) {
			return VERSION_DEFAULT;
		}

		if (format.contains("10") || format.contains("1.0")) {
			return 1.0;
		}

		if (format.contains("20") || format.contains("2.0")) {
			return 2.0;
		}

		return VERSION_DEFAULT;
	}

	public static ResourceURL getURL(
		ResourceURL resourceURL, int delta, String displayStyle,
		String feedType, String name) {

		if ((delta > 0) && (delta != SearchContainer.DEFAULT_DELTA)) {
			resourceURL.setParameter("max", String.valueOf(delta));
		}

		if (Validator.isNotNull(displayStyle) &&
			!displayStyle.equals(RSSUtil.DISPLAY_STYLE_DEFAULT)) {

			resourceURL.setParameter("displayStyle", displayStyle);
		}

		if (Validator.isNotNull(feedType) &&
			!feedType.equals(RSSUtil.FEED_TYPE_DEFAULT)) {

			resourceURL.setParameter("type", getFeedTypeFormat(feedType));
			resourceURL.setParameter(
				"version", String.valueOf(getFeedTypeVersion(feedType)));
		}

		if (Validator.isNotNull(name)) {
			resourceURL.setParameter("feedTitle", name);
		}

		return resourceURL;
	}

	public static String getURL(
		String url, int delta, String displayStyle, String feedType,
		String name) {

		if ((delta > 0) && (delta != SearchContainer.DEFAULT_DELTA)) {
			url = HttpComponentsUtil.addParameter(url, "max", delta);
		}

		if (Validator.isNotNull(displayStyle) &&
			!displayStyle.equals(RSSUtil.DISPLAY_STYLE_DEFAULT)) {

			url = HttpComponentsUtil.addParameter(
				url, "displayStyle", displayStyle);
		}

		if (Validator.isNotNull(feedType) &&
			!feedType.equals(RSSUtil.FEED_TYPE_DEFAULT)) {

			url = HttpComponentsUtil.addParameter(
				url, "type", getFeedTypeFormat(feedType));
			url = HttpComponentsUtil.addParameter(
				url, "version", String.valueOf(getFeedTypeVersion(feedType)));
		}

		if (Validator.isNotNull(name)) {
			url = HttpComponentsUtil.addParameter(url, "feedTitle", name);
		}

		return url;
	}

	private static String _getDisplayStyleDefault() {
		return GetterUtil.getString(
			PropsUtil.get(PropsKeys.RSS_FEED_DISPLAY_STYLE_DEFAULT),
			DISPLAY_STYLE_FULL_CONTENT);
	}

	private static String _getFeedTypeDefault() {
		return GetterUtil.getString(
			PropsUtil.get(PropsKeys.RSS_FEED_TYPE_DEFAULT),
			getFeedType(ATOM, 1.0));
	}

	private static String[] _getFeedTypes() {
		return GetterUtil.getStringValues(
			PropsUtil.getArray(PropsKeys.RSS_FEED_TYPES),
			new String[] {FEED_TYPE_DEFAULT});
	}

}