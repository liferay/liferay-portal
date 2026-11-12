/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.internal.client.extension;

import java.util.LinkedHashMap;
import java.util.Map;

import org.gradle.api.GradleException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class ClientExtensionDescriptorTest {

	@Test
	public void testDerive() {
		Map<String, Object> lcpMap = new LinkedHashMap<>();

		lcpMap.put("env", Map.of("A", "lcp", "PORT", 58081));
		lcpMap.put("loadBalancer", Map.of("targetPort", 58081));
		lcpMap.put("scale", 2);

		Map<String, Object> clientExtensionMap = _derive(
			Map.of("key~able", Map.of()), "liferay/jar-runner:latest", lcpMap);

		Assert.assertEquals(
			Map.of("key~able", Map.of()), clientExtensionMap.get("configs"));
		Assert.assertEquals(
			ClientExtensionDescriptor.VERSION,
			clientExtensionMap.get("descriptorVersion"));
		Assert.assertEquals(
			Map.of(
				"digest", "sha256:1", "repository", "registry/able", "tag",
				"1.0.0"),
			clientExtensionMap.get("image"));
		Assert.assertNull(
			clientExtensionMap.get("oauthApplicationHeadlessServerERC"));
		Assert.assertNull(clientExtensionMap.get("serviceId"));
		Assert.assertEquals(
			ClientExtensionDescriptor.TYPE_SERVICE,
			clientExtensionMap.get("type"));
		Assert.assertEquals(
			Map.of(
				"env", Map.of("A", "lcp", "PORT", "58081"), "kind",
				"Deployment", "port", 58081, "replicas", 2),
			clientExtensionMap.get("workload"));
	}

	@Test
	public void testDeriveWhenIsBatch() {
		Map<String, Object> clientExtensionMap = _derive(
			Map.of(
				"com.liferay.oauth2.provider.configuration." +
					"OAuth2ProviderApplicationHeadlessServerConfiguration~" +
						"able-batch/liferay.com",
				Map.of()),
			"liferay/batch:latest", Map.of("kind", "Job"));

		Assert.assertEquals(
			"able-batch",
			clientExtensionMap.get("oauthApplicationHeadlessServerERC"));
		Assert.assertEquals(
			ClientExtensionDescriptor.TYPE_BATCH,
			clientExtensionMap.get("type"));

		Map<String, Object> workloadMap =
			(Map<String, Object>)clientExtensionMap.get("workload");

		Assert.assertEquals(Map.of(), workloadMap.get("env"));
		Assert.assertEquals("Job", workloadMap.get("kind"));

		clientExtensionMap = _derive(
			Map.of(
				_CONFIGURATION_KEY_HEADLESS_SERVER + "able-batch/able.com",
				Map.of(),
				_CONFIGURATION_KEY_HEADLESS_SERVER + "able-batch/liferay.com",
				Map.of()),
			"liferay/batch:latest", Map.of());

		Assert.assertEquals(
			"able-batch",
			clientExtensionMap.get("oauthApplicationHeadlessServerERC"));

		try {
			_derive(
				Map.of(
					_CONFIGURATION_KEY_HEADLESS_SERVER +
						"able-batch/liferay.com",
					Map.of(),
					_CONFIGURATION_KEY_HEADLESS_SERVER +
						"baker-batch/liferay.com",
					Map.of()),
				"liferay/batch:latest", Map.of());

			Assert.fail();
		}
		catch (GradleException gradleException) {
			String message = gradleException.getMessage();

			Assert.assertTrue(message, message.contains("able-batch"));
			Assert.assertTrue(message, message.contains("baker-batch"));
		}
	}

	@Test
	public void testDeriveWhenIsConfigurationOnly() {
		Map<String, Object> clientExtensionMap = _derive(
			Map.of(), "liferay/noop", Map.of());

		Assert.assertNull(clientExtensionMap.get("image"));
		Assert.assertEquals(
			ClientExtensionDescriptor.TYPE_CONFIGURATION,
			clientExtensionMap.get("type"));
		Assert.assertNull(clientExtensionMap.get("workload"));
	}

	@Test
	public void testDeriveWhenLCPJsonHasProbesAndSchedule() {
		Map<String, Object> readinessProbeMap = new LinkedHashMap<>();

		readinessProbeMap.put("httpGet", Map.of("path", "/ready"));
		readinessProbeMap.put("timeoutSeconds", 5);

		Map<String, Object> clientExtensionMap = _derive(
			Map.of(), "liferay/jar-runner:latest",
			Map.of(
				"kind", "CronJob", "livenessProbe", Map.of(), "readinessProbe",
				readinessProbeMap, "schedule", "*/5 * * * *"));

		Map<String, Object> workloadMap =
			(Map<String, Object>)clientExtensionMap.get("workload");

		Assert.assertNull(workloadMap.get("livenessProbe"));
		Assert.assertEquals(
			readinessProbeMap, workloadMap.get("readinessProbe"));
		Assert.assertEquals("*/5 * * * *", workloadMap.get("schedule"));
		Assert.assertNull(workloadMap.get("startupProbe"));
	}

	@Test
	public void testGetBaseImage() {
		Assert.assertEquals(
			"liferay/caddy:latest",
			ClientExtensionDescriptor.getBaseImage(
				"# A comment\nFROM --platform=linux/amd64 " +
					"liferay/caddy:latest AS base\nFROM other"));
		Assert.assertEquals(
			"", ClientExtensionDescriptor.getBaseImage("COPY . /public_html"));
	}

	@Test
	public void testGetRepository() {
		Assert.assertEquals(
			"localhost:5001/able",
			ClientExtensionDescriptor.getRepository(
				"localhost:5001/able:1.0.0@sha256:1"));
		Assert.assertEquals(
			"localhost:5001/able",
			ClientExtensionDescriptor.getRepository("localhost:5001/able"));
	}

	@Test
	public void testGetType() {
		Assert.assertEquals(
			ClientExtensionDescriptor.TYPE_FRONTEND,
			ClientExtensionDescriptor.getType("liferay/caddy:latest"));
		Assert.assertEquals(
			ClientExtensionDescriptor.TYPE_FRONTEND,
			ClientExtensionDescriptor.getType(
				"registry.example.com/liferay/caddy:latest"));
		Assert.assertEquals(
			ClientExtensionDescriptor.TYPE_SERVICE,
			ClientExtensionDescriptor.getType("myliferay/caddy:latest"));
	}

	private Map<String, Object> _derive(
		Map<String, Object> configsMap, String baseImage,
		Map<String, Object> lcpMap) {

		Map<String, Object> descriptorMap = ClientExtensionDescriptor.derive(
			baseImage, configsMap, "sha256:1", "registry/able", "1.0.0",
			lcpMap);

		Assert.assertEquals(descriptorMap.toString(), 1, descriptorMap.size());

		return (Map<String, Object>)descriptorMap.get("clientExtension");
	}

	private static final String _CONFIGURATION_KEY_HEADLESS_SERVER =
		"com.liferay.oauth2.provider.configuration." +
			"OAuth2ProviderApplicationHeadlessServerConfiguration~";

}