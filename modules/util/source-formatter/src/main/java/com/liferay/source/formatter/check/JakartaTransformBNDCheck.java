/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ToolsUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class JakartaTransformBNDCheck extends BaseJakartaTransformCheck {

	@Override
	protected String format(
			String fileName, String absolutePath, String content)
		throws IOException {

		content = _formatHeaders(content);
		content = replace(content);

		return replaceTaglibURIs(content);
	}

	@Override
	protected String[] getValidExtensions() {
		return _VALID_EXTENSIONS;
	}

	private String _formatHeaders(String content) throws IOException {
		Properties properties = new Properties();

		properties.load(new StringReader(content));

		_replaceRequireCapability(properties);

		return _toString(properties);
	}

	private String _formatParameters(
		Parameters parameters, String propertyName) {

		StringBundler sb = new StringBundler();

		for (Map.Entry<String, Attrs> entry : parameters.entrySet()) {
			String parameterKey = entry.getKey();

			sb.append("\t");
			sb.append(parameterKey.replaceAll("(.+?)~+", "$1"));

			String attrsString = String.valueOf(entry.getValue());

			if (attrsString.isBlank()) {
				sb.append(",\\\n");

				continue;
			}

			if (!propertyName.equals("Provide-Capability") &&
				!propertyName.equals("Require-Capability")) {

				sb.append(";");
				sb.append(attrsString);
				sb.append(",\\\n");

				continue;
			}

			attrsString = "\t\t" + attrsString;

			int x = -1;

			while (true) {
				x = attrsString.indexOf(";", x + 1);

				if (x == -1) {
					break;
				}

				if (ToolsUtil.isInsideQuotes(attrsString, x)) {
					continue;
				}

				attrsString = StringUtil.replaceFirst(
					attrsString, ";", ";\\\n\t\t", x);
			}

			sb.append(";\\\n");
			sb.append(attrsString);
			sb.append(",\\\n");
		}

		if (sb.length() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private synchronized Map<String, String>
			_getJakartaTransformOSGiContractsMap()
		throws IOException {

		if (_jakartaTransformOSGiContractsMap != null) {
			return _jakartaTransformOSGiContractsMap;
		}

		_jakartaTransformOSGiContractsMap = new HashMap<>();

		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			"dependencies/jakarta-transform-osgi-contracts.txt");

		if (inputStream == null) {
			return Collections.emptyMap();
		}

		String[] lines = StringUtil.splitLines(StringUtil.read(inputStream));

		for (String line : lines) {
			String[] parts = line.split("=");

			_jakartaTransformOSGiContractsMap.put(parts[0], parts[1]);
		}

		return _jakartaTransformOSGiContractsMap;
	}

	private void _replaceRequireCapability(Properties properties)
		throws IOException {

		String requireCapability = properties.getProperty("Require-Capability");

		if (requireCapability == null) {
			return;
		}

		Map<String, String> jakartaTransformOSGiContractsMap =
			_getJakartaTransformOSGiContractsMap();

		Parameters parameters = new Parameters(requireCapability);

		for (Map.Entry<String, Attrs> entry : parameters.entrySet()) {
			String parameterKey = entry.getKey();

			if (!parameterKey.matches("osgi\\.contract~*")) {
				continue;
			}

			Attrs attrs = entry.getValue();

			String osgiContract = attrs.get("osgi.contract");

			if (osgiContract != null) {
				String newContract = jakartaTransformOSGiContractsMap.get(
					osgiContract);

				if (newContract == null) {
					continue;
				}

				String[] values = newContract.split(":");

				attrs.put("osgi.contract", values[0]);

				String version = attrs.get("version");

				if (version != null) {
					attrs.put("version", values[1]);
				}

				continue;
			}

			String filter = attrs.get("filter:");

			if (filter == null) {
				continue;
			}

			Matcher matcher = _osgiContractPattern.matcher(filter);

			if (!matcher.find()) {
				continue;
			}

			osgiContract = matcher.group(1);

			String newContract = jakartaTransformOSGiContractsMap.get(
				osgiContract);

			if (newContract == null) {
				continue;
			}

			String[] values = newContract.split(":");

			filter = StringUtil.replaceFirst(
				filter, osgiContract, values[0], matcher.start(1));
			filter = StringUtil.replaceFirst(
				filter, matcher.group(2), values[1], matcher.start(2));

			attrs.put("filter:", filter);
		}

		properties.setProperty("Require-Capability", parameters.toString());
	}

	private String _toString(Properties properties) {
		List<String> propertyNames = new ArrayList<>(
			properties.stringPropertyNames());

		Collections.sort(propertyNames, new HeaderComparator());

		StringBundler sb = new StringBundler(propertyNames.size() * 4);

		for (String propertyName : propertyNames) {
			sb.append(propertyName);

			Parameters parameters = new Parameters(
				properties.getProperty(propertyName));

			String parametersString = _formatParameters(
				parameters, propertyName);

			if (!parametersString.contains("\n")) {
				parametersString = parametersString.trim();
				sb.append(": ");
			}
			else {
				sb.append(":\\\n");
			}

			sb.append(parametersString);
			sb.append("\n");
		}

		if (sb.length() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private static final String[] _VALID_EXTENSIONS = {".bnd"};

	private static final Pattern _osgiContractPattern = Pattern.compile(
		"\\(osgi\\.contract!?=(\\w+)\\)\\(version[<>]?=([\\d.]+)\\)");

	private Map<String, String> _jakartaTransformOSGiContractsMap;

	private static class HeaderComparator implements Comparator<String> {

		@Override
		public int compare(String header1, String header2) {
			if (header1.startsWith(StringPool.DASH) ^
				header2.startsWith(StringPool.DASH)) {

				return -header1.compareTo(header2);
			}

			String headerName1 = StringUtil.extractFirst(
				header1, StringPool.COLON);
			String headerName2 = StringUtil.extractFirst(
				header2, StringPool.COLON);

			if ((headerName1 != null) && (headerName2 != null)) {
				return headerName1.compareTo(headerName2);
			}

			return header1.compareTo(header2);
		}

	}

}