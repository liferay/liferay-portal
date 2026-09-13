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
		return getFileNames(
			new String[] {"**/templates/_scripts.tpl"}, getIncludes());
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
			String firstLine = matcher.group(2);

			if (matcher.group(3) != null) {
				firstLine = matcher.group(2) + matcher.group(3);
			}

			String indent = matcher.group(4);

			if (indent.length() <= firstLine.length()) {
				continue;
			}

			String secondLine = matcher.group(4) + matcher.group(5);

			String replacement =
				firstLine + secondLine.substring(firstLine.length());

			matcher.appendReplacement(
				sb, "\n" + Matcher.quoteReplacement(replacement));
		}

		if (sb.length() > 0) {
			matcher.appendTail(sb);

			String newContent = sb.toString();

			if (!content.equals(newContent)) {
				newContent = postFormat(newContent, originalReturnCharacter);
			}

			return super.postFormat(
				StringUtil.trim(newContent), originalReturnCharacter);
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

			sb.append(_preFormatArray(line));
			sb.append("\n");
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private String _preFormatArray(String line) {
		Matcher matcher = _dashPattern1.matcher(line);

		if (!matcher.matches()) {
			return line;
		}

		StringBundler sb = new StringBundler(3);

		String indent = matcher.group(1);

		sb.append(StringUtil.trimTrailing(indent));

		sb.append("\n");
		sb.append(
			_preFormatArray(indent.replaceFirst("-", " ") + matcher.group(2)));

		return sb.toString();
	}

	private static final String[] _INCLUDES = {
		"**/*.gotmpl", "**/templates/*.tpl", "**/*.yaml", "**/*.yml"
	};

	private static final Pattern _dashPattern1 = Pattern.compile("( *- +)(.+)");
	private static final Pattern _dashPattern2 = Pattern.compile(
		"(\\A|\n)( *-)( +-)*\n( +)(.+)");

}