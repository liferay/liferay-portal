/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.internal.test.BlogPosting;
import com.liferay.batch.engine.model.BatchEngineExportTask;
import com.liferay.batch.engine.service.BatchEngineExportTaskLocalService;
import com.liferay.batch.engine.service.BatchEngineExportTaskService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;

import java.io.Serializable;

import java.util.Collections;
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
public class BatchEngineExportTaskServiceTest
	extends BaseBatchEngineTaskServiceTest {

	@Before
	public void setUp() throws Exception {
		_companyAdminBatchEngineExportTask = _addBatchEngineExportTask(
			companyAdminUser);
		_defaultCompanyBatchEngineExportTask = _addBatchEngineExportTask(
			omniadminUser);
		_userBatchEngineExportTask = _addBatchEngineExportTask(user);
	}

	@Test
	public void testAddBatchEngineExportTask() throws Exception {
		UserTestUtil.setUser(user);

		AssertUtils.assertFailure(
			PrincipalException.class, null,
			() -> _batchEngineExportTaskService.addBatchEngineExportTask(
				null, company.getCompanyId(), omniadminUser.getUserId(), null,
				BlogPosting.class.getName(), "JSON",
				BatchEngineTaskExecuteStatus.INITIAL.name(),
				Collections.emptyList(),
				HashMapBuilder.<String, Serializable>put(
					"siteId", TestPropsValues.getGroupId()
				).build(),
				null));
		AssertUtils.assertFailure(
			PrincipalException.class, null,
			() -> _batchEngineExportTaskService.addBatchEngineExportTask(
				null, defaultCompany.getCompanyId(), user.getUserId(), null,
				BlogPosting.class.getName(), "JSON",
				BatchEngineTaskExecuteStatus.INITIAL.name(),
				Collections.emptyList(),
				HashMapBuilder.<String, Serializable>put(
					"siteId", TestPropsValues.getGroupId()
				).build(),
				null));

		_batchEngineExportTaskService.addBatchEngineExportTask(
			null, company.getCompanyId(), user.getUserId(), null,
			BlogPosting.class.getName(), "JSON",
			BatchEngineTaskExecuteStatus.INITIAL.name(),
			Collections.emptyList(),
			HashMapBuilder.<String, Serializable>put(
				"siteId", TestPropsValues.getGroupId()
			).build(),
			null);
	}

	@Test
	public void testGetBatchEngineExportTask() throws Exception {

		// Company admin

		UserTestUtil.setUser(companyAdminUser);

		_batchEngineExportTaskService.getBatchEngineExportTask(
			_companyAdminBatchEngineExportTask.getBatchEngineExportTaskId());

		AssertUtils.assertFailure(
			PrincipalException.class, null,
			() -> _batchEngineExportTaskService.getBatchEngineExportTask(
				_defaultCompanyBatchEngineExportTask.
					getBatchEngineExportTaskId()));

		_batchEngineExportTaskService.getBatchEngineExportTask(
			_userBatchEngineExportTask.getBatchEngineExportTaskId());

		// Omniadmin

		UserTestUtil.setUser(omniadminUser);

		_batchEngineExportTaskService.getBatchEngineExportTask(
			_companyAdminBatchEngineExportTask.getBatchEngineExportTaskId());
		_batchEngineExportTaskService.getBatchEngineExportTask(
			_defaultCompanyBatchEngineExportTask.getBatchEngineExportTaskId());
		_batchEngineExportTaskService.getBatchEngineExportTask(
			_userBatchEngineExportTask.getBatchEngineExportTaskId());

		// User

		UserTestUtil.setUser(user);

		AssertUtils.assertFailure(
			PrincipalException.class, null,
			() -> _batchEngineExportTaskService.getBatchEngineExportTask(
				_companyAdminBatchEngineExportTask.
					getBatchEngineExportTaskId()));
		AssertUtils.assertFailure(
			PrincipalException.class, null,
			() -> _batchEngineExportTaskService.getBatchEngineExportTask(
				_defaultCompanyBatchEngineExportTask.
					getBatchEngineExportTaskId()));

		_batchEngineExportTaskService.getBatchEngineExportTask(
			_userBatchEngineExportTask.getBatchEngineExportTaskId());
	}

	@Test
	public void testGetBatchEngineExportTaskByExternalReferenceCode()
		throws Exception {

		// Company admin

		UserTestUtil.setUser(companyAdminUser);

		_batchEngineExportTaskService.
			getBatchEngineExportTaskByExternalReferenceCode(
				_companyAdminBatchEngineExportTask.getExternalReferenceCode(),
				_companyAdminBatchEngineExportTask.getCompanyId());

		AssertUtils.assertFailure(
			PrincipalException.class, null,
			() ->
				_batchEngineExportTaskService.
					getBatchEngineExportTaskByExternalReferenceCode(
						_defaultCompanyBatchEngineExportTask.
							getExternalReferenceCode(),
						_defaultCompanyBatchEngineExportTask.getCompanyId()));

		_batchEngineExportTaskService.
			getBatchEngineExportTaskByExternalReferenceCode(
				_userBatchEngineExportTask.getExternalReferenceCode(),
				_userBatchEngineExportTask.getCompanyId());

		// Omniadmin

		UserTestUtil.setUser(omniadminUser);

		_batchEngineExportTaskService.
			getBatchEngineExportTaskByExternalReferenceCode(
				_companyAdminBatchEngineExportTask.getExternalReferenceCode(),
				_companyAdminBatchEngineExportTask.getCompanyId());
		_batchEngineExportTaskService.
			getBatchEngineExportTaskByExternalReferenceCode(
				_defaultCompanyBatchEngineExportTask.getExternalReferenceCode(),
				_defaultCompanyBatchEngineExportTask.getCompanyId());
		_batchEngineExportTaskService.
			getBatchEngineExportTaskByExternalReferenceCode(
				_userBatchEngineExportTask.getExternalReferenceCode(),
				_userBatchEngineExportTask.getCompanyId());

		// User

		UserTestUtil.setUser(user);

		AssertUtils.assertFailure(
			PrincipalException.class, null,
			() ->
				_batchEngineExportTaskService.
					getBatchEngineExportTaskByExternalReferenceCode(
						_companyAdminBatchEngineExportTask.
							getExternalReferenceCode(),
						_companyAdminBatchEngineExportTask.getCompanyId()));
		AssertUtils.assertFailure(
			PrincipalException.class, null,
			() ->
				_batchEngineExportTaskService.
					getBatchEngineExportTaskByExternalReferenceCode(
						_defaultCompanyBatchEngineExportTask.
							getExternalReferenceCode(),
						_defaultCompanyBatchEngineExportTask.getCompanyId()));

		_batchEngineExportTaskService.
			getBatchEngineExportTaskByExternalReferenceCode(
				_userBatchEngineExportTask.getExternalReferenceCode(),
				_userBatchEngineExportTask.getCompanyId());
	}

	@Test
	public void testGetBatchEngineExportTasks() throws Exception {

		// Company admin

		UserTestUtil.setUser(companyAdminUser);

		List<BatchEngineExportTask> batchEngineExportTasks =
			_batchEngineExportTaskService.getBatchEngineExportTasks(
				company.getCompanyId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertThat(
			batchEngineExportTasks,
			CoreMatchers.hasItem(_companyAdminBatchEngineExportTask));
		Assert.assertThat(
			batchEngineExportTasks,
			CoreMatchers.hasItem(_userBatchEngineExportTask));

		AssertUtils.assertFailure(
			PrincipalException.class, null,
			() -> _batchEngineExportTaskService.getBatchEngineExportTasks(
				defaultCompany.getCompanyId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS));

		// Omniadmin

		UserTestUtil.setUser(omniadminUser);

		batchEngineExportTasks =
			_batchEngineExportTaskService.getBatchEngineExportTasks(
				company.getCompanyId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertThat(
			batchEngineExportTasks,
			CoreMatchers.hasItem(_companyAdminBatchEngineExportTask));
		Assert.assertThat(
			batchEngineExportTasks,
			CoreMatchers.hasItem(_userBatchEngineExportTask));

		batchEngineExportTasks =
			_batchEngineExportTaskService.getBatchEngineExportTasks(
				defaultCompany.getCompanyId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		Assert.assertThat(
			batchEngineExportTasks,
			CoreMatchers.hasItem(_defaultCompanyBatchEngineExportTask));

		// User

		UserTestUtil.setUser(user);

		batchEngineExportTasks =
			_batchEngineExportTaskService.getBatchEngineExportTasks(
				company.getCompanyId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertThat(
			batchEngineExportTasks,
			CoreMatchers.not(
				CoreMatchers.hasItem(_companyAdminBatchEngineExportTask)));
		Assert.assertThat(
			batchEngineExportTasks,
			CoreMatchers.hasItem(_userBatchEngineExportTask));

		AssertUtils.assertFailure(
			PrincipalException.class, null,
			() -> _batchEngineExportTaskService.getBatchEngineExportTasks(
				defaultCompany.getCompanyId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS));
	}

	private BatchEngineExportTask _addBatchEngineExportTask(User user)
		throws Exception {

		return _batchEngineExportTaskLocalService.addBatchEngineExportTask(
			null, user.getCompanyId(), user.getUserId(), null,
			BlogPosting.class.getName(), "JSON",
			BatchEngineTaskExecuteStatus.COMPLETED.name(),
			Collections.emptyList(),
			HashMapBuilder.<String, Serializable>put(
				"siteId", TestPropsValues.getGroupId()
			).build(),
			null);
	}

	@Inject
	private BatchEngineExportTaskLocalService
		_batchEngineExportTaskLocalService;

	@Inject
	private BatchEngineExportTaskService _batchEngineExportTaskService;

	private BatchEngineExportTask _companyAdminBatchEngineExportTask;
	private BatchEngineExportTask _defaultCompanyBatchEngineExportTask;
	private BatchEngineExportTask _userBatchEngineExportTask;

}