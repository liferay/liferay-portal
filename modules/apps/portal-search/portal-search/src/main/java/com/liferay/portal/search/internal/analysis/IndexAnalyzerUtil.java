/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.analysis;

import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Selena Aungst
 */
public class IndexAnalyzerUtil {

	public static String getIndexAnalyzer(String fieldName) {
		Matcher matcher = _pattern.matcher(fieldName);

		if (!matcher.matches()) {
			return null;
		}

		String analyzer = _analyzers.get(matcher.group(1));

		if (analyzer != null) {
			return analyzer;
		}

		return _analyzers.get(matcher.group(2));
	}

	private static final Map<String, String> _analyzers = HashMapBuilder.put(
		"ar", "arabic"
	).put(
		"ca", "catalan"
	).put(
		"de", "german"
	).put(
		"en", "english"
	).put(
		"es", "spanish"
	).put(
		"fi", "finnish"
	).put(
		"fr", "french"
	).put(
		"hu", "hungarian"
	).put(
		"it", "italian"
	).put(
		"ja", "kuromoji"
	).put(
		"nl", "dutch"
	).put(
		"pt", "portuguese"
	).put(
		"pt_BR", "brazilian"
	).put(
		"sv", "swedish"
	).put(
		"zh", "smartcn"
	).build();
	private static final Pattern _pattern = Pattern.compile(
		"\\w+?_(([a-z]{2})(?:_[A-Z]{2})?)");

}