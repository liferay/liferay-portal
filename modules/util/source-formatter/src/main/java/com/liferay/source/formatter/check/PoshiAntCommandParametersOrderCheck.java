/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class PoshiAntCommandParametersOrderCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		ParameterNameComparator parameterNameComparator =
			new ParameterNameComparator();

		Matcher matcher1 = _antCommandCallPattern.matcher(content);

		while (matcher1.find()) {
			String antCommandCall = matcher1.group();

			if (getLevel(antCommandCall) != 0) {
				continue;
			}

			Matcher matcher2 = _valuePattern.matcher(antCommandCall);

			while (matcher2.find()) {
				Matcher matcher3 = _parameterPattern.matcher(matcher2.group(1));

				String previousParameter = null;

				while (matcher3.find()) {
					String parameter = matcher3.group();

					if (previousParameter != null) {
						int compare = parameterNameComparator.compare(
							previousParameter, parameter);

						if (compare > 0) {
							content = StringUtil.replaceFirst(
								content, parameter, previousParameter,
								matcher1.start());

							return StringUtil.replaceFirst(
								content, previousParameter, parameter,
								matcher1.start());
						}
					}

					previousParameter = parameter;
				}
			}
		}

		int x = -1;

		while (true) {
			x = content.indexOf("AntCommands.runCommand(", x + 1);

			if (x == -1) {
				break;
			}

			List<String> parameterList = JavaSourceUtil.getParameterList(
				JavaSourceUtil.getMethodCall(content, x));

			if (parameterList.size() != 2) {
				continue;
			}

			Matcher matcher = _parameterPattern.matcher(
				StringUtil.unquote(parameterList.get(1)));

			String previousParameter = null;

			while (matcher.find()) {
				String parameter = matcher.group();

				if (previousParameter != null) {
					int compare = parameterNameComparator.compare(
						previousParameter, parameter);

					if (compare > 0) {
						content = StringUtil.replaceFirst(
							content, parameter, previousParameter, x);

						return StringUtil.replaceFirst(
							content, previousParameter, parameter, x);
					}
				}

				previousParameter = parameter;
			}
		}

		return content;
	}

	private static final Pattern _antCommandCallPattern = Pattern.compile(
		"\n\t+AntCommand\\((.*?)\\);\n", Pattern.DOTALL);
	private static final Pattern _parameterPattern = Pattern.compile(
		" -D[^=]+?=(\\\\\"|).+?\\1(?= |\\Z)");
	private static final Pattern _valuePattern = Pattern.compile(
		"\t+value1 = \"(.+)\"\\);\n");

	private class ParameterNameComparator extends NaturalOrderStringComparator {

		@Override
		public int compare(String parameter1, String parameter2) {
			return super.compare(
				_getParameterName(parameter1), _getParameterName(parameter2));
		}

		private String _getParameterName(String parameter) {
			int x = parameter.indexOf(StringPool.EQUAL);

			return parameter.substring(0, x);
		}

	}

}