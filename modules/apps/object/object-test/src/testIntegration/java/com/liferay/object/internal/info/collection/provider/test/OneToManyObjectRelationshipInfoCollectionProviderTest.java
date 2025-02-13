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
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
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
public class OneToManyObjectRelationshipInfoCollectionProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_childObjectDefinition = _addObjectDefinition(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"childTextObjectFieldName"
			).build());

		_childObjectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				_childObjectDefinition.getObjectDefinitionId());

		_parentObjectDefinition = _addObjectDefinition(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"parentTextObjectFieldName"
			).build());

		_parentObjectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				_parentObjectDefinition.getObjectDefinitionId());

		_objectRelationshipLocalService.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			_parentObjectDefinition.getObjectDefinitionId(),
			_childObjectDefinition.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			"oneToManyRelationshipName", false,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);
	}

	@Test
	public void testOneToManyObjectRelationshipRelatedInfoCollectionProvider()
		throws Exception {

		ObjectEntry parentObjectEntry = _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), _group.getGroupId(),
			_parentObjectDefinition.getObjectDefinitionId(), null,
			HashMapBuilder.<String, Serializable>put(
				"parentTextObjectFieldName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		ObjectEntry childObjectEntry1 = _addChildObjectEntry(
			_group, _childObjectDefinition, _parentObjectDefinition,
			parentObjectEntry);

		ObjectEntry childObjectEntry2 = _addChildObjectEntry(
			_group, _childObjectDefinition, _parentObjectDefinition,
			parentObjectEntry);

		RelatedInfoItemCollectionProvider relatedInfoItemCollectionProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				RelatedInfoItemCollectionProvider.class,
				_parentObjectDefinition.getClassName());

		Assert.assertNotNull(relatedInfoItemCollectionProvider);

		CollectionQuery collectionQuery = new CollectionQuery();

		collectionQuery.setPagination(Pagination.of(2, 0));
		collectionQuery.setRelatedItemObject(parentObjectEntry);

		InfoPage collectionInfoPage =
			relatedInfoItemCollectionProvider.getCollectionInfoPage(
				collectionQuery);

		List<ObjectEntry> objectEntries = collectionInfoPage.getPageItems();

		Assert.assertNotNull(objectEntries);
		Assert.assertEquals(objectEntries.toString(), 2, objectEntries.size());
		Assert.assertTrue(objectEntries.contains(childObjectEntry1));
		Assert.assertTrue(objectEntries.contains(childObjectEntry2));

		Assert.assertEquals(2, collectionInfoPage.getTotalCount());
	}

	private ObjectEntry _addChildObjectEntry(
			Group group, ObjectDefinition objectDefinition,
			ObjectDefinition parentObjectDefinition,
			ObjectEntry parentObjectEntry)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), group.getGroupId(),
			objectDefinition.getObjectDefinitionId(), null,
			HashMapBuilder.<String, Serializable>put(
				"childTextObjectFieldName", RandomTestUtil.randomString()
			).put(
				"r_oneToManyRelationshipName_" +
					parentObjectDefinition.getPKObjectFieldName(),
				parentObjectEntry.getObjectEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext());
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

	@DeleteAfterTestRun
	private ObjectDefinition _childObjectDefinition;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _parentObjectDefinition;

}