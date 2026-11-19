/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.internal.client.extension;

import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class ClientExtensionDescriptorTest {

	@Test
	public void testDerive() {
		Map<String, Object> clientExtensionMap = _derive(
			ClientExtensionDescriptor.TYPE_SERVICE, _randomString());

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
		Assert.assertNull(clientExtensionMap.get("workload"));
	}

	@Test
	public void testDeriveWhenTypeIsBatch() {
		String oAuthApplicationHeadlessServerERC = _randomString();

		Map<String, Object> clientExtensionMap = _derive(
			ClientExtensionDescriptor.TYPE_BATCH,
			oAuthApplicationHeadlessServerERC);

		Assert.assertNotNull(clientExtensionMap.get("image"));
		Assert.assertEquals(
			oAuthApplicationHeadlessServerERC,
			clientExtensionMap.get("oauthApplicationHeadlessServerERC"));
		Assert.assertEquals(
			ClientExtensionDescriptor.TYPE_BATCH,
			clientExtensionMap.get("type"));
	}

	@Test
	public void testDeriveWhenTypeIsConfiguration() {
		Map<String, Object> clientExtensionMap = _derive(
			ClientExtensionDescriptor.TYPE_CONFIGURATION, _randomString());

		Assert.assertNull(clientExtensionMap.get("image"));
		Assert.assertNull(
			clientExtensionMap.get("oauthApplicationHeadlessServerERC"));
		Assert.assertEquals(
			ClientExtensionDescriptor.TYPE_CONFIGURATION,
			clientExtensionMap.get("type"));
		Assert.assertNull(clientExtensionMap.get("workload"));
	}

	@Test
	public void testGetRepository() {
		Assert.assertEquals(
			"localhost:5001/able",
			ClientExtensionDescriptor.getRepository("localhost:5001/able"));
		Assert.assertEquals(
			"localhost:5001/able",
			ClientExtensionDescriptor.getRepository(
				"localhost:5001/able:1.0.0@sha256:1"));
	}

	@Test
	public void testGetType() {
		Assert.assertEquals(
			ClientExtensionDescriptor.TYPE_BATCH,
			ClientExtensionDescriptor.getType("batch", true));
		Assert.assertEquals(
			ClientExtensionDescriptor.TYPE_CONFIGURATION,
			ClientExtensionDescriptor.getType("configuration", false));
		Assert.assertEquals(
			ClientExtensionDescriptor.TYPE_SERVICE,
			ClientExtensionDescriptor.getType("configuration", true));
		Assert.assertEquals(
			"frontend", ClientExtensionDescriptor.getType("frontend", true));
		Assert.assertEquals(
			ClientExtensionDescriptor.TYPE_SERVICE,
			ClientExtensionDescriptor.getType("microservice", false));
	}

	private Map<String, Object> _derive(
		String type, String oAuthApplicationHeadlessServerERC) {

		Map<String, Object> descriptorMap = ClientExtensionDescriptor.derive(
			Map.of("key~able", Map.of()), "sha256:1", "registry/able", "1.0.0",
			oAuthApplicationHeadlessServerERC, type);

		Assert.assertEquals(descriptorMap.toString(), 1, descriptorMap.size());

		return (Map<String, Object>)descriptorMap.get("descriptor");
	}

	private String _randomString() {
		return String.valueOf(UUID.randomUUID());
	}

}