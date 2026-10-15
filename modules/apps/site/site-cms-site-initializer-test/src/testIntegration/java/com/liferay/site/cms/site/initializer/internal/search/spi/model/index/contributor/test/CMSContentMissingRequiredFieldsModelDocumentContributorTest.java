/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.search.spi.model.index.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-82226"))
@RunWith(Arquillian.class)
public class CMSContentMissingRequiredFieldsModelDocumentContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_contentStructuresObjectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
				TestPropsValues.getCompanyId());

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		_group = _depotEntry.getGroup();

		_objectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
					_group.getGroupId(), TestPropsValues.getCompanyId());
	}

	@After
	public void tearDown() throws Exception {
		for (ObjectDefinition objectDefinition : _objectDefinitions) {
			TreeTestUtil.unbind(
				objectDefinition.getObjectDefinitionId(),
				_objectRelationshipLocalService);
		}

		for (ObjectDefinition objectDefinition : _objectDefinitions) {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition.getObjectDefinitionId());
		}

		_objectDefinitions.clear();

		_depotEntryLocalService.deleteDepotEntry(_depotEntry.getDepotEntryId());
	}

	@Test
	public void testContribute() throws Exception {
		_testContributeWhenObjectDefinitionIsNotCMS();
		_testContributeWhenRepeatableGroupRequiredObjectFieldIsAdded();
		_testContributeWhenRequiredAttachmentObjectFieldIsSet();
		_testContributeWhenRequiredObjectFieldsAreAddedAfterApproval();
	}

	private ObjectDefinition _addCMSObjectDefinition() throws Exception {
		return _addCMSObjectDefinition(
			_contentStructuresObjectFolder.getObjectFolderId());
	}

	private ObjectDefinition _addCMSObjectDefinition(long objectFolderId)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				true, false, true, ObjectDefinitionTestUtil.getRandomName(),
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"text"
					).build()),
				objectFolderId, ObjectDefinitionConstants.SCOPE_DEPOT,
				TestPropsValues.getUserId());

		_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS, "true");

		_objectDefinitions.add(objectDefinition);

		return objectDefinition;
	}

	private ObjectEntry _addObjectEntry(
			ObjectDefinition objectDefinition, Map<String, Serializable> values,
			int workflowAction)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			_objectEntryFolder.getObjectEntryFolderId(), null, values,
			_getServiceContext(workflowAction));
	}

	private void _addRequiredObjectFields(ObjectDefinition objectDefinition)
		throws Exception {

		_objectFieldLocalService.addCustomObjectField(
			null, TestPropsValues.getUserId(), 0,
			objectDefinition.getObjectDefinitionId(),
			ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
			ObjectFieldConstants.DB_TYPE_INTEGER, null, false, false, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			false, "integer", null, null, true, false, Collections.emptyList());
		_objectFieldLocalService.addCustomObjectField(
			null, TestPropsValues.getUserId(), 0,
			objectDefinition.getObjectDefinitionId(),
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, null, false, false, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			false, "requiredText", null, null, true, false,
			Collections.emptyList());
	}

	private void _assertMissingRequiredFieldsCount(
			Integer expectedMissingRequiredFieldsCount, ObjectEntry objectEntry)
		throws Exception {

		objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntry.getObjectEntryId());

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		objectEntry.setObjectDefinition(objectDefinition);

		Indexer<ObjectEntry> indexer = IndexerRegistryUtil.getIndexer(
			objectDefinition.getClassName());

		Document document = indexer.getDocument(objectEntry);

		if (expectedMissingRequiredFieldsCount == null) {
			Assert.assertNull(document.getField("missingRequiredFieldsCount"));

			return;
		}

		Assert.assertEquals(
			String.valueOf(expectedMissingRequiredFieldsCount),
			document.get("missingRequiredFieldsCount"));
	}

	private ServiceContext _getServiceContext(int workflowAction)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setWorkflowAction(workflowAction);

		return serviceContext;
	}

	private void _testContributeWhenObjectDefinitionIsNotCMS()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				true, false, true, ObjectDefinitionTestUtil.getRandomName(),
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"text"
					).build()),
				0, ObjectDefinitionConstants.SCOPE_COMPANY,
				TestPropsValues.getUserId());

		_objectDefinitions.add(objectDefinition);

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"text", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_addRequiredObjectFields(objectDefinition);

		_assertMissingRequiredFieldsCount(null, objectEntry);
	}

	private void _testContributeWhenRepeatableGroupRequiredObjectFieldIsAdded()
		throws Exception {

		ObjectDefinition objectDefinition = _addCMSObjectDefinition();

		ObjectFolder repeatableGroupsObjectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_STRUCTURE_REPEATABLE_GROUPS,
				TestPropsValues.getCompanyId());

		ObjectDefinition repeatableGroupObjectDefinition =
			_addCMSObjectDefinition(
				repeatableGroupsObjectFolder.getObjectFolderId());

		ObjectRelationship objectRelationship = TreeTestUtil.bind(
			objectDefinition.getObjectDefinitionId(),
			repeatableGroupObjectDefinition.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		ObjectEntry objectEntry = _addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"text", RandomTestUtil.randomString()
			).build(),
			WorkflowConstants.ACTION_PUBLISH);

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		for (int i = 0; i < 2; i++) {
			_addObjectEntry(
				repeatableGroupObjectDefinition,
				HashMapBuilder.<String, Serializable>put(
					objectField.getName(), objectEntry.getObjectEntryId()
				).put(
					"text", RandomTestUtil.randomString()
				).build(),
				WorkflowConstants.ACTION_PUBLISH);
		}

		_assertMissingRequiredFieldsCount(null, objectEntry);

		_addRequiredObjectFields(repeatableGroupObjectDefinition);

		_assertMissingRequiredFieldsCount(4, objectEntry);
	}

	private void _testContributeWhenRequiredAttachmentObjectFieldIsSet()
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_DOCUMENT", TestPropsValues.getCompanyId());

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, null,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			null, new ByteArrayInputStream(TestDataConstants.TEST_BYTE_ARRAY),
			TestDataConstants.TEST_BYTE_ARRAY.length, null, null, null,
			ServiceContextTestUtil.getServiceContext());

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
					_group.getGroupId(), TestPropsValues.getCompanyId());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			objectEntryFolder.getObjectEntryFolderId(), null,
			HashMapBuilder.<String, Serializable>put(
				"file", dlFileEntry.getFileEntryId()
			).put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build(),
			_getServiceContext(WorkflowConstants.ACTION_PUBLISH));

		_assertMissingRequiredFieldsCount(null, objectEntry);

		ObjectField objectField = _objectFieldLocalService.addCustomObjectField(
			null, TestPropsValues.getUserId(), 0,
			objectDefinition.getObjectDefinitionId(),
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, null, false, false, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			false, "requiredText", null, null, true, false,
			Collections.emptyList());

		try {
			_assertMissingRequiredFieldsCount(1, objectEntry);
		}
		finally {
			_objectFieldLocalService.deleteObjectField(
				objectField.getObjectFieldId());
		}
	}

	private void _testContributeWhenRequiredObjectFieldsAreAddedAfterApproval()
		throws Exception {

		ObjectDefinition objectDefinition = _addCMSObjectDefinition();

		ObjectEntry approvedObjectEntry = _addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"text", RandomTestUtil.randomString()
			).build(),
			WorkflowConstants.ACTION_PUBLISH);
		ObjectEntry draftObjectEntry = _addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"text", RandomTestUtil.randomString()
			).build(),
			WorkflowConstants.ACTION_SAVE_DRAFT);
		ObjectEntry draftOverApprovedObjectEntry = _addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"text", RandomTestUtil.randomString()
			).build(),
			WorkflowConstants.ACTION_PUBLISH);
		ObjectEntry expiredObjectEntry = _addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"text", RandomTestUtil.randomString()
			).build(),
			WorkflowConstants.ACTION_PUBLISH);

		_addRequiredObjectFields(objectDefinition);

		_assertMissingRequiredFieldsCount(2, approvedObjectEntry);
		_assertMissingRequiredFieldsCount(null, draftObjectEntry);

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(),
			draftOverApprovedObjectEntry.getObjectEntryId(),
			_objectEntryFolder.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"integer", RandomTestUtil.randomInt()
			).put(
				"requiredText", RandomTestUtil.randomString()
			).build(),
			_getServiceContext(WorkflowConstants.ACTION_SAVE_DRAFT));

		_assertMissingRequiredFieldsCount(2, draftOverApprovedObjectEntry);

		_objectEntryLocalService.expireObjectEntry(
			TestPropsValues.getUserId(), expiredObjectEntry.getObjectEntryId(),
			ServiceContextTestUtil.getServiceContext());

		_assertMissingRequiredFieldsCount(null, expiredObjectEntry);

		ObjectEntry newObjectEntry = _addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"integer", 0
			).put(
				"requiredText", RandomTestUtil.randomString()
			).build(),
			WorkflowConstants.ACTION_PUBLISH);

		_assertMissingRequiredFieldsCount(null, newObjectEntry);

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), approvedObjectEntry.getObjectEntryId(),
			_objectEntryFolder.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"integer", 0
			).put(
				"requiredText", RandomTestUtil.randomString()
			).put(
				"text", RandomTestUtil.randomString()
			).build(),
			_getServiceContext(WorkflowConstants.ACTION_PUBLISH));

		_assertMissingRequiredFieldsCount(null, approvedObjectEntry);
	}

	private ObjectFolder _contentStructuresObjectFolder;
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	private Group _group;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	private final List<ObjectDefinition> _objectDefinitions = new ArrayList<>();
	private ObjectEntryFolder _objectEntryFolder;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}