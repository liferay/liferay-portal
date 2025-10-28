/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.system.info.item.provider.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.object.system.SystemObjectEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class SystemObjectEntryInfoItemFieldValuesProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(_getServiceContext());

		_objectDefinition = _objectDefinitionLocalService.fetchObjectDefinition(
			TestPropsValues.getCompanyId(), AccountEntry.class.getSimpleName());

		_objectField = ObjectFieldUtil.addCustomObjectField(
			new AttachmentObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				StringUtil.randomId()
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).objectFieldSettings(
				Arrays.asList(
					_createObjectFieldSetting(
						ObjectFieldSettingConstants.
							NAME_ACCEPTED_FILE_EXTENSIONS,
						"txt"),
					_createObjectFieldSetting(
						ObjectFieldSettingConstants.NAME_FILE_SOURCE,
						ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA),
					_createObjectFieldSetting(
						ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE, "100"))
			).userId(
				TestPropsValues.getUserId()
			).build());
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();

		_objectFieldLocalService.deleteObjectField(
			_objectField.getObjectFieldId());
	}

	@Test
	public void testSystemObjectEntryInfoItemFieldValuesProvider()
		throws Exception {

		SystemObjectDefinitionManager systemObjectDefinitionManager =
			_systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager(_objectDefinition.getName());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "test.txt",
			ContentTypes.TEXT, RandomTestUtil.randomBytes(), null, null, null,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));

		long accountEntryId = systemObjectDefinitionManager.addBaseModel(
			false, TestPropsValues.getUser(),
			HashMapBuilder.<String, Object>put(
				"description", RandomTestUtil.randomString()
			).put(
				"name", RandomTestUtil.randomString()
			).put(
				"type", "business"
			).put(
				_objectField.getName(), fileEntry.getFileEntryId()
			).build());

		InfoItemFieldValuesProvider<SystemObjectEntry>
			infoItemFieldValuesProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemFieldValuesProvider.class,
					_objectDefinition.getClassName() + StringPool.POUND +
						_objectDefinition.getObjectDefinitionId());

		InfoItemObjectProvider<SystemObjectEntry> infoItemObjectProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class,
				_objectDefinition.getClassName() + StringPool.POUND +
					_objectDefinition.getObjectDefinitionId());

		InfoItemFieldValues infoItemFieldValues =
			infoItemFieldValuesProvider.getInfoItemFieldValues(
				infoItemObjectProvider.getInfoItem(
					new ClassPKInfoItemIdentifier(accountEntryId)));

		_assertInfoFieldValue(
			fileEntry.getFileName(),
			_objectField.getObjectFieldId() + "#fileName", infoItemFieldValues);
		_assertInfoFieldValue(
			fileEntry.getMimeType(),
			_objectField.getObjectFieldId() + "#mimeType", infoItemFieldValues);
		_assertInfoFieldValue(
			fileEntry.getSize(), _objectField.getObjectFieldId() + "#size",
			infoItemFieldValues);

		AccountEntry accountEntry = _accountEntryLocalService.fetchAccountEntry(
			accountEntryId);

		_assertInfoFieldValue(
			accountEntry.getCreateDate(), "createDate", infoItemFieldValues);
		_assertInfoFieldValue(
			accountEntry.getDescription(), "description", infoItemFieldValues);
		_assertInfoFieldValue(
			accountEntry.getExternalReferenceCode(), "externalReferenceCode",
			infoItemFieldValues);
		_assertInfoFieldValue(
			accountEntry.getModifiedDate(), "modifiedDate",
			infoItemFieldValues);
		_assertInfoFieldValue(
			accountEntry.getName(), "name", infoItemFieldValues);
		_assertInfoFieldValue(
			accountEntry.getPrimaryKey(), "objectEntryId", infoItemFieldValues);
		_assertInfoFieldValue(
			WorkflowConstants.getStatusLabel(accountEntry.getStatus()),
			"status", infoItemFieldValues);
		_assertInfoFieldValue(
			accountEntry.getType(), "type", infoItemFieldValues);
		_assertInfoFieldValue(
			accountEntry.getUserName(), "author", infoItemFieldValues);
	}

	private void _assertInfoFieldValue(
		Object expectedValue, String infoFieldName,
		InfoItemFieldValues infoItemFieldValues) {

		InfoFieldValue<Object> infoFieldValue =
			infoItemFieldValues.getInfoFieldValue(infoFieldName);

		Assert.assertEquals(expectedValue, infoFieldValue.getValue());
	}

	private ObjectFieldSetting _createObjectFieldSetting(
		String name, String value) {

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.createObjectFieldSetting(0L);

		objectFieldSetting.setName(name);
		objectFieldSetting.setValue(value);

		return objectFieldSetting;
	}

	private ServiceContext _getServiceContext() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setTimeZone(TimeZoneUtil.getDefault());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		serviceContext.setRequest(mockHttpServletRequest);

		return serviceContext;
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DTOConverterRegistry _dtoConverterRegistry;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ObjectField _objectField;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Inject
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

}