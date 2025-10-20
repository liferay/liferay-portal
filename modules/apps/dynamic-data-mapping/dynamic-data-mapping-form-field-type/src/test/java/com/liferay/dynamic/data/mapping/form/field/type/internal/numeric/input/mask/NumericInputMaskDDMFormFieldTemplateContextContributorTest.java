/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.numeric.input.mask;

import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.test.util.BaseDDMFormFieldTemplateContextContributorTestCase;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Carolina Barbosa
 */
public class NumericInputMaskDDMFormFieldTemplateContextContributorTest
	extends BaseDDMFormFieldTemplateContextContributorTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_setUpJSONFactory();
		_setUpJSONFactoryUtil();

		_setUpLanguage();

		_setUpPortal();
	}

	@Test
	public void testGetParameters() {
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			createDDMFormFieldRenderingContext();

		Map<String, Object> parameters =
			_numericInputMaskDDMFormFieldTemplateContextContributor.
				getParameters(
					new DDMFormField(
						"field", DDMFormFieldTypeConstants.NUMERIC),
					ddmFormFieldRenderingContext);

		Assert.assertEquals("", parameters.get("append"));
		Assert.assertEquals("prefix", parameters.get("appendType"));
		Assert.assertEquals(2, parameters.get("decimalPlaces"));
		Assert.assertEquals(".", parameters.get("decimalSymbol"));
		Assert.assertEquals("none", parameters.get("thousandsSeparator"));

		ddmFormFieldRenderingContext.setValue(
			JSONUtil.put(
				"append", "%"
			).put(
				"appendType", "suffix"
			).put(
				"decimalPlaces", 3
			).put(
				"symbols",
				JSONUtil.put(
					"decimalSymbol", ","
				).put(
					"thousandsSeparator", "\'"
				)
			).toString());

		parameters =
			_numericInputMaskDDMFormFieldTemplateContextContributor.
				getParameters(
					new DDMFormField(
						"field", DDMFormFieldTypeConstants.NUMERIC),
					ddmFormFieldRenderingContext);

		Assert.assertEquals("%", parameters.get("append"));
		Assert.assertEquals("suffix", parameters.get("appendType"));
		Assert.assertEquals(3, parameters.get("decimalPlaces"));
		Assert.assertEquals(",", parameters.get("decimalSymbol"));
		Assert.assertEquals("\'", parameters.get("thousandsSeparator"));

		List<Object> decimalSymbols = (List<Object>)parameters.get(
			"decimalSymbols");

		Assert.assertEquals(
			decimalSymbols.toString(), 2, decimalSymbols.size());
		Assert.assertEquals(
			HashMapBuilder.put(
				"label", "0.00"
			).put(
				"reference", "."
			).put(
				"value", "."
			).build(),
			decimalSymbols.get(0));
		Assert.assertEquals(
			HashMapBuilder.put(
				"label", "0,00"
			).put(
				"reference", ","
			).put(
				"value", ","
			).build(),
			decimalSymbols.get(1));

		Assert.assertEquals("\'", parameters.get("thousandsSeparator"));

		List<Object> thousandsSeparators = (List<Object>)parameters.get(
			"thousandsSeparators");

		Assert.assertEquals(
			thousandsSeparators.toString(), 5, thousandsSeparators.size());
		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"label",
				HashMapBuilder.put(
					"en_US", "None"
				).build()
			).put(
				"reference", "none"
			).put(
				"value", "none"
			).build(),
			thousandsSeparators.get(0));
		Assert.assertEquals(
			HashMapBuilder.put(
				"label", "1,000"
			).put(
				"reference", ","
			).put(
				"value", ","
			).build(),
			thousandsSeparators.get(1));
		Assert.assertEquals(
			HashMapBuilder.put(
				"label", "1.000"
			).put(
				"reference", "."
			).put(
				"value", "."
			).build(),
			thousandsSeparators.get(2));
		Assert.assertEquals(
			HashMapBuilder.put(
				"label", "1 000"
			).put(
				"reference", " "
			).put(
				"value", " "
			).build(),
			thousandsSeparators.get(3));
		Assert.assertEquals(
			HashMapBuilder.put(
				"label", "1\'000"
			).put(
				"reference", "\'"
			).put(
				"value", "\'"
			).build(),
			thousandsSeparators.get(4));
	}

	private void _setUpJSONFactory() {
		ReflectionTestUtil.setFieldValue(
			_numericInputMaskDDMFormFieldTemplateContextContributor,
			"_jsonFactory", new JSONFactoryImpl());
	}

	private void _setUpJSONFactoryUtil() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	private void _setUpLanguage() {
		language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(Mockito.any(ResourceBundle.class), Mockito.eq("none"))
		).thenReturn(
			"None"
		);

		Mockito.when(
			language.getAvailableLocales()
		).thenReturn(
			SetUtil.fromArray(LocaleUtil.US)
		);

		Mockito.when(
			language.getLanguageId(LocaleUtil.US)
		).thenReturn(
			"en_US"
		);

		ReflectionTestUtil.setFieldValue(
			_numericInputMaskDDMFormFieldTemplateContextContributor,
			"_language", language);
	}

	private void _setUpPortal() {
		Portal portal = Mockito.mock(Portal.class);

		ReflectionTestUtil.setFieldValue(
			_numericInputMaskDDMFormFieldTemplateContextContributor, "_portal",
			portal);
	}

	private final NumericInputMaskDDMFormFieldTemplateContextContributor
		_numericInputMaskDDMFormFieldTemplateContextContributor =
			new NumericInputMaskDDMFormFieldTemplateContextContributor();

}