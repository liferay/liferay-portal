/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.exportimport.data.handler.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseCompanyBatchEnginePortletDataHandlerTestCase;
import com.liferay.headless.admin.user.dto.v1_0.Account;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Date;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Alberto Javier Moreno Lage
 */
@RunWith(Arquillian.class)
public class AccountCompanyBatchEnginePortletDataHandlerTest
	extends BaseCompanyBatchEnginePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	protected void addEmptyEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		_accountEntryLocalService.getOrAddEmptyAccountEntry(
			externalReferenceCode, group.getCompanyId(),
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS);
	}

	@Override
	protected void addEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_accountEntryLocalService.addAccountEntry(
			externalReferenceCode, TestPropsValues.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, null, null, AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()));
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_accountEntryLocalService.deleteAccountEntry(
			_getAccountEntry(externalReferenceCode, groupId));
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		AccountEntry accountEntry = _getAccountEntry(
			externalReferenceCode, groupId);

		return accountEntry.getUserId();
	}

	@Override
	protected String getEntityClassName() {
		return Account.class.getName();
	}

	@Override
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		AccountEntry accountEntry = _getAccountEntry(
			externalReferenceCode, groupId);

		return accountEntry.getAccountEntryId();
	}

	@Override
	protected int getStatus(String externalReferenceCode, long groupId)
		throws Exception {

		AccountEntry accountEntry = _getAccountEntry(
			externalReferenceCode, groupId);

		return accountEntry.getStatus();
	}

	@Override
	protected boolean hasEntry(String externalReferenceCode, long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return false;
		}

		AccountEntry accountEntry =
			_accountEntryLocalService.fetchAccountEntryByExternalReferenceCode(
				externalReferenceCode, group.getCompanyId());

		if (accountEntry == null) {
			return false;
		}

		return true;
	}

	@Override
	protected boolean supportsComments() {
		return false;
	}

	@Override
	protected boolean supportsEmptyEntries() {
		return true;
	}

	@Override
	protected boolean supportsPermissions() {
		return true;
	}

	@Override
	protected void updateCreatorUserId(
			String externalReferenceCode, long groupId, long userId)
		throws Exception {

		AccountEntry accountEntry = _getAccountEntry(
			externalReferenceCode, groupId);

		accountEntry.setUserId(userId);

		_accountEntryLocalService.updateAccountEntry(accountEntry);
	}

	@Override
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		AccountEntry accountEntry = _getAccountEntry(
			externalReferenceCode, groupId);

		_accountEntryLocalService.updateAccountEntry(
			accountEntry.getExternalReferenceCode(),
			accountEntry.getAccountEntryId(),
			accountEntry.getParentAccountEntryId(), accountEntry.getName(),
			RandomTestUtil.randomString(), false,
			accountEntry.getDomainsArray(), accountEntry.getEmailAddress(),
			null, accountEntry.getTaxIdNumber(), accountEntry.getStatus(),
			ServiceContextTestUtil.getServiceContext(
				accountEntry.getCompanyId(), groupId,
				accountEntry.getUserId()));
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate)
		throws Exception {

		AccountEntry accountEntry = _getAccountEntry(
			externalReferenceCode, groupId);

		accountEntry.setModifiedDate(modifiedDate);

		_accountEntryLocalService.updateAccountEntry(accountEntry);
	}

	private AccountEntry _getAccountEntry(
			String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		return _accountEntryLocalService.getAccountEntryByExternalReferenceCode(
			externalReferenceCode, group.getCompanyId());
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

}