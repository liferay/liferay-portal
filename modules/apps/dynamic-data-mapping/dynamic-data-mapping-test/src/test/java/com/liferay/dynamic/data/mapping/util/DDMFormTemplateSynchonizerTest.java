/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.util;

import com.liferay.dynamic.data.mapping.BaseDDMTestCase;
import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.internal.util.DDMFormTemplateSynchonizer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializerSerializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializerSerializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.impl.DDMTemplateImpl;
import com.liferay.dynamic.data.mapping.service.impl.DDMTemplateLocalServiceImpl;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Marcellus Tavares
 */
public class DDMFormTemplateSynchonizerTest extends BaseDDMTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		setUpConfigurationFactoryUtil();
		setUpDDMFormJSONSerializer();
		setUpDDMFormJSONDeserializer();
		setUpJSONFactoryUtil();
		setUpLanguageUtil();
		setUpSAXReaderUtil();
	}

	@Test
	public void testAddRequiredFieldsIfTheyWereAddedInStructureAndModeIsCreate()
		throws Exception {

		DDMForm structureDDMForm = createDDMForm();

		addDDMFormField(
			structureDDMForm, createTextDDMFormField("name1", false));

		createFormTemplates(structureDDMForm);

		addDDMFormField(
			structureDDMForm, createTextDDMFormField("name2", true));

		DDMFormTemplateSynchonizer ddmFormTemplateSynchonizer =
			new MockDDMFormTemplateSynchronizer(structureDDMForm);

		ddmFormTemplateSynchonizer.synchronize();

		testFormTemplatesAfterAddRequiredFields();
	}

	@Test
	public void testRemoveTemplateFieldsIfTheyWereRemovedFromStructure()
		throws Exception {

		DDMForm structureDDMForm = createDDMForm();

		addDDMFormField(
			structureDDMForm, createTextDDMFormField("name1", false));
		addDDMFormField(
			structureDDMForm, createTextDDMFormField("name2", false));

		createFormTemplates(structureDDMForm);

		removeDDMFormField(structureDDMForm, "name1");

		DDMFormTemplateSynchonizer ddmFormTemplateSynchonizer =
			new MockDDMFormTemplateSynchronizer(structureDDMForm);

		ddmFormTemplateSynchonizer.synchronize();

		testFormTemplatesAfterRemoveFields();
	}

	@Test
	public void testUpdateDDMFormFieldOptionsIfTheyWereModifiedFromStructure()
		throws Exception {

		DDMForm structureDDMForm = createDDMForm();

		DDMFormField selectDDMFormField = createSelectDDMFormField(
			"Select", false, 2);

		addDDMFormField(structureDDMForm, selectDDMFormField);

		DDMFormField radioDDMFormField = createSelectDDMFormField(
			"Radio", true, 3);

		addDDMFormField(structureDDMForm, radioDDMFormField);

		createFormTemplates(structureDDMForm);

		selectDDMFormField.setDDMFormFieldOptions(createDDMFormFieldOptions(1));

		radioDDMFormField.setDDMFormFieldOptions(createDDMFormFieldOptions(2));

		DDMFormTemplateSynchonizer ddmFormTemplateSynchonizer =
			new MockDDMFormTemplateSynchronizer(structureDDMForm);

		ddmFormTemplateSynchonizer.synchronize();

		testFormTemplatesAfterChangeDDMFormFieldOptionsProperty(
			structureDDMForm, "Select", "Radio");
	}

	@Test
	public void testUpdateRequiredPropertyIfTemplateModeIsCreate()
		throws Exception {

		DDMForm structureDDMForm = createDDMForm();

		addDDMFormField(
			structureDDMForm, createTextDDMFormField("name1", false));
		addDDMFormField(
			structureDDMForm, createTextDDMFormField("name2", true));

		createFormTemplates(structureDDMForm);

		updateDDMFormFieldRequiredProperty(structureDDMForm, "name1", true);
		updateDDMFormFieldRequiredProperty(structureDDMForm, "name2", false);

		DDMFormTemplateSynchonizer ddmFormTemplateSynchonizer =
			new MockDDMFormTemplateSynchronizer(structureDDMForm);

		ddmFormTemplateSynchonizer.synchronize();

		testFormTemplatesAfterUpdateRequiredFieldProperties();
	}

	protected void addDDMFormField(DDMForm ddmForm, DDMFormField ddmFormField) {
		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		ddmFormFields.add(ddmFormField);
	}

	protected DDMForm createDDMForm() {
		DDMForm ddmForm = new DDMForm();

		ddmForm.setAvailableLocales(createAvailableLocales(LocaleUtil.US));
		ddmForm.setDefaultLocale(LocaleUtil.US);

		return ddmForm;
	}

	protected DDMFormFieldOptions createDDMFormFieldOptions(
		int availableOptions) {

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		for (int i = 1; i <= availableOptions; i++) {
			ddmFormFieldOptions.addOptionLabel(
				"Value " + i, LocaleUtil.US, "Option " + i);
		}

		return ddmFormFieldOptions;
	}

	protected void createFormTemplates(DDMForm ddmForm) {
		DDMFormSerializerSerializeRequest.Builder builder =
			DDMFormSerializerSerializeRequest.Builder.newBuilder(ddmForm);

		DDMFormSerializerSerializeResponse ddmFormSerializerSerializeResponse =
			ddmFormJSONSerializer.serialize(builder.build());

		_createDDMTemplate = createTemplate(
			RandomTestUtil.randomLong(), "Test Create Mode Form Template",
			DDMTemplateConstants.TEMPLATE_MODE_CREATE,
			ddmFormSerializerSerializeResponse.getContent());

		_editDDMTemplate = createTemplate(
			RandomTestUtil.randomLong(), "Test Edit Mode Form Template",
			DDMTemplateConstants.TEMPLATE_MODE_EDIT,
			ddmFormSerializerSerializeResponse.getContent());
	}

	protected DDMFormField createRadioDDMFormField(
		String name, boolean required, int availableOptions) {

		DDMFormField ddmFormField = new DDMFormField(name, "radio");

		ddmFormField.setDataType("string");
		ddmFormField.setRequired(required);

		LocalizedValue label = ddmFormField.getLabel();

		label.addString(LocaleUtil.US, StringUtil.randomString());

		ddmFormField.setDDMFormFieldOptions(
			createDDMFormFieldOptions(availableOptions));

		return ddmFormField;
	}

	protected DDMFormField createSelectDDMFormField(
		String name, boolean required, int availableOptions) {

		DDMFormField ddmFormField = new DDMFormField(name, "select");

		ddmFormField.setDataType("string");
		ddmFormField.setRequired(required);

		LocalizedValue label = ddmFormField.getLabel();

		label.addString(LocaleUtil.US, StringUtil.randomString());

		ddmFormField.setDDMFormFieldOptions(
			createDDMFormFieldOptions(availableOptions));

		return ddmFormField;
	}

	@Override
	protected DDMTemplate createTemplate(
		long templateId, String name, String mode, String script) {

		DDMTemplate template = new DDMTemplateImpl();

		template.setTemplateId(templateId);
		template.setName(name);
		template.setMode(mode);
		template.setScript(script);

		return template;
	}

	protected DDMFormField createTextDDMFormField(
		String name, boolean required) {

		DDMFormField ddmFormField = new DDMFormField(name, "text");

		ddmFormField.setDataType("string");
		ddmFormField.setRequired(required);

		LocalizedValue label = ddmFormField.getLabel();

		label.addString(LocaleUtil.US, StringUtil.randomString());

		return ddmFormField;
	}

	protected Map<String, DDMFormField> getDDMFormFieldsMap(
			DDMTemplate ddmTemplate)
		throws Exception {

		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(
				ddmTemplate.getScript());

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				ddmFormJSONDeserializer.deserialize(builder.build());

		DDMForm ddmForm = ddmFormDeserializerDeserializeResponse.getDDMForm();

		return ddmForm.getDDMFormFieldsMap(true);
	}

	protected void removeDDMFormField(DDMForm ddmForm, String fieldName) {
		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		Iterator<DDMFormField> iterator = ddmFormFields.iterator();

		while (iterator.hasNext()) {
			DDMFormField ddmFormField = iterator.next();

			if (!fieldName.equals(ddmFormField.getName())) {
				continue;
			}

			iterator.remove();
		}
	}

	protected void testFormTemplatesAfterAddRequiredFields() throws Exception {

		// Create

		Map<String, DDMFormField> ddmFormFieldsMap = getDDMFormFieldsMap(
			_createDDMTemplate);

		DDMFormField ddmFormField = ddmFormFieldsMap.get("name2");

		Assert.assertNotNull(ddmFormField);
		Assert.assertTrue(ddmFormField.isRequired());

		// Edit

		ddmFormFieldsMap = getDDMFormFieldsMap(_editDDMTemplate);

		ddmFormField = ddmFormFieldsMap.get("name2");

		Assert.assertNull(ddmFormField);
	}

	protected void testFormTemplatesAfterChangeDDMFormFieldOptionsProperty(
			DDMForm structureForm, String... fields)
		throws Exception {

		Map<String, DDMFormField> structureDDMFormFieldsMap =
			structureForm.getDDMFormFieldsMap(true);

		Map<String, DDMFormField> createTemplateDDMFormFieldsMap =
			getDDMFormFieldsMap(_createDDMTemplate);

		Map<String, DDMFormField> editTemplateDDMFormFieldsMap =
			getDDMFormFieldsMap(_editDDMTemplate);

		for (String field : fields) {
			DDMFormField structureDDMFormField = structureDDMFormFieldsMap.get(
				field);

			DDMFormFieldOptions structureDDMFormFieldOptions =
				structureDDMFormField.getDDMFormFieldOptions();

			// Create

			DDMFormField templateDDMFormField =
				createTemplateDDMFormFieldsMap.get(field);

			DDMFormFieldOptions templateDDMFormFieldOptions =
				templateDDMFormField.getDDMFormFieldOptions();

			Assert.assertEquals(
				structureDDMFormFieldOptions, templateDDMFormFieldOptions);

			// Edit

			templateDDMFormField = editTemplateDDMFormFieldsMap.get(field);

			templateDDMFormFieldOptions =
				templateDDMFormField.getDDMFormFieldOptions();

			Assert.assertEquals(
				structureDDMFormFieldOptions, templateDDMFormFieldOptions);
		}
	}

	protected void testFormTemplatesAfterRemoveFields() throws Exception {

		// Create

		Map<String, DDMFormField> ddmFormFieldsMap = getDDMFormFieldsMap(
			_createDDMTemplate);

		DDMFormField ddmFormField = ddmFormFieldsMap.get("name1");

		Assert.assertNull(ddmFormField);

		// Edit

		ddmFormFieldsMap = getDDMFormFieldsMap(_editDDMTemplate);

		ddmFormField = ddmFormFieldsMap.get("name1");

		Assert.assertNull(ddmFormField);
	}

	protected void testFormTemplatesAfterUpdateRequiredFieldProperties()
		throws Exception {

		// Create

		Map<String, DDMFormField> ddmFormFieldsMap = getDDMFormFieldsMap(
			_createDDMTemplate);

		DDMFormField name1DDMFormField = ddmFormFieldsMap.get("name1");
		DDMFormField name2DDMFormField = ddmFormFieldsMap.get("name2");

		Assert.assertTrue(name1DDMFormField.isRequired());
		Assert.assertFalse(name2DDMFormField.isRequired());

		// Edit

		ddmFormFieldsMap = getDDMFormFieldsMap(_editDDMTemplate);

		name1DDMFormField = ddmFormFieldsMap.get("name1");
		name2DDMFormField = ddmFormFieldsMap.get("name2");

		Assert.assertFalse(name1DDMFormField.isRequired());
		Assert.assertTrue(name2DDMFormField.isRequired());
	}

	protected void updateDDMFormFieldRequiredProperty(
		DDMForm ddmForm, String fieldName, boolean required) {

		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		for (DDMFormField ddmFormField : ddmFormFields) {
			if (!fieldName.equals(ddmFormField.getName())) {
				continue;
			}

			ddmFormField.setRequired(required);
		}
	}

	private DDMTemplate _createDDMTemplate;
	private DDMTemplate _editDDMTemplate;

	private class MockDDMFormTemplateSynchronizer
		extends DDMFormTemplateSynchonizer {

		public MockDDMFormTemplateSynchronizer(DDMForm structureDDMForm) {
			super(
				structureDDMForm, ddmFormJSONDeserializer,
				ddmFormJSONSerializer, new DDMTemplateLocalServiceImpl());
		}

		@Override
		protected List<DDMTemplate> getDDMFormTemplates() {
			return ListUtil.fromArray(_createDDMTemplate, _editDDMTemplate);
		}

		@Override
		protected void updateDDMTemplate(
			DDMTemplate ddmTemplate, DDMForm templateDDMForm) {

			DDMFormSerializerSerializeRequest.Builder builder =
				DDMFormSerializerSerializeRequest.Builder.newBuilder(
					templateDDMForm);

			DDMFormSerializerSerializeResponse
				ddmFormSerializerSerializeResponse =
					ddmFormJSONSerializer.serialize(builder.build());

			ddmTemplate.setScript(
				ddmFormSerializerSerializeResponse.getContent());

			if (Objects.equals(
					ddmTemplate.getMode(),
					DDMTemplateConstants.TEMPLATE_MODE_CREATE)) {

				_createDDMTemplate = ddmTemplate;
			}
			else {
				_editDDMTemplate = ddmTemplate;
			}
		}

	}

}