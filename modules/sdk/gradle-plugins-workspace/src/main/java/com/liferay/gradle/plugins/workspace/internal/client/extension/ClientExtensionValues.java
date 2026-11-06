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
		String baseImage, Map<String, Object> configs, String imageRepository,
		String imageTag, Map<String, Object> lcp, String serviceId) {

		String baseRepository = getRepository(baseImage);

		Map<String, Object> workload = new LinkedHashMap<>();

		workload.put("containerPort", 80);
		workload.put("kind", "Deployment");
		workload.put("replicas", 1);

		Map<?, ?> loadBalancer = _getMap(lcp.get("loadBalancer"));

		if (loadBalancer != null) {
			Integer targetPort = _getInteger(loadBalancer.get("targetPort"));

			if (targetPort != null) {
				workload.put("containerPort", targetPort);
			}
		}

		String kind = _getString(lcp.get("kind"));

		if (kind != null) {
			workload.put("kind", kind);
		}

		Integer scale = _getInteger(lcp.get("scale"));

		if (scale != null) {
			workload.put("replicas", scale);
		}

		String schedule = _getString(lcp.get("schedule"));

		if (schedule != null) {
			workload.put("schedule", schedule);
		}

		for (String probeName :
				new String[] {"livenessProbe", "readinessProbe"}) {

			Map<?, ?> probe = _getMap(lcp.get(probeName));

			if ((probe == null) || probe.isEmpty()) {
				continue;
			}

			workload.put(probeName, probe);
		}

		Map<String, Object> startupProbe = _getStartupProbe(lcp);

		if (startupProbe != null) {
			workload.put("startupProbe", startupProbe);
		}

		Map<String, Object> env = new TreeMap<>();

		Map<?, ?> lcpEnv = _getMap(lcp.get("env"));

		if (lcpEnv != null) {
			for (Map.Entry<?, ?> entry : lcpEnv.entrySet()) {
				env.put(
					String.valueOf(entry.getKey()),
					String.valueOf(entry.getValue()));
			}
		}

		if (_isLiferayRepository(baseRepository, "batch")) {
			for (String configurationKey : configs.keySet()) {
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

				env.put("LIFERAY_BATCH_OAUTH_APP_ERC", identifier);
			}
		}

		workload.put("env", env);

		boolean workloadEnabled = !isConfigurationOnly(baseImage);

		workload.put("enabled", workloadEnabled);

		if (_isLiferayRepository(baseRepository, "caddy")) {
			Map<String, Object> volumeMount = new TreeMap<>();

			volumeMount.put("mountPath", "/etc/caddy.d");
			volumeMount.put("name", "caddy-d");

			workload.put(
				"volumeMounts", Collections.singletonList(volumeMount));

			Map<String, Object> volume = new TreeMap<>();

			volume.put("emptyDir", Collections.emptyMap());
			volume.put("name", "caddy-d");

			workload.put("volumes", Collections.singletonList(volume));
		}

		Map<String, Object> clientExtension = new LinkedHashMap<>();

		clientExtension.put("configs", new TreeMap<>(configs));
		clientExtension.put("serviceId", serviceId);

		Map<String, Object> values = new LinkedHashMap<>();

		values.put("clientExtension", clientExtension);

		values.put("fullnameOverride", serviceId);

		if (workloadEnabled) {
			Map<String, Object> image = new LinkedHashMap<>();

			image.put("repository", imageRepository);
			image.put("tag", imageTag);

			values.put("image", image);
		}

		values.put(
			"service",
			Collections.singletonMap(
				"enabled",
				workloadEnabled &&
				Objects.equals(workload.get("kind"), "Deployment")));

		values.put("workload", workload);

		return values;
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

	private static Map<String, Object> _getStartupProbe(
		Map<String, Object> lcp) {

		for (String probeName :
				new String[] {"readinessProbe", "livenessProbe"}) {

			Map<?, ?> probe = _getMap(lcp.get(probeName));

			if (probe == null) {
				continue;
			}

			Map<String, Object> startupProbe = new LinkedHashMap<>();

			startupProbe.put("failureThreshold", 60);
			startupProbe.put("periodSeconds", 5);

			for (String handler :
					new String[] {"exec", "grpc", "httpGet", "tcpSocket"}) {

				Object value = probe.get(handler);

				if (value == null) {
					continue;
				}

				startupProbe.put(handler, value);
			}

			if (startupProbe.size() > 2) {
				return startupProbe;
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