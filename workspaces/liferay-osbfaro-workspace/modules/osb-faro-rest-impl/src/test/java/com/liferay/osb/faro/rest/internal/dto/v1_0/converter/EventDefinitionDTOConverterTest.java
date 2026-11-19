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

		GetWorkspaceGroupEventDefinitionsPageResponse.EventDefinition
			responseEventDefinition = _createResponseEventDefinition("CUSTOM");

		EventDefinition eventDefinition = _eventDefinitionDTOConverter.toDTO(
			null, responseEventDefinition);

		Assert.assertEquals(
			responseEventDefinition.getDescription(),
			eventDefinition.getDescription());
		Assert.assertEquals(
			responseEventDefinition.getDisplayName(),
			eventDefinition.getDisplayName());
		Assert.assertEquals(
			responseEventDefinition.getHidden(), eventDefinition.getHidden());
		Assert.assertEquals(
			responseEventDefinition.getId(), eventDefinition.getId());
		Assert.assertEquals(
			responseEventDefinition.getName(), eventDefinition.getName());
		Assert.assertEquals(
			EventDefinition.Type.CUSTOM, eventDefinition.getType());
	}

	@Test
	public void testToDTOWithUnknownType() {
		EventDefinition eventDefinition = _eventDefinitionDTOConverter.toDTO(
			null,
			_createResponseEventDefinition(RandomTestUtil.randomString()));

		Assert.assertNull(eventDefinition.getType());

		eventDefinition = _eventDefinitionDTOConverter.toDTO(
			null, _createResponseEventDefinition(null));

		Assert.assertNull(eventDefinition.getType());
	}

	private GetWorkspaceGroupEventDefinitionsPageResponse.EventDefinition
		_createResponseEventDefinition(String type) {

		GetWorkspaceGroupEventDefinitionsPageResponse.EventDefinition
			responseEventDefinition =
				new GetWorkspaceGroupEventDefinitionsPageResponse.
					EventDefinition();

		responseEventDefinition.setDescription(RandomTestUtil.randomString());
		responseEventDefinition.setDisplayName(RandomTestUtil.randomString());
		responseEventDefinition.setHidden(RandomTestUtil.randomBoolean());
		responseEventDefinition.setId(RandomTestUtil.randomString());
		responseEventDefinition.setName(RandomTestUtil.randomString());
		responseEventDefinition.setType(type);

		return responseEventDefinition;
	}

	private final EventDefinitionDTOConverter _eventDefinitionDTOConverter =
		new EventDefinitionDTOConverter();

}