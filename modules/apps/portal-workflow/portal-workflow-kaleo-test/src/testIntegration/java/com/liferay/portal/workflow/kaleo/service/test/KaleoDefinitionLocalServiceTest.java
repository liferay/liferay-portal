/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.kaleo.exception.KaleoDefinitionGroupIdException;
import com.liferay.portal.workflow.kaleo.exception.NoSuchDefinitionException;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Inácio Nery
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class KaleoDefinitionLocalServiceTest
	extends BaseKaleoLocalServiceTestCase {

	@Test
	public void testAddKaleoDefinition() throws Exception {
		_testAddKaleoDefinition();
		_testAddKaleoDefinitionWithScope();
		_testAddKaleoDefinitionWithSystem();
	}

	@Test
	public void testDeactivateKaleoDefinition() throws Exception {
		KaleoDefinition kaleoDefinition = addKaleoDefinition(null);

		deactivateKaleoDefinition(kaleoDefinition);

		Assert.assertFalse(kaleoDefinition.isActive());
	}

	@Test(expected = WorkflowException.class)
	public void testDeleteKaleoDefinition1() throws Exception {
		KaleoDefinition kaleoDefinition = addKaleoDefinition(null);

		deleteKaleoDefinition(kaleoDefinition);
	}

	@Test(expected = NoSuchDefinitionException.class)
	public void testDeleteKaleoDefinition2() throws Exception {
		KaleoDefinition kaleoDefinition = addKaleoDefinition(null);

		deactivateKaleoDefinition(kaleoDefinition);

		deleteKaleoDefinition(kaleoDefinition);

		_kaleoDefinitionLocalService.getKaleoDefinition(
			kaleoDefinition.getKaleoDefinitionId());
	}

	@Test
	public void testUpdateKaleoDefinition() throws Exception {
		_testUpdateKaleoDefinition();
		_testUpdateKaleoDefinitionWithScope();
		_testUpdateKaleoDefinitionWithSystem();
	}

	private AccountEntry _addAccountEntry() throws Exception {
		return _accountEntryLocalService.addAccountEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null,
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private void _testAddKaleoDefinition() throws Exception {
		KaleoDefinition kaleoDefinition = addKaleoDefinition(null);

		Assert.assertEquals(1, kaleoDefinition.getVersion());
	}

	private void _testAddKaleoDefinitionWithScope() throws Exception {

		// Group ID as 0

		serviceContext.setScopeGroupId(0);

		AssertUtils.assertFailure(
			KaleoDefinitionGroupIdException.class,
			"Invalid group ID 0 for scope AI",
			() -> addKaleoDefinition(WorkflowDefinitionConstants.SCOPE_AI));

		// Group ID as account entry group ID

		AccountEntry accountEntry = _addAccountEntry();

		serviceContext.setScopeGroupId(accountEntry.getAccountEntryGroupId());

		Assert.assertNotNull(
			addKaleoDefinition(WorkflowDefinitionConstants.SCOPE_AI));

		// Group ID as nonaccount entry group ID

		serviceContext.setScopeGroupId(TestPropsValues.getGroupId());

		AssertUtils.assertFailure(
			KaleoDefinitionGroupIdException.class,
			"Invalid group ID " + TestPropsValues.getGroupId() +
				" for scope AI",
			() -> addKaleoDefinition(WorkflowDefinitionConstants.SCOPE_AI));
	}

	private void _testAddKaleoDefinitionWithSystem() throws Exception {
		KaleoDefinition kaleoDefinition = addKaleoDefinition(null, false);

		Assert.assertFalse(kaleoDefinition.isSystem());

		kaleoDefinition = addKaleoDefinition(null, true);

		Assert.assertTrue(kaleoDefinition.isSystem());
	}

	private void _testUpdateKaleoDefinition() throws Exception {
		KaleoDefinition kaleoDefinition = addKaleoDefinition(null);

		kaleoDefinition = updateKaleoDefinition(kaleoDefinition);

		Assert.assertEquals(2, kaleoDefinition.getVersion());
	}

	private void _testUpdateKaleoDefinitionWithScope() throws Exception {

		// Group ID as 0

		AccountEntry accountEntry = _addAccountEntry();

		serviceContext.setScopeGroupId(accountEntry.getAccountEntryGroupId());

		KaleoDefinition kaleoDefinition = addKaleoDefinition(
			WorkflowDefinitionConstants.SCOPE_AI);

		serviceContext.setScopeGroupId(0);

		AssertUtils.assertFailure(
			KaleoDefinitionGroupIdException.class,
			"Invalid group ID 0 for scope AI",
			() -> updateKaleoDefinition(kaleoDefinition));

		// Group ID as account entry group ID

		serviceContext.setScopeGroupId(accountEntry.getAccountEntryGroupId());

		Assert.assertNotNull(
			addKaleoDefinition(WorkflowDefinitionConstants.SCOPE_AI));

		// Group ID as nonaccount entry group ID

		serviceContext.setScopeGroupId(TestPropsValues.getGroupId());

		AssertUtils.assertFailure(
			KaleoDefinitionGroupIdException.class,
			"Invalid group ID " + TestPropsValues.getGroupId() +
				" for scope AI",
			() -> updateKaleoDefinition(kaleoDefinition));
	}

	private void _testUpdateKaleoDefinitionWithSystem() throws Exception {
		KaleoDefinition kaleoDefinition = addKaleoDefinition(null, true);

		Assert.assertTrue(kaleoDefinition.isSystem());

		kaleoDefinition = updateKaleoDefinition(kaleoDefinition, true);

		Assert.assertTrue(kaleoDefinition.isSystem());

		kaleoDefinition = updateKaleoDefinition(kaleoDefinition, false);

		Assert.assertFalse(kaleoDefinition.isSystem());
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

}