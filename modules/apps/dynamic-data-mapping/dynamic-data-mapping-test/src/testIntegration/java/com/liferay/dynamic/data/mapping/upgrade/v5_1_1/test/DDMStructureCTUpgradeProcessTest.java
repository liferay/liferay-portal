/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.upgrade.v5_1_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.test.util.BaseCTUpgradeProcessTestCase;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.io.DDMFormSerializer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidationExpression;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormSerializeUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Paulo Albuquerque
 */
@RunWith(Arquillian.class)
public class DDMStructureCTUpgradeProcessTest
	extends BaseCTUpgradeProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		_ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), getClassName(),
			DDMFormTestUtil.createDDMForm(RandomTestUtil.randomString()));

		return _ddmStructure;
	}

	@Override
	protected CTService<?> getCTService() {
		return _ddmStructureLocalService;
	}

	protected String getClassName() {
		return DDMFormInstance.class.getName();
	}

	protected DDMForm getDDMForm() {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = DDMFormTestUtil.createDDMFormField(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			DDMFormFieldTypeConstants.DATE, DDMFormFieldTypeConstants.DATE,
			false, false, false);

		DDMFormFieldValidation ddmFormFieldValidation =
			new DDMFormFieldValidation();

		ddmFormFieldValidation.setDDMFormFieldValidationExpression(
			new DDMFormFieldValidationExpression() {
				{
					setName("dateRange");
					setValue("dateRange({parameter} AND {parameter})");
				}
			});

		ddmFormField.setDDMFormFieldValidation(ddmFormFieldValidation);

		ddmForm.addDDMFormField(ddmFormField);

		return ddmForm;
	}

	protected String getUpgradeStepClassName() {
		return "com.liferay.dynamic.data.mapping.internal.upgrade.v5_1_1." +
			"DDMValidationUpgradeProcess";
	}

	@Override
	protected void runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, getUpgradeStepClassName());

		upgradeProcess.upgrade();
	}

	@Override
	protected CTModel<?> updateCTModel(CTModel<?> ctModel) throws Exception {
		DDMStructure ddmStructure = (DDMStructure)ctModel;

		ddmStructure.setDefinition(
			DDMFormSerializeUtil.serialize(
				getDDMForm(), _jsonDDMFormSerializer));

		return _ddmStructureLocalService.updateDDMStructure(ddmStructure);
	}

	@DeleteAfterTestRun
	private DDMStructure _ddmStructure;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(filter = "ddm.form.serializer.type=json")
	private DDMFormSerializer _jsonDDMFormSerializer;

	@Inject(
		filter = "(&(component.name=com.liferay.dynamic.data.mapping.internal.upgrade.registry.DDMServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}