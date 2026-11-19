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

		GetWorkspaceGroupEventAttributeDefinitionsPageResponse.
			EventAttributeDefinition responseEventAttributeDefinition =
				_createResponseEventAttributeDefinition("DURATION", "LOCAL");

		EventAttributeDefinition eventAttributeDefinition =
			_eventAttributeDefinitionDTOConverter.toDTO(
				null, responseEventAttributeDefinition);

		Assert.assertEquals(
			EventAttributeDefinition.DataType.DURATION,
			eventAttributeDefinition.getDataType());
		Assert.assertEquals(
			responseEventAttributeDefinition.getDescription(),
			eventAttributeDefinition.getDescription());
		Assert.assertEquals(
			responseEventAttributeDefinition.getDisplayName(),
			eventAttributeDefinition.getDisplayName());
		Assert.assertEquals(
			responseEventAttributeDefinition.getId(),
			eventAttributeDefinition.getId());
		Assert.assertEquals(
			responseEventAttributeDefinition.getName(),
			eventAttributeDefinition.getName());
		Assert.assertEquals(
			responseEventAttributeDefinition.getSampleValue(),
			eventAttributeDefinition.getSampleValue());
		Assert.assertEquals(
			EventAttributeDefinition.Type.LOCAL,
			eventAttributeDefinition.getType());
	}

	@Test
	public void testToDTOWithUnknownEnums() {
		EventAttributeDefinition eventAttributeDefinition =
			_eventAttributeDefinitionDTOConverter.toDTO(
				null,
				_createResponseEventAttributeDefinition(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString()));

		Assert.assertNull(eventAttributeDefinition.getDataType());
		Assert.assertNull(eventAttributeDefinition.getType());

		eventAttributeDefinition = _eventAttributeDefinitionDTOConverter.toDTO(
			null, _createResponseEventAttributeDefinition(null, null));

		Assert.assertNull(eventAttributeDefinition.getDataType());
		Assert.assertNull(eventAttributeDefinition.getType());
	}

	private GetWorkspaceGroupEventAttributeDefinitionsPageResponse.
		EventAttributeDefinition _createResponseEventAttributeDefinition(
			String dataType, String type) {

		GetWorkspaceGroupEventAttributeDefinitionsPageResponse.
			EventAttributeDefinition responseEventAttributeDefinition =
				new GetWorkspaceGroupEventAttributeDefinitionsPageResponse.
					EventAttributeDefinition();

		responseEventAttributeDefinition.setDataType(dataType);
		responseEventAttributeDefinition.setDescription(
			RandomTestUtil.randomString());
		responseEventAttributeDefinition.setDisplayName(
			RandomTestUtil.randomString());
		responseEventAttributeDefinition.setId(RandomTestUtil.randomString());
		responseEventAttributeDefinition.setName(RandomTestUtil.randomString());
		responseEventAttributeDefinition.setSampleValue(
			RandomTestUtil.randomString());
		responseEventAttributeDefinition.setType(type);

		return responseEventAttributeDefinition;
	}

	private final EventAttributeDefinitionDTOConverter
		_eventAttributeDefinitionDTOConverter =
			new EventAttributeDefinitionDTOConverter();

}