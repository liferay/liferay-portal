/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.upgrade.v4_3_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializer;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMFormInstanceTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesSerializeUtil;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Paulo Albuquerque
 */
@RunWith(Arquillian.class)
public class DDMFormInstanceVersionDDMFormInstanceCTUpgradeProcessTest
	extends DDMFormInstanceCTUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		DDMFormInstance ddmFormInstance = (DDMFormInstance)super.addCTModel();

		return ddmFormInstance.getFormInstanceVersion(
			ddmFormInstance.getVersion());
	}

	@Override
	protected void deleteCTModel(long primaryKey) throws Exception {
		_ddmFormInstanceVersionLocalService.deleteDDMFormInstanceVersion(
			primaryKey);
	}

	@Override
	protected CTService<?> getCTService() {
		return _ddmFormInstanceVersionLocalService;
	}

	@Override
	protected CTModel<?> updateCTModel(CTModel<?> ctModel) throws Exception {
		DDMFormInstanceVersion ddmFormInstanceVersion =
			(DDMFormInstanceVersion)ctModel;

		ddmFormInstanceVersion.setSettings(
			DDMFormValuesSerializeUtil.serialize(
				DDMFormInstanceTestUtil.createSettingsDDMFormValues(),
				_jsonDDMFormValuesSerializer));

		return _ddmFormInstanceVersionLocalService.updateDDMFormInstanceVersion(
			ddmFormInstanceVersion);
	}

	@Inject
	private DDMFormInstanceVersionLocalService
		_ddmFormInstanceVersionLocalService;

	@Inject(filter = "ddm.form.values.serializer.type=json")
	private DDMFormValuesSerializer _jsonDDMFormValuesSerializer;

}