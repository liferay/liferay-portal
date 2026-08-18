/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.portlet;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Alberto Sousa
 */
public class ObjectEntriesPortletTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			_objectDefinitionLocalService.fetchObjectDefinition(
				_RELATED_OBJECT_DEFINITION_ID)
		).thenReturn(
			_relatedObjectDefinition
		);
	}

	@Test
	@TestInfo("LPD-102111")
	public void testGetObjectDefinition() {
		_testGetObjectDefinition(_objectDefinition, 0, null);
		_testGetObjectDefinition(
			_objectDefinition, _OBJECT_DEFINITION_ID, null);
		_testGetObjectDefinition(
			_objectDefinition, _RELATED_OBJECT_DEFINITION_ID,
			_getObjectRelationship(
				_OBJECT_DEFINITION_ID, _UNRELATED_OBJECT_DEFINITION_ID));
		_testGetObjectDefinition(
			_objectDefinition, _RELATED_OBJECT_DEFINITION_ID,
			_getObjectRelationship(
				_UNRELATED_OBJECT_DEFINITION_ID,
				_RELATED_OBJECT_DEFINITION_ID));
		_testGetObjectDefinition(
			_objectDefinition, _RELATED_OBJECT_DEFINITION_ID, null);
		_testGetObjectDefinition(
			_relatedObjectDefinition, _RELATED_OBJECT_DEFINITION_ID,
			_getObjectRelationship(
				_OBJECT_DEFINITION_ID, _RELATED_OBJECT_DEFINITION_ID));
	}

	private ObjectRelationship _getObjectRelationship(
		long objectDefinitionId1, long objectDefinitionId2) {

		ObjectRelationship objectRelationship = Mockito.mock(
			ObjectRelationship.class);

		Mockito.when(
			objectRelationship.getObjectDefinitionId1()
		).thenReturn(
			objectDefinitionId1
		);

		Mockito.when(
			objectRelationship.getObjectDefinitionId2()
		).thenReturn(
			objectDefinitionId2
		);

		return objectRelationship;
	}

	private RenderRequest _getRenderRequest(long objectDefinitionId) {
		RenderRequest renderRequest = Mockito.mock(RenderRequest.class);

		Mockito.when(
			renderRequest.getParameter("objectDefinitionId")
		).thenReturn(
			String.valueOf(objectDefinitionId)
		);

		Mockito.when(
			renderRequest.getParameter("objectRelationshipId")
		).thenReturn(
			String.valueOf(_OBJECT_RELATIONSHIP_ID)
		);

		return renderRequest;
	}

	private void _testGetObjectDefinition(
		ObjectDefinition expectedObjectDefinition, long objectDefinitionId,
		ObjectRelationship objectRelationship) {

		Mockito.when(
			_objectRelationshipLocalService.fetchObjectRelationship(
				_OBJECT_RELATIONSHIP_ID)
		).thenReturn(
			objectRelationship
		);

		ObjectEntriesPortlet objectEntriesPortlet = new ObjectEntriesPortlet(
			null, _OBJECT_DEFINITION_ID, _objectDefinitionLocalService, null,
			null, _objectRelationshipLocalService, null, null, null, null);

		Assert.assertEquals(
			expectedObjectDefinition,
			ReflectionTestUtil.invoke(
				objectEntriesPortlet, "_getObjectDefinition",
				new Class<?>[] {ObjectDefinition.class, RenderRequest.class},
				_objectDefinition, _getRenderRequest(objectDefinitionId)));
	}

	private static final long _OBJECT_DEFINITION_ID =
		RandomTestUtil.randomLong();

	private static final long _OBJECT_RELATIONSHIP_ID =
		RandomTestUtil.randomLong();

	private static final long _RELATED_OBJECT_DEFINITION_ID =
		RandomTestUtil.randomLong();

	private static final long _UNRELATED_OBJECT_DEFINITION_ID =
		RandomTestUtil.randomLong();

	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);
	private final ObjectDefinitionLocalService _objectDefinitionLocalService =
		Mockito.mock(ObjectDefinitionLocalService.class);
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService = Mockito.mock(
			ObjectRelationshipLocalService.class);
	private final ObjectDefinition _relatedObjectDefinition = Mockito.mock(
		ObjectDefinition.class);

}