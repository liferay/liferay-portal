/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.info.collection.provider;

import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Carolina Barbosa
 */
public class OneToOneObjectRelationshipRelatedInfoCollectionProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpObjectEntry();
		_setUpObjectRelationship();
	}

	@Test
	public void testGetCollectionInfoPage() throws Exception {
		InfoPage<ObjectEntry> infoPage = _getCollectionInfoPage(
			Arrays.asList(
				Mockito.mock(ObjectEntry.class),
				Mockito.mock(ObjectEntry.class)),
			0);

		Assert.assertTrue(ListUtil.isEmpty(infoPage.getPageItems()));
		Assert.assertEquals(0, infoPage.getTotalCount());

		infoPage = _getCollectionInfoPage(Collections.emptyList(), 0);

		Assert.assertEquals(0, infoPage.getTotalCount());

		ObjectEntry relatedObjectEntry = Mockito.mock(ObjectEntry.class);

		infoPage = _getCollectionInfoPage(
			Collections.singletonList(relatedObjectEntry), 0);

		Assert.assertEquals(
			Collections.singletonList(relatedObjectEntry),
			infoPage.getPageItems());
		Assert.assertEquals(1, infoPage.getTotalCount());

		infoPage = _getCollectionInfoPage(
			Collections.singletonList(relatedObjectEntry), 1);

		Assert.assertTrue(ListUtil.isEmpty(infoPage.getPageItems()));
		Assert.assertEquals(1, infoPage.getTotalCount());
	}

	private InfoPage<ObjectEntry> _getCollectionInfoPage(
			List<ObjectEntry> relatedObjectEntries, int start)
		throws Exception {

		ObjectEntryLocalService objectEntryLocalService = Mockito.mock(
			ObjectEntryLocalService.class);

		Mockito.when(
			objectEntryLocalService.getRelatedObjectEntries(
				Mockito.anyLong(), Mockito.eq(_OBJECT_RELATIONSHIP_ID),
				Mockito.any(), Mockito.anyBoolean(),
				Mockito.eq(_OBJECT_ENTRY_ID), Mockito.anyBoolean(),
				Mockito.any(), Mockito.eq(QueryUtil.ALL_POS),
				Mockito.eq(QueryUtil.ALL_POS), Mockito.any())
		).thenReturn(
			relatedObjectEntries
		);

		OneToOneObjectRelationshipRelatedInfoCollectionProvider
			oneToOneObjectRelationshipRelatedInfoCollectionProvider =
				new OneToOneObjectRelationshipRelatedInfoCollectionProvider(
					Mockito.mock(Language.class),
					Mockito.mock(ObjectDefinition.class),
					Mockito.mock(ObjectDefinition.class),
					objectEntryLocalService, _objectRelationship);

		CollectionQuery collectionQuery = new CollectionQuery();

		collectionQuery.setPagination(Pagination.of(start + 10, start));
		collectionQuery.setRelatedItemObject(_objectEntry);

		return oneToOneObjectRelationshipRelatedInfoCollectionProvider.
			getCollectionInfoPage(collectionQuery);
	}

	private void _setUpObjectEntry() {
		Mockito.when(
			_objectEntry.getObjectEntryId()
		).thenReturn(
			_OBJECT_ENTRY_ID
		);
	}

	private void _setUpObjectRelationship() {
		Mockito.when(
			_objectRelationship.getObjectRelationshipId()
		).thenReturn(
			_OBJECT_RELATIONSHIP_ID
		);
	}

	private static final long _OBJECT_ENTRY_ID = RandomTestUtil.randomLong();

	private static final long _OBJECT_RELATIONSHIP_ID =
		RandomTestUtil.randomLong();

	private final ObjectEntry _objectEntry = Mockito.mock(ObjectEntry.class);
	private final ObjectRelationship _objectRelationship = Mockito.mock(
		ObjectRelationship.class);

}