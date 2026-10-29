/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.cbom.builder;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.cyclonedx.model.Component;

/**
 * @author Manuele Castro
 */
public class CBOMBuilder {

	public static String build(
			String configurationName, List<CryptoCall> cryptoCalls)
		throws Exception {

		String portalPropertiesContent = StringUtil.read(
			CBOMBuilder.class.getClassLoader(), "portal.properties");

		Properties portalProperties = PropertiesUtil.load(
			portalPropertiesContent);

		List<Component> components = CryptoAssetComponentUtil.toComponents(
			cryptoCalls, portalProperties);

		return CycloneDXCBOMWriter.write(
			components, configurationName, ReleaseInfo.getName(),
			ReleaseInfo.getVersionDisplayName());
	}

	public static List<CryptoCall> getCryptoCalls(
			List<CryptoCall> cryptoCalls, Set<String> messages)
		throws Exception {

		String json = StringUtil.read(
			CBOMBuilder.class, "dependencies/crypto-features.json");

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(json);

		Set<String> unmatchedKeys = new TreeSet<>(jsonObject.keySet());

		List<CryptoCall> scopedCryptoCalls = new ArrayList<>();

		for (CryptoCall cryptoCall : cryptoCalls) {
			if (!cryptoCall.isResolved()) {
				scopedCryptoCalls.add(cryptoCall);

				messages.add(
					StringBundler.concat(
						"The scan cannot read the algorithm of the ",
						cryptoCall.getJCAClassName(), " call in ",
						cryptoCall.getClassName(), ", which comes from ",
						StringUtil.merge(
							cryptoCall.getUnresolvedReasons(), ", ")));

				continue;
			}

			if (cryptoCall.isConfigurable()) {
				scopedCryptoCalls.addAll(
					_splitByFeature(
						cryptoCall, jsonObject, messages,
						cryptoCall.getConfiguredBy(), unmatchedKeys));

				continue;
			}

			if (!_portalBundleSymbolicNames.contains(
					cryptoCall.getBundleSymbolicName())) {

				scopedCryptoCalls.addAll(
					_splitByFeature(
						cryptoCall, jsonObject, messages,
						cryptoCall.getSelectedBy(), unmatchedKeys));

				continue;
			}

			scopedCryptoCalls.add(cryptoCall);

			String key = StringBundler.concat(
				cryptoCall.getAlgorithm(), " in ", cryptoCall.getClassName());

			unmatchedKeys.remove(key);

			if (jsonObject.has(key)) {
				cryptoCall.setFeatureName(jsonObject.getString(key));
			}
			else {
				messages.add(
					StringBundler.concat(
						"crypto-features.json has no feature for ", key,
						", so the CBOM uses the bundle name ",
						cryptoCall.getFeatureName()));
			}
		}

		for (String key : unmatchedKeys) {
			messages.add(
				StringBundler.concat(
					"crypto-features.json maps ", key, " to ",
					jsonObject.getString(key),
					", but no crypto call matches it"));
		}

		return scopedCryptoCalls;
	}

	public static void main(String[] args) throws Exception {
		File configurationDir = new File(args[0]);

		Properties portalExtProperties = _loadProperties(
			new File(configurationDir, "portal-ext.properties"));

		if (GetterUtil.getBoolean(
				portalExtProperties.getProperty("fips.enabled"))) {

			throw new UnsupportedOperationException(
				"Unable to build the CBOM of a FIPS mode configuration");
		}

		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		Set<String> bundleSymbolicNames = new HashSet<>(
			_portalBundleSymbolicNames);

		bundleSymbolicNames.addAll(_settingBundleSymbolicNames);

		CryptoCallScanner cryptoCallScanner = new CryptoCallScanner(
			bundleSymbolicNames, false);

		for (File file : _getFiles(new File(args[3]), new File(args[2]))) {
			cryptoCallScanner.scan(file);
		}

		List<CryptoCall> scannedCryptoCalls =
			cryptoCallScanner.getCryptoCalls();

		Set<String> messages = new TreeSet<>();

		String cbomJSON = build(
			configurationDir.getName(),
			getCryptoCalls(scannedCryptoCalls, messages));

		File file = new File(args[1]);

		File parentDir = file.getParentFile();

		parentDir.mkdirs();

		Files.write(file.toPath(), cbomJSON.getBytes(StandardCharsets.UTF_8));

		for (String message : messages) {
			System.out.println(message);
		}

		System.out.println("Wrote " + file.getAbsolutePath());
	}

	private static void _addFiles(
		File dir, File excludedDir, List<File> files) {

		File[] childFiles = dir.listFiles();

		if (childFiles == null) {
			return;
		}

		for (File childFile : childFiles) {
			String fileName = childFile.getName();

			if (childFile.isDirectory()) {
				if (!childFile.equals(excludedDir)) {
					_addFiles(childFile, excludedDir, files);
				}
			}
			else if (fileName.endsWith(".jar") || fileName.endsWith(".lpkg")) {
				files.add(childFile);
			}
		}
	}

	private static List<File> _getFiles(
		File appServerPortalDir, File liferayHomeDir) {

		List<File> files = new ArrayList<>();

		File osgiDir = new File(liferayHomeDir, "osgi");

		_addFiles(osgiDir, new File(osgiDir, "test"), files);

		_addFiles(new File(appServerPortalDir, "WEB-INF/lib"), null, files);
		_addFiles(
			new File(appServerPortalDir, "WEB-INF/shielded-container-lib"),
			null, files);

		files.sort(null);

		return files;
	}

	private static Properties _loadProperties(File file) throws Exception {
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			return PropertiesUtil.load(fileInputStream, StringPool.UTF8);
		}
	}

	private static List<CryptoCall> _splitByFeature(
		CryptoCall cryptoCall, JSONObject jsonObject, Set<String> messages,
		List<String> propertyKeys, Set<String> unmatchedKeys) {

		Map<String, List<String>> propertyKeysMap = new LinkedHashMap<>();

		for (String propertyKey : propertyKeys) {
			String featureName = cryptoCall.getFeatureName();

			if (jsonObject.has(propertyKey)) {
				featureName = jsonObject.getString(propertyKey);
			}
			else if (!_portalBundleSymbolicNames.contains(
						cryptoCall.getBundleSymbolicName())) {

				continue;
			}
			else {
				messages.add(
					StringBundler.concat(
						"crypto-features.json has no feature for ", propertyKey,
						", so the CBOM uses the bundle name ",
						cryptoCall.getFeatureName()));
			}

			List<String> featurePropertyKeys = propertyKeysMap.computeIfAbsent(
				featureName, key -> new ArrayList<>());

			featurePropertyKeys.add(propertyKey);

			unmatchedKeys.remove(propertyKey);
		}

		List<CryptoCall> cryptoCalls = new ArrayList<>();

		for (Map.Entry<String, List<String>> entry :
				propertyKeysMap.entrySet()) {

			CryptoCall featureCryptoCall = cryptoCall.createPart();

			if (cryptoCall.isConfigurable()) {
				featureCryptoCall.setConfiguredBy(entry.getValue());
			}
			else {
				featureCryptoCall.setAlgorithm(cryptoCall.getAlgorithm());
				featureCryptoCall.setKeySize(cryptoCall.getKeySize());
				featureCryptoCall.setSelectedBy(entry.getValue());
			}

			featureCryptoCall.setFeatureName(entry.getKey());

			cryptoCalls.add(featureCryptoCall);
		}

		return cryptoCalls;
	}

	private static final Set<String> _portalBundleSymbolicNames =
		SetUtil.fromArray(
			"com.liferay.portal.impl", "com.liferay.portal.kernel");
	private static final Set<String> _settingBundleSymbolicNames =
		SetUtil.fromArray(
			"com.liferay.object.service", "com.liferay.portal.encryptor",
			"com.liferay.portal.security.key.impl",
			"com.liferay.portal.security.password.encryptor.impl");

}