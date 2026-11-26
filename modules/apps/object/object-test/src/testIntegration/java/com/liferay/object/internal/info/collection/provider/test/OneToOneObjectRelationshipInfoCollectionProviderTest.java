/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
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
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@FeatureFlag("LPD-108620")
@RunWith(Arquillian.class)
public class OneToOneObjectRelationshipInfoCollectionProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_objectFieldName = "a" + RandomTestUtil.randomString();

		_objectDefinition1 = ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					RandomTestUtil.randomLocaleStringMap()
				).name(
					_objectFieldName
				).build()),
			ObjectDefinitionConstants.SCOPE_SITE);
		_objectDefinition2 = ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					RandomTestUtil.randomLocaleStringMap()
				).name(
					_objectFieldName
				).build()),
			ObjectDefinitionConstants.SCOPE_SITE);
	}

	@Test
	public void testGetCollectionInfoPage() throws Exception {
		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, _objectDefinition1,
				_objectDefinition2,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
				StringUtil.randomId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_ONE);

		RelatedInfoItemCollectionProvider relatedInfoItemCollectionProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				RelatedInfoItemCollectionProvider.class,
				_objectDefinition1.getClassName());

		Assert.assertEquals(
			_objectDefinition2.getClassName(),
			relatedInfoItemCollectionProvider.getCollectionItemClassName());

		ObjectEntry objectEntry1 = _addObjectEntry(_objectDefinition1, null, 0);

		_assertCollectionInfoPage(objectEntry1, Pagination.of(10, 0));

		ObjectEntry objectEntry2 = _addObjectEntry(
			_objectDefinition2, objectRelationship,
			objectEntry1.getObjectEntryId());

		_assertCollectionInfoPage(
			objectEntry1, Pagination.of(10, 0), objectEntry2);
	}

	private ObjectEntry _addObjectEntry(
			ObjectDefinition objectDefinition,
			ObjectRelationship objectRelationship, long relatedObjectEntryId)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				_objectFieldName, RandomTestUtil.randomString()
			).put(
				() -> {
					if (objectRelationship == null) {
						return null;
					}

					return ObjectRelationshipUtil.
						getObjectRelationshipFieldName(
							_objectDefinition1, objectRelationship.getName());
				},
				() -> {
					if (objectRelationship == null) {
						return null;
					}

					return relatedObjectEntryId;
				}
			).build(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private void _assertCollectionInfoPage(
		ObjectEntry objectEntry, Pagination pagination,
		ObjectEntry... expectedObjectEntries) {

		RelatedInfoItemCollectionProvider relatedInfoItemCollectionProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				RelatedInfoItemCollectionProvider.class,
				_objectDefinition1.getClassName());

		CollectionQuery collectionQuery = new CollectionQuery();

		collectionQuery.setPagination(pagination);
		collectionQuery.setRelatedItemObject(objectEntry);

		InfoPage<ObjectEntry> infoPage =
			(InfoPage<ObjectEntry>)
				relatedInfoItemCollectionProvider.getCollectionInfoPage(
					collectionQuery);

		Assert.assertEquals(
			expectedObjectEntries.length, infoPage.getTotalCount());

		List<ObjectEntry> objectEntries =
			(List<ObjectEntry>)infoPage.getPageItems();

		Assert.assertEquals(
			objectEntries.toString(), expectedObjectEntries.length,
			objectEntries.size());

		for (ObjectEntry expectedObjectEntry : expectedObjectEntries) {
			Assert.assertTrue(objectEntries.contains(expectedObjectEntry));
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition1;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition2;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private String _objectFieldName;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}