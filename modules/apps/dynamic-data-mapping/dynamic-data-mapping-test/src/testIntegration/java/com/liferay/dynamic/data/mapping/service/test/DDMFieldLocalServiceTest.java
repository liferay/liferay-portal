/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestHelper;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class DDMFieldLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_ddmStructureTestHelper = new DDMStructureTestHelper(
			_classNameLocalService.getClassNameId(DDMFormInstance.class),
			_group);
	}

	@After
	public void tearDown() throws Exception {
		_ddmFieldLocalService.deleteDDMFormValues(_STORAGE_ID);
	}

	@Test
	public void testJSONAttributeForm() throws Exception {
		Locale locale = LocaleUtil.getSiteDefault();

		DDMForm ddmForm = new DDMForm();

		ddmForm.setAvailableLocales(Collections.singleton(locale));
		ddmForm.setDefaultLocale(locale);

		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		ddmFormFields.add(
			_createDDMFormField(
				locale, ddmForm, "Page", DDMFormFieldType.LINK_TO_PAGE,
				"link-to-page", "ddm", null));
		ddmFormFields.add(
			_createDDMFormField(
				locale, ddmForm, "Number", DDMFormFieldType.NUMBER, "number",
				"ddm", null));

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions(
			locale);

		ddmFormFieldOptions.addOptionLabel("value 1", locale, "value 1");
		ddmFormFieldOptions.addOptionLabel("value 2", locale, "value 2");
		ddmFormFieldOptions.addOptionLabel("value 3", locale, "value 3");

		ddmFormFields.add(
			_createDDMFormField(
				locale, ddmForm, "Select", "select", "string", null,
				ddmFormFieldOptions));

		DDMStructure ddmStructure = _ddmStructureTestHelper.addStructure(
			ddmForm, StorageType.DEFAULT.toString());

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.setAvailableLocales(Collections.singleton(locale));
		ddmFormValues.setDDMFormFieldValues(
			Arrays.asList(
				_createDDMFormFieldValue(
					locale, "Page",
					_jsonFactory.looseSerialize(
						HashMapBuilder.<String, Object>put(
							"groupId", _group.getGroupId()
						).put(
							"layoutId", _LAYOUT_ID
						).put(
							"privateLayout", false
						).build())),
				_createDDMFormFieldValue(locale, "Number", "123"),
				_createDDMFormFieldValue(
					locale, "Select",
					JSONUtil.putAll(
						"value 2"
					).toString())));
		ddmFormValues.setDefaultLocale(locale);

		_ddmFieldLocalService.updateDDMFormValues(
			ddmStructure.getStructureId(), _STORAGE_ID, ddmFormValues);

		Assert.assertEquals(
			1,
			_ddmFieldLocalService.getDDMFormValuesCount(
				_group.getCompanyId(), DDMFormFieldType.LINK_TO_PAGE,
				HashMapBuilder.<String, Object>put(
					"groupId", _group.getGroupId()
				).put(
					"layoutId", _LAYOUT_ID
				).put(
					"privateLayout", Boolean.FALSE
				).build()));
		Assert.assertEquals(
			ddmFormValues,
			_ddmFieldLocalService.getDDMFormValues(ddmForm, _STORAGE_ID));
	}

	@Test
	public void testMultipleLanguageForm() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm("field1");

		DDMStructure ddmStructure = _ddmStructureTestHelper.addStructure(
			ddmForm, StorageType.DEFAULT.toString());

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.setDefaultLocale(LocaleUtil.ENGLISH);

		Set<Locale> availableLocales = new LinkedHashSet<>(
			Arrays.asList(
				LocaleUtil.CHINA, LocaleUtil.ENGLISH, LocaleUtil.SPAIN));

		ddmFormValues.setAvailableLocales(availableLocales);

		DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

		ddmFormFieldValue.setName("field1");
		ddmFormFieldValue.setInstanceId(StringUtil.randomString(8));

		Value value = new LocalizedValue(LocaleUtil.ENGLISH);

		for (Locale locale : availableLocales) {
			value.addString(locale, LocaleUtil.toLanguageId(locale) + " value");
		}

		ddmFormFieldValue.setValue(value);

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		_ddmFieldLocalService.updateDDMFormValues(
			ddmStructure.getStructureId(), _STORAGE_ID, ddmFormValues);

		Assert.assertEquals(
			1,
			_ddmFieldLocalService.getDDMFormValuesCount(
				_group.getCompanyId(), "text",
				Collections.singletonMap(
					StringPool.BLANK,
					LocaleUtil.toLanguageId(LocaleUtil.ENGLISH) + " value")));

		DDMFormValues deserializedDDMFormValues =
			_ddmFieldLocalService.getDDMFormValues(ddmForm, _STORAGE_ID);

		Assert.assertEquals(ddmFormValues, deserializedDDMFormValues);
	}

	@Test
	public void testNestedFieldsForm() throws Exception {
		Locale locale = LocaleUtil.getSiteDefault();

		DDMForm ddmForm = new DDMForm();

		ddmForm.setAvailableLocales(Collections.singleton(locale));
		ddmForm.setDefaultLocale(locale);

		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		DDMFormField rootDDMFormField = _createDDMFormField(
			locale, ddmForm, "root", "text", "string", null, null);

		ddmFormFields.add(rootDDMFormField);

		DDMFormField childDDMFormField1 = _createDDMFormField(
			locale, ddmForm, "child1", "text", "string", null, null);

		rootDDMFormField.addNestedDDMFormField(childDDMFormField1);

		DDMFormField childDDMFormField2 = _createDDMFormField(
			locale, ddmForm, "child2", "text", "string", null, null);

		rootDDMFormField.addNestedDDMFormField(childDDMFormField2);

		childDDMFormField1.addNestedDDMFormField(
			_createDDMFormField(
				locale, ddmForm, "grandChild", "text", "string", null, null));

		DDMStructure ddmStructure = _ddmStructureTestHelper.addStructure(
			ddmForm, StorageType.DEFAULT.toString());

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.setDefaultLocale(locale);
		ddmFormValues.setAvailableLocales(Collections.singleton(locale));

		DDMFormFieldValue rootDDMFormFieldValue = _createDDMFormFieldValue(
			locale, "root", "root value");

		DDMFormFieldValue childDDMFormFieldValue1 = _createDDMFormFieldValue(
			locale, "child1", "child 1 value");

		rootDDMFormFieldValue.addNestedDDMFormFieldValue(
			childDDMFormFieldValue1);

		DDMFormFieldValue childDDMFormFieldValue2 = _createDDMFormFieldValue(
			locale, "child2", "child 2 value a");

		rootDDMFormFieldValue.addNestedDDMFormFieldValue(
			childDDMFormFieldValue2);

		DDMFormFieldValue grandChildDDMFormFieldValue =
			_createDDMFormFieldValue(
				locale, "grandChild", "grand child value a");

		childDDMFormFieldValue1.addNestedDDMFormFieldValue(
			grandChildDDMFormFieldValue);

		ddmFormValues.addDDMFormFieldValue(rootDDMFormFieldValue);

		_ddmFieldLocalService.updateDDMFormValues(
			ddmStructure.getStructureId(), _STORAGE_ID, ddmFormValues);

		DDMFormValues deserializedDDMFormValues =
			_ddmFieldLocalService.getDDMFormValues(ddmForm, _STORAGE_ID);

		Assert.assertEquals(ddmFormValues, deserializedDDMFormValues);

		Value value = new LocalizedValue(locale);

		value.addString(locale, "child 2 value b");

		childDDMFormFieldValue2.setValue(value);

		value = new LocalizedValue(locale);

		value.addString(locale, "grand child value b");

		grandChildDDMFormFieldValue.setValue(value);

		_ddmFieldLocalService.updateDDMFormValues(
			ddmStructure.getStructureId(), _STORAGE_ID, ddmFormValues);

		deserializedDDMFormValues = _ddmFieldLocalService.getDDMFormValues(
			ddmForm, _STORAGE_ID);

		Assert.assertEquals(ddmFormValues, deserializedDDMFormValues);
	}

	@Test
	public void testSimpleForm() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm("field1");

		DDMStructure ddmStructure = _ddmStructureTestHelper.addStructure(
			ddmForm, StorageType.DEFAULT.toString());

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.setDefaultLocale(LocaleUtil.ENGLISH);
		ddmFormValues.setAvailableLocales(
			Collections.singleton(LocaleUtil.ENGLISH));
		ddmFormValues.setDDMFormFieldValues(
			Collections.singletonList(
				_createDDMFormFieldValue(
					LocaleUtil.ENGLISH, "field1", "value1")));

		_ddmFieldLocalService.updateDDMFormValues(
			ddmStructure.getStructureId(), _STORAGE_ID, ddmFormValues);

		Assert.assertEquals(
			1,
			_ddmFieldLocalService.getDDMFormValuesCount(
				_group.getCompanyId(), "text",
				Collections.singletonMap(StringPool.BLANK, "value1")));

		DDMFormValues deserializedDDMFormValues =
			_ddmFieldLocalService.getDDMFormValues(ddmForm, _STORAGE_ID);

		Assert.assertEquals(ddmFormValues, deserializedDDMFormValues);
	}

	@Test
	public void testUpdateDDMFormValuesWithLegacyDDMFormField()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm("field");

		DDMStructure ddmStructure = _ddmStructureTestHelper.addStructure(
			ddmForm, StorageType.DEFAULT.toString());

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.setDefaultLocale(LocaleUtil.ENGLISH);

		ddmFormValues.setDDMFormFieldValues(
			Collections.singletonList(
				_createDDMFormFieldValue(
					LocaleUtil.ENGLISH, "field",
					LocaleUtil.toLanguageId(LocaleUtil.ENGLISH) + " value")));

		_ddmFieldLocalService.updateDDMFormValues(
			ddmStructure.getStructureId(), _STORAGE_ID, ddmFormValues);

		DDMFormValues deserializedDDMFormValues =
			_ddmFieldLocalService.getDDMFormValues(ddmForm, _STORAGE_ID);

		Assert.assertEquals(ddmFormValues, deserializedDDMFormValues);

		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		DDMFormField ddmFormField = ddmFormFields.get(0);

		String fieldName = DDMFormFieldUtil.getDDMFormFieldName("field");

		ddmFormField.setName(fieldName);

		ddmStructure = _ddmStructureTestHelper.updateStructure(
			ddmStructure.getStructureId(), ddmForm);

		ddmFormValues.setDDMFormFieldValues(
			Collections.singletonList(
				_createDDMFormFieldValue(
					LocaleUtil.ENGLISH, fieldName,
					LocaleUtil.toLanguageId(LocaleUtil.ENGLISH) + " value")));

		_ddmFieldLocalService.updateDDMFormValues(
			ddmStructure.getStructureId(), _STORAGE_ID, ddmFormValues);

		Assert.assertEquals(
			1,
			_ddmFieldLocalService.getDDMFormValuesCount(
				_group.getCompanyId(), "text",
				Collections.singletonMap(
					StringPool.BLANK,
					LocaleUtil.toLanguageId(LocaleUtil.ENGLISH) + " value")));

		deserializedDDMFormValues = _ddmFieldLocalService.getDDMFormValues(
			ddmForm, _STORAGE_ID);

		Assert.assertEquals(ddmFormValues, deserializedDDMFormValues);
	}

	@Test
	public void testUpdatedForm() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			"field1", "field2", "field3");

		DDMStructure ddmStructure = _ddmStructureTestHelper.addStructure(
			ddmForm, StorageType.DEFAULT.toString());

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.setDefaultLocale(LocaleUtil.ENGLISH);
		ddmFormValues.setAvailableLocales(
			Collections.singleton(LocaleUtil.ENGLISH));
		ddmFormValues.setDDMFormFieldValues(
			Arrays.asList(
				_createDDMFormFieldValue(
					LocaleUtil.ENGLISH, "field1", "value1"),
				_createDDMFormFieldValue(
					LocaleUtil.ENGLISH, "field2", "value2 a")));

		_ddmFieldLocalService.updateDDMFormValues(
			ddmStructure.getStructureId(), _STORAGE_ID, ddmFormValues);

		Assert.assertEquals(
			1,
			_ddmFieldLocalService.getDDMFormValuesCount(
				_group.getCompanyId(), "text",
				Collections.singletonMap(StringPool.BLANK, "value1")));
		Assert.assertEquals(
			1,
			_ddmFieldLocalService.getDDMFormValuesCount(
				_group.getCompanyId(), "text",
				Collections.singletonMap(StringPool.BLANK, "value2 a")));

		DDMFormValues deserializedDDMFormValues =
			_ddmFieldLocalService.getDDMFormValues(ddmForm, _STORAGE_ID);

		Assert.assertEquals(ddmFormValues, deserializedDDMFormValues);

		ddmFormValues.setDDMFormFieldValues(
			Arrays.asList(
				_createDDMFormFieldValue(
					LocaleUtil.ENGLISH, "field2", "value2 b"),
				_createDDMFormFieldValue(
					LocaleUtil.ENGLISH, "field3", "value3")));

		_ddmFieldLocalService.updateDDMFormValues(
			ddmStructure.getStructureId(), _STORAGE_ID, ddmFormValues);

		Assert.assertEquals(
			0,
			_ddmFieldLocalService.getDDMFormValuesCount(
				_group.getCompanyId(), "text",
				Collections.singletonMap(StringPool.BLANK, "value1")));
		Assert.assertEquals(
			0,
			_ddmFieldLocalService.getDDMFormValuesCount(
				_group.getCompanyId(), "text",
				Collections.singletonMap(StringPool.BLANK, "value2 a")));

		Assert.assertEquals(
			1,
			_ddmFieldLocalService.getDDMFormValuesCount(
				_group.getCompanyId(), "text",
				Collections.singletonMap(StringPool.BLANK, "value2 b")));
		Assert.assertEquals(
			1,
			_ddmFieldLocalService.getDDMFormValuesCount(
				_group.getCompanyId(), "text",
				Collections.singletonMap(StringPool.BLANK, "value3")));

		deserializedDDMFormValues = _ddmFieldLocalService.getDDMFormValues(
			ddmForm, _STORAGE_ID);

		Assert.assertEquals(ddmFormValues, deserializedDDMFormValues);
	}

	private DDMFormField _createDDMFormField(
		Locale locale, DDMForm ddmForm, String name, String type,
		String dataType, String fieldNamespace,
		DDMFormFieldOptions ddmFormFieldOptions) {

		DDMFormField ddmFormField = new DDMFormField(name, type);

		ddmFormField.setDataType(dataType);

		if (fieldNamespace != null) {
			ddmFormField.setFieldNamespace(fieldNamespace);
		}

		ddmFormField.setFieldReference(ddmFormField.getName());
		ddmFormField.setMultiple(false);
		ddmFormField.setLocalizable(true);
		ddmFormField.setRepeatable(false);
		ddmFormField.setRequired(false);

		LocalizedValue localizedValue = ddmFormField.getLabel();

		localizedValue.addString(locale, ddmFormField.getName());

		ddmFormField.setDDMForm(ddmForm);

		if (ddmFormFieldOptions != null) {
			ddmFormField.setDDMFormFieldOptions(ddmFormFieldOptions);
		}

		return ddmFormField;
	}

	private DDMFormFieldValue _createDDMFormFieldValue(
		Locale locale, String name, String s) {

		DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

		ddmFormFieldValue.setName(name);
		ddmFormFieldValue.setInstanceId(StringUtil.randomString(8));

		Value value = new LocalizedValue(locale);

		value.addString(locale, s);

		ddmFormFieldValue.setValue(value);

		return ddmFormFieldValue;
	}

	private static final long _LAYOUT_ID = 1;

	private static final long _STORAGE_ID = 0;

	@Inject
	private static ClassNameLocalService _classNameLocalService;

	@Inject
	private static DDMFieldLocalService _ddmFieldLocalService;

	@Inject
	private static JSONFactory _jsonFactory;

	private DDMStructureTestHelper _ddmStructureTestHelper;

	@DeleteAfterTestRun
	private Group _group;

}