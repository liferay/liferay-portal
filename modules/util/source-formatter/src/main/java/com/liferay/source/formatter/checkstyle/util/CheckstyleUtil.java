/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.util;

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONArrayImpl;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.SourceFormatterArgs;
import com.liferay.source.formatter.util.CheckType;
import com.liferay.source.formatter.util.DebugUtil;
import com.liferay.source.formatter.util.SourceFormatterCheckUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.xml.sax.InputSource;

/**
 * @author Hugo Huijser
 */
public class CheckstyleUtil {

	public static final String BASE_DIR_NAME_KEY = "baseDirName";

	public static final int BATCH_SIZE = 1000;

	public static final String FILTER_CHECK_NAMES_KEY = "filterCheckNames";

	public static final String MAX_DIR_LEVEL_KEY = "maxDirLevel";

	public static final String MAX_LINE_LENGTH_KEY = "maxLineLength";

	public static final String SHOW_DEBUG_INFORMATION_KEY =
		"showDebugInformation";

	public static Configuration getConfiguration(
			String configurationFileName, Map<String, Properties> propertiesMap,
			SourceFormatterArgs sourceFormatterArgs)
		throws CheckstyleException {

		ClassLoader classLoader = CheckstyleUtil.class.getClassLoader();

		Configuration configuration = ConfigurationLoader.loadConfiguration(
			new InputSource(
				classLoader.getResourceAsStream(configurationFileName)),
			new PropertiesExpander(System.getProperties()),
			ConfigurationLoader.IgnoredModulesOptions.EXECUTE);

		DefaultConfiguration treeWalkerDefaultConfiguration =
			_getChildDefaultConfiguration(configuration, "TreeWalker");

		Configuration[] checkConfigurations =
			treeWalkerDefaultConfiguration.getChildren();

		if (checkConfigurations == null) {
			return configuration;
		}

		JSONObject excludesJSONObject =
			SourceFormatterCheckUtil.getExcludesJSONObject(propertiesMap);

		List<String> checkNames = new ArrayList<>();

		List<String> filterCheckCategoryNames =
			sourceFormatterArgs.getCheckCategoryNames();
		List<String> filterCheckNames = sourceFormatterArgs.getCheckNames();

		for (Configuration checkConfiguration : checkConfigurations) {
			if (!(checkConfiguration instanceof DefaultConfiguration)) {
				continue;
			}

			String checkCategory = checkConfiguration.getAttribute("category");

			String checkName = checkConfiguration.getName();

			String checkSimpleName = SourceFormatterUtil.getSimpleName(
				checkName);

			if ((checkCategory.startsWith("Upgrade") &&
				 !filterCheckCategoryNames.contains(checkCategory)) ||
				((!filterCheckCategoryNames.isEmpty() ||
				  !filterCheckNames.isEmpty()) &&
				 !filterCheckCategoryNames.contains(checkCategory) &&
				 !filterCheckNames.contains(checkSimpleName))) {

				treeWalkerDefaultConfiguration.removeChild(checkConfiguration);

				continue;
			}

			DefaultConfiguration defaultConfiguration =
				new DefaultConfiguration(checkName);

			Map<String, String> messages = checkConfiguration.getMessages();

			for (Map.Entry<String, String> entry : messages.entrySet()) {
				defaultConfiguration.addMessage(
					entry.getKey(), entry.getValue());
			}

			if (checkName.startsWith("com.liferay.")) {
				checkNames.add(checkSimpleName);

				if (excludesJSONObject.length() != 0) {
					defaultConfiguration.addAttribute(
						_EXCLUDES_KEY, excludesJSONObject.toString());
				}

				defaultConfiguration.addAttribute(
					_ATTRIBUTES_KEY,
					String.valueOf(
						_getAttributesJSONObject(
							propertiesMap, checkSimpleName, checkConfiguration,
							sourceFormatterArgs)));
			}
			else {
				for (String attributeName :
						checkConfiguration.getAttributeNames()) {

					if (attributeName.equals("category") ||
						attributeName.equals("description") ||
						attributeName.equals("documentationLocation")) {

						continue;
					}

					defaultConfiguration.addAttribute(
						attributeName,
						checkConfiguration.getAttribute(attributeName));
				}
			}

			treeWalkerDefaultConfiguration.removeChild(checkConfiguration);

			treeWalkerDefaultConfiguration.addChild(defaultConfiguration);
		}

		if (sourceFormatterArgs.isShowDebugInformation()) {
			DebugUtil.addCheckNames(CheckType.CHECKSTYLE, checkNames);
		}

		return configuration;
	}

	public static List<String> getLines(String s) throws IOException {
		List<String> lines = new ArrayList<>();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(s))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				lines.add(line);
			}
		}

		return lines;
	}

	private static JSONObject _addCustomAttributes(
		JSONObject jsonObject, String[][] attributesArray) {

		for (String[] values : attributesArray) {
			JSONArray jsonArray = new JSONArrayImpl();

			jsonArray.put(values[1]);

			jsonObject.put(values[0], jsonArray);
		}

		return jsonObject;
	}

	private static JSONObject _getAttributesJSONObject(
			Map<String, Properties> propertiesMap, String checkName,
			Configuration configuration,
			SourceFormatterArgs sourceFormatterArgs)
		throws CheckstyleException {

		JSONObject attributesJSONObject = new JSONObjectImpl();

		JSONObject configurationAttributesJSONObject =
			_getConfigurationAttributesJSONObject(configuration);

		configurationAttributesJSONObject = _addCustomAttributes(
			configurationAttributesJSONObject,
			new String[][] {
				{BASE_DIR_NAME_KEY, sourceFormatterArgs.getBaseDirName()},
				{
					FILTER_CHECK_NAMES_KEY,
					StringUtil.merge(sourceFormatterArgs.getCheckNames())
				},
				{
					MAX_DIR_LEVEL_KEY,
					String.valueOf(sourceFormatterArgs.getMaxDirLevel())
				},
				{
					MAX_LINE_LENGTH_KEY,
					String.valueOf(sourceFormatterArgs.getMaxLineLength())
				},
				{
					SHOW_DEBUG_INFORMATION_KEY,
					String.valueOf(sourceFormatterArgs.isShowDebugInformation())
				}
			});

		attributesJSONObject.put(
			SourceFormatterCheckUtil.CONFIGURATION_FILE_LOCATION,
			configurationAttributesJSONObject);

		attributesJSONObject = SourceFormatterCheckUtil.addPropertiesAttributes(
			attributesJSONObject, propertiesMap,
			SourceFormatterUtil.GIT_LIFERAY_PORTAL_BRANCH,
			SourceFormatterUtil.UPGRADE_FROM_VERSION,
			SourceFormatterUtil.UPGRADE_TO_LIFERAY_VERSION,
			SourceFormatterUtil.UPGRADE_TO_RELEASE_VERSION);

		attributesJSONObject = SourceFormatterCheckUtil.addPropertiesAttributes(
			attributesJSONObject, propertiesMap, CheckType.CHECKSTYLE,
			checkName);

		return attributesJSONObject;
	}

	private static DefaultConfiguration _getChildDefaultConfiguration(
		Configuration configuration, String name) {

		if (!(configuration instanceof DefaultConfiguration)) {
			return null;
		}

		DefaultConfiguration defaultConfiguration =
			(DefaultConfiguration)configuration;

		for (Configuration childConfiguration :
				defaultConfiguration.getChildren()) {

			String configurationName = childConfiguration.getName();

			if (configurationName.equals(name) &&
				(childConfiguration instanceof DefaultConfiguration)) {

				return (DefaultConfiguration)childConfiguration;
			}
		}

		return null;
	}

	private static JSONObject _getConfigurationAttributesJSONObject(
			Configuration configuration)
		throws CheckstyleException {

		JSONObject configurationAttributesJSONObject = new JSONObjectImpl();

		for (String attributeName : configuration.getAttributeNames()) {
			JSONArray jsonArray = new JSONArrayImpl();

			String[] values = StringUtil.split(
				configuration.getAttribute(attributeName), StringPool.COMMA);

			for (String value : values) {
				jsonArray.put(value);
			}

			configurationAttributesJSONObject.put(attributeName, jsonArray);
		}

		return configurationAttributesJSONObject;
	}

	private static final String _ATTRIBUTES_KEY = "attributes";

	private static final String _EXCLUDES_KEY = "excludes";

}