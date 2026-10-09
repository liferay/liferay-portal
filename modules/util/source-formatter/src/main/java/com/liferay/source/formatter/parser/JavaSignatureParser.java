/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.parser;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Hugo Huijser
 */
public class JavaSignatureParser {

	public static JavaSignature parseJavaSignature(
		String content, String accessModifier, String packageName,
		List<String> importNames, boolean method) {

		JavaSignature javaSignature = new JavaSignature();

		int x = content.indexOf(CharPool.TAB + accessModifier + CharPool.SPACE);

		if (x == -1) {
			return javaSignature;
		}

		int y = content.indexOf(CharPool.OPEN_PARENTHESIS, x);

		if (method) {
			javaSignature.setReturnType(
				_getReturnType(content.substring(x, y)), packageName,
				importNames);
		}

		x = y;

		String parameters = null;

		while (true) {
			y = content.indexOf(CharPool.CLOSE_PARENTHESIS, y + 1);

			if (y == -1) {
				return javaSignature;
			}

			parameters = content.substring(x + 1, y);

			if (ToolsUtil.getLevel(parameters) == 0) {
				break;
			}
		}

		boolean isFinal = false;
		Set<String> parameterAnnotations = new TreeSet<>();

		parameters = StringUtil.replace(
			parameters, new String[] {"\t", ".\n", "\n"},
			new String[] {"", ".", " "});

		parameters = parameters.replaceAll(" +<", "<");

		for (x = 0;;) {
			int pos = -1;

			while (true) {
				parameters = StringUtil.trim(parameters);

				if (!parameters.startsWith(StringPool.AT)) {
					break;
				}

				pos = parameters.indexOf(CharPool.SPACE, pos + 1);

				if (pos == -1) {
					break;
				}

				String annotation = parameters.substring(0, pos);

				if ((ToolsUtil.getLevel(annotation) != 0) ||
					(ToolsUtil.getLevel(annotation, "<", ">") != 0)) {

					continue;
				}

				parameterAnnotations.add(annotation);

				parameters = parameters.substring(pos + 1);

				pos = -1;
			}

			if (parameters.startsWith("final ")) {
				isFinal = true;
				parameters = parameters.substring(6);
			}

			x = parameters.indexOf(CharPool.SPACE, x + 1);

			if (x == -1) {
				return javaSignature;
			}

			String parameterType = parameters.substring(0, x);

			if (ToolsUtil.getLevel(parameterType, "<", ">") != 0) {
				continue;
			}

			y = parameters.indexOf(CharPool.COMMA, x);

			if (y == -1) {
				String parameterName = parameters.substring(x + 1);

				javaSignature.addParameter(
					parameterName, parameterType, parameterAnnotations, isFinal,
					packageName, importNames);

				return javaSignature;
			}

			String parameterName = parameters.substring(x + 1, y);

			javaSignature.addParameter(
				parameterName, parameterType, parameterAnnotations, isFinal,
				packageName, importNames);

			isFinal = false;
			parameterAnnotations = new TreeSet<>();
			parameters = parameters.substring(y + 1);

			x = 0;
		}
	}

	private static String _getReturnType(String s) {
		String returnType = null;

		s = StringUtil.replace(
			s, new String[] {"\t", ".\n", "\n"}, new String[] {"", ".", " "});

		int z = s.lastIndexOf(CharPool.SPACE);

		s = s.substring(0, z);

		while (true) {
			z = s.lastIndexOf(CharPool.SPACE, z - 1);

			returnType = s.substring(z + 1);

			if (ToolsUtil.getLevel(returnType, "<", ">") == 0) {
				break;
			}
		}

		if (returnType.equals("void")) {
			return null;
		}

		return returnType;
	}

}