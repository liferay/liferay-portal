/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.internal.client.extension;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author Gregory Amerson
 */
public class ClientExtensionValues {

	public static Map<String, Object> derive(
		String baseImage, Map<String, Object> configsMap,
		String imageRepository, String imageTag, Map<String, Object> lcpMap,
		String serviceId) {

		String baseRepository = getRepository(baseImage);

		Map<String, Object> workloadMap = new LinkedHashMap<>();

		workloadMap.put("containerPort", 80);
		workloadMap.put("kind", "Deployment");
		workloadMap.put("replicas", 1);

		Map<?, ?> loadBalancerMap = _getMap(lcpMap.get("loadBalancer"));

		if (loadBalancerMap != null) {
			Integer targetPort = _getInteger(loadBalancerMap.get("targetPort"));

			if (targetPort != null) {
				workloadMap.put("containerPort", targetPort);
			}
		}

		String kind = _getString(lcpMap.get("kind"));

		if (kind != null) {
			workloadMap.put("kind", kind);
		}

		Integer scale = _getInteger(lcpMap.get("scale"));

		if (scale != null) {
			workloadMap.put("replicas", scale);
		}

		String schedule = _getString(lcpMap.get("schedule"));

		if (schedule != null) {
			workloadMap.put("schedule", schedule);
		}

		for (String probeName :
				new String[] {"livenessProbe", "readinessProbe"}) {

			Map<?, ?> probeMap = _getMap(lcpMap.get(probeName));

			if ((probeMap == null) || probeMap.isEmpty()) {
				continue;
			}

			workloadMap.put(probeName, probeMap);
		}

		Map<String, Object> startupProbeMap = _getStartupProbeMap(lcpMap);

		if (startupProbeMap != null) {
			workloadMap.put("startupProbe", startupProbeMap);
		}

		Map<String, Object> envMap = new TreeMap<>();

		Map<?, ?> lcpEnvMap = _getMap(lcpMap.get("env"));

		if (lcpEnvMap != null) {
			for (Map.Entry<?, ?> entry : lcpEnvMap.entrySet()) {
				envMap.put(
					String.valueOf(entry.getKey()),
					String.valueOf(entry.getValue()));
			}
		}

		if (_isLiferayRepository(baseRepository, "batch")) {
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

				envMap.put("LIFERAY_BATCH_OAUTH_APP_ERC", identifier);
			}
		}

		workloadMap.put("env", envMap);

		boolean workloadEnabled = !isConfigurationOnly(baseImage);

		workloadMap.put("enabled", workloadEnabled);

		if (_isLiferayRepository(baseRepository, "caddy")) {
			Map<String, Object> volumeMountMap = new TreeMap<>();

			volumeMountMap.put("mountPath", "/etc/caddy.d");
			volumeMountMap.put("name", "caddy-d");

			workloadMap.put(
				"volumeMounts", Collections.singletonList(volumeMountMap));

			Map<String, Object> volumeMap = new TreeMap<>();

			volumeMap.put("emptyDir", Collections.emptyMap());
			volumeMap.put("name", "caddy-d");

			workloadMap.put("volumes", Collections.singletonList(volumeMap));
		}

		Map<String, Object> clientExtensionMap = new LinkedHashMap<>();

		clientExtensionMap.put("configs", new TreeMap<>(configsMap));
		clientExtensionMap.put("serviceId", serviceId);

		Map<String, Object> valuesMap = new LinkedHashMap<>();

		valuesMap.put("clientExtension", clientExtensionMap);

		valuesMap.put("fullnameOverride", serviceId);

		if (workloadEnabled) {
			Map<String, Object> imageMap = new LinkedHashMap<>();

			imageMap.put("repository", imageRepository);
			imageMap.put("tag", imageTag);

			valuesMap.put("image", imageMap);
		}

		valuesMap.put(
			"service",
			Collections.singletonMap(
				"enabled",
				workloadEnabled &&
				Objects.equals(workloadMap.get("kind"), "Deployment")));

		valuesMap.put("workload", workloadMap);

		return valuesMap;
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

	public static boolean isConfigurationOnly(String baseImage) {
		String baseRepository = getRepository(baseImage);

		return _isLiferayRepository(baseRepository, "noop");
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

	private static Map<String, Object> _getStartupProbeMap(
		Map<String, Object> lcpMap) {

		for (String probeName :
				new String[] {"readinessProbe", "livenessProbe"}) {

			Map<?, ?> probeMap = _getMap(lcpMap.get(probeName));

			if (probeMap == null) {
				continue;
			}

			Map<String, Object> startupProbeMap = new LinkedHashMap<>();

			startupProbeMap.put("failureThreshold", 60);
			startupProbeMap.put("periodSeconds", 5);

			for (String handler :
					new String[] {"exec", "grpc", "httpGet", "tcpSocket"}) {

				Object value = probeMap.get(handler);

				if (value == null) {
					continue;
				}

				startupProbeMap.put(handler, value);
			}

			if (startupProbeMap.size() > 2) {
				return startupProbeMap;
			}
		}

		return null;
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