/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.builder.ObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Bryan Engler
 */
@RunWith(Arquillian.class)
public class ObjectEntryLocalServiceSearchObjectEntriesTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Test
	public void testAttachment() throws Exception {
		ObjectFieldBuilder attachmentObjectFieldBuilder =
			new AttachmentObjectFieldBuilder();

		_testAttachment(
			attachmentObjectFieldBuilder.businessType(
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT
			).dbType(
				ObjectFieldConstants.DB_TYPE_LONG
			).indexed(
				true
			).labelMap(
				LocalizedMapUtil.getLocalizedMap("Alpha")
			).name(
				"alpha"
			).objectFieldSettings(
				Arrays.asList(
					_createObjectFieldSetting("acceptedFileExtensions", "txt"),
					_createObjectFieldSetting("fileSource", "userComputer"),
					_createObjectFieldSetting("maximumFileSize", "100"))
			).build());
		_testAttachment(
			attachmentObjectFieldBuilder.indexedAsKeyword(
				true
			).build());
		_testAttachment(
			attachmentObjectFieldBuilder.indexed(
				false
			).build());
	}

	@Test
	public void testBigDecimal() throws Exception {
		_addObjectDefinition(
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_PRECISION_DECIMAL,
				ObjectFieldConstants.DB_TYPE_BIG_DECIMAL, true, false, null,
				"Alpha", "alpha", false));

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", new BigDecimal("45.25")
			).build());
		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", new BigDecimal("54")
			).build());

		_assertKeywords("[44 TO 46]", 1);
		_assertKeywords("[44.9999 TO 45.3333]", 1);
		_assertKeywords("4", 0);
		_assertKeywords("45", 0);
		_assertKeywords("45.25", 1);
		_assertKeywords("45.2500", 1);
		_assertKeywords("45.2501", 0);
		_assertKeywords("bravo 4 charlie", 0);
		_assertKeywords("bravo 45 charlie", 0);
		_assertKeywords("bravo 45.25 charlie", 1);
		_assertKeywords("search from [ 44 TO 46 ]", 1);
	}

	@Test
	public void testBigDecimalKeyword() throws Exception {
		_addObjectDefinition(
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_PRECISION_DECIMAL,
				ObjectFieldConstants.DB_TYPE_BIG_DECIMAL, true, true, null,
				"Alpha", "alpha", false));

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", new BigDecimal("45.25")
			).build());
		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", new BigDecimal("54")
			).build());

		_assertKeywords("[44 TO 46]", 0);
		_assertKeywords("[44.9999 TO 45.1111]", 0);
		_assertKeywords("4", 1);
		_assertKeywords("45", 1);
		_assertKeywords("45.25", 1);
		_assertKeywords("45.2500", 0);
		_assertKeywords("45.2501", 0);
		_assertKeywords("bravo 4 charlie", 1);
		_assertKeywords("bravo 45 charlie", 1);
		_assertKeywords("bravo 45.25 charlie", 1);
		_assertKeywords("search from [ 44 TO 46 ]", 0);
	}

	@Test
	public void testBoolean() throws Exception {
		_addObjectDefinition(
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN,
				ObjectFieldConstants.DB_TYPE_BOOLEAN, true, false, null,
				"Alpha", "alpha", false));

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", true
			).build());
		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", false
			).build());

		_assertKeywords("0", 0);
		_assertKeywords("1", 0);
		_assertKeywords("false", 1);
		_assertKeywords("no", 1);
		_assertKeywords("the false statement is not", 1);
		_assertKeywords("the false statement is not true", 2);
		_assertKeywords("Tr", 0);
		_assertKeywords("TRUE", 1);
		_assertKeywords("True", 1);
		_assertKeywords("true", 1);
		_assertKeywords("yes", 1);
	}

	@Test
	public void testBooleanKeyword() throws Exception {
		_addObjectDefinition(
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN,
				ObjectFieldConstants.DB_TYPE_BOOLEAN, true, true, null, "Alpha",
				"alpha", false));

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", true
			).build());
		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", false
			).build());

		_assertKeywords("0", 0);
		_assertKeywords("1", 0);
		_assertKeywords("false", 1);
		_assertKeywords("no", 0);
		_assertKeywords("the false statement is not", 1);
		_assertKeywords("the false statement is not true", 2);
		_assertKeywords("Tr", 1);
		_assertKeywords("TRUE", 1);
		_assertKeywords("True", 1);
		_assertKeywords("true", 1);
		_assertKeywords("yes", 0);
	}

	@Test
	public void testClob() throws Exception {
		_testCharacterDataType(
			ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT,
			ObjectFieldConstants.DB_TYPE_CLOB, false, false, null);
		_testCharacterDataType(
			ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT,
			ObjectFieldConstants.DB_TYPE_CLOB, true, false, "en_US");
		_testCharacterDataType(
			ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT,
			ObjectFieldConstants.DB_TYPE_CLOB, true, false, null);
		_testCharacterDataType(
			ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT,
			ObjectFieldConstants.DB_TYPE_CLOB, true, true, null);
	}

	@Test
	public void testDate() throws Exception {
		_addObjectDefinition(
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_DATE,
				ObjectFieldConstants.DB_TYPE_DATE, true, false, null, "Alpha",
				"alpha", false));

		long date = 1632268800000L;

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", new Date(date)
			).build());
		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", new Date(date + (Time.DAY * 1))
			).build());

		_assertKeywords("[ 2020 TO 2022 ]", 0);
		_assertKeywords("[ 2021 TO 2021 ]", 0);
		_assertKeywords("[2020 TO 2022]", 0);
		_assertKeywords("[2021 TO 2021]", 0);
		_assertKeywords("[20210921000000 TO 20210922000000]", 1);
		_assertKeywords("[20210921000000 TO 20210923000000]", 2);
		_assertKeywords("09", 0);
		_assertKeywords("1632335654272", 0);
		_assertKeywords("18", 0);
		_assertKeywords("2021", 0);
		_assertKeywords("2021-09", 0);
		_assertKeywords("2021-09-22", 0);
		_assertKeywords("2021-09-22 18:34:14.272", 0);
		_assertKeywords("20210922000000", 1);
	}

	@Test
	public void testDateKeyword() throws Exception {
		_addObjectDefinition(
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_DATE,
				ObjectFieldConstants.DB_TYPE_DATE, true, true, null, "Alpha",
				"alpha", false));

		long date = 1632335654272L;

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", new Date(date)
			).build());
		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", new Date(date + (Time.DAY * 1))
			).build());

		_assertKeywords("[ 2020 TO 2022 ]", 0);
		_assertKeywords("[ 2021 TO 2021 ]", 0);
		_assertKeywords("[2020 TO 2022]", 0);
		_assertKeywords("[2021 TO 2021]", 0);
		_assertKeywords("[20210922183413 TO 20210922183415]", 0);
		_assertKeywords("[20210922183413 TO 20210923183415]", 0);
		_assertKeywords("09", 0);
		_assertKeywords("1632335654272", 0);
		_assertKeywords("18", 0);
		_assertKeywords("2021", 2);
		_assertKeywords("2021-09", 2);
		_assertKeywords("2021-09-22", 1);
		_assertKeywords("2021-09-22 18:34:14.272", 1);
		_assertKeywords("20210922183414", 0);
	}

	@Test
	public void testDouble() throws Exception {
		_addObjectDefinition(
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_DECIMAL,
				ObjectFieldConstants.DB_TYPE_DOUBLE, true, false, null, "Alpha",
				"alpha", false));

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", 45D
			).build());
		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", 54D
			).build());

		_assertKeywords("[44 TO 46]", 1);
		_assertKeywords("[44.9999 TO 45.1111]", 1);
		_assertKeywords("4", 0);
		_assertKeywords("45", 1);
		_assertKeywords("45.0", 1);
		_assertKeywords("45.0000", 1);
		_assertKeywords("45.0001", 0);
		_assertKeywords("bravo 4 charlie", 0);
		_assertKeywords("bravo 45 charlie", 1);
		_assertKeywords("bravo 45.0 charlie", 1);
		_assertKeywords("search from [ 44 TO 46 ]", 1);
	}

	@Test
	public void testDoubleKeyword() throws Exception {
		_addObjectDefinition(
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_DECIMAL,
				ObjectFieldConstants.DB_TYPE_DOUBLE, true, true, null, "Alpha",
				"alpha", false));

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", 45D
			).build());
		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", 54D
			).build());

		_assertKeywords("[44 TO 46]", 0);
		_assertKeywords("[44.9999 TO 45.1111]", 0);
		_assertKeywords("4", 1);
		_assertKeywords("45", 1);
		_assertKeywords("45.0", 1);
		_assertKeywords("45.0000", 0);
		_assertKeywords("45.0001", 0);
		_assertKeywords("bravo 4 charlie", 1);
		_assertKeywords("bravo 45 charlie", 1);
		_assertKeywords("bravo 45.0 charlie", 1);
		_assertKeywords("search from [ 44 TO 46 ]", 0);
	}

	@Test
	public void testInteger() throws Exception {
		_addObjectDefinition(
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
				ObjectFieldConstants.DB_TYPE_INTEGER, true, false, null,
				"Alpha", "alpha", false));

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", 45
			).build());
		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", 54
			).build());

		_assertKeywords("[44 TO 46]", 1);
		_assertKeywords("[44.9 TO 45.1]", 0);
		_assertKeywords("4", 0);
		_assertKeywords("45", 1);
		_assertKeywords("45.0", 0);
		_assertKeywords("bravo 4 charlie", 0);
		_assertKeywords("bravo 45 charlie", 1);
		_assertKeywords("bravo 45.0 charlie", 0);
		_assertKeywords("search from [ 44 TO 46 ]", 1);
	}

	@Test
	public void testIntegerKeyword() throws Exception {
		_addObjectDefinition(
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
				ObjectFieldConstants.DB_TYPE_INTEGER, true, true, null, "Alpha",
				"alpha", false));

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", 45
			).build());
		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", 54
			).build());

		_assertKeywords("[44 TO 46]", 0);
		_assertKeywords("[44.9 TO 45.1]", 0);
		_assertKeywords("4", 1);
		_assertKeywords("45", 1);
		_assertKeywords("45.0", 0);
		_assertKeywords("bravo 4 charlie", 1);
		_assertKeywords("bravo 45 charlie", 1);
		_assertKeywords("bravo 45.0 charlie", 0);
		_assertKeywords("search from [ 44 TO 46 ]", 0);
	}

	@Test
	public void testLong() throws Exception {
		_addObjectDefinition(
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER,
				ObjectFieldConstants.DB_TYPE_LONG, true, false, null, "Alpha",
				"alpha", false));

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", 45L
			).build());
		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", 54L
			).build());

		_assertKeywords("[44 TO 46]", 1);
		_assertKeywords("4", 0);
		_assertKeywords("45", 1);
		_assertKeywords("bravo 4 charlie", 0);
		_assertKeywords("bravo 45 charlie", 1);
		_assertKeywords("search from [ 44 TO 46 ]", 1);
	}

	@Test
	public void testLongKeyword() throws Exception {
		_addObjectDefinition(
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER,
				ObjectFieldConstants.DB_TYPE_LONG, true, true, null, "Alpha",
				"alpha", false));

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", 45L
			).build());
		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", 54L
			).build());

		_assertKeywords("[44 TO 46]", 0);
		_assertKeywords("4", 1);
		_assertKeywords("45", 1);
		_assertKeywords("bravo 4 charlie", 1);
		_assertKeywords("bravo 45 charlie", 1);
		_assertKeywords("search from [ 44 TO 46 ]", 0);
	}

	@Test
	public void testSearchByTitleValue() throws Exception {
		_addObjectDefinition(
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_TEXT,
				ObjectFieldConstants.DB_TYPE_STRING, false, false, null,
				"Alpha", "alpha", false));

		ObjectEntry objectEntry = _addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", RandomTestUtil.randomString()
			).put(
				"beta", RandomTestUtil.randomString()
			).build());

		String titleValue = objectEntry.getTitleValue();

		Assert.assertTrue(Validator.isNotNull(titleValue));

		int index = RandomTestUtil.randomInt(0, titleValue.length());

		_assertKeywords(titleValue.substring(0, index), 1);
	}

	@Test
	public void testSearchWithDoubleQuotes() throws Exception {
		_addObjectDefinition(
			ObjectFieldUtil.createObjectField(
				ObjectFieldConstants.BUSINESS_TYPE_TEXT,
				ObjectFieldConstants.DB_TYPE_STRING, true, false, null, "Alpha",
				"alpha", false));

		String string1 = RandomTestUtil.randomString();
		String string2 = RandomTestUtil.randomString();

		String text = string1 + StringPool.SPACE + string2;

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", string1
			).build());
		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", string1 + RandomTestUtil.randomString()
			).build());

		_assertKeywords(StringPool.QUOTE + string1 + StringPool.QUOTE, 1);

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", string2
			).build());
		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", string2 + RandomTestUtil.randomString()
			).build());

		_assertKeywords(StringPool.QUOTE + string2 + StringPool.QUOTE, 1);

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", text
			).build());

		_assertKeywords(StringPool.QUOTE + text + StringPool.QUOTE, 1);
	}

	@Test
	public void testString() throws Exception {
		_testCharacterDataType(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, false, false, null);
		_testCharacterDataType(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, true, false, "en_US");
		_testCharacterDataType(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, true, false, null);
		_testCharacterDataType(
			ObjectFieldConstants.BUSINESS_TYPE_TEXT,
			ObjectFieldConstants.DB_TYPE_STRING, true, true, null);
	}

	private void _addObjectDefinition(ObjectField objectField)
		throws Exception {

		_objectDefinition = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			false, Arrays.asList(objectField));

		_objectDefinition.setTitleObjectFieldId(_getTitleObjectFieldId());

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);

		_objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				_objectDefinition.getObjectDefinitionId());
	}

	private ObjectEntry _addObjectEntry(Map<String, Serializable> values)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, values, ServiceContextTestUtil.getServiceContext());
	}

	private void _assertKeywords(String keywords, int count) throws Exception {
		BaseModelSearchResult<ObjectEntry> baseModelSearchResult =
			_objectEntryLocalService.searchObjectEntries(
				0, _objectDefinition.getObjectDefinitionId(), keywords, 0, 20);

		Assert.assertEquals(count, baseModelSearchResult.getLength());
	}

	private ObjectFieldSetting _createObjectFieldSetting(
		String name, String value) {

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.createObjectFieldSetting(0L);

		objectFieldSetting.setName(name);
		objectFieldSetting.setValue(value);

		return objectFieldSetting;
	}

	private long _getTitleObjectFieldId() throws Exception {
		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).indexed(
				true
			).indexedAsKeyword(
				true
			).labelMap(
				LocalizedMapUtil.getLocalizedMap("Beta")
			).name(
				"beta"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).build());

		return objectField.getObjectFieldId();
	}

	private void _testAttachment(ObjectField objectField) throws Exception {
		if (_objectDefinition != null) {
			_objectDefinitionLocalService.deleteObjectDefinition(
				_objectDefinition);
		}

		_addObjectDefinition(objectField);

		FileEntry fileEntry = TempFileEntryUtil.addTempFileEntry(
			TestPropsValues.getGroupId(), TestPropsValues.getUserId(),
			_objectDefinition.getPortletId(),
			TempFileEntryUtil.getTempFileName("document.txt"),
			FileUtil.createTempFile(RandomTestUtil.randomBytes()),
			ContentTypes.TEXT_PLAIN);

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", fileEntry.getFileEntryId()
			).build());

		if (objectField.isIndexed()) {
			_assertKeywords(
				"DOCUMENT.TX", objectField.isIndexedAsKeyword() ? 1 : 0);
			_assertKeywords("DOCUMENT.TXT", 1);
			_assertKeywords(
				"document.tx", objectField.isIndexedAsKeyword() ? 1 : 0);
			_assertKeywords("document.txt", 1);
			_assertKeywords("ocument.txt", 0);
		}
		else {
			_assertKeywords("document.txt", 0);
		}
	}

	private void _testCharacterDataType(
			String businessType, String dbType, boolean indexed,
			boolean indexedAsKeyword, String indexedLanguageId)
		throws Exception {

		if (_objectDefinition != null) {
			_objectDefinitionLocalService.deleteObjectDefinition(
				_objectDefinition);
		}

		_addObjectDefinition(
			ObjectFieldUtil.createObjectField(
				businessType, dbType, indexed, indexedAsKeyword,
				indexedLanguageId, "Alpha", "alpha", false));

		String text = "The quick brown fox jumps over the lazy dog";

		if (Objects.equals(
				businessType, ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT)) {

			text = StringBundler.concat("<strong>", text, "</strong>");
		}

		_addObjectEntry(
			HashMapBuilder.<String, Serializable>put(
				"alpha", text
			).build());

		if (indexedAsKeyword) {
			_assertKeywords("quick", 0);
			_assertKeywords("The quick", 1);
			_assertKeywords("THE QUICK", 1);
			_assertKeywords("the quick", 1);
			_assertKeywords("They", 0);
		}
		else if (indexed) {
			_assertKeywords("fox", 1);
			_assertKeywords("jump", indexedAsKeyword ? 0 : 1);
			_assertKeywords("jumps", 1);
			_assertKeywords("LAZY dog", 1);
			_assertKeywords("lazy dog", 1);
			_assertKeywords("strong", 0);
		}
		else {
			_assertKeywords("fox", 0);
			_assertKeywords("LAZY dog", 0);
			_assertKeywords("lazy dog", 0);
		}
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

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

}