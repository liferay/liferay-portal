/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.model.BatchEngineImportTaskErrorTable;
import com.liferay.batch.engine.service.BatchEngineExportTaskLocalService;
import com.liferay.batch.engine.service.BatchEngineImportTaskErrorLocalService;
import com.liferay.batch.engine.service.BatchEngineImportTaskLocalService;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carlos Correa
 */
@RunWith(Arquillian.class)
public class CompanyModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void test() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		User user = UserTestUtil.getAdminUser(company.getCompanyId());

		try {
			UserTestUtil.setUser(user);

			_batchEngineExportTaskLocalService.addBatchEngineExportTask(
				null, company.getCompanyId(), user.getUserId(), null,
				"com.liferay.headless.admin.user.dto.v1_0.UserAccount", "JSON",
				BatchEngineTaskExecuteStatus.INITIAL.name(), null,
				new HashMap<>(), null);

			BatchEngineImportTask batchEngineImportTask =
				_batchEngineImportTaskLocalService.addBatchEngineImportTask(
					null, company.getCompanyId(), user.getUserId(), 10, null,
					"com.liferay.headless.admin.user.dto.v1_0.UserAccount",
					new byte[0], "JSON",
					BatchEngineTaskExecuteStatus.INITIAL.name(), null,
					BatchEngineImportTaskConstants.
						IMPORT_STRATEGY_ON_ERROR_FAIL,
					BatchEngineTaskOperation.CREATE.name(), new HashMap<>(),
					null);

			_batchEngineImportTaskErrorLocalService.
				addBatchEngineImportTaskError(
					batchEngineImportTask.getCompanyId(),
					batchEngineImportTask.getUserId(),
					batchEngineImportTask.getBatchEngineImportTaskId(), null,
					RandomTestUtil.randomInt(), "This is just an error");
		}
		finally {
			_companyLocalService.deleteCompany(company);
		}

		Assert.assertEquals(
			0,
			_batchEngineExportTaskLocalService.getBatchEngineExportTasksCount(
				company.getCompanyId()));
		Assert.assertEquals(
			0,
			_batchEngineImportTaskLocalService.getBatchEngineImportTasksCount(
				company.getCompanyId()));
		Assert.assertEquals(
			0,
			_batchEngineImportTaskErrorLocalService.dslQueryCount(
				DSLQueryFactoryUtil.count(
				).from(
					BatchEngineImportTaskErrorTable.INSTANCE
				).where(
					BatchEngineImportTaskErrorTable.INSTANCE.companyId.eq(
						company.getCompanyId())
				)));
	}

	@Inject
	private BatchEngineExportTaskLocalService
		_batchEngineExportTaskLocalService;

	@Inject
	private BatchEngineImportTaskErrorLocalService
		_batchEngineImportTaskErrorLocalService;

	@Inject
	private BatchEngineImportTaskLocalService
		_batchEngineImportTaskLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

}