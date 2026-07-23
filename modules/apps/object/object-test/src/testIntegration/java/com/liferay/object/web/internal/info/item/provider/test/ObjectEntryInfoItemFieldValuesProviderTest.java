/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.type.KeyLocalizedLabelPair;
import com.liferay.info.type.WebImage;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.builder.DateTimeObjectFieldBuilder;
import com.liferay.object.field.builder.PicklistObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class ObjectEntryInfoItemFieldValuesProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
	}

	@AfterClass
	public static void tearDownClass() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_listTypeEntryKey = RandomTestUtil.randomString();

		_listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false,
				Collections.singletonList(
					ListTypeEntryUtil.createListTypeEntry(_listTypeEntryKey)),
				new ServiceContext());

		_childObjectDefinition = _addObjectDefinition(
			new AttachmentObjectFieldBuilder(
			).labelMap(
				RandomTestUtil.randomLocaleStringMap()
			).name(
				"attachmentObjectFieldName"
			).objectFieldSettings(
				Arrays.asList(
					_createObjectFieldSetting("acceptedFileExtensions", "png"),
					_createObjectFieldSetting(
						"fileSource", "documentsAndMedia"),
					_createObjectFieldSetting("maximumFileSize", "100"))
			).build(),
			new DateTimeObjectFieldBuilder(
			).labelMap(
				RandomTestUtil.randomLocaleStringMap()
			).name(
				"dateTimeObjectFieldName"
			).objectFieldSettings(
				Collections.singletonList(
					_createObjectFieldSetting("timeStorage", "convertToUTC"))
			).build(),
			new PicklistObjectFieldBuilder(
			).labelMap(
				RandomTestUtil.randomLocaleStringMap()
			).listTypeDefinitionId(
				_listTypeDefinition.getListTypeDefinitionId()
			).name(
				"picklistObjectFieldName"
			).objectFieldSettings(
				Collections.emptyList()
			).state(
				false
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
	public void testObjectEntryInfoItemFieldValuesProvider() throws Exception {
		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "test.png",
			ContentTypes.IMAGE_PNG, DLTestUtil.getImageBytes("png"), null, null,
			null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		LocalDateTime localDateTime = LocalDateTime.of(2026, 1, 1, 23, 30);

		String parentTextObjectFieldNameValue = RandomTestUtil.randomString();

		ObjectEntry parentObjectEntry = _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			_parentObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"parentTextObjectFieldName", parentTextObjectFieldNameValue
			).build(),
			ServiceContextTestUtil.getServiceContext());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			_childObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"r_oneToManyRelationshipName_" +
					_parentObjectDefinition.getPKObjectFieldName(),
				parentObjectEntry.getObjectEntryId()
			).put(
				"attachmentObjectFieldName", fileEntry.getFileEntryId()
			).put(
				"dateTimeObjectFieldName",
				Date.from(localDateTime.toInstant(ZoneOffset.UTC))
			).put(
				"expirationDate",
				new Date(System.currentTimeMillis() + Time.DAY)
			).put(
				"picklistObjectFieldName", _listTypeEntryKey
			).put(
				"reviewDate", new Date(System.currentTimeMillis() + Time.DAY)
			).build(),
			ServiceContextTestUtil.getServiceContext());

		ObjectAction objectAction = _objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			objectEntry.getObjectDefinitionId(), true, StringPool.BLANK,
			RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_STANDALONE,
			UnicodePropertiesBuilder.put(
				"url", RandomTestUtil.randomString()
			).build(),
			false);

		_testObjectEntryInfoItemFieldValuesProvider(
			fileEntry, localDateTime, objectAction, objectEntry,
			parentTextObjectFieldNameValue, null);

		TimeZone timeZone = TimeZoneUtil.getTimeZone("Asia/Kolkata");

		_testObjectEntryInfoItemFieldValuesProvider(
			fileEntry,
			localDateTime.atZone(
				ZoneOffset.UTC
			).withZoneSameInstant(
				timeZone.toZoneId()
			).toLocalDateTime(),
			objectAction, objectEntry, parentTextObjectFieldNameValue,
			_getThemeDisplay(StringPool.BLANK, "Asia/Kolkata"));

		_testObjectEntryInfoItemFieldValuesProvider(
			fileEntry, localDateTime, objectAction, objectEntry,
			parentTextObjectFieldNameValue,
			_getThemeDisplay(RandomTestUtil.randomString(), "UTC"));
	}

	@Test
	public void testObjectEntryInfoItemFieldValuesProviderWithAttachmentObjectField()
		throws Exception {

		_objectDefinition = _addObjectDefinition(
			new AttachmentObjectFieldBuilder(
			).labelMap(
				RandomTestUtil.randomLocaleStringMap()
			).localized(
				true
			).name(
				"localizedAttachmentObjectFieldName"
			).objectFieldSettings(
				Arrays.asList(
					_createObjectFieldSetting("acceptedFileExtensions", "png"),
					_createObjectFieldSetting(
						"fileSource", "documentsAndMedia"),
					_createObjectFieldSetting("maximumFileSize", "100"))
			).build());

		_objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				_objectDefinition.getObjectDefinitionId());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "test.png",
			ContentTypes.IMAGE_PNG, DLTestUtil.getImageBytes("png"), null, null,
			null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			_objectDefinition.getObjectDefinitionId(),
			"localizedAttachmentObjectFieldName");

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				objectField.getI18nObjectFieldName(),
				HashMapBuilder.<String, Object>put(
					"en_US", fileEntry.getFileEntryId()
				).put(
					"es_ES", () -> null
				).build()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_pushServiceContext(_getThemeDisplay(StringPool.BLANK, "UTC"));

		try {
			InfoItemFieldValuesProvider<ObjectEntry>
				infoItemFieldValuesProvider =
					_infoItemServiceRegistry.getFirstInfoItemService(
						InfoItemFieldValuesProvider.class,
						_objectDefinition.getClassName());

			InfoItemFieldValues infoItemFieldValues =
				infoItemFieldValuesProvider.getInfoItemFieldValues(objectEntry);

			Assert.assertNotNull(infoItemFieldValues);

			InfoFieldValue<Object> downloadURLInfoFieldValue =
				infoItemFieldValues.getInfoFieldValue(
					objectField.getObjectFieldId() + "#downloadURL");

			Assert.assertNotNull(
				downloadURLInfoFieldValue.getValue(LocaleUtil.US));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testObjectEntryInfoItemFieldValuesProviderWithObjectEntryVersioning()
		throws Exception {

		ObjectField objectField = new TextObjectFieldBuilder(
		).labelMap(
			RandomTestUtil.randomLocaleStringMap()
		).name(
			"a" + RandomTestUtil.randomString()
		).build();

		_objectDefinition = _addObjectDefinition(true, objectField);

		_objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				_objectDefinition.getObjectDefinitionId());

		_pushServiceContext(_getThemeDisplay(StringPool.BLANK, "UTC"));

		try {
			String approvedValue = RandomTestUtil.randomString();

			ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
				_group.getGroupId(), TestPropsValues.getUserId(),
				_objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					objectField.getName(), approvedValue
				).build(),
				ServiceContextTestUtil.getServiceContext());

			_objectEntryLocalService.partialUpdateObjectEntry(
				TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
				objectEntry.getObjectEntryFolderId(),
				HashMapBuilder.<String, Serializable>put(
					objectField.getName(), RandomTestUtil.randomString()
				).put(
					"status", WorkflowConstants.STATUS_PENDING
				).build(),
				ServiceContextTestUtil.getServiceContext());

			InfoItemFieldValuesProvider<ObjectEntry>
				infoItemFieldValuesProvider =
					_infoItemServiceRegistry.getFirstInfoItemService(
						InfoItemFieldValuesProvider.class,
						_objectDefinition.getClassName());

			InfoItemFieldValues infoItemFieldValues =
				infoItemFieldValuesProvider.getInfoItemFieldValues(objectEntry);

			Assert.assertNotNull(infoItemFieldValues);

			InfoFieldValue<Object> infoFieldValue =
				infoItemFieldValues.getInfoFieldValue(objectField.getName());

			Assert.assertEquals(approvedValue, infoFieldValue.getValue());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testObjectEntryInfoItemFieldValuesProviderWithObjectRelationship()
		throws Exception {

		ObjectDefinition parentObjectDefinition = _addObjectDefinition(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"parentTitle"
			).build());

		parentObjectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				parentObjectDefinition.getObjectDefinitionId());

		ObjectDefinition childObjectDefinition = _addObjectDefinition(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"childTitle"
			).build());

		childObjectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				childObjectDefinition.getObjectDefinitionId());

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				parentObjectDefinition.getObjectDefinitionId(),
				childObjectDefinition.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, true,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"oneToManyRelationshipName", false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		ObjectEntry parentObjectEntry = _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			parentObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"parentTitle", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		String childTitleValue = RandomTestUtil.randomString();

		ObjectEntry childObjectEntry = _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			childObjectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"childTitle", childTitleValue
			).put(
				"r_oneToManyRelationshipName_" +
					parentObjectDefinition.getPKObjectFieldName(),
				parentObjectEntry.getObjectEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_pushServiceContext(_getThemeDisplay(StringPool.BLANK, "UTC"));

		InfoItemFieldValuesProvider<ObjectEntry> infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class,
				childObjectDefinition.getClassName());

		InfoItemFieldValues infoItemFieldValues =
			infoItemFieldValuesProvider.getInfoItemFieldValues(
				childObjectEntry);

		InfoFieldValue<Object> infoFieldValue =
			infoItemFieldValues.getInfoFieldValue("childTitle");

		Assert.assertEquals(childTitleValue, infoFieldValue.getValue());

		ServiceContextThreadLocal.popServiceContext();

		objectRelationship =
			_objectRelationshipLocalService.updateObjectRelationship(
				objectRelationship.getExternalReferenceCode(),
				objectRelationship.getObjectRelationshipId(),
				objectRelationship.getParameterObjectFieldId(),
				objectRelationship.getDeletionType(), false,
				objectRelationship.getLabelMap(), null);

		_objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		_objectDefinitionLocalService.deleteObjectDefinition(
			childObjectDefinition);
		_objectDefinitionLocalService.deleteObjectDefinition(
			parentObjectDefinition);
	}

	private ObjectDefinition _addObjectDefinition(
			boolean enableObjectEntryVersioning, ObjectField... objectFields)
		throws Exception {

		return _objectDefinitionLocalService.addCustomObjectDefinition(
			null, TestPropsValues.getUserId(), 0, null, true, false, true,
			false, true, false, false, false, enableObjectEntryVersioning, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			ObjectDefinitionTestUtil.getRandomName(), null, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			true, ObjectDefinitionConstants.SCOPE_SITE,
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
			Collections.emptyList(), Arrays.asList(objectFields),
			Collections.emptyList(), new ServiceContext());
	}

	private ObjectDefinition _addObjectDefinition(ObjectField... objectFields)
		throws Exception {

		return _addObjectDefinition(false, objectFields);
	}

	private void _assertInfoFieldValue(
		String infoFieldName, InfoItemFieldValues infoItemFieldValues,
		ObjectField objectField, Object expectedValue) {

		InfoFieldValue<Object> fileNameInfoFieldValue =
			infoItemFieldValues.getInfoFieldValue(
				objectField.getObjectFieldId() + infoFieldName);

		Assert.assertEquals(expectedValue, fileNameInfoFieldValue.getValue());
	}

	private void _assertWebImage(
			FileEntry fileEntry, InfoFieldValue<?> infoFieldValue,
			ThemeDisplay themeDisplay)
		throws Exception {

		Assert.assertTrue(infoFieldValue.getValue() instanceof WebImage);

		WebImage webImage = (WebImage)infoFieldValue.getValue();

		InfoItemReference infoItemReference = webImage.getInfoItemReference();

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			(ClassPKInfoItemIdentifier)
				infoItemReference.getInfoItemIdentifier();

		Assert.assertEquals(
			fileEntry.getFileEntryId(), classPKInfoItemIdentifier.getClassPK());

		Assert.assertEquals(
			HttpComponentsUtil.removeParameter(
				_dlURLHelper.getPreviewURL(
					fileEntry, fileEntry.getFileVersion(), themeDisplay,
					StringPool.BLANK),
				"t"),
			HttpComponentsUtil.removeParameter(webImage.getURL(), "t"));
	}

	private ObjectFieldSetting _createObjectFieldSetting(
		String name, String value) {

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.createObjectFieldSetting(0L);

		objectFieldSetting.setName(name);
		objectFieldSetting.setValue(value);

		return objectFieldSetting;
	}

	private ThemeDisplay _getThemeDisplay(String doAsUserId, String timeZoneId)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setDoAsUserId(doAsUserId);

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());

		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setTimeZone(TimeZoneUtil.getTimeZone(timeZoneId));

		User user = UserTestUtil.addUser();

		user.setTimeZoneId(timeZoneId);

		themeDisplay.setUser(user);

		return themeDisplay;
	}

	private void _pushServiceContext(ThemeDisplay themeDisplay)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		serviceContext.setRequest(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);
	}

	private void _testObjectEntryInfoItemFieldValuesProvider(
			FileEntry fileEntry, LocalDateTime localDateTime,
			ObjectAction objectAction, ObjectEntry objectEntry,
			String parentTextObjectFieldNameValue, ThemeDisplay themeDisplay)
		throws Exception {

		_pushServiceContext(themeDisplay);

		try {
			InfoItemFieldValuesProvider<ObjectEntry>
				infoItemFieldValuesProvider =
					_infoItemServiceRegistry.getFirstInfoItemService(
						InfoItemFieldValuesProvider.class,
						_childObjectDefinition.getClassName());

			InfoItemFieldValues infoItemFieldValues =
				infoItemFieldValuesProvider.getInfoItemFieldValues(objectEntry);

			Assert.assertNotNull(infoItemFieldValues);

			InfoFieldValue<Object> infoLocalizedValue =
				infoItemFieldValues.getInfoFieldValue(objectAction.getName());

			Map<Locale, String> labelMap = objectAction.getLabelMap();

			for (Map.Entry<Locale, String> entry : labelMap.entrySet()) {
				Assert.assertEquals(
					entry.getValue(),
					infoLocalizedValue.getValue(entry.getKey()));
			}

			ObjectField objectField = _objectFieldLocalService.fetchObjectField(
				_childObjectDefinition.getObjectDefinitionId(),
				"attachmentObjectFieldName");

			InfoFieldValue<Object> downloadURLInfoFieldValue =
				infoItemFieldValues.getInfoFieldValue(
					objectField.getObjectFieldId() + "#downloadURL");

			String downloadURL = String.valueOf(
				downloadURLInfoFieldValue.getValue());

			Assert.assertEquals(
				"true",
				HttpComponentsUtil.getParameter(
					downloadURL, "download", false));
			Assert.assertEquals(
				_childObjectDefinition.getExternalReferenceCode(),
				HttpComponentsUtil.getParameter(
					downloadURL, "objectDefinitionExternalReferenceCode",
					false));
			Assert.assertEquals(
				objectEntry.getExternalReferenceCode(),
				HttpComponentsUtil.getParameter(
					downloadURL, "objectEntryExternalReferenceCode", false));
			Assert.assertEquals(
				objectField.getExternalReferenceCode(),
				HttpComponentsUtil.getParameter(
					downloadURL, "objectFieldExternalReferenceCode", false));

			if (themeDisplay != null) {
				Assert.assertEquals(
					themeDisplay.getDoAsUserId(),
					HttpComponentsUtil.getParameter(
						downloadURL, "doAsUserId", false));
			}

			_assertWebImage(
				fileEntry,
				infoItemFieldValues.getInfoFieldValue(
					objectField.getObjectFieldId() + "#fileURL"),
				themeDisplay);
			_assertWebImage(
				fileEntry,
				infoItemFieldValues.getInfoFieldValue(
					objectField.getObjectFieldId() + "#previewURL"),
				themeDisplay);

			_assertInfoFieldValue(
				"#fileName", infoItemFieldValues, objectField,
				fileEntry.getFileName());
			_assertInfoFieldValue(
				"#mimeType", infoItemFieldValues, objectField,
				fileEntry.getMimeType());
			_assertInfoFieldValue(
				"#size", infoItemFieldValues, objectField, fileEntry.getSize());

			InfoFieldValue<Object> dateTimeInfoFieldValue =
				infoItemFieldValues.getInfoFieldValue(
					"dateTimeObjectFieldName");

			Assert.assertEquals(
				localDateTime, dateTimeInfoFieldValue.getValue());

			InfoFieldValue<Object> parentTextObjectFieldNameInfoFieldValue =
				infoItemFieldValues.getInfoFieldValue(
					"parentTextObjectFieldName");

			Assert.assertEquals(
				parentTextObjectFieldNameValue,
				parentTextObjectFieldNameInfoFieldValue.getValue());

			InfoFieldValue<Object> picklistObjectFieldNameInfoFieldValue =
				infoItemFieldValues.getInfoFieldValue(
					"picklistObjectFieldName");

			for (KeyLocalizedLabelPair keyLocalizedLabelPair :
					(ArrayList<KeyLocalizedLabelPair>)
						picklistObjectFieldNameInfoFieldValue.getValue()) {

				Assert.assertEquals(
					_listTypeEntryKey, keyLocalizedLabelPair.getKey());
			}
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private static PermissionChecker _originalPermissionChecker;

	@DeleteAfterTestRun
	private ObjectDefinition _childObjectDefinition;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLURLHelper _dlURLHelper;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@DeleteAfterTestRun
	private ListTypeDefinition _listTypeDefinition;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	private String _listTypeEntryKey;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _parentObjectDefinition;

}