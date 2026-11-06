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
		Map<String, Object> lcp = new LinkedHashMap<>();

		lcp.put("env", Map.of("A", "lcp", "PORT", 58081));
		lcp.put("loadBalancer", Map.of("targetPort", 58081));
		lcp.put("scale", 2);

		Map<String, Object> values = _derive(
			Map.of("key~able", Map.of()), "liferay/jar-runner:latest", lcp);

		Assert.assertEquals(
			Map.of(
				"configs", Map.of("key~able", Map.of()), "serviceId", "able"),
			values.get("clientExtension"));
		Assert.assertEquals(
			Map.of("repository", "registry/able", "tag", "1.0.0@sha256:1"),
			values.get("image"));
		Assert.assertEquals(Map.of("enabled", true), values.get("service"));

		Map<String, Object> workload = (Map<String, Object>)values.get(
			"workload");

		Assert.assertEquals(58081, workload.get("containerPort"));
		Assert.assertEquals(
			Map.of("A", "lcp", "PORT", "58081"), workload.get("env"));
		Assert.assertEquals("Deployment", workload.get("kind"));
		Assert.assertEquals(2, workload.get("replicas"));
	}

	@Test
	public void testDeriveWhenIsBatch() {
		Map<String, Object> values = _derive(
			Map.of(
				"com.liferay.oauth2.provider.configuration." +
					"OAuth2ProviderApplicationHeadlessServerConfiguration~" +
						"able-batch/liferay.com",
				Map.of()),
			"liferay/batch:latest", Map.of("kind", "Job"));

		Map<String, Object> workload = (Map<String, Object>)values.get(
			"workload");

		Assert.assertTrue((Boolean)workload.get("enabled"));
		Assert.assertEquals(
			Map.of("LIFERAY_BATCH_OAUTH_APP_ERC", "able-batch"),
			workload.get("env"));
		Assert.assertNull(workload.get("volumes"));
	}

	@Test
	public void testDeriveWhenIsConfigurationOnly() {
		Map<String, Object> values = _derive(
			Map.of(), "liferay/noop", Map.of());

		Assert.assertNull(values.get("image"));
		Assert.assertEquals(Map.of("enabled", false), values.get("service"));

		Map<String, Object> workload = (Map<String, Object>)values.get(
			"workload");

		Assert.assertFalse((Boolean)workload.get("enabled"));
	}

	@Test
	public void testDeriveWhenIsFrontend() {
		for (String baseImage :
				new String[] {
					"liferay/caddy:latest",
					"registry.example.com/liferay/caddy:latest"
				}) {

			Map<String, Object> values = _derive(Map.of(), baseImage, Map.of());

			Map<String, Object> workload = (Map<String, Object>)values.get(
				"workload");

			Assert.assertEquals(
				List.of(Map.of("mountPath", "/etc/caddy.d", "name", "caddy-d")),
				workload.get("volumeMounts"));
			Assert.assertEquals(
				List.of(Map.of("emptyDir", Map.of(), "name", "caddy-d")),
				workload.get("volumes"));
		}

		Map<String, Object> values = _derive(
			Map.of(), "myliferay/caddy:latest", Map.of());

		Map<String, Object> workload = (Map<String, Object>)values.get(
			"workload");

		Assert.assertNull(workload.get("volumeMounts"));
		Assert.assertNull(workload.get("volumes"));
	}

	@Test
	public void testDeriveWhenLCPJsonHasReadinessProbe() {
		Map<String, Object> readinessProbe = new LinkedHashMap<>();

		readinessProbe.put("httpGet", Map.of("path", "/ready"));
		readinessProbe.put("timeoutSeconds", 5);

		Map<String, Object> values = _derive(
			Map.of(), "liferay/jar-runner:latest",
			Map.of("readinessProbe", readinessProbe));

		Map<String, Object> workload = (Map<String, Object>)values.get(
			"workload");

		Assert.assertEquals(readinessProbe, workload.get("readinessProbe"));
		Assert.assertEquals(
			Map.of(
				"failureThreshold", 60, "httpGet", Map.of("path", "/ready"),
				"periodSeconds", 5),
			workload.get("startupProbe"));
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
			"localhost:5000/able",
			ClientExtensionValues.getRepository(
				"localhost:5000/able:1.0.0@sha256:1"));
		Assert.assertEquals(
			"localhost:5000/able",
			ClientExtensionValues.getRepository("localhost:5000/able"));
	}

	private Map<String, Object> _derive(
		Map<String, Object> configs, String baseImage,
		Map<String, Object> lcp) {

		return ClientExtensionValues.derive(
			baseImage, configs, "registry/able", "1.0.0@sha256:1", lcp, "able");
	}

}