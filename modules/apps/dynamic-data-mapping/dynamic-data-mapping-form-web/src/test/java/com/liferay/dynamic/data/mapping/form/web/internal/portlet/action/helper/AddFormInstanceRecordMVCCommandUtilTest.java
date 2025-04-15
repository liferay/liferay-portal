/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.helper;

import com.liferay.dynamic.data.mapping.exception.FormInstanceExpiredException;
import com.liferay.dynamic.data.mapping.exception.FormInstanceSubmissionLimitException;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorFieldContextKey;
import com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.util.AddFormInstanceRecordMVCCommandUtil;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.ActionRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Leonardo Barros
 */
public class AddFormInstanceRecordMVCCommandUtilTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDisabledField() throws Exception {
		_updateNonevaluableDDMFormFields(
			HashMapBuilder.<String, Object>put(
				"readOnly", true
			).build(),
			false, RandomTestUtil.randomBoolean(),
			new UnlocalizedValue(_STRING_VALUE));

		_assertDDMFormFields(false, new UnlocalizedValue(StringPool.BLANK));
	}

	@Test
	public void testEnabledField() throws Exception {
		boolean required = RandomTestUtil.randomBoolean();

		_updateNonevaluableDDMFormFields(
			HashMapBuilder.<String, Object>put(
				"readOnly", false
			).build(),
			false, required, new UnlocalizedValue(_STRING_VALUE));

		_assertDDMFormFields(required, new UnlocalizedValue(_STRING_VALUE));
	}

	@Test
	public void testInvisibleAndLocalizableField() throws Exception {
		_updateNonevaluableDDMFormFields(
			HashMapBuilder.<String, Object>put(
				"visible", false
			).build(),
			true, RandomTestUtil.randomBoolean(),
			DDMFormValuesTestUtil.createLocalizedValue(
				"Test", "Teste", LocaleUtil.US));

		Value value = _getFieldValue(_FIELD_NAME);

		Assert.assertEquals(
			StringPool.BLANK, value.getString(LocaleUtil.BRAZIL));
		Assert.assertEquals(StringPool.BLANK, value.getString(LocaleUtil.US));

		value = _getFieldValue(_NESTED_FIELD_NAME);

		Assert.assertEquals(
			StringPool.BLANK, value.getString(LocaleUtil.BRAZIL));
		Assert.assertEquals(StringPool.BLANK, value.getString(LocaleUtil.US));
	}

	@Test
	public void testInvisibleField() throws Exception {
		_updateNonevaluableDDMFormFields(
			HashMapBuilder.<String, Object>put(
				"visible", false
			).build(),
			false, RandomTestUtil.randomBoolean(),
			new UnlocalizedValue(_STRING_VALUE));

		_assertDDMFormFields(false, new UnlocalizedValue(StringPool.BLANK));
	}

	@Test
	public void testInvisibleFieldWithNullValue() throws Exception {
		_updateNonevaluableDDMFormFields(
			HashMapBuilder.<String, Object>put(
				"visible", false
			).build(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			null);

		_assertDDMFormFields(false, null);
	}

	@Test(expected = FormInstanceExpiredException.class)
	public void testValidateExpirationStatus() throws Exception {
		ThemeDisplay themeDisplay = _mockThemeDisplay();

		Mockito.when(
			_actionRequest.getAttribute(Mockito.eq(WebKeys.THEME_DISPLAY))
		).thenReturn(
			themeDisplay
		);

		AddFormInstanceRecordMVCCommandUtil.validateExpirationStatus(
			_mockDDMFormInstance(), _actionRequest);
	}

	@Test(expected = FormInstanceSubmissionLimitException.class)
	public void testValidateSubmissionLimitStatus() throws Exception {
		ThemeDisplay themeDisplay = _mockThemeDisplay();

		Mockito.when(
			_actionRequest.getAttribute(Mockito.eq(WebKeys.THEME_DISPLAY))
		).thenReturn(
			themeDisplay
		);

		AddFormInstanceRecordMVCCommandUtil.validateSubmissionLimitStatus(
			_mockDDMFormInstance(),
			_mockDDMFormInstanceRecordVersionLocalService(), _actionRequest);
	}

	@Test
	public void testVisibleField() throws Exception {
		boolean required = RandomTestUtil.randomBoolean();

		_updateNonevaluableDDMFormFields(
			HashMapBuilder.<String, Object>put(
				"visible", true
			).build(),
			false, required, new UnlocalizedValue(_STRING_VALUE));

		_assertDDMFormFields(required, new UnlocalizedValue(_STRING_VALUE));
	}

	private void _assertDDMFormFields(
		boolean expectedRequired, Value expectedValue) {

		Assert.assertEquals(expectedRequired, _ddmFormField.isRequired());
		Assert.assertEquals(expectedValue, _getFieldValue(_FIELD_NAME));
		Assert.assertEquals(expectedValue, _getFieldValue(_NESTED_FIELD_NAME));
	}

	private void _createDDMFormFields(
		DDMForm ddmForm, boolean localizable, boolean required) {

		_ddmFormField = DDMFormTestUtil.createTextDDMFormField(
			_FIELD_NAME, localizable, false, required);

		ddmForm.addDDMFormField(_ddmFormField);

		DDMFormField ddmFormField = DDMFormTestUtil.createDDMFormField(
			RandomTestUtil.randomString(), null, null, null, false, false,
			false);

		ddmFormField.addNestedDDMFormField(
			DDMFormTestUtil.createTextDDMFormField(
				_NESTED_FIELD_NAME, localizable, false, required));

		ddmForm.addDDMFormField(ddmFormField);
	}

	private void _createDDMFormValues(DDMForm ddmForm, Value value) {
		_ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);

		_ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				_FIELD_INSTANCE_ID, _FIELD_NAME, value));

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				RandomTestUtil.randomString(), null);

		ddmFormFieldValue.addNestedDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				_NESTED_FIELD_INSTANCE_ID, _NESTED_FIELD_NAME, value));

		_ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);
	}

	private Value _getFieldValue(String fieldName) {
		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			_ddmFormValues.getDDMFormFieldValuesMap(true);

		if (!ddmFormFieldValuesMap.containsKey(fieldName)) {
			return null;
		}

		List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValuesMap.get(
			fieldName);

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		return ddmFormFieldValue.getValue();
	}

	private DDMFormInstance _mockDDMFormInstance() throws Exception {
		DDMFormInstance ddmFormInstance = Mockito.mock(DDMFormInstance.class);

		DDMFormInstanceSettings ddmFormInstanceSettings =
			_mockDDMFormInstanceSettings();

		Mockito.when(
			ddmFormInstance.getSettingsModel()
		).thenReturn(
			ddmFormInstanceSettings
		);

		return ddmFormInstance;
	}

	private DDMFormInstanceRecordVersionLocalService
		_mockDDMFormInstanceRecordVersionLocalService() {

		DDMFormInstanceRecordVersionLocalService
			ddmFormInstanceRecordVersionLocalService = Mockito.mock(
				DDMFormInstanceRecordVersionLocalService.class);

		Mockito.when(
			ddmFormInstanceRecordVersionLocalService.
				getFormInstanceRecordVersions(
					Mockito.anyLong(), Mockito.anyLong())
		).thenReturn(
			Collections.singletonList(
				Mockito.mock(DDMFormInstanceRecordVersion.class))
		);

		return ddmFormInstanceRecordVersionLocalService;
	}

	private DDMFormInstanceSettings _mockDDMFormInstanceSettings() {
		DDMFormInstanceSettings ddmFormInstanceSettings = Mockito.mock(
			DDMFormInstanceSettings.class);

		Mockito.when(
			ddmFormInstanceSettings.expirationDate()
		).thenReturn(
			"1987-09-22"
		);

		Mockito.when(
			ddmFormInstanceSettings.limitToOneSubmissionPerUser()
		).thenReturn(
			true
		);

		return ddmFormInstanceSettings;
	}

	private ThemeDisplay _mockThemeDisplay() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getTimeZone()
		).thenReturn(
			TimeZone.getDefault()
		);

		Mockito.when(
			themeDisplay.getUser()
		).thenReturn(
			Mockito.mock(User.class)
		);

		return themeDisplay;
	}

	private void _updateNonevaluableDDMFormFields(
			Map<String, Object> fieldChangesProperties, boolean localizable,
			boolean required, Value value)
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		_createDDMFormFields(ddmForm, localizable, required);

		_createDDMFormValues(ddmForm, value);

		AddFormInstanceRecordMVCCommandUtil.updateNonevaluableDDMFormFields(
			ddmForm.getDDMFormFieldsMap(true),
			HashMapBuilder.put(
				new DDMFormEvaluatorFieldContextKey(
					_FIELD_NAME, _FIELD_INSTANCE_ID),
				fieldChangesProperties
			).put(
				new DDMFormEvaluatorFieldContextKey(
					_NESTED_FIELD_NAME, _NESTED_FIELD_INSTANCE_ID),
				fieldChangesProperties
			).build(),
			_ddmFormValues.getDDMFormFieldValuesMap(true), new DDMFormLayout(),
			Collections.emptySet());
	}

	private static final String _FIELD_INSTANCE_ID =
		RandomTestUtil.randomString();

	private static final String _FIELD_NAME = RandomTestUtil.randomString();

	private static final String _NESTED_FIELD_INSTANCE_ID =
		RandomTestUtil.randomString();

	private static final String _NESTED_FIELD_NAME =
		RandomTestUtil.randomString();

	private static final String _STRING_VALUE = RandomTestUtil.randomString();

	private final ActionRequest _actionRequest = Mockito.mock(
		ActionRequest.class);
	private DDMFormField _ddmFormField;
	private DDMFormValues _ddmFormValues;

}