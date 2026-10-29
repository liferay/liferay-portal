/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.cbom.builder;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import com.liferay.petra.string.StringPool;

import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.cyclonedx.Version;
import org.cyclonedx.generators.BomGeneratorFactory;
import org.cyclonedx.generators.json.BomJsonGenerator;
import org.cyclonedx.model.Bom;
import org.cyclonedx.model.Component;
import org.cyclonedx.model.LifecycleChoice;
import org.cyclonedx.model.Lifecycles;
import org.cyclonedx.model.Metadata;
import org.cyclonedx.model.Property;

/**
 * @author Manuele Castro
 */
public class CycloneDXCBOMWriter {

	public static String write(
			List<Component> components, String configurationName,
			String productName, String productVersion)
		throws Exception {

		Bom bom = new Bom();

		bom.setComponents(components);
		bom.setMetadata(
			_getMetadata(configurationName, productName, productVersion));
		bom.setSerialNumber(
			_getSerialNumber(configurationName, productVersion));
		bom.setVersion(1);

		BomJsonGenerator bomJsonGenerator = BomGeneratorFactory.createJson(
			Version.VERSION_16, bom);

		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode jsonNode = objectMapper.readTree(
			bomJsonGenerator.toJsonString(false));

		DefaultPrettyPrinter defaultPrettyPrinter = new DefaultPrettyPrinter();

		defaultPrettyPrinter.indentArraysWith(
			DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

		ObjectWriter objectWriter = objectMapper.writer(defaultPrettyPrinter);

		return objectWriter.writeValueAsString(jsonNode);
	}

	private static Metadata _getMetadata(
		String configurationName, String productName, String productVersion) {

		Metadata metadata = new Metadata();

		Component component = new Component();

		component.setName(productName);
		component.setType(Component.Type.APPLICATION);
		component.setVersion(productVersion);

		LifecycleChoice lifecycleChoice = new LifecycleChoice();

		lifecycleChoice.setPhase(LifecycleChoice.Phase.POST_BUILD);

		Lifecycles lifecycles = new Lifecycles();

		lifecycles.setLifecycleChoice(
			Collections.singletonList(lifecycleChoice));

		metadata.setComponent(component);
		metadata.setLifecycles(lifecycles);
		metadata.setProperties(
			Arrays.asList(
				new Property("liferay:cbom:configuration", configurationName),
				new Property("liferay:cbom:schema-version", "1.0")));
		metadata.setTimestamp(null);

		return metadata;
	}

	private static String _getSerialNumber(
		String configurationName, String productVersion) {

		String name = productVersion + StringPool.POUND + configurationName;

		return "urn:uuid:" +
			UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
	}

}