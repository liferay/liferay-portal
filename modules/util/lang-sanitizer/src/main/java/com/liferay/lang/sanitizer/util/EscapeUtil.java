/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.lang.sanitizer.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Seiphon Wang
 */
public class EscapeUtil {

	public static String escapeTag(String content) {
		content = content.replaceAll(">", "&gt;");
		content = content.replaceAll("<", "&lt;");

		return content;
	}

	public static String formatTag(String content) {
		Matcher matcher = _tagPattern.matcher(content);

		Set<String> matchedTags = new HashSet<>();

		while (matcher.find()) {
			matchedTags.add(matcher.group());
		}

		if (!matchedTags.isEmpty()) {
			if (matchedTags.contains("<br />")) {
				content = content.replaceAll("<br />", "<br>");
			}

			if (matchedTags.contains("<a {0}>")) {
				content = content.replaceAll("<a \\{0\\}>", "<a>");
			}
		}

		return content;
	}

	public static Set<String> getEscapedCharacters() {
		return _unescapedCharactersMap.keySet();
	}

	public static String unescape(String content) {
		Set<String> keys = _unescapedCharactersMap.keySet();

		for (String key : keys) {
			if (!content.contains(key)) {
				continue;
			}

			content = content.replaceAll(key, _unescapedCharactersMap.get(key));
		}

		return content;
	}

	public static String unescapeTag(String content) {
		content = content.replaceAll("&gt;", ">");
		content = content.replaceAll("&lt;", "<");

		return content;
	}

	private static final Pattern _tagPattern = Pattern.compile("<.+?>");

	@SuppressWarnings("serial")
	private static final Map<String, String> _unescapedCharactersMap =
		new HashMap<String, String>() {
			{
				put("&#039;", "'");
				put("&#39;", "'");
				put("&#149;", "•");
				put("&amp;", "&");
				put("&gt;", ">");
				put("&hellip;", "…");
				put("&laquo;", "«");
				put("&lt;", "<");
				put("&nbsp;", " ");
				put("&quot;", "\"");
				put("&raquo;", "»");
				put("&reg;", "®");
				put("&trade;", "™");
			}
		};

}