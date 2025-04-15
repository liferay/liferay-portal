/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.renderer.internal;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunctionFactory;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunctionRegistry;
import com.liferay.dynamic.data.mapping.expression.internal.DDMExpressionFactoryImpl;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluator;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.DDMFormEvaluatorImpl;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.function.HasGooglePlacesAPIKeyFunction;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.function.JumpPageFunction;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.function.SetVisibleFunction;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueAccessor;
import com.liferay.dynamic.data.mapping.form.field.type.DefaultDDMFormFieldValueAccessor;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.renderer.internal.helper.DDMFormFieldTemplateContextContributorTestHelper;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormRule;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMStructureLayoutLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormLayoutTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.FastDateFormatFactoryImpl;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Marcellus Tavares
 */
public class DDMFormPagesTemplateContextFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_setUpDDMFormFieldTypeServicesRegistry();
		_setUpFastDateFormatFactoryUtil();
		_setUpGooglePlacesUtil();
		_setUpHtmlParser();
		_setUpHttpServletRequest();
		setUpLanguageUtil();
		_setUpLocaleThreadLocal();
		_setUpPortalUtil();
		_setUpResourceBundle();
		_setUpResourceBundleLoaderUtil();
		_setUpResourceBundleUtil();
	}

	@Test
	public void testCheckboxMultipleFieldTemplateContext() throws Exception {

		// Dynamic data mapping form

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		String formFieldLabel = String.format(_HTML_WRAPPER, "label");
		String formFieldTip = String.format(_HTML_WRAPPER, "tip");
		String formFieldOption = String.format(_HTML_WRAPPER, "option");

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				"Field1", formFieldLabel, "checkbox-multiple", "string", false,
				false, true, formFieldTip, formFieldOption));

		mockDDMFormFieldTypeServicesRegistry(
			"checkbox-multiple",
			_ddmFormFieldTemplateContextContributorTestHelper.
				createCheckboxMultipleDDMFormFieldTemplateContextContributor());

		// Template context

		DDMFormPagesTemplateContextFactory ddmFormPagesTemplateContextFactory =
			_createDDMFormPagesTemplateContextFactory(
				ddmForm,
				DDMFormLayoutTestUtil.createDDMFormLayout(
					"Page 1 Description", "Page 1", new String[] {"Field1"}),
				null, false, true, true);

		List<Object> pages = ddmFormPagesTemplateContextFactory.create();

		Map<String, Object> fieldTemplateContext = _getFieldTemplateContext(
			pages);

		Assert.assertEquals(formFieldLabel, fieldTemplateContext.get("label"));

		List<Map<String, String>> options =
			(List<Map<String, String>>)fieldTemplateContext.get("options");

		Map<String, String> optionField = options.get(0);

		Assert.assertEquals(formFieldOption, optionField.get("label"));

		Assert.assertEquals(formFieldTip, fieldTemplateContext.get("tip"));
	}

	@Test
	public void testDateFieldTemplateContext() throws Exception {

		// Dynamic data mapping form

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		String formFieldLabel = String.format(_HTML_WRAPPER, "label");
		String formFieldTip = String.format(_HTML_WRAPPER, "tip");

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				"Field1", formFieldLabel, "date", "string", false, false, true,
				formFieldTip));

		mockDDMFormFieldTypeServicesRegistry(
			"date",
			_ddmFormFieldTemplateContextContributorTestHelper.
				createDateDDMFormFieldTemplateContextContributor());

		// Template context

		DDMFormPagesTemplateContextFactory ddmFormPagesTemplateContextFactory =
			_createDDMFormPagesTemplateContextFactory(
				ddmForm,
				DDMFormLayoutTestUtil.createDDMFormLayout(
					"Page 1 Description", "Page 1", new String[] {"Field1"}),
				null, false, true, true);

		List<Object> pages = ddmFormPagesTemplateContextFactory.create();

		Map<String, Object> fieldTemplateContext = _getFieldTemplateContext(
			pages);

		Assert.assertEquals(formFieldLabel, fieldTemplateContext.get("label"));
		Assert.assertEquals(formFieldTip, fieldTemplateContext.get("tip"));
	}

	@Test
	public void testDisablePages() throws Exception {

		// Dynamic data mapping form

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		ddmForm.addDDMFormRule(
			new DDMFormRule(Arrays.asList("jumpPage(0, 2)"), "TRUE"));

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				"Field1", "Field1", "text", "string", false, false, true));

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				"Field2", "Field2", "text", "string", false, false, false));

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				"Field3", "Field3", "text", "string", false, false, false));

		// Dynamic data mapping form values

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"Field1", "A"));

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"Field2", ""));

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"Field3", ""));

		// Template context

		DDMFormPagesTemplateContextFactory ddmFormPagesTemplateContextFactory =
			_createDDMFormPagesTemplateContextFactory(
				ddmForm,
				DDMFormLayoutTestUtil.createDDMFormLayout(
					DDMFormLayoutTestUtil.createDDMFormLayoutPage(
						"Page 1 Description", "Page 1",
						new String[] {"Field1"}),
					DDMFormLayoutTestUtil.createDDMFormLayoutPage(
						"Page 2 Description", "Page 2",
						new String[] {"Field2"}),
					DDMFormLayoutTestUtil.createDDMFormLayoutPage(
						"Page 3 Description", "Page 3",
						new String[] {"Field3"})),
				ddmFormValues, false, false, false);

		mockDDMFormFieldTypeServicesRegistry(
			"text",
			_ddmFormFieldTemplateContextContributorTestHelper.
				createTextDDMFormFieldTemplateContextContributor());

		List<Object> pagesTemplateContext =
			ddmFormPagesTemplateContextFactory.create();

		Assert.assertEquals(
			pagesTemplateContext.toString(), 3, pagesTemplateContext.size());

		Map<String, Object> page1TemplateContext =
			(Map<String, Object>)pagesTemplateContext.get(0);

		Assert.assertTrue(MapUtil.getBoolean(page1TemplateContext, "enabled"));

		Map<String, Object> page2TemplateContext =
			(Map<String, Object>)pagesTemplateContext.get(1);

		Assert.assertFalse(MapUtil.getBoolean(page2TemplateContext, "enabled"));

		Map<String, Object> page3TemplateContext =
			(Map<String, Object>)pagesTemplateContext.get(2);

		Assert.assertTrue(MapUtil.getBoolean(page3TemplateContext, "enabled"));
	}

	@Test
	public void testGridFieldTemplateContext() throws Exception {

		// Dynamic data mapping form

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		String formFieldLabel = String.format(_HTML_WRAPPER, "label");
		String formFieldTip = String.format(_HTML_WRAPPER, "tip");
		String formFieldOption = String.format(_HTML_WRAPPER, "option");

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createGridDDMFormField(
				"Field1", formFieldLabel, "grid", "string", false, false, true,
				formFieldTip, formFieldOption));

		mockDDMFormFieldTypeServicesRegistry(
			"grid",
			_ddmFormFieldTemplateContextContributorTestHelper.
				createGridDDMFormFieldTemplateContextContributor());

		// Template context

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"Field1", new UnlocalizedValue("{}")));

		DDMFormPagesTemplateContextFactory ddmFormPagesTemplateContextFactory =
			_createDDMFormPagesTemplateContextFactory(
				ddmForm,
				DDMFormLayoutTestUtil.createDDMFormLayout(
					"Page 1 Description", "Page 1", new String[] {"Field1"}),
				ddmFormValues, false, true, true);

		List<Object> pages = ddmFormPagesTemplateContextFactory.create();

		Map<String, Object> fieldTemplateContext = _getFieldTemplateContext(
			pages);

		Assert.assertEquals(formFieldLabel, fieldTemplateContext.get("label"));

		List<Map<String, String>> columns =
			(List<Map<String, String>>)fieldTemplateContext.get("columns");

		Map<String, String> columnField = columns.get(0);

		Assert.assertEquals(formFieldOption, columnField.get("label"));

		List<Map<String, String>> rows =
			(List<Map<String, String>>)fieldTemplateContext.get("rows");

		Map<String, String> rowField = rows.get(0);

		Assert.assertEquals(formFieldOption, rowField.get("label"));

		Assert.assertEquals(formFieldTip, fieldTemplateContext.get("tip"));
	}

	@Test
	public void testInvisibleFieldByGooglePlacesAPIKey() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm("Field1");

		ddmForm.addDDMFormRule(
			new DDMFormRule(
				Collections.singletonList(
					"setVisible('Field1', hasGooglePlacesAPIKey())"),
				"TRUE"));

		DDMFormPagesTemplateContextFactory ddmFormPagesTemplateContextFactory =
			_createDDMFormPagesTemplateContextFactory(
				ddmForm,
				DDMFormLayoutTestUtil.createDDMFormLayout(
					"Page 1 Description", "Page 1", new String[] {"Field1"}),
				null, false, true, true);

		Map<String, Object> fieldTemplateContext = _getFieldTemplateContext(
			ddmFormPagesTemplateContextFactory.create());

		Assert.assertFalse(
			GetterUtil.getBoolean(fieldTemplateContext.get("visible")));
	}

	@Test
	public void testNumericFieldTemplateContext() throws Exception {

		// Dynamic data mapping form

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		String formFieldLabel = String.format(_HTML_WRAPPER, "label");
		String formFieldTip = String.format(_HTML_WRAPPER, "tip");
		String formFieldPlaceholder = String.format(
			_HTML_WRAPPER, "placeHolder");
		String formFieldTooltip = String.format(_HTML_WRAPPER, "toolTip");

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createNumericDDMFormField(
				"Field1", formFieldLabel, "integer", false, false, true,
				formFieldTip, formFieldPlaceholder, formFieldTooltip));

		DDMFormFieldTemplateContextContributor
			ddmFormFieldTemplateContextContributor =
				_ddmFormFieldTemplateContextContributorTestHelper.
					createNumericDDMFormFieldTemplateContextContributor();

		ReflectionTestUtil.setFieldValue(
			ddmFormFieldTemplateContextContributor, "_htmlParser", _htmlParser);

		Mockito.when(
			_htmlParser.extractText(StringPool.BLANK)
		).thenReturn(
			StringPool.BLANK
		);

		mockDDMFormFieldTypeServicesRegistry(
			"numeric", ddmFormFieldTemplateContextContributor);

		// Template context

		DDMFormPagesTemplateContextFactory ddmFormPagesTemplateContextFactory =
			_createDDMFormPagesTemplateContextFactory(
				ddmForm,
				DDMFormLayoutTestUtil.createDDMFormLayout(
					"Page 1 Description", "Page 1", new String[] {"Field1"}),
				null, false, true, true);

		List<Object> pages = ddmFormPagesTemplateContextFactory.create();

		Map<String, Object> fieldTemplateContext = _getFieldTemplateContext(
			pages);

		Assert.assertEquals(formFieldLabel, fieldTemplateContext.get("label"));
		Assert.assertEquals(
			formFieldPlaceholder, fieldTemplateContext.get("placeholder"));
		Assert.assertEquals(formFieldTip, fieldTemplateContext.get("tip"));
		Assert.assertEquals(
			formFieldTooltip, fieldTemplateContext.get("tooltip"));
	}

	@Test
	public void testOnePageThreeRows() throws Exception {

		// Dynamic data mapping form

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			"Field1", "Field2", "Field3", "Field4", "Field5");

		// Template context

		DDMFormPagesTemplateContextFactory ddmFormPagesTemplateContextFactory =
			_createDDMFormPagesTemplateContextFactory(
				ddmForm,
				DDMFormLayoutTestUtil.createDDMFormLayout(
					DDMFormLayoutTestUtil.createDDMFormLayoutPage(
						"Page 1 Description", "Page 1",
						DDMFormLayoutTestUtil.createDDMFormLayoutRow(
							DDMFormLayoutTestUtil.createDDMFormLayoutColumns(
								"Field1", "Field2")),
						DDMFormLayoutTestUtil.createDDMFormLayoutRow("Field3"),
						DDMFormLayoutTestUtil.createDDMFormLayoutRow(
							"Field4", "Field5"))),
				null, false, true, false);

		List<Object> pages = ddmFormPagesTemplateContextFactory.create();

		Assert.assertEquals(pages.toString(), 1, pages.size());

		Map<String, Object> page1 = (Map<String, Object>)pages.get(0);

		Assert.assertEquals("Page 1", page1.get("title"));
		Assert.assertEquals("Page 1 Description", page1.get("description"));

		List<Object> rows = (List<Object>)page1.get("rows");

		Assert.assertEquals(rows.toString(), 3, rows.size());

		Map<String, Object> row1 = (Map<String, Object>)rows.get(0);

		List<Object> columnsRow1 = (List<Object>)row1.get("columns");

		Assert.assertEquals(columnsRow1.toString(), 2, columnsRow1.size());

		_assertColumnSize(6, (Map<String, Object>)columnsRow1.get(0));
		_assertColumnSize(6, (Map<String, Object>)columnsRow1.get(1));

		Map<String, Object> row2 = (Map<String, Object>)rows.get(1);

		List<Object> columnsRow2 = (List<Object>)row2.get("columns");

		Assert.assertEquals(columnsRow2.toString(), 1, columnsRow2.size());

		_assertColumnSize(12, (Map<String, Object>)columnsRow2.get(0));

		Map<String, Object> row3 = (Map<String, Object>)rows.get(2);

		List<Object> columnsRow3 = (List<Object>)row3.get("columns");

		Assert.assertEquals(columnsRow3.toString(), 1, columnsRow3.size());

		_assertColumnSize(12, (Map<String, Object>)columnsRow3.get(0));
	}

	@Test
	public void testPageDescription() throws Exception {

		// Dynamic data mapping form

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		String descriptionPage = String.format(
			_HTML_WRAPPER, "descriptionPage");

		// Template context

		DDMFormPagesTemplateContextFactory ddmFormPagesTemplateContextFactory =
			_createDDMFormPagesTemplateContextFactory(
				ddmForm,
				DDMFormLayoutTestUtil.createDDMFormLayout(
					descriptionPage, "titlePage", null),
				null, false, true, true);

		List<Object> pages = ddmFormPagesTemplateContextFactory.create();

		Map<String, Object> pageTemplateContext =
			(Map<String, Object>)pages.get(0);

		Assert.assertEquals(
			descriptionPage, pageTemplateContext.get("description"));
	}

	@Test
	public void testPageTitle() throws Exception {

		// Dynamic data mapping form

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		String pageTitle = String.format(_HTML_WRAPPER, "titlePage");

		// Template context

		DDMFormPagesTemplateContextFactory ddmFormPagesTemplateContextFactory =
			_createDDMFormPagesTemplateContextFactory(
				ddmForm,
				DDMFormLayoutTestUtil.createDDMFormLayout(
					"descriptionPage", pageTitle, null),
				null, false, true, true);

		List<Object> pages = ddmFormPagesTemplateContextFactory.create();

		Map<String, Object> pageTemplateContext =
			(Map<String, Object>)pages.get(0);

		Assert.assertEquals(pageTitle, pageTemplateContext.get("title"));
	}

	@Test
	public void testRadioFieldTemplateContext() throws Exception {

		// Dynamic data mapping form

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		String formFieldLabel = String.format(_HTML_WRAPPER, "label");
		String formFieldOption = String.format(_HTML_WRAPPER, "option");
		String formFieldPredefinedValue = StringBundler.concat(
			StringPool.OPEN_BRACKET,
			String.format(_HTML_WRAPPER, "predefinedValue"),
			StringPool.CLOSE_BRACKET);
		String formFieldTip = String.format(_HTML_WRAPPER, "tip");

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				"Field1", formFieldLabel, "radio", "string", false, false, true,
				formFieldTip, formFieldPredefinedValue, formFieldOption));

		mockDDMFormFieldTypeServicesRegistry(
			"radio",
			_ddmFormFieldTemplateContextContributorTestHelper.
				createRadioDDMFormFieldTemplateContextContributor());

		// Template context

		DDMFormPagesTemplateContextFactory ddmFormPagesTemplateContextFactory =
			_createDDMFormPagesTemplateContextFactory(
				ddmForm,
				DDMFormLayoutTestUtil.createDDMFormLayout(
					"Page 1 Description", "Page 1", new String[] {"Field1"}),
				null, false, true, false);

		List<Object> pages = ddmFormPagesTemplateContextFactory.create();

		Map<String, Object> fieldTemplateContext = _getFieldTemplateContext(
			pages);

		List<Map<String, String>> options =
			(List<Map<String, String>>)fieldTemplateContext.get("options");

		Map<String, String> optionField = options.get(0);

		Assert.assertEquals(formFieldLabel, fieldTemplateContext.get("label"));
		Assert.assertEquals(formFieldOption, optionField.get("label"));

		Object predefinedValue = fieldTemplateContext.get("predefinedValue");

		Assert.assertEquals(
			formFieldPredefinedValue, predefinedValue.toString());

		Assert.assertEquals(formFieldTip, fieldTemplateContext.get("tip"));
	}

	@Test
	public void testRequiredFieldsWithoutRequiredFieldsWarning()
		throws Exception {

		// Dynamic data mapping form

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				"Field1", "Field1", "text", "string", false, false, true));

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				"Field2", "Field2", "text", "string", false, false, false));

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				"Field3", "Field3", "text", "string", false, false, false));

		// Template context

		DDMFormPagesTemplateContextFactory ddmFormPagesTemplateContextFactory =
			_createDDMFormPagesTemplateContextFactory(
				ddmForm,
				DDMFormLayoutTestUtil.createDDMFormLayout(
					DDMFormLayoutTestUtil.createDDMFormLayoutPage(
						"Page 1 Description", "Page 1",
						DDMFormLayoutTestUtil.createDDMFormLayoutColumns(
							"Field1", "Field2")),
					DDMFormLayoutTestUtil.createDDMFormLayoutPage(
						"Page 2 Description", "Page 2",
						new String[] {"Field3"})),
				null, false, false, false);

		List<Object> pagesTemplateContext =
			ddmFormPagesTemplateContextFactory.create();

		Assert.assertEquals(
			pagesTemplateContext.toString(), 2, pagesTemplateContext.size());

		Map<String, Object> page1TemplateContext =
			(Map<String, Object>)pagesTemplateContext.get(0);

		Assert.assertFalse(
			MapUtil.getBoolean(
				page1TemplateContext, "showRequiredFieldsWarning"));

		Map<String, Object> page2TemplateContext =
			(Map<String, Object>)pagesTemplateContext.get(1);

		Assert.assertFalse(
			MapUtil.getBoolean(
				page2TemplateContext, "showRequiredFieldsWarning"));
	}

	@Test
	public void testRequiredFieldsWithRequiredFieldsWarning() throws Exception {

		// Dynamic data mapping form

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				"Field1", "Field1", "text", "string", false, false, true));

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				"Field2", "Field2", "text", "string", false, false, false));

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				"Field3", "Field3", "text", "string", false, false, false));

		// Template context

		DDMFormPagesTemplateContextFactory ddmFormPagesTemplateContextFactory =
			_createDDMFormPagesTemplateContextFactory(
				ddmForm,
				DDMFormLayoutTestUtil.createDDMFormLayout(
					DDMFormLayoutTestUtil.createDDMFormLayoutPage(
						"Page 1 Description", "Page 1",
						DDMFormLayoutTestUtil.createDDMFormLayoutColumns(
							"Field1", "Field2")),
					DDMFormLayoutTestUtil.createDDMFormLayoutPage(
						"Page 2 Description", "Page 2",
						new String[] {"Field3"})),
				null, false, true, false);

		List<Object> pagesTemplateContext =
			ddmFormPagesTemplateContextFactory.create();

		Assert.assertEquals(
			pagesTemplateContext.toString(), 2, pagesTemplateContext.size());

		Map<String, Object> page1TemplateContext =
			(Map<String, Object>)pagesTemplateContext.get(0);

		Assert.assertTrue(
			MapUtil.getBoolean(
				page1TemplateContext, "showRequiredFieldsWarning"));

		Map<String, Object> page2TemplateContext =
			(Map<String, Object>)pagesTemplateContext.get(1);

		Assert.assertFalse(
			MapUtil.getBoolean(
				page2TemplateContext, "showRequiredFieldsWarning"));
	}

	@Test
	public void testSelectFieldTemplateContext() throws Exception {

		// Dynamic data mapping form

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		String formFieldLabel = String.format(_HTML_WRAPPER, "label");
		String formFieldOption = String.format(_HTML_WRAPPER, "option");
		String formFieldTip = String.format(_HTML_WRAPPER, "tip");

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				"Field1", formFieldLabel, "select", "string", false, false,
				true, formFieldTip, formFieldOption));

		mockDDMFormFieldTypeServicesRegistry(
			"select",
			_ddmFormFieldTemplateContextContributorTestHelper.
				createSelectDDMFormFieldTemplateContextContributor());

		// Template context

		DDMFormPagesTemplateContextFactory ddmFormPagesTemplateContextFactory =
			_createDDMFormPagesTemplateContextFactory(
				ddmForm,
				DDMFormLayoutTestUtil.createDDMFormLayout(
					"Page 1 Description", "Page 1", new String[] {"Field1"}),
				null, false, true, true);

		List<Object> pages = ddmFormPagesTemplateContextFactory.create();

		Map<String, Object> fieldTemplateContext = _getFieldTemplateContext(
			pages);

		Assert.assertEquals(formFieldLabel, fieldTemplateContext.get("label"));

		List<Map<String, String>> options =
			(List<Map<String, String>>)fieldTemplateContext.get("options");

		Map<String, String> optionField = options.get(0);

		Assert.assertEquals(formFieldOption, optionField.get("label"));

		Assert.assertEquals(formFieldTip, fieldTemplateContext.get("tip"));
	}

	@Test
	public void testTextFieldTemplateContext() throws Exception {

		// Dynamic data mapping form

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		String formFieldLabel = String.format(_HTML_WRAPPER, "label");
		String formFieldOption = String.format(_HTML_WRAPPER, "option");
		String formFieldPredefinedValue = String.format(
			_HTML_WRAPPER, "predefinedValue");
		String formFieldPlaceholder = String.format(
			_HTML_WRAPPER, "placeHolder");
		String formFieldTip = String.format(_HTML_WRAPPER, "tip");
		String formFieldTooltip = String.format(_HTML_WRAPPER, "toolTip");

		ddmForm.addDDMFormField(
			DDMFormTestUtil.createDDMFormField(
				"Field1", formFieldLabel, "text", "string", false, false, true,
				formFieldTip, formFieldPredefinedValue, formFieldPlaceholder,
				formFieldTooltip, formFieldOption));

		mockDDMFormFieldTypeServicesRegistry(
			"text",
			_ddmFormFieldTemplateContextContributorTestHelper.
				createTextDDMFormFieldTemplateContextContributor());

		// Template context

		DDMFormPagesTemplateContextFactory ddmFormPagesTemplateContextFactory =
			_createDDMFormPagesTemplateContextFactory(
				ddmForm,
				DDMFormLayoutTestUtil.createDDMFormLayout(
					"Page 1 Description", "Page 1", new String[] {"Field1"}),
				null, false, true, false);

		List<Object> pages = ddmFormPagesTemplateContextFactory.create();

		Map<String, Object> fieldTemplateContext = _getFieldTemplateContext(
			pages);

		Assert.assertEquals(formFieldLabel, fieldTemplateContext.get("label"));

		List<Map<String, String>> options =
			(List<Map<String, String>>)fieldTemplateContext.get("options");

		Map<String, String> optionField = options.get(0);

		Assert.assertEquals(formFieldOption, optionField.get("label"));

		Assert.assertEquals(
			formFieldPlaceholder, fieldTemplateContext.get("placeholder"));
		Assert.assertEquals(
			formFieldPredefinedValue,
			fieldTemplateContext.get("predefinedValue"));
		Assert.assertEquals(formFieldTip, fieldTemplateContext.get("tip"));
		Assert.assertEquals(
			formFieldTooltip, fieldTemplateContext.get("tooltip"));
	}

	protected static void setUpLanguageUtil() {
		Language language = Mockito.mock(Language.class);

		whenLanguageGet(
			language, LocaleUtil.US, "this-field-is-required",
			"This field is required.");

		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(language);
	}

	protected static void whenLanguageGet(
		Language language, Locale locale, String key, String returnValue) {

		Mockito.when(
			language.get(Mockito.any(ResourceBundle.class), Mockito.eq(key))
		).thenReturn(
			returnValue
		);
	}

	protected void mockDDMFormFieldTypeServicesRegistry(
		String type,
		DDMFormFieldTemplateContextContributor
			ddmFormFieldTemplateContextContributor) {

		Mockito.when(
			_ddmFormFieldTypeServicesRegistry.
				getDDMFormFieldTemplateContextContributor(Mockito.eq(type))
		).thenReturn(
			ddmFormFieldTemplateContextContributor
		);
	}

	private static void _setUpDDMFormFieldTypeServicesRegistry() {
		DDMFormFieldValueAccessor<?> ddmFormFieldValueAccessor =
			new DefaultDDMFormFieldValueAccessor();

		Mockito.when(
			_ddmFormFieldTypeServicesRegistry.getDDMFormFieldValueAccessor(
				Mockito.anyString())
		).thenReturn(
			(DDMFormFieldValueAccessor<Object>)ddmFormFieldValueAccessor
		);
	}

	private static void _setUpFastDateFormatFactoryUtil() {
		FastDateFormatFactoryUtil fastDateFormatFactoryUtil =
			new FastDateFormatFactoryUtil();

		fastDateFormatFactoryUtil.setFastDateFormatFactory(
			new FastDateFormatFactoryImpl());
	}

	private static void _setUpGooglePlacesUtil() throws Exception {
		ReflectionTestUtil.setFieldValue(
			PrefsPropsUtil.class, "_prefsProps", _prefsProps);

		Mockito.when(
			_prefsProps.getPreferences(Mockito.anyLong())
		).thenReturn(
			_portletPreferences
		);

		Mockito.when(
			_portletPreferences.getValue(
				Mockito.anyString(), Mockito.anyString())
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			_groupLocalService.getGroup(Mockito.anyLong())
		).thenReturn(
			null
		);
	}

	private static void _setUpHtmlParser() {
		Mockito.when(
			_htmlParser.extractText("descriptionPage")
		).thenReturn(
			"descriptionPage"
		);

		Mockito.when(
			_htmlParser.extractText("Page 1")
		).thenReturn(
			"Page 1"
		);

		Mockito.when(
			_htmlParser.extractText("Page 1 Description")
		).thenReturn(
			"Page 1 Description"
		);

		Mockito.when(
			_htmlParser.extractText("titlePage")
		).thenReturn(
			"titlePage"
		);

		Mockito.when(
			_htmlParser.extractText("<a>descriptionPage</a>")
		).thenReturn(
			"descriptionPage"
		);

		Mockito.when(
			_htmlParser.extractText("<a>titlePage</a>")
		).thenReturn(
			"titlePage"
		);
	}

	private static void _setUpHttpServletRequest() {
		_httpServletRequest = Mockito.mock(HttpServletRequest.class);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setPathThemeImages(StringPool.BLANK);
		themeDisplay.setUser(Mockito.mock(User.class));

		Mockito.when(
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);
	}

	private static void _setUpLocaleThreadLocal() {
		LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.US);
	}

	private static void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		ResourceBundle resourceBundle = Mockito.mock(ResourceBundle.class);

		Mockito.when(
			portal.getCompanyId(Mockito.any(PortletRequest.class))
		).thenReturn(
			1L
		);

		Mockito.when(
			portal.getUserId(Mockito.any(PortletRequest.class))
		).thenReturn(
			1L
		);

		Mockito.when(
			portal.getResourceBundle(Mockito.any(Locale.class))
		).thenReturn(
			resourceBundle
		);

		portalUtil.setPortal(portal);
	}

	private static void _setUpResourceBundle() {
		Portal portal = Mockito.mock(Portal.class);

		ResourceBundle resourceBundle = Mockito.mock(ResourceBundle.class);

		Mockito.when(
			portal.getResourceBundle(Mockito.any(Locale.class))
		).thenReturn(
			resourceBundle
		);
	}

	private static void _setUpResourceBundleLoaderUtil() {
		ResourceBundleLoaderUtil.setPortalResourceBundleLoader(
			Mockito.mock(ResourceBundleLoader.class));
	}

	private static void _setUpResourceBundleUtil() {
		ResourceBundleLoader resourceBundleLoader = Mockito.mock(
			ResourceBundleLoader.class);

		ResourceBundleLoaderUtil.setPortalResourceBundleLoader(
			resourceBundleLoader);

		Mockito.when(
			resourceBundleLoader.loadResourceBundle(Mockito.any(Locale.class))
		).thenReturn(
			ResourceBundleUtil.EMPTY_RESOURCE_BUNDLE
		);
	}

	private void _assertColumnSize(
		int expectedSize, Map<String, Object> columnTemplateContex) {

		Assert.assertEquals(
			expectedSize, MapUtil.getInteger(columnTemplateContex, "size"));
	}

	private DDMFormPagesTemplateContextFactory
		_createDDMFormPagesTemplateContextFactory(
			DDMForm ddmForm, DDMFormLayout ddmFormLayout,
			DDMFormValues ddmFormValues, boolean ddmFormReadOnly,
			boolean showRequiredFieldsWarning, boolean viewMode) {

		DDMFormRenderingContext ddmFormRenderingContext =
			new DDMFormRenderingContext();

		ddmFormRenderingContext.setDDMFormValues(ddmFormValues);
		ddmFormRenderingContext.setHttpServletRequest(_httpServletRequest);
		ddmFormRenderingContext.setLocale(_LOCALE);
		ddmFormRenderingContext.setPortletNamespace(_PORTLET_NAMESPACE);
		ddmFormRenderingContext.setReadOnly(ddmFormReadOnly);
		ddmFormRenderingContext.setReturnFullContext(true);
		ddmFormRenderingContext.setShowRequiredFieldsWarning(
			showRequiredFieldsWarning);
		ddmFormRenderingContext.setViewMode(viewMode);

		DDMFormPagesTemplateContextFactory ddmFormPagesTemplateContextFactory =
			new DDMFormPagesTemplateContextFactory(
				ddmForm, ddmFormLayout, ddmFormRenderingContext,
				_ddmStructureLayoutLocalService, _ddmStructureLocalService,
				_groupLocalService, _htmlParser, new JSONFactoryImpl());

		ddmFormPagesTemplateContextFactory.setDDMFormEvaluator(
			_getDDMFormEvaluator());
		ddmFormPagesTemplateContextFactory.setDDMFormFieldTypeServicesRegistry(
			_ddmFormFieldTypeServicesRegistry);

		return ddmFormPagesTemplateContextFactory;
	}

	private DDMFormEvaluator _getDDMFormEvaluator() {
		DDMFormEvaluator ddmFormEvaluator = new DDMFormEvaluatorImpl();

		DDMExpressionFactoryImpl ddmExpressionFactoryImpl =
			new DDMExpressionFactoryImpl();

		ReflectionTestUtil.setFieldValue(
			ddmFormEvaluator, "ddmExpressionFactory", ddmExpressionFactoryImpl);

		ReflectionTestUtil.setFieldValue(
			ddmFormEvaluator, "ddmFormFieldTypeServicesRegistry",
			_ddmFormFieldTypeServicesRegistry);

		Map<String, DDMExpressionFunctionFactory>
			ddmExpressionFunctionFactoryMap =
				HashMapBuilder.<String, DDMExpressionFunctionFactory>put(
					"hasGooglePlacesAPIKey",
					() -> new HasGooglePlacesAPIKeyFunction()
				).put(
					"jumpPage", () -> new JumpPageFunction()
				).put(
					"setVisible", () -> new SetVisibleFunction()
				).build();

		DDMExpressionFunctionRegistry ddmExpressionFunctionRegistry =
			Mockito.mock(DDMExpressionFunctionRegistry.class);

		Mockito.when(
			ddmExpressionFunctionRegistry.getDDMExpressionFunctionFactories(
				Mockito.any())
		).thenReturn(
			ddmExpressionFunctionFactoryMap
		);

		ReflectionTestUtil.setFieldValue(
			ddmExpressionFactoryImpl, "ddmExpressionFunctionRegistry",
			ddmExpressionFunctionRegistry);

		return ddmFormEvaluator;
	}

	private Map<String, Object> _getFieldTemplateContext(List<Object> pages) {
		Map<String, Object> page1 = (Map<String, Object>)pages.get(0);

		List<Object> rows = (List<Object>)page1.get("rows");

		Map<String, Object> row1 = (Map<String, Object>)rows.get(0);

		List<Object> columnsRow1 = (List<Object>)row1.get("columns");

		Map<String, Object> column1Row1 = (Map<String, Object>)columnsRow1.get(
			0);

		List<Object> fieldsColumn1Row1 = (List<Object>)column1Row1.get(
			"fields");

		return (Map<String, Object>)fieldsColumn1Row1.get(0);
	}

	private static final String _HTML_WRAPPER = "<a>%s</a>";

	private static final Locale _LOCALE = LocaleUtil.US;

	private static final String _PORTLET_NAMESPACE = StringUtil.randomString();

	private static final DDMFormFieldTypeServicesRegistry
		_ddmFormFieldTypeServicesRegistry = Mockito.mock(
			DDMFormFieldTypeServicesRegistry.class);
	private static final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private static final HtmlParser _htmlParser = Mockito.mock(
		HtmlParser.class);
	private static HttpServletRequest _httpServletRequest;
	private static final PortletPreferences _portletPreferences = Mockito.mock(
		PortletPreferences.class);
	private static final PrefsProps _prefsProps = Mockito.mock(
		PrefsProps.class);

	private final DDMFormFieldTemplateContextContributorTestHelper
		_ddmFormFieldTemplateContextContributorTestHelper =
			new DDMFormFieldTemplateContextContributorTestHelper();
	private final DDMStructureLayoutLocalService
		_ddmStructureLayoutLocalService = Mockito.mock(
			DDMStructureLayoutLocalService.class);
	private final DDMStructureLocalService _ddmStructureLocalService =
		Mockito.mock(DDMStructureLocalService.class);

}