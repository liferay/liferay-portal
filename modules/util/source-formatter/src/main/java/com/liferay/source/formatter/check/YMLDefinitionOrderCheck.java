/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.comparator.PropertyValueComparator;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.check.util.YMLSourceUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 * @author Alan Huang
 */
public class YMLDefinitionOrderCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		List<String> documents = YMLSourceUtil.splitDocuments(content);

		StringBundler sb = new StringBundler(documents.size() * 2);

		for (String document : documents) {
			sb.append(_sortDefinitions(document));
			sb.append("\n---\n");
		}

		sb.setIndex(sb.index() - 1);

		content = _sortFeatureFlags(sb.toString());
		content = _sortPathParameters(content);

		if (fileName.endsWith("docker-compose.yaml")) {
			content = _sortPorts(content);
		}

		return content;
	}

	private List<YMLDefinition> _combineComments(List<String> definitions) {
		List<YMLDefinition> ymlDefinitions = new ArrayList<>();

		StringBundler commentsSB = new StringBundler(definitions.size() * 2);

		for (String definition : definitions) {
			if (definition.matches(" *#.*")) {
				commentsSB.append(definition);
				commentsSB.append("\n");
			}
			else if (commentsSB.length() > 0) {
				commentsSB.setIndex(commentsSB.index() - 1);

				ymlDefinitions.add(
					new YMLDefinition(definition, commentsSB.toString()));

				commentsSB.setIndex(0);
			}
			else {
				ymlDefinitions.add(
					new YMLDefinition(definition, commentsSB.toString()));
			}
		}

		if (commentsSB.index() > 0) {
			commentsSB.setIndex(commentsSB.index() - 1);

			ymlDefinitions.add(new YMLDefinition("", commentsSB.toString()));
		}

		return ymlDefinitions;
	}

	private boolean _containsDigit(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (Character.isDigit(s.charAt(i))) {
				return true;
			}
		}

		return false;
	}

	private String _sortDefinitions(String s) {
		List<String> definitions = _splitDefinitions(s);

		List<YMLDefinition> ymlDefinitions = _combineComments(definitions);

		Collections.sort(ymlDefinitions, new DefinitionComparator());

		StringBundler sb1 = new StringBundler();

		for (YMLDefinition ymlDefinition : ymlDefinitions) {
			String precedingComments = ymlDefinition.getPrecedingComments();

			if (!Validator.isBlank(precedingComments)) {
				sb1.append(precedingComments);
				sb1.append("\n");
			}

			String content = ymlDefinition.getContent();

			if (Validator.isBlank(content)) {
				continue;
			}

			String[] lines = content.split("\n");

			String firstLine = lines[0];

			if ((lines.length == 1) || YMLSourceUtil.isBlockStyle(firstLine)) {
				sb1.append(content);
				sb1.append("\n");

				continue;
			}

			String secondLine = lines[1];

			if (firstLine.matches(" +-") &&
				YMLSourceUtil.isBlockStyle(secondLine)) {

				sb1.append(content);
				sb1.append("\n");

				continue;
			}

			String trimmedSecondLine = secondLine.trim();

			if (firstLine.endsWith(":") && !trimmedSecondLine.contains(":") &&
				!trimmedSecondLine.equals("-") &&
				!trimmedSecondLine.startsWith("#") &&
				!trimmedSecondLine.startsWith("{{")) {

				sb1.append(content);
				sb1.append("\n");

				continue;
			}

			StringBundler sb2 = new StringBundler(lines.length * 2);

			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];

				if (i == 0) {
					sb1.append(line);
					sb1.append("\n");

					continue;
				}

				sb2.append(line);
				sb2.append("\n");
			}

			if (sb2.index() > 0) {
				sb2.setIndex(sb2.index() - 1);
			}

			sb1.append(_sortDefinitions(sb2.toString()));
			sb1.append("\n");
		}

		if (sb1.index() > 0) {
			sb1.setIndex(sb1.index() - 1);
		}

		return sb1.toString();
	}

	private String _sortFeatureFlags(String content) {
		int x = -1;

		while (true) {
			x = content.indexOf("featureFlags: ", x + 1);

			if (x == -1) {
				return content;
			}

			String featureFlags = content.substring(x + 14);

			int y = featureFlags.indexOf("\n");

			if (y != -1) {
				featureFlags = featureFlags.substring(0, y);
			}

			String[] array = featureFlags.split(",");

			if (array.length < 2) {
				return content;
			}

			Arrays.sort(array, new NaturalOrderStringComparator());

			String newFeatureFlags = StringUtil.merge(array);

			if (featureFlags.equals(newFeatureFlags)) {
				continue;
			}

			return StringUtil.replaceFirst(
				content, featureFlags, newFeatureFlags, x);
		}
	}

	private String _sortPathParameters(String content) {
		Matcher matcher1 = _pathPattern1.matcher(content);

		while (matcher1.find()) {
			String path = matcher1.group();

			String[] lines = path.split("\n", 2);

			Matcher matcher2 = _pathPattern2.matcher(lines[0]);

			Map<String, String> inPathsMap = new LinkedHashMap<>();

			while (matcher2.find()) {
				inPathsMap.put(matcher2.group(1), "");
			}

			int inPathCount = inPathsMap.size();

			Pattern pattern = Pattern.compile(
				"( *-\n( +)in: path(\n\\2.+)*\n){" + inPathCount + "}");

			matcher2 = pattern.matcher(lines[1]);

			while (matcher2.find()) {
				String inPaths = matcher2.group();

				Matcher matcher3 = _pathPattern3.matcher(inPaths);

				while (matcher3.find()) {
					String inPath = matcher3.group();

					inPathsMap.replace(
						inPath.replaceAll("(?s).*name: (\\S+).*", "$1"),
						inPath);
				}

				StringBundler sb = new StringBundler(inPathCount);

				for (Map.Entry<String, String> entry : inPathsMap.entrySet()) {
					sb.append(entry.getValue());
				}

				content = StringUtil.replaceFirst(
					content, inPaths, sb.toString());
			}
		}

		return content;
	}

	private String _sortPorts(String content) {
		Matcher matcher = _portsPattern.matcher(content);

		while (matcher.find()) {
			String indent = matcher.group(1) + StringPool.FOUR_SPACES;

			String ports = matcher.group(2);

			String trimmedPorts = StringUtil.trimLeading(ports);

			trimmedPorts = trimmedPorts.replaceAll(" *-\n +", "");

			String[] portsArray = StringUtil.splitLines(trimmedPorts);

			Arrays.sort(portsArray);

			StringBundler sb = new StringBundler(portsArray.length * 8);

			for (String port : portsArray) {
				sb.append(StringPool.NEW_LINE);
				sb.append(indent);
				sb.append(StringPool.DASH);
				sb.append(StringPool.NEW_LINE);
				sb.append(indent);
				sb.append(StringPool.FOUR_SPACES);
				sb.append(port);
			}

			String newPorts = sb.toString();

			if (ports.equals(newPorts)) {
				continue;
			}

			return StringUtil.replaceFirst(
				content, ports, newPorts, matcher.start(2));
		}

		return content;
	}

	private List<String> _splitDefinitions(String content) {
		List<String> definitions = new ArrayList<>();

		String[] lines = content.split("\n");

		StringBundler sb = new StringBundler(lines.length * 2);

		String leadingSpaces = StringPool.BLANK;
		int leadingSpacesLength = 0;

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];

			if (i == 0) {
				leadingSpaces = SourceUtil.getLeadingSpaces(line);

				leadingSpacesLength = leadingSpaces.length();

				sb.append(line);
				sb.append("\n");

				continue;
			}

			if ((line.length() == 0) || line.matches(" +")) {
				sb.append(line);
				sb.append("\n");

				continue;
			}

			if (line.charAt(leadingSpacesLength) != ' ') {
				if (sb.index() > 0) {
					sb.setIndex(sb.index() - 1);
				}

				definitions.add(sb.toString());

				sb.setIndex(0);
			}

			sb.append(line);
			sb.append("\n");
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);

			definitions.add(sb.toString());
		}

		return definitions;
	}

	private static final Map<String, Integer> _parametersWeightMap =
		HashMapBuilder.put(
			"cookie", 4
		).put(
			"header", 3
		).put(
			"path", 1
		).put(
			"query", 2
		).build();
	private static final Pattern _pathPattern1 = Pattern.compile(
		"(?<=\n)( *)\"([^{}\"]*\\{[^}]+\\}[^{}\"]*){2,}\":(\n\\1 .*)*");
	private static final Pattern _pathPattern2 = Pattern.compile(
		"\\{([^{}]+)\\}");
	private static final Pattern _pathPattern3 = Pattern.compile(
		" *-\n( +)in: path(\n\\1.+)*\n");
	private static final Pattern _portsPattern = Pattern.compile(
		"\n( +)ports:((\n +-\\s+\\d{4}:\\d{4}){2,})");

	private class DefinitionComparator implements Comparator<YMLDefinition> {

		@Override
		public int compare(
			YMLDefinition ymlDefinition1, YMLDefinition ymlDefinition2) {

			String content1 = ymlDefinition1.getContent();
			String content2 = ymlDefinition2.getContent();

			String trimmedContent1 = StringUtil.trimLeading(content1);
			String trimmedContent2 = StringUtil.trimLeading(content2);

			if (trimmedContent1.startsWith("{{") ||
				trimmedContent2.startsWith("{{") ||
				(trimmedContent1.startsWith("-\n") ^
				 trimmedContent2.startsWith("-\n"))) {

				return 0;
			}

			if (trimmedContent1.startsWith("-\n") &&
				trimmedContent2.startsWith("-\n")) {

				if (content1.contains("in: ") && content2.contains("in: ")) {
					return _sortParameters(content1, content2);
				}

				if (content1.contains("mountPath: ") &&
					content2.contains("mountPath: ")) {

					return _sortMounts(content1, content2);
				}

				return 0;
			}

			if (trimmedContent1.startsWith("in:") ||
				trimmedContent2.startsWith("in:")) {

				if (trimmedContent1.startsWith("in:")) {
					return -1;
				}

				return 1;
			}

			int x = trimmedContent1.indexOf(":");

			if (x == -1) {
				return 0;
			}

			String name1 = trimmedContent1.substring(0, x);

			x = trimmedContent2.indexOf(":");

			if (x == -1) {
				return 0;
			}

			String name2 = trimmedContent2.substring(0, x);

			if (_containsDigit(name1) && _containsDigit(name2)) {
				NaturalOrderStringComparator comparator =
					new NaturalOrderStringComparator();

				return comparator.compare(name1, name2);
			}

			return name1.compareTo(name2);
		}

		private String _getInValue(String parameter) {
			return parameter.replaceAll("(?s).*in: (\\S*).*", "$1");
		}

		private String _getMountPathValue(String parameter) {
			return parameter.replaceAll("(?s).*mountPath: (\\S*).*", "$1");
		}

		private int _getParameterWeight(String definitionKey) {
			return _parametersWeightMap.getOrDefault(definitionKey, -1);
		}

		private int _sortMounts(String mountPath1, String mountPath2) {
			String mountPathValue1 = _getMountPathValue(mountPath1);
			String mountPathValue2 = _getMountPathValue(mountPath2);

			PropertyValueComparator comparator = new PropertyValueComparator();

			return comparator.compare(mountPathValue1, mountPathValue2);
		}

		private int _sortParameters(String parameter1, String parameter2) {
			String inValue1 = _getInValue(parameter1);
			String inValue2 = _getInValue(parameter2);

			if (!inValue1.equals(inValue2)) {
				return _getParameterWeight(inValue1) -
					_getParameterWeight(inValue2);
			}

			String name1 = StringPool.BLANK;

			Matcher matcher = _namePattern.matcher(parameter1);

			if (matcher.find()) {
				name1 = matcher.group(1);
			}

			String name2 = StringPool.BLANK;

			matcher = _namePattern.matcher(parameter2);

			if (matcher.find()) {
				name2 = matcher.group(1);
			}

			return name1.compareTo(name2);
		}

		private final Pattern _namePattern = Pattern.compile(
			"^ *name: *(.*)(\n|\\Z)", Pattern.MULTILINE);

	}

	private class YMLDefinition {

		public YMLDefinition(String content, String precedingComments) {
			_content = content;
			_precedingComments = precedingComments;
		}

		public String getContent() {
			return _content;
		}

		public String getPrecedingComments() {
			return _precedingComments;
		}

		private final String _content;
		private final String _precedingComments;

	}

}