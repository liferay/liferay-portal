/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.manager.v1_0;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Carolina Barbosa
 */
public class ObjectEntry1to1ObjectRelationshipElementsParserImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testParse() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		List<ObjectEntry> objectEntries =
			_objectEntry1to1ObjectRelationshipElementsParserImpl.parse(
				_objectRelationship,
				HashMapBuilder.<String, Object>put(
					"externalReferenceCode", externalReferenceCode
				).build());

		Assert.assertEquals(objectEntries.toString(), 1, objectEntries.size());

		ObjectEntry objectEntry = objectEntries.get(0);

		Assert.assertEquals(
			externalReferenceCode, objectEntry.getExternalReferenceCode());
	}

	private final ObjectEntry1to1ObjectRelationshipElementsParserImpl
		_objectEntry1to1ObjectRelationshipElementsParserImpl =
			new ObjectEntry1to1ObjectRelationshipElementsParserImpl(
				Mockito.mock(ObjectDefinition.class));
	private final ObjectRelationship _objectRelationship = Mockito.mock(
		ObjectRelationship.class);

}