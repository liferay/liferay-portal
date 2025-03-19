/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.select;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldOptionsFactory;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.test.util.BaseDDMFormFieldTemplateContextContributorTestCase;
import com.liferay.dynamic.data.mapping.test.util.DDMFormFieldOptionsTestUtil;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Marcellus Tavares
 */
public class SelectDDMFormFieldTemplateContextContributorTest
	extends BaseDDMFormFieldTemplateContextContributorTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		setUpLanguageUtil();

		_setUpDDMFormInstanceLocalService();
		_setUpJSONFactory();
		_setUpLocaleThreadLocal();

		PropsTestUtil.setProps("collator.rules", "<<<");

		ReflectionTestUtil.setFieldValue(
			_selectDDMFormFieldTemplateContextContributor, "_language",
			language);
		ReflectionTestUtil.setFieldValue(
			_selectDDMFormFieldTemplateContextContributor,
			"_listTypeEntryLocalService", _listTypeEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_selectDDMFormFieldTemplateContextContributor,
			"_objectDefinitionLocalService", _objectDefinitionLocalService);
		ReflectionTestUtil.setFieldValue(
			_selectDDMFormFieldTemplateContextContributor,
			"_objectFieldLocalService", _objectFieldLocalService);
	}

	@Test
	public void testGetMultiple() {
		Assert.assertFalse(
			_selectDDMFormFieldTemplateContextContributor.getMultiple(
				_createDDMFormField(false), new DDMFormFieldRenderingContext(),
				null));
		Assert.assertTrue(
			_selectDDMFormFieldTemplateContextContributor.getMultiple(
				_createDDMFormField(true), new DDMFormFieldRenderingContext(),
				null));

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setProperty(
			"changedProperties",
			HashMapBuilder.<String, Object>put(
				"multiple", true
			).build());

		Assert.assertTrue(
			_selectDDMFormFieldTemplateContextContributor.getMultiple(
				_createDDMFormField(false), ddmFormFieldRenderingContext,
				null));

		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST)
		).thenReturn(
			false
		);

		Assert.assertFalse(
			_selectDDMFormFieldTemplateContextContributor.getMultiple(
				_createDDMFormField(true), new DDMFormFieldRenderingContext(),
				objectField));

		Mockito.when(
			objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST)
		).thenReturn(
			true
		);

		Assert.assertTrue(
			_selectDDMFormFieldTemplateContextContributor.getMultiple(
				_createDDMFormField(false), new DDMFormFieldRenderingContext(),
				objectField));
	}

	@Test
	public void testGetObjectFieldOptions() throws Exception {
		DDMFormInstance ddmFormInstance = Mockito.mock(DDMFormInstance.class);

		long objectDefinitionId = RandomTestUtil.randomLong();

		Mockito.when(
			ddmFormInstance.getObjectDefinitionId()
		).thenReturn(
			objectDefinitionId
		);

		long ddmFormInstanceId = RandomTestUtil.randomLong();

		Mockito.when(
			_ddmFormInstanceLocalService.fetchDDMFormInstance(ddmFormInstanceId)
		).thenReturn(
			ddmFormInstance
		);

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getObjectDefinitionId()
		).thenReturn(
			objectDefinitionId
		);

		Mockito.when(
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectDefinitionId)
		).thenReturn(
			objectDefinition
		);

		ObjectField objectField = Mockito.mock(ObjectField.class);

		long listTypeDefinitionId = RandomTestUtil.randomLong();

		Mockito.when(
			objectField.getListTypeDefinitionId()
		).thenReturn(
			listTypeDefinitionId
		);

		Mockito.when(
			_objectFieldLocalService.getObjectField(
				objectDefinitionId, "picklistObjectField")
		).thenReturn(
			objectField
		);

		List<ListTypeEntry> listTypeEntries = new ArrayList<>();

		listTypeEntries.add(_getListTypeEntry("List Type Entry 1"));
		listTypeEntries.add(_getListTypeEntry("List Type Entry 2"));

		Mockito.when(
			_listTypeEntryLocalService.getListTypeEntries(
				listTypeDefinitionId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null)
		).thenReturn(
			listTypeEntries
		);

		DDMFormField ddmFormField = new DDMFormField("field", "select");

		ddmFormField.setProperty(
			"objectFieldName", "[\"picklistObjectField\"]");

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		ddmFormFieldOptions.addOption("Option1");
		ddmFormFieldOptions.addOptionReference("Option1", "ListTypeEntry1");

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setDDMFormInstanceId(ddmFormInstanceId);

		Assert.assertEquals(
			Arrays.asList(
				HashMapBuilder.<String, Object>put(
					"label", "List Type Entry 1"
				).put(
					"labelMap",
					HashMapBuilder.put(
						LocaleUtil.US, "List Type Entry 1"
					).build()
				).put(
					"reference", "ListTypeEntry1"
				).put(
					"value", "Option1"
				).build(),
				HashMapBuilder.<String, Object>put(
					"label", "List Type Entry 2"
				).put(
					"labelMap",
					HashMapBuilder.put(
						LocaleUtil.US, "List Type Entry 2"
					).build()
				).put(
					"reference", "ListTypeEntry2"
				).put(
					"value", "ListTypeEntry2"
				).build()),
			_selectDDMFormFieldTemplateContextContributor.getObjectFieldOptions(
				ddmFormField, ddmFormFieldOptions, objectField));
	}

	@Test
	public void testGetOptions() {
		Map<Locale, String> labelMap1 = HashMapBuilder.put(
			LocaleUtil.SPAIN, RandomTestUtil.randomString()
		).put(
			LocaleUtil.US, RandomTestUtil.randomString()
		).build();

		long listTypeDefinitionId = RandomTestUtil.randomLong();

		_mockListTypeEntry("value 1", listTypeDefinitionId, labelMap1);

		Map<Locale, String> labelMap2 = HashMapBuilder.put(
			LocaleUtil.SPAIN, RandomTestUtil.randomString()
		).put(
			LocaleUtil.US, RandomTestUtil.randomString()
		).build();

		_mockListTypeEntry("value 2", listTypeDefinitionId, labelMap2);

		_mockListTypeEntry("value 3", listTypeDefinitionId, null);

		List<Object> expectedOptions = new ArrayList<>();

		expectedOptions.add(
			DDMFormFieldOptionsTestUtil.createOption(
				"Label 1", labelMap1, "Reference 1", "value 1"));
		expectedOptions.add(
			DDMFormFieldOptionsTestUtil.createOption(
				"Label 2", labelMap2, "Reference 2", "value 2"));
		expectedOptions.add(
			DDMFormFieldOptionsTestUtil.createOption(
				"Label 3",
				HashMapBuilder.put(
					LocaleUtil.US, "Label 3"
				).build(),
				"Reference 3", "value 3"));

		DDMFormField ddmFormField = new DDMFormField(
			"field", DDMFormFieldTypeConstants.SELECT);

		ddmFormField.setProperty("listTypeDefinitionId", listTypeDefinitionId);

		DDMFormFieldOptions ddmFormFieldOptions =
			DDMFormFieldOptionsTestUtil.createDDMFormFieldOptions();

		Assert.assertEquals(
			expectedOptions,
			_getActualOptions(
				ddmFormField, ddmFormFieldOptions, LocaleUtil.US));
	}

	@Test
	public void testGetOptionsAlphabeticallyOrdered() {
		List<Object> expectedOptions = new ArrayList<>();

		expectedOptions.add(
			DDMFormFieldOptionsTestUtil.createOption(
				"Label 1",
				HashMapBuilder.put(
					LocaleUtil.US, "Label 1"
				).build(),
				"Reference 1", "value 1"));
		expectedOptions.add(
			DDMFormFieldOptionsTestUtil.createOption(
				"Label 2",
				HashMapBuilder.put(
					LocaleUtil.US, "Label 2"
				).build(),
				"Reference 2", "value 2"));
		expectedOptions.add(
			DDMFormFieldOptionsTestUtil.createOption(
				"Label 3",
				HashMapBuilder.put(
					LocaleUtil.US, "Label 3"
				).build(),
				"Reference 3", "value 3"));

		DDMFormField ddmFormField = new DDMFormField("field", "select");

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		for (int i = 3; i > 0; i--) {
			ddmFormFieldOptions.addOptionLabel(
				"value " + i, LocaleUtil.US, "Label " + i);
			ddmFormFieldOptions.addOptionReference(
				"value " + i, "Reference " + i);
		}

		Assert.assertNotEquals(
			expectedOptions,
			_getActualOptions(
				ddmFormField, ddmFormFieldOptions, LocaleUtil.US));

		ddmFormField.setProperty("alphabeticalOrder", "true");

		Assert.assertEquals(
			expectedOptions,
			_getActualOptions(
				ddmFormField, ddmFormFieldOptions, LocaleUtil.US));
	}

	@Test
	public void testGetParameters1() throws Exception {
		DDMFormField ddmFormField = new DDMFormField("field", "select");

		ddmFormField.setDDMForm(getDDMForm());
		ddmFormField.setProperty("dataSourceType", "data-provider");
		ddmFormField.setProperty("localizedObjectField", false);

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			createDDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setValue("[\"value 1\"]");

		_setUpDDMFormFieldOptionsFactory(
			ddmFormField, ddmFormFieldRenderingContext);

		SelectDDMFormFieldTemplateContextContributor
			selectDDMFormFieldTemplateContextContributor = _createSpy();

		Map<String, Object> parameters =
			selectDDMFormFieldTemplateContextContributor.getParameters(
				ddmFormField, ddmFormFieldRenderingContext);

		Assert.assertTrue(parameters.containsKey("dataSourceType"));
		Assert.assertEquals("data-provider", parameters.get("dataSourceType"));

		Assert.assertTrue(parameters.containsKey("localizedObjectField"));
		Assert.assertFalse((boolean)parameters.get("localizedObjectField"));

		Assert.assertTrue(parameters.containsKey("multiple"));
		Assert.assertFalse((boolean)parameters.get("multiple"));

		Assert.assertTrue(parameters.containsKey("options"));

		List<Object> options = (List<Object>)parameters.get("options");

		Assert.assertEquals(options.toString(), 3, options.size());

		Map<String, String> optionMap = (Map<String, String>)options.get(0);

		Assert.assertEquals("Label 1", optionMap.get("label"));
		Assert.assertEquals("value 1", optionMap.get("value"));

		optionMap = (Map<String, String>)options.get(1);

		Assert.assertEquals("Label 2", optionMap.get("label"));
		Assert.assertEquals("value 2", optionMap.get("value"));

		optionMap = (Map<String, String>)options.get(2);

		Assert.assertEquals("Label 3", optionMap.get("label"));
		Assert.assertEquals("value 3", optionMap.get("value"));

		Assert.assertTrue((boolean)parameters.get("showEmptyOption"));

		List<String> value = (List<String>)parameters.get("value");

		Assert.assertEquals(value.toString(), 1, value.size());
		Assert.assertTrue(value.toString(), value.contains("value 1"));
	}

	@Test
	public void testGetParameters2() throws Exception {
		DDMFormField ddmFormField = new DDMFormField("field", "select");

		ddmFormField.setDDMForm(getDDMForm());
		ddmFormField.setMultiple(true);
		ddmFormField.setProperty("dataSourceType", "manual");
		ddmFormField.setProperty("localizedObjectField", true);
		ddmFormField.setProperty("showEmptyOption", false);

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			createDDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setValue(
			JSONUtil.put(
				"en_US", "value 1"
			).put(
				"pt_BR", "value 2"
			).toString());

		LocalizedValue predefinedValue = new LocalizedValue();

		predefinedValue.setDefaultLocale(LocaleUtil.US);
		predefinedValue.addString(LocaleUtil.US, "[\"value 2\",\"value 3\"]");

		ddmFormField.setPredefinedValue(predefinedValue);

		_setUpDDMFormFieldOptionsFactory(
			ddmFormField, ddmFormFieldRenderingContext);

		SelectDDMFormFieldTemplateContextContributor
			selectDDMFormFieldTemplateContextContributor = _createSpy();

		Map<String, Object> parameters =
			selectDDMFormFieldTemplateContextContributor.getParameters(
				ddmFormField, ddmFormFieldRenderingContext);

		Assert.assertTrue(parameters.containsKey("dataSourceType"));
		Assert.assertEquals("manual", parameters.get("dataSourceType"));

		Assert.assertTrue(parameters.containsKey("localizedObjectField"));
		Assert.assertTrue((boolean)parameters.get("localizedObjectField"));

		Assert.assertTrue(parameters.containsKey("multiple"));
		Assert.assertTrue((boolean)parameters.get("multiple"));

		Assert.assertTrue(parameters.containsKey("options"));

		List<Object> options = (List<Object>)parameters.get("options");

		Assert.assertEquals(options.toString(), 3, options.size());

		Map<String, String> optionMap = (Map<String, String>)options.get(0);

		Assert.assertEquals("Label 1", optionMap.get("label"));
		Assert.assertEquals("value 1", optionMap.get("value"));

		optionMap = (Map<String, String>)options.get(1);

		Assert.assertEquals("Label 2", optionMap.get("label"));
		Assert.assertEquals("value 2", optionMap.get("value"));

		optionMap = (Map<String, String>)options.get(2);

		Assert.assertEquals("Label 3", optionMap.get("label"));
		Assert.assertEquals("value 3", optionMap.get("value"));

		List<String> predefinedValueParameter = (List<String>)parameters.get(
			"predefinedValue");

		Assert.assertEquals(
			predefinedValueParameter.toString(), 2,
			predefinedValueParameter.size());
		Assert.assertTrue(
			predefinedValueParameter.toString(),
			predefinedValueParameter.contains("value 2"));
		Assert.assertTrue(
			predefinedValueParameter.toString(),
			predefinedValueParameter.contains("value 3"));

		Assert.assertFalse((boolean)parameters.get("showEmptyOption"));

		JSONAssert.assertEquals(
			JSONUtil.put(
				"en_US",
				JSONFactoryUtil.createJSONArray(
					Collections.singletonList("value 1"))
			).put(
				"pt_BR",
				JSONFactoryUtil.createJSONArray(
					Collections.singletonList("value 2"))
			).toString(),
			String.valueOf(parameters.get("value")), false);
	}

	@Test
	public void testGetValue1() {
		List<String> values =
			_selectDDMFormFieldTemplateContextContributor.getValue(
				"[\"a\",\"b\"]");

		Assert.assertTrue(values.toString(), values.contains("a"));
		Assert.assertTrue(values.toString(), values.contains("b"));
	}

	@Test
	public void testGetValue2() {
		List<String> values =
			_selectDDMFormFieldTemplateContextContributor.getValue("value");

		Assert.assertTrue(values.toString(), values.contains("value"));
	}

	private DDMFormField _createDDMFormField(boolean multiple) {
		DDMFormField ddmFormField = new DDMFormField(
			RandomTestUtil.randomString(), DDMFormFieldTypeConstants.SELECT);

		ddmFormField.setProperty("multiple", multiple);

		return ddmFormField;
	}

	private SelectDDMFormFieldTemplateContextContributor _createSpy() {
		SelectDDMFormFieldTemplateContextContributor
			selectDDMFormFieldTemplateContextContributor = Mockito.spy(
				_selectDDMFormFieldTemplateContextContributor);

		Mockito.doReturn(
			_resourceBundle
		).when(
			selectDDMFormFieldTemplateContextContributor
		).getResourceBundle(
			Mockito.any(Locale.class)
		);

		return selectDDMFormFieldTemplateContextContributor;
	}

	private List<Map<String, Object>> _getActualOptions(
		DDMFormField ddmFormField, DDMFormFieldOptions ddmFormFieldOptions,
		Locale locale) {

		return _selectDDMFormFieldTemplateContextContributor.getOptions(
			ddmFormField, ddmFormFieldOptions, locale, null);
	}

	private ListTypeEntry _getListTypeEntry(String name) {
		ListTypeEntry listTypeEntry = Mockito.mock(ListTypeEntry.class);

		Mockito.when(
			listTypeEntry.getKey()
		).thenReturn(
			StringUtil.removeChars(name, CharPool.SPACE)
		);

		Mockito.when(
			listTypeEntry.getNameMap()
		).thenReturn(
			HashMapBuilder.put(
				LocaleUtil.US, name
			).build()
		);

		return listTypeEntry;
	}

	private void _mockListTypeEntry(
		String key, Long listTypeDefinitionId, Map<Locale, String> nameMap) {

		ListTypeEntry listTypeEntry = Mockito.mock(ListTypeEntry.class);

		Mockito.when(
			listTypeEntry.getNameMap()
		).thenReturn(
			nameMap
		);

		Mockito.when(
			_listTypeEntryLocalService.fetchListTypeEntry(
				Mockito.eq(listTypeDefinitionId), Mockito.eq(key))
		).thenReturn(
			listTypeEntry
		);
	}

	private void _setUpDDMFormFieldOptionsFactory(
			DDMFormField ddmFormField,
			DDMFormFieldRenderingContext ddmFormFieldRenderingContext)
		throws Exception {

		ReflectionTestUtil.setFieldValue(
			_selectDDMFormFieldTemplateContextContributor,
			"ddmFormFieldOptionsFactory", _ddmFormFieldOptionsFactory);

		DDMFormFieldOptions ddmFormFieldOptions =
			DDMFormFieldOptionsTestUtil.createDDMFormFieldOptions();

		Mockito.when(
			_ddmFormFieldOptionsFactory.create(
				ddmFormField, ddmFormFieldRenderingContext)
		).thenReturn(
			ddmFormFieldOptions
		);
	}

	private void _setUpDDMFormInstanceLocalService() throws Exception {
		Mockito.when(
			_ddmFormInstanceLocalService.fetchDDMFormInstance(0)
		).thenReturn(
			null
		);

		ReflectionTestUtil.setFieldValue(
			_selectDDMFormFieldTemplateContextContributor,
			"_ddmFormInstanceLocalService", _ddmFormInstanceLocalService);
	}

	private void _setUpJSONFactory() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_selectDDMFormFieldTemplateContextContributor, "jsonFactory",
			_jsonFactory);
	}

	private void _setUpLocaleThreadLocal() {
		LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.US);
	}

	private final DDMFormFieldOptionsFactory _ddmFormFieldOptionsFactory =
		Mockito.mock(DDMFormFieldOptionsFactory.class);
	private final DDMFormInstanceLocalService _ddmFormInstanceLocalService =
		Mockito.mock(DDMFormInstanceLocalService.class);
	private final JSONFactory _jsonFactory = new JSONFactoryImpl();
	private final ListTypeEntryLocalService _listTypeEntryLocalService =
		Mockito.mock(ListTypeEntryLocalService.class);
	private final ObjectDefinitionLocalService _objectDefinitionLocalService =
		Mockito.mock(ObjectDefinitionLocalService.class);
	private final ObjectFieldLocalService _objectFieldLocalService =
		Mockito.mock(ObjectFieldLocalService.class);
	private final ResourceBundle _resourceBundle = Mockito.mock(
		ResourceBundle.class);
	private final SelectDDMFormFieldTemplateContextContributor
		_selectDDMFormFieldTemplateContextContributor =
			new SelectDDMFormFieldTemplateContextContributor();

}