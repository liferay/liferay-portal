/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.parser.JavaParameter;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaFinalVariableCheck extends BaseJavaTermCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, JavaTerm javaTerm,
		String fileContent) {

		String content = javaTerm.getContent();

		Matcher matcher = _finalVariablePattern.matcher(content);

		while (matcher.find()) {
			if (_requiresFinal(content, matcher.group(1))) {
				continue;
			}

			return StringUtil.replaceFirst(
				content, "final ", "", matcher.start());
		}

		int x = content.indexOf("{\n");

		if (x == -1) {
			x = content.length() - 1;
		}

		JavaSignature javaSignature = javaTerm.getSignature();

		for (JavaParameter javaParameter : javaSignature.getParameters()) {
			if (!javaParameter.isFinal()) {
				continue;
			}

			String name = javaParameter.getParameterName();

			if (_requiresFinal(content, name)) {
				continue;
			}

			String type = javaParameter.getParameterType();

			int y = content.lastIndexOf(
				StringBundler.concat("final ", type, " ", name), x);

			if (y != -1) {
				return StringUtil.replaceFirst(content, "final ", "", y - 1);
			}

			addMessage(
				fileName,
				"Keyword \"final\" not required for parameter \"" + name + "\"",
				javaTerm.getLineNumber());
		}

		return content;
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CONSTRUCTOR, JAVA_METHOD};
	}

	private int _getMatchingCloseCharPos(
		String content, String openChar, String closeChar, int startPos) {

		int endPos = startPos;

		while (true) {
			endPos = content.indexOf(closeChar, endPos + 1);

			if (endPos == -1) {
				return -1;
			}

			String s = content.substring(startPos, endPos + 1);

			int level = ToolsUtil.getLevel(s, openChar, closeChar);

			if (level == 0) {
				return endPos;
			}
		}
	}

	private boolean _requiresFinal(String content, String variableName) {
		Matcher matcher = _anonymouseClassPattern.matcher(content);

		while (matcher.find()) {
			int x = _getMatchingCloseCharPos(
				content, "(", ")", matcher.start());

			String followingCode = content.substring(x);

			if (!followingCode.startsWith(") {")) {
				continue;
			}

			int y = _getMatchingCloseCharPos(content, "{", "}", x);

			String body = content.substring(x + 3, y);

			if (body.matches("(?s).*\\W" + variableName + "\\W.*")) {
				return true;
			}
		}

		return false;
	}

	private static final Pattern _anonymouseClassPattern = Pattern.compile(
		"\\Wnew\\s[\\w\\s\\.<>,\\?]+\\(");
	private static final Pattern _finalVariablePattern = Pattern.compile(
		"[\t,]final [\\w\\s<>,]+?([\\w]+)\\s*[;=]");

}