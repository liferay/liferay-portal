/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.BNDSettings;
import com.liferay.source.formatter.SourceFormatterExcludes;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class LanguageKeysCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		_checkLanguageKeys(fileName, absolutePath, content, getPatterns());

		return content;
	}

	protected List<Pattern> getPatterns() {
		return Arrays.asList(
			languageKeyPattern, _metaAnnotationDescriptionParameterPattern,
			_metaAnnotationNameParameterPattern);
	}

	protected final Pattern languageKeyPattern = Pattern.compile(
		"LanguageUtil.(?:get|format)\\([^;%]+|Liferay.Language.get\\('([^']+)");

	private void _checkLanguageKeys(
			String fileName, String absolutePath, String content,
			List<Pattern> patterns)
		throws IOException {

		if (fileName.endsWith(".vm")) {
			return;
		}

		Properties portalLanguageProperties = _getPortalLanguageProperties(
			absolutePath);

		if (portalLanguageProperties.isEmpty()) {
			return;
		}

		for (Pattern pattern : patterns) {
			_checkLanguageKeys(
				fileName, absolutePath, content, portalLanguageProperties,
				pattern);
		}
	}

	private void _checkLanguageKeys(
			String fileName, String absolutePath, String content,
			Properties portalLanguageProperties, Pattern pattern)
		throws IOException {

		Matcher matcher = pattern.matcher(content);

		Properties buildGradleLanguageProperties = null;
		Properties langModuleLanguageProperties = null;
		Properties moduleLanguageProperties = null;

		while (matcher.find()) {
			String[] languageKeys = _getLanguageKeys(matcher);

			for (String languageKey : languageKeys) {
				if (Validator.isNumber(languageKey) ||
					languageKey.endsWith(StringPool.CLOSE_CURLY_BRACE) ||
					languageKey.endsWith(StringPool.DASH) ||
					languageKey.endsWith(StringPool.OPEN_BRACKET) ||
					languageKey.endsWith(StringPool.PERIOD) ||
					languageKey.endsWith(StringPool.UNDERLINE) ||
					languageKey.startsWith(StringPool.DASH) ||
					languageKey.startsWith(StringPool.DOLLAR) ||
					languageKey.startsWith(StringPool.OPEN_BRACKET) ||
					languageKey.startsWith(StringPool.OPEN_CURLY_BRACE) ||
					languageKey.startsWith(StringPool.PERIOD) ||
					languageKey.startsWith(StringPool.UNDERLINE) ||
					portalLanguageProperties.containsKey(languageKey)) {

					continue;
				}

				if (moduleLanguageProperties == null) {
					moduleLanguageProperties = _getModuleLanguageProperties(
						fileName);
				}

				if (moduleLanguageProperties.containsKey(languageKey)) {
					continue;
				}

				if (buildGradleLanguageProperties == null) {
					buildGradleLanguageProperties =
						_getBuildGradleLanguageProperties(absolutePath);
				}

				if (buildGradleLanguageProperties.containsKey(languageKey)) {
					continue;
				}

				if (langModuleLanguageProperties == null) {
					langModuleLanguageProperties =
						_getLangModuleLanguageProperties(absolutePath);
				}

				if (langModuleLanguageProperties.containsKey(languageKey)) {
					continue;
				}

				BNDSettings bndSettings = getBNDSettings(fileName);

				if (bndSettings != null) {
					Properties bndLanguageProperties =
						bndSettings.getLanguageProperties();

					if ((bndLanguageProperties == null) ||
						bndLanguageProperties.containsKey(languageKey)) {

						continue;
					}
				}

				addMessage(
					fileName, "Missing language key \"" + languageKey + "\"");
			}
		}
	}

	private Properties _getBuildGradleLanguageProperties(String absolutePath)
		throws IOException {

		Properties properties = new Properties();

		String buildGradleContent = null;
		String buildGradleFileLocation = absolutePath;

		while (true) {
			int pos = buildGradleFileLocation.lastIndexOf(StringPool.SLASH);

			if (pos == -1) {
				return properties;
			}

			buildGradleFileLocation = buildGradleFileLocation.substring(
				0, pos + 1);

			File file = new File(buildGradleFileLocation + "build.gradle");

			if (file.exists()) {
				buildGradleContent = FileUtil.read(file);

				break;
			}

			buildGradleFileLocation = StringUtil.replaceLast(
				buildGradleFileLocation, CharPool.SLASH, StringPool.BLANK);
		}

		Matcher matcher = _applyLangMergerPluginPattern.matcher(
			buildGradleContent);

		if (!matcher.find()) {
			return properties;
		}

		String moduleLocation = StringUtil.replaceLast(
			buildGradleFileLocation, CharPool.SLASH, StringPool.BLANK);

		List<String> moduleLangDirNames = _getModuleLangDirNames(
			moduleLocation, buildGradleContent);

		for (String moduleLangDirName : moduleLangDirNames) {
			String moduleLangLanguagePropertiesFileName =
				moduleLangDirName + "/Language.properties";

			File file = new File(moduleLangLanguagePropertiesFileName);

			if (!file.exists()) {
				continue;
			}

			InputStream inputStream = new FileInputStream(file);

			properties.load(inputStream);
		}

		return properties;
	}

	private Properties _getLangModuleLanguageProperties(String absolutePath)
		throws IOException {

		Properties properties = new Properties();

		if (!isModulesFile(absolutePath)) {
			return properties;
		}

		List<String> langModulePaths = new ArrayList<>();

		String fileLocation = absolutePath;

		int x = fileLocation.length();

		while (true) {
			x = fileLocation.lastIndexOf(CharPool.SLASH, x - 1);

			if (x == -1) {
				break;
			}

			fileLocation = fileLocation.substring(0, x);

			if (fileLocation.endsWith("/modules")) {
				break;
			}

			File directory = new File(fileLocation);

			if (!directory.exists()) {
				continue;
			}

			for (File subdirectory : directory.listFiles(File::isDirectory)) {
				String subdirectoryPath = SourceUtil.getAbsolutePath(
					subdirectory);

				if (!subdirectoryPath.endsWith("-lang")) {
					continue;
				}

				langModulePaths.add(subdirectoryPath);
			}

			if (isSubrepository() &&
				FileUtil.exists(fileLocation + "/gradle.properties")) {

				break;
			}
		}

		for (String langModulePath : langModulePaths) {
			List<String> languagePropertyFileNames =
				SourceFormatterUtil.scanForFileNames(
					langModulePath, new String[0],
					new String[] {"**/resources/content/Language.properties"},
					new SourceFormatterExcludes(), true);

			if (languagePropertyFileNames.isEmpty()) {
				continue;
			}

			properties.load(
				new FileInputStream(
					new File(languagePropertyFileNames.get(0))));
		}

		if (absolutePath.contains("/modules/dxp/apps/")) {
			properties.putAll(
				_getLangModuleLanguageProperties(
					StringUtil.replace(
						absolutePath, "/modules/dxp/apps/", "/modules/apps/")));
		}

		return properties;
	}

	private String[] _getLanguageKeys(Matcher matcher) {
		int groupCount = matcher.groupCount();

		if (groupCount == 1) {
			String languageKey = matcher.group(1);

			if (Validator.isNotNull(languageKey)) {
				return new String[] {languageKey};
			}
		}
		else if (groupCount == 2) {
			String languageKey = matcher.group(2);

			languageKey = TextFormatter.format(languageKey, TextFormatter.K);

			return new String[] {languageKey};
		}

		StringBundler sb = new StringBundler();

		String match = matcher.group();

		int count = 0;

		for (int i = 0; i < match.length(); i++) {
			char c = match.charAt(i);

			if (c == CharPool.CLOSE_PARENTHESIS) {
				if (count <= 1) {
					return new String[0];
				}

				count--;
			}
			else if (c == CharPool.OPEN_PARENTHESIS) {
				count++;
			}
			else if ((c == CharPool.QUOTE) && (count <= 1)) {
				while (i < match.length()) {
					i++;

					if (match.charAt(i) == CharPool.QUOTE) {
						String languageKey = sb.toString();

						if (match.startsWith("names")) {
							return StringUtil.split(languageKey);
						}

						return new String[] {languageKey};
					}

					sb.append(match.charAt(i));
				}
			}
		}

		return new String[0];
	}

	private List<String> _getModuleLangDirNames(
		String moduleLocation, String buildGradleContent) {

		List<String> moduleLangDirNames = new ArrayList<>();

		Matcher matcher = _mergeLangPattern.matcher(buildGradleContent);

		if (matcher.find()) {
			String[] sourceDirs = StringUtil.split(matcher.group(1));

			for (String sourceDir : sourceDirs) {
				sourceDir = StringUtil.trim(sourceDir);

				moduleLangDirNames.add(
					moduleLocation + StringPool.SLASH +
						sourceDir.substring(1, sourceDir.length() - 1));
			}

			return moduleLangDirNames;
		}

		int x = moduleLocation.lastIndexOf(StringPool.SLASH);

		String baseModuleName = moduleLocation.substring(0, x);

		int y = baseModuleName.lastIndexOf(StringPool.SLASH);

		baseModuleName = baseModuleName.substring(y + 1);

		String moduleLangDirName =
			moduleLocation.substring(0, x + 1) + baseModuleName +
				"-lang/src/main/resources/content";

		File moduleLangDir = new File(moduleLangDirName);

		if (!moduleLangDir.exists() &&
			moduleLangDirName.contains("/modules/ee/")) {

			moduleLangDirName = StringUtil.replaceFirst(
				moduleLangDirName, "/modules/ee/", "/modules/");
		}

		moduleLangDirNames.add(moduleLangDirName);

		if (moduleLangDirName.contains("/modules/dxp/apps/")) {
			moduleLangDirNames.add(
				StringUtil.replace(
					moduleLangDirName, "/modules/dxp/apps/", "/modules/apps/"));
		}

		String projectName = getProjectName();

		if (Validator.isNotNull(projectName)) {
			String projectLangDirName = StringBundler.concat(
				moduleLocation.substring(0, x + 1), projectName,
				"-lang/src/main/resources/content");

			File projectLangDir = new File(projectLangDirName);

			if (projectLangDir.exists()) {
				moduleLangDirNames.add(projectLangDirName);
			}
		}

		return moduleLangDirNames;
	}

	private Properties _getModuleLanguageProperties(String fileName) {
		Properties properties = new Properties();

		StringBundler sb = new StringBundler(3);

		int pos = fileName.indexOf("/docroot/");

		if (pos != -1) {
			sb.append(fileName.substring(0, pos + 9));
			sb.append("WEB-INF/src/");
		}
		else {
			pos = fileName.indexOf("src/");

			if (pos == -1) {
				return properties;
			}

			sb.append(fileName.substring(0, pos + 4));

			if (fileName.contains("src/main/")) {
				sb.append("main/resources/");
			}
		}

		sb.append("content/Language.properties");

		try {
			properties = new Properties();

			InputStream inputStream = new FileInputStream(sb.toString());

			properties.load(inputStream);

			return properties;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return properties;
	}

	private synchronized Properties _getPortalLanguageProperties(
			String absolutePath)
		throws IOException {

		if (_portalLanguageProperties != null) {
			return _portalLanguageProperties;
		}

		_portalLanguageProperties = new Properties();

		String portalLanguagePropertiesFileName = getAttributeValue(
			_PORTAL_LANGUAGE_PROPERTIES_FILE_NAME, absolutePath);

		if (Validator.isNull(portalLanguagePropertiesFileName)) {
			return _portalLanguageProperties;
		}

		String propertiesContent = getPortalContent(
			portalLanguagePropertiesFileName, absolutePath);

		Properties properties = new Properties();

		properties.load(new StringReader(propertiesContent));

		_portalLanguageProperties.putAll(properties);

		return _portalLanguageProperties;
	}

	private static final String _PORTAL_LANGUAGE_PROPERTIES_FILE_NAME =
		"portalLanguagePropertiesFileName";

	private static final Log _log = LogFactoryUtil.getLog(
		LanguageKeysCheck.class);

	private static final Pattern _applyLangMergerPluginPattern =
		Pattern.compile(
			"^apply[ \t]+plugin[ \t]*:[ \t]+\"com.liferay.lang.merger\"$",
			Pattern.MULTILINE);
	private static final Pattern _mergeLangPattern = Pattern.compile(
		"mergeLang \\{.*sourceDirs = \\[(.*?)\\]", Pattern.DOTALL);
	private static final Pattern _metaAnnotationDescriptionParameterPattern =
		Pattern.compile(
			"@Meta\\.(?:AD|OCD)\\([^\\{]*?description\\s*=\\s*\"(.+?)\"");
	private static final Pattern _metaAnnotationNameParameterPattern =
		Pattern.compile("@Meta\\.(?:AD|OCD)\\([^\\{]*?name\\s*=\\s*\"(.+?)\"");

	private Properties _portalLanguageProperties;

}