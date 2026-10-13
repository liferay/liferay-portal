/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.exportimport.data.handler.test;

import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.test.util.exportimport.data.handler.BaseCompanyBatchEnginePortletDataHandlerTestCase;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
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
public class AccountGroupCompanyBatchEnginePortletDataHandlerTest
	extends BaseCompanyBatchEnginePortletDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	protected void addEmptyEntry(String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		_accountGroupLocalService.getOrAddEmptyAccountGroup(
			externalReferenceCode, group.getCompanyId(),
			TestPropsValues.getUserId(), RandomTestUtil.randomString());
	}

	@Override
	protected void addEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_accountGroupLocalService.addAccountGroup(
			externalReferenceCode, TestPropsValues.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()));
	}

	@Override
	protected void deleteEntry(String externalReferenceCode, long groupId)
		throws Exception {

		_accountGroupLocalService.deleteAccountGroup(
			_getAccountGroup(externalReferenceCode, groupId));
	}

	@Override
	protected long getCreatorUserId(String externalReferenceCode, long groupId)
		throws Exception {

		AccountGroup accountGroup = _getAccountGroup(
			externalReferenceCode, groupId);

		return accountGroup.getUserId();
	}

	@Override
	protected String getEntityClassName() {
		return com.liferay.headless.admin.user.dto.v1_0.AccountGroup.class.
			getName();
	}

	@Override
	protected long getPrimaryKey(String externalReferenceCode, long groupId)
		throws Exception {

		AccountGroup accountGroup = _getAccountGroup(
			externalReferenceCode, groupId);

		return accountGroup.getAccountGroupId();
	}

	@Override
	protected int getStatus(String externalReferenceCode, long groupId)
		throws Exception {

		AccountGroup accountGroup = _getAccountGroup(
			externalReferenceCode, groupId);

		return accountGroup.getStatus();
	}

	@Override
	protected boolean hasEntry(String externalReferenceCode, long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return false;
		}

		AccountGroup accountGroup =
			_accountGroupLocalService.fetchAccountGroupByExternalReferenceCode(
				externalReferenceCode, group.getCompanyId());

		if (accountGroup == null) {
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

		AccountGroup accountGroup = _getAccountGroup(
			externalReferenceCode, groupId);

		accountGroup.setUserId(userId);

		_accountGroupLocalService.updateAccountGroup(accountGroup);
	}

	@Override
	protected void updateEntry(String externalReferenceCode, long groupId)
		throws Exception {

		AccountGroup accountGroup = _getAccountGroup(
			externalReferenceCode, groupId);

		_accountGroupLocalService.updateAccountGroup(
			accountGroup.getExternalReferenceCode(),
			accountGroup.getAccountGroupId(), RandomTestUtil.randomString(),
			accountGroup.getName(),
			ServiceContextTestUtil.getServiceContext(
				accountGroup.getCompanyId(), groupId,
				accountGroup.getUserId()));
	}

	@Override
	protected void updateModifiedDate(
			String externalReferenceCode, long groupId, Date modifiedDate)
		throws Exception {

		AccountGroup accountGroup = _getAccountGroup(
			externalReferenceCode, groupId);

		accountGroup.setModifiedDate(modifiedDate);

		_accountGroupLocalService.updateAccountGroup(accountGroup);
	}

	private AccountGroup _getAccountGroup(
			String externalReferenceCode, long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		return _accountGroupLocalService.getAccountGroupByExternalReferenceCode(
			externalReferenceCode, group.getCompanyId());
	}

	@Inject
	private AccountGroupLocalService _accountGroupLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

}