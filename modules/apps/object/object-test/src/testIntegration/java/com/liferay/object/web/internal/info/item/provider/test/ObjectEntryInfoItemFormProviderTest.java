/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.type.OptionInfoFieldType;
import com.liferay.info.field.type.SelectInfoFieldType;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.builder.PicklistObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.model.ObjectState;
import com.liferay.object.model.ObjectStateFlow;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectStateFlowLocalService;
import com.liferay.object.service.ObjectStateLocalService;
import com.liferay.object.service.ObjectStateTransitionLocalService;
import com.liferay.object.test.util.ObjectActionTestUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.object.tree.Edge;
import com.liferay.object.tree.Node;
import com.liferay.object.tree.Tree;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.props.test.util.PropsTemporarySwapper;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
public class ObjectEntryInfoItemFormProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false, Collections.emptyList(), new ServiceContext());

		_listTypeEntry1 = _addListTypeEntry();
		_listTypeEntry2 = _addListTypeEntry();
		_listTypeEntry3 = _addListTypeEntry();

		_childObjectDefinition = _addObjectDefinition(
			new AttachmentObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"attachmentObjectFieldName"
			).objectFieldSettings(
				Arrays.asList(
					_createObjectFieldSetting("acceptedFileExtensions", "txt"),
					_createObjectFieldSetting(
						"fileSource", "documentsAndMedia"),
					_createObjectFieldSetting("maximumFileSize", "100"))
			).system(
				true
			).build(),
			new PicklistObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).listTypeDefinitionId(
				_listTypeDefinition.getListTypeDefinitionId()
			).name(
				"picklistObjectFieldName"
			).objectFieldSettings(
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_DEFAULT_VALUE
					).value(
						_listTypeEntry1.getKey()
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

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			_childObjectDefinition.getObjectDefinitionId(),
			"picklistObjectFieldName");

		ObjectStateFlow objectStateFlow =
			_objectStateFlowLocalService.fetchObjectFieldObjectStateFlow(
				objectField.getObjectFieldId());

		ObjectState objectState1 = _getObjectState(
			_listTypeEntry1, objectStateFlow);
		ObjectState objectState2 = _getObjectState(
			_listTypeEntry2, objectStateFlow);
		ObjectState objectState3 = _getObjectState(
			_listTypeEntry3, objectStateFlow);

		_objectStateTransitionLocalService.
			deleteObjectStateObjectStateTransitions(
				objectState1.getObjectStateId());
		_objectStateTransitionLocalService.
			deleteObjectStateObjectStateTransitions(
				objectState2.getObjectStateId());
		_objectStateTransitionLocalService.
			deleteObjectStateObjectStateTransitions(
				objectState3.getObjectStateId());

		_addObjectStateTransition(objectStateFlow, objectState1, objectState2);
		_addObjectStateTransition(objectStateFlow, objectState2, objectState3);

		objectStateFlow.setObjectStates(
			ListUtil.fromArray(objectState1, objectState2));

		_objectStateTransitionLocalService.updateObjectStateTransitions(
			objectStateFlow);

		_childObjectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				_childObjectDefinition.getObjectDefinitionId());

		_childInfoForm = _getInfoForm(_childObjectDefinition);

		_parentObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"parentTextObjectFieldName"
					).build()),
				false);

		_parentInfoForm = _getInfoForm(_parentObjectDefinition);

		_tree = TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			true,
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[] {"AAA"}
			).put(
				"AAA", new String[0]
			).build());

		_objectDefinitionA = _objectDefinitionLocalService.getObjectDefinition(
			TestPropsValues.getCompanyId(), "C_A");
		_objectDefinitionAA = _objectDefinitionLocalService.getObjectDefinition(
			TestPropsValues.getCompanyId(), "C_AA");
		_objectDefinitionAAA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AAA");
	}

	@After
	public void tearDown() throws Exception {
		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {
				_objectDefinitionA.getName(), _objectDefinitionAA.getName(),
				_objectDefinitionAAA.getName()
			},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testGetInfoForm() throws Exception {
		_testGetInfoFormWithAttachmentObjectField();
		_testGetInfoFormWithEdgeObjectRelationship();
		_testGetInfoFormWithEnableObjectEntrySchedule();
		_testGetInfoFormWithObjectAction();
		_testGetInfoFormWithObjectRelationship();
		_testGetInfoFormWithPicklistObjectField();
		_testGetInfoFormWithRootObjectDefinition();
	}

	private ListTypeEntry _addListTypeEntry() throws Exception {
		String listTypeEntryKey = RandomTestUtil.randomString();

		return _listTypeEntryLocalService.addListTypeEntry(
			null, TestPropsValues.getUserId(),
			_listTypeDefinition.getListTypeDefinitionId(), listTypeEntryKey,
			LocalizedMapUtil.getLocalizedMap(listTypeEntryKey),
			_listTypeDefinition.isSystem());
	}

	private ObjectDefinition _addObjectDefinition(ObjectField... objectFields)
		throws Exception {

		return _objectDefinitionLocalService.addCustomObjectDefinition(
			null, TestPropsValues.getUserId(), 0, null, true, false, true,
			false, true, false, false, false, false, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			ObjectDefinitionTestUtil.getRandomName(), null, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			true, ObjectDefinitionConstants.SCOPE_SITE,
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
			Collections.emptyList(), Arrays.asList(objectFields),
			Collections.emptyList(), new ServiceContext());
	}

	private void _addObjectStateTransition(
			ObjectStateFlow objectStateFlow, ObjectState sourceObjectState,
			ObjectState targetObjectState)
		throws Exception {

		sourceObjectState.setObjectStateTransitions(
			Collections.singletonList(
				_objectStateTransitionLocalService.addObjectStateTransition(
					TestPropsValues.getUserId(),
					objectStateFlow.getObjectStateFlowId(),
					sourceObjectState.getObjectStateId(),
					targetObjectState.getObjectStateId())));
	}

	private void _assertInfoField(
		boolean expectedEditable, String infoFieldName, InfoForm infoForm) {

		InfoField<?> infoField = infoForm.getInfoField(infoFieldName);

		Assert.assertEquals(expectedEditable, infoField.isEditable());
	}

	private void _assertInfoField(String infoFieldName, InfoForm infoForm) {
		Assert.assertNotNull(infoForm.getInfoField(infoFieldName));
	}

	private void _assertOptionInfoFieldType(
		String expectedValue, OptionInfoFieldType optionInfoFieldType) {

		Assert.assertEquals(
			expectedValue, optionInfoFieldType.getLabel(LocaleUtil.US));
		Assert.assertEquals(expectedValue, optionInfoFieldType.getValue());
	}

	private void _assertOptionInfoFieldTypes(
		InfoForm infoForm, String... listTypeEntryKeys) {

		InfoField infoField = infoForm.getInfoField("picklistObjectFieldName");

		List<OptionInfoFieldType> optionInfoFieldTypes =
			(List<OptionInfoFieldType>)infoField.getAttribute(
				SelectInfoFieldType.OPTIONS);

		Assert.assertEquals(
			optionInfoFieldTypes.toString(), 2, optionInfoFieldTypes.size());

		_assertOptionInfoFieldType(
			listTypeEntryKeys[0], optionInfoFieldTypes.get(0));
		_assertOptionInfoFieldType(
			listTypeEntryKeys[1], optionInfoFieldTypes.get(1));
	}

	private ObjectFieldSetting _createObjectFieldSetting(
		String name, String value) {

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.createObjectFieldSetting(0L);

		objectFieldSetting.setName(name);
		objectFieldSetting.setValue(value);

		return objectFieldSetting;
	}

	private InfoForm _getInfoForm(ObjectDefinition objectDefinition)
		throws Exception {

		InfoItemFormProvider<?> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, objectDefinition.getClassName());

		return infoItemFormProvider.getInfoForm(
			String.valueOf(objectDefinition.getObjectDefinitionId()), 0);
	}

	private ObjectState _getObjectState(
		ListTypeEntry listTypeEntry, ObjectStateFlow objectStateFlow) {

		return _objectStateLocalService.fetchObjectStateFlowObjectState(
			listTypeEntry.getListTypeEntryId(),
			objectStateFlow.getObjectStateFlowId());
	}

	private void _testGetInfoFormWithAttachmentObjectField() throws Exception {
		_assertInfoField("attachmentObjectFieldName", _childInfoForm);

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			_childObjectDefinition.getObjectDefinitionId(),
			"attachmentObjectFieldName");

		_assertInfoField(
			objectField.getObjectFieldId() + "#downloadURL", _childInfoForm);
		_assertInfoField(
			objectField.getObjectFieldId() + "#fileName", _childInfoForm);
		_assertInfoField(
			objectField.getObjectFieldId() + "#fileURL", _childInfoForm);
		_assertInfoField(
			objectField.getObjectFieldId() + "#mimeType", _childInfoForm);
		_assertInfoField(
			objectField.getObjectFieldId() + "#size", _childInfoForm);
	}

	private void _testGetInfoFormWithEdgeObjectRelationship() throws Exception {
		Node node = _tree.getNode(_objectDefinitionAA.getObjectDefinitionId());

		Edge edge = node.getEdge();

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				edge.getObjectRelationshipId());

		InfoForm infoForm = _getInfoForm(_objectDefinitionA);

		InfoFieldSet infoFieldSet = (InfoFieldSet)infoForm.getInfoFieldSetEntry(
			_objectDefinitionA.getName());

		InfoFieldSet relationshipInfoFieldSet =
			(InfoFieldSet)infoFieldSet.getInfoFieldSetEntry(
				objectRelationship.getName());

		Assert.assertNotNull(
			relationshipInfoFieldSet.getInfoFieldSetEntry("able"));
	}

	private void _testGetInfoFormWithEnableObjectEntrySchedule()
		throws Exception {

		_assertInfoField(false, "displayDate", _childInfoForm);
		_assertInfoField(false, "expirationDate", _childInfoForm);
		_assertInfoField(false, "reviewDate", _childInfoForm);

		_childObjectDefinition.setEnableObjectEntrySchedule(true);

		_childObjectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_childObjectDefinition);

		_objectDefinitionLocalService.deployObjectDefinition(
			_childObjectDefinition);

		_childInfoForm = _getInfoForm(_childObjectDefinition);

		_assertInfoField(true, "displayDate", _childInfoForm);
		_assertInfoField(true, "expirationDate", _childInfoForm);
		_assertInfoField(true, "reviewDate", _childInfoForm);
	}

	private void _testGetInfoFormWithObjectAction() throws Exception {
		ObjectAction objectAction = ObjectActionTestUtil.addObjectAction(
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_STANDALONE, _childObjectDefinition,
			UnicodePropertiesBuilder.put(
				"url", RandomTestUtil.randomString()
			).build());

		_assertInfoField(
			objectAction.getName(), _getInfoForm(_childObjectDefinition));
	}

	private void _testGetInfoFormWithObjectRelationship() throws Exception {
		_objectRelationship = ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService, _parentObjectDefinition,
			_childObjectDefinition);

		_childInfoForm = _getInfoForm(_childObjectDefinition);

		try (PropsTemporarySwapper propsTemporarySwapper =
				new PropsTemporarySwapper(
					"feature.flag.LPD-60546", Boolean.FALSE.toString())) {

			_parentInfoForm = _getInfoForm(_parentObjectDefinition);
		}

		_assertInfoField("parentTextObjectFieldName", _childInfoForm);
		_assertInfoField("parentTextObjectFieldName", _parentInfoForm);

		InfoFieldSet infoFieldSet =
			(InfoFieldSet)_parentInfoForm.getInfoFieldSetEntry(
				_parentObjectDefinition.getName());

		InfoFieldSet relationshipInfoFieldSet =
			(InfoFieldSet)infoFieldSet.getInfoFieldSetEntry(
				_objectRelationship.getName());

		Assert.assertNotNull(
			relationshipInfoFieldSet.getInfoFieldSetEntry(
				"attachmentObjectFieldName"));
		Assert.assertNotNull(
			relationshipInfoFieldSet.getInfoFieldSetEntry(
				"picklistObjectFieldName"));
	}

	private void _testGetInfoFormWithPicklistObjectField() throws Exception {
		_assertOptionInfoFieldTypes(
			_childInfoForm, _listTypeEntry1.getKey(), _listTypeEntry2.getKey());

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			TestPropsValues.getGroupId(),
			_childObjectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"picklistObjectFieldName", _listTypeEntry2.getKey()
			).build());

		try {
			ServiceContextThreadLocal.pushServiceContext(
				_updateServiceContext(
					objectEntry.getObjectEntryId(),
					_layoutDisplayPageProviderRegistry.
						getLayoutDisplayPageProviderByClassName(
							_childObjectDefinition.getCompanyId(),
							_childObjectDefinition.getClassName())));

			_assertOptionInfoFieldTypes(
				_getInfoForm(_childObjectDefinition), _listTypeEntry2.getKey(),
				_listTypeEntry3.getKey());

			JournalArticle journalArticle = JournalTestUtil.addArticle(
				TestPropsValues.getGroupId(), 0);

			_updateServiceContext(
				journalArticle.getResourcePrimKey(),
				_journalArticleLayoutDisplayPageProvider);

			_assertOptionInfoFieldTypes(
				_getInfoForm(_childObjectDefinition), _listTypeEntry1.getKey(),
				_listTypeEntry2.getKey());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@TestInfo("LPD-66678")
	private void _testGetInfoFormWithRootObjectDefinition() throws Exception {
		_parentInfoForm = _getInfoForm(_objectDefinitionA);

		_assertInfoField("able", _parentInfoForm);

		String objectFieldName = "a" + RandomTestUtil.randomString();

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				objectFieldName
			).objectDefinitionId(
				_objectDefinitionAA.getObjectDefinitionId()
			).userId(
				TestPropsValues.getUserId()
			).build());

		Assert.assertNull(_parentInfoForm.getInfoField(objectField.getName()));

		_childInfoForm = _getInfoForm(_objectDefinitionAA);

		_assertInfoField(objectFieldName, _childInfoForm);

		Node node = _tree.getNode(_objectDefinitionAA.getObjectDefinitionId());

		Edge edge = node.getEdge();

		_objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				edge.getObjectRelationshipId());

		objectField = _objectFieldLocalService.getObjectField(
			_objectRelationship.getObjectFieldId2());

		Assert.assertNull(_childInfoForm.getInfoField(objectField.getName()));

		objectFieldName = "a" + RandomTestUtil.randomString();

		objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				objectFieldName
			).objectDefinitionId(
				_objectDefinitionAAA.getObjectDefinitionId()
			).userId(
				TestPropsValues.getUserId()
			).build());

		Assert.assertNull(_childInfoForm.getInfoField(objectField.getName()));

		_childInfoForm = _getInfoForm(_objectDefinitionAAA);

		_assertInfoField(objectFieldName, _childInfoForm);

		node = _tree.getNode(_objectDefinitionAAA.getObjectDefinitionId());

		edge = node.getEdge();

		_objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				edge.getObjectRelationshipId());

		objectField = _objectFieldLocalService.getObjectField(
			_objectRelationship.getObjectFieldId2());

		Assert.assertNull(_childInfoForm.getInfoField(objectField.getName()));
	}

	private ServiceContext _updateServiceContext(
			long classPK,
			LayoutDisplayPageProvider<?> layoutDisplayPageProvider)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			serviceContext = ServiceContextTestUtil.getServiceContext();
		}

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				new InfoItemReference(
					layoutDisplayPageProvider.getClassName(), classPK)));

		serviceContext.setRequest(httpServletRequest);

		return serviceContext;
	}

	private InfoForm _childInfoForm;
	private ObjectDefinition _childObjectDefinition;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject(
		filter = "component.name=com.liferay.journal.web.internal.layout.display.page.JournalArticleLayoutDisplayPageProvider"
	)
	private LayoutDisplayPageProvider<JournalArticle>
		_journalArticleLayoutDisplayPageProvider;

	@Inject
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@DeleteAfterTestRun
	private ListTypeDefinition _listTypeDefinition;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@DeleteAfterTestRun
	private ListTypeEntry _listTypeEntry1;

	@DeleteAfterTestRun
	private ListTypeEntry _listTypeEntry2;

	@DeleteAfterTestRun
	private ListTypeEntry _listTypeEntry3;

	@Inject
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	private ObjectDefinition _objectDefinitionA;
	private ObjectDefinition _objectDefinitionAA;
	private ObjectDefinition _objectDefinitionAAA;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@DeleteAfterTestRun
	private ObjectRelationship _objectRelationship;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private ObjectStateFlowLocalService _objectStateFlowLocalService;

	@Inject
	private ObjectStateLocalService _objectStateLocalService;

	@Inject
	private ObjectStateTransitionLocalService
		_objectStateTransitionLocalService;

	private InfoForm _parentInfoForm;
	private ObjectDefinition _parentObjectDefinition;
	private Tree _tree;

}