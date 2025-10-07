/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.internal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestHelper;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesToFieldsConverter;
import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.entry.processor.helper.FragmentEntryProcessorHelper;
import com.liferay.fragment.entry.processor.helper.InfoItemFieldMapped;
import com.liferay.fragment.processor.DefaultFragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.journal.util.JournalConverter;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.InputStream;
import java.io.Serializable;

import java.text.DateFormat;

import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class FragmentEntryProcessorHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group.getGroupId());

		_themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_companyLocalService.getCompany(_group.getCompanyId()), _group,
			_layout);
	}

	@Test
	public void testGetFieldValueFromCollectionValue() throws Exception {
		JournalArticle journalArticle = _addJournalArticle(
			_addImageFileEntry(), "ImageFieldName",
			RandomTestUtil.randomString());

		Assert.assertEquals(
			"one, two, three",
			_getFieldValue(
				JSONUtil.put(
					"className", JournalArticle.class.getName()
				).put(
					"classNameId",
					_portal.getClassNameId(JournalArticle.class.getName())
				).put(
					"classPK", journalArticle.getResourcePrimKey()
				).put(
					"fieldId", "AssetTag_tagNames"
				),
				LocaleUtil.SPAIN));
	}

	@Test
	public void testGetFieldValueFromLabeledValue() throws Exception {
		JournalArticle journalArticle = _addJournalArticle(
			_addImageFileEntry(), "ImageFieldName", "Custom Title");

		Assert.assertEquals(
			"Custom Title",
			_getFieldValue(
				JSONUtil.put(
					"className", JournalArticle.class.getName()
				).put(
					"classNameId",
					_portal.getClassNameId(JournalArticle.class.getName())
				).put(
					"classPK", journalArticle.getResourcePrimKey()
				).put(
					"fieldId", "title"
				),
				LocaleUtil.SPAIN));
	}

	@Test
	@TestInfo("LPD-62842")
	public void testGetFieldValueFromLongText() throws Exception {
		String fieldValue = StringBundler.concat(
			"<script>alert(\"", RandomTestUtil.randomString(), "\")</script>");

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, "My Long Text",
						"myLongText")),
				ObjectDefinitionConstants.SCOPE_SITE);

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getGroupId(), objectDefinition.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).put(
				"myLongText", fieldValue
			).build(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		try {
			_pushServiceContext(_layout, _themeDisplay);

			Assert.assertEquals(
				HtmlUtil.escape(fieldValue),
				_getFieldValue(
					JSONUtil.put(
						"className", objectDefinition.getClassName()
					).put(
						"classNameId",
						_portal.getClassNameId(objectDefinition.getClassName())
					).put(
						"classPK", objectEntry.getObjectEntryId()
					).put(
						"fieldId", "myLongText"
					),
					LocaleUtil.getSiteDefault()));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetFieldValueFromNullValue() throws Exception {
		JournalArticle journalArticle = _addJournalArticle(
			_addImageFileEntry(), "ImageFieldName",
			RandomTestUtil.randomString());

		Assert.assertNull(
			_getFieldValue(
				JSONUtil.put(
					"className", JournalArticle.class.getName()
				).put(
					"classNameId",
					_portal.getClassNameId(JournalArticle.class.getName())
				).put(
					"classPK", journalArticle.getResourcePrimKey()
				).put(
					"fieldId", "NoExistingFieldId"
				),
				LocaleUtil.SPAIN));
	}

	@Test
	public void testGetFieldValueFromStringValue() throws Exception {
		DDMFormField ddmFormField = _createDDMFormField(
			DDMFormFieldTypeConstants.TEXT);
		String fieldValue = StringBundler.concat(
			"<script>alert(\"", RandomTestUtil.randomString(), "\")</script>");

		JournalArticle journalArticle = JournalTestUtil.addJournalArticle(
			_dataDefinitionResourceFactory, ddmFormField,
			_ddmFormValuesToFieldsConverter, fieldValue, _group.getGroupId(),
			_journalConverter);

		Assert.assertEquals(
			HtmlUtil.escape(fieldValue),
			_getFieldValue(
				JSONUtil.put(
					"className", JournalArticle.class.getName()
				).put(
					"classNameId",
					_portal.getClassNameId(JournalArticle.class.getName())
				).put(
					"classPK", journalArticle.getResourcePrimKey()
				).put(
					"fieldId", "DDMStructure_" + ddmFormField.getName()
				),
				LocaleUtil.SPAIN));
	}

	@Test
	@TestInfo("LPS-162223")
	public void testGetFieldValueFromStringValueDateDDMFormFieldType()
		throws Exception {

		DDMFormField ddmFormField = _createDDMFormField(
			DDMFormFieldTypeConstants.DATE);

		Date date = new Date();

		JournalArticle journalArticle = JournalTestUtil.addJournalArticle(
			_dataDefinitionResourceFactory, ddmFormField,
			_ddmFormValuesToFieldsConverter,
			DateUtil.getDate(date, "yyyy-MM-dd", LocaleUtil.SPAIN),
			_group.getGroupId(), _journalConverter);

		Assert.assertEquals(
			_formatDate(date, LocaleUtil.GERMANY),
			_getFieldValue(
				JSONUtil.put(
					"className", JournalArticle.class.getName()
				).put(
					"classNameId",
					_portal.getClassNameId(JournalArticle.class.getName())
				).put(
					"classPK", journalArticle.getResourcePrimKey()
				).put(
					"fieldId", "DDMStructure_" + ddmFormField.getName()
				),
				LocaleUtil.GERMANY));

		Assert.assertEquals(
			_formatDate(date, LocaleUtil.SPAIN),
			_getFieldValue(
				JSONUtil.put(
					"className", JournalArticle.class.getName()
				).put(
					"classNameId",
					_portal.getClassNameId(JournalArticle.class.getName())
				).put(
					"classPK", journalArticle.getResourcePrimKey()
				).put(
					"fieldId", "DDMStructure_" + ddmFormField.getName()
				),
				LocaleUtil.SPAIN));

		Assert.assertEquals(
			_formatDate(date, LocaleUtil.US),
			_getFieldValue(
				JSONUtil.put(
					"className", JournalArticle.class.getName()
				).put(
					"classNameId",
					_portal.getClassNameId(JournalArticle.class.getName())
				).put(
					"classPK", journalArticle.getResourcePrimKey()
				).put(
					"fieldId", "DDMStructure_" + ddmFormField.getName()
				),
				LocaleUtil.US));
	}

	@Test
	public void testGetFieldValueFromStringValueLinkToLayoutDDMFormFieldType()
		throws Exception {

		DDMFormField ddmFormField = _createDDMFormField(
			DDMFormFieldTypeConstants.LINK_TO_LAYOUT);

		JournalArticle journalArticle = JournalTestUtil.addJournalArticle(
			_dataDefinitionResourceFactory, ddmFormField,
			_ddmFormValuesToFieldsConverter,
			JSONUtil.put(
				"groupId", _layout.getGroupId()
			).put(
				"id", _layout.getUuid()
			).put(
				"layoutId", _layout.getLayoutId()
			).put(
				"name", _layout.getName()
			).put(
				"privateLayout", _layout.isPrivateLayout()
			).put(
				"returnType",
				"com.liferay.item.selector.criteria.UUIDItemSelectorReturnType"
			).put(
				"title", _layout.getTitle()
			).toString(),
			_group.getGroupId(), _journalConverter);

		try {
			_pushServiceContext(_layout, _themeDisplay);

			Assert.assertEquals(
				_portal.getLayoutFriendlyURL(
					_layout, _themeDisplay, LocaleUtil.SPAIN),
				_getFieldValue(
					JSONUtil.put(
						"className", JournalArticle.class.getName()
					).put(
						"classNameId",
						_portal.getClassNameId(JournalArticle.class.getName())
					).put(
						"classPK", journalArticle.getResourcePrimKey()
					).put(
						"fieldId", "DDMStructure_" + ddmFormField.getName()
					),
					LocaleUtil.SPAIN));
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetFieldValueFromStringValueRichTextDDMFormFieldType()
		throws Exception {

		DDMFormField ddmFormField = _createDDMFormField(
			DDMFormFieldTypeConstants.RICH_TEXT);
		String fieldValue = StringBundler.concat(
			"<p>", RandomTestUtil.randomString(), "</p>");

		JournalArticle journalArticle = JournalTestUtil.addJournalArticle(
			_dataDefinitionResourceFactory, ddmFormField,
			_ddmFormValuesToFieldsConverter, fieldValue, _group.getGroupId(),
			_journalConverter);

		Assert.assertEquals(
			fieldValue,
			_getFieldValue(
				JSONUtil.put(
					"className", JournalArticle.class.getName()
				).put(
					"classNameId",
					_portal.getClassNameId(JournalArticle.class.getName())
				).put(
					"classPK", journalArticle.getResourcePrimKey()
				).put(
					"fieldId", "DDMStructure_" + ddmFormField.getName()
				),
				LocaleUtil.SPAIN));
	}

	@Test
	public void testGetFieldValueFromURLStringValue() throws Exception {
		DDMFormField ddmFormField = _createDDMFormField(
			DDMFormFieldTypeConstants.TEXT);
		String fieldValue = "testurl.com?test=info&param=info2";

		JournalArticle journalArticle = JournalTestUtil.addJournalArticle(
			_dataDefinitionResourceFactory, ddmFormField,
			_ddmFormValuesToFieldsConverter, fieldValue, _group.getGroupId(),
			_journalConverter);

		Assert.assertEquals(
			fieldValue,
			_getFieldValue(
				JSONUtil.put(
					"className", JournalArticle.class.getName()
				).put(
					"classNameId",
					_portal.getClassNameId(JournalArticle.class.getName())
				).put(
					"classPK", journalArticle.getResourcePrimKey()
				).put(
					"fieldId", "DDMStructure_" + ddmFormField.getName()
				),
				LocaleUtil.SPAIN));
	}

	@Test
	public void testGetFieldValueFromWebImage() throws Exception {
		String fieldId = "ImageFieldName";

		JournalArticle journalArticle = _addJournalArticle(
			_addImageFileEntry(), fieldId, RandomTestUtil.randomString());

		Object actual = _getFieldValue(
			JSONUtil.put(
				"className", JournalArticle.class.getName()
			).put(
				"classNameId",
				_portal.getClassNameId(JournalArticle.class.getName())
			).put(
				"classPK", journalArticle.getResourcePrimKey()
			).put(
				"fieldId", fieldId
			),
			LocaleUtil.SPAIN);

		Assert.assertTrue(actual instanceof JSONObject);
	}

	@Test
	public void testGetFieldValueWithNonexistingInfoItem() throws Exception {
		Assert.assertNull(
			_getFieldValue(
				JSONUtil.put(
					"className", JournalArticle.class.getName()
				).put(
					"classNameId",
					_portal.getClassNameId(JournalArticle.class.getName())
				).put(
					"classPK", RandomTestUtil.randomLong()
				).put(
					"fieldId", "AssetTag_tagNames"
				),
				LocaleUtil.SPAIN));
	}

	@Test
	public void testGetFileEntryIdClassNameClassPKDLImage() throws Exception {
		FileEntry fileEntry = _addImageFileEntry();

		Assert.assertEquals(
			fileEntry.getFileEntryId(),
			_fragmentEntryProcessorHelper.getFileEntryId(
				FileEntry.class.getName(), fileEntry.getFileEntryId()));
	}

	@Test
	public void testGetFileEntryIdClassNameClassPKJournalArticle()
		throws Exception {

		JournalArticle journalArticle = _addJournalArticle(
			_addImageFileEntry(), "ImageFieldName",
			RandomTestUtil.randomString());

		Assert.assertEquals(
			0L,
			_fragmentEntryProcessorHelper.getFileEntryId(
				JournalArticle.class.getName(),
				journalArticle.getResourcePrimKey()));
	}

	@Test
	public void testGetFileEntryIdClassPKDLImage() throws Exception {
		FileEntry fileEntry = _addImageFileEntry();

		Assert.assertEquals(
			fileEntry.getFileEntryId(),
			_fragmentEntryProcessorHelper.getFileEntryId(
				_portal.getClassNameId(FileEntry.class.getName()),
				fileEntry.getFileEntryId(), "fileURL",
				LocaleUtil.getSiteDefault()));
	}

	@Test
	public void testGetFileEntryIdClassPKJournalArticle() throws Exception {
		FileEntry fileEntry = _addImageFileEntry();

		String fieldId = "ImageFieldName";

		JournalArticle journalArticle = _addJournalArticle(
			fileEntry, fieldId, RandomTestUtil.randomString());

		Assert.assertEquals(
			fileEntry.getFileEntryId(),
			_fragmentEntryProcessorHelper.getFileEntryId(
				_portal.getClassNameId(JournalArticle.class.getName()),
				journalArticle.getResourcePrimKey(), fieldId,
				LocaleUtil.getSiteDefault()));
	}

	@Test
	public void testGetFileEntryIdDisplayObjectJournalArticle()
		throws Exception {

		FileEntry fileEntry = _addImageFileEntry();

		String fieldId = "ImageFieldName";

		JournalArticle journalArticle = _addJournalArticle(
			fileEntry, fieldId, RandomTestUtil.randomString());

		Assert.assertEquals(
			fileEntry.getFileEntryId(),
			_fragmentEntryProcessorHelper.getFileEntryId(
				new InfoItemReference(
					JournalArticle.class.getName(),
					new ClassPKInfoItemIdentifier(
						journalArticle.getResourcePrimKey())),
				fieldId, LocaleUtil.getSiteDefault()));
	}

	@Test
	@TestInfo("LPD-62842")
	public void testGetInfoItemFieldMapped() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				ListUtil.fromArray(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						"myText"
					).objectFieldSettings(
						Collections.emptyList()
					).build()),
				ObjectDefinitionConstants.SCOPE_DEPOT);

		_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
			objectDefinition.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
			StringPool.TRUE);

		long classNameId = _portal.getClassNameId(
			objectDefinition.getClassName());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		DepotEntry depotEntry1 = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY, serviceContext);

		String externalReferenceCode = RandomTestUtil.randomString();

		ObjectEntry objectEntry1 = _objectEntryLocalService.addObjectEntry(
			depotEntry1.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"myText", RandomTestUtil.randomString()
			).build(),
			serviceContext);

		_testGetInfoItemFieldMapped(
			JSONUtil.put(
				"className", objectDefinition.getClassName()
			).put(
				"classNameId", classNameId
			).put(
				"classPK", objectEntry1.getObjectEntryId()
			).put(
				"fieldId", "myText"
			),
			depotEntry1.getGroupId(),
			new InfoItemFieldMapped(
				"myText",
				new InfoItemReference(
					objectDefinition.getClassName(),
					new ClassPKInfoItemIdentifier(
						objectEntry1.getObjectEntryId())),
				objectEntry1));

		DepotEntry depotEntry2 = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY, serviceContext);

		ObjectEntry objectEntry2 = _objectEntryLocalService.addObjectEntry(
			depotEntry2.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"myText", RandomTestUtil.randomString()
			).build(),
			serviceContext);

		_testGetInfoItemFieldMapped(
			JSONUtil.put(
				"className", objectDefinition.getClassName()
			).put(
				"classNameId", classNameId
			).put(
				"classPK", objectEntry2.getObjectEntryId()
			).put(
				"fieldId", "myText"
			),
			depotEntry2.getGroupId(),
			new InfoItemFieldMapped(
				"myText",
				new InfoItemReference(
					objectDefinition.getClassName(),
					new ClassPKInfoItemIdentifier(
						objectEntry2.getObjectEntryId())),
				objectEntry2));

		Group group1 = depotEntry1.getGroup();

		_testGetInfoItemFieldMapped(
			JSONUtil.put(
				"className", objectDefinition.getClassName()
			).put(
				"classNameId", classNameId
			).put(
				"classPK", objectEntry1.getObjectEntryId()
			).put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"fieldId", "myText"
			).put(
				"scopeExternalReferenceCode", group1.getExternalReferenceCode()
			),
			depotEntry1.getGroupId(),
			new InfoItemFieldMapped(
				"myText",
				new InfoItemReference(
					objectDefinition.getClassName(),
					new ClassPKInfoItemIdentifier(
						objectEntry1.getObjectEntryId())),
				objectEntry1));

		Group group2 = depotEntry2.getGroup();

		_testGetInfoItemFieldMapped(
			JSONUtil.put(
				"className", objectDefinition.getClassName()
			).put(
				"classNameId", classNameId
			).put(
				"classPK", objectEntry1.getObjectEntryId()
			).put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"fieldId", "myText"
			).put(
				"scopeExternalReferenceCode", group2.getExternalReferenceCode()
			),
			depotEntry2.getGroupId(),
			new InfoItemFieldMapped(
				"myText",
				new InfoItemReference(
					objectDefinition.getClassName(),
					new ClassPKInfoItemIdentifier(
						objectEntry1.getObjectEntryId())),
				objectEntry1));

		_testGetInfoItemFieldMapped(
			JSONUtil.put(
				"className", objectDefinition.getClassName()
			).put(
				"classNameId", classNameId
			).put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"fieldId", "myText"
			).put(
				"scopeExternalReferenceCode", group1.getExternalReferenceCode()
			),
			depotEntry1.getGroupId(),
			new InfoItemFieldMapped(
				"myText",
				new InfoItemReference(
					objectDefinition.getClassName(),
					new ERCInfoItemIdentifier(
						externalReferenceCode,
						group1.getExternalReferenceCode())),
				objectEntry1));
		_testGetInfoItemFieldMapped(
			JSONUtil.put(
				"className", objectDefinition.getClassName()
			).put(
				"classNameId", classNameId
			).put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"fieldId", "myText"
			).put(
				"scopeExternalReferenceCode", group2.getExternalReferenceCode()
			),
			depotEntry2.getGroupId(),
			new InfoItemFieldMapped(
				"myText",
				new InfoItemReference(
					objectDefinition.getClassName(),
					new ERCInfoItemIdentifier(
						externalReferenceCode,
						group2.getExternalReferenceCode())),
				objectEntry2));
	}

	@Test
	@TestInfo({"LPD-11377", "LPD-51076"})
	public void testGetRepeatableAssetTags() throws Exception {
		JSONObject jsonObject = JSONUtil.put(
			"className", JournalArticle.class.getName()
		).put(
			"classNameId",
			_portal.getClassNameId(JournalArticle.class.getName())
		).put(
			"classPK",
			() -> {
				JournalArticle journalArticle = _addJournalArticle(
					_addImageFileEntry(), "ImageFieldName",
					RandomTestUtil.randomString());

				return journalArticle.getResourcePrimKey();
			}
		).put(
			"fieldId", "AssetTag_tagNames"
		);

		Assert.assertEquals(
			"one, two, three",
			_getFieldValue(
				jsonObject.put("config", JSONUtil.put("iterationType", "all")),
				LocaleUtil.SPAIN));
		Assert.assertEquals(
			"one",
			_getFieldValue(
				jsonObject.put(
					"config", JSONUtil.put("iterationType", "first")),
				LocaleUtil.SPAIN));
		Assert.assertEquals(
			"two",
			_getFieldValue(
				jsonObject.put(
					"config",
					JSONUtil.put(
						"iterationNumber", "2"
					).put(
						"iterationType", "iteration-number"
					)),
				LocaleUtil.SPAIN));
		Assert.assertEquals(
			"three",
			_getFieldValue(
				jsonObject.put("config", JSONUtil.put("iterationType", "last")),
				LocaleUtil.SPAIN));
	}

	@Test
	@TestInfo("LPD-11377")
	public void testGetRepeatableAssetVocabularies() throws Exception {
		JSONObject jsonObject = JSONUtil.put(
			"className", JournalArticle.class.getName()
		).put(
			"classNameId",
			_portal.getClassNameId(JournalArticle.class.getName())
		).put(
			"classPK",
			() -> {
				JournalArticle journalArticle = _addJournalArticle(
					_addImageFileEntry(), "ImageFieldName",
					RandomTestUtil.randomString());

				return journalArticle.getResourcePrimKey();
			}
		).put(
			"fieldId",
			AssetVocabulary.class.getSimpleName() + StringPool.UNDERLINE +
				_assetVocabulary.getVocabularyId()
		);

		Assert.assertEquals(
			"category1",
			_getFieldValue(
				jsonObject.put(
					"config", JSONUtil.put("iterationType", "first")),
				LocaleUtil.SPAIN));
		Assert.assertEquals(
			"category2",
			_getFieldValue(
				jsonObject.put(
					"config",
					JSONUtil.put(
						"iterationNumber", "2"
					).put(
						"iterationType", "iteration-number"
					)),
				LocaleUtil.SPAIN));
		Assert.assertEquals(
			"category3",
			_getFieldValue(
				jsonObject.put("config", JSONUtil.put("iterationType", "last")),
				LocaleUtil.SPAIN));
	}

	@Test
	@TestInfo("LPD-11377")
	public void testGetRepeatableFieldValue() throws Exception {
		JSONObject jsonObject = JSONUtil.put(
			"className", JournalArticle.class.getName()
		).put(
			"classNameId",
			_portal.getClassNameId(JournalArticle.class.getName())
		).put(
			"classPK",
			() -> {
				JournalArticle journalArticle = _addJournalArticle(
					_addDDMStructure(
						_readJSONFileToString(
							"ddm_structure_with_repeatable_field.json")),
					_readFileToString(
						"dynamic_content_with_repeatable_field.xml"),
					RandomTestUtil.randomString());

				return journalArticle.getResourcePrimKey();
			}
		).put(
			"fieldId", "DDMStructure_Text"
		);

		Assert.assertEquals(
			"uno", _getFieldValue(jsonObject, LocaleUtil.SPAIN));
		Assert.assertEquals(
			"one",
			_getFieldValue(
				jsonObject.put(
					"config", JSONUtil.put("iterationType", "first")),
				LocaleUtil.US));
		Assert.assertEquals(
			"dos",
			_getFieldValue(
				jsonObject.put(
					"config",
					JSONUtil.put(
						"iterationNumber", "2"
					).put(
						"iterationType", "iteration-number"
					)),
				LocaleUtil.SPAIN));
		Assert.assertEquals(
			"three",
			_getFieldValue(
				jsonObject.put("config", JSONUtil.put("iterationType", "last")),
				LocaleUtil.US));
	}

	@FeatureFlag("LPD-19955")
	@Test
	@TestInfo({"LPD-12834", "LPD-52354"})
	public void testHasViewPermission() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), 0L);

		JSONObject editableValueJSONObject = JSONUtil.put(
			"className", JournalArticle.class.getName()
		).put(
			"classNameId",
			_portal.getClassNameId(JournalArticle.class.getName())
		).put(
			"classPK", journalArticle.getResourcePrimKey()
		).put(
			"fieldId", RandomTestUtil.randomString()
		);

		_testHasViewPermission(editableValueJSONObject, Boolean.TRUE);

		RoleTestUtil.removeResourcePermission(
			RoleConstants.GUEST, JournalArticle.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(journalArticle.getResourcePrimKey()),
			ActionKeys.VIEW);

		_testHasViewPermission(editableValueJSONObject, Boolean.FALSE);
	}

	private DDMStructure _addDDMStructure(String content) throws Exception {
		DDMStructureTestHelper ddmStructureTestHelper =
			new DDMStructureTestHelper(
				_portal.getClassNameId(JournalArticle.class), _group);

		return ddmStructureTestHelper.addStructure(
			_portal.getClassNameId(JournalArticle.class),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_deserialize(content), StorageType.DEFAULT.getValue(),
			DDMStructureConstants.TYPE_DEFAULT);
	}

	private FileEntry _addImageFileEntry() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		byte[] bytes = FileUtil.getBytes(
			FragmentEntryProcessorHelperTest.class,
			"/com/liferay/fragment/entry/processor/internal/util/test" +
				"/dependencies/image.jpg");

		InputStream inputStream = new UnsyncByteArrayInputStream(bytes);

		LocalRepository localRepository =
			RepositoryProviderUtil.getLocalRepository(_group.getGroupId());

		return localRepository.addFileEntry(
			null, TestPropsValues.getUserId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.IMAGE_JPEG,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, inputStream, bytes.length, null,
			null, null, serviceContext);
	}

	private JournalArticle _addJournalArticle(
			DDMStructure ddmStructure, String fieldId, FileEntry fileEntry,
			String title)
		throws Exception {

		String content = StringUtil.replace(
			_readFileToString("dynamic_content.xml"),
			new String[] {"[$FIELD_ID$]", "[$IMAGE_JSON$]"},
			new String[] {fieldId, _toJSON(fileEntry)});

		return _addJournalArticle(ddmStructure, content, title);
	}

	private JournalArticle _addJournalArticle(
			DDMStructure ddmStructure, String content, String title)
		throws Exception {

		User user = TestPropsValues.getUser();

		Locale defaultLocale = LocaleUtil.getSiteDefault();

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			_portal.getClassNameId(JournalArticle.class));

		Calendar displayCalendar = CalendarFactoryUtil.getCalendar(
			user.getTimeZone());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		_assetVocabulary = _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(), "category1",
			_assetVocabulary.getVocabularyId(), serviceContext);
		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(), "category2",
			_assetVocabulary.getVocabularyId(), serviceContext);
		AssetCategory assetCategory3 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(), "category3",
			_assetVocabulary.getVocabularyId(), serviceContext);

		serviceContext.setAssetCategoryIds(
			new long[] {
				assetCategory1.getCategoryId(), assetCategory2.getCategoryId(),
				assetCategory3.getCategoryId()
			});

		serviceContext.setAssetTagNames(new String[] {"one", "two", "three"});

		return _journalArticleLocalService.addArticle(
			null, user.getUserId(), _group.getGroupId(), 0,
			JournalArticleConstants.CLASS_NAME_ID_DEFAULT, 0, StringPool.BLANK,
			true, JournalArticleConstants.VERSION_DEFAULT,
			HashMapBuilder.put(
				defaultLocale, title
			).build(),
			HashMapBuilder.put(
				defaultLocale, defaultLocale.toString()
			).build(),
			HashMapBuilder.put(
				defaultLocale, RandomTestUtil.randomString()
			).build(),
			content, ddmStructure.getStructureId(),
			ddmTemplate.getTemplateKey(), null,
			displayCalendar.get(Calendar.MONTH),
			displayCalendar.get(Calendar.DATE),
			displayCalendar.get(Calendar.YEAR),
			displayCalendar.get(Calendar.HOUR_OF_DAY),
			displayCalendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true, 0, 0, 0,
			0, 0, true, true, false, 0, 0, null, null, null, null,
			serviceContext);
	}

	private JournalArticle _addJournalArticle(
			FileEntry fileEntry, String fieldId, String title)
		throws Exception {

		String ddmStructureContent = _readJSONFileToString(
			"ddm_structure.json");

		ddmStructureContent = StringUtil.replace(
			ddmStructureContent, "FIELD_NAME", fieldId);

		DDMStructure ddmStructure = _addDDMStructure(ddmStructureContent);

		return _addJournalArticle(ddmStructure, fieldId, fileEntry, title);
	}

	private DDMFormField _createDDMFormField(String type) {
		DDMFormField ddmFormField = new DDMFormField("name", type);

		ddmFormField.setDataType("text");
		ddmFormField.setIndexType("text");
		ddmFormField.setLocalizable(true);

		LocalizedValue localizedValue = new LocalizedValue(LocaleUtil.US);

		localizedValue.addString(
			LocaleUtil.US, RandomTestUtil.randomString(10));

		ddmFormField.setLabel(localizedValue);

		return ddmFormField;
	}

	private DDMForm _deserialize(String content) {
		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(content);

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				_jsonDDMFormDeserializer.deserialize(builder.build());

		return ddmFormDeserializerDeserializeResponse.getDDMForm();
	}

	private String _formatDate(Date date, Locale locale) {
		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			DateTimeFormatterBuilder.getLocalizedDateTimePattern(
				FormatStyle.SHORT, null, IsoChronology.INSTANCE, locale),
			locale);

		return dateFormat.format(date);
	}

	private Object _getFieldValue(
			JSONObject editableValuesJSONObject, Locale locale)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, _layout);
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		FragmentEntryProcessorContext fragmentEntryProcessorContext =
			new DefaultFragmentEntryProcessorContext(
				mockHttpServletRequest, new MockHttpServletResponse(),
				FragmentEntryLinkConstants.EDIT, locale);

		return _fragmentEntryProcessorHelper.getFieldValue(
			editableValuesJSONObject, new HashMap<>(),
			fragmentEntryProcessorContext);
	}

	private void _pushServiceContext(Layout layout, ThemeDisplay themeDisplay)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, layout);

		themeDisplay.setRequest(mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		serviceContext.setRequest(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);
	}

	private String _readFileToString(String fileName) throws Exception {
		Class<?> clazz = getClass();

		return StringUtil.read(
			clazz.getClassLoader(),
			"com/liferay/fragment/entry/processor/internal/util/test" +
				"/dependencies/" + fileName);
	}

	private String _readJSONFileToString(String jsonFileName) throws Exception {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			_readFileToString(jsonFileName));

		return jsonObject.toString();
	}

	private void _testGetInfoItemFieldMapped(
			JSONObject editableValuesJSONObject, long groupId,
			InfoItemFieldMapped infoItemFieldMapped)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, _layout);

		_themeDisplay.setScopeGroupId(groupId);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		serviceContext.setRequest(mockHttpServletRequest);

		try {
			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			FragmentEntryProcessorContext fragmentEntryProcessorContext =
				new DefaultFragmentEntryProcessorContext(
					mockHttpServletRequest, new MockHttpServletResponse(),
					FragmentEntryLinkConstants.EDIT, LocaleUtil.US);

			InfoItemFieldMapped actualInfoItemFieldMapped =
				_fragmentEntryProcessorHelper.getInfoItemFieldMapped(
					editableValuesJSONObject, fragmentEntryProcessorContext);

			Assert.assertEquals(
				infoItemFieldMapped.getFieldName(),
				actualInfoItemFieldMapped.getFieldName());
			Assert.assertEquals(
				infoItemFieldMapped.getInfoItemIdentifier(),
				actualInfoItemFieldMapped.getInfoItemIdentifier());
			Assert.assertEquals(
				infoItemFieldMapped.getObject(),
				actualInfoItemFieldMapped.getObject());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private void _testHasViewPermission(
			JSONObject editableValueJSONObject, boolean expected)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, _layout);

		User user = _userLocalService.getGuestUser(
			TestPropsValues.getCompanyId());

		_themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		_themeDisplay.setRealUser(user);
		_themeDisplay.setUser(user);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		FragmentEntryProcessorContext fragmentEntryProcessorContext =
			new DefaultFragmentEntryProcessorContext(
				mockHttpServletRequest, new MockHttpServletResponse(),
				FragmentEntryLinkConstants.EDIT,
				_portal.getSiteDefaultLocale(_group));

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					CompanyConstants.SYSTEM);
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.feature.flag.web.internal.feature.flag." +
					"FeatureFlagsBag",
				LoggerTestUtil.ERROR)) {

			Assert.assertEquals(
				expected,
				_fragmentEntryProcessorHelper.hasViewPermission(
					editableValueJSONObject, fragmentEntryProcessorContext));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.isEmpty());
		}
	}

	private String _toJSON(FileEntry fileEntry) {
		return JSONUtil.put(
			"alt", StringPool.BLANK
		).put(
			"description", StringPool.BLANK
		).put(
			"fileEntryId", fileEntry.getFileEntryId()
		).put(
			"groupId", fileEntry.getGroupId()
		).put(
			"name", fileEntry.getFileName()
		).put(
			"title", fileEntry.getTitle()
		).put(
			"type", "journal"
		).put(
			"uuid", fileEntry.getUuid()
		).toString();
	}

	@Inject
	private static JournalArticleLocalService _journalArticleLocalService;

	@Inject(filter = "ddm.form.deserializer.type=json")
	private static DDMFormDeserializer _jsonDDMFormDeserializer;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	private AssetVocabulary _assetVocabulary;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Inject
	private DDMFormValuesToFieldsConverter _ddmFormValuesToFieldsConverter;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private FragmentEntryProcessorHelper _fragmentEntryProcessorHelper;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalConverter _journalConverter;

	private Layout _layout;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private Portal _portal;

	private ThemeDisplay _themeDisplay;

	@Inject
	private UserLocalService _userLocalService;

}