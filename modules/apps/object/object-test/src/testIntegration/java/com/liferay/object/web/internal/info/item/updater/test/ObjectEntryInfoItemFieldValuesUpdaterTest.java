/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.updater.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.RelatedInfoFieldValue;
import com.liferay.info.field.type.FileInfoFieldType;
import com.liferay.info.field.type.MultiselectInfoFieldType;
import com.liferay.info.field.type.RelationshipInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.updater.InfoItemFieldValuesUpdater;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.definition.setting.builder.ObjectDefinitionSettingBuilder;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.info.item.util.ObjectEntryInfoItemUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.object.tree.Node;
import com.liferay.object.tree.ObjectEntryTreeFactory;
import com.liferay.object.tree.Tree;
import com.liferay.object.web.internal.info.item.BaseObjectEntryInfoItemTestCase;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.InfoFormException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class ObjectEntryInfoItemFieldValuesUpdaterTest
	extends BaseObjectEntryInfoItemTestCase {

	@Test
	public void testUpdateFromInfoItemFieldValues() throws Exception {
		String name1 = RandomTestUtil.randomString();
		String name2 = RandomTestUtil.randomString();

		_updateFromInfoItemFieldValues(
			geInfoItemFieldValues(name1, name2), objectDefinition2,
			objectEntry2);

		assertObjectEntryValues(name1, name2);

		ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"baker"
			).objectDefinitionId(
				objectDefinition2.getObjectDefinitionId()
			).userId(
				TestPropsValues.getUserId()
			).build());

		String value = RandomTestUtil.randomString();

		objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntry2.getObjectEntryId(),
			objectEntry2.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"baker", value
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_updateFromInfoItemFieldValues(
			geInfoItemFieldValues(name1, name2), objectDefinition2,
			objectEntryLocalService.getObjectEntry(
				objectEntry2.getObjectEntryId()));

		Map<String, Serializable> values = objectEntryLocalService.getValues(
			objectEntry2.getObjectEntryId());

		Assert.assertEquals(values.toString(), value, values.get("baker"));
	}

	@Test
	public void testUpdateFromInfoItemFieldValuesWithAssetCategorization()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						"name"
					).build()),
				ObjectDefinitionConstants.SCOPE_SITE);

		long groupId = TestPropsValues.getGroupId();

		_assetVocabulary = _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), groupId, RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(groupId));

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), groupId, RandomTestUtil.randomString(),
			_assetVocabulary.getVocabularyId(),
			ServiceContextTestUtil.getServiceContext(groupId));

		String assetTagName = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		serviceContext.setAssetCategoryIds(
			new long[] {assetCategory.getCategoryId()});
		serviceContext.setAssetTagNames(new String[] {assetTagName});

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			groupId, objectDefinition.getObjectDefinitionId(), serviceContext,
			HashMapBuilder.<String, Serializable>put(
				"name", RandomTestUtil.randomString()
			).build());

		String name = RandomTestUtil.randomString();

		_updateFromInfoItemFieldValues(
			InfoItemFieldValues.builder(
			).infoFieldValue(
				new InfoFieldValue<>(
					InfoField.builder(
					).infoFieldType(
						TextInfoFieldType.INSTANCE
					).namespace(
						ObjectField.class.getSimpleName()
					).name(
						"name"
					).build(),
					name)
			).build(),
			objectDefinition, objectEntry);

		Assert.assertEquals(
			name,
			MapUtil.getString(
				objectEntryLocalService.getValues(
					objectEntry.getObjectEntryId()),
				"name"));

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			objectDefinition.getClassName(), objectEntry.getObjectEntryId());

		Assert.assertArrayEquals(
			new long[] {assetCategory.getCategoryId()},
			assetEntry.getCategoryIds());
		Assert.assertArrayEquals(
			new String[] {assetTagName}, assetEntry.getTagNames());

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testUpdateFromInfoItemFieldValuesWithAttachmentField()
		throws Exception {

		_testUpdateFromInfoItemFieldValuesWithAttachmentObjectField(
			false, null);
		_testUpdateFromInfoItemFieldValuesWithAttachmentObjectField(
			true, StringPool.FORWARD_SLASH);
		_testUpdateFromInfoItemFieldValuesWithAttachmentObjectField(
			true, "/new/folder");
	}

	@Test
	public void testUpdateFromInfoItemFieldValuesWithDeletedItemIdentifiers()
		throws Exception {

		// Object entries from the same tree

		Tree objectDefinitionTree = TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			true,
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA", "AB"}
			).put(
				"AA", new String[] {"AAA", "AAB"}
			).put(
				"AB", new String[0]
			).put(
				"AAA", new String[0]
			).put(
				"AAB", new String[0]
			).build());

		Node objectDefinitionRootNode = objectDefinitionTree.getRootNode();

		TreeTestUtil.createObjectEntryTree(
			"1", _objectDefinitionLocalService, objectEntryLocalService,
			_objectFieldLocalService, _objectRelationshipLocalService,
			objectDefinitionRootNode.getPrimaryKey());

		ObjectEntry rootObjectEntry1 = _getObjectEntry("A1", "C_A");

		_updateFromInfoItemFieldValues(
			InfoItemFieldValues.builder(
			).infoFieldValue(
				_getDeletedItemIdentifiersInfoFieldValue(
					_getObjectEntry("AA1", "C_AA"),
					_getObjectEntry("AB1", "C_AB"))
			).build(),
			rootObjectEntry1.getObjectDefinition(), rootObjectEntry1);

		ObjectEntryTreeFactory objectEntryTreeFactory =
			new ObjectEntryTreeFactory(
				objectEntryLocalService, _objectRelationshipLocalService);

		TreeTestUtil.assertObjectEntryTree(
			LinkedHashMapBuilder.put(
				"A1", new String[0]
			).build(),
			objectEntryTreeFactory.create(rootObjectEntry1.getObjectEntryId()),
			objectEntryLocalService);

		// Object entry from another tree

		TreeTestUtil.createObjectEntryTree(
			"2", _objectDefinitionLocalService, objectEntryLocalService,
			_objectFieldLocalService, _objectRelationshipLocalService,
			objectDefinitionRootNode.getPrimaryKey());
		TreeTestUtil.createObjectEntryTree(
			"3", _objectDefinitionLocalService, objectEntryLocalService,
			_objectFieldLocalService, _objectRelationshipLocalService,
			objectDefinitionRootNode.getPrimaryKey());

		ObjectEntry rootObjectEntry2 = _getObjectEntry("A2", "C_A");

		AssertUtils.assertFailure(
			InfoFormException.class, null,
			() -> _updateFromInfoItemFieldValues(
				InfoItemFieldValues.builder(
				).infoFieldValue(
					_getDeletedItemIdentifiersInfoFieldValue(
						_getObjectEntry("AA3", "C_AA"))
				).build(),
				rootObjectEntry2.getObjectDefinition(), rootObjectEntry2));

		ObjectEntry rootObjectEntry3 = _getObjectEntry("A3", "C_A");

		TreeTestUtil.assertObjectEntryTree(
			LinkedHashMapBuilder.put(
				"A3", new String[] {"AA3", "AB3"}
			).put(
				"AA3", new String[] {"AAA3", "AAB3"}
			).put(
				"AB3", new String[0]
			).put(
				"AAA3", new String[0]
			).put(
				"AAB3", new String[0]
			).build(),
			objectEntryTreeFactory.create(rootObjectEntry3.getObjectEntryId()),
			objectEntryLocalService);

		// Object entry from the same tree with an updated sibling

		TreeTestUtil.createObjectEntryTree(
			"4", _objectDefinitionLocalService, objectEntryLocalService,
			_objectFieldLocalService, _objectRelationshipLocalService,
			objectDefinitionRootNode.getPrimaryKey());

		ObjectDefinition objectDefinitionAA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AA");

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				RandomTestUtil.randomLocaleStringMap()
			).name(
				"a" + RandomTestUtil.randomString()
			).objectDefinitionId(
				objectDefinitionAA.getObjectDefinitionId()
			).userId(
				TestPropsValues.getUserId()
			).build());

		ObjectRelationship objectRelationship =
			TreeTestUtil.getEdgeObjectRelationship(
				objectDefinitionAA, _objectRelationshipLocalService,
				objectDefinitionTree);

		ObjectField relationshipObjectField =
			_objectFieldLocalService.getObjectField(
				objectRelationship.getObjectFieldId2());

		ObjectEntry rootObjectEntry4 = _getObjectEntry("A4", "C_A");

		ObjectEntry siblingObjectEntryAA4 = ObjectEntryTestUtil.addObjectEntry(
			0, objectDefinitionAA.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				relationshipObjectField.getName(),
				rootObjectEntry4.getObjectEntryId()
			).build());

		InfoField<TextInfoFieldType> infoField = InfoField.builder(
		).infoFieldType(
			TextInfoFieldType.INSTANCE
		).namespace(
			ObjectEntryInfoItemUtil.getInfoFieldNamespace(
				objectDefinitionAA, objectRelationship)
		).name(
			objectField.getName()
		).build();
		String value = RandomTestUtil.randomString();

		_updateFromInfoItemFieldValues(
			InfoItemFieldValues.builder(
			).infoFieldValue(
				_getDeletedItemIdentifiersInfoFieldValue(siblingObjectEntryAA4)
			).infoFieldValue(
				new InfoFieldValue<>(
					infoField,
					new RelatedInfoFieldValue<>(
						HashMapBuilder.
							<RelatedInfoFieldValue.
								RelatedInfoFieldValueIdentifier,
							 InfoFieldValue<Object>>put(
								new RelatedInfoFieldValue.
									RelatedInfoFieldValueIdentifier(
										"AA4", "A4"),
								new InfoFieldValue<>(infoField, value)
							).build()))
			).build(),
			rootObjectEntry4.getObjectDefinition(), rootObjectEntry4);

		TreeTestUtil.assertObjectEntryTree(
			LinkedHashMapBuilder.put(
				"A4", new String[] {"AA4", "AB4"}
			).put(
				"AA4", new String[] {"AAA4", "AAB4"}
			).put(
				"AB4", new String[0]
			).put(
				"AAA4", new String[0]
			).put(
				"AAB4", new String[0]
			).build(),
			objectEntryTreeFactory.create(rootObjectEntry4.getObjectEntryId()),
			objectEntryLocalService);

		ObjectEntry objectEntryAA4 = _getObjectEntry("AA4", "C_AA");

		Assert.assertEquals(
			value,
			MapUtil.getString(
				objectEntryAA4.getValues(), objectField.getName()));

		// Standalone object entry

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, objectDefinitionAA.getObjectDefinitionId(),
			Collections.emptyMap());

		_updateFromInfoItemFieldValues(
			InfoItemFieldValues.builder(
			).infoFieldValue(
				_getDeletedItemIdentifiersInfoFieldValue(objectEntry)
			).build(),
			rootObjectEntry4.getObjectDefinition(), rootObjectEntry4);

		Assert.assertNull(
			objectEntryLocalService.fetchObjectEntry(
				objectEntry.getObjectEntryId()));

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AB", "C_AAA", "C_AAB"},
			objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testUpdateFromInfoItemFieldValuesWithLocalizedObjectField()
		throws Exception {

		String enValue = RandomTestUtil.randomString();

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).localized(
						true
					).name(
						"name"
					).build()));

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"name_i18n",
				HashMapBuilder.put(
					"en_US", enValue
				).build()
			).build());

		String ptValue = RandomTestUtil.randomString();

		_updateFromInfoItemFieldValues(
			InfoItemFieldValues.builder(
			).infoFieldValue(
				new InfoFieldValue<>(
					InfoField.builder(
					).infoFieldType(
						TextInfoFieldType.INSTANCE
					).namespace(
						ObjectField.class.getSimpleName()
					).name(
						"name"
					).localizable(
						true
					).build(),
					InfoLocalizedValue.builder(
					).value(
						LocaleUtil.BRAZIL, ptValue
					).build())
			).build(),
			objectDefinition, objectEntry);

		Map<String, Serializable> values = objectEntryLocalService.getValues(
			objectEntry.getObjectEntryId());

		Map<String, Object> localizedValues = (Map<String, Object>)values.get(
			"name_i18n");

		Assert.assertEquals(enValue, localizedValues.get("en_US"));
		Assert.assertEquals(ptValue, localizedValues.get("pt_BR"));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testUpdateFromInfoItemFieldValuesWithNestedManyToManyObjectRelationship()
		throws Exception {

		_testUpdateFromInfoItemFieldValuesWithNestedManyToManyObjectRelationship(
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT);
		_testUpdateFromInfoItemFieldValuesWithNestedManyToManyObjectRelationship(
			TestObjectEntryManager.STORAGE_TYPE);
	}

	@Test
	public void testUpdateFromInfoItemFieldValuesWithNondefaultStorageType()
		throws Exception {

		TestObjectEntryManager testObjectEntryManager =
			new TestObjectEntryManager();

		ServiceRegistration<ObjectEntryManager> serviceRegistration = _register(
			testObjectEntryManager);

		ObjectDefinition objectDefinition = _publishObjectDefinition(
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					RandomTestUtil.randomLocaleStringMap()
				).name(
					"name"
				).build()),
			TestObjectEntryManager.STORAGE_TYPE);

		ObjectEntry objectEntry = objectEntryLocalService.createObjectEntry(0);

		objectEntry.setExternalReferenceCode(RandomTestUtil.randomString());
		objectEntry.setCompanyId(TestPropsValues.getCompanyId());
		objectEntry.setObjectDefinitionId(
			objectDefinition.getObjectDefinitionId());
		objectEntry.setValues(new HashMap<>());

		String value = RandomTestUtil.randomString();

		_updateFromInfoItemFieldValues(
			InfoItemFieldValues.builder(
			).infoFieldValue(
				new InfoFieldValue<>(
					InfoField.builder(
					).infoFieldType(
						TextInfoFieldType.INSTANCE
					).namespace(
						ObjectField.class.getSimpleName()
					).name(
						"name"
					).build(),
					value)
			).build(),
			objectDefinition, objectEntry);

		com.liferay.object.rest.dto.v1_0.ObjectEntry dtoObjectEntry =
			testObjectEntryManager.getDTOObjectEntry();

		Assert.assertEquals(
			objectEntry.getExternalReferenceCode(),
			dtoObjectEntry.getExternalReferenceCode());

		Map<String, Object> properties = dtoObjectEntry.getProperties();

		Assert.assertEquals(value, properties.get("name"));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		serviceRegistration.unregister();
	}

	@Test
	public void testUpdateFromInfoItemFieldValuesWithObjectRelationship()
		throws Exception {

		ObjectDefinition childObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						"text"
					).build()));
		String childTextValue = RandomTestUtil.randomString();
		String enValue = RandomTestUtil.randomString();
		String externalReferenceCode = StringUtil.randomId();
		ObjectDefinition parentObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).localized(
						true
					).name(
						"text"
					).build()));
		String ptValue = RandomTestUtil.randomString();

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				parentObjectDefinition.getObjectDefinitionId(),
				childObjectDefinition.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, null, true,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);
		ObjectEntry parentObjectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, parentObjectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"text_i18n",
				HashMapBuilder.put(
					"en_US", enValue
				).build()
			).build());

		String namespace = ObjectEntryInfoItemUtil.getInfoFieldNamespace(
			childObjectDefinition, objectRelationship);

		_updateFromInfoItemFieldValues(
			InfoItemFieldValues.builder(
			).infoFieldValue(
				new InfoFieldValue<>(
					InfoField.builder(
					).infoFieldType(
						TextInfoFieldType.INSTANCE
					).namespace(
						ObjectField.class.getSimpleName()
					).name(
						"text"
					).localizable(
						true
					).build(),
					InfoLocalizedValue.builder(
					).value(
						LocaleUtil.BRAZIL, ptValue
					).value(
						LocaleUtil.US, enValue
					).build())
			).infoFieldValue(
				new InfoFieldValue<>(
					InfoField.builder(
					).infoFieldType(
						TextInfoFieldType.INSTANCE
					).namespace(
						namespace
					).name(
						"text"
					).build(),
					new RelatedInfoFieldValue<>(
						HashMapBuilder.
							<RelatedInfoFieldValue.
								RelatedInfoFieldValueIdentifier,
							 InfoFieldValue<Object>>put(
								new RelatedInfoFieldValue.
									RelatedInfoFieldValueIdentifier(
										externalReferenceCode,
										parentObjectEntry.
											getExternalReferenceCode()),
								new InfoFieldValue<>(
									InfoField.builder(
									).infoFieldType(
										TextInfoFieldType.INSTANCE
									).namespace(
										namespace
									).name(
										"text"
									).build(),
									childTextValue)
							).build()))
			).build(),
			parentObjectDefinition, parentObjectEntry);

		Map<String, Serializable> values = objectEntryLocalService.getValues(
			parentObjectEntry.getObjectEntryId());

		Map<String, Object> localizedValues = (Map<String, Object>)values.get(
			"text_i18n");

		Assert.assertEquals(enValue, localizedValues.get("en_US"));
		Assert.assertEquals(ptValue, localizedValues.get("pt_BR"));

		List<ObjectEntry> objectEntries =
			objectEntryLocalService.getObjectEntries(
				0, childObjectDefinition.getObjectDefinitionId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(objectEntries.toString(), 1, objectEntries.size());

		ObjectEntry childObjectEntry = objectEntries.get(0);

		Assert.assertEquals(
			childTextValue,
			MapUtil.getString(childObjectEntry.getValues(), "text"));

		objectRelationship =
			_objectRelationshipLocalService.updateObjectRelationship(
				objectRelationship.getExternalReferenceCode(),
				objectRelationship.getObjectRelationshipId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, null, false,
				objectRelationship.getLabelMap(), null);

		_objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		_objectDefinitionLocalService.deleteObjectDefinition(
			childObjectDefinition);
		_objectDefinitionLocalService.deleteObjectDefinition(
			parentObjectDefinition);
	}

	private ObjectDefinition _addCMSObjectDefinition(
			long storageDepotGroupId, boolean showFilesInLibrary,
			String storageDLFolderPath)
		throws Exception {

		Group group = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);

		List<ObjectFieldSetting> objectFieldSettings = new ArrayList<>();

		objectFieldSettings.add(
			new ObjectFieldSettingBuilder(
			).name(
				ObjectFieldSettingConstants.NAME_ACCEPTED_FILE_EXTENSIONS
			).value(
				"txt"
			).build());
		objectFieldSettings.add(
			new ObjectFieldSettingBuilder(
			).name(
				ObjectFieldSettingConstants.NAME_FILE_SOURCE
			).value(
				ObjectFieldSettingConstants.
					VALUE_USER_COMPUTER_TO_CMS_BASIC_DOCUMENT
			).build());
		objectFieldSettings.add(
			new ObjectFieldSettingBuilder(
			).name(
				ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
			).value(
				"100"
			).build());
		objectFieldSettings.add(
			new ObjectFieldSettingBuilder(
			).name(
				ObjectFieldSettingConstants.NAME_SHOW_FILES_IN_LIBRARY
			).value(
				String.valueOf(showFilesInLibrary)
			).build());

		if (showFilesInLibrary) {
			objectFieldSettings.add(
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_STORAGE_DEPOT_GROUP
				).value(
					String.valueOf(storageDepotGroupId)
				).build());
			objectFieldSettings.add(
				new ObjectFieldSettingBuilder(
				).name(
					ObjectFieldSettingConstants.NAME_STORAGE_DL_FOLDER_PATH
				).value(
					storageDLFolderPath
				).build());
		}

		ObjectFolder objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
				group.getCompanyId());

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(),
				objectFolder.getObjectFolderId(), null, null, true, false, true,
				false, true, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_DEPOT,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.singletonList(
					new ObjectDefinitionSettingBuilder(
					).name(
						ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS
					).value(
						StringPool.TRUE
					).build()),
				Arrays.asList(
					new AttachmentObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"attachment"
					).objectFieldSettings(
						objectFieldSettings
					).build(),
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"title"
					).build()),
				Collections.emptyList(),
				ServiceContextTestUtil.getServiceContext());

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private FileEntry _addTempFileEntry(long groupId) throws Exception {
		return TempFileEntryUtil.addTempFileEntry(
			groupId, TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			TempFileEntryUtil.getTempFileName("document.txt"),
			FileUtil.createTempFile(DLTestUtil.randomTextFileBytes()),
			ContentTypes.TEXT_PLAIN);
	}

	private List<ObjectEntry> _getCMSBasicDocumentObjectEntries(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			String title)
		throws Exception {

		return ListUtil.filter(
			objectEntryLocalService.getObjectEntries(
				objectEntry.getGroupId(),
				objectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			curObjectEntry -> StringUtil.equals(
				title, MapUtil.getString(curObjectEntry.getValues(), "title")));
	}

	private InfoFieldValue<Object> _getDeletedItemIdentifiersInfoFieldValue(
		ObjectEntry... objectEntries) {

		return new InfoFieldValue<>(
			InfoField.builder(
			).infoFieldType(
				MultiselectInfoFieldType.INSTANCE
			).namespace(
				InfoItemFieldValuesUpdater.class.getSimpleName()
			).name(
				"deletedItemIdentifiers"
			).build(),
			TransformUtil.transform(
				objectEntries,
				objectEntry -> {
					ObjectDefinition objectDefinition =
						objectEntry.getObjectDefinition();

					return StringBundler.concat(
						objectDefinition.getClassName(), StringPool.UNDERLINE,
						objectEntry.getExternalReferenceCode());
				},
				String.class));
	}

	private ObjectEntryFolder _getExpectedObjectEntryFolder(
			ObjectEntry objectEntry, String storageDLFolderPath)
		throws Exception {

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
					objectEntry.getGroupId(), objectEntry.getCompanyId());

		for (String name :
				StringUtil.split(storageDLFolderPath, CharPool.FORWARD_SLASH)) {

			if (Validator.isNull(name)) {
				continue;
			}

			objectEntryFolder =
				_objectEntryFolderLocalService.fetchObjectEntryFolder(
					objectEntry.getGroupId(), objectEntry.getCompanyId(),
					objectEntryFolder.getObjectEntryFolderId(), name);

			Assert.assertNotNull(name, objectEntryFolder);
		}

		return objectEntryFolder;
	}

	private ObjectEntry _getObjectEntry(
			String externalReferenceCode, String objectDefinitionName)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), objectDefinitionName);

		return objectEntryLocalService.getObjectEntry(
			externalReferenceCode, 0, objectDefinition.getObjectDefinitionId());
	}

	private int _getObjectEntryFoldersCount(
		ObjectEntryFolder objectEntryFolder) {

		return _objectEntryFolderLocalService.getObjectEntryFoldersCount(
			objectEntryFolder.getGroupId(), objectEntryFolder.getCompanyId(),
			objectEntryFolder.getObjectEntryFolderId());
	}

	private ObjectDefinition _publishObjectDefinition(
			List<ObjectField> objectFields, String storageType)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, null, false, false,
				true, false, true, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY, storageType,
				Collections.emptyList(), objectFields, Collections.emptyList(),
				ServiceContextTestUtil.getServiceContext());

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private ServiceRegistration<ObjectEntryManager> _register(
		ObjectEntryManager objectEntryManager) {

		Bundle bundle = FrameworkUtil.getBundle(
			ObjectEntryInfoItemFieldValuesUpdaterTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.registerService(
			ObjectEntryManager.class, objectEntryManager, null);
	}

	private void _testUpdateFromInfoItemFieldValuesWithAttachmentObjectField(
			boolean showFilesInLibrary, String storageDLFolderPath)
		throws Exception {

		Group group = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		ObjectDefinition objectDefinition = _addCMSObjectDefinition(
			depotEntry.getGroupId(), showFilesInLibrary, storageDLFolderPath);

		ObjectDefinition cmsBasicDocumentObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_DOCUMENT", group.getCompanyId());

		FileEntry tempFileEntry = _addTempFileEntry(depotEntry.getGroupId());

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			depotEntry.getGroupId(), objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			HashMapBuilder.<String, Serializable>put(
				"title", RandomTestUtil.randomString()
			).build());

		int objectEntriesCount = objectEntryLocalService.getObjectEntriesCount(
			cmsBasicDocumentObjectDefinition.getObjectDefinitionId());

		_updateFromInfoItemFieldValues(
			InfoItemFieldValues.builder(
			).infoFieldValue(
				new InfoFieldValue<>(
					InfoField.builder(
					).infoFieldType(
						FileInfoFieldType.INSTANCE
					).namespace(
						ObjectField.class.getSimpleName()
					).name(
						"attachment"
					).build(),
					tempFileEntry.getFileEntryId())
			).build(),
			objectDefinition, objectEntry);

		objectEntry = objectEntryLocalService.getObjectEntry(
			objectEntry.getObjectEntryId());

		if (showFilesInLibrary) {
			long expectedObjectEntryFolderId = 0;

			String expectedTitle = TempFileEntryUtil.getOriginalTempFileName(
				tempFileEntry.getFileName());

			List<ObjectEntry> cmsBasicDocumentObjectEntries =
				_getCMSBasicDocumentObjectEntries(
					cmsBasicDocumentObjectDefinition, objectEntry,
					expectedTitle);

			long fileEntryId = MapUtil.getLong(
				objectEntry.getValues(), "attachment");

			Assert.assertNotEquals(tempFileEntry.getFileEntryId(), fileEntryId);

			Assert.assertEquals(
				objectEntriesCount + 1,
				objectEntryLocalService.getObjectEntriesCount(
					cmsBasicDocumentObjectDefinition.getObjectDefinitionId()));

			Assert.assertEquals(
				cmsBasicDocumentObjectEntries.toString(), 1,
				cmsBasicDocumentObjectEntries.size());

			ObjectEntry cmsBasicDocumentObjectEntry =
				cmsBasicDocumentObjectEntries.get(0);

			Assert.assertEquals(
				cmsBasicDocumentObjectDefinition.getObjectDefinitionId(),
				cmsBasicDocumentObjectEntry.getObjectDefinitionId());
			Assert.assertEquals(
				expectedTitle,
				MapUtil.getString(
					cmsBasicDocumentObjectEntry.getValues(), "title"));

			if (Validator.isNotNull(storageDLFolderPath)) {
				ObjectEntryFolder filesObjectEntryFolder =
					_objectEntryFolderLocalService.
						getObjectEntryFolderByExternalReferenceCode(
							ObjectEntryFolderConstants.
								EXTERNAL_REFERENCE_CODE_FILES,
							objectEntry.getGroupId(),
							objectEntry.getCompanyId());

				ObjectEntryFolder expectedObjectEntryFolder =
					_getExpectedObjectEntryFolder(
						objectEntry, storageDLFolderPath);

				expectedObjectEntryFolderId =
					expectedObjectEntryFolder.getObjectEntryFolderId();

				Assert.assertEquals(
					expectedObjectEntryFolderId,
					cmsBasicDocumentObjectEntry.getObjectEntryFolderId());

				if (StringUtil.equals(
						storageDLFolderPath, StringPool.FORWARD_SLASH)) {

					int objectEntryFoldersCount = _getObjectEntryFoldersCount(
						filesObjectEntryFolder);

					Assert.assertEquals(
						expectedObjectEntryFolderId,
						filesObjectEntryFolder.getObjectEntryFolderId());
					Assert.assertEquals(0, objectEntryFoldersCount);
				}
			}

			_updateFromInfoItemFieldValues(
				InfoItemFieldValues.builder(
				).infoFieldValue(
					new InfoFieldValue<>(
						InfoField.builder(
						).infoFieldType(
							FileInfoFieldType.INSTANCE
						).namespace(
							ObjectField.class.getSimpleName()
						).name(
							"attachment"
						).build(),
						fileEntryId)
				).build(),
				objectDefinition, objectEntry);

			Assert.assertEquals(
				objectEntriesCount + 1,
				objectEntryLocalService.getObjectEntriesCount(
					cmsBasicDocumentObjectDefinition.getObjectDefinitionId()));

			if (Validator.isNotNull(storageDLFolderPath)) {
				ObjectEntryFolder expectedObjectEntryFolder =
					_getExpectedObjectEntryFolder(
						objectEntry, storageDLFolderPath);

				Assert.assertEquals(
					expectedObjectEntryFolderId,
					expectedObjectEntryFolder.getObjectEntryFolderId());

				if (StringUtil.equals(
						storageDLFolderPath, StringPool.FORWARD_SLASH)) {

					ObjectEntryFolder filesObjectEntryFolder =
						_objectEntryFolderLocalService.
							getObjectEntryFolderByExternalReferenceCode(
								ObjectEntryFolderConstants.
									EXTERNAL_REFERENCE_CODE_FILES,
								objectEntry.getGroupId(),
								objectEntry.getCompanyId());

					Assert.assertEquals(
						expectedObjectEntryFolderId,
						filesObjectEntryFolder.getObjectEntryFolderId());
					Assert.assertEquals(
						0, _getObjectEntryFoldersCount(filesObjectEntryFolder));
				}
			}
		}
		else {
			Assert.assertEquals(
				objectEntriesCount,
				objectEntryLocalService.getObjectEntriesCount(
					cmsBasicDocumentObjectDefinition.getObjectDefinitionId()));
		}

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	private void
			_testUpdateFromInfoItemFieldValuesWithNestedManyToManyObjectRelationship(
				String storageType)
		throws Exception {

		TestObjectEntryManager testObjectEntryManager =
			new TestObjectEntryManager();

		ServiceRegistration<ObjectEntryManager> serviceRegistration = _register(
			testObjectEntryManager);

		ObjectDefinition childObjectDefinition = _publishObjectDefinition(
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					RandomTestUtil.randomLocaleStringMap()
				).name(
					"text"
				).build()),
			storageType);
		ObjectDefinition parentObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						"text"
					).build()));

		String namespace = ObjectEntryInfoItemUtil.getInfoFieldNamespace(
			childObjectDefinition,
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, parentObjectDefinition,
				childObjectDefinition,
				ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
				StringUtil.randomId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY));

		ObjectDefinition relatedObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						"text"
					).build()));

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, childObjectDefinition,
				relatedObjectDefinition,
				ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
				StringUtil.randomId(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		InfoField<RelationshipInfoFieldType> relationshipInfoField =
			InfoField.builder(
			).infoFieldType(
				RelationshipInfoFieldType.INSTANCE
			).namespace(
				namespace
			).name(
				ObjectRelationshipConstants.
					OBJECT_RELATIONSHIP_FIELD_NAME_PREFIX +
						objectRelationship.getName()
			).attribute(
				RelationshipInfoFieldType.MULTIPLE, true
			).build();

		ObjectEntry parentObjectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, parentObjectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"text", RandomTestUtil.randomString()
			).build());

		RelatedInfoFieldValue.RelatedInfoFieldValueIdentifier
			relatedInfoFieldValueIdentifier =
				new RelatedInfoFieldValue.RelatedInfoFieldValueIdentifier(
					StringUtil.randomId(),
					parentObjectEntry.getExternalReferenceCode());

		ObjectEntry relatedObjectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, relatedObjectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"text", RandomTestUtil.randomString()
			).build());

		InfoField<TextInfoFieldType> textInfoField = InfoField.builder(
		).infoFieldType(
			TextInfoFieldType.INSTANCE
		).namespace(
			namespace
		).name(
			"text"
		).build();

		_updateFromInfoItemFieldValues(
			InfoItemFieldValues.builder(
			).infoFieldValue(
				new InfoFieldValue<>(
					InfoField.builder(
					).infoFieldType(
						TextInfoFieldType.INSTANCE
					).namespace(
						ObjectField.class.getSimpleName()
					).name(
						"text"
					).build(),
					RandomTestUtil.randomString())
			).infoFieldValue(
				new InfoFieldValue<>(
					relationshipInfoField,
					new RelatedInfoFieldValue<>(
						HashMapBuilder.
							<RelatedInfoFieldValue.
								RelatedInfoFieldValueIdentifier,
							 InfoFieldValue<Object>>put(
								relatedInfoFieldValueIdentifier,
								new InfoFieldValue<>(
									relationshipInfoField,
									Collections.singletonList(
										String.valueOf(
											relatedObjectEntry.
												getObjectEntryId())))
							).build()))
			).infoFieldValue(
				new InfoFieldValue<>(
					textInfoField,
					new RelatedInfoFieldValue<>(
						HashMapBuilder.
							<RelatedInfoFieldValue.
								RelatedInfoFieldValueIdentifier,
							 InfoFieldValue<Object>>put(
								relatedInfoFieldValueIdentifier,
								new InfoFieldValue<>(
									textInfoField,
									RandomTestUtil.randomString())
							).build()))
			).build(),
			parentObjectDefinition, parentObjectEntry);

		if (childObjectDefinition.isDefaultStorageType()) {
			ObjectEntry childObjectEntry = _getObjectEntry(
				relatedInfoFieldValueIdentifier.getExternalReferenceCode(),
				childObjectDefinition.getName());

			List<ObjectEntry> objectEntries =
				objectEntryLocalService.getManyToManyObjectEntries(
					childObjectEntry.getGroupId(),
					objectRelationship.getObjectRelationshipId(),
					childObjectEntry.getObjectEntryId(), true, false, null,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			Assert.assertEquals(
				objectEntries.toString(), 1, objectEntries.size());

			ObjectEntry objectEntry = objectEntries.get(0);

			Assert.assertEquals(
				relatedObjectEntry.getObjectEntryId(),
				objectEntry.getObjectEntryId());
		}
		else {
			com.liferay.object.rest.dto.v1_0.ObjectEntry dtoObjectEntry =
				testObjectEntryManager.getDTOObjectEntry();

			Assert.assertEquals(
				relatedInfoFieldValueIdentifier.getExternalReferenceCode(),
				dtoObjectEntry.getExternalReferenceCode());
		}

		_objectDefinitionLocalService.deleteObjectDefinition(
			childObjectDefinition);
		_objectDefinitionLocalService.deleteObjectDefinition(
			parentObjectDefinition);
		_objectDefinitionLocalService.deleteObjectDefinition(
			relatedObjectDefinition);

		serviceRegistration.unregister();
	}

	private void _updateFromInfoItemFieldValues(
			InfoItemFieldValues infoItemFieldValues,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws Exception {

		InfoItemFieldValuesUpdater<ObjectEntry> infoItemFieldValuesUpdater =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesUpdater.class,
				objectDefinition.getClassName());

		infoItemFieldValuesUpdater.updateFromInfoItemFieldValues(
			objectEntry, infoItemFieldValues);
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@DeleteAfterTestRun
	private AssetVocabulary _assetVocabulary;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private static class TestObjectEntryManager implements ObjectEntryManager {

		public static final String STORAGE_TYPE = RandomTestUtil.randomString();

		@Override
		public com.liferay.object.rest.dto.v1_0.ObjectEntry addObjectEntry(
			DTOConverterContext dtoConverterContext,
			ObjectDefinition objectDefinition,
			com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry,
			String scopeKey) {

			return objectEntry;
		}

		@Override
		public void deleteObjectEntry(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey) {
		}

		public com.liferay.object.rest.dto.v1_0.ObjectEntry
			getDTOObjectEntry() {

			return _dtoObjectEntry;
		}

		@Override
		public Page<com.liferay.object.rest.dto.v1_0.ObjectEntry>
			getObjectEntries(
				long companyId, ObjectDefinition objectDefinition,
				String scopeKey, Aggregation aggregation,
				DTOConverterContext dtoConverterContext, String filterString,
				Pagination pagination, String search, Sort[] sorts) {

			return Page.of(Collections.emptyList());
		}

		@Override
		public com.liferay.object.rest.dto.v1_0.ObjectEntry getObjectEntry(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String scopeKey) {

			com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry =
				new com.liferay.object.rest.dto.v1_0.ObjectEntry();

			objectEntry.setExternalReferenceCode(externalReferenceCode);
			objectEntry.setProperties(new HashMap<>());

			return objectEntry;
		}

		@Override
		public String getStorageLabel(Locale locale) {
			return RandomTestUtil.randomString();
		}

		@Override
		public String getStorageType() {
			return STORAGE_TYPE;
		}

		@Override
		public com.liferay.object.rest.dto.v1_0.ObjectEntry updateObjectEntry(
			long companyId, DTOConverterContext dtoConverterContext,
			String externalReferenceCode, ObjectDefinition objectDefinition,
			com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry,
			String scopeKey) {

			_dtoObjectEntry = objectEntry;

			return objectEntry;
		}

		private com.liferay.object.rest.dto.v1_0.ObjectEntry _dtoObjectEntry;

	}

}