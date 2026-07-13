/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.internal.util;

import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Gregory Amerson
 */
public class StringUtil {

	public static final String BLANK = "";

	public static final String COLON = ":";

	public static final String COMMA = ",";

	public static final String COMMA_AND_SPACE = ", ";

	public static final String FORWARD_SLASH = "/";

	public static final String NEW_LINE = "\n";

	public static final String STAR = "*";

	public static final String UNDERSCORE = "_";

	public static String capitalize(String s) {
		if ((s == null) || s.isEmpty()) {
			return "";
		}

		char firstChar = s.charAt(0);

		if (Character.isLowerCase(firstChar)) {
			s = Character.toUpperCase(firstChar) + s.substring(1);
		}

		return s;
	}

	public static String concat(Object... objects) {
		if (objects.length == 0) {
			return BLANK;
		}

		if (objects.length == 1) {
			return String.valueOf(objects[0]);
		}

		StringBuilder sb = new StringBuilder();

		for (Object object : objects) {
			sb.append(object);
		}

		return sb.toString();
	}

	public static String getDockerSafeName(String name) {
		Matcher matcher = _camelCasePattern.matcher(name);

		String dockerSafeName = matcher.replaceAll("-");

		if ((name.charAt(0) != '-') && (dockerSafeName.charAt(0) == '-') &&
			(dockerSafeName.length() > 1)) {

			dockerSafeName = dockerSafeName.substring(1);
		}

		return dockerSafeName.toLowerCase();
	}

	public static boolean isBlank(String s) {
		if ((s == null) || s.isEmpty()) {
			return true;
		}

		return false;
	}

	public static boolean isUrl(String url) {
		if (isBlank(url) || !url.contains(COLON)) {
			return false;
		}

		try {
			new URL(url);

			return true;
		}
		catch (MalformedURLException malformedURLException) {
			return false;
		}
	}

	public static String join(String delimiter, Collection<?> objects) {
		return join(delimiter, objects.stream());
	}

	public static String join(String delimiter, Object[] objects) {
		return join(delimiter, Arrays.stream(objects));
	}

	public static String join(String delimiter, Stream<?> stream) {
		return stream.map(
			String::valueOf
		).collect(
			Collectors.joining(delimiter)
		);
	}

	public static String normalizeFilePath(String s) {
		if (s == null) {
			return null;
		}

		return s.replace('\\', '/');
	}

	public static String quote(Object object) {
		return "\"" + object + "\"";
	}

	public static String read(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[8192];
		int offset = 0;

		while (true) {
			int count = inputStream.read(
				buffer, offset, buffer.length - offset);

			if (count == -1) {
				break;
			}

			offset += count;

			if (offset == buffer.length) {
				byte[] newBuffer = new byte[buffer.length << 1];

				System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);

				buffer = newBuffer;
			}
		}

		if (offset == 0) {
			return "";
		}

		return new String(buffer, 0, offset, "UTF-8");
	}

	public static List<String> split(String s) {
		return split(s, COMMA);
	}

	public static List<String> split(String s, String delimiter) {
		if ((s == null) || s.isEmpty()) {
			return Collections.emptyList();
		}

		s = s.trim();

		if (s.isEmpty()) {
			return Collections.emptyList();
		}

		return Arrays.asList(s.split(COMMA));
	}

	public static String suffixIfNotBlank(String s, String suffix) {
		return suffixIfNotBlank(s, UNDERSCORE, suffix);
	}

	public static String suffixIfNotBlank(
		String s, String separator, String suffix) {

		if (isBlank(suffix)) {
			return s;
		}

		return concat(s, separator, suffix);
	}

	public static String toAlphaNumericLowerCase(String value) {
		return toLowerCase(value.replaceAll("[^a-zA-Z0-9]", ""));
	}

	public static String toLowerCase(String s) {
		if (s == null) {
			return null;
		}

		StringBuilder sb = null;

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (c > 127) {

				// Found non-ascii char, fallback to the slow unicode detection

				return s.toLowerCase(Locale.getDefault());
			}

			if ((c >= 'A') && (c <= 'Z')) {
				if (sb == null) {
					sb = new StringBuilder(s);
				}

				sb.setCharAt(i, (char)(c + 32));
			}
		}

		if (sb == null) {
			return s;
		}

		return sb.toString();
	}

	public static String toString(List<String> strings) {
		Stream<String> stream = strings.stream();

		return stream.collect(Collectors.joining(", "));
	}

	private static final Pattern _camelCasePattern = Pattern.compile(
		"(?<=[a-z])(?=[A-Z])|(?=[A-Z][a-z])");

}