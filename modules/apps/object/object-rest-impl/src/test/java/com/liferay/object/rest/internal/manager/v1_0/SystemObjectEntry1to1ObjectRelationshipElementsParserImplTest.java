/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.manager.v1_0;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Carolina Barbosa
 */
public class SystemObjectEntry1to1ObjectRelationshipElementsParserImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testParse() throws Exception {
		Map<String, Object> properties = HashMapBuilder.<String, Object>put(
			"emailAddress", RandomTestUtil.randomString()
		).put(
			"screenName", RandomTestUtil.randomString()
		).build();

		List<Map<String, Object>> systemObjectEntryMaps =
			_systemObjectEntry1to1ObjectRelationshipElementsParserImpl.parse(
				_objectRelationship, properties);

		Assert.assertEquals(
			systemObjectEntryMaps.toString(), 1, systemObjectEntryMaps.size());
		Assert.assertEquals(properties, systemObjectEntryMaps.get(0));
	}

	private final ObjectRelationship _objectRelationship = Mockito.mock(
		ObjectRelationship.class);
	private final SystemObjectEntry1to1ObjectRelationshipElementsParserImpl
		_systemObjectEntry1to1ObjectRelationshipElementsParserImpl =
			new SystemObjectEntry1to1ObjectRelationshipElementsParserImpl(
				Mockito.mock(ObjectDefinition.class));

}