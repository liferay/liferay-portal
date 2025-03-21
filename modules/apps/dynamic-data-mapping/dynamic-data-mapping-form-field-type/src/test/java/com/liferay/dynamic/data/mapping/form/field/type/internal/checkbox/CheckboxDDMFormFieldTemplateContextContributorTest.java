/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.checkbox;

import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.test.util.BaseDDMFormFieldTemplateContextContributorTestCase;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Marcellus Tavares
 */
public class CheckboxDDMFormFieldTemplateContextContributorTest
	extends BaseDDMFormFieldTemplateContextContributorTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		setUpLanguageUtil();

		_ddmFormField.setDDMForm(getDDMForm());

		PortletURLFactoryUtil portletURLFactoryUtil =
			new PortletURLFactoryUtil();

		PortletURLFactory portletURLFactory = Mockito.mock(
			PortletURLFactory.class);

		LiferayPortletURL liferayPortletURL = new MockLiferayPortletURL();

		Mockito.doReturn(
			liferayPortletURL
		).when(
			portletURLFactory
		).create(
			Mockito.any(PortletRequest.class), Mockito.anyString(),
			Mockito.anyString()
		);

		Mockito.doReturn(
			liferayPortletURL
		).when(
			portletURLFactory
		).create(
			Mockito.any(HttpServletRequest.class), Mockito.anyString(),
			Mockito.anyLong(), Mockito.anyString()
		);

		portletURLFactoryUtil.setPortletURLFactory(portletURLFactory);
	}

	@Test
	public void testGetLocalizedObjectFieldTrue() {
		_ddmFormField.setProperty("localizedObjectField", true);

		Map<String, Object> parameters =
			_checkboxDDMFormFieldTemplateContextContributor.getParameters(
				_ddmFormField, createDDMFormFieldRenderingContext());

		Assert.assertTrue((boolean)parameters.get("localizedObjectField"));
	}

	@Test
	public void testGetMaximumRepetitionsInfoTrue() {
		_ddmFormField.setProperty("showMaximumRepetitionsInfo", true);

		Map<String, Object> parameters =
			_checkboxDDMFormFieldTemplateContextContributor.getParameters(
				_ddmFormField, createDDMFormFieldRenderingContext());

		boolean showMaximumRepetitionsInfo = (boolean)parameters.get(
			"showMaximumRepetitionsInfo");

		Assert.assertTrue(showMaximumRepetitionsInfo);
	}

	@Test
	public void testGetNotDefinedPredefinedValue() {
		Map<String, Object> parameters =
			_checkboxDDMFormFieldTemplateContextContributor.getParameters(
				_ddmFormField, createDDMFormFieldRenderingContext());

		boolean predefinedValue = (boolean)parameters.get("predefinedValue");

		Assert.assertFalse(predefinedValue);
	}

	@Test
	public void testGetPredefinedValueFalse() {
		LocalizedValue predefinedValue = new LocalizedValue(LocaleUtil.US);

		predefinedValue.addString(LocaleUtil.US, StringPool.FALSE);

		_ddmFormField.setProperty("predefinedValue", predefinedValue);

		Map<String, Object> parameters =
			_checkboxDDMFormFieldTemplateContextContributor.getParameters(
				_ddmFormField, createDDMFormFieldRenderingContext());

		boolean actualPredefinedValue = (boolean)parameters.get(
			"predefinedValue");

		Assert.assertFalse(actualPredefinedValue);
	}

	@Test
	public void testGetPredefinedValueTrue() {
		LocalizedValue predefinedValue = new LocalizedValue(LocaleUtil.US);

		predefinedValue.addString(LocaleUtil.US, StringPool.TRUE);

		_ddmFormField.setProperty("predefinedValue", predefinedValue);

		Map<String, Object> parameters =
			_checkboxDDMFormFieldTemplateContextContributor.getParameters(
				_ddmFormField, createDDMFormFieldRenderingContext());

		boolean actualPredefinedValue = (boolean)parameters.get(
			"predefinedValue");

		Assert.assertTrue(actualPredefinedValue);
	}

	@Test
	public void testGetSettingsURL() {
		Map<String, Object> parameters =
			_checkboxDDMFormFieldTemplateContextContributor.getParameters(
				_ddmFormField, createDDMFormFieldRenderingContext());

		String systemSettingsURL = String.valueOf(
			parameters.get("systemSettingsURL"));

		Assert.assertTrue(Validator.isBlank(systemSettingsURL));
	}

	@Test
	public void testGetSystemSettingsURL() {
		_ddmFormField.setProperty("showMaximumRepetitionsInfo", true);

		Map<String, Object> parameters =
			_checkboxDDMFormFieldTemplateContextContributor.getParameters(
				_ddmFormField, createDDMFormFieldRenderingContext());

		String systemSettingsURL = String.valueOf(
			parameters.get("systemSettingsURL"));

		Assert.assertThat(
			systemSettingsURL,
			CoreMatchers.containsString(
				"param_factoryPid=com.liferay.dynamic.data.mapping.form.web." +
					"internal.configuration.DDMFormWebConfiguration"));
		Assert.assertThat(
			systemSettingsURL,
			CoreMatchers.containsString(
				"param_mvcRenderCommandName=/configuration_admin" +
					"/edit_configuration"));
	}

	@Test
	public void testGetValue() {
		_ddmFormField.setProperty("localizedObjectField", true);

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			createDDMFormFieldRenderingContext();

		String value = JSONUtil.put(
			"en_US", true
		).put(
			"pt_BR", false
		).toString();

		ddmFormFieldRenderingContext.setValue(value);

		Map<String, Object> parameters =
			_checkboxDDMFormFieldTemplateContextContributor.getParameters(
				_ddmFormField, ddmFormFieldRenderingContext);

		Assert.assertEquals(value, String.valueOf(parameters.get("value")));
	}

	private final CheckboxDDMFormFieldTemplateContextContributor
		_checkboxDDMFormFieldTemplateContextContributor =
			new CheckboxDDMFormFieldTemplateContextContributor();
	private final DDMFormField _ddmFormField = new DDMFormField(
		"field", "checkbox");

}