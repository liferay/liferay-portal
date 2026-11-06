/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.upgrade.v5_3_2.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.test.rule.Inject;

import org.junit.runner.RunWith;

/**
 * @author Paulo Albuquerque
 */
@RunWith(Arquillian.class)
public class DDMFormInstanceVersionDDMFormInstanceCTUpgradeProcessTest
	extends DDMFormInstanceCTUpgradeProcessTest {

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

		DDMFormInstance ddmFormInstance = updateDDMFormInstance(
			ddmFormInstanceVersion.getFormInstance());

		ddmFormInstanceVersion.setSettings(ddmFormInstance.getSettings());

		return _ddmFormInstanceVersionLocalService.updateDDMFormInstanceVersion(
			ddmFormInstanceVersion);
	}

	@DeleteAfterTestRun
	private DDMFormInstance _ddmFormInstance;

	@Inject
	private DDMFormInstanceVersionLocalService
		_ddmFormInstanceVersionLocalService;

	@DeleteAfterTestRun
	private Group _group;

}