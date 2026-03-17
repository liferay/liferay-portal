/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMStructureLayoutLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.Field;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestHelper;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.util.JournalConverter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.InputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Bruno Basto
 * @author Marcellus Tavares
 */
@RunWith(Arquillian.class)
public class JournalConverterImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_enLocale = LocaleUtil.fromLanguageId("en_US");
		_ptLocale = LocaleUtil.fromLanguageId("pt_BR");

		_group = GroupTestUtil.addGroup();

		_ddmStructureTestHelper = new DDMStructureTestHelper(
			PortalUtil.getClassNameId(JournalArticle.class), _group);

		String definition = read("test-ddm-structure-all-fields.xml");

		DDMForm ddmForm = deserialize(definition);

		_ddmStructure = _ddmStructureTestHelper.addStructure(
			PortalUtil.getClassNameId(JournalArticle.class), null,
			"Test Structure", ddmForm, StorageType.DEFAULT.getValue(),
			DDMStructureConstants.TYPE_DEFAULT);
	}

	@Test
	public void testGetContentFromListField() throws Exception {
		Fields fields = new Fields();

		_addField(
			_ddmStructure.getStructureId(), null, fields, "list",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList("[\"value 01\"]")
			).build());

		fields.put(
			getFieldsDisplayField(
				_ddmStructure.getStructureId(), "list_INSTANCE_pcm9WPVX"));

		String expectedContent = read("test-journal-content-list-field.xml");

		String actualContent = _journalConverter.getContent(
			_ddmStructure, fields, _ddmStructure.getGroupId());

		assertEquals(expectedContent, actualContent);
	}

	@Test
	public void testGetContentFromMultiListField() throws Exception {
		Fields fields = new Fields();

		_addField(
			_ddmStructure.getStructureId(), null, fields, "multi_list",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale,
				Collections.singletonList("[\"value 01\",\"value 02\"]")
			).build());

		fields.put(
			getFieldsDisplayField(
				_ddmStructure.getStructureId(),
				"multi_list_INSTANCE_9X5wVsSv"));

		String expectedContent = read(
			"test-journal-content-multi-list-field.xml");

		String actualContent = _journalConverter.getContent(
			_ddmStructure, fields, _ddmStructure.getGroupId());

		assertEquals(expectedContent, actualContent);
	}

	@Test
	public void testGetContentFromNestedFields() throws Exception {
		Fields expectedFields = getNestedFields(_ddmStructure.getStructureId());

		String expectedContent = read("test-journal-content-nested-fields.xml");

		String actualContent = _journalConverter.getContent(
			_ddmStructure, expectedFields, _ddmStructure.getGroupId());

		assertEquals(expectedContent, actualContent);
	}

	@Test
	public void testGetContentFromTextAreaField() throws Exception {
		Fields fields = new Fields();

		_addField(
			_ddmStructure.getStructureId(), null, fields, "text_area",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList("<p>Hello World!</p>")
			).build());

		fields.put(
			getFieldsDisplayField(
				_ddmStructure.getStructureId(), "text_area_INSTANCE_RFnJ1nCn"));

		String expectedContent = read(
			"test-journal-content-text-area-field.xml");

		String actualContent = _journalConverter.getContent(
			_ddmStructure, fields, _ddmStructure.getGroupId());

		assertEquals(expectedContent, actualContent);
	}

	@Test
	public void testGetContentFromTextBoxField() throws Exception {
		Fields fields = new Fields();

		_addField(
			_ddmStructure.getStructureId(), null, fields, "text_box",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Arrays.asList("one", "two", "three")
			).put(
				_ptLocale, Arrays.asList("um", "dois", "tres")
			).build());

		fields.put(
			getFieldsDisplayField(
				_ddmStructure.getStructureId(),
				"text_box_INSTANCE_ND057krU,text_box_INSTANCE_HvemvQgl," +
					"text_box_INSTANCE_enAnbvq6"));

		String expectedContent = read(
			"test-journal-content-text-box-repeatable-field.xml");

		String actualContent = _journalConverter.getContent(
			_ddmStructure, fields, _ddmStructure.getGroupId());

		assertEquals(expectedContent, actualContent);
	}

	@Test
	public void testGetContentFromTextField() throws Exception {
		Fields fields = new Fields();

		_addField(
			_ddmStructure.getStructureId(), null, fields, "text",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList("one")
			).put(
				_ptLocale, Collections.singletonList("um")
			).build());

		fields.put(
			getFieldsDisplayField(
				_ddmStructure.getStructureId(), "text_INSTANCE_bf4sdx6Q"));

		String expectedContent = read("test-journal-content-text-field.xml");

		String actualContent = _journalConverter.getContent(
			_ddmStructure, fields, _ddmStructure.getGroupId());

		assertEquals(expectedContent, actualContent);
	}

	@Test
	public void testGetFieldsFromContentWithListElement() throws Exception {
		Fields expectedFields = new Fields();

		_addField(
			_ddmStructure.getStructureId(), null, expectedFields, "list",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList("[\"value 01\"]")
			).build());

		expectedFields.put(
			getFieldsDisplayField(
				_ddmStructure.getStructureId(),
				StringBundler.concat(
					"list_INSTANCE_pcm9WPVX,contactFieldSet_INSTANCE_",
					_ddmStructure.getStructureId(), ",phoneFieldSet_INSTANCE_",
					_ddmStructure.getStructureId(),
					",phoneFieldSetFieldSet_INSTANCE_",
					_ddmStructure.getStructureId())));

		String content = read("test-journal-content-list-field.xml");

		Fields actualFields = _journalConverter.getDDMFields(
			_ddmStructure, content);

		_assertFields(expectedFields, actualFields);
	}

	@Test
	public void testGetFieldsFromContentWithMultiListElement()
		throws Exception {

		Fields expectedFields = new Fields();

		_addField(
			_ddmStructure.getStructureId(), null, expectedFields, "multi_list",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale,
				Collections.singletonList("[\"value 01\",\"value 02\"]")
			).build());

		expectedFields.put(
			getFieldsDisplayField(
				_ddmStructure.getStructureId(),
				StringBundler.concat(
					"multi_list_INSTANCE_9X5wVsSv,contactFieldSet_INSTANCE_",
					_ddmStructure.getStructureId(), ",phoneFieldSet_INSTANCE_",
					_ddmStructure.getStructureId(),
					",phoneFieldSetFieldSet_INSTANCE_",
					_ddmStructure.getStructureId())));

		String content = read("test-journal-content-multi-list-field.xml");

		Fields actualFields = _journalConverter.getDDMFields(
			_ddmStructure, content);

		_assertFields(expectedFields, actualFields);
	}

	@Test
	public void testGetFieldsFromContentWithNestedElements() throws Exception {
		Fields expectedFields = getNestedFields(_ddmStructure.getStructureId());

		String content = read("test-journal-content-nested-fields.xml");

		Fields actualFields = _journalConverter.getDDMFields(
			_ddmStructure, content);

		_assertFields(expectedFields, actualFields);
	}

	@Test
	public void testGetFieldsFromContentWithParentStructuresElementsBackwardsCompatibility()
		throws Exception {

		String parentStructureDefinition = read(
			"test-ddm-structure-parent-structure.json");

		DDMStructure parentDDMStructure = _ddmStructureTestHelper.addStructure(
			PortalUtil.getClassNameId(JournalArticle.class), null,
			"Test Structure", jsonDeserialize(parentStructureDefinition),
			StorageType.DEFAULT.getValue(), DDMStructureConstants.TYPE_DEFAULT);

		DDMStructureVersion parentDDMStructureVersion =
			parentDDMStructure.getStructureVersion();

		DDMStructureLayout parentDDMStructureLayout =
			DDMStructureLayoutLocalServiceUtil.
				getStructureLayoutByStructureVersionId(
					parentDDMStructureVersion.getStructureVersionId());

		String childStructureDefinition = StringUtil.replace(
			read("test-ddm-structure-child-structure.json"),
			new String[] {"$DDM_STRUCTURE_ID", "$DDM_STRUCTURE_LAYOUT_ID"},
			new String[] {
				String.valueOf(parentDDMStructure.getStructureId()),
				String.valueOf(parentDDMStructureLayout.getStructureLayoutId())
			});

		DDMStructure childDDMStructure = _ddmStructureTestHelper.addStructure(
			PortalUtil.getClassNameId(JournalArticle.class), null,
			"Test Structure", jsonDeserialize(childStructureDefinition),
			StorageType.DEFAULT.getValue(), DDMStructureConstants.TYPE_DEFAULT);

		Fields expectedFields = new Fields();

		_addField(
			childDDMStructure.getStructureId(), _enLocale, expectedFields,
			"Text23i4",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList("Text 1")
			).build());

		_addField(
			childDDMStructure.getStructureId(), _enLocale, expectedFields,
			"Textlmzq",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList("Text 2")
			).build());

		expectedFields.put(
			getFieldsDisplayField(
				childDDMStructure.getStructureId(),
				StringBundler.concat(
					"parentStructureFieldSet37599_INSTANCE_",
					childDDMStructure.getStructureId(),
					",Text23i4_INSTANCE_ngkuwrmn,Textlmzq_INSTANCE_",
					"yxxxshhf")));

		String content = read(
			"test-journal-content-parent-structure-fields.xml");

		Fields actualFields = _journalConverter.getDDMFields(
			childDDMStructure, content);

		_assertFields(expectedFields, actualFields);
	}

	@Test
	public void testGetFieldsFromContentWithUnlocalizedElement()
		throws Exception {

		Fields expectedFields = new Fields();

		_addField(
			_ddmStructure.getStructureId(), null, expectedFields, "text",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList("one")
			).build());

		expectedFields.put(
			getFieldsDisplayField(
				_ddmStructure.getStructureId(),
				StringBundler.concat(
					"contactFieldSet_INSTANCE_", _ddmStructure.getStructureId(),
					",phoneFieldSet_INSTANCE_", _ddmStructure.getStructureId(),
					",phoneFieldSetFieldSet_INSTANCE_",
					_ddmStructure.getStructureId(),
					",text_INSTANCE_Okhyj7Ni")));

		String content = read(
			"test-journal-content-text-unlocalized-field.xml");

		Fields actualFields = _journalConverter.getDDMFields(
			_ddmStructure, content);

		_assertFields(expectedFields, actualFields);
	}

	@Test
	public void testGetFieldsFromEmptyContent() throws Exception {
		Assert.assertEquals(
			new Fields(), _journalConverter.getDDMFields(_ddmStructure, null));
		Assert.assertEquals(
			new Fields(),
			_journalConverter.getDDMFields(_ddmStructure, StringPool.BLANK));
	}

	@Test
	public void testGetFieldsFromRearrangedStructure() throws Exception {
		DDMStructure ddmStructure = _ddmStructureTestHelper.addStructure(
			PortalUtil.getClassNameId(JournalArticle.class), null,
			"Test Structure",
			jsonDeserialize(read("test-ddm-structure-rearranged-fields.json")),
			StorageType.DEFAULT.getValue(), DDMStructureConstants.TYPE_DEFAULT);

		Fields expectedFields = new Fields();

		_addField(
			ddmStructure.getStructureId(), null, expectedFields, "Text0",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList("0")
			).build());
		_addField(
			ddmStructure.getStructureId(), null, expectedFields, "Text1",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList("1")
			).build());

		expectedFields.put(
			getFieldsDisplayField(
				ddmStructure.getStructureId(),
				"FieldSet0_INSTANCE_Ou1IjCng,Text0_INSTANCE_fYZzWkyv" +
					",Text1_INSTANCE_iTVc3xgV"));

		String content = read("test-journal-content-rearranged-fields.xml");

		Fields actualFields = _journalConverter.getDDMFields(
			ddmStructure, content);

		_assertFields(expectedFields, actualFields);
	}

	@Test
	public void testGetFieldsFromRemovedNestedField() throws Exception {
		String structureDefinition = read(
			"test-ddm-structure-removed-nested-field.json");

		DDMStructure ddmStructure = _ddmStructureTestHelper.addStructure(
			PortalUtil.getClassNameId(JournalArticle.class), null,
			"Test Structure", jsonDeserialize(structureDefinition),
			StorageType.DEFAULT.getValue(), DDMStructureConstants.TYPE_DEFAULT);

		Fields expectedFields = _getRemovedNestedFields(
			ddmStructure.getStructureId());
		Fields actualFields = _journalConverter.getDDMFields(
			ddmStructure,
			read("test-journal-content-removed-nested-field.xml"));

		_assertFields(expectedFields, actualFields);
	}

	protected void assertEquals(
		DDMForm expectedDDMForm, DDMForm actualDDMForm) {

		Map<String, DDMFormField> expectedDDMFormFieldsMap =
			expectedDDMForm.getDDMFormFieldsMap(true);

		Map<String, DDMFormField> actualDDMFormFieldsMap =
			actualDDMForm.getDDMFormFieldsMap(true);

		for (Map.Entry<String, DDMFormField> expectedEntry :
				expectedDDMFormFieldsMap.entrySet()) {

			DDMFormField actualDDMFormField = actualDDMFormFieldsMap.get(
				expectedEntry.getKey());

			assertEquals(expectedEntry.getValue(), actualDDMFormField);
		}
	}

	protected void assertEquals(
		DDMFormField expectedDDMFormField, DDMFormField actualDDMFormField) {

		Assert.assertEquals(
			expectedDDMFormField.getDataType(),
			actualDDMFormField.getDataType());
		assertEquals(
			expectedDDMFormField.getDDMFormFieldOptions(),
			actualDDMFormField.getDDMFormFieldOptions());
		Assert.assertEquals(
			expectedDDMFormField.getIndexType(),
			actualDDMFormField.getIndexType());
		assertEquals(
			expectedDDMFormField.getLabel(), actualDDMFormField.getLabel());
		Assert.assertEquals(
			expectedDDMFormField.getName(), actualDDMFormField.getName());
		assertEquals(
			expectedDDMFormField.getStyle(), actualDDMFormField.getStyle());
		assertEquals(
			expectedDDMFormField.getTip(), actualDDMFormField.getTip());
		Assert.assertEquals(
			expectedDDMFormField.getType(), actualDDMFormField.getType());
		Assert.assertEquals(
			expectedDDMFormField.isMultiple(), actualDDMFormField.isMultiple());
		Assert.assertEquals(
			expectedDDMFormField.isRepeatable(),
			actualDDMFormField.isRepeatable());
		Assert.assertEquals(
			expectedDDMFormField.isRequired(), actualDDMFormField.isRequired());
	}

	protected void assertEquals(
		DDMFormFieldOptions expectedDDMFormFieldOptions,
		DDMFormFieldOptions actualDDMFormFieldOptions) {

		Set<String> expectedOptionsValues =
			expectedDDMFormFieldOptions.getOptionsValues();

		for (String expectedOptionsValue : expectedOptionsValues) {
			LocalizedValue expectedOptionLabels =
				expectedDDMFormFieldOptions.getOptionLabels(
					expectedOptionsValue);

			LocalizedValue actualOptionLabels =
				actualDDMFormFieldOptions.getOptionLabels(expectedOptionsValue);

			assertEquals(expectedOptionLabels, actualOptionLabels);
		}
	}

	protected void assertEquals(
		LocalizedValue expectedLocalizedValue,
		LocalizedValue actualLocalizedValue) {

		Set<Locale> expectedAvailableLocales =
			expectedLocalizedValue.getAvailableLocales();

		for (Locale expectedLocale : expectedAvailableLocales) {
			String expectedValue = expectedLocalizedValue.getString(
				expectedLocale);

			String actualValue = actualLocalizedValue.getString(expectedLocale);

			Assert.assertEquals(expectedValue, actualValue);
		}
	}

	protected void assertEquals(String expectedContent, String actualContent)
		throws Exception {

		Map<String, Map<Locale, List<String>>> expectedFieldsMap = getFieldsMap(
			expectedContent);

		Map<String, Map<Locale, List<String>>> actualFieldsMap = getFieldsMap(
			actualContent);

		Assert.assertEquals(expectedFieldsMap, actualFieldsMap);
	}

	protected DDMForm deserialize(String content) {
		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(content);

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				_xsdDDMFormDeserializer.deserialize(builder.build());

		return ddmFormDeserializerDeserializeResponse.getDDMForm();
	}

	protected Field getFieldsDisplayField(long ddmStructureId, String value) {
		Field fieldsDisplayField = new Field();

		fieldsDisplayField.setDDMStructureId(ddmStructureId);
		fieldsDisplayField.setName(DDM.FIELDS_DISPLAY_NAME);
		fieldsDisplayField.setValue(value);

		return fieldsDisplayField;
	}

	protected Map<String, Map<Locale, List<String>>> getFieldsMap(
			String content)
		throws Exception {

		Map<String, Map<Locale, List<String>>> fieldsMap = new HashMap<>();

		Document document = UnsecureSAXReaderUtil.read(content);

		Element rootElement = document.getRootElement();

		List<Element> dynamicElementElements = rootElement.elements(
			"dynamic-element");

		for (Element dynamicElementElement : dynamicElementElements) {
			udpateFieldsMap(dynamicElementElement, fieldsMap);
		}

		return fieldsMap;
	}

	protected Fields getNestedFields(long ddmStructureId) {
		Fields fields = new Fields();

		_addField(
			ddmStructureId, null, fields, "boolean",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList(false)
			).put(
				_ptLocale, Collections.singletonList(false)
			).build());

		_addField(
			ddmStructureId, null, fields, "document_library",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList("document")
			).put(
				_ptLocale, Collections.singletonList("document")
			).build());

		_addField(
			ddmStructureId, null, fields, "link_to_layout",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList("link")
			).put(
				_ptLocale, Collections.singletonList("link")
			).build());

		_addField(
			ddmStructureId, null, fields, "text_area",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList("Sample text area")
			).put(
				_ptLocale, Collections.singletonList("Texto de exemplo")
			).build());

		_addField(
			ddmStructureId, null, fields, "multi_list",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList("[\"\"]")
			).put(
				_ptLocale, Collections.singletonList("[\"\"]")
			).build());

		_addField(
			ddmStructureId, null, fields, "list",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList("[\"\"]")
			).put(
				_ptLocale, Collections.singletonList("[\"\"]")
			).build());

		_addField(
			ddmStructureId, null, fields, "contact",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Arrays.asList("joe", "richard")
			).put(
				_ptLocale, Arrays.asList("joao", "ricardo")
			).build());

		_addField(
			ddmStructureId, null, fields, "phone",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Arrays.asList("123", "456")
			).put(
				_ptLocale, Arrays.asList("123", "456")
			).build());

		_addField(
			ddmStructureId, null, fields, "ext",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Arrays.asList("1", "2", "3", "4", "5")
			).put(
				_ptLocale, Arrays.asList("1", "2", "3", "4", "5")
			).build());

		_addField(
			ddmStructureId, null, fields, "text",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList("hello")
			).put(
				_ptLocale, Collections.singletonList("ola")
			).build());

		_addField(
			ddmStructureId, null, fields, "text_box",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList("value1")
			).put(
				_ptLocale, Collections.singletonList("valor1")
			).build());

		_addField(
			ddmStructureId, null, fields, "image_1",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList("image1")
			).put(
				_ptLocale, Collections.singletonList("image1")
			).build());

		_addField(
			ddmStructureId, null, fields, "image_2",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList("image2")
			).put(
				_ptLocale, Collections.singletonList("image2")
			).build());

		_addField(
			ddmStructureId, null, fields, "image_3",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList("image3")
			).put(
				_ptLocale, Collections.singletonList("image3")
			).build());

		Field fieldsDisplayField = new Field(
			ddmStructureId, DDM.FIELDS_DISPLAY_NAME,
			StringBundler.concat(
				"boolean_INSTANCE_YELSrniM,document_library_INSTANCE_HzKJrSts,",
				"link_to_layout_INSTANCE_eHQALxHa,text_area_INSTANCE_ucizquBv,",
				"multi_list_INSTANCE_oOUZHUcy,list_INSTANCE_RMYIbORN,",
				"contactFieldSet_INSTANCE_RJSkjdfi,contact_INSTANCE_RF3do1m5,",
				"phoneFieldSet_INSTANCE_zhglwgmk,phone_INSTANCE_QK6B0wK9,",
				"phoneFieldSetFieldSet_INSTANCE_42904,",
				"ext_INSTANCE_L67MPqQf,ext_INSTANCE_8uxzZl41,",
				"ext_INSTANCE_S58K861T,",
				"contactFieldSet_INSTANCE_3hACzXcE,contact_INSTANCE_CUeFxcrA,",
				"phoneFieldSet_INSTANCE_UgCyiQd3,phone_INSTANCE_lVTcTviF,",
				"phoneFieldSetFieldSet_INSTANCE_42904,",
				"ext_INSTANCE_cZalDSll,ext_INSTANCE_HDrK2Um5,",
				"text_INSTANCE_zWlZDoLc,",
				"text_box_INSTANCE_fmyemVKH,image_1_INSTANCE_xhdykzYh,",
				"image_2_INSTANCE_FViKyTea,image_3_INSTANCE_JVBHdcGZ"));

		fields.put(fieldsDisplayField);

		return fields;
	}

	protected List<String> getValues(
		Map<Locale, List<String>> valuesMap, Locale locale) {

		return valuesMap.computeIfAbsent(locale, key -> new ArrayList<>());
	}

	protected DDMForm jsonDeserialize(String content) {
		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(content);

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				_jsonDDMFormDeserializer.deserialize(builder.build());

		return ddmFormDeserializerDeserializeResponse.getDDMForm();
	}

	protected String read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(
			"com/liferay/journal/dependencies/" + fileName);

		return StringUtil.read(inputStream);
	}

	protected void udpateFieldsMap(
		Element dynamicElementElement,
		Map<String, Map<Locale, List<String>>> fieldsMap) {

		List<Element> childrenDynamicElementElements =
			dynamicElementElement.elements("dynamic-element");

		for (Element childrenDynamicElementElement :
				childrenDynamicElementElements) {

			udpateFieldsMap(childrenDynamicElementElement, fieldsMap);
		}

		String name = dynamicElementElement.attributeValue("name");

		Map<Locale, List<String>> valuesMap = fieldsMap.computeIfAbsent(
			name, key -> new HashMap<>());

		List<Element> dynamicContentElements = dynamicElementElement.elements(
			"dynamic-content");

		for (Element dynamicContentElement : dynamicContentElements) {
			Locale locale = LocaleUtil.fromLanguageId(
				dynamicContentElement.attributeValue("language-id"));

			List<String> values = getValues(valuesMap, locale);

			List<Element> optionElements = dynamicContentElement.elements(
				"option");

			if (!optionElements.isEmpty()) {
				for (Element optionElement : optionElements) {
					values.add(optionElement.getText());
				}
			}
			else {
				values.add(dynamicContentElement.getText());
			}
		}
	}

	private void _addField(
		long ddmStructureId, Locale defaultLocale, Fields fields, String name,
		HashMap<Locale, List<Serializable>> values) {

		Field field = new Field();

		field.setDDMStructureId(ddmStructureId);

		if (defaultLocale != null) {
			field.setDefaultLocale(defaultLocale);
		}

		field.setName(name);

		for (Map.Entry<Locale, List<Serializable>> entry : values.entrySet()) {
			for (Serializable value : entry.getValue()) {
				field.addValue(entry.getKey(), value);
			}
		}

		fields.put(field);
	}

	private void _assertFields(Fields expectedFields, Fields actualFields) {
		Set<String> actualFieldsNames = actualFields.getNames();
		Set<String> expectedFieldsNames = expectedFields.getNames();

		Assert.assertEquals(
			actualFieldsNames.toString(), expectedFieldsNames.size(),
			actualFieldsNames.size());

		for (String name : expectedFieldsNames) {
			Assert.assertEquals(
				expectedFields.getDDMStructureId(),
				actualFields.getDDMStructureId());

			if (!name.equals(DDM.FIELDS_DISPLAY_NAME)) {
				Assert.assertEquals(
					expectedFields.get(name), actualFields.get(name));
			}
			else {
				Field actualFieldsDisplayField = actualFields.get(name);

				String actualFieldsDisplayValues =
					(String)actualFieldsDisplayField.getValue();

				Field expectedFieldsDisplayField = expectedFields.get(name);

				String[] expectedFieldsDisplayValues = StringUtil.split(
					(String)expectedFieldsDisplayField.getValue());

				for (String expectedFieldsDisplayValue :
						expectedFieldsDisplayValues) {

					if (!expectedFieldsDisplayValue.contains("FieldSet")) {
						Assert.assertTrue(
							actualFieldsDisplayValues.contains(
								expectedFieldsDisplayValue));
					}
					else {
						Assert.assertTrue(
							actualFieldsDisplayValues.contains(
								StringUtil.extractFirst(
									expectedFieldsDisplayValue,
									DDM.INSTANCE_SEPARATOR)));
					}
				}
			}
		}
	}

	private Fields _getRemovedNestedFields(long ddmStructureId) {
		Fields fields = new Fields();

		_addField(
			ddmStructureId, null, fields, "backgroundcolor",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList("#FFF")
			).build());

		_addField(
			ddmStructureId, null, fields, "titulo",
			HashMapBuilder.<Locale, List<Serializable>>put(
				_enLocale, Collections.singletonList("Servicios")
			).build());

		Field fieldsDisplayField = new Field(
			ddmStructureId, DDM.FIELDS_DISPLAY_NAME,
			"backgroundcolor_INSTANCE_koamvduz,titulo_INSTANCE_ieaastyz,");

		fields.put(fieldsDisplayField);

		return fields;
	}

	@Inject(filter = "ddm.form.deserializer.type=json")
	private static DDMFormDeserializer _jsonDDMFormDeserializer;

	@Inject(filter = "ddm.form.deserializer.type=xsd")
	private static DDMFormDeserializer _xsdDDMFormDeserializer;

	private DDMStructure _ddmStructure;
	private DDMStructureTestHelper _ddmStructureTestHelper;
	private Locale _enLocale;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalConverter _journalConverter;

	private Locale _ptLocale;

}