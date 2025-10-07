/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.common.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

/**
 * @author Zoltán Takács
 * @author Igor Beslic
 */
public class DebugUtils {

	public static void debugCurrentStackTrace(Logger logger) {
		if (logger.isDebugEnabled()) {
			logger.debug(
				"Actual thread's stack trace: " + _getCurrentStackTrace());
		}
	}

	public static void infoCurrentStackTrace(Logger logger) {
		if (logger.isInfoEnabled()) {
			logger.info(
				"Actual thread's stack trace: " + _getCurrentStackTrace());
		}
	}

	private static String _getCurrentStackTrace() {
		Throwable throwable = new Throwable();

		List<StackTraceElement> stackTraceElements = new ArrayList<>(
			Arrays.asList(throwable.getStackTrace()));

		List<String> classNameAndMethodNames = new ArrayList<>();

		for (StackTraceElement stackTraceElement : stackTraceElements) {
			classNameAndMethodNames.add(
				_toClassNameAndMethodName(stackTraceElement.toString()));
		}

		return String.join(System.lineSeparator(), classNameAndMethodNames);
	}

	private static String _toClassNameAndMethodName(String line) {
		Matcher matcher = _stackLinePattern.matcher(line);

		if (!matcher.matches()) {
			return line;
		}

		return matcher.group(2) + matcher.group(1);
	}

	private DebugUtils() {
	}

	private static final Pattern _stackLinePattern = Pattern.compile(
		".+(\\.\\w+)\\((.+)\\)");

}