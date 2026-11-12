/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.internal.client.extension;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.gradle.api.GradleException;

/**
 * @author Gregory Amerson
 */
public class ClientExtensionDescriptor {

	public static final String TYPE_BATCH = "batch";

	public static final String TYPE_CONFIGURATION = "configuration";

	public static final String TYPE_FRONTEND = "frontend";

	public static final String TYPE_SERVICE = "service";

	public static final int VERSION = 1;

	public static Map<String, Object> derive(
		String baseImage, Map<String, Object> configsMap, String imageDigest,
		String imageRepository, String imageTag, Map<String, Object> lcpMap) {

		String type = getType(baseImage);

		Map<String, Object> clientExtensionMap = new LinkedHashMap<>();

		clientExtensionMap.put("configs", new TreeMap<>(configsMap));
		clientExtensionMap.put("descriptorVersion", VERSION);

		if (!type.equals(TYPE_CONFIGURATION)) {
			Map<String, Object> imageMap = new LinkedHashMap<>();

			imageMap.put("digest", imageDigest);
			imageMap.put("repository", imageRepository);
			imageMap.put("tag", imageTag);

			clientExtensionMap.put("image", imageMap);
		}

		String oAuthApplicationHeadlessServerERC =
			_getOAuthApplicationHeadlessServerERC(configsMap);

		if (oAuthApplicationHeadlessServerERC != null) {
			clientExtensionMap.put(
				"oauthApplicationHeadlessServerERC",
				oAuthApplicationHeadlessServerERC);
		}

		clientExtensionMap.put("type", type);

		if (!type.equals(TYPE_CONFIGURATION)) {
			clientExtensionMap.put("workload", _getWorkloadMap(lcpMap));
		}

		return Collections.singletonMap("clientExtension", clientExtensionMap);
	}

	public static String getBaseImage(String dockerfileText) {
		for (String line : dockerfileText.split("\n")) {
			String trimmedLine = line.trim();

			String[] tokens = trimmedLine.split("\\s+");

			if (!tokens[0].equalsIgnoreCase("FROM")) {
				continue;
			}

			for (int i = 1; i < tokens.length; i++) {
				if (!tokens[i].startsWith("--")) {
					return tokens[i];
				}
			}
		}

		return "";
	}

	public static String getRepository(String image) {
		int index = image.indexOf('@');

		if (index >= 0) {
			image = image.substring(0, index);
		}

		index = image.lastIndexOf(':');

		if (index > image.lastIndexOf('/')) {
			image = image.substring(0, index);
		}

		return image;
	}

	public static String getType(String baseImage) {
		String baseRepository = getRepository(baseImage);

		if (_isLiferayRepository(baseRepository, "batch")) {
			return TYPE_BATCH;
		}

		if (_isLiferayRepository(baseRepository, "caddy")) {
			return TYPE_FRONTEND;
		}

		if (_isLiferayRepository(baseRepository, "noop")) {
			return TYPE_CONFIGURATION;
		}

		return TYPE_SERVICE;
	}

	public static boolean isConfigurationOnly(String baseImage) {
		return TYPE_CONFIGURATION.equals(getType(baseImage));
	}

	private static Integer _getInteger(Object value) {
		if (value instanceof Number) {
			Number number = (Number)value;

			return number.intValue();
		}

		return null;
	}

	private static Map<?, ?> _getMap(Object value) {
		if (value instanceof Map<?, ?>) {
			return (Map<?, ?>)value;
		}

		return null;
	}

	private static String _getOAuthApplicationHeadlessServerERC(
		Map<String, Object> configsMap) {

		String oAuthApplicationHeadlessServerERC = null;

		for (String configurationKey : configsMap.keySet()) {
			int index = configurationKey.indexOf(
				_CONFIGURATION_KEY_HEADLESS_SERVER);

			if (index < 0) {
				continue;
			}

			String identifier = configurationKey.substring(
				index + _CONFIGURATION_KEY_HEADLESS_SERVER.length());

			index = identifier.indexOf('/');

			if (index >= 0) {
				identifier = identifier.substring(0, index);
			}

			if ((oAuthApplicationHeadlessServerERC != null) &&
				!oAuthApplicationHeadlessServerERC.equals(identifier)) {

				throw new GradleException(
					"Unable to choose between OAuth 2 headless server " +
						"applications \"" + oAuthApplicationHeadlessServerERC +
							"\" and \"" + identifier + "\"");
			}

			oAuthApplicationHeadlessServerERC = identifier;
		}

		return oAuthApplicationHeadlessServerERC;
	}

	private static String _getString(Object value) {
		if (value instanceof String) {
			String string = (String)value;

			if (!string.isEmpty()) {
				return string;
			}
		}

		return null;
	}

	private static Map<String, Object> _getWorkloadMap(
		Map<String, Object> lcpMap) {

		Map<String, Object> workloadMap = new LinkedHashMap<>();

		Map<String, Object> envMap = new TreeMap<>();

		Map<?, ?> lcpEnvMap = _getMap(lcpMap.get("env"));

		if (lcpEnvMap != null) {
			for (Map.Entry<?, ?> entry : lcpEnvMap.entrySet()) {
				envMap.put(
					String.valueOf(entry.getKey()),
					String.valueOf(entry.getValue()));
			}
		}

		workloadMap.put("env", envMap);

		String kind = _getString(lcpMap.get("kind"));

		if (kind == null) {
			kind = "Deployment";
		}

		workloadMap.put("kind", kind);

		for (String probeName :
				new String[] {"livenessProbe", "readinessProbe"}) {

			Map<?, ?> probeMap = _getMap(lcpMap.get(probeName));

			if ((probeMap == null) || probeMap.isEmpty()) {
				continue;
			}

			workloadMap.put(probeName, probeMap);
		}

		Integer port = null;

		Map<?, ?> loadBalancerMap = _getMap(lcpMap.get("loadBalancer"));

		if (loadBalancerMap != null) {
			port = _getInteger(loadBalancerMap.get("targetPort"));
		}

		if (port == null) {
			port = 80;
		}

		workloadMap.put("port", port);

		Integer replicas = _getInteger(lcpMap.get("scale"));

		if (replicas == null) {
			replicas = 1;
		}

		workloadMap.put("replicas", replicas);

		String schedule = _getString(lcpMap.get("schedule"));

		if (schedule != null) {
			workloadMap.put("schedule", schedule);
		}

		return workloadMap;
	}

	private static boolean _isLiferayRepository(
		String repository, String name) {

		String liferayRepository = "liferay/" + name;

		if (repository.equals(liferayRepository) ||
			repository.endsWith("/" + liferayRepository)) {

			return true;
		}

		return false;
	}

	private static final String _CONFIGURATION_KEY_HEADLESS_SERVER =
		"com.liferay.oauth2.provider.configuration." +
			"OAuth2ProviderApplicationHeadlessServerConfiguration~";

}