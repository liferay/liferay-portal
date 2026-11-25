/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.EventAttributeDefinition;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupEventAttributeDefinitionsPageResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Caio Pinheiro
 */
public class EventAttributeDefinitionDTOConverterTest {

	@Test
	public void testToDTO() {
		Assert.assertNull(
			_eventAttributeDefinitionDTOConverter.toDTO(null, null));

		_testToDTOWithKnownEnums();
		_testToDTOWithUnknownEnums();
	}

	private void _testToDTOWithKnownEnums() {
		EventAttributeDefinition.DataType dataType =
			EventAttributeDefinition.DataType.DURATION;
		String description = RandomTestUtil.randomString();
		String displayName = RandomTestUtil.randomString();
		String id = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();
		String sampleValue = RandomTestUtil.randomString();
		EventAttributeDefinition.Type type =
			EventAttributeDefinition.Type.LOCAL;

		EventAttributeDefinition eventAttributeDefinition = _toDTO(
			dataType.getValue(), description, displayName, id, name,
			sampleValue, type.getValue());

		Assert.assertEquals(dataType, eventAttributeDefinition.getDataType());
		Assert.assertEquals(
			description, eventAttributeDefinition.getDescription());
		Assert.assertEquals(
			displayName, eventAttributeDefinition.getDisplayName());
		Assert.assertEquals(id, eventAttributeDefinition.getId());
		Assert.assertEquals(name, eventAttributeDefinition.getName());
		Assert.assertEquals(
			sampleValue, eventAttributeDefinition.getSampleValue());
		Assert.assertEquals(type, eventAttributeDefinition.getType());
	}

	private void _testToDTOWithUnknownEnums() {
		EventAttributeDefinition eventAttributeDefinition = _toDTO(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		Assert.assertNull(eventAttributeDefinition.getDataType());
		Assert.assertNull(eventAttributeDefinition.getType());

		eventAttributeDefinition = _toDTO(null, null);

		Assert.assertNull(eventAttributeDefinition.getDataType());
		Assert.assertNull(eventAttributeDefinition.getType());
	}

	private EventAttributeDefinition _toDTO(String dataType, String type) {
		return _toDTO(
			dataType, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), type);
	}

	private EventAttributeDefinition _toDTO(
		String dataType, String description, String displayName, String id,
		String name, String sampleValue, String type) {

		GetWorkspaceGroupEventAttributeDefinitionsPageResponse.
			EventAttributeDefinition eventAttributeDefinition =
				new GetWorkspaceGroupEventAttributeDefinitionsPageResponse.
					EventAttributeDefinition();

		eventAttributeDefinition.setDataType(dataType);
		eventAttributeDefinition.setDescription(description);
		eventAttributeDefinition.setDisplayName(displayName);
		eventAttributeDefinition.setId(id);
		eventAttributeDefinition.setName(name);
		eventAttributeDefinition.setSampleValue(sampleValue);
		eventAttributeDefinition.setType(type);

		return _eventAttributeDefinitionDTOConverter.toDTO(
			null, eventAttributeDefinition);
	}

	private final EventAttributeDefinitionDTOConverter
		_eventAttributeDefinitionDTOConverter =
			new EventAttributeDefinitionDTOConverter();

}