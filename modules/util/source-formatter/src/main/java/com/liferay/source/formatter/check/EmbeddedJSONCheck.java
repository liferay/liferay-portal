/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JSONSourceUtil;
import com.liferay.source.formatter.check.util.TFSourceUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class EmbeddedJSONCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (fileName.endsWith(".md")) {
			return _formatJSONInFencedCodeBlock(content);
		}

		if (fileName.endsWith(".tf")) {
			return _formatJSONInHeredoc(content);
		}

		if (fileName.endsWith(".xml")) {
			content = _formatJSONInCDATA(content);

			return _formatJSONInFencedCodeBlock(content);
		}

		return content;
	}

	private String _formatJSONInCDATA(String content) {
		Matcher matcher = _cdataPattern1.matcher(content);

		while (matcher.find()) {
			String cdataValue = matcher.group(5);

			cdataValue = JSONSourceUtil.removeJSONComments(cdataValue);

			JSONObject jsonObject = JSONSourceUtil.getJSONObject(cdataValue);

			if (jsonObject == null) {
				continue;
			}

			String indent = matcher.group(2);

			StringBundler sb = new StringBundler(9);

			sb.append(matcher.group(1));
			sb.append("\n");
			sb.append(indent);
			sb.append("\t<![CDATA[\n");
			sb.append(
				JSONSourceUtil.fixIndentation(jsonObject, indent + "\t\t"));
			sb.append(indent);
			sb.append("\t]]>\n");
			sb.append(indent);
			sb.append(matcher.group(6));

			return StringUtil.replace(content, matcher.group(), sb.toString());
		}

		matcher = _cdataPattern2.matcher(content);

		while (matcher.find()) {
			String cdataValue = matcher.group(3);

			cdataValue = JSONSourceUtil.removeJSONComments(cdataValue);

			JSONObject jsonObject = JSONSourceUtil.getJSONObject(cdataValue);

			if (jsonObject == null) {
				continue;
			}

			String indent = matcher.group(2);
			String match = matcher.group();

			StringBundler sb = new StringBundler(5);

			sb.append(matcher.group(1));
			sb.append("\n");
			sb.append(JSONSourceUtil.fixIndentation(jsonObject, indent + "\t"));
			sb.append(indent);
			sb.append("]]>\n");

			String replacement = sb.toString();

			if (!match.equals(replacement)) {
				return StringUtil.replace(content, match, replacement);
			}
		}

		return content;
	}

	private String _formatJSONInFencedCodeBlock(String content) {
		Matcher matcher = _fencedCodeBlockPattern.matcher(content);

		while (matcher.find()) {
			String jsonString = matcher.group(1);

			String newJsonString = JSONSourceUtil.removeJSONComments(
				jsonString);

			JSONObject jsonObject = JSONSourceUtil.getJSONObject(newJsonString);

			if (jsonObject == null) {
				continue;
			}

			newJsonString = JSONSourceUtil.fixIndentation(jsonObject, "");

			if (jsonString.equals(newJsonString)) {
				continue;
			}

			return StringUtil.replaceFirst(
				content, jsonString, newJsonString, matcher.start());
		}

		return content;
	}

	private String _formatJSONInHeredoc(String content) {
		String[] lines = content.split("\n", -1);

		for (int i = 0; i < lines.length; i++) {
			String heredocMarker = TFSourceUtil.getHeredocMarker(lines[i]);

			if (heredocMarker == null) {
				continue;
			}

			int heredocEndLineIndex = TFSourceUtil.getHeredocEndLineIndex(
				lines, i, heredocMarker);

			if (heredocEndLineIndex == -1) {
				return content;
			}

			if ((heredocEndLineIndex == (i + 1)) ||
				!StringUtil.equals(
					StringUtil.trim(lines[i + 1]),
					StringPool.OPEN_CURLY_BRACE)) {

				i = heredocEndLineIndex;

				continue;
			}

			StringBundler sb = new StringBundler();

			for (int j = i + 1; j < heredocEndLineIndex; j++) {
				sb.append(lines[j]);
				sb.append("\n");
			}

			String json = sb.toString();

			JSONObject jsonObject = JSONSourceUtil.getJSONObject(json);

			if (jsonObject == null) {
				i = heredocEndLineIndex;

				continue;
			}

			String line = lines[i + 1];

			String newJSON = JSONSourceUtil.fixIndentation(
				jsonObject,
				line.substring(0, line.indexOf(CharPool.OPEN_CURLY_BRACE)));

			if (!json.equals(newJSON)) {
				return StringUtil.replaceFirst(
					content, json, newJSON, content.indexOf(lines[i] + "\n"));
			}

			i = heredocEndLineIndex;
		}

		return content;
	}

	private static final Pattern _cdataPattern1 = Pattern.compile(
		"(\n(\t*)<([\\w-]+)( .+)?>)<\\!\\[CDATA\\[(.*?)\\]\\]>(</\\3>\n)");
	private static final Pattern _cdataPattern2 = Pattern.compile(
		"(\n(\t*)<\\!\\[CDATA\\[)(.*?)\\]\\]>\n", Pattern.DOTALL);
	private static final Pattern _fencedCodeBlockPattern = Pattern.compile(
		"\n```json\n(.+?\n)```", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

}