/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.app.manager.web.internal.util;

import com.liferay.marketplace.app.manager.web.internal.constants.BundleStateConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

/**
 * @author Ryan Park
 */
public class MarketplaceAppManagerSearchUtil {

	public static List<Object> getResults(
		List<Bundle> bundles, String keywords, Locale locale) {

		List<Object> results = new ArrayList<>();

		String keywordsRegex = _getKeywordsRegex(keywords);

		// App display

		List<AppDisplay> appDisplays = AppDisplayFactoryUtil.getAppDisplays(
			bundles, StringPool.BLANK, BundleStateConstants.ANY, locale);

		for (AppDisplay appDisplay : appDisplays) {
			if (!_hasAppDisplayKeywordsMatch(appDisplay, keywordsRegex)) {
				continue;
			}

			results.add(appDisplay);
		}

		// Bundle

		for (Bundle bundle : bundles) {
			if (!_hasBundleKeywordsMatch(bundle, keywordsRegex)) {
				continue;
			}

			results.add(bundle);
		}

		return results;
	}

	private static boolean _containsMatches(String regex, String string) {
		if (string == null) {
			return false;
		}

		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

		Matcher matcher = pattern.matcher(string);

		return matcher.find();
	}

	private static String _getKeywordsRegex(String keywords) {
		keywords = StringUtil.replace(
			keywords,
			new String[] {
				StringPool.SPACE, StringPool.APOSTROPHE, StringPool.QUOTE
			},
			new String[] {StringPool.PIPE, StringPool.BLANK, StringPool.BLANK});

		return StringPool.OPEN_PARENTHESIS + keywords +
			StringPool.CLOSE_PARENTHESIS;
	}

	private static boolean _hasAppDisplayKeywordsMatch(
		AppDisplay appDisplay, String keywordsRegex) {

		if (_containsMatches(keywordsRegex, appDisplay.getDisplayTitle()) ||
			_containsMatches(keywordsRegex, appDisplay.getDescription())) {

			return true;
		}

		return false;
	}

	private static boolean _hasBundleKeywordsMatch(
		Bundle bundle, String keywordsRegex) {

		if (_containsMatches(keywordsRegex, bundle.getSymbolicName())) {
			return true;
		}

		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		String bundleDescription = headers.get(Constants.BUNDLE_DESCRIPTION);

		if (_containsMatches(keywordsRegex, bundleDescription)) {
			return true;
		}

		String bundleName = headers.get(Constants.BUNDLE_NAME);

		return _containsMatches(keywordsRegex, bundleName);
	}

}