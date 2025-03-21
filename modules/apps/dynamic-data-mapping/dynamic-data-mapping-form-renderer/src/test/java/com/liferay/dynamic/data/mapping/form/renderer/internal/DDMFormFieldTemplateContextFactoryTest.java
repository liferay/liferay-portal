/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.renderer.internal;

import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluator;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorFieldContextKey;
import com.liferay.dynamic.data.mapping.form.field.type.BaseDDMFormFieldRenderer;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldRenderer;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.service.DDMStructureLayoutLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.language.constants.LanguageConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Marcellus Tavares
 */
public class DDMFormFieldTemplateContextFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_setUpDDMFormTemplateContextFactoryUtil();
		setUpLanguageUtil();
	}

	@Test
	public void testNotReadOnlyTextFieldAndReadOnlyForm() {

		// Dynamic data mapping form

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField(
			"Field1", false, false, false);

		boolean readOnly = false;

		ddmFormField.setReadOnly(readOnly);

		ddmForm.addDDMFormField(ddmFormField);

		// Dynamic data mapping form field evaluation

		String instanceId = StringUtil.randomString();

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				HashMapBuilder.
					<DDMFormEvaluatorFieldContextKey, Map<String, Object>>put(
						new DDMFormEvaluatorFieldContextKey(
							"Field1", instanceId),
						HashMapBuilder.<String, Object>put(
							"readOnly", readOnly
						).put(
							"visible", true
						).build()
					).build();

		// Dynamic data mapping form values

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"Field1", "Value 1");

		ddmFormFieldValue.setInstanceId(instanceId);

		ddmFormFieldValues.add(ddmFormFieldValue);

		DDMFormFieldTemplateContextFactory ddmFormFieldTemplateContextFactory =
			_createDDMFormFieldTemplateContextFactory(
				ddmForm, ddmFormField.getName(), ddmFormFieldsPropertyChanges,
				ddmFormFieldValues, true, _getTextDDMFormFieldRenderer(),
				_getTextDDMFormFieldTemplateContextContributor());

		List<Object> fields = ddmFormFieldTemplateContextFactory.create();

		Assert.assertEquals(fields.toString(), 1, fields.size());

		Map<String, Object> fieldTemplateContext =
			(Map<String, Object>)fields.get(0);

		Assert.assertTrue(MapUtil.getBoolean(fieldTemplateContext, "readOnly"));
	}

	@Test
	public void testReadOnlyTextFieldAndNotReadOnlyForm() {

		// Dynamic data mapping form

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField(
			"Field1", false, false, true);

		boolean readOnly = true;

		ddmFormField.setReadOnly(readOnly);

		ddmForm.addDDMFormField(ddmFormField);

		// Dynamic data mapping form field evaluation

		String instanceId = StringUtil.randomString();

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				HashMapBuilder.
					<DDMFormEvaluatorFieldContextKey, Map<String, Object>>put(
						new DDMFormEvaluatorFieldContextKey(
							"Field1", instanceId),
						HashMapBuilder.<String, Object>put(
							"readOnly", readOnly
						).put(
							"visible", true
						).build()
					).build();

		// Dynamic data mapping form values

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"Field1", "Value 1");

		ddmFormFieldValue.setInstanceId(instanceId);

		ddmFormFieldValues.add(ddmFormFieldValue);

		DDMFormFieldTemplateContextFactory ddmFormFieldTemplateContextFactory =
			_createDDMFormFieldTemplateContextFactory(
				ddmForm, ddmFormField.getName(), ddmFormFieldsPropertyChanges,
				ddmFormFieldValues, false, _getTextDDMFormFieldRenderer(),
				_getTextDDMFormFieldTemplateContextContributor());

		List<Object> fields = ddmFormFieldTemplateContextFactory.create();

		Assert.assertEquals(fields.toString(), 1, fields.size());

		Map<String, Object> fieldTemplateContext =
			(Map<String, Object>)fields.get(0);

		Assert.assertTrue(MapUtil.getBoolean(fieldTemplateContext, "readOnly"));
	}

	@Test
	public void testTextField() {

		// Dynamic data mapping form

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		boolean required = true;

		DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField(
			"Field1", false, false, required);

		ddmFormField.setLabel(
			DDMFormValuesTestUtil.createLocalizedValue("Field 1", _LOCALE));
		ddmFormField.setReadOnly(false);
		ddmFormField.setTip(
			DDMFormValuesTestUtil.createLocalizedValue(
				"This is a tip.", _LOCALE));
		ddmFormField.setProperty("displayStyle", "singleline");
		ddmFormField.setRequiredErrorMessage(
			DDMFormValuesTestUtil.createLocalizedValue(
				"Custom required error message.", _LOCALE));

		ddmForm.addDDMFormField(ddmFormField);

		// Dynamic data mapping form field evaluation

		String instanceId = StringUtil.randomString();

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				HashMapBuilder.
					<DDMFormEvaluatorFieldContextKey, Map<String, Object>>put(
						new DDMFormEvaluatorFieldContextKey(
							"Field1", instanceId),
						HashMapBuilder.<String, Object>put(
							"required", true
						).put(
							"valid", true
						).put(
							"visible", true
						).build()
					).build();

		// Dynamic data mapping form values

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"Field1", "Value 1");

		ddmFormFieldValue.setInstanceId(instanceId);

		ddmFormFieldValues.add(ddmFormFieldValue);

		DDMFormFieldTemplateContextFactory ddmFormFieldTemplateContextFactory =
			_createDDMFormFieldTemplateContextFactory(
				ddmForm, ddmFormField.getName(), ddmFormFieldsPropertyChanges,
				ddmFormFieldValues, false, _getTextDDMFormFieldRenderer(),
				_getTextDDMFormFieldTemplateContextContributor());

		List<Object> fields = ddmFormFieldTemplateContextFactory.create();

		Assert.assertEquals(fields.toString(), 1, fields.size());

		Map<String, Object> fieldTemplateContext =
			(Map<String, Object>)fields.get(0);

		Assert.assertEquals(
			"singleline",
			MapUtil.getString(fieldTemplateContext, "displayStyle"));
		Assert.assertEquals(
			"Field 1", MapUtil.getString(fieldTemplateContext, "label"));
		Assert.assertFalse(
			MapUtil.getBoolean(fieldTemplateContext, "readOnly"));
		Assert.assertFalse(
			MapUtil.getBoolean(fieldTemplateContext, "repeatable"));
		Assert.assertTrue(MapUtil.getBoolean(fieldTemplateContext, "required"));
		Assert.assertEquals(
			"Custom required error message.",
			MapUtil.getString(fieldTemplateContext, "requiredErrorMessage"));
		Assert.assertEquals(
			"This is a tip.", MapUtil.getString(fieldTemplateContext, "tip"));
		Assert.assertTrue(MapUtil.getBoolean(fieldTemplateContext, "valid"));
		Assert.assertEquals(
			StringPool.BLANK,
			MapUtil.getString(fieldTemplateContext, "validationErrorMessage"));
		Assert.assertEquals(
			"Value 1", MapUtil.getString(fieldTemplateContext, "value"));
		Assert.assertTrue(MapUtil.getBoolean(fieldTemplateContext, "visible"));

		String expectedName = String.format(
			_FIELD_NAME_FORMAT, "Field1", instanceId, 0, _LOCALE.toString());

		Assert.assertEquals(
			expectedName, MapUtil.getString(fieldTemplateContext, "name"));
	}

	protected static void setUpLanguageUtil() {
		Language language = Mockito.mock(Language.class);

		whenLanguageGet(
			language, LocaleUtil.US, LanguageConstants.KEY_DIR, "ltr");

		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(language);
	}

	protected static void whenLanguageGet(
		Language language, Locale locale, String key, String returnValue) {

		Mockito.when(
			language.get(Mockito.eq(locale), Mockito.eq(key))
		).thenReturn(
			returnValue
		);
	}

	protected DDMFormFieldTypeServicesRegistry
		mockDDMFormFieldTypeServicesRegistry(
			DDMFormFieldRenderer ddmFormFieldRenderer,
			DDMFormFieldTemplateContextContributor
				ddmFormFieldTemplateContextContributor) {

		DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry =
			Mockito.mock(DDMFormFieldTypeServicesRegistry.class);

		Mockito.when(
			ddmFormFieldTypeServicesRegistry.getDDMFormFieldRenderer(
				Mockito.anyString())
		).thenReturn(
			ddmFormFieldRenderer
		);

		Mockito.when(
			ddmFormFieldTypeServicesRegistry.
				getDDMFormFieldTemplateContextContributor(Mockito.anyString())
		).thenReturn(
			ddmFormFieldTemplateContextContributor
		);

		return ddmFormFieldTypeServicesRegistry;
	}

	private static void _setUpDDMFormTemplateContextFactoryUtil() {
		_httpServletRequest = Mockito.mock(HttpServletRequest.class);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setPathThemeImages(StringPool.BLANK);

		Mockito.when(
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);
	}

	private DDMFormFieldTemplateContextFactory
		_createDDMFormFieldTemplateContextFactory(
			DDMForm ddmForm, String ddmFormFieldName,
			Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
				ddmFormFieldsPropertyChanges,
			List<DDMFormFieldValue> ddmFormFieldValues, boolean ddmFormReadOnly,
			DDMFormFieldRenderer ddmFormFieldRenderer,
			DDMFormFieldTemplateContextContributor
				ddmFormFieldTemplateContextContributor) {

		DDMFormRenderingContext ddmFormRenderingContext =
			new DDMFormRenderingContext();

		ddmFormRenderingContext.setHttpServletRequest(_httpServletRequest);
		ddmFormRenderingContext.setLocale(_LOCALE);
		ddmFormRenderingContext.setPortletNamespace(_PORTLET_NAMESPACE);
		ddmFormRenderingContext.setReadOnly(ddmFormReadOnly);

		DDMFormFieldTemplateContextFactory ddmFormFieldTemplateContextFactory =
			new DDMFormFieldTemplateContextFactory(
				_ddmFormEvaluator, ddmFormFieldName,
				ddmForm.getDDMFormFieldsMap(true), ddmFormFieldsPropertyChanges,
				ddmFormFieldValues, ddmFormRenderingContext,
				_ddmStructureLayoutLocalService, _ddmStructureLocalService,
				_groupLocalService, _htmlParser, new JSONFactoryImpl(), true,
				new DDMFormLayout());

		ddmFormFieldTemplateContextFactory.setDDMFormFieldTypeServicesRegistry(
			mockDDMFormFieldTypeServicesRegistry(
				ddmFormFieldRenderer, ddmFormFieldTemplateContextContributor));

		return ddmFormFieldTemplateContextFactory;
	}

	private DDMFormFieldRenderer _getTextDDMFormFieldRenderer() {
		return new BaseDDMFormFieldRenderer() {

			public String getTemplateLanguage() {
				return null;
			}

			public String getTemplateNamespace() {
				return "ddm.text";
			}

			public TemplateResource getTemplateResource() {
				return null;
			}

		};
	}

	private DDMFormFieldTemplateContextContributor
		_getTextDDMFormFieldTemplateContextContributor() {

		return (ddmFormField, ddmFormFieldRenderingContext) -> {
			Map<String, Object> parameters = new HashMap<>();

			parameters.put(
				"displayStyle", ddmFormField.getProperty("displayStyle"));

			return parameters;
		};
	}

	private static final String _FIELD_NAME_FORMAT =
		"_PORTLET_NAMESPACE_ddm$$%s$%s$%d$$%s";

	private static final Locale _LOCALE = LocaleUtil.US;

	private static final String _PORTLET_NAMESPACE = "_PORTLET_NAMESPACE_";

	private static HttpServletRequest _httpServletRequest;

	private final DDMFormEvaluator _ddmFormEvaluator = Mockito.mock(
		DDMFormEvaluator.class);
	private final DDMStructureLayoutLocalService
		_ddmStructureLayoutLocalService = Mockito.mock(
			DDMStructureLayoutLocalService.class);
	private final DDMStructureLocalService _ddmStructureLocalService =
		Mockito.mock(DDMStructureLocalService.class);
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private final HtmlParser _htmlParser = Mockito.mock(HtmlParser.class);

}