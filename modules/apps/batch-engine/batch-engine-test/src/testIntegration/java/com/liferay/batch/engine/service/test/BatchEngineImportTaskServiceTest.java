/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.internal.test.BlogPosting;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.service.BatchEngineImportTaskLocalService;
import com.liferay.batch.engine.service.BatchEngineImportTaskService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.HashMap;
import java.util.List;

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vendel Toreki
 */
@RunWith(Arquillian.class)
public class BatchEngineImportTaskServiceTest
	extends BaseBatchEngineTaskServiceTest {

	@Before
	public void setUp() throws Exception {
		_companyAdminBatchEngineImportTask = _addBatchEngineImportTask(
			companyAdminUser);
		_defaultCompanyBatchEngineImportTask = _addBatchEngineImportTask(
			omniadminUser);
		_userBatchEngineImportTask = _addBatchEngineImportTask(user);
	}

	@Test
	public void testAddBatchEngineImportTask() throws Exception {
		UserTestUtil.setUser(user);

		AssertUtils.assertFailure(
			PrincipalException.class, null,
			() -> _batchEngineImportTaskService.addBatchEngineImportTask(
				null, company.getCompanyId(), omniadminUser.getUserId(), 10,
				null, BlogPosting.class.getName(), new byte[0], "JSON",
				BatchEngineTaskExecuteStatus.INITIAL.name(), null,
				BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_FAIL,
				BatchEngineTaskOperation.CREATE.name(), new HashMap<>(), null));
		AssertUtils.assertFailure(
			PrincipalException.class, null,
			() -> _batchEngineImportTaskService.addBatchEngineImportTask(
				null, defaultCompany.getCompanyId(), user.getUserId(), 10, null,
				BlogPosting.class.getName(), new byte[0], "JSON",
				BatchEngineTaskExecuteStatus.INITIAL.name(), null,
				BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_FAIL,
				BatchEngineTaskOperation.CREATE.name(), new HashMap<>(), null));

		_batchEngineImportTaskService.addBatchEngineImportTask(
			null, company.getCompanyId(), user.getUserId(), 10, null,
			BlogPosting.class.getName(), new byte[0], "JSON",
			BatchEngineTaskExecuteStatus.INITIAL.name(), null,
			BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_FAIL,
			BatchEngineTaskOperation.CREATE.name(), new HashMap<>(), null);
	}

	@Test
	public void testGetBatchEngineImportTask() throws Exception {

		// Company admin

		UserTestUtil.setUser(companyAdminUser);

		_batchEngineImportTaskService.getBatchEngineImportTask(
			_companyAdminBatchEngineImportTask.getBatchEngineImportTaskId());

		AssertUtils.assertFailure(
			PrincipalException.class, null,
			() -> _batchEngineImportTaskService.getBatchEngineImportTask(
				_defaultCompanyBatchEngineImportTask.
					getBatchEngineImportTaskId()));

		_batchEngineImportTaskService.getBatchEngineImportTask(
			_userBatchEngineImportTask.getBatchEngineImportTaskId());

		// Omniadmin

		UserTestUtil.setUser(omniadminUser);

		_batchEngineImportTaskService.getBatchEngineImportTask(
			_companyAdminBatchEngineImportTask.getBatchEngineImportTaskId());
		_batchEngineImportTaskService.getBatchEngineImportTask(
			_defaultCompanyBatchEngineImportTask.getBatchEngineImportTaskId());
		_batchEngineImportTaskService.getBatchEngineImportTask(
			_userBatchEngineImportTask.getBatchEngineImportTaskId());

		// User

		UserTestUtil.setUser(user);

		AssertUtils.assertFailure(
			PrincipalException.class, null,
			() -> _batchEngineImportTaskService.getBatchEngineImportTask(
				_companyAdminBatchEngineImportTask.
					getBatchEngineImportTaskId()));
		AssertUtils.assertFailure(
			PrincipalException.class, null,
			() -> _batchEngineImportTaskService.getBatchEngineImportTask(
				_defaultCompanyBatchEngineImportTask.
					getBatchEngineImportTaskId()));

		_batchEngineImportTaskService.getBatchEngineImportTask(
			_userBatchEngineImportTask.getBatchEngineImportTaskId());
	}

	@Test
	public void testGetBatchEngineImportTaskByExternalReferenceCode()
		throws Exception {

		// Company admin

		UserTestUtil.setUser(companyAdminUser);

		_batchEngineImportTaskService.
			getBatchEngineImportTaskByExternalReferenceCode(
				_companyAdminBatchEngineImportTask.getExternalReferenceCode(),
				_companyAdminBatchEngineImportTask.getCompanyId());

		AssertUtils.assertFailure(
			PrincipalException.class, null,
			() ->
				_batchEngineImportTaskService.
					getBatchEngineImportTaskByExternalReferenceCode(
						_defaultCompanyBatchEngineImportTask.
							getExternalReferenceCode(),
						_defaultCompanyBatchEngineImportTask.getCompanyId()));

		_batchEngineImportTaskService.
			getBatchEngineImportTaskByExternalReferenceCode(
				_userBatchEngineImportTask.getExternalReferenceCode(),
				_userBatchEngineImportTask.getCompanyId());

		// Omniadmin

		UserTestUtil.setUser(omniadminUser);

		_batchEngineImportTaskService.
			getBatchEngineImportTaskByExternalReferenceCode(
				_companyAdminBatchEngineImportTask.getExternalReferenceCode(),
				_companyAdminBatchEngineImportTask.getCompanyId());
		_batchEngineImportTaskService.
			getBatchEngineImportTaskByExternalReferenceCode(
				_defaultCompanyBatchEngineImportTask.getExternalReferenceCode(),
				_defaultCompanyBatchEngineImportTask.getCompanyId());
		_batchEngineImportTaskService.
			getBatchEngineImportTaskByExternalReferenceCode(
				_userBatchEngineImportTask.getExternalReferenceCode(),
				_userBatchEngineImportTask.getCompanyId());

		// User

		UserTestUtil.setUser(user);

		AssertUtils.assertFailure(
			PrincipalException.class, null,
			() ->
				_batchEngineImportTaskService.
					getBatchEngineImportTaskByExternalReferenceCode(
						_companyAdminBatchEngineImportTask.
							getExternalReferenceCode(),
						_companyAdminBatchEngineImportTask.getCompanyId()));
		AssertUtils.assertFailure(
			PrincipalException.class, null,
			() ->
				_batchEngineImportTaskService.
					getBatchEngineImportTaskByExternalReferenceCode(
						_defaultCompanyBatchEngineImportTask.
							getExternalReferenceCode(),
						_defaultCompanyBatchEngineImportTask.getCompanyId()));

		_batchEngineImportTaskService.
			getBatchEngineImportTaskByExternalReferenceCode(
				_userBatchEngineImportTask.getExternalReferenceCode(),
				_userBatchEngineImportTask.getCompanyId());
	}

	@Test
	public void testGetBatchEngineImportTasks() throws Exception {

		// Company admin

		UserTestUtil.setUser(companyAdminUser);

		List<BatchEngineImportTask> batchEngineImportTasks =
			_batchEngineImportTaskService.getBatchEngineImportTasks(
				company.getCompanyId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertThat(
			batchEngineImportTasks,
			CoreMatchers.hasItem(_companyAdminBatchEngineImportTask));
		Assert.assertThat(
			batchEngineImportTasks,
			CoreMatchers.hasItem(_userBatchEngineImportTask));

		AssertUtils.assertFailure(
			PrincipalException.class, null,
			() -> _batchEngineImportTaskService.getBatchEngineImportTasks(
				defaultCompany.getCompanyId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS));

		// Omniadmin

		UserTestUtil.setUser(omniadminUser);

		batchEngineImportTasks =
			_batchEngineImportTaskService.getBatchEngineImportTasks(
				company.getCompanyId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertThat(
			batchEngineImportTasks,
			CoreMatchers.hasItem(_companyAdminBatchEngineImportTask));
		Assert.assertThat(
			batchEngineImportTasks,
			CoreMatchers.hasItem(_userBatchEngineImportTask));

		batchEngineImportTasks =
			_batchEngineImportTaskService.getBatchEngineImportTasks(
				defaultCompany.getCompanyId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertThat(
			batchEngineImportTasks,
			CoreMatchers.hasItem(_defaultCompanyBatchEngineImportTask));

		// User

		UserTestUtil.setUser(user);

		batchEngineImportTasks =
			_batchEngineImportTaskService.getBatchEngineImportTasks(
				company.getCompanyId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertThat(
			batchEngineImportTasks,
			CoreMatchers.not(
				CoreMatchers.hasItem(_companyAdminBatchEngineImportTask)));
		Assert.assertThat(
			batchEngineImportTasks,
			CoreMatchers.hasItem(_userBatchEngineImportTask));

		AssertUtils.assertFailure(
			PrincipalException.class, null,
			() -> _batchEngineImportTaskService.getBatchEngineImportTasks(
				defaultCompany.getCompanyId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS));
	}

	private BatchEngineImportTask _addBatchEngineImportTask(User user)
		throws Exception {

		return _batchEngineImportTaskLocalService.addBatchEngineImportTask(
			null, user.getCompanyId(), user.getUserId(), 10, null,
			BlogPosting.class.getName(), new byte[0], "JSON",
			BatchEngineTaskExecuteStatus.COMPLETED.name(), null,
			BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_FAIL,
			BatchEngineTaskOperation.CREATE.name(), new HashMap<>(), null);
	}

	@Inject
	private BatchEngineImportTaskLocalService
		_batchEngineImportTaskLocalService;

	@Inject
	private BatchEngineImportTaskService _batchEngineImportTaskService;

	private BatchEngineImportTask _companyAdminBatchEngineImportTask;
	private BatchEngineImportTask _defaultCompanyBatchEngineImportTask;
	private BatchEngineImportTask _userBatchEngineImportTask;

}