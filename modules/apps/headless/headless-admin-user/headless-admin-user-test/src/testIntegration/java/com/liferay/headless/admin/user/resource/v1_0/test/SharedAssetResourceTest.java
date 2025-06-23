/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.headless.admin.user.client.dto.v1_0.SharedAsset;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.SharingEntryLocalService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
public class SharedAssetResourceTest extends BaseSharedAssetResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_objectDefinition = _getObjectDefinition();

		_user = UserTestUtil.addUser();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_objectDefinitionLocalService.deleteObjectDefinition(_objectDefinition);

		_userLocalService.deleteUser(_user);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"actionIds", "assetType", "externalReferenceCode", "fileTypeIcon",
			"fileTypeIconColor", "id", "title"
		};
	}

	@Override
	protected SharedAsset
			testGetMyUserAccountSharedAssetsSharedByMePage_addSharedAsset(
				SharedAsset sharedAsset)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.addObjectEntryFolder(
				null, testGroup.getGroupId(), TestPropsValues.getUserId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.put(
					LocaleUtil.US, sharedAsset.getTitle()
				).build(),
				sharedAsset.getTitle(), new ServiceContext());

		return _toObjectEntryFolderSharedAsset(
			sharedAsset,
			_sharingEntryLocalService.addSharingEntry(
				sharedAsset.getExternalReferenceCode(),
				TestPropsValues.getUserId(), 0, _user.getUserId(),
				_classNameLocalService.getClassNameId(
					ObjectEntryFolder.class.getName()),
				objectEntryFolder.getObjectEntryFolderId(),
				testGroup.getGroupId(), true,
				Arrays.asList(SharingEntryAction.VIEW), null, serviceContext));
	}

	@Override
	protected SharedAsset
			testGetMyUserAccountSharedAssetsSharedWithMePage_addSharedAsset(
				SharedAsset sharedAsset)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), _user.getUserId());

		DLFileEntry dlFileEntry = _addDLFileEntry(
			testGroup.getGroupId(), TestPropsValues.getUserId());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			testGroup.getGroupId(), _user.getUserId(),
			_objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"file", dlFileEntry.getFileEntryId()
			).put(
				"title", sharedAsset.getTitle()
			).build(),
			serviceContext);

		return _toObjectEntrySharedAsset(
			objectEntry, sharedAsset,
			_sharingEntryLocalService.addSharingEntry(
				sharedAsset.getExternalReferenceCode(), _user.getUserId(), 0,
				TestPropsValues.getUserId(),
				_classNameLocalService.getClassNameId(
					_objectDefinition.getClassName()),
				objectEntry.getObjectEntryId(), testGroup.getGroupId(), true,
				Arrays.asList(SharingEntryAction.VIEW), null, serviceContext));
	}

	private static ObjectFieldSetting _createObjectFieldSetting(
		String name, String value) {

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.createObjectFieldSetting(0);

		objectFieldSetting.setName(name);
		objectFieldSetting.setValue(value);

		return objectFieldSetting;
	}

	private static ObjectDefinition _getObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				true, ObjectDefinitionTestUtil.getRandomName(),
				Arrays.asList(
					new AttachmentObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"file"
					).objectFieldSettings(
						Arrays.asList(
							_createObjectFieldSetting(
								"acceptedFileExtensions", "txt"),
							_createObjectFieldSetting(
								"fileSource", "documentsAndMedia"),
							_createObjectFieldSetting("maximumFileSize", "100"))
					).build(),
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).indexed(
						true
					).indexedAsKeyword(
						true
					).name(
						"title"
					).localized(
						false
					).build()),
				ObjectDefinitionConstants.SCOPE_SITE,
				TestPropsValues.getUserId());

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinition.getObjectDefinitionId(), "title");

		objectDefinition.setTitleObjectFieldId(objectField.getObjectFieldId());

		return _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);
	}

	private DLFileEntry _addDLFileEntry(long groupId, long userId)
		throws Exception {

		byte[] bytes = TestDataConstants.TEST_BYTE_ARRAY;

		InputStream inputStream = new ByteArrayInputStream(bytes);

		return _dlFileEntryLocalService.addFileEntry(
			null, userId, groupId, groupId,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt",
			MimeTypesUtil.getExtensionContentType("txt"),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			null, inputStream, bytes.length, null, null, null,
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private SharedAsset _toObjectEntryFolderSharedAsset(
			SharedAsset sharedAsset, SharingEntry sharingEntry)
		throws Exception {

		return new SharedAsset() {
			{
				actionIds = TransformUtil.transformToArray(
					SharingEntryAction.getSharingEntryActions(
						sharingEntry.getActionIds()),
					SharingEntryAction::getActionId, String.class);
				assetType = "Object Entry Folder";
				dateCreated = sharingEntry.getCreateDate();
				dateModified = sharingEntry.getModifiedDate();
				externalReferenceCode = sharedAsset.getExternalReferenceCode();
				fileTypeIcon = "folder";
				fileTypeIconColor = "folder";
				id = sharingEntry.getSharingEntryId();
				title = sharedAsset.getTitle();
			}
		};
	}

	private SharedAsset _toObjectEntrySharedAsset(
			ObjectEntry objectEntry, SharedAsset sharedAsset,
			SharingEntry sharingEntry)
		throws Exception {

		return new SharedAsset() {
			{
				actionIds = TransformUtil.transformToArray(
					SharingEntryAction.getSharingEntryActions(
						sharingEntry.getActionIds()),
					SharingEntryAction::getActionId, String.class);
				assetType = _objectDefinition.getLabel(LocaleUtil.US);
				dateCreated = sharingEntry.getCreateDate();
				dateModified = sharingEntry.getModifiedDate();
				externalReferenceCode = sharedAsset.getExternalReferenceCode();
				fileTypeIcon = "document-text";
				fileTypeIconColor = "file-icon-color-6";
				id = sharingEntry.getSharingEntryId();
				title = objectEntry.getTitleValue();
			}
		};
	}

	private static ObjectDefinition _objectDefinition;

	@Inject
	private static ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private static ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private static ObjectFieldSettingLocalService
		_objectFieldSettingLocalService;

	private static User _user;

	@Inject
	private static UserLocalService _userLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private SharingEntryLocalService _sharingEntryLocalService;

}