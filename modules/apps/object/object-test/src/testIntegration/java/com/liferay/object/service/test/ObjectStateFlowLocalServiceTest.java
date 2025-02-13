/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.exception.NoSuchObjectStateException;
import com.liferay.object.field.builder.ObjectFieldBuilder;
import com.liferay.object.field.builder.PicklistObjectFieldBuilder;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectState;
import com.liferay.object.model.ObjectStateFlow;
import com.liferay.object.model.ObjectStateTransition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectStateFlowLocalService;
import com.liferay.object.service.ObjectStateLocalService;
import com.liferay.object.service.ObjectStateTransitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
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
 * @author Selton Guedes
 */
@RunWith(Arquillian.class)
public class ObjectStateFlowLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false, Collections.emptyList());

		_step1ListTypeEntry = _addListTypeEntry("step1");
		_step2ListTypeEntry = _addListTypeEntry("step2");
		_step3ListTypeEntry = _addListTypeEntry("step3");

		_objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, false,
				false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), Collections.emptyList());

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new PicklistObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).listTypeDefinitionId(
				_listTypeDefinition.getListTypeDefinitionId()
			).indexedAsKeyword(
				true
			).name(
				StringUtil.randomId()
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).objectFieldSettings(
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_DEFAULT_VALUE
					).value(
						_step1ListTypeEntry.getKey()
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE
					).value(
						ObjectFieldSettingConstants.VALUE_INPUT_AS_VALUE
					).build())
			).required(
				true
			).state(
				true
			).build());

		_objectStateFlow =
			_objectStateFlowLocalService.fetchObjectFieldObjectStateFlow(
				objectField.getObjectFieldId());
	}

	@Test
	public void testAddDefaultObjectStateFlow() throws Exception {
		Assert.assertNull(
			_objectStateFlowLocalService.addDefaultObjectStateFlow(
				_addObjectField(
					_listTypeDefinition.getListTypeDefinitionId(), false)));

		ObjectField objectField = _addObjectField(
			_listTypeDefinition.getListTypeDefinitionId(), true);

		ObjectStateFlow objectStateFlow =
			_objectStateFlowLocalService.addDefaultObjectStateFlow(objectField);

		Assert.assertEquals(
			TestPropsValues.getCompanyId(), objectStateFlow.getCompanyId());
		Assert.assertEquals(
			TestPropsValues.getUserId(), objectStateFlow.getUserId());
		Assert.assertEquals(
			objectField.getObjectFieldId(), objectStateFlow.getObjectFieldId());

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				objectField.getObjectFieldId(),
				ObjectFieldSettingConstants.NAME_STATE_FLOW);

		Assert.assertNotNull(objectFieldSetting);
		Assert.assertEquals(
			String.valueOf(objectStateFlow.getObjectStateFlowId()),
			objectFieldSetting.getValue());

		Assert.assertEquals(
			Arrays.asList(
				_step1ListTypeEntry.getListTypeEntryId(),
				_step2ListTypeEntry.getListTypeEntryId(),
				_step3ListTypeEntry.getListTypeEntryId()),
			ListUtil.toList(
				_objectStateLocalService.getObjectStateFlowObjectStates(
					objectStateFlow.getObjectStateFlowId()),
				ObjectState::getListTypeEntryId));

		_assertNextObjectStates(
			Arrays.asList(
				_step2ListTypeEntry.getListTypeEntryId(),
				_step3ListTypeEntry.getListTypeEntryId()),
			_step1ListTypeEntry.getListTypeEntryId(),
			objectStateFlow.getObjectStateFlowId());
		_assertNextObjectStates(
			Arrays.asList(
				_step1ListTypeEntry.getListTypeEntryId(),
				_step3ListTypeEntry.getListTypeEntryId()),
			_step2ListTypeEntry.getListTypeEntryId(),
			objectStateFlow.getObjectStateFlowId());
		_assertNextObjectStates(
			Arrays.asList(
				_step1ListTypeEntry.getListTypeEntryId(),
				_step2ListTypeEntry.getListTypeEntryId()),
			_step3ListTypeEntry.getListTypeEntryId(),
			objectStateFlow.getObjectStateFlowId());
	}

	@Test
	public void testAddListTypeEntry() throws Exception {
		ListTypeEntry listTypeEntry = _addListTypeEntry(
			RandomTestUtil.randomString());

		Assert.assertNotNull(
			_objectStateLocalService.getObjectStateFlowObjectState(
				listTypeEntry.getListTypeEntryId(),
				_objectStateFlow.getObjectStateFlowId()));
	}

	@Test
	public void testDeleteListType() throws Exception {
		_listTypeEntryLocalService.deleteListTypeEntry(
			_step1ListTypeEntry.getListTypeEntryId());

		try {
			_objectStateLocalService.getObjectStateFlowObjectState(
				_step1ListTypeEntry.getListTypeEntryId(),
				_objectStateFlow.getObjectStateFlowId());

			Assert.fail();
		}
		catch (NoSuchObjectStateException noSuchObjectStateException) {
			Assert.assertEquals(
				noSuchObjectStateException.getMessage(),
				StringBundler.concat(
					"No ObjectState exists with the key {listTypeEntryId=",
					_step1ListTypeEntry.getListTypeEntryId(),
					", objectStateFlowId=",
					_objectStateFlow.getObjectStateFlowId(), "}"));
		}
	}

	@Test
	public void testDeleteObjectFieldObjectStateFlow() throws Exception {
		ObjectField objectField = _addObjectField(
			_listTypeDefinition.getListTypeDefinitionId(), true);

		ObjectStateFlow objectStateFlow =
			_objectStateFlowLocalService.fetchObjectFieldObjectStateFlow(
				objectField.getObjectFieldId());

		Assert.assertNotNull(objectStateFlow);

		_objectStateFlowLocalService.deleteObjectFieldObjectStateFlow(
			objectStateFlow.getObjectFieldId());

		Assert.assertNull(
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				objectStateFlow.getObjectFieldId(),
				ObjectFieldSettingConstants.NAME_STATE_FLOW));

		Assert.assertNull(
			_objectStateFlowLocalService.fetchObjectFieldObjectStateFlow(
				objectField.getObjectFieldId()));

		Assert.assertEquals(
			Collections.emptyList(),
			_objectStateLocalService.getObjectStateFlowObjectStates(
				objectStateFlow.getObjectStateFlowId()));

		Assert.assertEquals(
			Collections.emptyList(),
			_objectStateTransitionLocalService.
				getObjectStateFlowObjectStateTransitions(
					objectStateFlow.getObjectStateFlowId()));
	}

	@Test
	public void testUpdateDefaultObjectStateFlow() throws Exception {
		ObjectFieldBuilder objectFieldBuilder = new ObjectFieldBuilder();

		Assert.assertNull(
			_objectStateFlowLocalService.updateDefaultObjectStateFlow(
				objectFieldBuilder.build(), objectFieldBuilder.build()));

		ObjectField objectField1 = _addObjectField(
			_listTypeDefinition.getListTypeDefinitionId(), true);

		Assert.assertNotNull(
			_objectStateFlowLocalService.fetchObjectFieldObjectStateFlow(
				objectField1.getObjectFieldId()));

		Assert.assertNull(
			_objectStateFlowLocalService.updateDefaultObjectStateFlow(
				objectFieldBuilder.build(), objectField1));

		Assert.assertNull(
			_objectStateFlowLocalService.fetchObjectFieldObjectStateFlow(
				objectField1.getObjectFieldId()));

		ObjectField objectField2 = _addObjectField(
			_listTypeDefinition.getListTypeDefinitionId(), false);

		Assert.assertNull(
			_objectStateFlowLocalService.fetchObjectFieldObjectStateFlow(
				objectField2.getObjectFieldId()));

		Assert.assertNotNull(
			_objectStateFlowLocalService.updateDefaultObjectStateFlow(
				objectFieldBuilder.state(
					true
				).listTypeDefinitionId(
					_listTypeDefinition.getListTypeDefinitionId()
				).objectFieldId(
					objectField2.getObjectFieldId()
				).userId(
					TestPropsValues.getUserId()
				).build(),
				objectField2));

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false,
				Arrays.asList(
					ListTypeEntryUtil.createListTypeEntry(
						RandomTestUtil.randomString()),
					ListTypeEntryUtil.createListTypeEntry(
						RandomTestUtil.randomString())));

		ObjectStateFlow objectStateFlow =
			_objectStateFlowLocalService.updateDefaultObjectStateFlow(
				objectFieldBuilder.state(
					true
				).listTypeDefinitionId(
					listTypeDefinition.getListTypeDefinitionId()
				).objectFieldId(
					objectField2.getObjectFieldId()
				).userId(
					TestPropsValues.getUserId()
				).build(),
				objectField2);

		Assert.assertNotNull(objectStateFlow);

		List<ObjectState> objectStates =
			_objectStateLocalService.getObjectStateFlowObjectStates(
				objectStateFlow.getObjectStateFlowId());

		Assert.assertEquals(objectStates.toString(), 2, objectStates.size());
	}

	@Test
	public void testUpdateObjectStateTransitions() throws Exception {
		ListTypeEntry listTypeEntry = _addListTypeEntry("step4");

		List<ObjectState> objectStates =
			_objectStateLocalService.getObjectStateFlowObjectStates(
				_objectStateFlow.getObjectStateFlowId());

		for (ObjectState objectState : objectStates) {
			objectState.setObjectStateTransitions(
				_objectStateTransitionLocalService.
					getObjectStateObjectStateTransitions(
						objectState.getObjectStateId()));
		}

		_objectStateFlow.setObjectStates(objectStates);

		ObjectStateFlow newObjectStateFlow =
			(ObjectStateFlow)_objectStateFlow.clone();

		List<ObjectState> newObjectStates = new ArrayList<>(objectStates);

		for (ObjectState objectState : objectStates) {
			List<ObjectStateTransition> objectStateTransitions =
				_objectStateTransitionLocalService.
					getObjectStateObjectStateTransitions(
						objectState.getObjectStateId());

			if (objectStateTransitions.isEmpty()) {
				continue;
			}

			objectState.setObjectStateTransitions(
				Arrays.asList(
					objectStateTransitions.get(0),
					_objectStateTransitionLocalService.addObjectStateTransition(
						TestPropsValues.getUserId(),
						_objectStateFlow.getObjectStateFlowId(),
						objectState.getObjectStateId(),
						listTypeEntry.getListTypeEntryId())));
		}

		newObjectStateFlow.setObjectStates(newObjectStates);

		_objectStateTransitionLocalService.updateObjectStateTransitions(
			newObjectStateFlow);

		_assertEquals(
			newObjectStates,
			_objectStateLocalService.getObjectStateFlowObjectStates(
				_objectStateFlow.getObjectStateFlowId()));
	}

	private ListTypeEntry _addListTypeEntry(String key) throws Exception {
		return _listTypeEntryLocalService.addListTypeEntry(
			null, TestPropsValues.getUserId(),
			_listTypeDefinition.getListTypeDefinitionId(), key,
			LocalizedMapUtil.getLocalizedMap(key));
	}

	private ObjectField _addObjectField(
			long listTypeDefinitionId, boolean state)
		throws Exception {

		List<ObjectFieldSetting> objectFieldSettings = Collections.emptyList();

		if (state) {
			objectFieldSettings = Arrays.asList(
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_DEFAULT_VALUE
				).value(
					_step1ListTypeEntry.getKey()
				).build(),
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_DEFAULT_VALUE_TYPE
				).value(
					ObjectFieldSettingConstants.VALUE_INPUT_AS_VALUE
				).build());
		}

		return ObjectFieldUtil.addCustomObjectField(
			new PicklistObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).listTypeDefinitionId(
				listTypeDefinitionId
			).indexedAsKeyword(
				true
			).name(
				StringUtil.randomId()
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).objectFieldSettings(
				objectFieldSettings
			).required(
				true
			).state(
				state
			).build());
	}

	private void _assertEquals(
		List<ObjectState> objectStates1, List<ObjectState> objectStates2) {

		Assert.assertEquals(
			objectStates1.toString(), objectStates1.size(),
			objectStates2.size());

		for (int i = 0; i < objectStates1.size(); i++) {
			ObjectState objectState1 = objectStates1.get(i);
			ObjectState objectState2 = objectStates2.get(i);

			Assert.assertEquals(
				objectState1.getListTypeEntryId(),
				objectState2.getListTypeEntryId());

			List<ObjectStateTransition> objectStateTransitions1 =
				objectState1.getObjectStateTransitions();
			List<ObjectStateTransition> objectStateTransitions2 =
				_objectStateTransitionLocalService.
					getObjectStateObjectStateTransitions(
						objectState2.getObjectStateId());

			Assert.assertEquals(
				objectStateTransitions1.toString(),
				objectStateTransitions1.size(), objectStateTransitions2.size());

			for (int j = 0; j < objectStateTransitions1.size(); j++) {
				ObjectStateTransition objectStateTransition1 =
					objectStateTransitions1.get(j);
				ObjectStateTransition objectStateTransition2 =
					objectStateTransitions2.get(j);

				Assert.assertEquals(
					objectStateTransition1.getSourceObjectStateId(),
					objectStateTransition2.getSourceObjectStateId());
				Assert.assertEquals(
					objectStateTransition1.getTargetObjectStateId(),
					objectStateTransition2.getTargetObjectStateId());
			}
		}
	}

	private void _assertNextObjectStates(
			List<Long> expectedListTypeEntryIds, long listTypeEntryId,
			long objectStateFlowId)
		throws Exception {

		ObjectState objectState =
			_objectStateLocalService.getObjectStateFlowObjectState(
				listTypeEntryId, objectStateFlowId);

		Assert.assertEquals(
			expectedListTypeEntryIds,
			ListUtil.toList(
				_objectStateLocalService.getNextObjectStates(
					objectState.getObjectStateId()),
				ObjectState::getListTypeEntryId));
	}

	private ListTypeDefinition _listTypeDefinition;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	private ObjectStateFlow _objectStateFlow;

	@Inject
	private ObjectStateFlowLocalService _objectStateFlowLocalService;

	@Inject
	private ObjectStateLocalService _objectStateLocalService;

	@Inject
	private ObjectStateTransitionLocalService
		_objectStateTransitionLocalService;

	private ListTypeEntry _step1ListTypeEntry;
	private ListTypeEntry _step2ListTypeEntry;
	private ListTypeEntry _step3ListTypeEntry;

}