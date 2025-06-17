/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ToolsUtil;

import java.io.IOException;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Peter Shin
 */
public class PropertiesEnvironmentVariablesCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!isPortalSource() ||
			(!absolutePath.matches(
				".*/portal-impl/src/portal(_.*)?\\.properties") &&
			 !absolutePath.matches(".*/portal-impl/src/system.properties"))) {

			return content;
		}

		return _formatPortalProperties(fileName, content);
	}

	private String _addEnvVariables(
		String fileName, String content, String commentsBlock,
		String environmentVariablesBlock, String variablesContent,
		int lineNumber) {

		Set<String> environmentVariables = _getEnvironmentVariables(
			fileName, variablesContent);

		if (environmentVariables.isEmpty()) {
			return content;
		}

		StringBundler sb = new StringBundler();

		if (Validator.isNull(commentsBlock) &&
			Validator.isNull(environmentVariablesBlock)) {

			sb.append(StringPool.NEW_LINE);
			sb.append(StringPool.FOUR_SPACES);
			sb.append(StringPool.POUND);
			sb.append(StringPool.NEW_LINE);
		}

		for (String environmentVariable : environmentVariables) {
			sb.append("    # Env: ");
			sb.append(environmentVariable);
			sb.append(StringPool.NEW_LINE);
		}

		sb.append(StringPool.FOUR_SPACES);
		sb.append(StringPool.POUND);
		sb.append(StringPool.NEW_LINE);

		String environmentVariablesComment = sb.toString();

		if (environmentVariablesBlock.equals(environmentVariablesComment)) {
			return content;
		}

		int pos = getLineStartPos(content, lineNumber + 1);

		if (Validator.isNotNull(environmentVariablesBlock)) {
			return StringUtil.replaceFirst(
				content, environmentVariablesBlock, environmentVariablesComment,
				pos);
		}

		variablesContent = StringUtil.replaceLast(variablesContent, "\n", "");

		return StringUtil.replaceFirst(
			content, variablesContent,
			environmentVariablesComment + variablesContent,
			getLineStartPos(content, lineNumber + 1));
	}

	private String _formatPortalProperties(String fileName, String content) {
		StringBundler commentBlockSB = new StringBundler();
		StringBundler environmentVariablesBlockSB = new StringBundler();
		StringBundler variablesContentSB = new StringBundler();

		int lineNumber = 0;

		String[] lines = StringUtil.splitLines(content);

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];

			if (line.startsWith("        ") || line.matches("    #?\\w.*")) {
				variablesContentSB.append(line);
				variablesContentSB.append("\n");

				continue;
			}

			if (variablesContentSB.index() > 0) {
				String newContent = _addEnvVariables(
					fileName, content, commentBlockSB.toString(),
					environmentVariablesBlockSB.toString(),
					variablesContentSB.toString(), lineNumber);

				if (!content.equals(newContent)) {
					return newContent;
				}

				environmentVariablesBlockSB = new StringBundler();
				variablesContentSB = new StringBundler();

				lineNumber = i;
			}

			if (line.startsWith("    # Env: ") ||
				((environmentVariablesBlockSB.index() > 0) &&
				 line.equals("    #"))) {

				environmentVariablesBlockSB.append(line);
				environmentVariablesBlockSB.append("\n");
			}
			else if (line.equals("    #") || line.startsWith("    # ")) {
				commentBlockSB.append(line);
				commentBlockSB.append("\n");
			}
		}

		return _addEnvVariables(
			fileName, content, commentBlockSB.toString(),
			environmentVariablesBlockSB.toString(),
			variablesContentSB.toString(), lineNumber);
	}

	private Set<String> _getEnvironmentVariables(String fileName, String s) {
		Set<String> environmentVariables = new LinkedHashSet<>();

		String previousLine = null;

		for (String line : StringUtil.splitLines(s)) {
			if ((previousLine != null) && previousLine.endsWith("\\")) {
				previousLine = line;

				continue;
			}

			String trimmedLine = StringUtil.trim(line);

			if (trimmedLine.startsWith(StringPool.POUND)) {
				trimmedLine = trimmedLine.substring(1);

				trimmedLine = StringUtil.trim(trimmedLine);
			}

			int pos = trimmedLine.indexOf(StringPool.EQUAL);

			if (pos == -1) {
				previousLine = line;

				continue;
			}

			String environmentVariable = ToolsUtil.encodeEnvironmentProperty(
				trimmedLine.substring(0, pos));

			if (fileName.endsWith("/system.properties")) {
				environmentVariable = environmentVariable.replaceFirst(
					"LIFERAY_", "SYSTEM_LIFERAY_");
			}

			previousLine = line;

			environmentVariables.add(environmentVariable);
		}

		return environmentVariables;
	}

}