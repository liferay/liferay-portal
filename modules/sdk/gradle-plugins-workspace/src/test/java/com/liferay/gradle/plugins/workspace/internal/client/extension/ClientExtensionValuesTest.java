/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.internal.client.extension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class ClientExtensionValuesTest {

	@Test
	public void testDerive() {
		Map<String, Object> lcpMap = new LinkedHashMap<>();

		lcpMap.put("env", Map.of("A", "lcp", "PORT", 58081));
		lcpMap.put("loadBalancer", Map.of("targetPort", 58081));
		lcpMap.put("scale", 2);

		Map<String, Object> valuesMap = _derive(
			Map.of("key~able", Map.of()), "liferay/jar-runner:latest", lcpMap);

		Assert.assertEquals(
			Map.of(
				"configs", Map.of("key~able", Map.of()), "serviceId", "able"),
			valuesMap.get("clientExtension"));
		Assert.assertEquals(
			Map.of("repository", "registry/able", "tag", "1.0.0@sha256:1"),
			valuesMap.get("image"));
		Assert.assertEquals(Map.of("enabled", true), valuesMap.get("service"));

		Map<String, Object> workloadMap = (Map<String, Object>)valuesMap.get(
			"workload");

		Assert.assertEquals(58081, workloadMap.get("containerPort"));
		Assert.assertEquals(
			Map.of("A", "lcp", "PORT", "58081"), workloadMap.get("env"));
		Assert.assertEquals("Deployment", workloadMap.get("kind"));
		Assert.assertEquals(2, workloadMap.get("replicas"));
	}

	@Test
	public void testDeriveWhenIsBatch() {
		Map<String, Object> valuesMap = _derive(
			Map.of(
				"com.liferay.oauth2.provider.configuration." +
					"OAuth2ProviderApplicationHeadlessServerConfiguration~" +
						"able-batch/liferay.com",
				Map.of()),
			"liferay/batch:latest", Map.of("kind", "Job"));

		Map<String, Object> workloadMap = (Map<String, Object>)valuesMap.get(
			"workload");

		Assert.assertTrue((Boolean)workloadMap.get("enabled"));
		Assert.assertEquals(
			Map.of("LIFERAY_BATCH_OAUTH_APP_ERC", "able-batch"),
			workloadMap.get("env"));
		Assert.assertNull(workloadMap.get("volumes"));
	}

	@Test
	public void testDeriveWhenIsConfigurationOnly() {
		Map<String, Object> valuesMap = _derive(
			Map.of(), "liferay/noop", Map.of());

		Assert.assertNull(valuesMap.get("image"));
		Assert.assertEquals(Map.of("enabled", false), valuesMap.get("service"));

		Map<String, Object> workloadMap = (Map<String, Object>)valuesMap.get(
			"workload");

		Assert.assertFalse((Boolean)workloadMap.get("enabled"));
	}

	@Test
	public void testDeriveWhenIsFrontend() {
		for (String baseImage :
				new String[] {
					"liferay/caddy:latest",
					"registry.example.com/liferay/caddy:latest"
				}) {

			Map<String, Object> valuesMap = _derive(
				Map.of(), baseImage, Map.of());

			Map<String, Object> workloadMap =
				(Map<String, Object>)valuesMap.get("workload");

			Assert.assertEquals(
				List.of(Map.of("mountPath", "/etc/caddy.d", "name", "caddy-d")),
				workloadMap.get("volumeMounts"));
			Assert.assertEquals(
				List.of(Map.of("emptyDir", Map.of(), "name", "caddy-d")),
				workloadMap.get("volumes"));
		}

		Map<String, Object> valuesMap = _derive(
			Map.of(), "myliferay/caddy:latest", Map.of());

		Map<String, Object> workloadMap = (Map<String, Object>)valuesMap.get(
			"workload");

		Assert.assertNull(workloadMap.get("volumeMounts"));
		Assert.assertNull(workloadMap.get("volumes"));
	}

	@Test
	public void testDeriveWhenLCPJsonHasReadinessProbe() {
		Map<String, Object> readinessProbeMap = new LinkedHashMap<>();

		readinessProbeMap.put("httpGet", Map.of("path", "/ready"));
		readinessProbeMap.put("timeoutSeconds", 5);

		Map<String, Object> valuesMap = _derive(
			Map.of(), "liferay/jar-runner:latest",
			Map.of("readinessProbe", readinessProbeMap));

		Map<String, Object> workloadMap = (Map<String, Object>)valuesMap.get(
			"workload");

		Assert.assertEquals(
			readinessProbeMap, workloadMap.get("readinessProbe"));
		Assert.assertEquals(
			Map.of(
				"failureThreshold", 60, "httpGet", Map.of("path", "/ready"),
				"periodSeconds", 5),
			workloadMap.get("startupProbe"));
	}

	@Test
	public void testGetBaseImage() {
		Assert.assertEquals(
			"liferay/caddy:latest",
			ClientExtensionValues.getBaseImage(
				"# A comment\nFROM --platform=linux/amd64 " +
					"liferay/caddy:latest AS base\nFROM other"));
		Assert.assertEquals(
			"", ClientExtensionValues.getBaseImage("COPY . /public_html"));
	}

	@Test
	public void testGetRepository() {
		Assert.assertEquals(
			"localhost:5001/able",
			ClientExtensionValues.getRepository(
				"localhost:5001/able:1.0.0@sha256:1"));
		Assert.assertEquals(
			"localhost:5001/able",
			ClientExtensionValues.getRepository("localhost:5001/able"));
	}

	private Map<String, Object> _derive(
		Map<String, Object> configsMap, String baseImage,
		Map<String, Object> lcpMap) {

		return ClientExtensionValues.derive(
			baseImage, configsMap, "registry/able", "1.0.0@sha256:1", lcpMap,
			"able");
	}

}