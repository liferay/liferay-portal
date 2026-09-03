/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.comparison;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.service.ObjectEntryVersionService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.FastDateFormatFactoryImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class ObjectEntryVersionFieldValueResolverTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			new FastDateFormatFactoryImpl());

		Mockito.when(
			_language.isAvailableLocale(Mockito.any(Locale.class))
		).thenReturn(
			true
		);

		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(_language);

		_objectEntryVersionFieldValueResolver =
			new ObjectEntryVersionFieldValueResolver(
				_dlAppLocalService, _dlFileEntryLocalService, _dlURLHelper,
				_language, _listTypeEntryLocalService,
				_objectEntryVersionService);
	}

	@Test
	public void testGetFieldValues() throws Exception {
		_testGetFieldValuesWithoutTranslation();
		_testGetFieldValuesWithTranslation();
	}

	@Test
	public void testIsDateBusinessType() {
		Assert.assertTrue(
			_objectEntryVersionFieldValueResolver.isDateBusinessType(
				_mockObjectField(ObjectFieldConstants.BUSINESS_TYPE_DATE)));
		Assert.assertTrue(
			_objectEntryVersionFieldValueResolver.isDateBusinessType(
				_mockObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME)));
		Assert.assertFalse(
			_objectEntryVersionFieldValueResolver.isDateBusinessType(
				_mockObjectField(ObjectFieldConstants.BUSINESS_TYPE_TEXT)));
		Assert.assertFalse(
			_objectEntryVersionFieldValueResolver.isDateBusinessType(null));
	}

	@Test
	public void testToDisplayValue() throws Exception {
		_testToDisplayValueWithAttachmentObjectField();
		_testToDisplayValueWithBooleanObjectField();
		_testToDisplayValueWithDateObjectField();
		_testToDisplayValueWithDateTimeObjectField();
		_testToDisplayValueWithHTMLFileName();
		_testToDisplayValueWithHTMLListTypeEntryName();
		_testToDisplayValueWithHTMLTextValue();
		_testToDisplayValueWithMultiselectPicklistObjectField();
		_testToDisplayValueWithNonexistentFileEntry();
		_testToDisplayValueWithNullValue();
		_testToDisplayValueWithPicklistObjectField();
		_testToDisplayValueWithRichTextObjectField();
	}

	private void _assertNullDisplayValue(String businessType) {
		Assert.assertEquals(
			StringPool.BLANK,
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID, _mockObjectField(businessType), null, null));
	}

	private ObjectField _mockObjectField(String businessType) {
		return _mockObjectField(businessType, null);
	}

	private ObjectField _mockObjectField(
		String businessType, String timeStorage) {

		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.getBusinessType()
		).thenReturn(
			businessType
		);

		List<ObjectFieldSetting> objectFieldSettings = Collections.emptyList();

		if (timeStorage != null) {
			ObjectFieldSetting objectFieldSetting = Mockito.mock(
				ObjectFieldSetting.class);

			Mockito.when(
				objectFieldSetting.getName()
			).thenReturn(
				ObjectFieldSettingConstants.NAME_TIME_STORAGE
			);

			Mockito.when(
				objectFieldSetting.getValue()
			).thenReturn(
				timeStorage
			);

			objectFieldSettings = Collections.singletonList(objectFieldSetting);
		}

		Mockito.when(
			objectField.getObjectFieldSettings()
		).thenReturn(
			objectFieldSettings
		);

		return objectField;
	}

	private void _setUpAttachment(
			long fileEntryId, String fileName, String previewURL)
		throws Exception {

		DLFileEntry dlFileEntry = Mockito.mock(DLFileEntry.class);

		Mockito.when(
			dlFileEntry.getFileName()
		).thenReturn(
			fileName
		);

		Mockito.when(
			_dlFileEntryLocalService.fetchDLFileEntry(fileEntryId)
		).thenReturn(
			dlFileEntry
		);

		FileEntry fileEntry = Mockito.mock(FileEntry.class);
		FileVersion fileVersion = Mockito.mock(FileVersion.class);

		Mockito.when(
			fileEntry.getFileVersion()
		).thenReturn(
			fileVersion
		);

		Mockito.when(
			_dlAppLocalService.getFileEntry(fileEntryId)
		).thenReturn(
			fileEntry
		);

		Mockito.when(
			_dlURLHelper.getPreviewURL(
				fileEntry, fileVersion, null, StringPool.BLANK)
		).thenReturn(
			previewURL
		);
	}

	private void _setUpBooleanLabels() {
		Mockito.when(
			_language.get(Mockito.any(Locale.class), Mockito.eq("no"))
		).thenReturn(
			"No"
		);

		Mockito.when(
			_language.get(Mockito.any(Locale.class), Mockito.eq("yes"))
		).thenReturn(
			"Yes"
		);
	}

	private void _setUpListTypeEntry(
		long listTypeDefinitionId, String key, String name) {

		ListTypeEntry listTypeEntry = Mockito.mock(ListTypeEntry.class);

		Mockito.when(
			listTypeEntry.getName(_LANGUAGE_ID)
		).thenReturn(
			name
		);

		Mockito.when(
			_listTypeEntryLocalService.fetchListTypeEntry(
				listTypeDefinitionId, key)
		).thenReturn(
			listTypeEntry
		);
	}

	private void _setUpObjectEntryVersion(
			long objectEntryId, String content, int version)
		throws Exception {

		ObjectEntryVersion objectEntryVersion = Mockito.mock(
			ObjectEntryVersion.class);

		Mockito.when(
			objectEntryVersion.getContent()
		).thenReturn(
			content
		);

		Mockito.when(
			_objectEntryVersionService.getObjectEntryVersion(
				objectEntryId, version)
		).thenReturn(
			objectEntryVersion
		);
	}

	private void _testGetFieldValuesWithoutTranslation() throws Exception {
		long objectEntryId = RandomTestUtil.randomLong();
		int version = RandomTestUtil.randomInt();

		_setUpObjectEntryVersion(
			objectEntryId,
			JSONUtil.put(
				"friendlyUrlPath", "hello-world"
			).put(
				"friendlyUrlPath_i18n", JSONUtil.put("en_US", "hello-world")
			).put(
				"properties",
				JSONUtil.put(
					"title", "Hello"
				).put(
					"title_i18n", JSONUtil.put("en_US", "Hello")
				)
			).toString(),
			version);

		Map<String, Object> fieldValues =
			_objectEntryVersionFieldValueResolver.getFieldValues(
				"es_ES", objectEntryId, version);

		Assert.assertEquals(fieldValues.toString(), 2, fieldValues.size());
		Assert.assertTrue(
			fieldValues.toString(), fieldValues.containsKey("title"));
		Assert.assertNull(fieldValues.get("title"));

		Assert.assertEquals(
			"hello-world", fieldValues.get("objectEntryFriendlyURL"));
	}

	private void _testGetFieldValuesWithTranslation() throws Exception {
		long objectEntryId = RandomTestUtil.randomLong();
		int version = RandomTestUtil.randomInt();

		_setUpObjectEntryVersion(
			objectEntryId,
			JSONUtil.put(
				"friendlyUrlPath", "hello-world"
			).put(
				"friendlyUrlPath_i18n",
				JSONUtil.put(
					"en_US", "hello-world"
				).put(
					"es_ES", "hola-mundo"
				)
			).put(
				"properties",
				JSONUtil.put(
					"content", "<p>Hello</p>"
				).put(
					"contentRawText", "Hello"
				).put(
					"title", "Hello"
				).put(
					"title_i18n",
					JSONUtil.put(
						"en_US", "Hello"
					).put(
						"es_ES", "Hola"
					)
				)
			).toString(),
			version);

		Map<String, Object> fieldValues =
			_objectEntryVersionFieldValueResolver.getFieldValues(
				"es_ES", objectEntryId, version);

		Assert.assertEquals(fieldValues.toString(), 3, fieldValues.size());
		Assert.assertEquals("<p>Hello</p>", fieldValues.get("content"));
		Assert.assertEquals(
			"hola-mundo", fieldValues.get("objectEntryFriendlyURL"));
		Assert.assertEquals("Hola", fieldValues.get("title"));
	}

	private void _testToDisplayValueWithAttachmentObjectField()
		throws Exception {

		long fileEntryId = RandomTestUtil.randomLong();
		String fileName = RandomTestUtil.randomString();
		String previewURL = RandomTestUtil.randomString();

		_setUpAttachment(fileEntryId, fileName, previewURL);

		ObjectField objectField = _mockObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT);

		String expectedDisplayValue = StringBundler.concat(
			"<img alt=\"", fileName,
			"\" class=\"cms-compare-versions-attachment\" src=\"", previewURL,
			"\" /> ", fileName);

		Assert.assertEquals(
			expectedDisplayValue,
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID, objectField, null,
				HashMapBuilder.<String, Object>put(
					"id", fileEntryId
				).build()));
		Assert.assertEquals(
			expectedDisplayValue,
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID, objectField, null, fileEntryId));
	}

	private void _testToDisplayValueWithBooleanObjectField() {
		_setUpBooleanLabels();

		ObjectField objectField = _mockObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN);

		Assert.assertEquals(
			"No",
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID, objectField, null, false));
		Assert.assertEquals(
			"No",
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID, objectField, null, null));
		Assert.assertEquals(
			"Yes",
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID, objectField, null, true));
	}

	private void _testToDisplayValueWithDateObjectField() {
		Assert.assertEquals(
			"09/15/2026",
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID,
				_mockObjectField(ObjectFieldConstants.BUSINESS_TYPE_DATE), null,
				_DATE_VALUE));
		Assert.assertEquals(
			"15.09.2026",
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				"de_DE",
				_mockObjectField(ObjectFieldConstants.BUSINESS_TYPE_DATE), null,
				_DATE_VALUE));
	}

	private void _testToDisplayValueWithDateTimeObjectField() {
		ObjectField convertToUTCObjectField = _mockObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME,
			ObjectFieldSettingConstants.VALUE_CONVERT_TO_UTC);
		ObjectField useInputAsEnteredObjectField = _mockObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME,
			ObjectFieldSettingConstants.VALUE_USE_INPUT_AS_ENTERED);

		User user = Mockito.mock(User.class);

		Mockito.when(
			user.getTimeZoneId()
		).thenReturn(
			"America/New_York"
		);

		String convertToUTCDisplayValue =
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID, convertToUTCObjectField, user,
				"2026-09-15T12:00:00.000Z");

		Assert.assertTrue(
			convertToUTCDisplayValue.startsWith("09/15/2026, 08:00"));

		String useInputAsEnteredDisplayValue =
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID, useInputAsEnteredObjectField, user,
				"2026-09-15T12:00:00.000");

		Assert.assertTrue(
			useInputAsEnteredDisplayValue.startsWith("09/15/2026, 12:00"));
	}

	private void _testToDisplayValueWithHTMLFileName() throws Exception {
		long fileEntryId = RandomTestUtil.randomLong();
		String previewURL = RandomTestUtil.randomString();

		_setUpAttachment(
			fileEntryId, "\"><img src=x onerror=alert(1)>", previewURL);

		String escapedFileName = "&#34;&gt;&lt;img src=x onerror=alert(1)&gt;";

		Assert.assertEquals(
			StringBundler.concat(
				"<img alt=\"", escapedFileName,
				"\" class=\"cms-compare-versions-attachment\" src=\"",
				previewURL, "\" /> ", escapedFileName),
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID,
				_mockObjectField(ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT),
				null, fileEntryId));
	}

	private void _testToDisplayValueWithHTMLListTypeEntryName() {
		long listTypeDefinitionId = RandomTestUtil.randomLong();
		String key = RandomTestUtil.randomString();

		ObjectField objectField = _mockObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_PICKLIST);

		Mockito.when(
			objectField.getListTypeDefinitionId()
		).thenReturn(
			listTypeDefinitionId
		);

		_setUpListTypeEntry(
			listTypeDefinitionId, key, "<img src=x onerror=alert(1)>");

		Assert.assertEquals(
			"&lt;img src=x onerror=alert(1)&gt;",
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID, objectField, null, key));
	}

	private void _testToDisplayValueWithHTMLTextValue() {
		Assert.assertEquals(
			"&lt;img src=x onerror=alert(1)&gt;",
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID,
				_mockObjectField(ObjectFieldConstants.BUSINESS_TYPE_TEXT), null,
				"<img src=x onerror=alert(1)>"));
	}

	private void _testToDisplayValueWithMultiselectPicklistObjectField() {
		long listTypeDefinitionId = RandomTestUtil.randomLong();

		ObjectField objectField = _mockObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST);

		Mockito.when(
			objectField.getListTypeDefinitionId()
		).thenReturn(
			listTypeDefinitionId
		);

		String firstKey = RandomTestUtil.randomString();

		_setUpListTypeEntry(listTypeDefinitionId, firstKey, "First");

		String secondKey = RandomTestUtil.randomString();

		_setUpListTypeEntry(listTypeDefinitionId, secondKey, "Second");

		Assert.assertEquals(
			"First",
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID, objectField, null, firstKey));
		Assert.assertEquals(
			"First, Second",
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID, objectField, null,
				new Object[] {firstKey, secondKey}));
		Assert.assertEquals(
			"First, Second",
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID, objectField, null,
				Arrays.asList(firstKey, secondKey)));
		Assert.assertEquals(
			StringPool.BLANK,
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID, objectField, null, new Object[0]));
	}

	private void _testToDisplayValueWithNonexistentFileEntry() {
		long fileEntryId = RandomTestUtil.randomLong();

		Mockito.when(
			_dlFileEntryLocalService.fetchDLFileEntry(fileEntryId)
		).thenReturn(
			null
		);

		ObjectField objectField = _mockObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT);

		Assert.assertEquals(
			StringPool.BLANK,
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID, objectField, null, Collections.emptyMap()));
		Assert.assertEquals(
			StringPool.BLANK,
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID, objectField, null, fileEntryId));
	}

	private void _testToDisplayValueWithNullValue() {
		_setUpBooleanLabels();

		_assertNullDisplayValue(ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT);
		_assertNullDisplayValue(ObjectFieldConstants.BUSINESS_TYPE_DATE);
		_assertNullDisplayValue(ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME);
		_assertNullDisplayValue(
			ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST);
		_assertNullDisplayValue(ObjectFieldConstants.BUSINESS_TYPE_PICKLIST);
		_assertNullDisplayValue(ObjectFieldConstants.BUSINESS_TYPE_TEXT);

		Assert.assertEquals(
			StringPool.BLANK,
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID, null, null, null));
	}

	private void _testToDisplayValueWithPicklistObjectField() {
		long listTypeDefinitionId = RandomTestUtil.randomLong();
		String key = RandomTestUtil.randomString();

		ObjectField objectField = _mockObjectField(
			ObjectFieldConstants.BUSINESS_TYPE_PICKLIST);

		Mockito.when(
			objectField.getListTypeDefinitionId()
		).thenReturn(
			listTypeDefinitionId
		);

		_setUpListTypeEntry(listTypeDefinitionId, key, "Label");

		Assert.assertEquals(
			"Label",
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID, objectField, null, key));
		Assert.assertEquals(
			"Label",
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID, objectField, null,
				HashMapBuilder.<String, Object>put(
					"key", key
				).build()));

		String unknownKey = RandomTestUtil.randomString();

		Assert.assertEquals(
			unknownKey,
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID, objectField, null, unknownKey));
	}

	private void _testToDisplayValueWithRichTextObjectField() {
		String richText = "<p>Hello <b>World</b></p>";

		Assert.assertEquals(
			richText,
			_objectEntryVersionFieldValueResolver.toDisplayValue(
				_LANGUAGE_ID,
				_mockObjectField(ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT),
				null, richText));
	}

	private static final String _DATE_VALUE = "2026-09-15T00:00:00.000Z";

	private static final String _LANGUAGE_ID = "en_US";

	private final DLAppLocalService _dlAppLocalService = Mockito.mock(
		DLAppLocalService.class);
	private final DLFileEntryLocalService _dlFileEntryLocalService =
		Mockito.mock(DLFileEntryLocalService.class);
	private final DLURLHelper _dlURLHelper = Mockito.mock(DLURLHelper.class);
	private final Language _language = Mockito.mock(Language.class);
	private final ListTypeEntryLocalService _listTypeEntryLocalService =
		Mockito.mock(ListTypeEntryLocalService.class);
	private ObjectEntryVersionFieldValueResolver
		_objectEntryVersionFieldValueResolver;
	private final ObjectEntryVersionService _objectEntryVersionService =
		Mockito.mock(ObjectEntryVersionService.class);

}