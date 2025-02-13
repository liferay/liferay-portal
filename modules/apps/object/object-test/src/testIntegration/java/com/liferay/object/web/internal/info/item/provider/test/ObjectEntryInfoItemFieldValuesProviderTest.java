/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.type.KeyLocalizedLabelPair;
import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.builder.PicklistObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
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
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

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
					ListTypeEntryUtil.createListTypeEntry(_listTypeEntryKey)));

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
			).build(),
			new PicklistObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
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
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "test.txt",
			ContentTypes.TEXT, RandomTestUtil.randomBytes(), null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		String parentTextObjectFieldNameValue = RandomTestUtil.randomString();

		ObjectEntry parentObjectEntry = _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), _group.getGroupId(),
			_parentObjectDefinition.getObjectDefinitionId(), null,
			HashMapBuilder.<String, Serializable>put(
				"parentTextObjectFieldName", parentTextObjectFieldNameValue
			).build(),
			ServiceContextTestUtil.getServiceContext());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), _group.getGroupId(),
			_childObjectDefinition.getObjectDefinitionId(), null,
			HashMapBuilder.<String, Serializable>put(
				"r_oneToManyRelationshipName_" +
					_parentObjectDefinition.getPKObjectFieldName(),
				parentObjectEntry.getObjectEntryId()
			).put(
				"attachmentObjectFieldName", fileEntry.getFileEntryId()
			).put(
				"picklistObjectFieldName", _listTypeEntryKey
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
			fileEntry, objectAction, objectEntry,
			parentTextObjectFieldNameValue, null);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setTimeZone(TimeZoneUtil.getDefault());
		themeDisplay.setUser(TestPropsValues.getUser());

		_testObjectEntryInfoItemFieldValuesProvider(
			fileEntry, objectAction, objectEntry,
			parentTextObjectFieldNameValue, themeDisplay);
	}

	private ObjectDefinition _addObjectDefinition(ObjectField... objectFields)
		throws Exception {

		return _objectDefinitionLocalService.addCustomObjectDefinition(
			TestPropsValues.getUserId(), 0, null, false, false, true, true,
			false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			ObjectDefinitionTestUtil.getRandomName(), null, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			true, ObjectDefinitionConstants.SCOPE_SITE,
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
			Collections.emptyList(), Arrays.asList(objectFields));
	}

	private void _assertInfoFieldValue(
		String infoFieldName, InfoItemFieldValues infoItemFieldValues,
		ObjectField objectField, Object expectedValue) {

		InfoFieldValue<Object> fileNameInfoFieldValue =
			infoItemFieldValues.getInfoFieldValue(
				objectField.getObjectFieldId() + infoFieldName);

		Assert.assertEquals(expectedValue, fileNameInfoFieldValue.getValue());
	}

	private ObjectFieldSetting _createObjectFieldSetting(
		String name, String value) {

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.createObjectFieldSetting(0L);

		objectFieldSetting.setName(name);
		objectFieldSetting.setValue(value);

		return objectFieldSetting;
	}

	private void _testObjectEntryInfoItemFieldValuesProvider(
			FileEntry fileEntry, ObjectAction objectAction,
			ObjectEntry objectEntry, String parentTextObjectFieldNameValue,
			ThemeDisplay themeDisplay)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		serviceContext.setRequest(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

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

			Assert.assertEquals(
				HttpComponentsUtil.removeParameter(
					_dlURLHelper.getDownloadURL(
						fileEntry, fileEntry.getFileVersion(), null,
						StringPool.BLANK),
					"t"),
				HttpComponentsUtil.removeParameter(
					String.valueOf(downloadURLInfoFieldValue.getValue()), "t"));

			_assertInfoFieldValue(
				"#fileName", infoItemFieldValues, objectField,
				fileEntry.getFileName());
			_assertInfoFieldValue(
				"#mimeType", infoItemFieldValues, objectField,
				fileEntry.getMimeType());
			_assertInfoFieldValue(
				"#size", infoItemFieldValues, objectField, fileEntry.getSize());

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