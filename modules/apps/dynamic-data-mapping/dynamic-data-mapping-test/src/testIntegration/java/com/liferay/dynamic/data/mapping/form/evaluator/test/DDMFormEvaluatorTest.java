/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.evaluator.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluator;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorEvaluateRequest;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorEvaluateResponse;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorFieldContextKey;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormRule;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormInstanceTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Leonardo Barros
 */
@RunWith(Arquillian.class)
public class DDMFormEvaluatorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testConcatFunction() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			"result", "text1", "text2", "text3");

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"result",
				new LocalizedValue() {
					{
						addString(LocaleUtil.US, null);
					}
				});

		ddmFormFieldValue.setInstanceId("instanceId");

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"text1",
				new LocalizedValue() {
					{
						addString(LocaleUtil.US, "How");
					}
				}));

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"text2",
				new LocalizedValue() {
					{
						addString(LocaleUtil.US, "are");
					}
				}));

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"text3",
				new LocalizedValue() {
					{
						addString(LocaleUtil.US, "you");
					}
				}));

		ddmForm.addDDMFormRule(
			new DDMFormRule() {
				{
					setActions(
						ListUtil.fromArray(
							"setValue('result',concat(getValue('text1'), " +
								"getValue('text2'),getValue('text3'),'?'))"));
					setCondition("TRUE");
					setEnabled(true);
				}
			});

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			_ddmFormEvaluator.evaluate(
				DDMFormEvaluatorEvaluateRequest.Builder.newBuilder(
					ddmForm, ddmFormValues, LocaleUtil.US
				).build());

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Map<String, Object> map = ddmFormFieldsPropertyChanges.get(
			new DDMFormEvaluatorFieldContextKey("result", "instanceId"));

		Assert.assertEquals("Howareyou?", map.get("value"));
	}

	@Test
	public void testEvaluate() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			"TextField1", "TextField2");

		DDMFormTestUtil.addDDMFormRule(
			Collections.singletonList("setVisible('TextField2', true)"),
			"isEmpty(getValue('TextField1'))", ddmForm);

		DDMFormInstance ddmFormInstance =
			DDMFormInstanceTestUtil.addDDMFormInstance(
				ddmForm, _group,
				DDMFormInstanceTestUtil.createSettingsDDMFormValues(false),
				TestPropsValues.getUserId());

		DDMStructure ddmStructure = ddmFormInstance.getStructure();

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			_ddmFormEvaluator.evaluate(
				DDMFormEvaluatorEvaluateRequest.Builder.newBuilder(
					ddmForm,
					DDMFormValuesTestUtil.createDDMFormValuesWithRandomValues(
						ddmForm),
					LocaleUtil.US
				).withCompanyId(
					TestPropsValues.getCompanyId()
				).withDDMFormInstanceId(
					ddmFormInstance.getFormInstanceId()
				).withDDMFormLayout(
					ddmStructure.getDDMFormLayout()
				).withEditingFieldValue(
					true
				).withGroupId(
					TestPropsValues.getGroupId()
				).withUserId(
					TestPropsValues.getUserId()
				).withViewMode(
					true
				).build());

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		Assert.assertEquals(
			ddmFormFieldsPropertyChanges.toString(), 1,
			ddmFormFieldsPropertyChanges.size());

		for (Map.Entry<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
				entry : ddmFormFieldsPropertyChanges.entrySet()) {

			DDMFormEvaluatorFieldContextKey ddmFormEvaluatorFieldContextKey =
				entry.getKey();

			Assert.assertEquals(
				"TextField2", ddmFormEvaluatorFieldContextKey.getName());

			Map<String, Object> propertyChanges = entry.getValue();

			Assert.assertEquals(StringPool.BLANK, propertyChanges.get("value"));
			Assert.assertFalse((Boolean)propertyChanges.get("visible"));
		}
	}

	@Inject
	private DDMFormEvaluator _ddmFormEvaluator;

	@DeleteAfterTestRun
	private Group _group;

}