/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.EventDefinition;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupEventDefinitionsPageResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Caio Pinheiro
 */
public class EventDefinitionDTOConverterTest {

	@Test
	public void testToDTO() {
		Assert.assertNull(_eventDefinitionDTOConverter.toDTO(null, null));

		_testToDTOWithKnownType();
		_testToDTOWithUnknownType();
	}

	private void _testToDTOWithKnownType() {
		String description = RandomTestUtil.randomString();
		String displayName = RandomTestUtil.randomString();
		boolean hidden = RandomTestUtil.randomBoolean();
		String id = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();
		EventDefinition.Type type = EventDefinition.Type.CUSTOM;

		EventDefinition eventDefinition = _toDTO(
			description, displayName, hidden, id, name, type.getValue());

		Assert.assertEquals(description, eventDefinition.getDescription());
		Assert.assertEquals(displayName, eventDefinition.getDisplayName());
		Assert.assertEquals(hidden, eventDefinition.getHidden());
		Assert.assertEquals(id, eventDefinition.getId());
		Assert.assertEquals(name, eventDefinition.getName());
		Assert.assertEquals(type, eventDefinition.getType());
	}

	private void _testToDTOWithUnknownType() {
		EventDefinition eventDefinition = _toDTO(RandomTestUtil.randomString());

		Assert.assertNull(eventDefinition.getType());

		eventDefinition = _toDTO(null);

		Assert.assertNull(eventDefinition.getType());
	}

	private EventDefinition _toDTO(String type) {
		return _toDTO(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), type);
	}

	private EventDefinition _toDTO(
		String description, String displayName, boolean hidden, String id,
		String name, String type) {

		GetWorkspaceGroupEventDefinitionsPageResponse.EventDefinition
			eventDefinition =
				new GetWorkspaceGroupEventDefinitionsPageResponse.
					EventDefinition();

		eventDefinition.setDescription(description);
		eventDefinition.setDisplayName(displayName);
		eventDefinition.setHidden(hidden);
		eventDefinition.setId(id);
		eventDefinition.setName(name);
		eventDefinition.setType(type);

		return _eventDefinitionDTOConverter.toDTO(null, eventDefinition);
	}

	private final EventDefinitionDTOConverter _eventDefinitionDTOConverter =
		new EventDefinitionDTOConverter();

}