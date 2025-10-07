/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.processor.PropertiesSourceProcessor;

import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;

/**
 * @author Hugo Huijser
 */
public class PropertiesPortalFileCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		int pos = fileName.lastIndexOf(StringPool.SLASH);

		String shortFileName = fileName.substring(pos + 1);

		if (absolutePath.contains("/workspaces/") &&
			!absolutePath.contains("/playwright/") &&
			shortFileName.equals("portal-ext.properties")) {

			return content;
		}

		if (shortFileName.equals("test-portal-impl.properties") ||
			((isPortalSource() || isSubrepository()) &&
			 shortFileName.startsWith("portal") &&
			 !shortFileName.contains("-legacy-") &&
			 !shortFileName.equals("portal-osgi-configuration.properties") &&
			 !shortFileName.equals("portal-upgrade-database.properties") &&
			 !shortFileName.equals("portal-upgrade-ext.properties")) ||
			(!isPortalSource() && !isSubrepository() &&
			 (shortFileName.equals("portal.properties") ||
			  shortFileName.equals("portal-ext.properties")))) {

			content = _sortPortalProperties(absolutePath, content);

			content = _formatPortalProperties(absolutePath, content);
		}

		return content;
	}

	private String _formatPortalProperties(String absolutePath, String content)
		throws IOException {

		List<String> allowedSingleLinePropertyKeys = getAttributeValues(
			_ALLOWED_SINGLE_LINE_PROPERTY_KEYS, absolutePath);

		StringBundler sb = new StringBundler();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				if (line.matches("    [^# ]+?=[^,]+(,[^ ][^,]+)+")) {
					String propertyKey = StringUtil.extractFirst(
						StringUtil.trimLeading(line), "=");

					if (!propertyKey.contains("regex") &&
						!allowedSingleLinePropertyKeys.contains(propertyKey)) {

						line = line.replaceFirst("=", "=\\\\\n        ");

						line = line.replaceAll(",", ",\\\\\n        ");
					}
				}

				sb.append(line);
				sb.append("\n");
			}
		}

		content = sb.toString();

		if (content.endsWith("\n")) {
			content = content.substring(0, content.length() - 1);
		}

		return content;
	}

	private String _generateProperties(Map<String, List<String>> properties) {
		StringBundler sb = new StringBundler();

		for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
			List<String> values = entry.getValue();

			if ((values.size() > 1) && (sb.length() > 0) &&
				!StringUtil.endsWith(sb.toString(), "\n\n")) {

				sb.append("\n");
			}

			sb.append(entry.getKey());
			sb.append(StringPool.EQUAL);

			if (values.size() > 1) {
				String mergedValues = _mergeValues(entry.getValue());

				if (!mergedValues.startsWith(StringPool.OPEN_BRACKET)) {
					sb.append("\\\n");
				}

				sb.append(mergedValues);
				sb.append("\n");
			}
			else {
				sb.append(values.get(0));
			}

			sb.append("\n");
		}

		if (sb.length() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private synchronized String _getPortalPropertiesContent(String absolutePath)
		throws IOException {

		if (_portalPropertiesContent != null) {
			return _portalPropertiesContent;
		}

		if (isPortalSource() || isSubrepository()) {
			_portalPropertiesContent = getPortalContent(
				"portal-impl/src/portal.properties", absolutePath);

			if (_portalPropertiesContent == null) {
				_portalPropertiesContent = StringPool.BLANK;
			}

			return _portalPropertiesContent;
		}

		ClassLoader classLoader =
			PropertiesSourceProcessor.class.getClassLoader();

		URL url = classLoader.getResource("portal.properties");

		if (url != null) {
			_portalPropertiesContent = IOUtils.toString(url);
		}
		else {
			_portalPropertiesContent = StringPool.BLANK;
		}

		return _portalPropertiesContent;
	}

	private boolean _hasPortalPropertiesCommonPrefixes(String propertyKey) {
		for (String portalPropertiesCommonPrefix :
				_PORTAL_PROPERTIES_COMMON_PREFIXES) {

			if (propertyKey.startsWith(portalPropertiesCommonPrefix)) {
				return true;
			}
		}

		return false;
	}

	private String _mergeValues(List<String> values) {
		StringBundler sb = new StringBundler(3 * values.size());

		for (String value : values) {
			if (!StringUtil.equals(value, StringPool.CLOSE_BRACKET) &&
				!StringUtil.equals(value, StringPool.OPEN_BRACKET)) {

				sb.append(StringPool.FOUR_SPACES);
			}

			sb.append(value);

			if (StringUtil.equals(value, StringPool.BACK_SLASH)) {
				sb.append("\n");
			}
			else if (StringUtil.equals(value, StringPool.OPEN_BRACKET)) {
				sb.append("\\\n");
			}
			else {
				sb.append(",\\\n");
			}
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	private String _sortPortalProperties(String absolutePath, String content)
		throws IOException {

		if (absolutePath.endsWith("/portal-impl/src/portal.properties")) {
			return content;
		}

		Map<String, List<String>> propertiesMap = new TreeMap<>(
			new NaturalOrderStringComparator());

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String key = null;
			String line = null;
			String value = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				line = line.trim();

				if (Validator.isNull(line) ||
					line.startsWith(StringPool.POUND)) {

					continue;
				}

				int x = line.indexOf('=');

				if (x != -1) {
					key = line.substring(0, x);

					if (propertiesMap.containsKey(key)) {
						return content;
					}

					value = line.substring(x + 1);

					if ((value != null) && !value.equals("\\")) {
						List<String> list = propertiesMap.get(key);

						if (list == null) {
							list = new ArrayList<>();
						}

						if (value.equals("[\\")) {
							value = StringUtil.removeLast(value, "\\");
						}

						list.add(value);

						propertiesMap.put(key, list);
					}
				}
				else {
					value = line;

					if (value.endsWith(",")) {
						value = value.substring(0, value.length() - 1);
					}
					else if (value.endsWith(",\\")) {
						value = value.substring(0, value.length() - 2);
					}

					if (key == null) {
						return content;
					}

					List<String> list = propertiesMap.get(key);

					if (list == null) {
						list = new ArrayList<>();
					}

					list.add(value);

					propertiesMap.put(key, list);
				}
			}
		}

		String portalPropertiesContent = _getPortalPropertiesContent(
			absolutePath);

		Map<String, List<String>> portalOSGiEnvironmentPropertiesMap =
			new TreeMap<>(new NaturalOrderStringComparator());
		Map<String, List<String>> portalPropertiesMap = new TreeMap<>(
			new PortalPropertiesComparator());

		Set<Map.Entry<String, List<String>>> entrySet =
			propertiesMap.entrySet();

		Iterator<Map.Entry<String, List<String>>> iterator =
			entrySet.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, List<String>> properties = iterator.next();

			String propertyKey = properties.getKey();

			if (!Validator.isChar(propertyKey.charAt(0))) {
				continue;
			}

			if (portalPropertiesContent.contains(propertyKey + "=") ||
				portalPropertiesContent.contains("#" + propertyKey + "=") ||
				_hasPortalPropertiesCommonPrefixes(propertyKey)) {

				portalPropertiesMap.put(
					propertyKey, propertiesMap.get(propertyKey));
				iterator.remove();
			}
			else if (propertyKey.startsWith(
						"configuration.override.com.liferay.")) {

				portalOSGiEnvironmentPropertiesMap.put(
					propertyKey, propertiesMap.get(propertyKey));
				iterator.remove();
			}
		}

		StringBundler sb = new StringBundler(6);

		for (Map<String, List<String>> map :
				Arrays.asList(
					portalPropertiesMap, propertiesMap,
					portalOSGiEnvironmentPropertiesMap)) {

			if (map.isEmpty()) {
				continue;
			}

			String propertiesString = _generateProperties(map);

			if (Validator.isBlank(propertiesString)) {
				continue;
			}

			sb.append(propertiesString);
			sb.append("\n\n");
		}

		String newContent = StringUtil.trim(sb.toString());

		if (!StringUtil.equals(content, newContent)) {
			return newContent;
		}

		return content;
	}

	private static final String _ALLOWED_SINGLE_LINE_PROPERTY_KEYS =
		"allowedSingleLinePropertyKeys";

	private static final String[] _PORTAL_PROPERTIES_COMMON_PREFIXES = {
		"com.liferay.portal.servlet.filters.",
		"data.limit.model.max.count[com.liferay.", "module.framework."
	};

	private String _portalPropertiesContent;

	private class PortalPropertiesComparator
		extends NaturalOrderStringComparator {

		public PortalPropertiesComparator() {
			for (String portalPropertiesCommonPrefix :
					_PORTAL_PROPERTIES_COMMON_PREFIXES) {

				int x = _portalPropertiesContent.lastIndexOf(
					StringPool.FOUR_SPACES + portalPropertiesCommonPrefix);

				if (x == -1) {
					x = _portalPropertiesContent.lastIndexOf(
						StringPool.FOUR_SPACES + StringPool.POUND +
							portalPropertiesCommonPrefix);
				}

				_commonPrefixesLastPositionsMap.put(
					portalPropertiesCommonPrefix, x);
			}
		}

		@Override
		public int compare(String propertyKey1, String propertyKey2) {
			int propertyKey1Position = _getPortalPropertiesPosition(
				_portalPropertiesContent, propertyKey1);
			int propertyKey2Position = _getPortalPropertiesPosition(
				_portalPropertiesContent, propertyKey2);

			if ((propertyKey1Position != -1) && (propertyKey2Position != -1)) {
				return propertyKey1Position - propertyKey2Position;
			}

			int propertiesLastPosition1 = _getCommonPrefixLastPosition(
				propertyKey1);

			if ((propertyKey1Position == -1) && (propertyKey2Position != -1)) {
				if (propertyKey2Position <= propertiesLastPosition1) {
					return 1;
				}

				return -1;
			}

			int propertiesLastPosition2 = _getCommonPrefixLastPosition(
				propertyKey2);

			if ((propertyKey1Position != -1) && (propertyKey2Position == -1)) {
				if (propertyKey1Position <= propertiesLastPosition2) {
					return -1;
				}

				return 1;
			}

			if (propertiesLastPosition1 != propertiesLastPosition2) {
				return propertiesLastPosition1 - propertiesLastPosition2;
			}

			return super.compare(propertyKey1, propertyKey2);
		}

		private int _getCommonPrefixLastPosition(String propertyKey) {
			for (Map.Entry<String, Integer> entry :
					_commonPrefixesLastPositionsMap.entrySet()) {

				if (StringUtil.startsWith(propertyKey, entry.getKey())) {
					return entry.getValue();
				}
			}

			return -1;
		}

		private int _getPortalPropertiesPosition(
			String content, String propertyKey) {

			int x = content.indexOf("    " + propertyKey + "=");

			if (x == -1) {
				x = content.indexOf("    #" + propertyKey + "=");
			}

			return x;
		}

		private final Map<String, Integer> _commonPrefixesLastPositionsMap =
			new HashMap<>();

	}

}