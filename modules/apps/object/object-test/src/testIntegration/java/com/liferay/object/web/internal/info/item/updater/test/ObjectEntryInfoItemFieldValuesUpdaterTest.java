/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.updater.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.RelatedInfoFieldValue;
import com.liferay.info.field.type.FileInfoFieldType;
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
import com.liferay.object.info.item.util.ObjectEntryInfoItemUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.web.internal.info.item.BaseObjectEntryInfoItemTestCase;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

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
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, true,
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
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
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
				objectFolder.getObjectFolderId(), null, true, false, true,
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

	private int _getObjectEntryFoldersCount(
		ObjectEntryFolder objectEntryFolder) {

		return _objectEntryFolderLocalService.getObjectEntryFoldersCount(
			objectEntryFolder.getGroupId(), objectEntryFolder.getCompanyId(),
			objectEntryFolder.getObjectEntryFolderId());
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
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}