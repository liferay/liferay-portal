/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class YMLSourceProcessor extends BaseSourceProcessor {

	@Override
	protected List<String> doGetFileNames() throws IOException {
		return getFileNames(new String[0], getIncludes());
	}

	@Override
	protected String[] doGetIncludes() {
		return _INCLUDES;
	}

	@Override
	protected String postFormat(
		String content, String originalReturnCharacter) {

		StringBuffer sb = new StringBuffer();

		Matcher matcher = _dashPattern2.matcher(content);

		while (matcher.find()) {
			String firstLine = matcher.group(1);
			String indent = matcher.group(2);

			if (indent.length() <= firstLine.length()) {
				continue;
			}

			String secondLine = matcher.group(2) + matcher.group(3);

			String replacement =
				firstLine + secondLine.substring(firstLine.length());

			matcher.appendReplacement(
				sb, "\n" + Matcher.quoteReplacement(replacement));
		}

		if (sb.length() > 0) {
			matcher.appendTail(sb);

			return super.postFormat(sb.toString(), originalReturnCharacter);
		}

		return super.postFormat(content, originalReturnCharacter);
	}

	@Override
	protected String preFormat(
			File file, String fileName, String content,
			Set<String> modifiedMessages, String originalReturnCharacter)
		throws Exception {

		StringBundler sb = new StringBundler();

		content = super.preFormat(
			file, fileName, content, modifiedMessages, originalReturnCharacter);

		content = content.replaceAll("\\n +\\n", "\n\n");

		String[] lines = content.split("\n");

		for (String line : lines) {
			String trimmedLine = line.trim();

			if (Validator.isBlank(trimmedLine)) {
				sb.append("\n");

				continue;
			}

			Matcher matcher = _dashPattern1.matcher(line);

			if (matcher.matches()) {
				String indent = matcher.group(1);

				sb.append(StringUtil.trimTrailing(indent));

				sb.append("\n");
				sb.append(indent.replaceFirst("-", " "));
				sb.append(matcher.group(2));
				sb.append("\n");

				continue;
			}

			sb.append(line);
			sb.append("\n");
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private static final String[] _INCLUDES = {
		"**/templates/*.tpl", "**/*.yaml", "**/*.yml"
	};

	private static final Pattern _dashPattern1 = Pattern.compile("( +- +)(.+)");
	private static final Pattern _dashPattern2 = Pattern.compile(
		"\n( *-)\n( +)(.+)");

}