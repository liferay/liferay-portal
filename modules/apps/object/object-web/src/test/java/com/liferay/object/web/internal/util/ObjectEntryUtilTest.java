/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.util;

import com.liferay.headless.delivery.dto.v1_0.Creator;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.impl.ObjectEntryImpl;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jhosseph Gonzalez
 */
public class ObjectEntryUtilTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testToObjectEntry() {
		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry =
			new com.liferay.object.rest.dto.v1_0.ObjectEntry();

		Creator creator = new Creator();

		creator.setId(RandomTestUtil.randomLong());
		creator.setName(RandomTestUtil.randomString());

		objectEntry.setCreator(creator);

		objectEntry.setDateCreated(new Date());
		objectEntry.setDateModified(new Date());
		objectEntry.setDisplayDate(new Date());

		try (MockedStatic<ObjectEntryLocalServiceUtil>
				objectEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
					ObjectEntryLocalServiceUtil.class)) {

			ObjectEntry serviceBuilderObjectEntry = new ObjectEntryImpl();

			objectEntryLocalServiceUtilMockedStatic.when(
				() -> ObjectEntryLocalServiceUtil.createObjectEntry(
					Mockito.anyLong())
			).thenReturn(
				serviceBuilderObjectEntry
			);

			Mockito.when(
				objectDefinition.getObjectDefinitionId()
			).thenReturn(
				RandomTestUtil.randomLong()
			);

			serviceBuilderObjectEntry = ObjectEntryUtil.toObjectEntry(
				objectDefinition, objectEntry);

			Assert.assertEquals(
				(long)creator.getId(), serviceBuilderObjectEntry.getUserId());
			Assert.assertEquals(
				creator.getName(), serviceBuilderObjectEntry.getUserName());
			Assert.assertEquals(
				objectEntry.getDateCreated(),
				serviceBuilderObjectEntry.getCreateDate());
			Assert.assertEquals(
				objectEntry.getDateModified(),
				serviceBuilderObjectEntry.getModifiedDate());
			Assert.assertEquals(
				objectEntry.getDisplayDate(),
				serviceBuilderObjectEntry.getDisplayDate());

			objectEntryLocalServiceUtilMockedStatic.verify(
				() -> ObjectEntryLocalServiceUtil.createObjectEntry(
					Mockito.anyLong()));
		}
	}

}