/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.info.collection.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.collection.provider.CollectionQuery;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.pagination.InfoPage;
import com.liferay.info.pagination.Pagination;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class ManyToManyObjectRelationshipInfoCollectionProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_objectDefinition1 = _addObjectDefinition(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"objectDefinition1TextObjectFieldName"
			).build());

		_objectDefinition1 =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				_objectDefinition1.getObjectDefinitionId());

		_objectDefinition2 = _addObjectDefinition(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"objectDefinition2TextObjectFieldName"
			).build());

		_objectDefinition2 =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				_objectDefinition2.getObjectDefinitionId());

		_objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_objectDefinition2.getObjectDefinitionId(),
				_objectDefinition1.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);
	}

	@Test
	public void testManyToManyObjectRelationshipRelatedInfoCollectionProvider()
		throws Exception {

		ObjectEntry objectDefinition1ObjectEntry1 =
			_objectEntryLocalService.addObjectEntry(
				TestPropsValues.getUserId(), _group.getGroupId(),
				_objectDefinition1.getObjectDefinitionId(), null,
				HashMapBuilder.<String, Serializable>put(
					"objectDefinition1TextObjectFieldName",
					RandomTestUtil.randomString()
				).build(),
				ServiceContextTestUtil.getServiceContext());

		ObjectEntry objectDefinition1ObjectEntry2 =
			_objectEntryLocalService.addObjectEntry(
				TestPropsValues.getUserId(), _group.getGroupId(),
				_objectDefinition1.getObjectDefinitionId(), null,
				HashMapBuilder.<String, Serializable>put(
					"objectDefinition1TextObjectFieldName",
					RandomTestUtil.randomString()
				).build(),
				ServiceContextTestUtil.getServiceContext());

		ObjectEntry objectDefinition2ObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				TestPropsValues.getUserId(), _group.getGroupId(),
				_objectDefinition2.getObjectDefinitionId(), null,
				HashMapBuilder.<String, Serializable>put(
					"objectDefinition2TextObjectFieldName",
					RandomTestUtil.randomString()
				).build(),
				ServiceContextTestUtil.getServiceContext());

		_objectRelationshipLocalService.addObjectRelationshipMappingTableValues(
			TestPropsValues.getUserId(),
			_objectRelationship.getObjectRelationshipId(),
			objectDefinition2ObjectEntry.getObjectEntryId(),
			objectDefinition1ObjectEntry1.getObjectEntryId(),
			new ServiceContext());

		_objectRelationshipLocalService.addObjectRelationshipMappingTableValues(
			TestPropsValues.getUserId(),
			_objectRelationship.getObjectRelationshipId(),
			objectDefinition2ObjectEntry.getObjectEntryId(),
			objectDefinition1ObjectEntry2.getObjectEntryId(),
			new ServiceContext());

		_assertRelatedInfoCollectionProvider(
			_objectDefinition1, objectDefinition1ObjectEntry1,
			objectDefinition2ObjectEntry);
		_assertRelatedInfoCollectionProvider(
			_objectDefinition1, objectDefinition1ObjectEntry2,
			objectDefinition2ObjectEntry);
		_assertRelatedInfoCollectionProvider(
			_objectDefinition2, objectDefinition2ObjectEntry,
			objectDefinition1ObjectEntry1, objectDefinition1ObjectEntry2);
	}

	private ObjectDefinition _addObjectDefinition(ObjectField objectField)
		throws Exception {

		return _objectDefinitionLocalService.addCustomObjectDefinition(
			TestPropsValues.getUserId(), 0, null, false, false, true, false,
			false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			ObjectDefinitionTestUtil.getRandomName(), null, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			true, ObjectDefinitionConstants.SCOPE_SITE,
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
			Collections.emptyList(), Arrays.asList(objectField));
	}

	private void _assertRelatedInfoCollectionProvider(
		ObjectDefinition objectDefinition, ObjectEntry objectEntry,
		ObjectEntry... relatedObjectEntries) {

		RelatedInfoItemCollectionProvider relatedInfoItemCollectionProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				RelatedInfoItemCollectionProvider.class,
				objectDefinition.getClassName());

		Assert.assertNotNull(relatedInfoItemCollectionProvider);

		CollectionQuery collectionQuery = new CollectionQuery();

		collectionQuery.setPagination(
			Pagination.of(relatedObjectEntries.length, 0));
		collectionQuery.setRelatedItemObject(objectEntry);

		InfoPage collectionInfoPage =
			relatedInfoItemCollectionProvider.getCollectionInfoPage(
				collectionQuery);

		List<ObjectEntry> objectEntries = collectionInfoPage.getPageItems();

		Assert.assertNotNull(objectEntries);

		Assert.assertEquals(
			objectEntries.toString(), relatedObjectEntries.length,
			objectEntries.size());

		Assert.assertEquals(
			relatedObjectEntries.length, collectionInfoPage.getTotalCount());

		for (ObjectEntry relatedObjectEntry : relatedObjectEntries) {
			Assert.assertTrue(objectEntries.contains(relatedObjectEntry));
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	private ObjectDefinition _objectDefinition1;
	private ObjectDefinition _objectDefinition2;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@DeleteAfterTestRun
	private ObjectRelationship _objectRelationship;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}