/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.input.template.parser.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.input.template.parser.FragmentEntryInputTemplateNodeContextHelper;
import com.liferay.fragment.input.template.parser.InputTemplateNode;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.attachment.AttachmentManager;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.builder.DateObjectFieldBuilder;
import com.liferay.object.field.builder.DateTimeObjectFieldBuilder;
import com.liferay.object.field.builder.MultiselectPicklistObjectFieldBuilder;
import com.liferay.object.field.builder.PicklistObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import java.sql.Timestamp;

import java.text.DateFormat;

import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class FragmentEntryInputTemplateNodeContextHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_objectDefinition = _addObjectDefinition();

		_objectEntry = _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"myMultiselectPicklist",
				() -> {
					ListTypeEntry listTypeEntry2 = _listTypeEntries.get(1);
					ListTypeEntry listTypeEntry3 = _listTypeEntries.get(2);

					return listTypeEntry2.getKey() +
						StringPool.COMMA_AND_SPACE + listTypeEntry3.getKey();
				}
			).put(
				"myPicklist",
				() -> {
					ListTypeEntry listTypeEntry = _listTypeEntries.get(0);

					return listTypeEntry.getKey();
				}
			).put(
				"myText", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testGetMultiselectPicklistSelectInfoFieldTypeValue()
		throws Exception {

		ListTypeEntry listTypeEntry2 = _listTypeEntries.get(1);
		ListTypeEntry listTypeEntry3 = _listTypeEntries.get(2);

		_assertInputTemplateNodeInputValue(
			listTypeEntry2.getKey() + StringPool.COMMA +
				listTypeEntry3.getKey(),
			"ObjectField_myMultiselectPicklist");
	}

	@Test
	public void testGetPicklistSelectInfoFieldTypeValue() throws Exception {
		ListTypeEntry listTypeEntry = _listTypeEntries.get(0);

		_assertInputTemplateNodeInputValue(
			listTypeEntry.getKey(), "ObjectField_myPicklist");
	}

	@Test
	public void testGetRelationshipInfoFieldTypeValue() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				"ObjectDefinition",
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, "myText", "myText",
						false)),
				ObjectDefinitionConstants.SCOPE_SITE);

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, _objectDefinition,
				objectDefinition);

		String relationshipObjectFieldName = StringBundler.concat(
			"r_", StringUtil.toLowerCase(objectRelationship.getName()),
			"_c_customObjectDefinitionId");

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				relationshipObjectFieldName, _objectEntry.getObjectEntryId()
			).put(
				"myText", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		HttpServletRequest httpServletRequest = _getHttpServletRequest();

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			_layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(
					objectDefinition.getClassName());

		httpServletRequest.setAttribute(
			LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				new InfoItemReference(
					objectDefinition.getClassName(),
					objectEntry.getObjectEntryId())));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		serviceContext.setRequest(httpServletRequest);

		InfoItemFormProvider<?> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, objectDefinition.getClassName());

		try {
			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			InputTemplateNode inputTemplateNode =
				_fragmentEntryInputTemplateNodeContextHelper.
					toInputTemplateNode(
						Collections.emptyMap(), "Default",
						_addInputFragmentEntryLink(
							"ObjectField_" + relationshipObjectFieldName),
						httpServletRequest,
						infoItemFormProvider.getInfoForm(
							StringPool.BLANK, _group.getGroupId()),
						LocaleUtil.getSiteDefault());

			Map<String, Object> attributes = inputTemplateNode.getAttributes();

			Assert.assertEquals(
				_objectEntry.getTitleValue(),
				attributes.get("selectedOptionLabel"));
			Assert.assertEquals(
				String.valueOf(_objectEntry.getObjectEntryId()),
				attributes.get("selectedOptionValue"));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetRichTextSelectInfoFieldTypeValueWithInfoParametersMap()
		throws Exception {

		HttpServletRequest httpServletRequest = _getHttpServletRequest();

		Map<Locale, String> localeMap = HashMapBuilder.put(
			LocaleUtil.SPAIN, RandomTestUtil.randomString()
		).put(
			LocaleUtil.US, RandomTestUtil.randomString()
		).build();

		SessionMessages.add(
			httpServletRequest, "infoFormParameterMap",
			HashMapBuilder.<String, Object>put(
				"myRichText", localeMap
			).build());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		serviceContext.setRequest(httpServletRequest);

		InfoItemFormProvider<?> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, _objectDefinition.getClassName());

		try {
			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			InputTemplateNode inputTemplateNode =
				_fragmentEntryInputTemplateNodeContextHelper.
					toInputTemplateNode(
						Collections.emptyMap(), "Default",
						_addInputFragmentEntryLink("myRichText"),
						httpServletRequest,
						infoItemFormProvider.getInfoForm(
							StringPool.BLANK, _group.getGroupId()),
						LocaleUtil.getSiteDefault());

			Assert.assertEquals(
				localeMap.get(LocaleUtil.getSiteDefault()),
				inputTemplateNode.getInputValue());
			Assert.assertEquals(localeMap, inputTemplateNode.getValueI18n());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetTextSelectInfoFieldTypeValueWithInfoParametersMap()
		throws Exception {

		HttpServletRequest httpServletRequest = _getHttpServletRequest();

		String value = RandomTestUtil.randomString();

		SessionMessages.add(
			httpServletRequest, "infoFormParameterMap",
			HashMapBuilder.<String, Object>put(
				"myText", value
			).build());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		serviceContext.setRequest(httpServletRequest);

		InfoItemFormProvider<?> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, _objectDefinition.getClassName());

		try {
			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			InputTemplateNode inputTemplateNode =
				_fragmentEntryInputTemplateNodeContextHelper.
					toInputTemplateNode(
						Collections.emptyMap(), "Default",
						_addInputFragmentEntryLink("myText"),
						httpServletRequest,
						infoItemFormProvider.getInfoForm(
							StringPool.BLANK, _group.getGroupId()),
						LocaleUtil.getSiteDefault());

			Assert.assertEquals(value, inputTemplateNode.getInputValue());

			Assert.assertEquals(
				Collections.emptyMap(), inputTemplateNode.getValueI18n());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@FeatureFlag("LPD-32050")
	@Test
	public void testToInputTemplateNodeLocalizedInputValue() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				ListUtil.fromArray(
					new AttachmentObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"myAttachment"
					).objectFieldSettings(
						Arrays.asList(
							_createObjectFieldSetting(
								"acceptedFileExtensions", "txt"),
							_createObjectFieldSetting(
								"fileSource", "userComputer"),
							_createObjectFieldSetting("maximumFileSize", "100"))
					).build(),
					new DateObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"myDate"
					).objectFieldSettings(
						Collections.emptyList()
					).build(),
					new DateTimeObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"myDateTime"
					).objectFieldSettings(
						Collections.singletonList(
							_createObjectFieldSetting(
								ObjectFieldSettingConstants.NAME_TIME_STORAGE,
								ObjectFieldSettingConstants.
									VALUE_CONVERT_TO_UTC))
					).build(),
					new AttachmentObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).localized(
						true
					).name(
						"myLocalizedAttachment"
					).objectFieldSettings(
						Arrays.asList(
							_createObjectFieldSetting(
								"acceptedFileExtensions", "txt"),
							_createObjectFieldSetting(
								"fileSource", "userComputer"),
							_createObjectFieldSetting("maximumFileSize", "100"))
					).build(),
					new DateObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"myLocalizedDate"
					).localized(
						true
					).objectFieldSettings(
						Collections.emptyList()
					).build(),
					new DateTimeObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"myLocalizedDateTime"
					).localized(
						true
					).objectFieldSettings(
						Collections.singletonList(
							_createObjectFieldSetting(
								ObjectFieldSettingConstants.NAME_TIME_STORAGE,
								ObjectFieldSettingConstants.
									VALUE_CONVERT_TO_UTC))
					).build()),
				ObjectDefinitionConstants.SCOPE_SITE);

		DLFileEntry dlFileEntry = _addDLFileEntry();

		Date enDate = DateUtil.parseDate(
			"yyyy-MM-dd", "2021-05-31", LocaleUtil.US);

		Timestamp enTimestamp = new Timestamp(enDate.getTime());

		DLFileEntry esDLFileEntry = _addDLFileEntry();
		DLFileEntry enDLFileEntry = _addDLFileEntry();

		Date esDate = new Date(System.currentTimeMillis());

		Timestamp esTimestamp = new Timestamp(esDate.getTime());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"myAttachment", dlFileEntry.getFileEntryId()
			).put(
				"myDate", enDate
			).put(
				"myDateTime", enTimestamp
			).put(
				"myLocalizedAttachment_i18n",
				HashMapBuilder.put(
					LocaleUtil.toLanguageId(LocaleUtil.SPAIN),
					esDLFileEntry.getFileEntryId()
				).put(
					LocaleUtil.toLanguageId(LocaleUtil.US),
					enDLFileEntry.getFileEntryId()
				).build()
			).put(
				"myLocalizedDate_i18n",
				HashMapBuilder.put(
					LocaleUtil.toLanguageId(LocaleUtil.SPAIN), esDate
				).put(
					LocaleUtil.toLanguageId(LocaleUtil.US), enDate
				).build()
			).put(
				"myLocalizedDateTime_i18n",
				HashMapBuilder.put(
					LocaleUtil.toLanguageId(LocaleUtil.SPAIN), esTimestamp
				).put(
					LocaleUtil.toLanguageId(LocaleUtil.US), enTimestamp
				).build()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Map<String, ObjectField> objectFieldsMap =
			ObjectFieldUtil.toObjectFieldsMap(
				_objectFieldLocalService.getObjectFields(
					objectDefinition.getObjectDefinitionId()));

		long fileEntryId = _getFileEntryId(
			dlFileEntry, objectFieldsMap.get("myAttachment"));

		_testToInputTemplateNodeLocalizedInputValue(
			objectDefinition.getClassName(), "ObjectField_myAttachment",
			objectEntry,
			HashMapBuilder.<Locale, Object>put(
				LocaleUtil.SPAIN, String.valueOf(fileEntryId)
			).put(
				LocaleUtil.US, String.valueOf(fileEntryId)
			).build(),
			Collections.emptyMap());

		DateFormat enDateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd", LocaleUtil.US);

		String enValue = enDateFormat.format(enDate);

		_testToInputTemplateNodeLocalizedInputValue(
			objectDefinition.getClassName(), "ObjectField_myDate", objectEntry,
			Collections.emptyMap(),
			HashMapBuilder.<Locale, Object>put(
				LocaleUtil.SPAIN, enValue
			).put(
				LocaleUtil.US, enValue
			).build());

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
			"yyyy-MM-dd'T'HH:mm");

		enValue = dateTimeFormatter.format(enTimestamp.toLocalDateTime());

		_testToInputTemplateNodeLocalizedInputValue(
			objectDefinition.getClassName(), "ObjectField_myDateTime",
			objectEntry, Collections.emptyMap(),
			HashMapBuilder.<Locale, Object>put(
				LocaleUtil.SPAIN, enValue
			).put(
				LocaleUtil.US, enValue
			).build());

		long esFileEntryId = _getFileEntryId(
			esDLFileEntry, objectFieldsMap.get("myLocalizedAttachment"));
		long enFileEntryId = _getFileEntryId(
			enDLFileEntry, objectFieldsMap.get("myLocalizedAttachment"));

		_testToInputTemplateNodeLocalizedInputValue(
			objectDefinition.getClassName(),
			"ObjectField_myLocalizedAttachment", objectEntry,
			HashMapBuilder.<Locale, Object>put(
				LocaleUtil.SPAIN, String.valueOf(esFileEntryId)
			).put(
				LocaleUtil.US, String.valueOf(enFileEntryId)
			).build(),
			HashMapBuilder.<Locale, Object>put(
				LocaleUtil.SPAIN, String.valueOf(esFileEntryId)
			).put(
				LocaleUtil.US, String.valueOf(enFileEntryId)
			).build());

		DateFormat esDateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd", LocaleUtil.SPAIN);

		String esValue = esDateFormat.format(esDate);

		enValue = enDateFormat.format(enDate);

		_testToInputTemplateNodeLocalizedInputValue(
			objectDefinition.getClassName(), "ObjectField_myLocalizedDate",
			objectEntry,
			HashMapBuilder.<Locale, Object>put(
				LocaleUtil.SPAIN, esValue
			).put(
				LocaleUtil.US, enValue
			).build(),
			HashMapBuilder.<Locale, Object>put(
				LocaleUtil.SPAIN, esValue
			).put(
				LocaleUtil.US, enValue
			).build());

		esValue = dateTimeFormatter.format(esTimestamp.toLocalDateTime());
		enValue = dateTimeFormatter.format(enTimestamp.toLocalDateTime());

		_testToInputTemplateNodeLocalizedInputValue(
			objectDefinition.getClassName(), "ObjectField_myLocalizedDateTime",
			objectEntry,
			HashMapBuilder.<Locale, Object>put(
				LocaleUtil.SPAIN, esValue
			).put(
				LocaleUtil.US, enValue
			).build(),
			HashMapBuilder.<Locale, Object>put(
				LocaleUtil.SPAIN, esValue
			).put(
				LocaleUtil.US, enValue
			).build());
	}

	private DLFileEntry _addDLFileEntry() throws Exception {
		byte[] bytes = TestDataConstants.TEST_BYTE_ARRAY;

		InputStream inputStream = new ByteArrayInputStream(bytes);

		return _dlFileEntryLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt",
			MimeTypesUtil.getExtensionContentType("txt"),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			null, inputStream, bytes.length, null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private FragmentEntryLink _addInputFragmentEntryLink(String inputFieldId)
		throws Exception {

		return _fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
			RandomTestUtil.randomLong(),
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid()),
			_layout.getPlid(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK,
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("inputFieldId", inputFieldId)
			).toString(),
			StringPool.BLANK, 0, StringPool.BLANK, FragmentConstants.TYPE_INPUT,
			ServiceContextTestUtil.getServiceContext());
	}

	private ObjectDefinition _addObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				_group.getCompanyId(), "C_CustomObjectDefinition");

		if (objectDefinition != null) {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition.getObjectDefinitionId());
		}

		_listTypeEntries.add(
			ListTypeEntryUtil.createListTypeEntry(
				"Apple", Collections.singletonMap(LocaleUtil.US, "Apple")));
		_listTypeEntries.add(
			ListTypeEntryUtil.createListTypeEntry(
				"Banana", Collections.singletonMap(LocaleUtil.US, "Banana")));
		_listTypeEntries.add(
			ListTypeEntryUtil.createListTypeEntry(
				"Orange", Collections.singletonMap(LocaleUtil.US, "Orange")));

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				false, _listTypeEntries);

		ObjectField myRichTextObjectField = ObjectFieldUtil.createObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, RandomTestUtil.randomString(),
			"myRichText", false);

		myRichTextObjectField.setLocalized(true);

		List<ObjectField> objectFields = Arrays.asList(
			new AttachmentObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"myAttachment"
			).objectFieldSettings(
				Arrays.asList(
					_createObjectFieldSetting("acceptedFileExtensions", "txt"),
					_createObjectFieldSetting("fileSource", "userComputer"),
					_createObjectFieldSetting("maximumFileSize", "100"))
			).build(),
			new DateTimeObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"myDateTime"
			).objectFieldSettings(
				Collections.singletonList(
					_createObjectFieldSetting(
						ObjectFieldSettingConstants.NAME_TIME_STORAGE,
						ObjectFieldSettingConstants.VALUE_CONVERT_TO_UTC))
			).build(),
			new PicklistObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).listTypeDefinitionId(
				listTypeDefinition.getListTypeDefinitionId()
			).name(
				"myPicklist"
			).build(),
			new MultiselectPicklistObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).listTypeDefinitionId(
				listTypeDefinition.getListTypeDefinitionId()
			).name(
				"myMultiselectPicklist"
			).build(),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_DATE,
				ObjectFieldConstants.DB_TYPE_DATE,
				RandomTestUtil.randomString(), "myDate", false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_DECIMAL,
				ObjectFieldConstants.DB_TYPE_DOUBLE,
				RandomTestUtil.randomString(), "myDecimal", false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN,
				ObjectFieldConstants.DB_TYPE_BOOLEAN,
				RandomTestUtil.randomString(), "myBoolean", false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
				ObjectFieldConstants.DB_TYPE_INTEGER,
				RandomTestUtil.randomString(), "myInteger", false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER,
				ObjectFieldConstants.DB_TYPE_LONG,
				RandomTestUtil.randomString(), "myLongInteger", false),
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_PRECISION_DECIMAL,
				ObjectFieldConstants.DB_TYPE_BIG_DECIMAL,
				RandomTestUtil.randomString(), "myPrecisionDecimal", false),
			myRichTextObjectField);

		objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, true,
				true, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"CustomObjectDefinition", null, "control_panel.sites",
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_SITE,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), objectFields);

		ObjectField myTextObjectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).indexed(
				true
			).indexedAsKeyword(
				true
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"myText"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).build());

		objectDefinition.setTitleObjectFieldId(
			myTextObjectField.getObjectFieldId());

		objectDefinition = _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private void _assertInputTemplateNodeInputValue(
			String expectedValue, String inputFieldId)
		throws Exception {

		HttpServletRequest httpServletRequest = _getHttpServletRequest();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		serviceContext.setRequest(httpServletRequest);

		InfoItemFormProvider<?> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, _objectDefinition.getClassName());

		try {
			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			InputTemplateNode inputTemplateNode =
				_fragmentEntryInputTemplateNodeContextHelper.
					toInputTemplateNode(
						Collections.emptyMap(), "Default",
						_addInputFragmentEntryLink(inputFieldId),
						httpServletRequest,
						infoItemFormProvider.getInfoForm(
							StringPool.BLANK, _group.getGroupId()),
						LocaleUtil.getSiteDefault());

			Assert.assertEquals(
				expectedValue, inputTemplateNode.getInputValue());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private ObjectFieldSetting _createObjectFieldSetting(
		String name, String value) {

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.createObjectFieldSetting(0L);

		objectFieldSetting.setName(name);
		objectFieldSetting.setValue(value);

		return objectFieldSetting;
	}

	private long _getFileEntryId(
			DLFileEntry dlFileEntry, ObjectField objectField)
		throws Exception {

		DLFolder dlFolder = _attachmentManager.getDLFolder(
			_group.getCompanyId(), _group.getGroupId(),
			objectField.getObjectFieldId(),
			ServiceContextTestUtil.getServiceContext(),
			TestPropsValues.getUserId());

		FileEntry fileEntry = _dlAppLocalService.getFileEntryByFileName(
			_group.getGroupId(), dlFolder.getFolderId(),
			TempFileEntryUtil.getOriginalTempFileName(
				dlFileEntry.getFileName()));

		return fileEntry.getFileEntryId();
	}

	private HttpServletRequest _getHttpServletRequest() throws Exception {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			_layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(
					_objectDefinition.getClassName());

		httpServletRequest.setAttribute(
			LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				new InfoItemReference(
					_objectDefinition.getClassName(),
					_objectEntry.getObjectEntryId())));

		return httpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLayout(_layout);
		themeDisplay.setLocale(LocaleUtil.getSiteDefault());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _testToInputTemplateNodeLocalizedInputValue(
			String className, String inputFieldId, ObjectEntry objectEntry,
			Map<Locale, Object> valueI18nMap, Map<Locale, Object> values)
		throws Exception {

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		ThemeDisplay themeDisplay = _getThemeDisplay();

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			_layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(className);

		httpServletRequest.setAttribute(
			LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				new InfoItemReference(
					className, objectEntry.getObjectEntryId())));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		serviceContext.setRequest(httpServletRequest);

		InfoItemFormProvider<?> infoItemFormProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormProvider.class, className);

		try {
			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			for (Map.Entry<Locale, Object> entry : values.entrySet()) {
				Locale locale = entry.getKey();

				themeDisplay.setLocale(locale);

				InputTemplateNode inputTemplateNode =
					_fragmentEntryInputTemplateNodeContextHelper.
						toInputTemplateNode(
							Collections.emptyMap(), "Default",
							_addInputFragmentEntryLink(inputFieldId),
							httpServletRequest,
							infoItemFormProvider.getInfoForm(
								StringPool.BLANK, _group.getGroupId()),
							locale);

				Assert.assertEquals(
					entry.getValue(), inputTemplateNode.getInputValue());

				Map<Locale, String> actualValueI18nMap =
					inputTemplateNode.getValueI18n();

				Assert.assertEquals(
					MapUtil.toString(actualValueI18nMap), valueI18nMap.size(),
					actualValueI18nMap.size());

				for (Map.Entry<Locale, Object> curEntry :
						valueI18nMap.entrySet()) {

					Assert.assertEquals(
						curEntry.getValue(),
						actualValueI18nMap.get(curEntry.getKey()));
				}
			}
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Inject
	private AttachmentManager _attachmentManager;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private FragmentEntryInputTemplateNodeContextHelper
		_fragmentEntryInputTemplateNodeContextHelper;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	private Layout _layout;

	@Inject
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	private final List<ListTypeEntry> _listTypeEntries = new ArrayList<>();
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ObjectEntry _objectEntry;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}