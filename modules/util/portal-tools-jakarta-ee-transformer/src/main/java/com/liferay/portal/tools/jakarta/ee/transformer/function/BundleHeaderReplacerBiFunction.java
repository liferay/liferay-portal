/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.jakarta.ee.transformer.function;

import com.liferay.portal.tools.jakarta.ee.transformer.TransformerAgent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Shuyang Zhou
 */
public class BundleHeaderReplacerBiFunction
	implements BiFunction<String, Map<Object, Object>, Map<Object, Object>> {

	public static final BiFunction
		<String, Map<Object, Object>, Map<Object, Object>> INSTANCE =
			new BundleHeaderReplacerBiFunction();

	@Override
	public Map<Object, Object> apply(
		String invoker, Map<Object, Object> headerMap) {

		Map<Object, Object> modifiedHeaderMap = new LinkedHashMap<>();

		StringBuilder sb = new StringBuilder();

		for (Map.Entry<Object, Object> entry : headerMap.entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();

			if ((value instanceof String) &&
				!Objects.equals(key.toString(), "Bundle-ClassPath")) {

				String newValue = TransformerAgent.replace(
					TransformerAgent.replacementDashDotMap, (String)value);

				if (Objects.equals(key.toString(), "Import-Package")) {
					newValue = _fixDuplication(newValue);

					newValue = _fixVersion(newValue);
				}
				else if (Objects.equals(key.toString(), "Export-Package")) {
					newValue = _fixVersion(newValue);
				}

				newValue = _fixContract(newValue);

				if (!Objects.equals(value, newValue)) {
					sb.append("key: ");
					sb.append(entry.getKey());
					sb.append(", old value: ");
					sb.append(value);
					sb.append(", new value: ");
					sb.append(newValue);
					sb.append(", ");
				}

				value = newValue;
			}

			modifiedHeaderMap.put(key, value);
		}

		if (sb.length() != 0) {
			sb.setLength(sb.length() - 2);
			sb.append(']');
		}

		if (!_JAKARTA_EE_TRANSFORMER_BUNDLE_HEADER_REPLACER_LOGGING_DISABLED &&
			!sb.isEmpty()) {

			System.err.println(
				"JakartaEETransformer#BundleHeaderReplacer#" + invoker + "[" +
					sb.toString());
		}

		return modifiedHeaderMap;
	}

	private static Map<String, String> _loadOSGiContracts() throws IOException {
		Map<String, String> osgiContracts = new HashMap<>();

		try (InputStream inputStream =
				BundleHeaderReplacerBiFunction.class.getResourceAsStream(
					"dependencies/osgi-contract.properties");
			Reader reader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(reader)) {

			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}

				String[] parts = line.split("=");

				osgiContracts.put(parts[0], parts[1]);
			}
		}

		return osgiContracts;
	}

	private static Map<String, String> _loadPackageVersions()
		throws IOException {

		Map<String, String> packageVersions = new HashMap<>();

		try (InputStream inputStream =
				BundleHeaderReplacerBiFunction.class.getResourceAsStream(
					"dependencies/package-version.properties");
			Reader reader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(reader)) {

			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}

				String[] parts = line.split("=");

				packageVersions.put(parts[0], parts[1]);
			}
		}

		return packageVersions;
	}

	private int _count(String s, int start, int end, char c) {
		if ((s == null) || s.isEmpty() || ((end - start) < 1)) {
			return 0;
		}

		int count = 0;

		int pos = start;

		while ((pos < end) && ((pos = s.indexOf(c, pos)) != -1)) {
			if (pos < end) {
				count++;
			}

			pos++;
		}

		return count;
	}

	private String _fixContract(String value) {
		if (!value.contains("osgi.contract")) {
			return value;
		}

		StringBuilder sb = new StringBuilder();

		for (String capability : _splitLines(value)) {
			if (!capability.contains("osgi.contract=")) {
				sb.append(capability);

				continue;
			}

			Matcher matcher = _contractPattern.matcher(capability);

			if (!matcher.find()) {

				// Invalid contract

				sb.append(capability);

				continue;
			}

			String contract = matcher.group(1);

			String newContract = _osgiContracts.get(contract);

			if (newContract == null) {

				// Unknown contract

				sb.append(capability);

				continue;
			}

			String[] values = newContract.split(":");

			capability = capability.replace(contract, values[0]);

			if (capability.contains("uses:")) {
				matcher = _versionListPattern.matcher(capability);

				if (!matcher.find()) {

					// Invalid contract

					sb.append(capability);

					continue;
				}

				String versionList = matcher.group();

				capability = capability.replace(
					versionList,
					versionList.replace(matcher.group(3), values[1]));
			}
			else {
				matcher = _versionPattern.matcher(capability);

				if (!matcher.find()) {

					// Invalid contract

					sb.append(capability);

					continue;
				}

				String version = matcher.group();

				capability = capability.replace(
					version, version.replace(matcher.group(1), values[1]));
			}

			sb.append(capability);
		}

		return sb.toString();
	}

	private String _fixDuplication(String importPackages) {
		Set<String> cleanPackages = new HashSet<>();

		StringBuilder sb = new StringBuilder();

		for (String importPackage : importPackages.split(",(?=\\D)")) {
			String cleanPackage = importPackage;

			int index = importPackage.indexOf(';');

			if (index != -1) {
				cleanPackage = importPackage.substring(0, index);
			}

			if (cleanPackages.add(cleanPackage)) {
				sb.append(importPackage);
				sb.append(',');
			}
		}

		if (sb.length() != 0) {
			sb.setLength(sb.length() - 1);
		}

		return sb.toString();
	}

	private String _fixVersion(String content) {
		List<String> packages = _splitPackages(content);

		for (int i = 0; i < packages.size(); i++) {
			String packageString = packages.get(i);

			String packageKey = packageString;

			int index = packageString.indexOf(';');

			if (index != -1) {
				packageKey = packageString.substring(0, index);
			}

			String version = _packageVersions.get(packageKey);

			if (version == null) {
				continue;
			}

			index = packageString.indexOf(";version=\"");

			if (index == -1) {
				packageString += ";version=\"" + version + "\"";
			}
			else {
				int closeIndex = packageString.indexOf('"', index + 10);

				packageString =
					packageString.substring(0, index + 10) + version +
						packageString.substring(closeIndex);
			}

			packages.set(i, packageString);
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < packages.size(); i++) {
			sb.append(packages.get(i));

			if (i < (packages.size() - 1)) {
				sb.append(',');
			}
		}

		return sb.toString();
	}

	private boolean _isValidComma(String content, int start, int end) {
		int count = 0;

		for (int i = start; i < end; i++) {
			if (content.charAt(i) == '"') {
				count++;
			}
		}

		if ((count % 2) == 0) {
			return true;
		}

		return false;
	}

	private List<String> _splitLines(String value) {
		List<String> lines = new ArrayList<>();

		int previousIndex = 0;
		int index = 0;

		while ((index = value.indexOf(',', index)) != -1) {
			index++;

			if ((_count(value, 0, index, '"') % 2) == 1) {
				continue;
			}

			lines.add(value.substring(previousIndex, index));

			previousIndex = index;
		}

		lines.add(value.substring(previousIndex));

		return lines;
	}

	private List<String> _splitPackages(String content) {
		List<String> packages = new ArrayList<>();

		int startIndex = 0;

		int scanIndex = 0;

		int index = -1;

		while ((index = content.indexOf(',', scanIndex)) != -1) {
			scanIndex = index + 1;

			if (_isValidComma(content, startIndex, index)) {
				packages.add(content.substring(startIndex, index));

				startIndex = scanIndex;
			}
		}

		packages.add(content.substring(startIndex));

		return packages;
	}

	private static final boolean
		_JAKARTA_EE_TRANSFORMER_BUNDLE_HEADER_REPLACER_LOGGING_DISABLED =
			Boolean.getBoolean(
				"jakarta.ee.transformer.bundle.header.replacer.logging." +
					"disabled");

	private static final Pattern _contractPattern = Pattern.compile(
		"osgi.contract=(\\w+)\\b");
	private static final Map<String, String> _osgiContracts;
	private static final Map<String, String> _packageVersions;
	private static final Pattern _versionListPattern = Pattern.compile(
		"version:(List<)?Version(>)?=\"([\\d,.]+)\"");
	private static final Pattern _versionPattern = Pattern.compile(
		"\\(version=([\\d.]+)\\)");

	static {
		try {
			_osgiContracts = _loadOSGiContracts();
			_packageVersions = _loadPackageVersions();
		}
		catch (IOException ioException) {
			throw new ExceptionInInitializerError(ioException);
		}
	}

}