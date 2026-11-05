/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.SystemEventLocalServiceUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
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

	@Test
	public void testDeleteKaleoDefinition() throws Exception {
		KaleoDefinition kaleoDefinition = addKaleoDefinition(null);

		AssertUtils.assertFailure(
			WorkflowException.class,
			"Cannot delete active workflow definition " +
				kaleoDefinition.getKaleoDefinitionId(),
			() -> deleteKaleoDefinition(kaleoDefinition));

		deactivateKaleoDefinition(kaleoDefinition);

		deleteKaleoDefinition(kaleoDefinition);

		SystemEvent systemEvent = SystemEventLocalServiceUtil.fetchSystemEvent(
			kaleoDefinition.getGroupId(),
			ClassNameLocalServiceUtil.getClassNameId(KaleoDefinition.class),
			kaleoDefinition.getKaleoDefinitionId(),
			SystemEventConstants.TYPE_DELETE);

		Assert.assertEquals(
			kaleoDefinition.getExternalReferenceCode(),
			systemEvent.getClassExternalReferenceCode());

		AssertUtils.assertFailure(
			NoSuchDefinitionException.class,
			"No KaleoDefinition exists with the primary key " +
				kaleoDefinition.getKaleoDefinitionId(),
			() -> _kaleoDefinitionLocalService.getKaleoDefinition(
				kaleoDefinition.getKaleoDefinitionId()));
	}

	@Test
	public void testGetOrAddEmptyKaleoDefinition() throws Exception {

		// Lazy referencing disabled

		String externalReferenceCode = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			NoSuchDefinitionException.class,
			StringBundler.concat(
				"No KaleoDefinition exists with the key ",
				"{externalReferenceCode=", externalReferenceCode,
				", companyId=", TestPropsValues.getCompanyId(), "}"),
			() -> _kaleoDefinitionLocalService.getOrAddEmptyKaleoDefinition(
				externalReferenceCode, RandomTestUtil.randomString(),
				WorkflowDefinitionConstants.SCOPE_ALL, false, serviceContext));

		// Lazy referencing enabled

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			KaleoDefinition kaleoDefinition1 =
				_kaleoDefinitionLocalService.getOrAddEmptyKaleoDefinition(
					externalReferenceCode, RandomTestUtil.randomString(),
					WorkflowDefinitionConstants.SCOPE_ALL, false,
					serviceContext);

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, kaleoDefinition1.getStatus());

			KaleoDefinition kaleoDefinition2 =
				_kaleoDefinitionLocalService.getOrAddEmptyKaleoDefinition(
					externalReferenceCode, RandomTestUtil.randomString(),
					WorkflowDefinitionConstants.SCOPE_ALL, false,
					serviceContext);

			Assert.assertEquals(
				kaleoDefinition1.getKaleoDefinitionId(),
				kaleoDefinition2.getKaleoDefinitionId());
		}
	}

	@Test
	public void testUpdateKaleoDefinition() throws Exception {
		_testUpdateKaleoDefinition();
		_testUpdateKaleoDefinitionWithEmptyStatus();
		_testUpdateKaleoDefinitionWithScope();
		_testUpdateKaleoDefinitionWithSystem();
		_testUpdateKaleoDefinitionWithoutExternalReferenceCode();
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

		String title = RandomTestUtil.randomString(4001);

		kaleoDefinition = addKaleoDefinition(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), title, null);

		kaleoDefinition = _kaleoDefinitionLocalService.getKaleoDefinition(
			kaleoDefinition.getKaleoDefinitionId());

		Assert.assertEquals(
			title, kaleoDefinition.getTitle(LocaleUtil.getDefault()));
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

	private void _testUpdateKaleoDefinitionWithEmptyStatus() throws Exception {
		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			KaleoDefinition kaleoDefinition =
				_kaleoDefinitionLocalService.getOrAddEmptyKaleoDefinition(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					WorkflowDefinitionConstants.SCOPE_ALL, false,
					serviceContext);

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, kaleoDefinition.getStatus());
			Assert.assertEquals(1, kaleoDefinition.getVersion());

			kaleoDefinition =
				_kaleoDefinitionLocalService.updatedKaleoDefinition(
					kaleoDefinition.getExternalReferenceCode(),
					kaleoDefinition.getKaleoDefinitionId(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					read("legal-marketing-workflow-definition.xml"), false,
					serviceContext);

			Assert.assertEquals(
				WorkflowConstants.STATUS_DRAFT, kaleoDefinition.getStatus());
			Assert.assertEquals(1, kaleoDefinition.getVersion());

			kaleoDefinition = updateKaleoDefinition(kaleoDefinition);

			Assert.assertEquals(
				WorkflowConstants.STATUS_DRAFT, kaleoDefinition.getStatus());
			Assert.assertEquals(2, kaleoDefinition.getVersion());
		}
	}

	private void _testUpdateKaleoDefinitionWithScope() throws Exception {

		// Group ID as 0

		AccountEntry accountEntry1 = _addAccountEntry();

		serviceContext.setScopeGroupId(accountEntry1.getAccountEntryGroupId());

		KaleoDefinition kaleoDefinition1 = addKaleoDefinition(
			WorkflowDefinitionConstants.SCOPE_AI);

		serviceContext.setScopeGroupId(0);

		AssertUtils.assertFailure(
			KaleoDefinitionGroupIdException.class,
			"Invalid group ID 0 for scope AI",
			() -> updateKaleoDefinition(kaleoDefinition1));

		// Group ID as account entry group ID

		AccountEntry accountEntry2 = _addAccountEntry();

		serviceContext.setScopeGroupId(accountEntry2.getAccountEntryGroupId());

		KaleoDefinition kaleoDefinition2 = updateKaleoDefinition(
			kaleoDefinition1);

		Assert.assertEquals(
			accountEntry1.getAccountEntryGroupId(),
			kaleoDefinition2.getGroupId());

		// Group ID as nonaccount entry group ID

		serviceContext.setScopeGroupId(TestPropsValues.getGroupId());

		AssertUtils.assertFailure(
			KaleoDefinitionGroupIdException.class,
			"Invalid group ID " + TestPropsValues.getGroupId() +
				" for scope AI",
			() -> updateKaleoDefinition(kaleoDefinition2));
	}

	private void _testUpdateKaleoDefinitionWithSystem() throws Exception {
		KaleoDefinition kaleoDefinition = addKaleoDefinition(null, true);

		Assert.assertTrue(kaleoDefinition.isSystem());

		kaleoDefinition = updateKaleoDefinition(kaleoDefinition, true);

		Assert.assertTrue(kaleoDefinition.isSystem());

		kaleoDefinition = updateKaleoDefinition(kaleoDefinition, false);

		Assert.assertFalse(kaleoDefinition.isSystem());
	}

	private void _testUpdateKaleoDefinitionWithoutExternalReferenceCode()
		throws Exception {

		KaleoDefinition kaleoDefinition = addKaleoDefinition(null);

		String externalReferenceCode =
			kaleoDefinition.getExternalReferenceCode();

		kaleoDefinition = _kaleoDefinitionLocalService.updatedKaleoDefinition(
			null, kaleoDefinition.getKaleoDefinitionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			kaleoDefinition.getContent(), false, serviceContext);

		Assert.assertEquals(
			externalReferenceCode, kaleoDefinition.getExternalReferenceCode());
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

}