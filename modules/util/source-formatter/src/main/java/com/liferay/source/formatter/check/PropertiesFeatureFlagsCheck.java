/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.source.formatter.check.util.BNDSourceUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class PropertiesFeatureFlagsCheck extends BaseFileCheck {

	@Override
	public void setAllFileNames(List<String> allFileNames) {
		_allFileNames = allFileNames;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!absolutePath.endsWith("/portal-impl/src/portal.properties")) {
			return content;
		}

		_checkUnnecessaryFeatureFlags(fileName, content);

		content = _generateFeatureFlagProperties(content);
		content = _generateFeatureFlagUIProperties(
			fileName, absolutePath, content);

		return content;
	}

	private void _checkUnnecessaryFeatureFlags(String fileName, String content)
		throws IOException {

		Properties properties = new Properties();

		properties.load(new StringReader(content));

		Enumeration<String> enumeration =
			(Enumeration<String>)properties.propertyNames();

		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();

			if (!key.startsWith("feature.flag.") || !key.endsWith(".type")) {
				continue;
			}

			String value = properties.getProperty(key);

			if (StringUtil.equals(value, "dev")) {
				addMessage(
					fileName,
					"Remove unnecessary property \"" + key +
						"\", since \"dev\" is the default value");
			}
		}
	}

	private String _generateFeatureFlagProperties(String content) {
		List<String> featureFlagKeys = new ArrayList<>();

		List<String> fileNames = SourceFormatterUtil.filterFileNames(
			_allFileNames, new String[] {"**/test/**"},
			new String[] {
				"**/bnd.bnd", "**/*.java", "**/*.js", "**/*.json", "**/*.jsp",
				"**/*.jspf", "**/*.jsx", "**/*.ts", "**/*.tsx"
			},
			getSourceFormatterExcludes(), true);

		for (String fileName : fileNames) {
			fileName = StringUtil.replace(
				fileName, CharPool.BACK_SLASH, CharPool.SLASH);

			String fileContent = FileUtil.read(new File(fileName));

			if (fileName.endsWith("bnd.bnd")) {
				String liferaySiteInitializerFeatureFlagKey =
					BNDSourceUtil.getDefinitionValue(
						fileContent, "Liferay-Site-Initializer-Feature-Flag");

				if (liferaySiteInitializerFeatureFlagKey == null) {
					continue;
				}

				featureFlagKeys.add(liferaySiteInitializerFeatureFlagKey);
			}
			else if (fileName.endsWith(".java")) {
				featureFlagKeys.addAll(
					_getFeatureFlagKeys(fileContent, _featureFlagPattern1));
				featureFlagKeys.addAll(
					_getFeatureFlagKeys(fileContent, _featureFlagPattern4));
				featureFlagKeys.addAll(
					_getFeatureFlagKeys(fileContent, _featureFlagPattern5));
				featureFlagKeys.addAll(
					_getFeatureFlagKeysByFeatureFlagManagerUtilIsEnabledCall(
						fileContent, true));
				featureFlagKeys.addAll(
					_getFeatureFlagKeysByMapUtilSingletonDictionaryCall(
						fileContent));
			}
			else if (fileName.endsWith(".json")) {
				featureFlagKeys.addAll(
					_getFeatureFlagKeys(fileContent, _featureFlagPattern3));
			}
			else if (fileName.endsWith(".jsp") || fileName.endsWith(".jspf")) {
				featureFlagKeys.addAll(
					_getFeatureFlagKeys(fileContent, _featureFlagPattern2));
				featureFlagKeys.addAll(
					_getFeatureFlagKeysByFeatureFlagManagerUtilIsEnabledCall(
						fileContent, false));
			}
			else {
				featureFlagKeys.addAll(
					_getFeatureFlagKeys(fileContent, _featureFlagPattern2));
			}
		}

		ListUtil.distinct(featureFlagKeys, new NaturalOrderStringComparator());

		Matcher matcher = _featureFlagsPattern.matcher(content);

		if (matcher.find()) {
			String matchedFeatureFlags = matcher.group(2);

			if (featureFlagKeys.isEmpty()) {
				if (matchedFeatureFlags.contains("feature.flag.")) {
					return StringUtil.replaceFirst(
						content, matchedFeatureFlags, StringPool.BLANK,
						matcher.start(2));
				}

				return content;
			}

			List<String> deprecationFeatureFlagKeys = new ArrayList<>();

			Matcher deprecationFeatureFlagKeyMatcher =
				_deprecationFeatureFlagPattern.matcher(content);

			while (deprecationFeatureFlagKeyMatcher.find()) {
				deprecationFeatureFlagKeys.add(
					deprecationFeatureFlagKeyMatcher.group(1));
			}

			StringBundler sb = new StringBundler(featureFlagKeys.size() * 6);

			for (String featureFlagKey : featureFlagKeys) {
				String featureFlagPropertyKey =
					"feature.flag." + featureFlagKey;

				String environmentVariable =
					ToolsUtil.encodeEnvironmentProperty(featureFlagPropertyKey);

				sb.append("\n\n    #\n    # Env: ");
				sb.append(environmentVariable);
				sb.append("\n    #\n    ");
				sb.append(featureFlagPropertyKey);
				sb.append(StringPool.EQUAL);
				sb.append(deprecationFeatureFlagKeys.contains(featureFlagKey));
			}

			if (matchedFeatureFlags.contains("feature.flag.")) {
				content = StringUtil.replaceFirst(
					content, matchedFeatureFlags, sb.toString(),
					matcher.start(2));
			}
			else {
				content = StringUtil.insert(
					content, sb.toString(), matcher.start(2));
			}
		}

		return content;
	}

	private String _generateFeatureFlagUIProperties(
		Map<String, String> properties) {

		StringBundler sb1 = new StringBundler(properties.size() * 6);

		for (Map.Entry<String, String> entry : properties.entrySet()) {
			String key = entry.getKey();

			String environmentVariable = ToolsUtil.encodeEnvironmentProperty(
				key);

			sb1.append("\n\n    #\n    # Env: ");
			sb1.append(environmentVariable);
			sb1.append("\n    #\n    ");
			sb1.append(key);
			sb1.append(StringPool.EQUAL);

			String[] jiraIssueIds = StringUtil.split(entry.getValue());

			Arrays.sort(jiraIssueIds, new NaturalOrderStringComparator());

			StringBundler sb2 = new StringBundler(jiraIssueIds.length * 2);

			for (String jiraIssueId : jiraIssueIds) {
				sb2.append(jiraIssueId);
				sb2.append(StringPool.COMMA);
			}

			if (sb2.index() > 0) {
				sb2.setIndex(sb2.index() - 1);
			}

			sb1.append(sb2.toString());
		}

		return sb1.toString();
	}

	private String _generateFeatureFlagUIProperties(
			String fileName, String absolutePath, String content)
		throws IOException {

		Matcher matcher = _featureFlagUIPattern.matcher(content);

		if (!matcher.find()) {
			return content;
		}

		String matchedFeatureFlags = matcher.group(2);

		if (!matchedFeatureFlags.contains("feature.flag.")) {
			return content;
		}

		Map<String, String> featureFlagUIPropertiesMap = new TreeMap<>(
			new NaturalOrderStringComparator());
		Map<String, String> featureFlagUICommonPropertiesMap = new TreeMap<>(
			new NaturalOrderStringComparator());

		Properties portalLanguageProperties = _getPortalLanguageProperties(
			absolutePath);

		Properties properties = new Properties();

		properties.load(new StringReader(matcher.group()));

		Enumeration<String> enumeration =
			(Enumeration<String>)properties.propertyNames();

		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();

			String value = properties.getProperty(key);

			if (!key.matches("feature\\.flag\\.[A-Z]+-\\d+\\.\\w+")) {
				featureFlagUICommonPropertiesMap.put(key, value);

				continue;
			}

			if (key.endsWith(".type") &&
				StringUtil.contains("beta,deprecation,release", value)) {

				int x = key.lastIndexOf(".");

				for (String enforcePropertyName : _ENFORCE_PROPERTY_NAMES) {
					String featureFlagUIPropertyName =
						key.substring(0, x) + "." + enforcePropertyName;

					if (properties.containsKey(featureFlagUIPropertyName)) {
						addMessage(
							fileName,
							"Property \"" + featureFlagUIPropertyName +
								"\" must be in Language.properties");
					}

					if (!portalLanguageProperties.containsKey(
							featureFlagUIPropertyName)) {

						addMessage(
							fileName,
							"Missing property \"" + featureFlagUIPropertyName +
								"\" in Language.properties");
					}
				}
			}

			featureFlagUIPropertiesMap.put(key, value);
		}

		String featureFlagUIProperties =
			_generateFeatureFlagUIProperties(featureFlagUICommonPropertiesMap) +
				_generateFeatureFlagUIProperties(featureFlagUIPropertiesMap);

		if (!StringUtil.equals(matchedFeatureFlags, featureFlagUIProperties)) {
			return StringUtil.replaceFirst(
				content, matchedFeatureFlags, featureFlagUIProperties,
				matcher.start(2));
		}

		return content;
	}

	private List<String> _getFeatureFlagKeys(String content, Pattern pattern) {
		List<String> featureFlagKeys = new ArrayList<>();

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			featureFlagKeys.add(matcher.group(1));
		}

		return featureFlagKeys;
	}

	private List<String>
		_getFeatureFlagKeysByFeatureFlagManagerUtilIsEnabledCall(
			String content, boolean javaSource) {

		List<String> featureFlagKeys = new ArrayList<>();

		int x = -1;

		while (true) {
			x = content.indexOf("FeatureFlagManagerUtil.isEnabled(", x + 1);

			if (x == -1) {
				return featureFlagKeys;
			}

			if (javaSource && ToolsUtil.isInsideQuotes(content, x)) {
				continue;
			}

			String methodCall = null;

			if (javaSource) {
				methodCall = JavaSourceUtil.getMethodCall(content, x);
			}
			else {
				methodCall = JavaSourceUtil.getMethodCall(
					content.substring(x), 0);
			}

			List<String> parameterList = JavaSourceUtil.getParameterList(
				methodCall);

			if (parameterList.isEmpty()) {
				continue;
			}

			String parameter = null;

			if (parameterList.size() == 1) {
				parameter = parameterList.get(0);
			}
			else {
				parameter = parameterList.get(1);
			}

			if (!parameter.endsWith(StringPool.QUOTE) ||
				!parameter.startsWith(StringPool.QUOTE)) {

				continue;
			}

			String unquotedParameterValue = StringUtil.unquote(parameter);

			if (!unquotedParameterValue.matches("[A-Z]+-\\d+")) {
				continue;
			}

			featureFlagKeys.add(unquotedParameterValue);
		}
	}

	private List<String> _getFeatureFlagKeysByMapUtilSingletonDictionaryCall(
		String content) {

		List<String> featureFlagKeys = new ArrayList<>();

		Matcher matcher = _mapUtilSingletonDictionaryPattern.matcher(content);

		while (matcher.find()) {
			List<String> parameterList = JavaSourceUtil.getParameterList(
				JavaSourceUtil.getMethodCall(content, matcher.start()));

			if (parameterList.size() != 2) {
				continue;
			}

			String parameter = parameterList.get(0);

			if (!parameter.equals("\"feature.flag.key\"")) {
				continue;
			}

			parameter = parameterList.get(1);

			if (!parameter.endsWith(StringPool.QUOTE) ||
				!parameter.startsWith(StringPool.QUOTE)) {

				continue;
			}

			String unquotedParameterValue = StringUtil.unquote(parameter);

			if (!unquotedParameterValue.matches("[A-Z]+-\\d+")) {
				continue;
			}

			featureFlagKeys.add(unquotedParameterValue);
		}

		return featureFlagKeys;
	}

	private Properties _getPortalLanguageProperties(String absolutePath)
		throws IOException {

		String portalLanguagePropertiesFileName = getAttributeValue(
			_PORTAL_LANGUAGE_PROPERTIES_FILE_NAME, absolutePath);

		Properties properties = new Properties();

		properties.load(
			new StringReader(
				getPortalContent(
					portalLanguagePropertiesFileName, absolutePath)));

		return properties;
	}

	private static final String[] _ENFORCE_PROPERTY_NAMES = {
		"description", "title"
	};

	private static final String _PORTAL_LANGUAGE_PROPERTIES_FILE_NAME =
		"portalLanguagePropertiesFileName";

	private static final Pattern _deprecationFeatureFlagPattern =
		Pattern.compile("feature\\.flag\\.([A-Z]+-\\d+)\\.type=deprecation");
	private static final Pattern _featureFlagPattern1 = Pattern.compile(
		"feature\\.flag[.=]([A-Z]+-\\d+)");
	private static final Pattern _featureFlagPattern2 = Pattern.compile(
		"Liferay\\.FeatureFlags\\['(.+?)'\\]");
	private static final Pattern _featureFlagPattern3 = Pattern.compile(
		"\"featureFlag\": \"(.+?)\"");
	private static final Pattern _featureFlagPattern4 = Pattern.compile(
		"\"feature\\.flag\\.key=([A-Z]+-\\d+)\"");
	private static final Pattern _featureFlagPattern5 = Pattern.compile(
		"featureFlagKey = \"([A-Z]+-\\d+)\"");
	private static final Pattern _featureFlagsPattern = Pattern.compile(
		"(\n|\\A)##\n## Feature Flag\n##(\n\n[\\s\\S]*?)(?=(\n\n##|\\Z))");
	private static final Pattern _featureFlagUIPattern = Pattern.compile(
		"(\n|\\A)##\n## Feature Flag UI\n##(\n\n[\\s\\S]*?)(?=(\n\n##|\\Z))");
	private static final Pattern _mapUtilSingletonDictionaryPattern =
		Pattern.compile("MapUtil\\.singletonDictionary\\(");

	private List<String> _allFileNames;

}