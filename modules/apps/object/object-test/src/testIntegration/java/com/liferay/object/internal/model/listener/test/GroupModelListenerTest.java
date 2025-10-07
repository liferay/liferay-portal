/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.exception.NoSuchObjectDefinitionSettingException;
import com.liferay.object.exception.RequiredObjectRelationshipException;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class GroupModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testOnBeforeRemove() throws Exception {

		// Depot

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_testOnBeforeRemove(depotEntry.getGroup());

		// Site

		_testOnBeforeRemove(GroupTestUtil.addGroup());
	}

	private void _testOnBeforeRemove(Group group) throws Exception {
		ObjectField objectField = new TextObjectFieldBuilder(
		).labelMap(
			RandomTestUtil.randomLocaleStringMap()
		).name(
			StringUtil.randomId()
		).build();

		String scope = group.isSite() ? ObjectDefinitionConstants.SCOPE_SITE :
			ObjectDefinitionConstants.SCOPE_DEPOT;

		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(objectField), scope);
		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(objectField), scope);

		if (scope.equals(ObjectDefinitionConstants.SCOPE_DEPOT)) {
			_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
				objectDefinition1.getUserId(),
				objectDefinition1.getObjectDefinitionId(),
				ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS,
				String.valueOf(group.getGroupId()));

			DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				DepotConstants.TYPE_ASSET_LIBRARY,
				ServiceContextTestUtil.getServiceContext());

			_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
				objectDefinition2.getUserId(),
				objectDefinition2.getObjectDefinitionId(),
				ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS,
				StringBundler.concat(
					group.getGroupId(), StringPool.COMMA,
					depotEntry.getGroupId()));
		}

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, objectDefinition1,
				objectDefinition2,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT);

		ObjectEntry objectEntry1 = _objectEntryLocalService.addObjectEntry(
			group.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition1.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				objectField.getName(), RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		ObjectField relationshipObjectField =
			_objectFieldLocalService.getObjectField(
				objectRelationship.getObjectFieldId2());

		ObjectEntry objectEntry2 = _objectEntryLocalService.addObjectEntry(
			group.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition2.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				objectField.getName(), RandomTestUtil.randomString()
			).put(
				relationshipObjectField.getName(),
				objectEntry1.getObjectEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		AssertUtils.assertFailure(
			RequiredObjectRelationshipException.class,
			StringBundler.concat(
				"Object relationship ",
				objectRelationship.getObjectRelationshipId(),
				" does not allow deletes"),
			() -> _objectEntryLocalService.deleteObjectEntry(objectEntry1));

		GroupTestUtil.deleteGroup(group);

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				objectEntry1.getObjectEntryId()));
		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				objectEntry2.getObjectEntryId()));

		if (scope.equals(ObjectDefinitionConstants.SCOPE_DEPOT)) {
			AssertUtils.assertFailure(
				NoSuchObjectDefinitionSettingException.class,
				StringBundler.concat(
					"No ObjectDefinitionSetting exists with the key ",
					"{objectDefinitionId=",
					objectDefinition1.getObjectDefinitionId(), ", name=",
					ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS,
					"}"),
				() ->
					_objectDefinitionSettingLocalService.
						getObjectDefinitionSetting(
							objectDefinition1.getObjectDefinitionId(),
							ObjectDefinitionSettingConstants.
								NAME_ACCEPTED_GROUP_IDS));

			ObjectDefinitionSetting objectDefinitionSetting =
				_objectDefinitionSettingLocalService.getObjectDefinitionSetting(
					objectDefinition2.getObjectDefinitionId(),
					ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS);

			String acceptedGroupIds = objectDefinitionSetting.getValue();

			List<Long> acceptedGroupIdsList = TransformUtil.transformToList(
				acceptedGroupIds.split("\\s*,\\s*"), GetterUtil::getLong);

			Assert.assertFalse(
				acceptedGroupIdsList.contains(group.getGroupId()));
		}

		_objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition1);
		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition2);
	}

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}